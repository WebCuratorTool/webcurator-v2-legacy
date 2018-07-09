<%@page contentType="text/html; charset=UTF-8" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div id="primaryNavBar">
	<nobr>
		<img src="images/x.gif" width="5" height="38" border="0" />
		<img src="images/primary-left.gif" alt="" width="3" height="38" border="0" />
		<a href="in-tray.html" accesskey="2" onMouseOver="rollOn('intray')" onMouseOut="rollOff('intray')"><img src="images/in-tray-off.gif" alt="In Tray" width="50" height="38" border="0" name="intray" /></a>
		<img src="images/primary-mid.gif" alt="" width="2" height="38" border="0" />
		<a href="authorisations.html" accesskey="3" onMouseOver="rollOn('authorisations')" onMouseOut="rollOff('authorisations')"><img src="images/harvest-authorisations-off.gif" alt="Harvest Authorisations" width="133" height="38" border="0" name="authorisations" /></a>
		<img src="images/primary-mid.gif" alt="" width="2" height="38" border="0" />
		<a href="curator/target/search.html" accesskey="4"><img src="images/targets-on.gif" alt="Targets" width="53" height="38" border="0" name="targets" /></a>
		<img src="images/primary-mid.gif" alt="" width="2" height="38" border="0" />
		<a href="groups.html" accesskey="5" onMouseOver="rollOn('groups')" onMouseOut="rollOff('groups')"><img src="images/groups-off.gif" alt="Groups" width="51" height="38" border="0" name="groups" /></a>
		<img src="images/primary-mid.gif" alt="" width="2" height="38" border="0" />
		<a href="<%= Constants.CNTRL_TI_QUEUE%>" accesskey="6" onMouseOver="rollOn('instances')" onMouseOut="rollOff('instances')"><img src="images/target-instances-off.gif" alt="Target Instances" width="101" height="38" border="0" name="instances" /></a>
		<img src="images/primary-mid.gif" alt="" width="2" height="38" border="0" />
		<a href="reports.html" accesskey="7" onMouseOver="rollOn('reports')" onMouseOut="rollOff('reports')"><img src="images/reports-off.gif" alt="Reports" width="55" height="38" border="0" name="reports" /></a>
		<img src="images/primary-mid.gif" alt="" width="2" height="38" border="0" />
		<a href="management.html" accesskey="8" onMouseOver="rollOn('management')" onMouseOut="rollOff('management')"><img src="images/management-off.gif" alt="Management" width="82" height="38" border="0" name="management" /></a>
		<img src="images/primary-right.gif" alt="" width="3" height="38" border="0" />
	</nobr>
</div>