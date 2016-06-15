<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.webcurator.ui.target.command.TargetInstanceCommand" %>
 
<script type="text/javascript"> 
/*
 * This function is called before submitting the form.
 * Please note that some of the functions being called from
 * this function (validate() and getConfirmationMessage())are 
 * to be implemented by the custom deposit form.
 */
function validateAndConfirm() {
	var validationStatus = true; 
	if (typeof validate == 'function') { // Check if this function is implemented by the custom deposit form HTML
		var validationStatus = validate(); // Validate the form elements as implemented by the custom form
	}
	if (validationStatus == false) return false;
	var confirmMessage = "Do you really want to submit this Target Instance to the Archive?";
	if (typeof getConfirmationMessage == 'function') { // Check if this function is implemented by the custom deposit form HTML
		confirmMessage = getConfirmationMessage(); // Get the confirmation message as implemented by the custom form
	}
	var confirmStatus = confirm(confirmMessage);
	if (typeof doAnyCleanUps == 'function') { // Check if this function is implemented by the custom deposit form HTML
		doAnyCleanUps(); // Do any final cleanups as implemented by the custom form
	}
	return confirmStatus;
}
 
/*
 * Prevent the form submission by accidental hitting of ENTER key
 */
function stopRKey(evt) { 
	var evt = (evt) ? evt : ((event) ? event : null); 
	var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null); 
	var nodeType = (node.type) ? node.type.toLowerCase() : null;
	if ((evt.keyCode == 13) && ((nodeType == "text") || (nodeType == "password"))) {
		return false;
	}
} 
 
document.onkeypress = stopRKey; 
 
</script> 
 
<form name="CustomDepositForm" action="curator/archive/submit.html" onSubmit="javascript: return validateAndConfirm();">
 
<input type="hidden" name="instanceID" value="<%= request.getParameter("targetInstanceID") %>">
<input type="hidden" name="harvestNumber" value="<%= request.getParameter("harvestResultNumber") %>">
<input type="hidden" name="customDepositForm_customFormPopulated" value="true">
<input type="hidden" name="customDepositForm_loggedInUser" value="<%= org.webcurator.core.util.AuthUtil.getRemoteUser() %>">
<input type="hidden" name="customDepositForm_ProducerPreset" value="<c:out value="${customDepositFormProducerId}"/>">
<input type="hidden" name="customDepositForm_targetDcType" value="<c:out value="${sessionTargetInstance.target.dublinCoreMetaData.type}"/>">

<c:choose>
<c:when test="${not empty customDepositFormURL}">
<c:import url="${customDepositFormURL}"/>
</c:when>
<c:when test="${not empty customDepositFormHTMLContent}">
<c:out value="${customDepositFormHTMLContent}" escapeXml="false"/>
</c:when>
</c:choose>
<p/>
<input type="image" src="images/generic-btn-apply.gif" title="Apply these values and submit to archive" alt="Apply these values and submit to archive" />
<a href="#" onclick="window.history.back();return false;"/><img src="images/generic-btn-cancel.gif" border="0" alt="Cancel"></a>
 
</form>

