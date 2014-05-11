<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@page import="org.webcurator.domain.model.auth.Privilege"%>
<%@ page import="org.webcurator.ui.target.command.TargetSearchCommand" %>
<jsp:include page="include/useragencyfilter.jsp"/>
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

<style type="text/css">
	.wrapping {
		max-width:300px;
		word-break:break-all;
		white-space:normal;
	}
	
	#resultsTable table {
		width:100%;
	}
	
	.idColumn      { width:50px; max-width:70px; }
	.createdColumn { width:50px;max-width:50px; }
	.nameColumn    { overflow:hidden;}
	.agencyColumn  { width:100px;max-width:250px; }
	.ownerColumn   { width:50px; max-width:150px; }
	.statusColumn  { width:50px; max-width:70px; }
	.seedsColumn   { overflow:hidden; }
	.actionColumn  { width:50px;max-width:200px; padding-right:20px;}
</style>

<form id="searchForm" action="curator/target/search.html" method="post">
<input type="hidden" id="pageNumber" name="pageNumber" value="${command.pageNumber}">
<input type="hidden" id="selectedPageSize" name="selectedPageSize" value="${page.pageSize}">
<input type="hidden" id="actionCmd" name="actionCmd" value="<%= TargetSearchCommand.ACTION_SEARCH %>">
<div id="searchBox">
			<img src="images/search-box-top.gif" alt="Search" width="900" height="36" border="0" /><br/>
			<div id="searchBoxContent">
				<table cellpadding="0" cellspacing="3" border="0">
				<tr>
					<td class="searchBoxLabel">
						ID:<br />
						<input type="text" name="searchOid" value="<c:out value="${command.searchOid}"/>" style="width:70px" />
					</td>				
					<td class="searchBoxLabel">
						Name:<br />
						<input type="text" name="name" value="<c:out value="${command.name}"/>" />
					</td>
					<td class="searchBoxLabel">
						Seed:<br />
						<input type="text" name="seed" value="<c:out value="${command.seed}"/>" />
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
						User:<br />
						<select name="owner" id="owner">
					        <option value="" ${command.owner eq '' ? 'SELECTED' : ''}></option>
							<c:forEach items="${owners}" var="o">
        						<option value="<c:out value="${o.username}"/>" ${command.owner eq o.username ? 'SELECTED' : ''}><c:out value="${o.firstname}"/>&nbsp;<c:out value="${o.lastname}"/></option>
							</c:forEach>
      					</select>
					</td>
					<td class="searchBoxLabel">
						Sort Order:<br />
						<select name="sortorder" id="sortorder">
							<option value="<%= TargetSearchCommand.SORT_NAME_ASC %>" ${command.sortorder eq 'nameasc' ? 'SELECTED' : ''}>Name (ascending)</option>
							<option value="<%= TargetSearchCommand.SORT_NAME_DESC %>" ${command.sortorder eq 'namedesc' ? 'SELECTED' : ''}>Name (descending)</option>
							<option value="<%= TargetSearchCommand.SORT_DATE_DESC %>" ${command.sortorder eq 'datedesc' ? 'SELECTED' : ''}>Most Recent First</option>
							<option value="<%= TargetSearchCommand.SORT_DATE_ASC %>" ${command.sortorder eq 'dateasc' ? 'SELECTED' : ''}>Oldest First</option>
						</select>
					</td>
				</tr>
				</table>
				<table cellpadding="0" cellspacing="3" border="0" width="100%">
				<tr>
					<td class="searchBoxLabel">
						Description:<br />
						<input type="text" name="description" value="<c:out value="${command.description}"/>" />
					</td>
					<td class="searchBoxLabel">
						Member of:<br />
						<input type="text" name="memberOf" value="<c:out value="${command.memberOf}"/>" style="width: 100px">
					</td>					
					<td class="searchBoxLabel">
						<authority:hasPrivilege privilege="<%= Privilege.MODIFY_TARGET %>" scope="<%= Privilege.SCOPE_NONE %>">
							&nbsp;&nbsp;Non-Display Only<br />&nbsp;&nbsp;<input type="checkbox" name="nondisplayonly" id="nondisplayonly" ${command.nondisplayonly ? 'checked' : ''}/>
						</authority:hasPrivilege>
					</td>
					<td class="searchBoxLabel" valign="top">State:</td>
					<td class="searchBoxLabel" valign="top">
					    <% int maxStates = 7; %>
						<table cellpadding="0" cellspacing="0" border="0">
						    <% for(int i=1; i<= maxStates;) { %>
						    <tr>
						    <% for(int c=0; c<4; c++,i++) { 
						       if(i<=maxStates) {
						         pageContext.setAttribute("stateId", new Integer(i));
						       
						        %>
						       
						      
					          <td class="searchBoxLabel" valign="top">
							  <input type="checkbox" id="states_<c:out value="${stateId}"/>" name="states" value="<c:out value="${stateId}"/>" ${wct:contains(command.states, stateId) ? 'checked' : ''}><label for="states_<c:out value="${stateId}"/>"><spring:message code="target.state_${stateId}"/></label>
					          </td>
					        <% }
					           else {
					           %><td>&nbsp;</td><%
					           }
					           } %>
					        </tr>
					        <% } %>
						  </table>
					</td>
					<td align="right" valign="bottom">
					<input type="image" src="images/search-box-btn.gif" alt="search" width="82" height="24" border="0" onclick="setPageNumber(0);"/>
					&nbsp;
					<input type="image" src="images/search-box-reset-btn.gif" alt="reset" width="82" height="24" border="0" onclick="setActionCmd('<%= TargetSearchCommand.ACTION_RESET %>'); setPageNumber(0);"/>
					</td>
				</tr>
				</table>
			</div>
