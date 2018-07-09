package org.webcurator.domain;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.ArcHarvestFile;
import org.webcurator.domain.model.core.ArcHarvestResource;
import org.webcurator.domain.model.core.ArcHarvestResult;
import org.webcurator.domain.model.core.HarvestResource;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResult;
import org.webcurator.domain.model.core.HarvesterStatus;
import org.webcurator.domain.model.core.ProfileOverrides;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.HarvestHistoryDTO;
import org.webcurator.domain.model.dto.QueuedTargetInstanceDTO;
import org.webcurator.domain.model.dto.TargetInstanceDTO;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MockTargetInstanceDAO implements TargetInstanceDAO {

	private static Log log = LogFactory.getLog(MockTargetInstanceDAO.class);
	private Document theFile = null; 
    private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private Map<Long,TargetInstance> tiOids = new HashMap<Long, TargetInstance>();
	private Map<Long,HarvesterStatus> hsOids = new HashMap<Long, HarvesterStatus>();
	private Map<Long,HarvestResult> hrOids = new HashMap<Long, HarvestResult>();
	private Map<Long,HarvestResource> hrsOids = new HashMap<Long, HarvestResource>();
	private Map<Long,ArcHarvestFile> ahfOids = new HashMap<Long, ArcHarvestFile>();
	
	private Long baseTargetInstanceOid = 5000L;
	private Long baseHarvestResultOid = 21000L;
	
	private MockUserRoleDAO userRoleDAO = null;
	private MockAnnotationDAO annotationDAO = null;
	private MockIndicatorDAO indicatorDAO = null;
	private MockIndicatorCriteriaDAO indicatorCriteriaDAO = null;
	private MockTargetDAO targetDAO = null;
	
	public MockTargetInstanceDAO(String filename) 
	{
		try
		{
			userRoleDAO = new MockUserRoleDAO(filename);
			annotationDAO = new MockAnnotationDAO(filename);
			targetDAO = new MockTargetDAO(filename);
			
			indicatorDAO = new MockIndicatorDAO(filename);
			indicatorCriteriaDAO = new MockIndicatorCriteriaDAO(filename);
			
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        theFile = docBuilder.parse (new File(filename));
	        
	    	NodeList targetInstanceNodes = theFile.getElementsByTagName("target-instance");
	    	
	    	//force a nested load of everything
	        loadTargetInstancesFromNodeList(targetInstanceNodes);
	        
		}
    	catch (SAXParseException err) 
    	{
	        log.debug ("** Parsing error" + ", line " 
	             + err.getLineNumber () + ", uri " + err.getSystemId ());
	        log.debug(" " + err.getMessage ());
        }
    	catch (SAXException se) 
    	{
            Exception x = se.getException ();
            ((x == null) ? se : x).printStackTrace ();
        }
	    catch (Exception e) 
	    {
	    	log.debug(e.getClass().getName() + ": ");
	    	if (e.getMessage() != null){
	    		log.debug(e.getMessage());
	    	}
	    }
	}

	public int countTargetInstances(String username, ArrayList<String> states) 
	{
		int counter = 0;
		Iterator<TargetInstance> it = tiOids.values().iterator();
		while(it.hasNext())
		{
			TargetInstance ti = it.next();
			User user = userRoleDAO.getUserByName(username);
			if(user.equals(ti.getOwner()))
			{
				if(states.isEmpty())
				{
					counter++;
				}
				else if(states.contains(ti.getState()))
				{
					counter++;
				}
			}
		}
		return counter;
	}

	public int countActiveTIsForTarget(Long targetOid) 
	{
		return 1;
	}
	
	public int countTargetInstancesByTarget(Long targetOid) 
	{
		return 1;
	}

	public void delete(Object object) 
	{
		if(object instanceof TargetInstance)
		{
			TargetInstance ti = (TargetInstance)object;
			log.info("deleting TargetInstance - " + ti.getOid());
			tiOids.remove(ti.getOid());
		}
		else
		{
			log.info("delete - " + object.toString());
		}
	}

	public void deleteScheduledInstances(Schedule schedule) 
	{
		List<TargetInstance> toDelete = new ArrayList<TargetInstance>();
		Iterator<TargetInstance> it = tiOids.values().iterator();
		while(it.hasNext())
		{
			TargetInstance ti = it.next();
			if(schedule.equals(ti.getSchedule()))
			{
				toDelete.add(ti);
			}
		}
		
		it = toDelete.iterator();
		while(it.hasNext())
		{
			TargetInstance ti = it.next();
			delete(ti);
		}
	}

	public void deleteScheduledInstances(Long targetOid, Long scheduleOid) 
	{
		List<TargetInstance> toDelete = new ArrayList<TargetInstance>();
		Iterator<TargetInstance> it = tiOids.values().iterator();
		while(it.hasNext())
		{
			TargetInstance ti = it.next();
			if(scheduleOid.equals(ti.getSchedule().getOid()) &&
					targetOid.equals(ti.getTarget().getOid()))
			{
				toDelete.add(ti);
			}
		}
		
		it = toDelete.iterator();
		while(it.hasNext())
		{
			TargetInstance ti = it.next();
			delete(ti);
		}
	}

	public void deleteScheduledInstances(AbstractTarget anAbstractTarget) {
		List<TargetInstance> toDelete = new ArrayList<TargetInstance>();
		Iterator<TargetInstance> it = tiOids.values().iterator();
		while(it.hasNext())
		{
			TargetInstance ti = it.next();
			if(anAbstractTarget.getOid().equals(ti.getTarget().getOid()))
			{
				toDelete.add(ti);
			}
		}
		
		it = toDelete.iterator();
		while(it.hasNext())
		{
			TargetInstance ti = it.next();
			delete(ti);
		}
	}

	public void endDateGroups() 
	{
	}

	public List<TargetInstance> findPurgeableTargetInstances(Date purgeDate) 
	{
		List<TargetInstance> tiList = new ArrayList<TargetInstance>(); 
		Iterator<TargetInstance> it = tiOids.values().iterator();
		while(it.hasNext())
		{
			TargetInstance ti = it.next();
			if(ti.getArchivedTime().before(purgeDate))
			{
				tiList.add(ti);
			}
		}
		
		return tiList;
	}

	public List<TargetInstance> findPurgeableAbortedTargetInstances(Date purgeDate) 
	{
		List<TargetInstance> tiList = new ArrayList<TargetInstance>(); 
		Iterator<TargetInstance> it = tiOids.values().iterator();
		while(it.hasNext())
		{
			TargetInstance ti = it.next();
			if(ti.getSchedule().getEndDate().before(purgeDate))
			{
				tiList.add(ti);
			}
		}
		
		return tiList;
	}

	public List<TargetInstance> findTargetInstances(
			TargetInstanceCriteria criteria) {
		// TODO implement TargetInstanceCriteria matching
		return new ArrayList<TargetInstance>(); 
	}

	public List<HarvestHistoryDTO> getHarvestHistory(Long targetOid) {
		List<HarvestHistoryDTO> hhdtos = new ArrayList<HarvestHistoryDTO>();
		Target t = targetDAO.load(targetOid);
		Iterator<TargetInstance> it = tiOids.values().iterator();
		while(it.hasNext())
		{
			TargetInstance ti = it.next();
			if(t.equals(ti.getTarget()))
			{
				HarvestHistoryDTO hhdto = null;
				HarvesterStatus hs = ti.getStatus();
				if(hs != null)
				{
					hhdto = new HarvestHistoryDTO(ti.getOid(), 
							ti.getActualStartTime(), 
							ti.getState(), 
							hs.getDataDownloaded(), 
							hs.getUrlsDownloaded(), 
							hs.getUrlsFailed(), 
							hs.getElapsedTime(), 
							hs.getAverageKBs(), 
							hs.getStatus());
				}
				else
				{
					hhdto = new HarvestHistoryDTO(ti.getOid(), 
							ti.getActualStartTime(), 
							ti.getState(), 
							0L, 
							0L, 
							0L, 
							0L, 
							0.0, 
							"");
				}
				
				hhdtos.add(hhdto);
			}
		}
		
		return hhdtos;
	}

	public HarvestResourceDTO getHarvestResourceDTO(long harvestResultOid,
			String resource) {
		HarvestResult hr = hrOids.get(harvestResultOid);
		HarvestResource hrs = hr.getResources().get(resource);
		return hrs.buildDTO();
	}
	
	@Override
	public List<HarvestResourceDTO> getHarvestResourceDTOs(long harvestResultOid) {
		HarvestResult hr = hrOids.get(harvestResultOid);
		Iterator<HarvestResource> it = hr.getResources().values().iterator();
		
		List<HarvestResourceDTO> resources = new ArrayList<HarvestResourceDTO>();
		
		while (it.hasNext()) {
			HarvestResource hrc = it.next();
			// build the DTO
			HarvestResourceDTO hrcDTO = hrc.buildDTO();
			// add to the resources list
			resources.add(hrcDTO);
		}
		
		return resources;
	}

	public HarvestResult getHarvestResult(Long harvestResultOid) {
		
		return hrOids.get(harvestResultOid);
	}

	public HarvestResult getHarvestResult(Long harvestResultOid,
			boolean loadFully) {
		return getHarvestResult(harvestResultOid);
	}

	public List<HarvestResult> getHarvestResults(long targetInstanceId) {
		
		TargetInstance ti =  tiOids.get(targetInstanceId);
		if(ti != null)
		{
			return ti.getHarvestResults();
		}
		
		return new ArrayList<HarvestResult>();
	}

	public List<QueuedTargetInstanceDTO> getQueue() {
		List<QueuedTargetInstanceDTO> tiList = new ArrayList<QueuedTargetInstanceDTO>(); 
		Iterator<TargetInstance> it = tiOids.values().iterator();
		while(it.hasNext())
		{
			TargetInstance ti = it.next();
			if(TargetInstance.STATE_QUEUED.equals(ti.getState()))
			{
				tiList.add(new QueuedTargetInstanceDTO(ti.getOid(), 
						ti.getScheduledTime(), 
						ti.getPriority(), 
						ti.getState(), 
						ti.getBandwidthPercent(), 
						ti.getOwner().getAgency().getName()));
			}
		}
		
		return tiList;
	}

	public TargetInstanceDTO getTargetInstanceDTO(Long oid) {
		TargetInstance ti = tiOids.get(oid);
		return new TargetInstanceDTO(oid, 
				ti.getScheduledTime(), 
				ti.getPriority(), 
				ti.getState(), 
				ti.getOwner().getOid());
	}

	public TargetInstance load(long targetInstanceOid) {
		return tiOids.get(targetInstanceOid);
	}

	public TargetInstance populate(TargetInstance targetInstance) {
		return tiOids.get(targetInstance.getOid());
	}

	public void save(Object object) {
		if(object instanceof TargetInstance)
		{
			TargetInstance ti =(TargetInstance)object; 
			if(ti.getOid() == null)
			{
				ti.setOid(baseTargetInstanceOid + tiOids.size());
				tiOids.put(ti.getOid(), ti);
			}
			log.debug("Saved TargetInstance "+ti.getOid());
		}
		else if(object instanceof HarvestResult)
		{
			HarvestResult hr = (HarvestResult)object;
			if(hr.getOid() == null)
			{
				hr.setOid(baseHarvestResultOid + hrOids.size());
				hrOids.put(hr.getOid(), hr);
			}
			log.debug("Saved HarvestResult "+hr.getOid());
		}
		else
		{
			log.debug("Saving "+object.toString());
		}

	}

	public void saveAll(Collection collection) {
		Iterator it = collection.iterator();
		while(it.hasNext())
		{
			save(it.next());
		}
	}

	public Pagination search(TargetInstanceCriteria criteria, int page, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void deleteHarvestResources(Long targetInstanceId) 
	{
		List<HarvestResult> hrs = getHarvestResults(targetInstanceId);
		Iterator<HarvestResult> it = hrs.iterator();
		while(it.hasNext())
		{
			deleteHarvestResultResources(it.next().getOid());
		}
	}
	
	public void deleteIndicators() 
	{
		indicatorDAO.deleteIndicators();
	}

	public void deleteHarvestResultResources(Long harvestResultId) 
	{
		//fully load the harvest result
		HarvestResult hr = getHarvestResult(harvestResultId, true);
		
		//delete all the associated resources
		if(hr.getResources() != null)
		{
			hr.getResources().clear();
		}
	}

	public void deleteHarvestResultFiles(Long harvestResultId) 
	{
		//fully load the harvest result
		HarvestResult hr = getHarvestResult(harvestResultId, true);
		
		if(hr instanceof ArcHarvestResult)
		{
			//delete all the associated resources
			if(((ArcHarvestResult)hr).getArcFiles() != null)
			{
				((ArcHarvestResult)hr).getArcFiles().clear();
			}
		}
	}

    protected Set<TargetInstance> loadTargetInstancesFromNodeList(NodeList tiNodes)
    {
    	Set<TargetInstance> targetInstances = new HashSet<TargetInstance>();
    	for (int i = 0; i < tiNodes.getLength(); i++)
    	{
    		Node tiNode = tiNodes.item(i);
    		if(tiNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			targetInstances.add(loadTargetInstanceFromNode(tiNode));
    		}
    	}
    	
    	return targetInstances;
    }
    
    protected TargetInstance loadTargetInstanceFromNode(Node tiNode)
    {
    	//Check the oid first
    	Long oid = getOid(tiNode);
    	if(oid != null && tiNode.hasChildNodes() && !tiOids.containsKey(oid))
    	{
    		TargetInstance ti = new TargetInstance();
    		ti.setOid(oid);
    		
	 		NodeList children = tiNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("target"))
					{
						ti.setTarget(targetDAO.load(getOid(child)));
					}
					else if(child.getNodeName().equals("harvestResults"))
					{
						ti.setHarvestResults(loadHarvestResultsFromNodeList(ti, child.getChildNodes()));
					}
					else if(child.getNodeName().equals("schedule"))
					{
						Long schedOid = getOid(child);
						if(schedOid != null)
						{
							Schedule schedule = targetDAO.loadSchedule(schedOid);
							ti.setSchedule(schedule);
							Set<TargetInstance> tis = schedule.getTargetInstances();
							tis.add(ti);
						}
					}
					else if(child.getNodeName().equals("scheduledTime"))
					{
						ti.setScheduledTime(getDate(child));
					}
					else if(child.getNodeName().equals("actualStartTime"))
					{
						ti.setActualStartTime(getDate(child));
					}
					else if(child.getNodeName().equals("priority"))
					{
						ti.setPriority(getInteger(child));
					}
					else if(child.getNodeName().equals("state"))
					{
						ti.setState(getString(child));
					}
					else if(child.getNodeName().equals("bandwidthPercent"))
					{
						ti.setBandwidthPercent(getInteger(child));
					}
					else if(child.getNodeName().equals("allocatedBandwidth"))
					{
						ti.setAllocatedBandwidth(getLong(child));
					}
					else if(child.getNodeName().equals("status"))
					{
						ti.setStatus(loadHarvesterStatusFromNode(child));
					}
					else if(child.getNodeName().equals("owner"))
					{
						ti.setOwner(userRoleDAO.getUserByOid(getOid(child)));
					}
					else if(child.getNodeName().equals("annotations"))
					{
						ti.setAnnotations(annotationDAO.loadAnnotationsFromNodeList(oid, ti, child.getChildNodes()));
					}
					else if(child.getNodeName().equals("indicators"))
					{
						ti.setIndicators(indicatorDAO.loadIndicatorsFromNodeList(ti, child.getChildNodes()));
					}
					//else if(child.getNodeName().equals("indicator-criterias"))
					//{
					//	ti.setIndicatorCriterias(indicatorCriteriaDAO.loadIndicatorCriteriasFromNodeList(child.getChildNodes()));
					//}
					else if(child.getNodeName().equals("version"))
					{
						ti.setVersion(getInteger(child));
					}
					else if(child.getNodeName().equals("referenceNumber"))
					{
						ti.setReferenceNumber(getString(child));
					}
					else if(child.getNodeName().equals("harvestServer"))
					{
						ti.setHarvestServer(getString(child));
					}
					else if(child.getNodeName().equals("sipParts"))
					{
						//TODO: handle SIP Parts
					}
					else if(child.getNodeName().equals("originalSeeds"))
					{
						Set<String> originalSeeds = new HashSet<String>();
						NodeList osNodes = child.getChildNodes();
				    	for (int j = 0; j < osNodes.getLength(); j++)
				    	{
				    		Node osNode = osNodes.item(j);
				    		if(osNode.getNodeType() == Node.ELEMENT_NODE)
				    		{
				    			originalSeeds.add(getString(osNode));
				    		}
				    	}
						
						ti.setOriginalSeeds(originalSeeds);
					}
					else if(child.getNodeName().equals("overrides"))
					{
						ProfileOverrides overrides = new ProfileOverrides();
						//TODO: populate this object
						ti.setOverrides(overrides);
					}
					else if(child.getNodeName().equals("archiveIdentifier"))
					{
						ti.setArchiveIdentifier(getString(child));
					}
					else if(child.getNodeName().equals("purged"))
					{
						ti.setPurged(getBool(child));
					}
					else if(child.getNodeName().equals("displayInstance"))
					{
						ti.setDisplay(getBool(child));
					}
					else if(child.getNodeName().equals("displayNote"))
					{
						ti.setDisplayNote(getString(child));
					}
					else if(child.getNodeName().equals("flagged"))
					{
						ti.setFlagged(getBool(child));
					}
				}
			}
			
			if(tiOids.isEmpty())
			{
				//first one
				baseTargetInstanceOid = oid;
			}
			
			tiOids.put(oid, ti);
    	}
    	
    	return tiOids.get(oid);
    }

    protected List<HarvestResult> loadHarvestResultsFromNodeList(TargetInstance ti, NodeList hrNodes)
    {
    	List<HarvestResult> harvestResults = new ArrayList<HarvestResult>();
    	for (int i = 0; i < hrNodes.getLength(); i++)
    	{
    		Node hrNode = hrNodes.item(i);
    		if(hrNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			harvestResults.add(loadHarvestResultFromNode(ti, hrNode));
    		}
    	}
    	
    	return harvestResults;
    }
    
    
    protected Map<String,HarvestResource> loadHarvestResourcesFromNodeList(ArcHarvestResult hr, NodeList hrsNodes)
    {
    	Map<String,HarvestResource> harvestResources = new Hashtable<String,HarvestResource>();
    	for (int i = 0; i < hrsNodes.getLength(); i++)
    	{
    		Node hrsNode = hrsNodes.item(i);
    		if(hrsNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			HarvestResource hrs = loadHarvestResourceFromNode(hr, hrsNode);
    			harvestResources.put(hrs.getName(), hrs);
    		}
    	}
    	
    	return harvestResources;
    }
    
    protected Set<ArcHarvestFile> loadArcFilesFromNodeList(ArcHarvestResult hr, NodeList hrsNodes)
    {
    	Set<ArcHarvestFile> harvestFiles = new HashSet<ArcHarvestFile>();
    	for (int i = 0; i < hrsNodes.getLength(); i++)
    	{
    		Node hrsNode = hrsNodes.item(i);
    		if(hrsNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			ArcHarvestFile ahf = loadArcFileFromNode(hr, hrsNode);
    			harvestFiles.add(ahf);
    		}
    	}
    	
    	return harvestFiles;
    }
    
    protected HarvestResult loadHarvestResultFromNode(TargetInstance ti, Node hrNode)
    {
       	//Check the oid first
    	Long oid = getOid(hrNode);
    	if(oid != null &&  hrNode.hasChildNodes() && !hrOids.containsKey(oid))
    	{
    		ArcHarvestResult hr = new ArcHarvestResult();
    		hr.setOid(oid);
    		hr.setTargetInstance(ti);
    		
	 		NodeList children = hrNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("harvestNumber"))
					{
						hr.setHarvestNumber(getInteger(child));
					}
					else if(child.getNodeName().equals("resources"))
					{
						hr.setResources(loadHarvestResourcesFromNodeList(hr, child.getChildNodes()));
					}
					else if(child.getNodeName().equals("provenanceNote"))
					{
						hr.setProvenanceNote(getString(child));
					}
					else if(child.getNodeName().equals("creationDate"))
					{
						hr.setCreationDate(getDate(child));
					}
					else if(child.getNodeName().equals("createdBy"))
					{
						hr.setCreatedBy(userRoleDAO.getUserByOid(getOid(child)));
					}
					else if(child.getNodeName().equals("state"))
					{
						hr.setState(getInteger(child));
					}
					else if(child.getNodeName().equals("modificationNotes"))
					{
						List<String> modificationNotes = new ArrayList<String>();
						NodeList mnNodes = child.getChildNodes();
				    	for (int j = 0; j < mnNodes.getLength(); j++)
				    	{
				    		Node mnNode = mnNodes.item(j);
				    		if(mnNode.getNodeType() == Node.ELEMENT_NODE)
				    		{
				    			modificationNotes.add(getString(mnNode));
				    		}
				    	}
						
						hr.setModificationNotes(modificationNotes);
					}
					else if(child.getNodeName().equals("derivedFrom"))
					{
						hr.setDerivedFrom(getInteger(child));
					}
					else if(child.getNodeName().equals("arcFiles"))
					{
						hr.setArcFiles(loadArcFilesFromNodeList(hr, child.getChildNodes()));
					}
				}
			}
			
			if(hrOids.isEmpty())
			{
				//first one
				baseHarvestResultOid = oid;
			}
			
			hrOids.put(oid, hr);
    	}
     	
    	return hrOids.get(oid);
    }
    
    protected HarvestResource loadHarvestResourceFromNode(ArcHarvestResult hr, Node hrsNode)
    {
    	Long oid = getOid(hrsNode);
    	if(oid != null &&  hrsNode.hasChildNodes() && !hrsOids.containsKey(oid))
    	{
    		ArcHarvestResource hrs = new ArcHarvestResource();
    		hrs.setOid(oid);
    		hrs.setResult(hr);
    		
	 		NodeList children = hrsNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("length"))
					{
						hrs.setLength(getLong(child));
					}
					else if(child.getNodeName().equals("name"))
					{
						hrs.setName(getString(child));
					}
					else if(child.getNodeName().equals("statusCode"))
					{
						hrs.setStatusCode(getInteger(child));
					}
					else if(child.getNodeName().equals("arcFileName"))
					{
						hrs.setArcFileName(getString(child));
					}
					else if(child.getNodeName().equals("compressed"))
					{
						hrs.setCompressed(getBool(child));
					}
					else if(child.getNodeName().equals("resourceLength"))
					{
						hrs.setResourceLength(getLong(child));
					}
					else if(child.getNodeName().equals("resourceOffset"))
					{
						hrs.setResourceOffset(getLong(child));
					}
				}
			}
			
			hrsOids.put(oid, hrs);
    	}
     	
    	return hrsOids.get(oid);
    }

    protected ArcHarvestFile loadArcFileFromNode(ArcHarvestResult hr, Node ahfNode)
    {
    	Long oid = getOid(ahfNode);
    	if(oid != null &&  ahfNode.hasChildNodes() && !ahfOids.containsKey(oid))
    	{
    		ArcHarvestFile ahf = new ArcHarvestFile();
    		ahf.setOid(oid);
    		ahf.setHarvestResult(hr);
    		
	 		NodeList children = ahfNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("name"))
					{
						ahf.setName(getString(child));
					}
					else if(child.getNodeName().equals("compressed"))
					{
						ahf.setCompressed(getBool(child));
					}
					else if(child.getNodeName().equals("baseDir"))
					{
						ahf.setBaseDir(getString(child));
					}
				}
			}
			
			ahfOids.put(oid, ahf);
    	}
     	
    	return ahfOids.get(oid);
    }

    protected HarvesterStatus loadHarvesterStatusFromNode(Node hsNode)
    {
    	//Check the oid first
    	Long oid = getOid(hsNode);
    	if(oid != null && hsNode.hasChildNodes() && !hsOids.containsKey(oid))
    	{
        	HarvesterStatus hs = new HarvesterStatus();
    		hs.setOid(oid);
    		
	 		NodeList children = hsNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("jobName"))
					{
						hs.setJobName(getString(child));
					}
					else if(child.getNodeName().equals("averageURIs"))
					{
						hs.setAverageURIs(getDouble(child));
					}
					else if(child.getNodeName().equals("averageKBs"))
					{
						hs.setAverageKBs(getDouble(child));
					}
					else if(child.getNodeName().equals("urlsDownloaded"))
					{
						hs.setUrlsDownloaded(getLong(child));
					}
					else if(child.getNodeName().equals("urlsFailed"))
					{
						hs.setUrlsFailed(getLong(child));
					}
					else if(child.getNodeName().equals("dataDownloaded"))
					{
						hs.setDataDownloaded(getLong(child));
					}
					else if(child.getNodeName().equals("status"))
					{
						hs.setStatus(getString(child));
					}
					else if(child.getNodeName().equals("elapsedTime"))
					{
						hs.setElapsedTime(getLong(child));
					}
					else if(child.getNodeName().equals("alertCount"))
					{
						hs.setAlertCount(getInteger(child));
					}
				}
			}
			
			hsOids.put(oid, hs);
    	}
    	
    	return hsOids.get(oid);
    }
	
    private String getString(Node child)
    {
    	return child.getTextContent();
    }
    
    private Integer getInteger(Node child)
    {
    	if(getString(child).isEmpty())
    	{
    		return null;
    	}
    	
    	return new Integer(getString(child));
    }
    
    private Double getDouble(Node child)
    {
    	if(getString(child).isEmpty())
    	{
    		return null;
    	}
    	
    	return new Double(getString(child));
    }
    
    private boolean getBool(Node child)
    {
    	return (getString(child).equals("true"));
    }
    
    private Long getLong(Node child)
    {
    	if(getString(child).isEmpty())
    	{
    		return null;
    	}
    	
    	return new Long(getString(child));
    }
    
    private Date getDate(Node child)
    {
		try
		{
			if(getString(child).length() > 0)
			{
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				return format.parse(getString(child));
			}
		}
		catch(Exception e)
		{
			log.debug("Error parsing date for '"+child.getNodeName()+"' node: "+e.getMessage());
		}

		return null;
    }
    
    private Long getOid(Node child)
    {
		Node idNode = child.getAttributes().getNamedItem("id");
		if(idNode != null){
		return new Long(idNode.getNodeValue());
		}
		else
		{
			return null;
		}
    }

	@Override
	public List<QueuedTargetInstanceDTO> getQueueForTarget(Long targetOid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long countQueueLengthForTarget(Long targetOid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QueuedTargetInstanceDTO> getUpcomingJobs(long futureMs) {
		// TODO Auto-generated method stub
		return null;
	}


}
