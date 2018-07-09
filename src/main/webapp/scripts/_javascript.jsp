<%@page contentType="text/javascript"%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>


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


var intray_off = new Image(); intray_off.src = "<spring:message code="image.tab.intray.url"/>-off.gif";
var intray_lit = new Image(); intray_lit.src = "<spring:message code="image.tab.intray.url"/>-lit.gif";
var authorisations_off = new Image(); authorisations_off.src = "<spring:message code="image.tab.harvestAuthorisiations.url"/>-off.gif";
var authorisations_lit = new Image(); authorisations_lit.src = "<spring:message code="image.tab.harvestAuthorisiations.url"/>-lit.gif";
var targets_off = new Image(); targets_off.src = "<spring:message code="image.tab.targets.url"/>-off.gif";
var targets_lit = new Image(); targets_lit.src = "<spring:message code="image.tab.targets.url"/>-lit.gif";
var groups_off = new Image(); groups_off.src = "<spring:message code="image.tab.groups.url"/>-off.gif";
var groups_lit = new Image(); groups_lit.src = "<spring:message code="image.tab.groups.url"/>-lit.gif";
var instances_off = new Image(); instances_off.src = "<spring:message code="image.tab.instances.url"/>-off.gif";
var instances_lit = new Image(); instances_lit.src = "<spring:message code="image.tab.instances.url"/>-lit.gif";
var reports_off = new Image(); reports_off.src = "<spring:message code="image.tab.reports.url"/>-off.gif";
var reports_lit = new Image(); reports_lit.src = "<spring:message code="image.tab.reports.url"/>-lit.gif";
var management_off = new Image(); management_off.src = "<spring:message code="image.tab.management.url"/>-off.gif";
var management_lit = new Image(); management_lit.src = "<spring:message code="image.tab.management.url"/>-lit.gif";



var pageLoaded = 1;
	
