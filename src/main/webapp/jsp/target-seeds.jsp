<%@page import="org.webcurator.ui.target.command.SeedsCommand" %>
<%@page import="org.webcurator.domain.model.auth.Privilege" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>

<script type="text/javascript">

  function selectSeed(seedId) {
	document.getElementById("selectedSeed").value = seedId;
	return true;
  }
  
  function selectPermission(permissionId) {
	document.getElementById("selectedPermission").value = permissionId;
	return true;
  }  
  
  function setActionCmd(val) {
  	document.getElementById('actionCmd').value = val;
  }
  
  function removeSeed(seedId) {
  	selectSeed(seedId);
  	setActionCommand('<%= SeedsCommand.ACTION_REMOVE %>');
  }

  function removeSeed(seedId) {
  	selectSeed(seedId);
  	setActionCmd('<%= SeedsCommand.ACTION_REMOVE %>');
  }  
  
  function addSeed() {
  	setActionCmd('<%= SeedsCommand.ACTION_ADD %>');
  }
  
  function unlinkSeed(seedId, permissionId) {
    selectSeed(seedId);
    selectPermission(permissionId);
    setActionCmd('<%= SeedsCommand.ACTION_UNLINK %>');
  }
  
  function togglePrimary(seedId) {
    selectSeed(seedId);
  	setActionCmd('<%= SeedsCommand.ACTION_TOGGLE_PRIMARY %>');
  }
  
  function linkNewPermissions(seedId) {
    selectSeed(seedId);
    setActionCmd('<%= SeedsCommand.ACTION_LINK_NEW %>');
  }
  
  function preview(permissionId) {
    var url = '<%= basePath %>curator/target/permission-popup.html?permissionOid=' + permissionId;
    var winObj = window.open(url, 'permissionPreview', 'menubar=no,scrollbars=yes,status=no,toolbar=no,resizable=yes,width=500,height=400', true);
    winObj.focus();
  }
  
  function selectAll(seedCount)
  {
	var checkSelectAll = document.getElementById("chkSelectAll");
	for(var i = 1; i <= seedCount; i++)
	{
		var check = document.getElementById("chkSelect"+i);
		if(check && checkSelectAll)
		{
			check.checked = checkSelectAll.checked;
		}
	}
  }
  
  function unlinkSelected()
  {
  	setActionCmd('<%= SeedsCommand.ACTION_UNLINK_SELECTED %>');
  }
  
  function removeSelected()
  {
  	setActionCmd('<%= SeedsCommand.ACTION_REMOVE_SELECTED %>');
  }
  
  function linkSelected()
  {
  	setActionCmd('<%= SeedsCommand.ACTION_LINK_SELECTED %>');
  }
  
  $(document).ready(function() {
  	$(".seedNameEdit").hide()
  });
  
  function editName(id) {
  	cancelEdit()
  	//Reset any previously entered value
	name = $("#seedNameLabelDiv"+id + " a").attr("href")
	$("#seedNameInput"+id).val(name)
	
  	$("#seedNameInputDiv"+id).show()
  	$("#seedNameLabelDiv"+id).hide()
  }
  
  function saveSeedName(id, oid) {
    $("#updatedNameSeedId").val(oid)
    value = $("#seedNameInput"+id).val()
    $("#updatedNameSeedValue").val(value)
  	setActionCmd('<%= SeedsCommand.ACTION_SET_NAME %>');
  }
  
  function cancelEdit() {
	$(".seedNameEdit").hide()
	$(".seedNameView").show()
  }
  
</script>
<c:set var="editMode" value="${targetEditorContext.editMode}"/>
	<authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
        <authority:show>
