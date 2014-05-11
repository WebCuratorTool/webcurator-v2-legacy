<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@ taglib prefix="wct" uri="http://www.webcurator.org/wct"  %>
<%@ page import="org.webcurator.ui.groups.command.GroupAnnotationCommand" %>
<%@ page import="org.webcurator.ui.common.Constants" %>
<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>

<style>
	.hidden {
		display: none;
	}
</style>
<script language="javascript">

  function setAction(index, str) {
    document.getElementById('noteIndex').value = index;
   	document.getElementById('actionCmd').value = str;
  }

  function setEdit(index) {
    document.getElementById('noteIndex').value = index;
    document.getElementById('btnAdd').className="hidden"; 
    document.getElementById('btnChange').className=""; 
    document.getElementById('note').value = document.getElementById('note'+index).innerHTML;

	var table = document.getElementById('tblAnnotations');
    for(i = 0; i <  table.childNodes.length; i++)
    {
    	var row = table.childNodes[i];
     	if((row.tagName == "TR" || row.tagName == "tr") && row.id != "header" && row.id != "empty")
    	{
		    for(j = 0; j <  row.childNodes.length; j++)
		    {
		    	var col = row.childNodes[j];
		     	if(col.tagName == "TD" || col.tagName == "td")
		    	{
		    		if(row.id == "row"+index)
		    		{
			    		col.className = "annotationsDarkRow";
		    		}
		    		else
		    		{
			    		col.className = "annotationsLiteRow";
		    		}
		    	}
		    } 
    	}
    } 
  }
  function promptSave() {
    var save = confirm('Save group while adding annotation?'); ;
    if(save) {
      $("#hiddenSaveParam").val("save").attr("name", "_tab_save");
    }
    setAction(-1,'<%= GroupAnnotationCommand.ACTION_ADD_NOTE %>');
  }
</script>
	<input id="actionCmd" name="actionCmd" type="hidden">
	<input id="noteIndex" name="<%= GroupAnnotationCommand.PARAM_INDEX %>" value="-1"  type="hidden">
		 <div id="annotationsBox">
			<span class="subBoxTitle">Annotations</span><br />
		 	  <c:if test="${groupEditorContext.editMode}">
				<textarea id="note" rows="<%= Constants.ANNOTATION_ROWS%>" cols="<%= Constants.ANNOTATION_COLS%>" name="<%= GroupAnnotationCommand.PARAM_NOTE %>"><c:out value="${command.note}"/></textarea>
         	    <input id="hiddenSaveParam" name="" type="hidden" value=""/>
		     	<input id="btnAdd" type="image" src="images/subtabs-add-btn.gif" alt="Add" width="49" height="23" border="0" hspace="5" align="absmiddle" onclick="promptSave();">
		     	<input id="btnChange" class="hidden" type="image" src="images/subtabs-change-btn.gif" alt="Change" width="49" height="23" border="0" hspace="5" align="absmiddle" onclick="setAction(document.getElementById('noteIndex').value,'<%= GroupAnnotationCommand.ACTION_MODIFY_NOTE%>');">
				<br />
			</c:if>
			<img src="images/x.gif" alt="" width="1" height="10" border="0" /><br />
     	    
			
			<table width="100%" cellpadding="3" cellspacing="0" border="0">
			<tbody id="tblAnnotations">
				<tr id="header">
					<td class="annotationsHeaderRow">Date</td>
					<td class="annotationsHeaderRow">User</td>
					<td class="annotationsHeaderRow">Notes</td>
		 	  		<c:choose>
		 	  			<c:when test="${groupEditorContext.editMode}">
							<td class="annotationsHeaderRow">Action</td>
						</c:when>
						<c:otherwise>
							<td class="annotationsHeaderRow"></td>
						</c:otherwise>
					</c:choose>
				</tr>
				<c:choose>
				  	<c:when test="${empty annotations}">
				  	<tr id="empty">
				    	<td class="annotationsLiteRow" colspan="4">There are no annotations available.</td>
				  	</tr>
				  	</c:when>
				  	<c:otherwise>
						<c:set var="count" scope="page" value="0"/>
				  		<c:forEach items="${annotations}" var="a">
				  			<tr id="row<c:out value="${count}"/>">
				  				<td class="annotationsLiteRow"><wct:date value="${a.date}" type="shortDateTime"/></td>
								<td class="annotationsLiteRow"><c:out value="${a.user.niceName}"/></td>
				  				<td id="note<c:out value="${count}"/>" class="annotationsLiteRow"><c:out value="${a.note}"/></td>  				
				  				<td class="annotationsLiteRow">
					      	 	<c:if test="${groupEditorContext.editMode}">
				  				<c:if test="${a.user.username eq command.username}">
								<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
								<img style="cursor:pointer;" title="edit" src="images/action-icon-edit.gif" alt="click here to Edit this item" border="0"
								 	onclick="javascript:setEdit(<c:out value="${count}"/>);"/>
								
								<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
								<input type="image" title="delete" src="images/action-icon-delete.gif" alt="click here to Delete this item" border="0"
								 	onclick="javascript:var proceed=confirm('Do you really want to delete this Annotation?'); if (proceed) {setAction(<c:out value="${count}"/>,'<%= GroupAnnotationCommand.ACTION_DELETE_NOTE%>');} else { return false; }" />
								</c:if>
								</c:if>
								</td>
				  			</tr>  		
							<c:set var="count" scope="page" value="${count + 1}"/>
				  		</c:forEach>
				  	</c:otherwise>
				  </c:choose>
			</tbody>	  
			</table>
		</div>