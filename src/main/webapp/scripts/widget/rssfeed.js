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
var myRss = angular.module('adf.rssfeed.widget', ['adf.provider','restangular']);
  
myRss.config(rSSFeedWidget);
       
    function rSSFeedWidget(dashboardProvider){
    dashboardProvider
      .widget('rss.widget', {
        title: 'RSS Feeds',
        description: 'Display a RSS feed',
        controller: 'rssWidgetCtrl',
        templateUrl: '{widgetsPath}/scripts/widget/view.html',
        edit: {
          templateUrl: '{widgetsPath}/scripts/widget/edit.html'
        }
      });
    };
    myRss.factory('rssWidgetService',rssFeedFactory); 
    
    function rssFeedFactory(Restangular){
        return {
            get: function (etwas) {
                return Restangular.one('rss').get({'url': etwas});
            }
        };
    };
    
        
    myRss.controller('rssWidgetCtrl',['$scope','rssWidgetService',rssFeedController]);
      
    function rssFeedController($scope, rssWidgetService){
       var input = $scope.config.inputURL;
        
        if(input !== undefined) {
           rssWidgetService.get(input).then(function (response){
           $scope.items = response;
           });
       }  
        if($scope.config.size === undefined) {
            $scope.config.size = 3;
        }
        $scope.showSize = $scope.config.size;
    };
   
    var INTEGER_REGEXP = /^\-?\d+$/;
    myRss.directive('validateInteger',checkValueOfNgModel); 
    
    function checkValueOfNgModel() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, elm, attrs, rssWidgetCtrl) {
//            var regex = /^\d$/;
        var validat = function (modelValue) {
            console.info("DIRECTIVE ");
          // it is valid
          rssWidgetCtrl.$setValidity('validateInteger', INTEGER_REGEXP.test(modelValue));

        // it is invalid
        return modelValue;
      };
      ctrl.$parsers.unshift(validat);
      ctrl.$formatters.unshift(validat);
    }
  };
};


    