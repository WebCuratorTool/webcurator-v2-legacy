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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">    
<HTML>
  <HEAD>
  	<base href="<%=basePath%>">   
	<link rel=stylesheet href="styles/stylesheet.css" type="text/css">
    <title><tiles:getAsString name="title"/></title>
	<meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">	
  </HEAD>

<body bgcolor="#ffffff" text="#000000" link="#023264" alink="#023264" vlink="#023264">
<tiles:insert attribute="header" />
<tiles:insert attribute="validation" />

<form id="tabForm" method="post" action="<tiles:getAsString name="controller"/>" <c:if test="${tabStatus.currentTab.formEncodingType != null}">enctype="${tabStatus.currentTab.formEncodingType}"</c:if>>


<input type="hidden" name="_tab_current_page" value="<c:out value="${tabStatus.currentTab.pageId}"/>">

<div class="tabs">
<c:choose>
  <c:when test="${tabStatus.enabled}">
    <c:forEach items="${tabs.tabs}" var="tab">
     <c:choose>
        <c:when test="${tab == tabStatus.currentTab}">
          <c:out value="${tab.title}"/>
        </c:when>
        <c:otherwise>    
	      <input type="submit" name="_tab_change" value="<c:out value="${tab.title}"/>">
	    </c:otherwise>
	  </c:choose>
    </c:forEach>
  </c:when>
  <c:otherwise>
    <c:forEach items="${tabs.tabs}" var="tab">
      <c:choose>
        <c:when test="${tab == tabStatus.currentTab}">
          <c:out value="${tab.title}"/>
        </c:when>
        <c:otherwise>
          <span class="disabled"><c:out value="${tab.title}"/></span>
        </c:otherwise>
      </c:choose>
      
    </c:forEach>
  </c:otherwise>
</c:choose>
</div>

<div style="background-color: #EEEEEE; padding: 1em; margin-bottom: 1em;">
<jsp:include page="<%= currentPage %>"/>
</div>

<input type="image" name="_tab_save" src="images/generic-btn-save.gif" alt="Save" width="82" height="23" border="0"><img src="images/x.gif" alt="" width="10" height="1" border="0" />
<input type="image" name="_tab_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0">

</form>

<tiles:insert attribute="footer" />
</body>
</html>