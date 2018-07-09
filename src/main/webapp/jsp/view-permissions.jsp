<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.site.command.GeneratePermissionTemplateCommand"%>
<%@ page import="org.webcurator.ui.common.Constants"%>
<%@page import="org.webcurator.domain.model.auth.Privilege" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
		<div id="annotationsBox">
				<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
				<table width="100%" cellpadding="3" cellspacing="0" border="0">
				  <tr>
				    <td class="annotationsHeaderRow">Status</td>
				    <td class="annotationsHeaderRow">Authorising Agent</td>
				    <td class="annotationsHeaderRow">From</td>
				    <td class="annotationsHeaderRow">To</td>
				    <td class="annotationsHeaderRow">URL Patterns</td>
				    <td class="annotationsHeaderRow" colspan="2">Action</td>
				  </tr>
				  <c:forEach items="${permissions}" var="permission" varStatus="status">
				  <tr>
				    <td class="annotationsLiteRow"><spring:message code="permission.state_${permission.status}"/></td>
				    <td class="annotationsLiteRow"><c:out value="${permission.authorisingAgent.name}"/></td>
				    <td class="annotationsLiteRow"><wct:date value="${permission.startDate}" type="fullDate"/></td>
				    <td class="annotationsLiteRow"><wct:date value="${permission.endDate}" type="fullDate"/></td>
				    <td class="annotationsLiteRow">
				    	<c:forEach items="${permission.urls}" var="url">
				    	  ${url.pattern}<br/>
				    	</c:forEach>
				    
				    </td>
				    <td class="annotationsLiteRow">
				      <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				    </td>
				    <td class="annotationsLiteRow">  
				      <c:choose>
				      <c:when test="${!empty templates}">
				      <authority:hasAgencyOwnedPriv ownedObject="${permission}" privilege="<%= Privilege.GENERATE_TEMPLATE %>">
				      <form name="generateTemplate" action="<%= Constants.CNTRL_GENERATE_TEMPLATE %>" method="POST">
				      <input type="hidden" name="<%= GeneratePermissionTemplateCommand.PARAM_ACTION%>" value="<%= GeneratePermissionTemplateCommand.ACTION_GENERATE_TEMPLATE %>">
				      <input type="hidden" name="<%= GeneratePermissionTemplateCommand.PARAM_PERMISSION_OID%>" value="${permission.oid}" />
				      <input type="hidden" name="<%= GeneratePermissionTemplateCommand.PARAM_SITE_OID %>" value="${permission.site.oid}" />
				      <select name="<%= GeneratePermissionTemplateCommand.PARAM_TEMPLATE_OID %>">
					  <c:forEach items="${templates}" var="template">
					     <option value="${template.oid}">${template.templateName}</option>
					  </c:forEach>				      
				      </select>
				      <input type="image" name="_generate_template" src="images/template-icon.gif" alt="Generate Template" title="Generate Template" border="0">
				      </form>
				      </authority:hasAgencyOwnedPriv>
				      </c:when>
				      <c:otherwise>
				      No Templates defined
				      </c:otherwise>
				      </c:choose>
				    </td>
				  </tr>
				  </c:forEach>
				</table>
			</div>
			<table width="100%">
			<tr>
			<td align="center">
			<a href="<%= Constants.CNTRL_SEARCH_SITE%>"><img src="images/harvest-btn-done.gif" border="0"></a>
			</td>
			</tr>
			</table>