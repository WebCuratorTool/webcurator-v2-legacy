<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="org.webcurator.ui.admin.command.ChangePasswordCommand" %>
<%@ page import="org.webcurator.ui.common.Constants" %>

		<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
		<span class="subBoxTitle">Change Password</span><br />
		
		<form name="ChangePwd" action="<%= Constants.CNTRL_CHANGE_PWD%>" method="POST">
		<input type="hidden" name="<%= ChangePasswordCommand.PARAM_USER_OID %>" value="${command.userOid}">
		<table border="0">
		  <tr>
		    <td class="subBoxTextHdr">New Password:</td>
		    <td class="subBoxText"><input type="password" name="<%=ChangePasswordCommand.PARAM_NEW_PWD%>"/><font color=red size=2>&nbsp;<strong>*</strong></font></td>
		  </tr>
		  <tr>
		    <td class="subBoxTextHdr">Confirm Password:</td>
		    <td class="subBoxText"><input type="password" name="<%=ChangePasswordCommand.PARAM_CONFIRM_PWD%>"/><font color=red size=2>&nbsp;<strong>*</strong></font></td>
		  </tr>
		  <tr>
		    <td class="subBoxTextHdr">&nbsp;</td>
		    <td class="subBoxText">&nbsp;</td>
		  </tr>
		  <tr>
		    <td colspan="2" align="center"><input type="image" src="images/mgmt-btn-password.gif" />
		    <input type="hidden" name="<%=ChangePasswordCommand.PARAM_ACTION %>" value="<%=ChangePasswordCommand.ACTION_SAVE%>"/>
		    </td>
		  </tr>
		</table>
		</form>