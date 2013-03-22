/* 	  
	http://alignedleft.com/tutorials/d3/axes/
	http://www.w3schools.com/js/js_objects.asp
	http://christopheviau.com/d3_tutorial/
	http://stackoverflow.com/questions/13053935/importing-multiple-csv-files-in-javascript
	http://stackoverflow.com/questions/9268645/d3-creating-a-table-linked-to-a-csv-file
	http://blog.stephenboak.com/2011/08/07/easy-as-a-pie.html
	http://www.w3.org/TR/2001/PR-SVG-20010719/styling.html#ClassAttribute
	http://mbostock.github.com/d3/tutorial/circle.html
	http://jsfiddle.net/stephenboak/hYuPb/
	https://github.com/enjalot/d3/blob/master/src/layout/pie.js
	https://github.com/enjalot/d3/blob/master/src/svg/arc.js 
*/

var busLineNames;
var busStopNames = new Array();
var busLines;

/*
	Initializes main data structure that will be holding all of the data.
*/
function initializeData()
{
	busLineNames = new Array("A", "B", "C", "D", "E", "F", "G", "J", "K", "L", "M", "O", "P", "Q", "S", "T", "V", "W");
	busLines = new Array();

	for(i = 0; i < busLineNames.length; i++) //for each bus line
	{						
		var months = new Array(); //make 12 month array
		for(m = 0; m < 12; m++) //0 - 11
		{
			var days = new Array(); //make 31 day array
			for(d = 0; d <= 31; d++) //1 - 31
			{
				var hours = new Array(); //make 24 hour array
				for(h = 0; h <= 24; h++) //0 - 23
					hours.push(new hour(h, busLineNames[i]));
				days.push(new day(d, hours, busLineNames[i]));
			}
			months.push(new month(m, days, busLineNames[i])); 
		}

		var newBusLine = new busLine(busLineNames[i], months);		
		busLines.push(newBusLine); //initialize busLine
	}
}

function datapoint(stop, timeAndDate, boardCount, deboardCount, route)
{
	this.stop = stop; //string
	this.timeAndDate = timeAndDate; //date
	this.boardCount = boardCount; //int
	this.deboardCount = deboardCount; //int
	this.route = route; //string
}

function boardingEvent(stop, boarding, deboarding)
{
	this.stop = stop;
	this.boarding = boarding;
	this.deboarding = deboarding;
	this.total = function() { return this.boarding + this.deboarding; }
}

function allBoardings()
{
	this.events = new Array(); //array of boardingEvents
	this.stopsSoFar = new Array(); //array of stops that have been accounted for so far
	this.boarding = 0;
	this.deboarding = 0;
	this.total = total;
	function total() 
	{ 
		return this.boarding + this.deboarding; 
	}
	this.enter = enter;
	function enter(board, deboard, stop)
	{
		if(this.stopsSoFar.indexOf(stop) == -1) //if the stop hasn't been encountered yet
		{
			this.events.push(new boardingEvent(stop, board, deboard));
			this.stopsSoFar.push(stop);
		}
		else
		{
			var index = this.stopsSoFar.indexOf(stop);
			this.events[index].boarding += board;
			this.events[index].deboarding += deboard;
		}
		this.boarding += board;
		this.deboarding += deboard;
	}
}

function hour(num, line)
{
	this.num = num;
	this.boardings = new allBoardings();
	this.line = line;
}

function day(num, hours, line)
{
	this.num = num;
	this.hours = hours; //max size 24
	this.boardings = new allBoardings();
	this.line = line;
}

function month(num, days, line)
{
	this.num = num;
	this.days = days; //max size 31
	this.boardings = new allBoardings();
	this.line = line;
}

function busLine(name, months)
{
	this.months = months;
	this.name = name;
}

/*
	Reads all the data from the .csv file
*/
function readAllData()
{
	var formatDate = d3.time.format("%Y-%m-%d%X");
	
	
	d3.csv("data/unitrans-oct-2011.csv", function(data) {
		data.forEach(function(d) {

			var date = formatDate.parse(d.date + d.time);
			var month = date.getMonth();
			var day = date.getDate();
			var hour = date.getHours();

			var busline = busLineNames.indexOf(d.route);
			var boarding = parseInt(d.boarding);
			var deboarding = parseInt(d.deboarding);
			var stop = d.stopTitle;

			busLines[busline].months[month].days[day].hours[hour].boardings.enter(boarding, deboarding, stop);
			busLines[busline].months[month].days[day].boardings.enter(boarding, deboarding, stop);
			busLines[busline].months[month].boardings.enter(boarding, deboarding, stop);

			if(this.busStopNames.indexOf(d.stopTitle) == -1)
				busStopNames.push(d.stopTitle);

		});
	});

}