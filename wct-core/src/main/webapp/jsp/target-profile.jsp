<%@ page import="org.webcurator.ui.target.command.ProfileCommand" %>
<%@ page import="org.webcurator.domain.model.core.AbstractTarget" %>
<%@ page import="org.webcurator.domain.model.core.TargetGroup" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>
<script src="scripts/codemirror/lib/codemirror.js"></script>
<link rel="stylesheet" href="scripts/codemirror/lib/codemirror.css"/>
<script src="scripts/codemirror/mode/xml/xml.js"></script>

<style>
.CodeMirror {
  height: 40em;
}
</style>


<script language="javascript">
<!--
  function removeCredential(credIndex) {
    document.getElementById('actionCmd').value="<%= ProfileCommand.ACTION_DELETE_CREDENTIALS %>";
    document.getElementById('credentialToRemove').value = credIndex;
  }
  
  function toggleOverride() {
    document.getElementById('actionCmd').value="toggleOverride";
    document.getElementById('tabForm').submit();
  
  }
  
// -->
</script>

<script type="text/javascript">

  function changeBaseProfileList(profilesList, harvesterTypeValueSelected, commandProfileOid) {
      // Change the base profile list to those profiles that match the selected harvester type.
      var matchingProfiles = [];
      $.each(profilesList, function(index, value) {
        if (value.harvesterType == harvesterTypeValueSelected) {
          matchingProfiles.push(value);
        }
      });
      $("#profileOid").html('');
      $(matchingProfiles).each(function(i) {
        if (matchingProfiles[i].oid == commandProfileOid) {
          $("#profileOid").append("<option value=" + matchingProfiles[i].oid + " SELECTED>" + matchingProfiles[i].name + "</option>");
        } else {
          $("#profileOid").append("<option value=" + matchingProfiles[i].oid + ">" + matchingProfiles[i].name + "</option>");
        }
      });
  }


function toggleProvideOverrides(profilesList, harvesterTypeValueSelected, onPageLoad=false) {

    var selectedProfile = getSelectedProfile(profilesList);

    if (harvesterTypeValueSelected == 'HERITRIX1') {
      $('#h1ProfileOverrides').show();
      $('#h1Credentials').show();
      $('#h3ProfileOverrides').hide();
      $('#editorDiv').hide();
      $('#overrideRawProfileCheckbox').hide();
    } else if (selectedProfile.imported == "true") {
      $('#h1ProfileOverrides').hide();
      $('#h3ProfileOverrides').hide();
      $('#h1Credentials').hide();
      $('#overrideRawProfileCheckbox').show();
      if ($('#overrideRawProfile').is(":checked")) {
        $('#editorDiv').show();
        if (!onPageLoad) {
          codeMirrorInstance.setValue(selectedProfile.rawProfile);
        }
      } else {
        $('#editorDiv').hide();
      }
    } else {
      $('#h3ProfileOverrides').show();
      $('#h1ProfileOverrides').hide();
      $('#h1Credentials').hide();
      $('#overrideRawProfileCheckbox').hide();
      $('#editorDiv').hide();
    }
  }


  $(document).ready(function() {
    var profilesList = [];
    <c:forEach items="${profiles}" var="prf">
      var jsProfile = {
        name: "${prf.name}",
        oid: "${prf.oid}",
        harvesterType: "${prf.harvesterType}",
        <c:if test="${prf.harvesterType eq 'HERITRIX3' && prf.imported eq 'true'}">rawProfile: "<spring:escapeBody javaScriptEscape="true">${prf.profile}</spring:escapeBody>",</c:if>
        imported: "${prf.imported}"
      };
      profilesList.push(jsProfile);
    </c:forEach>
    var selectedHarvesterTypeName = "<c:out value='${harvesterTypeName}' />";
    var commandProfileOid = "<c:out value='${command.profileOid}' />";
    $("#harvesterType option[value='" + selectedHarvesterTypeName + "']").prop('selected', true);
    changeBaseProfileList(profilesList, selectedHarvesterTypeName, commandProfileOid);
    toggleProvideOverrides(profilesList, selectedHarvesterTypeName, true);

    $('#harvesterType').change(function() {
      var harvesterTypeValueSelected = this.value;
      changeBaseProfileList(profilesList, harvesterTypeValueSelected, commandProfileOid);
      toggleProvideOverrides(profilesList, harvesterTypeValueSelected);
    });

    $('#profileOid').change(function() {
      var harvesterType = document.getElementById('harvesterType');
      var harvesterTypeValueSelected = harvesterType.options[harvesterType.selectedIndex].value;
      toggleProvideOverrides(profilesList, harvesterTypeValueSelected);
    });

    $('#overrideRawProfile').change(function() {
      var harvesterType = document.getElementById('harvesterType');
      var harvesterTypeValueSelected = harvesterType.options[harvesterType.selectedIndex].value;
      toggleProvideOverrides(profilesList, harvesterTypeValueSelected);
    });

  });

    function getSelectedProfile(profilesList) {
		<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${editMode && urlPrefix ne 'ti'}">
        	<authority:show>
        var oid = document.getElementById('profileOid');
        var prfIdx = profilesList.map(function(elem) { return elem.oid; }).indexOf(oid.options[oid.selectedIndex].value);
        return profilesList[prfIdx];
		    </authority:show>
		    <authority:dont>
		        <c:forEach items="${profiles}" var="prf">
		          <c:if test="${prf.oid == command.profileOid}">
        var jsProfile = {
          name: "${prf.name}",
          oid: "${prf.oid}",
          harvesterType: "${prf.harvesterType}",
          imported: "${prf.imported}"
        };
		          </c:if>
		        </c:forEach>
        return jsProfile;
		    </authority:dont>
		</authority:showControl>
    }

