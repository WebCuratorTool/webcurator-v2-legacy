<jsp:directive.page import="org.webcurator.domain.model.auth.Privilege"/><%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@page import="org.webcurator.ui.common.Constants"%>
<%@page import="org.webcurator.ui.site.command.SitePermissionCommand" %>
<%@page import="org.webcurator.domain.model.core.Permission" %>

<link rel="stylesheet" href="styles/blitzer/jquery-ui-1.10.2.custom.min.css" />
<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>
<script src="scripts/jquery-ui-1.10.2.custom.min.js" type="text/javascript"></script>

<style>
	.hidden {
		display: none;
	}
</style>
<script language="JavaScript">
<!--
  function setActionCmd(actionCmd) {
    document.getElementById('actionCmd').value=actionCmd;
  }
  
  function deletePermissionExclusion(index) {
    document.getElementById('deleteExclusionIndex').value=index;
    setActionCmd("<%= SitePermissionCommand.ACTION_DELETE_EXCLUSION %>");
  }
 
  function setAction(index, str) {
    document.getElementById('noteIndex').value = index;
   	setActionCmd(str);
  }

  function setEdit(index) {
    document.getElementById('noteIndex').value = index;
    document.getElementById('btnAdd').className="hidden"; 
    document.getElementById('btnChange').className=""; 
    document.getElementById('note').value = document.getElementById('note'+index).innerHTML;

	var table = document.getElementById('tblAnnotations');
    for(i = 0; i <  table.childNodes.length; i++)
    {
    	var row = table.childNodes[i];
     	if((row.tagName == "TR" || row.tagName == "tr") && row.id != "header" && row.id != "empty")
    	{
		    for(j = 0; j <  row.childNodes.length; j++)
		    {
		    	var col = row.childNodes[j];
		     	if(col.tagName == "TD" || col.tagName == "td")
		    	{
		    		if(row.id == "row"+index)
		    		{
			    		col.className = "annotationsDarkRow";
		    		}
		    		else
		    		{
			    		col.className = "annotationsLiteRow";
		    		}
		    	}
		    } 
    	}
    } 
  }
  
//-->
</script>

<script type="text/javascript"> 
	$(document).ready(function() {
		$('.dateEntry').datepicker({dateFormat: 'dd/mm/yy', changeMonth: true, changeYear: true, showOtherMonths: true, selectOtherMonths: true, showButtonPanel: true});
	});
</script>
<c:set var="editMode" value="${permissionEditMode}"/>

