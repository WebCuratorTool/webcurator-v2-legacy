<jsp:directive.page import="org.webcurator.ui.site.command.SiteCommand"/>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="org.webcurator.domain.model.auth.Privilege" %>
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
    setAction(-1,'<%= SiteCommand.ACTION_ADD_NOTE%>');
  }
</script>
	<input id="noteIndex" name="<%= SiteCommand.PARAM_NOTE_INDEX %>" value="-1"  type="hidden">
<c:set var="editMode" value="${siteEditorContext.editMode}"/>
<c:set var="editAnnotations" value="${siteEditorContext.editAnnotations}"/>
			<table cellpadding="3" cellspacing="0" border="0">
			<c:if test="${siteEditorContext.site.oid != null}">
			<tr>
				<td class="subBoxText">Id:</td>
				<td>
					<c:out value="${siteEditorContext.site.oid}"/>
				</td>
			</tr>			
			</c:if>
			<c:choose>
			<c:when test="${siteEditorContext.site.owningAgency != null}">
			<tr>
				<td class="subBoxText">Agency:</td>
				<td>
					<c:out value="${siteEditorContext.site.owningAgency.name}"/>
				</td>
			</tr>			
			</c:when>
			<c:otherwise>
			<tr>
				<td class="subBoxText">Agency:</td>
				<td>None</td>
			</tr>			
			</c:otherwise>
			</c:choose>
			<tr>
				<td class="subBoxText">Title:</td>
				<td>
				<input type="hidden" name="editMode" value="<c:out value="${editMode}"/>"/>
				<input type="hidden" id="actionCmd" name="cmdAction" value="" />
				<c:choose>
					<c:when test="${editMode}">
						<input type="text" name="title" value="<c:out value="${command.title}"/>" style="width:350px;" maxlength="<%=SiteCommand.CNST_MAX_LEN_TITLE%>" /><font color=red size=2>&nbsp;<strong>*</strong></font>
					</c:when>
					<c:otherwise>
						<c:out value="${siteEditorContext.site.title}"/>
					</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<td class="subBoxText" valign="top">Description:</td>
				<td>
				<c:choose>
					<c:when test="${editMode}">
						<textarea name="description" cols="40" rows="6" style="width:350px;"><c:out value="${command.description}"/></textarea>
					</c:when>
					<c:otherwise>
						<c:out value="${siteEditorContext.site.description}"/>
					</c:otherwise>
				</c:choose>				
				</td>
			</tr>
			<tr>
				<td class="subBoxText">Order No:</td>
				<td>
				<c:choose>
					<c:when test="${editMode}">
						<input type="text" name="libraryOrderNo" value="<c:out value="${command.libraryOrderNo}"/>" style="width:350px;" maxlength="<%=SiteCommand.CNST_MAX_LEN_ORDERNO%>" />
					</c:when>
					<c:otherwise>
						<c:out value="${siteEditorContext.site.libraryOrderNo}"/>
					</c:otherwise>
				</c:choose>	
				</td>
			</tr>
			<tr>
				<td class="subBoxText">Published:</td>
				<td>
				<c:choose>
					<c:when test="${editMode}">
						<input type="checkbox" name="published" ${command.published ? 'checked' : ''} />
					</c:when>
					<c:otherwise>					
						<c:out value="${siteEditorContext.site.published ? 'Yes' : 'No'}"/>
					</c:otherwise>
				</c:choose>	
				
				</td>
			</tr>	
			<tr>
				<td class="subBoxText">Enabled:</td>
				<td>
				<authority:noPrivilege privilege="<%=Privilege.ENABLE_DISABLE_SITE%>" scope="<%=Privilege.SCOPE_NONE%>">
					<c:out value="${siteEditorContext.site.active ? 'Yes' : 'No'}"/>
					<input type="hidden" name="active"  value="${command.active}" />
				</authority:noPrivilege>
				<authority:hasPrivilege privilege="<%=Privilege.ENABLE_DISABLE_SITE%>" scope="<%=Privilege.SCOPE_NONE%>">
				<c:choose>					
					<c:when test="${editMode}">					
						<input type="checkbox" name="active" ${command.active ? 'checked' : ''} />
					</c:when>
					<c:otherwise>					
						<c:out value="${siteEditorContext.site.active ? 'Yes' : 'No'}"/>
						<input type="hidden" name="active"  value="${command.active}" />
					</c:otherwise>
				</c:choose>					
				</authority:hasPrivilege>
				</td>
			</tr>	
					
			</table>
			<div id="annotationsBox">
				<span class="subBoxTitle">Annotations</span><br />
				<c:if test="${editMode || editAnnotations}">
				  <textarea id="note" rows="<%= Constants.ANNOTATION_ROWS%>" cols="<%= Constants.ANNOTATION_COLS%>" name="<%= SiteCommand.PARAM_NOTE %>"></textarea>
		   	    <input id="hiddenSaveParam" name="" type="hidden" value=""/>
		     	  <input id="btnAdd" type="image" src="images/subtabs-add-btn.gif" alt="Add" width="49" height="23" border="0" hspace="5" align="absmiddle" onclick="promptSave();">
		     	  <input id="btnChange" class="hidden" type="image" src="images/subtabs-change-btn.gif" alt="Change" width="49" height="23" border="0" hspace="5" align="absmiddle" onclick="setAction(document.getElementById('noteIndex').value,'<%= SiteCommand.ACTION_MODIFY_NOTE%>');">
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
							<c:when test="${editMode || editAnnotations}">
								<td class="annotationsHeaderRow">Action</td>
							</c:when>
							<c:otherwise>
								<td class="annotationsHeaderRow"></td>
							</c:otherwise>
						</c:choose>
					</tr>
				<c:choose>
					<c:when test="${empty siteEditorContext.site.annotations}">
				  	<tr id="empty">
				    	<td class="annotationsLiteRow" colspan="4">There are no annotations available.</td>
				  	</tr>
					</c:when>
					<c:otherwise>
						<c:set var="count" scope="page" value="0"/>
						<c:forEach items="${siteEditorContext.site.annotations}" var="anno">
			  			<tr id="row<c:out value="${count}"/>">
				    		<td class="annotationsLiteRow"><wct:date value="${anno.date}" type="longDateTime"/></td>
				    		<td class="annotationsLiteRow"><c:out value="${anno.user.niceName}"/></td>
			  				<td id="note<c:out value="${count}"/>" class="annotationsLiteRow"><c:out value="${anno.note}"/></td>					
			  				<td class="annotationsLiteRow">
							<c:if test="${editMode || editAnnotations}">
			  				<c:if test="${anno.user.username eq command.username}">
							<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
							<img style="cursor:pointer;" title="edit" src="images/action-icon-edit.gif" alt="click here to Edit this item" border="0"
							 	onclick="javascript:setEdit(<c:out value="${count}"/>);"/>
							
							<img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
							<input type="image" title="delete" src="images/action-icon-delete.gif" alt="click here to Delete this item" border="0"
							 	onclick="javascript:var proceed=confirm('Do you really want to delete this Annotation?'); if (proceed) {setAction(<c:out value="${count}"/>,'<%= SiteCommand.ACTION_DELETE_NOTE%>');} else { return false; }" />
							</c:if>
							</c:if>
				  		</tr>
			  			<c:set var="count" scope="page" value="${count + 1}"/>
				  		</c:forEach>					
					</c:otherwise>
				</c:choose>
				</tbody>
				</table>
			</div>				
