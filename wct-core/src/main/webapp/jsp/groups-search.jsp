<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@page import="org.webcurator.domain.model.auth.Privilege"%>
<%@page import="org.webcurator.ui.groups.command.SearchCommand"%>

<script language="javascript">
<!-- 
  function setPageNumber(pageNumber) {
    document.getElementById('pageNumber').value = pageNumber;
    document.getElementById('searchForm').submit();
  }
  
  function setPageSize(pageSize) {
    document.getElementById('selectedPageSize').value = pageSize;
    document.getElementById('searchForm').submit();
  }

  function setActionCmd(actionCmd) {
    document.getElementById('actionCmd').value = actionCmd;
  }  
// -->
</script>
<jsp:include page="include/useragencyfilter.jsp"/>
<c:if test="${message != null}">
<p><b><c:out value="${message}"/></b></p>
</c:if>
<form id="searchForm" action="curator/groups/search.html" method="post">
<input type="hidden" id="pageNumber" name="pageNumber" value="${command.pageNumber}">
<input type="hidden" id="selectedPageSize" name="selectedPageSize" value="${page.pageSize}">
<input type="hidden" id="actionCmd" name="actionCmd" value="">
<div id="searchBox">
			<img src="images/search-box-top.gif" alt="Search" width="900" height="36" border="0" /><br/>
			<div id="searchBoxContent">
				<table cellpadding="0" cellspacing="3" border="0">
				<tr>
					<td class="searchBoxLabel">
						ID:<br />
						<input type="text" name="searchOid" value="<c:out value="${command.searchOid}"/>" style="width:70px"/>
					</td>				
					<td class="searchBoxLabel">
						Name:<br />
						<input type="text" name="name" value="<c:out value="${command.name}"/>" />
					</td>
					<td class="searchBoxLabel">
						Agency:<br />
						<select name="agency" id="agency" onchange="onAgencyChange('agency', 'owner');">
        					<option value="" ${command.agency eq '' ? 'SELECTED' : ''}></option>
							<c:forEach items="${agencies}" var="a">
								<option value="<c:out value="${a.name}"/>" ${command.agency eq a.name ? 'SELECTED' : ''}><c:out value="${a.name}"/></option>
							</c:forEach>
      					</select>
					</td>
					<td class="searchBoxLabel">
						Owner:<br />
						<select name="owner" id="owner">
					        <option value="" ${command.owner eq '' ? 'SELECTED' : ''}></option>
							<c:forEach items="${owners}" var="o">
        						<option value="<c:out value="${o.username}"/>" ${command.owner eq o.username ? 'SELECTED' : ''}><c:out value="${o.firstname}"/>&nbsp;<c:out value="${o.lastname}"/></option>
							</c:forEach>
      					</select>
					</td>
					<td class="searchBoxLabel">
						Member of:<br />
						<input name="memberOf" type="text" value="<c:out value="${command.memberOf}"/>"/>
					</td>					
				</tr>
				<tr>
				    <td class="searchBoxLabel">
				    	Type:<br />
		            	<wct:list paramName="groupType" currentValue="${command.groupType}" list="${groupTypesList}"/>
					</td>    
					<td class="searchBoxLabel">
						<authority:hasPrivilege privilege="<%= Privilege.MODIFY_TARGET %>" scope="<%= Privilege.SCOPE_NONE %>">
							&nbsp;&nbsp;Non-Display Only<br />&nbsp;&nbsp;<input type="checkbox" name="nondisplayonly" id="nondisplayonly" ${command.nondisplayonly ? 'checked' : ''}/>
						</authority:hasPrivilege>
					</td>
					<td align="right" valign="bottom" colspan="4"><input type="image" src="images/search-box-btn.gif" alt="search" width="82" height="24" border="0" onclick="setPageNumber(0);" />&nbsp;<input type="image" src="images/search-box-reset-btn.gif" alt="reset" width="82" height="24" border="0" onclick="setActionCmd('<%= SearchCommand.ACTION_RESET %>'); setPageNumber(0); return;" /></td>					
				</tr>
				</table>
			</div>
