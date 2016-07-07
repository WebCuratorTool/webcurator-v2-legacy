<link rel="stylesheet" href="styles/blitzer/jquery-ui-1.10.2.custom.min.css" />
<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>
<script src="scripts/jquery-ui-1.10.2.custom.min.js" type="text/javascript"></script>
<script type="text/javascript"> 
	$(document).ready(function() {
		$('.dateEntry').datepicker({dateFormat: 'dd/mm/yy', changeMonth: true, changeYear: true, showOtherMonths: true, selectOtherMonths: true, showButtonPanel: true});
	});
</script>

<script type="text/javascript">

/**
 * DHTML date validation script. Courtesy of SmartWebby.com (http://www.smartwebby.com/dhtml/)
 */
// Declaring valid date separator character
var dtCh = "/";

function isInteger(s) {
	var i;
	for (i = 0; i < s.length; i++){   
		// Check that current character is number.
		var c = s.charAt(i);
		if (((c < "0") || (c > "9"))) return false;
	}
	// All characters are numbers.
	return true;
}

function stripCharsInBag(s, bag) {
	var i;
	var returnString = "";
	// Search through string's characters one by one.
	// If character is not in bag, append to returnString.
	for (i = 0; i < s.length; i++){   
		var c = s.charAt(i);
		if (bag.indexOf(c) == -1) returnString += c;
	}
	return returnString;
}

function daysInFebruary (year) {
	// February has 29 days in any year evenly divisible by four,
	// EXCEPT for centurial years which are not also divisible by 400.
	return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
}

function DaysArray(n) {
	for (var i = 1; i <= n; i++) {
		this[i] = 31;
		if (i == 4 || i == 6 || i == 9 || i == 11) {
			this[i] = 30;
		}
		if (i == 2) {
			this[i] = 29;
		}
	}
	return this;
}

function getYear(dtStr) {
	var pos1 = dtStr.indexOf(dtCh)
	var pos2 = dtStr.indexOf(dtCh,pos1+1)
	var strYear = dtStr.substring(pos2+1)
	return strYear;
}

function validateDate(dtStr) {
	var daysInMonth = DaysArray(12)
	var pos1 = dtStr.indexOf(dtCh)
	var pos2 = dtStr.indexOf(dtCh,pos1+1)
	var strDay = dtStr.substring(0,pos1)
	var strMonth = dtStr.substring(pos1+1,pos2)
	var strYear = dtStr.substring(pos2+1)
	strYr = strYear
	if (strDay.charAt(0) == "0" && strDay.length > 1) strDay = strDay.substring(1)
	if (strMonth.charAt(0) == "0" && strMonth.length > 1) strMonth = strMonth.substring(1)
	for (var i = 1; i <= 3; i++) {
		if (strYr.charAt(0) == "0" && strYr.length>1) strYr = strYr.substring(1)
	}
	month = parseInt(strMonth)
	day = parseInt(strDay)
	year = parseInt(strYr)
	if (pos1 == -1 || pos2 == -1){
		return ("The date format should be dd/mm/yyyy");
	}
	if (strMonth.length < 1 || month < 1 || month > 12){
		return ("Please enter a valid month");
	}
	if (strDay.length < 1 || day < 1 || day > 31 || (month == 2 && day > daysInFebruary(year)) || day > daysInMonth[month]) {
		return ("Please enter a valid day");
	}
	if (strYear.length != 4 || year == 0) {
		return ("Please enter a valid 4 digit year");
	}
	if (dtStr.indexOf(dtCh,pos2+1)!=-1 || isInteger(stripCharsInBag(dtStr, dtCh)) == false){
		return ("Please enter a valid date");
	}
	return null;
}

/*
 * Validates the form elements - with exception handling.
 * Note: This function will be invoked by the validateAndConfirm() function in the
 * deposit-form-envelope.jsp in WCTCore module.
 */
function validate() {
	try {
		return validate_internal();
	} catch (e) {
		alert("Please try later - Unexpected Javascript error occurred: " + e.message);
		return false;
	}
}

/*
 * Validates the form elements. Any errors will be thrown back to the calling function.
 */
