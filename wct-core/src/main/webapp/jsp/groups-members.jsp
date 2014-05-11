<%@ page import="org.webcurator.ui.groups.command.MembersCommand" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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

  function unlinkMember(childOid) {
    setAction('<%= MembersCommand.ACTION_UNLINK_MEMBER %>');
    document.getElementById('childOid').value = childOid;
  }
  
  function toggleSelectAll(seedCount)
  {
	var checkSelectAll = document.getElementById("chkSelectAll");
	var targetCount = document.getElementById("targetCount");
	for(var i = 1; i <= targetCount.value; i++)
	{
		var check = document.getElementById("chkTargetOid"+i);
		if(check && checkSelectAll)
		{
			check.checked = checkSelectAll.checked;
		}
	}
  }
  
  function setAction(str)
  {
  	document.getElementById('actionCmd').value = str;
  }
  
  function processCheckbox(chkName)
  {
  	var chkBox = document.getElementById(chkName);
  	var chkAll = document.getElementById("chkSelectAll");
  	if(chkBox.checked == false)
  	{
  		chkAll.checked = false;
  	}
  }
  
// -->
</script>

<input type="hidden" id="pageNumber" name="pageNumber" value="${command.pageNumber}">
<input type="hidden" id="selectedPageSize" name="selectedPageSize" value="${members.pageSize}">
<input type="hidden" id="childOid" name="childOid" value="">
<input type="hidden" id="actionCmd" name="actionCmd" value="<%= MembersCommand.ACTION_UNLINK_MEMBER %>">
			<authority:showControl ownedObject="${groupEditorContext.targetGroup}" editMode="${editMode}" privileges="<%= Privilege.ADD_TARGET_TO_GROUP %>">
			  <authority:show>
				<table width="100%" cellpadding="3" cellspacing="0" border="0">
					<tr>
					    <td align="right" valign="bottom">
					    	<a href="curator/groups/add-members.html"><img src="images/subtabs-add-btn.gif" alt="Add Members" title="Add Members" width="49" height="23" border="0" align="right" /></a></td>
				      	</td>
					    <td align="left" width="10%" valign="bottom">
				      		<input id="btnMove" type="image" src="images/subtabs-move-btn.gif" alt="Move Targets" width="49" height="23" border="0" align="right" onclick="setAction('<%= MembersCommand.ACTION_MOVE_TARGETS%>');">
				      	</td>
					</tr>
				</table>
			  </authority:show>
			</authority:showControl>
			<div id="annotationsBox">
				<table width="100%" cellpadding="3" cellspacing="0" border="0">
					<tr>
						<td class="annotationsHeaderRow"><input id="chkSelectAll" name="chkSelectAll" type="checkbox" onclick="toggleSelectAll()"/></td>
						<td class="annotationsHeaderRow">Type</td>
						<td class="annotationsHeaderRow">Name</td>
						<td class="annotationsHeaderRow">Action</td>
					</tr>
					<c:choose>
				    <c:when test="${empty members.list}">
				      <tr>
				        <td class="annotationsLiteRow" colspan="3">This group has no members.</td>
				      </tr>
				    </c:when>
				    <c:otherwise>
				    <c:forEach items="${members.list}" var="result" varStatus="i">
		  			<c:set var="targetCount" value="${i.count}"/>
					<tr>
					  <td class="annotationsLiteRow">
						  <c:choose> 
						    <c:when test="${result.childType == 0}">&nbsp;</c:when>
						    <c:otherwise>
						    	<input type="checkbox" id="<c:out value="chkTargetOid${i.count}"/>" name="targetOids" value="<c:out value="${result.childOid}"/>" onclick="processCheckbox('<c:out value="chkTargetOid${i.count}"/>')"/>
						    </c:otherwise>
						  </c:choose>     
					  </td>
					  <td class="annotationsLiteRow">
						  <c:choose> 
						    <c:when test="${result.childType == 0}">
							  <c:choose> 
							    <c:when test="${fn:contains(result.childName,subGroupSeparator)}">Sub-Group</c:when>
							    <c:otherwise>Group</c:otherwise>
							  </c:choose>     
						    </c:when>
						    <c:otherwise><spring:message code="ui.label.common.target"/></c:otherwise>
						  </c:choose>     
					  </td>
			          <td class="annotationsLiteRow">
			            <c:choose>
			              <c:when test="${wct:containsObj(groupEditorContext.targetGroup.removedChildren, result.childOid)}">
			              	<span class="deletedGroupMember"><wct:groupname name="${result.childName}" subGroupSeparator="${subGroupSeparator}"/></span>
			              </c:when>
			              <c:otherwise>
						  <c:choose> 
						    <c:when test="${result.childType == 0}">
								<wct:groupname name="${result.childName}" subGroupSeparator="${subGroupSeparator}"/>
						    </c:when>
						    <c:otherwise>
				                <c:out value="${result.childName}"/>
							</c:otherwise>
						  </c:choose>     
			                
			              </c:otherwise>
			            </c:choose>
			          </td>
			          <td class="annotationsLiteRow">
			            <authority:showControl ownedObject="${groupEditorContext.targetGroup}" privileges="<%= Privilege.MANAGE_GROUP %>" editMode="${groupEditorContext.editMode}">
						  <authority:show>
						  <c:if test="${!fn:contains(result.childName,subGroupSeparator)}">
						    <c:if test="${!wct:containsObj(groupEditorContext.targetGroup.removedChildren, result.childOid)}">
						    <input type="image" title="Remove" alt="Remove" src="images/action-icon-delete.gif" height="19" width="18" onclick="unlinkMember(${result.childOid});">
						    </c:if>
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
			        	<td colspan="3">
			        		<jsp:include page="pagination.jsp"/>
			        	</td>
			        </tr>					
			        </c:otherwise>
				    </c:choose>
				 </table>
			 </div>
			 <input type="hidden" id="targetCount" name="targetCount" value="<c:out value="${targetCount}"/>">
			 