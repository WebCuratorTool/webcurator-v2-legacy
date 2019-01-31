<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.lang.StringBuilder"%>




<%-- The code that deals with the form post: saves the submitted profile --%>
<%
String submittedProfile = (String)request.getParameter("profile");
%>




<html>
    <head>
    <title>JSON editor proof of concept</title>

<script src="../scripts/jsoneditor.min.js"></script>

    </head>

    <body>

	<h2>Proof of concept JSON editor for crawler profiles</h2>

	<p>Posted: <%= submittedProfile %></p>

	<div id='editorDiv'></div>

        <form id="editorForm" method="POST" action="/wct/jsp/json-editor-poc.jsp">
	<input type="hidden" id="profile" name="profile"/>
	<button id='submit'>Save profile</button>
	</form>

<script language="JavaScript">



<%-- Read files from classpath (shouldn't normally be done in JSP)--%>
<%!
private String readFileFromClasspath(String baseName) throws Exception {
	try (
     	     InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(baseName);
     	     BufferedReader br = new BufferedReader(new InputStreamReader(in))
            ) {
		StringBuilder sb = new StringBuilder();
		String line;

		while((line = br.readLine())!= null){
			sb.append(line+"\n");
		}
		return sb.toString();
	}
}
%>

<%
String profileSchema = readFileFromClasspath("profile-schema.json");
String profile = readFileFromClasspath("example-profile.json");
%>



// create the editor using the profile schema
var element = document.getElementById('editorDiv');
var editor = new JSONEditor(element, {schema: <%= profileSchema %>, disable_collapse: true, disable_edit_json: true, disable_properties: true});



// load the example profile in the editor
editor.setValue(<%= profile %>);



// Handle form submission
document.getElementById('submit').addEventListener('click',

	function() {
		// Validate and complain or submit
		errors=editor.validate();
		if (errors.length) {
			alert("Validation error: " + errors);
		} else {
			document.getElementById('profile').value = JSON.stringify(editor.getValue());
			alert(document.getElementById('profile').value);
			document.getElementById('editorForm').submit();
		}
	}

);


</script>

    </body>
</html>
