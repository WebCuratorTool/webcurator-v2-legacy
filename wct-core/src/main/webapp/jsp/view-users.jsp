<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.ui.admin.command.UserCommand" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ page import="org.webcurator.ui.admin.command.CreateUserCommand" %>
<%@ page import="org.webcurator.ui.admin.command.AssociateUserRoleCommand" %>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<c:set var="allowPrivs"><%= Privilege.MANAGE_USERS +","+ Privilege.MODIFY_OWN_CREDENTIALS %></c:set>

<div id="resultsTable">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td>
			<span class="midtitleGrey">Users</span>
		</td>
		<td colspan="3" align="right">
		  Agency Filter:&nbsp;
		</td>
		<td>
			<form id="frmFilter" action="<%= Constants.CNTRL_USER %>" method="POST">
			<input type="hidden" name="<%=UserCommand.PARAM_CMD%>" value="<%= UserCommand.ACTION_FILTER%>">
		  	<select name="<%=UserCommand.PARAM_AGENCY_FILTER%>" id="agencyfilter" onchange="document.getElementById('frmFilter').submit();">
	  		<option id="" ${agencyfilter eq '' ? 'SELECTED' : ''}></option>
			<c:forEach items="${agencies}" var="agency">
		  		<option id="${agency.name}" ${agencyfilter eq agency.name ? 'SELECTED' : ''}>${agency.name}</option>
		  	</c:forEach>
		  	</select>
		  	</form>
		</td>
		<td>
		<authority:hasPrivilege privilege="<%= Privilege.MANAGE_USERS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
			<form action="<%= Constants.CNTRL_CREATE_USER %>" method="POST">
			<input type="hidden" name="<%= CreateUserCommand.PARAM_ACTION%>" value="<%= CreateUserCommand.ACTION_NEW%>">
			<input type="image" src="images/create-new-btn-red.gif" alt="Create a new User" width="82" height="24" border="0" align="right" vspace="3">
			</form>
		</authority:hasPrivilege>
		</td>
	</tr>
	<tr>
		<td class="tableHead">Username</td>
		<td class="tableHead">First name</td>
		<td class="tableHead">Last name</td>
		<td class="tableHead">Email</td>
		<td class="tableHead">Agency</td>
		<td class="tableHead">Action</td>
	</tr>
	<c:forEach items="${userDTOs}" var="user">
	<c:if test="${agencyfilter eq '' or agencyfilter eq user.agencyName}">
	  <c:set var="showButton">false</c:set>
	  <tr>
	    <td class="tableRowLite"><c:out value="${user.username} ${user.active == true? '':'(disabled)'}"/></td>
	    <td class="tableRowLite"><c:out value="${user.firstname}"/></td>
	    <td class="tableRowLite"><c:out value="${user.lastname}"/></td>
	    <td class="tableRowLite"><c:out value="${user.email}"/></td>
	    <td class="tableRowLite"><c:out value="${user.agencyName}"/></td>
	    <td class="tableRowLite">
	    	<table>
	    	<tr>
	    		<td>
				    <form name="viewForm" action="<%= Constants.CNTRL_CREATE_USER %>" method="POST">
				    <input type="hidden" name="<%= CreateUserCommand.PARAM_ACTION %>" value="<%= CreateUserCommand.ACTION_VIEW%>">
				    <input type="hidden" name="<%= CreateUserCommand.PARAM_OID %>" value="${user.oid}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				    <input type="image" name="view" src="images/action-icon-view.gif" title="View" alt="click here to VIEW this item" width="18" height="18" border="0">
				    </form>
	    		</td>
	    		<td>
				    <authority:hasAtLeastOnePriv privileges="${allowPrivs}">
					    <authority:hasPrivilege privilege="<%= Privilege.MODIFY_OWN_CREDENTIALS %>" scope="<%= Privilege.SCOPE_NONE %>">
					    <c:if test="${user.oid == loggedInUser.oid}">
					        <c:set var="showButton">true</c:set>
					    </c:if>
					    </authority:hasPrivilege>
					    <authority:hasPrivilege privilege="<%= Privilege.MANAGE_USERS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
					    	<c:set var="showButton">true</c:set>
					    </authority:hasPrivilege>
					    <c:if test="${showButton == 'true'}">
						    <form name="editForm" action="<%= Constants.CNTRL_CREATE_USER %>" method="POST">
						    <input type="hidden" name="<%= CreateUserCommand.PARAM_ACTION %>" value="<%= CreateUserCommand.ACTION_EDIT%>">
						    <input type="hidden" name="<%= CreateUserCommand.PARAM_OID %>" value="${user.oid}">
							<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
						    <input type="image" name="edit" src="images/action-icon-edit.gif" title="Edit" alt="click here to EDIT this item" width="18" height="18" border="0">
						    </form>
					    </c:if>
				    </authority:hasAtLeastOnePriv>
			    </td>
			    <td>
				    <authority:hasPrivilege privilege="<%= Privilege.MANAGE_USERS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
					    <form name="rolesForm" action="<%= Constants.CNTRL_ASSOCIATE_USERROLE %>" method="POST">
					    <input type="hidden" name="<%= AssociateUserRoleCommand.PARAM_ACTION %>" value="<%= AssociateUserRoleCommand.ACTION_ASSOCIATE_VIEW%>">
					    <input type="hidden" name="<%= AssociateUserRoleCommand.PARAM_USER_OID %>" value="${user.oid}">
					    <input type="hidden" name="<%= AssociateUserRoleCommand.PARAM_USERNAME %>" value="${user.username}">
						<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					    <input type="image" name="roles" src="images/action-icon-roles.gif" title="Roles" alt="Roles" />
					    </form>
				    </authority:hasPrivilege>
			    </td>
			    <td>
				    <authority:hasPrivilege privilege="<%= Privilege.MANAGE_USERS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
					    <form name="statusForm" action="<%= Constants.CNTRL_USER %>" method="POST">
					    <input type="hidden" name="<%= UserCommand.PARAM_CMD %>" value="<%= UserCommand.ACTION_STATUS%>">
					    <input type="hidden" name="<%= UserCommand.PARAM_OID %>" value="${user.oid}">
					    
						<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					    <c:choose>
					    	<c:when test="${user.active}">
					    	<input type="image" name="status" src="images/action-icon-disable.gif" title="Disable" alt="Disable" />
					    	</c:when>
					    	<c:otherwise>
					    	<input type="image" name="status" src="images/action-icon-enable.gif" title="Enable" alt="Enable" />
					    	</c:otherwise>
					    </c:choose>
					    </form>
				    </authority:hasPrivilege>
			    </td>
			    <td>
				    <authority:hasPrivilege privilege="<%= Privilege.MANAGE_USERS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
					    <form name="deleteForm" action="<%= Constants.CNTRL_USER %>" method="POST" onSubmit="return confirm('Do you really want to delete this user?');">
					    <input type="hidden" name="<%= UserCommand.PARAM_CMD %>" value="<%= UserCommand.ACTION_DELETE%>">
					    <input type="hidden" name="<%= UserCommand.PARAM_OID %>" value="${user.oid}">
						<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					    <input type="image" name="delete" src="images/action-icon-delete.gif" title="Delete" alt="click here to DELETE this item" width="18" height="19" border="0">
					    </form>
				    </authority:hasPrivilege>
			    </td>
		    </tr>
	    </table>

	    </td>
	  </tr>
	</c:if>
	</c:forEach>
	</table>
</div>
