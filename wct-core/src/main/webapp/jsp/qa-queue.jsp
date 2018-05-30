<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="org.webcurator.ui.target.command.TargetInstanceCommand" %>
<%@page import="org.webcurator.ui.target.command.TargetInstanceSummaryCommand" %>
<%@page import="org.webcurator.domain.model.core.TargetInstance" %>
<%@page import="org.webcurator.ui.common.Constants" %>
<%@page import="org.webcurator.domain.model.auth.Privilege" %>
<%@page import="org.webcurator.domain.model.core.Indicator" %> 

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<link rel="stylesheet" href="styles/blitzer/jquery-ui-1.10.2.custom.min.css" />
<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>
<script src="scripts/jquery-ui-1.10.2.custom.min.js" type="text/javascript"></script>
<script src="scripts/jquery.inview.js" type="text/javascript"></script>

<!--[if IE]>
<style> #frame {
	width: 900px;
	height: 520px;
	border: none;
	zoom: 0.2; 
} 
</style>
<![endif]-->
<style>
#frame {
	width: 900px;
	height: 520px;
	border: none;
	-moz-transform: scale(0.2);
	-moz-transform-origin: 0 0;
	-o-transform: scale(0.2);
	-o-transform-origin: 0 0;
	-webkit-transform: scale(0.2);
	-webkit-transform-origin: 0 0;
}
</style>

<script type="text/javascript"> 
<!-- JQuery Section: ANNOTATIONS HISTORY (VIA AJAX) JAVASCRIPT -->

function hideAllTooltips() {
	// hide all tooltips
	$("#tooltips").children().each(function() {
		$(this).css('visibility', 'hidden');
	});
}
// display the tooltip
// region: 				a jquery reference to the element over which to display the tooltip
// targetOid:			the target oid for which annotation history and status info will be retrieved
// targetInstanceOid:	the target instance oid identifying the start of the historical list
function showTooltip(region, targetOid, targetInstanceOid, requestType) {
	
	var tooltipName = 'tooltip' + region.prop('id');
	var tooltip;
	
	hideAllTooltips();
	if ($('#' + tooltipName).length == 0) {		
		//generate the tooltip div 
		var className;
		if (requestType == '<%=Constants.AJAX_REQUEST_FOR_TI_ANNOTATIONS%>') {
			className='titooltip';
		}
		if (requestType == '<%=Constants.AJAX_REQUEST_FOR_TARGET_ANNOTATIONS%>') {
			className='ttooltip';
		}
		$('#tooltips').append($('<div id="' + tooltipName + '" name="' + tooltipName + '" class="' + className + '"></div>'));
		tooltip = $('#' + tooltipName);
	} else {
		// tooltip has already been generated so show the tooltip
		tooltip = $('#' + tooltipName);
		
		if (requestType == '<%=Constants.AJAX_REQUEST_FOR_TARGET_ANNOTATIONS%>') {
			// reposition the tooltip if this is a target annotation (since we only need one copy)
			resizeTooltip(region, tooltip);
		} else {
			// if the tooltip is not empty
			if(tooltip.html().toLowerCase().indexOf('<table') != -1) {
				// make the tooltip visible
				tooltip.fadeTo(0,0).css('visibility','visible').fadeTo('slow',1);
			}
		}
		return;
	}
	
	// clear its content
	tooltip.empty();
		
	// setup an event handler for the tooltip
	tooltip.mouseleave(function(){ return hideTooltip(tooltip); });
	
	// fetch the new content from the server
	buildTooltip(region, targetOid, targetInstanceOid, tooltip, requestType);

	return true;
}

//performs the required ajax calls to build the tooltip
//targetOid:			the target oid for which annotation history and status info will be retrieved
//targetInstanceOid:	the target instance oid identifying the start of the historical list
//requestType:			request for target annotations or target instance annotations
function buildTooltip(region, targetOid, targetInstanceOid, tooltip, requestType) {
	var request = $.ajax({
		url: "<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/curator/target/annotation-ajax.html",   
		type: "POST",   
		data: {targetOid : targetOid, targetInstanceOid: targetInstanceOid, <%=Constants.AJAX_REQUEST_TYPE%>: requestType},
		dataType: "html"
	});
	
	request.done(function(msg) {
		if (msg.indexOf('<table') != -1) {
			tooltip.html( msg );
			resizeTooltip(region, tooltip);
		}
	});  
		
	request.fail(function(jqXHR, textStatus) {
		alert( "Request failed: cannot connect to server (" + textStatus +")");
	});
}

function resizeTooltip(region, tooltip) {
	
	// get the position of the trigger region
	var offset = region.offset();
	var regionx = offset.left;
	var regiony = offset.top;

	// get the region's width and height
	var regionw = region.width();
	var regionh = region.height();
	
	// get the width and height of the tooltip
	var tooltipw = tooltip.width();
	var tooltiph = tooltip.height();

	// get the height of the window
	var windowh = $(window).height();
	// resize the tooltip if its height exceeds the visible window
	if (regiony + tooltiph > windowh) {
		tooltiph = windowh - regiony;
		var newh = tooltiph - 20;
		// ensure that the resized tooltip is not too small
		if (newh > 40) {
			tooltip.height(tooltiph - 20);
		}
	}
	
	// align the tooltip so that its right hand edge aligns with the right hand edge of the region,
	// and the top of the tooltip lies under the region (ie: under the region's bottom edge)
	var tooltipy = regiony + regionh;
	var tooltipx = regionx + regionw - tooltipw;
	
	// position the tooltip
	tooltip.offset({top: tooltipy, left: tooltipx});
	
	// display the tooltip	
	tooltip.fadeTo(0,0).css('visibility','visible').fadeTo('slow',1);
	return true;

}

// hide the tooltip
// tooltip: a jquery reference to the tooltip element
function hideTooltip(tooltip) {
	// hide the tooltip	
	tooltip.css('visibility', 'hidden');
	return true;
}

</script>

<script type="text/javascript"> 
<!-- JQuery Section: MULTI-SELECTION JAVASCRIPT -->
// TODO: 	move .gif references into messages.properties
// 			move this section into its own jsp
//			replace sortorder column names with the corresponding values from TargetInstanceCommand