</script>



<% Object ownable = request.getAttribute("ownable");
   if(ownable instanceof AbstractTarget &&
      ((AbstractTarget)ownable).getObjectType() == AbstractTarget.TYPE_GROUP &&
      ((TargetGroup)ownable).getSipType() == TargetGroup.MANY_SIP) { %>
<p style="color: red;"><b><spring:message code="ui.label.target.profile.warning"/>:</b> <spring:message code="ui.label.target.profile.warningManySip"/></p>
<% } %>
<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
<span class="subBoxTitle">Base Profile</span>
<input type="hidden" id="actionCmd" name="actionCmd" value="submit">

<table cellpadding="3" cellspacing="0" border="0">
  <tr>
    <td class="subBoxTextHdr">Harvester Type:</td>
    <td class="subBoxText">
		<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${editMode && urlPrefix ne 'ti'}">
        	<authority:show>
		      <select name="harvesterType" id="harvesterType">
		        <c:forEach items="${harvesterTypes}" var="hType">
		          <option value="<c:out value="${hType}"/>"><c:out value="${hType}"/></option>
		        </c:forEach>
		      </select>
		    </authority:show>
		    <authority:dont>
		      <c:out value="${harvesterTypeName}"/>
		    </authority:dont>
		</authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Base Profile:</td>
    <td class="subBoxText">
		<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${editMode && urlPrefix ne 'ti'}">
        	<authority:show>
		      <select name="profileOid" id="profileOid">
		        <c:forEach items="${profiles}" var="profile">
		          <option value="<c:out value="${profile.oid}"/>" ${profile.oid == command.profileOid ? 'SELECTED' : '' }><c:out value="${profile.name}"/></option>
		        </c:forEach>
		      </select>
		    </authority:show>
		    <authority:dont>
		      <c:out value="${profileName}"/>
		      <input type="hidden" id="profileOid" name="profileOid" value="<c:out value="${command.profileOid}"/>" />
		    </authority:dont>
		</authority:showControl>
    </td>
  </tr>
  <c:if test="${urlPrefix eq 'ti'}">
  <tr>
    <td class="subBoxTextHdr">Override Target Overrides:</td>
    <td class="subBoxText">
		<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${editMode}">
        	<authority:show>
		      <input type="checkbox" name="overrideTarget" ${command.overrideTarget ? 'checked' : ''} onclick="toggleOverride();">
		    </authority:show>
		    <authority:dont>
		      ${command.overrideTarget ? 'Yes' : 'No'}
		      <input type="hidden" name="overrideTarget" value="${command.overrideTarget ? 'true' : 'false'}" />
		    </authority:dont>
		</authority:showControl>
    </td>
  </tr>
  </c:if>
</table>
<img src="images/x.gif" alt="" width="1" height="20" border="0" /><br />
<span class="subBoxTitle">Profile Overrides</span>

<c:set var="profileEditMode" value="${editMode && (urlPrefix ne 'ti' || command.overrideTarget)}"/>

<div id="h1ProfileOverrides">
	<table width="100%" cellpadding="3" cellspacing="0" border="0">
		<tr>
			<td class="annotationsHeaderRow">Profile Element</td>
			<td class="annotationsHeaderRow">Override Value</td>
			<td class="annotationsHeaderRow">Enable Override</td>
		</tr>

