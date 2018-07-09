<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>

<table>
  <tr>
    <td class="subBoxTextHdr">Authorising Agent:</td>
    <td class="subBoxText"><c:out value="${permission.authorisingAgent.name}"/></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Dates:</td>
    <td class="subBoxText">
      <wct:date value="${permission.startDate}" type="fullDate"/>
      <c:choose>
        <c:when test="${permission.endDate != null}">
         to 
        <wct:date value="${permission.endDate}" type="fullDate"/>
        </c:when>
        <c:otherwise>
          (open ended)
        </c:otherwise>
      </c:choose>
    </td>
  </tr>  
  <tr>  
    <td class="subBoxTextHdr">Status:</td>
    <td class="subBoxText"><spring:message code="permission.state_${permission.status}"/></td>
  </tr>  
  
  <tr>
    <td class="subBoxTextHdr" valign="top">Auth. Agency Response:</td>
    <td class="subBoxText"><c:out value="${permission.authResponse}"/></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr" valign="top">Special Restrictions:</td>
    <td class="subBoxText"><c:out value="${permission.specialRequirements}"/></td>
  </tr>

  <tr>
    <td class="subBoxTextHdr">Quick Pick:</td>
    <td class="subBoxText">${permission.quickPick ? 'Yes' : 'No'}</td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Display Name:</td>
    <td class="subBoxText"><c:out value="${permission.displayName}"/></td>
  </tr>  

  
  <tr>
    <td class="subBoxTextHdr">Urls:</td>
    <td class="subBoxText">
      <c:forEach items="${permission.urls}" var="url" varStatus="status">
	    <c:out value="${url.pattern}"/><br/>
      </c:forEach>
    </td>
  </tr>
  
  <tr>
    <td class="subBoxTextHdr">File Reference:</td>
    <td class="subBoxText"><c:out value="${permission.fileReference}"/></td>
  </tr>    
</table>
  
<%-- Exclusions Section --%>
<div id="annotationsBox">
  <span class="subBoxTitle">Exclusions</span><br/>

  <table width="100%" cellpadding="3" cellspacing="0" border="0">
  	  <tr>
	    <td class="annotationsHeaderRow">URL</td>
	    <td class="annotationsHeaderRow">Reason</td>
	  </tr>
  <c:choose>
	<c:when test="${empty permission.exclusions}">
	  <tr>
	    <td class="annotationsListRow" colspan="2">No exclusions have been defined.</td>
	  </tr>
	</c:when>
	<c:otherwise>
	  <c:forEach items="${permission.exclusions}" var="exclusion" varStatus="varStatus">
	  <tr>
	    <td class="annotationsLiteRow"><c:out value="${exclusion.url}"/></td>
	    <td class="annotationsLiteRow"><c:out value="${exclusion.reason}"/></td>	    
	  </tr>
	  </c:forEach>
	</c:otherwise>
  </c:choose>
  <table>
</div> 

<div id="annotationsBox">
  <span class="subBoxTitle">Annotations</span><br />
  <table width="100%" cellpadding="3" cellspacing="0" border="0">
    <tr>
	  <td class="annotationsHeaderRow">Date</td>
	  <td class="annotationsHeaderRow">User</td>
	  <td class="annotationsHeaderRow">Notes</td>
	</tr>
	<c:choose>
	<c:when test="${empty permission.annotations}">
	  <tr>
		<td colspan="3"><spring:message code="ui.label.common.noAnnotations"/></td>
      </tr>
	</c:when>
	<c:otherwise>
	  <c:forEach items="${permission.annotations}" var="anno">
		<tr>
		  <td class="annotationsLiteRow"><fmt:formatDate value="${anno.date}" pattern="dd MMM yyyy HH:mm:ss"/></td>
		  <td class="annotationsLiteRow"><c:out value="${anno.user.niceName}"/></td>
		  <td class="annotationsLiteRow"><c:out value="${anno.note}"/></td>
		</tr>
	  </c:forEach>					
	</c:otherwise>
    </c:choose>
  </table>
</div>			
