//private function, to get executed only by angular
(function(angular){
	'use strict';
	angular.module('invoice1',['finance1'])
	.controller('InvoiceController',['currencyConverter', function(currencyConverter) {
		this.qty = 1;
		this.cost = 2;
		this.inCurr = 'USD';
		this.currencies = currencyConverter.currencies;
		 
		this.total = function total(outCurr){
			return currencyConverter.convert(this.qty * this.cost, this.inCurr, outCurr);
		};		
		this.pay = function pay() {
		      window.alert("Thanks!");
		};
	}])
})(window.angular);