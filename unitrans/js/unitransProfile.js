/*
	Time object
*/
function selectedTime()
{
	this.year = 2012;
	this.month = 9; //string
	this.days = new Array(); //date
	this.hours = new Array(); //int
}

/*
	User profile class
*/
function userProfile()
{
	this.boardingDeboarding;
	this.buslines = new Array(); //buslines selected
	this.busStops = new Array();
	this.currentTime = new selectedTime();
}

var user = new userProfile();

/*
	Converts 24 hour time into 12 hour time
*/
function timeConverter(time)
{
	if(time == 0 || time == 24)
		return "12:00a.m."
	if(time < 12)	
		return time + ":00a.m.";	
	else if(time == 12)
		return time + ":00p.m.";
	else if(time > 12 && time < 24)
		return (time - 12) + ":00p.m.";
}

/*
	Formats selected hours into neat time ranges. (ie. 1pm, 2pm, 3pm, 4pn, 7pm, and 9pm would be formatted into 1pm-4pm, 7pm, 9pm)
*/
function hourFormatter()
{
	var start = parseInt(user.currentTime.hours[0]);
	var count = 1;
	var format = new Array();

	var str = "";

	for(i = 1; i < user.currentTime.hours.length; i++)
	{	
		if(user.currentTime.hours[i] == (start + count))
			count++;		
		else
		{		
			format.push(count);
			count = 1;
			start = parseInt(user.currentTime.hours[i]);
		}
	}

	format.push(count);

	var count = 0;

	for(i = 0; i < format.length; i++)
	{
		var c = format[i];
		str += timeConverter(user.currentTime.hours[count]);
		count += c - 1;

		if(c > 1)
		{
			str += " - ";
			str += timeConverter(user.currentTime.hours[count]);
		}		
		
		if(i + 1 < format.length)
		{
			count++;
			str += ", ";
		}
	}

	return str;
}

/*
	Takes a day from October 2012 and returns what day of the week it was.
*/
function dateConverter(day)
{
	var date = new Date(user.currentTime.year, user.currentTime.month, day);

	switch(date.getDay())
	{
		case 0:
			return "Sunday";
		case 1:
			return "Monday";
		case 2:
			return "Tuesday";
		case 3:
			return "Wednesday";
		case 4:
			return "Thursday";
		case 5:
			return "Friday";
		case 6:
			return "Saturday";
	}
}

/*
	First method that is called after the pate has been loaded (aka after all of the data has finished loading)
*/
function begin()
{
	var select = document.getElementById("stops");
	
	for(i = 0; i < busStopNames.length; i++) //populate the Stop selection
	{
		var option = document.createElement("option");
		option.value = i + 1;
		option.innerHTML = busStopNames[i];
		option.selected = false;
		select.appendChild(option);
	}

	//******************************************* INITIAL OPTION SELECTION ********************************************
	//select boardings, deselect deboardings
	document.getElementById("boardings").checked = true;
	document.getElementById("deboardings").checked = false;

	//select all bus lines
	for(i = 0; i < busLineNames.length; i++) //select all bus lines
		document.getElementById(busLineNames[i]).selected = true;
	
	//deselect daysOfWeek
	var daysOfWeek = document.getElementsByClassName("daysOfWeek");	
	for(i = 0; i < daysOfWeek.length; i++)
		daysOfWeek[i].selected = false;	

	//deselect days
	var days = document.getElementsByClassName("days");
	for(i = 0; i < days.length; i++)
		days[i].selected = false;

	//deselect hours
	var hours = document.getElementsByClassName("hours");
	for(i = 0; i < hours.length; i++)
		hours[i].selected = false;

	//select none options of each selection element
	var noneElements = document.getElementsByClassName("none"); //get all of the none options
	for(i = 1; i < noneElements.length; i++) //skip over the none option for the bus lines since all bus lines are being selected
		noneElements[i].selected = true;

	updateUserProfileSelections();
}

/*
	Updates user profile based on selected option.
*/
function updateUserProfileSelections()
{		
	//Check Boardings/Deboardings selections
	if(document.getElementById("boardings").checked && document.getElementById("deboardings").checked)
		user.boardingDeboarding = 3;
	else if(document.getElementById("boardings").checked)
		user.boardingDeboarding = 1;
	else if(document.getElementById("deboardings").checked)
		user.boardingDeboarding = 2;
	else
		user.boardingDeboarding = 0;

	//bus lines
	var buslineSelectList = document.getElementById("buslines"); //get the selected element
	var allBusLineOptions = buslineSelectList && buslineSelectList.options; //extract all of its options
	var selectedBusLineOptions = new Array();	
	for(i = 1; i < allBusLineOptions.length; i++)
	{
		if(allBusLineOptions[i].selected)
			selectedBusLineOptions.push(allBusLineOptions[i].value);
	}
	user.buslines = selectedBusLineOptions;	

	//days
	var daysSelectList = document.getElementById("days"); //get the selected element
	var allDaysOptions = daysSelectList && daysSelectList.options; //extract all of its options
	var selectedDaysOptions = new Array();	
	for(i = 1; i < allDaysOptions.length; i++)
	{
		if(allDaysOptions[i].selected)
			selectedDaysOptions.push(allDaysOptions[i].value);
	}
	user.currentTime.days = selectedDaysOptions;

	//days of the week
	var daysOfWeekSelectList = document.getElementById("daysOfWeek"); //get the selected element
	var allDaysOfWeekOptions = daysOfWeekSelectList && daysOfWeekSelectList.options; //extract all of its options
	var selectedDaysOfWeekOptions = new Array();	
	for(i = 1; i < allDaysOfWeekOptions.length; i++)
	{
		if(allDaysOfWeekOptions[i].selected)
			selectedDaysOfWeekOptions.push(parseInt(allDaysOfWeekOptions[i].value));
	}	

	if(selectedDaysOfWeekOptions.length > 0) //filter currently selected days with days of the week that were selected
	{
		var newDays = new Array();

		if(user.currentTime.days.length > 0)
		{
			for(i = 0; i < user.currentTime.days.length; i++) //days
			{
				var day = user.currentTime.days[i];
				var date = new Date(user.currentTime.year, user.currentTime.month, day);
				
				if(selectedDaysOfWeekOptions.indexOf(date.getDay()) != -1)
					newDays.push(day);
			}
		}
		if(user.currentTime.days.length == 0)
		{
			for(i = 1; i <= 31; i++) //days
			{
				var day = i;
				var date = new Date(user.currentTime.year, user.currentTime.month, day);

				if(selectedDaysOfWeekOptions.indexOf(date.getDay()) != -1)
					newDays.push(day);
			}
		}

		user.currentTime.days = newDays;
	}

	//hours
	var hoursSelectList = document.getElementById("hours"); //get the selected element
	var allHoursOptions = hoursSelectList && hoursSelectList.options; //extract all of its options
	var selectedHoursOptions = new Array();	
	for(i = 1; i < allHoursOptions.length; i++)
	{
		if(allHoursOptions[i].selected)
			selectedHoursOptions.push(allHoursOptions[i].value);
	}
	user.currentTime.hours = selectedHoursOptions;	

	//stops
	var stopsSelectList = document.getElementById("stops"); //get the selected element
	var allStopsOptions = stopsSelectList && stopsSelectList.options; //extract all of its options
	var selectedStopsOptions = new Array();	
	for(i = 1; i < allStopsOptions.length; i++)
	{
		if(allStopsOptions[i].selected)
			selectedStopsOptions.push(allStopsOptions[i].text);
	}
	user.busStops = selectedStopsOptions;			

	if(user.busStops.length == 0)
		processSelectionData();
	else
		processStopSelectionData();
}

