<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"%>
<%@page import="org.webcurator.ui.common.Constants"%>
<%@page import="org.webcurator.domain.model.core.AbstractTarget.AccessZone"%>


<table cellpadding="3" cellspacing="0" border="0">
  <tr>
    <td class="subBoxTextHdr" valign="top">
		<c:choose>
			<c:when test="${command.tabType eq 'Group'}">
			    Display Group:
			</c:when>
			<c:otherwise>
			    Display Target:
			</c:otherwise>
		</c:choose>				
    </td>
    <td class="subBoxText" valign="top">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
	      <input type="checkbox" name="displayTarget" ${ command.displayTarget ? 'CHECKED' : ''}>
	    </authority:show>
	    <authority:dont>
		  ${command.displayTarget ? 'Yes' : 'No' }
	    </authority:dont>
	  </authority:showControl>
    </td>
    <td class="subBoxText" valign="top">
      <b>Reason for Display Change:&nbsp;</b><br />
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
    	  <textarea name="displayChangeReason" rows="<%=Constants.DISPLAY_CHANGE_REASON_ROWS%>" cols="<%=Constants.DISPLAY_CHANGE_REASON_COLS%>" style="width:320px;"><c:out value="${command.displayChangeReason}"/></textarea>
	    </authority:show>
	    <authority:dont>
    	  <textarea name="displayChangeReason" rows="<%=Constants.DISPLAY_CHANGE_REASON_ROWS%>" cols="<%=Constants.DISPLAY_CHANGE_REASON_COLS%>" style="width:320px;" readOnly><c:out value="${command.displayChangeReason}"/></textarea>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>  
  <tr>
    <td class="subBoxTextHdr">Access Zone:</td>
    <td colspan="2" class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
      		<select name="accessZone">
      			<option value="<%=AccessZone.PUBLIC%>" ${command.accessZone == 0 ? 'SELECTED' : ''}><%=AccessZone.getText(AccessZone.PUBLIC)%></option>
      			<option value="<%=AccessZone.ONSITE%>" ${command.accessZone == 1 ? 'SELECTED' : ''}><%=AccessZone.getText(AccessZone.ONSITE)%></option>
      			<option value="<%=AccessZone.RESTRICTED%>" ${command.accessZone == 2 ? 'SELECTED' : ''}><%=AccessZone.getText(AccessZone.RESTRICTED)%></option>
		    </select>
		</authority:show>
		<authority:dont>
			  <c:if test="${command.accessZone == 0}">
                <c:out value="<%=AccessZone.getText(AccessZone.PUBLIC)%>"/>
			  </c:if>
			  <c:if test="${command.accessZone == 1}">
                <c:out value="<%=AccessZone.getText(AccessZone.ONSITE)%>"/>
			  </c:if>
			  <c:if test="${command.accessZone == 2}">
                <c:out value="<%=AccessZone.getText(AccessZone.RESTRICTED)%>"/>
			  </c:if>
		</authority:dont>
      </authority:showControl>
    </td>
  </tr>  
  <tr>
    <td class="subBoxTextHdr" valign="top">
		<c:choose>
			<c:when test="${command.tabType eq 'Group'}">
			    Group Introductory<br />Display Note:
			</c:when>
			<c:otherwise>
			    Target Introductory<br />Display Note:
			</c:otherwise>
		</c:choose>				
    </td>
    <td colspan="2" class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
          <textarea name="displayNote" rows="<%=Constants.ANNOTATION_ROWS%>" cols="<%=Constants.ANNOTATION_COLS%>" style="width:350px;"><c:out value="${command.displayNote}"/></textarea>
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.displayNote}"/>
	    </authority:dont>
	  </authority:showControl>    
  </tr>
</table>