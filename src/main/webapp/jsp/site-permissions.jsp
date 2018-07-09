<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@page import="org.webcurator.domain.model.auth.Privilege" %>
<script>
  function selectRow(id) {
	document.getElementById("selectedPermission").value = id;
	return true;
  }
</script>
<c:set var="editMode" value="${siteEditorContext.editMode}"/>
<input type="hidden" id="selectedPermission" name="selectedPermission">
			<c:if test="${editMode}">
			<authority:hasPrivilege privilege="<%=Privilege.MODIFY_PERMISSION%>" scope="<%=Privilege.SCOPE_AGENCY%>">
			<table width="100%" cellpadding="3" cellspacing="0" border="0">
				<tr>
				    <td align="right" valign="bottom"><input type="image" src="images/create-new-btn-red.gif" alt="New" width="82" height="24" border="0" align="right" name="_new" /></td>
				</tr>
			</table>
			</authority:hasPrivilege>
			</c:if>
			<div id="annotationsBox">
				<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
				<table width="100%" cellpadding="3" cellspacing="0" border="0">
				  <tr>
				    <td class="annotationsHeaderRow">Status</td>
				    <td class="annotationsHeaderRow">Date Requested</td>
				    <td class="annotationsHeaderRow">Authorising Agent</td>
				    <td class="annotationsHeaderRow">From</td>
				    <td class="annotationsHeaderRow">To</td>
				    <td class="annotationsHeaderRow">URL Patterns</td>
				    <td class="annotationsHeaderRow">Action</td>
				  </tr>
				  <c:forEach items="${permissions}" var="permission" varStatus="status">
				  <tr>
				    <td class="annotationsLiteRow"><spring:message code="permission.state_${permission.status}"/></td>
					<c:choose>
						<c:when test="${permission.status eq '1'}">
							<td class="annotationsLiteRow"><wct:date value="${permission.permissionSentDate}" type="fullDateTime"/></td>
						</c:when>
						<c:otherwise>
							<td class="annotationsLiteRow">&nbsp;</td>
						</c:otherwise>
					</c:choose>				
				    <td class="annotationsLiteRow"><c:out value="${permission.authorisingAgent.name}"/></td>
				    <td class="annotationsLiteRow"><wct:date value="${permission.startDate}" type="fullDate"/></td>
				    <td class="annotationsLiteRow"><wct:date value="${permission.endDate}" type="fullDate"/></td>
				    <td class="annotationsLiteRow">
				    	<c:forEach items="${permission.urls}" var="url">
				    	  ${url.pattern}<br/>
				    	</c:forEach>				    
				    </td>
				    <td class="annotationsLiteRow">				    
				      <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />					
				      <input type="image" name="_view_permission" src="images/action-icon-view.gif" title="View" alt="View" width="18" height="18" border="0" onclick="selectRow('<c:out value="${permission.identity}"/>')">				      					
				    
				    <authority:showControl ownedObject="${permission}" privileges="<%= Privilege.MODIFY_PERMISSION %>" editMode="${editMode}">
				      <authority:show>
				      <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				      <input type="image" name="_edit_permission" src="images/action-icon-edit.gif" title="Edit" alt="Edit" width="18" height="18" border="0" onclick="selectRow('<c:out value="${permission.identity}"/>')">
				      <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				      <input type="image" name="_remove_permission" src="images/action-icon-delete.gif" title="Delete" alt="Delete" width="18" height="19" border="0" onclick="selectRow('<c:out value="${permission.identity}"/>')"/>
				      </authority:show>
					</authority:showControl>
				   	<authority:showControl ownedObject="${permission}" privileges="<%= Privilege.TRANSFER_LINKED_TARGETS %>" editMode="${editMode}">
				   	  <authority:show>
					   	  <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />					
					      <!-- <a href="curator/site/transfer.html?actionCmd=search&fromPermissionOid=${permission.oid}"><img src="images/action-transfer-seed.gif" border=0 title="Transfer" alt="Transfer" /></a> -->
					      <a href="curator/site/transfer.html?actionCmd=init&fromPermissionOid=${permission.oid}" onclick="return confirm('Continuing will save this Harvest Authorisation. Do you wish to continue?');"><img src="images/action-transfer-seed.gif" border=0 title="Transfer" alt="Transfer" /></a>
				      </authority:show>
				   	</authority:showControl>
				    </td>
				  </tr>
				  </c:forEach>
				</table>
			</div>