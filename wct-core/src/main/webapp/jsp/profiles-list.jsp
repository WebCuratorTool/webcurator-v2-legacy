<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@page import="org.webcurator.domain.model.auth.Privilege"%>
<%@page import="org.webcurator.ui.profiles.command.ProfileListCommand"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<script>
<!--
  String.prototype.trim = function(){
  	return (this.replace(/^[\s\xA0]+/, "").replace(/[\s\xA0]+$/, ""))
  }
	
  String.prototype.startsWith = function(str) {
  	return (this.match("^"+str)==str)
  }

  function viewProfile(profileOid) {
    var url = '<%= basePath %>curator/profiles/view.html?profileOid=' + profileOid;
    var winObj = window.open(url, 'profileView', 'menubar=no,scrollbars=yes,status=no,toolbar=no,resizable=yes,width=800,height=600', true);
    winObj.focus();
  }

  function importFile() {
   	if(document.getElementById("sourceFile").value.trim() != "") {
   	  document.forms.importForm.submit();
    }
    else {
       alert("You must specify a profile XML file name to import.");
       return false;
   	}
  }

//-->
</script>

<span class="midtitleGrey">Profile</span>
<div id="resultsTable">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<authority:hasPrivilege privilege="<%=Privilege.MANAGE_PROFILES%>" scope="<%=Privilege.SCOPE_AGENCY%>">
	<tr>
		<td colspan="4" valign="top">
			<fieldset>
			<legend class="smalltitleGrey">Import from file</legend>
			<form id="importForm" action="curator/profiles/list.html" method="POST" enctype="multipart/form-data">
				<table width="100%">
					<tr>
						<td valign="top">
							Select&nbsp;XML&nbsp;File:&nbsp;<input size="60" height="24" type="file" id="sourceFile" name="sourceFile" value=""/>
							<input type="hidden" id="actionCommand" name="actionCommand" value="<%=ProfileListCommand.ACTION_IMPORT %>">
						</td>
						<authority:hasPrivilege privilege="<%=Privilege.MANAGE_PROFILES%>" scope="<%=Privilege.SCOPE_ALL%>">
						<td>
							Import to agency:&nbsp;
				  			<select name="importAgency" id="importAgency">
								<c:forEach items="${agencies}" var="agency">
							  		<option value="${agency.oid}" ${command.defaultAgency eq agency.name ? 'SELECTED' : ''}>${agency.name}</option>
				  				</c:forEach>
			  				</select>
							Type to import:&nbsp;
				  			<select name="importType" id="importType"> 
								<c:forEach items="${types}" var="type">
							  		<option value="${type}" ${defaultType eq type ? 'SELECTED' : ''}>${type}</option>
				  				</c:forEach>
			  				</select>
						</td>
						</authority:hasPrivilege>
						<td valign="bottom">
							<img src="images/generic-btn-import-red.gif" alt="Import a Profile"  onclick="importFile();" style="cursor: pointer" border="0" />
						</td>
					</tr>
				</table>
			</form>
			</fieldset>
		</td>
		<td valign="top" colspan="3" align="right">
			<select name="createType" id="createType"> 
				<c:forEach items="${types}" var="type">
					<option value="${type}" ${defaultType eq type ? 'SELECTED' : ''}>${type}</option>
				</c:forEach>
			</select>
			<a href="curator/profiles/profiles.html"><img src="images/create-new-btn-red.gif" alt="Create a new Profile" width="82" height="24" border="0" align="right" vspace="3" /></a>
		</td>
	</tr>
	</authority:hasPrivilege>
	<tr>			
		<td colspan="6" valign="top">
			<form id="listForm" action="curator/profiles/list.html" method="POST">
			<table width="80%" cellpadding="0" cellspacing="0" border="0">
			  <tr>
				  <td>
					<input type="hidden" id="actionCommand" name="actionCommand" value="<%=ProfileListCommand.ACTION_FILTER %>">
				  	<input type="checkbox" name="showInactive" id="showInactive" ${command.showInactive ? 'CHECKED' : '' } onclick="document.getElementById('listForm').submit();"><label for="showInactive">Show Inactive Profiles</label>
				  </td>
				  <td>
				  Agency Filter:&nbsp;
				  	<select name="defaultAgency" id="defaultAgency" onchange="document.getElementById('listForm').submit();">
			  		<option id="" ${command.defaultAgency eq '' ? 'SELECTED' : ''}></option>
					<c:forEach items="${agencies}" var="agency">
				  		<option id="${agency.name}" ${command.defaultAgency eq agency.name ? 'SELECTED' : ''}>${agency.name}</option>
				  	</c:forEach>
				  	</select>
				  </td>
				  <td>
				  Type Filter:&nbsp;
				  	<select name="type" id="type" onchange="document.getElementById('listForm').submit();">
			  		<option id=""></option>
					<c:forEach items="${types}" var="type">
				  		<option id="${type}" ${defaultType eq type ? 'SELECTED' : ''}>${type}</option>
				  	</c:forEach>
				  	</select>
				  </td>
			  </tr>
		    </table>
			</form>
		</td>
	</tr>
	<tr>
		<td class="tableHead">Name</td>
		<td class="tableHead">Default</td>
		<td class="tableHead">Description</td>
		<td class="tableHead">Type</td>
		<td class="tableHead">Status</td>
		<td class="tableHead">Agency</td>
		<td class="tableHead">Action</td>
	</tr>
	<c:forEach items="${profiles}" var="profile" varStatus="status">
		<c:if test="${command.defaultAgency eq '' or command.defaultAgency eq profile.owningAgency.name}">
		<tr>
		    <td class="tableRowLite"><c:out value="${profile.name}"/></td>
		    <td class="tableRowLite">
			    <authority:showControl ownedObject="${profile}" privileges="<%= Privilege.MANAGE_PROFILES %>" editMode="${profile.status == 1}">
					<authority:show>
					<form id="changeDefaultForm_${profile.oid}" action="curator/profiles/make-default.html?profileOid=<c:out value="${profile.oid}"/>">
		      	    <input type="radio" name="profileOid" value="<c:out value="${profile.oid}"/>" ${profile.defaultProfile ? 'checked' : ''} onclick="document.getElementById('changeDefaultForm_${profile.oid}').submit();">
		      	    </form>
					</authority:show>
					<authority:dont>
					${profile.defaultProfile ? 'Yes' : 'No'}
					</authority:dont>
				</authority:showControl>
		    </td>
		    <td class="tableRowLite"><c:out value="${profile.description}"/></td>    
		    <td class="tableRowLite"><c:out value="${profile.harvesterType}"/></td>
		    <td class="tableRowLite"><spring:message code="profile.state_${profile.status}"/></td>
		    <td class="tableRowLite"><c:out value="${profile.owningAgency.name}"/></td>    
		    <td class="tableRowLite">
		    <form action="curator/profiles/delete.html" method="post"> 
		        <input type="hidden" name="profileOid" value="${profile.oid}">
		        <input type="hidden" id="actionCmd" name="actionCmd">
		        
		    	<authority:hasAgencyOwnedPriv ownedObject="${profile}" privilege="<%= Privilege.VIEW_PROFILES %>">		        
		    	<a href="javascript:viewProfile(${profile.oid});" title="View"><img src="images/action-icon-view.gif" border="0"></a>
			    </authority:hasAgencyOwnedPriv>		    	
		    
		    	<authority:hasAgencyOwnedPriv ownedObject="${profile}" privilege="<%=Privilege.MANAGE_PROFILES%>">
			    	<c:if test="${profile.status != 2}">
				    	<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				        <a href="curator/profiles/profiles.html?profileOid=<c:out value="${profile.oid}"/>&mode=edit" title="Edit">
				        <img src="images/action-icon-edit.gif" alt="click here to EDIT this item" width="18" height="18" border="0" />
				        </a>
			        </c:if>
			    </authority:hasAgencyOwnedPriv>
			    
				<%-- Must have both "create" profile and "view profile" to make a copy --%>
				<authority:hasPrivilege privilege="<%=Privilege.MANAGE_PROFILES%>" scope="<%=Privilege.SCOPE_AGENCY%>">
				    <authority:hasAgencyOwnedPriv ownedObject="${profile}" privilege="<%= Privilege.VIEW_PROFILES %>">		        				    	    
				        <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />		    				    
				        <a href="curator/profiles/profiles.html?profileOid=<c:out value="${profile.oid}"/>&mode=copy" title="Copy">
				        <img src="images/action-icon-copy.gif" alt="click here to COPY this item" border="0" />
				        </a>
					</authority:hasAgencyOwnedPriv>
		        </authority:hasPrivilege>
		        
		    	<authority:hasAgencyOwnedPriv ownedObject="${profile}" privilege="<%=Privilege.MANAGE_PROFILES%>">
			    	<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
			        <a href="curator/profiles/profiles.html?profileOid=<c:out value="${profile.oid}"/>&mode=export" title="Export">
			        <img src="images/action-icon-export.gif" alt="click here to Export this item as XML" width="18" height="18" border="0" />
			        </a>
			    </authority:hasAgencyOwnedPriv>

		    	<authority:hasAgencyOwnedPriv ownedObject="${profile}" privilege="<%=Privilege.MANAGE_PROFILES%>">
			    	<c:if test="${profile.status != 2}">
				    	<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				        <a href="curator/profiles/profiletargets.html?profileOid=<c:out value="${profile.oid}"/>" title="Transfer Targets">
				        <img src="images/action-icon-xfer-targets.gif" alt="click here to Transfer this profiles targets" width="25" height="20" border="0" />
				        </a>
			        </c:if>
			    </authority:hasAgencyOwnedPriv>

		        <authority:hasAgencyOwnedPriv ownedObject="${profile}" privilege="<%=Privilege.MANAGE_PROFILES%>">
		    	<c:if test="${profile.status != 2}">
			        <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />		    		        
		      	    <input type="image" src="images/action-icon-delete.gif" alt="click here to DELETE this item" title="Delete" onClick="return confirm('Do you really want to delete this profile?');">
		      	</c:if>
		      	</authority:hasAgencyOwnedPriv>

			</form>	    
						
		    </td>    
		  </tr>
		  </c:if>
	</c:forEach>
	</table>
</div>
