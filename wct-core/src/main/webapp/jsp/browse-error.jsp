<jsp:directive.page import="org.apache.commons.logging.Log"/>
<jsp:directive.page import="org.apache.commons.logging.LogFactory"/>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<% 
Log log = LogFactory.getLog("org.webcurator.errorPage");
if (log.isErrorEnabled()) {
	log.error("The error page detected an unexpected error: " + request.getAttribute("exception"), (Exception) request.getAttribute("exception"));
}
%>
<div id="resultsTable">
<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td colspan="4"><span class="midtitleGrey">Error</span></td>
	</tr>
	<tr>
		<td class="tableRowDark">An unexpected error occurred</td>
	</tr>
	<tr>
		<td class="tableRowLite"><c:out value="${exception}"/></td>
	</tr>
</table>
</div>