function validate_internal() {
//	if (validateText(document.CustomDepositForm.customDepositForm_volume.value) == false) {
//		alert("Please enter the Volume");
//		return false;
//	}
//	var dctermsPeriodicity = getSelectionFromList(document.CustomDepositForm.customDepositForm_dctermsAccrualPeriodicity);
//	if (validateText(dctermsPeriodicity) == false) {
//		alert("Please select the frequency of publication");
//		return false;
//	}
//	var message = validateDate(document.CustomDepositForm.customDepositForm_dctermsAvailable.value);
//	if (message) {
//		alert("Issue date is invalid. " + message);
//		return false;
//	}
	// If user didn't provide issue year, populate it from the year part of dcterms available
//	if (validateText(document.CustomDepositForm.customDepositForm_dctermsIssued.value) == false) {
//		document.CustomDepositForm.customDepositForm_dctermsIssued.value = getYear(document.CustomDepositForm.customDepositForm_dctermsAvailable.value);
//	}
	if (validateText(document.CustomDepositForm.customDepositForm_producerAgent.value) == false) {
		alert("Please enter the producer agent id");
		return false;
	}
	if (validateText(document.CustomDepositForm.customDepositForm_producerAgentPassword.value) == false) {
		alert("Please enter the producer agent password");
		return false;
	}

//	var producerId = "";
//	// If a Producer Id was preset then use that
//	var producerPreset = document.CustomDepositForm.customDepositForm_ProducerIdPreset.value
//	if(producerPreset != ""){
//		producerId = producerPreset;
//		if(validateText(producerId) == false) {
//			alert("Please select a producer");
//			return false;
//		}
//	}
//	else{
//		producerId = getSelectionFromList(document.CustomDepositForm.customDepositForm_producerId);
//		if (validateText(producerId) == false) {
//			alert("Please select a producer");
//			return false;
//		}
//	}

	var producerId = getChosenProducer();
	if (validateText(producerId) == false) {
		alert("Please select a producer");
		return false;
	}

	/*
	 * Now comes the tricky part.
	 *The following statements will validate:
	 * (1) the agent name/password and 
	 * (2) whether the producer is assigned to correct material flow. 
	 *
	 * The calls for these are made asynchronous via AJAX and then the control flow waits for
	 * a predefined time (as defined by variable "sleepTimeSecs"). If the Rosetta does not
	 * respond within this time, then the validation is considered failed.
	 */
	// Get the div elements for various statuses, and initialise them. 
	var statusDiv = document.getElementById('submissionValidationResultDiv');
	var passwordValidationResultDiv = document.getElementById('passwordValidationResultDiv');
	var materialFlowValidationResultDiv = document.getElementById('materialFlowValidationResultDiv');
	statusDiv.innerHTML = "<b>Submission Status</b>:Validating various parameters against Rosetta";
	passwordValidationResultDiv.innerHTML = "Loading from Rosetta...";
	materialFlowValidationResultDiv.innerHTML = "Loading from Rosetta...";
	// Send the validation requests for name/password and material flow
	sendPasswordValidationRequest();
	sendMaterialFlowValidationRequest();
	var sleepTimeSecs = 15; // Define the sleep time
	var i=0;
	var targetDcType = document.CustomDepositForm.customDepositForm_targetDcType.value;
	var gotPasswordValidationResult = false;
	var gotMaterialFlowValidationResult = false;
	var rosettaTimeoutHappened = false;
	/*
	 * Do a check if the results have come back. If not, sleep for 1 sec.
	 * Do this until "sleepTimeSecs" seconds elapse.
	 */
	for (i = 0; i < sleepTimeSecs; i++) {
		if (passwordValidationResultDiv.innerHTML != "Loading from Rosetta...") {
			statusDiv.innerHTML = "<b>Submission Status</b>: " + passwordValidationResultDiv.innerHTML;
			gotPasswordValidationResult = true;
		}
		if (materialFlowValidationResultDiv.innerHTML != "Loading from Rosetta...") {
			statusDiv.innerHTML = "<b>Submission Status</b>: " + materialFlowValidationResultDiv.innerHTML;
			gotMaterialFlowValidationResult = true;
		}
		if (gotPasswordValidationResult == true && gotMaterialFlowValidationResult == true) {
			rosettaTimeoutHappened = false;
			break;
		}
		rosettaTimeoutHappened = true;
		sleep(1000); // Sleep 1 second
	}
	// Rosetta timeout occurred. Alert the user and do not submit the form
	if (rosettaTimeoutHappened == true) {
		var msg1 = "Rosetta did not respond within " + sleepTimeSecs + " seconds."
		var msg2 = "Aborting the submit process. Please try later."
		statusDiv.innerHTML = "<b>Submission Status</b>: " + msg1 + " " + msg2;
		alert(msg1 + "\n" + msg2);
		clearValidationResultDivs();
		return false;
	}
	// User/password is invalid. Alert the user and do not submit the form
	if (passwordValidationResultDiv.innerHTML.indexOf('Producer agent name and password are valid') < 0) {
		statusDiv.innerHTML = "<b>Submission Status</b>: " + passwordValidationResultDiv.innerHTML;
		alert(passwordValidationResultDiv.innerHTML);
		clearValidationResultDivs();
		return false;
	}
	// Material flow is invalid. Alert the user and do not submit the form
	if (materialFlowValidationResultDiv.innerHTML.indexOf('Producer is associated with the right material flow') < 0) {
		statusDiv.innerHTML = "<b>Submission Status</b>: " + materialFlowValidationResultDiv.innerHTML;
		alert("Selected producer is not associated with the right material flow for type " + targetDcType + "."
			+ "\n\nPlease logon to Rosetta and make this association before submitting this harvest.");
		clearValidationResultDivs();
		return false;
	}
	clearValidationResultDivs();
	statusDiv.innerHTML = "<b>Submission Status</b>: Validation is complete - Harvest is ready to submit to Rosetta";
	// All clear. Return true to submit the form.
	return true;
}