// json array of images representing the enabled action buttons
var enabled_buttons = {
		"archive": 'images/multi-archive-enabled.gif',
		"endorse": 'images/multi-endorse-enabled.gif',
		"delete": 'images/multi-delete-enabled.gif',
		"reject": 'images/multi-reject-enabled.gif',
		"delist": 'images/multi-delist-enabled.gif'	
}
// json array of images representing the disabled action buttons
var disabled_buttons = {
		"archive": 'images/multi-archive-disabled.gif',
		"endorse": 'images/multi-endorse-disabled.gif',
		"delete": 'images/multi-delete-disabled.gif',
		"reject": 'images/multi-reject-disabled.gif',
		"delist": 'images/multi-delist-disabled.gif'
}
// json array to map the specific table header sort to the corresponding sort arrow
var sort_arrows = {
		"nameasc": 'images/sort-up-arrow.gif',
		"namedesc": 'images/sort-down-arrow.gif',
		"dateasc": 'images/sort-up-arrow.gif',
		"datedesc": 'images/sort-down-arrow.gif',
		"stateasc": 'images/sort-up-arrow.gif',
		"statedesc": 'images/sort-down-arrow.gif',
		"elapsedtimeasc": 'images/sort-up-arrow.gif',
		"elapsedtimedesc": 'images/sort-down-arrow.gif',
		"datadownloadedasc": 'images/sort-up-arrow.gif',
		"datadownloadeddesc": 'images/sort-down-arrow.gif',
		"urlssucceededasc": 'images/sort-up-arrow.gif',
		"urlssucceededdesc": 'images/sort-down-arrow.gif',
		"percentageurlsfailedasc": 'images/sort-up-arrow.gif',
		"percentageurlsfaileddesc": 'images/sort-down-arrow.gif',
		"crawlsasc": 'images/sort-up-arrow.gif',
		"crawlsdesc": 'images/sort-down-arrow.gif'
}
// array of ascending sort fields (used to toggle the sort arrow)
var sort_fields_asc = ["nameasc", "dateasc", "stateasc", "elapsedtimeasc", "datadownloadedasc", "urlssucceededasc", "percentageurlsfailedasc", "crawlsasc"];
//array of descending sort fields (used to toggle the sort arrow)
var sort_fields_desc = ["namedesc", "datedesc", "statedesc", "elapsedtimedesc", "datadownloadeddesc", "urlssucceededdesc", "percentageurlsfaileddesc", "crawlsdesc"];

// toggle the specified sortable field
// sortfield: the column name to toggle (without the 'asc' or 'desc' suffix)
function toggleField( sortfield ) {
	// fetch the current sort field from the hidden 'sortorder' field
	var currentSort = $('#sortorder').prop("value");
	// if current sort field is different from the clicked field, then change to the clicked field
	if (currentSort != sortfield.data('id') + 'asc' && currentSort != sortfield.data('id') + 'desc')
		currentSort = sortfield.data('id') + 'asc'
	// check if the sortfield is currently ascending
	var idx = jQuery.inArray(currentSort, sort_fields_asc);
	if (idx >= 0) {
		// change the field to descending
		$('#sortorder').prop("value", sort_fields_desc[idx]);
		sortfield.data('id', sort_fields_desc[idx]);
		// submit the filter form
		$("#filter").submit();
		return;
	}
	// check if the sortfield is currently descending
	idx = jQuery.inArray(currentSort, sort_fields_desc);
	if (idx >= 0) {
		// change the field to ascending
		$('#sortorder').prop("value", sort_fields_asc[idx]);
		sortfield.data('id', sort_fields_asc[idx]);
		// submit the filter form
		$("#filter").submit();
		return;	
	}
	// if not currently sorted then sort on the input field
	$('#sortorder').prop("value", sortfield);
	// submit the filter form
	$("#filter").submit();
	return;
}

// check/uncheck all checkboxes within a named parent element
// id: id of the 'selectall' checkbox
// pID: id of the parent container
function jqCheckAll( id, pID )
{
    $( "#" + pID + " :checkbox").each(function(){   	 	
    	if ($(this).prop("disabled")) {
    		return;
    	} else {    
    		// set the checked status of the checkbox to the same status as the 'selectall' checkbox
    		$(this).attr('checked', $('#' + id).is(':checked'));
    	}
    });
}

// Enable/disable all checkboxes that have a specified custom attribute (stored in 'id')
// id: the custom attribute to test eg: 'data-archive' = checkbox can be enabled for the archive action
// pID: parent container id of the checkboxes
function jqEnableAll( id, pID )
{	
	// derive the multi-select action (multi-select commands are prefixed with 'multi-' eg: 'multi-archive')
	var action = id.replace('multi-', '');
	// for each checkbox ...
	$( "#" + pID + " :checkbox").each(function(){
			if (this.id == 'selectall') {
				// disable 'selectall' checkbox whenever the action is switched
				this.disabled = "disabled";	
				// also reset (uncheck) the 'selectall' checkbox
				this.checked = null;
				// also disable the multi-action command button
				$("#apply").prop("disabled", "disabled");
				// display the appropriate disabled image button				
				$("#apply").prop('src', disabled_buttons[action]);
				// disable the rejection reason drop down if the action is reject
				if (action == 'reject') {
					$("#rejection_reason").prop("disabled", "disabled");
				}
				return;
			}

			// if the checkbox contains a multi-action custom attribute (ie: a specific multi-action is enabled)
			if ($(this).data(id)) {
				// enable the checkbox
				this.disabled = null;
	    		// if at least one checkbox is enabled, then enable the 'selectall' checkbox
	    		$("#selectall").prop("disabled", null);
	    		// enable the multi-action command button
	    		$("#apply").prop("disabled", null);
				// display the appropriate disabled image button			
				$("#apply").prop('src', enabled_buttons[action]);
				// enable the rejection reason drop down if the action is reject
				if (action == 'reject') {
					$("#rejection_reason").prop("disabled", null);
				}
				return;
			} else {
				// ensure the checkbox remains disabled
				this.disabled = 'disabled';
				// ensure that its unchecked
				this.checked = null;
				return;
			}			
		});
}

