<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.admin.command.CreateUserCommand" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ page import="org.webcurator.ui.admin.command.ChangePasswordCommand;"%>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="allowPrivs"><%= Privilege.MANAGE_USERS +","+ Privilege.MODIFY_OWN_CREDENTIALS %></c:set>
		<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
		<span class="subBoxTitle">Credentials</span><br />
		<form id="registerUserForm" name="registerUserForm" action="<%= Constants.CNTRL_CREATE_USER %>" method="POST">
		<input type="hidden" name="<%= CreateUserCommand.PARAM_OID %>" value="${command.oid}">
		<input type="hidden" name="<%= CreateUserCommand.PARAM_MODE %>" value="${command.mode}">
		<table cellpadding="3" cellspacing="0" border="0">
		<tr>
			<td class="subBoxTextHdr">Title:</td>
			<c:choose>
			<c:when test="${command.mode == 'view'}">
				<td class="subBoxText">
					<c:out value="${command.title}"></c:out>
				</td>
				<td></td>
			</c:when>
			<c:otherwise>
				<td class="subBoxText">
					<input type="text" name="<%= CreateUserCommand.PARAM_TITLE%>" value="${command.title}" maxlength="10">
				</td>
				<td>e.g. Mr, Mrs, Ms</td>
			</c:otherwise>
			</c:choose>
			<td></td>
		</tr>
		<tr>
			<td class="subBoxTextHdr">First Name:</td>
			<td class="subBoxText">
			<c:choose>
			<c:when test="${command.mode == 'view'}">
			<c:out value="${command.firstname}"/>
			</c:when>
			<c:otherwise>
			<input type="text" name="<%= CreateUserCommand.PARAM_FIRSTNAME%>" value="${command.firstname}" maxlength="50"><font color=red size=2>&nbsp;<strong>*</strong></font>
			</c:otherwise>
			</c:choose>
			</td>
			<td class="subBoxTextHdr">Last Name:</td>
			<td class="subBoxText">
			<c:choose>
			<c:when test="${command.mode == 'view'}">
			<c:out value="${command.lastname}"/>
			</c:when>
			<c:otherwise>
			<input type="text" name="<%= CreateUserCommand.PARAM_LASTNAME%>" value="${command.lastname}" maxlength="50"><font color=red size=2>&nbsp;<strong>*</strong></font>
			</c:otherwise>
			</c:choose>
			</td>
		</tr>
		<tr>
			<td class="subBoxTextHdr">Username:</td>
			<td class="subBoxText">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
				<c:out value="${command.username}"/>
				</c:when>
				<c:when test="${command.mode == 'edit'}">
				<input type="hidden" name="<%= CreateUserCommand.PARAM_USERNAME%>" value="${command.username}">${command.username}
				</c:when>
				<c:otherwise>
				<input type="text" name="<%= CreateUserCommand.PARAM_USERNAME%>" value="${command.username}" maxlength="80"><font color=red size=2>&nbsp;<strong>*</strong></font>
				</c:otherwise>
			</c:choose>
			</td>
			<td></td>
			<td></td>
		</tr>
		<c:choose>
		<c:when test="${command.mode == 'view'}">
		</c:when>
		<c:when test="${command.mode == 'edit'}">
		</c:when>
		<c:otherwise>
			<tr>
				<td class="subBoxText" colspan="2"><input type="checkbox" name="<%= CreateUserCommand.PARAM_EXTERNAL_AUTH%>" value="true" ${command.externalAuth == true?'checked':''}>Use External Directory for Password Credentials</td>
				<td class="subBoxText"></td>
				<td class="subBoxText"></td>
			</tr>
			<tr>
				<td class="subBoxTextHdr">Password:</td>
				<td class="subBoxText"><input type="password" name="<%= CreateUserCommand.PARAM_PASSWORD%>" maxlength="50"><font color=red size=2>&nbsp;<strong>*</strong></font></td>
				<td class="subBoxTextHdr">Confirm Password:</td>
				<td class="subBoxText"><input type="password" name="<%= CreateUserCommand.PARAM_CONFIRM_PASSWORD%>" maxlength="50"><font color=red size=2>&nbsp;<strong>*</strong></font></td>
			</tr>
		</c:otherwise>
		</c:choose>
		</table>
	
		<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
		<span class="subBoxTitle">Contact Information</span><br />
			
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
				    	  <input type="hidden" name="<%= CreateUserCommand.PARAM_AGENCY_OID %>" value="${agency.oid}">${agency.name}
				     </c:if>
				  </c:forEach>
				</c:when>
				<c:otherwise>
					<select name="<%= CreateUserCommand.PARAM_AGENCY_OID %>">
					    	  <c:forEach items="${agencies}" var="agency">
					    	  <option value="<c:out value="${agency.oid}"/>" <c:if test="${agency.oid == command.agencyOid}">selected</c:if>>${agency.name}</option>
					    	  </c:forEach>
					</select>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
		<tr>
			<td class="subBoxTextHdr">Address:</td>
			<td class="subBoxText">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
					<textarea rows="3" cols="60" name="<%= CreateUserCommand.PARAM_ADDRESS%>" readonly>${command.address}</textarea>
				</c:when>
				<c:otherwise>
					<textarea rows="3" cols="60" name="<%= CreateUserCommand.PARAM_ADDRESS%>">${command.address}</textarea>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
		<tr>
			<td class="subBoxTextHdr">Phone:</td>
			<td class="subBoxText">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
					<c:out value="${command.phone}"/>
				</c:when>
				<c:otherwise>
					<input type="text" name="<%= CreateUserCommand.PARAM_PHONE%>" value="${command.phone}" maxlength="16">
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
		<tr>
			<td class="subBoxTextHdr">Email:</td>
			<td class="subBoxText">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
					<c:out value="${command.email}"/>
				</c:when>
				<c:otherwise>
					<input type="text" name="<%= CreateUserCommand.PARAM_EMAIL%>" value="${command.email}" maxlength="100"><font color=red size=2>&nbsp;<strong>*</strong></font>
				</c:otherwise>
			</c:choose>	
			</td>
		</tr>
		<tr>
			<td class="subBoxText" colspan="2">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
					<input type="checkbox" name="notifyOnHarvestWarnings" value="true" <c:out value="${command.notifyOnHarvestWarnings == true?'checked':''}"/> disabled>Receive notifications for Harvester Warnings
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="notifyOnHarvestWarnings" value="true" <c:out value="${command.notifyOnHarvestWarnings == true?'checked':''}"/>>Receive notifications for Harvester Warnings
				</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td class="subBoxText" colspan="2">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
					<input type="checkbox" name="notifyOnGeneral" value="true" <c:out value="${command.notifyOnGeneral == true?'checked':''}"/> disabled>Receive general notifications
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="notifyOnGeneral" value="true" <c:out value="${command.notifyOnGeneral == true?'checked':''}"/>>Receive general notifications
				</c:otherwise>
				</c:choose>
			</td>
		</tr>				
		<tr>
			<td class="subBoxText" colspan="2">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
					<input type="checkbox" name="<%= CreateUserCommand.PARAM_NOTIFICATIONS_BY_EMAIL%>" value="true" <c:out value="${command.notificationsByEmail == true?'checked':''}"/> disabled>Receive notifications by email
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="<%= CreateUserCommand.PARAM_NOTIFICATIONS_BY_EMAIL%>" value="true" <c:out value="${command.notificationsByEmail == true?'checked':''}"/>>Receive notifications by email
				</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td class="subBoxText" colspan="2">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
					<input type="checkbox" name="<%= CreateUserCommand.PARAM_TASKS_BY_EMAIL%>" value="true" ${command.tasksByEmail == true?'checked':''} disabled>Receive tasks by email
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="<%= CreateUserCommand.PARAM_TASKS_BY_EMAIL%>" value="true" ${command.tasksByEmail == true?'checked':''}>Receive tasks by email
				</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<c:if test="${command.mode == 'view'}">
		<tr>
		<td class="subBoxTextHdr">
		<b>Associated Roles</b>
		</td>
		<td class="subBoxText">
			<textarea rows="3" cols="60" readonly><c:forEach items="${assignedRoles}" var="role"><c:out value="${role.name}"/>
