<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="org.webcurator.ui.target.command.TargetInstanceCommand" %>
<%@page import="org.webcurator.domain.model.auth.Privilege"%>

<input type="hidden" name="<%=TargetInstanceCommand.PARAM_OID%>" value="<c:out value="${command.targetInstanceId}"/>"/>
<input type="hidden" name="<%=TargetInstanceCommand.PARAM_CMD%>" value="<c:out value="${command.cmd}"/>"/>
<input type="hidden" name="<%=TargetInstanceCommand.PARAM_HR_ID%>" value=""/>
<input type="hidden" name="<%=TargetInstanceCommand.PARAM_REJREASON_ID%>" value=""/>

<script>

  function clickReIndex(hrOid) {
    if( confirm('<spring:message code="ui.label.targetinstance.results.confirmReIndex" javaScriptEscape="true"/>')) {
  	  document.forms['tabForm'].<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_REINDEX%>'; 
	  document.forms['tabForm'].<%=TargetInstanceCommand.PARAM_HR_ID%>.value=hrOid; 
	  document.forms['tabForm'].submit(); 
	}
	return false;
  }
  
  function clickReject(hrOid) {
    if( confirm('<spring:message code="ui.label.targetinstance.results.confirmReject" javaScriptEscape="true"/>')) {
  	  document.forms['tabForm'].<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_REJECT%>'; 
	  document.forms['tabForm'].<%=TargetInstanceCommand.PARAM_HR_ID%>.value=hrOid; 
	  document.forms['tabForm'].<%=TargetInstanceCommand.PARAM_REJREASON_ID%>.value=document.getElementsByName('<%=TargetInstanceCommand.PARAM_REASON%>'+hrOid)[0].value; 
	  document.forms['tabForm'].submit(); 
	}
	return false;
  }
  
  function clickEndorse(hrOid) { 
    if( confirm('<spring:message code="ui.label.targetinstance.results.confirmEndorse" javaScriptEscape="true"/>')) {
      document.forms['tabForm'].<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_ENDORSE%>'; 
      document.forms['tabForm'].<%=TargetInstanceCommand.PARAM_HR_ID%>.value=hrOid; 
      document.forms['tabForm'].submit(); 
    }
    return false;
  }
  
  function clickUnEndorse(hrOid) { 
    if( confirm('<spring:message code="ui.label.targetinstance.results.confirmUnEndorse" javaScriptEscape="true"/>')) {
      document.forms['tabForm'].<%=TargetInstanceCommand.PARAM_CMD%>.value='<%=TargetInstanceCommand.ACTION_UNENDORSE%>'; 
      document.forms['tabForm'].<%=TargetInstanceCommand.PARAM_HR_ID%>.value=hrOid; 
      document.forms['tabForm'].submit(); 
    }
    return false;
  }

  //When viewing this target instance from the "Harvest history" screen, clicking on "review" will 
  //cause the "Return to Harvest History Page" to forget which target instance was originally being
  //reviewed.  Warn the user to try and prevent mistakes.
  function checkForHistory() {
	//Loaded from JSP
	var historyInSession = <%= session.getAttribute( "sessionHarvestHistoryTIOid" )!= "" && session.getAttribute( "sessionHarvestHistoryTIOid" )!= null%>;
	if(historyInSession===true) {
	  return confirm("WARNING: As you were previously reviewing another target instance from the Harvest History screen,"
	  	+ " reviewing this target instance will cause WCT to forget the previous target instance being reviewed.  Please"
	  	+ " carefully check the target instance ID when endorsing or rejecting.");
	}
	return true;
  }

</script>

