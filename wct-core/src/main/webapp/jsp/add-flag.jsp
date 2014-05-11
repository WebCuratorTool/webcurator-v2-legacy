<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.admin.command.CreateFlagCommand" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<c:set var="allowPrivs"><%= Privilege.MANAGE_FLAGS %></c:set>

   
<link rel="stylesheet" media="screen" type="text/css" href="styles/colorpicker.css" />
<script type="text/javascript" src="scripts/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="scripts/colorpicker.js"></script>
<link rel="stylesheet" media="screen" type="text/css" href="styles/colorpicker_div.css" />

<script type="text/javascript">
  $(document).ready(function() {
	setupColorPicker('#<%= CreateFlagCommand.PARAM_RGB%>', '#colorPicker');
  });
  
  function setupColorPicker(inputSelector, anchorSelector) {
  	$(anchorSelector+' div').css('backgroundColor', '#' + $(inputSelector).val());
			<c:choose>
			<c:when test="${command.mode != 'view'}">
	$(anchorSelector).ColorPicker({
		color:'#'+$(inputSelector).val(),
		onChange: function (hsb, hex, rgb) { 
			$(anchorSelector+' div').css('backgroundColor', '#' + hex);
			$(inputSelector).val(hex);
		}
	});
			</c:when>
			<c:otherwise>
	$(".threshold").prop('disabled', true);
			</c:otherwise>
			</c:choose>
  }

</script>
 
    
		<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
		<br />
		<form name="registerFlagForm" action="<%= Constants.CNTRL_CREATE_FLAG %>" method="POST">
		<input type="hidden" name="<%= CreateFlagCommand.PARAM_OID %>" value="${command.oid}">
		<input type="hidden" name="<%= CreateFlagCommand.PARAM_MODE %>" value="${command.mode}">
		<table cellpadding="3" cellspacing="0" border="0">
		
		<tr>
			<td class="subBoxTextHdr">Agency:</td>
			<td class="subBoxText">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
				  <c:forEach items="${agencies}" var="agency">
				     <c:if test="${command.agencyOid == agency.oid}">
				    	  <c:out value="${agency.name}"/>
				     </c:if>
				  </c:forEach>
				</c:when>
				<c:when test="${command.mode == 'edit'}">
				  <c:forEach items="${agencies}" var="agency">
				     <c:if test="${command.agencyOid == agency.oid}">
				    	  <input type="hidden" name="<%= CreateFlagCommand.PARAM_AGENCY_OID %>" value="${agency.oid}">${agency.name}
				     </c:if>
				  </c:forEach>
				</c:when>
				<c:otherwise>
					<select name="<%= CreateFlagCommand.PARAM_AGENCY_OID %>">
					    	  <c:forEach items="${agencies}" var="agency">
					    	  <option value="<c:out value="${agency.oid}"/>" <c:if test="${agency.oid == command.agencyOid}">selected</c:if>>${agency.name}</option>
					    	  </c:forEach>
					</select>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
		
		<tr>
			<td class="subBoxTextHdr">Name:</td>
			<c:choose>
			<c:when test="${command.mode == 'view'}">
				<td class="subBoxText">
					<c:out value="${command.name}"></c:out>
				</td>
				<td></td>
			</c:when>
			<c:otherwise>
				<td class="subBoxText">
					<input type="text" name="<%= CreateFlagCommand.PARAM_NAME%>" value="${command.name}" size="100" maxlength="100">
				</td>
			</c:otherwise>
			</c:choose>
			<td></td>
		</tr>
		
		<tr>
			<td class="subBoxTextHdr">RGB Colour:</td>
				<td class="subBoxText">
					<input type="hidden" id="<%= CreateFlagCommand.PARAM_RGB%>" name="<%= CreateFlagCommand.PARAM_RGB%>" value="${command.rgb}" size="10" maxlength="10">
					<div class="colorPickerAnchor" id="colorPicker"><div></div></div>				
				</td>
			<td class="subBoxText">				

			</td>
		</tr>
		<tr>
			<td class="subBoxText" colspan="2">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
				    <authority:hasAtLeastOnePriv privileges="${allowPrivs}">
					    <authority:hasPrivilege privilege="<%= Privilege.MANAGE_FLAGS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
					    	<c:set var="showButton">true</c:set>
					    </authority:hasPrivilege>
					    <c:if test="${showButton == 'true'}">
				<input type="image" name="edit" src="images/generic-btn-edit.gif" />
				<input type="hidden" name="<%= CreateFlagCommand.PARAM_ACTION %>" value="<%= CreateFlagCommand.ACTION_EDIT %>"> 			
					    </c:if>
				    </authority:hasAtLeastOnePriv>
				<a href="<%= Constants.CNTRL_FLAGS %>"><img name="_done" src="images/generic-btn-done.gif" alt="Done" width="82" height="23" border="0"></a>
				</c:when>
				<c:when test="${command.mode == 'edit'}">
				<input type="hidden" name="<%= CreateFlagCommand.PARAM_ACTION %>" value="<%= CreateFlagCommand.ACTION_SAVE %>"> 			
				<input type="image" name="update" src="images/mgmt-btn-update.gif" />
				<a href="<%= Constants.CNTRL_FLAGS %>"><img name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0"></a>
				</c:when>
				<c:otherwise>
				<input type="hidden" name="<%= CreateFlagCommand.PARAM_ACTION %>" value="<%= CreateFlagCommand.ACTION_SAVE %>"> 			
				<input type="image" name="create" src="images/generic-btn-save.gif" />
				<a href="<%= Constants.CNTRL_FLAGS %>"><img name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0"></a>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
		
		</table>
		</form>
		<br/>

		