function sleep(milliSeconds){
	var responseText = ajaxFunctionSynchronized("query=sleep&milliSeconds=" + milliSeconds, 'sleepResultDiv');
}

/*
 * Provides a custom confirmation message to be shown to the user before submitting
 * a harvest after filling in the custom deposit screen.
 * Note: This function will be invoked by the validateAndConfirm() function in the
 * deposit-form-envelope.jsp in WCTCore module.
 */
function getConfirmationMessage() {
	var finalList = "Please confirm the following before submitting. If you need to make changes, select Cancel";
	finalList += "\n";
	if (validateText(document.CustomDepositForm.customDepositForm_volume.value)) {
		finalList += "\n\tVolume: " + document.CustomDepositForm.customDepositForm_volume.value;
	}
	if (validateText(document.CustomDepositForm.customDepositForm_issue.value)) {
		finalList += "\n\tIssue: " + document.CustomDepositForm.customDepositForm_issue.value;
	}
	if (validateText(document.CustomDepositForm.customDepositForm_number.value)) {
		finalList += "\n\tNumber: " + document.CustomDepositForm.customDepositForm_number.value;
	}
	if (validateText(document.CustomDepositForm.customDepositForm_year.value)) {
		finalList += "\n\tYear: " + document.CustomDepositForm.customDepositForm_year.value;
	}
	if (validateText(document.CustomDepositForm.customDepositForm_month.value)) {
		finalList += "\n\tMonth: " + document.CustomDepositForm.customDepositForm_month.value;
	}
	if (validateText(document.CustomDepositForm.customDepositForm_day.value)) {
		finalList += "\n\tDay: " + document.CustomDepositForm.customDepositForm_day.value;
	}
	finalList += "\n\tProducer Agent: " + document.CustomDepositForm.customDepositForm_producerAgent.value;
	finalList += "\n\tProducer Password: *******";
	finalList += "\n\tProducer: " + getChosenProducer();
	return finalList;
}

/*
 * Performs any cleanups before submitting a harvest after filling in the custom deposit screen.
 * Note: This function will be invoked by the validateAndConfirm() function in the
 * deposit-form-envelope.jsp in WCTCore module.
 */
function doAnyCleanUps() {
	clearValidationResultDivs();
}

function trim(stringToTrim) {
	if (stringToTrim)
		return stringToTrim.replace(/^\s+|\s+$/g,"");
	else
		return stringToTrim;
}

function validateText(textValue) {
	if (textValue == null || trim(textValue) == "") {
		return false;
	}
	return true;
}

function getSelectedOptionFromList(listObj) {
	if (! listObj) return null;
	var selectedIndex = listObj.selectedIndex;
	if (selectedIndex == null || selectedIndex < 0) return null;
	return listObj.options[selectedIndex];
}

function getSelectionFromList(listObj) {
	var optionSelected = getSelectedOptionFromList(listObj);
	if (! optionSelected) return "";
	return optionSelected.value;
}

