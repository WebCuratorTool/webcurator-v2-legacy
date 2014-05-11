<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ page import="org.webcurator.ui.admin.command.TemplateCommand" %>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<img src="images/x.gif" alt="" width="1" height="15" border="0" /><br />
	<div id="homeRightBoxTop"><img src="images/home-box-top-right.gif" alt="" width="20" height="13" border="0" align="right" /><img src="images/home-box-top-left.gif" alt="" width="20" height="13" border="0" /></div>
	<div id="homeRightBoxContent">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="8" background="images/home-box-left.gif"><img src="images/x.gif" width="8" height="1" border="0" /></td>
			<td valign="top" width="82"><img src="images/icon-home-templates.jpg" alt="" width="82" height="70" border="0" /></td>
			<td valign="top" width="100%">
				<div id="homeBoxTitle">Permission Request Templates</div>
				<div id="homeBoxLine"><img src="images/x.gif" width="1" height="5" border="0" /></div>
				<div id="homeBoxText">
					<table border="0">
					<tr>
					<td>
						<a href="<%= Constants.CNTRL_PERMISSION_TEMPLATE %>"><img src="images/home-btn-open.gif" alt="Open" width="66" height="18" border="0" vspace="5" hspace="3" /></a>
					</td>
					<td>
					<authority:hasPrivilege privilege="<%= Privilege.PERMISSION_REQUEST_TEMPLATE%>" scope="<%= Privilege.SCOPE_AGENCY%>">
						<form name="addTemplate" action="<%= Constants.CNTRL_PERMISSION_TEMPLATE%>" method="POST">
						<input type="hidden" name="<%= TemplateCommand.PARAM_ACTION%>" value="<%= TemplateCommand.ACTION_NEW %>"/> 
						<input type="image" src="images/home-btn-addnew.gif" alt="add new" width="66" height="18" border="0"/>
						</form>
					</authority:hasPrivilege>
					</td>					
					</tr>
					</table>
				</div>
			</td>
			<td width="10" background="images/home-box-right.gif"><img src="images/x.gif" width="10" height="1" border="0" /></td>
		</tr>
		</table>
	</div>
	<div id="homeRightBoxBottom"><img src="images/home-box-btm-right.gif" alt="" width="20" height="14" border="0" align="right" /><img src="images/home-box-btm-left.gif" alt="" width="20" height="14" border="0" /></div>
<div id="homeRightBoxTop"><img src="images/home-box-top-right.gif" alt="" width="20" height="13" border="0" align="right" /><img src="images/home-box-top-left.gif" alt="" width="20" height="13" border="0" /></div>
<div id="homeRightBoxContent">
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td width="8" background="images/home-box-left.gif"><img src="images/x.gif" width="8" height="1" border="0" /></td>
		<td valign="top" width="82"><img src="images/icon-home-harvester.jpg" alt="" width="82" height="70" border="0" /></td>
		<td valign="top" width="100%">
			<div id="homeBoxTitle"><spring:message code="ui.label.management.harvesterConfiguration"/></div>
			<div id="homeBoxLine"><img src="images/x.gif" width="1" height="5" border="0" /></div>
			<div id="homeBoxText">
				<a href="<%=Constants.CNTRL_MNG_AGENTS%>"><img src="images/home-btn-general.gif" alt="general" width="66" height="18" border="0" vspace="5" /></a><a href="<%=Constants.CNTRL_MNG_BANDWIDTH%>"><img src="images/home-btn-bandwidth.gif" alt="edit" width="80" height="18" border="0" vspace="5" hspace="3" /></a>
				<authority:hasAtLeastOnePriv privileges='<%=Privilege.MANAGE_PROFILES + "," + Privilege.VIEW_PROFILES %>'>
				<a href="curator/profiles/list.html"><img src="images/home-btn-profile.gif" alt="profile" width="66" height="18" border="0" vspace="5" /></a>
				</authority:hasAtLeastOnePriv>
			</div>
		</td>
		<td width="10" background="images/home-box-right.gif"><img src="images/x.gif" width="10" height="1" border="0" /></td>
	</tr>
	</table>
