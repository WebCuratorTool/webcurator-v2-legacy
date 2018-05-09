<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<table cellpadding="3" cellspacing="0" border="0">
  <tr>
    <td class="subBoxTextHdr">Contact URL:</td>
    <td class="subBoxText"><input size="60" type="text" name="contactURL" value="<c:out value="${command.contactURL}"/>"><font color=red size=2>&nbsp;<strong>*</strong></font></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Job Name:</td>
    <td class="subBoxText"><input size="60" type="text" name="jobName" value="<c:out value="${command.jobName}"/>"><font color=red size=2>&nbsp;<strong>*</strong></font></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Description:</td>
    <td class="subBoxText"><input size="60" type="text" name="description" value="<c:out value="${command.description}"/>"><font color=red size=2>&nbsp;<strong>*</strong></font></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">User Agent:</td>
    <td class="subBoxText"><input size="60" type="text" name="userAgent" value="<c:out value="${command.userAgent}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Document Limit:</td>
    <td class="subBoxText"><input size="20" type="number" min="0" name="documentLimit" value="<c:out value="${command.documentLimit}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Data Limit:</td>
    <td class="subBoxText"><input size="20" type="number" min="0" name="dataLimit" value="<c:out value="${command.dataLimit}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Time Limit:</td>
    <td class="subBoxText"><input size="20" type="number" min="0" name="timeLimit" value="<c:out value="${command.timeLimit}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Max Path Depth:</td>
    <td class="subBoxText"><input size="20" type="number" min="0" name="maxPathDepth" value="<c:out value="${command.maxPathDepth}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Max Hops:</td>
    <td class="subBoxText"><input size="20" type="number" min="0" name="maxHops" value="<c:out value="${command.maxHops}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Max Transitive Hops:</td>
    <td class="subBoxText"><input size="20" type="number" min="0" name="maxTransitiveHops" value="<c:out value="${command.maxTransitiveHops}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Ignore Robots:</td>
    <td class="subBoxText"><input type="checkbox" name="ignoreRobotsTxt" ${command.ignoreRobotsTxt ? 'CHECKED':''}></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Ignore Cookies:</td>
    <td class="subBoxText"><input type="checkbox" name="ignoreCookies" ${command.ignoreCookies ? 'CHECKED':''}></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Default Encoding:</td>
    <td class="subBoxText"><input size="20" type="text" name="defaultEncoding" value="<c:out value="${command.defaultEncoding}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Max File Size:</td>
    <td class="subBoxText"><input size="20" type="number" min="0" name="maxFileSize" value="<c:out value="${command.maxFileSize}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Compress:</td>
    <td class="subBoxText"><input type="checkbox" name="compress" ${command.compress ? 'CHECKED':''}></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Prefix:</td>
    <td class="subBoxText"><input size="20" type="text" name="prefix" value="<c:out value="${command.prefix}"/>"></td>
  </tr>

</table>