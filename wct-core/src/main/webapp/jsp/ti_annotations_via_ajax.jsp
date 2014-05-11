<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="org.webcurator.ui.target.command.TargetInstanceCommand" %>
<%@page import="org.webcurator.domain.model.core.TargetInstance" %>
<%@page import="org.webcurator.ui.common.Constants" %>
<%@page import="org.webcurator.domain.model.auth.Privilege" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:if test="${instances gt 0}">
	<table cellspacing="0" cellpadding="0" width="100%">
	
	<c:set var="count" scope="page" value="0"/>
	<c:forEach items="${targetInstances.list}" var="instance">
	<c:set var="count" scope="page" value="${count + 1}"/>
	<c:choose>
		<c:when test="${count eq 1}">
			<tr class="titooltip_header"><td>Date&nbsp;</td><td>URIs&nbsp;</td><td>Data&nbsp;</td><td>Job Status&nbsp;</td><td>Status&nbsp;</td></tr>	
			<tr class="titooltip_statuscurrent"><td><wct:date value="${instance.sortOrderDate}" type="fullDateTime"/>&nbsp;</td><td><c:out value="${instance.status.urlsSucceeded}" />&nbsp;</td><td><c:out value="${instance.status.dataDownloadedString}" />&nbsp;</td><td><c:out value="${instance.status.status}" />&nbsp;</td><td><c:out value="${instance.state}"/></td></tr>
			<c:forEach items="${instance.sortedAnnotations}" var="annotation">
				<tr><td colspan="5"><span class="titooltip_annotationdate"><wct:date value="${annotation.date}" type="fullDateTime"/>:&nbsp;</span>
				<span class="titooltip_annotationtext"><c:out value="${annotation.note}" /></span></td></tr>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<tr style="font-weight: bold; background-color: rgb(192, 192, 192); color: rgb(255, 255, 128)"><td><wct:date value="${instance.sortOrderDate}" type="fullDateTime"/>&nbsp;</td><td><c:out value="${instance.status.urlsSucceeded}" />&nbsp;</td><td><c:out value="${instance.status.dataDownloadedString}" />&nbsp;</td><td><c:out value="${instance.status.status}" />&nbsp;</td><td><c:out value="${instance.state}"/></td></tr>
			<c:forEach items="${instance.sortedAnnotations}" var="annotation">
				<tr><td colspan="5"><span class="titooltip_annotationdate"><wct:date value="${annotation.date}" type="fullDateTime"/>:&nbsp;</span>
				<span class="titooltip_annotationtext"><c:out value="${annotation.note}" /></span></td></tr>
			</c:forEach>
		</c:otherwise>
	</c:choose>
	</c:forEach>
	
	</table>
</c:if>