<%@ page import="org.webcurator.ui.groups.command.MemberOfCommand" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>

<c:set var="editMode" value="${groupEditorContext.editMode}"/>

<script type="text/javascript">
<!--
  function setPageNumber(pageNum) {
    document.getElementById('pageNumber').value = pageNum;
    document.getElementById('tabForm').submit();
  }

  function setPageSize(pageSize) {
    document.getElementById('selectedPageSize').value = pageSize;
    document.getElementById('tabForm').submit();
  }

  function unlinkMember(parentOid) {
    document.getElementById('actionCmd').value = '<%= MemberOfCommand.ACTION_UNLINK_FROM_GROUP %>';
    document.getElementById('parentOid').value = parentOid;
  }
  
// -->
</script>

<input type="hidden" id="pageNumber" name="pageNumber" value="${command.pageNumber}">
<input type="hidden" id="selectedPageSize" name="selectedPageSize" value="${page.pageSize}">
<input type="hidden" id="parentOid" name="parentOid" value="">
<input type="hidden" id="actionCmd" name="actionCmd" value="<%= MemberOfCommand.ACTION_UNLINK_FROM_GROUP %>">
			<authority:showControl ownedObject="${groupEditorContext.targetGroup}" editMode="${editMode}" privileges="<%= Privilege.ADD_TARGET_TO_GROUP %>">
			  <authority:show>
			  </authority:show>
			</authority:showControl>
			<div id="annotationsBox">
				<table width="100%" cellpadding="3" cellspacing="0" border="0">
					<tr>
						<td class="annotationsHeaderRow">Type</td>
						<td class="annotationsHeaderRow">Name</td>
						<td class="annotationsHeaderRow">Action</td>
					</tr>
					<c:choose>
				    <c:when test="${empty memberof.list}">
				      <tr>
				        <td class="annotationsLiteRow" colspan="3">This group is not a member of any other groups.</td>
				      </tr>
				    </c:when>
				    <c:otherwise>
				    <c:forEach items="${memberof.list}" var="result" varStatus="i">
					<tr>
					  <td class="annotationsLiteRow">
						  <c:choose> 
						    <c:when test="${result.childType == 0}">Group</c:when>
						    <c:otherwise><spring:message code="ui.label.common.target"/></c:otherwise>
						  </c:choose>     
					  </td>
			          <td class="annotationsLiteRow">
			            <c:choose>
			              <c:when test="${wct:containsObj(groupEditorContext.targetGroup.removedChildren, result.childOid)}">
			              	<span class="deletedGroupMember"><wct:groupname name="${result.parentName}" subGroupSeparator="${subGroupSeparator}"/></span>
			              </c:when>
			              <c:otherwise>
							<wct:groupname name="${result.parentName}" subGroupSeparator="${subGroupSeparator}"/>
			              </c:otherwise>
			            </c:choose>
			          </td>
			          <td class="annotationsLiteRow">
						  <a href="curator/groups/groups.html?targetGroupOid=<c:out value="${result.parentOid}"/>&mode=view"><img src="images/action-icon-view.gif" title="View" alt="click here to VIEW this item" width="15" height="19" border="0" hspace="3" /></a>			            
						  <authority:showControl ownedObject="${groupEditorContext.targetGroup}" privileges="<%= Privilege.CREATE_GROUP %>" editMode="${groupEditorContext.editMode}">
						    <authority:show>
							  <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
							  <a href="curator/groups/groups.html?targetGroupOid=<c:out value="${result.parentOid}"/>&mode=edit"><img src="images/action-icon-edit.gif" title="Edit" alt="click here to EDIT this item" width="18" height="18" border="0" /></a>
						    </authority:show>
						    <authority:dont>
						      &nbsp;
						    </authority:dont>
					      </authority:showControl>			          
			          </td>
			        </tr>       
			        </c:forEach>	
			        <tr>
			        	<td colspan="3">
			        		<jsp:include page="pagination.jsp"/>
			        	</td>
			        </tr>					
			        </c:otherwise>
				    </c:choose>
				 </table>
			 </div>