</c:forEach></textarea>
		</td>
		</tr>
		</c:if>
		<tr>
			<td>
		<c:choose>
				<c:when test="${command.mode == 'view'}">
				    <authority:hasAtLeastOnePriv privileges="${allowPrivs}">
					    <authority:hasPrivilege privilege="<%= Privilege.MODIFY_OWN_CREDENTIALS %>" scope="<%= Privilege.SCOPE_NONE %>">
					    <c:if test="${not empty command.oid && command.oid == loggedInUser.oid}">
					        <c:set var="showButton">true</c:set>
					    </c:if>
					    </authority:hasPrivilege>
					    <authority:hasPrivilege privilege="<%= Privilege.MANAGE_USERS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
					    	<c:set var="showButton">true</c:set>
					    </authority:hasPrivilege>
					    <c:if test="${showButton == 'true'}">
<input type="image" name="edit" onclick="submitFormForEdit();" src="images/generic-btn-edit.gif" title="Edit" alt="Edit" width="82" height="23" border="0">
<input type="hidden" name="<%= CreateUserCommand.PARAM_ACTION %>" value="<%= CreateUserCommand.ACTION_EDIT%>">
						</c:if>
					</authority:hasAtLeastOnePriv>
				<a href="<%= Constants.CNTRL_USER %>"><img name="_done" src="images/generic-btn-done.gif" alt="Done" width="82" height="23" border="0"></a>
				</c:when>
				<c:when test="${command.mode == 'edit'}">
				<input type="hidden" name="<%= CreateUserCommand.PARAM_ACTION %>" value="<%= CreateUserCommand.ACTION_SAVE %>">
				<input type="image" name="update" src="images/mgmt-btn-update.gif" />
				<a href="<%= Constants.CNTRL_USER %>"><img name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0"></a>
				</c:when>
				<c:otherwise>
				<input type="hidden" name="<%= CreateUserCommand.PARAM_ACTION %>" value="<%= CreateUserCommand.ACTION_SAVE %>">
				<input type="image" name="create" src="images/generic-btn-save.gif" />
				<a href="<%= Constants.CNTRL_USER %>"><img name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0"></a>
				</c:otherwise>
		</c:choose>
			</td>
		</tr>
		</table>
		</form>

		<br/>
		<c:if test="${command.mode == 'edit'}">
			<c:if test="${command.externalAuth == false}">
				<form name="passwordChange" method="POST" action="<%=Constants.CNTRL_CHANGE_PWD %>">
					<input type="hidden" name="<%=ChangePasswordCommand.PARAM_USER_OID %>" value="<c:out value="${command.oid}"/>">
					<input type="image" src="images/mgmt-btn-password.gif" />
				</form>
			</c:if>
		</c:if>
		