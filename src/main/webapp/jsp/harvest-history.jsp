<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
  <tr>
    <th class="tableHead" width="10%">Id</th>
    <th class="tableHead" width="10%">Start Date</th>
    <th class="tableHead" width="10%">State</th>
	<th class="tableHead" width="10%" align="center">Data</th>
	<th class="tableHead" width="10%" align="center">URLs</th>
	<th class="tableHead" width="10%" align="center">URLs Failed</th>
	<th class="tableHead" width="10%" align="center">Elapsed Time</th>
	<th class="tableHead" width="10%" align="center">KB/s</th>
	<th class="tableHead" width="20%">Harvest Job Status</th>
  </tr>
  
  <c:forEach items="${history}" var="result">
  <tr <c:if test="${result.oid == ti_oid}">style="background-color: blue; color: white; font-weight:bold"</c:if>>
    <td width="10%"><a href="curator/target/target-instance.html?targetInstanceId=<c:out value="${result.oid}"/>&cmd=view&ti_oid=<c:out value="${ti_oid}"/>&harvestResultId=<c:out value="${harvestResultId}"/>"><c:out value="${result.oid}"/></a></td>
    <td width="10%"><wct:date type="fullDateTime" value="${result.startTime}"/></td>
    <td width="10%"><c:out value="${result.state}"/></td>    
    <td width="10%" align="center"><c:out value="${result.downloadSize}"/></td>
    <td width="10%" align="center"><c:out value="${result.urlsDownloaded}"/></td>
    <td width="10%" align="center"><c:out value="${result.urlsFailed}"/></td>
    <td width="10%" align="center"><c:out value="${result.elapsedTimeString}"/></td>
    <td width="10%" align="center"><c:out value="${result.kilobytesPerSecond}"/></td>
    <td width="20%"><c:out value="${result.harvestStatus}"/></td>
  </tr>
  </c:forEach>
</table>

<br/>
<center>
<a href="curator/target/quality-review-toc.html?targetInstanceOid=<c:out value="${sessionTargetInstance.oid}"/>&harvestResultId=<c:out value="${harvestResultId}"/>"><img src="images/generic-btn-done.gif" border="0"></a>
</center>
