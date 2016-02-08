angular.module('myChart')
.factory('d3', function() {
  return d3;
})
.directive('issuesTypeBarChart', ["d3",
  function(d3){

    function draw(svg, width, height, data) {
        svg
        .attr('width', width)
        .attr('height', height);
        

        var margin = 30;

        var sum = function(data, stop) {
          return data.reduce(function(prev, d, i){
            if (stop === undefined || i < stop) {
              return prev + d.y;
            }
            return prev;
          }, 0);
        }

        var colors = d3.scale.category10();
        var total = sum(data);

        var arc = d3.svg.arc()
          .innerRadius(40)
          .outerRadius(100)
          .startAngle(function(d, i) {
            if (i) {
              return sum(data, i)/total * 2*Math.PI;
            }
            return 0;
          })
          .endAngle(function(d, i) {
             return sum(data, i + 1)/total * 2*Math.PI;
          });

        svg.select(".data")
          .attr("transform", "translate(" +(width*0.5)+ "," +(height*0.5)+ ")")
          .selectAll("path")
          .data(data)
          .enter().append("path")
          .attr("d", arc)
          .style("fill", function(d, i) { return colors(i); });

        svg.select(".label")
          .attr("transform", "translate(" +(width*0.5)+ "," +(height*0.5)+ ")")
          .selectAll("text")
          .data(data)
          .enter().append("text")
          .attr("text-anchor", "middle")
          .attr("transform", function(d, i) { return "translate(" + arc.centroid(d, i) + ")"; })
          .text(function(d){ return d.x; });
    }
    return{
    	restrict: 'E',
        scope: {
          data: '='
        },
        compile: function( element, attrs, transclude ) {
            
        	var svg = d3.select(element[0]).append('svg');
            var width = 450, height = 450;
            
            /* Create container */
            var data_container = svg.append('g').attr('class', 'data');
            var label_container = svg.append('g').attr('class', 'label');
            
            return function(scope, element, attrs) {
              scope.$watch('data', function(newVal, oldVal, scope) {                
                  if (scope.data) {
                	  var data = scope.data.map(function(d){
                          return {
                            x: d.type,
                            y: d.count
                          }
                        });
                     draw(svg, width, height,data);
                  }                  
              }, true);
            };
        }
    }
}]);