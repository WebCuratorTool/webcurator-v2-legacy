<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.webcurator.ui.util.*" %>
<%
	// Expires immediately.
	response.addHeader("Expires", "-1");
	
	// always modified 
	// Need to convert from PHP.
	//header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT"); 
	
	// HTTP/1.1
	response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
	response.addHeader("Cache-Control", "post-check=0, pre-check=0");

	// HTTP/1.0
	response.addHeader("Pragma", "no-cache");
%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">    
<HEAD>
	<base href="<%=basePath%>">   
	<link rel="stylesheet" media="screen" href="styles/styles.css" type="text/css" title="WCT" />
	<link rel="stylesheet" media="screen" href="styles/basic.css" type="text/css" title="WCT" />
	<title><tiles:getAsString name="title"/></title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">	
</HEAD>

<body style="margin: 10px 10px 0px 10px;"> 
<div id="subBoxTop"><img src="images/subtabs-right.gif" alt="" width="13" height="34" border="0" align="right" /><img src="images/subtabs-left.gif" alt="" width="11" height="34" border="0" /></div>		
<div id="subBoxContent">	
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
	  <tr>
		<td width="15" background="images/subbox-left-bg.gif"><img src="images/x.gif" width="15" height="1" border="0" /></td>
		<td bgcolor="#fffcf9" width="100%">
		<tiles:insert attribute="body" />
		</td>
		<td width="18" background="images/subbox-right-bg.gif"><img src="images/x.gif" width="18" height="1" border="0" /></td>
	  </tr>
	</table>
</div>
<div id="subBoxBtm"><img src="images/subbtm-right.gif" alt="" width="13" height="17" border="0" align="right" /><img src="images/subbtm-left.gif" alt="" width="11" height="17" border="0" /></div>
<p align="center"><input type="image" src="images/generic-btn-done.gif" onclick="self.close();"></p>
</body>
</html>