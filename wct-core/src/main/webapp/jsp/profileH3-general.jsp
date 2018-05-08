<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<table cellpadding="3" cellspacing="0" border="0">
  <tr>
    <td class="subBoxTextHdr">Name:</td>
    <td class="subBoxText"><input type="text" name="name" value="<c:out value="${command.name}"/>"><font color=red size=2>&nbsp;<strong>*</strong></font></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr" valign=top>H3 H3 Description:</td>
    <td class="subBoxText"><textarea cols="80" rows="5" name="description"><c:out value="${command.description}"/></textarea></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Agency:</td>
    <td class="subBoxText"><c:out value="${profile.owningAgency.name}"/></td>
  </tr>  
  <tr>
    <td class="subBoxTextHdr">State:</td>
    <td class="subBoxText">
    	<c:choose>
    		<c:when test="${!profile.defaultProfile}">
		    	<select name="status">
				  <c:forEach begin="0" end="1" var="i">
				    <option value="${i}" ${i == command.status ? 'SELECTED' : ''}><spring:message code="profile.state_${i}"/></option>
				  </c:forEach>
		    	</select>    		
    		</c:when>
    		<c:otherwise>
    			<spring:message code="profile.state_${command.status}"/>
    			<input type="hidden" name="status" value="${command.status}" />    			
    		</c:otherwise>
    	</c:choose>
    </td>
  </tr>  
  <tr>
    <td class="subBoxTextHdr">Level:</td>
    <td class="subBoxText">
    	<select name="requiredLevel">
		  <c:forEach begin="1" end="3" var="i">
		    <option value="${i}" ${i == command.requiredLevel ? 'SELECTED' : ''}>${i}</option>
		  </c:forEach>
    	</select>

    </td>
  </tr>  
</table>