<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="resultsTable">

<table width="100%" cellpadding="0" cellspacing="0" border="0">	
	<tr>
		<td><span class="midtitleGrey">Maximum File Upload Size Exceeded</span></td>
	</tr>
	<tr>
		<td class="tableRowDark">&nbsp;</td>
	</tr>
	<tr>
		<td class="tableRowDark">
		
		<p>The system has been configured to accept files up to <c:out value="${exception.maxUploadSize}"/> bytes.</p>
		
		<form id="tabForm" name="tabForm" method="post" action="curator/target/target.html">
			<input type="hidden" name="actionCmd" value="START_IMPORT">
			<input type="hidden" name="_tab_current_page" value="SEEDS">
			<a href="javascript: history.go(-1)">Back</a>
		</form>
		
		</td>
	</tr>
	<tr>
		<td class="tableRowDark">&nbsp;</td>
	</tr>	
</table>
</div>

