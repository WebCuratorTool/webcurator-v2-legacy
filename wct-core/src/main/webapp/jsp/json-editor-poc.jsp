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
	<h3>TODO</h3>

	<ol>
		<li>Generate form from JSON schema <b>[V]</b></li>
		<li>Save and load JSON data (from/into this form) into/from file or db</li>
		<li>Build read-only HTML page from template schema</li>
	</ol>

	<div id='editorDiv'></div>
	<button id='submit'>Submit (console.log)</button>
<script language="JavaScript">


var schema = {
          type: "object",
          title: "Heritrix 3 Profile Schema",
          required: ["name", "contactUrl", "ignoreRobotsTxt"],
          properties: {
            name: {
              type: "string",
            },
            contactUrl: {
              type: "string",
	      format: uri 
            },
            description: {
              type: "string"
            },
            documentLimit: {
              type: "integer",
            },
            ignoreRobotsTxt: {
              type: "boolean",
	      default: false
            }
          }
	};

var element = document.getElementById('editorDiv');
var editor = new JSONEditor(element, {schema, disable_collapse: true, disable_edit_json: true, disable_properties: true});



<%-- read example document from disk (shouldn't normally be done in JSP) --%>
<%
InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("example-profile.json");
BufferedReader br = new BufferedReader(new InputStreamReader(in));
StringBuilder sb = new StringBuilder();
String line;

while((line = br.readLine())!= null){
	sb.append(line+"\n");
}
String profile = sb.toString();
%>

// load the example document in the editor
editor.setValue(<%= profile %>);




// Hook up the submit button to log to the console
document.getElementById('submit').addEventListener('click',

	function() {
		// Validate and log
		errors=editor.validate();
		if (errors.length) {
			console.log(errors);
		} else {
			console.log(editor.getValue());
		}
	}

);


</script>



	<p>
		<% long time = System.currentTimeMillis(); %>
		Time: <%= time %>
	</p>


    </body>
</html>