// Switch the radio button action for multi-select (archive, endorse, delete or reject)
// id: the custom attribute to test eg: data-archive = checkbox can be enabled for the archive action
// pID: id of the parent container
function applyAction(id, pID) {
	// set the action name as the value displayed on the action button
	$("#apply").prop('value', id);
	// set the hidden action command ('cmd')
	$('#cmd').prop('value', 'multi-' + id);
	// make the 'rejection reason' drop-down appear only when reject is selected
	if (id == 'reject'){
		$("#rejection_reason").css('visibility', 'visible');
	} else {
		$("#rejection_reason").css('visibility', 'hidden');
	}
	// enable/disable the relevant TIs given a specified action (stored in 'id')
	return jqEnableAll( id, pID );
}

// Apply the submit action for the multi-select form
// interate over the checkboxes and generate the checked ones in the multiselect form, then submit
function submitMultiSelection() {
	var icnt = 0;  // keep a note of the checked checkbox count
	$( "#resultsTable :checkbox").each(function(){
		// skip the 'selectall' checkbox
		if (this.id != 'selectall') {
			if (!this.disabled && this.checked) {
				// generate a hidden checkbox on the multiselect form (to enable submission of the checkbox values)
				var el = $('<input type="checkbox" value="' + this.value + '" name="' + this.id + '"' + ' checked="checked" style="visibility: hidden;" />'); 
				$("#multiSelectForm").append(el); 
				icnt++;
			}
		}
	});
	// if the checked checkbox count is 0, then there is nothing to do
	if (icnt == 0) return;
	
	// otherwise construct the confirmation dialog
	var action = $('#cmd').prop('value');
	var msgAction = action.replace('multi-', '');
	
	// if this is a reject action, populate the hidden rejection reason
	if (msgAction == 'reject') {
		$('#rejReasonId').prop('value', $('#rejection_reason').prop('value'));
	}

	var proceed=confirm('Do you really want to ' + msgAction + ' the ' + icnt + ' Target Instances selected?'); 
	if (proceed) {
		// submit the multi-select form
		$("#multiSelectForm").submit();
	} else { 
		return false; 
	}
	
}

// copies contents of an iframe to a frame (div) denoted by the data-frame attribute
function loadFrame() {
	var frameName = $('#loader').data('frameName');
	var frame = $('#' + frameName);
	alert('ready: ' + $('#loader').data('ready'));
	alert('copying content into frame ' + frameName);
	alert($('#loader').contents().toString());
	frame.html($('#loader').contents().find('body'));
	$('#loader').data('ready', 'true');
}

//$(window).bind('beforeunload', function(){     return "Do you want to leave this page?"; }); 

// the JQuery body onload function
$(document).ready(function() {
	// bind the inview event to the div
	$('iframe').each(function() {
		$(this).one('inview', function (event, visible) {  
			if (visible == true) {    
				// element is now visible in the viewport
				// load the iframe
				$(this).prop('src', $(this).data('url'));
			} else {    
				// element has gone out of viewport  
			};
		});
	});
	// kick the window event to load the current iframes in view
	$(window).scroll();
	// apply the table header sort arrows
	var sortorder = $("#sortorder").prop("value");
	if (sortorder != null) {
		var el = '<img src="' + sort_arrows[sortorder] + '" />'; 
		$('#' + sortorder).append(el);
	}
	// check the archive radio button by default
	$("#archive").prop("checked", "checked");
	// enabled the appropriate selections
	
	$('#dateEntryStart').datepicker({dateFormat: 'dd/mm/yy 00:00:00', changeMonth: true, changeYear: true, showOtherMonths: true, selectOtherMonths: true, showButtonPanel: true});
	$('#dateEntryEnd').datepicker({dateFormat: 'dd/mm/yy 23:59:59', changeMonth: true, changeYear: true, showOtherMonths: true, selectOtherMonths: true, showButtonPanel: true});
	
	return applyAction($("#archive").prop('id'), 'resultsTable');
});


</script>
<script>
  function setPageNumber(pageNumber) {
	document.filter2.<%=TargetInstanceCommand.PARAM_PAGE%>.value=pageNumber; 
	document.filter2.<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_SHOW_PAGE%>'; 
	document.filter2.submit();
  }

  function setPageSize(pageSize) {
	document.filter2.<%=TargetInstanceCommand.PARAM_PAGESIZE%>.value=pageSize; 
	document.filter2.<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_SHOW_PAGE%>'; 
	document.filter2.submit();
  }
</script>

<script>

function clickEndorse(hrOid) { 
    if( confirm('<spring:message code="ui.label.targetinstance.results.confirmEndorse" javaScriptEscape="true"/>')) {
      document.forms['tabForm'].<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_ENDORSE%>'; 
      document.forms['tabForm'].<%=TargetInstanceCommand.PARAM_HR_ID%>.value=hrOid; 
      document.forms['tabForm'].submit(); 
    }
    return false;
  }

  function viewH3ScriptConsole(instanceOid) {
    //alert('Instance Oid ' + instanceOid);
    var url = '<%= basePath %>curator/target/h3ScriptConsole.html?targetInstanceOid=' + instanceOid;
    var winObj = window.open(url, 'h3ScriptConsole', 'menubar=no,scrollbars=yes,status=no,toolbar=no,resizable=yes,width=800,height=600', true);
    winObj.focus();
  }

</script>


<jsp:include page="include/useragencyfilter.jsp"/>
<form name="filter" id="filter" method="POST" action="<c:out value="${action}"/>">
<c:if test="${command.queuePaused}">
	<table border="1px" BORDERCOLOR="#FF0000" bgcolor="#F4F0E7" id="messageBox">  	
		<tr valign="top">
			<td><font color="black" size="2"><strong>Harvesting of Queued and Scheduled Target Instances has been suspended.</strong></font></td>
		</tr>
	</table>
</c:if>


<div id="tooltips" name="tooltips"></div>

