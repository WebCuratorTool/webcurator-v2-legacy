<%@page import="org.webcurator.core.util.ApplicationContextFactory" %>
<%@page import="org.webcurator.core.archive.dps.DPSArchive" %>
<%@page import="org.webcurator.core.archive.dps.DPSArchive.DepData" %>
<%@page import="java.util.HashMap"%>

<%
	DPSArchive dpsArchive = (DPSArchive)ApplicationContextFactory.getWebApplicationContext().getBean("dpsArchive");
	response.addHeader("Cache-Control", "no-store");
	String query = request.getParameter("query");
	if ("getProducers".equals(query)) {
		String producerAgent = request.getParameter("producerAgent");
		String fromCacheStr = request.getParameter("fromCache");
		boolean fromCache = Boolean.parseBoolean(fromCacheStr);
		DepData[] producers = dpsArchive.getProducer(producerAgent, fromCache);
		if (producers == null || producers.length <= 0) {
%>
<b>No producers available in Rosetta for the user <%= producerAgent %></b>
<%
		} else {
%>

<b>Select a producer for agent <%= producerAgent %></b>
(<a title="Retrieves a fresh list of producers from Rosetta for the agent" style="text-decoration:underline;" 
href="#" onClick="javascript: return getProducers(false);">Retrieve the list from Rosetta</a>)
<p></p>
<select title="Producer" name="customDepositForm_producerId" size="10" style="width:100%;">
<%
			for (int i = 0; i < producers.length; i ++) {
				DepData aProducer = producers[i];
				String prodId = aProducer.id;
				String prodName = aProducer.description; 
%>
<option title="Producer-<%= prodId%>" value="<%= prodId%>"><%= prodName%> (ID: <%= prodId%>)</option>
<%
			}
%>
</select>
<%
		}
	} else if ("validateProducerAgent".equals(query)) {
		String producerAgent = request.getParameter("producerAgent");
		String agentPassword = request.getParameter("agentPassword");
		// Validate producer agent name first
		boolean status = dpsArchive.validateProducerAgentName(producerAgent);
		if (status == false) {
%>
Producer agent name <%= producerAgent %> is invalid in Rosetta.
<%
		} else {
			status = dpsArchive.isLoginSuccessful(producerAgent, agentPassword);
			if (status == false) {
%>
Unable to login. Password may be invalid.
<%
			} else {
%>
Producer agent name and password are valid.
<%
			}
		}
	} else if ("validateMaterialFlowAssociation".equals(query)) {
		String producerId = request.getParameter("producer");
		String targetDcType = request.getParameter("targetDcType");
		boolean status = dpsArchive.validateMaterialFlowAssociation(producerId, targetDcType);
		if (status == false) {
%>
Producer is not associated with the right material flow. Please correct this in Rosetta.
<%
		} else {
%>
Producer is associated with the right material flow.
<%
		}
	} else if ("sleep".equals(query)) {
		String milliSeconds = request.getParameter("milliSeconds");
		try {
			long ms = Long.parseLong(milliSeconds);
			Thread.sleep(ms);
%>
Slept over successfully for <%=  milliSeconds%> milliseconds.
<%
		} catch (Exception ex) {
%>
Sleep was interrupted by exception <%= ex %>.
<%
		}
	}
%>


