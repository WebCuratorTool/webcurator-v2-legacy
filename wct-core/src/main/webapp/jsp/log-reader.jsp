<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.ui.target.command.LogReaderCommand" %>
<style>
	.hidden { display:none; }
	.logView {font-family: courier new;	font-size: 9pt; width: 100%; }
</style>
<c:if test="${messageText != ''}">
<font color="red"><c:out value="${messageText}"/></font>
</c:if>
<form name="logReader" method="POST" action="<%=Constants.CNTRL_LOG_READER%>">
<script>
function doFilterChange()
{
	document.logReader.<%=LogReaderCommand.PARAM_FILTER%>.value = "";
	
	<c:forEach items="${filterNames}" var="name">
	document.getElementById("<c:out value="${name.key}"/>").className = "hidden";
	</c:forEach>
	
	var id = document.getElementById("<%=LogReaderCommand.PARAM_FILTER_TYPE%>").value;
	document.getElementById(id).className = "";
	if(document.getElementById(id).innerHTML == "")
	{
	document.getElementById("<%=LogReaderCommand.PARAM_FILTER%>").className = "hidden";
	}
	else
	{
	document.getElementById("<%=LogReaderCommand.PARAM_FILTER%>").className = "";
	}
	
}

function doFilterKeyUp()
{
}
</script>
<h1>Log viewer: <c:out value="${command.logFileName}"/></h1>
<table border="0" width="100%" align="center">
	<tr>
		<td align="center" colspan="2">
		<textarea class="logView" readonly="readonly" rows="25" wrap=off><c:forEach items="${lines}" var="line"><c:out value="${line}"></c:out></c:forEach></textarea>  	  				
		</td>
	</tr>
	<tr>
		<td align="right">
		<c:choose>
		<c:when test="${command.showLineNumbers}">
			Show Line Numbers</td>
			<td align="left">
			<input type="checkbox" name="<%=LogReaderCommand.PARAM_SHOW_LINE_NUMBERS%>" checked>
		</c:when>
		<c:otherwise>
			Show Line Numbers</td>
			<td align="left">
			<input type="checkbox" name="<%=LogReaderCommand.PARAM_SHOW_LINE_NUMBERS%>">
		</c:otherwise>
		</c:choose>
		</td>
	</tr>
	<tr>
		<td align="right" width="25%">Number of lines to display</td>
		<td align="left">
		<input type="hidden" name="<%=LogReaderCommand.PARAM_OID%>" value="<c:out value="${command.targetInstanceOid}"/>" />
		<input type="hidden" name="<%=LogReaderCommand.PARAM_LOGFILE%>" value="<c:out value="${command.logFileName}"/>" />		
		<input type="hidden" name="<%=LogReaderCommand.PARAM_NUM_LINES%>" value="<c:out value="${command.numLines}"/>" />		
		<input type="text" name="<%=LogReaderCommand.PARAM_LINES%>" size="5" maxlength="5" value="<c:out value="${command.noOfLines}"/>" />
		</td>
	</tr>
	<tr>
		<td align="right">
		Filter Type
		</td>
		<td align="left">
		<select id="<%=LogReaderCommand.PARAM_FILTER_TYPE%>" name="<%=LogReaderCommand.PARAM_FILTER_TYPE%>" onchange="doFilterChange();" >
			<c:forEach items="${filterTypes}" var="type">
				<c:set var="selected" value=""/>
				<c:if test="${command.filterType eq type.key}">
					<c:set var="selected" value="selected"/>
				</c:if>
				<option value="<c:out value="${type.key}"/>" <c:out value="${selected}"/>><c:out value="${type.value}"/></option>
			</c:forEach>
		</select>
		</td>
	</tr>	
	<tr>
		<td align="right">
			<c:set var="hideFilter" value=""/>
			<c:forEach items="${filterNames}" var="name">
				<c:set var="showhide" value="hidden"/>
				<c:if test="${command.filterType eq name.key}">
					<c:set var="showhide" value=""/>
					<c:if test="${name.value eq ''}">
						<c:set var="hideFilter" value="hidden"/>
					</c:if>
				</c:if>
				<span id="<c:out value="${name.key}"/>" name="<c:out value="${name.key}"/>" class="<c:out value="${showhide}"/>"><c:out value="${name.value}"/></span>
			</c:forEach>
		</td>
		<td align="left">
		<input type="text" class="<c:out value="${hideFilter}" />" id="<%=LogReaderCommand.PARAM_FILTER%>" name="<%=LogReaderCommand.PARAM_FILTER%>" onKeyUp="doFilterKeyUp()" value="<c:out value="${command.filter}"/>" />
		</td>
	</tr>	
	<tr>
		<td align="center" colspan="2"><input type="image" src="images/generic-btn-apply.gif" title="Apply and view changes" alt="Apply and view changes" /></td>
	</tr>	
</table>
</form>