<div id="searchBox">
	<img src="images/search-box-top.gif" alt="Search" width="900" height="36" border="0" /><br/>
	<div id="searchBoxContent">
		<table cellpadding="0" cellspacing="3" border="0">
		<tr>
			<td class="searchBoxLabel">
				ID:<br />
				<input type="text" name="<%=TargetInstanceCommand.PARAM_SEARCH_OID%>" value="<c:out value="${command.searchOid}"/>" style="width:70px;" />
			</td>		
			<td class="searchBoxLabel">
				From:<br />
				<input type="text" id="dateEntryStart" name="<%=TargetInstanceCommand.PARAM_FROM%>" value="<wct:date type="fullDateTime" value="${command.from}"/>" maxlength="19" size="19" style="width:120px;" />
			</td>
			<td class="searchBoxLabel">
				To:<br />
				<input type="text" id="dateEntryEnd" name="<%=TargetInstanceCommand.PARAM_TO%>" value="<wct:date type="fullDateTime" value="${command.to}"/>" maxlength="19" size="19" style="width:120px;" />
			</td>
			<td class="searchBoxLabel">
				Agency:<br />
				<select name="<%=TargetInstanceCommand.PARAM_AGENCY%>" id="agency" onchange="javascript:onAgencyChange('agency', 'owner')">
				<c:choose>
					<c:when test="${command.agency eq ''}">
						<option value="" selected="selected"></option>
					</c:when>
					<c:otherwise>
						<option value=""></option>
					</c:otherwise>
				</c:choose>				
				<c:forEach items="${agencies}" var="a">
				<c:choose>
					<c:when test="${command.agency eq a.name}">
						<option value="<c:out value="${a.name}"/>" selected="selected"><c:out value="${a.name}"/></option>
					</c:when>
					<c:otherwise>
						<option value="<c:out value="${a.name}"/>"><c:out value="${a.name}"/></option>
					</c:otherwise>
				</c:choose>				
				</c:forEach>
			</select>
			</td>
			<td class="searchBoxLabel">
				Owner:<br />
				<select name="<%=TargetInstanceCommand.PARAM_OWNER%>" id="owner">
				<c:choose>
					<c:when test="${command.owner eq ''}">
						<option value="" selected="selected"></option>
					</c:when>
					<c:otherwise>
						<option value=""></option>
					</c:otherwise>
				</c:choose>				
				<c:forEach items="${owners}" var="o">
				<c:choose>
					<c:when test="${command.owner eq o.username}">
						<option value="<c:out value="${o.username}"/>" selected="selected"><c:out value="${o.firstname}"/>&nbsp;<c:out value="${o.lastname}"/></option>
					</c:when>
					<c:otherwise>
						<option value="<c:out value="${o.username}"/>"><c:out value="${o.firstname}"/>&nbsp;<c:out value="${o.lastname}"/></option>
					</c:otherwise>
				</c:choose>				
				</c:forEach>
			</select>
			</td>
			<td class="searchBoxLabel">
				<input type="hidden" name="sortorder" id="sortorder" value="${command.sortorder}">
			</td>
			<td class="searchBoxLabel">
			</td>
		</tr>
		</table>
		<table cellpadding="0" cellspacing="3" border="0" width="100%">
			<tr>								
				<td class="searchBoxLabel" valign="top">
					<table cellpadding="0" cellspacing="0" border="0">
						<tr><td>&nbsp;</td></tr>
						<tr>
					         <td class="searchBoxLabel" valign="top">Name:</td>
					    </tr>
					    <tr>
					         <td class="searchBoxLabel" valign="top"><input type="text" name="name" value="<c:out value="${command.name}" />" maxlength="255" /></td>
					    </tr>
					</table>
				</td>
				<td class="searchBoxLabel" valign="top">
					<table cellpadding="0" cellspacing="0" border="0">
						<tr><td>&nbsp;</td></tr>
						<tr>
					         <td class="searchBoxLabel" valign="top" align="right">Flag:&nbsp; 
					          
					          	<select id="flagOid" name="flagOid" style="width: 92px; height: 18px;">
					          		<option style="font-size: 11px; background-color: #ffffff; color: #000000" value="" <c:if test="${command.flagOid eq null}">selected</c:if>  >None</option>
				   					<c:forEach items="${flags}" var="flag">
										<option style="font-size: 11px; background-color: #${flag.rgb}; color: #${flag.complementRgb}" value="${flag.oid}" <c:if test="${command.flagOid ne null && flag.oid eq command.flagOid}">selected</c:if>  >${flag.name}</option>	   					
									</c:forEach>
				   				</select>
					         
					    </tr>
					    <tr>
					         <td class="searchBoxLabel" valign="top" align="right">
								<authority:hasPrivilege privilege="<%=Privilege.MODIFY_TARGET%>" scope="<%=Privilege.SCOPE_NONE%>">
									Non-Display&nbsp;Only:&nbsp;<input type="checkbox" name="nondisplayonly" id="nondisplayonly" ${command.nondisplayonly ? 'checked' : ''} />
								</authority:hasPrivilege>
					         </td>
					    </tr>
					</table>
				</td>
				<td class="searchBoxLabel" valign="top">
					<table cellpadding="0" cellspacing="0" border="0">
					<tr><td>State:</td></tr>
					<tr>
				         <td class="searchBoxLabel" valign="top"><input type="checkbox" id="states_<%=TargetInstance.STATE_SCHEDULED%>" name="states" value="<%=TargetInstance.STATE_SCHEDULED%>" ${wct:containsObj(command.states, 'Scheduled') ? 'checked' : ''} />
		  					<label for="states_<%=TargetInstance.STATE_SCHEDULED%>">
							<%=TargetInstance.STATE_SCHEDULED%> 
						  	</label>
						 </td>
						 <td class="searchBoxLabel" valign="top"><input type="checkbox" id="states_<%=TargetInstance.STATE_QUEUED%>" name="states" value="<%=TargetInstance.STATE_QUEUED%>" ${wct:containsObj(command.states, 'Queued') ? 'checked' : ''} />
						  	<label for="states_<%=TargetInstance.STATE_QUEUED%>">
							<%=TargetInstance.STATE_QUEUED%> 
						  	</label>
						</td>
						<td class="searchBoxLabel" valign="top"><input type="checkbox" id="states_<%=TargetInstance.STATE_RUNNING%>" name="states" value="<%=TargetInstance.STATE_RUNNING%>" ${wct:containsObj(command.states, 'Running') ? 'checked' : ''} />
						  	<label for="states_<%=TargetInstance.STATE_RUNNING%>">
							<%=TargetInstance.STATE_RUNNING%> 
						  	</label>
						</td>
						<td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_PAUSED%>" name="states" value="<%=TargetInstance.STATE_PAUSED%>" ${wct:containsObj(command.states, 'Paused') ? 'checked' : ''} />
						  	<label for="states_<%=TargetInstance.STATE_PAUSED%>">
							<%=TargetInstance.STATE_PAUSED%> 
						  	</label>
						</td>	
						<td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_HARVESTED%>" name="states" value="<%=TargetInstance.STATE_HARVESTED%>" ${wct:containsObj(command.states, 'Harvested') ? 'checked' : ''} />
						  	<label for="states_<%=TargetInstance.STATE_HARVESTED%>">
							<%=TargetInstance.STATE_HARVESTED%> 
						  	</label>
						</td>
				    </tr>				    
				    <tr>				    	
						<td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_ABORTED%>" name="states" value="<%=TargetInstance.STATE_ABORTED%>" ${wct:containsObj(command.states, 'Aborted') ? 'checked' : ''} />
						  	<label for="states_<%=TargetInstance.STATE_ABORTED%>">
							<%=TargetInstance.STATE_ABORTED%> 
						  	</label>
						</td>
					    <td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_ENDORSED%>" name="states" value="<%=TargetInstance.STATE_ENDORSED%>" ${wct:containsObj(command.states, 'Endorsed') ? 'checked' : ''} />
						  	<label for="states_<%=TargetInstance.STATE_ENDORSED%>">
							<%=TargetInstance.STATE_ENDORSED%> 
						  	</label>
						</td>	
						<td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_REJECTED%>" name="states" value="<%=TargetInstance.STATE_REJECTED%>" ${wct:containsObj(command.states, 'Rejected') ? 'checked' : ''} />
						  	<label for="states_<%=TargetInstance.STATE_REJECTED%>">
							<%=TargetInstance.STATE_REJECTED%> 
						  	</label>
						</td>	
						<td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_ARCHIVED%>" name="states" value="<%=TargetInstance.STATE_ARCHIVED%>" ${wct:containsObj(command.states, 'Archived') ? 'checked' : ''} />
						  	<label for="states_<%=TargetInstance.STATE_ARCHIVED%>">
							<%=TargetInstance.STATE_ARCHIVED%> 
						  	</label>
						</td>
						<td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_ARCHIVING%>" name="states" value="<%=TargetInstance.STATE_ARCHIVING%>" ${wct:containsObj(command.states, 'Archiving') ? 'checked' : ''} />
						  	<label for="states_<%=TargetInstance.STATE_ARCHIVING%>">
							<%=TargetInstance.STATE_ARCHIVING%> 
						  	</label>
						</td>						
				    </tr>
					</table>
				</td>				
				
				<td>
					<table border="0" cellpadding="0" cellspacing="0" >
						<tr>
							<td colspan="2" class="searchBoxLabel" valign="top">QA Recommendation:</td>
						</tr>
						<tr>
							<td class="searchBoxLabel" valign="top"><input type="checkbox" id="recommendationFilter" name="recommendationFilter" value="Archive" ${wct:containsObj(command.recommendationFilter, 'Archive') ? 'checked' : ''} />Archive</td>
							<td class="searchBoxLabel" valign="top"><input type="checkbox" id="recommendationFilter" name="recommendationFilter" value="Reject" ${wct:containsObj(command.recommendationFilter, 'Reject') ? 'checked' : ''} />Reject</td>
						</tr>
						<tr>
							<td class="searchBoxLabel" valign="top"><input type="checkbox" id="recommendationFilter" name="recommendationFilter" value="Investigate" ${wct:containsObj(command.recommendationFilter, 'Investigate') ? 'checked' : ''} />Investigate</td>
							<td class="searchBoxLabel" valign="top"><input type="checkbox" id="recommendationFilter" name="recommendationFilter" value="Delist" ${wct:containsObj(command.recommendationFilter, 'Delist') ? 'checked' : ''} />Delist</td>
						</tr>
						<tr>
							<td class="searchBoxLabel" valign="top"><input type="checkbox" id="recommendationFilter" name="recommendationFilter" value="Failed" ${wct:containsObj(command.recommendationFilter, 'Failed') ? 'checked' : ''} />Failed</td>
							<td class="searchBoxLabel" valign="top">&nbsp;</td>
						</tr>
					</table>
				</td>
				
				<td align="right" valign="middle">
					<table cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td>
					         <input type="image" src="images/search-box-btn.gif" alt="apply" width="82" height="24" border="0" />
		  					 <input type="hidden" name="<%=TargetInstanceCommand.PARAM_CMD%>" value="<%=TargetInstanceCommand.ACTION_FILTER%>" />
		  					</td>
					    </tr>
					    <tr>
							<td>					    
					         <input type="image" src="images/search-box-reset-btn.gif" alt="reset" width="82" height="24" border="0" onclick="javascript:document.filter.<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_RESET%>'" />
					        </td>
					    </tr>
					</table>					
				</td>

			</tr>
		</table>
	</div>
	<img src="images/search-box-btm.gif" alt="" width="900" height="12" border="0" /></div>
