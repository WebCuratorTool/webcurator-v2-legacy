<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="org.webcurator.ui.site.command.SiteSearchCommand"%>
<%@page import="org.webcurator.domain.model.auth.Privilege" %>
<%@page import="org.webcurator.ui.site.command.TransferSeedsCommand" %>


<script language="JavaScript">
  function setAction(actionCmd) {
	document.getElementById('actionCmd').value = actionCmd;
  }
</script>
<form id="siteSearchForm" name="siteSearch" action="curator/site/transfer.html" method="post">
<input type="hidden" name="actionCmd" id="actionCmd" value="<%= TransferSeedsCommand.ACTION_SEARCH %>">
<p class="subBoxTextHdr">Transfer ${seedCount} seed${seedCount > 1 ? 's' : ''} to:</p>
<input type="hidden" name="<%= TransferSeedsCommand.PARAM_FROM_PERMISSION_OID %>" value="<c:out value="${command.fromPermissionOid}"/>"/>
<div id="searchBox">
<img src="images/search-box-top.gif" alt="Search for permissions to transfer to" width="900" height="36" border="0" /><br/>
	<div id="searchBoxContent">
		<table width="100%" cellpadding="0" cellspacing="3" border="0">
		<tr>
			<td class="searchBoxLabel">
				Site Title:<br/>
				<input type="text" name="<%= TransferSeedsCommand.PARAM_SITE_TITLE %>" value="<c:out value="${command.siteTitle}"/>" style="width:200px;" /><br/><br/>
			</td>
			<td class="searchBoxLabel">
				URL Pattern:<br/>
				<input type="text" name="<%= TransferSeedsCommand.PARAM_URL_PATTERN %>" value="<c:out value="${command.urlPattern}"/>" style="width:200px;" /><br/><br/>
			</td>
			<td align="right" valign="bottom"><input type="image" src="images/search-box-btn.gif" alt="search" width="82" height="24" border="0" /></td>
		</tr>
		</table>
	</div>
	<img src="images/search-box-btm.gif" alt="" width="900" height="12" border="0" /></div>
<div id="resultsTable">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
		<tr>
		    <td class="tableHead">&nbsp;</td>
			<td class="tableHead">Id</td>
			<td class="tableHead">Agency</td>
			<td class="tableHead">Site</td>
			<td class="tableHead">URL Patterns</td>
			<td class="tableHead">Start</td>
			<td class="tableHead">End</td>
			<td class="tableHead">Status</td>
			<td class="tableHead">&nbsp;</td>
		</tr>
		<tr>
			  <c:choose>
			    <c:when test="${page == null || empty page.list}">
			    <td class="tableRowLite" colspan="9">
			    <p>No results found</p>
			    </td>
			    </c:when>
			    <c:otherwise>
			      <c:forEach items="${page.list}" var="result">
			         <tr>
			          <td class="tableRowLite"><input type="radio" name="<%= TransferSeedsCommand.PARAM_TO_PERMISSION_OID %>" value="<c:out value="${result.oid}"/>"/></td>
			          <td class="tableRowLite"><c:out value="${result.oid}"/></td>
			          <td class="tableRowLite"><c:out value="${result.owningAgency.name}"/></td>			          
			          <td class="tableRowLite"><c:out value="${result.site.title}"/></td>
			          <td class="tableRowLite">
			            <c:forEach items="${result.urls}" var="pattern">
			              <c:out value="${pattern.pattern}"/><br/>
			            </c:forEach>
			          </td>
				      <td class="tableRowLite"><wct:date value="${result.startDate}" type="fullDate"/></td>
				      <td class="tableRowLite"><wct:date value="${result.endDate}" type="fullDate"/></td>
				      <td class="tableRowLite"><spring:message code="permission.state_${result.status}"/></td>	
				      <td class="tableRowLite">&nbsp;</td>			      
			        </tr>
			        <tr>			
						<td colspan="9" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="5" border="0" /></td>
					</tr>    
			      </c:forEach>
			    </c:otherwise>
			  </c:choose>
		</tr>
		
		<tr>
			<td colspan="9" align="right">&nbsp;</td>
		<tr>
			<td colspan="9">
			&nbsp;
			<c:if test="${page.previousPage}">			
			<input type="image" src="images/previous.gif" onclick="setAction('<%= TransferSeedsCommand.ACTION_PREV %>');"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</c:if>	
			<p align="center">
			<input type="image" src="images/generic-btn-transfer.gif" onclick="setAction('<%= TransferSeedsCommand.ACTION_TRANSFER %>');"/>
			<input type="image" src="images/generic-btn-cancel.gif" onclick="setAction('<%= TransferSeedsCommand.ACTION_CANCEL %>');"/>
			</p>			
			<c:if test="${page.nextPage}">			
			<input type="image" src="images/next.gif" onclick="setAction('<%= TransferSeedsCommand.ACTION_NEXT %>');"/>
			</c:if>
			</td>
		</tr>
	</table>
</div>
</form>
