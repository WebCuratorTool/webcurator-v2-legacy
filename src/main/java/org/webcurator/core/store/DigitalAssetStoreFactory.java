package org.webcurator.core.store;

import org.webcurator.core.reader.LogReader;

/**
 * Interface for a factory to create instances of a DigitalAssetStore.
 * @author kurwin
 */
public interface DigitalAssetStoreFactory {

    /**
     * Return an instance of the DigitalAssetStore.
     * @return the DigitalAssetStore
     */
	   public DigitalAssetStore getDAS();    

	    /**
	     * Return an instance of the log reader for the DigitalAssetStore
	     * @return the log reader
	     */
	    public LogReader getLogReader();       

}
