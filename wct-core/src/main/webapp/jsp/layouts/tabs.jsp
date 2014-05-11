<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>

<input type="hidden" name="_tab_current_page" value="<c:out value="${tabStatus.currentTab.pageId}"/>">

<div class="tabs">
<c:choose>
  <c:when test="${tabStatus.enabled}">
    <c:forEach items="${tabs.tabs}" var="tab">
     <c:choose>
        <c:when test="${tab == tabStatus.currentTab}">
          <c:out value="${tab.title}"/>
        </c:when>
        <c:otherwise>    
	      <input type="submit" name="_tab_change" value="<c:out value="${tab.title}"/>">
	    </c:otherwise>
	  </c:choose>
    </c:forEach>
  </c:when>
  <c:otherwise>
    <c:forEach items="${tabs.tabs}" var="tab">
      <c:choose>
        <c:when test="${tab == tabStatus.currentTab}">
          <c:out value="${tab.title}"/>
        </c:when>
        <c:otherwise>
          <div class="disabled"><c:out value="${tab.title}"/></div>
        </c:otherwise>
      </c:choose>
      
    </c:forEach>
  </c:otherwise>
</c:choose>
</div>
