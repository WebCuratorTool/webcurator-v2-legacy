<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>

<script type="text/javascript">
<!--
  function setPageNumber(pageNum) {
    document.getElementById('pageNumber').value = pageNum;
    document.getElementById('addParents').submit();
  }

  function setPageSize(pageSize) {
    document.getElementById('selectedPageSize').value = pageSize;
    document.getElementById('addParents').submit();
  }
  
  function removeSelection(parentIndex) {
    document.addParents.parentIndex.value = parentIndex;
    document.addParents.actionCmd.value = "Remove";
  }
//-->
</script>
<form id="addParents" name="addParents" action="curator/targets/add-parents.html" method="post">
<div id="searchBox">
<input type="hidden" name="pageNumber" id="pageNumber" value="${command.pageNumber}"/>
<input type="hidden" name="selectedPageSize" id="selectedPageSize" value="${page.pageSize}"/>
<input type="hidden" name="parentIndex" id="parentIndex" value="0"/>

<img src="images/search-box-top.gif" alt="Search" width="900" height="36" border="0" /><br/>
	<div id="searchBoxContent">
		<table width="100%" cellpadding="0" cellspacing="3" border="0">
		<tr>
			<td class="searchBoxLabel">
				Name:<br />
				<input type="text" name="search" value="<c:out value="${command.search}"/>" style="width:200px;">
			</td>
			<td align="right" valign="bottom"><input type="image" src="images/search-box-btn.gif" alt="Search" width="82" height="24" border="0" /></td>
		</tr>
		</table>
	</div>
	<img src="images/search-box-btm.gif" alt="" width="900" height="12" border="0" /></div>
	<div id="selectedItems">
	<table width="60%" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td colspan>
		<span class="midtitleGrey">Selected</span>
		</td>
	</tr>
	<tr>
	<td align="left">
		<table width="100%" cellpadding="0" cellspacing="3" border="0">
		<c:set var="count" scope="page" value="0"/>
		<c:choose>
		<c:when test="${empty selections}">
		<tr>
			<td class="tableRowLite" colspan="2">
			No Selection
			</td>
		</tr>
		</c:when>
		<c:otherwise>
		<c:set var="col" scope="page" value="0"/>
		<c:forEach items="${selections}" var="item">
			<c:choose>
				<c:when test="${col == 0}">
					<tr>
						<td class="tableRowLite">
						<c:out value="${item.parentName}"/>
						&nbsp;
						<input type="image" src="images/delete.gif" title="Remove" alt="Remove" onclick="removeSelection(<c:out value="${count}"/>);"/>
						</td>
				<c:set var="col" scope="page" value="1"/>
				</c:when>
				<c:otherwise>
						<td class="tableRowLite">
						<c:out value="${item.parentName}"/>
						&nbsp;
						<input type="image" src="images/delete.gif" title="Remove" alt="Remove" onclick="removeSelection(<c:out value="${count}"/>);"/>
						</td>
					</tr>
				<c:set var="col" scope="page" value="0"/>
				</c:otherwise>
			</c:choose>
		<c:set var="count" scope="page" value="${count + 1}"/>
		</c:forEach>
		<c:if test="${col == 1}">
				<td class="tableRowLite">
					&nbsp;
				</td>
			</tr>
		</c:if>
		</c:otherwise>
		</c:choose>
		</table>
	</td>
	</tr>
	</table>
	<input type="hidden" name="selectedCount" id="selectedCount" value="<c:out value="${count}"/>"/>
	</div>
	<br/>
	<div id="resultsTable">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td colspan="5">
				<span class="midtitleGrey">Results</span>
				</td>
			</tr>
			<tr>
				<td class="tableHead">&nbsp;</td>
				<td class="tableHead">Name</td>
				<td class="tableHead">Status</td>
				<td class="tableHead">Agency</td>
			</tr>
			<c:choose>
				  <c:when test="${empty page.list}">
				  <tr>
				    <td class="tableRowLite" colspan="5"><p>No results found</p></td>
				    </tr>
				  </c:when>
				<c:otherwise>
				<c:forEach items="${page.list}" var="target">
				<tr>
			        <td class="tableRowLite"><input type="checkbox" name="parentOids" value="${target.oid}"></td>
			        <td class="tableRowLite"><wct:groupname name="${target.name}" subGroupSeparator="${subGroupSeparator}"/></td>
			        <td class="tableRowLite"><spring:message code="target.state_${target.state}"/></td>        
			        <td class="tableRowLite"><c:out value="${target.agencyName}"/></td>
			        </tr>
				</c:forEach>
                    <tr>
                      <td colspan="5" align="center">
                      <jsp:include page="pagination.jsp"/>

				      </td>
				    </tr>
				</c:otherwise>
				</c:choose>
		</table>
	</div>
	<br />
	<input type="hidden" name="actionCmd" value=""/>
    <input type="image" name="_add" src="images/generic-btn-save.gif" alt="Save" width="82" height="23" border="0" onclick="document.addParents.actionCmd.value='Add'" /><img src="images/x.gif" alt="" width="10" height="1" border="0" />
	<input type="image" name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0" onclick="document.addParents.actionCmd.value='Cancel'"/>    
</form>