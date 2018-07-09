<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="org.webcurator.ui.common.Constants"%>
<%@ page import="org.webcurator.ui.intray.command.InTrayCommand" %>
<%@ page import="org.webcurator.domain.model.auth.Privilege"%>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="authority" uri="http://www.webcurator.org/authority"%>
<%@ taglib prefix="wct" uri="http://www.webcurator.org/wct"%>

<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>

<script>
  function setTaskPage(pageNumber) {
    document.getElementById('taskPage').value = pageNumber;
    document.getElementById('pagingForm').submit();
  }
  
  function setNotificationPage(pageNumber) {
    document.getElementById('notificationPage').value = pageNumber;
    document.getElementById('pagingForm').submit();
  }  

  function setPageSize(pageSize) {
    document.getElementById('selectedPageSize').value = pageSize;
    document.getElementById('pagingForm').submit();
  }
  
  function toggleTasks() {
  	$('.taskRows').toggle();
	$('#showLabel').toggle();
	$('#hideLabel').toggle();
	var showTasksVal = $('#showTasks').val();
	if(showTasksVal=="true") {
		$('#showTasks').val("false");
	} else {
		$('#showTasks').val("true");
	}
  }
   
  $(document).ready(function() {
    var showTasksVal = $('#showTasks').val();
    if(showTasksVal=="true") {
  		$('#showLabel').hide();
  		$('#hideLabel').show();
  		$('.taskRows').show();
    } else {
  		$('#showLabel').show();
  		$('#hideLabel').hide();
  		$('.taskRows').hide();
    }
	
<authority:hasPrivilege privilege="<%= Privilege.DELETE_TASK %>" scope="<%= Privilege.SCOPE_OWNER %>">
	$('#deleteAllTasksForm').submit(function() {
		var confirm = window.confirm("Are you sure you want to delete ALL tasks?");
		if(confirm==true) {
			//Safeguard in case jquery doesn't work,a nd the prompt is not shown
			var formAction = '<%= InTrayCommand.ACTION_DELETE_ALL_TASKS %>';
			$('#deleteAllTasksAction').val(formAction);
			return true;
		}
		return false;
	});
</authority:hasPrivilege>
  });
</script>


<form id="pagingForm" method="POST" action="<%= Constants.CNTRL_INTRAY %>"> 
    <input type="hidden" id="taskPage" name="taskPage" value="${tasks.page}">
    <input type="hidden" id="notificationPage" name="notificationPage" value="${notifications.page}">
    <input type="hidden" id="selectedPageSize" name="selectedPageSize" value="${tasks.pageSize}">
    <input type="hidden" name="action" value="next">
    <input type="hidden" id="showTasks" name="showTasks" value="${showTasks}">
</form>

