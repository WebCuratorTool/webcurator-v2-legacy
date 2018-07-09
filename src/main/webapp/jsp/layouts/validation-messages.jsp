<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<br />
<spring:hasBindErrors name="command">  
	<table border="1px" RULES=NONE BORDERCOLOR="#FF0000" bgcolor="#F4F0E7">  
		<tr valign="top">
			<td><font color="red" size="2"><strong>The following validation errors have occured.</strong></font></td>
		</tr>			
		<spring:bind path="command.*">
	       <c:forEach var="errorMessage" items="${status.errorMessages}">
	       <tr>
	          <td><font color="red" size="2"><c:out value="${errorMessage}"/></font></td>
	       </tr>
	       </c:forEach>
		</spring:bind>	
	</table>
</spring:hasBindErrors>
<c:if test="${page_message != null}">
	<table border="2pt" BORDERCOLOR="#FF0000" bgcolor="#F4F0E7" id="messageBox">  	
		<tr valign="top">
            <td><font color="black" size="2"><strong><c:out value="${page_message}"/></strong></font></td>
		</tr>
	</table>
</c:if>	
	
				