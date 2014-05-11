<%@ page import="org.webcurator.ui.target.command.ProfileCommand" %>
<%@ page import="org.webcurator.domain.model.core.AbstractTarget" %>
<%@ page import="org.webcurator.domain.model.core.TargetGroup" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>

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
    <td class="subBoxTextHdr">Base Profile:</td>
    <td class="subBoxText">    
		<authority:showControl ownedObject="${ownable}" privileges="${privlege}" editMode="${editMode && urlPrefix ne 'ti'}">
        	<authority:show>
		      <select name="profileOid">
		        <c:forEach items="${profiles}" var="profile">
		          <option value="<c:out value="${profile.oid}"/>" ${profile.oid == command.profileOid ? 'SELECTED' : '' }><c:out value="${profile.name}"/></option>
		        </c:forEach>
		      </select>
		    </authority:show>
		    <authority:dont>
		      <c:out value="${profileName}"/>
		      <input type="hidden" name="profileOid" value="<c:out value="${command.profileOid}"/>" />
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

<div id="annotationsBox">
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

<img src="images/x.gif" alt="" width="1" height="20" border="0" /><br />
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

<c:if test="${urlPrefix ne 'ti'}">
<table>
  <tr> 
    <td class="annotationsLiteRow">Profile Note</td>
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