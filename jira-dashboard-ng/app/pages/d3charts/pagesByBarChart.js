angular.module('myChart')
.factory('d3', function() {
  return d3;
})
.directive('pagesByBarChart', ["d3",
  function(d3){

    function draw(svg, width, height, data) {
        svg
        .attr('width', width)
        .attr('height', height);

        var margin = 30;
        
        var xScale = d3.scale.ordinal()
          .domain(data.map(function(d,i) {return i; }))
          .rangeRoundBands([0, width], .1);

          //.range([margin, width-margin]);
        //console.log(.map(xScale));
        var xAxis = d3.svg.axis()
          .scale(xScale)
          .orient('bottom');
          //.tickFormat(d3.time.format('%H:%I'));
        
        var yScale = d3.scale.linear()
        .domain([0, d3.max(data, function(d) { return d.count; })])
        .range([height-margin, margin]);      
        var yAxis = d3.svg.axis()
          .scale(yScale)
          .orient('left')
          //.tickFormat(d3.format('f'));
        
        svg.select('.x-axis')
          .attr("transform", "translate(0, " + (height-margin) + ")")
          .call(xAxis);      
        svg.select('.y-axis')
          .attr("transform", "translate(" + margin + ")")
          .call(yAxis);

        svg.select('.x-grid')
          .attr("transform", "translate(0, " + margin + ")")
          .call(xAxis
              .tickSize(height - 2*margin, 0, 0)
              .tickFormat("")
          );
        svg.select('.y-grid')
          .attr("transform", "translate(" + margin + ")")
          .call(yAxis
              .tickSize(-width + 2*margin, 0, 0)
              .tickFormat("")
        );
        
    	var barWidth = (width-10*margin)/data.length;
    	svg.select('.data')
    	   .selectAll('rect').data(data)
    	   .enter()
    	   .append('rect')
    	   .attr('class', 'data-bar');
        svg.select('.data')
          .selectAll('rect').data(data)
          .attr('r', 2.5)
          .attr('x', function(d,i) { return xScale(i) + barWidth; })
          .attr('y', function(d) { return yScale(d.count); })
          .attr('width', function(d) { return barWidth; })
          .attr('height', function(d) { return yScale(0) - yScale(d.count); });

        svg.select('.data')
           .selectAll('rect').data(data)
           .exit()
           .transition().style({opacity: 0})
           .remove();        

    }
    return{
    	restrict: 'E',
        scope: {
          data: '='
        },
        compile: function( element, attrs, transclude ) {
            
        	var svg = d3.select(element[0]).append('svg');

            /* Create container */
            var axis_container = svg.append('g').attr('class', 'axis');
            var data_container = svg.append('g').attr('class', 'data');

            axis_container.append('g').attr('class', 'x-grid grid');
            axis_container.append('g').attr('class', 'y-grid grid');
            
            axis_container.append('g').attr('class', 'x-axis axis');
            axis_container.append('g').attr('class', 'y-axis axis');
            
            data_container.append('path').attr('class', 'data-line');
            data_container.append('path').attr('class', 'data-area');

            // Define the dimensions for the chart
            var width = 450, height = 450;
            
            return function(scope, element, attrs) {
              scope.$watch('data', function(newVal, oldVal, scope) {                
                  if (scope.data) {
                     draw(svg, width, height, scope.data);
                  }                  
              }, true);
            };
        }
    }
}]);