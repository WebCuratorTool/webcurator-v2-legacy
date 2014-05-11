<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="org.webcurator.ui.site.command.AgencySearchCommand"%>

<script type="text/javascript">
<!--
  function setActionCmd(str) {
    document.getElementById('<%= AgencySearchCommand.PARAM_ACTION_CMD %>').value=str;
  }
  
  function setPageNumber(pageNum) {
    document.getElementById('pageNumber').value = pageNum;
  }
    
//-->
</script>

<form name="addAgency" action="curator/site/site-auth-agency-search.html" method="post">
<input type="hidden" name="pageNumber" id="pageNumber" value="${command.pageNumber}"/>

<div id="searchBox">
<img src="images/search-box-top.gif" alt="Search" width="900" height="36" border="0" /><br/>
	<div id="searchBoxContent">
		<table width="100%" cellpadding="0" cellspacing="3" border="0">
		<tr>
			<td class="searchBoxLabel">
				Name:<br />
				<input type="text" name="name" value="<c:out value="${command.name}"/>" style="width:200px;">
			</td>
			<td align="right" valign="bottom"><input type="image" src="images/search-box-btn.gif" alt="Search" width="82" height="24" border="0" /></td>
		</tr>
		</table>
	</div>
	<img src="images/search-box-btm.gif" alt="" width="900" height="12" border="0" /></div>

	<div id="resultsTable">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td colspan="3">
				<span class="midtitleGrey">Results</span>
				</td>
			</tr>
			<tr>
				<td class="tableHead" width="10px">&nbsp;</td>
				<td class="tableHead">Name</td>
				<td class="tableHead">Contact</td>
			</tr>
			<c:choose>
				  <c:when test="${empty results.list}">
				  <tr>
				    <td class="tableRowLite" colspan="5"><p>No results found</p></td>
				    </tr>
				  </c:when>
				<c:otherwise>
				<c:forEach items="${results.list}" var="agency">
				<tr>
			        <td class="tableRowLite" width="10px"><input type="checkbox" name="<%= AgencySearchCommand.PARAM_SELECTED_OIDS %>" value="${agency.oid}"></td>
			        <td class="tableRowLite"><c:out value="${agency.name}"/></td>
			        <td class="tableRowLite"><c:out value="${agency.contact}"/></td>			        
			        </tr>
				</c:forEach>
                    <tr>
                      <td colspan="5" align="center">
                        <c:if test="${results.previousPage}">
                          <input type="image" title="Previous" src="images/previous.gif" alt="Prev" border="0" onclick="setPageNumber(${command.pageNumber - 1});"/><img src="images/x.gif" alt="" width="10" height="1" border="0" />	
                        </c:if>
						<c:if test="${results.nextPage}">
						  <input type="image" title="Next" src="images/next.gif" alt="Next" border="0" onclick="setPageNumber(${command.pageNumber + 1});"/><img src="images/x.gif" alt="" width="10" height="1" border="0" />
						</c:if>				
				      </td>
				    </tr>				
				</c:otherwise>
			</c:choose>
			</tr>
		</table>
	</div>
	<br />
	<input type="hidden" id="actionCmd" name="actionCmd" value=""/>
    <input type="image" name="_add" src="images/generic-btn-save.gif" alt="Save" width="82" height="23" border="0" onclick="setActionCmd('<%= AgencySearchCommand.ACTION_ADD %>');" /><img src="images/x.gif" alt="" width="10" height="1" border="0" />
	<input type="image" name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0" onclick="setActionCmd('<%= AgencySearchCommand.ACTION_CANCEL %>');"/>    
</div>
</form>