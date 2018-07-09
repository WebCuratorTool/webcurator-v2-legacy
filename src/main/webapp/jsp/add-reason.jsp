<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.admin.command.CreateRejReasonCommand" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<c:set var="allowPrivs"><%= Privilege.MANAGE_REASONS %></c:set>
		<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
		<br />
		<form name="registerRejReasonForm" action="<%= Constants.CNTRL_CREATE_REASON %>" method="POST">
		<input type="hidden" name="<%= CreateRejReasonCommand.PARAM_OID %>" value="${command.oid}">
		<input type="hidden" name="<%= CreateRejReasonCommand.PARAM_MODE %>" value="${command.mode}">
		<table cellpadding="3" cellspacing="0" border="0">
		
		<tr>
			<td class="subBoxTextHdr">Agency:</td>
			<td class="subBoxText">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
				  <c:forEach items="${agencies}" var="agency">
				     <c:if test="${command.agencyOid == agency.oid}">
				    	  <c:out value="${agency.name}"/>
				     </c:if>
				  </c:forEach>
				</c:when>
				<c:when test="${command.mode == 'edit'}">
				  <c:forEach items="${agencies}" var="agency">
				     <c:if test="${command.agencyOid == agency.oid}">
				    	  <input type="hidden" name="<%= CreateRejReasonCommand.PARAM_AGENCY_OID %>" value="${agency.oid}">${agency.name}
				     </c:if>
				  </c:forEach>
				</c:when>
				<c:otherwise>
					<select name="<%= CreateRejReasonCommand.PARAM_AGENCY_OID %>">
					    	  <c:forEach items="${agencies}" var="agency">
					    	  <option value="<c:out value="${agency.oid}"/>" <c:if test="${agency.oid == command.agencyOid}">selected</c:if>>${agency.name}</option>
					    	  </c:forEach>
					</select>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
		
		<tr>
			<td class="subBoxTextHdr">Rejection Reason:</td>
			<c:choose>
			<c:when test="${command.mode == 'view'}">
				<td class="subBoxText">
					<c:out value="${command.name}"></c:out>
				</td>
				<td></td>
			</c:when>
			<c:otherwise>
				<td class="subBoxText">
					<input type="text" name="<%= CreateRejReasonCommand.PARAM_NAME%>" value="${command.name}" size="100" maxlength="100">
				</td>
			</c:otherwise>
			</c:choose>
			<td></td>
		</tr>
		<tr>
			<td class="subBoxText" colspan="2">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
					<input type="checkbox" name="availableForTargets" value="true" <c:out value="${command.availableForTargets == true?'checked':''}"/> disabled>Available as a Rejection Reason for Targets
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="availableForTargets" value="true" <c:out value="${command.availableForTargets == true?'checked':''}"/>>Available as a Rejection Reason for Targets
				</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td class="subBoxText" colspan="2">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
					<input type="checkbox" name="availableForTIs" value="true" <c:out value="${command.availableForTIs == true?'checked':''}"/> disabled>Available as a Rejection Reason for harvested Target Instances
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="availableForTIs" value="true" <c:out value="${command.availableForTIs == true?'checked':''}"/>>Available as a Rejection Reason for harvested Target Instances
				</c:otherwise>
				</c:choose>
			</td>
		</tr>				
		<tr>
			<td class="subBoxText" colspan="2"> 			
			<c:choose>
				<c:when test="${command.mode == 'view'}">
				    <authority:hasAtLeastOnePriv privileges="${allowPrivs}">
					    <authority:hasPrivilege privilege="<%= Privilege.MANAGE_REASONS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
					    	<c:set var="showButton">true</c:set>
					    </authority:hasPrivilege>
					    <c:if test="${showButton == 'true'}">
				<input type="hidden" name="<%= CreateRejReasonCommand.PARAM_ACTION %>" value="<%= CreateRejReasonCommand.ACTION_EDIT %>">
				<input type="image" name="edit" src="images/generic-btn-edit.gif" />
					    </c:if>
				    </authority:hasAtLeastOnePriv>
				<a href="<%= Constants.CNTRL_REASONS %>"><img name="_done" src="images/generic-btn-done.gif" alt="Done" width="82" height="23" border="0"></a>
				</c:when>
				<c:when test="${command.mode == 'edit'}">
				<input type="hidden" name="<%= CreateRejReasonCommand.PARAM_ACTION %>" value="<%= CreateRejReasonCommand.ACTION_SAVE %>">
				<input type="image" name="update" src="images/mgmt-btn-update.gif" />
				<a href="<%= Constants.CNTRL_REASONS %>"><img name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0"></a>
				</c:when>
				<c:otherwise>
				<input type="hidden" name="<%= CreateRejReasonCommand.PARAM_ACTION %>" value="<%= CreateRejReasonCommand.ACTION_SAVE %>">
				<input type="image" name="create" src="images/generic-btn-save.gif" />
				<a href="<%= Constants.CNTRL_REASONS %>"><img name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0"></a>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
		
		</table>
		</form>
		<br/>

		