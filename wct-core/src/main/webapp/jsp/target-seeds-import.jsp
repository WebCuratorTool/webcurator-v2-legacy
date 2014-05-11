<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="org.webcurator.ui.target.command.SeedsCommand" %>

<script type="text/javascript">
<!--
 
  function setActionCmd(val) {
  	document.getElementById('actionCmd').value = val;
  }
 
// -->
</script>
<input type="hidden" id="actionCmd" name="actionCmd">

<p class="subBoxText">
  Select a file to upload seeds from. Each seed must be on
  a new line. All empty lines or lines beginning with the 
  # symbol are ignored.
</p>

<table>
  <tr>
    <td class="subBoxTextHdr">Seed File:</td>
    <td class="subBoxText"><input name="seedsFile" type="file"></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Permission:</td>
    <td class="subBoxText">
		<select name="permissionMappingOption">
		  <option value="<%= SeedsCommand.PERM_MAPPING_AUTO %>">Auto</option>
		  <option value="<%= SeedsCommand.PERM_MAPPING_NONE %>">Add Later</option>
		  <c:forEach items="${quickPicks}" var="quickPick">
		  <option value="${quickPick.oid}"><c:out value="${quickPick.displayName}"/></option>
		  </c:forEach>
		</select>    
    </td>
  </tr>
</table>
<br/>
<br/>
<input type="image" src="images/generic-btn-done.gif" alt="Import" title="Import" width="82" height="23" border="0" onclick="setActionCmd('<%= SeedsCommand.ACTION_DO_IMPORT %>');"/><img src="images/x.gif" alt="" width="10" height="1" border="0" />
<input type="image" src="images/generic-btn-cancel.gif" alt="Cancel" title="Cancel" width="82" height="23" border="0" onclick="setActionCmd('<%= SeedsCommand.ACTION_LINK_NEW_CANCEL %>');"/>