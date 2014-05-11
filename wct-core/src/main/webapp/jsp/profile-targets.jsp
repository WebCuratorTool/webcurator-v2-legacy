<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@page import="org.webcurator.domain.model.auth.Privilege"%>
<%@page import="org.webcurator.ui.profiles.command.ProfileTargetsCommand"%>

<script language="javascript">
<!-- 
  function setPageNumber(pageNumber) {
    document.getElementById('pageNumber').value = pageNumber;
    document.getElementById('listForm').submit();
  }

  function setPageSize(pageSize) {
    document.getElementById('selectedPageSize').value = pageSize;
    document.getElementById('listForm').submit();
  }
  
  function setActionCmd(actionCmd) {
    document.getElementById('actionCommand').value = actionCmd;
  }
  function verifySelections(actionCmd) {
	// check we have selected some targets
	if (document.listForm.targetOids == null) {
		// no checkboxes present
		alert('There are no targets available to select.');
		return false;
	} else {
        var count=0;
	    // a single checkbox is not an array of checkboxes.
		if (typeOf(document.listForm.targetOids)=='object') {
			if (document.listForm.targetOids.checked==true) count++;
		} else {
			for(var i=0; i < document.listForm.targetOids.length; i++){
				if(document.listForm.targetOids[i].checked) {
					count ++;
				};
			};
		};
		if (count == 0) {
			alert('No targets selected.');
			return false;
		}
		// OK we have selected at least one target..
		if (document.getElementById('newProfileOid').value == document.getElementById('profileOid').value) {
			alert("You must select a new profile for the selected targets.");
			return false;
		} else {
			var proceed=false;
			if (document.listForm.cancelTargets.checked==true) {
				proceed=confirm('Do you really want to allocate the selected profile to these targets and also set them to cancelled?');
			} else {
				proceed=confirm('Do you really want to allocate the selected profile to these targets and leave the target status unchanged?');
			};
			if (proceed) {
			    document.getElementById('actionCommand').value = actionCmd;
				return true;
			} else {
				return false;
			};
		}		
	}
  }
  function checkAllTargets()
  {
	  if (document.listForm.targetOids != null) {
	      // a single checkbox is not an array of checkboxes.
	      if (typeOf(document.listForm.targetOids)=='object') {
	    	  document.listForm.targetOids.checked=true;
	      } else {
			  for (i = 0; i < document.listForm.targetOids.length; i++)
				  document.listForm.targetOids[i].checked = true ;
	      }
	  }
  }

  function unCheckAllTargets()
  {
	  if (document.listForm.targetOids != null) {
	      // a single checkbox is not an array of checkboxes.
	      if (typeOf(document.listForm.targetOids)=='object') {
	    	  document.listForm.targetOids.checked=false;
	      } else {
			  for (i = 0; i < document.listForm.targetOids.length; i++)
				  document.listForm.targetOids[i].checked = false ;
	      }
	  }
  }
  function typeOf(obj) {
	  if ( typeof(obj) == 'object' ) {
	    if (obj.length)
	      return 'array';
	    else
	      return 'object';
	  } else {
	    return typeof(obj);
	  }
  }
// -->
</script>

<span class="midtitleGrey">Profile Transfer - allocate selected targets to another profile</span>
<div id="resultsTable">
<form id="listForm" name="listForm" action="curator/profiles/profiletargets.html" method="POST">
<input type="hidden" id="pageNumber" name="pageNumber" value="${command.pageNumber}">
<input type="hidden" id="selectedPageSize" name="selectedPageSize" value="${page.pageSize}">
<input type="hidden" id="profileOid" name="profileOid" value="${command.profileOid}">
<input type="hidden" id="actionCommand" name="actionCommand" value="<%= ProfileTargetsCommand.ACTION_LIST %>">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<authority:hasPrivilege privilege="<%=Privilege.MANAGE_PROFILES%>" scope="<%=Privilege.SCOPE_AGENCY%>">
	<tr>
		<td colspan="2" valign="top">
			<fieldset>
			<legend class="smalltitleGrey">Current&nbsp;Profile&nbsp;Details</legend>
			<table width="100%">
				<tr>
					<td valign="top">
						Name:&nbsp;
					</td>
					<td>
						<c:out value="${currentprofile.name}"/>
					</td>
				</tr>
				<tr>
					<td valign="top">
						Description:&nbsp;
					</td>
					<td>
						<c:out value="${currentprofile.description}"/>
					</td>
				</tr>
			</table>
			</fieldset>
		</td>
		<td colspan="2" valign="top">
			<fieldset>
			<legend class="smalltitleGrey">New&nbsp;Profile&nbsp;Details</legend>
			<table width="100%">
				<tr>
					<td valign="top">
						Select&nbsp;New&nbsp;Profile:&nbsp;
						<select name="newProfileOid" id="newProfileOid">
						<c:forEach items="${newprofiles}" var="profile">
							<option value="${profile.oid}" ${currentprofile.name eq profile.name ? 'SELECTED' : ''}>${profile.name}</option>
						</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td>Cancel&nbsp;Selected&nbsp;Targets:&nbsp;<input type="checkbox" name="cancelTargets" ${ command.cancelTargets ? 'CHECKED' : ''}></td>
				</tr>
			</table>
			</fieldset>
		</td>
		<td colspan="2" align="right" valign="bottom">
			<input type="image" src="images/generic-btn-transfer.gif" alt="Transfer" width="82" height="24" border="0" onclick="return verifySelections('<%= ProfileTargetsCommand.ACTION_TRANSFER %>');"/>
			&nbsp;&nbsp;<input type="image" name="cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0" onclick="setActionCmd('<%= ProfileTargetsCommand.ACTION_CANCEL %>');">
		</td>
	</tr>
	<tr>
		<td class="smalltitleGrey" colspan="6">Targets&nbsp;allocated&nbsp;to&nbsp;profile:&nbsp;<c:out value="${currentprofile.name}"/></td>
	</tr>
	</authority:hasPrivilege>
	<tr>
		<td class="tableHead">ID</td>
		<td class="tableHead">Created</td>
		<td class="tableHead">Name</td>
		<td class="tableHead">Status</td>
		<td class="tableHead">Include?</td>
		<td class="tableHead" nowrap>
			<input type="button" name="Check_all" value="Select all" onClick="checkAllTargets();">&nbsp;<input type="button" name="Uncheck_all" value="De-select all" onClick="unCheckAllTargets();">
		</td>
	</tr>
	<c:forEach items="${page.list}" var="result">
	<tr>
		<td class="tableRowLite">
			<c:out value="${result.oid}"/>
		</td>
		<td class="tableRowLite">
			<wct:date value="${result.creationDate}" type="shortDate"/>
		</td>
		<td class="tableRowLite">
			<c:out value="${result.name}"/>
		</td>
		<td class="tableRowLite"><spring:message code="target.state_${result.state}"/></td>
        <td>
        	<input type="checkbox" name="targetOids" value="${result.oid}">
        </td>
	</tr>
	<tr>
		<td></td>
	</tr>
	<tr>			
		<td colspan="6" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="5" border="0" /></td>
	</tr>
	</c:forEach>
	<tr>			
		<td colspan="6" class="tableRowLite" align="center">
		<jsp:include page="pagination.jsp"/>
		</td>
	</tr>
	</table>
</form>
</div>
