(function(angular){
	'use strict';
	angular.module('finance1',[])
	.factory('currencyConverter',function(){
		
		var currencies = ['EUR','USD','INR'];
		var usdToForeignRates = {
	      USD: 1,
	      EUR: 0.74,
	      INR: 65
	    };		
		var convert = function (amount, inCurr, outCurr) {
			return amount * usdToForeignRates[outCurr] / usdToForeignRates[inCurr];
		};
		
		return {
		    currencies: currencies,
		    convert: convert
		 };
	});
})(window.angular);