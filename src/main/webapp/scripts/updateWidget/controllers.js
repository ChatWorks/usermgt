/* 
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * http://www.scm-manager.com
 */

angular.module('universeadm.updateWidget.controllers', ['adf.provider','universeadm.updateWidget.services','universeadm.widget.update'])
        .controller('widgetCtrl',
        function ($scope, $log, $modal,$interval,$timeout, updateService,modalService, update) {
          
          $scope.availableVersion = update.newVersion;
          $scope.version = update.version;
          $scope.animationsEnabled = true;
          $scope.pcDialogOpen=false;
          $scope.uiDialogOpen=false;
          var intervalPromise;
          $scope.$on("$destroy", function () {$interval.cancel(intervalPromise);});
          
          $scope.startInterval=function(){
              intervalPromise =$interval(function(){$scope.checkUpdateStatus();},2000);
          };
          
          $scope.startInterval();
          
          $scope.detectStatus= function(data){
              if (data.status == "scheduled") {
                $scope.updateScheduled = true;
              }
              else if (data.status == "preCheck") {
                $scope.updateScheduled = false;
                $scope.updatePrecheck = true;
              }
              else if (data.status == "in progress") {
                $scope.updateScheduled = false;
                $scope.updatePrecheck = false;
                $scope.updateInProgress = true;
              }
              else if (data.status == "preCheckResult") {
                $scope.updatePrecheck = false;
                $interval.cancel(intervalPromise); 
                $scope.openPCResult(data.syscheck);
              }
              else if (data.status == "updateFormAvailable") {
                $interval.cancel(intervalPromise); 
                $scope.openUserInput();
              }
              else if (data.status == "done") {
                $interval.cancel(intervalPromise); 
                $scope.checkUpdateCheckStatus();
                $scope.updateScheduled = false;
                $scope.updatePrecheck = false;
                $scope.updateInProgress = false;
                updateService.versionCheck().then(function(data){
                  $scope.availableVersion = data.newVersion;
                  $scope.version = data.version;
                });
                if (data.result == "successful") { 
                  $scope.updateSuccessful = true;
                }
                else if (data.result == "failed") {
                  $scope.updateFailed = true;
                }
                else if (data.result == "unknown") {
                }
                else {
                  $scope.updateFailedUnexpected = true;
                }
              }
              else if (data.status == "no update") {
                $scope.noUpdate = true;
                $interval.cancel(intervalPromise); 
                $scope.checkUpdateCheckStatus();
              }
              else if (data.status == "wrong credentials") {
                $scope.wrongCreds = true;
                $interval.cancel(intervalPromise); 
                $scope.checkUpdateCheckStatus();
              }
          };

          $scope.checkUpdateStatus = function () {
            updateService.check().then(function(data){
              $scope.detectStatus(data);
            },function (){
                $log.error("Cant check update status");
              }
            );
          }
          
          $scope.checkUpdateCheckStatus = function (){
            updateService.updateCheck().then(function (e){
              $scope.updateAvailable = e.updateAvailable;
              $scope.validCreds = e.validCreds;
            },function () {
                      $log.error("Cant check if update is possible.");
            });
          };

          $scope.startUpdate = function () {
            updateService.start();
          };

          // Modals:
          $scope.openUpdateStartConfirmation = function (size) {

            var modalInstance = $modal.open({
              animation: $scope.animationsEnabled,
              templateUrl: 'views/updateWidget/updateStartConfirmation.dialog.html',
              controller: 'updateStartConfirmationCtrl',
              size: size,
              backdrop: 'static',
              resolve: {
              }
            });

            modalInstance.result.then(function () {
              $scope.startUpdate();
              $scope.startInterval();
            }, function () {
              $log.info('Modal dismissed at: ' + new Date());
            });
          };

          $scope.openPCResult = function (data, size) {
            $interval.cancel(intervalPromise);
            if (!$scope.pcDialogOpen){
              $scope.pcDialogOpen = true;
              modalService.pcResult(data, size).result.then(function(response){
                $scope.pcDialogOpen = false;
                $timeout($scope.startInterval(), 10000); 
              });
              $scope.pcDialogOpen = false;
            }
          };

          $scope.openUserInput = function (size) {
            $interval.cancel(intervalPromise);
            if(!$scope.uiDialogOpen){
              $scope.uiDialogOpen=true;
              modalService.userInput(size).result.then(function(response){
                $scope.uiDialogOpen=false;
                $scope.startInterval();   
              });
             $scope.uiDialogOpen=false;  
            }
          };
        });