<form name="sitePermission" method="POST" action="<%=Constants.CNTRL_SITE_PERMISSIONS%>">
<input type="hidden" name="selectedPermission" value="${command.identity}">
<input type="hidden" name="identity" value="${command.identity}">
<input type="hidden" name="deleteExclusionIndex" id="deleteExclusionIndex" value="">
<table>
  <tr>
    <td class="subBoxText">Authorising Agent:</td>
    <td class="subBoxText">
      <c:choose>
      	<c:when test="${editMode}">
	      <select name="authorisingAgent">
			<c:forEach items="${agents}" var="agent" varStatus="status">
			<option value="<c:out value="${agent.identity}"/>" ${agent == command.authorisingAgent ? 'SELECTED' : ''}><c:out value="${agent.name}"/></option>
			</c:forEach>
	      </select>
	    </c:when>
	    <c:otherwise>
	      <c:out value="${permission.authorisingAgent.name}"/>
	    </c:otherwise>
	  </c:choose>
    </td>
  </tr>
  <tr>
    <td class="subBoxText">Dates:</td>
    <td class="subBoxText">
      <c:choose>
      	<c:when test="${editMode}">
	      <input type="text" class="dateEntry" name="startDate" value="<wct:date value="${command.startDate}" type="fullDate"/>" maxlength="10"><font color=red size=2>&nbsp;<strong>*</strong>&nbsp;</font>
	       to&nbsp;
	      <input type="text" class="dateEntry" name="endDate" value="<wct:date value="${command.endDate}" type="fullDate"/>" maxlength="10"><font size="1">&nbsp;dd/mm/yyyy</font>    
	    </c:when>
	    <c:otherwise>
	      <c:choose>
	        <c:when test="${command.endDate != null}">
		      <wct:date value="${command.startDate}" type="fullDate"/> to <wct:date value="${command.endDate}" type="fullDate"/>
		    </c:when>
		    <c:otherwise>
		      <wct:date value="${command.startDate}" type="fullDate"/> (open ended)
		    </c:otherwise>
		  </c:choose>
	    </c:otherwise>
	  </c:choose>
    </td>
  </tr>  
  <tr>  
    <td class="subBoxText">Status:</td>
    <td class="subBoxText"> 
      <input type="hidden" name="originalStatus" value="${command.originalStatus}"/>
      <c:choose>
      	<c:when test="${editMode}">
	    	<c:choose>
	    		<c:when test="${command.originalStatus < 2}">
	    	<select name="status">
	    	  <c:forEach begin="0" end="1" var="i">
			    <option value="${i}" ${i == command.status ? 'SELECTED' : ''}><spring:message code="permission.state_${i}"/></option>
			  </c:forEach>
			  <authority:hasPrivilege privilege="<%=Privilege.CONFIRM_PERMISSION%>" scope="<%=Privilege.SCOPE_AGENCY%>">
			  <c:forEach begin="2" end="3" var="i">
			    <option value="${i}" ${i == command.status ? 'SELECTED' : ''}><spring:message code="permission.state_${i}"/></option>
			  </c:forEach>
			  </authority:hasPrivilege>
	    	</select>    		
	    		</c:when>    		
	    		<c:otherwise>
	    			<input type="hidden" name="status" value="${command.status}"/>
	    			<spring:message code="permission.state_${command.status}"/>
	    		</c:otherwise>
	    	</c:choose>
	    </c:when>
	    <c:otherwise>
	      <spring:message code="permission.state_${command.status}"/>
	    </c:otherwise>
	  </c:choose>
    </td>
  </tr>    
  <tr>
    <td class="subBoxText" valign="top">Auth.&nbsp;Agency&nbsp;Response:</td>
    <td class="subBoxText">
      <c:choose>
      	<c:when test="${editMode}">   
	      <textarea name="authResponse" rows="5" cols="80" style="width:480px"><c:out value="${command.authResponse}"/></textarea>
	    </c:when>
	    <c:otherwise>
	      <c:out value="${command.authResponse}"/>
	    </c:otherwise>
	  </c:choose>
    </td>
  </tr>
  <tr>
    <td class="subBoxText" valign="top">Special Restrictions:</td>
    <td class="subBoxText">
      <c:choose>
      	<c:when test="${editMode}">   
	      <textarea name="specialRequirements" rows="5" cols="80" style="width:480px"><c:out value="${command.specialRequirements}"/></textarea>
	    </c:when>
	    <c:otherwise>
	      <c:out value="${command.specialRequirements}"/>
	    </c:otherwise>
	  </c:choose>
    </td>
  </tr>
  <tr>
    <td class="subBoxText" valign="top">Copyright Statement:</td>
    <td class="subBoxText">
      <c:choose>
      	<c:when test="${editMode}">  
	      <textarea name="copyrightStatement" rows="5" cols="80" style="width:480px"><c:out value="${command.copyrightStatement}"/></textarea>
	    </c:when>
	    <c:otherwise>
	      <c:out value="${command.copyrightStatement}"/>
		</c:otherwise>
	  </c:choose>
    </td>
  </tr>
  <tr>
    <td class="subBoxText">Copyright URL:</td>
    <td class="subBoxText">
      <c:choose>
      	<c:when test="${editMode}">   
	      <input type="text" name="copyrightUrl" value="<c:out value="${command.copyrightUrl}"/>" style="width: 480px"/>
	    </c:when>
	    <c:otherwise>
	      <c:out value="${command.copyrightUrl}"/>
	    </c:otherwise>
	  </c:choose>
    </td>
  </tr>
  <tr>
    <td class="subBoxText">Access Status:</td>
    <td class="subBoxText">
      <c:choose>
      	<c:when test="${editMode}">   
	      <select name="accessStatus">
	        <c:forEach items="${accessStatusList}" var="access">
	          <option value="<c:out value="${access}"/>" ${command.accessStatus eq access ? 'selected' :''}><c:out value="${access}"/></option>
	        </c:forEach>
	      </select>
	    </c:when>
	    <c:otherwise>
	      <c:out value="${command.accessStatus}"/>
	    </c:otherwise>
	  </c:choose>
    </td>
  </tr>  
  <tr>
    <td class="subBoxText">Open Access Date:</td>
    <td class="subBoxText">
      <c:choose>
      	<c:when test="${editMode}"> 
	      <input type="text" class="dateEntry" name="openAccessDate" value="<wct:date value="${command.openAccessDate}" type="fullDate"/>" size="10" maxlength="10" />
	    </c:when>
	    <c:otherwise>
	      <wct:date value="${command.openAccessDate}" type="fullDate"/>
	    </c:otherwise>
	  </c:choose>
    </td>
  </tr>
  <tr>
    <td class="subBoxText">Quick Pick:</td>
    <td class="subBoxText">
      <c:choose>
      	<c:when test="${editMode}">    
	      <input type="checkbox" name="quickPick" ${command.quickPick ? 'CHECKED':''}>
	    </c:when>
	    <c:otherwise>
	      ${command.quickPick ? 'Yes' : 'No' }
	    </c:otherwise>
	  </c:choose>
    </td>
  </tr>
  <tr>
    <td class="subBoxText">Display Name:</td>
    <td class="subBoxText">
      <c:choose>
      	<c:when test="${editMode}">     
	      <input type="text" name="displayName" value="<c:out value="${command.displayName}"/>" maxlength="32" style="width:480px" />
	    </c:when>
	    <c:otherwise>
	      <c:out value="${command.displayName}"/>
	    </c:otherwise>
	  </c:choose>
    </td>
  </tr>  
  <tr>
    <td class="subBoxText" valign="top">Urls:</td>
    <td class="subBoxText" valign="top">
      <c:choose>
      	<c:when test="${editMode}">     
	      <c:choose>
	        <c:when test="${!empty urls}">
	          <c:forEach items="${urls}" var="url" varStatus="status">      
	            <label for="urls_${url.identity}"><input id="urls_${url.identity}" type="checkbox" name="urls" value="<c:out value="${url.identity}"/>" ${wct:containsObj(command.urls, url) ? 'CHECKED':''}><c:out value="${url.pattern}"/><font color=red size=2>&nbsp;<strong>*</strong>&nbsp;</font></label><br/>
	          </c:forEach>
	        </c:when>
		    <c:otherwise>
		      <font color=red><strong>No Url pattern(s) related to this permission.&nbsp;*</strong></font>
		    </c:otherwise>
		  </c:choose>
        </c:when>
	    <c:otherwise>
	      <c:choose>
	        <c:when test="${!empty command.urls}">
		      <c:forEach items="${command.urls}" var="url">
		        <c:out value="${url.pattern}"/><br/>
		      </c:forEach>
		    </c:when>
		    <c:otherwise>
		      No URL Pattern related to this permission.
		    </c:otherwise>
		  </c:choose>
	    </c:otherwise>
	  </c:choose>
    </td>
  </tr>
  
  <tr>
    <td class="subBoxText">File Reference:</td>
    <td class="subBoxText">
      <c:choose>
      	<c:when test="${editMode}">     
	      <input type="text" size="100" name="fileReference" value="<c:out value="${command.fileReference}"/>" maxlength="<%= Permission.MAX_FILE_REF_LENGTH %>" style="width:480px"/>
	    </c:when>
	    <c:otherwise>
	      <c:out value="${command.fileReference}"/>
	    </c:otherwise>
	  </c:choose>
    </td>
  </tr>    

