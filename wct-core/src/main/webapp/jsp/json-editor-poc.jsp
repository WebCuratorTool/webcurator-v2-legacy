<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.FileWriter"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.lang.StringBuilder"%>




<%-- The code that deals with the form post: blindly writes the submitted profile to disk --%>
<%
String submittedProfile = (String)request.getParameter("profile");
if (submittedProfile != null) {
	String filePath = Thread.currentThread().getContextClassLoader().getResource("example-profile.json").getPath();
	try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath)))
	{
    		writer.write(submittedProfile);
	}
}
%>




<html>
    <head>
    <meta http-equiv="Pragma" content="no-cache"/>
    <title>JSON editor proof of concept</title>

<script src="../scripts/jsoneditor.min.js"></script>

    </head>

    <body>

	<h2>Proof of concept JSON editor for crawler profiles</h2>

	<div id='editorDiv'></div>

        <form id="editorForm" onsubmit="return submitForm();" method="POST" action="/wct/jsp/json-editor-poc.jsp" accept-charset="utf-8">
	<input type="hidden" id="profile" name="profile"/>
	<input type="submit" value="Save profile"/>
	</form>

<script language="JavaScript">


// Handle form submission
function submitForm() {
	// Validate 
	errors=editor.validate();
	if (errors.length) { // and complain 
		alert("Validation error: " + errors);
	} else { // or submit
		var form = document.getElementById('editorForm'); 
		document.getElementById('profile').value = JSON.stringify(editor.getValue());
		form.submit();
	}
	return false;
}


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
var editor = new JSONEditor(element, {schema: <%= profileSchema %>, 
                                      disable_collapse: true, 
				      disable_edit_json: true, 
                                      disable_properties: true,
                                      show_errors: "change"});



// load the example profile in the editor
editor.setValue(<%= profile %>);





</script>

    </body>
</html>
