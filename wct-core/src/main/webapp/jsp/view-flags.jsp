<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.ui.admin.command.FlagCommand" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ page import="org.webcurator.ui.admin.command.CreateFlagCommand" %>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<c:set var="allowPrivs"><%= Privilege.MANAGE_FLAGS %></c:set>

<div id="resultsTable">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td>
			<span class="midtitleGrey">Flags</span>
		</td>
		<td align="right">
		  Agency Filter:&nbsp;
		</td>
		<td colspan="2">
			<form id="frmFilter" action="<%= Constants.CNTRL_FLAGS %>" method="POST">
			<input type="hidden" name="<%=FlagCommand.PARAM_CMD%>" value="<%= FlagCommand.ACTION_FILTER%>">
		  	<select name="<%=FlagCommand.PARAM_AGENCY_FILTER%>" id="agencyfilter" onchange="document.getElementById('frmFilter').submit();">
	  		<option id="" ${agencyfilter eq '' ? 'SELECTED' : ''}></option>
			<c:forEach items="${agencies}" var="agency">
		  		<option id="${agency.name}" ${agencyfilter eq agency.name ? 'SELECTED' : ''}>${agency.name}</option>
		  	</c:forEach>
		  	</select>
		  	</form>
		</td>
		<td>
		<authority:hasPrivilege privilege="<%= Privilege.MANAGE_FLAGS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
			<form action="<%= Constants.CNTRL_CREATE_FLAG %>" method="POST">
			<input type="hidden" name="<%= CreateFlagCommand.PARAM_ACTION%>" value="<%= CreateFlagCommand.ACTION_NEW%>">
			<input type="image" src="images/create-new-btn-red.gif" alt="Create a new Rejection flag" width="82" height="24" border="0" align="right" vspace="3">
			</form>
		</authority:hasPrivilege>
		</td>
	</tr>
	<tr>
		<td class="tableHead">Flag Name</td>
		<td class="tableHead">RGB Colour</td>
		<td class="tableHead">Agency</td>
		<td class="tableHead">Action</td>
	</tr>
	<c:forEach items="${flags}" var="flag">
	<c:if test="${agencyfilter eq '' or agencyfilter eq flag.agency.name}">
	  <c:set var="showButton">false</c:set>
	  <tr>
	    <td class="tableRowLite"><c:out value="${flag.name}"/></td>
	    <td class="tableRowLite"><div style="width: 18px; height: 18px; background-color: #${flag.rgb};"><img src="images/flag-icon-alpha.png" /></div></td>
	    <td class="tableRowLite"><c:out value="${flag.agency.name}"/></td>
	    <td class="tableRowLite">
	    	<table>
	    	<tr>
	    		<td>
				    <form name="viewForm" action="<%= Constants.CNTRL_CREATE_FLAG %>" method="POST">
				    <input type="hidden" name="<%= CreateFlagCommand.PARAM_ACTION %>" value="<%= CreateFlagCommand.ACTION_VIEW%>">
				    <input type="hidden" name="<%= CreateFlagCommand.PARAM_OID %>" value="${flag.oid}">
					<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
				    <input type="image" name="view" src="images/action-icon-view.gif" title="View" alt="click here to VIEW this item" width="18" height="18" border="0">
				    </form>
	    		</td>
	    		<td>
				    <authority:hasAtLeastOnePriv privileges="${allowPrivs}">
					    <authority:hasPrivilege privilege="<%= Privilege.MANAGE_FLAGS %>" scope="<%= Privilege.SCOPE_AGENCY %>">
					    	<c:set var="showButton">true</c:set>
					    </authority:hasPrivilege>
					    <c:if test="${showButton == 'true'}">
						    <form name="editForm" action="<%= Constants.CNTRL_CREATE_FLAG %>" method="POST">
						    <input type="hidden" name="<%= CreateFlagCommand.PARAM_ACTION %>" value="<%= CreateFlagCommand.ACTION_EDIT%>">
						    <input type="hidden" name="<%= CreateFlagCommand.PARAM_OID %>" value="${flag.oid}">
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
