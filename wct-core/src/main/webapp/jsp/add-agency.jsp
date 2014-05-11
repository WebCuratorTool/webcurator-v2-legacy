<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.admin.command.AgencyCommand" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct"  %>

<form name="addAgencyForm" action="<%= Constants.CNTRL_AGENCY %>" method="POST">
<input type="hidden" name="<%= AgencyCommand.PARAM_OID %>" value="${command.oid}">
		<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
		<span class="subBoxTitle">Agency Information</span><br />
		<img src="images/x.gif" alt="" width="1" height="10" border="0" />

			<table cellpadding="3" cellspacing="0" border="0">
			<tr>
				<td class="subBoxTextHdr">Agency Name:</td>
				<td class="subBoxText">
				<c:choose>
				<c:when test="${command.viewOnlyMode}">
				<c:out value="${command.name}"/>
				</c:when>
				<c:otherwise>
				<input type="text" style="width:250px;" name="<%= AgencyCommand.PARAM_NAME%>" value="${command.name}" maxlength="80"><font color=red size=2>&nbsp;<strong>*</strong></font>
				</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<td class="subBoxTextHdr" valign=top>Agency Address:</td>
				<td class="subBoxText">
				<c:choose>
				<c:when test="${command.viewOnlyMode}">
				<textarea cols="60" rows="3" name="<%= AgencyCommand.PARAM_ADDRESS%>" readonly>${command.address}</textarea>
				</c:when>
				<c:otherwise>
				<textarea cols="60" rows="3" name="<%= AgencyCommand.PARAM_ADDRESS%>">${command.address}</textarea>
				<font color=red size=2>&nbsp;<strong>*</strong></font>
				</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<td class="subBoxTextHdr">Agency Phone:</td>
				<td class="subBoxText">
				<c:choose>
				<c:when test="${command.viewOnlyMode}">
				<c:out value="${command.phone}"/>
				</c:when>
				<c:otherwise>
				<input type="text" name="<%= AgencyCommand.PARAM_PHONE%>" value="${command.phone}" maxlength="20">
				</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<td class="subBoxTextHdr">Agency Fax:</td>
				<td class="subBoxText">
				<c:choose>
				<c:when test="${command.viewOnlyMode}">
				<c:out value="${command.fax}"/>
				</c:when>
				<c:otherwise>
				<input type="text" name="<%= AgencyCommand.PARAM_FAX%>" value="${command.fax}" maxlength="20">
				</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<td class="subBoxTextHdr">Agency Email:</td>
				<td class="subBoxText">
				<c:choose>
				<c:when test="${command.viewOnlyMode}">
				<c:out value="${command.email}"/>
				</c:when>
				<c:otherwise>
				<input type="text" style="width:250px;" name="<%= AgencyCommand.PARAM_EMAIL%>" value="${command.email}" maxlength="80">
				</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<td class="subBoxTextHdr">Agency URL:</td>
				<td class="subBoxText">
				<c:choose>
				<c:when test="${command.viewOnlyMode}">
				<c:out value="${command.agencyURL}"/>
				</c:when>
				<c:otherwise>
				<input type="text" style="width:250px;" name="<%= AgencyCommand.PARAM_AGENCY_URL%>" value="${command.agencyURL}" maxlength="255">
				</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<td class="subBoxTextHdr">Agency Logo URL:</td>
				<td class="subBoxText">
				<c:choose>
				<c:when test="${command.viewOnlyMode}">
				<c:out value="${command.agencyLogoURL}"/>
				</c:when>
				<c:otherwise>
				<input type="text" style="width:250px;" name="<%= AgencyCommand.PARAM_AGENCY_LOGO_URL%>" value="${command.agencyLogoURL}" maxlength="255">
				</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<td class="subBoxText" colspan="2">
				<c:choose>
					<c:when test="${command.viewOnlyMode}">
					<input type="checkbox" name="<%= AgencyCommand.PARAM_SHOW_TASKS%>" value="true" <c:out value="${command.showTasks == true?'checked':''}"/> disabled>Show tasks on intray (takes effect after next login)
					</c:when>
					<c:otherwise>
					<input type="checkbox" name="<%= AgencyCommand.PARAM_SHOW_TASKS%>" value="true" <c:out value="${command.showTasks == true?'checked':''}"/>>Show tasks on intray (takes effect after next login)
					</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<td class="subBoxTextHdr">Default type:</td>
				<td>
					<wct:list list="${descriptionTypes}" paramName="descriptionType" currentValue="${command.descriptionType}"/>
				</td>
			</tr>
			
			<tr>
				<td class="subBoxText" colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td class="subBoxText" colspan="2" align="center">
				<c:choose>
				<c:when test="${command.viewOnlyMode}">
			<authority:hasPrivilege privilege="<%= Privilege.MANAGE_AGENCIES %>" scope="<%= Privilege.SCOPE_AGENCY %>">
				<input type="hidden" name="<%= AgencyCommand.PARAM_ACTION %>" value="<%= AgencyCommand.ACTION_EDIT %>"> 
				<input type="image" name="edit" src="images/generic-btn-edit.gif" />
			</authority:hasPrivilege>
				<a href="<%= Constants.CNTRL_AGENCY %>"><img name="_done" src="images/generic-btn-done.gif" alt="Done" width="82" height="23" border="0"></a>
				</c:when>
				<c:otherwise>
				<input type="hidden" name="<%= AgencyCommand.PARAM_ACTION %>" value="<%= AgencyCommand.ACTION_SAVE %>"> 
				<input type="image" name="save" src="images/generic-btn-save.gif" />
				<a href="<%= Constants.CNTRL_AGENCY %>"><img name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0"></a>
				</c:otherwise>
				</c:choose>
				</td>
			</tr>

			</table>
</form>
