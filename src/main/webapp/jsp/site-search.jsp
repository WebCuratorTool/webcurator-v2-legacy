<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct"%>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"%>
<%@page import="org.webcurator.ui.site.command.SiteSearchCommand"%>
<%@page import="org.webcurator.domain.model.auth.Privilege"%>

<script>
  function setPageNumber(pageNumber) {
	document.getElementById("<%=SiteSearchCommand.PARAM_PAGENO%>").value=pageNumber; 
	document.getElementById("<%=SiteSearchCommand.PARAM_ACTION%>").value='Prev';
	document.getElementById("siteSearch").submit();
  }

  function setPageSize(pageSize) {
	document.getElementById("<%=SiteSearchCommand.PARAM_PAGESIZE%>").value=pageSize; 
	document.getElementById("siteSearch").submit();
  }
</script>
<%!
private boolean useComma = false; 
String buildStatusString(int count, String status)
{
	String output = ""; 
    if(count > 0)
    {
		if(useComma)
		{ 
		 output = ", "; 
		}
		output = output+status;
		if(count > 1) 
		{
		 output = output+"("+count+")";
		}
		useComma = true;
    }
	return output;
}

String getStatus(int pending, int requested, int approved, int denied)
{
	String output = "";
	 
	useComma = false; 
	output = output + buildStatusString(pending, "Pending");
	output = output + buildStatusString(requested, "Requested");
	output = output + buildStatusString(approved, "Approved");
	output = output + buildStatusString(denied, "Rejected");
    
	return output;
}
 %>


<form id="siteSearch" name="siteSearch" action="curator/site/search.html" method="post">

