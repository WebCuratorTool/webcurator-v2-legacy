<%@ page language="java" pageEncoding="UTF-8"%><%@ page import="org.webcurator.ui.admin.command.ChangePasswordCommand" %><%@ page import="org.webcurator.ui.credentials.command.ResetPasswordCommand" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %><%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <link rel="stylesheet" media="screen" href="styles/styles.css" type="text/css" title="WCT" />
    <link rel="stylesheet" media="screen" href="styles/basic.css" type="text/css" title="WCT" />
    <title>Change Password</title>    
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
  </head>
  
  <body>
<a name="top"></a>
<div id="topBar"><img src="images/web-curator-tool-logo.gif" alt="Web Curator Tool" width="320" height="68" border="0" /></div>
<br class="clear" />

		<form name="ResetPwd" action="<%= org.webcurator.ui.common.Constants.CNTRL_RESET_PWD%>" method="POST">
		  <input type="hidden" name="<%=ResetPasswordCommand.PARAM_ACTION %>" value="<%=ResetPasswordCommand.ACTION_SAVE%>"/>
			<div id="loginBox">

				<div id="homeRightBoxTop">
				<img src="images/home-box-top-right.gif" alt="" width="20" height="13" border="0" align="right" /><img src="images/home-box-top-left.gif" alt="" width="20" height="13" border="0" /></div>
				<div id="homeRightBoxContent">
					<table cellpadding="0" cellspacing="0" border="0">
					<tr>
						<td width="8" background="images/home-box-left.gif"><img src="images/x.gif" width="8" height="1" border="0" /></td>
						<td valign="top" width="73"><img src="images/login-icon.jpg" alt="" width="73" height="108" border="0" /></td>
						<td valign="top" width="100%">
							<div id="homeBoxTitle">Password Expired</div>
							<div id="homeBoxLine"><img src="images/x.gif" width="1" height="5" border="0" /></div>
						  	<div id="homeBoxText">
						  	    <p>Your password has expired and must be changed</p>
						  	    <tiles:insert attribute="validation" />
								New Password:<br />
								<input type="password" name="<%=ChangePasswordCommand.PARAM_NEW_PWD%>"/><font color=red size=2>&nbsp;<strong>*</strong></font><br />
								Confirm Password:<br />
								<input type="password" name="<%=ChangePasswordCommand.PARAM_CONFIRM_PWD%>"/><font color=red size=2>&nbsp;<strong>*</strong></font><br />
								<input type="image" src="images/mgmt-btn-password.gif" alt="login" border="0" vspace="5" />
							</div>
						</td>
						<td width="10" background="images/home-box-right.gif"><img src="images/x.gif" width="10" height="1" border="0" /></td>
					</tr>
					</table>
				</div>
				<div id="homeRightBoxBottom"><img src="images/home-box-btm-right.gif" alt="" width="20" height="14" border="0" align="right" /><img src="images/home-box-btm-left.gif" alt="" width="20" height="14" border="0" /></div>
			</div>
		</form>
  </body>
</html>