<table cellpadding="3" cellspacing="0" border="0">	
	<input type="hidden" id="selectedSeed" name="selectedSeed"/>
	<input type="hidden" id="selectedPermission" name="selectedPermission"/>
	<input type="hidden" id="updatedNameSeedId" name="updatedNameSeedId"/>
	<input type="hidden" id="updatedNameSeedValue" name="updatedNameSeedValue"/>
	<input type="hidden" id="actionCmd" name="actionCmd"/>
  <tr>
    <td class="subBoxTextHdr">Seed:</td>
    <td class="subBoxText"><input type="text" name="seed" value="<c:out value="${ command.seed}"/>" style="width:480px"></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Authorisation:</td>
    <td class="subBoxText">
		<select name="permissionMappingOption">
		  <option value="<%= SeedsCommand.PERM_MAPPING_AUTO %>">Auto</option>
		  <option value="<%= SeedsCommand.PERM_MAPPING_NONE %>">Add Later</option>
		  <c:forEach items="${quickPicks}" var="quickPick">
		  <option value="${quickPick.oid}"><c:out value="${quickPick.displayName}"/></option>
		  </c:forEach>
		</select>
	</td>
  </tr>
</table>	
<div style="float:right"><input type="image" src="images/generic-btn-import.gif"  alt="Import" onclick="setActionCmd('<%= SeedsCommand.ACTION_START_IMPORT %>');"></div>
<input type="image" name="_new" src="images/subtabs-link-btn.gif" alt="Link" width="49" height="23" border="0" hspace="5" align="absmiddle" onclick="addSeed();">
	    </authority:show>
	  </authority:showControl>    

