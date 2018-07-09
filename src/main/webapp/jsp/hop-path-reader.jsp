<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.ui.target.command.ShowHopPathCommand" %>
<style>
	.hidden { display:none; }
	.logView {font-family: courier new;	font-size: 8pt; width: 100%; }
</style>
<c:if test="${messageText != ''}">
<font color="red"><c:out value="${messageText}"/></font>
</c:if>
<form name="hopPathReader" method="POST" action="<%=Constants.CNTRL_HOP_PATH_READER%>">
<h1>Hop Path viewer</h1>
<table border="0" width="100%" align="center">
	<c:forEach items="${lines}" var="line">
	<tr>
		<td class="logView">${line}</td>
	</tr>
	</c:forEach>
</table>
</form>