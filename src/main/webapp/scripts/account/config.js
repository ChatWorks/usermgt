/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular.module('universeadm.account.config', ['ui.router', 
  'universeadm.account.controllers', 'universeadm.account.services', 
  'universeadm.navigation'])
  .config(function($stateProvider, navigationProvider){
    // registar navigation
    navigationProvider.add({
      url: '/account',
      label: 'Account',
      requireAdminPrivileges: false
    });
    
    $stateProvider.state('account', {
      url: '/account',
      templateUrl: 'views/user/edit.html',
      controller: 'accountController',
      resolve: {
        account: function(accountService){
          return accountService.get();
        }
      }
    });
  });