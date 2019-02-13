<html>
    <head>
    <title>JSON editor proof of concept</title>

    </head>

    <body>

	<h2>Proof of concept JSON-based crawler stats view</h2>



    <div id="stats"/>

    <script>

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

        var d = [
            {'Status':'RUNNING'},
            {'Start time':'2019-02-12T21:21:11.020Z'},
            {'Harvested': {'Number of URLs': '512', 'Size (MB)': '233'}},
	    {'Number of errors':'32'}
        ];

        document.getElementById("stats").innerHTML = "<ul>" + display(d) + "</ul>";


    </script>


    </body>

    </html>
