
<script type="text/javascript">


/*
 * Validates the form elements.
 * Note: This function will be invoked by the validateAndConfirm() function in the
 * deposit-form-envelope.jsp in WCTCore module.
 */
function validate() {
	alert("You have not selected correct properties for this harvest.\nPlease select Cancel to correct the data associated with this harvest.");
	return false;
}

/*
 * Provides a custom confirmation message to be shown to the user before submitting
 * a harvest after filling in the custom deposit screen.
 * Note: This function will be invoked by the validateAndConfirm() function in the
 * deposit-form-envelope.jsp in WCTCore module.
 */
function getConfirmationMessage() {
	var finalList = "Please select Cancel to correct the data associated with this harvest";
	return finalList;
}


</script>

<div id="messageDiv">
<h1>Invalid Target Type</h1>
A target type of HTML serial was not selected for this harvest.
Please hit Cancel and go to the Descriptions tab of the target to set a target type.
</div>

<br>

