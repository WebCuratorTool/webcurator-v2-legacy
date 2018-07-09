package org.webcurator.core.store;

import org.webcurator.core.store.DigitalAssetStore;
import org.webcurator.core.store.DigitalAssetStoreSOAPClient;
import org.webcurator.core.reader.LogReader;
import org.webcurator.core.reader.LogReaderSOAPClient;

/**
 * Factory to create DigitalAssetStore instances that use SOAP to communicate 
 * with a remote DigitalAssetStore
 * @author kurwin
 */
public class DigitalAssetStoreFactoryImpl implements DigitalAssetStoreFactory {
	private DigitalAssetStoreConfig digitalAssetStoreConfig;
    /** @see org.webcurator.core.harvester.agent.HarvestAgentFactory#getHarvestAgent(String, int). */
    public DigitalAssetStore getDAS() {        
    	DigitalAssetStoreSOAPClient store = new DigitalAssetStoreSOAPClient();
    	store.setHost(digitalAssetStoreConfig.getHost());
    	store.setPort(digitalAssetStoreConfig.getPort());
    	store.setService(digitalAssetStoreConfig.getAssetStoreServiceName());
    	store.setLogReaderService(digitalAssetStoreConfig.getLogReaderServiceName());
    	return store;
    }
    
    /** @see org.webcurator.core.harvester.agent.HarvestAgentFactory#getHarvestAgent(String, int). */
    public LogReader getLogReader() {        
        return new LogReaderSOAPClient(digitalAssetStoreConfig.getHost(),
        		digitalAssetStoreConfig.getPort(), 
        		digitalAssetStoreConfig.getLogReaderServiceName());
    }
    
    public void setDigitalAssetStoreConfig(DigitalAssetStoreConfig digitalAssetStoreConfig)
    {
    	this.digitalAssetStoreConfig = digitalAssetStoreConfig;
    }
    
    public DigitalAssetStoreConfig getDigitalAssetStoreConfig()
    {
    	return digitalAssetStoreConfig;
    }
}
