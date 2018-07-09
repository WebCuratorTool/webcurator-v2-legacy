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
<table cellspacing="0" cellpadding="0" width="100%">

<c:set var="count" scope="page" value="0"/>
<c:forEach items="${target.sortedAnnotations}" var="annotation">
<c:set var="count" scope="page" value="${count + 1}"/>
<c:choose>
	<c:when test="${count eq 1}">
		<tr class="ttooltip_header"><td>Date&nbsp;</td></tr>	
		<tr><td><span class="ttooltip_annotationdate"><wct:date value="${annotation.date}" type="fullDateTime"/>:&nbsp;</span>
		<span class="ttooltip_annotationtext"><c:out value="${annotation.note}" /></span>
		</td></tr>
	</c:when>
	<c:otherwise>
		<tr><td><span class="ttooltip_annotationdate"><wct:date value="${annotation.date}" type="fullDateTime"/>:&nbsp;</span>
		<span class="ttooltip_annotationtext"><c:out value="${annotation.note}" /></span>
		</td></tr>
	</c:otherwise>
</c:choose>
</c:forEach>
<c:choose>
	<c:when test="${count eq 0}">
		<tr><td>No annotations for target id <c:out value="${target.oid}" /></td></tr>		
	</c:when>
</c:choose>

</table>