<img src="images/x.gif" alt="" width="1" height="30" border="0" /><br />
<div class="resultsTable">
    <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr>
            <td colspan="4" onclick="toggleTasks();"><span class="midtitleGrey">Tasks</span>
            	<div style="display: inline" id="taskToggle" >
            		<div style="display: inline;font-weight:bold;" id="showLabel">&nbsp;&nbsp;&nbsp;(Click to Show)</div>
            		<div style="display: inline;font-weight:bold;" id="hideLabel">&nbsp;&nbsp;&nbsp;(Click to Hide)</div>
            	</div>
            </td>
        </tr>
        <tr class="taskRows">
            <td class="tableHead">Date</td>
            <td class="tableHead">Subject</td>
            <td class="tableHead">Owner</td>
            <td class="tableHead">Action</td>
        </tr>
        <c:forEach items="${tasks.list}" var="task">
            <tr class="taskRows">
                <td class="tableRowLite"><wct:date value="${task.sentDate}" type="fullDateTime"/></td>
                <td class="tableRowLite"><c:out value="${task.subject}"/></td>
                <td class="tableRowLite">
                    <c:if test="${task.privilege != null}">
                        Unclaimed
                    </c:if>
                    <c:if test="${task.assigneeOid != null}">
                        <c:out value="${user.niceName}"/>
                    </c:if>
                </td>
                <td class="tableRowLite">
                    <table border="0">
                        <tr>
                            <td>
                                <form name="view" action="<%= Constants.CNTRL_INTRAY %>" method="POST">
                                    <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
                                    <input type="hidden" name="<%= InTrayCommand.PARAM_TASK_OID %>" value="<c:out value='${task.oid}'/>">
                                    <input type="hidden" name="<%= InTrayCommand.PARAM_ACTION %>" value="<%= InTrayCommand.ACTION_VIEW_TASK%>">
                                    <input type="image" name="view" src="images/action-icon-view.gif" title="View" alt="click here to VIEW this item" width="15" height="19" border="0" />
                                </form>
                            </td>
                            <authority:hasPrivilege privilege="<%= Privilege.DELETE_TASK %>" scope="<%= Privilege.SCOPE_OWNER %>">
                                <td class="tableRowLite">
                                    <form name="delete" action="<%= Constants.CNTRL_INTRAY %>" method="POST">
                                        <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
                                        <input type="hidden" name="<%= InTrayCommand.PARAM_TASK_OID %>" value="<c:out value='${task.oid}'/>">
                                        <input type="hidden" name="<%= InTrayCommand.PARAM_ACTION %>" value="<%= InTrayCommand.ACTION_DELETE_TASK%>">
                                        <input type="hidden" name="taskPage" value="${tasks.page}">
                                        <input type="hidden" name="notificationPage" value="${notifications.page}">
                                        <input type="image" name="delete" src="images/action-icon-delete.gif" title="Delete" alt="click here to DELETE this item" width="18" height="19" border="0" />
                                    </form>
                                </td>
                            </authority:hasPrivilege>
                            <c:if test="${task.privilege != null}">
                                <td class="tableRowLite">
                                    <form name="claim" action="<%= Constants.CNTRL_INTRAY %>" method="POST">
                                        <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
                                        <input type="hidden" name="<%= InTrayCommand.PARAM_TASK_OID %>" value="<c:out value='${task.oid}'/>">
                                        <input type="hidden" name="<%= InTrayCommand.PARAM_ACTION %>" value="<%= InTrayCommand.ACTION_CLAIM_TASK%>">
                                        <input type="hidden" name="taskPage" value="${tasks.page}">
                                        <input type="hidden" name="notificationPage" value="${notifications.page}">
                                        <input type="image" name="claim" src="images/action-icon-claim.gif" title="Claim" alt="click here to CLAIM this item" width="18" height="18" border="0" />
                                    </form>
                                </td>
                            </c:if>
                            <c:if test="${task.assigneeOid != null}">
                                <td class="tableRowLite">
                                    <form name="unclaim" action="<%= Constants.CNTRL_INTRAY %>" method="POST">
                                        <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
                                        <input type="hidden" name="<%= InTrayCommand.PARAM_TASK_OID %>" value="<c:out value='${task.oid}'/>">
                                        <input type="hidden" name="<%= InTrayCommand.PARAM_ACTION %>" value="<%= InTrayCommand.ACTION_UNCLAIM_TASK%>">
                                        <input type="hidden" name="taskPage" value="${tasks.page}">
                                        <input type="hidden" name="notificationPage" value="${notifications.page}">
                                        <input type="image" name="unclaim" src="images/action-icon-unclaim.gif" title="UnClaim" alt="click here to set this item back to UN-CLAIMED" width="18" height="18" border="0" />
                                    </form>
                                </td>
                            </c:if>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr class="taskRows">			
                <td colspan="4" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="5" border="0" /></td>
            </tr>
        </c:forEach>
        <tr class="taskRows">			
            <td class="tableRowLite" colspan="4" align="center">
                
                <table width="100%">
					<tr>
                        <td width="100%" colspan="3" align="center">
                            <authority:hasPrivilege privilege="<%= Privilege.DELETE_TASK %>" scope="<%= Privilege.SCOPE_OWNER %>">
							<form id="deleteAllTasksForm" method="POST" action="<%= Constants.CNTRL_INTRAY %>">
                                <input id="deleteAllTasksAction" type="hidden" name="action" value="">
                                <input type="submit" value="Delete All">
                            </form>
                            </authority:hasPrivilege>
						</td>
					</tr>
                    <tr>
                        <td width="33%">
                            <c:if test="${tasks.previousPage}"><input type="image" title="Prev" src="images/previous.gif" alt="Prev" border="0" onclick="return setTaskPage(${tasks.page - 1});"/><img src="images/x.gif" alt="" width="10" height="1" border="0" /></c:if>
                        </td>
                        <td width="34%" align="center">
                            <p>Results ${tasks.firstResult} to ${tasks.lastResult} of ${tasks.total}
                                <br>
                                Page <select onchange="setTaskPage(this.value);">
                                    <c:forEach begin="0" end="${tasks.numberOfPages}" varStatus="s">
                                        <option value="<c:out value="${s.index}"/>" <c:if test="${s.index == tasks.page }">selected</c:if>><c:out value="${s.index + 1}"/></option>
                                    </c:forEach>	  
                                </select>
                                of <c:out value="${tasks.numberOfPages+1}"/><br />
								Rows per page:&nbsp;<select onchange="setPageSize(this.value);">
														<option value="10" <c:if test="${10 == notifications.pageSize }">selected</c:if>>10</option>
														<option value="20" <c:if test="${20 == notifications.pageSize }">selected</c:if>>20</option>
														<option value="50" <c:if test="${50 == notifications.pageSize }">selected</c:if>>50</option>
														<option value="100" <c:if test="${100 == notifications.pageSize }">selected</c:if>>100</option>
													</select>
                            </p>
                        </td>
                        <td width="33%" align="right">
                            <c:if test="${tasks.nextPage}"><input type="image" title="Next" src="images/next.gif" alt="Next" border="0" onclick="return setTaskPage(${tasks.page + 1});"/><img src="images/x.gif" alt="" width="10" height="1" border="0" /></c:if>				
                        </td>
                    </tr>
                </table>		
            </td>
        </tr>
    </table>
