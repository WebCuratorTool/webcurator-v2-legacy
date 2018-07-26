<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@page import="org.webcurator.domain.model.auth.Privilege"%>
<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>

<span class="midtitleGrey">Hibernate Test</span>
<div id="resultsTable">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td class="tableHead">ID</td>
		<td class="tableHead">Column 1</td>
		<td class="tableHead">Column 2</td>
	</tr>
	<c:forEach items="${hibernateTests}" var="hibernateTest">
		<tr>
		    <td class="tableRowLite"><c:out value="${hibernateTest.id}"/></td>
		    <td class="tableRowLite"><c:out value="${hibernateTest.column1}"/></td>
		    <td class="tableRowLite"><c:out value="${hibernateTest.column2}"/></td>
		</tr>
	</c:forEach>
	</table>
    <form id="addHibernateTestDataForm" name="addHibernateTestDataForm" method="post" action="curator/hibernate-test-add.html">
        <input type="submit" value="Add Data"/>
    </form>
</div>
