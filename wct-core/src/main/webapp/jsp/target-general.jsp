<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ page import="org.webcurator.domain.model.core.Target" %>
<%@page import="org.webcurator.ui.common.Constants" %>
<%@page import="org.webcurator.ui.target.command.TargetInstanceCommand" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>

<script>

  function stateChanged(originalState) {
	if (originalState == 4) return;
	if(document.getElementById('state').value=='4') {
		//user has selected rejected, so show rejection reasons
		document.getElementById('trReasons').style.display = 'block';
	} else {
		document.getElementById('trReasons').style.display = 'none';
	}
  }

</script>

<c:set var="editMode" value="${targetEditorContext.editMode}"/>

<table cellpadding="5" cellspacing="0" border="0" width="100%">
  <tr>
    <td class="subBoxTextHdr">Id:</td>
    <td class="subBoxText" colspan="2"><c:out value="${targetEditorContext.target.oid}"/></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr" width="20%">Name:</td>
    <td class="subBoxText" width="50%">
      <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
        <authority:show>
	      <input type="text" name="name" value="<c:out value="${command.name}"/>" style="width:350px;" maxlength="<%= Target.MAX_NAME_LENGTH %>"><font color=red size=2>&nbsp;<strong>*</strong></font>
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.name}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
    <td class="subBoxText">
    <c:choose>
    	<c:when test="${targetEditorContext.target.oid != null}">
		<a class="subBoxText" href="<%=Constants.CNTRL_TI_QUEUE%>?<%=TargetInstanceCommand.REQ_TYPE%>=<%=TargetInstanceCommand.TYPE_TARGET%>&<%=TargetInstanceCommand.PARAM_TARGET_NAME%>=<c:out value="${command.encodedName}"/>">View Target Instances</a>
		</c:when>
		<c:otherwise>
		&nbsp;
		</c:otherwise>
	</c:choose>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr" valign="top">Description:</td>
    <td class="subBoxText" colspan="2">
      <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
        <authority:show>
          <textarea name="description" cols="40" rows="6" style="width:350px;"><c:out value="${command.description}"/></textarea>
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.description}"/>
	    </authority:dont>
	  </authority:showControl>    
  </tr>
  <tr>
    <td class="subBoxTextHdr">Reference Number:</td>
    <td class="subBoxText" colspan="2">
      <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
        <authority:show>
	      <input type="text" name="reference" value="<c:out value="${command.reference}"/>" maxlength="<%= Target.MAX_REFERENCE_LENGTH %>">
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.reference}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Run on Approval:</td>
    <td class="subBoxText" colspan="2">
      <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
        <authority:show>
	      <input type="checkbox" name="runOnApproval" ${ command.runOnApproval ? 'CHECKED' : ''}>
	    </authority:show>
	    <authority:dont>
		  ${command.runOnApproval ? 'Yes' : 'No' }
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>  
  <tr style="display:${showAQAOption == 1 ? 'block':'none'}">
    <td class="subBoxTextHdr">Use Automated QA:</td>
    <td class="subBoxText" colspan="2">
      <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
        <authority:show>
	      <input type="checkbox" name="useAQA" value="true" ${ command.useAQA ? 'checked' : ''}>
	    </authority:show>
	    <authority:dont>
	      ${command.useAQA ? 'Yes' : 'No' }
	      <input type="hidden" name="useAQA" value="<c:out value="${command.useAQA ? 'true' : 'false'}"/>" />
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>  
  <tr>
    <td class="subBoxTextHdr">Owner:</td>
    <td class="subBoxText" colspan="2">
      <authority:showControl ownedObject="${targetEditorContext.target}" privileges="<%= Privilege.TAKE_OWNERSHIP %>" editMode="${editMode}">
        <authority:show>
      		<select name="ownerOid">
		        <c:forEach items="${allUsers}" var="user">
        		  <option value="<c:out value="${user.oid}"/>" ${user.oid == command.ownerOid ? 'SELECTED' : ''}><c:out value="${user.firstname} ${user.lastname}"/></option>
		        </c:forEach>
		    </select>
		</authority:show>
		<authority:dont>
			<c:forEach items="${allUsers}" var="user">
			  <c:if test="${user.oid == command.ownerOid}">
                <c:out value="${user.firstname} ${user.lastname}"/>
			  </c:if>
		    </c:forEach>	
		</authority:dont>
      </authority:showControl>
    </td>
  </tr>  
  <tr>
    <td class="subBoxTextHdr">State:</td>
      <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
        <authority:show>    
    	<td class="subBoxText" colspan="2">
	    	<select name="state" id="state" onchange="javascript: stateChanged(${originalState});">
	    	  <option value="<c:out value="${originalState}"/>" ${command.state == originalState ? 'SELECTED':''}><spring:message code="target.state_${originalState}"/></option>
	    	  <option value="-1">----------------</option>
	    	  <c:forEach items="${nextStates}" var="state">
	    	  <option value="<c:out value="${state}"/>" ${command.state == state ? 'SELECTED':''}><spring:message code="target.state_${state}"/></option>
	    	  </c:forEach>
	    	</select>&nbsp;
	    </td>
	    </authority:show>
	    <authority:dont>
    	<td class="subBoxText" colspan="2">
	      <spring:message code="target.state_${originalState}"/>
	    </td>
	    </authority:dont>
	  </authority:showControl>
  </tr>  
  <tr style="display:${originalState == 4 ? 'block':'none'}" id="trRejectedReason" name="trRejectedReason">
    <td class="subBoxTextHdr">Rejection&nbsp;Reason:</td>
    <td class="subBoxText" colspan="2">
	  <c:choose>
		<c:when test="${targetEditorContext.target.rejReason != null}">
			<c:out value="${targetEditorContext.target.rejReason.name}"/>
		</c:when>
		<c:otherwise>
			&nbsp;Reason&nbsp;Not&nbsp;Specified.
		</c:otherwise>
	  </c:choose>
    </td>
  </tr>
  <tr style="display:none" id="trReasons" name="trReasons">
    <td class="subBoxTextHdr">Choose&nbsp;Rejection&nbsp;Reason:</td>
    <td class="subBoxText" colspan="2">
  	  <select name="reasonOid" id="reasonOid">
  	    <c:forEach items="${rejReasons}" var="reason">
  	    <option value="<c:out value="${reason.oid}"/>"><c:out value="${reason.name}"/></option>
  	    </c:forEach>
  	  </select>
    </td>
  </tr>
    <tr>
    <td class="subBoxTextHdr">Auto-prune:</td>
    <td class="subBoxText" colspan="2">
      <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
        <authority:show>
	      <input type="checkbox" name="autoPrune" ${ command.autoPrune ? 'CHECKED' : ''}>
	    </authority:show>
	    <authority:dont>
		  ${command.autoPrune ? 'Yes' : 'No' }
	    </authority:dont>
	  </authority:showControl>
    </td>
      <tr>
    <td class="subBoxTextHdr">Reference Crawl:</td>
    <td class="subBoxText" colspan="2">
      <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
        <authority:show>
	      <input type="checkbox" name="autoDenoteReferenceCrawl" ${ command.autoDenoteReferenceCrawl ? 'CHECKED' : ''}>
	    </authority:show>
	    <authority:dont>
		  ${command.autoDenoteReferenceCrawl ? 'Yes' : 'No' }
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
    <tr>
    <td class="subBoxTextHdr" valign="top">Request to Archivists:</td>
    <td class="subBoxText" colspan="2">
      <authority:showControl ownedObject="${targetEditorContext.target}" privileges='<%= Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET %>' editMode="${editMode}">
        <authority:show>
          <textarea name="requestToArchivists" cols="40" rows="6" style="width:350px;"><c:out value="${command.requestToArchivists}"/></textarea>
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.requestToArchivists}"/>
	    </authority:dont>
	  </authority:showControl>    
  </tr>
  </tr>
</table>
</div>
