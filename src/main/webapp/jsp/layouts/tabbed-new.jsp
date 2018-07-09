<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.webcurator.ui.util.*" %>
<%
	TabStatus status = (TabStatus) pageContext.findAttribute("tabStatus");
	String currentPage = status.getCurrentTab().getJsp();
	
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
<%-- Layout Tiles 
  This layout create a html page with <header> and <body> tags. It render
   a header, left menu, body and footer tile.
  @param title String use in page title
  @param header Header tile (jsp url or definition name)
  @param menu Menu 
  @param body Body
  @param footer Footer
--%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">    
<HTML>
  <HEAD>
  	<base href="<%=basePath%>">   
    <link rel="stylesheet" media="screen" href="styles/styles.css" type="text/css" title="WCT" />
    <link rel="stylesheet" media="screen" href="styles/basic.css" type="text/css" title="WCT" />
    <title><tiles:getAsString name="title"/></title>
	<meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">	
  </HEAD>

<body> 
<a name="top"></a>
<tiles:insert attribute="header">
  <tiles:put name="page-help">
    <tiles:getAsString name="page-help"/> 
  </tiles:put>
</tiles:insert>
<tiles:insert page="top-nav-bar.jsp">
  <tiles:put name="activeTab"><tiles:getAsString name="activeTab"/></tiles:put>
  <tiles:put name="pageIcon"><tiles:getAsString name="pageIcon"/></tiles:put>
</tiles:insert>
<div id="pageBody">
	<div id="pageContent">
		<tiles:insert attribute="page-title" />
		<tiles:insert attribute="validation" />	
		
		<tiles:insert attribute="object-identifier"/>
		
<form id="tabForm" name="tabForm" method="post" action="<tiles:getAsString name="controller"/>"<c:if test="${tabStatus.currentTab.formEncodingType != null}">enctype="${tabStatus.currentTab.formEncodingType}"</c:if>>
<input type="hidden" name="_tab_current_page" value="<c:out value="${tabStatus.currentTab.pageId}"/>">
<input type="hidden" name="tabChangedTo" value=""/>
<div id="subBoxTop"><img src="images/subtabs-right.gif" alt="" width="13" height="34" border="0" align="right" /><nobr><img src="images/subtabs-left.gif" alt="" width="9" height="34" border="0" /><img src="images/subtabs-start.gif" alt="" width="2" height="34" border="0" /><c:forEach items="${tabs.tabs}" var="tab" varStatus="status"><c:choose><c:when test="${tab == tabStatus.currentTab}"><img src="images/subtabs-<c:out value="${tab.title}"/>-on.gif" alt="<c:out value="${tab.title}"/>" border="0" /><c:if test="${!status.last}"><img src="images/subtabs-mid.gif" alt="" width="2" height="34" border="0" /></c:if></c:when><c:otherwise><input type="image" name="_tab_change" src="images/subtabs-<c:out value="${tab.title}"/>-off.gif" alt="<c:out value="${tab.title}"/>" onclick="javascript:document.tabForm.tabChangedTo.value='${tab.title}'" border="0" /><c:if test="${!status.last}"><img src="images/subtabs-mid.gif" alt="" width="2" height="34" border="0" /></c:if></c:otherwise></c:choose></c:forEach><img src="images/subtabs-end.gif" alt="" width="4" height="34" /></nobr></div>
<div id="subBoxContent">
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
	  <tr>
		<td width="15" background="images/subbox-left-bg.gif"><img src="images/x.gif" width="15" height="1" border="0" /></td>
		<td bgcolor="#fffcf9" width="100%">
		<jsp:include page="<%= currentPage %>"/>
		</td>
		<td width="18" background="images/subbox-right-bg.gif"><img src="images/x.gif" width="18" height="1" border="0" /></td>
	  </tr>
	</table>
</div>
<div id="subBoxBtm"><img src="images/subbtm-right.gif" alt="" width="13" height="17" border="0" align="right" /><img src="images/subbtm-left.gif" alt="" width="11" height="17" border="0" /></div>
<p align="center">
<c:choose>
	<c:when test="${gblEditMode}">
	<input type="image" name="_tab_save" src="images/generic-btn-save.gif" alt="Save" width="82" height="23" border="0"><img src="images/x.gif" alt="" width="10" height="1" border="0" />
	<input type="image" name="_tab_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0">	
	</c:when>
	<c:otherwise>
	<input type="image" name="_tab_cancel" src="images/generic-btn-done.gif" alt="Done" width="82" height="23" border="0">
		<c:choose>
		<c:when test="${gblCanEdit}">
			<img src="images/x.gif" alt="" width="10" height="1" border="0" /><input type="image" name="_tab_edit" src="images/generic-btn-edit.gif" alt="Edit" width="82" height="23" border="0">
		</c:when>
		</c:choose>
	</c:otherwise>
</c:choose>
</p>
</form>
	<tiles:insert attribute="footer-nav" />
	</div>
</div>
<script language="javascript" src="scripts/_javascript.jsp"></script>
</body>
</html>