function sendPasswordValidationRequest() {
	var producerAgent = document.CustomDepositForm.customDepositForm_producerAgent.value;
	if (validateText(producerAgent) == false) {
		alert("Please enter the producer agent id");
		return false;
	}
	var producerAgentPassword = document.CustomDepositForm.customDepositForm_producerAgentPassword.value;
	if (validateText(producerAgentPassword) == false) {
		alert("Please enter the producer agent password");
		return false;
	}
	ajaxFunction("query=validateProducerAgent&producerAgent=" + producerAgent + "&agentPassword=" + producerAgentPassword, 'passwordValidationResultDiv');
	return false;
}

function sendMaterialFlowValidationRequest() {
	var targetDcType = document.CustomDepositForm.customDepositForm_targetDcType.value;
	//var producerId = getSelectionFromList(document.CustomDepositForm.customDepositForm_producerId);
	var producerId = getChosenProducer();
	if (validateText(producerId) == false) {
		alert("Please select a producer");
		return false;
	}
	ajaxFunction("query=validateMaterialFlowAssociation&producer=" + producerId + "&targetDcType=" + targetDcType, 'materialFlowValidationResultDiv');
	return false;
}

function getProducers(fromCache) {
	var producerAgent = document.CustomDepositForm.customDepositForm_producerAgent.value;
	if (validateText(producerAgent) == false) {
		alert("Please enter the producer agent id");
		return false;
	}
	ajaxFunction("query=getProducers&producerAgent=" + producerAgent + "&fromCache=" + fromCache, 'producerResultDiv');
	return false; 
}

function getProducer() {
	var producerAgent = document.CustomDepositForm.customDepositForm_producerAgent.value;
	var producerId = document.CustomDepositForm.customDepositForm_ProducerIdPreset.value;
	if (validateText(producerAgent) == false) {
//		alert("Please enter the producer agent id");
		return false;
	}
	if (validateText(producerId) == false) {
		alert("Please enter the producer agent id");
		return false;
	}
	ajaxFunction("query=getProducerName&producerAgent=" + producerAgent + "&producerId=" + producerId, 'producerPresetDiv');
	return false;
}

function getChosenProducer(){
	var producerId = "";
	// If a Producer Id was preset then use that
	var producerPreset = document.CustomDepositForm.customDepositForm_ProducerIdPreset.value
	if(producerPreset != ""){
		producerId = producerPreset;
	}
	else{
		producerId = getSelectionFromList(document.CustomDepositForm.customDepositForm_producerId);
	}
	return producerId;
}

/*
 * Asynchronous AJAX function to invoke a web URL that gets data
 * from Rosetta.
 */
function ajaxFunction(requestParams, destinationDiv) {
	return ajaxFunction_internal(requestParams, destinationDiv, true);
}

/*
 * Synchronous AJAX function to invoke a web URL that gets data
 * from Rosetta.
 * Note: The browser freezes until this method returns with something
 * from Rosetta. If this becomes a problem at some point, we need to 
 * come up with an alternate logic.
 */
function ajaxFunctionSynchronized(requestParams, destinationDiv) {
	return ajaxFunction_internal(requestParams, destinationDiv, false);
}

function ajaxFunction_internal(requestParams, destinationDiv, asynchronousFlag) {
	var xmlhttp;
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	} else {
		// code for IE6, IE5
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.onreadystatechange=function() {
		if(xmlhttp.readyState == 4) {
			document.getElementById(destinationDiv).innerHTML = trim(xmlhttp.responseText);
		} else {
			document.getElementById(destinationDiv).innerHTML = 'Loading from Rosetta...';
		}
	}
	//This will not work if the store is on a different host/port.  The same origin
	//policy will prevent the request if it is changed to use the configured values.
	xmlhttp.open("POST", "/wct-store/customDepositForms/rosetta_interface_via_ajax.jsp", asynchronousFlag);
	xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

	//The following two lines attempt to violate the XMLHttpRequest standard:
	//http://www.w3.org/TR/XMLHttpRequest/#the-setrequestheader-method
	//Section 4.6.2 (step 5).	
	xmlhttp.setRequestHeader("Content-length", requestParams.length);
	xmlhttp.setRequestHeader("Connection", "close");
	xmlhttp.send(requestParams);
	return "";
}

function clearValidationResultDivs() {
	setDivValue('submissionValidationResultDiv', "<b>&nbsp;</b>");
	clearDiv('passwordValidationResultDiv');
	clearDiv('materialFlowValidationResultDiv');
}

