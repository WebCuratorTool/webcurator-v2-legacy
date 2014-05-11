<%@page contentType="text/html; charset=UTF-8" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>

<%-- ${activeTab} holds the name of the active tab --%>
<tiles:importAttribute name="activeTab" scope="page"/>
<tiles:importAttribute name="pageIcon" scope="page"/>

<div id="primaryNavBar">
<nobr><img src="images/x.gif" width="5" height="38" border="0"/><img src="images/primary-left.gif" alt="" width="3" height="38" border="0" /><a href="<%= Constants.CNTRL_INTRAY%>" accesskey="2" <c:if test="${activeTab!='intray'}">onMouseOver="rollOn('intray')" onMouseOut="rollOff('intray')"</c:if>>  <img src="<spring:message code="image.tab.intray.url"/>-${activeTab == 'intray' ? 'on.gif' : 'off.gif'}" alt="<spring:message code="image.tab.intray.alt"/>" width="50" height="38" border="0" name="intray" /></a><img src="images/primary-mid.gif" alt="" width="2" height="38" border="0" /><a href="<%=Constants.CNTRL_SEARCH_SITE%>" accesskey="3" <c:if test="${activeTab!='authorisations'}">onMouseOver="rollOn('authorisations')" onMouseOut="rollOff('authorisations')"</c:if>><img src="<spring:message code="image.tab.harvestAuthorisiations.url"/>-${activeTab == 'authorisations' ? 'on.gif' : 'off.gif'}" alt="<spring:message code="image.tab.harvestAuthorisiations.alt"/>" width="133" height="38" border="0" name="authorisations" /></a><img src="images/primary-mid.gif" alt="" width="2" height="38" border="0" /><a href="curator/target/search.html" accesskey="4" <c:if test="${activeTab!='targets'}">onMouseOver="rollOn('targets')" onMouseOut="rollOff('targets')"</c:if>><img src="<spring:message code="image.tab.targets.url"/>-${activeTab == 'targets' ? 'on.gif' : 'off.gif'}" alt="<spring:message code="image.tab.targets.alt"/>" width="53" height="38" border="0" name="targets" /></a><img src="images/primary-mid.gif" alt="" width="2" height="38" border="0" /><a href="<%= Constants.CNTRL_TI_QUEUE%>" accesskey="6" <c:if test="${activeTab!='instances'}">onMouseOver="rollOn('instances')" onMouseOut="rollOff('instances')"</c:if>><img src="<spring:message code="image.tab.instances.url"/>-${activeTab == 'instances' ? 'on.gif' : 'off.gif'}" alt="<spring:message code="image.tab.instances.alt"/>" width="101" height="38" border="0" name="instances" /></a><img src="images/primary-mid.gif" alt="" width="2" height="38" border="0" /><a href="curator/groups/search.html" accesskey="5" <c:if test="${activeTab!='groups'}">onMouseOver="rollOn('groups')" onMouseOut="rollOff('groups')"</c:if>><img src="<spring:message code="image.tab.groups.url"/>-${activeTab == 'groups' ? 'on.gif' : 'off.gif'}" alt="<spring:message code="image.tab.groups.alt"/>" width="51" height="38" border="0" name="groups" /></a><img src="images/primary-mid.gif" alt="" width="2" height="38" border="0" /><a href="<%= Constants.CNTRL_MANAGEMENT%>" accesskey="8" <c:if test="${activeTab!='management'}">onMouseOver="rollOn('management')" onMouseOut="rollOff('management')"</c:if>><img src="<spring:message code="image.tab.management.url"/>-${activeTab == 'management' ? 'on.gif' : 'off.gif'}" alt="<spring:message code="image.tab.management.alt"/>" width="82" height="38" border="0" name="management" /></a><img src="images/primary-right.gif" alt="" width="3" height="38" border="0" /></nobr>
</div>

<div id="iconColumn">
<c:choose>
  <c:when test="${pageIcon == 'intray'}">
    <img src="images/title-icon-intray.jpg" alt="" width="106" height="141" border="0" />
  </c:when>
  
  <c:when test="${pageIcon == 'authorisations'}">
    <img src="images/title-icon-authorisations.jpg" alt="" width="106" height="141" border="0" />
  </c:when>
  
  <c:when test="${pageIcon == 'targets'}">
    <img src="images/title-icon-targets.gif" alt="" width="106" height="141" border="0" />  
  </c:when>
  
  <c:when test="${pageIcon == 'instances'}">
    <img src="images/title-icon-instances.jpg" alt="" width="106" height="141" border="0" />
  </c:when>
  
  <c:when test="${pageIcon == 'groups'}">
    <img src="images/title-icon-groups.jpg" alt="" width="106" height="141" border="0" />
  </c:when>
  
  <c:when test="${pageIcon == 'management'}">
    <img src="images/title-icon-management.jpg" alt="" width="106" height="141" border="0" />
  </c:when>
  
  <c:when test="${pageIcon == 'harvestConfig'}">
	<img src="images/title-icon-harvester.jpg" alt="" width="106" height="141" border="0" />
  </c:when>
  
  <c:when test="${pageIcon == 'users'}">
    <img src="images/title-icon-users.jpg" alt="" width="106" height="141" border="0" />  
  </c:when>
  
  <c:when test="${pageIcon == 'reports'}">
    <img src="images/title-icon-reports.jpg" alt="" width="106" height="141" border="0" />
  </c:when>
  
  <c:when test="${pageIcon == 'templates'}">
    <img src="images/title-icon-templates.jpg" alt="" width="106" height="141" border="0" />
  </c:when>  
  
</c:choose>
</div>