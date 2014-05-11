package org.webcurator.core.store;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.util.WebServiceEndPoint;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;

public class MockIndexer extends Indexer 
{
	protected static Log log = LogFactory.getLog(MockIndexer.class);

	public MockIndexer()
	{
		WebServiceEndPoint wsEndPoint = new WebServiceEndPoint("TestHost", 0, "TestService");
        WCTIndexer wctIndexer = new WCTIndexer();
        wctIndexer.setDoCreate(true);
        wctIndexer.setWsEndPoint(wsEndPoint);
        List<RunnableIndex> indexers = new ArrayList<RunnableIndex>();
        indexers.add(wctIndexer);
        this.setIndexers(indexers);
	}
	
	public MockIndexer(String host, int port) {
		this();
	}
	
	public MockIndexer(String host, int port, boolean doCreate) { 
		this();
	}	
	
	public MockIndexer(String host, int port, String service) {
		this();
	}
	
	public MockIndexer(WebServiceEndPoint wsEndPoint) {
		this();
	}
	
	public MockIndexer(WebServiceEndPoint wsEndPoint, boolean doCreate) {
		this();
	}
	
	
	public MockIndexer(String host, int port, String service, boolean doCreate) {
		this();
	}	

	
	public void runIndex(ArcHarvestResultDTO dto, File directory) { 
		log.debug("Indexing: "+dto.getTargetInstanceOid()+" - "+directory.getName());
	}	
	
}
