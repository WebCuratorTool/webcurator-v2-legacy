<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.admin.command.RoleCommand" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ page import="org.webcurator.ui.admin.command.RoleCommand" %>
<%@ page import="org.webcurator.auth.tag.HasPrivilegeTag" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.springframework.context.MessageSource" %>
<%@ page import="org.springframework.web.servlet.support.RequestContext" %>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>


<script language="JavaScript">
    function toggle(source) {
        var checkboxes = document.querySelectorAll('input[type="checkbox"]');
        for (var i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i] != source)
                checkboxes[i].checked = source.checked;
            }
        }
</script>



<%!
	private HasPrivilegeTag privTag = new HasPrivilegeTag();
		
	String insertPrivilege(String priv, String description, int defaultScope, int radioOffset, ArrayList scopes, String[] selectedPrivs, String[] selectedScope, boolean viewOnlyMode) {
		StringBuffer sb  = new StringBuffer();
		if(viewOnlyMode)
		{
			String roleChecked = displayPrivilege(priv, selectedPrivs);
			if(roleChecked.equals("checked"))
			{
				sb.append("<tr><td class=\"subBoxText\"><label for=\"" + RoleCommand.PARAM_PRIVILEGES + "_" + priv + "\">"+description+"</label></td>");
				sb.append("<td>");
			}
			else
			{
				return "";
			}
		}
		else
		{
			sb.append("<tr><td class=\"subBoxText\"><label for=\"" + RoleCommand.PARAM_PRIVILEGES + "_" + priv + "\"><input type=\"checkbox\" id=\"" + RoleCommand.PARAM_PRIVILEGES + "_" + priv + "\" name=\""+RoleCommand.PARAM_PRIVILEGES+"\" value=\""+priv+"\" "+displayPrivilege(priv, selectedPrivs)+">"+description+"</label></td>");
			sb.append("<td><select name=\""+RoleCommand.PARAM_SCOPED_PRIVILEGES+"\">");
		}
		//add the scope list to the privilege
		Iterator it = scopes.iterator();
		while (it.hasNext()) {
			int scope = ((Integer) it.next()).intValue();
			String selectedText = displaySelectedScope(priv,defaultScope,scope,selectedScope);
			if (scope != Privilege.SCOPE_ALL || 
				scopes.size() ==1 || 
				selectedText.equals("selected") || 
				privTag.hasPrivilege(Privilege.GRANT_CROSS_AGENCY_USER_ADMIN, Privilege.SCOPE_NONE)) {
				
				if(viewOnlyMode)
				{
					if(selectedText.equals("selected"))
					{
						sb.append(displayScope(scope));
					}
				}
				else
				{
					sb.append("<option value=\""+priv+"|"+scope+"\" "+selectedText+">"+displayScope(scope)+"</option>");
				}
			}
		}
		if(viewOnlyMode)
		{
			sb.append("</td></tr>");
		}
		else
		{
			sb.append("</select></td></tr>");
		}
		return sb.toString();
   }
   
   String displayScope(int scope) {
   		switch(scope) {
	   		case Privilege.SCOPE_OWNER:
	   			return "Owner";
	   		case Privilege.SCOPE_ALL:
	   			return "All";
	   		case Privilege.SCOPE_AGENCY:
	   			return "Agency";
	   		case Privilege.SCOPE_NONE:
	   			return "N/A";
   		}
   		return "";
   }
   
   String displayPrivilege(String priv, String[] selectedPrivs) {
		if (selectedPrivs != null) {
			int count = selectedPrivs.length;
			for (int i=0; i< count; i++) {
				if (priv.equals(selectedPrivs[i])) {
				  return "checked";
				}
			}
		}
		return "";
   }
   
   String displaySelectedScope(String priv, int defaultScope, int currentScope, String[] selectedScope) {
   		if (selectedScope != null) {
			int count = selectedScope.length;
			for (int i=0; i< count; i++) {
				if ((priv+"|"+currentScope).equals(selectedScope[i])) {
				  return "selected";
				}
			}
		} else {
   			return (defaultScope == currentScope?"selected":"");
   		}
   		return "";
   }
   
   String insertSectionHeader(String sectionTitle) {
   		StringBuffer sb  = new StringBuffer();
	    sb.append("<tr><td><img src=\"images/x.gif\" alt=\"\" width=\"1\" height=\"10\" border=\"0\" /><br />");
	    sb.append("<span class=\"subBoxTitle\">"+sectionTitle+"</span></td></tr>");
	    return sb.toString();
   }
   
   String getMessage(MessageSource msgSrc, String messageKey) { 
        return msgSrc.getMessage(messageKey, new Object[] {}, Locale.getDefault());
   }
