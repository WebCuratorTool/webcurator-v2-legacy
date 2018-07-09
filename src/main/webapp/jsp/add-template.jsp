<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.common.Constants" %>
<%@ page import="org.webcurator.ui.admin.command.TemplateCommand" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>


<form name="addTemplate" action="<%= Constants.CNTRL_PERMISSION_TEMPLATE %>" method="POST">
<input type="hidden" name="<%= TemplateCommand.PARAM_OID %>" value="<c:out value="${command.oid}"/>"/>
<input type="hidden" name="<%= TemplateCommand.PARAM_EMAIL_TYPE_TEXT %>" value="<c:out value="${command.emailTypeText}"/>"/>
	<table cellpadding="3" cellspacing="0" border="0">
	<tr>
		<td class="subBoxTextHdr">Template Name:</td>
		<td class="subBoxText" colspan="2">
		<input type="text" style="width:400px;" name="<%= TemplateCommand.PARAM_TEMPLATE_NAME%>" value="<c:out value="${command.templateName}"/>"><font color=red size=2>&nbsp;<strong>*</strong></font>
		</td>
	</tr>
	<tr>
		<td class="subBoxTextHdr" valign=top>Template Description:</td>
		<td class="subBoxText" colspan="2">
		<textarea style="width:400px; height:100px;" name="<%= TemplateCommand.PARAM_TEMPLATE_DESCRIPTION%>"><c:out value="${command.templateDescription}"/></textarea>
		</td>
	</tr>
	<tr>
		<td class="subBoxTextHdr">Agency:</td>
		<td class="subBoxText" colspan="2">
		<select name="<%= TemplateCommand.PARAM_AGENCY_OID%>">
		        <c:forEach items="${agencies}" var="agency">
		          <option value="<c:out value="${agency.oid}"/>" <c:if test="${command.agencyOid == agency.oid}">selected</c:if>><c:out value="${agency.name}"/></option>
		        </c:forEach>
		</select>
		</td>
	</tr>
	<tr>
		<td class="subBoxTextHdr">Template type:</td>
		<td class="subBoxText" colspan="2">
		<select name="<%= TemplateCommand.PARAM_TEMPLATE_TYPE%>" id="<%= TemplateCommand.PARAM_TEMPLATE_TYPE%>" onchange="changeform();" >
		        <c:forEach items="${templateTypes}" var="type">
		          <option value="<c:out value="${type}"/>" <c:if test="${command.templateType == type}">selected</c:if>><c:out value="${type}"/></option>
		        </c:forEach>
		</select>
		</td>
	</tr>
	
	<tr>
		<td class="subBoxTextHdr">Template Subject:</td>
		<td class="subBoxText" colspan="2">
		<input type="text" style="width:400px;" id="<%= TemplateCommand.PARAM_TEMPLATE_SUBJECT%>" name="<%= TemplateCommand.PARAM_TEMPLATE_SUBJECT%>" value="<c:out value="${command.templateSubject}"/>"><font color=red size=2>&nbsp;<strong>*</strong></font>
		</td>
	</tr>
	
	<tr>
		<td class="subBoxTextHdr">Template Overwrite From:</td>
		<td class="subBoxText" colspan="2">
		<select id="<%= TemplateCommand.PARAM_TEMPLATE_OVERWRITE_FROM%>" name="<%= TemplateCommand.PARAM_TEMPLATE_OVERWRITE_FROM%>" onchange="changeform();">
		          <option value="false" <c:if test="${!command.templateOverwriteFrom}">selected</c:if> >No</option>
		          <option value="true" <c:if test="${command.templateOverwriteFrom}">selected</c:if> >Yes</option>
		</select>
		</td>
	</tr>
	
	<tr>
		<td class="subBoxTextHdr">Template From Address:</td>
		<td class="subBoxText" colspan="2">
		<input type="text" style="width:400px;" id="<%= TemplateCommand.PARAM_TEMPLATE_FROM%>" name="<%= TemplateCommand.PARAM_TEMPLATE_FROM%>" value="<c:out value="${command.templateFrom}"/>">
		</td>
	</tr>
	
	<tr>
		<td class="subBoxTextHdr" valign=top>Template CC Address(s):</td>
		<td class="subBoxText">
		<textarea style="width:400px; height:50px;" id="<%= TemplateCommand.PARAM_TEMPLATE_CC%>" name="<%= TemplateCommand.PARAM_TEMPLATE_CC%>"><c:out value="${command.templateCc}"/></textarea>
		</td>
		<td class="subBoxText" valign=top>		
		</td>
	</tr>
	
	<tr>
		<td class="subBoxTextHdr" valign=top>Template BCC Address(s):</td>
		<td class="subBoxText">
		<textarea style="width:400px; height:50px;" id="<%= TemplateCommand.PARAM_TEMPLATE_BCC%>" name="<%= TemplateCommand.PARAM_TEMPLATE_BCC%>"><c:out value="${command.templateBcc}"/></textarea>
		</td>
		<td class="subBoxText" valign=top>		
		</td>
	</tr>
	<tr>
		<td class="subBoxTextHdr">Template Reply-to Address:</td>
		<td class="subBoxText" colspan="2">
		<input type="text" style="width:400px;" id="<%= TemplateCommand.PARAM_TEMPLATE_REPLY_TO%>" name="<%= TemplateCommand.PARAM_TEMPLATE_REPLY_TO%>" value="<c:out value="${command.replyTo}"/>">
		</td>
	</tr>
	
		<tr>
		<td class="subBoxTextHdr" valign=top>Template text:</td>
		<td class="subBoxText">
		<textarea style="width:400px; height:300px;" name="<%= TemplateCommand.PARAM_TEMPLATE_TEXT%>"><c:out value="${command.templateText}"/></textarea>
		</td>
		<td class="subBoxText" valign=top><font color=red size=2>&nbsp;<strong>*</strong></font>		
		</td>
	</tr>

	
	<tr>
		<td  class="subBoxText" colspan="3" align="center">
		<input type="hidden" name="<%= TemplateCommand.PARAM_ACTION%>" value="<%= TemplateCommand.ACTION_SAVE %>">
		<input type="image" src="images/generic-btn-save.gif" border="0" />
		<a href="<%= Constants.CNTRL_PERMISSION_TEMPLATE %>"><img name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0"></a>
		</td>
	</tr>
	</table>