<c:if test="${editMode}">  
  <tr>
    <td class="subBoxText">Assign Approval Task:</td>
    <td>
      <select name="createSeekPermissionTask">
        <option value="false" ${!command.createSeekPermissionTask ? 'selected' : ''}>No</option>
        <option value="true" ${command.createSeekPermissionTask ? 'selected' : ''}>Yes</option>
      </select>
    </td>
  </tr>  
</c:if>
 
</table>

<%-- Exclusions Section --%>
<div id="annotationsBox">
  <span class="subBoxTitle">Exclusions</span><br/>
  
  <c:if test="${editMode}">
  <table>
    <tr>
      <td>URL</td>
      <td>Reason</td>   
      <td>&nbsp;</td>   
    </tr>
    <tr>
      <td><input type="text" name="exclusionUrl" size="60"></td>
      <td><input type="text" name="exclusionReason" size="60"></td>
      <td><input type="image" src="images/subtabs-add-btn.gif" alt="Add" title="Add Exclusion" onclick="setActionCmd('<%= SitePermissionCommand.ACTION_ADD_EXCLUSION %>');">
    </tr>
  </table>
  </c:if>

  <table width="100%" cellpadding="3" cellspacing="0" border="0">
  	  <tr>
	    <td class="annotationsHeaderRow">URL</td>
	    <td class="annotationsHeaderRow">Reason</td>
	    <td class="annotationsHeaderRow">&nbsp;</td>
	  </tr>
  <c:choose>
	<c:when test="${empty permission.exclusions}">
	  <tr>
	    <td class="annotationsListRow" colspan="3">No exclusions have been defined.</td>
	  </tr>
	</c:when>
	<c:otherwise>
	  <c:forEach items="${permission.exclusions}" var="exclusion" varStatus="varStatus">
	  <tr>
	    <td class="annotationsLiteRow"><c:out value="${exclusion.url}"/></td>
	    <td class="annotationsLiteRow"><c:out value="${exclusion.reason}"/></td>	    
	    <td class="annotationsLiteRow">
	      <c:choose>
	        <c:when test="${editMode}">
	          <input type="image" src="images/action-icon-delete.gif" onclick="deletePermissionExclusion(${varStatus.count}-1)">
	        </c:when>
	        <c:otherwise>&nbsp;</c:otherwise>
	      </c:choose>
	    </td>
	  </tr>
	  </c:forEach>
	</c:otherwise>
  </c:choose>
  <table>
