<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@page import="org.webcurator.domain.model.auth.Privilege"%>
<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>

<span class="midtitleGrey">WS Test</span>
<div id="resultsTable">
    <form id="harvestAgentSOAPClientForm" name="harvestAgentSOAPClientForm" method="post" action="curator/ws-test.html">
        <table>
            <tr>
                <td>Job Number:</td>
                <td><input type="text" name="jobNumber" id="jobNumber"/></td>
            </tr>
            <tr>
                <td>Job Number in response:</td>
                <td><c:out value="${harvestAgentStatusDTO.jobNumber}"/></td>
            </tr>
            <tr>
                <td>Status:</td>
                <td><c:out value="${harvestAgentStatusDTO.status}"/></td>
            </tr>
            <tr>
                <td>Message:</td>
                <td><c:out value="${harvestAgentStatusDTO.message}"/></td>
            </tr>
        </table>
        <input type="submit" value="Test WS"/>
    </form>
</div>
