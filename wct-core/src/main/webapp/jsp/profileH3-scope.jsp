<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<table cellpadding="3" cellspacing="0" border="0">
  <tr>
    <td class="subBoxTextHdr">Contact URL:</td>
    <td class="subBoxText"><input size="60" type="text" name="contactURL" value="<c:out value="${command.contactURL}"/>"><font color=red size=2>&nbsp;<strong>*</strong></font></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Document Limit:</td>
    <td class="subBoxText"><input size="20" type="number" min="0" name="documentLimit" value="<c:out value="${command.documentLimit}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Data Limit:</td>
    <td class="subBoxText">
      <input size="20" type="number" step="0.001" min="0.000" name="dataLimit" value="<c:out value="${command.dataLimit}"/>">
      <select name="dataLimitUnit" id="dataLimitUnit">
	    <c:forEach items="${profileDataUnits}" var="unit">
	      <option id="${unit}" ${command.dataLimitUnit eq unit ? 'SELECTED' : ''}>${unit}</option>
	    </c:forEach>
	  </select>
    </td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Time Limit:</td>
    <td class="subBoxText">
      <input size="20" type="number" step="0.001" min="0.000" name="timeLimit" value="<c:out value="${command.timeLimit}"/>">
      <select name="timeLimitUnit" id="timeLimitUnit">
	    <c:forEach items="${profileTimeUnits}" var="unit">
	      <option id="${unit}" ${command.timeLimitUnit eq unit ? 'SELECTED' : ''}>${unit}</option>
	    </c:forEach>
	  </select>
    </td>
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
    <td class="subBoxTextHdr" valign=top>Block URLs:</td>
    <td class="subBoxText"><textarea cols="80" rows="5" name="blockUrls"><c:out value="${command.blockUrls}"/></textarea></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr" valign=top>Include URLs:</td>
    <td class="subBoxText"><textarea cols="80" rows="5" name="includeUrls"><c:out value="${command.includeUrls}"/></textarea></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Max File Size:</td>
    <td class="subBoxText">
      <input size="20" type="number" step="0.001" min="0.000" name="maxFileSize" value="<c:out value="${command.maxFileSize}"/>">
      <select name="maxFileSizeUnit" id="maxFileSizeUnit">
	    <c:forEach items="${profileDataUnits}" var="unit">
	      <option id="${unit}" ${command.maxFileSizeUnit eq unit ? 'SELECTED' : ''}>${unit}</option>
	    </c:forEach>
	  </select>
    </td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Compress:</td>
    <td class="subBoxText"><input type="checkbox" name="compress" ${command.compress ? 'CHECKED':''}></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Prefix:</td>
    <td class="subBoxText"><input size="20" type="text" name="prefix" value="<c:out value="${command.prefix}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Politeness:</td>
    <td class="subBoxText">
	    <select name="politeness" id="politeness">
	        <c:forEach items="${politenessTypes}" var="type">
	            <option id="${type}" ${command.politeness eq type ? 'SELECTED' : ''}>${type}</option>
	        </c:forEach>
	    </select>
    </td>
  </tr>

</table>