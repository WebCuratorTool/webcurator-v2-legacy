<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.admin.command.CreateQaIndicatorCommand" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<c:set var="allowPrivs"><%= Privilege.MANAGE_INDICATORS %></c:set>
		<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
		<br />
		<form name="registerQaIndicatorForm" action="<%= Constants.CNTRL_CREATE_QA_INDICATOR %>" method="POST">
		<input type="hidden" name="<%= CreateQaIndicatorCommand.PARAM_OID %>" value="${command.oid}">
		<input type="hidden" name="<%= CreateQaIndicatorCommand.PARAM_MODE %>" value="${command.mode}">
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
				    	  <input type="hidden" name="<%= CreateQaIndicatorCommand.PARAM_AGENCY_OID %>" value="${agency.oid}">${agency.name}
				     </c:if>
				  </c:forEach>
				</c:when>
				<c:otherwise>
					<select name="<%= CreateQaIndicatorCommand.PARAM_AGENCY_OID %>">
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
					<input type="text" name="<%= CreateQaIndicatorCommand.PARAM_NAME%>" value="${command.name}" size="30" maxlength="100">
				</td>
			</c:otherwise>
			</c:choose>
			<td></td>
		</tr>
		
		<tr>
			<td class="subBoxTextHdr">Description:</td>
			<c:choose>
			<c:when test="${command.mode == 'view'}">
				<td class="subBoxText">
					<c:out value="${command.description}"></c:out>
				</td>
				<td></td>
			</c:when>
			<c:otherwise>
				<td class="subBoxText">
					<input type="text" name="<%= CreateQaIndicatorCommand.PARAM_DESCRIPTION%>" value="${command.description}" size="100" maxlength="255">
				</td>
			</c:otherwise>
			</c:choose>
			<td></td>
		</tr>
		
		<tr>
			<td class="subBoxTextHdr">Upper Limit:</td>
			<c:choose>
			<c:when test="${command.mode == 'view'}">
				<td class="subBoxText">
					<c:out value="${command.upperLimit}"></c:out>
				</td>
				<td></td>
			</c:when>
			<c:otherwise>
				<td class="subBoxText">
					<input type="text" name="<%= CreateQaIndicatorCommand.PARAM_UPPER_LIMIT%>" value="${command.upperLimit}" size="10" maxlength="10">
				</td>
			</c:otherwise>
			</c:choose>
			<td></td>
		</tr>
		<tr>
			<td class="subBoxTextHdr">Lower Limit:</td>
			<c:choose>
			<c:when test="${command.mode == 'view'}">
				<td class="subBoxText">
					<c:out value="${command.lowerLimit}"></c:out>
				</td>
				<td></td>
			</c:when>
			<c:otherwise>
				<td class="subBoxText">
					<input type="text" name="<%= CreateQaIndicatorCommand.PARAM_LOWER_LIMIT%>" value="${command.lowerLimit}" size="10" maxlength="10">
				</td>
			</c:otherwise>
			</c:choose>
			<td></td>
		</tr>
		<tr>
			<td class="subBoxTextHdr">Upper Limit Percentage:</td>
			<c:choose>
			<c:when test="${command.mode == 'view'}">
				<td class="subBoxText">
					<c:out value="${command.upperLimitPercentage}"></c:out>
				</td>
				<td></td>
			</c:when>
			<c:otherwise>
				<td class="subBoxText">
					<input type="text" name="<%= CreateQaIndicatorCommand.PARAM_UPPER_LIMIT_PERCENTAGE%>" value="${command.upperLimitPercentage}" size="10" maxlength="10">
				</td>
			</c:otherwise>
			</c:choose>
			<td></td>
		</tr>
		<tr>
			<td class="subBoxTextHdr">Lower Limit Percentage:</td>
			<c:choose>
			<c:when test="${command.mode == 'view'}">
				<td class="subBoxText">
					<c:out value="${command.lowerLimitPercentage}"></c:out>
				</td>
				<td></td>
			</c:when>
			<c:otherwise>
				<td class="subBoxText">
					<input type="text" name="<%= CreateQaIndicatorCommand.PARAM_LOWER_LIMIT_PERCENTAGE%>" value="${command.lowerLimitPercentage}" size="10" maxlength="10">
				</td>
			</c:otherwise>
			</c:choose>
			<td></td>
		</tr>
				<tr>
					<td class="subBoxTextHdr">Unit:</td>
					<c:choose>
					<c:when test="${command.mode == 'view'}">
						<td class="subBoxText">
							<c:out value="${command.unit}"></c:out>
						</td>
						<td></td>
					</c:when>
					<c:otherwise>
						<td class="subBoxText">
						<select name="<%=CreateQaIndicatorCommand.PARAM_UNIT%>" id="unit">
						<c:choose>
							<c:when test="${command.unit eq ''}">
								<option value="" selected="selected"></option>
							</c:when>
							<c:otherwise>
								<option value=""></option>
							</c:otherwise>
						</c:choose>				
						<c:forEach items="${units}" var="unit">
						<c:choose>
							<c:when test="${command.unit eq unit}">
								<option value="<c:out value="${unit}"/>" selected="selected"><c:out value="${unit}"/></option>
							</c:when>
							<c:otherwise>
								<option value="<c:out value="${unit}"/>"><c:out value="${unit}"/></option>
							</c:otherwise>
						</c:choose>				
						</c:forEach>
					</select>
				</td>
			</c:otherwise>
			</c:choose>
			<td></td>
		</tr>	
		<tr>
			<td class="subBoxTextHdr">Show Delta:</td>
			<td class="subBoxText">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
					<input type="checkbox" name="showDelta" value="true" <c:out value="${command.showDelta == true?'checked':''}"/> disabled>
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="showDelta" value="true" <c:out value="${command.showDelta == true?'checked':''}"/>>
				</c:otherwise>
				</c:choose>
			</td>
		</tr>	
		<tr>
			<td class="subBoxTextHdr">Enable Report:</td>
			<td class="subBoxText">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
					<input type="checkbox" name="enableReport" value="true" <c:out value="${command.enableReport == true?'checked':''}"/> disabled>
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="enableReport" value="true" <c:out value="${command.enableReport == true?'checked':''}"/>>
				</c:otherwise>
				</c:choose>
			</td>
		</tr>	
		<tr>
			<td class="subBoxText" colspan="2">
			<c:choose>
				<c:when test="${command.mode == 'view'}">
				    <authority:hasAtLeastOnePriv privileges="${allowPrivs}">
					    <authority:hasPrivilege privilege="<%= Privilege.MANAGE_INDICATORS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
					    	<c:set var="showButton">true</c:set>
					    </authority:hasPrivilege>
					    <c:if test="${showButton == 'true'}">
				<input type="hidden" name="<%= CreateQaIndicatorCommand.PARAM_ACTION %>" value="<%= CreateQaIndicatorCommand.ACTION_EDIT %>"> 			
				<input type="image" name="edit" src="images/generic-btn-edit.gif" />
					    </c:if>
				    </authority:hasAtLeastOnePriv>
				<a href="<%= Constants.CNTRL_QA_INDICATORS %>"><img name="_done" src="images/generic-btn-done.gif" alt="Done" width="82" height="23" border="0"></a>
				</c:when>
				<c:when test="${command.mode == 'edit'}">
				<input type="hidden" name="<%= CreateQaIndicatorCommand.PARAM_ACTION %>" value="<%= CreateQaIndicatorCommand.ACTION_SAVE %>"> 			
				<input type="image" name="update" src="images/mgmt-btn-update.gif" />
				<a href="<%= Constants.CNTRL_QA_INDICATORS %>"><img name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0"></a>
				</c:when>
				<c:otherwise>
				<input type="hidden" name="<%= CreateQaIndicatorCommand.PARAM_ACTION %>" value="<%= CreateQaIndicatorCommand.ACTION_SAVE %>"> 			
				<input type="image" name="create" src="images/generic-btn-save.gif" />
				<a href="<%= Constants.CNTRL_QA_INDICATORS %>"><img name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0"></a>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
		
		</table>
		</form>
		<br/>

		