function clearDiv(divName) {
	setDivValue(divName, "");
}

function setDivValue(divName, value) {
	var divToClear = document.getElementById(divName);
	if (divToClear && divToClear.innerHTML) {
		document.getElementById(divName).innerHTML = value;
	}
}
</script>


<table>
<tr>
<td colspan="5"><h1>Electronic Serial Metadata:</h1></td>
</tr>
	<tr>
		<td>Volume: </td>
		<td><input size="25" title="Volume" type="text" name="customDepositForm_volume"/></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
		<td>Issue: </td>
		<td><input size="25" title="Issue" type="text" name="customDepositForm_issue"/></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
		<td>Number: </td>
		<td><input size="25" title="Number" type="text" name="customDepositForm_number"/></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
	</tr>
	<tr>
		<td>Year: </td>
		<td><input size="10" title="Year" type="text" name="customDepositForm_year"/></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
		<td>Month: </td>
		<td><input size="10" title="Month" type="text" name="customDepositForm_month"/></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
		<td>Day: </td>
		<td><input size="10" title="Day" type="text" name="customDepositForm_day"/></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
<!--	COMMENTED
<td>Frequency of Publication: </td>
<td>
<select name="customDepositForm_dctermsAccrualPeriodicity" title="Frequency of Publication"  style="width:100%;">
	<option value="">Please select frequency</option>
	<option value="Weekly">Weekly</option>
	<option value="Daily">Daily</option>
	<option value="Fortnightly">Fortnightly</option>
	<option value="Monthly">Monthly</option>
	<option value="Quarterly">Quarterly</option>
	<option value="Annually">Annually</option>
	<option value="2 per week">2 per week</option>
	<option value="2 per month">2 per month</option>
	<option value="6 per year">6 per year</option>
	<option value="2 per year">2 per year</option>
	<option value="3 per year">3 per year</option>
	<option value="Biennial">Biennial</option>
	<option value="Triennial">Triennial</option>
	<option value="Other">Other</option>
</select>
</td>    
END COMMENTED 	-->

</tr>
	<!--	COMMENTED
<tr>
<td>Issue Date (dd/mm/yyyy):</td>
<td><input class="dateEntry" size="25" title="Issue Date" type="text" name="customDepositForm_dctermsAvailable"/>
</td>
<td></td>
END COMMENTED 	-->

<tr>
<td colspan="5"><br></td>
</tr>
	<tr>
		<td colspan="5">
			<h1>Producer Details:</h1>
			<div id="producerLinkDiv" style="visibility:hidden">
				<a title="Displays the last viewed list of producers" style="text-decoration:underline;" href="#" onClick="javascript: return getProducers(true);">Display list</a>
			</div>
			<div id="producerPresetDiv" style="visibility:hidden">
			</div>
			<br>
			<div id="producerResultDiv">
			</div>
		</td>
	</tr>
<tr>
<td colspan="5"><br></td>
</tr>
<tr>
<td colspan="5"><h1>Enter the Rosetta password:</h1></td>
</tr>
<tr>
<td colspan="5">
<input size="25" title="Agent Password" type="password" name="customDepositForm_producerAgentPassword"/>
<input title="Producer Agent" type="hidden" name="customDepositForm_producerAgent" value=""/>
</td>
</tr>
<tr>
<td colspan="5">
<div id="genericValidationResultDiv">
</div>
<div id="passwordValidationResultDiv" style="display: none;">
</div>
<div id="materialFlowValidationResultDiv" style="display: none;">
</div>
<div id="sleepResultDiv" style="display: none;">
</div>
</td>
</tr>
<tr>
<td colspan="5">
<div id="submissionValidationResultDiv">
<b>&nbsp;</b>
</div>
</td>
</tr>
</table>
<script type="text/javascript">
	document.CustomDepositForm.customDepositForm_producerAgent.value = document.CustomDepositForm.customDepositForm_loggedInUser.value;
	// If there is a Producer Id already set, then query Rosetta for producer and display details in producerPresetDiv div
	var producerPreset = document.CustomDepositForm.customDepositForm_ProducerIdPreset.value
	if(producerPreset != "" && !isNaN(producerPreset)){
		getProducer();
		document.getElementById('producerPresetDiv').style.visibility = "visible";
	}
	else{
		document.getElementById('producerLinkDiv').style.visibility = "visible";
	}
</script>
