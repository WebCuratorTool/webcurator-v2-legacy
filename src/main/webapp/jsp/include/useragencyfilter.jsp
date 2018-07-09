<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">
<!--

var agencies = new Array();
var users = new Array();
var allUsers = new Array();
var user = new Array();

<c:set scope="page" var="auCnt" value="0" />
<c:set scope="page" var="uCnt" value="0" />
<c:forEach items="${agencies}" var="agency">	
	users = new Array();
	<c:forEach items="${agency.users}" var="user">
		user = new Array();		
		user[0] = "<c:out value="${user.username}"/>";
		user[1] = "<c:out value="${user.firstname}"/>" + " " + "<c:out value="${user.lastname}"/>";
		users["<c:out value="${uCnt}"/>"] = user;
		allUsers["<c:out value="${auCnt}"/>"] = user;
		<c:set scope="page" var="uCnt" value="${uCnt + 1}" />
		<c:set scope="page" var="auCnt" value="${auCnt + 1}" />		
	</c:forEach>		
	<c:set scope="page" var="uCnt" value="0" />
	agencies["<c:out value="${agency.name}"/>"] = users;	
</c:forEach>

//----------------------------------------------------------
// Respond to the onAgencyChange event. 
// This function will change the list of selectable users 
// based on the agency that is selected.
// @param agencyFieldId The agency field element's ID.
// @param userFieldId   The user field element's ID.
//----------------------------------------------------------
function onAgencyChange(agencyFieldId, userFieldId) {
    agencyField = document.getElementById(agencyFieldId);
    userField   = document.getElementById(userFieldId);
    
	var selectedAgency = agencyField.value;
	userField.options.length = 0;

	userField.options[0] = new Option("", "");
	if (selectedAgency != '') {
		for (i=0;i<agencies[selectedAgency].length;i++) {
			userField.options[i+1] = new Option(agencies[selectedAgency][i][1],agencies[selectedAgency][i][0]);
		}
	}
	else {		
		for (i=0;i<allUsers.length;i++) {
			userField.options[i+1] = new Option(allUsers[i][1],allUsers[i][0]);
		}
	}	
}


// -->
</script>