<div id="annotationsBox">
	<table width="100%" cellpadding="3" cellspacing="0" border="0">
		<tr>
			<td class="annotationsHeaderRow">No.</td>
			<td class="annotationsHeaderRow">Date</td>
			<td class="annotationsHeaderRow">Derived From</td>
			<td class="annotationsHeaderRow">User</td>
			<td class="annotationsHeaderRow">Notes</td>
			<td class="annotationsHeaderRow">State</td>
			<td class="annotationsHeaderRow">Action</td>
		</tr>
		<c:choose>
			<c:when test="${empty results}">
			<tr> 
			<td class="annotationsLiteRow" colspan="6"><spring:message code="ui.label.targetinstance.results.noResults"/></td>
			</tr>
			</c:when>
			<c:otherwise>
				<c:forEach var="hr" items="${results}">
				  <tr>
				    <td class="annotationsLiteRow"><c:out value="${hr.harvestNumber}"/></td>
				    <td class="annotationsLiteRow"><wct:date value="${hr.creationDate}" type="fullDateTime"/></td>
   				    <td class="annotationsLiteRow"><c:out value="${hr.derivedFrom}"/></td>
				    <td class="annotationsLiteRow"><c:out value="${hr.createdBy.niceName}"/></td>
				    <td class="annotationsLiteRow"><c:out value="${hr.provenanceNote}"/></td>
				    <td class="annotationsLiteRow">
				    <c:choose>
						<c:when test="${hr.state == 1}">
							Endorsed
						</c:when>
						<c:when test="${hr.state == 2}">
							Rejected
						</c:when>
						<c:when test="${hr.state == 3}">
						    Indexing
						</c:when>
						<c:when test="${hr.state == 4}">
						    Aborted
						</c:when>
				    </c:choose>  
				    </td>
				    <td class="annotationsLiteRow">
				    <c:if test="${editMode && hr.state != 4}">
				    	<c:choose>
				    	
				    		<c:when test="${hr.state eq 3}"> <!-- Indexing -->
				    		<authority:hasPrivilege privilege="<%=Privilege.ENDORSE_HARVEST%>" scope="<%=Privilege.SCOPE_OWNER%>">    		
					    		<a href="#" onclick="javascript: return clickReIndex(<c:out value="${hr.oid}"/>);">Restart Indexing</a>
					    		<c:choose>
						    		<c:when test="${wct:xHoursElapsed(12,hr.creationDate)}">
								    	&nbsp;|&nbsp;<a href="#" onclick="javascript: return clickReject(<c:out value="${hr.oid}"/>); ">Reject&nbsp;(Stuck&nbsp;in&nbsp;Indexing&nbsp;State):</a>&nbsp;
						    			<select name="<%=TargetInstanceCommand.PARAM_REASON%><c:out value="${hr.oid}"/>">				
											<c:forEach items="${reasons}" var="o">
												<option value="<c:out value="${o.oid}"/>"><c:out value="${o.name}"/></option>
											</c:forEach>
										</select>&nbsp;    			
						    		</c:when>
					    		</c:choose>
					    	</authority:hasPrivilege>
					    	&nbsp;
				    		</c:when>
				    		
				    		<c:when test="${instance.state eq 'Harvested' && hr.state != 3}">    		
				    		<authority:hasPrivilege privilege="<%=Privilege.ENDORSE_HARVEST%>" scope="<%=Privilege.SCOPE_OWNER%>">    		
					    	<a href="curator/target/quality-review-toc.html?targetInstanceOid=<c:out value="${hr.targetInstance.oid}"/>&harvestResultId=<c:out value="${hr.oid}"/>&harvestNumber=<c:out value="${hr.harvestNumber}"/>" onclick="return checkForHistory()">Review</a>
					    	&nbsp;|&nbsp;
					    	<a href="#" onclick="javascript: return clickEndorse(<c:out value="${hr.oid}"/>);">Endorse</a>
					    	&nbsp;|&nbsp;
					    	<c:choose>
					    		<c:when test="${hr.state eq 2}">
					    		Rejection&nbsp;Reason:&nbsp;<c:out value="${hr.rejReason.name}"/>    		
					    		</c:when>
					    		<c:otherwise>
							    	<a href="#" onclick="javascript: return clickReject(<c:out value="${hr.oid}"/>); ">Reject&nbsp;for&nbsp;Reason:</a>&nbsp;
					    			<select name="<%=TargetInstanceCommand.PARAM_REASON%><c:out value="${hr.oid}"/>">				
										<c:forEach items="${reasons}" var="o">
											<option value="<c:out value="${o.oid}"/>"><c:out value="${o.name}"/></option>
										</c:forEach>
									</select>&nbsp;    			
					    		</c:otherwise>
					    	</c:choose>
					    	</authority:hasPrivilege>
					    	&nbsp;
				    		</c:when>
				    		
				    		<c:when test="${(instance.state eq 'Endorsed' || instance.state eq 'Rejected') && hr.state != 3}">    		   		    		
					    	<authority:hasPrivilege privilege="<%=Privilege.ARCHIVE_HARVEST%>" scope="<%=Privilege.SCOPE_OWNER%>">
					    	<c:if test="${hr.state == 1}">
					    	<c:choose>
					    	<c:when test="${customDepositFormRequired}">
					    	<a href="curator/target/deposit-form-envelope.html?targetInstanceID=<c:out value="${hr.targetInstance.oid}"/>&harvestResultNumber=<c:out value="${hr.harvestNumber}"/>">Next</a>
					    	</c:when>
					    	<c:otherwise>
				    		<a href="curator/archive/submit.html?instanceID=<c:out value="${hr.targetInstance.oid}"/>&harvestNumber=<c:out value="${hr.harvestNumber}"/>" onclick="return confirm('<spring:message code="ui.label.targetinstance.results.confirmSubmit" javaScriptEscape="true"/>');">Submit to Archive</a>
				    		</c:otherwise>
				    		</c:choose>
				    		</c:if>
					    	</authority:hasPrivilege>    		
					    	<authority:hasPrivilege privilege="<%=Privilege.UNENDORSE_HARVEST%>" scope="<%=Privilege.SCOPE_OWNER%>">
					    	<c:if test="${hr.state == 1}">
					    	&nbsp;|&nbsp;
					    	<a href="#" onclick="javascript: return clickUnEndorse(<c:out value="${hr.oid}"/>);">UnEndorse</a>
				    		</c:if>
					    	<c:if test="${hr.state eq 2}">
					    	Rejection&nbsp;Reason:&nbsp;<c:out value="${hr.rejReason.name}"/>    		
					    	</c:if>
					    	</authority:hasPrivilege>    		
				    		</c:when>
				    		<c:otherwise>
				    		&nbsp;
				    		</c:otherwise>
				    	</c:choose>
				    </c:if>
				    <c:if test="${!editMode}">
				    &nbsp;
					    		<c:if test="${hr.state eq 2}">
					    		Rejection&nbsp;Reason:&nbsp;<c:out value="${hr.rejReason.name}"/>    		
					    		</c:if>
					    		
				    </c:if>
				    </td>
				  </tr>
			  </c:forEach>
		</c:otherwise>
		</c:choose>
	</table>
</div>