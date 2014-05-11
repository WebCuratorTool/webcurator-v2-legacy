<%@page import="org.webcurator.domain.model.auth.Privilege"%>
<%@page import="org.webcurator.domain.model.core.TargetGroup"%>
<%@ page import="org.webcurator.ui.groups.command.GeneralCommand" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>

<link rel="stylesheet" href="styles/blitzer/jquery-ui-1.10.2.custom.min.css" />
<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>
<script src="scripts/jquery-ui-1.10.2.custom.min.js" type="text/javascript"></script>

<input type="hidden" name="editMode" value="${groupEditorContext.editMode}">
<input type="hidden" name="<%= GeneralCommand.PARAM_SUBGROUP_TYPE %>" value="${command.subGroupType}">
<input type="hidden" name="<%= GeneralCommand.PARAM_SUBGROUP_SEPARATOR %>" value="${command.subGroupSeparator}">
<input type="hidden" name="<%= GeneralCommand.PARAM_PARENT_OID %>" value="${command.parentOid}">
<input type="hidden" id="<%= GeneralCommand.PARAM_ACTION %>" name="<%= GeneralCommand.PARAM_ACTION %>" value="${command.action}">

<script>
	function setAction(str) {
   		document.getElementById('<%= GeneralCommand.PARAM_ACTION %>').value = str;
  	}


	function onChangeType()
	{
		var elem = document.getElementById("type");
		if(elem.value == "${command.subGroupType}")
		{
			document.getElementById("subGroupParent").className = "";
		}
		else
		{
			document.getElementById("subGroupParent").className = "hidden";
		}
	}
</script>

<script type="text/javascript"> 
	$(document).ready(function() {
		$('.dateEntry').datepicker({dateFormat: 'dd/mm/yy', changeMonth: true, changeYear: true, showOtherMonths: true, selectOtherMonths: true, showButtonPanel: true});
	});
</script>

