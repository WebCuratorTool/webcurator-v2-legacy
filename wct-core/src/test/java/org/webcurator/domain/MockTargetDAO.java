package org.webcurator.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.text.SimpleDateFormat;

//XML file imports
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 


import org.webcurator.core.profiles.MockProfileManager;
import org.webcurator.core.targets.PermissionCriteria;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.DublinCore;
import org.webcurator.domain.model.core.GroupMember;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.domain.model.core.ProfileOverrides;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.AbstractTargetDTO;
import org.webcurator.domain.model.dto.GroupMemberDTO;

public class MockTargetDAO implements TargetDAO {

	private static Log log = LogFactory.getLog(TargetInstanceDAO.class);
	private Document theFile = null; 
    private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    private BusinessObjectFactory factory = new BusinessObjectFactory();

	protected Map<Long,Target> tOids = new HashMap<Long, Target>();
	protected Map<String,Target> tNames = new HashMap<String, Target>();
	protected Map<Long,TargetGroup> gOids = new HashMap<Long, TargetGroup>();
	protected Map<String,TargetGroup> gNames = new HashMap<String, TargetGroup>();
	protected Map<Long, GroupMember> gmOids = new HashMap<Long, GroupMember>();
	protected Map<Long,Seed> sOids = new HashMap<Long, Seed>();
	protected Map<Long,Schedule> schOids = new HashMap<Long, Schedule>();
	protected Map<Long,Profile> pOids = new HashMap<Long, Profile>();
	protected Map<Long,DublinCore> dcOids = new HashMap<Long, DublinCore>();
	    
	private MockUserRoleDAO userRoleDAO = null;
	private MockAnnotationDAO annotationDAO = null;
	private MockSiteDAO siteDAO = null;

	public MockTargetDAO(String filename) 
	{
		
		factory.setProfileManager(new MockProfileManager(filename));
		userRoleDAO = new MockUserRoleDAO(filename);
		annotationDAO = new MockAnnotationDAO(filename);
		siteDAO = new MockSiteDAO(filename);
		
		try
		{
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        theFile = docBuilder.parse (new File(filename));
	        
	    	NodeList profileNodes = theFile.getElementsByTagName("profile");
	    	NodeList targetNodes = theFile.getElementsByTagName("target");
	    	NodeList groupNodes = theFile.getElementsByTagName("group");
	    	NodeList groupMemberNodes = theFile.getElementsByTagName("group-member");
	    	
	    	//force a nested load of everything
	        loadProfilesFromNodeList(profileNodes);
	        loadTargetsFromNodeList(targetNodes);
	        loadGroupsFromNodeList(groupNodes);
	        loadGroupMembersFromNodeList(groupMemberNodes);
	        
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
	    	log.debug(e.getClass().getName() + ": " + e.getMessage ());
	    }
	}

	public int countTargetGroups(String username) {
		int counter = 0;
		Iterator<TargetGroup> it = gOids.values().iterator();
		while(it.hasNext())
		{
			TargetGroup g = it.next();
			if(userRoleDAO.getUserByName(username).equals(g.getOwner()))
			{
				counter++;
			}
		}
		return counter;
	}

	public int countTargets(String username) 
	{
		int counter = 0;
		Iterator<Target> it = tOids.values().iterator();
		while(it.hasNext())
		{
			Target t = it.next();
			if(userRoleDAO.getUserByName(username).equals(t.getOwner()))
			{
				counter++;
			}
		}
		return counter;
	}

	public void delete(Target target) 
	{
		tOids.remove(target.getOid());
		log.debug("delete Target: "+target.getName());
	}

	public boolean deleteGroup(TargetGroup targetGroup) 
	{
		gOids.remove(targetGroup.getOid());
		log.debug("delete Target: "+targetGroup.getName());
		return true;
	}

	public List<TargetGroup> findEndedGroups() 
	{
		List<TargetGroup> tgs = new ArrayList<TargetGroup>();
		Iterator<TargetGroup> it = gOids.values().iterator();
		while(it.hasNext())
		{
			TargetGroup g = it.next();
			if(g.getToDate().before(new Date()))
			{
				tgs.add(g);
			}
		}
		return tgs;
	}