<div id="searchBox">
<img src="images/search-box-top.gif" alt="Search" width="900" height="36" border="0" /><br/>
	<div id="searchBoxContent">
		<table cellpadding="0" cellspacing="3" border="0">
		<tr>
			<td class="searchBoxLabel" valign="top">
				ID:<br/>
				<input type="text" name="<%=SiteSearchCommand.PARAM_SEARCH_OID%>" value="<c:out value="${command.searchOid}"/>" style="width:50px;" />
			</td>			
			<td class="searchBoxLabel" valign="top">
				Name:<br/>
				<input type="text" name="<%=SiteSearchCommand.PARAM_TITLE%>" value="<c:out value="${command.title}"/>" style="width:120px;" />
			</td>
			<td class="searchBoxLabel" valign="top">
				Authorising Agent:<br/>
				<input type="text" name="<%=SiteSearchCommand.PARAM_AGENT%>" value="<c:out value="${command.agentName}"/>" style="width:120px;" />
			</td>
		    <td class="searchBoxLabel" valign="top">
				Order No:<br/>
				<input type="text" name="<%=SiteSearchCommand.PARAM_ORDERNO%>" value="<c:out value="${command.orderNo}"/>" style="width:80px;" />				
		    </td>
			<td class="searchBoxLabel">
				Agency:<br />
				<select name="agency" id="agency">
      				<option value="" ${command.agency eq '' ? 'SELECTED' : ''}></option>
					<c:forEach items="${agencies}" var="a">
						<option value="<c:out value="${a.name}"/>" ${command.agency eq a.name ? 'SELECTED' : ''}><c:out value="${a.name}"/></option>
					</c:forEach>
    			</select>
			</td>
			<td class="searchBoxLabel">
				Sort Order:<br />
				<select name="sortorder" id="sortorder">
					<option value="<%= SiteSearchCommand.SORT_NAME_ASC %>" ${command.sortorder eq 'nameasc' ? 'SELECTED' : ''}>Name (ascending)</option>
					<option value="<%= SiteSearchCommand.SORT_NAME_DESC %>" ${command.sortorder eq 'namedesc' ? 'SELECTED' : ''}>Name (descending)</option>
					<option value="<%= SiteSearchCommand.SORT_DATE_DESC %>" ${command.sortorder eq 'datedesc' ? 'SELECTED' : ''}>Most Recent First</option>
					<option value="<%= SiteSearchCommand.SORT_DATE_ASC %>" ${command.sortorder eq 'dateasc' ? 'SELECTED' : ''}>Oldest First</option>
				</select>
			</td>
			<td class="searchBoxLabel" valign="top">
				Show Disabled:<br/>
				<input type="checkbox" name="<%=SiteSearchCommand.PARAM_SHOW_DISABLED%>" ${command.showDisabled ? 'checked' : ''} >						  
			</td>
		</tr>
		</table>
		<table cellpadding="0" cellspacing="3" border="0" width="100%">
		<tr>
			<td class="searchBoxLabel" valign="top">
				URL Pattern:<br/>
				<input type="text" name="<%=SiteSearchCommand.PARAM_URL_PATTERN%>" value="<c:out value="${command.urlPattern}"/>" style="width:120px;" /><br/><br/>				
			</td>
			<td class="searchBoxLabel" valign="top">
				Permissions File Reference:<br/>
				<input type="text" name="<%=SiteSearchCommand.PARAM_PERMS_FILE_REF%>" value="<c:out value="${command.permsFileRef}"/>" style="width:120px;" /><br/><br/>				
			</td>
			<td class="searchBoxLabel" valign="top">
			    Permissions Status:<br/>
			    <% int maxStates = 3; %>
				<table cellpadding="0" cellspacing="0" border="0">
				    <% for(int i=0; i<= maxStates;) { %>
				    <tr>
					    <% for(int c=0; c<2; c++,i++) { 
					       if(i<=maxStates) {
					         pageContext.setAttribute("stateId", new Integer(i));
					       
					    %>
				          <td class="searchBoxLabel" valign="top">
						      <input type="checkbox" id="states_<c:out value="${stateId}"/>" name="states" value="<c:out value="${stateId}"/>" ${wct:contains(command.states, stateId) ? 'checked' : ''}><label for="states_<c:out value="${stateId}"/>"><spring:message code="permission.state_${stateId}"/></label>
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
		    <td class="searchBoxLabel" valign="bottom" align="right" colspan="2">
			  <input type="image" src="images/search-box-btn.gif" alt="search" width="82" height="24" border="0" />&nbsp;<input type="image" src="images/search-box-reset-btn.gif" alt="reset" width="82" height="24" border="0" onclick="javascript:document.siteSearch.<%= SiteSearchCommand.PARAM_ACTION %>.value='<%= SiteSearchCommand.ACTION_RESET %>'" />		  
		    </td>
		</tr>
		</table>
	</div>
	<img src="images/search-box-btm.gif" alt="" width="900" height="12" border="0" /></div>
<div id="resultsTable">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td colspan="5">
			<authority:hasPrivilege privilege="<%=Privilege.CREATE_SITE%>" scope="<%=Privilege.SCOPE_AGENCY%>">
			<a href="curator/site/site.html?editMode=true"><img src="images/create-new-btn-red.gif" alt="Create a new Site" width="82" height="24" border="0" align="right" vspace="3" /></a>			
			</authority:hasPrivilege>
			<span class="midtitleGrey">Results</span>
			</td>
		</tr>
		<tr>
			<td class="tableHead">Id</td>
			<td class="tableHead">Created</td>
			<td class="tableHead">Name</td>
			<td class="tableHead">Auth Agent</td>
			<td class="tableHead">Order No</td>
			<td class="tableHead">Status</td>
			<td class="tableHead">Action</td>
		</tr>
		<tr>
			  <c:choose>
			    <c:when test="${page == null || empty page.list}">
			    <td class="tableRowLite" colspan="5">
			    <p>No results found</p>
			    </td>
			    </c:when>
			    <c:otherwise>
			      <c:forEach items="${page.list}" var="result">
			         <tr>
			          <td class="tableRowLite"><c:out value="${result.oid}"/></td>
					  <td class="tableRowLite">
					  	<wct:date value="${result.creationDate}" type="shortDate"/>
					  </td>
			          
			          <td class="tableRowLite">
			        	<c:choose>
			              <c:when test="${!result.active}">
			              	<span class="deletedGroupMember"><c:out value="${result.title}"/></span>
			              </c:when>
			              <c:otherwise>
			                <c:out value="${result.title}"/>
			              </c:otherwise>
			            </c:choose>
			          </td>
			          <td class="tableRowLite">
			          	<c:forEach items="${result.authorisingAgents}" var="aa">
			          		<c:out value="${aa.name}"/><br/>
			          	</c:forEach>
					  </td>			
					  <td class="tableRowLite"><c:out value="${result.libraryOrderNo}"/></td>
					  <td class="tableRowLite">
			          <% int pending = 0;
				         int requested = 0;
				         int approved = 0;
				         int denied = 0;%>
			              <c:forEach items="${result.permissions}" var="p">
				              <c:choose>
					              <c:when test="${p.status == 0}">
							         <%pending++;%>
					              </c:when>
					              <c:when test="${p.status == 1}">
							         <%requested++;%>
					              </c:when>
					              <c:when test="${p.status == 2}">
							         <%approved++;%>
					              </c:when>
					              <c:when test="${p.status == 3}">
					    		     <%denied++;%>
					              </c:when>
				              </c:choose>
			              </c:forEach>
			              <%=getStatus(pending, requested, approved, denied) %>
					  </td>          			          
			          <td class="tableRowLite">
			          <a href="curator/site/site.html?siteOid=<c:out value="${result.oid}"/>"><img src="images/action-icon-view.gif" title="View" alt="click here to VIEW this item" border="0" /></a>
			          <c:choose>
			          <c:when test="${result.owningAgency != null}">
						  <authority:showControl ownedObject="${result}" privileges="<%= Privilege.MODIFY_SITE %>" editMode="true">
						      <authority:show>
						          <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
						 		  <a href="curator/site/site.html?siteOid=<c:out value="${result.oid}"/>&editMode=true"><img src="images/action-icon-edit.gif" title="Edit" alt="click here to EDIT this item" width="18" height="18" border="0" /></a>
					 		  </authority:show>
				 		  </authority:showControl>
			 		  </c:when>	
			 		  <c:otherwise>
				          <authority:hasPrivilege privilege="<%=Privilege.MODIFY_SITE%>" scope="<%=Privilege.SCOPE_ALL%>">
					          <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					 		  <a href="curator/site/site.html?siteOid=<c:out value="${result.oid}"/>&editMode=true"><img src="images/action-icon-edit.gif" title="Edit" alt="click here to EDIT this item" width="18" height="18" border="0" /></a>
						  </authority:hasPrivilege>	  
			 		  </c:otherwise>
			 		  </c:choose>  
			          <authority:hasPrivilege privilege="<%=Privilege.CREATE_SITE%>" scope="<%=Privilege.SCOPE_AGENCY%>">
			 		  <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
			 		  <a href="curator/site/site.html?siteOid=<c:out value="${result.oid}"/>&copyMode=true"><img src="images/action-icon-copy.gif" title="Copy" alt="click here to COPY this item" border="0" /></a>
			 		  </authority:hasPrivilege>		  
			 		  <authority:hasPrivilege privilege="<%=Privilege.GENERATE_TEMPLATE%>" scope="<%=Privilege.SCOPE_AGENCY%>">
			 		  <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
			 		  <a href="curator/site/generate.html?siteOid=<c:out value="${result.oid}"/>"><img src="images/template-icon.gif" title="Generate letter/email" alt="click here to Generate letter/email" border="0" /></a>			 		  
			 		  </authority:hasPrivilege>
			 		  </td>
			        </tr>
			        <tr>			
						<td colspan="5" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="5" border="0" /></td>
					</tr>    
			      </c:forEach>
			    </c:otherwise>
			  </c:choose>
		</tr>
		
		<tr>
			<td colspan="5" align="right">&nbsp;</td>
		<tr>
			<td colspan="5" align="center">
			<input type="hidden" id="<%=SiteSearchCommand.PARAM_PAGENO%>" name="<%=SiteSearchCommand.PARAM_PAGENO%>" value="0" />			
			<input type="hidden" id="<%=SiteSearchCommand.PARAM_PAGESIZE%>" name="<%=SiteSearchCommand.PARAM_PAGESIZE%>" value="${page.pageSize}" />			
			<input type="hidden" id="<%=SiteSearchCommand.PARAM_ACTION%>" name="<%=SiteSearchCommand.PARAM_ACTION%>" value="" />			
			<jsp:include page="pagination.jsp"/>
			</td>
		</tr>
	</table>
</div>
</form>