</form>
<div id="resultsTable">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td colspan="2"><span class="midtitleGrey">Results</span></td>
			<td colspan="13" class="searchBoxLabel" align="right">
			<table border="0"><tr>
				<td colspan="4" class="multiselectradio">
				Multi-select Action:
				<input type="radio" name="action" id="delist" onclick="applyAction(this.id, 'resultsTable');"><%=TargetInstanceCommand.ACTION_DELIST%></input>
				<input type="radio" name="action" id="archive" onclick="applyAction(this.id, 'resultsTable');" /><%=TargetInstanceCommand.ACTION_ARCHIVE%></input>
				<input type="radio" name="action" id="endorse" onclick="applyAction(this.id, 'resultsTable');"><%=TargetInstanceCommand.ACTION_ENDORSE%></input>
				<input type="radio" name="action" id="delete" onclick="applyAction(this.id, 'resultsTable');"><%=TargetInstanceCommand.ACTION_DELETE%></input>
				<input type="radio" name="action" id="reject" onclick="applyAction(this.id, 'resultsTable');"><%=TargetInstanceCommand.ACTION_REJECT%></input>
				</td><td>
				<input type="image" name="apply" id="apply" src="images/blank-button.gif" alt="apply multi-select action" width="82" height="24" border="0" onclick="submitMultiSelection();" />
	  			</td><td>
	  			<select name="<%=TargetInstanceCommand.PARAM_REASON%>" id="rejection_reason" style="visibility: hidden;">				
					<c:forEach items="${reasons}" var="o">
						<option value="<c:out value="${o.oid}"/>"><c:out value="${o.name}"/></option>
					</c:forEach>
				</select>&nbsp;
				</td>
			</tr></table>
			</td>
		</tr>
		<tr>
			<td class="tableHead"><input type="checkbox" name="selectall" id="selectall" disabled="disabled" onclick="jqCheckAll(this.id, 'resultsTable');" /></td>
			<td class="tableHead">Thumbnail</td>
			<td class="tableHead"><img src="images/flag-icon-grey.gif"/></td>
			<td class="tableHead"></td>
			<td class="sortTableHead" data-id="name" onclick="toggleField($(this));">Name&nbsp;<span id="nameasc" /><span id="namedesc" /></td>
			<td class="sortTableHead" data-id="date" onclick="toggleField($(this));"><spring:message code="ui.label.queue.search.harvestDate"/>&nbsp;<span id="dateasc" /><span id="datedesc" /></td>
			<td class="sortTableHead" data-id="state" onclick="toggleField($(this));">State&nbsp;<span id="stateasc" /><span id="statedesc" /></td>
			<td class="tableHead">Owner</td>
			<td class="sortTableHead" data-id="elapsedtime" onclick="toggleField($(this));">Run Time&nbsp;<span id="elapsedtimeasc" /><span id="elapsedtimedesc" /></td>
			<td class="sortTableHead" data-id="datadownloaded" onclick="toggleField($(this));">Data Downloaded&nbsp;<span id="datadownloadedasc" /><span id="datadownloadeddesc" /></td>
			<td class="sortTableHead" data-id="urlssucceeded" onclick="toggleField($(this));">URLs&nbsp;<span id="urlssucceededasc" /><span id="urlssucceededdesc" /></td>
			<td class="sortTableHead" data-id="percentageurlsfailed" onclick="toggleField($(this));">%&nbsp;Failed&nbsp;<span id="percentageurlsfailedasc" /><span id="percentageurlsfaileddesc" /></td>
			<td class="sortTableHead" data-id="crawls" onclick="toggleField($(this));">Crawls&nbsp;<span id="crawlsasc" /><span id="crawlsdesc" /></td>
			<td class="tableHead">QA&nbsp;Recom&nbsp;</td>
			<td class="tableHead" style="width:300px;">Action</td>
		</tr>
		<c:set var="count" scope="page" value="0"/>
	<c:forEach items="${targetInstances.list}" var="instance">
	<tr>
	<fmt:setLocale value="en_GB" />
	<form name="targetInstance<c:out value="${count}"/>" method="POST" action="<%=Constants.CNTRL_TI%>">
		<td class="tableRowLite">
		<fmt:formatNumber var="lhr_idx" value="${fn:length(instance.harvestResults)-1}" pattern="0" />
		
		<input type="checkbox" name="multiselect" id="multiselect"  	
		<c:if test="${instance.state eq 'Scheduled' || instance.status eq null}">
			<authority:showControl ownedObject="${instance}" privileges='<%=Privilege.MANAGE_TARGET_INSTANCES + ";" + Privilege.MANAGE_WEB_HARVESTER%>' editMode="true">
				<authority:show>
				 value="<c:out value="${instance.oid}"/>" 
				data-delete="true" 
				</authority:show>
			</authority:showControl>
		</c:if>
			<authority:showControl ownedObject="${instance}" privileges='<%=Privilege.MANAGE_TARGET_INSTANCES + ";" + Privilege.MANAGE_WEB_HARVESTER%>' editMode="true">
				<authority:show>
				<c:choose>
				<c:when test="${instance.state eq 'Harvested'}">
				<authority:hasPrivilege privilege="<%=Privilege.ENDORSE_HARVEST%>" scope="<%=Privilege.SCOPE_OWNER%>">    					
				 value="<c:out value='${instance.oid}'/>" 
				 data-endorse="true" 
				 data-reject="true"  
				 <c:if test="${futureScheduleCount[instance.target.oid] gt 0}">
				 data-delist="true" 
				 </c:if>
				</authority:hasPrivilege>
				</c:when>
				</c:choose>
				</authority:show>
			</authority:showControl>
		
		<c:if test="${instance.state eq 'Endorsed'}">
			<authority:hasUserOwnedPriv ownedObject="${instance}" privilege="<%=Privilege.ARCHIVE_HARVEST%>" scope="<%=Privilege.SCOPE_OWNER%>">
				 value="<c:out value='${instance.oid}'/>" 
				 data-archive="true" 
				 <c:if test="${futureScheduleCount[instance.target.oid] gt 0}">
				 data-delist="true" 
				 </c:if>
			</authority:hasUserOwnedPriv>
		</c:if>
		disabled="disabled" />
		
		</td>
		<td class="tableRowLite" style="cellpadding: 0px; cellspacing: 0px;">
		<c:choose>				
		<c:when test="${instance.state eq 'Harvested' || instance.state eq 'Endorsed' || instance.state eq 'Archived'}">
		<div style="width: <c:out value='${thumbnailWidth}' /> height: <c:out value='${thumbnailHeight}' /> " >
			<c:if test="${thumbnailRenderer eq 'browseTool'}">
				<iframe id="frame" name="frame" src="" data-url="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/<c:out value='${browseUrls[instance.oid]}'/>" ></iframe>
			</c:if>
			<c:if test="${thumbnailRenderer eq 'accessTool'}">
				<iframe id="frame" name="frame" src="" data-url="<c:out value='${browseUrls[instance.oid]}'/>" ></iframe>
			</c:if>
		</div>
		</c:when>
		<c:otherwise><div width="100%" align="center">--</div></c:otherwise>
		</c:choose>
		</td>
		<td class="tableRowLite">
			<c:if test="${instance.flag ne null}">
				<div style="width: 18px; height: 18px; background-color: #${instance.flag.rgb};"><img src="images/flag-icon-alpha.png" alt="${instance.flag.name}" /></div>
			</c:if>
		</td>
		<td class="tableRowLite">					
			<c:if test="${instance.alertable == true}">
				<img src="images/warn.gif" alt="Annotations with Alerts!" width="9" height="9" border="0" />
	 		</c:if>
		</td>
		<c:choose>
		<c:when test="${instance.display == false}" >
		<td class="tableRowGreyedOut" >
			<input type="hidden" name="targetInstanceId" value="<c:out value="${instance.oid}"/>"/>
			<input type="hidden" name="cmd" value="<%=TargetInstanceCommand.ACTION_VIEW%>"/>
			<div>
				<c:out value="${instance.target.name}"/>
			</div>
		</td>
		</c:when>
		<c:otherwise>
			<c:choose>
			<c:when test="${instance.firstFromTarget == true}" >
				<td>
				<table cellspacing="0" cellpadding="0" width="100%">
					<tr>
					<td class="tableRowNewTI">
					<input type="hidden" name="targetInstanceId" value="<c:out value="${instance.oid}"/>"/>
					<input type="hidden" name="cmd" value="<%=TargetInstanceCommand.ACTION_VIEW%>"/>
					<div>
					<a href="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/curator/target/qatisummary.html?<%=TargetInstanceSummaryCommand.PARAM_OID%>=${instance.oid}">
						<c:out value="${instance.target.name}"/>
					</a>
					</div>
					</td>
					<td class="noteImage">
					<div name="target<c:out value="${instance.oid}" />" id="target<c:out value="${instance.oid}" />" onclick="showTooltip($(this), <c:out value="${instance.target.oid}"/>, null, '<%=Constants.AJAX_REQUEST_FOR_TARGET_ANNOTATIONS%>');">
						<img src="images/note_blue.gif" />
					</div>
					</td></tr>
				</table>
				</td>
			</c:when>
			<c:otherwise> 
				<td>
				<table cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td class="tableRowLiteTargetName">
					<input type="hidden" name="targetInstanceId" value="<c:out value="${instance.oid}"/>"/>
					<input type="hidden" name="cmd" value="<%=TargetInstanceCommand.ACTION_VIEW%>"/>
					<div>
					<a href="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/curator/target/qatisummary.html?<%=TargetInstanceSummaryCommand.PARAM_OID%>=${instance.oid}">
						<c:out value="${instance.target.name}"/>
					</a>
					</div>
					</td>
					<td class="noteImage">
					<div name="target<c:out value="${instance.oid}" />" id="target<c:out value="${instance.oid}" />" onclick="showTooltip($(this), <c:out value="${instance.target.oid}"/>, null, '<%=Constants.AJAX_REQUEST_FOR_TARGET_ANNOTATIONS%>');">
						<img src="images/note_blue.gif" />
					</div>
					</td></tr>
				</table>
				</td>
			</c:otherwise>
			</c:choose>
		</c:otherwise>
		</c:choose>
		<c:choose>
		<c:when test="${instance.status.urlsSucceeded lt 0}">
			<fmt:formatNumber var="intDownloaded" value="0" pattern="0" />
		</c:when>
		<c:otherwise>
			<fmt:formatNumber var="intDownloaded" value="${instance.status.urlsSucceeded}" pattern="0" />
		</c:otherwise>
		</c:choose>
		
		<td class="tableRowLite"><wct:date value="${instance.sortOrderDate}" type="fullDateTime"/></td>
		<td class="tableRowLite"><c:out value="${instance.state}"/></td>
		<td class="tableRowLite"><c:out value="${instance.owner.niceName}"/></td>
		<td class="tableRowLite"><c:out value="${instance.status.elapsedTimeString}"/>&nbsp;</td>
		<td class="tableRowLite"><c:out value="${instance.status.dataDownloadedString}"/>&nbsp;</td>
		<td class="tableRowLite"><c:out value="${intDownloaded}"/>&nbsp;</td>
		
		<c:choose>
		<c:when test="${intDownloaded != null && intDownloaded ne '0'}">
			<fmt:formatNumber var="intFailed" value="${instance.status.percentageUrlsFailed}" pattern="0.0" groupingUsed="false" />
		</c:when>
		<c:when test="${intDownloaded != null && intDownloaded eq '0' && instance.status.urlsFailed gt 0}">
			<fmt:formatNumber var="intFailed" value="100.0" pattern="0.0" groupingUsed="false" />
		</c:when>
		<c:otherwise>
			<fmt:formatNumber var="intFailed" value="0.0" pattern="0.0" groupingUsed="false"  />
		</c:otherwise>		
		</c:choose>
		
		<c:choose>
		<c:when test="${intFailed < 100.0}">	
		<td class="tableRowLite"><c:out value="${intFailed}" />&nbsp;</td>
		</c:when>
		<c:otherwise>	
		<td class="tableRowLite">100.0&nbsp;</td>
		</c:otherwise>
		</c:choose>
		
		<td class="tableRowLite"><c:out value="${instance.target.crawls}"/>&nbsp;</td>
		<td class="tableRowLite">
			<div name="region<c:out value="${instance.oid}"/>" id="region<c:out value="${instance.oid}"/>" class="region_off" onmouseover="showTooltip($(this), <c:out value="${instance.target.oid}"/>, <c:out value="${instance.oid}"/>, '<%=Constants.AJAX_REQUEST_FOR_TI_ANNOTATIONS%>');">
				${instance.recommendation}
			</div>
		</td> 
		
		<td class="tableRowLite">
