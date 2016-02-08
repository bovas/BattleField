angular.module('myChart')
.controller('d3chartsCtrl', ['$scope', '$interval',
  function ($scope, $interval) {
    $scope.createBarChartData = function(){
		$scope.pagesByData = [{pages:'CM',count: 10},
		                      {pages:'AM',count: 20},
		                      {pages:'M2M',count: 30},
		                      {pages:'OC',count: 15},
		                      {pages:'MB',count: 10}];
		$scope.issuesTypeData = [{type:'In Progress',count: 30},
		                         {type:'Closed',count: 40},
		                         {type:'Open',count: 10},
		                         {type:'On hold',count: 20}];	                      
	}
	$scope.createBarChartData();
	
	$scope.createScatteredChartData = function($interval){
		var time = new Date('2014-01-01 00:00:00 +0100');

		// Random data point generator
		var randPoint = function() {
			var rand = Math.random;
			return { time: new Date(time.toString()), visitors: rand()*100 };
		}

		// We store a list of logs
		$scope.logs = [ randPoint() ];
		$scope.moreLogs = [
		                   [randPoint()],[randPoint()],[randPoint()],[randPoint()]
		                   ];

		$interval(function() {

			time.setSeconds(time.getSeconds() + 1);

			$scope.logs.push(randPoint());
			$scope.moreLogs[0].push(randPoint());
			$scope.moreLogs[1].push(randPoint());
			$scope.moreLogs[2].push(randPoint());
			$scope.moreLogs[3].push(randPoint());

		}, 1000);
	}
	$scope.createScatteredChartData($interval);
  }
]);