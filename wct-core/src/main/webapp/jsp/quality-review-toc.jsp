<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="resultsTable">
	<table width="100%" cellpadding="2" cellspacing="0" border="0">
		<tr>
			<td><span class="midtitleGrey">Quality Review Tools</span></td>
		</tr>
		<tr>
			<td colspan="2" class="tableHead">Browse</td>			
		</tr>
		<tr>
			<td colspan="2">
				<table width="100%">
					<c:forEach items="${seeds}" var="seed">
					<tr>
						<td width="30%">
							<c:choose> 
			  				<c:when test="${seed.primary == true}" > 
								<b><c:out value="${seed.seed}"/></b>
							</c:when> 
							<c:otherwise> 
								<c:out value="${seed.seed}"/>
							</c:otherwise> 
							</c:choose> 
						</td>
						<td width="70%">
						  <c:if test="${seed.browseUrl != ''}">
						  <a href="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/<c:out value="${seed.browseUrl}"/>" target="_blank">Review this Harvest</a> 
						  | 
						  </c:if>
						  <c:if test="${seed.accessUrl != ''}">
						  <a href="<c:out value="${seed.accessUrl}"/>" target="_blank">Review in Access Tool</a> 
						  | 
						  </c:if>
						  <a href="<c:out value="${seed.seed}"/>" target="_blank">Live Site</a>
						  <c:choose>
						    <c:when test="${archiveUrl == ''}"></c:when>
						    <c:otherwise>
						    | <a href="<c:out value="${archiveUrl}" escapeXml="false"/><c:out value="${seed.seed}"/>" target="_blank">
								  <c:choose>
								    <c:when test="${archiveName == ''}">Archives Harvested</c:when>
								    <c:otherwise><c:out value="${archiveName}"/></c:otherwise>
						  		  </c:choose>
						      </a>
						    </c:otherwise>
						  </c:choose>
						  <c:choose>
						    <c:when test="${archiveAlternative == ''}"></c:when>
						    <c:otherwise>
						    | <a href="<c:out value="${archiveAlternative}" escapeXml="false"/><c:out value="${seed.seed}"/>" target="_blank"><c:out value="${archiveAlternativeName}"/></a>
						    </c:otherwise>
						  </c:choose>
						  <c:choose>
						    <c:when test="${webArchiveTarget == ''}"></td>
						    | Web Archive not configured
						    </c:when>
						    <c:otherwise>
						    | <a href="<c:out value="${webArchiveTarget}" escapeXml="false"/><c:out value="${targetOid}"/>" target="_blank">Web Archive</a></td>
						    </c:otherwise>
						  </c:choose>
					</tr>
					<tr>			
						<td colspan="2" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
				    </tr>
					</c:forEach>
				</table>
			</td>
		</tr>
		<tr>
		  <td>&nbsp;</td>
		</tr>
		<tr>
			<td width="30%" class="tableHead">Tool</td>
			<td width="70%" class="tableHead">Description</td>			
		</tr>
		
		<tr>
			<td width="30%"><a href="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()%>/curator/tools/harvest-history.html?targetInstanceOid=<c:out value="${command.targetInstanceOid}"/>&harvestResultId=<c:out value="${command.harvestResultId}"/>">Harvest History</a></td>
			<td width="70%">Compare current harvest result with previous harvests.</td>			
		</tr>
		<tr>
			<td colspan="2" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
		</tr>	
		<tr>
			<td width="30%"><a href="curator/tools/treetool.html?loadTree=<c:out value="${command.harvestResultId}"/>&targetInstanceOid=<c:out value="${targetInstanceOid}"/>&logFileName=aqa-report(<c:out value="${command.harvestNumber}"/>).xml">Tree View</a></td>			
			<td width="70%">Graphical view of harvested data.</td>			
		</tr>
		<tr>
			<td colspan="2" class="tableRowSep"><img src="images/x.gif" alt="" width="1" height="1" border="0" /></td>
		</tr>
		<tr>
			<td colsapan="2">&nbsp;</td>
		</tr>	
		<tr class="tableRowLite">
			<td width="30%"><a href="curator/target/target-instance.html?targetInstanceId=<c:out value="${targetInstanceOid}&cmd=edit&init_tab=RESULTS"/>"><img src="images/generic-btn-done.gif" border="0"></a></td>			
		    <td width="70%">&nbsp;</td>
		</tr>
	</table>
</div>