<img src="images/search-box-btm.gif" alt="" width="900" height="12" border="0" />
</div>	
</form>
<div id="resultsTable">
		<table cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td colspan="7">
				<authority:hasPrivilege privilege="<%= Privilege.CREATE_TARGET %>" scope="<%= Privilege.SCOPE_AGENCY %>">
				<a href="curator/target/target.html"><img src="images/create-new-btn-red.gif" alt="Create a new Target" width="82" height="24" border="0" align="right" vspace="3" /></a>
				</authority:hasPrivilege><span class="midtitleGrey">Results</span></td>
			</tr>
			<tr>
				<td class="tableHead idColumn">Id</td>
				<td class="tableHead createdColumn">Created</td>
				<td class="tableHead wrapping nameColumn">Name</td>
				<td class="tableHead agencyColumn">Agency</td>
				<td class="tableHead ownerColumn">Owner</td>
				<td class="tableHead statusColumn">Status</td>
				<td class="tableHead wrapping seedsColumn">Seeds</td>
				<td class="tableHead actionColumn">Action</td>
			</tr>
			<c:forEach items="${page.list}" var="result">
			<tr>
				<td class="tableRowLite idColumn">
					<c:if test="${result.alertable == true}">
						<img src="images/warn.gif" alt="Annotations with Alerts!" width="9" height="9" border="0" />
     		 		</c:if>
					<c:out value="${result.oid}"/>
				</td>
				<td class="tableRowLite createdColumn">
					<wct:date value="${result.creationDate}" type="shortDate"/>
				</td>
				<c:choose> 
  				<c:when test="${result.displayTarget == false}" > 
				<td class="tableRowGreyedOut wrapping nameColumn">
					<c:out value="${result.name}"/>
				</td>
				</c:when> 
				<c:otherwise> 
				<td class="tableRowLite wrapping nameColumn">
					<c:out value="${result.name}"/>
				</td>
				</c:otherwise> 
				</c:choose> 
				<td class="tableRowLite agencyColumn"><c:out value="${result.owner.agency.name}"/></td>
				<td class="tableRowLite ownerColumn"><c:out value="${result.owner.firstname} ${result.owner.lastname}"/></td>
				<td class="tableRowLite statusColumn"><spring:message code="target.state_${result.state}"/></td>
				<td class="tableRowLite wrapping seedsColumn">
		          	<c:forEach items="${result.seeds}" var="seed">
						<c:choose> 
		  				<c:when test="${seed.primary == true}" > 
							<b><c:out value="${seed.seed}"/></b><br/>
						</c:when> 
						<c:otherwise> 
							<c:out value="${seed.seed}"/><br/>
						</c:otherwise> 
						</c:choose> 
		          	</c:forEach>
		          </td>
				<td class="tableRowLite actionColumn">
					<form action="curator/target/search.html" method="post" onSubmit="return confirm('<spring:message code="ui.label.target.search.confirmDelete" javaScriptEscape="true"/>')">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<a href="curator/target/target.html?targetOid=<c:out value="${result.oid}"/>&mode=view"><img src="images/action-icon-view.gif" title="View" alt="click here to VIEW this item" width="15" height="19" border="0" hspace="3" /></a>

					<authority:hasUserOwnedPriv privilege="<%= Privilege.MODIFY_TARGET %>" ownedObject="${result}" scope="5">
						<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
						<a href="curator/target/target.html?targetOid=<c:out value="${result.oid}"/>&mode=edit"><img src="images/action-icon-edit.gif" title="Edit" alt="click here to EDIT this item" width="18" height="18" border="0" /></a>
		 			</authority:hasUserOwnedPriv>
		 			
		 			<authority:hasPrivilege privilege="<%= Privilege.CREATE_TARGET %>" scope="<%= Privilege.SCOPE_AGENCY %>">
		 			<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
     		 		<a href="curator/target/target.html?targetOid=<c:out value="${result.oid}"/>&copyMode=true&mode=edit"><img src="images/action-icon-copy.gif" title="Copy" alt="click here to COPY this item" border="0" /></a>
     		 		</authority:hasPrivilege>
     		 		
		 			<authority:hasPrivilege privilege="<%= Privilege.MANAGE_TARGET_INSTANCES %>" scope="<%= Privilege.SCOPE_AGENCY %>">
		 			<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
     		 		<a href="curator/target/queue.html?type=target&targetname=<c:out value="${result.name}"/>"><img src="images/action-icon-target-instances.gif" title="View Target Instances" alt="click here to view TIs" border="0" /></a>
     		 		</authority:hasPrivilege>

     		 		<c:if test="${result.state == 1}">
     		 		<authority:hasUserOwnedPriv privilege="<%= Privilege.DELETE_TARGET %>" ownedObject="${result}">
		 			<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
		 			<input type="hidden" name="selectedTargetOid" value="<c:out value="${result.oid}"/>" />
		 			<input type="hidden" name="actionCmd" value="<%= TargetSearchCommand.ACTION_DELETE %>" />
		 			<input type="image" src="images/action-icon-delete.gif" title="Delete" alt="Delete" />
     		 		</authority:hasUserOwnedPriv>
     		 		</c:if>
					</form>
				</td>
			</tr>
			<tr>			
				<td colspan="7" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="5" border="0" /></td>
			</tr>
			
	        </c:forEach>
			<tr>			
				<td colspan="7" class="tableRowLite" align="center">
				<jsp:include page="pagination.jsp"/>
				</td>
			</tr>
		</table>		
</div>

