<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.lang.StringBuilder"%>

<html>
    <head>
    <title>JSON editor proof of concept</title>

<script src="../scripts/jsoneditor.min.js"></script>

    </head>

    <body>

	<h2>Proof of concept JSON editor for crawler profiles</h2>

	<div id='editorDiv'></div>
	<button id='submit'>Submit (console.log)</button>
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




// Hook up the submit button to log to the console
document.getElementById('submit').addEventListener('click',

	function() {
		// Validate and log
		errors=editor.validate();
		if (errors.length) {
			alert("Validation error: " + errors);
		} else {
			console.log(editor.getValue());
		}
	}

);


</script>

    </body>
</html>
