angular.module('myChart')
.factory('d3', function() {
  return d3;
})
.directive('issuesTypeMultiDonutChart', ["d3",
  function(d3){

    function draw(width, height, data) {

    	var canvas = d3.select("#canvas");
    	var art = d3.select("#art");
    	var labels = d3.select("#labels");

    	var jhw_pie = d3.layout.pie()
    	jhw_pie.value(function (d, i) {
    	    return d.count;
    	});

    	combine_pie_data = function(data){
    	    var pies = jhw_pie(data.issueDetails)
    	    var return_data = [],i=0;
    	    for (var i in pies){
    	      var singleData = {pie:pies[i], data:data.issueDetails[i]};
    	      return_data.push(singleData);
    	    }
    	    console.log(return_data);
    	    return return_data;
    	}    	    
    	    
    	var pied_data = jhw_pie(data);
    	var pied_colors = d3.scale.category10();
    	
    	var cDim = {
    	    height: 500,
    	    width: 500,
    	    innerRadius: 50,
    	    outerRadius: 150,
    	    labelRadius: 175
    	};

    	/*svg.attr({
    	    height: cDim.height,
    	    width: cDim.width
    	});*/

    	canvas.attr("transform", "translate(" + (cDim.width / 2) + "," + (cDim.width / 2) + ")");
    	
    	var pied_arc = d3.svg.arc()
    	    .innerRadius(50)
    	    .outerRadius(150);
        
    	/*************New Code************/
    	containers = d3.select("#canvas").selectAll('.state')
    	.data(data)
    	.enter().append('div')
    	.attr('class', 'state')

    	vis = containers.append("svg")
    	.attr("width", 400)
    	.attr("height", 400)
    	.append("g")
    	.attr("transform", "translate(130,160)");
    	
    	vis.append("svg:text")
    	.attr("class", "title")
    	.text(function(d){return d.issueType;})
    	.attr("dy", "-10px")
    	.attr("transform", "translate(170)")
    	.attr("text-anchor", "middle")
    	
    	sects = vis.selectAll('.sect')
    	.data(function(d){return combine_pie_data(d)})
    	.enter().append("g")
    	.attr("class", "sect")
    	.attr("transform", "translate(70)")
    	    	
    	sects.append("svg:path")
    	.attr("d", function(d, i){ 
    		console.log(pied_arc(d.pie));
    		return pied_arc(d.pie); 
    	})
    	.style("fill", function (d, i) {
    	    return pied_colors(i);
    	})
    	.style("stroke", "#333")    	
	
    }
    return{
    	restrict: 'E',
        scope: {
          data: '='
        },
        compile: function( element, attrs, transclude ) {
                    	
            var width = 450, height = 450;
            
            /* Create container */
            var data_container = d3.select(element[0]).append('div').
                                attr('id', 'canvas').attr({height: 450,
            	                                          width: 450});            
            return function(scope, element, attrs) {
              scope.$watch('data', function(newVal, oldVal, scope) {                
                  if (scope.data) {                	  
                     draw(width, height,scope.data);
                  }                  
              }, true);
            };
        }
    }
}]);