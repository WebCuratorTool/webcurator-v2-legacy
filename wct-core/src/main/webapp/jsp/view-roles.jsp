<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.ui.admin.command.RoleCommand" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<script>
function setAction(index, actionCmd)
{	
	document.getElementById("rowAction"+index).value = actionCmd;
}
</script>
<div id="resultsTable">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td>
			<span class="midtitleGrey">Roles</span>
		</td>
		<td align="right">
			Agency Filter:&nbsp;
		</td>
		<td>
			<form id="frmFilter" action="<%= Constants.CNTRL_ROLES %>" method="POST">
			<input type="hidden" name="<%=RoleCommand.PARAM_ACTION%>" value="<%= RoleCommand.ACTION_FILTER%>">
		  	<select name="<%=RoleCommand.PARAM_AGENCY_FILTER%>" id="agencyfilter" onchange="document.getElementById('frmFilter').submit();">
	  		<option id="" ${command.agencyFilter eq '' ? 'SELECTED' : ''}></option>
			<c:forEach items="${agencies}" var="agency">
		  		<option id="${agency.name}" ${command.agencyFilter eq agency.name ? 'SELECTED' : ''}>${agency.name}</option>
		  	</c:forEach>
		  	</select>
		  	</form>
		</td>
		<td>
			<authority:hasPrivilege privilege="<%= Privilege.MANAGE_ROLES %>" scope="<%= Privilege.SCOPE_AGENCY %>">
			<form action="<%= Constants.CNTRL_ROLES %>" method="POST">
			<input type="hidden" name="<%= RoleCommand.PARAM_ACTION%>" value="<%= RoleCommand.ACTION_NEW%>">
			<input type="image" src="images/create-new-btn-red.gif" alt="Create a new Role" width="82" height="24" border="0" align="right" vspace="3">
			</form>
			</authority:hasPrivilege>
		</td>
	</tr>
	<tr>
		<td class="tableHead" colspan="2">Role name</td>
		<td class="tableHead">Agency</td>
		<td class="tableHead">Action</td>
	</tr>
	<c:set var="count" scope="page" value="0" />
	<c:forEach items="${roles}" var="role">
	<c:if test="${command.agencyFilter eq '' or command.agencyFilter eq role.agency.name}">
	<tr>
	    <td class="tableRowLite" colspan="2"><c:out value="${role.name}"/></td>
	    <td class="tableRowLite"><c:out value="${role.agency.name}"/></td>
	    <td class="tableRowLite">
		    <form action="<%= Constants.CNTRL_ROLES %>" method="POST">
			<input type="hidden" name="<%= RoleCommand.PARAM_OID%>" value="${role.oid}">
			<input id="rowAction<c:out value="${count}"/>" type="hidden" name="<%= RoleCommand.PARAM_ACTION%>">
			<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
			<input type="image" title="view" src="images/action-icon-view.gif" alt="click here to VIEW this item" width="18" height="18" border="0" onclick="javascript:setAction(<c:out value="${count}"/>, '<%= RoleCommand.ACTION_VIEW%>');">
		    <authority:hasPrivilege privilege="<%= Privilege.MANAGE_ROLES %>" scope="<%= Privilege.SCOPE_AGENCY %>">
				<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				<input type="image" title="edit" src="images/action-icon-edit.gif" alt="click here to EDIT this item" width="18" height="18" border="0" onclick="javascript:setAction(<c:out value="${count}"/>, '<%= RoleCommand.ACTION_EDIT%>');">
				<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				<input type="image" title="delete" src="images/action-icon-delete.gif" alt="click here to DELETE this item" width="18" height="19" border="0" onclick="javascript:var proceed=confirm('Do you really want to delete this Role'); if (proceed) {setAction(<c:out value="${count}"/>, '<%= RoleCommand.ACTION_DELETE%>');} else { return false; }">
			</authority:hasPrivilege>
			</form>
	    </td>
  	</tr>
	<c:set var="count" scope="page" value="${count+1}" />
	</c:if>
	</c:forEach>
	</table>
</div>
