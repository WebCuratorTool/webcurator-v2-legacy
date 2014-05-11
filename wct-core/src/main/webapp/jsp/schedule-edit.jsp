<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct" %>
<%@page import="org.webcurator.ui.target.command.TargetSchedulesCommand"%>

<link rel="stylesheet" href="styles/blitzer/jquery-ui-1.10.2.custom.min.css" />
<script src="scripts/jquery-1.7.2.min.js" type="text/javascript"></script>
<script src="scripts/jquery-ui-1.10.2.custom.min.js" type="text/javascript"></script>

<script type="text/css">
.heavySchedule a.ui-state-default= {
	color:black;
	background-color: #<c:out value="${command.heatMapThresholds['high'].color}"/>;
	background: #<c:out value="${command.heatMapThresholds['high'].color}"/>;
}
.mediumSchedule a.ui-state-default= {
	color:black;
	background-color: #<c:out value="${command.heatMapThresholds['medium'].color}"/>;
	background: #<c:out value="${command.heatMapThresholds['medium'].color}"/>;
}
.lightSchedule a.ui-state-default= {
	color:black;
	background-color: #<c:out value="${command.heatMapThresholds['low'].color}"/>;
	background: #<c:out value="${command.heatMapThresholds['low'].color}"/>;
}
</script>

<script>
  function onChangeScheduleType() {
    document.getElementById('actionCmd').value = 'refresh';
    document.getElementById('scheduleForm').submit();
  }
</script>

<script type="text/javascript"> 
	var dateHeatMap = {
        <c:forEach items="${command.heatMap}" var="item" varStatus="loop">'${item.key}': '${item.value}' ${not loop.last ? ',' : ''}</c:forEach>  
	};
	
	$(document).ready(function() {
		$('#scheduleForm').submit(submitCheck);
		$('.dateEntry').datepicker({dateFormat: 'dd/mm/yy', changeMonth: true, changeYear: true, showOtherMonths: true, selectOtherMonths: true, showButtonPanel: true});
		var widget = $('#heatmap').datepicker({
        		buttonImage: 'images/calendar.gif',
		        buttonImageOnly: true,
		        changeMonth: true,
		        changeYear: true,
			showOtherMonths: true, 
			selectOtherMonths: true, 
			showButtonPanel: true,
		        showOn: 'both',
				beforeShowDay:colorDay
	        });
		setCss();
	});
	
	function setCss() {
		$("<style type='text/css'>.heavySchedule a.ui-state-default {color:black;background-color:#<c:out value="${command.heatMapThresholds['high'].color}"/>;background:#<c:out value="${command.heatMapThresholds['high'].color}"/>;}</style>").appendTo("head");
		$("<style type='text/css'>.mediumSchedule a.ui-state-default {color:black;background-color:#<c:out value="${command.heatMapThresholds['medium'].color}"/>;background:#<c:out value="${command.heatMapThresholds['medium'].color}"/>;}</style>").appendTo("head");
		$("<style type='text/css'>.lightSchedule a.ui-state-default {color:black;background-color:#<c:out value="${command.heatMapThresholds['low'].color}"/>;background:#<c:out value="${command.heatMapThresholds['low'].color}"/>;} </style>").appendTo("head");
	}

	function leadingZero(value) {
		if(value<10) {
			return "0"+value;
		}
		return value;
	}
	
	function colorDay(date) {
		var dateString = ""+date.getFullYear() + leadingZero(date.getMonth()+1) + leadingZero(date.getDate());
		var dateColorValue = dateHeatMap[dateString];
		var dayColor = "";
		if(dateColorValue) {
				//The colors cannot be dynamic as they need to be CSS classes
				if(dateColorValue>=<c:out value="${command.heatMapThresholds['high'].thresholdLowest}"/>) {
					dayColor = "heavySchedule";
				} else if(dateColorValue>=<c:out value="${command.heatMapThresholds['medium'].thresholdLowest}"/>) {
					dayColor = "mediumSchedule";
				} else if(dateColorValue>=<c:out value="${command.heatMapThresholds['low'].thresholdLowest}"/>) {
					dayColor = "lightSchedule";
				}
		}
		return [true, dayColor];
	}	
	function submitCheck() {
		var scheduleType = Number($('#scheduleType').val());
		
		//If schedule type is monthly or greater
		if(scheduleType < -2 || scheduleType==0) {
			var dayOfMonthSelection = $('.dayOfMonthSelection').val();
			if(dayOfMonthSelection.indexOf('29')!==-1 || dayOfMonthSelection.indexOf('30')!==-1 || dayOfMonthSelection.indexOf('31')!==-1) {
				if(scheduleType!=-6 && dayOfMonthSelection!="Jan, Jul") {
					var confirmation = confirm("Warning!  This schedule will *NOT be harvested* on months which do not have one of the days-of-month selected.\n\nIt is recommended to use the 'L'ast day of month option.\n\nClick OK to save, Cancel to return to edit.");
					return confirmation;
				}				
			}
		}
		return true;		
	}
	
