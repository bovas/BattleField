'use strict';

var phonecatControllers = angular.module('phonecatControllers', []);

/* Controllers */
//Option1 of defining controller and injecting via Http
/*function PhoneListCtrl($scope,$http){
	$http.get('phones/phones.json').success(function(data){
		$scope.phones = data;
	});
	$scope.orderProp = 'age';
}
PhoneListCtrl.$inject = ['$scope', '$http'];*/ 

//Injecting via Service
function PhoneListCtrl($scope,$phone){
	$scope.phones = $phone.query();
	$scope.orderProp='age';
}
PhoneListCtrl.$inject = ['$scope', 'Phone'];
phonecatControllers.controller('PhoneListCtrl', PhoneListCtrl);


//Option2 of defining controller and injecting
/*phonecatControllers.controller('PhoneDetailCtrl',['$scope','$http','$routeParams',
    function($scope,$http,$routeParams) {
		$http.get('phones/' + $routeParams.phoneId + '.json').success(function(data) {
	      $scope.phone = data;
	      $scope.mainImageUrl = data.images[0]; 
	});
	$scope.setImage = function(imageUrl) {
	      $scope.mainImageUrl = imageUrl;
	};
}]);*/

phonecatControllers.controller('PhoneDetailCtrl',['$scope','Phone','$routeParams',
    function($scope,$Phone,$routeParams){
		$scope.phone = $Phone.get({phoneId:$routeParams.phoneId},  function(phone){
			$scope.mainImageUrl = phone.images[0];
		});
		$scope.setImage = function(imageUrl) {
		    $scope.mainImageUrl = imageUrl;
		}
	},	
]);