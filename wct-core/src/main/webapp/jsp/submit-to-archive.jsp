<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div id="resultsTable">	
	<c:if test="${archiveIID != null}">
	<span class="tableRowLite"><spring:message code="ui.label.targetinstance.submitToArchiveResult"/></span><br/><br/>
	<span class="tableRowLite">Archive upload successful - Archive ID returned:<c:out value="${archiveIID}"/></span><br>    
	</c:if>
	<br/>
	<center><a href="curator/target/target-instance.html?targetInstanceId=<c:out value="${instance.oid}&cmd=edit&init_tab=RESULTS"/>"><img src="images/generic-btn-done.gif" border="0"></a></center>
</div>