</div>

<div id="annotationsBox">
    <input id="noteIndex" name="<%= SitePermissionCommand.PARAM_NOTE_INDEX %>" value="-1" type="hidden">
    <span class="subBoxTitle">Annotations</span><br />
	<c:if test="${editMode}">
	  <textarea id="note" rows="<%= Constants.ANNOTATION_ROWS%>" cols="<%= Constants.ANNOTATION_COLS%>" name="<%= SitePermissionCommand.PARAM_NOTE %>"></textarea>
	   	  <input id="btnAdd" type="image" src="images/subtabs-add-btn.gif" alt="Add" width="49" height="23" border="0" hspace="5" align="absmiddle" onclick="setAction(-1,'<%= SitePermissionCommand.ACTION_ADD_NOTE%>');">
	   	  <input id="btnChange" class="hidden" type="image" src="images/subtabs-change-btn.gif" alt="Change" width="49" height="23" border="0" hspace="5" align="absmiddle" onclick="setAction(document.getElementById('noteIndex').value,'<%= SitePermissionCommand.ACTION_MODIFY_NOTE%>');">
	  <br />
	</c:if>
	<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
	<table width="100%" cellpadding="3" cellspacing="0" border="0">
	<tbody id="tblAnnotations">
	<tr id="header">
		<td class="annotationsHeaderRow">Date</td>
		<td class="annotationsHeaderRow">User</td>
		<td class="annotationsHeaderRow">Notes</td>
		<c:choose>
			<c:when test="${editMode}">
				<td class="annotationsHeaderRow">Action</td>
			</c:when>
			<c:otherwise>
				<td class="annotationsHeaderRow"></td>
			</c:otherwise>
		</c:choose>
	</tr>
	<c:choose>
		<c:when test="${empty permission.annotations}">
		  	<tr id="empty">
				<td colspan="4"><spring:message code="ui.label.common.noAnnotations"/></td>
			</tr>
		</c:when>
		<c:otherwise>
			<c:set var="count" scope="page" value="0"/>
			<c:forEach items="${permission.annotations}" var="anno">
	 			<tr id="row<c:out value="${count}"/>">
	    			<td class="annotationsLiteRow"><wct:date value="${anno.date}" type="longDateTime"/></td>
	    			<td class="annotationsLiteRow"><c:out value="${anno.user.niceName}"/></td>
	 				<td id="note<c:out value="${count}"/>" class="annotationsLiteRow"><c:out value="${anno.note}"/></td>					
	 				<td class="annotationsLiteRow">
						<c:if test="${editMode}">
	 						<c:if test="${anno.user.username eq command.username}">
								<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
								<img style="cursor:pointer;" title="edit" src="images/action-icon-edit.gif" alt="click here to Edit this item" border="0"
				 							onclick="javascript:setEdit(<c:out value="${count}"/>);"/>
								<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
								<input type="image" title="delete" src="images/action-icon-delete.gif" alt="click here to Delete this item" border="0"
				 							onclick="javascript:var proceed=confirm('Do you really want to delete this Annotation?'); if (proceed) {setAction(<c:out value="${count}"/>,'<%= SitePermissionCommand.ACTION_DELETE_NOTE%>');} else { return false; }" />
							</c:if>
						</c:if>
					</td>
	  			</tr>
	 			<c:set var="count" scope="page" value="${count + 1}"/>
	  		</c:forEach>					
		</c:otherwise>
	</c:choose>
	</table>
</div>				

<input type="hidden" name="actionCmd" id="actionCmd" value="" />
<p align="center">
<c:if test="${editMode}">
<input type="image" name="_save_perm" src="images/generic-btn-save.gif" alt="Save" width="82" height="23" border="0" onclick="javascript:setActionCmd('<%= SitePermissionCommand.ACTION_SAVE %>');"><img src="images/x.gif" alt="" width="10" height="1" border="0" />
</c:if>
<input type="image" name="_cancel_perm" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0" onclick="setActionCmd('<%= SitePermissionCommand.ACTION_CANCEL %>');">
</p>

</form>

