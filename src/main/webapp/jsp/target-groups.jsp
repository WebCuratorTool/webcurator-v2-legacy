<%@ page import="org.webcurator.ui.target.command.TargetGroupsCommand" %>
<%@ page import="org.webcurator.domain.model.dto.GroupMemberDTO.SAVE_STATE" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>

<c:set var="editMode" value="${targetEditorContext.editMode}"/>

<script type="text/javascript">
<!--
  function setPageNumber(pageNum) {
    document.getElementById('pageNumber').value = pageNum;
    document.getElementById('tabForm').submit();
  }
  
  function setPageSize(pageSize) {
    document.getElementById('selectedPageSize').value = pageSize;
    document.getElementById('actionCmd').value = '';
    document.getElementById('tabForm').submit();
  }

  function unlinkParent(parentOid) {
    document.getElementById('actionCmd').value = '<%= TargetGroupsCommand.ACTION_UNLINK_PARENT %>';
    document.getElementById('parentOid').value = parentOid;
  }  

// -->
</script>

<input type="hidden" id="pageNumber" name="pageNumber" value="${page.page}">
<input type="hidden" id="selectedPageSize" name="selectedPageSize" value="${page.pageSize}">
<input type="hidden" id="parentOid" name="parentOid" value="">
<input type="hidden" id="actionCmd" name="actionCmd" value="<%= TargetGroupsCommand.ACTION_UNLINK_PARENT %>">
			<authority:showControl ownedObject="${targetEditorContext.target}" editMode="${editMode}" privileges="<%= Privilege.ADD_TARGET_TO_GROUP %>">
			  <authority:show>
				<table width="100%" cellpadding="3" cellspacing="0" border="0">
					<tr>
					    <td align="right" valign="bottom"><a href="curator/targets/add-parents.html"><img src="images/subtabs-add-btn.gif" alt="Add Members" title="Add Members" width="49" height="23" border="0" align="right" /></a></td>
					</tr>
				</table>
			  </authority:show>
			</authority:showControl>
			<div id="annotationsBox">
				<table width="100%" cellpadding="3" cellspacing="0" border="0">
					<tr>
						<td class="annotationsHeaderRow">Name</td>
						<td class="annotationsHeaderRow">Action</td>
					</tr>
					<c:choose>
				    <c:when test="${empty page.list}">
				      <tr>
				        <td class="annotationsLiteRow" colspan="3">This target does not belong to any groups.</td>
				      </tr>
				    </c:when>
				    <c:otherwise>
				    <c:forEach items="${page.list}" var="result" varStatus="i">
					<tr>
			          <td class="annotationsLiteRow">
			            <c:choose>
			              <c:when test="${result.saveState == 'DELETED'}">
			              	<span class="deletedGroupMember"><wct:groupname name="${result.parentName}" subGroupSeparator="${subGroupSeparator}"/></span>
			              </c:when>
			              <c:otherwise>
			              	<wct:groupname name="${result.parentName}" subGroupSeparator="${subGroupSeparator}"/>
			              </c:otherwise>
			            </c:choose>
			          </td>
			          <td class="annotationsLiteRow">
			            <authority:showControl ownedObject="${targetEditorContext.target}" privileges="<%= Privilege.MODIFY_TARGET %>" editMode="${targetEditorContext.editMode}">
						  <authority:show>
						    <c:if test="${result.saveState != 'DELETED'}">
						    <input type="image" title="Remove" alt="Remove" src="images/action-icon-delete.gif" height="19" width="18" onclick="unlinkParent(${result.parentOid});">
						    </c:if>
						  </authority:show>
						  <authority:dont>
						    &nbsp;
						  </authority:dont>
						</authority:showControl>			          
			          </td>
			        </tr>       
			        </c:forEach>	
			        <tr>
			        	<td colspan="2">
			        		<jsp:include page="pagination.jsp"/>
			        	</td>
			        </tr>					
			        </c:otherwise>
				    </c:choose>
				 </table>
			 </div>