</script>
<c:set var="viewMode" value="${viewMode}"/>
<form id="scheduleForm" action="curator/${viewPrefix}/schedule.html" method="post">
<input type="hidden" id="actionCmd" name="actionCmd" value="">
<input type="hidden" name="selectedItem" value="<c:out value="${command.selectedItem}"/>">
			<table cellpadding="3" cellspacing="0" border="0">
			  <tr>
			    <td valign="top">
			      <table cellpadding="3" cellspacing="0" border="0">
			      	<tr>
					    <td class="subBoxTextHdr" width="100px">From Date:</td>
					    <td class="subBoxText">
					    <c:choose>					    
					    	<c:when test="${viewMode}">
					    		<wct:date value="${command.startDate}" type="fullDate"/>
					    	</c:when>
					    	<c:otherwise>
					    		<input type="text" class="dateEntry" name="startDate" value="<wct:date value="${command.startDate}" type="fullDate"/>"/><font color=red size=2>&nbsp;<strong>*</strong></font>
					    	</c:otherwise>
					    </c:choose>						    
					    </td>
					</tr>
					<tr>
					    <td class="subBoxTextHdr">To Date:</td>
					    <td class="subBoxText">
					    <c:choose>					    
					    	<c:when test="${viewMode}">
					    		<wct:date value="${command.endDate}" type="fullDate"/>
					    	</c:when>
					    	<c:otherwise>
					    		<input type="text" class="dateEntry" name="endDate" value="<wct:date value="${command.endDate}" type="fullDate"/>"/>
					    	</c:otherwise>
					    </c:choose>	
					    </td>					    					    
					</tr> 
  					<tr>
					    <td class="subBoxTextHdr">Type:</td>
					    <td class="subBoxText">
					    <c:choose>					    
					    	<c:when test="${viewMode}">
					    		<c:choose>
					    			<c:when test="${command.scheduleType == 0}">Custom</c:when>
              		    			<c:when test="${command.scheduleType == -1}">Daily</c:when>
              		    			<c:when test="${command.scheduleType == -2}">Weekly</c:when>
              		    			<c:when test="${command.scheduleType == -3}">Monthly</c:when>
              		    			<c:when test="${command.scheduleType == -4}">Bi-Monthly</c:when>
              		    			<c:when test="${command.scheduleType == -5}">Quarterly</c:when>
              		    			<c:when test="${command.scheduleType == -6}">Half Yearly</c:when>
              		    			<c:when test="${command.scheduleType == -7}">Annually</c:when>
              		    			
					    			<c:otherwise>
					    			<c:forEach var="pattern" items="${patterns}">
					    			  <c:if test="${command.scheduleType == pattern.scheduleType}">
					    			  	<c:out value="${pattern.description}" />  
					    			  </c:if>							          
							        </c:forEach>
					    			</c:otherwise>
					    		</c:choose>
					    	</c:when>
					    	<c:otherwise>
					    		<select id="scheduleType" name="scheduleType" onchange="onChangeScheduleType();">
							        <c:forEach var="pattern" items="${patterns}">
							        <option value="${pattern.scheduleType}" ${command.scheduleType == pattern.scheduleType ? 'selected' : ''} >${pattern.description}</option>
							        </c:forEach>
							        <option value="0" ${command.scheduleType == 0 ? 'selected' : ''}>Custom</option>
							        
							        <option value="-1" ${command.scheduleType == -1 ? 'selected' : ''}>Daily</option>
							        <option value="-2" ${command.scheduleType == -2 ? 'selected' : ''}>Weekly</option>
							        <option value="-3" ${command.scheduleType == -3 ? 'selected' : ''}>Monthly</option>
							        <option value="-4" ${command.scheduleType == -4 ? 'selected' : ''}>Bi-Monthly</option>
							        <option value="-5" ${command.scheduleType == -5 ? 'selected' : ''}>Quarterly</option>
							        <option value="-6" ${command.scheduleType == -6 ? 'selected' : ''}>Half Yearly</option>
							        <option value="-7" ${command.scheduleType == -7 ? 'selected' : ''}>Annually</option>							        
							     </select>
					    	</c:otherwise>
					     </c:choose>
					    	<c:choose>
					    	  <c:when test="${command.scheduleType == -1}">
							    <input type="hidden" name="daysOfWeek" value="?">	
							    <input type="hidden" name="daysOfMonth" value="*">								    
							    <input type="hidden" name="months" value="*">
							    <input type="hidden" name="years" value="*">						    	  
					    	  </c:when>
					    	  <c:when test="${command.scheduleType == -2}">
							    <input type="hidden" name="daysOfMonth" value="?">	
							    <input type="hidden" name="months" value="*">
							    <input type="hidden" name="years" value="*">						    	  
					    	  </c:when>
					    	  <c:when test="${command.scheduleType == -3}">
							    <input type="hidden" name="daysOfWeek" value="?">	
							    <input type="hidden" name="months" value="*">
							    <input type="hidden" name="years" value="*">						    	  
					    	  </c:when>
					    	  <c:when test="${command.scheduleType <= -4}">
					    	    <input type="hidden" name="daysOfWeek" value="?">	
							    <input type="hidden" name="years" value="*">					    	  
					    	  </c:when>
					    	</c:choose>
					    					      
					    </td>
					</tr>