/*
	Processes user options that do not include a Stop.
*/
function processSelectionData()
{
	dataAdded = new Array();
	labelData = new Array();
	var monthYear = "October 2012";
	var descriptionText = monthYear;	

	if(user.buslines.length == 0) //0 buslines
	{
		if(user.currentTime.days.length == 0) //0 days
		{
			if(user.currentTime.hours.length == 0) //0 buslines, 0 days, 0 hours
			{		
				var data = 0;		
				
				for(i = 0; i < busLineNames.length; i++)	
				{	
					if(user.boardingDeboarding == 3)						
						data += busLines[i].months[user.currentTime.month].boardings.total();	
					else if(user.boardingDeboarding == 1)						
						data += busLines[i].months[user.currentTime.month].boardings.boarding;	
					else if(user.boardingDeboarding == 2)						
						data += busLines[i].months[user.currentTime.month].boardings.deboarding;		
				}
				
				if(data > 0)
				{
					dataAdded.push(data);
					labelData.push("");
				}

				if(user.boardingDeboarding == 3)						
					descriptionText = "Total Boardings and Deboardings";	
				else if(user.boardingDeboarding == 1)						
					descriptionText = "Boardings";
				else if(user.boardingDeboarding == 2)						
					descriptionText = "Deboardings";		
				descriptionText += " - " + monthYear;
			}
			else if(user.currentTime.hours.length >= 1) //0 buslines, 0 days, 1/1+ hours(inclusive)
			{
				for(i = 0; i < user.currentTime.hours.length; i++)
				{
					var h = user.currentTime.hours[i];
					var data = 0;
					for(j = 0; j < busLineNames.length; j++)	
					{		
						for(k = 1; k <= 31; k++)
						{	
							if(user.boardingDeboarding == 3)						
								data += busLines[j].months[user.currentTime.month].days[k].hours[h].boardings.total();
							else if(user.boardingDeboarding == 1)						
								data += busLines[j].months[user.currentTime.month].days[k].hours[h].boardings.boarding;
							else if(user.boardingDeboarding == 2)						
								data += busLines[j].months[user.currentTime.month].days[k].hours[h].boardings.deboarding;		
							// data += busLines[j].months[user.currentTime.month].days[k].hours[h].boardings.boarding; //add up all boardings for all lines at selected hour h		
						}			
					}
					
					if(data > 0)
					{
						dataAdded.push(data);
						labelData.push(timeConverter(h));
					}										
				}
				descriptionText = monthYear + " (" + hourFormatter() + ")";
			}
		}
		else if(user.currentTime.days.length == 1) //1 day
		{
			if(user.currentTime.hours.length == 0) //0 buslines, 1 day, 0 hours
			{
				var d = user.currentTime.days[0];
				var data = 0;
				// document.getElementById("test").innerHTML = "day: " + d;
				for(i = 0; i < busLineNames.length; i++)
				{	
					if(user.boardingDeboarding == 3)						
						data += busLines[i].months[user.currentTime.month].days[d].boardings.total();
					else if(user.boardingDeboarding == 1)						
						data += busLines[i].months[user.currentTime.month].days[d].boardings.boarding;
					else if(user.boardingDeboarding == 2)						
						data += busLines[i].months[user.currentTime.month].days[d].boardings.deboarding;			
					// data += busLines[i].months[user.currentTime.month].days[d].boardings.boarding; //add up all boardings for all lines at selected hour h					
				}

				if(data > 0)
				{
					dataAdded.push(data);
					labelData.push("");
				}

				descriptionText = dateConverter(d) + " - October " + d + ", 2012";
			}
			else if(user.currentTime.hours.length >= 1) //0 buslines, 1 day, 1+ hours(inclusive)
			{
				var d = user.currentTime.days[0];

				for(i = 0; i < user.currentTime.hours.length; i++)
				{
					var h = user.currentTime.hours[i];
					var data = 0;

					for(j = 0; j < busLineNames.length; j++)	
					{		
						if(user.boardingDeboarding == 3)						
							data += busLines[j].months[user.currentTime.month].days[d].hours[h].boardings.total();
						else if(user.boardingDeboarding == 1)						
							data += busLines[j].months[user.currentTime.month].days[d].hours[h].boardings.boarding;
						else if(user.boardingDeboarding == 2)						
							data += busLines[j].months[user.currentTime.month].days[d].hours[h].boardings.deboarding;
						// data += busLines[j].months[user.currentTime.month].days[d].hours[h].boardings.boarding; //add up all boardings for all lines at selected hour h					
					}

					if(data > 0)
					{
						dataAdded.push(data);
						labelData.push(timeConverter(h));					
					}
				}

				descriptionText = dateConverter(d) + " - October " + d + ", 2012" + " (" + hourFormatter() + ")";
			}
		}
		else if(user.currentTime.days.length > 1) //1+ days
		{
			if(user.currentTime.hours.length == 0) //0 buslines, 1+ days, 0 hours
			{
				for(i = 0; i < user.currentTime.days.length; i++)
				{
					var d = user.currentTime.days[i];
					var data = 0;

					for(j = 0; j < busLineNames.length; j++)
					{
						if(user.boardingDeboarding == 3)						
							data += busLines[j].months[user.currentTime.month].days[d].boardings.total();
						else if(user.boardingDeboarding == 1)						
							data += busLines[j].months[user.currentTime.month].days[d].boardings.boarding;
						else if(user.boardingDeboarding == 2)						
							data += busLines[j].months[user.currentTime.month].days[d].boardings.deboarding;
						// data += busLines[j].months[user.currentTime.month].days[d].boardings.boarding;
					}
					
					
					if(data > 0)
					{
						dataAdded.push(data);
						labelData.push(dateConverter(d) + " - Oct. " + d);					
					}
				}

				descriptionText = monthYear;
			}
			else if(user.currentTime.hours.length >= 1) //0 buslines, 1+ days, 1+ hours
			{
				for(i = 0; i < user.currentTime.days.length; i++)
				{
					var d = user.currentTime.days[i];
					var data = 0;

					for(j =  0; j < user.currentTime.hours.length; j++)					
					{
						var h = user.currentTime.hours[j];

						for(k = 0; k < busLineNames.length; k++)
						{
							if(user.boardingDeboarding == 3)						
								data += busLines[k].months[user.currentTime.month].days[d].hours[h].boardings.total();
							else if(user.boardingDeboarding == 1)						
								data += busLines[k].months[user.currentTime.month].days[d].hours[h].boardings.boarding;
							else if(user.boardingDeboarding == 2)						
								data += busLines[k].months[user.currentTime.month].days[d].hours[h].boardings.deboarding;
							// data += busLines[k].months[user.currentTime.month].days[d].hours[h].boardings.boarding;
						}
					}					
					
					if(data > 0)
					{
						dataAdded.push(data);
						labelData.push(dateConverter(d) + " - Oct. " + d);					
					}
				}

				descriptionText = monthYear + " (" + hourFormatter() + ")";
			}
		}
	}
	else if(user.buslines.length == 1) //1 buslines 
	{
		if(user.currentTime.days.length == 0) //0 days
		{
			if(user.currentTime.hours.length == 0) //1 busline, 0 days, 0 hours
			{
				var b = busLineNames.indexOf(user.buslines[0]);
				
				var data = 0;

				if(user.boardingDeboarding == 3)						
					data = busLines[b].months[user.currentTime.month].boardings.total();
				else if(user.boardingDeboarding == 1)						
					data = busLines[b].months[user.currentTime.month].boardings.boarding;
				else if(user.boardingDeboarding == 2)						
					data = busLines[b].months[user.currentTime.month].boardings.deboarding;
				// data = busLines[b].months[user.currentTime.month].boardings.boarding;

				if(data > 0)
				{
					dataAdded.push(data);
					labelData.push("");
					// document.getElementById("descriptionText").innerHTML = buslineLetter + " " + buslineNumber + " " + data;
				}

				descriptionText = user.buslines[0] + " Line - " + monthYear;
			}
			else if(user.currentTime.hours.length >= 1) //1 busline, 0 days, 1/1+ hour
			{
				var b = busLineNames.indexOf(user.buslines[0]); //get the single busline

				for(i = 0; i < user.currentTime.hours.length; i++)
				{
					var h = user.currentTime.hours[i];
					var data = 0;
							
					for(j = 1; j <= 31; j++)	
					{
						if(user.boardingDeboarding == 3)						
							data += busLines[b].months[user.currentTime.month].days[j].hours[h].boardings.total();
						else if(user.boardingDeboarding == 1)						
							data += busLines[b].months[user.currentTime.month].days[j].hours[h].boardings.boarding;
						else if(user.boardingDeboarding == 2)						
							data += busLines[b].months[user.currentTime.month].days[j].hours[h].boardings.deboarding;
						// data += busLines[b].months[user.currentTime.month].days[j].hours[h].boardings.boarding; //add up all boardings for all lines at selected hour h										
					}

					if(data > 0)
					{
						dataAdded.push(data);
						labelData.push(timeConverter(h));
					}										
				}

				descriptionText = user.buslines[0] + " Line - " + monthYear + " (" + hourFormatter() + ")";
			}
		}
		else if(user.currentTime.days.length == 1) //1 day
		{
			if(user.currentTime.hours.length == 0) //1 busline, 1 day, 0 hours
			{
				var b = busLineNames.indexOf(user.buslines[0]);
				var d = user.currentTime.days[0];

				var data = 0;

				if(user.boardingDeboarding == 3)						
					data = busLines[b].months[user.currentTime.month].days[d].boardings.total();
				else if(user.boardingDeboarding == 1)						
					data = busLines[b].months[user.currentTime.month].days[d].boardings.boarding;
				else if(user.boardingDeboarding == 2)						
					data = busLines[b].months[user.currentTime.month].days[d].boardings.deboarding;
				// data = busLines[b].months[user.currentTime.month].days[d].boardings.boarding;

				if(data > 0)
				{
					dataAdded.push(data);
					labelData.push("");
				}

				descriptionText = user.buslines[0] + " Line - " + dateConverter(d) + ", October " + d + ", 2012";
			}
			else if(user.currentTime.hours.length >= 1) //1 busline, 1 day, 1/1+ hour
			{
				var b = busLineNames.indexOf(user.buslines[0]); //get the single busline
				var d = user.currentTime.days[0];

				for(i = 0; i < user.currentTime.hours.length; i++)
				{
					var h = user.currentTime.hours[i];
					var data = 0;
					
					if(user.boardingDeboarding == 3)						
						data = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.total();
					else if(user.boardingDeboarding == 1)						
						data = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.boarding;
					else if(user.boardingDeboarding == 2)						
						data = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.deboarding;	
					// data = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.boarding; //add up all boardings for all lines at selected hour h										
					
					if(data > 0)
					{
						dataAdded.push(data);
						labelData.push(timeConverter(h));
					}										
				}

				descriptionText = user.buslines[0] + " Line - " + dateConverter(d) + ", October " + d + ", 2012" + " (" + hourFormatter() + ")";
			}
		}
		else if(user.currentTime.days.length > 1) //1+ days
		{
			if(user.currentTime.hours.length == 0) //1 busline, 1+ days, 0 hours
			{
				var b = busLineNames.indexOf(user.buslines[0]);

				for(i = 0; i < user.currentTime.days.length; i++)
				{
					var d = user.currentTime.days[i];
					var data = 0;

					if(user.boardingDeboarding == 3)						
						data = busLines[b].months[user.currentTime.month].days[d].boardings.total();
					else if(user.boardingDeboarding == 1)						
						data = busLines[b].months[user.currentTime.month].days[d].boardings.boarding;
					else if(user.boardingDeboarding == 2)						
						data = busLines[b].months[user.currentTime.month].days[d].boardings.deboarding;
					// data = busLines[b].months[user.currentTime.month].days[d].boardings.boarding;

					if(data > 0)
					{
						dataAdded.push(data);
						labelData.push(dateConverter(d) + ", Oct. " + d);					
					}
				}

				descriptionText = user.buslines[0] + " Line - " + monthYear;			
			}
			else if(user.currentTime.hours.length >= 1) //1 busline, 1+ days, 1/1+ hour
			{
				var b = busLineNames.indexOf(user.buslines[0]);

				for(i = 0; i < user.currentTime.days.length; i++)
				{
					var d = user.currentTime.days[i];
					var data = 0;

					for(j =  0; j < user.currentTime.hours.length; j++)					
					{
						var h = user.currentTime.hours[j];
						
						if(user.boardingDeboarding == 3)						
							data += busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.total();	
						else if(user.boardingDeboarding == 1)						
							data += busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.boarding;	
						else if(user.boardingDeboarding == 2)						
							data += busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.deboarding;	
						// data += busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.boarding;						
					}					
					
					if(data > 0)
					{
						dataAdded.push(data);
						labelData.push(dateConverter(d) + ", Oct. " + d);					
					}
				}

				descriptionText = user.buslines[0] + " Line - " + monthYear + " (" + hourFormatter() + ")";
			}
		}
	}
	else if(user.buslines.length > 1) //1+ buslines
	{
		if(user.currentTime.days.length == 0) //0 days
		{
			if(user.currentTime.hours.length == 0) //1+ buslines, 0 days, 0 hours
			{
				for(i = 0; i < user.buslines.length; i++)
				{
					var b = busLineNames.indexOf(user.buslines[i]);
					
					var data = 0;

					if(user.boardingDeboarding == 3)						
						data = busLines[b].months[user.currentTime.month].boardings.total();	
					else if(user.boardingDeboarding == 1)						
						data = busLines[b].months[user.currentTime.month].boardings.boarding;
					else if(user.boardingDeboarding == 2)						
						data = busLines[b].months[user.currentTime.month].boardings.deboarding;	
					// data = busLines[b].months[user.currentTime.month].boardings.boarding;

					if(data > 0)
					{
						dataAdded.push(data);
						labelData.push(user.buslines[i]);
					}				
				}
				descriptionText = monthYear;
			}
			else if(user.currentTime.hours.length >= 1) //1+ buslines, 0 days, 1/1+ hours
			{
				for(i = 0; i < user.buslines.length; i++)
				{	
					var b = busLineNames.indexOf(user.buslines[i]);
					var data = 0;

					for(j = 0; j < user.currentTime.hours.length; j++)
					{

						var h = user.currentTime.hours[j];
						for(k = 1; k <= 31; k++)	
						{
							if(user.boardingDeboarding == 3)						
								data += busLines[b].months[user.currentTime.month].days[k].hours[h].boardings.total();	
							else if(user.boardingDeboarding == 1)						
								data += busLines[b].months[user.currentTime.month].days[k].hours[h].boardings.boarding;
							else if(user.boardingDeboarding == 2)						
								data += busLines[b].months[user.currentTime.month].days[k].hours[h].boardings.deboarding;
							// data += busLines[b].months[user.currentTime.month].days[k].hours[h].boardings.boarding; //add up all boardings for all lines at selected hour h																																				
						}
					}

					if(data > 0)
					{										
						dataAdded.push(data);						
						labelData.push(user.buslines[i]);
					}							
				}

				descriptionText = monthYear + " (" + hourFormatter() + ")";
			}
		}
		else if(user.currentTime.days.length == 1) //1 day
		{
			if(user.currentTime.hours.length == 0) //1+ buslines, 1 day, 0 hours
			{
				var d = user.currentTime.days[0];

				for(i = 0; i < user.buslines.length; i++)
				{
					var b = busLineNames.indexOf(user.buslines[i]);									
					var data = 0;

					if(user.boardingDeboarding == 3)						
						data = busLines[b].months[user.currentTime.month].days[d].boardings.total();	
					else if(user.boardingDeboarding == 1)						
						data = busLines[b].months[user.currentTime.month].days[d].boardings.boarding;
					else if(user.boardingDeboarding == 2)						
						data = busLines[b].months[user.currentTime.month].days[d].boardings.deboarding;
					// data = busLines[b].months[user.currentTime.month].days[d].boardings.boarding;

					if(data > 0)
					{
						dataAdded.push(data);
						labelData.push(user.buslines[i]);
					}				
				}
				descriptionText = dateConverter(d) + " - October " + d + ", 2012";
			}
			else if(user.currentTime.hours.length >= 1) //1+ buslines, 1 day, 1/1+ hour
			{
				var d = user.currentTime.days[0];

				for(i = 0; i < user.buslines.length; i++)
				{	
					var b = busLineNames.indexOf(user.buslines[i]);
					var data = 0;

					for(j = 0; j < user.currentTime.hours.length; j++)
					{
						var h = user.currentTime.hours[j];	

						if(user.boardingDeboarding == 3)						
							data += busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.total();
						else if(user.boardingDeboarding == 1)						
							data += busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.boarding;
						else if(user.boardingDeboarding == 2)						
							data += busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.deboarding;				
						// data += busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.boarding; //add up all boardings for all lines at selected hour h																																				
					}

					if(data > 0)
					{										
						dataAdded.push(data);						
						labelData.push(user.buslines[i]);
					}							
				}

				descriptionText = dateConverter(d) + " - October " + d + ", 2012" + " (" + hourFormatter() + ")";
			}
		}
		else if(user.currentTime.days.length > 1) //1+ days
		{
			if(user.currentTime.hours.length == 0) //1+ buslines, 1+ day, 0 hours
			{				
				for(i = 0; i < user.buslines.length; i++)
				{
					var b = busLineNames.indexOf(user.buslines[i]);									
					var data = 0;

					for(j = 0; j < user.currentTime.days.length; j++)
					{
						var d = user.currentTime.days[j];

						if(user.boardingDeboarding == 3)						
							data += busLines[b].months[user.currentTime.month].days[d].boardings.total();
						else if(user.boardingDeboarding == 1)						
							data += busLines[b].months[user.currentTime.month].days[d].boardings.boarding;
						else if(user.boardingDeboarding == 2)						
							data += busLines[b].months[user.currentTime.month].days[d].boardings.deboarding;
						// data += busLines[b].months[user.currentTime.month].days[d].boardings.boarding;
					}

					if(data > 0)
					{
						dataAdded.push(data);
						labelData.push(user.buslines[i]);
					}				
				}
				descriptionText = "October " + user.currentTime.days + ", 2012";
			}
			else if(user.currentTime.hours.length >= 1) //1+ buslines, 1+ days, 1/1+ hour
			{
				for(i = 0; i < user.buslines.length; i++)
				{	
					var b = busLineNames.indexOf(user.buslines[i]);
					var data = 0;

					for(j = 0; j < user.currentTime.days.length; j++)
					{
						var d = user.currentTime.days[j];	

						for(k = 0; k < user.currentTime.hours.length; k++)	
						{		
							var h = user.currentTime.hours[k];

							if(user.boardingDeboarding == 3)						
								data += busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.total();
							else if(user.boardingDeboarding == 1)						
								data += busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.boarding;
							else if(user.boardingDeboarding == 2)						
								data += busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.deboarding;
							// data += busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.boarding; //add up all boardings for all lines at selected hour h		
						}																																				
					}

					if(data > 0)
					{										
						dataAdded.push(data);						
						labelData.push(user.buslines[i]);
					}							
				}

				descriptionText = "October " + user.currentTime.days + ", 2012" + " (" + hourFormatter() + ")";
			}
		}
	}

	for(i = 0; i < busLineNames.length; i++)
	{
		document.getElementById(busLineNames[i]).disabled = false;
	}

	if(dataAdded.length > 0)
	{
		document.getElementById("descriptionText").innerHTML = descriptionText;
		
		if(user.boardingDeboarding == 3)						
			boardingsDeboardings = "Total";
		else if(user.boardingDeboarding == 1)						
			boardingsDeboardings = "Boardings";
		else if(user.boardingDeboarding == 2)						
			boardingsDeboardings = "Deboardings";
		
		updatePie();
	}
	else
	{
		dataAdded.push(1);
		document.getElementById("descriptionText").innerHTML = "";
		updatePie();
		boardDeboardLabel.text("No data available for the current selection.");

		paths = arcs.append("circle")
					.attr("r", r)
					.transition()
					.delay(100)
					.duration(200)
					.attr("fill", "dimgray");					
	}					
}

