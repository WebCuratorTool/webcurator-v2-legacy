<%@page import="org.webcurator.ui.target.command.SeedsCommand" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<script type="text/javascript">
<!--
  function setActionCmd(val) {
  	document.getElementById('actionCmd').value = val;
  }
  
  function setPageNumber(pageNum) {
    document.getElementById('pageNumber').value = pageNum;
    setActionCmd('<%= SeedsCommand.ACTION_LINK_NEW_SEARCH %>');
  }
  
  function preview(permissionId) {
    var url = '<%= basePath %>curator/target/permission-popup.html?permissionOid=' + permissionId;
    var winObj = window.open(url, 'permissionPreview', 'menubar=no,scrollbars=yes,status=no,toolbar=no,resizable=yes,width=500,height=400', true);
    winObj.focus();
  }
  
// -->
</script>


<input type="hidden" id="actionCmd" name="actionCmd">
<input type="hidden" id="pageNumber" name="pageNumber" value="${command.pageNumber}">
<input type="hidden" name="selectedSeed" value="${command.selectedSeed}"/>

<div id="searchBox">
	<img src="images/search-box-top.gif" alt="Search" width="900" height="36" border="0" /><br/>
	<div id="searchBoxContent">
		<table cellpadding="0" cellspacing="3" border="0" width="100%">
		<tr>
			<td class="searchBoxLabel">
				<input type="radio" name="searchType" id="searchType_site" value="site" ${command.searchType == 'site' ? 'checked' : ''}><label for="searchType_site"><spring:message code="ui.label.target.seeds.link.harvestAuthorisation"/>:</label>
			</td>
			<td class="searchBoxLabel">
				<input type="text" name="siteSearchCriteria" value="<c:out value="${command.siteSearchCriteria}"/>">
			</td>
			<td class="searchBoxLabel">
				<input type="radio" name="searchType" id="searchType_url" value="url" ${command.searchType == 'url' ? 'checked' : ''}><label for="searchType_url">URL:</label>
			</td>
			<td class="searchBoxLabel">
				<input type="text" name="urlSearchCriteria" value="<c:out value="${command.urlSearchCriteria}"/>">
			</td>
			<td align="left" valign="bottom"><input type="image" src="images/search-box-btn.gif" alt="search" width="82" height="24" border="0" onclick="setActionCmd('<%= SeedsCommand.ACTION_LINK_NEW_SEARCH %>');"/></td>
		</tr>
		</table>
	</div>
	<input type="image" src="images/search-box-btm.gif" alt="" width="900" height="12" border="0" />
</div>	
<c:choose>
  <c:when test="${!empty results.list}">
<div id="annotationsBox">
<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
<table width="100%" cellpadding="3" cellspacing="0" border="0">
		<tr>
			<td class="annotationsHeaderRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
		    <td class="annotationsHeaderRow"><spring:message code="ui.label.target.seeds.link.harvestAuthorisation"/></td>
		    <td class="annotationsHeaderRow">Authorising Agent</td>
		    <td class="annotationsHeaderRow">URL Patterns</td>
		    <td class="annotationsHeaderRow">Start</td>
		    <td class="annotationsHeaderRow">End</td>
		    <td class="annotationsHeaderRow">Status</td>
		    <td class="annotationsHeaderRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
	   </tr>
  <c:forEach items="${results.list}" var="permission">
  <tr>
    <td class="annotationsLiteRow"><input type="checkbox" name="linkPermIdentity" value="${permission.identity}"></td>
    <td class="annotationsLiteRow"><c:out value="${permission.site.title}"/></td>
    <td class="annotationsLiteRow"><c:out value="${permission.authorisingAgent.name}"/></td>
    <td class="annotationsLiteRow">
      <c:forEach items="${permission.urls}" var="url">
      	<c:out value="${url.pattern}"/><br/>
      </c:forEach>
    </td>
    <td class="annotationsLiteRow"><wct:date value="${permission.startDate}" type="fullDate"/></td>
    <td class="annotationsLiteRow"><wct:date value="${permission.endDate}" type="fullDate"/></td>
    <td class="annotationsLiteRow"><spring:message code="permission.state_${permission.currentStatus}"/></td>
    <td class="annotationsLiteRow">
   	      <c:choose>
   	        <c:when test="${permission.restricted && empty permission.exclusions}">
   	          <img src="images/warn.gif" height="20" width="22" title="Permission has Special Restrictions" alt="Warning Special Restrictions">
   	        </c:when>
   	        <c:when test="${!permission.restricted && !empty permission.exclusions}">
              <img src="images/warn.gif" height="20" width="22" title="Permission has Exclusions" alt="Warning Special Restrictions">
   	        </c:when>
			<c:when test="${permission.restricted && !empty permission.exclusions}">
              <img src="images/warn.gif" height="20" width="22" title="Permission has Special Restrictions and Exclusions" alt="Warning Special Restrictions">
   	        </c:when>   
   	        <c:otherwise>
   	          <img src="images/x.gif" height="20" width="22">
   	        </c:otherwise>	        
		  </c:choose>
      <input type="image" src="images/action-icon-view.gif" height="19" width="15" onclick="preview(${permission.oid}); return false;">      
    </td>
  </tr>
  </c:forEach>  
</table>
</div>
<br/>
<br/>
<c:if test="${results.previousPage}"><input type="image" title="Prev" src="images/previous.gif" alt="Prev" border="0" onclick="setPageNumber(${command.pageNumber - 1});"/><img src="images/x.gif" alt="" width="10" height="1" border="0" /></c:if>
<c:if test="${results.nextPage}"><input type="image" title="Next" src="images/next.gif" alt="Next" border="0" onclick="setPageNumber(${command.pageNumber + 1});"/><img src="images/x.gif" alt="" width="10" height="1" border="0" /></c:if>
</c:when>
<c:otherwise>
  <p>No results found.</p>
</c:otherwise>
</c:choose>
<input type="image" src="images/generic-btn-done.gif" alt="Link" title="Link" width="82" height="23" border="0" onclick="setActionCmd('<%= SeedsCommand.ACTION_LINK_NEW_CONFIRM %>');"/><img src="images/x.gif" alt="" width="10" height="1" border="0" />
<input type="image" src="images/generic-btn-cancel.gif" alt="Cancel" title="Cancel" width="82" height="23" border="0" onclick="setActionCmd('<%= SeedsCommand.ACTION_LINK_NEW_CANCEL %>');"/>