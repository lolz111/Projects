/*
http://stackoverflow.com/questions/1085801/how-to-get-the-selected-value-of-dropdownlist-using-javascript
http://stackoverflow.com/questions/5866169/getting-all-selected-values-of-a-multiple-select-box-when-clicking-on-a-button-u
http://stackoverflow.com/questions/5330030/javascript-get-values-from-multiple-select-option-box
http://stackoverflow.com/questions/4794380/javascript-multiple-selection-select-box-validation
*/

var firstRun = true; 

var w = window.innerWidth;
var h = window.innerHeight;
var r = window.innerHeight / 2.65;
var tweenDuration = 1000;
var textOffset = 14;
var boardingsDeboardings = "";

var lines, valueLabels, nameLabels;
var pieData = [], oldPieData = [], filteredPieData = [], dataAdded;
var labelData, passengerCount = "Loading...";

var pie = d3.layout.pie().value(function(d) { return d; });

var color = d3.scale.category20b();

//arc
var arc = d3.svg.arc()
			    .startAngle(function(d){ return d.startAngle; })
			    .endAngle(function(d){ return d.endAngle; })
			    .innerRadius(r / 2)
			    .outerRadius(r);	

var vis = d3.select("div#pieContainer").append("svg").attr("id", "pie")
			.attr("width", w)
			.attr("height", h);

//arcs/paths group
var arcs = vis.append("g")
			  .attr("class", "arc")
			  .attr("transform", "translate(" + w/2 + ", " + h/2 + ")");

//labels group
var labels = vis.append("g")
					.attr("class", "labels")
					.attr("transform", "translate(" + w/2 + ", " + h/2 + ")");

//center label group
var center_group = vis.append("svg:g")
					  .attr("class", "center_group")
					  .attr("transform", "translate(" + w/2 + "," + h/2 + ")");

// center label
var boardDeboardLabel = center_group.append("svg:text")
							 		.attr("class", "boardDeboardLabel")
									.attr("text-anchor", "middle")							
									.text(passengerCount);

//placeholder for pie
var paths = arcs.append("circle")
				.attr("fill", "DimGray")
				.attr("r", r);

/*
	Updates the Pie if there is new data.
*/
function updatePie()
{	
	oldPieData = filteredPieData;
	pieData = pie(dataAdded);

	filteredPieData = pieData.filter(filterData); 
	function filterData(element, index, array) 
	{
	    element.name = dataAdded[index];
	    element.value = dataAdded[index];
	    return (element.value > 0);
	}

	if((filteredPieData.length > 0 && oldPieData.length > 0) || firstRun)
	{
		firstRun = false; //ensure it runs the first time; set to false after the first run

		arcs.selectAll("circle").remove(); //remove gray placeholder circle
		
		boardDeboardLabel.text("");

		var previousColor;

		//draw pie slices
		paths = arcs.selectAll("path").data(filteredPieData);
		paths.enter().append("path")
			 // .attr("stroke", "DimGray")
			 // .attr("stroke", "Black")
			 // .attr("stroke-width", 0.5)
			 .attr("fill", function(d, i) { return color(i); })
			 .on("mouseover", function(d) {
			 	boardDeboardLabel.text(boardingsDeboardings + " - " + d.value);
			 	previousColor = d3.select(this).attr("fill"); //store the current color for mouseout
			 	d3.select(this).attr("fill", "yellow");
			 })
			 .on("mouseout", function(d) {
			 	boardDeboardLabel.text("");
			 	d3.select(this).attr("fill", previousColor); //set the original color back
			 })
			 .transition()
			 	.duration(tweenDuration)
			 	.attrTween("d", pieTween);
		paths.transition()
			 .duration(tweenDuration)
			 .attrTween("d", pieTween);
		paths.exit()
			 .transition()
			 .duration(tweenDuration)
			 .attrTween("d", removePieTween)
			 .remove();
	
		//draw labels
  		valueLabels = labels.selectAll("text.value").data(filteredPieData)
  							.attr("dy",  function(d) {
						        if ((d.startAngle + d.endAngle) / 2 > Math.PI / 2 && (d.startAngle + d.endAngle)/2 < Math.PI * 1.5 ) { return 5; } 
						        else { return -7; }
						    })
						    .attr("text-anchor", function(d) {
						        if ( (d.startAngle + d.endAngle) / 2 < Math.PI ) { return "beginning"; } 
						        else {  return "end"; }
						    })
						    .text(function(d, i) { /*return d.value;*/ return labelData[i]; });
	    valueLabels.enter().append("text")
	    		   .attr("class", "value")
	    		   .attr("transform", function(d){
				        return "translate(" + Math.cos(((d.startAngle + d.endAngle - Math.PI) / 2)) * (r + textOffset) + "," + Math.sin((d.startAngle + d.endAngle - Math.PI) / 2) * (r + textOffset) + ")";
				   })
				   .attr("dy", function(d){
				       if ((d.startAngle + d.endAngle) / 2 > Math.PI / 2 && (d.startAngle + d.endAngle) / 2 < Math.PI * 1.5 ) { return 5; } 
				       else { return -7; }
			       })
			       .attr("text-anchor", function(d){
				       if ( (d.startAngle + d.endAngle) / 2 < Math.PI ){ return "beginning"; } 
				       else { return "end"; }
				   })
				   .text(function(d, i) { /*return d.value;*/ return labelData[i]; });
		valueLabels.transition().duration(tweenDuration).attrTween("transform", textTween);
    	valueLabels.exit().remove();
	}	
}

