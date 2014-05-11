<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
  function selectItem(id) {
	document.getElementById("identity").value = id;
	return true;
  }
</script>
<c:set var="editMode" value="${siteEditorContext.editMode}"/>
<input type="hidden" id="identity" name="identity" value="-1">

			<c:if test="${editMode}">
			<table width="100%" cellpadding="3" cellspacing="0" border="0">
				<tr>
				    <td align="right" valign="bottom">
				      <table>
				        <tr>
				          <td><a href="curator/site/site-auth-agency-search.html"><img src="images/search-box-btn.gif" border="0"></a></td>
				          <td><input type="image" src="images/create-new-btn-red.gif" alt="New" width="82" height="24" border="0" align="right" name="_new_auth" /></td>
				        </tr>
				      </table>
				    </td>
				</tr>
			</table>
			</c:if>
			<div id="annotationsBox">
				<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
				<table width="100%" cellpadding="4" cellspacing="0" border="0">
				  <tr>
				    <td class="annotationsHeaderRow">Authorising Agency</td>
				    <td class="annotationsHeaderRow">Contact</td>
				    <td class="annotationsHeaderRow"><c:if test="${editMode}">Action</c:if></td>
				  </tr>
				<c:forEach items="${agents}" var="agent" varStatus="status">
				  <tr>
				    <td class="annotationsLiteRow"><c:out value="${agent.name}"/></td>
   				    <td class="annotationsLiteRow"><c:out value="${agent.contact}"/></td>
				    <td class="annotationsLiteRow">
				    <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				      <input type="image" name="_view_agency" src="images/action-icon-view.gif" title="View" alt="View" width="18" height="18" border="0" onclick="selectItem('<c:out value="${agent.identity}"/>')">    				    
				    <c:if test="${editMode}">
				      <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				      <input type="image" name="_edit_agency" src="images/action-icon-edit.gif" title="Edit" alt="Edit" width="18" height="18" border="0" onclick="selectItem('<c:out value="${agent.identity}"/>')">    
				      <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				      <input type="image" name="_remove_agency" src="images/action-icon-delete.gif" title="Delete" alt="Delete" width="18" height="19" border="0" onclick="selectItem('<c:out value="${agent.identity}"/>')">
				     </c:if>
				    </td>
				  </tr>
				</c:forEach>
				</table>
			</div>