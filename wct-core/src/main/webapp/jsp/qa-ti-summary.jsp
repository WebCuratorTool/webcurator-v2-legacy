<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="org.webcurator.ui.target.command.TargetInstanceSummaryCommand" %>
<%@page import="org.webcurator.ui.target.command.TargetInstanceCommand" %>
<%@page import="org.webcurator.domain.model.core.TargetInstance" %>
<%@page import="org.webcurator.ui.common.Constants" %>
<%@page import="org.webcurator.domain.model.auth.Privilege" %>
<%@page import="org.webcurator.domain.model.core.Indicator" %>
<%@page import="org.webcurator.domain.model.core.Schedule" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>

<script src="scripts/codemirror/lib/codemirror.js"></script>
<link rel="stylesheet" href="scripts/codemirror/lib/codemirror.css"/>
<link rel="stylesheet" href="scripts/codemirror/theme/elegant.css"/>
<script src="scripts/codemirror/mode/xml/xml.js"></script>

<style>
	.CodeMirror {
		height: 40em;
	}
</style>

<script type="text/javascript"> 
<!-- JQuery Section: ANNOTATIONS HISTORY (VIA AJAX) JAVASCRIPT -->

var currentProfileIndex = -1;

//json array of images representing the enabled action buttons
var enabled_buttons = {
		"archive":			'images/multi-archive-enabled.gif',
		"endorse": 			'images/multi-endorse-enabled.gif',
		"reject": 			'images/multi-reject-enabled.gif',
		"runqa":			'images/runqa-enabled.gif',
		"applyprofile":		'images/applyprofile-enabled.gif',
		"applyschedule":	'images/applyschedule-enabled.gif',
		"discardchanges":	'images/discardchanges-enabled.gif',
		"harvestnow":		'images/harvestnow-enabled.gif',
		"denoterefcrawl":	'images/denotereferencecrawl-enabled.gif',
		"addannotation":	'images/addannotation-enabled.gif'
}
// json array of images representing the disabled action buttons
var disabled_buttons = {
		"archive": 			'images/multi-archive-disabled.gif',
		"endorse": 			'images/multi-endorse-disabled.gif',
		"reject": 			'images/multi-reject-disabled.gif',
		"runqa":			'images/runqa-disabled.gif',
		"applyprofile":		'images/applyprofile-disabled.gif',
		"applyschedule":	'images/applyschedule-disabled.gif',
		"discardchanges":	'images/discardchanges-disabled.gif',
		"harvestnow":		'images/harvestnow-disabled.gif',
		"denoterefcrawl":	'images/denotereferencecrawl-disabled.gif',
		"addannotation":	'images/addannotation-disabled.gif'
}

function enableButton(buttonId) {
	$('#' + buttonId).prop('src', enabled_buttons[buttonId]);
	$('#' + buttonId).prop('disabled', '');
	return true;
}

function disableButton(buttonId) {
	$('#' + buttonId).prop('src', disabled_buttons[buttonId]);
	$('#' + buttonId).prop('disabled', 'disabled');
	return true;
}

