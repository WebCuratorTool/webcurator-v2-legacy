<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="org.webcurator.ui.target.command.TargetInstanceCommand" %>
<%@page import="org.webcurator.domain.model.core.TargetInstance" %>
<%@page import="org.webcurator.ui.common.Constants" %>
<%@page import="org.webcurator.domain.model.auth.Privilege" %>

<link rel="stylesheet" href="styles/blitzer/jquery-ui-1.10.2.custom.min.css" />
<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>
<script src="scripts/jquery-ui-1.10.2.custom.min.js" type="text/javascript"></script>
<script src="scripts/jquery.inview.js" type="text/javascript"></script>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<script>
  function setPageNumber(pageNumber) {
	document.filter2.<%= TargetInstanceCommand.PARAM_PAGE%>.value=pageNumber; 
	document.filter2.<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_SHOW_PAGE %>'; 
	document.filter2.submit();
  }

  function setPageSize(pageSize) {
	document.filter2.<%= TargetInstanceCommand.PARAM_PAGESIZE%>.value=pageSize; 
	document.filter2.<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_SHOW_PAGE %>'; 
	document.filter2.submit();
  }
  
  
  // the JQuery body onload function
  $(document).ready(function() {
 	  $('#dateEntryStart').datepicker({dateFormat: 'dd/mm/yy 00:00:00', changeMonth: true, changeYear: true, showOtherMonths: true, selectOtherMonths: true, showButtonPanel: true});
	  $('#dateEntryEnd').datepicker({dateFormat: 'dd/mm/yy 23:59:59', changeMonth: true, changeYear: true, showOtherMonths: true, selectOtherMonths: true, showButtonPanel: true});
  });

  function viewH3ScriptConsole(instanceOid) {
    //alert('Instance Oid ' + instanceOid);
    var url = '<%= basePath %>curator/target/h3ScriptConsole.html?targetInstanceOid=' + instanceOid;
    var winObj = window.open(url, 'h3ScriptConsole', 'menubar=no,scrollbars=yes,status=no,toolbar=no,resizable=yes,width=800,height=700', true);
    winObj.focus();
  }
</script>


<jsp:include page="include/useragencyfilter.jsp"/>
<form name="filter" method="POST" action="<c:out value="${action}"/>">
<c:if test="${command.queuePaused}">
	<table border="1px" BORDERCOLOR="#FF0000" bgcolor="#F4F0E7" id="messageBox">  	
		<tr valign="top">
			<td><font color="black" size="2"><strong>Harvesting of Queued and Scheduled Target Instances has been suspended.</strong></font></td>
		</tr>
	</table>
