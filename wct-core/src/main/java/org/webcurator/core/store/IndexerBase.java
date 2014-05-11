package org.webcurator.core.store;

import java.io.File;
import java.io.FilenameFilter;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.util.WCTSoapCall;
import org.webcurator.core.util.WebServiceEndPoint;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;

public abstract class IndexerBase implements RunnableIndex {
	private static Log log = LogFactory.getLog(IndexerBase.class);

	private WebServiceEndPoint wsEndPoint;
	private boolean defaultIndexer = false;
	private Mode mode = Mode.INDEX;

	public class ARCFilter implements FilenameFilter {
	    public boolean accept(File dir, String name) {
	        return (name.toLowerCase().endsWith(".arc") ||
	        		name.toLowerCase().endsWith(".arc.gz") ||
	        		name.toLowerCase().endsWith(".warc") ||
	        		name.toLowerCase().endsWith(".warc.gz"));
	    }
	}

	protected WCTSoapCall getCall(String method) throws ServiceException
	{
		return new WCTSoapCall(wsEndPoint.getHost(), wsEndPoint.getPort(), wsEndPoint.getService(), method);
	}
	
	public IndexerBase()
	{
	}
	
	protected IndexerBase(IndexerBase original)
	{
		this.defaultIndexer = original.defaultIndexer;
		this.wsEndPoint = original.wsEndPoint;
	}
	
	protected abstract ArcHarvestResultDTO getResult(); 
	
	@Override
	public void setMode(Mode mode)
	{
		this.mode = mode;
	}
	
	@Override
	public void run() {
    	Long harvestResultOid = null;
        try {
        	harvestResultOid = begin();
        	if(mode == Mode.REMOVE)
        	{
        		removeIndex(harvestResultOid);
        	}
        	else
        	{
				indexFiles(harvestResultOid);
				markComplete(harvestResultOid);
        		
        	}
        }
        catch(ServiceException ex) { 
        	throw new WCTRuntimeException("Service Exception");
        }
        finally
        {
    		synchronized(Indexer.lock)
    		{
    			Indexer.removeRunningIndex(getName(), harvestResultOid);
    		}
        }
	}

	@Override
	public final void markComplete(Long harvestResultOid) throws ServiceException { 
		
		synchronized(Indexer.lock)
		{
			if(Indexer.lastRunningIndex(this.getName(), harvestResultOid))
			{
		        log.info("Marking harvest result for job " + getResult().getTargetInstanceOid() + " as ready");
		        
		        WCTSoapCall call3 = getCall("finaliseIndex");
		        call3.infiniteRetryingInvoke(30000l, harvestResultOid);
				
				log.info("Index for job " + getResult().getTargetInstanceOid() + " is now ready");
			}

        	Indexer.removeRunningIndex(getName(), harvestResultOid);
		}
	}

	@Override
	public void removeIndex(Long harvestResultOid) { 
		//Default implementation is to do nothing
	}
	
	public void setWsEndPoint(WebServiceEndPoint wsEndPoint) {
		this.wsEndPoint = wsEndPoint;
	}

	public WebServiceEndPoint getWsEndPoint() {
		return wsEndPoint;
	}
}
