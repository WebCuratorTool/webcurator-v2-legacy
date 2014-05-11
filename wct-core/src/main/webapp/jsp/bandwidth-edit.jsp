<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.agent.command.BandwidthRestrictionsCommand" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div id="resultsTable">
<table border="0" cellspacing="5">
<form name="editBandwidth" method="POST" action="<c:out value="${action}"/>">
	<tr> 
		<td class="tableRowLite" colspan="3" align="left"><c:out value="${command.day}"/></td>
		<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_OID%>" value="<c:out value="${command.oid}"/>"/>
  		<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_DAY%>" value="<c:out value="${command.day}"/>"/>
  		<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_ACTION%>" value="<%=BandwidthRestrictionsCommand.ACTION_SAVE%>"/>
	</tr>	
	<tr> 
		<td class="tableRowLite">&nbsp;</td>
		<td class="tableRowLite">Start Time</td>
		<td class="tableRowLite"><input type="text" name="<%=BandwidthRestrictionsCommand.PARAM_START%>" value="<fmt:formatDate value="${command.start}" pattern="HH:mm:ss"/>" maxlength="8" size="8"/></td>  		
	</tr>
	<tr> 
		<td class="tableRowLite">&nbsp;</td>
		<td class="tableRowLite">End Time</td>
		<td class="tableRowLite"><input type="text" name="<%=BandwidthRestrictionsCommand.PARAM_END%>" value="<fmt:formatDate value="${command.end}" pattern="HH:mm:ss"/>" maxlength="8" size="8"/></td>
	</tr>
	<tr> 
		<td class="tableRowLite">&nbsp;</td>
		<td class="tableRowLite">Limit (KB/Sec)</td>
		<td class="tableRowLite"> <input type="text" name="<%=BandwidthRestrictionsCommand.PARAM_LIMIT%>" value="<c:out value="${command.limit}"/>" maxlength="8" size="8"/></td>
	</tr>	
	<tr> 
		<td class="tableRowLite">&nbsp;</td>
		<td class="tableRowLite">Harvest optimization enabled</td>
		<td class="tableRowLite">
			<!-- workaround for spring to notice when the checkbox value is false -->
			<input type="hidden" value="on" name="_<%=BandwidthRestrictionsCommand.PARAM_ALLOW_OPTIMIZE%>"> 
			<input type="checkbox" name="<%=BandwidthRestrictionsCommand.PARAM_ALLOW_OPTIMIZE%>" <c:if test="${command.allowOptimize==true}">checked="checked"</c:if>/>
		</td>
	</tr>	
	<tr> 
		<td class="tableRowLite" colspan="3">&nbsp;</td>
	</tr>	
	<tr > 
		<td class="tableRowLite" valign="top"><input type="image" src="images/generic-btn-save.gif"/></form></td>
		<td class="tableRowLite" valign="top"><form name="deleteBandwidth" method="POST" action="<c:out value="${action}"/>">
		<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_ACTION%>" value="<%=BandwidthRestrictionsCommand.ACTION_DELETE%>"/>
		<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_OID%>" value="<c:out value="${command.oid}"/>"/>
		<c:if test="${command.oid != null}"><input type="image" src="images/generic-btn-delete.gif" value="Delete"/></c:if></form></td>
		<td class="tableRowLite" valign="top"><form name="cancelBandwidth" method="get" action="<c:out value="${action}"/>">
		<input type="image" src="images/generic-btn-cancel.gif"/></form></td>
	</tr>	
</table>
</div>