</div>
<div id="homeRightBoxBottom"><img src="images/home-box-btm-right.gif" alt="" width="20" height="14" border="0" align="right" /><img src="images/home-box-btm-left.gif" alt="" width="20" height="14" border="0" /></div>
<div id="homeRightBoxTop"><img src="images/home-box-top-right.gif" alt="" width="20" height="13" border="0" align="right" /><img src="images/home-box-top-left.gif" alt="" width="20" height="13" border="0" /></div>
<div id="homeRightBoxContent">
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td width="8" background="images/home-box-left.gif"><img src="images/x.gif" width="8" height="1" border="0" /></td>
		<td valign="top" width="82"><img src="images/icon-home-users.jpg" alt="Users, Roles & Agencies" width="82" height="70" border="0" /></td>
		<td valign="top" width="100%">
			<div id="homeBoxTitle">Users, Roles, Agencies &amp; Rejection Reasons</div>
			<div id="homeBoxLine"><img src="images/x.gif" width="1" height="5" border="0" /></div>
			<div id="homeBoxText">
				<table border="0">
				<tr>
					<td><b>Users:</b></td>
					<td><a href="curator/admin/user.html"><img src="images/home-btn-open.gif" alt="Open" width="66" height="18" border="0" vspace="5" align="absmiddle" /></a></td>
					<authority:hasPrivilege privilege="<%= Privilege.MANAGE_USERS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
					<td>
						<form action="<%= Constants.CNTRL_CREATE_USER %>" method="POST">
						<input type="hidden" name="action" value="new">
						<input type="image" src="images/home-btn-addnew.gif" name="newUser">
						</form>
					</td>
					</authority:hasPrivilege>					
				</tr>
				<tr>
					<td><b>Roles:</b></td>
					<td><a href="curator/admin/role.html"><img src="images/home-btn-open.gif" alt="Open" width="66" height="18" border="0" vspace="5" align="absmiddle" /></a></td>
					<authority:hasPrivilege privilege="<%= Privilege.MANAGE_ROLES %>" scope="<%= Privilege.SCOPE_AGENCY %>">
					<td>
						<form action="<%= Constants.CNTRL_ROLES %>" method="POST">
						<input type="hidden" name="action" value="new">
						<input type="image" src="images/home-btn-addnew.gif" name="newRole">
						</form>
					</td>
					</authority:hasPrivilege>					
				</tr>
				<tr>
					<td><b>Agencies:</b></td>
					<td><a href="<%=Constants.CNTRL_AGENCY%>"><img src="images/home-btn-open.gif" alt="Open" width="66" height="18" border="0" vspace="5" align="absmiddle" /></a></td>
					<authority:hasPrivilege privilege="<%= Privilege.MANAGE_AGENCIES %>" scope="<%= Privilege.SCOPE_ALL %>">
					<td>
						<form action="<%= Constants.CNTRL_AGENCY %>" method="POST">
						<input type="hidden" name="actionCommand" value="new">
						<input type="image" src="images/home-btn-addnew.gif" name="newAgency">
						</form>
					</td>
					</authority:hasPrivilege>					
				</tr>
				<tr>
					<td><b>Rejection&nbsp;Reasons:</b></td>
					<td><a href="<%=Constants.CNTRL_REASONS%>"><img src="images/home-btn-open.gif" alt="Open" width="66" height="18" border="0" vspace="5" align="absmiddle" /></a></td>
					<authority:hasPrivilege privilege="<%= Privilege.MANAGE_REASONS %>" scope="<%= Privilege.SCOPE_ALL %>">
					<td>
						<form action="<%= Constants.CNTRL_CREATE_REASON %>" method="POST">
						<input type="hidden" name="action" value="new">
						<input type="image" src="images/home-btn-addnew.gif" name="newReason">
						</form>
					</td>
					</authority:hasPrivilege>					
				</tr>
				</table>
			</div>
		</td>		
		<td width="10" background="images/home-box-right.gif"><img src="images/x.gif" width="10" height="1" border="0" /></td>
	</tr>
	</table>
</div>
<div id="homeRightBoxBottom"><img src="images/home-box-btm-right.gif" alt="" width="20" height="14" border="0" align="right" /><img src="images/home-box-btm-left.gif" alt="" width="20" height="14" border="0" /></div>
<div id="homeRightBoxTop"><img src="images/home-box-top-right.gif" alt="" width="20" height="13" border="0" align="right" /><img src="images/home-box-top-left.gif" alt="" width="20" height="13" border="0" /></div>
<div id="homeRightBoxContent">
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td width="8" background="images/home-box-left.gif"><img src="images/x.gif" width="8" height="1" border="0" /></td>
		<td valign="top" width="82"><img src="images/icon-home-reports.jpg" alt="" width="82" height="70" border="0" /></td>
		<td valign="top" width="100%">
			<div id="homeBoxTitle"><spring:message code="ui.label.management.reports"/></div>
			<div id="homeBoxLine"><img src="images/x.gif" width="1" height="5" border="0" /></div>
			<div id="homeBoxText">
				<a href="curator/report/report.html"><img src="images/home-btn-open.gif" alt="Open" width="66" height="18" border="0" vspace="5" align="absmiddle" /></a>
			</div>
		</td>
		<td width="10" background="images/home-box-right.gif"><img src="images/x.gif" width="10" height="1" border="0" /></td>
	</tr>
	</table>
</div>
<div id="homeRightBoxBottom"><img src="images/home-box-btm-right.gif" alt="" width="20" height="14" border="0" align="right" /><img src="images/home-box-btm-left.gif" alt="" width="20" height="14" border="0" /></div>
<img src="images/x.gif" alt="" width="1" height="30" border="0" /><br />