package org.webcurator.soap.service;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.webcurator.xml.harvestagent.HarvestAgentStatus;
import org.webcurator.xml.harvestagent.HarvestAgentStatusRequest;
import org.webcurator.xml.harvestagent.HarvestAgentStatusResponse;

@Endpoint
public class HarvestAgentEndpoint {
    public static final String NAMESPACE_URI = "http://webcurator.org/xml/harvestagent";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HarvestAgentStatusRequest")
    @ResponsePayload
    public HarvestAgentStatusResponse getStudent(@RequestPayload HarvestAgentStatusRequest request) {
        HarvestAgentStatusResponse harvestAgentStatusResponse = new HarvestAgentStatusResponse();
        HarvestAgentStatus harvestAgentStatus = new HarvestAgentStatus();
        harvestAgentStatus.setJobNumber(request.getJobNumber());
        harvestAgentStatus.setStatus(0);
        harvestAgentStatus.setMessage("Job " + request.getJobNumber() + " successful");
        harvestAgentStatusResponse.setHarvestAgentStatus(harvestAgentStatus);
        return harvestAgentStatusResponse;
    }
}
