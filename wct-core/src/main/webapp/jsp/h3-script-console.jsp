<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.webcurator.ui.profiles.command.H3ScriptConsoleCommand"%>
<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>

<script type="text/javascript">

  function updateScriptValue(scriptsList, scriptValueSelected) {
    //alert(scriptValueSelected);
    if (scriptValueSelected != 'none') {
      // find the script in the scripts list
      var script;
      $.each(scriptsList, function(index, value) {
        if (value.scriptName == scriptValueSelected) {
          script = value.script;
        }
      });
      //alert(script);
      $("#script").val(script);
    }
  }

  function updateScriptEngine(scriptsList, scriptValueSelected) {
    //alert(scriptValueSelected);
    if (scriptValueSelected != 'none') {
      // find the type in the scripts list
      var type;
      $.each(scriptsList, function(index, value) {
        if (value.scriptName == scriptValueSelected) {
          type = value.scriptType;
        }
      });
      //alert(type);
      $("#scriptEngine option[value='" + type + "']").attr("selected", true);
    }
  }

  function makeReadOnlyScript(scriptValueSelected) {
    var readOnly = scriptValueSelected == 'none' ? false : true;
    $("#script").prop('readonly', readOnly);
  }

  $(document).ready(function() {
    var scriptsList = [];
    <c:forEach items="${scripts}" var="scriptInList">
      var jsScript = {
        scriptName: "<c:out value='${scriptInList.key.key}'/>",
        scriptType: "<c:out value='${scriptInList.key.value}'/>",
        script: "<c:out value='${scriptInList.value}'/>"
      };
      scriptsList.push(jsScript);
    </c:forEach>
    //alert(JSON.stringify(scriptsList));

    // update script text box
    updateScriptValue(scriptsList, $('#scriptSelected').val());
    // update script engine list
    updateScriptEngine(scriptsList, $('#scriptSelected').val());
    // make read only (or not) based on selection
    makeReadOnlyScript($('#scriptSelected').val());

    $('#scriptSelected').change(function() {
      var scriptValueSelected = this.value;
      // update script text box
      updateScriptValue(scriptsList, scriptValueSelected);
      // update script engine list
      updateScriptEngine(scriptsList, scriptValueSelected);
      // make read only (or not) based on selection
      makeReadOnlyScript(scriptValueSelected);
    });

  });

</script>

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
      <td class="subBoxTextHdr">Scripts:</td>
      <td class="subBoxText">
        <select name="scriptSelected" id="scriptSelected">
	      <option value="none" ${command.scriptSelected eq 'none' ? 'SELECTED' : ''}>None</option>
	      <c:forEach items="${scripts}" var="scr">
	        <option value="${scr.key.key}" ${command.scriptSelected eq scr.key.key ? 'SELECTED' : ''}>${scr.key.key}</option>
	      </c:forEach>
	    </select>
      </td>
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
