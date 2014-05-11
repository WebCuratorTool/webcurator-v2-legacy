<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.ui.admin.command.QaIndicatorCommand" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ page import="org.webcurator.ui.admin.command.CreateQaIndicatorCommand" %>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<c:set var="allowPrivs"><%= Privilege.MANAGE_INDICATORS %></c:set>

<style type="text/css">
	.wrapping {
		width:100%;
		word-break:break-word;
		white-space:normal;
	}
</style>

<div id="resultsTable">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td>
			<span class="midtitleGrey">QA Indicators</span>
		</td>
		<td align="right">
		  Agency Filter:&nbsp;
		</td>
		<td colspan="2">
			<form id="frmFilter" action="<%= Constants.CNTRL_QA_INDICATORS %>" method="POST">
			<input type="hidden" name="<%=QaIndicatorCommand.PARAM_CMD%>" value="<%= QaIndicatorCommand.ACTION_FILTER%>">
		  	<select name="<%=QaIndicatorCommand.PARAM_AGENCY_FILTER%>" id="agencyfilter" onchange="document.getElementById('frmFilter').submit();">
	  		<option id="" ${agencyfilter eq '' ? 'SELECTED' : ''}></option>
			<c:forEach items="${agencies}" var="agency">
		  		<option id="${agency.name}" ${agencyfilter eq agency.name ? 'SELECTED' : ''}>${agency.name}</option>
		  	</c:forEach>
		  	</select>
		  	</form>
		</td>
		<td>
		<authority:hasPrivilege privilege="<%= Privilege.MANAGE_INDICATORS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
			<form action="<%= Constants.CNTRL_CREATE_QA_INDICATOR %>" method="POST">
			<input type="hidden" name="<%= CreateQaIndicatorCommand.PARAM_ACTION%>" value="<%= CreateQaIndicatorCommand.ACTION_NEW%>">
			<input type="image" src="images/create-new-btn-red.gif" alt="Create a new Rejection indicator" width="82" height="24" border="0" align="right" vspace="3">
			</form>
		</authority:hasPrivilege>
		</td>
	</tr>
	<tr>
		<td class="tableHead">Indicator Name</td>
		<td class="tableHead wrapping">Description</td>
		<td class="tableHead">Upper Limit (absolute)</td>
		<td class="tableHead">Lower Limit (absolute)</td>
		<td class="tableHead">Upper Limit (+%)</td>
		<td class="tableHead">Lower Limit (-%)</td>
		<td class="tableHead">Agency</td>
		<td class="tableHead">Unit</td>
		<td class="tableHead">Show Delta</td>
		<td class="tableHead">Enable Report</td>
		<td class="tableHead">Action</td>
	</tr>
	<c:forEach items="${qaIndicatorDTOs}" var="indicator">
	<c:if test="${agencyfilter eq '' or agencyfilter eq indicator.agency.name}">
	  <c:set var="showButton">false</c:set>
	  <tr>
	    <td class="tableRowLite"><c:out value="${indicator.name}"/></td>
	    <td class="tableRowLite wrapping"><c:out value="${indicator.description}"/></td>
	    <td class="tableRowLite"><c:out value="${indicator.upperLimit}"/></td>
	    <td class="tableRowLite"><c:out value="${indicator.lowerLimit}"/></td>
	    <td class="tableRowLite"><c:out value="${indicator.upperLimitPercentage}"/></td>
	    <td class="tableRowLite"><c:out value="${indicator.lowerLimitPercentage}"/></td>
	    <td class="tableRowLite"><c:out value="${indicator.agency.name}"/></td>
	    <td class="tableRowLite"><c:out value="${indicator.unit}"/></td>
	    <td class="tableRowLite"><c:out value="${indicator.showDelta == true?'Yes':'No'}"/></td>
	    <td class="tableRowLite"><c:out value="${indicator.enableReport == true?'Yes':'No'}"/></td>
	    <td class="tableRowLite">
	    	<table>
	    	<tr>
	    		<td>
				    <form name="viewForm" action="<%= Constants.CNTRL_CREATE_QA_INDICATOR %>" method="POST">
				    <input type="hidden" name="<%= CreateQaIndicatorCommand.PARAM_ACTION %>" value="<%= CreateQaIndicatorCommand.ACTION_VIEW%>">
				    <input type="hidden" name="<%= CreateQaIndicatorCommand.PARAM_OID %>" value="${indicator.oid}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				    <input type="image" name="view" src="images/action-icon-view.gif" title="View" alt="click here to VIEW this item" width="18" height="18" border="0">
				    </form>
	    		</td>
	    		<td>
				    <authority:hasAtLeastOnePriv privileges="${allowPrivs}">
					    <authority:hasPrivilege privilege="<%= Privilege.MANAGE_INDICATORS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
					    	<c:set var="showButton">true</c:set>
					    </authority:hasPrivilege>
					    <c:if test="${showButton == 'true'}">
						    <form name="editForm" action="<%= Constants.CNTRL_CREATE_QA_INDICATOR %>" method="POST">
						    <input type="hidden" name="<%= CreateQaIndicatorCommand.PARAM_ACTION %>" value="<%= CreateQaIndicatorCommand.ACTION_EDIT%>">
						    <input type="hidden" name="<%= CreateQaIndicatorCommand.PARAM_OID %>" value="${indicator.oid}">
							<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
						    <input type="image" name="edit" src="images/action-icon-edit.gif" title="Edit" alt="click here to EDIT this item" width="18" height="18" border="0">
						    </form>
					    </c:if>
				    </authority:hasAtLeastOnePriv>
			    </td>
		    </tr>
	    </table>

	    </td>
	  </tr>
	</c:if>
	</c:forEach>
	</table>
</div>
