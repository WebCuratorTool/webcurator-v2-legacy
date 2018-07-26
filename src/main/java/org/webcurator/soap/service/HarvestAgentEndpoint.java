package org.webcurator.soap.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.webcurator.xml.harvestagent.HarvestAgentStatus;
import org.webcurator.xml.harvestagent.HarvestAgentStatusRequest;
import org.webcurator.xml.harvestagent.HarvestAgentStatusResponse;

@Endpoint
public class HarvestAgentEndpoint {
    private static Log log = LogFactory.getLog(HarvestAgentEndpoint.class);
    public static final String NAMESPACE_URI = "http://webcurator.org/xml/harvestagent";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HarvestAgentStatusRequest")
    @ResponsePayload
    public HarvestAgentStatusResponse getHarvestAgentStatus(@RequestPayload HarvestAgentStatusRequest request) {
        log.debug("Calling Harvest Agent SOAP Web Service - getHarvestAgentStatus: jobNumber=" + request.getJobNumber());
        HarvestAgentStatusResponse harvestAgentStatusResponse = new HarvestAgentStatusResponse();
        HarvestAgentStatus harvestAgentStatus = new HarvestAgentStatus();
        harvestAgentStatus.setJobNumber(request.getJobNumber());
        harvestAgentStatus.setStatus(0);
        harvestAgentStatus.setMessage("Job " + request.getJobNumber() + " successful");
        harvestAgentStatusResponse.setHarvestAgentStatus(harvestAgentStatus);
        return harvestAgentStatusResponse;
    }
}