%>
<%
    MessageSource msgSrc = new RequestContext(request).getWebApplicationContext();
   
    int radioOffset = 0;
	
	RoleCommand cmdObj = (RoleCommand)request.getAttribute(Constants.GBL_CMD_DATA);
	String[] selectedPrivs = null;
	String[] selectedScope = null;
	boolean viewOnlyMode = false;
	
	if (cmdObj != null) {
		selectedPrivs = cmdObj.getPrivileges();
		selectedScope = cmdObj.getScopedPrivileges();
		viewOnlyMode = cmdObj.getViewOnlyMode();
	}
	
	ArrayList none = new ArrayList();
	none.add(new Integer(Privilege.SCOPE_NONE));
	
	ArrayList full = new ArrayList();
	full.add(new Integer(Privilege.SCOPE_ALL));
	full.add(new Integer(Privilege.SCOPE_AGENCY));
	full.add(new Integer(Privilege.SCOPE_OWNER));
	full.add(new Integer(Privilege.SCOPE_NONE));
	
	ArrayList main = new ArrayList();
	main.add(new Integer(Privilege.SCOPE_ALL));
	main.add(new Integer(Privilege.SCOPE_AGENCY));
	main.add(new Integer(Privilege.SCOPE_OWNER));
	
	ArrayList agencyOwner = new ArrayList();
	agencyOwner.add(new Integer(Privilege.SCOPE_AGENCY));
	agencyOwner.add(new Integer(Privilege.SCOPE_OWNER));
	
	ArrayList allAgency = new ArrayList();
	allAgency.add(new Integer(Privilege.SCOPE_AGENCY));
	allAgency.add(new Integer(Privilege.SCOPE_ALL));
	
	ArrayList owner = new ArrayList();
	owner.add(new Integer(Privilege.SCOPE_OWNER));
	
	ArrayList agency = new ArrayList();
	agency.add(new Integer(Privilege.SCOPE_AGENCY));
	
	ArrayList all = new ArrayList();
	all.add(new Integer(Privilege.SCOPE_ALL));
	
