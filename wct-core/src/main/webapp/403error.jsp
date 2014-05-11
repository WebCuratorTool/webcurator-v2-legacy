<%@ page language="java" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <title>WEB CURATOR TOOL: Error</title>

    <style type="text/css" media="screen">
      @import url(<%= basePath %>styles/styles.css);
    </style>
    <link rel="stylesheet" media="screen" href="<%= basePath %>styles/basic.css" type="text/css" />
  </head>

<body> 
<a name="top"></a>
<div id="topBar"><div id="secondaryNav"></div><img src="<%= basePath %>images/web-curator-tool-logo.gif" alt="Web Curator Tool" width="320" height="68" border="0" /></div>
<div id="pageBody">
	<div id="pageContent">
		<div id="pageTitleOrange">		  
			<img src="<%= basePath %>images/header-right-orange.gif" alt="" width="5" height="7" border="0" align="right" /><img src="<%= basePath %>images/header-left-orange.gif" alt="" width="5" height="7" border="0" /><br />
			<span id="titleOrange">Error</span>		
		</div>
		<img src="<%= basePath %>images/x.gif" alt="" width="1" height="30" border="0" />
		<br><b>Error 403</b> - Access Forbidden<br>
		You do not have a valid WCT user account, or you have not been assigned the correct privileges to access this resource.
		Please contact your system administrator.
		<div id="footer">
			<div id="footerright"><img src="<%= basePath %>images/footer-right.gif" alt="" width="12" height="32" border="0" /></div>			
			<div id="footerleft"><img src="<%= basePath %>images/footer-left.gif" alt="" width="15" height="32" border="0" /></div>
			<div id="footercontent"></div>
		</div>
	</div>
</div>
</body>
</html>
<%
session.invalidate();
%>


