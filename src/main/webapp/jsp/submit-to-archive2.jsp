<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div id="resultsTable">	
<span class="tableRowLite">
<c:if test="${!hasErrors}">
<p><spring:message code="ui.label.targetinstance.submitToArchiveStarted"/></p>
</c:if>
<center><a href="curator/target/queue.html"/><img src="images/generic-btn-done.gif" border="0"></a></center>
</span>

<br/>
<br/>
</div>