<div id="annotationsBox">
 <img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
	<table width="100%" cellpadding="3" cellspacing="0" border="0">
		<tr>
			<td class="annotationsHeaderRow">&nbsp;</td>
			<td class="annotationsHeaderRow">Seed</td>
			<td class="annotationsHeaderRow">Primary</td>
			<td class="annotationsHeaderRow"><spring:message code="ui.label.target.seeds.harvestAuth"/></td>
			<td class="annotationsHeaderRow">Auth Agent</td>
			<td class="annotationsHeaderRow">Start</td>
			<td class="annotationsHeaderRow">End</td>
			<td class="annotationsHeaderRow">Status</td>
			<td class="annotationsHeaderRow">Action</td>
		</tr>
		<c:forEach items="${seeds}" var="seed" varStatus="i">
  			<c:set var="style">${i.count % 2 == 0 ? 'style="background-color: #DDDDEE;"' : 'style="background-color: #CCCCEE;"'}</c:set>
  			<c:set var="seedCount" value="${i.count}"/>
  			<c:choose>
    			<c:when test="${empty seed.permissions}">
    			<tr id="trSeeds" ${style}>
	    			<td class="annotationsLiteRow"><input id="<c:out value="chkSelect${i.count}"/>" name="<c:out value="chkSelect${seed.identity}"/>" type="checkbox" /></td>
	    			<td class="annotationsLiteRow">
	    				<div class="seedNameView" id="<c:out value="seedNameLabelDiv${i.count}"/>">
		    				<a href="<c:out value="${seed.seed}"/>" target="_blank"><c:out value="${seed.seed}"/>**</a>
	    					<input style="float:right;" type="image" src="images/action-icon-edit.gif"  alt="Edit name" onclick="editName('<c:out value="${i.count}"/>');return false;">			    			
	    				</div>
		    			<div class="seedNameEdit" id="<c:out value="seedNameInputDiv${i.count}"/>">
							<input type="text" name="<c:out value="seedNameInput${i.count}"/>" value="<c:out value="${seed.seed}"/>"/>			    			
		    			</div>
	    			</td>
	    			<td class="annotationsLiteRow"> 
						<authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
	        			<authority:show>
	        			<c:choose>
	        				<c:when test="${allowMultiplePrimarySeeds == true}">
	          					<input type="checkbox" ${ seed.primary ? 'checked' : ''} onclick="togglePrimary('<c:out value="${seed.identity}"/>'); document.tabForm.submit()" />
	        				</c:when>
	        				<c:otherwise>
	          					<input type="radio" name="chkPrimary" ${ seed.primary ? 'checked' : ''} onclick="togglePrimary('<c:out value="${seed.identity}"/>'); document.tabForm.submit()" />
	        				</c:otherwise>  
	        			</c:choose>  
		    			</authority:show>	 
		    			<authority:dont>
		    				${ seed.primary ? 'Yes' : 'No'}
		    			</authority:dont>   
						</authority:showControl>		      
					</td>
				    <td class="annotationsLiteRow">
					    <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
				        <authority:show>
				        	<input type="image" name="_add" src="images/subtabs-add-btn.gif" alt="Add" width="49" height="23" border="0" hspace="5" align="absmiddle" onclick="linkNewPermissions('${seed.identity}');">
					    </authority:show>	    
						</authority:showControl>
					</td>
				    <td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
				    <td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
				    <td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
				    <td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
				    <td class="annotationsLiteRow">
	    			   	<authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
		    	    	<authority:show>	    
			        		<input type="image" title="Remove Seed" alt="Remove Seed" src="images/action-icon-delete.gif" height="19" width="18" onclick="removeSeed('<c:out value="${seed.identity}"/>');">
		        		</authority:show>
		        		<authority:dont>
		          			<img src="images/x.gif" height="19" width="18">
		        		</authority:dont>
			        	</authority:showControl>	    
				    </td>    
    		</tr>
		    <tr>			
				<td colspan="9" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
			</tr> 
		    </c:when>
    		<c:otherwise>
	  			<c:forEach items="${seed.permissions}" var="permission" varStatus="j">
					<tr ${style}>
	  				<c:choose>
	      				<c:when test="${j.count == 1}">
		    			<td class="annotationsLiteRow"><input id="<c:out value="chkSelect${i.count}"/>" name="<c:out value="chkSelect${seed.identity}"/>" type="checkbox" /></td>
	    				<td class="annotationsLiteRow">
		    				<div class="seedNameView" id="<c:out value="seedNameLabelDiv${i.count}"/>">
		    					<a href="<c:out value="${seed.seed}"/>" target="_blank"><c:out value="${seed.seed}"/></a>
		    					<input style="float:right;" type="image" src="images/action-icon-edit.gif"  alt="Edit name" onclick="editName('<c:out value="${i.count}"/>');return false;">			    			
			    			</div>
			    			<div class="seedNameEdit" id="<c:out value="seedNameInputDiv${i.count}"/>">
								<input style="width:250px" type="text" id="<c:out value="seedNameInput${i.count}"/>" name="<c:out value="seedNameInput${i.count}"/>" value="<c:out value="${seed.seed}"/>"/>
		    					<input style="float:right" type="image" src="images/abort-icon.gif"  alt="Discard name" onclick="cancelEdit();return false;">			    			
		    					<input style="float:right" type="image" src="images/action-icon-export.gif"  alt="Save name" onclick="saveSeedName(<c:out value="${i.count}"/>, <c:out value="${seed.oid}"/>)">			    			
							</div>
	    				</td>
	        			<td class="annotationsLiteRow">
	    					<authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
	        				<authority:show>
	        				<c:choose>
						        <c:when test="${allowMultiplePrimarySeeds == true}">
						            <input type="checkbox" ${ seed.primary ? 'checked' : ''} onclick="togglePrimary('<c:out value="${seed.identity}"/>'); document.tabForm.submit()" />
						        </c:when>
						        <c:otherwise>
						            <input type="radio" name="chkPrimary" ${ seed.primary ? 'checked' : ''} onclick="togglePrimary('<c:out value="${seed.identity}"/>'); document.tabForm.submit()" />
						        </c:otherwise>  
				        	</c:choose>  
						    </authority:show>	
						    <authority:dont>
						    	${ seed.primary ? 'Yes' : 'No'}
						    </authority:dont>     
							</authority:showControl>		
				        </td>
				       </c:when>
					   <c:otherwise>
					     <td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
					     <td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
					     <td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
					   </c:otherwise>
				  	</c:choose>  
					<td class="annotationsLiteRow"><c:out value="${permission.site.title}"/></td>
					<td class="annotationsLiteRow"><c:out value="${permission.authorisingAgent.name}"/></td>
					<td class="annotationsLiteRow"><wct:date value="${permission.startDate}" type="fullDate"/></td>
					<td class="annotationsLiteRow"><wct:date value="${permission.endDate}" type="fullDate"/></td>
					<td class="annotationsLiteRow"><spring:message code="permission.state_${permission.currentStatus}"/></td>
					<td class="annotationsLiteRow">
				   	      <c:choose>
				   	        <c:when test="${permission.restricted && empty permission.exclusions}">
				   	          <img src="images/warn.gif" height="20" width="22" title="Permission has Special Restrictions" alt="Warning Special Restrictions">
				   	        </c:when>
				   	        <c:when test="${!permission.restricted && !empty permission.exclusions}">
				              <img src="images/warn.gif" height="20" width="22" title="Permission has Exclusions" alt="Warning Special Restrictions">
				   	        </c:when>
							<c:when test="${permission.restricted && !empty permission.exclusions}">
				              <img src="images/warn.gif" height="20" width="22" title="Permission has Special Restrictions and Exclusions" alt="Warning Special Restrictions">
				   	        </c:when>   
				   	        <c:otherwise>
				   	          <img src="images/x.gif" height="20" width="22">
				   	        </c:otherwise>	        
						  </c:choose>
					      <input type="image" title="View Permission" alt="View Permission" src="images/action-icon-view.gif" height="19" width="15" onclick="preview(${permission.oid}); return false;">		  
						  <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
						    <authority:show>
								<input type="image" title="Unlink Permission" alt="Unlink Permission" src="images/action-icon-delink.gif" onclick="unlinkSeed('${seed.identity}', ${permission.identity});">
							</authority:show>	    
						  </authority:showControl>	
						  <c:choose>
						    <c:when test="${j.count == 1}">
						  	<authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
					    	  	<authority:show>	    
						      		<input type="image" title="Remove Seed" alt="Remove Seed" src="images/action-icon-delete.gif" height="19" width="18" onclick="removeSeed('<c:out value="${seed.identity}"/>');">
					        	</authority:show>
					        	<authority:dont>
					        	   <img src="images/x.gif" height="19" width="18">
					        	</authority:dont>
					        </authority:showControl>	    
					        </c:when>
					        <c:otherwise>
					        	<img src="images/x.gif" height="19" width="18">
					        </c:otherwise>
					      </c:choose>	
				    </td>
	  			</tr>
	  		</c:forEach>	  
		<tr ${style}>
	    	<td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
	    	<td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
	    	<td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
	    	<td class="annotationsLiteRow">
	    		<authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
        		<authority:show>
            		<input type="image" name="_add2" src="images/subtabs-add-btn.gif" alt="Add" width="49" height="23" border="0" hspace="5" align="absmiddle" onclick="linkNewPermissions('${seed.identity}');">
	    		</authority:show>	    
				</authority:showControl>
			</td>
		    <td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
		    <td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
		    <td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
		    <td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
		    <td class="annotationsLiteRow"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>	    
	  </tr>  
	  <tr>			
			<td colspan="9" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
	  </tr> 
	 </c:otherwise>
  </c:choose>
</c:forEach>
<tr>
<td class="annotationsFooterRow"><input id="chkSelectAll" type="checkbox" title="Select All Seeds" alt="Select All Seeds" onclick="selectAll(<c:out value="${seedCount}"/>);"/></td>
<td class="annotationsFooterRow" colspan="8">
  <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
    <authority:show>
		<input type="image" title="Link Permissions to Selected Seeds" alt="Link Permissions to Selected Seeds" src="images/action-icon-link.gif"  onclick="linkSelected();">
	</authority:show>
  </authority:showControl>    
  <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
    <authority:show>
		<input type="image" title="Unlink Permissions from Selected Seeds" alt="Unlink Permissions from Selected Seeds" src="images/action-icon-delink.gif" onclick="unlinkSelected();">
	</authority:show>	    
  </authority:showControl>	
  <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
   	<authority:show>	    
  		<input type="image" title="Remove Selected Seeds" alt="Remove Selected Seeds" src="images/action-icon-delete.gif" onclick="removeSelected();">
   	</authority:show>
   	<authority:dont>
   	   	<img src="images/x.gif" height="19" width="18">
   	</authority:dont>
  </authority:showControl>	 
</td>
</tr>
	</table>
 </div>