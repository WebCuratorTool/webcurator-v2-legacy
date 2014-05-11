<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ page import="org.webcurator.ui.admin.command.AgencyCommand" %>
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
		<td colspan="3">
			<authority:hasPrivilege privilege="<%= Privilege.MANAGE_AGENCIES %>" scope="<%= Privilege.SCOPE_ALL %>">
				<form action="<%= Constants.CNTRL_AGENCY %>" method="POST">
				<input type="hidden" name="<%= AgencyCommand.PARAM_ACTION%>" value="<%= AgencyCommand.ACTION_NEW%>">
				<input type="image" src="images/create-new-btn-red.gif" alt="Create a new item" width="82" height="24" border="0" align="right" vspace="3">
				</form>
			</authority:hasPrivilege>
			<span class="midtitleGrey">Agencies</span>
		</td>
	</tr>
	<tr>
		<td class="tableHead">Agency name</td>
		<td class="tableHead">Address</td>
		<td class="tableHead">Action</td>
	</tr>
	<c:set var="count" scope="page" value="0" />
	<c:forEach items="${agencies}" var="agency">
	<tr>
	    <td class="tableRowLite"><c:out value="${agency.name}"/></td>
	    <td class="tableRowLite"><c:out value="${agency.address}"/></td>
		    <td class="tableRowLite">
		    <form action="<%= Constants.CNTRL_AGENCY %>" method="POST">
			<input type="hidden" name="<%= AgencyCommand.PARAM_OID%>" value="${agency.oid}">
			<input id="rowAction<c:out value="${count}"/>" type="hidden" name="<%= AgencyCommand.PARAM_ACTION%>"/>
			<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
			<input type="image" title="view" src="images/action-icon-view.gif" alt="click here to VIEW this item" width="18" height="18" border="0" onclick="javascript:setAction(<c:out value="${count}"/>, '<%= AgencyCommand.ACTION_VIEW%>');"/>
			<authority:hasPrivilege privilege="<%= Privilege.MANAGE_AGENCIES %>" scope="<%= Privilege.SCOPE_AGENCY %>">
				<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				<input type="image" title="edit" src="images/action-icon-edit.gif" alt="click here to EDIT this item" width="18" height="18" border="0" onclick="javascript:setAction(<c:out value="${count}"/>, '<%= AgencyCommand.ACTION_EDIT%>');"/>
			</authority:hasPrivilege>
			</form>
			</td>
	</tr>
	<c:set var="count" scope="page" value="${count+1}" />
	</c:forEach>
	</table>
</div>
