<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@ taglib prefix="wct" uri="http://www.webcurator.org/wct"  %>

<script>
  function selectItem(id) {
	document.getElementById("selectedItem").value = id;
	return true;
  }
</script>

<c:if test="${groupEditorContext.editMode}">
	<authority:hasUserOwnedPriv privilege="<%= Privilege.MANAGE_GROUP_SCHEDULE %>" ownedObject="${groupEditorContext.targetGroup}">
		<input type="hidden" id="selectedItem" name="selectedItem">
		<input type="submit" name="_new" value="Add">
	</authority:hasUserOwnedPriv>
</c:if>

<table>
  <tr>
    <th>Schedule</th>
    <th>Owner</th>
    <th>Next Scheduled Time</th>
    <th>Action</th>
  </tr>

<c:forEach items="${schedules}" var="schedule" varStatus="status">
  <tr>
    <td>
      <c:choose>
        <c:when test="${schedule.scheduleType == 0}">
        <c:out value="${schedule.cronPatternWithoutSeconds}"/>
        </c:when>
        <c:otherwise>
          ${patternMap[schedule.scheduleType].description}
        </c:otherwise>
      </c:choose>
    </td>
        
    <td>Owner</td>
    <td><wct:date value="${schedule.nextExecutionDate}" type="fullDateTime"/></td>
    <td>
      <c:if test="${targetEditorContext.editMode}">
        <authority:hasUserOwnedPriv privilege="<%= Privilege.MANAGE_GROUP_SCHEDULE %>" ownedObject="${groupEditorContext.targetGroup}">
          <input class="linkButton" type="submit" name="_remove" value="Remove" onclick="selectItem('<c:out value="${schedule.identity}"/>')"/>
          <input class="linkButton" type="submit" name="_edit" value="Edit" onclick="selectItem('<c:out value="${schedule.identity}"/>')"/>
        </authority:hasUserOwnedPriv>
      </c:if>
    </td>
  </tr>
</c:forEach>

</table>