	public Pagination getTargetsForProfile(int pageNumber, int pageSize, Long profileOid, String agencyName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Pagination getAbstractTargetDTOsForProfile(int pageNumber, int pageSize, Long profileOid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Pagination getAbstractTargetDTOs(String name, int pageNumber, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<AbstractTargetDTO> getAncestorDTOs(Long childOid) {
		Set<AbstractTargetDTO> atDtos = new HashSet<AbstractTargetDTO>();
		// TODO implement TargetGroup
		return atDtos;
	}

	public Set<Long> getAncestorOids(Long childOid) {
		Set<Long> oids = new HashSet<Long>();
		// TODO implement TargetGroup
		return oids;
	}

	public Pagination getGroupDTOs(String name, int pageNumber, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Long> getImmediateChildrenOids(Long groupOid) {
		Set<Long> oids = new HashSet<Long>();
		TargetGroup group = gOids.get(groupOid);
		Iterator<GroupMember> it = group.getChildren().iterator();
		while(it.hasNext())
		{
			GroupMember gm = it.next();
			oids.add(gm.getChild().getOid());
		}
		return oids;
	}

	public Date getLatestScheduledDate(AbstractTarget target, Schedule schedule) 
	{
		return schedule.getNextExecutionDate();
	}

	public List<Seed> getLinkedSeeds(Permission permission) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Pagination getMembers(TargetGroup targetGroup, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<Integer> getSavedMemberStates(final TargetGroup aTargetGroup) {
		if(aTargetGroup.isNew()) {
			return new LinkedList<Integer>();
		}
		else {
			List<Integer> states = new ArrayList<Integer>();
			Iterator<GroupMember> it = aTargetGroup.getChildren().iterator();
			while(it.hasNext())
			{
				GroupMember gm = it.next();
				states.add(new Integer(gm.getChild().getState()));
			}
			return states;
		}		
	}	
	
	public Pagination getParents(AbstractTarget target, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<GroupMemberDTO> getParents(AbstractTarget target) {
		List<GroupMemberDTO> gmDtos = new ArrayList<GroupMemberDTO>();
		Iterator<GroupMember> it = target.getParents().iterator();
		while(it.hasNext())
		{
			GroupMember gm = it.next();
			gmDtos.add(new GroupMemberDTO(gm.getParent(), gm.getChild()));
		}
		return gmDtos;
	}

	public List<Schedule> getSchedulesToRun() 
	{
		List<Schedule> schedules = new ArrayList<Schedule>();
		schedules.addAll(schOids.values());
		return schedules;
	}

	public Set<Seed> getSeeds(Target target) {
		return target.getSeeds();
	}

	public Set<Seed> getSeeds(TargetGroup target, Long agencyOid, String subGroupTypeName) {

		Set<Seed> seeds = new HashSet<Seed>();
		Iterator<GroupMember> it = target.getChildren().iterator();
		while(it.hasNext())
		{
			GroupMember gm = it.next();
			if(gm.getChild().getObjectType() == AbstractTarget.TYPE_GROUP)
			{
				TargetGroup childGroup = (TargetGroup)gm.getChild();
				//If the childGroup is a sub-group, we don't want to include the seeds from the sub-group members
				if(!subGroupTypeName.equals(childGroup))
				{
					seeds.addAll(getSeeds(childGroup, agencyOid, subGroupTypeName));
				}
			}
			else
			{
				Target t = (Target)gm.getChild();
				if(t.getOwningUser().getAgency().getOid().equals(agencyOid))
				{
					seeds.addAll(getSeeds((Target)gm.getChild()));
				}
			}
		}
		return seeds;
	}

	public boolean isNameOk(AbstractTarget target) {

		if(tNames.containsKey(target.getName()))
		{
			if(!(tNames.get(target.getName()).equals(target)))
			{
				return false;
			}
		}
		
		return true;
	}

	public Target load(long targetOid) {
		return tOids.get(targetOid);
	}

	public Target load(long targetOid, boolean fullyInitialise) {
		return load(targetOid);
	}

	public AbstractTarget loadAbstractTarget(Long oid) {
		if(tOids.containsKey(oid))
		{
			return tOids.get(oid);
		}
		else
		{
			return gOids.get(oid);
		}
	}

	public AbstractTargetDTO loadAbstractTargetDTO(Long oid) {
		AbstractTarget target = loadAbstractTarget(oid);
		return new AbstractTargetDTO(target.getOid(), 
									target.getName(), 
									target.getOwner().getOid(), 
									target.getOwner().getUsername(), 
									target.getOwner().getAgency().getName(), 
									target.getState(), 
									target.getProfile().getOid(), 
									target.getObjectType());
	}

	public TargetGroup loadGroup(long targetGroupOid) {
		return gOids.get(targetGroupOid);
	}

	public TargetGroup loadGroup(long targetGroupOid, boolean fullyInitialise) {
		return loadGroup(targetGroupOid);
	}

	public Integer loadPersistedGroupSipType(Long oid) {
		return loadGroup(oid).getSipType();
	}

	public void refresh(Object anObject) 
	{
		log.debug("refresh "+anObject.toString());
	}

	public Target reloadTarget(Long oid) {
		log.debug("reloadTarget "+oid);
		return load(oid);
	}

	public TargetGroup reloadTargetGroup(Long oid) {
		log.debug("reloadTargetGroup "+oid);
		return loadGroup(oid);
	}

	public void save(Target target) {
		log.debug("save Target "+target.getName());

	}

	public void save(Target target, List<GroupMemberDTO> parents) {
		log.debug("save Target and Parents "+target.getName());
		if(parents != null)
		{
			Set<GroupMember> setparents = new HashSet<GroupMember>();
		
			for(GroupMemberDTO dto : parents)
			{
				GroupMember parent = new GroupMember();
				parent.setOid(dto.getOid());
				parent.setParent(loadGroup(dto.getParentOid()));
				parent.setChild(load(dto.getChildOid()));
				setparents.add(parent);
			}
			
			target.setParents(setparents);
		}
	}

	public void save(Schedule schedule) {
		log.debug("save Schedule "+schedule.getIdentity());

	}

	public void save(TargetGroup targetGroup, boolean withChildren, List<GroupMemberDTO> parents) {
		log.debug("save TargetGroup and children "+targetGroup.getName());
		if(parents != null)
		{
			Set<GroupMember> setparents = new HashSet<GroupMember>();
		
			for(GroupMemberDTO dto : parents)
			{
				GroupMember parent = new GroupMember();
				parent.setOid(dto.getOid());
				parent.setParent(loadGroup(dto.getParentOid()));
				parent.setChild(load(dto.getChildOid()));
				setparents.add(parent);
			}
			
			targetGroup.setParents(setparents);
		}
	}

	public void saveAll(Collection collection) {
		log.debug("saveAll");
	}

	public Pagination search(int pageNumber, int pageSize, Long searchOid, String targetName,
			Set<Integer> states, String seed, String username,
			String agencyName, String memberOf, boolean nondisplayonly, String sortorder, String description) {
		return new Pagination(tOids.values(),pageNumber,20);
	}

	public Pagination searchGroups(int pageNumber, int pageSize, Long searchOid, String name,
			String owner, String agency, String memberOf, String groupType, boolean nondisplayonly) {
		return new Pagination(gOids.values(),pageNumber,20);
	}

	public Pagination searchPermissions(PermissionCriteria permissionCriteria) {
		return siteDAO.findPermissionsBySiteTitle(permissionCriteria.getAgencyOid(), permissionCriteria.getSiteName(), permissionCriteria.getPageNumber());
	}

	public void transferSeeds(Long fromPermissionOid, Long toPermissionOid) {
		Permission fromPermission = siteDAO.loadPermission(fromPermissionOid);
		Permission toPermission = siteDAO.loadPermission(toPermissionOid);
		//TODO - loop through all targets changing the permission in seeds with 
		// fromPermission to toPermission
	}

	public void delete(Object anObject) {
		log.debug("delete "+anObject.toString());

	}

	public void deleteAll(Collection collection) {
		log.debug("deleteAll");
	}

	public void evict(Object anObject) {
		log.debug("evict "+anObject.toString());
	}

	public void initialize(Object anObject) {
		log.debug("initialize "+anObject.toString());

	}
    
    protected Schedule loadSchedule(Long oid)
    {
    	return schOids.get(oid);
    }

    protected Set<Target> loadTargetsFromNodeList(NodeList tNodes)
    {
    	Set<Target> targets = new HashSet<Target>();
    	for (int i = 0; i < tNodes.getLength(); i++)
    	{
    		Node tNode = tNodes.item(i);
    		if(tNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			targets.add(loadTargetFromNode(tNode));
    		}
    	}
    	
    	return targets;
    }
    
    protected Target loadTargetFromNode(Node tNode)
    {
    	//Check the oid first
    	Long oid = getOid(tNode);
    	if(oid != null && tNode.hasChildNodes() && !tOids.containsKey(oid))
    	{
    		Target t = factory.newTarget();
    		t.setOid(oid);
    		
	 		NodeList children = tNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("displayTarget"))
					{
						t.setDisplayTarget(getBool(child));
					}
					else if(child.getNodeName().equals("accessZone"))
					{
						t.setAccessZone(getInteger(child));
					}
					else if(child.getNodeName().equals("displayNote"))
					{
						t.setDisplayNote(getString(child));
					}
					else if(child.getNodeName().equals("displayChangeReason"))
					{
						t.setDisplayChangeReason(getString(child));
					}
					else if(child.getNodeName().equals("selectionDate"))
					{
						t.setSelectionDate(getDate(child));
					}
					else if(child.getNodeName().equals("selectionType"))
					{
						t.setSelectionType(getString(child));
					}
					else if(child.getNodeName().equals("selectionNote"))
					{
						t.setSelectionNote(getString(child));
					}
					else if(child.getNodeName().equals("evaluationNote"))
					{
						t.setEvaluationNote(getString(child));
					}
					else if(child.getNodeName().equals("harvestType"))
					{
						t.setHarvestType(getString(child));
					}
					else if(child.getNodeName().equals("seeds"))
					{
						t.setSeeds(this.loadSeedsFromNodeList(t, child.getChildNodes()));
					}
					else if(child.getNodeName().equals("annotations"))
					{
						t.setAnnotations(annotationDAO.loadAnnotationsFromNodeList(oid, t, child.getChildNodes()));
					}
					else if(child.getNodeName().equals("name"))
					{
						t.setName(getString(child));
					}
					else if(child.getNodeName().equals("description"))
					{
						t.setDescription(getString(child));
					}
					else if(child.getNodeName().equals("schedules"))
					{
						t.setSchedules(loadSchedulesFromNodeList(t, child.getChildNodes()));
					}
					else if(child.getNodeName().equals("owner"))
					{
						t.setOwner(userRoleDAO.getUserByOid(getOid(child)));
					}
					else if(child.getNodeName().equals("overrides"))
					{
						ProfileOverrides overrides = new ProfileOverrides();
						//TODO: populate this object
						t.setOverrides(overrides);
					}
					else if(child.getNodeName().equals("state"))
					{
						t.changeState(getInteger(child));
					}
					else if(child.getNodeName().equals("crawls"))
					{
						t.setCrawls(getInteger(child));
					}
					else if(child.getNodeName().equals("referenceCrawlOid"))
					{
						t.setReferenceCrawlOid(getLong(child));
					}
					else if(child.getNodeName().equals("profile"))
					{
						t.setProfile(loadProfileFromNode(child));
					}
					else if(child.getNodeName().equals("creationDate"))
					{
						t.setCreationDate(getDate(child));
					}
					else if(child.getNodeName().equals("parents"))
					{
						Set<GroupMember> parents = new HashSet<GroupMember>();
						//Need to fix up parents later
						t.setParents(parents);
					}
					else if(child.getNodeName().equals("referenceNumber"))
					{
						t.setReferenceNumber(getString(child));
					}
					else if(child.getNodeName().equals("dublinCoreMetaData"))
					{
						t.setDublinCoreMetaData(loadDublinCoreFromNode(child));
					}
					else if(child.getNodeName().equals("profileNote"))
					{
						t.setProfileNote(getString(child));
					}
				}
			}
			
			tOids.put(oid, t);
			tNames.put(t.getName(), t);
    	}
    	
    	return tOids.get(oid);
    }

    protected Set<TargetGroup> loadGroupsFromNodeList(NodeList gNodes)
    {
    	Set<TargetGroup> targetGroups = new HashSet<TargetGroup>();
    	for (int i = 0; i < gNodes.getLength(); i++)
    	{
    		Node gNode = gNodes.item(i);
    		if(gNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			targetGroups.add(loadGroupFromNode(gNode));
    		}
    	}
    	
    	return targetGroups;
    }
    
    protected TargetGroup loadGroupFromNode(Node gNode)
    {
    	//Check the oid first
    	Long oid = getOid(gNode);
    	if(oid != null && gNode.hasChildNodes() && !gOids.containsKey(oid))
    	{
    		TargetGroup g = factory.newTargetGroup();
    		g.setOid(oid);
    		
	 		NodeList children = gNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("displayTarget"))
					{
						g.setDisplayTarget(getBool(child));
					}
					else if(child.getNodeName().equals("accessZone"))
					{
						g.setAccessZone(getInteger(child));
					}
					else if(child.getNodeName().equals("displayNote"))
					{
						g.setDisplayNote(getString(child));
					}
					else if(child.getNodeName().equals("displayChangeReason"))
					{
						g.setDisplayChangeReason(getString(child));
					}
					else if(child.getNodeName().equals("name"))
					{
						g.setName(getString(child));
					}
					else if(child.getNodeName().equals("description"))
					{
						g.setDescription(getString(child));
					}
					else if(child.getNodeName().equals("schedules"))
					{
						g.setSchedules(loadSchedulesFromNodeList(g, child.getChildNodes()));
					}
					else if(child.getNodeName().equals("owner"))
					{
						g.setOwner(userRoleDAO.getUserByOid(getOid(child)));
					}
					else if(child.getNodeName().equals("overrides"))
					{
						ProfileOverrides overrides = new ProfileOverrides();
						//TODO: populate this object
						g.setOverrides(overrides);
					}
					else if(child.getNodeName().equals("state"))
					{
						g.changeState(getInteger(child));
					}
					else if(child.getNodeName().equals("profile"))
					{
						g.setProfile(loadProfileFromNode(child));
					}
					else if(child.getNodeName().equals("creationDate"))
					{
						g.setCreationDate(getDate(child));
					}
					else if(child.getNodeName().equals("parents"))
					{
						Set<GroupMember> parents = new HashSet<GroupMember>();
						//Need to fix up parents later
						g.setParents(parents);
					}
					else if(child.getNodeName().equals("referenceNumber"))
					{
						g.setReferenceNumber(getString(child));
					}
					else if(child.getNodeName().equals("dublinCoreMetaData"))
					{
						g.setDublinCoreMetaData(loadDublinCoreFromNode(child));
					}
					else if(child.getNodeName().equals("profileNote"))
					{
						g.setProfileNote(getString(child));
					}
					if(child.getNodeName().equals("sipType"))
					{
						g.setSipType(getInteger(child));
					}
					else if(child.getNodeName().equals("fromDate"))
					{
						g.setFromDate(getDate(child));
					}
					else if(child.getNodeName().equals("toDate"))
					{
						g.setToDate(getDate(child));
					}
					else if(child.getNodeName().equals("children"))
					{
						Set<GroupMember> groupMembers = new HashSet<GroupMember>();
						//Need to add the children later
						g.setChildren(groupMembers);
					}
					else if(child.getNodeName().equals("type"))
					{
						g.setType(getString(child));
					}
					else if(child.getNodeName().equals("annotations"))
					{
						g.setAnnotations(annotationDAO.loadAnnotationsFromNodeList(oid, g, child.getChildNodes()));
						
					}
				}
			}
			
			gOids.put(oid, g);
			gNames.put(g.getName(), g);
    	}
    	
    	return gOids.get(oid);
    }

    
    protected DublinCore loadDublinCoreFromNode(Node dcNode)
    {
       	//Check the oid first
    	Long oid = getOid(dcNode);
    	if(oid != null && dcNode.hasChildNodes() && !dcOids.containsKey(oid))
    	{
    		DublinCore dc = new DublinCore();
    		dc.setOid(oid);
    		
	 		NodeList children = dcNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("title"))
					{
						dc.setTitle(getString(child));
					}
					else if(child.getNodeName().equals("creator"))
					{
						dc.setCreator(getString(child));
					}
					else if(child.getNodeName().equals("subject"))
					{
						dc.setSubject(getString(child));
					}
					else if(child.getNodeName().equals("description"))
					{
						dc.setDescription(getString(child));
					}
					else if(child.getNodeName().equals("publisher"))
					{
						dc.setPublisher(getString(child));
					}
					else if(child.getNodeName().equals("contributor"))
					{
						dc.setContributor(getString(child));
					}
					else if(child.getNodeName().equals("type"))
					{
						dc.setType(getString(child));
					}
					else if(child.getNodeName().equals("format"))
					{
						dc.setFormat(getString(child));
					}
					else if(child.getNodeName().equals("identifier"))
					{
						dc.setIdentifier(getString(child));
					}
					else if(child.getNodeName().equals("source"))
					{
						dc.setSource(getString(child));
					}
					else if(child.getNodeName().equals("language"))
					{
						dc.setLanguage(getString(child));
					}
					else if(child.getNodeName().equals("relation"))
					{
						dc.setRelation(getString(child));
					}
					else if(child.getNodeName().equals("coverage"))
					{
						dc.setCoverage(getString(child));
					}
					else if(child.getNodeName().equals("issn"))
					{
						dc.setIssn(getString(child));
					}
					else if(child.getNodeName().equals("isbn"))
					{
						dc.setIsbn(getString(child));
					}
 				}
			}
			
			dcOids.put(oid, dc);
    	}
    	
    	return dcOids.get(oid);
    }

    protected Set<Profile> loadProfilesFromNodeList(NodeList pNodes)
    {
    	Set<Profile> profiles = new HashSet<Profile>();
    	for (int i = 0; i < pNodes.getLength(); i++)
    	{
    		Node pNode = pNodes.item(i);
    		if(pNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			profiles.add(loadProfileFromNode(pNode));
    		}
    	}
    	
    	return profiles;
    }
        
    protected Profile loadProfileFromNode(Node pNode)
    {
       	//Check the oid first
    	Long oid = getOid(pNode);
    	if(oid != null && pNode.hasChildNodes() && !pOids.containsKey(oid))
    	{
    		Profile p = new Profile();
    		p.setOid(oid);
    		
	 		NodeList children = pNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("name"))
					{
						p.setName(getString(child));
					}
					else if(child.getNodeName().equals("description"))
					{
						p.setDescription(getString(child));
					}
					else if(child.getNodeName().equals("status"))
					{
						p.setStatus(getInteger(child));
					}
					else if(child.getNodeName().equals("requiredLevel"))
					{
						p.setRequiredLevel(getInteger(child));
					}
					else if(child.getNodeName().equals("owningAgency"))
					{
						p.setOwningAgency(userRoleDAO.getAgencyByOid(getOid(child)));
					}
					else if(child.getNodeName().equals("profile"))
					{
						p.setProfile(getString(child));
					}
					else if(child.getNodeName().equals("defaultProfile"))
					{
						p.setDefaultProfile(getBool(child));
					}
					else if(child.getNodeName().equals("version"))
					{
						p.setVersion(getInteger(child));
					}
 				}
			}
			
			pOids.put(oid, p);
    	}
    	
    	return pOids.get(oid);
    }
    
    protected Set<Seed> loadSeedsFromNodeList(Target t, NodeList sNodes)
    {
    	Set<Seed> seeds = new HashSet<Seed>();
    	for (int i = 0; i < sNodes.getLength(); i++)
    	{
    		Node sNode = sNodes.item(i);
    		if(sNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			seeds.add(loadSeedFromNode(t, sNode));
    		}
    	}
    	
    	return seeds;
    }
    
    protected Seed loadSeedFromNode(Target t, Node sNode)
    {
    	//Check the oid first
    	Long oid = getOid(sNode);
    	if(oid != null && sNode.hasChildNodes() && !sOids.containsKey(oid))
    	{
    		Seed s = factory.newSeed(t);
    		s.setOid(oid);
    		
	 		NodeList children = sNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("seed-url"))
					{
						s.setSeed(getString(child));
					}
					else if(child.getNodeName().equals("permissions"))
					{
						Set<Permission> permissions = new HashSet<Permission>();
						NodeList pNodes = child.getChildNodes();
				    	for (int j = 0; j < pNodes.getLength(); j++)
				    	{
				    		Node pNode = pNodes.item(j);
				    		if(pNode.getNodeType() == Node.ELEMENT_NODE)
				    		{
				    			permissions.add(siteDAO.loadPermission(getOid(pNode)));
				    		}
				    	}
						
						s.setPermissions(permissions);
				
					}
					else if(child.getNodeName().equals("primary"))
					{
						s.setPrimary(getBool(child));
					}
				}
			}
			
			sOids.put(oid, s);
    	}
    	
    	return sOids.get(oid);
    }

    protected Set<Schedule> loadSchedulesFromNodeList(AbstractTarget t, NodeList schNodes)
    {
    	Set<Schedule> schedules = new HashSet<Schedule>();
    	for (int i = 0; i < schNodes.getLength(); i++)
    	{
    		Node schNode = schNodes.item(i);
    		if(schNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			schedules.add(loadScheduleFromNode(t, schNode));
    		}
    	}
    	
    	return schedules;
    }
    
    protected Schedule loadScheduleFromNode(AbstractTarget target, Node schNode)
    {
    	//Check the oid first
    	Long oid = getOid(schNode);
    	if(oid != null && schNode.hasChildNodes() && !schOids.containsKey(oid))
    	{
    		Schedule sch = factory.newSchedule(target);
    		sch.setOid(oid);
    		sch.setTargetInstances(new HashSet<TargetInstance>());
    		
	 		NodeList children = schNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("startdate"))
					{
						sch.setStartDate(getDate(child));
					}
					else if(child.getNodeName().equals("enddate"))
					{
						sch.setEndDate(getDate(child));
					}
					else if(child.getNodeName().equals("cronPattern"))
					{
						sch.setCronPattern(getString(child));
					}
					else if(child.getNodeName().equals("scheduleType"))
					{
						sch.setScheduleType(getInteger(child));
					}
					else if(child.getNodeName().equals("owner"))
					{
						sch.setOwningUser(userRoleDAO.getUserByOid(getOid(child)));
					}
					else if(child.getNodeName().equals("nextScheduleAfterPeriod"))
					{
						sch.setNextScheduleAfterPeriod(getDate(child));
					}
				}
			}
			
			schOids.put(oid, sch);
    	}
    	
    	return schOids.get(oid);
    }


    protected Set<GroupMember> loadGroupMembersFromNodeList(NodeList gmNodes)
    {
    	Set<GroupMember> groupMembers = new HashSet<GroupMember>();
    	for (int i = 0; i < gmNodes.getLength(); i++)
    	{
    		Node gmNode = gmNodes.item(i);
    		if(gmNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			groupMembers.add(loadGroupMemberFromNode(gmNode));
    		}
    	}
    	
    	return groupMembers;
    }
    
    protected GroupMember loadGroupMemberFromNode(Node gmNode)
    {
    	//Check the oid first
    	Long oid = getOid(gmNode);
    	if(oid != null && gmNode.hasChildNodes() && !gmOids.containsKey(oid))
    	{
    		GroupMember gm = new GroupMember();
    		gm.setOid(oid);
    		
	 		NodeList children = gmNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("parent"))
					{
						Long parentOid = getOid(child);
						TargetGroup parent = gOids.get(parentOid);
						gm.setParent(parent);
						Set<GroupMember> gmChildren = parent.getChildren();
						if(!gmChildren.contains(gm))
						{
							gmChildren.add(gm);
						}
					}
					else if(child.getNodeName().equals("child"))
					{
						Long childOid = getOid(child);
						AbstractTarget childTarget = null;
						if(gOids.containsKey(childOid))
						{
							childTarget = gOids.get(childOid);
						}
						else
						{
							childTarget = tOids.get(childOid);
						}

						gm.setChild(childTarget);
						Set<GroupMember> gmParents = childTarget.getParents();
						if(!gmParents.contains(gm))
						{
							gmParents.add(gm);
						}
					}
				}
			}
			
			gmOids.put(oid, gm);
    	}
    	
    	return gmOids.get(oid);
    }

    private String getString(Node child)
    {
    	return child.getTextContent();
    }
    
    private Integer getInteger(Node child)
    {
    	return new Integer(getString(child));
    }
    
    private Double getDouble(Node child)
    {
    	return new Double(getString(child));
    }
    
    private boolean getBool(Node child)
    {
    	return (getString(child).equals("true"));
    }
    
    private Long getLong(Node child)
    {
    	String longValue = getString(child);
    	if (!longValue.equals("")) {
    		return new Long(longValue);
    	} 
    	return null;
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
	public Pagination getSubGroupParentDTOs(String name, List types,
			int pageNumber, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pagination getNonSubGroupDTOs(String name, String subGroupType,
			int pageNumber, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