<c:set var="editMode" value="${groupEditorContext.editMode}"/>
			<table cellpadding="3" cellspacing="0" border="0">
			  <tr>
			    <td class="subBoxTextHdr">Id:</td>
			    <td class="subBoxText"><c:out value="${groupEditorContext.targetGroup.oid}"/></td>
			  </tr>
			  <c:choose>
			  <c:when test="${command.subGroupType == command.type}">
				  <tr id="subGroupParent">
			  </c:when>
			  <c:otherwise>
				  <tr id="subGroupParent" class="hidden">
			  </c:otherwise>
			  </c:choose>
			    <td class="subBoxTextHdr">Parent Group:</td>
			    <td class="subBoxText">
			      <authority:showControl ownedObject="${groupEditorContext.targetGroup}" privileges="<%= Privilege.CREATE_GROUP %>" editMode="${editMode}">
			        <authority:show>
				      <input type="text" name="<%= GeneralCommand.PARAM_PARENT_NAME %>" style="width:300px;" value="<c:out value="${command.parentName}"/>" maxlength="<%= GeneralCommand.CNST_MAX_LEN_NAME %>" READONLY><font color=red size=2>&nbsp;<strong>*</strong></font>
				      <input id="btnAdd" type="image" src="images/subtabs-add-btn.gif" alt="Add" width="49" height="23" border="0" align="right" onclick="setAction('<%= GeneralCommand.ACTION_ADD_PARENT%>');">
				      
				    </authority:show>
				    <authority:dont>
				      <c:out value="${command.parentName}"/>
				    </authority:dont>
				  </authority:showControl>
			    </td>
			  </tr>
			  <tr>
			    <td class="subBoxTextHdr">Name:</td>
			    <td class="subBoxText">
			      <authority:showControl ownedObject="${groupEditorContext.targetGroup}" privileges="<%= Privilege.CREATE_GROUP %>" editMode="${editMode}">
			        <authority:show>
				      <input type="text" name="name" style="width:350px;" value="<c:out value="${command.name}"/>" maxlength="<%= GeneralCommand.CNST_MAX_LEN_NAME %>"><font color=red size=2>&nbsp;<strong>*</strong></font>
				    </authority:show>
				    <authority:dont>
				      <c:out value="${command.name}"/>
				    </authority:dont>
				  </authority:showControl>
			    </td>
			  </tr>
			  <tr>
			    <td class="subBoxTextHdr" valign="top">Description:</td>
			    <td class="subBoxText">
			      <authority:showControl ownedObject="${groupEditorContext.targetGroup}" privileges="<%= Privilege.CREATE_GROUP %>" editMode="${editMode}">
			        <authority:show>
	                  <textarea name="description" cols="40" rows="6" style="width:350px;"><c:out value="${command.description}"/></textarea>
				    </authority:show>
				    <authority:dont>
				      <c:out value="${command.description}"/>
				    </authority:dont>
				  </authority:showControl>    
			  </tr>
			   <tr>
			    <td class="subBoxTextHdr">Reference Number:</td>
			    <td class="subBoxText">
			      <authority:showControl ownedObject="${groupEditorContext.targetGroup}" privileges="<%= Privilege.CREATE_GROUP %>" editMode="${editMode}">
			        <authority:show>
				      <input type="text" name="reference" value="<c:out value="${command.reference}"/>" maxlength="<%= TargetGroup.MAX_REFERENCE_LENGTH %>">
				    </authority:show>
				    <authority:dont>
				      <c:out value="${command.reference}"/>
				    </authority:dont>
				  </authority:showControl>
			    </td>
			  </tr>
			  <tr>
			    <td class="subBoxTextHdr" valign="top">Type:</td>
			    <td class="subBoxText">
			      <authority:showControl ownedObject="${groupEditorContext.targetGroup}" privileges="<%= Privilege.CREATE_GROUP %>" editMode="${editMode}">
			        <authority:show>
	                  <wct:list paramName="type" onChangeFunction="onChangeType();" currentValue="${command.type}" list="${groupTypesList}"/>
				    </authority:show>
				    <authority:dont>
				      <c:out value="${command.type}"/>
				    </authority:dont>
				  </authority:showControl>    
			  </tr>			  
			  <tr>
			    <td class="subBoxTextHdr">Owner:</td>
			    <td class="subBoxText">
			      <authority:showControl ownedObject="${groupEditorContext.targetGroup}" privileges="<%= Privilege.CREATE_GROUP %>" editMode="${editMode}">
			        <authority:show>
			      		<select name="ownerOid">
					        <c:forEach items="${allUsers}" var="user">
			        		  <option value="<c:out value="${user.oid}"/>" ${user.oid == command.ownerOid ? 'SELECTED' : ''}><c:out value="${user.firstname} ${user.lastname}"/></option>
					        </c:forEach>
					    </select>
					</authority:show>
					<authority:dont>
						<c:forEach items="${allUsers}" var="user">
						  <c:if test="${user.oid == command.ownerOid}">
			                <c:out value="${user.firstname} ${user.lastname}"/>
						  </c:if>
					    </c:forEach>	
					</authority:dont>
			      </authority:showControl>
			    </td>
			  </tr>  
			  <tr>
			    <td class="subBoxTextHdr" valign="top">Ownership Info:</td>
			    <td class="subBoxText">
			      <authority:showControl ownedObject="${groupEditorContext.targetGroup}" privileges="<%= Privilege.CREATE_GROUP %>" editMode="${editMode}">
			        <authority:show>
				      <textarea name="ownershipMetaData" style="width:350px;" rows="2"><c:out value="${command.ownershipMetaData}"/></textarea>
				    </authority:show>
				    <authority:dont>
				      <c:out value="${command.ownershipMetaData}"/>
				    </authority:dont>
				  </authority:showControl>
			    </td>
			  </tr>  
			  <tr>
			    <td class="subBoxTextHdr">From Date:</td>
			    <td class="subBoxText">
			      <authority:showControl ownedObject="${groupEditorContext.targetGroup}" privileges="<%= Privilege.CREATE_GROUP %>" editMode="${editMode}">
			        <authority:show>
				      <input class="dateEntry" type="text" name="fromDate" value="<wct:date value="${command.fromDate}" type="fullDate"/>"  size="10" maxlength="10"/>
				    </authority:show>
				    <authority:dont>
				      <wct:date value="${command.fromDate}" type="fullDate"/>
				    </authority:dont>
				  </authority:showControl>
			    </td>
			  </tr>  	
			  <tr>
			    <td class="subBoxTextHdr">To Date:</td>
			    <td class="subBoxText">
			      <authority:showControl ownedObject="${groupEditorContext.targetGroup}" privileges="<%= Privilege.CREATE_GROUP %>" editMode="${editMode}">
			        <authority:show>
				      <input class="dateEntry" type="text" name="toDate" value="<wct:date value="${command.toDate}" type="fullDate"/>" size="10" maxlength="10"/>
				    </authority:show>
				    <authority:dont>
				      <wct:date value="${command.toDate}" type="fullDate"/>
				    </authority:dont>
				  </authority:showControl>
			    </td>
			  </tr> 			  		  
			  <tr>
			    <td class="subBoxTextHdr"><spring:message code="ui.label.groups.general.harvestType"/>:</td>
			    <td class="subBoxText">
			      <authority:showControl ownedObject="${groupEditorContext.targetGroup}" privileges="<%= Privilege.CREATE_GROUP %>" editMode="${editMode}">
			        <authority:show>
			      		<input type="radio" id="sipTypeMany" name="sipType" value="<%= TargetGroup.MANY_SIP %>" ${command.sipType == 2 ? 'checked' :''}><label for="sipTypeMany"><spring:message code="ui.label.groups.general.manySip"/></label><br/>
			      		<input type="radio" id="sipTypeOne" name="sipType" value="<%= TargetGroup.ONE_SIP %>" ${command.sipType == 1 ? 'checked' :''}><label for="sipTypeOne"><spring:message code="ui.label.groups.general.oneSip"/></label>
					</authority:show>
					<authority:dont>
					  <c:choose>
					    <c:when test="${command.sipType == 2}">
					      <spring:message code="ui.label.groups.general.manySip"/>
					    </c:when>
					    <c:otherwise>
					      <spring:message code="ui.label.groups.general.oneSip"/>
					    </c:otherwise>
					  </c:choose>
					</authority:dont>
			      </authority:showControl>
			    </td>
			  </tr>    			
			</table>