<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>

<script type="text/javascript">

  function makeReadOnlyPolitenessOptions(politenessValueSelected) {
    var readOnly = politenessValueSelected == 'Custom' ? false : true;
    //alert(readOnly);
    $("#delayFactor").prop('readonly', readOnly);
    $("#minDelayMs").prop('readonly', readOnly);
    $("#maxDelayMs").prop('readonly', readOnly);
    $("#respectCrawlDelayUpToSeconds").prop('readonly', readOnly);
    $("#maxPerHostBandwidthUsageKbSec").prop('readonly', readOnly);
  }

  function setPolitenessValues(option) {
    //alert(JSON.stringify(option));
    $("#delayFactor").val(option.delayFactor);
    $("#minDelayMs").val(option.minDelayMs);
    $("#maxDelayMs").val(option.maxDelayMs);
    $("#respectCrawlDelayUpToSeconds").val(option.respectCrawlDelayUpToSeconds);
    $("#maxPerHostBandwidthUsageKbSec").val(option.maxPerHostBandwidthUsageKbSec);
  }

  function setUserAgentResult(userAgent, contactURL) {
    //$("#userAgentResult").val(userAgent + " " + contactURL + ")");
    $("#userAgentResult").val(userAgent + contactURL + ")");
  }

  $(document).ready(function() {

    var politeOption = {
      delayFactor: "${politeOption.delayFactor}",
      minDelayMs: "${politeOption.minDelayMs}",
      maxDelayMs: "${politeOption.maxDelayMs}",
      respectCrawlDelayUpToSeconds: "${politeOption.respectCrawlDelayUpToSeconds}",
      maxPerHostBandwidthUsageKbSec: "${politeOption.maxPerHostBandwidthUsageKbSec}",
    };
    var mediumOption = {
      delayFactor: "${mediumOption.delayFactor}",
      minDelayMs: "${mediumOption.minDelayMs}",
      maxDelayMs: "${mediumOption.maxDelayMs}",
      respectCrawlDelayUpToSeconds: "${mediumOption.respectCrawlDelayUpToSeconds}",
      maxPerHostBandwidthUsageKbSec: "${mediumOption.maxPerHostBandwidthUsageKbSec}",
    };
    var aggressiveOption = {
      delayFactor: "${aggressiveOption.delayFactor}",
      minDelayMs: "${aggressiveOption.minDelayMs}",
      maxDelayMs: "${aggressiveOption.maxDelayMs}",
      respectCrawlDelayUpToSeconds: "${aggressiveOption.respectCrawlDelayUpToSeconds}",
      maxPerHostBandwidthUsageKbSec: "${aggressiveOption.maxPerHostBandwidthUsageKbSec}",
    };

    // make read only (or not) based on selection
    makeReadOnlyPolitenessOptions($('#politeness').value);


    $('#politeness').change(function() {
      var politenessValueSelected = this.value;
      //alert("Politeness selected: " + politenessValueSelected);
      // set the politeness values
      if (politenessValueSelected == 'Polite') {
        setPolitenessValues(politeOption);
      }
      if (politenessValueSelected == 'Medium') {
        setPolitenessValues(mediumOption);
      }
      if (politenessValueSelected == 'Aggressive') {
        setPolitenessValues(aggressiveOption);
      }
      // make read only (or not) politeness options
      makeReadOnlyPolitenessOptions(politenessValueSelected);
    });

    // set the H3 user agent
    setUserAgentResult($("#userAgent").val(), $("#contactURL").val());

    $("#contactURL").keyup(function() {
      setUserAgentResult($("#userAgent").val(), $(this).val());
    });

    $("#userAgent").keyup(function() {
      setUserAgentResult($(this).val(), $("#contactURL").val());
    });
  });

</script>

<table cellpadding="3" cellspacing="0" border="0">
  <tr>
    <td class="subBoxTextHdr">Contact URL:</td>
    <td class="subBoxText"><input size="80" type="text" name="contactURL" id="contactURL" value="<c:out value="${command.contactURL}"/>"><font color=red size=2>&nbsp;<strong>*</strong></font></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">User Agent Prefix:</td>
    <td class="subBoxText"><input size="80" type="text" name="userAgent" id="userAgent" value="<c:out value="${command.userAgent}"/>"><font color=red size=2>&nbsp;<strong>*</strong></font></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">User Agent used by Heritrix 3:</td>
    <td class="subBoxText"><input size="80" type="text" name="userAgentResult" id="userAgentResult" readonly value=""></td>
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
    <td class="subBoxTextHdr">Ignore Robots.txt:</td>
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

  <tr>
    <td class="subBoxTextHdr">Delay Factor:</td>
    <td class="subBoxText"><input size="20" type="number" step="0.01" min="0.00" name="delayFactor" id="delayFactor" value="<c:out value="${command.delayFactor}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Min Delay (ms):</td>
    <td class="subBoxText"><input size="20" type="number" min="0" name="minDelayMs" id="minDelayMs" value="<c:out value="${command.minDelayMs}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Max Delay (ms):</td>
    <td class="subBoxText"><input size="20" type="number" min="0" name="maxDelayMs" id="maxDelayMs" value="<c:out value="${command.maxDelayMs}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Respect Crawl Delay up to Seconds:</td>
    <td class="subBoxText"><input size="20" type="number" min="0" name="respectCrawlDelayUpToSeconds" id="respectCrawlDelayUpToSeconds" value="<c:out value="${command.respectCrawlDelayUpToSeconds}"/>"></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Max Per Host Bandwidth Usage (kb/s):</td>
    <td class="subBoxText"><input size="20" type="number" min="0" name="maxPerHostBandwidthUsageKbSec" id="maxPerHostBandwidthUsageKbSec" value="<c:out value="${command.maxPerHostBandwidthUsageKbSec}"/>"></td>
  </tr>

</table>