%>
<form name="createRole" action="<%= Constants.CNTRL_ROLES %>" method="POST">
			<table cellpadding="3" cellspacing="0" border="0">

			<tr>
				<td class="subBoxTextHdr">Agency:</td>
				<td class="subBoxText">
				<c:choose>
				<c:when test="${command.viewOnlyMode}">
			        <c:forEach items="${agencies}" var="agency">
			          <c:if test="${command.agency == agency.oid}">
			          <c:out value="${agency.name}"/>
			          </c:if>
			        </c:forEach>
				</c:when>
				<c:otherwise>
				<select name="<%= RoleCommand.PARAM_AGENCY%>">
				        <c:forEach items="${agencies}" var="agency">
				          <option value="<c:out value="${agency.oid}"/>" <c:if test="${command.agency == agency.oid}">selected</c:if>><c:out value="${agency.name}"/></option>
				        </c:forEach>
				</select>
				</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<td class="subBoxTextHdr">Role Name:</td>
				<td class="subBoxText">
				<c:choose>
				<c:when test="${command.viewOnlyMode}">
				<c:out value="${command.roleName}" />
				</c:when>
				<c:otherwise>
				<input type="text" name="<%= RoleCommand.PARAM_ROLE_NAME%>" value="${command.roleName}" maxlength="80" style="width:350px;"><font color=red size=2>&nbsp;<strong>*</strong></font>
				</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<td class="subBoxTextHdr" valign="top">Description:</td>
				<td class="subBoxText" valign="top">
				<c:choose>
				<c:when test="${command.viewOnlyMode}">
				<textarea name="<%= RoleCommand.PARAM_ROLE_DESCRIPTION%>" style="width:350px;" readonly>${command.description}</textarea>
				</c:when>
				<c:otherwise>
				<textarea name="<%= RoleCommand.PARAM_ROLE_DESCRIPTION%>" style="width:350px;">${command.description}</textarea><font color=red size=2>&nbsp;<strong>*</strong></font>
				</c:otherwise>
				</c:choose>
				</td>
			</tr>
			</table>

			<table cellpadding="3" cellspacing="0" border="0">

				<c:if test="${!command.viewOnlyMode}">
                    <tr>
                        <td class="subBoxTitle">Select All</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="checkbox" name="select-all" id="select-all" onClick="toggle(this);"/>
                        </td>
                    </tr>
                </c:if>


				<%
					StringBuffer Loginsb = new StringBuffer();
					Loginsb.append(insertPrivilege(Privilege.LOGIN, getMessage(msgSrc, "ui.label.roles.privilege.login"), Privilege.SCOPE_NONE,radioOffset++,none,selectedPrivs,selectedScope,viewOnlyMode));
					Loginsb.append(insertPrivilege(Privilege.MODIFY_OWN_CREDENTIALS, getMessage(msgSrc, "ui.label.roles.privilege.updateUserCredentials"), Privilege.SCOPE_OWNER,radioOffset++,owner,selectedPrivs,selectedScope,viewOnlyMode));
					if(Loginsb.length() > 0)
					{
						Loginsb.insert(0,insertSectionHeader(getMessage(msgSrc, "ui.label.roles.sectionHeader.login")));
					}
				 %>
				 <%=Loginsb.toString()%>
				 <%
				 	StringBuffer Copysb = new StringBuffer();
					Copysb.append(insertPrivilege(Privilege.CREATE_SITE, getMessage(msgSrc, "ui.label.roles.privilege.createHarvestAuthorisations"), Privilege.SCOPE_AGENCY,radioOffset++, agency,selectedPrivs,selectedScope,viewOnlyMode));
					Copysb.append(insertPrivilege(Privilege.MODIFY_SITE, getMessage(msgSrc, "ui.label.roles.privilege.modifyHarvestAuthorisations"), Privilege.SCOPE_ALL,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					Copysb.append(insertPrivilege(Privilege.CONFIRM_PERMISSION, getMessage(msgSrc, "ui.label.roles.privilege.confirmPermissions"), Privilege.SCOPE_AGENCY,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					Copysb.append(insertPrivilege(Privilege.MODIFY_PERMISSION, getMessage(msgSrc, "ui.label.roles.privilege.modifyPermissions"), Privilege.SCOPE_AGENCY,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					Copysb.append(insertPrivilege(Privilege.TRANSFER_LINKED_TARGETS, getMessage(msgSrc, "ui.label.roles.privilege.transferLinkedTargets"), Privilege.SCOPE_AGENCY,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					Copysb.append(insertPrivilege(Privilege.ENABLE_DISABLE_SITE, getMessage(msgSrc, "ui.label.roles.privilege.enableDisableHarvestAuthorisations"), Privilege.SCOPE_ALL,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					Copysb.append(insertPrivilege(Privilege.GENERATE_TEMPLATE, getMessage(msgSrc, "ui.label.roles.privilege.generatePermissionRequests"), Privilege.SCOPE_AGENCY,radioOffset++, agency,selectedPrivs,selectedScope,viewOnlyMode));
					if(Copysb.length() > 0)
					{
						Copysb.insert(0, insertSectionHeader(getMessage(msgSrc, "ui.label.roles.sectionHeader.manageCopyingPermissionsandAccessRights")));
					}
				  %>
				<%=Copysb.toString() %>
				 <%
				 	StringBuffer Targetsb = new StringBuffer();
					Targetsb.append(insertPrivilege(Privilege.CREATE_TARGET, getMessage(msgSrc, "ui.label.roles.privilege.createTarget"), Privilege.SCOPE_AGENCY,radioOffset++, agency,selectedPrivs,selectedScope,viewOnlyMode));
					Targetsb.append(insertPrivilege(Privilege.MODIFY_TARGET, getMessage(msgSrc, "ui.label.roles.privilege.modifyTarget"), Privilege.SCOPE_OWNER,radioOffset++, main,selectedPrivs,selectedScope,viewOnlyMode));
					Targetsb.append(insertPrivilege(Privilege.APPROVE_TARGET, getMessage(msgSrc, "ui.label.roles.privilege.approveTarget"), Privilege.SCOPE_AGENCY,radioOffset++, main,selectedPrivs,selectedScope,viewOnlyMode));
					Targetsb.append(insertPrivilege(Privilege.CANCEL_TARGET, getMessage(msgSrc, "ui.label.roles.privilege.cancelTarget"), Privilege.SCOPE_OWNER,radioOffset++, main,selectedPrivs,selectedScope,viewOnlyMode));
					Targetsb.append(insertPrivilege(Privilege.DELETE_TARGET, getMessage(msgSrc, "ui.label.roles.privilege.deleteTarget"), Privilege.SCOPE_OWNER,radioOffset++, main,selectedPrivs,selectedScope,viewOnlyMode));
					Targetsb.append(insertPrivilege(Privilege.REINSTATE_TARGET, getMessage(msgSrc, "ui.label.roles.privilege.reinstateTarget"), Privilege.SCOPE_AGENCY,radioOffset++, main,selectedPrivs,selectedScope,viewOnlyMode));
					Targetsb.append(insertPrivilege(Privilege.ADD_SCHEDULE_TO_TARGET, getMessage(msgSrc, "ui.label.roles.privilege.addScheduletoTarget"), Privilege.SCOPE_OWNER,radioOffset++, main,selectedPrivs,selectedScope,viewOnlyMode));
					Targetsb.append(insertPrivilege(Privilege.SET_HARVEST_PROFILE_LV1, getMessage(msgSrc, "ui.label.roles.privilege.setHarvestProfileLevel1"), Privilege.SCOPE_AGENCY,radioOffset++, agency,selectedPrivs,selectedScope,viewOnlyMode));
					Targetsb.append(insertPrivilege(Privilege.SET_HARVEST_PROFILE_LV2, getMessage(msgSrc, "ui.label.roles.privilege.setHarvestProfileLevel2"), Privilege.SCOPE_AGENCY,radioOffset++, agency,selectedPrivs,selectedScope,viewOnlyMode));
					Targetsb.append(insertPrivilege(Privilege.SET_HARVEST_PROFILE_LV3, getMessage(msgSrc, "ui.label.roles.privilege.setHarvestProfileLevel3"), Privilege.SCOPE_AGENCY,radioOffset++, agency,selectedPrivs,selectedScope,viewOnlyMode));
					if(Targetsb.length() > 0)
					{
						Targetsb.insert(0, insertSectionHeader(getMessage(msgSrc, "ui.label.roles.sectionHeader.manageTargets")));
					}
				  %>
				<%=Targetsb.toString()  %>
				 <%
				 	StringBuffer Harvestsb = new StringBuffer();
					Harvestsb.append(insertPrivilege(Privilege.MANAGE_TARGET_INSTANCES, getMessage(msgSrc, "ui.label.roles.privilege.manageTargetInstances"), Privilege.SCOPE_AGENCY,radioOffset++, main,selectedPrivs,selectedScope,viewOnlyMode));
					Harvestsb.append(insertPrivilege(Privilege.LAUNCH_TARGET_INSTANCE_IMMEDIATE, getMessage(msgSrc, "ui.label.roles.privilege.launchTargetInstanceImmediate"), Privilege.SCOPE_AGENCY,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					Harvestsb.append(insertPrivilege(Privilege.MANAGE_WEB_HARVESTER, getMessage(msgSrc, "ui.label.roles.privilege.manageWebHarvesterSystem"), Privilege.SCOPE_ALL,radioOffset++, all,selectedPrivs,selectedScope,viewOnlyMode));
					Harvestsb.append(insertPrivilege(Privilege.ENDORSE_HARVEST, getMessage(msgSrc, "ui.label.roles.privilege.endorseHarvest"), Privilege.SCOPE_AGENCY,radioOffset++, main,selectedPrivs,selectedScope,viewOnlyMode));
					Harvestsb.append(insertPrivilege(Privilege.UNENDORSE_HARVEST, getMessage(msgSrc, "ui.label.roles.privilege.unendorseHarvest"), Privilege.SCOPE_AGENCY,radioOffset++, main,selectedPrivs,selectedScope,viewOnlyMode));
					Harvestsb.append(insertPrivilege(Privilege.ARCHIVE_HARVEST, getMessage(msgSrc, "ui.label.roles.privilege.archiveHarvest"), Privilege.SCOPE_AGENCY,radioOffset++, main,selectedPrivs,selectedScope,viewOnlyMode));
					if(Harvestsb.length() > 0)
					{
						Harvestsb.insert(0, insertSectionHeader(getMessage(msgSrc, "ui.label.roles.sectionHeader.manageHarvests")));
					}
				  %>
				<%=Harvestsb.toString()  %>
				 <%
				 	StringBuffer TargetGroupsb = new StringBuffer();
					TargetGroupsb.append(insertPrivilege(Privilege.CREATE_GROUP, getMessage(msgSrc, "ui.label.roles.privilege.createTargetGroup"), Privilege.SCOPE_AGENCY,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					TargetGroupsb.append(insertPrivilege(Privilege.ADD_TARGET_TO_GROUP, getMessage(msgSrc, "ui.label.roles.privilege.addTargettoGroup"), Privilege.SCOPE_OWNER,radioOffset++, main,selectedPrivs,selectedScope,viewOnlyMode));
					TargetGroupsb.append(insertPrivilege(Privilege.MANAGE_GROUP, getMessage(msgSrc, "ui.label.roles.privilege.manageTargetGroup"), Privilege.SCOPE_OWNER,radioOffset++,main,selectedPrivs,selectedScope,viewOnlyMode));
					TargetGroupsb.append(insertPrivilege(Privilege.MANAGE_GROUP_SCHEDULE, getMessage(msgSrc, "ui.label.roles.privilege.manageGroupSchedules"), Privilege.SCOPE_OWNER,radioOffset++, main,selectedPrivs,selectedScope,viewOnlyMode));
					TargetGroupsb.append(insertPrivilege(Privilege.MANAGE_GROUP_OVERRIDES, getMessage(msgSrc, "ui.label.roles.privilege.manageGroupOverrides"), Privilege.SCOPE_OWNER,radioOffset++, main,selectedPrivs,selectedScope,viewOnlyMode));
					if(TargetGroupsb.length() > 0)
					{
						TargetGroupsb.insert(0, insertSectionHeader(getMessage(msgSrc, "ui.label.roles.sectionHeader.manageTargetGroups")));
					}
				  %>
				<%=TargetGroupsb.toString()  %>
				 <%
				 	StringBuffer Profilesb = new StringBuffer();
					Profilesb.append(insertPrivilege(Privilege.VIEW_PROFILES, getMessage(msgSrc, "ui.label.roles.privilege.viewProfiles"), Privilege.SCOPE_AGENCY,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					Profilesb.append(insertPrivilege(Privilege.MANAGE_PROFILES, getMessage(msgSrc, "ui.label.roles.privilege.manageProfiles"), Privilege.SCOPE_AGENCY,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					if(Profilesb.length() > 0)
					{
						Profilesb.insert(0, insertSectionHeader(getMessage(msgSrc, "ui.label.roles.sectionHeader.manageProfiles")));
					}
				  %>
				<%=Profilesb.toString()  %>
				<authority:hasPrivilege privilege="<%= Privilege.GRANT_CROSS_AGENCY_USER_ADMIN%>" scope="<%= Privilege.SCOPE_NONE%>">
				 <%
				 	StringBuffer Usersb1 = new StringBuffer();
					Usersb1.append(insertPrivilege(Privilege.MANAGE_REASONS, getMessage(msgSrc, "ui.label.roles.privilege.manageReasons"), Privilege.SCOPE_AGENCY,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					Usersb1.append(insertPrivilege(Privilege.MANAGE_INDICATORS, getMessage(msgSrc, "ui.label.roles.privilege.manageIndicators"), Privilege.SCOPE_AGENCY,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					Usersb1.append(insertPrivilege(Privilege.MANAGE_FLAGS, getMessage(msgSrc, "ui.label.roles.privilege.manageFlags"), Privilege.SCOPE_AGENCY,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					Usersb1.append(insertPrivilege(Privilege.MANAGE_AGENCIES, getMessage(msgSrc, "ui.label.roles.privilege.manageAgencies"), Privilege.SCOPE_AGENCY,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					Usersb1.append(insertPrivilege(Privilege.MANAGE_USERS, getMessage(msgSrc, "ui.label.roles.privilege.manageUsers"), Privilege.SCOPE_AGENCY,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					Usersb1.append(insertPrivilege(Privilege.MANAGE_ROLES, getMessage(msgSrc, "ui.label.roles.privilege.manageRoles"), Privilege.SCOPE_AGENCY,radioOffset++, allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					Usersb1.append(insertPrivilege(Privilege.GRANT_CROSS_AGENCY_USER_ADMIN, getMessage(msgSrc, "ui.label.roles.privilege.grantcrossagencyuseradministration"), Privilege.SCOPE_NONE,radioOffset++,none,selectedPrivs,selectedScope,viewOnlyMode));
					if(Usersb1.length() > 0)
					{
						Usersb1.insert(0, insertSectionHeader(getMessage(msgSrc, "ui.label.roles.sectionHeader.manageUsersandRoles")));
					}
				  %>
				<%=Usersb1.toString()  %>
				</authority:hasPrivilege>
				<authority:noPrivilege privilege="<%= Privilege.GRANT_CROSS_AGENCY_USER_ADMIN%>" scope="<%= Privilege.SCOPE_NONE%>">
				 <%
				 	StringBuffer Usersb2 = new StringBuffer();
					Usersb2.append(insertPrivilege(Privilege.MANAGE_REASONS, getMessage(msgSrc, "ui.label.roles.privilege.manageReason"), Privilege.SCOPE_AGENCY,radioOffset++, agency,selectedPrivs,selectedScope,viewOnlyMode));
					Usersb2.append(insertPrivilege(Privilege.MANAGE_REASONS, getMessage(msgSrc, "ui.label.roles.privilege.manageIndicator"), Privilege.SCOPE_AGENCY,radioOffset++, agency,selectedPrivs,selectedScope,viewOnlyMode));
					Usersb2.append(insertPrivilege(Privilege.MANAGE_REASONS, getMessage(msgSrc, "ui.label.roles.privilege.manageFlag"), Privilege.SCOPE_AGENCY,radioOffset++, agency,selectedPrivs,selectedScope,viewOnlyMode));
					Usersb2.append(insertPrivilege(Privilege.MANAGE_AGENCIES, getMessage(msgSrc, "ui.label.roles.privilege.manageAgency"), Privilege.SCOPE_AGENCY,radioOffset++, agency,selectedPrivs,selectedScope,viewOnlyMode));
					Usersb2.append(insertPrivilege(Privilege.MANAGE_USERS, getMessage(msgSrc, "ui.label.roles.privilege.manageUser"), Privilege.SCOPE_AGENCY,radioOffset++, agency,selectedPrivs,selectedScope,viewOnlyMode));
					Usersb2.append(insertPrivilege(Privilege.MANAGE_ROLES, getMessage(msgSrc, "ui.label.roles.privilege.manageRole"), Privilege.SCOPE_AGENCY,radioOffset++, agency,selectedPrivs,selectedScope,viewOnlyMode));
					if(Usersb2.length() > 0)
					{
						Usersb2.insert(0, insertSectionHeader(getMessage(msgSrc, "ui.label.roles.sectionHeader.manageUsersandRoles")));
					}
				  %>
				<%=Usersb2.toString()  %>
				</authority:noPrivilege>
				 <%
				 	StringBuffer Systemsb = new StringBuffer();
					Systemsb.append(insertPrivilege(Privilege.CONFIGURE_PARAMETERS, getMessage(msgSrc, "ui.label.roles.privilege.configureParameters"), Privilege.SCOPE_NONE,radioOffset++,none,selectedPrivs,selectedScope,viewOnlyMode));
					Systemsb.append(insertPrivilege(Privilege.PERMISSION_REQUEST_TEMPLATE, getMessage(msgSrc, "ui.label.roles.privilege.managePermissionRequestTemplates"), Privilege.SCOPE_AGENCY,radioOffset++,allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					if(Systemsb.length() > 0)
					{
						Systemsb.insert(0, insertSectionHeader(getMessage(msgSrc, "ui.label.roles.sectionHeader.manageSystem")));
					}
				  %>
				<%=Systemsb.toString()  %>
				 <%
				 	StringBuffer Reportsb = new StringBuffer();
					Reportsb.append(insertPrivilege(Privilege.SYSTEM_REPORT_LEVEL_1, getMessage(msgSrc, "ui.label.roles.privilege.systemReports"), Privilege.SCOPE_AGENCY,radioOffset++,allAgency,selectedPrivs,selectedScope,viewOnlyMode));
					if(Reportsb.length() > 0)
					{
						Reportsb.insert(0, insertSectionHeader(getMessage(msgSrc, "ui.label.roles.sectionHeader.reporting")));
					}
				  %>
				<%=Reportsb.toString()  %>
				 <%
				 	StringBuffer Intraysb = new StringBuffer();
					Intraysb.append(insertPrivilege(Privilege.DELETE_TASK, getMessage(msgSrc, "ui.label.roles.privilege.deleteTask"), Privilege.SCOPE_OWNER,radioOffset++,owner,selectedPrivs,selectedScope,viewOnlyMode));
					if(Intraysb.length() > 0)
					{
						Intraysb.insert(0, insertSectionHeader(getMessage(msgSrc, "ui.label.roles.sectionHeader.in-tray")));
					}
				  %>
				<%=Intraysb.toString()  %>
				 <%
				 	StringBuffer Ownersb = new StringBuffer();
					Ownersb.append(insertPrivilege(Privilege.GIVE_OWNERSHIP, getMessage(msgSrc, "ui.label.roles.privilege.giveOwnershipto"), Privilege.SCOPE_AGENCY,radioOffset++,main,selectedPrivs,selectedScope,viewOnlyMode));
					Ownersb.append(insertPrivilege(Privilege.TAKE_OWNERSHIP, getMessage(msgSrc, "ui.label.roles.privilege.takeOwnershipfrom"), Privilege.SCOPE_AGENCY,radioOffset++,main,selectedPrivs,selectedScope,viewOnlyMode));
					if(Ownersb.length() > 0)
					{
						Ownersb.insert(0, insertSectionHeader(getMessage(msgSrc, "ui.label.roles.sectionHeader.ownership")));
					}
				  %>
				<%=Ownersb.toString()  %>
				<tr>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td align="center">
					<c:choose>
					<c:when test="${command.viewOnlyMode}">
		    <authority:hasPrivilege privilege="<%= Privilege.MANAGE_ROLES %>" scope="<%= Privilege.SCOPE_AGENCY %>">
<input type="image" name="edit" onclick="" src="images/generic-btn-edit.gif" title="Edit" alt="Edit" width="82" height="23" border="0">
<input type="hidden" name="<%= RoleCommand.PARAM_ACTION %>" value="<%= RoleCommand.ACTION_EDIT %>"> 
<input type="hidden" name="<%= RoleCommand.PARAM_OID %>" value="${command.oid}">
			</authority:hasPrivilege>
					<a href="<%= Constants.CNTRL_ROLES %>"><img name="_done" src="images/generic-btn-done.gif" alt="Done" width="82" height="23" border="0"></a>
					</c:when>
					<c:otherwise>
					<input type="hidden" name="<%= RoleCommand.PARAM_ACTION %>" value="<%= RoleCommand.ACTION_SAVE %>"> 
					<input type="hidden" name="<%= RoleCommand.PARAM_RADIO_GROUP_COUNT %>" value="<%= radioOffset %>">
					<input type="hidden" name="<%= RoleCommand.PARAM_OID %>" value="${command.oid}">
					<input type="image" name="create" src="images/<c:choose><c:when test="${command.oid != null}">mgmt-btn-update.gif</c:when><c:otherwise>generic-btn-save.gif</c:otherwise></c:choose>">
					<a href="<%= Constants.CNTRL_ROLES %>"><img name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0"></a>
					</c:otherwise>
					</c:choose>
					</td>
				</tr>
			</table>
</form>

