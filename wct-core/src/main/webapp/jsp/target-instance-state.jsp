<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.webcurator.ui.target.command.TargetInstanceCommand" %>
<table cellpadding="3" cellspacing="0" border="0">
  <input type="hidden" name="<%=TargetInstanceCommand.PARAM_OID%>" value="<c:out value="${command.targetInstanceId}"/>"/>
  <input type="hidden" name="<%=TargetInstanceCommand.PARAM_CMD%>" value="<c:out value="${command.cmd}"/>"/>
  <c:if test="${harvesterStatus != null}">
  <tr>
    <td class="subBoxTextHdr">WCT Application Version:</td>
    <c:choose>
    	<c:when test="${harvesterStatus.applicationVersion != null}">
		    <td class="subBoxText"><c:out value="${harvesterStatus.applicationVersion}"/></td>
    	</c:when>
    	<c:otherwise>
		    <td class="subBoxText"><c:out value="Unknown"/></td>
    	</c:otherwise>
    </c:choose>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Capture System:</td>
    <c:choose>
    	<c:when test="${harvesterStatus.heritrixVersion != null}">
		    <td class="subBoxText"><c:out value="${harvesterStatus.heritrixVersion}"/></td>
    	</c:when>
    	<c:otherwise>
		    <td class="subBoxText"><c:out value="Unknown"/></td>
    	</c:otherwise>
    </c:choose>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Harvest Server:</td>
    <td class="subBoxText"><c:out value="${instance.harvestServer}"/></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Job:</td>
    <td class="subBoxText"><c:out value="${harvesterStatus.jobName}"/></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Status:</td>
    <td class="subBoxText"><c:out value="${harvesterStatus.status}"/></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Average KB/s:</td>
    <td class="subBoxText"><fmt:formatNumber value="${harvesterStatus.averageKBs}" pattern="#.##"/></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Average URI/s:</td>
    <td class="subBoxText"><fmt:formatNumber value="${harvesterStatus.averageURIs}" pattern="#.##"/></td>
  </tr>
  <c:if test="${instance.state eq 'Running' || instance.state eq 'Paused'}">
  <tr>
    <td class="subBoxTextHdr">Current KB/s:</td>
    <td class="subBoxText"><fmt:formatNumber value="${harvesterStatus.currentKBs}" pattern="#.##"/></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Current URI/s:</td>
    <td class="subBoxText"><fmt:formatNumber value="${harvesterStatus.currentURIs}" pattern="#.##"/></td>
  </tr>
  </c:if>    
  <tr>
    <td class="subBoxTextHdr">URLs Downloaded:</td>
    <td class="subBoxText"><c:out value="${harvesterStatus.urlsDownloaded}"/></td>
  </tr>
    <c:if test="${instance.state eq 'Running' || instance.state eq 'Paused'}">
  <tr>
    <td class="subBoxTextHdr">URLs Queued:</td>
    <td class="subBoxText"><c:out value="${harvesterStatus.urlsQueued}"/></td>
  </tr>
  </c:if>
  <tr>
    <td class="subBoxTextHdr">URLs Failed:</td>
    <td class="subBoxText"><c:out value="${harvesterStatus.urlsFailed}"/></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Data Downloaded:</td>
    <td class="subBoxText"><c:out value="${harvesterStatus.dataDownloadedString}"/></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Elapsed Time:</td>
    <td class="subBoxText"><c:out value="${harvesterStatus.elapsedTimeString}"/></td>
  </tr>  
  <!--  
  <tr>
    <td class="subBoxTextHdr">Alerts:</td>
    <td class="subBoxText"><c:out value="${harvesterStatus.alertCount}"/></td>
  </tr> 
  --> 
  </c:if>
</table>