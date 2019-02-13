<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.lang.StringBuilder"%>



<html>
    <head>
    <title>JSON editor proof of concept</title>

    </head>

    <body>

	<h2>Proof of concept JSON-based crawler stats view</h2>



    <div id="stats"/>





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

<%-- Read example status info from disk --%>
<%
String statusInfo = readFileFromClasspath("example-status-info.json");
%>



    <script>

	// Simple display function for arbitrary JSON input
	function display(node) {
		var html = "";
		if (node !== null) {
			if (Array.isArray(node)) {
				html += "<ul>";
				for (i in node) {
					html += display(node[i]);	
				}
				html += "</ul>";
			} else {
				for (k in node) {
					html += "<li>" + k + ": ";
					if (typeof(node[k]) == "object") {
						html += "<ul>" + display(node[k]) + "</ul>";
					} else {
						html += node[k];
					}
					html += "</li>";
				}
			}
		}
		return html;
	}

        var statusJson = <%= statusInfo %> ;
        document.getElementById("stats").innerHTML = "<ul>" + display(statusJson) + "</ul>";


    </script>


    </body>

    </html>