<table border=0><tr><td style="width: 350px;">
			<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
			<input type="image" src="images/action-icon-view.gif" title="View" alt="click here to VIEW this item" width="15" height="19" border="0" hspace="3" onclick="javascript:document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_VIEW%>';"/>&nbsp;
			<authority:showControl ownedObject="${instance}" privileges='<%=Privilege.MANAGE_TARGET_INSTANCES + ";" + Privilege.MANAGE_WEB_HARVESTER%>' editMode="true">
				<authority:show>
				<c:if test="${instance.state ne 'Archiving'}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/action-icon-edit.gif" title="Edit" alt="click here to EDIT this item" width="18" height="18" border="0" onclick="javascript:document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_EDIT%>';"/>
				</c:if>
				<c:if test="${instance.state eq 'Running'}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/pause-icon.gif" title="Pause" alt="click here to Pause this item" width="21" height="20" border="0" onclick="javascript:document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_PAUSE%>'; document.targetInstance<c:out value="${count}"/>.action='<c:out value="${action}"/>';"/>			
				</c:if>
				<c:if test="${instance.state eq 'Paused'}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/resume-icon.gif" title="Resume" alt="click here to Resume this item" width="21" height="20" border="0" onclick="javascript:document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_RESUME%>'; document.targetInstance<c:out value="${count}"/>.action='<c:out value="${action}"/>';"/>			
				</c:if>
				<c:if test="${instance.state eq 'Running' || instance.state eq 'Paused' || instance.state eq 'Stopping'}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/abort-icon.gif" title="Abort" alt="click here to Abort this item" width="21" height="20" border="0" onclick="javascript:document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_ABORT%>'; document.targetInstance<c:out value="${count}"/>.action='<c:out value="${action}"/>';"/>			
				</c:if>
				<c:if test="${instance.state eq 'Running'}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/stop-icon.gif" title="Stop" alt="click here to Stop this item" width="21" height="20" border="0" onclick="javascript:document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_STOP%>'; document.targetInstance<c:out value="${count}"/>.action='<c:out value="${action}"/>';"/>			
				</c:if>
				<c:if test="${instance.state eq 'Running' && instance.profile.isHeritrix3Profile()}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
		    	    <a href="javascript:viewH3ScriptConsole(${instance.oid});" title="View"><img src="images/note.gif" title="H3 Script Console" alt="click here to Open H3 Script Console" width="21" height="20" border="0"></a>
				</c:if>
				</authority:show>
				<authority:dont>&nbsp;</authority:dont>
			</authority:showControl>
			<c:if test="${instance.state eq 'Scheduled'}">
				<authority:hasUserOwnedPriv ownedObject="${instance}" privilege="<%=Privilege.LAUNCH_TARGET_INSTANCE_IMMEDIATE%>" scope="<%=Privilege.SCOPE_AGENCY%>">
				<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				<a href="curator/target/ti-harvest-now.html?targetInstanceId=${instance.oid}"><img src="images/resume-icon.gif" title="Harvest Now" alt="click here to Harvest this item" width="21" height="20" border="0"></a>
				</authority:hasUserOwnedPriv>
				<authority:showControl ownedObject="${instance}" privileges='<%=Privilege.MANAGE_TARGET_INSTANCES + ";" + Privilege.MANAGE_WEB_HARVESTER%>' editMode="true">
				    <authority:show>
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/action-icon-delete.gif" title="Delete" alt="click here to DELETE this item" width="18" height="19" border="0" onclick="javascript:var proceed=confirm('Do you really want to delete this Target Instance?'); if (proceed) {document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_DELETE%>'; document.targetInstance<c:out value="${count}"/>.action='<c:out value="${action}"/>';} else { return false; }"/>			
					</authority:show>
				</authority:showControl>
			</c:if>
			<c:if test="${instance.state eq 'Endorsed'}">
				<authority:hasUserOwnedPriv ownedObject="${instance}" privilege="<%=Privilege.ARCHIVE_HARVEST%>" scope="<%=Privilege.SCOPE_OWNER%>">
				<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
	    		<a href="curator/archive/submit.html?instanceID=<c:out value="${instance.oid}"/>&harvestNumber=<c:out value="0"/>" onclick="return confirm('<spring:message code="ui.label.targetinstance.results.confirmSubmit" javaScriptEscape="true"/>');"><img src="images/action-icon-archive.gif" title="Archive" alt="click here to Archive this item" width="21" height="20" border="0"></a>    		
				</authority:hasUserOwnedPriv>
			</c:if>
			<c:if test="${instance.state eq 'Queued'}">
			<c:if test="${instance.status eq null}">
				<authority:showControl ownedObject="${instance}" privileges='<%=Privilege.MANAGE_TARGET_INSTANCES + ";" + Privilege.MANAGE_WEB_HARVESTER%>' editMode="true">
				    <authority:show>
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/action-icon-delete.gif" title="Delete" alt="click here to DELETE this item" width="18" height="19" border="0" onclick="javascript:var proceed=confirm('Do you really want to delete this Target Instance?'); if (proceed) {document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_DELETE%>'; document.targetInstance<c:out value="${count}"/>.action='<c:out value="${action}"/>';} else { return false; }"/>
					</authority:show>
				</authority:showControl>
			</c:if>
			</c:if>
</td></tr></table>
		</td>
		<c:set var="count" scope="page" value="${count + 1}"/>
	</form>
	</tr>
	<tr>
		<td colspan="14" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="5" border="0" /></td>
	</tr>		
	</c:forEach>		
	<tr>
		<form name="filter2" method="POST" action="<c:out value="${action}"/>">
		<td class="tableRowLite" colspan="14" align="center">
			<input type="hidden" name="<%=TargetInstanceCommand.PARAM_PAGE%>" value="<c:out value="${targetInstances.page}"/>" />			
			<input type="hidden" name="<%=TargetInstanceCommand.PARAM_PAGESIZE%>" value="<c:out value="${page.pageSize}"/>" />			
			<input type="hidden" name="<%=TargetInstanceCommand.PARAM_CMD%>" value="" />			
			
			<jsp:include page="pagination.jsp"/>

			</td>
		</form>		
		</tr>	
	</table>
	<form id="multiSelectForm" method="POST" action="<c:out value="${action}"/>">
		<input type="hidden" name="targetInstanceId" value="<c:out value="${instance.oid}"/>"/>
		<input type="hidden" id="cmd" name="cmd" value="<%=TargetInstanceCommand.ACTION_VIEW%>"/>
		<input type="hidden" id="<%=TargetInstanceCommand.PARAM_REJREASON_ID%>" name="<%=TargetInstanceCommand.PARAM_REJREASON_ID%>" value="" />
	</form>
</div>

