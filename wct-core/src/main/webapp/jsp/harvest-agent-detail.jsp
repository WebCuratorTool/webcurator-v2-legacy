<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="org.webcurator.ui.agent.command.ManageHarvestAgentCommand"%>
<%@page import="org.webcurator.ui.common.Constants"%>
<div id="resultsTable">
<table border="0" cellpadding="1" cellspacing="0">
	<tr>
		<td class="tableHeadLite">Name</td>
		<td class="tableHeadDark" colspan="2">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td colspan="2" align="center">Memory</td>
				</tr>
				<tr>
					<td align="center">Avail</td>		
					<td align="center">Used</td>
				</tr>
			</table>			
		</td>	
		<td class="tableHeadLite">Updated</td>
		<td class="tableHeadDark" colspan="2">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td colspan="2" align="center"><spring:message code="ui.label.harvestconfig.harvests"/></td>
				</tr>
				<tr>
					<td align="center">Max</td>		
					<td align="center">Current</td>
				</tr>
			</table>			
		</td>		
	</tr>	
	<tr>	
		<td class="subHeadLite" width="16%"><c:out value="${harvestAgent.name}"/></td>		
		<td class="subHeadDark" width="7%" align="center"><c:out value="${harvestAgent.memoryAvailableString}"/></td>
		<td class="subHeadLite" width="7%" align="center"><c:out value="${harvestAgent.memoryUsedString}"/></td>
		<td class="subHeadDark" width="7%"><wct:date value="${harvestAgent.lastUpdated}" type="fullDateTime"/></td>
		<td class="subHeadLite" width="7%" align="center"><c:out value="${harvestAgent.maxHarvests}"/></td>
		<td class="subHeadDark" width="7%" align="center"><c:out value="${harvestAgent.harvesterStatusCount}"/></td>
	</tr>
	<tr>			
		<td colspan="6" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="5" border="0" /></td>
	</tr>
</table>
<table border="0" cellpadding="1" cellspacing="0" width="100%">
	<tr>		
		<td class="tableHeadLite">Job</td>
		<td class="tableHeadDark">Job Status</td>		
		<td class="tableHeadLite" colspan="2">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td colspan="2" align="center">Average</td>
				</tr>
				<tr>
					<td align="center">KB/sec</td>		
					<td align="center">URI/sec</td>
				</tr>
			</table>			
		</td>		
		<td class="tableHeadDark" colspan="2">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td colspan="2" align="center">Current</td>
				</tr>
				<tr>
					<td align="center">KB/sec</td>		
					<td align="center">URI/sec</td>
				</tr>
			</table>			
		</td>	
		<td class="tableHeadLite" colspan="3">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td colspan="3" align="center">URLs</td>
				</tr>
				<tr>
					<td align="center">Saved</td>		
					<td align="center">Queued</td>
					<td align="center">Failed</td>	
				</tr>
			</table>			
		</td>			
		<td class="tableHeadDark">Data</td>	
		<td class="tableHeadLite">Elapsed Time</td>	
	</tr>	
	<tr>			
		<td colspan="11" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="5" border="0" /></td>
	</tr>
	<c:forEach items="${harvestAgent.harvesterStatus}" var="harvester">
	<tr>	
		<td class="subHeadLite" width="10%"><c:out value="${harvester.value.jobName}"/></td>		
		<td class="subHeadDark" width="9%"><c:out value="${harvester.value.status}"/></td>		
		<td class="subHeadLite" width="9%" align="center"><fmt:formatNumber value="${harvester.value.averageKBs}" pattern="#.##"/></td>
		<td class="subHeadDark" width="9%" align="center"><fmt:formatNumber value="${harvester.value.averageURIs}" pattern="#.##"/></td>
		<td class="subHeadLite" width="9%" align="center"><fmt:formatNumber value="${harvester.value.currentKBs}" pattern="#.##"/></td>
		<td class="subHeadDark" width="9%" align="center"><fmt:formatNumber value="${harvester.value.currentURIs}" pattern="#.##"/></td>
		<td class="subHeadLite" width="9%" align="center"><c:out value="${harvester.value.urlsDownloaded}"/></td>
		<td class="subHeadDark" width="9%" align="center"><c:out value="${harvester.value.urlsQueued}"/></td>
		<td class="subHeadLite" width="9%" align="center"><c:out value="${harvester.value.urlsFailed}"/></td>
		<td class="subHeadDark" width="9%" align="center"><c:out value="${harvester.value.dataDownloadedString}"/></td>
		<td class="subHeadLite" width="9%" align="center">
			<c:out value="${harvester.value.elapsedTimeString}"/>
			<input type="hidden" value="<c:out value="${harvester.value.harvesterName}"/>"/>
			<input type="hidden" value="<c:out value="${harvester.value.harvesterState}"/>"/>
		</td>
	</tr>
	<tr>			
		<td colspan="11" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="5" border="0" /></td>
	</tr>
	</c:forEach>	
	<form name="harvestAgentDone" method="POST" action="<%=Constants.CNTRL_MNG_AGENTS%>">
	<tr>
		<td colspan="11" align="center">
			<input type="hidden" name="<%=ManageHarvestAgentCommand.PARAM_ACTION%>" value="<%=ManageHarvestAgentCommand.ACTION_SUMMARY%>" />
			<img src="images/x.gif" width="1" height="15" border="0" /><br>
			<input type="image" src="images/generic-btn-done.gif" width="82" height="23" alt="Done" value="Done" />
		</td>
	</tr>	
	</form>
</table>
</div>