</c:if>
<div id="searchBox">
	<img src="images/search-box-top.gif" alt="Search" width="900" height="36" border="0" /><br/>
	<div id="searchBoxContent">
		<table cellpadding="0" cellspacing="3" border="0">
		<tr>
			<td class="searchBoxLabel">
				ID:<br />
				<input type="text" name="<%=TargetInstanceCommand.PARAM_SEARCH_OID%>" value="<c:out value="${command.searchOid}"/>" style="width:70px;" />
			</td>		
			<td class="searchBoxLabel">
				From:<br />
				<input type="text" id="dateEntryStart" name="<%=TargetInstanceCommand.PARAM_FROM%>" value="<wct:date type="fullDateTime" value="${command.from}"/>" maxlength="19" size="19" style="width:120px;" />
			</td>
			<td class="searchBoxLabel">
				To:<br />
				<input type="text" id="dateEntryEnd" name="<%=TargetInstanceCommand.PARAM_TO%>" value="<wct:date type="fullDateTime" value="${command.to}"/>" maxlength="19" size="19" style="width:120px;" />
			</td>
			<td class="searchBoxLabel">
				Agency:<br />
				<select name="<%=TargetInstanceCommand.PARAM_AGENCY%>" id="agency" onchange="javascript:onAgencyChange('agency', 'owner')">
				<c:choose>
					<c:when test="${command.agency eq ''}">
						<option value="" selected="selected"></option>
					</c:when>
					<c:otherwise>
						<option value=""></option>
					</c:otherwise>
				</c:choose>				
				<c:forEach items="${agencies}" var="a">
				<c:choose>
					<c:when test="${command.agency eq a.name}">
						<option value="<c:out value="${a.name}"/>" selected="selected"><c:out value="${a.name}"/></option>
					</c:when>
					<c:otherwise>
						<option value="<c:out value="${a.name}"/>"><c:out value="${a.name}"/></option>
					</c:otherwise>
				</c:choose>				
				</c:forEach>
			</select>
			</td>
			<td class="searchBoxLabel">
				Owner:<br />
				<select name="<%=TargetInstanceCommand.PARAM_OWNER%>" id="owner">
				<c:choose>
					<c:when test="${command.owner eq ''}">
						<option value="" selected="selected"></option>
					</c:when>
					<c:otherwise>
						<option value=""></option>
					</c:otherwise>
				</c:choose>				
				<c:forEach items="${owners}" var="o">
				<c:choose>
					<c:when test="${command.owner eq o.username}">
						<option value="<c:out value="${o.username}"/>" selected="selected"><c:out value="${o.firstname}"/>&nbsp;<c:out value="${o.lastname}"/></option>
					</c:when>
					<c:otherwise>
						<option value="<c:out value="${o.username}"/>"><c:out value="${o.firstname}"/>&nbsp;<c:out value="${o.lastname}"/></option>
					</c:otherwise>
				</c:choose>				
				</c:forEach>
			</select>
			</td>
			<td class="searchBoxLabel">
				Sort Order:<br />
				<select name="sortorder" id="sortorder">
					<option value="<%= TargetInstanceCommand.SORT_DEFAULT %>" ${command.sortorder eq 'default' ? 'SELECTED' : ''}>Default Ordering</option>
					<option value="<%= TargetInstanceCommand.SORT_NAME_ASC %>" ${command.sortorder eq 'nameasc' ? 'SELECTED' : ''}>Name (ascending)</option>
					<option value="<%= TargetInstanceCommand.SORT_NAME_DESC %>" ${command.sortorder eq 'namedesc' ? 'SELECTED' : ''}>Name (descending)</option>
					<option value="<%= TargetInstanceCommand.SORT_DATE_DESC %>" ${command.sortorder eq 'datedesc' ? 'SELECTED' : ''}>Most Recent First</option>
					<option value="<%= TargetInstanceCommand.SORT_DATE_ASC %>" ${command.sortorder eq 'dateasc' ? 'SELECTED' : ''}>Oldest First</option>
				</select>
			</td>
			<td class="searchBoxLabel">
			</td>
		</tr>
		</table>
		<table cellpadding="0" cellspacing="3" border="0" width="100%">
			<tr>								
				<td class="searchBoxLabel" valign="top">
					<table cellpadding="0" cellspacing="0" border="0">
						<tr>
					         <td class="searchBoxLabel" valign="top">Name:</td>
					    </tr>
					    <tr>
					         <td class="searchBoxLabel" valign="top"><input type="text" name="name" value="<c:out value="${command.name}" />" maxlength="255" /></td>
					    </tr>
					</table>
				</td>
				<td class="searchBoxLabel" valign="top">
					<table cellpadding="0" cellspacing="0" border="0">
						<tr>
					         <td class="searchBoxLabel" valign="top" align="left">Flagged&nbsp;Only:&nbsp;<input type="checkbox" name="<%=TargetInstanceCommand.PARAM_FLAGGED%>" ${command.flagged ? 'checked' : ''}/></td>
					    </tr>
					    <tr>
					         <td class="searchBoxLabel" valign="top" align="left">
								<authority:hasPrivilege privilege="<%= Privilege.MODIFY_TARGET %>" scope="<%= Privilege.SCOPE_NONE %>">
									Non-Display&nbsp;Only:&nbsp;<input type="checkbox" name="nondisplayonly" id="nondisplayonly" ${command.nondisplayonly ? 'checked' : ''}/>
								</authority:hasPrivilege>
					         </td>
					    </tr>
					</table>
				</td>
				<td class="searchBoxLabel" valign="top">State:</td>
				<td class="searchBoxLabel" valign="top">
					<table cellpadding="0" cellspacing="0" border="0">
					<tr>
				         <td class="searchBoxLabel" valign="top"><input type="checkbox" id="states_<%=TargetInstance.STATE_SCHEDULED%>" name="states" value="<%=TargetInstance.STATE_SCHEDULED%>" ${wct:containsObj(command.states, 'Scheduled') ? 'checked' : ''}>
		  					<label for="states_<%=TargetInstance.STATE_SCHEDULED%>">
							<%=TargetInstance.STATE_SCHEDULED%> 
						  	</label>
						 </td>
						 <td class="searchBoxLabel" valign="top"><input type="checkbox" id="states_<%=TargetInstance.STATE_QUEUED%>" name="states" value="<%=TargetInstance.STATE_QUEUED%>" ${wct:containsObj(command.states, 'Queued') ? 'checked' : ''}>
						  	<label for="states_<%=TargetInstance.STATE_QUEUED%>">
							<%=TargetInstance.STATE_QUEUED%> 
						  	</label>
						</td>
						<td class="searchBoxLabel" valign="top"><input type="checkbox" id="states_<%=TargetInstance.STATE_RUNNING%>" name="states" value="<%=TargetInstance.STATE_RUNNING%>" ${wct:containsObj(command.states, 'Running') ? 'checked' : ''}>
						  	<label for="states_<%=TargetInstance.STATE_RUNNING%>">
							<%=TargetInstance.STATE_RUNNING%> 
						  	</label>
						</td>
						<td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_PAUSED%>" name="states" value="<%=TargetInstance.STATE_PAUSED%>" ${wct:containsObj(command.states, 'Paused') ? 'checked' : ''}>
						  	<label for="states_<%=TargetInstance.STATE_PAUSED%>">
							<%=TargetInstance.STATE_PAUSED%> 
						  	</label>
						</td>	
						<td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_HARVESTED%>" name="states" value="<%=TargetInstance.STATE_HARVESTED%>" ${wct:containsObj(command.states, 'Harvested') ? 'checked' : ''}>
						  	<label for="states_<%=TargetInstance.STATE_HARVESTED%>">
							<%=TargetInstance.STATE_HARVESTED%> 
						  	</label>
						</td>
				    </tr>				    
				    <tr>				    	
						<td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_ABORTED%>" name="states" value="<%=TargetInstance.STATE_ABORTED%>" ${wct:containsObj(command.states, 'Aborted') ? 'checked' : ''}>
						  	<label for="states_<%=TargetInstance.STATE_ABORTED%>">
							<%=TargetInstance.STATE_ABORTED%> 
						  	</label>
						</td>
					    <td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_ENDORSED%>" name="states" value="<%=TargetInstance.STATE_ENDORSED%>" ${wct:containsObj(command.states, 'Endorsed') ? 'checked' : ''}>
						  	<label for="states_<%=TargetInstance.STATE_ENDORSED%>">
							<%=TargetInstance.STATE_ENDORSED%> 
						  	</label>
						</td>	
						<td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_REJECTED%>" name="states" value="<%=TargetInstance.STATE_REJECTED%>" ${wct:containsObj(command.states, 'Rejected') ? 'checked' : ''}>
						  	<label for="states_<%=TargetInstance.STATE_REJECTED%>">
							<%=TargetInstance.STATE_REJECTED%> 
						  	</label>
						</td>	
						<td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_ARCHIVED%>" name="states" value="<%=TargetInstance.STATE_ARCHIVED%>" ${wct:containsObj(command.states, 'Archived') ? 'checked' : ''}>
						  	<label for="states_<%=TargetInstance.STATE_ARCHIVED%>">
							<%=TargetInstance.STATE_ARCHIVED%> 
						  	</label>
						</td>
						<td class="searchBoxLabel" valign="top">
							<input type="checkbox" id="states_<%=TargetInstance.STATE_ARCHIVING%>" name="states" value="<%=TargetInstance.STATE_ARCHIVING%>" ${wct:containsObj(command.states, 'Archiving') ? 'checked' : ''}>
						  	<label for="states_<%=TargetInstance.STATE_ARCHIVING%>">
							<%=TargetInstance.STATE_ARCHIVING%> 
						  	</label>
						</td>						
				    </tr>
					</table>
				</td>				
				<td align="right" valign="middle">
					<table cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td>
					         <input type="image" src="images/search-box-btn.gif" alt="apply" width="82" height="24" border="0" />
		  					 <input type="hidden" name="<%=TargetInstanceCommand.PARAM_CMD%>" value="<%=TargetInstanceCommand.ACTION_FILTER%>" />
		  					</td>
					    </tr>
					    <tr>
							<td>					    
					         <input type="image" src="images/search-box-reset-btn.gif" alt="reset" width="82" height="24" border="0" onclick="javascript:document.filter.<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_RESET%>'" />
					        </td>
					    </tr>
					</table>					
				</td>
			</tr>
		</table>
	</div>
	<img src="images/search-box-btm.gif" alt="" width="900" height="12" border="0" /></div>
