<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script src="scripts/codemirror/lib/codemirror.js"></script>
<link rel="stylesheet" href="scripts/codemirror/lib/codemirror.css"/>
<script src="scripts/codemirror/mode/xml/xml.js"></script>

<style>
.CodeMirror {
  height: 40em;
}
</style>



<table cellpadding="3" cellspacing="0" border="0">
<input type="hidden" name="profileName" value="${command.profileName}"/>
<textarea id="h3RawProfile" name="h3RawProfile">
<c:out value="${command.h3RawProfile}"/>
</textarea>


<script>
var myCodeMirror = CodeMirror.fromTextArea(document.getElementById("h3RawProfile"),
                                        {mode: "text/xml",
                                        lineNumbers: true,
                                        lineWrapping: true});
</script>


</table>