<!--				</table>   
					
					
					
				<table cellpadding="3" cellspacing="0" border="0"> -->
				  <c:choose>
					<c:when test="${command.scheduleType == 0}">
					<tr>
					    <td class="subBoxTextHdr" width="100px">Minutes:</td>
					    <td class="subBoxText">
					    <c:choose>					    
					    	<c:when test="${viewMode}">
					    		<c:out value="${command.minutes}"/>
					    	</c:when>
					    	<c:otherwise>
					    		<input type="text" name="minutes" value="<c:out value="${command.minutes}"/>"/>
					    	</c:otherwise>
					    </c:choose>						   
					    </td>
					</tr>
					<tr>
					    <td class="subBoxTextHdr">Hours:</td>
					    <td class="subBoxText">
					    <c:choose>					    
					    	<c:when test="${viewMode}">
					    		<c:out value="${command.hours}"/>
					    	</c:when>
					    	<c:otherwise>
					    		<input type="text" name="hours" value="<c:out value="${command.hours}"/>">
					    	</c:otherwise>
					    </c:choose>						   					    
					    </td>
					</tr>
					</c:when>
					<c:when test="${command.scheduleType < 0}">
					<tr>
					    <td class="subBoxTextHdr">Time:</td>
					    <td class="subBoxText">
					    <c:choose>					    
					    	<c:when test="${viewMode}">
					    		<c:out value="${command.time}"/>
					    	</c:when>
					    	<c:otherwise>
					    		<input type="text" name="time" value="<c:out value="${command.time}"/>"><font color=red size=2>&nbsp;<strong>*</strong></font>
					    	</c:otherwise>
					    </c:choose>						   					    
					    </td>
					</tr>					
					</c:when>
				  </c:choose>
					
					
					<c:if test="${command.scheduleType == -2}">
					<tr>
						<td class="subBoxTextHdr">Day of Week:</td>
						<td class="subBoxText">
							<c:choose>					    
					    	  <c:when test="${viewMode}">		
							  <c:out value="${command.daysOfWeek}"/>
							  </c:when>
							  <c:otherwise>
						    					
								<select name="daysOfWeek">
									<option value="MON" ${command.daysOfWeek == 'MON' ? 'selected' :''}>Monday</option>
									<option value="TUE" ${command.daysOfWeek == 'TUE' ? 'selected' :''}>Tuesday</option>
									<option value="WED" ${command.daysOfWeek == 'WED' ? 'selected' :''}>Wednesday</option>
									<option value="THU" ${command.daysOfWeek == 'THU' ? 'selected' :''}>Thursday</option>
									<option value="FRI" ${command.daysOfWeek == 'FRI' ? 'selected' :''}>Friday</option>
									<option value="SAT" ${command.daysOfWeek == 'SAT' ? 'selected' :''}>Saturday</option>
									<option value="SUN" ${command.daysOfWeek == 'SUN' ? 'selected' :''}>Sunday</option>
								</select>
							  </c:otherwise>
							</c:choose>
						</td>
					</tr>
					</c:if>
					
					<c:if test="${command.scheduleType < -2}">
					<tr>
						<td class="subBoxTextHdr">Day of Month:</td>
						<td class="subBoxText">
							<c:choose>					    
					    	  <c:when test="${viewMode}">	
					    	    <c:out value="${command.daysOfMonth}"/>
					    	  </c:when>
					    	  <c:otherwise>			  			
								<select name="daysOfMonth" class="dayOfMonthSelection">
									<c:forEach var="i" begin="1" end="31">
									<option value="<c:out value="${i}"/>" <%
									   TargetSchedulesCommand cmd = (TargetSchedulesCommand)pageContext.findAttribute("command");
									   String compValue = pageContext.findAttribute("i").toString();
									   if(cmd != null && cmd.getDaysOfMonth() != null && cmd.getDaysOfMonth().equals(compValue)) {
									     out.print("selected");
									   }
									%>><c:out value="${i}"/></option>
									</c:forEach>
									<option value="L" ${'L' == command.daysOfMonth ? 'selected' :''}>Last</option>
								</select>
						      </c:otherwise>
						    </c:choose>
						</td>
					</tr>
					</c:if>
					
					<c:if test="${command.scheduleType < -3}">
					<tr>
						<td class="subBoxTextHdr">Month:</td>
						<td class="subBoxText">
						  <c:choose>
						    <c:when test="${viewMode}">
						      <c:out value="${monthOptions[command.months]}"/>
						    </c:when>
						    <c:otherwise>
								<select name="months" class="dayOfMonthSelection">
									<c:forEach var="month" items="${monthOptions}">
									<option value="<c:out value="${month.key}"/>" ${month.key == command.months ? 'selected' :''}><c:out value="${month.value}"/></option>
									</c:forEach>
								</select>
							</c:otherwise>
						  </c:choose>
						</td>
					</tr>	
					</c:if>			
					
				<c:if test="${command.scheduleType == 0}">
					

					<tr>
					    <td class="subBoxTextHdr">Days of Week:</td>
					    <td class="subBoxText">
					    <c:choose>					    
					    	<c:when test="${viewMode}">
					    		<c:out value="${command.daysOfWeek}"/>
					    	</c:when>
					    	<c:otherwise>
					    		<input type="text" name="daysOfWeek" value="<c:out value="${command.daysOfWeek}"/>"/>
					    	</c:otherwise>
					    </c:choose>						    
					    </td>
					</tr>
					<tr>
					    <td class="subBoxTextHdr">Days of Month:</td>
					    <td class="subBoxText">
					    <c:choose>					    
					    	<c:when test="${viewMode}">
					    		<c:out value="${command.daysOfMonth}"/>
					    	</c:when>
					    	<c:otherwise>
					    		<input type="text" name="daysOfMonth" class="dayOfMonthSelection" value="<c:out value="${command.daysOfMonth}"/>"/>
					    	</c:otherwise>
					    </c:choose>						    
					    </td>
					</tr>
					<tr>
					    <td class="subBoxTextHdr">Months:</td>
					    <td class="subBoxText">
					    <c:choose>					    
					    	<c:when test="${viewMode}">
					    		<c:out value="${command.months}"/>
					    	</c:when>
					    	<c:otherwise>
					    		<input type="text" name="months" value="<c:out value="${command.months}"/>"/>
					    	</c:otherwise>
					    </c:choose>						    
					    </td>
					</tr>
					<tr>
					    <td class="subBoxTextHdr">Years:</td>
					    <td class="subBoxText">
					    <c:choose>					    
					    	<c:when test="${viewMode}">
					    		<c:out value="${command.years}"/>
					    	</c:when>
					    	<c:otherwise>
					    		<input type="text" name="years" value="<c:out value="${command.years}"/>"/>
					    	</c:otherwise>
					    </c:choose>						    					    
					    </td>
					</tr>
			      </table>
			   </td>
			   <td><img src="images/x.gif" alt="" width="40" height="1" border="0" /></td>
			   <td valign="top">
			    <table cellpadding="3" cellspacing="0" border="0"  id="testScheduleDiv" <c:if test="${!empty patterns && command.scheduleType != 0}">style="display:none;"</c:if>>
			      	<tr>
			      		<td class="subBoxText" width="200px">
			      		<p><b>Next 10 Scheduled Times</b></p>
			      		<c:if test="${ !empty testResults }">
					     <ul>
					       <c:forEach items="${testResults}" var="res">
					       <li><fmt:formatDate value="${res}" pattern="dd/MM/yyyy HH:mm:ss"/></li>
					       </c:forEach>  
					     </ul>
					     </c:if> 
			      		</td>
			      	</tr>
			      	<tr>			      	
			      		<td class="subBoxText" colspan="2">
			      		<c:choose>					    
					    	<c:when test="${viewMode}">
					    		&nbsp;
					    	</c:when>
					    	<c:otherwise>
					    		<input type="image" name="_test" src="images/generic-btn-test.gif" title="click to View schedule dates" alt="click to View schedule dates" border="0" onclick="document.getElementById('actionCmd').value='test'; return true">
					    	</c:otherwise>
					    </c:choose>					      		
			      		</td>
			      	</tr>			      	
				</c:if>			          					
				<c:if test="${not viewMode}">
					<tr>
						<td class="subBoxTextHdr">Heat map:</td>
		      			<td class="subBoxText" width="200px"><input type="hidden" id="heatmap" /></td>
					</tr>
	    		</c:if>
						
				</table>					
			   </td>
			</tr>	  
		</table>
		
		<img src="images/x.gif" alt="" width="1" height="30" border="0" />
		<c:choose>					    
	    	<c:when test="${viewMode}">
	    		<input type="image" name="_cancel" src="images/generic-btn-done.gif" alt="Done" width="82" height="23" border="0" onclick="document.getElementById('actionCmd').value='cancel'; return true"><br/>
	    	</c:when>
	    	<c:otherwise>
	    		<input type="image" name="_save" src="images/generic-btn-save.gif" alt="Save" width="82" height="23" border="0" onclick="document.getElementById('actionCmd').value='save'; return true"><img src="images/x.gif" alt="" width="10" height="1" border="0" />
				<input type="image" name="_cancel" src="images/generic-btn-cancel.gif" alt="Cancel" width="82" height="23" border="0" onclick="document.getElementById('actionCmd').value='cancel'; return true"><br/>
	    	</c:otherwise>
	    </c:choose>					      				
</form>

