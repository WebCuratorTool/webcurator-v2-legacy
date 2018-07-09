<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="org.webcurator.core.report.*" %>
<%@page import="org.webcurator.ui.report.controller.ReportSaveController" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<%
//OperationalReport op = (OperationalReport)session.getAttribute("operationalReport");
OperationalReport op = (OperationalReport)request.getAttribute("operationalReport");
request.setAttribute("operationalReport",op);
%>
<div id="resultsTable">
<form name="reportSaveForm" action="curator/report/report-save.html" method="post">
<table border="0">
	<tr>
		<td class="tableRowLite"><spring:message code="ui.label.reporting.preview.reportFormat"/>:</td>
		<td class="tableRowLite">
			<c:set var="first" value="true"/>
			<select name="format">
				<c:forEach items="${formats}" var="format">
					<c:choose>
						<c:when test="${first == true}">
							<option SELECTED><c:out value="${format}"/>
						</c:when>
						<c:otherwise>
							<option><c:out value="${format}"/>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
	</tr>
</table>
<p align="center">
	<input type="hidden" id="actionCmd" name="actionCmd" value="" />
	<input type="image" src="images/reports-btn-save.gif" onclick="document.getElementById('actionCmd').value='<%=ReportSaveController.ACTION_SAVE%>'" />
	<input type="image" src="images/reports-btn-cancel.gif" onclick="document.getElementById('actionCmd').value='<%=ReportSaveController.ACTION_CANCEL%>'" />
</p>
</form>
</div>