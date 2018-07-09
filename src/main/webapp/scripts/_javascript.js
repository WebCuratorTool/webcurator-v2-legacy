
function rollOn(img) 
{
	if (document.images) 
	{
		document[img].src = eval(img + '_lit.src');
	}
}

function rollOff(img) 
{
	if (document.images)
	{
		document[img].src = eval(img + '_off.src');
	}
}

var intray_off = new Image(); intray_off.src = "images/in-tray-off.gif";
var intray_lit = new Image(); intray_lit.src = "images/in-tray-lit.gif";
var authorisations_off = new Image(); authorisations_off.src = "images/harvest-authorisations-off.gif";
var authorisations_lit = new Image(); authorisations_lit.src = "images/harvest-authorisations-lit.gif";
var targets_off = new Image(); targets_off.src = "images/targets-off.gif";
var targets_lit = new Image(); targets_lit.src = "images/targets-lit.gif";
var groups_off = new Image(); groups_off.src = "images/groups-off.gif";
var groups_lit = new Image(); groups_lit.src = "images/groups-lit.gif";
var instances_off = new Image(); instances_off.src = "images/target-instances-off.gif";
var instances_lit = new Image(); instances_lit.src = "images/target-instances-lit.gif";
var reports_off = new Image(); reports_off.src = "images/reports-off.gif";
var reports_lit = new Image(); reports_lit.src = "images/reports-lit.gif";
var management_off = new Image(); management_off.src = "images/management-off.gif";
var management_lit = new Image(); management_lit.src = "images/management-lit.gif";



var pageLoaded = 1;
	