<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${profileEditMode}">
<authority:show>
    <tr>
      <td class="annotationsLiteRow">Robot Honouring Policy</td>
      <td class="annotationsLiteRow">
        <select name="robots">
          <option value="classic" ${command.robots eq 'classic' ? 'selected' : ''}>classic</option>
          <option value="ignore" ${command.robots eq 'ignore' ? 'selected' : ''}>ignore</option>
        </select>
      </td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideRobots" ${command.overrideRobots ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Maximum Hours</td>
      <td class="annotationsLiteRow"><input type="text" size="60" name="maxHours" value="<c:out value="${command.maxHours}"/>"/></td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideMaxHours" ${command.overrideMaxHours ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Maximum Kilobytes</td>
      <td class="annotationsLiteRow"><input type="text" size="60" name="maxBytesDownload" value="<c:out value="${command.maxBytesDownload}"/>"/></td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideMaxBytesDownload" ${command.overrideMaxBytesDownload ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Maximum Documents</td>
      <td class="annotationsLiteRow"><input type="text" size="60" name="maxDocuments" value="<c:out value="${command.maxDocuments}"/>"/></td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideMaxDocuments" ${command.overrideMaxDocuments ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Maximum Path Depth</td>
      <td class="annotationsLiteRow"><input type="text" size="60" name="maxPathDepth" value="<c:out value="${command.maxPathDepth}"/>"/></td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideMaxPathDepth" ${command.overrideMaxPathDepth ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Maximum Hops</td>
      <td class="annotationsLiteRow"><input type="text" size="60" name="maxHops" value="<c:out value="${command.maxHops}"/>"/></td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideMaxHops" ${command.overrideMaxHops ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow" valign="top">Exclude Filters</td>
      <td class="annotationsLiteRow" valign="top"><textarea name="excludeFilters" cols="62" rows="4"><c:out value="${command.excludeFilters}"/></textarea></td>
      <td class="annotationsLiteRow" valign="top"><input type="checkbox" name="overrideExcludeFilters" ${command.overrideExcludeFilters ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow" valign="top">Force Accept Filters</td>
      <td class="annotationsLiteRow" valign="top"><textarea name="forceAcceptFilters" cols="62" rows="4"><c:out value="${command.forceAcceptFilters}"/></textarea></td>
      <td class="annotationsLiteRow" valign="top"><input type="checkbox" name="overrideForceAcceptFilters" ${command.overrideForceAcceptFilters ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Excluded MIME Types</td>
      <td class="annotationsLiteRow"><input type="text" size="60" name="excludedMimeTypes" value="<c:out value="${command.excludedMimeTypes}"/>"/></td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideExcludedMimeTypes" ${command.overrideExcludedMimeTypes ? 'checked' : ''}/></td>
    </tr>
</authority:show>
<authority:dont>

    <tr>
      <td class="annotationsLiteRow">Robot Honouring Policy</td>
      <td class="annotationsLiteRow"><c:out value="${command.robots}"/></td>
      <td class="annotationsLiteRow">${command.overrideRobots ? 'Yes' : 'No'}</td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Maximum Hours</td>
      <td class="annotationsLiteRow"><c:out value="${command.maxHours}"/></td>
      <td class="annotationsLiteRow">${command.overrideMaxHours ? 'Yes' : 'No'}</td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Maximum Kilobytes</td>
      <td class="annotationsLiteRow"><c:out value="${command.maxBytesDownload}"/></td>
      <td class="annotationsLiteRow">${command.overrideMaxBytesDownload ? 'Yes' : 'No'}</td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Maximum Documents</td>
      <td class="annotationsLiteRow"><c:out value="${command.maxDocuments}"/></td>
      <td class="annotationsLiteRow">${command.overrideMaxDocuments ? 'Yes' : 'No'}</td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Maximum Path Depth</td>
      <td class="annotationsLiteRow"><c:out value="${command.maxPathDepth}"/></td>
      <td class="annotationsLiteRow">${command.overrideMaxPathDepth ? 'Yes' : 'No'}</td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Maximum Hops</td>
      <td class="annotationsLiteRow"><c:out value="${command.maxHops}"/></td>
      <td class="annotationsLiteRow">${command.overrideMaxHops ? 'Yes' : 'No'}</td>
    </tr>
    <tr>
      <td class="annotationsLiteRow" valign="top">Exclude Filters</td>
      <td class="annotationsLiteRow" valign="top"><pre><c:out value="${command.excludeFilters}"/></pre></td>
      <td class="annotationsLiteRow" valign="top">${command.overrideExcludeFilters ? 'Yes' : 'No'}</td>
    </tr>
    <tr>
      <td class="annotationsLiteRow" valign="top">Force Accept Filters</td>
      <td class="annotationsLiteRow" valign="top"><pre><c:out value="${command.forceAcceptFilters}"/></pre></td>
      <td class="annotationsLiteRow" valign="top">${command.overrideForceAcceptFilters ? 'Yes' : 'No'}</td>
    </tr>
    <tr>
      <td class="annotationsLiteRow">Excluded MIME Types</td>
      <td class="annotationsLiteRow"><c:out value="${command.excludedMimeTypes}"/></td>
      <td class="annotationsLiteRow">${command.overrideExcludedMimeTypes ? 'Yes' : 'No'}</td>
    </tr>

</authority:dont>
</authority:showControl>

</table>
</div>

<div id="h3ProfileOverrides">
	<table width="100%" cellpadding="3" cellspacing="0" border="0">
		<tr>
			<td class="annotationsHeaderRow">Profile Element</td>
			<td class="annotationsHeaderRow">Override Value</td>
			<td class="annotationsHeaderRow">Enable Override</td>
		</tr>

<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${profileEditMode}">
<authority:show>

    <tr>
      <td class="annotationsLiteRow">Document Limit</td>
      <td class="annotationsLiteRow"><input size="20" type="number" min="0" name="h3DocumentLimit" value="<c:out value="${command.h3DocumentLimit}"/>"/></td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideH3DocumentLimit" ${command.overrideH3DocumentLimit ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Data Limit</td>
      <td class="annotationsLiteRow">
        <input size="20" type="number" step="0.001" min="0.000" name="h3DataLimit" value="<c:out value="${command.h3DataLimit}"/>"/>
        <select name="h3DataLimitUnit" id="h3DataLimitUnit">
          <c:forEach items="${profileDataUnits}" var="unit">
	        <option id="${unit}" ${command.h3DataLimitUnit eq unit ? 'SELECTED' : ''}>${unit}</option>
	      </c:forEach>
	    </select>
      </td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideH3DataLimit" ${command.overrideH3DataLimit ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Time Limit</td>
      <td class="annotationsLiteRow">
        <input size="20" type="number" step="0.001" min="0.000" name="h3TimeLimit" value="<c:out value="${command.h3TimeLimit}"/>"/>
        <select name="h3TimeLimitUnit" id="h3TimeLimitUnit">
          <c:forEach items="${profileTimeUnits}" var="unit">
	        <option id="${unit}" ${command.h3TimeLimitUnit eq unit ? 'SELECTED' : ''}>${unit}</option>
	      </c:forEach>
	    </select>
      </td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideH3TimeLimit" ${command.overrideH3TimeLimit ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Max Path Depth</td>
      <td class="annotationsLiteRow"><input size="20" type="number" min="0" name="h3MaxPathDepth" value="<c:out value="${command.h3MaxPathDepth}"/>"/></td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideH3MaxPathDepth" ${command.overrideH3MaxPathDepth ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Max Hops</td>
      <td class="annotationsLiteRow"><input size="20" type="number" min="0" name="h3MaxHops" value="<c:out value="${command.h3MaxHops}"/>"/></td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideH3MaxHops" ${command.overrideH3MaxHops ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Max Transitive Hops</td>
      <td class="annotationsLiteRow"><input size="20" type="number" min="0" name="h3MaxTransitiveHops" value="<c:out value="${command.h3MaxTransitiveHops}"/>"/></td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideH3MaxTransitiveHops" ${command.overrideH3MaxTransitiveHops ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Ignore Robots</td>
      <td class="annotationsLiteRow">
        <select name="h3IgnoreRobots">
          <option value="ignore" ${command.h3IgnoreRobots eq 'ignore' ? 'selected' : ''}>ignore</option>
          <option value="obey" ${command.h3IgnoreRobots eq 'obey' ? 'selected' : ''}>obey</option>
        </select>
      </td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideH3IgnoreRobots" ${command.overrideH3IgnoreRobots ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow">Ignore Cookies</td>
      <td class="annotationsLiteRow"><input type="checkbox" name="h3IgnoreCookies" ${command.h3IgnoreCookies ? 'checked':''}></td>
      <td class="annotationsLiteRow"><input type="checkbox" name="overrideH3IgnoreCookies" ${command.overrideH3IgnoreCookies ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow" valign="top">Block URLs</td>
      <td class="annotationsLiteRow" valign="top"><textarea name="h3BlockedUrls" cols="80" rows="5"><c:out value="${command.h3BlockedUrls}"/></textarea></td>
      <td class="annotationsLiteRow" valign="top"><input type="checkbox" name="overrideH3BlockedUrls" ${command.overrideH3BlockedUrls ? 'checked' : ''}/></td>
    </tr>

    <tr>
      <td class="annotationsLiteRow" valign="top">Include URLs</td>
      <td class="annotationsLiteRow" valign="top"><textarea name="h3IncludedUrls" cols="80" rows="5"><c:out value="${command.h3IncludedUrls}"/></textarea></td>
      <td class="annotationsLiteRow" valign="top"><input type="checkbox" name="overrideH3IncludedUrls" ${command.overrideH3IncludedUrls ? 'checked' : ''}/></td>
    </tr>

</authority:show>
<authority:dont>

    <tr>
      <td class="annotationsLiteRow">Document Limit</td>
      <td class="annotationsLiteRow"><c:out value="${command.h3DocumentLimit}"/></td>
      <td class="annotationsLiteRow">${command.overrideH3DocumentLimit ? 'Yes' : 'No'}</td>
    </tr>
    <tr>
      <td class="annotationsLiteRow">Data Limit</td>
      <td class="annotationsLiteRow"><c:out value="${command.h3DataLimit}"/>&nbsp;<c:out value="${command.h3DataLimitUnit}"/></td>
      <td class="annotationsLiteRow">${command.overrideH3DataLimit ? 'Yes' : 'No'}</td>
    </tr>
    <tr>
      <td class="annotationsLiteRow">Time Limit</td>
      <td class="annotationsLiteRow"><c:out value="${command.h3TimeLimit}"/>&nbsp;<c:out value="${command.h3TimeLimitUnit}"/></td>
      <td class="annotationsLiteRow">${command.overrideH3TimeLimit ? 'Yes' : 'No'}</td>
    </tr>
    <tr>
      <td class="annotationsLiteRow">Max Path Depth</td>
      <td class="annotationsLiteRow"><c:out value="${command.h3MaxPathDepth}"/></td>
      <td class="annotationsLiteRow">${command.overrideH3MaxPathDepth ? 'Yes' : 'No'}</td>
    </tr>
    <tr>
      <td class="annotationsLiteRow">Max Hops</td>
      <td class="annotationsLiteRow"><c:out value="${command.h3MaxHops}"/></td>
      <td class="annotationsLiteRow">${command.overrideH3MaxHops ? 'Yes' : 'No'}</td>
    </tr>
    <tr>
      <td class="annotationsLiteRow">Max Transitive Hops</td>
      <td class="annotationsLiteRow"><c:out value="${command.h3MaxTransitiveHops}"/></td>
      <td class="annotationsLiteRow">${command.overrideH3MaxTransitiveHops ? 'Yes' : 'No'}</td>
    </tr>
    <tr>
      <td class="annotationsLiteRow">Ignore Robots</td>
      <td class="annotationsLiteRow"><c:out value="${command.h3IgnoreRobots}"/></td>
      <td class="annotationsLiteRow">${command.overrideH3IgnoreRobots ? 'Yes' : 'No'}</td>
    </tr>
    <tr>
      <td class="annotationsLiteRow">Ignore Cookies</td>
      <td class="annotationsLiteRow">${command.h3IgnoreCookies ? 'Yes' : 'No'}</td>
      <td class="annotationsLiteRow">${command.overrideH3IgnoreCookies ? 'Yes' : 'No'}</td>
    </tr>
    <tr>
      <td class="annotationsLiteRow">Block URLs</td>
      <td class="annotationsLiteRow"><pre><c:out value="${command.h3BlockedUrls}"/></pre></td>
      <td class="annotationsLiteRow">${command.overrideH3BlockedUrls ? 'Yes' : 'No'}</td>
    </tr>
    <tr>
      <td class="annotationsLiteRow">Include URLs</td>
      <td class="annotationsLiteRow"><pre><c:out value="${command.h3IncludedUrls}"/></pre></td>
      <td class="annotationsLiteRow">${command.overrideH3IncludedUrls ? 'Yes' : 'No'}</td>
    </tr>

</authority:dont>
</authority:showControl>

</table>
</div>

<div id="overrideRawProfileCheckbox">
<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${profileEditMode}">
<authority:show>
<table>
<tr>
<td class="subBoxTextHdr">
Override Imported Profile:
</td>
<td>
<input type="checkbox" id="overrideRawProfile" name="overrideRawProfile" ${command.overrideRawProfile ? 'checked' : ''}/>
</td>
</tr>
</table>
</authority:show>
</authority:showControl>
</div>
<div id="editorDiv">
<textarea id="rawProfile" name="rawProfile"/>
<c:out value="${command.rawProfile}"/>
</textarea>
</div>
<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${profileEditMode}">
<authority:show>
<script>
      codeMirrorInstance = CodeMirror.fromTextArea(document.getElementById("rawProfile"),
                                              {mode: "text/xml",
                                              lineNumbers: true,
                                              lineWrapping: true});
</script>
</authority:show>
<authority:dont>
<script>
      codeMirrorInstance = CodeMirror.fromTextArea(document.getElementById("rawProfile"),
                                              {mode: "text/xml",
                                              lineNumbers: true,
                                              lineWrapping: true,
                                              readOnly: true});
</script>
<input type="hidden" id="overrideRawProfile" name="overrideRawProfile" value="false"/>
</authority:dont>
</authority:showControl>

<img src="images/x.gif" alt="" width="1" height="20" border="0" /><br />
<div id="h1Credentials">
<span class="subBoxTitle">Credentials Override</span>

<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${profileEditMode}">
<authority:show>
<p><input type="checkbox" name="overrideCredentials" id="overrideCredentials" ${command.overrideCredentials ? 'checked' : ''} /><label for="overrideCredentials">Click to override the security credentials</label></p>
</authority:show>
<authority:dont>
  <c:choose>
    <c:when test="${command.overrideCredentials}"><p>Credentials are overridden</p></c:when>
    <c:otherwise><p>Credentials are not overridden</p></c:otherwise>
  </c:choose>
</authority:dont>
</authority:showControl>

<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${profileEditMode}">
<authority:show>
<p>
  <ul>
    <li><a href="curator/target/${urlPrefix}-basic-credentials.html?actionCmd=new">Add Basic Credentials</a></li>
    <li><a href="curator/target/${urlPrefix}-form-credentials.html?actionCmd=new">Add Form Credentials</a></li>
  </ul>
</p>
</authority:show>
</authority:showControl>


  <input type="hidden" id="credentialToRemove" name="credentialToRemove">

  <c:choose>
    <c:when test="${empty credentials}">
      <p>No credentials provided in the overrides</p>
    </c:when>
    <c:otherwise>
<table>
  <tr>
    <th>Domain</th>
    <th>Type</th>
    <th>Username</th>
    
<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${profileEditMode}">
<authority:show>    
    <th>&nbsp;</th>
</authority:show>
</authority:showControl>

  </tr>    
  <c:forEach items="${credentials}" var="credential" varStatus="i">
  <tr>
    <td><c:out value="${credential.credentialsDomain}"/></td>
    <td><c:out value="${credential.typeName}"/></td>
    <td><c:out value="${credential.username}"/></td>
<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${profileEditMode}">
<authority:show>    
    <td>
      <c:choose>
        <c:when test="${credential.typeName == 'Basic'}">
          <a href="curator/target/${urlPrefix}-basic-credentials.html?actionCmd=edit&listIndex=${i.count-1}"><img src="images/action-icon-edit.gif" height="18" width="18" border="0" /></a>
        </c:when>
        <c:otherwise>
          <a href="curator/target/${urlPrefix}-form-credentials.html?actionCmd=edit&listIndex=${i.count-1}"><img src="images/action-icon-edit.gif" height="18" width="18" border="0" /></a>        
        </c:otherwise>
      </c:choose>
		<img src="images/action-sep-line.gif" border="0" />      
		<input type="image" name="delete" title="Delete" alt="Delete" src="images/action-icon-delete.gif" height="19" width="18" onclick="removeCredential(${i.count-1});"/>
     </td>
</authority:show>
</authority:showControl> 
  </tr>
  </c:forEach>
</table>
</c:otherwise>
</c:choose>
</div>

<c:if test="${urlPrefix ne 'ti'}">
<table>
  <tr> 
    <td class="subBoxTextHdr">Profile Note:</td>
    <td class="annotationsLiteRow">
      <authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${profileEditMode}">
        <authority:show>
	      <textarea name="profileNote" rows="2" cols="100"><c:out value="${command.profileNote}"/></textarea>
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.profileNote}"/>
	    </authority:dont>
      </authority:showControl>
    </td>
  </tr>
</table>
</c:if>