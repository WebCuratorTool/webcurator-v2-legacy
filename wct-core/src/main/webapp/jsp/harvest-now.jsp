<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="org.webcurator.ui.target.command.TargetInstanceCommand" %>
<%@page import="org.webcurator.ui.common.Constants" %>
<div id="annotationsBox">
<form name="harvestNow" method="POST" action="<%=Constants.CNTRL_HARVEST_NOW%>">
<table width="100%" cellpadding="3" cellspacing="0" border="0">
	<tr>
		<td class="annotationsLiteRow" colspan="4"><span class="subBoxTitle"><spring:message code="ui.label.harvestnow.harvestNow"/></span></td>
	</tr>
	<tr>
    <td class="annotationsLiteRow">Id:</td>
    <td class="annotationsLiteRow" colspan="3">
    	<c:out value="${instance.oid}"/>
    	<input type="hidden" name="<%=TargetInstanceCommand.PARAM_OID%>" value="<c:out value="${command.targetInstanceId}"/>"/>	
		<input type="hidden" name="<%=TargetInstanceCommand.PARAM_CMD%>" value="<%=TargetInstanceCommand.ACTION_HARVEST%>"/>				
		<input type="hidden" name="<%=TargetInstanceCommand.PARAM_AGENT%>" value=""/>
    </td>
  </tr>
  <tr>
    <td class="annotationsLiteRow"><spring:message code="ui.label.harvestnow.targetName"/>:</td>
    <td class="annotationsLiteRow" colspan="3">
    	<c:out value="${instance.target.name}"/>
    </td>
  </tr>
  <tr>
    <td class="annotationsLiteRow">Schedule:</td>
    <td class="annotationsLiteRow" colspan="3">
		<wct:date value="${instance.scheduledTime}" type="fullDateTime"/>
    </td>
  </tr>	
  <tr>
	<td class="annotationsLiteRow" colspan="4">&nbsp;</td>
  </tr>
	<c:choose>
		<c:when test="${empty harvestAgents}">
			<tr>
				<td colspan="4" class="annotationsLiteRow"><spring:message code="ui.label.harvestnow.noAgentsAvailable"/></td>
			</tr>
		</c:when>
		<c:otherwise>
			<tr>
				<td class="annotationsHeaderRow"><spring:message code="ui.label.harvestnow.harvestAgent"/></td>		
				<td class="annotationsHeaderRow" align="center"><spring:message code="ui.label.harvestnow.maxHarvests"/></td>	
				<td class="annotationsHeaderRow" align="center"><spring:message code="ui.label.harvestnow.currentHarvests"/></td>
				<td class="annotationsHeaderRow">&nbsp;</td>
			</tr>
			<c:forEach items="${harvestAgents}" var="agent">
			<tr>
				<td class="annotationsLiteRow"><c:out value="${agent.value.name}"/></td>		
				<td class="annotationsLiteRow" align="center"><c:out value="${agent.value.maxHarvests}"/></td>	
				<td class="annotationsLiteRow" align="center"><c:out value="${agent.value.harvesterStatusCount}"/></td>
				<c:choose>
				<c:when test="${agent.value.memoryWarning == false && agent.value.acceptTasks}">
					<td class="annotationsLiteRow"><input type="image" src="images/targets-btn-allocate.gif" onclick="javascript:document.harvestNow.<%=TargetInstanceCommand.PARAM_AGENT%>.value='<c:out value="${agent.value.name}"/>';"/></td>
				</c:when>
				<c:when test="${agent.value.memoryWarning == false && !agent.value.acceptTasks}">
					<td class="annotationsLiteRow">Agent manually set to stop accepting new tasks</td>
				</c:when>
				<c:otherwise>
					<td class="annotationsLiteRow">Memory threshold exceeded - Not accepting new harvests</td>
				</c:otherwise>
				</c:choose>
			</tr>	
			</c:forEach>			
		</c:otherwise>		
	</c:choose>
  <tr>
	<td class="annotationsLiteRow" colspan="4">&nbsp;</td>
  </tr>
  <tr>
	<td class="annotationsLiteRow" colspan="4" align="right">
	<input type="image" src="images/generic-btn-done.gif" onclick="javascript:document.harvestNow.<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_ABORT%>';" />
	</td>
  </tr>
</table>
</form>
</div>