</form>

<script type="text/javascript">
<!--
function changeform()
{
	
	var hide1 = false;
	var hide2 = false;
	var colour1 = '';
	var colour2 = '';
	if(document.forms.addTemplate.<%=TemplateCommand.PARAM_TEMPLATE_TYPE%>.value != '<c:out value="${command.emailTypeText}"/>')
	{
		hide1 = true;
		hide2 = true;
		var colour1 = '#D5CCBB';
		var colour2 = '#D5CCBB';
	}
	else
	{
		if(document.forms.addTemplate.<%=TemplateCommand.PARAM_TEMPLATE_OVERWRITE_FROM%>.value == "false")
		{
			var colour2 = '#D5CCBB';
			hide2 = true;
		}
		
	}
	
	document.forms.addTemplate.<%=TemplateCommand.PARAM_TEMPLATE_SUBJECT%>.disabled = hide1;
	document.forms.addTemplate.<%=TemplateCommand.PARAM_TEMPLATE_OVERWRITE_FROM%>.disabled = hide1;
	document.forms.addTemplate.<%=TemplateCommand.PARAM_TEMPLATE_FROM%>.disabled = hide2;
	document.forms.addTemplate.<%=TemplateCommand.PARAM_TEMPLATE_CC%>.disabled = hide1;
	document.forms.addTemplate.<%=TemplateCommand.PARAM_TEMPLATE_BCC%>.disabled = hide1;
	
	document.forms.addTemplate.<%=TemplateCommand.PARAM_TEMPLATE_SUBJECT%>.style.background = colour1;
	document.forms.addTemplate.<%=TemplateCommand.PARAM_TEMPLATE_OVERWRITE_FROM%>.style.background = colour1;
	document.forms.addTemplate.<%=TemplateCommand.PARAM_TEMPLATE_FROM%>.style.background = colour2;
	document.forms.addTemplate.<%=TemplateCommand.PARAM_TEMPLATE_CC%>.style.background = colour1;
	document.forms.addTemplate.<%=TemplateCommand.PARAM_TEMPLATE_BCC%>.style.background = colour1;
	
	
	
}
changeform();
//-->
</script>