<img src="images/search-box-btm.gif" alt="" width="900" height="12" border="0" /></div>	
</form>
<div id="resultsTable">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td colspan="6">
				<authority:hasPrivilege privilege="<%= Privilege.CREATE_GROUP %>" scope="<%= Privilege.SCOPE_AGENCY %>">
				<a href="curator/groups/groups.html"><img src="images/create-new-btn-red.gif" alt="Create a new Target Group" width="82" height="24" border="0" align="right" vspace="3" /></a>
				</authority:hasPrivilege><span class="midtitleGrey">Results</span></td>
			</tr>
			<tr>
				<td class="tableHead">Id</td>
				<td class="tableHead">Name</td>
				<td class="tableHead">Type</td>
				<td class="tableHead">Agency</td>
				<td class="tableHead">Owner</td>
				<td class="tableHead">Status</td>
				<td class="tableHead">Action</td>
			</tr>
			<c:forEach items="${page.list}" var="result">
			<tr>
				<td class="tableRowLite"><c:out value="${result.oid}"/></td>
				<c:choose> 
  				<c:when test="${result.displayTarget == false}" > 
				<td class="tableRowGreyedOut">
					<wct:groupname name="${result.name}" subGroupSeparator="${subGroupSeparator}"/>
				</td>
				</c:when> 
				<c:otherwise> 
				<td class="tableRowLite">
					<wct:groupname name="${result.name}" subGroupSeparator="${subGroupSeparator}"/>
				</td>
				</c:otherwise> 
				</c:choose> 
				<td class="tableRowLite"><c:out value="${result.type}"/></td>
				<td class="tableRowLite"><c:out value="${result.owner.agency.name}"/></td>
				<td class="tableRowLite"><c:out value="${result.owner.firstname} ${result.owner.lastname}"/></td>
				<td class="tableRowLite"><spring:message code="target.state_${result.state}"/></td>
				<td class="tableRowLite">
					<form id="searchForm" action="curator/groups/search.html" method="post" onSubmit="return confirm('Do you really want to delete this Target Group?')">
						<input type="hidden" id="actionCmd" name="actionCmd" value="<%= SearchCommand.ACTION_DELETE %>">
						<input type="hidden" id="deletedGroupOid" name="deletedGroupOid" value="<c:out value="${result.oid}"/>">
						<input type="hidden" id="pageNumber" name="pageNumber" value="${command.pageNumber}">
						<input type="hidden" id="selectedPageSize" name="selectedPageSize" value="${page.pageSize}">
						
						<a href="curator/groups/groups.html?targetGroupOid=<c:out value="${result.oid}"/>&mode=view"><img src="images/action-icon-view.gif" title="View" alt="click here to VIEW this item" width="15" height="19" border="0" hspace="3" /></a>
						<authority:hasUserOwnedPriv privilege="<%= Privilege.CREATE_GROUP %>" ownedObject="${result}" scope="5">
							<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
							<a href="curator/groups/groups.html?targetGroupOid=<c:out value="${result.oid}"/>&mode=edit"><img src="images/action-icon-edit.gif" title="Edit" alt="click here to EDIT this item" width="18" height="18" border="0" /></a>
			 			</authority:hasUserOwnedPriv>
						<authority:hasUserOwnedPriv privilege="<%= Privilege.CREATE_GROUP %>" ownedObject="${result}" scope="5">
							<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
							<a href="curator/groups/groups.html?targetGroupOid=<c:out value="${result.oid}"/>&mode=edit&copyMode=true"><img src="images/action-icon-copy.gif" title="Copy" alt="click here to COPY this item" border="0" /></a>
			 			</authority:hasUserOwnedPriv>			 			
						<authority:hasUserOwnedPriv privilege="<%= Privilege.MANAGE_GROUP %>" ownedObject="${result}">		 			
							<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
							<input type="image" src="images/action-icon-delete.gif" title="Delete" alt="click here to DELETE this item"/>
						</authority:hasUserOwnedPriv>
					</form>

				</td>
			</tr>
			<tr>			
				<td colspan="6" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="5" border="0" /></td>
			</tr>
	        </c:forEach>
	        <tr>			
				<td colspan="6" class="tableRowLite" align="center">						
				<jsp:include flush="true" page="pagination.jsp"/>
				</td>
			</tr>
		</table>		
</div>	
