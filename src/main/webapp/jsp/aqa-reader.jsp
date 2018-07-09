<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.ui.target.command.LogReaderCommand" %>

<style>
	.hidden { display:none; }
	.logView {font-family: courier new;	font-size: 9pt; width: 100%; }
	.row0 { background-color:#ffffff; }
	.row1 { background-color:#eeeeee; }
</style>

<c:if test="${messageText != ''}">
<font color="red"><c:out value="${messageText}"/></font>
</c:if>
<form name="aqaReader" method="POST" action="<%=Constants.CNTRL_AQA_READER%>">
<h1>Automated Quality Assurance Results for <c:out value="${command.targetName}"/>(<c:out value="${command.targetInstanceOid}"/>)</h1>
<h2>Missing Content</h2>
<table border="0" width="90%" align="center">
	<tr>
		<th width="80%" class="tableHead">Url</th>
		<th width="10%" class="tableHead">Live Content</th>
		<th width="10%" class="tableHead">AQA Stored Content</th>
	</tr>

	<c:choose>
		<c:when test="${fn:length(missingElements) == 0}">
			<tr>
				<td colspan="2" class="tableRowLite">There are no AQA URLs available.</td>
			</tr>
		</c:when>
		<c:otherwise>
			<c:forEach items="${missingElements}" var="missingElement" varStatus="lineInfo">
			<tr class="row<c:out value="${lineInfo.index % 2}"/>">
				<td style="word-break: break-all;" valign="top">
					<c:out value="${missingElement.url}"/>
				</td>
				<td align="center" valign="top">
					<a href="<c:out value="${missingElement.url}"/>" target="_blank">View</a> | 
					<a href="curator/target/live-content-retriever.html?url=<c:out value="${missingElement.url}"/>&contentFileName=<c:out value="${missingElement.contentFile}"/>" target="_blank">Download</a>
				</td>
				<td align="center" valign="top">
					<a href="curator/target/content-viewer.html?targetInstanceOid=<c:out value="${command.targetInstanceOid}"/>&logFileName=<c:out value="${missingElement.contentFile}"/>" target="_blank">View</a> | 
					<a href="curator/target/log-retriever.html?targetInstanceOid=<c:out value="${command.targetInstanceOid}"/>&logFileName=<c:out value="${missingElement.contentFile}"/>" target="_blank">Download</a>
				</td>
			</tr>
			</c:forEach>
		</c:otherwise>
	</c:choose>
</table>
</form>