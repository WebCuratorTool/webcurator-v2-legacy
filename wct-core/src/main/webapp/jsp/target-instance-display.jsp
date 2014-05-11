<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@page import="org.webcurator.ui.target.command.TargetInstanceCommand" %>
<%@page import="org.webcurator.ui.common.Constants" %>

<style>
	.hidden {
		display: none;
	}
</style>
<input type="hidden" id="actionCmd" name="<%=TargetInstanceCommand.PARAM_CMD%>" value="<c:out value="${command.cmd}"/>">
<input type="hidden" name="<%=TargetInstanceCommand.PARAM_OID%>" value="<c:out value="${command.targetInstanceId}"/>"/>
<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
<table width="100%" cellpadding="3" cellspacing="0" border="0">
	<tr>
		<td class="subBoxTextHdr" width="20%" valign="top">
			<c:choose>
			<c:when test="${command.cmd eq 'edit'}">
			    <input type="checkbox" name="<%=TargetInstanceCommand.PARAM_DISPLAY%>" ${command.display ? 'checked' : ''}>Display Target Instance</input>
		    </c:when>
		    <c:otherwise>
			    <input type="checkbox" name="<%=TargetInstanceCommand.PARAM_DISPLAY%>" ${command.display ? 'checked' : ''} disabled>Display Target Instance</input>
		    </c:otherwise>
		    </c:choose>
		</td>
	    <td class="subBoxText" width="80%" valign="top">
	        <b>Reason for Display Change:&nbsp;</b><br />
			<c:choose>
			<c:when test="${command.cmd eq 'edit'}">
	    	  <textarea name="displayChangeReason" rows="<%=Constants.DISPLAY_CHANGE_REASON_ROWS%>" cols="<%=Constants.DISPLAY_CHANGE_REASON_COLS%>" style="width:280px;"><c:out value="${command.displayChangeReason}"/></textarea>
		    </c:when>
		    <c:otherwise>
	    	  <textarea name="displayChangeReason" rows="<%=Constants.DISPLAY_CHANGE_REASON_ROWS%>" cols="<%=Constants.DISPLAY_CHANGE_REASON_COLS%>" style="width:280px;" readOnly><c:out value="${command.displayChangeReason}"/></textarea>
		    </c:otherwise>
		    </c:choose>
	    </td>
	</tr>
	<tr>
		<td colspan="2" class="subBoxTitle">Target Instance Display Note:</td>
	</tr>
	<tr>
		<td colspan="2">
			<c:choose>
			<c:when test="${command.cmd eq 'edit'}">
				<textarea rows="<%= Constants.ANNOTATION_ROWS%>" cols="<%= Constants.ANNOTATION_COLS%>" name="<%= TargetInstanceCommand.PARAM_DISPLAY_NOTE %>"><c:out value="${command.displayNote}"/></textarea>
		    </c:when>
		    <c:otherwise>
				<textarea rows="<%= Constants.ANNOTATION_ROWS%>" cols="<%= Constants.ANNOTATION_COLS%>" name="<%= TargetInstanceCommand.PARAM_DISPLAY_NOTE %>" readOnly><c:out value="${command.displayNote}"/></textarea>
		    </c:otherwise>
		    </c:choose>
		</td>
	</tr>
</table>
