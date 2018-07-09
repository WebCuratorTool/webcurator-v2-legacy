<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<table width="100%">
  <tr>
    <td width="33%">
      <c:if test="${page.previousPage}"><input type="image" title="Prev" src="images/previous.gif" alt="Prev" border="0" onclick="return setPageNumber(${page.page - 1});"/><img src="images/x.gif" alt="" width="10" height="1" border="0" /></c:if>
	</td>
	<td width="34%" align="center">
	  <p>Results ${page.firstResult} to ${page.lastResult} of ${page.total}
	  <br>
	  Page <select onchange="setPageNumber(this.value);">
	  <c:forEach begin="0" end="${page.numberOfPages}" varStatus="s">
		<option value="<c:out value="${s.index}"/>" <c:if test="${s.index == page.page }">selected</c:if>><c:out value="${s.index + 1}"/></option>
	  </c:forEach>	  
	  </select>
	  of <c:out value="${page.numberOfPages+1}"/><br />
	  Rows per page:&nbsp;<select onchange="setPageSize(this.value);">
								<option value="10" <c:if test="${10 == page.pageSize }">selected</c:if>>10</option>
								<option value="20" <c:if test="${20 == page.pageSize }">selected</c:if>>20</option>
								<option value="50" <c:if test="${50 == page.pageSize }">selected</c:if>>50</option>
								<option value="100" <c:if test="${100 == page.pageSize }">selected</c:if>>100</option>
						  </select>
	  </p>
	</td>
	<td width="33%" align="right">
	  <c:if test="${page.nextPage}"><input type="image" title="Next" src="images/next.gif" alt="Next" border="0" onclick="return setPageNumber(${page.page + 1});"/><img src="images/x.gif" alt="" width="10" height="1" border="0" /></c:if>				
	</td>
  </tr>
</table>