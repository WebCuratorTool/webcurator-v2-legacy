<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="org.webcurator.ui.target.command.TargetInstanceCommand" %>
<%@page import="org.webcurator.ui.target.command.TargetInstanceSummaryCommand" %>
<%@page import="org.webcurator.domain.model.core.TargetInstance" %>
<%@page import="org.webcurator.ui.common.Constants" %>
<%@page import="org.webcurator.domain.model.auth.Privilege" %>
<%@page import="org.webcurator.domain.model.core.Indicator" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>


<script type="text/javascript"> 
// json array to map the specific table header sort to the corresponding sort arrow
var sort_arrows = {
		"indicatorvalueasc": 'images/sort-up-arrow.gif',
		"indicatorvaluedesc": 'images/sort-down-arrow.gif',
		"countasc": 'images/sort-up-arrow.gif',
		"countdesc": 'images/sort-down-arrow.gif'
}
// array of ascending sort fields (used to toggle the sort arrow)
var sort_fields_asc = ["indicatorvalueasc", "countasc"];
//array of descending sort fields (used to toggle the sort arrow)
var sort_fields_desc = ["indicatorvaluedesc", "countdesc"];

// toggle the specified sortable field
// sortfield: the column name to toggle (without the 'asc' or 'desc' suffix)
function toggleField( sortfield ) {
	// fetch the current sort field from the hidden 'sortorder' field
	var currentSort = $('#sortorder').prop("value");
	// if current sort field is different from the clicked field, then change to the clicked field
	if (currentSort != sortfield.data('id') + 'asc' && currentSort != sortfield.data('id') + 'desc')
		currentSort = sortfield.data('id') + 'asc'
	// check if the sortfield is currently ascending
	var idx = jQuery.inArray(currentSort, sort_fields_asc);
	if (idx >= 0) {
		// change the field to descending
		$('#sortorder').prop("value", sort_fields_desc[idx]);
		sortfield.data('id', sort_fields_desc[idx]);
		// submit the filter form
		$("#filter").submit();
		return;
	}
	// check if the sortfield is currently descending
	idx = jQuery.inArray(currentSort, sort_fields_desc);
	if (idx >= 0) {
		// change the field to ascending
		$('#sortorder').prop("value", sort_fields_asc[idx]);
		sortfield.data('id', sort_fields_asc[idx]);
		// submit the filter form
		$("#filter").submit();
		return;	
	}
	// if not currently sorted then sort on the input field
	$('#sortorder').prop("value", sortfield);
	// submit the filter form
	$("#filter").submit();
	return;
}

// the JQuery body onload function
$(document).ready(function() {
	
	// apply the table header sort arrows
	var sortorder = $("#sortorder").prop("value");
	if (sortorder != null) {
		var el = $('<img src="' + sort_arrows[sortorder] + '" />'); 
		$('#' + sortorder).append(el);
	}
});
</script>
<script>
  function setPageNumber(pageNumber) {
	document.filter2.<%=TargetInstanceCommand.PARAM_PAGE%>.value=pageNumber; 
	document.filter2.<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_SHOW_PAGE%>'; 
	document.filter2.submit();
  }

  function setPageSize(pageSize) {
	document.filter2.<%=TargetInstanceCommand.PARAM_PAGESIZE%>.value=pageSize; 
	document.filter2.<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_SHOW_PAGE%>'; 
	document.filter2.submit();
  }
</script>
<div id="resultsTable">
<body>

<form id="filter" name="filter" action="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/curator/target/qa-indicator-report.html?indicatorOid=${indicator.oid}" method="post">

		<table width="75%" cellpadding="0" cellspacing="0" border="0">
		<tr><td class="tableRowLite">Target Name:</td><td class="tableRowLite">${instance.target.name}</td></tr>
		<tr><td class="tableRowLite">Indicator Name:</td><td class="tableRowLite">${indicator.name}</td></tr>
		<tr><td class="tableRowLite">Description:</td><td class="tableRowLite">${indicator.indicatorCriteria.description}</td></tr>
		<tr><td class="tableRowLite">Data Collected On:</td><td class="tableRowLite"><wct:date value="${indicator.dateTime}" type="fullDateTime"/></td></tr>
		</table>
		<p/>
	
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td class="searchBoxLabel" align="right">&nbsp;</td>
		</tr>
		<tr>
			<td class="sortTableHead" data-id="indicatorvalue" onclick="toggleField($(this));">Indicator Value&nbsp;<span id="indicatorvalueasc" /><span id="indicatorvaluedesc" /></td>
			<td class="sortTableHead" data-id="count" onclick="toggleField($(this));">Occurrences&nbsp;<span id="countasc" /><span id="countdesc" /></td>
		</tr>
	<c:forEach items="${sortedSummary}" var="map">
	<tr>
		<td class="tableRowLite">${map.key}</td>
		<td class="tableRowLite">${map.value}</td>
	</tr>		
	</c:forEach> 
	<tr><td class="tableRowTotalBold" >Total</td><td class="tableRowTotalBold" ><c:out value="${total}" /></td></tr>
	<tr><td>&nbsp;</td></tr>
	</table>
	<table width="100%">
	<tr><td align="center"><a href="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/curator/target/qatisummary.html?targetInstanceOid=${instance.oid}"><img src="images/generic-btn-done.gif"  alt="Done" width="82" height="23" border="0"/></a></td></tr>
	</table>
<input id="sortorder" name="sortorder" type="hidden" value="${sortorder}"/>
</form>

</body>
</div>