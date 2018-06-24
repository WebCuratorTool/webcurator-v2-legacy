<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.webcurator.ui.profiles.command.H3ScriptConsoleCommand"%>

<script>
<!--
  function executeScript() {
   	if(document.getElementById("script").value.trim() != "") {
   	  document.forms.executeScriptForm.submit();
    } else {
       alert("You must specify a script to execute.");
       return false;
   	}
  }

//-->
</script>

<form id="executeScriptForm" action="curator/target/h3ScriptConsole.html" method="POST">
  <table>
    <tr>
      <td class="subBoxText" colspan="2">
        <input type="hidden" id="actionCommand" name="actionCommand" value="<%=H3ScriptConsoleCommand.ACTION_EXECUTE_SCRIPT %>" />
        <input type="hidden" name="targetInstanceOid" value="<c:out value="${targetInstance.oid}"/>" />
      </td>
    </tr>
    <tr>
      <td class="subBoxTextHdr">Target Instance Oid</td>
      <td class="subBoxText"><c:out value="${targetInstance.oid}"/></td>
    </tr>
    <tr>
      <td class="subBoxTextHdr">Target Name</td>
      <td class="subBoxText"><c:out value="${targetInstance.target.name}"/></td>
    </tr>
    <tr>
      <td class="subBoxTextHdr">Script Engine:</td>
      <td class="subBoxText">
        <select name="scriptEngine" id="scriptEngine">
          <option value="beanshell" ${command.scriptEngine eq 'beanshell' ? 'SELECTED' : ''}>BeanShell</option>
          <option value="groovy" ${command.scriptEngine eq 'groovy' ? 'SELECTED' : ''}>Groovy</option>
          <option value="nashorn" ${command.scriptEngine eq 'nashorn' ? 'SELECTED' : ''}>ECMAScript</option>
	    </select>
      </td>
    </tr>
    <tr>
      <td class="subBoxTextHdr">Script</td>
      <td class="subBoxText"><textarea cols="80" rows="5" id="script" name="script"><c:out value="${command.script}"/></textarea></td>
    </tr>
    <tr>
      <td class="subBoxTextHdr"></td>
      <td class="subBoxText">
        <input type="button" name="excecuteScript" value="Execute Script" onClick="executeScript();">
      </td>
    </tr>
  </table>

  <div style="height: 400px; width: 710px; overflow: scroll; border: 1px solid black; padding: 10px;">
    <pre><c:out value="${result}"/></pre>
  </div>
</form>
