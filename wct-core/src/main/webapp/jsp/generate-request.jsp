<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page
	import="org.webcurator.ui.site.command.GeneratePermissionTemplateCommand"%>
<%@ page import="org.webcurator.ui.common.Constants"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">

	function printRequest(printAction) {
	  var inputAction = document.getElementById("action");
	  inputAction.value = printAction;
	  window.print();
	}
	
	function emailRequest(emailAction) {
	  var inputAction = document.getElementById("action");
	  inputAction.value = emailAction;
	}

</script>


<img src="images/x.gif" alt="" width="1" height="30" border="0" />
<br />
<table width="100%" cellpadding="0" cellspacing="0" border="0" align="center">
	<tr>
		<td>
			<div class="noPrint">
				<span class="midtitleGrey">Request Permission Preview</span>
			</div>
		</td>
	</tr>
	<tr>
		<td>
			&nbsp;
			<br />
			&nbsp;
			<br />
		</td>
	</tr>
	<c:if test="${template.templateType eq 'Email Template'}">
		<tr>
			<td>
				
				<table>
					<c:if test="${template.templateOverwriteFrom}">
						<tr>
							<td width="100px">
								<span class="smalltitleGrey">From:</span>
							</td>
							<td>
								<pre><c:out value="${template.templateFrom}" /></pre>
							</td>
						</tr>
					</c:if>
					<tr>
						<td width="100px">
							<span class="smalltitleGrey">Subject:</span>
						</td>
						<td>
							<pre><c:out value="${template.templateSubject}" /></pre>
						</td>
					</tr>
					<c:if
						test='${template.templateCc != null && template.templateCc != ""}'>
						<tr>
							<td width="100px">
								<span class="smalltitleGrey">CC:</span>
							</td>
							<td>
								<pre><c:out value="${template.templateCc}" /></pre>
							</td>
						</tr>
					</c:if>
					<c:if
						test='${template.templateBcc != null && template.templateBcc != ""}'>
						<tr>
							<td width="100px">
								<span class="smalltitleGrey">BCC:</span>
							</td>
							<td>
								<pre><c:out value="${template.templateBcc}" /></pre>
							</td>
						</tr>
					</c:if>
					<tr>
							<td width="100px">
								<span class="smalltitleGrey">Email Text:</span>
							</td>
							<td>
								
							</td>
						</tr>
				</table>
			</td>
		</tr>
	</c:if>
	<tr>
		<td>
			<pre><c:out value="${template.parsedText}" escapeXml="false" /></pre>
		</td>
	</tr>
</table>
<div class="noPrint">
	<form name="frmSend" action="<%=Constants.CNTRL_GENERATE_TEMPLATE%>"
		method="POST">
		<table border="0" width="25%" cellpadding="0" cellspacing="0"
			align="center">
			<tr>
				<td align="center">
					<input type="hidden"
						name="<%=GeneratePermissionTemplateCommand.PARAM_TEMPLATE_OID%>"
						value="<c:out value="${template.oid}"/>">
					<input type="hidden"
						name="<%=GeneratePermissionTemplateCommand.PARAM_PERMISSION_OID%>"
						value="<c:out value="${command.permissionOid}"/>" />
					<input type="hidden"
						name="<%=GeneratePermissionTemplateCommand.PARAM_SITE_OID%>"
						value="<c:out value="${command.siteOid}"/>" />
					<input type="hidden"
						id="<%=GeneratePermissionTemplateCommand.PARAM_ACTION%>"
						name="<%=GeneratePermissionTemplateCommand.PARAM_ACTION%>"
						value="" />
					<input type="image" src="images/harvest-btn-print.gif"
						onclick="javascript:printRequest('<%=GeneratePermissionTemplateCommand.ACTION_PRINTIT%>');">
				</td>
				<c:if test="${template.templateType eq 'Email Template'}">
					<td align="center">
						<input type="image" title="email"
							src="images/harvest-btn-email.gif"
							onclick="javascript:emailRequest('<%=GeneratePermissionTemplateCommand.ACTION_SEND_EMAIL%>');">
					</td>
					<td align="center">
						<a
							href="<%=Constants.CNTRL_GENERATE_TEMPLATE%>?siteOid=${command.siteOid}"><img
								src="images/harvest-btn-done.gif" border="0"> </a>
					</td>
				</c:if>
			</tr>
		</table>
	</form>
</div>
