<%@page import="org.webcurator.ui.site.command.*" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>

<script>
  function removeUrl(urlId) {
	document.getElementById("urlId").value = urlId;
	return true;
  }
  
  function setActionCmd(val) {
    document.getElementById("actionCmd").value = val;
  }
</script>
<c:set var="editMode" value="${siteEditorContext.editMode}"/>
			<c:if test="${editMode}">
			<table cellpadding="3" cellspacing="0" border="0">
				<tr>
				  <td class="subBoxTextHdr">New URL Pattern: </td>
				  <c:choose>
				  	<c:when test="${!empty command}"><td class="subBoxText"><input type="text" name="url" value="<c:out value="${command.url}" />" style="width:250px;" /></td></c:when>
				  	<c:otherwise><td class="subBoxText"><input type="text" name="url" value="" style="width:480px"/></td></c:otherwise>
				  </c:choose>			      
				  <td class="subBoxText"><input type="image" name="_add_url" src="images/subtabs-add-btn.gif" alt="Add" width="49" height="23" border="0" hspace="5" align="absmiddle" onclick="setActionCmd('<%= UrlCommand.ACTION_ADD_URL %>');" /></td>
				</tr>
			</table>
			</c:if>
			<input type="hidden" id="urlId" name="urlId">
			<input type="hidden" id="actionCmd" name="actionCmd">
			
			<div id="annotationsBox">
			<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
			<table width="100%" cellpadding="3" cellspacing="0" border="0">
				<tr>
					<td class="annotationsHeaderRow">URL Pattern</td>
					<td class="annotationsHeaderRow">
					<c:if test="${editMode}">
					Action
					</c:if>
					</td>
				</tr>
			
				<c:forEach items="${urls}" var="url" varStatus="status">
				  <tr>
				    <td class="annotationsLiteRow"><c:out value="${url.pattern}"/></td>
				    <td class="annotationsLiteRow">
				    <c:if test="${editMode}">
				    	<input type="image" src="images/action-icon-delete.gif" title="Delete" alt="Delete" width="18" height="19" border="0" onclick="removeUrl('<c:out value="${url.identity}"/>'); setActionCmd('<%= UrlCommand.ACTION_REMOVE_URL %>');" /></td>
				    </c:if>
				  </tr>

				</c:forEach>
			</table>
			</div>		