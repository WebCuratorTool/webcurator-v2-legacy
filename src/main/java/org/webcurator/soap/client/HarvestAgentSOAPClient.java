package org.webcurator.soap.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.webcurator.core.harvester.agent.HarvestAgent;
import org.webcurator.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.wsclient.HarvestAgentStatus;
import org.webcurator.wsclient.HarvestAgentStatusRequest;
import org.webcurator.wsclient.HarvestAgentStatusResponse;

public class HarvestAgentSOAPClient implements HarvestAgent {
    private static Logger log = LogManager.getLogger(HarvestAgentSOAPClient.class);
    private SOAPConnector soapConnector;

    @Override
    public HarvestAgentStatusDTO getHarvestAgentStatus(String jobNumber) {
        log.debug("Calling Harvest Agent SOAP Web Client - getHarvestAgentStatus: jobNumber=" + jobNumber);
        HarvestAgentStatusRequest harvestAgentStatusRequest = new HarvestAgentStatusRequest();
        harvestAgentStatusRequest.setJobNumber(jobNumber);
        HarvestAgentStatusResponse harvestAgentStatusResponse = (HarvestAgentStatusResponse) soapConnector.callWebService("http://localhost:8080/wct/service/harvest-agent", harvestAgentStatusRequest);
        // copy response to dto
        HarvestAgentStatus harvestAgentStatus = harvestAgentStatusResponse.getHarvestAgentStatus();
        HarvestAgentStatusDTO harvestAgentStatusDTO = new HarvestAgentStatusDTO(harvestAgentStatus.getJobNumber(),
                harvestAgentStatus.getStatus(), harvestAgentStatus.getMessage());
        return harvestAgentStatusDTO;
    }

    public void setSoapConnector(SOAPConnector soapConnector) {
        this.soapConnector = soapConnector;
    }
}
