package org.webcurator.domain;

import java.text.SimpleDateFormat;
import java.util.*;

//XML file imports
import java.io.*;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NodeList;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.domain.model.dto.ProfileDTO;
import org.webcurator.core.exceptions.WCTInvalidStateRuntimeException;
import org.webcurator.core.util.*;
import org.webcurator.domain.model.core.*;
import org.webcurator.core.exceptions.WCTInvalidStateRuntimeException;

public class MockProfileDAO implements ProfileDAO {

	private static Log log = LogFactory.getLog(MockProfileDAO.class);
	private Document theFile = null; 
    private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	protected Map<Long,Profile> pOids = new HashMap<Long, Profile>();
	protected Map<String,Profile> pOrigOids = new HashMap<String, Profile>();
	private MockUserRoleDAO userRoleDAO = null;

	public MockProfileDAO(String filename)
	{
		super();
		try
		{
			userRoleDAO = new MockUserRoleDAO(filename);
			
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        theFile = docBuilder.parse (new File(filename));

	    	NodeList profileNodes = theFile.getElementsByTagName("profile");
	    	
	    	//force a nested load of everything
	        loadProfilesFromNodeList(profileNodes);
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
	
	public int countProfileUsage(Profile profile) {
		int counter = 0;
		
    	NodeList pNodes = theFile.getElementsByTagName("profile");
    	for (int i = 0; i < pNodes.getLength(); i++)
    	{
    		Node pNode = pNodes.item(i);
    		//Count only References in the xml file
    		if(pNode.hasChildNodes() == false)
    		{
    			Long oid = getOid(pNode);
    			if(oid != null && oid.equals(profile.getOid()))
    			{
    				counter++;
    			}
    		}
    	}
		
		return counter;
	}

	public int countProfileActiveTargets(Profile profile) {
		return 1;
	}

	public List<ProfileDTO> getAllDTOs() {
		return getDTOs(true);
	}

	public List<ProfileDTO> getAvailableProfiles(Agency anAgency, int level,
			Long currentProfileOid) 
	{
		List<ProfileDTO> profileDTOs = new ArrayList<ProfileDTO>();
		Iterator<Profile> it = pOids.values().iterator();
		while(it.hasNext())
		{
			Profile p = it.next();
			if(p.getOid() != currentProfileOid &&
					p.getRequiredLevel() <= level &&
					p.getOwningAgency().equals(anAgency))
			{
				profileDTOs.add(getProfileDTO(p));
			}
		}
		return profileDTOs;
	}

	public ProfileDTO getDTO(Long oid) {
		return getProfileDTO(pOids.get(oid));
	}

	public ProfileDTO getLockedDTO(Long origOid, Integer version) {
		String key = origOid+"-"+version;

		Profile p = pOrigOids.get(key);
		if(p != null)
		{
			return getProfileDTO(p);
		}
		
		return null;
	}

	public List<ProfileDTO> getDTOs(boolean showInactive) {
		List<ProfileDTO> profileDTOs = new ArrayList<ProfileDTO>();
		Iterator<Profile> it = pOids.values().iterator();
		while(it.hasNext())
		{
			Profile p = it.next();
			if(p.getStatus() == Profile.STATUS_ACTIVE ||
					showInactive == true)
			{
				profileDTOs.add(getProfileDTO(p));
			}
		}
		return profileDTOs;
	}

	public List<ProfileDTO> getAgencyDTOs(Agency agency, boolean showInactive) {
		List<ProfileDTO> profileDTOs = new ArrayList<ProfileDTO>();
		Iterator<Profile> it = pOids.values().iterator();
		while(it.hasNext())
		{
			Profile p = it.next();
			if(p.getOwningAgency().equals(agency) && (p.getStatus() == Profile.STATUS_ACTIVE ||
					showInactive == true))
			{
				profileDTOs.add(getProfileDTO(p));
			}
		}
		return profileDTOs;
	}

	public Profile getDefaultProfile(Agency anAgency) {
		Iterator<Profile> it = pOids.values().iterator();
		while(it.hasNext())
		{
			Profile p = it.next();
			if(p.getStatus() == Profile.STATUS_ACTIVE &&
					p.isDefaultProfile() &&
					p.getOwningAgency().equals(anAgency))
			{
				return p;
			}
		}
		
		return null;
	}

	public Profile load(Long oid) 
	{
		return pOids.get(oid);
	}

	public void saveOrUpdate(Profile profile) 
	{
		log.debug("saveOrUpdate "+profile.getName());

		if(profile.getOid() == null)
		{
			profile.setOid(new Long(13000L + pOids.size()));
		}

		if(pOids.containsKey(profile.getOid()))
		{
			Profile p = pOids.get(profile.getOid());
			p.setDefaultProfile(profile.isDefaultProfile());
			p.setDescription(profile.getDescription());
			p.setName(profile.getName());
			p.setOwningAgency(profile.getOwningAgency());
			p.setProfile(profile.getProfile());
			p.setRequiredLevel(profile.getRequiredLevel());
			p.setStatus(profile.getStatus());
			p.setVersion(profile.getVersion());
			p.setOrigOid(profile.getOrigOid());
			p.setHarvesterType(profile.getHarvesterType());
		}
		else
		{
			pOids.put(profile.getOid(), profile);
		}
		
		if(profile.isLocked())
		{
			String key = profile.getOrigOid()+"-"+profile.getVersion();
			if(!pOrigOids.containsKey(key))
			{
				pOrigOids.put(key, profile);
			}
		}
		
	}

	public void setProfileAsDefault(Profile profile) 
	{
		if(profile.getStatus() == Profile.STATUS_INACTIVE)
		{
			throw new WCTInvalidStateRuntimeException("Profile " + profile.getOid() + " is inactive and cannot be set to ge the default profile.");
		}
		
		Agency agency = profile.getOwningAgency();
		Iterator<Profile> it = pOids.values().iterator();
		while(it.hasNext())
		{
			Profile p = it.next();
			if(p.getOwningAgency().equals(agency))
			{
				p.setDefaultProfile(false);
			}
		}
		
		profile.setDefaultProfile(true);
		saveOrUpdate(profile);
	}

	public void delete(Object anObject) 
	{
		log.debug("delete "+anObject.toString());
		if(anObject instanceof Profile)
		{
			Profile p = (Profile)anObject;
			
			pOids.remove(p.getOid());
		}
	}

	public void deleteAll(Collection collection) 
	{
		Iterator it = collection.iterator();
		while(it.hasNext())
		{
			delete(it.next());
		}
	}

	public void evict(Object anObject) 
	{
		log.debug("evict "+anObject.toString());
	}

	public void initialize(Object anObject) 
	{
		log.debug("initialize"+anObject.toString());
	}

	private ProfileDTO getProfileDTO(Profile profile)
	{
		return new ProfileDTO(profile.getOid(),
				profile.getName(),
				profile.getDescription(),
				profile.getStatus(),
			    profile.getRequiredLevel(),
			    profile.getOwningAgency(),
			    profile.getHarvesterType(),
			    profile.isDefaultProfile());
	}
	
    private Set<Profile> loadProfilesFromNodeList(NodeList pNodes)
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
    
    private Profile loadProfileFromNode(Node pNode)
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
					else if(child.getNodeName().equals("origOid"))
					{
						p.setOrigOid(getLong(child));
					}
					else if (child.getNodeName().equals("harvesterType"))
					{
						p.setHarvesterType(getString(child));
					}
				}
			}
			
			pOids.put(oid, p);
			if(p.isLocked())
			{
				String key = p.getOrigOid()+"-"+p.getVersion();
				pOrigOids.put(key, p);
			}
    	}
     	
    	return pOids.get(oid);
    }
    
    
    private String getString(Node child)
    {
    	return child.getTextContent();
    }
    
    private Long getLong(Node child)
    {
    	return new Long(getString(child));
    }
    
    private Integer getInteger(Node child)
    {
    	return new Integer(getString(child));
    }
    
    private boolean getBool(Node child)
    {
    	return (getString(child).equals("true"));
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
	
}
	
