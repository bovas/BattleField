angular.module('myChart', []);
var module = angular.module('dashboardApp', [
   'ngResource',
   'ngRoute',
   'myChart',
   'JiraRESTClientApp'
   ]);

module.config(function($routeProvider) {
  $routeProvider
      .when('/vocChart', {
        templateUrl: 'pages/d3charts/landing.html',
        controller: 'd3chartsCtrl'
      })
      .when('/jiramisc', {
        templateUrl: 'pages/dashboardmisc/landing.html',
        controller: 'ApplicationController'
      })     
      .otherwise({
        redirectTo: '/vocChart'
      });
});
module.config(['$controllerProvider', function($controllerProvider) {
    $controllerProvider.allowGlobals();
}]);