function hideAllTooltips() {
	// hide all tooltips
	$("#tooltips").children().each(function() {
		$(this).css('visibility', 'hidden');
	});
}
// display the tooltip
// region: 				a jquery reference to the element over which to display the tooltip
function showTooltip(region) {
	var tooltipName = 'tooltip' + region.prop('id');
	var tooltip;
	hideAllTooltips();
	
	// tooltip has already been generated so show the tooltip
	tooltip = $('#' + tooltipName);
	
	// setup an event handler for the tooltip
	tooltip.mouseleave(function(){ return hideTooltip(tooltip); });
	
	// fetch the new content from the server
	resizeTooltip(region, tooltip);

	return true;
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
	
	// align the tooltip so that its left hand edge aligns with the left hand edge of the region,
	// and the top of the tooltip lies under the region (ie: under the region's bottom edge)
	var tooltipy = regiony + regionh;
	var tooltipx = regionx;
	
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

// submit the harvestresults form
function submitHarvestResultAction(action) {
	
	// construct the confirmation dialog
	var msgAction = action;
	
	// set the hidden command
	document.harvestresults.cmd.value = action;
	
	// get the harvest result oid from the radio button
	var harvestResultOid = $('input[name=harvestResultRadio]:checked').val();
	
	// set the hidden parameter field with the harvest result oid
	document.harvestresults.<%=TargetInstanceSummaryCommand.PARAM_HR_ID%>.value = harvestResultOid;
	
	// if this is a reject action, populate the hidden rejection reason
	if (msgAction == 'reject') {
		$('#<%=TargetInstanceSummaryCommand.PARAM_REJREASON_ID%>').prop('value', $('#rejection_reason').prop('value'));
	}

	var proceed=confirm('Do you really want to ' + msgAction + ' this Target Instance?'); 
	if (proceed) {
		// disable the button
		disableButton($('#' + action).attr('id')); 
		// submit the multi-select form
		$("#harvestresults").submit();
		return true;
	} else { 
		return false;
	}
	
}

//submit the harvestresults form
function submitScheduleChangeAction(formId, action) {
	// obtain a reference to the schedule command hidden field
	var command = $('#' + formId + ' input[name="cmd"]');
	// set the hidden command
	command.val(action);
	var message;
	
	if (action == '<%=TargetInstanceSummaryCommand.ACTION_SAVE_SCHEDULE%>') {
		message = 'Do you really want to save any changes for these schedules? (a type change will generate a new schedule, terminating the existing one)';
	} else if (action == '<%=TargetInstanceSummaryCommand.ACTION_RESET_SCHEDULE%>') {
		message = 'Are you sure you want to discard all schedule changes that you have made?';
	} else if (action == '<%=TargetInstanceSummaryCommand.ACTION_RUN_TARGET_NOW%>') {
		message = 'Are you sure you want to run the target for this target instance now?';
	} 
	
	var proceed=confirm(message); 
	if (proceed) {
		// disable the button
		disableButton($('#' + action).attr('id')); 
		// submit the form
		$('#scheduleForm').submit();
		return true;
	} else { 
		return false;
	}
	
}

function onChangeScheduleType(formName) {
       document.getElementById(formName).submit();
}

function onChangeScheduleDates(textbox) {
	
	//if (textbox.val() != '' && textbox.val() != null) {
		// enable the schedule buttons
		$('#applySchedule').removeAttr('disabled');
		$('#discardChanges').removeAttr('disabled');
	//} else {
		// disable the schedule buttons
	//	$('#applySchedule').attr('disabled','disabled');
	//	$('#discardChanges').attr('disabled','disabled');
	//}
	
	// enable the buttons
	enableButton('applyschedule');
	enableButton('discardchanges');
}

function toggleProfileOverrides(profilesList, onPageLoad=false) {
    if (!onPageLoad && currentProfileIndex >= 0) {
        // Save any h3RawProfile editor changes
        profilesList[currentProfileIndex].h3RawProfile = codeMirrorInstance.getValue();
    }
	var selectedProfile = getSelectedProfile(profilesList);

    if (!onPageLoad) {
        if ((typeof selectedProfile.h3RawProfile !== 'undefined') && selectedProfile.h3RawProfile != null) {
            codeMirrorInstance.setValue(selectedProfile.h3RawProfile);
        } else {
            codeMirrorInstance.setValue("");
        }
        // if we don't have this timeout, the editor will not display its contents until after it's clicked into
        setTimeout(function() {
            codeMirrorInstance.refresh();
        }, 1);
    }
	if (selectedProfile.harvesterType == 'HERITRIX1') {
		$('#h1Profile').show();
		$('#h3Profile').hide();
		$('#h3ImportedProfile').hide();
	} else if (selectedProfile.imported == "true") { // Imported HERITRIX3
		$('#h1Profile').hide();
		$('#h3Profile').hide();
		$('#h3ImportedProfile').show();
	} else { // Non-imported HERITRIX3
		$('#h1Profile').hide();
		$('#h3Profile').show();
		$('#h3ImportedProfile').hide();
	}
}

//the JQuery body onload function
$(document).ready(function() {
	<c:if test="${instance.state ne 'Harvested'}">disableButton('runqa');</c:if>
	<c:if test="${scheduleHasChanged eq 'false'}">disableButton('applyschedule');</c:if>
	<c:if test="${scheduleHasChanged eq 'false'}">disableButton('discardchanges');</c:if>
	disableButton('denoterefcrawl');

	var profilesList = [];
	<c:forEach items="${profiles}" var="prf">
	var jsProfile = {
		name: "${prf.name}",
        harvesterType: "${prf.harvesterType}",
		oid: "${prf.oid}",
		<c:if test="${prf.imported eq 'true'}">h3RawProfile: "<spring:escapeBody javaScriptEscape="true">${prf.profile}</spring:escapeBody>",</c:if>
		imported: "${prf.imported}"
	};
	profilesList.push(jsProfile);
	</c:forEach>
	toggleProfileOverrides(profilesList, true);

	$('#profileOid').change(function() {
		toggleProfileOverrides(profilesList);
	});
});

function getSelectedProfile(profilesList) {
	var oid = document.getElementById('profileOid');
    currentProfileIndex = profilesList.map(function(elem) { return elem.oid; }).indexOf(oid.options[oid.selectedIndex].value);
	return profilesList[currentProfileIndex];
}

</script>

<div id="tooltips" name="tooltips"></div>
	<div id="tooltipregion" name="tooltipregion" class="rectooltip">
		<table cellspacing="0" cellpadding="0" width="100%">
		<c:set var="count" scope="page" value="0"/>
		<c:forEach items="${indicators}" var="indicator">
			<c:set var="count" scope="page" value="${count + 1}"/>
			<c:choose>
				<c:when test="${count eq 1}">
					<tr class="ttooltip_header"><td width="30%">Indicator&nbsp;</td><td>Advice&nbsp;</td><td>Justification&nbsp;</td></tr>	
						<c:choose>
						<c:when test="${indicator.advice != null}">
						<tr><td class="rectooltiptext" width="30%"><c:out value="${indicator.name}"/>&nbsp;</td>
						<td class="indicator<c:out value='${indicator.advice}'/>"><c:out value="${indicator.advice}"/>&nbsp;</td>
						<td class="rectooltiptext"><c:out value="${indicator.justification}"/>&nbsp;</td>
						</tr>
						</c:when>
						</c:choose>
				</c:when>
				<c:otherwise>
						<c:choose>
						<c:when test="${indicator.advice != null && indicator.advice ne 'Running'}">
						<tr><td class="rectooltiptext" width="30%"><c:out value="${indicator.name}"/>&nbsp;</td>
						<td class="indicator<c:out value='${indicator.advice}'/>"><c:out value="${indicator.advice}"/>&nbsp;</td>
						<td class="rectooltiptext"><c:out value="${indicator.justification}"/>&nbsp;</td>
						</tr>
						</c:when>
						</c:choose>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		</table>
	</div>
	
<!-- main layout table -->
<table border="0" width="100%">
	<tr>
		<td width="10%"></td>
		<td colspan="2">
			<table><tr><td><div class="midtitleGrey"><c:out value="${instance.target.name}"/>&nbsp;(<c:out value="${instance.oid}" />)</div></td></tr></table>
		</td>
		<td width="10%"></td>
	</tr>
	<tr>
		<td width="10%"></td>
		
		<!-- LEFT HAND COLUMN -->
		<td class="layout_table"  style="vertical-align: top;">
	
			<!-- harvest results, archive, endorse, reject -->
			<table class="panel" border="0" width="100%" cellspacing="0px">
				<tr>
					<td colspan="5">
					<table border="0" cellspacing="0px" width="100%" height="100%">
					<tr>
						<td colspan="5">
							<table class="panel_header_row"><tr><td><div class="panel_header_title">Harvest Results</div></td></tr></table>
						</td>
					</tr> 
					<tr> 
						<td class="hhist_header_row" style="width: 5%"></td>
						<td class="hhist_header_row" style="width: 10%">#</td>
						<td class="hhist_header_row" style="width: 10%">Date</td>
						<td class="hhist_header_row" style="width: 50%;">Notes</td>
						<td class="hhist_header_row" style="width: 25%;">State</td>
					</tr>
					<c:set var="harvestResultCounter" value="0" />
					<c:forEach items="${results}" var="result">
					<c:set var="harvestResultCounter" value="${harvestResultCounter+1}" />
					<c:set var="harvestResultsSize" value="${fn:length(results)}" />
					<tr>
						<td width="10%"><input type="radio" name="harvestResultRadio" id="harvestResultRadio${harvestResultCounter}" value="${result.oid}" <c:if test='${harvestResultCounter eq harvestResultsSize}'>checked="checked"</c:if> /></td>
						<form id="harvestResults${harvestResultCounter}" name="harvestResults${harvestResultCounter}" method="post" action="curator/target/target-instance.html"> 
						<input type="hidden" id="init_tab" name="init_tab" value="RESULTS">
						<input type="hidden" name="<%=TargetInstanceSummaryCommand.PARAM_OID%>" value="${instance.oid}" />
						<input type="hidden" name="<%= TargetInstanceCommand.PARAM_CMD %>" value="<%=TargetInstanceCommand.ACTION_EDIT%>">
						<input type="hidden" name="<%=TargetInstanceCommand.PARAM_OID%>" value="${instance.oid}" />
						<td>
							<a id="home" class="panel_links" onclick="document.harvestResults${harvestResultCounter}.submit();">${result.harvestNumber}</a>
						</td>
						<td nowrap="nowrap">
							<a id="home" class="panel_links" onclick="document.harvestResults${harvestResultCounter}.submit();"><wct:date value="${result.creationDate}" type="fullDateTime"/></a>
						</td>
						<td nowrap="nowrap">
							<a id="about" class="panel_links" onclick="document.harvestResults${harvestResultCounter}.submit();">${result.provenanceNote}</a>
						</td>
						</form>
						<td nowrap="nowrap">
							<c:choose>
								<c:when test="${result.state == 1}">
									Endorsed
								</c:when>
								<c:when test="${result.state == 2}">
									Rejected
								</c:when>
								<c:when test="${result.state == 3}">
								    Indexing
								</c:when>
								<c:when test="${result.state == 4}">
								    Aborted
								</c:when>
						    </c:choose>  
						</td>
					</tr>
					</c:forEach>
					<tr>
						<td colspan="5" style="vertical-align: middle;">
							<form id="harvestresults" name="harvestresults" method="POST" action="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/curator/target/qatisummary.html">
								<input type="hidden" id="<%=TargetInstanceSummaryCommand.PARAM_HR_ID%>" name="<%=TargetInstanceSummaryCommand.PARAM_HR_ID%>" value="" />
								<input type="hidden" id="<%=TargetInstanceSummaryCommand.PARAM_CMD%>" name="<%=TargetInstanceSummaryCommand.PARAM_CMD%>" value="" />
								<input type="hidden" id="<%=TargetInstanceSummaryCommand.PARAM_OID%>" name="<%=TargetInstanceSummaryCommand.PARAM_OID%>" value="${instance.oid}" />
								<input type="hidden" id="<%=TargetInstanceSummaryCommand.PARAM_REJREASON_ID%>" name="<%=TargetInstanceSummaryCommand.PARAM_REJREASON_ID%>" value="" />
								
								<table border="0">
								<tr>
									<td>
									<c:if test="${instance.state eq 'Endorsed'}">
										<authority:hasUserOwnedPriv ownedObject="${instance}" privilege="<%=Privilege.ARCHIVE_HARVEST%>" scope="<%=Privilege.SCOPE_OWNER%>">
										<c:if test="${not customDepositFormRequired}">
											<input type="image" name="archive" id="archive" src="images/multi-archive-enabled.gif" alt="endorse the harvest result" width="82" height="22" border="0" onclick="return submitHarvestResultAction('<%=TargetInstanceSummaryCommand.ACTION_ARCHIVE%>');" />
										</c:if>
										</authority:hasUserOwnedPriv>
									</c:if>
									</td>
									
									<c:if test="${instance.state eq 'Harvested'}">   
										<authority:hasPrivilege privilege="<%=Privilege.ENDORSE_HARVEST%>" scope="<%=Privilege.SCOPE_OWNER%>">
									<td>
											<input type="image" name="endorse" id="endorse" src="images/multi-endorse-enabled.gif" alt="endorse the harvest result" width="82" height="22" border="0" onclick="return submitHarvestResultAction('<%=TargetInstanceSummaryCommand.ACTION_ENDORSE%>');" />
									</td><td>
											<input type="image" name="reject" id="reject" src="images/multi-reject-enabled.gif" alt="endorse the harvest result" width="82" height="22" border="0" onclick="return submitHarvestResultAction('<%=TargetInstanceSummaryCommand.ACTION_REJECT%>');" />
									</td><td>
										    					
								  			<select name="rejection_reason" id="rejection_reason" style="height: 22px;" >				
												<c:forEach items="${reasons}" var="o">
													<option value="<c:out value="${o.oid}"/>"><c:out value="${o.name}"/></option>
												</c:forEach>
											</select>
									</td>
									</authority:hasPrivilege>
									</c:if>
									</tr>
								</table>
							</form>
						</td>
					</tr>	
					</table>
					</td>
				</tr>
				<tr>
					<td colspan="5">
					
			<!-- resources panel -->
			<table class="panel" border="0" width="100%" cellspacing="0px">
				<tr><td colspan="2"><table class="panel_header_row"><tr><td><div class="panel_header_title">Resources</div></td></tr></table></td></tr>
				<tr><td colspan="2"><table class="panel_dotted_row"><tr><td>The following resources are associated with this instance:</td></tr></table></td></tr>
				<tr>
					<td colspan="2" style="word-wrap: break-word;">
						<form id="seeds" name="seeds" action="curator/target/target.html" method="post">
						<input type="hidden" id="_tab_change" name="_tab_change" value="" />
						<input type="hidden" id="tabChangedTo" name="tabChangedTo" value="seeds" />
						<input type="hidden" id="_tab_current_page" name="_tab_current_page" value="GENERAL">
						<input type="hidden" id="tabForceChangeTo" name="tabForceChangeTo" value="true" />
						<input type="hidden" id="targetOid" name="targetOid" value="${instance.target.oid}" />
						Seeds: <a id="home" class="panel_links" onclick="document.seeds.submit();">
						<wct:ellipsis strings="${seeds}" length="115" /></a>
						</form>
					</td>
				</tr>
				<tr><td colspan="2"><table class="panel_dotted_row"><tr><td></td></tr></table></td></tr>
				<tr>
					<td colspan="2" nowrap="nowrap">
					<form id="logs" name="logs" method="post" action="curator/target/target-instance.html">
						<input type="hidden" id="_tab_change" name="_tab_change" value="" />
						<input type="hidden" id="tabChangedTo" name="tabChangedTo" value="logs" />
						<input type="hidden" id="_tab_current_page" name="_tab_current_page" value="GENERAL">
						<input type="hidden" name="<%= TargetInstanceSummaryCommand.PARAM_CMD %>" value="">
						<input type="hidden" name="<%=TargetInstanceSummaryCommand.PARAM_OID%>" value="${instance.oid}" />
						
						<a id="home" class="panel_links" onclick="document.logs.submit();">
							<c:out value="${fn:length(logs)}" /> Logs
						</a>
					</form>
					</td>
				</tr>
				<tr><td colspan="2"><table class="panel_dotted_row"><tr><td></td></tr></table></td></tr>
			</table>
			
			<!-- key indicators panel -->
			<table class="panel" border="0" width="100%" cellspacing="0px">
				<tr><td colspan="3"><table class="panel_header_row"><tr><td><div class="panel_header_title">Key Indicators</div></td></tr></table></td></tr>
				<tr><td colspan="3"><table class="panel_dotted_row"><tr><td>Indicators collected for this instance:</td></tr></table></td></tr>
			<c:set var="indicatorCount" scope="page" value="0" />
			<c:forEach items="${indicators}" var="indicator">
			<c:set var="indicatorCount" scope="page" value="${indicatorCount + 1}"/>
			<c:choose>
			<c:when test="${indicatorCount%2 == 1}">
				<!-- when odd, start a new row -->
				<tr><td nowrap="nowrap" title="${indicator.indicatorCriteria.description}">
			</c:when>
			<c:when test="${indicatorCount%2 == 0}">
				<!-- when even, start a new cell -->
				<td nowrap="nowrap" title="${indicator.indicatorCriteria.description}">
			</c:when>
			</c:choose>
			<fmt:formatNumber var="indicatorValue" value="${indicator.floatValue}" pattern="0" />
					<c:if test="${indicator.indicatorCriteria.enableReport eq 'true'}">
					<a id="home" href="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/curator/target/qa-indicator-report.html?indicatorOid=${indicator.oid}" class="panel_links">
					</c:if>
				<wct:unit value="${indicatorValue}" unit="${indicator.unit}" /> ${indicator.name}&nbsp; 
				<c:choose>
				<c:when test="${indicator.showDelta && instance.target.referenceCrawlOid != null}" >
				(<c:if test="${indicator.floatValue - rcIndicators[indicator.name].floatValue >= 0}">+</c:if><wct:unit value="${indicator.floatValue - rcIndicators[indicator.name].floatValue}" unit="${indicator.unit}" />)
				</c:when>
				</c:choose>
					<c:if test="${indicator.indicatorCriteria.enableReport eq 'true'}">
					</a>
					</c:if>
				<c:choose>
				<c:when test="${indicatorCount%2 == 0}">
				</td><td width="10%">&nbsp;</td>
			</tr>
			<tr><td colspan="3"><table class="panel_dotted_row"><tr><td></td></tr></table></td></tr>
				</c:when>
				<c:when test="${indicatorCount%2 == 1}">
				</td><td width="10%">&nbsp;</td>
				</c:when>
				</c:choose>
				</c:forEach>
			<tr>
				<td colspan="3">
					<form id="rerunqa" name="rerunqa" method="POST" action="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/curator/target/qatisummary.html">
						<input type="hidden" name="<%=TargetInstanceSummaryCommand.PARAM_OID%>" value="${instance.oid}" />
						<input type="hidden" name="<%=TargetInstanceSummaryCommand.PARAM_CMD%>" value="<%=TargetInstanceSummaryCommand.ACTION_RERUN_QA%>" />
						<input type="image" name="runqa" id="runqa" src="images/runqa-enabled.gif" alt="run the QA analysis" width="82" height="22" border="0" onclick="disableButton($(this).attr('id')); document.rerunqa.submit();" />
					</form>
				</td>
			</tr>
		</table>
		
		<!-- recommendation panel -->
		<table class="panel" border="0" width="100%" cellspacing="0px">
			<tr><td colspan="2"><table class="panel_header_row"><tr><td><div class="panel_header_title">Recommendation</div></td></tr></table></td></tr>
			<tr><td colspan="2">
				<table class="panel_dotted_row">
				<tr>
					<td class="indicator${instance.recommendation}">
					<c:choose>
					<c:when test="${instance.recommendation ne 'None'}">
						<div name="region" id="region" class="region_off" onmouseover="showTooltip($(this));">
					</c:when>
					<c:otherwise>
						<div name="region" id="region" class="region_off">
					</c:otherwise>
					</c:choose>
							${instance.recommendation}
						</div>
					</td>
				</tr>
				</table>
				</td>
			</tr>
		</table>
				
					</td>
				</tr>
				

				<tr>
					<td colspan="5">
						
					</td>
				</tr>
			</table>
	
		
		</td>

		<!-- RIGHT HAND COLUMN -->
		<td style="vertical-align: top;" width="40%">
			<!-- profile panel -->
			<c:set var="profileEditMode" value="${editMode && instance.overrides != null}"/>
			<form id="profileoverrides" name="profileoverrides" method="POST" action="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/curator/target/qatisummary.html">
			<table class="panel" border="0" width="100%" cellspacing="0px">
				<tr><td colspan="4"><table class="panel_header_row"><tr><td><div class="panel_header_title">Profile Overrides</div></td></tr></table></td></tr>
				<tr>
					<td colspan="4" nowrap="nowrap">Base Profile: 

						<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${editMode}">
			        	<authority:show>
					      <select name="profileOid" id="profileOid">
					        <c:forEach items="${profiles}" var="profile">
					          <option value="<c:out value="${profile.oid}"/>" ${profile.oid == profileCommand.profileOid ? 'SELECTED' : '' }><c:out value="${profile.name}"/></option>
					        </c:forEach>
					      </select>
					    </authority:show>
					    <authority:dont>
					      <c:out value="${profileName}"/>
					      <input type="hidden" id="profileOid" name="profileOid" value="<c:out value="${profileCommand.profileOid}"/>" />
					    </authority:dont>
						</authority:showControl>
	
					</td>
				</tr>
				<tr><td colspan="4"><table class="panel_dotted_row"><tr><td></td></tr></table></td></tr>
			</table>
				<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${profileEditMode}">
				<authority:show>
				<div id="h1Profile">
					<table class="panel" border="0" width="100%" cellspacing="0px">
				<tr>
					<td nowrap="nowrap" title="Robot honoring policy">
						<input type="checkbox" name="overrideRobots" ${profileCommand.overrideRobots ? 'checked' : ''}/>Robot policy: 
      					<select name="robots">
        					<option value="classic" ${profileCommand.robots eq 'classic' ? 'selected' : ''}>classic</option>
        					<option value="ignore" ${profileCommand.robots eq 'ignore' ? 'selected' : ''}>ignore</option>
      					</select>
        				<input type="checkbox" name="overrideMaxBytesDownload" ${profileCommand.overrideMaxBytesDownload ? 'checked' : ''}/>
        				Max KB:<input type="text" size="10" name="maxBytesDownload" value="<c:out value="${profileCommand.maxBytesDownload}"/>"/>
 						<input type="checkbox" name="overrideMaxDocuments" ${profileCommand.overrideMaxDocuments ? 'checked' : ''}/>
						Docs:<input type="text" size="10" name="maxDocuments" value="<c:out value="${profileCommand.maxDocuments}"/>"/>
        			</td>
				</tr>
				<tr><td colspan="4"><table class="panel_dotted_row"><tr><td></td></tr></table></td></tr>
				<tr>
					<td colspan="4">
       				<input type="checkbox" name="overrideMaxPathDepth" ${profileCommand.overrideMaxPathDepth ? 'checked' : ''}/>
        				Path Depth:<input type="text" size="10" name="maxPathDepth" value="<c:out value="${profileCommand.maxPathDepth}"/>"/>
      				    <input type="checkbox" name="overrideMaxHops" ${profileCommand.overrideMaxHops ? 'checked' : ''}/>
        				Hops:<input type="text" size="3" name="maxHops" value="<c:out value="${profileCommand.maxHops}"/>"/>
       					<input type="checkbox" name="overrideMaxHours" ${profileCommand.overrideMaxHours ? 'checked' : ''}/>
       					Hours:<input type="text" size="3" name="maxHours" value="<c:out value="${profileCommand.maxHours}"/>"/></td>
				</tr>
				<tr><td colspan="4"><table class="panel_dotted_row"><tr><td></td></tr></table></td></tr>
								<tr>
					<td colspan="9">
						<table class="panel" border="0" width="100%">
						<tr>
							<td style="vertical-align: middle;">
								<input type="checkbox" name="overrideExcludeFilters" ${profileCommand.overrideExcludeFilters ? 'checked' : ''}/>
								Exclude Filters:
							</td>
						</tr>
						<tr>
							<td style="vertical-align: middle;">
								<textarea name="excludeFilters" cols="85" rows="2"><c:out value="${profileCommand.excludeFilters}"/></textarea>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="9">
						<table class="panel" border="0" width="100%">
						<tr>
							<td colspan="3" style="vertical-align: top;">
								<input type="checkbox" name="overrideForceAcceptFilters" ${profileCommand.overrideForceAcceptFilters ? 'checked' : ''}/>
							Force Accept Filters:
							</td>
						</tr>
						<tr>
							<td colspan="3" style="vertical-align: top;">
								<textarea name="forceAcceptFilters" cols="85" rows="2"><c:out value="${profileCommand.forceAcceptFilters}"/></textarea>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				</table>
				</div> <!-- end h1Profile -->
				<div id="h3Profile">
					<table class="panel" border="0" width="100%" cellspacing="0px">
						<tr>
							<td class="hhist_header_row" style="width: 20%">Profile Element</td>
							<td class="hhist_header_row" style="width: 60%">Override Value</td>
							<td class="hhist_header_row" style="width: 20%">Enable Override</td>
						</tr>
						<tr>
							<td>
								Ignore Robots:
							</td>
							<td>
								<input type="checkbox" name="h3IgnoreRobots" ${profileCommand.h3IgnoreRobots ? 'checked' : ''}/>
							</td>
							<td>
								<input type="checkbox" name="overrideH3IgnoreRobots" ${profileCommand.overrideH3IgnoreRobots ? 'checked' : ''}/>
							</td>
						</tr>
						<tr>
							<td>
								Ignore Cookies:
							</td>
							<td>
								<input type="checkbox" name="h3IgnoreCookies" ${profileCommand.h3IgnoreCookies ? 'checked' : ''}/>
							</td>
							<td>
								<input type="checkbox" name="overrideH3IgnoreCookies" ${profileCommand.overrideH3IgnoreCookies ? 'checked' : ''}/>
							</td>
						</tr>
						<tr><td colspan="4"><table class="panel_dotted_row"><tr><td></td></tr></table></td></tr>
						<tr>
							<td>
								Max Data:
							</td>
							<td>
								<input type="number" size="20" name="h3DataLimit" step="0.001" min="0.000" value="<c:out value="${profileCommand.h3DataLimit}"/>"/>
								Data Unit:
								<select name="h3DataLimitUnit" id="h3DataLimitUnit">
									<c:forEach items="${profileDataUnits}" var="unit">
										<option id="${unit}" ${profileCommand.h3DataLimitUnit eq unit ? 'SELECTED' : ''}>${unit}</option>
									</c:forEach>
								</select>
							</td>
							<td>
								<input type="checkbox" name="overrideH3DataLimit" ${profileCommand.overrideH3DataLimit ? 'checked' : ''}/>
							</td>
						</tr>
						<tr>
							<td>
								Max Docs:
							</td>
							<td>
								<input type="number" size="10" min="0" name="h3DocumentLimit" value="<c:out value="${profileCommand.h3DocumentLimit}"/>"/>
							</td>
							<td>
								<input type="checkbox" name="overrideH3DocumentLimit" ${profileCommand.overrideH3DocumentLimit ? 'checked' : ''}/>
							</td>
						</tr>
						<tr><td colspan="4"><table class="panel_dotted_row"><tr><td></td></tr></table></td></tr>
						<tr>
							<td style="white-space: nowrap;">
								Max Path Depth:
							</td>
							<td>
								<input type="number" min="0" size="3" name="h3MaxPathDepth" value="<c:out value="${profileCommand.h3MaxPathDepth}"/>"/>
							</td>
							<td>
								<input type="checkbox" name="overrideH3MaxPathDepth" ${profileCommand.overrideH3MaxPathDepth ? 'checked' : ''}/>
							</td>
						</tr>
						<tr>
							<td>
								Max Hops:
							</td>
							<td>
								<input type="number" size="3" name="h3MaxHops" value="<c:out value="${profileCommand.h3MaxHops}"/>"/>
							</td>
							<td>
								<input type="checkbox" name="overrideH3MaxHops" ${profileCommand.overrideH3MaxHops ? 'checked' : ''}/>
							</td>
						</tr>
						<tr>
							<td style="white-space: nowrap;">
								Max Transitive Hops:
							</td>
							<td>
								<input type="number" min="0" size="3" name="h3MaxTransitiveHops" value="<c:out value="${profileCommand.h3MaxTransitiveHops}"/>"/>
							</td>
							<td>
								<input type="checkbox" name="overrideH3MaxTransitiveHops" ${profileCommand.overrideH3MaxTransitiveHops ? 'checked' : ''}/>
							</td>
						</tr>
						<tr><td colspan="4"><table class="panel_dotted_row"><tr><td></td></tr></table></td></tr>
						<tr>
							<td>
								Max Time:
							</td>
							<td>
								<input type="number" size="20" step="0.001" min="0.000" name="h3TimeLimit" value="<c:out value="${profileCommand.h3TimeLimit}"/>"/>
								Time Unit:
								<select name="h3TimeLimitUnit" id="h3TimeLimitUnit">
									<c:forEach items="${profileTimeUnits}" var="unit">
										<option id="${unit}" ${profileCommand.h3TimeLimitUnit eq unit ? 'SELECTED' : ''}>${unit}</option>
									</c:forEach>
								</select>
							</td>
							<td>
								<input type="checkbox" name="overrideH3TimeLimit" ${profileCommand.overrideH3TimeLimit ? 'checked' : ''}/>
							</td>
						</tr>
						<tr><td colspan="4"><table class="panel_dotted_row"><tr><td></td></tr></table></td></tr>
						<tr>
							<td>
								Blocked URLs:
							</td>
							<td>
								<textarea name="h3BlockedUrls" cols="40" rows="3"><c:out value="${profileCommand.h3BlockedUrls}"/></textarea>
							</td>
							<td>
								<input type="checkbox" name="overrideH3BlockedUrls" ${profileCommand.overrideH3BlockedUrls ? 'checked' : ''}/>
							</td>
						</tr>
						<tr>
							<td>
								Included URLs:
							</td>
							<td>
								<textarea name="h3IncludedUrls" cols="40" rows="3"><c:out value="${profileCommand.h3IncludedUrls}"/></textarea>
							</td>
							<td>
								<input type="checkbox" name="overrideH3IncludedUrls" ${profileCommand.overrideH3IncludedUrls ? 'checked' : ''}/>
							</td>
						</tr>
					</table>
				</div> <!-- end h3Profile -->
				<div id="h3ImportedProfile">
					<table class="panel" border="0" width="100%" cellspacing="0px">
					<tr>
						<td colspan="4">
							<input type="hidden" name="overrideH3RawProfile" value="true"}/>Override imported profile:
							<input type="checkbox" name="visibleOverrideH3RawProfile" disabled="disabled" checked="checked"}/>
						</td>
					</tr>
					<tr>
						<td colspan="9">
							<div id="editorDiv">
