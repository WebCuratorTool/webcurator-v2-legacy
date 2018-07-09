<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.webcurator.ui.common.Constants"%>
<form name="siteAgency" method="POST" action="<%=Constants.CNTRL_SITE_AGENCY%>">
<input type="hidden" id="identity" name="identity" value="<c:out value="${command.identity}"/>">
<table>
  <tr>
    <td class="subBoxText">Name:</td>
    <td class="subBoxText">
      <c:choose>
        <c:when test="${authAgencyEditMode}"><input type="text" name="name" value="<c:out value="${command.name}"/>" size="60" maxlength="255"><font color=red size=2>&nbsp;<strong>*</strong></font></c:when>
        <c:otherwise><c:out value="${command.name}"/></c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td class="subBoxText">Description:</td>
    <td class="subBoxText">
      <c:choose>
        <c:when test="${authAgencyEditMode}"><textarea cols="62" rows="4" name="description"><c:out value="${command.description}"/></textarea></c:when>
        <c:otherwise><c:out value="${command.description}"/></c:otherwise>
      </c:choose>
    </td>    
  </tr>
  <tr>
    <td class="subBoxText">Contact:</td>
    <td class="subBoxText">
      <c:choose>
        <c:when test="${authAgencyEditMode}"><input type="text" name="contact" value="<c:out value="${command.contact}"/>" size="60" maxlength="255"><font color=red size=2>&nbsp;<strong>*</strong></font></c:when>
        <c:otherwise><c:out value="${command.contact}"/></c:otherwise>
      </c:choose>
	</td>
  </tr>  
  <tr>
    <td class="subBoxText">Phone:</td>
    <td class="subBoxText">
      <c:choose>
        <c:when test="${authAgencyEditMode}"><input type="text" name="phoneNumber" value="<c:out value="${command.phoneNumber}"/>" size="60" maxlength="32"></c:when>
        <c:otherwise><c:out value="${command.phoneNumber}"/></c:otherwise>
      </c:choose>
    </td>
  </tr>    
  <tr>
    <td class="subBoxText">Email:</td>
    <td class="subBoxText">
      <c:choose>
        <c:when test="${authAgencyEditMode}"><input type="text" name="email" value="<c:out value="${command.email}"/>" size="60" maxlength="255"></c:when>
        <c:otherwise><c:out value="${command.email}"/></c:otherwise>
      </c:choose>
    </td>
  </tr>      
  <tr>
    <td class="subBoxText">Address:</td>
    <td class="subBoxText">
      <c:choose>
        <c:when test="${authAgencyEditMode}"><textarea name="address" cols="62" rows="4" ><c:out value="${command.address}"/></textarea></c:when>
        <c:otherwise><c:out value="${command.address}"/></c:otherwise>
      </c:choose>
    </td>
  </tr>
</table>
<input type="hidden" name="cmdAction" value="" /> 
<p align="center">
<c:if test="${authAgencyEditMode}">
<input type="image" name="_save_auth_agent" src="images/generic-btn-save.gif" alt="Save" width="82" height="23" border="0" onclick="javascript:document.siteAgency.cmdAction.value='Save'"><img src="images/x.gif" alt="" width="10" height="1" border="0" />
</c:if>
<input type="image" name="_cancel_auth_agent" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0">
</p>
</form>