var previousStop = "";

/*
	Processes user options that includes a Stop
*/
function processStopSelectionData()
{
	dataAdded = new Array();
	labelData = new Array();
	var monthYear = "October 2012";
	var descriptionText = monthYear;	

	for(i = 0; i < busLineNames.length; i++) //enable all elements
		document.getElementById(busLineNames[i]).disabled = false;

	var stop = user.busStops[0];
	var selectedStopLines = new Array();

	for(i = 0; i < busLineNames.length; i++)
	{
		var events = busLines[i].months[user.currentTime.month].boardings.events;

		for(j = 0; j < events.length; j++)
		{
			if(events[j].stop == stop)
			{
				if(selectedStopLines.indexOf(busLineNames[i]) == -1)
					selectedStopLines.push(busLineNames[i]);
			}
		}
	}	

	for(i = 0; i < busLineNames.length; i++) //disable lines that do not have the selected stop
	{
		if(selectedStopLines.indexOf(busLineNames[i]) == -1)
		{
			document.getElementById(busLineNames[i]).disabled = true;
			document.getElementById(busLineNames[i]).selected = false;
		}
	}

	if(previousStop != stop) //if it's a new stop, start by showing data for all  of the lines
	{
		user.buslines = new Array();

		for(i = 0; i < busLineNames.length; i++) //deselect lines that do not have the selected stop		
			document.getElementById(busLineNames[i]).selected = false;			
		
		document.getElementsByClassName("none")[0].selected = true; //select the none option
	}

	previousStop = stop; //update previous stop

	if(user.buslines.length == 0) //0 buslines selected
	{
		if(user.currentTime.days.length == 0) //0 days selected
		{
			if(user.currentTime.hours.length == 0) //0 hours selected
			{
				var data = 0;

				for(i = 0; i < selectedStopLines.length; i++) //bus lines
				{
					var b = busLineNames.indexOf(selectedStopLines[i]);

					for(j = 1; j <= 31; j++) //days
					{					
						var d = j;

						for(k = 1; k < 24; k++) //hours
						{
							var h = k;							
							var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

							for(l = 0; l < events.length; l++)
							{
								if(events[l].stop == stop)
								{
									if(user.boardingDeboarding == 3)						
										data += events[l].total();
									else if(user.boardingDeboarding == 1)						
										data += events[l].boarding;
									else if(user.boardingDeboarding == 2)						
										data += events[l].deboarding;
								}
							}
						}
					}
				}

				if(data > 0) //show all aggregated data for all lines at this stop
				{	
					dataAdded.push(data);						
					labelData.push("");
				}

				descriptionText = stop + " - " + monthYear;	
			}
			else if(user.currentTime.hours.length >= 1) //1/1+ hours selected
			{
				for(k = 0; k < user.currentTime.hours.length; k++) //hours
				{
					var data = 0;
					var h = user.currentTime.hours[k];

					for(i = 0; i < selectedStopLines.length; i++) //bus lines
					{
						var b = busLineNames.indexOf(selectedStopLines[i]);
						
						for(j = 1; j <= 31; j++) //days
						{					
							var d = j;											
							var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

							for(l = 0; l < events.length; l++)
							{
								if(events[l].stop == stop)
								{
									if(user.boardingDeboarding == 3)						
										data += events[l].total();
									else if(user.boardingDeboarding == 1)						
										data += events[l].boarding;
									else if(user.boardingDeboarding == 2)						
										data += events[l].deboarding;
								}
							}
						}
					}
					
					if(data > 0) //show data based on selected hours
					{	
						dataAdded.push(data);						
						labelData.push(timeConverter(h));
					}
				}

				descriptionText = stop + " - " + monthYear + " (" + hourFormatter() + ")";	
			}
		}
		else if(user.currentTime.days.length == 1) //1 day selected
		{
			if(user.currentTime.hours.length == 0) //0 hours selected
			{								
				var d = user.currentTime.days[0]; //day
				var data = 0;

				for(i = 0; i < selectedStopLines.length; i++) //bus lines
				{
					var b = busLineNames.indexOf(selectedStopLines[i]);

					for(k = 1; k < 24; k++) //hours
					{
						var h = k;							
						var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

						for(l = 0; l < events.length; l++)
						{
							if(events[l].stop == stop)
							{
								if(user.boardingDeboarding == 3)						
									data += events[l].total();
								else if(user.boardingDeboarding == 1)						
									data += events[l].boarding;
								else if(user.boardingDeboarding == 2)						
									data += events[l].deboarding;
							}
						}
					}
				}

				if(data > 0) //show data for the selected day
				{	
					dataAdded.push(data);						
					labelData.push(dateConverter(d) + " - Oct. " + d);
				}				

				descriptionText = stop + " - " + "October " + user.currentTime.days + ", 2012";	
			}
			else if(user.currentTime.hours.length >= 1) //1/1+ hours selected
			{									
				var d = user.currentTime.days[0]; //day
				
				for(k = 0; k < user.currentTime.hours.length; k++) //hours
				{
					var h = user.currentTime.hours[k];	
					var data = 0;	

					for(i = 0; i < selectedStopLines.length; i++) //bus lines
					{
						var b = busLineNames.indexOf(selectedStopLines[i]);
						var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

						for(l = 0; l < events.length; l++)
						{
							if(events[l].stop == stop)
							{
								if(user.boardingDeboarding == 3)						
									data += events[l].total();
								else if(user.boardingDeboarding == 1)						
									data += events[l].boarding;
								else if(user.boardingDeboarding == 2)						
									data += events[l].deboarding;
							}
						}
					}

					if(data > 0) //show data based on selected hours
					{	
						dataAdded.push(data);						
						labelData.push(timeConverter(h));
					}	
				}
							
				descriptionText = stop + " - " + "October " + user.currentTime.days + ", 2012" + " (" + hourFormatter() + ")";	
			}
		}
		else if(user.currentTime.days.length > 1) //1+ days selected
		{
			if(user.currentTime.hours.length == 0) //no hours selected
			{
				for(j = 0; j < user.currentTime.days.length; j++) //days
				{					
					var d = user.currentTime.days[j];
					var data = 0;

					for(i = 0; i < selectedStopLines.length; i++) //bus lines
					{
						var b = busLineNames.indexOf(selectedStopLines[i]);

						for(k = 1; k < 24; k++) //hours
						{
							var h = k;							
							var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

							for(l = 0; l < events.length; l++)
							{
								if(events[l].stop == stop)
								{
									if(user.boardingDeboarding == 3)						
										data += events[l].total();
									else if(user.boardingDeboarding == 1)						
										data += events[l].boarding;
									else if(user.boardingDeboarding == 2)						
										data += events[l].deboarding;
								}
							}
						}
					}

					if(data > 0) //show data based on the selected days
					{	
						dataAdded.push(data);						
						labelData.push(dateConverter(d) + " - Oct. " + d);
					}
				}

				descriptionText = stop + " - " + "October " + user.currentTime.days + ", 2012";	
			}
			else if(user.currentTime.hours.length >= 1) //1/1+ hours selected
			{
				for(j = 0; j < user.currentTime.days.length; j++) //days
				{					
					var d = user.currentTime.days[j];
					var data = 0;

					for(i = 0; i < selectedStopLines.length; i++) //bus lines
					{
						var b = busLineNames.indexOf(selectedStopLines[i]);

						for(k = 0; k < user.currentTime.hours.length; k++) //hours
						{
							var h = user.currentTime.hours[k];					
							var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

							for(l = 0; l < events.length; l++)
							{
								if(events[l].stop == stop)
								{
									if(user.boardingDeboarding == 3)						
										data += events[l].total();
									else if(user.boardingDeboarding == 1)						
										data += events[l].boarding;
									else if(user.boardingDeboarding == 2)						
										data += events[l].deboarding;
								}
							}
						}
					}

					if(data > 0) //show data based on the days selected
					{	
						dataAdded.push(data);						
						labelData.push(dateConverter(d) + " - Oct. " + d);
					}
				}

				descriptionText = stop + " - " + "October " + user.currentTime.days + ", 2012" + " (" + hourFormatter() + ")";	
			}
		}
	}
	else if(user.buslines.length == 1) //1 busline selected
	{
		if(user.currentTime.days.length == 0) //0 days selected
		{
			if(user.currentTime.hours.length == 0) //0 hours selected
			{
				var b = busLineNames.indexOf(user.buslines[0]); //bus line
				var data = 0;

				for(j = 1; j <= 31; j++) //days
				{					
					var d = j;

					for(k = 1; k < 24; k++) //hours
					{
						var h = k;							
						var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

						for(l = 0; l < events.length; l++)
						{
							if(events[l].stop == stop)
							{
								if(user.boardingDeboarding == 3)						
									data += events[l].total();
								else if(user.boardingDeboarding == 1)						
									data += events[l].boarding;
								else if(user.boardingDeboarding == 2)						
									data += events[l].deboarding;
							}
						}
					}
				}

				if(data > 0) //show data for the selected busline
				{	
					dataAdded.push(data);						
					labelData.push(user.buslines[0]);
				}

				descriptionText = user.buslines[0] + " Line - " + stop + " - " + monthYear;	
			}
			else if(user.currentTime.hours.length >= 1) // 1/1+ hours selected
			{
				var b = busLineNames.indexOf(user.buslines[0]); //bus line

				for(k = 0; k < user.currentTime.hours.length; k++) //hours
				{
					var data = 0;
					var h = user.currentTime.hours[k];
					
					for(j = 1; j <= 31; j++) //days
					{					
						var d = j;											
						var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

						for(l = 0; l < events.length; l++)
						{
							if(events[l].stop == stop)
							{
								if(user.boardingDeboarding == 3)						
									data += events[l].total();
								else if(user.boardingDeboarding == 1)						
									data += events[l].boarding;
								else if(user.boardingDeboarding == 2)						
									data += events[l].deboarding;
								// data += events[l].boarding;
							}
						}
					}					
					
					if(data > 0) //show data based on the hours for the selected bus line
					{	
						dataAdded.push(data);						
						labelData.push(timeConverter(h));
					}
				}

				descriptionText = user.buslines[0] + " Line - " + stop + " - " + monthYear + " (" + hourFormatter() + ")";	
			}
		}
		else if(user.currentTime.days.length == 1) //1 day selected
		{
			if(user.currentTime.hours.length == 0) //0 hours selected
			{
				var b = busLineNames.indexOf(user.buslines[0]);	//bus line	
				var d = user.currentTime.days[0]; //day
				var data = 0;
				
				for(k = 1; k < 24; k++) //hours
				{
					var h = k;							
					var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

					for(l = 0; l < events.length; l++)
					{
						if(events[l].stop == stop)
						{
							if(user.boardingDeboarding == 3)						
								data += events[l].total();
							else if(user.boardingDeboarding == 1)						
								data += events[l].boarding;
							else if(user.boardingDeboarding == 2)						
								data += events[l].deboarding;
						}
					}
				}					

				if(data > 0) //show data based on the day for the selected bus line
				{	
					dataAdded.push(data);						
					labelData.push(dateConverter(d) + " - Oct. " + d);
				}				

				descriptionText = user.buslines[0] + " Line - " + stop + " - " + "October " + user.currentTime.days + ", 2012";
			}
			else if(user.currentTime.hours.length >= 1) //1/1+ hours selected
			{
				var b = busLineNames.indexOf(user.buslines[0]);	//bus line						
				var d = user.currentTime.days[0]; //day
				

				for(k = 0; k < user.currentTime.hours.length; k++) //hours
				{
					var h = user.currentTime.hours[k];					
					var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;
					var data = 0;

					for(l = 0; l < events.length; l++)
					{
						if(events[l].stop == stop)
						{
							if(user.boardingDeboarding == 3)						
								data += events[l].total();
							else if(user.boardingDeboarding == 1)						
								data += events[l].boarding;
							else if(user.boardingDeboarding == 2)						
								data += events[l].deboarding;
						}
					}

					if(data > 0) //show data based on the hours for the selected day for the selected bus line
					{	
						dataAdded.push(data);						
						labelData.push(timeConverter(h));
					}	
				}										

				descriptionText = user.buslines[0] + " Line - " + stop + " - " + "October " + user.currentTime.days + ", 2012" + " (" + hourFormatter() + ")";	
			}
		}
		else if(user.currentTime.days.length > 1) //1+ days selected
		{
			if(user.currentTime.hours.length == 0) //0 hours selected
			{
				var b = busLineNames.indexOf(user.buslines[0]); //bus line

				for(j = 0; j < user.currentTime.days.length; j++) //days
				{					
					var d = user.currentTime.days[j];
					var data = 0;
					
					for(k = 1; k < 24; k++) //hours
					{
						var h = k;							
						var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

						for(l = 0; l < events.length; l++)
						{
							if(events[l].stop == stop)
							{
								if(user.boardingDeboarding == 3)						
									data += events[l].total();
								else if(user.boardingDeboarding == 1)						
									data += events[l].boarding;
								else if(user.boardingDeboarding == 2)						
									data += events[l].deboarding;
							}
						}
					}					

					if(data > 0) //show data based on the selected day for the selected bus line
					{	
						dataAdded.push(data);						
						labelData.push(dateConverter(d) + " - Oct. " + d);
					}
				}

				descriptionText = user.buslines[0] + " Line - " + stop + " - " + "October " + user.currentTime.days + ", 2012";
			}
			else if(user.currentTime.hours.length >= 1) //1/1+ hours selected
			{
				var b = busLineNames.indexOf(user.buslines[0]); //bus line

				for(j = 0; j < user.currentTime.days.length; j++) //days
				{					
					var d = user.currentTime.days[j];
					var data = 0;

					for(k = 0; k < user.currentTime.hours.length; k++) //hours
					{
						var h = user.currentTime.hours[k];					
						var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

						for(l = 0; l < events.length; l++)
						{
							if(events[l].stop == stop)
							{
								if(user.boardingDeboarding == 3)						
									data += events[l].total();
								else if(user.boardingDeboarding == 1)						
									data += events[l].boarding;
								else if(user.boardingDeboarding == 2)						
									data += events[l].deboarding;
							}
						}
					}					

					if(data > 0) //show data for the selected days on the selected hours for the selected bus line
					{	
						dataAdded.push(data);						
						labelData.push(dateConverter(d) + " - Oct. " + d);
					}
				}

				descriptionText = user.buslines[0] + " Line - " + stop + " - " + "October " + user.currentTime.days + ", 2012" + " (" + hourFormatter() + ")";	
			}
		}
	}
	else if(user.buslines.length > 1) //1+ buslines selected
	{
		if(user.currentTime.days.length == 0) //0 days selected
		{
			if(user.currentTime.hours.length == 0) //0 hours selected
			{
				for(i = 0; i < user.buslines.length; i++) //bus lines
				{
					var data = 0;
					var b = busLineNames.indexOf(user.buslines[i]);

					for(j = 1; j <= 31; j++) //days
					{					
						var d = j;

						for(k = 1; k < 24; k++) //hours
						{
							var h = k;							
							var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

							for(l = 0; l < events.length; l++)
							{
								if(events[l].stop == stop)
								{
									if(user.boardingDeboarding == 3)						
										data += events[l].total();
									else if(user.boardingDeboarding == 1)						
										data += events[l].boarding;
									else if(user.boardingDeboarding == 2)						
										data += events[l].deboarding;
								}
							}
						}
					}

					if(data > 0) //show data based on the selected bus  lines
					{	
						dataAdded.push(data);						
						labelData.push(user.buslines[i]);
					}					
				}

				descriptionText = stop + " - " + monthYear;	
			}
			else if(user.currentTime.hours.length >= 1) //1/1+ hours selected
			{
				for(i = 0; i < user.buslines.length; i++) //bus lines
				{
					var data = 0;
					var b = busLineNames.indexOf(user.buslines[i]);

					for(k = 0; k < user.currentTime.hours.length; k++) //hours
					{
						var h = user.currentTime.hours[k];
						
						for(j = 1; j <= 31; j++) //days
						{					
							var d = j;											
							var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

							for(l = 0; l < events.length; l++)
							{
								if(events[l].stop == stop)
								{
									if(user.boardingDeboarding == 3)						
										data += events[l].total();
									else if(user.boardingDeboarding == 1)						
										data += events[l].boarding;
									else if(user.boardingDeboarding == 2)						
										data += events[l].deboarding;
								}
							}
						}											
					}

					if(data > 0) //show data based on the selected bus lines
					{	
						dataAdded.push(data);						
						labelData.push(user.buslines[i]);
					}				
				}

				descriptionText = stop + " - " + monthYear + " (" + hourFormatter() + ")";	
			}
		}
		else if(user.currentTime.days.length >= 1) //1/1+ days selected
		{
			if(user.currentTime.hours.length == 0) //0 hours selected
			{
				for(i = 0; i < user.buslines.length; i++) //bus lines
				{
					var data = 0;
					var b = busLineNames.indexOf(user.buslines[i]);

					for(j = 0; j < user.currentTime.days.length; j++) //days
					{					
						var d = user.currentTime.days[j];
						
						for(k = 1; k < 24; k++) //hours
						{
							var h = k;							
							var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

							for(l = 0; l < events.length; l++)
							{
								if(events[l].stop == stop)
								{
									if(user.boardingDeboarding == 3)						
										data += events[l].total();
									else if(user.boardingDeboarding == 1)						
										data += events[l].boarding;
									else if(user.boardingDeboarding == 2)						
										data += events[l].deboarding;
								}
							}
						}											
					}					

					if(data > 0) //show data based on the selected bus lines
					{	
						dataAdded.push(data);						
						labelData.push(user.buslines[i]);
					}
				}

				descriptionText = stop + " - " + "October " + user.currentTime.days + ", 2012";
			}
			else if(user.currentTime.hours.length >= 1) //1/1+ hours selected
			{
				for(i = 0; i < user.buslines.length; i++) //bus lines
				{
					var data = 0;
					var b = busLineNames.indexOf(user.buslines[i]);

					for(j = 0; j < user.currentTime.days.length; j++) //days
					{					
						var d = user.currentTime.days[j];
						var data = 0;

						for(k = 0; k < user.currentTime.hours.length; k++) //hours
						{
							var h = user.currentTime.hours[k];					
							var events = busLines[b].months[user.currentTime.month].days[d].hours[h].boardings.events;

							for(l = 0; l < events.length; l++)
							{
								if(events[l].stop == stop)
								{
									if(user.boardingDeboarding == 3)						
										data += events[l].total();
									else if(user.boardingDeboarding == 1)						
										data += events[l].boarding;
									else if(user.boardingDeboarding == 2)						
										data += events[l].deboarding;
								}
							}
						}											
					}					

					if(data > 0) //show data based on the selected bus lines
					{	
						dataAdded.push(data);						
						labelData.push(user.buslines[i]);
					}
				}

				descriptionText = stop + " - " + "October " + user.currentTime.days + ", 2012" + " (" + hourFormatter() + ")";	
			}
		}
	}
			
	if(dataAdded.length > 0) //if new data was added
	{
		document.getElementById("descriptionText").innerHTML = descriptionText;
		
		if(user.boardingDeboarding == 3)						
			boardingsDeboardings = "Total";
		else if(user.boardingDeboarding == 1)						
			boardingsDeboardings = "Boardings";
		else if(user.boardingDeboarding == 2)						
			boardingsDeboardings = "Deboardings";
		updatePie();
	}
	else
	{
		dataAdded.push(1);
		document.getElementById("descriptionText").innerHTML = "";
		updatePie();
		boardDeboardLabel.text("No data available for the current selection.");

		paths = arcs.append("circle")
					.attr("r", r)
					.transition()
					.delay(100)
					.duration(200)
					.attr("fill", "dimgray");					
	}			
}