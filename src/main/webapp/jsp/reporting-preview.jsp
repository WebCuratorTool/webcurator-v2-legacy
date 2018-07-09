<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.core.report.*" %>
<%@ page import="org.webcurator.ui.report.controller.ReportPreviewController" %>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>

<br>

<%
OperationalReport op = (OperationalReport)session.getAttribute("operationalReport");
out.print(op.getRendering(FileFactory.HTML_FORMAT));
%>
<br>
<div class="noPrint">

<form name="reportForm" action="curator/report/report-preview.html" method="post">
<input type="hidden" name="actionCmd" value="" />
<input type="image" src="images/reports-btn-print.gif" onclick="javascript:document.reportForm.actionCmd.value='<%=ReportPreviewController.ACTION_PRINT%>';window.print()">
<input type="image" src="images/reports-btn-save.gif"  onclick="javascript:document.reportForm.actionCmd.value='<%=ReportPreviewController.ACTION_SAVE%>'"/>
<input type="image" src="images/reports-btn-email.gif" onclick="javascript:document.reportForm.actionCmd.value='<%=ReportPreviewController.ACTION_EMAIL%>'"/>
<a href="curator/report/report.html"><img border="0" src="images/reports-btn-cancel.gif"></a>
</form>

</div>
<br>