<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@ page import="org.webcurator.ui.target.command.TargetInstanceCommand" %>

<input type="hidden" name="<%=TargetInstanceCommand.PARAM_OID%>" value="<c:out value="${command.targetInstanceId}"/>"/>
<input type="hidden" name="<%=TargetInstanceCommand.PARAM_CMD%>" value="<c:out value="${command.cmd}"/>"/>


<c:choose>
	<c:when test="${empty logList}">
	<table cellpadding="3" cellspacing="0" border="0">
  		<tr>
  			<td class="subBoxText" colspan="2">No log files are available.</td>
  		</tr>
  	</table>
  	</c:when>
  	<c:otherwise>
	<table cellpadding="3" cellspacing="0" border="0">
		<tr>
			<th width="20%" class="tableHead">Filename</th>
			<th width="20%" class="tableHead">Action</th>
			<th width="60%" class="tableHead">Size</th>
		</tr>
  		<c:forEach items="${logList}" var="logFile">
  		<tr>
  			<td class="subBoxText">
    			<c:out value="${logFile.name}"/>
  			</td>
  			<td class="subBoxText">
				<a href="curator/target/<c:out value="${logFile.viewer}"/>?targetInstanceOid=<c:out value="${instance.oid}"/>&logFileName=<c:out value="${logFile.name}"/>" target="_blank">View</a> |
	  			<a href="curator/target/<c:out value="${logFile.retriever}"/>?targetInstanceOid=<c:out value="${instance.oid}"/>&logFileName=<c:out value="${logFile.name}"/>">Download</a>
  			</td>
  			<td class="subBoxText">
    			<c:out value="${logFile.lengthString}"/>
  			</td>
  		</tr>
  		</c:forEach>
  	</table>
  	</c:otherwise>
</c:choose>