function pieTween(d, i) 
{
	var s0, e0;
	if(oldPieData[i])
	{
		s0 = oldPieData[i].startAngle;
		e0 = oldPieData[i].endAngle;
	} 
	else if (!(oldPieData[i]) && oldPieData[i - 1]) 
	{
		s0 = oldPieData[i - 1].endAngle;
		e0 = oldPieData[i - 1].endAngle;
	} 
	else if(!(oldPieData[i - 1]) && oldPieData.length > 0)
	{
		s0 = oldPieData[oldPieData.length - 1].endAngle;
		e0 = oldPieData[oldPieData.length - 1].endAngle;
	} 
	else 
	{
		s0 = 0;
		e0 = 0;
	}

	var i = d3.interpolate({startAngle: s0, endAngle: e0}, {startAngle: d.startAngle, endAngle: d.endAngle});
	return function(t) { return arc(i(t)); };
}

function removePieTween(d, i) 
{
	var s0 = 2 * Math.PI, e0 = 2 * Math.PI;
	var i = d3.interpolate({ startAngle: d.startAngle, endAngle: d.endAngle }, { startAngle: s0, endAngle: e0 });
	return function(t) { return arc(i(t)); };
}

function textTween(d, i) 
{
	var a;

	if(oldPieData[i])
	{
		a = (oldPieData[i].startAngle + oldPieData[i].endAngle - Math.PI)/2;
	} 
	else if (!(oldPieData[i]) && oldPieData[i - 1]) 
	{
		a = (oldPieData[i - 1].startAngle + oldPieData[i - 1].endAngle - Math.PI)/2;
	} 
	else if(!(oldPieData[i - 1]) && oldPieData.length > 0) 
	{
		a = (oldPieData[oldPieData.length - 1].startAngle + oldPieData[oldPieData.length - 1].endAngle - Math.PI)/2;
	} 
	else 
	{
		a = 0;
	}

	var b = (d.startAngle + d.endAngle - Math.PI)/2;
	var fn = d3.interpolateNumber(a, b);

	return function(t) { return "translate(" + Math.cos(fn(t)) * (r + textOffset) + "," + Math.sin(fn(t)) * (r+textOffset) + ")"; };
}