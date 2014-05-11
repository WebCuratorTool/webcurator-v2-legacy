<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority" %>
<%@ page import="org.webcurator.ui.agent.command.BandwidthRestrictionsCommand" %>

<%@page import="org.webcurator.domain.model.auth.Privilege"%>

<link rel="stylesheet" media="screen" type="text/css" href="styles/colorpicker.css" />
<script type="text/javascript" src="scripts/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="scripts/colorpicker.js"></script>
<link rel="stylesheet" media="screen" type="text/css" href="styles/colorpicker_div.css" />

<script type="text/javascript">
  $(document).ready(function() {
	setupColorPicker('#lowHeatmapColor', '#lowHeatmapColorPicker');
	setupColorPicker('#mediumHeatmapColor', '#mediumHeatmapColorPicker');
	setupColorPicker('#highHeatmapColor', '#highHeatmapColorPicker');
  });
  
  function setupColorPicker(inputSelector, anchorSelector) {
  	$(anchorSelector+' div').css('backgroundColor', '#' + $(inputSelector).val());
	<authority:hasPrivilege privilege="<%=Privilege.MANAGE_WEB_HARVESTER%>" scope="<%=Privilege.SCOPE_ALL%>">
	$(anchorSelector).ColorPicker({
		color:'#'+$(inputSelector).val(),
		onChange: function (hsb, hex, rgb) { 
			$(anchorSelector+' div').css('backgroundColor', '#' + hex);
			$(inputSelector).val(hex);
		}
	});
	</authority:hasPrivilege>
	<authority:noPrivilege privilege="<%=Privilege.MANAGE_WEB_HARVESTER%>" scope="<%=Privilege.SCOPE_ALL%>">
	$(".threshold").prop('disabled', true);
	</authority:noPrivilege>
  }

</script>

<div id="resultsTable">
<table border="0" width="100%">
  <tr>
  	<td colspan="2" style="float:right;">
  		<fieldset style="width:200px">
  			<legend>Legend - Harvest Optimization</legend>
    		<div class="bandwidthLegend optimizeAllowed"></div> Optimization allowed<br/>
    		<div class="bandwidthLegend optimizeNotAllowed"></div> Optimization prevented
    	</fieldset>
  	</td>
  </tr>
  <tr>
    <td>
    <c:set var="count" scope="page" value="0"/>
    <c:forEach items="${daysOfTheWeek}" var="day">    	    	
    	<c:set var="dayRestrictions" scope="page" value="${bandwidthRestrictions[day]}"/>
    	<table width="100%">
		  <tr>
		    <td colspan="2"><c:out value="${day}"/></td>
		  </tr>
		  <tr>
		    <td align="left">00:00:00</td>
		    <td align="right">23:59:59</td>
		  </tr>
		  <tr>
		    <td colspan="2">		    		  
		      <table width="100%" border="1" cellpadding="0" cellspacing="0" bordercolor="black">
		      	<tr>		      	
		  		<c:forEach items="${dayRestrictions}" var="restrictions">
		  		<c:set var="count" scope="page" value="${count + 1}"/>
		  		<td width="<c:out value="${restrictions.dayPercentage}"/>%" align="right" 
		  			<c:choose>
		  				<c:when test="${restrictions.allowOptimize==false}">class="optimizeNotAllowed"</c:when>
		  				<c:otherwise>class="optimizeAllowed"</c:otherwise>
		  			</c:choose>
		  		>
		  		<authority:hasPrivilege privilege="<%=Privilege.MANAGE_WEB_HARVESTER%>" scope="<%=Privilege.SCOPE_ALL%>">
			  		<form name="bandwidth<c:out value="${count}"/>" method="POST" action="<c:out value="${action}"/>">
			  		<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_OID%>" value="<c:out value="${restrictions.oid}"/>"/>
			  		<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_DAY%>" value="<c:out value="${restrictions.dayOfWeek}"/>"/>			  		
			  		<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_START%>" value="<fmt:formatDate value="${restrictions.startTime}" pattern="HH:mm:ss"/>"/>
			  		<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_END%>" value="<fmt:formatDate value="${restrictions.endTime}" pattern="HH:mm:ss"/>"/>
			  		<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_LIMIT%>" value="<c:out value="${restrictions.bandwidth}"/>"/>
			  		<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_ALLOW_OPTIMIZE%>" value="<c:out value="${restrictions.allowOptimize}"/>"/>
			  		<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_ACTION%>" value="<%=BandwidthRestrictionsCommand.ACTION_EDIT%>"/>
		  			<a href=# onclick="javascript:document.bandwidth<c:out value="${count}"/>.submit(); return false;">		  					  		
		  		</authority:hasPrivilege>
		  		<c:out value="${restrictions.bandwidth}"/>
		  		<authority:hasPrivilege privilege="<%=Privilege.MANAGE_WEB_HARVESTER%>" scope="<%=Privilege.SCOPE_ALL%>">		  		
		  		</a>
		  		</form>
		  		</authority:hasPrivilege>
		  		</td>		  	
		  		</c:forEach>
		  	    </tr>
		  	  </table>
			</td>		    
		  </tr>
    	</table>
    </c:forEach>
	</td>
  </tr>
