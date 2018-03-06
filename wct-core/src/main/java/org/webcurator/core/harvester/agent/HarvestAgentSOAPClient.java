/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.core.harvester.agent;

import java.rmi.RemoteException;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.util.WCTSoapCall;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;

/**
 * An implementation of the HarvestAgent Interface using SOAP
 * to communicate with the HarvestAgent
 * @author nwaight
 */
public class HarvestAgentSOAPClient implements HarvestAgent, HarvestAgentConfig {
    
	/** the host name or ip-address for the harvest agent. */
    private String host = "localhost";
    
    /** the port number for the harvest agent. */
    private int port = 8080;
    
    /** the service name of the harvest agent. */
    private String service = WCTSoapCall.HARVEST_AGENT;
    
    /** the service name of the harvest agent log reader. */
    private String logReaderService = WCTSoapCall.AGENT_LOG_READER;
    
	/** the logger. */
    private static Log log = LogFactory.getLog(HarvestAgentSOAPClient.class);
    
    /**
     * Constructor to initialise the host, port and service.
     * @param aHost the name of the host
     * @param aPort the port number
     */
    //public HarvestAgentSOAPClient(String aHost, int aPort) {
    //    host = aHost;
    //    port = aPort;
    //}
    
    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#initiateHarvest(java.lang.String, java.lang.String, java.lang.String)
     */
    public void initiateHarvest(String aJob, String aProfile, String aSeeds) {
        try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "initiateHarvest");
            Object[] data = {aJob, aProfile, aSeeds};            
            call.invoke(data);
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke initiateHarvest on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke initiateHarvest on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call initiateHarvest : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call initiateHarvest : " + e.getMessage(), e);
        }
    }

    public void recoverHarvests(List<String> activeJobs) {
        try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "recoverHarvests");
            Object[] data = {activeJobs};
            call.invoke(data);
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke recoverHarvests on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke recoverHarvests on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call recoverHarvests : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call recoverHarvests : " + e.getMessage(), e);
        }
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#restrictBandwidth(java.lang.String, int)
     */
    public void restrictBandwidth(String aJob, int aBandwidthLimit) {                
        try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "restrictBandwidth");
            Object[] data = {aJob, aBandwidthLimit};            
            call.invoke(data);
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke restrictBandwidth on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke restrictBandwidth on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call restrictBandwidth : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call restrictBandwidth : " + e.getMessage(), e);
        }
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#pause(java.lang.String)
     */
    public void pause(String aJob) {
        try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "pause");
            Object[] data = {aJob};            
            call.invoke(data);
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke pause on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke pause on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call pause : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call pause : " + e.getMessage(), e);
        }
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#resume(java.lang.String)
     */
    public void resume(String aJob) {
        try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "resume");
            Object[] data = {aJob};            
            call.invoke(data);
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke resume on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke resume on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call resume : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call resume : " + e.getMessage(), e);
        }
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#abort(java.lang.String)
     */
    public void abort(String aJob) {
        try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "abort");
            Object[] data = {aJob};            
            call.invoke(data);
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke abort on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke abort on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call abort : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call abort : " + e.getMessage(), e);
        }
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#stop(java.lang.String)
     */
    public void stop(String aJob) {
        try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "stop");
            Object[] data = {aJob};            
            call.invoke(data);
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke stop on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke stop on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call stop : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call stop : " + e.getMessage(), e);
        }
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#completeHarvest(java.lang.String, int)
     */
    public int completeHarvest(String aJob, int aFailureStep) {
    	throw new WCTRuntimeException("completeHarvest is not supported from the client");
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#loadSettings(java.lang.String)
     */
    public void loadSettings(String aJob) {
        try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "loadSettings");
            Object[] data = {aJob};            
            call.invoke(data);
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke loadSettings on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke loadSettings on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call loadSettings : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call loadSettings : " + e.getMessage(), e);
        }
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#pauseAll()
     */
    public void pauseAll() {
        try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "pauseAll");            
            call.invoke();
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke pauseAll on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke pauseAll on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call pauseAll : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call pauseAll : " + e.getMessage(), e);
        }
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#resumeAll()
     */
    public void resumeAll() {
        try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "resumeAll");            
            call.invoke();
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke resumeAll on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke resumeAll on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call resumeAll : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call resumeAll : " + e.getMessage(), e);
        }
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#getStatus()
     */
    public HarvestAgentStatusDTO getStatus() {
        try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "getStatus");            
            return (HarvestAgentStatusDTO) call.invoke();
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke getStatus on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke getStatus on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call getStatus : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call getStatus : " + e.getMessage(), e);
        }
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#getName()
     */
	public String getName() {		
		return null;
	}

    
	/**
	 * @see org.webcurator.core.harvester.agent.HarvestAgent#getMemoryWarning()
	 */
	public boolean getMemoryWarning() {
        return getStatus().getMemoryWarning();
	}

	/**
	 * @see org.webcurator.core.harvester.agent.HarvestAgent#setMemoryWarning(boolean memoryWarning)
	 */
	public void setMemoryWarning(boolean memoryWarning) {
        if (log.isErrorEnabled()) {
            log.error("Attempt to call unsupported method setMemoryWarning()");
        }
 	}
	
	/**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#updateProfileOverrides(String, String)
     */
	public void updateProfileOverrides(String aJob, String aProfile) {
		try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "updateProfileOverrides");
            Object[] data = {aJob, aProfile};            
            call.invoke(data);
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke updateProfileOverrides on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke updateProfileOverrides on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call updateProfileOverrides : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call updateProfileOverrides : " + e.getMessage(), e);
        }		
	}

	/**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#purgeAbortedTargetInstances(String[])
     */
	public void purgeAbortedTargetInstances(String [] targetInstanceNames) {

        try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "purgeAbortedTargetInstances");
			call.invoke((Object)targetInstanceNames);
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke purgeAbortedTargetInstances on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke purgeAbortedTargetInstances on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call purgeAbortedTargetInstances : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call purgeAbortedTargetInstances : " + e.getMessage(), e);
        }		
	}
	
	/**
     * @param host The host to set.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @param port The port to set.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @param service The service to set.
     */
    public void setService(String service) {
        this.service = service;
    }

	public String getHarvestAgentServiceName() {
		return service;
	}

	public String getHost() {
		return host;
	}

	public String getLogReaderServiceName() {
		return logReaderService;
	}

	public int getPort() {
		return port;
	}

	/**
	 * @param logReaderService the logReaderService to set
	 */
	public void setLogReaderService(String logReaderService) {
		this.logReaderService = logReaderService;
	}
	
}
