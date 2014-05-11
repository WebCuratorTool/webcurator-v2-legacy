package org.webcurator.core.store;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.util.WCTSoapCall;
import org.webcurator.domain.model.core.ArcHarvestFileDTO;
import org.webcurator.domain.model.core.ArcHarvestResourceDTO;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.domain.model.core.HarvestResourceDTO;

class WCTIndexer extends IndexerBase 
{
	private static Log log = LogFactory.getLog(WCTIndexer.class);
	
	private ArcHarvestResultDTO result;
	private File directory;
	private boolean doCreate = false;

	public WCTIndexer()
	{
	}
	
	protected WCTIndexer(WCTIndexer original)
	{
		super(original);
	}
	
	protected Long createIndex() throws ServiceException {
		// Step 1. Save the Harvest Result to the database.
		log.info("Initialising index for job " + getResult().getTargetInstanceOid());
        
        WCTSoapCall call = getCall("createHarvestResult");
        call.regTypes(ArcHarvestResultDTO.class);
        Long harvestResultOid = (Long) call.infiniteRetryingInvoke(30000l, getResult());
        log.info("Initialised index for job " + getResult().getTargetInstanceOid());
        
        return harvestResultOid;
	}
	
	@Override
	public Long begin() throws ServiceException
	{
    	Long harvestResultOid = null;
    	if(doCreate) { 
    		harvestResultOid = this.createIndex();
    		log.debug("Created new Harvest Result: " + harvestResultOid);
    	}
    	else {
    		log.debug("Using Harvest Result " + getResult().getOid());
    		harvestResultOid = getResult().getOid();
    	}
    	
    	return harvestResultOid;
	}
	
	@Override
	public void indexFiles(Long harvestResultOid) throws ServiceException {
		// Step 2. Save the Index for each file.
        log.info("Generating indexes for " + getResult().getTargetInstanceOid());
        File[] fileList = directory.listFiles(new ARCFilter());
        if(fileList == null) { 
        	log.error("Could not find any archive files in directory: " + directory.getAbsolutePath() );
        }
        else {
            for(File f: fileList) {
        		ArcHarvestFileDTO ahf = new ArcHarvestFileDTO();
                ahf.setName(f.getName());
                ahf.setBaseDir(directory.getAbsolutePath());
            	
                try {
                    ahf.setCompressed(ahf.checkIsCompressed());

                	log.info("Indexing " + ahf.getName());
                    Map<String, HarvestResourceDTO> resources = ahf.index();
                    Collection<HarvestResourceDTO> dtos = resources.values();
                    
                    // Submit to the server.
                    log.info("Sending Arc Harvest File " + ahf.getName());
                    WCTSoapCall call2 = getCall("addToHarvestResult");
                    call2.regTypes(ArcHarvestFileDTO.class);
                    call2.infiniteRetryingInvoke(30000l, harvestResultOid, ahf);
                   
                    log.info("Sending Resources for " + ahf.getName());
                    WCTSoapCall call3 = getCall("addHarvestResources");
                    call3.regTypes(ArcHarvestResourceDTO.class);
                    call3.infiniteRetryingInvoke(30000l, harvestResultOid, dtos);
                   
                    log.info("Completed indexing of " + ahf.getName());
                }
                catch(IOException ex) { 
                	log.error("Could not index file " + ahf.getName() + ". Ignoring and continuing with other files. "+ex.getClass().getCanonicalName()+": "+ ex.getMessage());
                }
                catch(ParseException ex) { 
                	log.error("Could not index file " + ahf.getName() + ". Ignoring and continuing with other files. "+ex.getClass().getCanonicalName()+": "+ ex.getMessage());
                }
            }
        }
        log.info("Completed indexing for job " + getResult().getTargetInstanceOid());		
	}

	@Override
	public String getName() {
		return getClass().getCanonicalName();
	}

	public void setDoCreate(boolean doCreate) {
		this.doCreate = doCreate;
	}

	@Override
	public void initialise(ArcHarvestResultDTO result, File directory) {
		this.result = result;
		this.directory = directory;
	}

	@Override
	protected ArcHarvestResultDTO getResult() {
		return result;
	}

	@Override
	public RunnableIndex getCopy() {
		return new WCTIndexer(this);
	}

	@Override
	public boolean isEnabled() {
		//WCT indexer is always enabled
		return true;
	}
	
}

