<%@page contentType="text/html; charset=UTF-8" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div id="footer">
	<div id="footerright"><img src="images/footer-right.gif" alt="" width="12" height="32" border="0" /></div>			
	<div id="footerleft"><img src="images/footer-left.gif" alt="" width="15" height="32" border="0" /></div>
	<div id="footercontent"><a href="<%= Constants.CNTRL_INTRAY%>"><spring:message code="ui.label.nav.intray"/></a> | <a href="<%=Constants.CNTRL_SEARCH_SITE%>"><spring:message code="ui.label.nav.harvestAuthorisations"/></a> | <a href="curator/target/search.html"><spring:message code="ui.label.nav.targets"/></a> | <a href="curator/groups/search.html"><spring:message code="ui.label.nav.groups"/></a> | <a href="<%= Constants.CNTRL_TI_QUEUE%>"><spring:message code="ui.label.nav.targetInstances"/></a> | <a href="curator/report/report.html"><spring:message code="ui.label.nav.reports"/></a> | <a href="<%=Constants.CNTRL_MANAGEMENT %>"><spring:message code="ui.label.nav.management"/></a></div>
</div>
