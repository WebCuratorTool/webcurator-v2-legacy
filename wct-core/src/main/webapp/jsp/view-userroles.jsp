<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.webcurator.ui.admin.command.AssociateUserRoleCommand" %>
<%@ page import="org.webcurator.domain.model.auth.Role" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<script language="JavaScript" src="scripts/multiselect.js"%>"></script>
<form name="associate" action="<%=Constants.CNTRL_ASSOCIATE_USERROLE%>" method="POST" ONSUBMIT="listBoxToHidden(document.associate.<%=AssociateUserRoleCommand.PARAM_ASSOCIATED_ROLES%>, document.associate.<%=AssociateUserRoleCommand.PARAM_SELECTED_ROLES%>); return true;">
<input type="hidden" name="<%=AssociateUserRoleCommand.PARAM_USERNAME%>" value="${command.choosenUser}">
<input type="hidden" name="<%=AssociateUserRoleCommand.PARAM_USER_OID%>" value="${command.choosenUserOid}">
<span class="subBoxTitle">User to Role Association</span><br />
  <table border="0" cellpadding="10" cellspacing="0">
  <tr>
  <td>
  <table border="0" cellpadding="2" cellspacing="0">
  <tr><td class="subBoxTextHdr">Username:</td>
  <TD class="subBoxText">${command.choosenUser}</TD>
  </tr>
  </table>
  <table border="0" cellspacing="2" cellpadding="0">
  <tr>
  <td>
  <span class="subBoxTextHdr"><b>All Roles</b></span><br>
  <select name="<%=AssociateUserRoleCommand.PARAM_ALL_ROLES%>" size="20" style="width:300px">
  <c:forEach items="${unassignedRoles}" var="role">
          <option value="<c:out value="${role.oid}"/>"><c:out value="${role.name}"/></option>
        </c:forEach>
  </select>
  </td>
  <td align="center">
  				  <input type="image" src="images/mgmt-roles-btn-next.gif" onclick="moveItem(document.forms.associate.<%=AssociateUserRoleCommand.PARAM_ALL_ROLES%>, document.forms.associate.<%=AssociateUserRoleCommand.PARAM_ASSOCIATED_ROLES%>); return false;"/>
                  <br>
                  <input type="image" src="images/mgmt-roles-btn-previous.gif" onclick="moveItem(document.forms.associate.<%=AssociateUserRoleCommand.PARAM_ASSOCIATED_ROLES%>, document.forms.associate.<%=AssociateUserRoleCommand.PARAM_ALL_ROLES%>); return false;"/>
  </td>
  <td>
  <span class="subBoxTextHdr"><b>Associated Roles</b></span><br>
  <select name="<%=AssociateUserRoleCommand.PARAM_ASSOCIATED_ROLES%>" size="20" STYLE="width:300px">
  		<c:forEach items="${assignedRoles}" var="role">
          <option value="<c:out value="${role.oid}"/>"><c:out value="${role.name}"/></option>
        </c:forEach>
  </select>
  </td>
  </tr>
  </table>
  </td>
  </tr>
  <tr>
  <td class="boxActionCell" align="center"><input type="image" name="update" src="images/mgmt-btn-update.gif" />
  <INPUT type="hidden" name="<%=AssociateUserRoleCommand.PARAM_ACTION%>" value="<%=AssociateUserRoleCommand.ACTION_ASSOCIATE_SAVE%>"/>
  <input type="hidden" name="<%=AssociateUserRoleCommand.PARAM_SELECTED_ROLES%>" >
  <a href="<%= Constants.CNTRL_USER %>"><img name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0"></a>
  </td>  
  </tr>
  </table>
  </form>