</div>

<img src="images/x.gif" alt="" width="1" height="30" border="0" /><br />
<div class="resultsTable">
    <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr>
            <td colspan="3"><span class="midtitleGrey">Notifications</span></td>
        </tr>
        <tr>
            <td class="tableHead">Date</td>
            <td class="tableHead">Subject</td>
            <td class="tableHead">Action</td>
        </tr>
        <c:forEach items="${notifications.list}" var="notify">
            <tr>
                <td class="tableRowLite"><wct:date value="${notify.sentDate}" type="fullDateTime"/></td>
                <td class="tableRowLite"><c:out value="${notify.subject}"/></td>
                <td class="tableRowLite">
                    <table>
                        <tr>
                            <td>
                                <form name="view" action="<%= Constants.CNTRL_INTRAY %>" method="POST">
                                    <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
                                    <input type="hidden" name="<%= InTrayCommand.PARAM_NOTIFICATION_OID %>" value="<c:out value='${notify.oid}'/>">
                                    <input type="hidden" name="<%= InTrayCommand.PARAM_ACTION %>" value="<%= InTrayCommand.ACTION_VIEW_NOTIFICATION%>">
                                    <input type="image" name="view" src="images/action-icon-view.gif" title="View" alt="click here to VIEW this item" width="15" height="19" border="0" />
                                </form>
                            </td>
                            <td>
                                <form name="delete" action="<%= Constants.CNTRL_INTRAY %>" method="POST">
                                    <img src="images/action-sep-line.gif" alt="" width="7" height="19" border="0" />
                                    <input type="hidden" name="<%= InTrayCommand.PARAM_NOTIFICATION_OID %>" value="<c:out value='${notify.oid}'/>">
                                    <input type="hidden" name="<%= InTrayCommand.PARAM_ACTION %>" value="<%= InTrayCommand.ACTION_DELETE_NOTIFICATION%>">
                                    <input type="hidden" name="taskPage" value="${tasks.page}">
                                    <input type="hidden" name="notificationPage" value="${notifications.page}">
                                    <input type="image" name="delete" src="images/action-icon-delete.gif" title="Delete" alt="click here to DELETE this item" width="18" height="19" border="0" />
                                </form>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td colspan="3" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="5" border="0" /></td>
            </tr>
        </c:forEach>
        <tr>		
            <td class="tableRowLite" colspan="3" align="center">
                
                <table width="100%">
                    <tr>
                        <td width="100%" colspan="3" align="center">
                            <form method="POST" action="<%= Constants.CNTRL_INTRAY %>">
                                <input type="hidden" name="action" value="<%= InTrayCommand.ACTION_DELETE_ALL_NOTIFICATIONS %>">
                                <input type="submit" value="Delete All">
                            </form>
                        </td>
                    </tr>
                    <tr>
                        <td width="33%">
                            <c:if test="${notifications.previousPage}"><input type="image" title="Prev" src="images/previous.gif" alt="Prev" border="0" onclick="return setNotificationPage(${notifications.page - 1});"/><img src="images/x.gif" alt="" width="10" height="1" border="0" /></c:if>
                        </td>
                        <td width="34%" align="center">
                            <p>Results ${notifications.firstResult} to ${notifications.lastResult} of ${notifications.total}
                                <br>
                                Page <select onchange="setNotificationPage(this.value);">
                                    <c:forEach begin="0" end="${notifications.numberOfPages}" varStatus="s">
                                        <option value="<c:out value="${s.index}"/>" <c:if test="${s.index == notifications.page }">selected</c:if>><c:out value="${s.index + 1}"/></option>
                                    </c:forEach>	  
                                </select>
                                of <c:out value="${notifications.numberOfPages+1}"/><br />
								Rows per page:&nbsp;<select onchange="setPageSize(this.value);">
														<option value="10" <c:if test="${10 == notifications.pageSize }">selected</c:if>>10</option>
														<option value="20" <c:if test="${20 == notifications.pageSize }">selected</c:if>>20</option>
														<option value="50" <c:if test="${50 == notifications.pageSize }">selected</c:if>>50</option>
														<option value="100" <c:if test="${100 == notifications.pageSize }">selected</c:if>>100</option>
													</select>
                            </p>
                        </td>
                        <td width="33%" align="right">
                            <c:if test="${notifications.nextPage}"><input type="image" title="Next" src="images/next.gif" alt="Next" border="0" onclick="return setNotificationPage(${notifications.page + 1});"/><img src="images/x.gif" alt="" width="10" height="1" border="0" /></c:if>				 
                        </td>
                    </tr>
                </table>		
            </td>
        </tr>		
    </table>
</div>