<textarea id="h3RawProfile" name="h3RawProfile" form="profileoverrides"/>
<c:out value="${profileCommand.h3RawProfile}"/>
</textarea>
							</div>
							<script>
								codeMirrorInstance = CodeMirror.fromTextArea(document.getElementById("h3RawProfile"),
									{
										mode: "text/xml",
										lineNumbers: true,
										lineWrapping: true,
										readOnly: false
									});
							</script>
						</td>
					</tr>
					</table>
				</div> <!-- h3ImportedProfile -->
				<table class="panel" border="0" width="100%" cellspacing="0px">
				<tr>
					<td colspan="9">
						<input type="hidden" name="<%=TargetInstanceSummaryCommand.PARAM_OID%>" value="${instance.oid}" />
						<input type="hidden" name="<%=TargetInstanceSummaryCommand.PARAM_CMD%>" value="<%=TargetInstanceSummaryCommand.ACTION_SAVE_PROFILE%>" />
						<input type="image" name="applyprofile" id="applyprofile" src="images/applyprofile-enabled.gif" alt="apply the profile override to the target" width="135" height="22" border="0" onclick="disableButton($(this).attr('id')); document.profileoverrides.submit();" />
					</td>
				</tr>

				<tr><td colspan="4"><table class="panel_dotted_row"><tr><td></td></tr></table></td></tr>
				</authority:show>
				</authority:showControl>
				</table>
				</form>			

				<!-- schedule and crawl filters -->
				<table class="panel" border="0" width="100%" cellspacing="0px">
				<tr>
					<td colspan="4">
					
						<table class="panel" border="0" width="100%" cellspacing="0px">
							<tr>
								<td colspan="9" style="vertical-align: middle;">
									<table class="panel" border="0" width="100%" cellspacing="0px">
										<tr><td colspan="9"><table width="100%" class="panel_header_row"><tr><td><div class="panel_header_title">Schedule</div></td></tr></table></td></tr>
									</table>
									<form id="scheduleForm" name="scheduleForm" method="post" action="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/curator/target/qatisummary.html">
									<table border="0" class="panel" width="100%" cellspacing="0px" cellpadding="0px">
										<tr> 
											<td class="hhist_header_row" style="width: 20%">From</td>
											<td class="hhist_header_row" style="width: 20%;">To</td>
											<td class="hhist_header_row" style="width: 20%;">Type</td>
											<td class="hhist_header_row" style="width: 40%;">Day/Month/Options</td>
										</tr>
										<c:set var="scheduleCount" value="0" />
										<input type="hidden" name="cmd" id="cmd" value="<%=TargetInstanceSummaryCommand.ACTION_REFRESH%>" />
										<input type="hidden" name="<%=TargetInstanceSummaryCommand.PARAM_OID%>" id="<%=TargetInstanceSummaryCommand.PARAM_OID%>" value="${instance.oid}" />
										
										<c:forEach items="${schedules}" var="schedule">
										<c:set var="scheduleCount" value="${scheduleCount+1}" />
										<tr>
							 				<td style="vertical-align: middle;">
							 					<!-- we exclude custom schedules from the summary screen -->
							 					<c:choose>
												<c:when test="${schedule.scheduleType ne 0}">
												<input type="hidden" name="scheduleOid" id="scheduleOid" value="${schedule.oid}"/>
												<input type="hidden" name="cronPattern" id="cronPattern" value="${scheduleCommands[schedule.oid].cronExpression}"/>
												<input type="hidden" name="minutes" id="minutes" value="${scheduleCommands[schedule.oid].minutes}"/>
												<input type="hidden" name="hours" id="hours" value="${scheduleCommands[schedule.oid].hours}"/>
												<input style="width: 60px;" type="text" id="startDate" name="startDate" size="10" maxlength="10" value="<wct:date value="${scheduleCommands[schedule.oid].startDate}" type="fullDate"/>" onkeypress="onChangeScheduleDates($(this));" onkeydown="onChangeScheduleDates($(this));" />
												</c:when>
												<c:otherwise>
												<div style='font-size:8; color:gray;'>
												<wct:date value='${scheduleCommands[schedule.oid].startDate}' type='fullDate'/>
												</div>
												</c:otherwise>
												</c:choose>
											</td>
											<td style="vertical-align: middle;">
												<c:choose>
												<c:when test="${schedule.scheduleType ne 0}">
												<input style="width: 60px;" type="text" id="endDate" name="endDate" size="10" maxlength="10" value="<wct:date value="${scheduleCommands[schedule.oid].endDate}" type="fullDate"/>" onkeypress="onChangeScheduleDates($(this));" onkeydown="onChangeScheduleDates($(this));" />
												</c:when>
												<c:otherwise>
												<div style='font-size:8; color:gray;'>
												<wct:date value='${scheduleCommands[schedule.oid].endDate}' type='fullDate'/>
												</div>
												</c:otherwise>
												</c:choose>
											</td>
											<td style="vertical-align: middle;">
												<c:choose>
												<c:when test="${schedule.scheduleType ne 0}">
												<select id="scheduleType" name="scheduleType" onchange="onChangeScheduleType('scheduleForm');">
											        <option value="-1" ${scheduleCommands[schedule.oid].scheduleType == -1 ? 'selected' : ''}>Daily</option>
											        <option value="-2" ${scheduleCommands[schedule.oid].scheduleType == -2 ? 'selected' : ''}>Weekly</option>
											        <option value="-3" ${scheduleCommands[schedule.oid].scheduleType == -3 ? 'selected' : ''}>Monthly</option>
											        <option value="-4" ${scheduleCommands[schedule.oid].scheduleType == -4 ? 'selected' : ''}>Bi-Monthly</option>
											        <option value="-5" ${scheduleCommands[schedule.oid].scheduleType == -5 ? 'selected' : ''}>Quarterly</option>
											        <option value="-6" ${scheduleCommands[schedule.oid].scheduleType == -6 ? 'selected' : ''}>Half Yearly</option>
											        <option value="-7" ${scheduleCommands[schedule.oid].scheduleType == -7 ? 'selected' : ''}>Annually</option>							        
											     </select>
											     </c:when>
											     <c:otherwise>
											     <div style='font-size:8; color:gray;'>
											     Custom
											     </div>
											     </c:otherwise>
											     </c:choose>
											</td>
											<td>
												<c:choose>
												<c:when test="${schedule.scheduleType ne 0}">
													<c:choose>
											    	  <c:when test="${scheduleCommands[schedule.oid].scheduleType == -1}">
													    <input type="hidden" name="daysOfWeek" value="?">	
													    <input type="hidden" name="daysOfMonth" value="*">								    
													    <input type="hidden" name="months" value="*">
													    <input type="hidden" name="years" value="*">						    	  
											    	  </c:when>
											    	  <c:when test="${scheduleCommands[schedule.oid].scheduleType == -2}">
													    <input type="hidden" name="daysOfMonth" value="?">	
													    <input type="hidden" name="months" value="*">
													    <input type="hidden" name="years" value="*">						    	  
											    	  </c:when>
											    	  <c:when test="${scheduleCommands[schedule.oid].scheduleType == -3}">
													    <input type="hidden" name="daysOfWeek" value="?">	
													    <input type="hidden" name="months" value="*">
													    <input type="hidden" name="years" value="*">						    	  
											    	  </c:when>
											    	  <c:when test="${scheduleCommands[schedule.oid].scheduleType <= -4}">
											    	    <input type="hidden" name="daysOfWeek" value="?">	
													    <input type="hidden" name="years" value="*">					    	  
											    	  </c:when>
										    		</c:choose>
												<c:if test="${scheduleCommands[schedule.oid].scheduleType == -2}">
													<select name="daysOfWeek" id="daysOfWeek" onchange="onChangeScheduleType('scheduleForm');">
														<option value="MON" ${scheduleCommands[schedule.oid].daysOfWeek == 'MON' ? 'selected' :''}>Monday</option>
														<option value="TUE" ${scheduleCommands[schedule.oid].daysOfWeek == 'TUE' ? 'selected' :''}>Tuesday</option>
														<option value="WED" ${scheduleCommands[schedule.oid].daysOfWeek == 'WED' ? 'selected' :''}>Wednesday</option>
														<option value="THU" ${scheduleCommands[schedule.oid].daysOfWeek == 'THU' ? 'selected' :''}>Thursday</option>
														<option value="FRI" ${scheduleCommands[schedule.oid].daysOfWeek == 'FRI' ? 'selected' :''}>Friday</option>
														<option value="SAT" ${scheduleCommands[schedule.oid].daysOfWeek == 'SAT' ? 'selected' :''}>Saturday</option>
														<option value="SUN" ${scheduleCommands[schedule.oid].daysOfWeek == 'SUN' ? 'selected' :''}>Sunday</option>
													</select>
												</c:if>
												<c:if test="${scheduleCommands[schedule.oid].scheduleType < -2}">
													<select name="daysOfMonth" id="daysOfMonth" onchange="onChangeScheduleType('scheduleForm');">
														<c:forEach var="i" begin="1" end="31">
														<option value="<c:out value='${i}'/>" 
															<c:set var="j">${i}</c:set>
															<c:if test="${scheduleCommands[schedule.oid].daysOfMonth != null && (scheduleCommands[schedule.oid].daysOfMonth == j)}"> 
															selected="selected"
															</c:if>
															>
														   <c:out value="${i}"/></option>
														</c:forEach>
														<option value="L" ${'L' == scheduleCommands[schedule.oid].daysOfMonth ? 'selected' :''}>Last</option>
													</select>
												</c:if>
												<c:if test="${scheduleCommands[schedule.oid].scheduleType < -3}">
													<select name="months" id="months" onchange="onChangeScheduleType('scheduleForm');">
														<c:forEach var="month" items="${scheduleMonthOptions[schedule.oid]}">
														<option value="<c:out value="${month.key}"/>" ${month.key == scheduleCommands[schedule.oid].months ? 'selected' :''}><c:out value="${month.value}"/></option>
														</c:forEach>
													</select>
												</c:if>
											</c:when>
											<c:otherwise>
											<div style='font-size:8; color:gray;'>
											<c:out value="${scheduleCommands[schedule.oid].cronExpression}" />
											</div>
											</c:otherwise>
											</c:choose>
											</td>
										</tr>
										</c:forEach>
									</table>
									</form>
									
								</td>
							</tr>
							<tr>
							<td colspan="5">
								<table width="100%">
								<tr>
									<td style="horizontal-align: left;" >
										<input type="image" name="applyschedule" id="applyschedule" src="images/applyschedule-enabled.gif" alt="apply the schedule to the target" width="135" height="22" border="0" <c:if test="${scheduleHasChanged eq 'false'}">disabled='disabled'</c:if> onclick="submitScheduleChangeAction('scheduleForm', '<%=TargetInstanceSummaryCommand.ACTION_SAVE_SCHEDULE%>');" />
										<input type="image" name="discardchanges" id="discardchanges" src="images/discardchanges-enabled.gif" alt="discard any schedule changes" width="135" height="22" border="0" <c:if test="${scheduleHasChanged eq 'false'}">disabled='disabled'</c:if> onclick="submitScheduleChangeAction('scheduleForm', '<%=TargetInstanceSummaryCommand.ACTION_RESET_SCHEDULE%>');" />
									
									</td>
									<td>
									</td>
									<td>
									<input type="checkbox" id="<%=TargetInstanceSummaryCommand.ACTION_RUN_TARGET_NOW%>" name="<%=TargetInstanceSummaryCommand.ACTION_RUN_TARGET_NOW%>" style="visibility: hidden;" checked="checked"/>
										<input type="image" name="harvestnow" id="harvestnow" src="images/harvestnow-enabled.gif" alt="run a new harvest for the target" width="135" height="22" border="0" onclick="submitScheduleChangeAction('scheduleForm', '<%=TargetInstanceSummaryCommand.ACTION_RUN_TARGET_NOW%>');" />
									</td>
								</tr>
								</table>
								
								</td>
							</tr>
							
							<tr>
								<td colspan="9">
									<table class="panel_dotted_row"><tr><td></td></tr></table>
								</td>
							</tr>
							
											<tr>
								<td colspan="9">
									<table class="panel_header_row"><tr><td><div class="panel_header_title">Annotations</div></td></tr></table>
								</td>
							</tr>
							<tr>
								<td colspan="9" cellpadding="0">
								<div style="border: solid 0px; border-color: black; width: 485px; height: 80px; resize: both; word-wrap: break-word; overflow-x: auto; overflow-y: auto;">
									<table>
									<tr>
										<td>
										<c:forEach items="${instance.sortedAnnotations}" var="annotation">
											<span class="hs_annotationdate"><wct:date value="${annotation.date}" type="fullDateTime"/>:&nbsp;</span>
											<span class="hs_annotationtext"><c:out value="${annotation.note}" /></span> <br/>
										</c:forEach>
										</td>
									</tr>
									</table>
								</div>
								</td>
							</tr> 
						</table>
					</td>
				</tr>
				</table>
			</td>
			<td width="10%"></td>
			</tr>
				<!-- LOWER PANEL -->
				<tr>
					<td width="10%"></td>
					<td colspan="2" width="80%">
					<table width="100%" border="0">
					<tr><td width="70%">
					<div>
						<table border="0" width="100%" cellspacing="0px" cellpadding="0px">
							<tr><td colspan="10"><table class="panel_header_row"><tr><td><div class="panel_header_title">Harvest History</div></td></tr></table></td></tr>
							<tr>
								<td>
									<div class="harvestHistoryPanelHeader">
									<table border="0" cellspacing="0px" cellpadding="0px">
									<tr>
										<th class="history_header_row" width="25px">&nbsp;</td>
										<th class="history_header_row" width="120px">Start Date</td>
										<th class="history_header_row" width="70px">State</td>
										<th class="history_header_row" width="70px">Data</td>
										<th class="history_header_row" width="40px">URLs</td>
										<th class="history_header_row" width="40px" title="Number of URLs that failed">Failed</td>
										<th class="history_header_row" width="70px">Elapsed</td>
										<th class="history_header_row" width="40px">KB/s</td>
										<th class="history_header_row" width="160px">Job Status</td>
										<th class="history_header_row" width="75px">QA Status</td>
									</tr>
									</table>
									</div>
								</td>
									
							</tr>
							<tr>
								<td>
								<div class="harvestHistoryPanelBody">
								<table border="0" cellspacing="0px" cellpadding="0px">
								
							<c:set var="historyCounter" scope="page" value="0" />
							<c:set var="tiFound" scope="page" value="false" />
							<c:forEach items="${history}" var="result">
							<c:if test="${instance.oid eq result.oid}">
								<c:set var="tiFound" scope="page" value="true" />
							</c:if>
							<c:set var="historyCounter" scope="page" value="${historyCounter+1}" />
							<c:if test="${tiFound eq 'true'}" >
							
						  		<tr 
						  			<c:if test="${historyCounter gt 1 && instance.target.referenceCrawlOid != null && instance.target.referenceCrawlOid == result.oid}">class='crawl_row_Reference'</c:if>
						  			<c:if test="${historyCounter eq 1}">class='crawl_row_Latest'</c:if>
						  			<c:if test="${historyCounter gt 1 && instance.target.referenceCrawlOid != null && instance.target.referenceCrawlOid != result.oid}">class='crawl_row_<c:out value="${instance.state}"/>'</c:if>
						  			>
						  		<td width="25px">
						  			<c:choose>
						  			<c:when test="${ result.state == 'Archived' && instance.target.referenceCrawlOid == null || (result.state == 'Archived' && instance.target.referenceCrawlOid != null && instance.target.referenceCrawlOid != result.oid)}">
						  			<input type="radio" name="RCrawl_radio" onclick="document.refcrawl.<%=TargetInstanceSummaryCommand.PARAM_REF_CRAWL_OID%>.value=<c:out value="${result.oid}"/>; enableButton('denoterefcrawl');" >
						  			</c:when>
						  			<c:otherwise>&nbsp;</c:otherwise>
						  			</c:choose>
						  		</td>
						  		<td width="120px" nowrap><wct:date type="fullDateTime" value="${result.startTime}"/></td>
						  		<td width="70px" nowrap><c:out value="${result.state}"/></td>
						  		<td width="70px" nowrap><c:out value="${result.downloadSize}"/></td>
						  		<td width="40px" nowrap><c:out value="${result.urlsDownloaded}"/></td>
						  		<td width="40px" nowrap><c:out value="${result.urlsFailed}"/></td>
						  		<td width="70px" nowrap><c:out value="${result.elapsedTimeString}"/></td>
						  		<td width="40px" nowrap><c:out value="${result.kilobytesPerSecond}"/></td>
						  		<td width="160px" nowrap><c:out value="${result.harvestStatus}"/></td>
						  		<td width="75px"><c:out value="${instance.state}"/></td>
								</tr>
							</c:if>
							</c:forEach>
							</table>
							</div>
							</td>
							</tr>
						</table>
						</div>
						
						</td>
						<td>
							<!-- annotations -->
							<table border="0" width="100%" height="100%">
								<tr>
									<td style="vertical-align: middle;">
										<table class="panel" border="0" width="100%" cellspacing="0px">
										<tr><td colspan="9"><table class="panel_header_row"><tr><td><div class="panel_header_title">Add Annotation</div></td></tr></table></td></tr>
										</table>
									</td>
								</tr>
								<tr>
									<td style="vertical-align: top; horizontal-align: left;">
										<table border="0" width="100%">
										<tr>
											<td width="100%" style="vertical-align: top;">
												<form id="addannotation" name="addannotation" method="POST" action="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/curator/target/qatisummary.html">
													<input type="hidden" name="<%=TargetInstanceSummaryCommand.PARAM_OID%>" value="${instance.oid}" />
													<input type="hidden" name="<%=TargetInstanceSummaryCommand.PARAM_CMD%>" value="<%=TargetInstanceSummaryCommand.ACTION_ADD_NOTE%>" />
													<textarea id="note" rows="8" cols="45" name="note"></textarea>
												</form>
											</td>
										</tr>
										</table>
									</td>
								</tr>
							</table>
						
						</td>
						</tr>
						<tr>
							<td>
								<form id="refcrawl" name="refcrawl" method="POST" action="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/curator/target/qatisummary.html">
									<input type="hidden" id="<%=TargetInstanceSummaryCommand.PARAM_OID%>" name="<%=TargetInstanceSummaryCommand.PARAM_OID%>" value="${instance.oid}" />
									<input type="hidden" id="<%=TargetInstanceSummaryCommand.PARAM_REF_CRAWL_OID%>" name="<%=TargetInstanceSummaryCommand.PARAM_REF_CRAWL_OID%>" value="" />
									<input type="hidden" name="<%=TargetInstanceSummaryCommand.PARAM_CMD%>" value="<%=TargetInstanceSummaryCommand.ACTION_DENOTE_REF_CRAWL%>" />
								</form>
															
								<input type="image" name="denoterefcrawl" id="denoterefcrawl" src="images/denotereferencecrawl-enabled.gif" alt="mark the harvest history record as a reference crawl" width="135" height="22" border="0" onclick="if (document.refcrawl.<%=TargetInstanceSummaryCommand.PARAM_REF_CRAWL_OID%>!='') {disableButton($(this).attr('id')); document.refcrawl.submit();} else return false;" />
								
							</td>
							<td>
								<input type="image" name="addannotation" id="addannotation" src="images/addannotation-enabled.gif" alt="add an annotation to this target instance" width="135" height="22" border="0" onclick="return document.addannotation.submit();" />
							</td>
						</tr>
						</table>
					</td>
					<td width="10%"></td>
				</tr>
			</table>