</form>
<div id="resultsTable">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td colspan="8"><span class="midtitleGrey">Results</span></td>
		</tr>
		<tr>
			<td class="tableHead"><img src="images/flag-icon-grey.gif"/></td>
			<td class="tableHead">Id</td>
			<td class="tableHead">Name</td>
			<td class="tableHead"><spring:message code="ui.label.queue.search.harvestDate"/></td>
			<td class="tableHead">State</td>
			<td class="tableHead">Owner</td>
			<td class="tableHead">Run Time</td>
			<td class="tableHead">Data Downloaded</td>
			<td class="tableHead" style="width:300px;">Action</td>
		</tr>
		<c:set var="count" scope="page" value="0"/>
	<c:forEach items="${targetInstances.list}" var="instance">
	<tr>
	<form name="targetInstance<c:out value="${count}"/>" method="POST" action="<%=Constants.CNTRL_TI%>">
		<td class="tableRowLite">						
			<c:if test="${instance.flagged}">
			<img src="images/flag-icon.gif" alt="Flagged"/>
			</c:if>
		</td>
		<td class="tableRowLite">						
			<c:if test="${instance.alertable == true}">
				<img src="images/warn.gif" alt="Annotations with Alerts!" width="9" height="9" border="0" />
	 		</c:if>
			<c:out value="${instance.oid}"/>
		</td>
		<c:choose> 
		<c:when test="${instance.display == false}" > 
		<td class="tableRowGreyedOut">
			<input type="hidden" name="targetInstanceId" value="<c:out value="${instance.oid}"/>"/>
			<input type="hidden" name="cmd" value="<%=TargetInstanceCommand.ACTION_VIEW%>"/>
			<c:out value="${instance.target.name}"/>
		</td>
		</c:when> 
		<c:otherwise> 
			<c:choose>
			<c:when test="${instance.firstFromTarget == true}" > 
				<td class="tableRowNewTI">
					<input type="hidden" name="targetInstanceId" value="<c:out value="${instance.oid}"/>"/>
					<input type="hidden" name="cmd" value="<%=TargetInstanceCommand.ACTION_VIEW%>"/>
					<c:out value="${instance.target.name}"/>
				</td>
			</c:when> 
			<c:otherwise> 
				<td class="tableRowLite">
					<input type="hidden" name="targetInstanceId" value="<c:out value="${instance.oid}"/>"/>
					<input type="hidden" name="cmd" value="<%=TargetInstanceCommand.ACTION_VIEW%>"/>
					<c:out value="${instance.target.name}"/>
				</td>
			</c:otherwise> 
			</c:choose>
		</c:otherwise> 
		</c:choose> 
		<td class="tableRowLite"><wct:date value="${instance.sortOrderDate}" type="fullDateTime"/></td>
		<td class="tableRowLite"><c:out value="${instance.state}"/></td>
		<td class="tableRowLite"><c:out value="${instance.owner.niceName}"/></td>
		<td class="tableRowLite"><c:out value="${instance.status.elapsedTimeString}"/>&nbsp;</td>
		<td class="tableRowLite"><c:out value="${instance.status.dataDownloadedString}"/>&nbsp;</td>
		<td class="tableRowLite">
			<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
			<input type="image" src="images/action-icon-view.gif" title="View" alt="click here to VIEW this item" width="15" height="19" border="0" hspace="3" onclick="javascript:document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_VIEW%>';"/>&nbsp;
			<authority:showControl ownedObject="${instance}" privileges='<%=Privilege.MANAGE_TARGET_INSTANCES + ";" + Privilege.MANAGE_WEB_HARVESTER%>' editMode="true">
				<authority:show>
				<c:if test="${instance.state ne 'Archiving'}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/action-icon-edit.gif" title="Edit" alt="click here to EDIT this item" width="18" height="18" border="0" onclick="javascript:document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_EDIT%>';"/>
				</c:if>
				<c:if test="${instance.state eq 'Running'}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/pause-icon.gif" title="Pause" alt="click here to Pause this item" width="21" height="20" border="0" onclick="javascript:document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_PAUSE%>'; document.targetInstance<c:out value="${count}"/>.action='<c:out value="${action}"/>';"/>			
				</c:if>
				<c:if test="${instance.state eq 'Paused'}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/resume-icon.gif" title="Resume" alt="click here to Resume this item" width="21" height="20" border="0" onclick="javascript:document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_RESUME%>'; document.targetInstance<c:out value="${count}"/>.action='<c:out value="${action}"/>';"/>			
				</c:if>
				<c:if test="${instance.state eq 'Running' || instance.state eq 'Paused' || instance.state eq 'Stopping'}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/abort-icon.gif" title="Abort" alt="click here to Abort this item" width="21" height="20" border="0" onclick="javascript:document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_ABORT%>'; document.targetInstance<c:out value="${count}"/>.action='<c:out value="${action}"/>';"/>			
				</c:if>
				<c:if test="${instance.state eq 'Running'}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/stop-icon.gif" title="Stop" alt="click here to Stop this item" width="21" height="20" border="0" onclick="javascript:document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_STOP%>'; document.targetInstance<c:out value="${count}"/>.action='<c:out value="${action}"/>';"/>			
				</c:if>
				<c:if test="${instance.state eq 'Running' && instance.profile.isHeritrix3Profile()}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
		    	    <a href="javascript:viewH3ScriptConsole(${instance.oid});" title="View"><img src="images/h3-script-console.png" title="H3 Script Console" alt="click here to Open H3 Script Console" width="21" height="20" border="0"></a>
				</c:if>
				</authority:show>
				<authority:dont>&nbsp;</authority:dont>
			</authority:showControl>
			<c:if test="${instance.state eq 'Scheduled'}">
				<authority:hasUserOwnedPriv ownedObject="${instance}" privilege="<%=Privilege.LAUNCH_TARGET_INSTANCE_IMMEDIATE%>" scope="<%=Privilege.SCOPE_AGENCY%>">
				<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				<a href="curator/target/ti-harvest-now.html?targetInstanceId=${instance.oid}"><img src="images/resume-icon.gif" title="Harvest Now" alt="click here to Harvest this item" width="21" height="20" border="0"></a>
				</authority:hasUserOwnedPriv>
				<authority:showControl ownedObject="${instance}" privileges='<%=Privilege.MANAGE_TARGET_INSTANCES + ";" + Privilege.MANAGE_WEB_HARVESTER%>' editMode="true">
				    <authority:show>
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/action-icon-delete.gif" title="Delete" alt="click here to DELETE this item" width="18" height="19" border="0" onclick="javascript:var proceed=confirm('Do you really want to delete this Target Instance?'); if (proceed) {document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_DELETE%>'; document.targetInstance<c:out value="${count}"/>.action='<c:out value="${action}"/>';} else { return false; }"/>			
					</authority:show>
				</authority:showControl>
			</c:if>
			<c:if test="${instance.state eq 'Endorsed'}">
				<authority:hasUserOwnedPriv ownedObject="${instance}" privilege="<%=Privilege.ARCHIVE_HARVEST%>" scope="<%=Privilege.SCOPE_OWNER%>">
				<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
	    		<a href="curator/archive/submit.html?instanceID=<c:out value="${instance.oid}"/>&harvestNumber=<c:out value="0"/>" onclick="return confirm('<spring:message code="ui.label.targetinstance.results.confirmSubmit" javaScriptEscape="true"/>');"><img src="images/action-icon-archive.gif" title="Archive" alt="click here to Archive this item" width="21" height="20" border="0"></a>    		
				</authority:hasUserOwnedPriv>
			</c:if>
			<c:if test="${instance.state eq 'Queued'}">
			<c:if test="${instance.status eq null}">
				<authority:showControl ownedObject="${instance}" privileges='<%=Privilege.MANAGE_TARGET_INSTANCES + ";" + Privilege.MANAGE_WEB_HARVESTER%>' editMode="true">
				    <authority:show>
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
					<input type="image" src="images/action-icon-delete.gif" title="Delete" alt="click here to DELETE this item" width="18" height="19" border="0" onclick="javascript:var proceed=confirm('Do you really want to delete this Target Instance?'); if (proceed) {document.targetInstance<c:out value="${count}"/>.cmd.value='<%=TargetInstanceCommand.ACTION_DELETE%>'; document.targetInstance<c:out value="${count}"/>.action='<c:out value="${action}"/>';} else { return false; }"/>
					</authority:show>
				</authority:showControl>
			</c:if>
			</c:if>
		</td>
		<c:set var="count" scope="page" value="${count + 1}"/>
	</form>
	</tr>
	<tr>
		<td colspan="8" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="5" border="0" /></td>
	</tr>		
	</c:forEach>		
	<tr>
		<form name="filter2" method="POST" action="<c:out value="${action}"/>">
		<td class="tableRowLite" colspan="7" align="center">
			<input type="hidden" name="<%=TargetInstanceCommand.PARAM_PAGE%>" value="<c:out value="${targetInstances.page}"/>" />			
			<input type="hidden" name="<%=TargetInstanceCommand.PARAM_PAGESIZE%>" value="<c:out value="${page.pageSize}"/>" />			
			<input type="hidden" name="<%=TargetInstanceCommand.PARAM_CMD%>" value="" />			
			
			<jsp:include page="pagination.jsp"/>

			</td>
		</form>		
		</tr>	
	</table>
</div>