</table>

<div id="subBoxTop">
	<img src="images/subtabs-right.gif" alt="" width="13" height="34" border="0" align="right" />
<img src="images/subtabs-left.gif" alt="" width="11" height="34" border="0" /></div>		
<div id="subBoxContent">	
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
	  <tr>
		<td width="15" background="images/subbox-left-bg.gif"><img src="images/x.gif" width="15" height="1" border="0" /></td>
		<td bgcolor="#fffcf9" width="100%">

<form method="POST">
<span class="midtitleGrey">Target scheduling heatmap colors and thresholds</span>

<table>
	<input type="hidden" id=""<%=BandwidthRestrictionsCommand.PARAM_ACTION%>" name="<%=BandwidthRestrictionsCommand.PARAM_ACTION%>" value="<%=BandwidthRestrictionsCommand.ACTION_SAVE_HEATMAP%>"/>
	<tr>
		<th>Name</th>
		<th>Color</th>
		<th>Lowest threshold</th>
	</tr>
	<tr>
		<td><c:out value="${lowHeatmapConfig.displayName}"/></td>
		<td>
			<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_HEATMAP_CONFIG_LOW%>.oid" value="${lowHeatmapConfig.oid}"/>
			<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_HEATMAP_CONFIG_LOW%>.color" id="lowHeatmapColor" value="${lowHeatmapConfig.color}"/>
			<div class="colorPickerAnchor" id="lowHeatmapColorPicker"/><div></div></div>
		</td>
		<td><input type="text" class="threshold" name="<%=BandwidthRestrictionsCommand.PARAM_HEATMAP_CONFIG_LOW%>.thresholdLowest" value="${lowHeatmapConfig.thresholdLowest}"/></td>
	</tr>
	<tr>
		<td><c:out value="${mediumHeatmapConfig.displayName}"/></td>
		<td>
			<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_HEATMAP_CONFIG_MEDIUM%>.oid" value="${mediumHeatmapConfig.oid}"/>
			<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_HEATMAP_CONFIG_MEDIUM%>.color" id="mediumHeatmapColor" value="${mediumHeatmapConfig.color}"/>
			<div class="colorPickerAnchor" id="mediumHeatmapColorPicker"><div></div></div>
		</td>
		<td><input type="text" class="threshold" name="<%=BandwidthRestrictionsCommand.PARAM_HEATMAP_CONFIG_MEDIUM%>.thresholdLowest" value="${mediumHeatmapConfig.thresholdLowest}"/></td>
	</tr>
	<tr>
		<td><c:out value="${highHeatmapConfig.displayName}"/></td>
		<td>
			<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_HEATMAP_CONFIG_HIGH%>.oid" value="${highHeatmapConfig.oid}"/>
			<input type="hidden" name="<%=BandwidthRestrictionsCommand.PARAM_HEATMAP_CONFIG_HIGH%>.color" id="highHeatmapColor" value="${highHeatmapConfig.color}" />
			<div class="colorPickerAnchor" id="highHeatmapColorPicker"><div></div></div>
		</td>
		<td><input type="text" class="threshold" name="<%=BandwidthRestrictionsCommand.PARAM_HEATMAP_CONFIG_HIGH%>.thresholdLowest" value="${highHeatmapConfig.thresholdLowest}"/></td>
	</tr>
</table>
<authority:hasPrivilege privilege="<%=Privilege.MANAGE_WEB_HARVESTER%>" scope="<%=Privilege.SCOPE_ALL%>">		  		
<input type="image" src="images/generic-btn-save.gif"/>
<input type="image" src="images/generic-btn-cancel.gif" onclick="location.reload(true);return false;"/>
</authority:hasPrivilege>
</form>
       		</td>
		<td width="18" background="images/subbox-right-bg.gif"><img src="images/x.gif" width="18" height="1" border="0" /></td>
	  </tr>
	</table>
</div>
<div id="subBoxBtm"><img src="images/subbtm-right.gif" alt="" width="13" height="17" border="0" align="right" /><img src="images/subbtm-left.gif" alt="" width="11" height="17" border="0" /></div>
<p align="center">
</p>
</div>
