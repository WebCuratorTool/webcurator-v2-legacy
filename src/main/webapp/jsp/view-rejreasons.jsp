<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.ui.admin.command.RejReasonCommand" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ page import="org.webcurator.ui.admin.command.CreateRejReasonCommand" %>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<c:set var="allowPrivs"><%= Privilege.MANAGE_REASONS %></c:set>

<div id="resultsTable">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td>
			<span class="midtitleGrey">Rejection Reasons</span>
		</td>
		<td align="right">
		  Agency Filter:&nbsp;
		</td>
		<td colspan="2">
			<form id="frmFilter" action="<%= Constants.CNTRL_REASONS %>" method="POST">
			<input type="hidden" name="<%=RejReasonCommand.PARAM_CMD%>" value="<%= RejReasonCommand.ACTION_FILTER%>">
		  	<select name="<%=RejReasonCommand.PARAM_AGENCY_FILTER%>" id="agencyfilter" onchange="document.getElementById('frmFilter').submit();">
	  		<option id="" ${agencyfilter eq '' ? 'SELECTED' : ''}></option>
			<c:forEach items="${agencies}" var="agency">
		  		<option id="${agency.name}" ${agencyfilter eq agency.name ? 'SELECTED' : ''}>${agency.name}</option>
		  	</c:forEach>
		  	</select>
		  	</form>
		</td>
		<td>
		<authority:hasPrivilege privilege="<%= Privilege.MANAGE_REASONS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
			<form action="<%= Constants.CNTRL_CREATE_REASON %>" method="POST">
			<input type="hidden" name="<%= CreateRejReasonCommand.PARAM_ACTION%>" value="<%= CreateRejReasonCommand.ACTION_NEW%>">
			<input type="image" src="images/create-new-btn-red.gif" alt="Create a new Rejection Reason" width="82" height="24" border="0" align="right" vspace="3">
			</form>
		</authority:hasPrivilege>
		</td>
	</tr>
	<tr>
		<td class="tableHead">Rejection Reason</td>
		<td class="tableHead">Available for Targets</td>
		<td class="tableHead">Available for Target Instances</td>
		<td class="tableHead">Agency</td>
		<td class="tableHead">Action</td>
	</tr>
	<c:forEach items="${reasonDTOs}" var="reason">
	<c:if test="${agencyfilter eq '' or agencyfilter eq reason.agency.name}">
	  <c:set var="showButton">false</c:set>
	  <tr>
	    <td class="tableRowLite"><c:out value="${reason.name}"/></td>
	    <td class="tableRowLite"><c:out value="${reason.availableForTargets == true?'Yes':'No'}"/></td>
	    <td class="tableRowLite"><c:out value="${reason.availableForTIs == true?'Yes':'No'}"/></td>
	    <td class="tableRowLite"><c:out value="${reason.agency.name}"/></td>
	    <td class="tableRowLite">
	    	<table>
	    	<tr>
	    		<td>
				    <form name="viewForm" action="<%= Constants.CNTRL_CREATE_REASON %>" method="POST">
				    <input type="hidden" name="<%= CreateRejReasonCommand.PARAM_ACTION %>" value="<%= CreateRejReasonCommand.ACTION_VIEW%>">
				    <input type="hidden" name="<%= CreateRejReasonCommand.PARAM_OID %>" value="${reason.oid}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				    <input type="image" name="view" src="images/action-icon-view.gif" title="View" alt="click here to VIEW this item" width="18" height="18" border="0">
				    </form>
	    		</td>
	    		<td>
				    <authority:hasAtLeastOnePriv privileges="${allowPrivs}">
					    <authority:hasPrivilege privilege="<%= Privilege.MANAGE_REASONS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
					    	<c:set var="showButton">true</c:set>
					    </authority:hasPrivilege>
					    <c:if test="${showButton == 'true'}">
						    <form name="editForm" action="<%= Constants.CNTRL_CREATE_REASON %>" method="POST">
						    <input type="hidden" name="<%= CreateRejReasonCommand.PARAM_ACTION %>" value="<%= CreateRejReasonCommand.ACTION_EDIT%>">
						    <input type="hidden" name="<%= CreateRejReasonCommand.PARAM_OID %>" value="${reason.oid}">
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
