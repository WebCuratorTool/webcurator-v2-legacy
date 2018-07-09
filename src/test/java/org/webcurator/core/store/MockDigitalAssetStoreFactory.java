package org.webcurator.core.store;

import org.webcurator.core.reader.*;

public class MockDigitalAssetStoreFactory implements DigitalAssetStoreFactory 
{
	DigitalAssetStore store;
	LogReader reader;
	
	public MockDigitalAssetStoreFactory()
	{
		store = new MockDigitalAssetStore();
		reader = new MockLogReader();
	}
	
	public MockDigitalAssetStoreFactory(DigitalAssetStore store)
	{
		this.store = store;
		reader = new MockLogReader();
	}
	
	public MockDigitalAssetStoreFactory(DigitalAssetStore store, LogReader reader)
	{
		this.store = store;
		this.reader = reader;
	}
	
	public DigitalAssetStore getDAS() {
		return store;
	}

	public LogReader getLogReader() {
		return reader;
	}

}
