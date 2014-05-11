<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.core.report.*" %>
<%@ page import="org.webcurator.ui.report.controller.ReportEmailController" %>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<br>
<div id="resultsTable">
<form id="reportSaveForm" action="curator/report/report-email.html" method="post">
<table border="0">
	<tr>
		<td class="tableRowLite">Report Format:</td>
		<td class="tableRowLite">
			<c:set var="first" value="true" />
			<select name="format">
				<c:forEach items="${formats}" var="format">
					<c:choose>
						<c:when test="${first == 'true'}">
							<option SELECTED><c:out value="${format}"/>
							<c:set var="first" value="false" />
						</c:when>
						<c:otherwise>
							<option><c:out value="${format}"/>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
	</tr>

	<tr>
		<TD class="tableRowLite">E-mail Recipient:</TD>
		<TD class="tableRowLite"><INPUT type="text" name="recipient" /></TD>
	</tr>
	<TR>
		<TD class="tableRowLite">Subject:</TD>
		<TD class="tableRowLite"><INPUT type="text" name="subject" value="<c:out value="${subject}" />" /></TD>
	</TR><TR>
	
	<TD class="tableRowLite">Message:</TD>
	<TD class="tableRowLite"><TEXTAREA name="message" rows="5"></TEXTAREA></TD></TR>
	
</table>
<p align="center">
	<input type="hidden" id="actionCmd" name="actionCmd" value="" />
	<input type="image" src="images/reports-btn-send.gif" onclick="document.getElementById('actionCmd').value='<%=ReportEmailController.ACTION_EMAIL%>'" >
	<input type="image" src="images/reports-btn-cancel.gif" onclick="document.getElementById('actionCmd').actionCmd.value='<%=ReportEmailController.ACTION_CANCEL%>'" />
</p>
</form>
</div>
<br>