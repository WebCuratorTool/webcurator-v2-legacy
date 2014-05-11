<%@page contentType="text/html; charset=UTF-8" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<div id="topBar"><div id="secondaryNav"><a href="<%= Constants.CNTRL_HOME%>">Home</a> | <a href="curator/target/queue.html?type=queue">Queue</a> | <a href="curator/target/queue.html?type=harvested">Harvested</a> | <a href="<tiles:getAsString name="page-help"/>" target="_blank">Help</a> | <a href="<%= Constants.CNTRL_LOGOUT%>">Logout</a>
<br/>User <%= org.webcurator.core.util.AuthUtil.getRemoteUser() %> is logged in.</div><a href="<%= Constants.CNTRL_HOME%>" accesskey="1"><img src="images/web-curator-tool-logo.gif" alt="Web Curator Tool" width="320" height="68" border="0" /></a></div>

