<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.common.Constants"%>
<%@ page import="org.webcurator.ui.admin.command.TemplateCommand"%>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="resultsTable">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td colspan="4">
		<authority:hasPrivilege privilege="<%= Privilege.PERMISSION_REQUEST_TEMPLATE%>" scope="<%= Privilege.SCOPE_AGENCY%>">
			<form name="new" action="<%= Constants.CNTRL_PERMISSION_TEMPLATE %>" method="POST">
			<input type="hidden" name="<%= TemplateCommand.PARAM_ACTION%>" value="<%= TemplateCommand.ACTION_NEW %>">
			<input type="image" src="images/create-new-btn-red.gif" alt="Create a new item" width="82" height="24" border="0" align="right" vspace="3">
			</form>
		</authority:hasPrivilege>
			<span class="midtitleGrey">List</span>
		</td>
	</tr>
	<tr>
		<td class="tableHead">Agency</td>
		<td class="tableHead">Template Name</td>
		<td class="tableHead">Template Description</td>
		<td class="tableHead">Action</td>
	</tr>
	<c:set var="count" scope="page" value="0"/>
	<c:forEach items="${templates}" var="template">
		<tr>
			<form name="template<c:out value="${count}"/>" method="POST" action="<%=Constants.CNTRL_PERMISSION_TEMPLATE%>">
			<td class="tableRowLite"><c:out value="${template.agency.name}"/></td>
			<td class="tableRowLite"><c:out value="${template.templateName}"/></td>
			<td class="tableRowLite"><c:out value="${template.templateDescription}"/></td>
			<td class="tableRowLite">
				<input type="hidden" name="<%= TemplateCommand.PARAM_OID %>" value="${template.oid}" />
				<input type="hidden" name="<%= TemplateCommand.PARAM_ACTION %>" value="<%=TemplateCommand.ACTION_VIEW%>"/>

				<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				<input type="image" title="view" src="images/action-icon-view.gif" alt="click here to View this item" border="0"
			 	onclick="javascript:document.template<c:out value="${count}"/>.<%= TemplateCommand.PARAM_ACTION %>.value='<%=TemplateCommand.ACTION_VIEW%>';"/>

				<authority:hasPrivilege privilege="<%= Privilege.PERMISSION_REQUEST_TEMPLATE%>" scope="<%= Privilege.SCOPE_AGENCY%>">
				<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				<input type="image" title="edit" src="images/action-icon-edit.gif" alt="click here to Edit this item" border="0"
			 		onclick="javascript:document.template<c:out value="${count}"/>.<%= TemplateCommand.PARAM_ACTION %>.value='<%=TemplateCommand.ACTION_EDIT%>';"/>
			
				<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				<input type="image" title="delete" src="images/action-icon-delete.gif" alt="click here to Delete this item" border="0"
			 		onclick="javascript:var proceed=confirm('Do you really want to delete this Template?'); if (proceed) {document.template<c:out value="${count}"/>.<%= TemplateCommand.PARAM_ACTION %>.value='<%=TemplateCommand.ACTION_DELETE%>';} else { return false; }" />
				</authority:hasPrivilege>
			</td>
			</form>
		</tr>
		<c:set var="count" scope="page" value="${count + 1}"/>
	</c:forEach>
	</table>
</div>
