<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.core.report.*" %>
<%@ page import="org.webcurator.core.report.parameter.*" %>
<%@ page import="org.webcurator.ui.report.controller.ReportController" %>
<%@ page import="org.apache.taglibs.standard.tag.common.core.ForEachSupport" %>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script type="text/javascript">

var browserType;

if (document.layers) {browserType = "nn4"};
if (document.all) {browserType = "ie"};
if (window.navigator.userAgent.toLowerCase().match("gecko")) {
   browserType= "gecko";
}

function hide(divName) {
  if (browserType == "gecko" )
     document.poppedLayer = 
         eval('document.getElementById("'+divName+'")');
  else if (browserType == "ie")
     document.poppedLayer = 
        eval('document.getElementById("'+divName+'")');
  else
     document.poppedLayer =   
        eval('document.layers["'+divName+'"]');
  document.poppedLayer.style.display = "none";
}

function show(divName) {
  if (browserType == "gecko" )
     document.poppedLayer = 
         eval('document.getElementById("'+divName+'")');
  else if (browserType == "ie")
     document.poppedLayer = 
        eval('document.getElementById("'+divName+'")');
  else
     document.poppedLayer = 
         eval('document.layers["'+divName+'"]');
  document.poppedLayer.style.display = "inline";
}

function HideAllParameterDivs() {
  for (var count=1; count<1000; count++) {
    if (document.getElementById("parmsDiv"+count)) {
      hide("parmsDiv"+count);
    } else {
      break;
    }
  }
}

function ShowParametersDiv(selectedReportOrdinal) {
	HideAllParameterDivs();
	show("parmsDiv"+selectedReportOrdinal);
}
 
function onSelectReportClicked(selectedReportName, selectedReportOrdinal) {
  hide("resultsTable");
  ShowParametersDiv(selectedReportOrdinal);
}

function onCancelClicked() {
  window.location.href  = "<%=request.getContextPath()%>/curator/report/report.html";
}

</script>

<%
List reports = (List) request.getAttribute("reports");
String selectedReport = (String) request.getSession().getAttribute("selectedRunReport");
boolean showSelectedReportParameters = !"".equals(selectedReport);
%>

<div id="resultsTable" style="display: <%=((showSelectedReportParameters==true) ? "none" : "inline")%>; height:400px; overflow:auto;">
<table width="100%" cellpadding="0" cellspacing="0" border="0">
<thead>  
  <tr> 
    <td class="tableHead"> Title </td>
    <td class="tableHead"> Description </td>
    <td class="tableHead">&nbsp;</td>
  </tr>
</thead>

<%
Integer rptOrdinal = 0;
for(Iterator itReports = reports.iterator(); itReports.hasNext(); ){
	Report report = (Report)itReports.next();
	rptOrdinal++;
%>
	<tr> 
	   <td class="tableRowLite" width="20%"><%=report.getName()%></td>
	   <td class="tableRowLite" width="60%"><%=report.getDescription()%></td>
	   <td class="tableRowLite" width="20%"><input type="image" src="images/home-btn-view.gif" onclick="javascript:onSelectReportClicked('<%=report.getName()%>', '<%=rptOrdinal%>');"/></td>
	</tr>
	<tr>			
		<td colspan="3" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="5" border="0" /></td>
	</tr>
<%
	}
%>	
	</table>
</div>


<%
Integer rptOrdinal2 = 0;
for(Iterator itReports = reports.iterator(); itReports.hasNext(); ){
	Report report = (Report)itReports.next();
	rptOrdinal2++;
%>
	<div id="parmsDiv<%=rptOrdinal2%>" style="display: <%=((selectedReport.equals(report.getName())) ? "inline" : "none")%>">
	<span><b><%=report.getName()%></b></span><br/>
	<span><%=report.getDescription()%></span><br/><br/>
	<%
		if(report.getInfo() != null && report.getInfo().length() > 0){
			out.print(report.getInfo() + "<br>");
		}
	%>
	<form name="frm<%=rptOrdinal2%>" id="frm<%=rptOrdinal2%>" action="curator/report/report.html" method="post">
    <input type="hidden" name="selectedReport" value="<%=report.getName()%>" />
	<table border="0">
	<%
		for(Iterator itParameters = report.getParameters().iterator(); itParameters.hasNext(); ) {
			Parameter prmt = (Parameter)itParameters.next();
	%>
			<tr>	
				<td class="tableRowLite"><%=prmt.getDescription()%>:</td>
				<input type="hidden" name="parameters" value="<%=report.getName()%>" >
				<input type="hidden" name="parameters" value="<%=prmt.getName()%>" >				
				<td class="tableRowLite"><%=prmt.getInputRendering()%>
				</td>
				<input type="hidden" name="parameters" value="<%=prmt.getType()%>" >
				<input type="hidden" name="parameters" value="<%=prmt.getDescription()%>" >
				<input type="hidden" name="parameters" value="<%=prmt.getOptional()%>" >
			</tr>
	<%
		}
	%>	
		<tr>
		    <td colspan=2>&nbsp;</td>
		</tr>
		<tr>
			<td>
			  <input type="image" src="images/reports-btn-generate.gif"/>
			</td>
			<td>
			  <input type="image" src="images/reports-btn-cancel.gif" onclick="javascript:onCancelClicked();return false;" />
			</td>
		</tr>
	</table>
	</form>
	</div>
<%
	}
%>	
