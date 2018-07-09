<%@ page import="org.webcurator.ui.common.Constants" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

session.invalidate();
response.sendRedirect(basePath+Constants.CNTRL_HOME);
%>