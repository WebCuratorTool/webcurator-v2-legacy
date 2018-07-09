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
import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Site;
import org.webcurator.domain.model.core.UrlPattern;

public class MockSiteDAO implements SiteDAO {

	private static Log log = LogFactory.getLog(MockSiteDAO.class);
	private Document theFile = null; 
    private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    private BusinessObjectFactory factory = new BusinessObjectFactory();
	private Map<Long,Permission> pOids = new HashMap<Long, Permission>();
	private Map<Long,Site> siteOids = new HashMap<Long, Site>();
	private Map<Long,AuthorisingAgent> aaOids = new HashMap<Long, AuthorisingAgent>();
	private Map<String,AuthorisingAgent> aaNames = new HashMap<String, AuthorisingAgent>();
	private Map<Long,UrlPattern> urlOids = new HashMap<Long, UrlPattern>();
	
	private MockUserRoleDAO userRoleDAO = null;
	private MockAnnotationDAO annotationDAO = null;

	public MockSiteDAO(String filename)
	{
		super();
		try
		{
			userRoleDAO = new MockUserRoleDAO(filename);
			annotationDAO = new MockAnnotationDAO(filename);
			
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        theFile = docBuilder.parse (new File(filename));

	    	NodeList siteNodes = theFile.getElementsByTagName("site");
	    	
	    	//force a nested load of everything
	        loadSitesFromNodeList(siteNodes);
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
	
	/**
	 * Save or update the specifed site to the persistent store.
	 * @param aSite the site to save or update.
	 */
	public void saveOrUpdate(Site aSite) {
		log.debug("saveOrUpdate "+aSite.getTitle());
	}
	
	/**
	 * Search for any Site's that match the specified criteria.
	 * @param criteria the search criteria
	 * @param page the page number to return
	 * @return the page of search results
	 */
	public Pagination search(final SiteCriteria criteria, final int page, final int pageSize) {
		return new Pagination(siteOids.values(),page,pageSize);
	}
	
	/**
	 * Load the specified site from the persistent store.
	 * @param siteOid the id of the site to load
	 * @return the requested site
	 */
	public Site load(final long siteOid) 
	{
		return siteOids.get(siteOid);
	}
	
	/**
	 * Load the specified site from the persistent store and also
	 * load all its related Objects if the flag is set. 
	 * @param siteOid the id of the site to load
	 * @param fullyInitialise true if the relationships should be loaded
	 * @return the requested site
	 */
	public Site load(final long siteOid, boolean fullyInitialise) 
	{
		return load(siteOid);
	}
	
	/**
	 * Load the specified permission from the persistent store.
	 * @param permOid the id of the requested permission
	 * @return the requested permission object.
	 */	
	public Permission loadPermission(final long permOid) 
	{
		return pOids.get(permOid);
	}
	
	/**
	 * List all the site objects that match the specified title.
	 * @param aTitle the title of the sites to retrieve
	 * @return the list of sites
	 */
	public List<Site> listSitesByTitle(final String aTitle) 
	{
		List<Site> sites = new ArrayList<Site>();
		Iterator<Site> it = siteOids.values().iterator();
		while(it.hasNext())
		{
			Site site = it.next();
			if(aTitle.equals(site.getTitle()))
			{
				sites.add(site);
			}
		}
		
		return sites;
	}
	
	/**
	 * Return the list of permissions that are marked as quick picks 
	 * for the specified agency.
	 * @param anAgency the agency to return the quick pick list for
	 * @return the list of permission quick picks
	 */
	public List<Permission> getQuickPickPermissions(Agency anAgency) 
	{
		List<Permission> perms = new ArrayList<Permission>();
		Iterator<Permission> it = pOids.values().iterator();
		while(it.hasNext())
		{
			Permission perm = it.next();
			if(anAgency.equals(perm.getOwningAgency()) &&
					perm.isQuickPick())
			{
				perms.add(perm);
			}
		}
		
		return perms;
	}
	
	/**
	 * Count the number of sites in the persistent store.
	 * @return the number of sites
	 */
	public int countSites() {
		return siteOids.size();
	}
	
	/**
	 * Find permissions by Site
	 * @param anAgencyOid The OID of the agency to restrict the search to.
	 * @param aSiteTitle The name of the site.
	 * @param aPageNumber The page number to return.
	 * @return A List of Permissions.
	 */
	public Pagination findPermissionsBySiteTitle(Long anAgencyOid, String aSiteTitle, int aPageNumber) {
		return new Pagination(pOids.values(),aPageNumber,20);
	}
	
	/**
	 * Get a count of the number of seeds related to a given permission.
	 * @param aPermissionOid The permission oid
	 * @return The number of seeds linked to the permission
	 */
	public int countLinkedSeeds(Long aPermissionOid) {
		Permission p = pOids.get(aPermissionOid);
		//TODO: use MockTargetDAO to link back to seeds
		return 0;
	}
	
	/**
	 * Search for existing Authorising Agencies by name.
	 * @param name The name of the agency to search for. 
	 * @param page The page number.
	 * @return A pagination of results.
	 */
	public Pagination searchAuthAgents(final String name, final int page) {
		return new Pagination(aaOids.values(),page,20);
	}
	
	/**
	 * Load an authorising agent from the database.
	 * @param authAgentOid The OID of the authorising agent to load.
	 * @return The authorising agent.
	 */
	public AuthorisingAgent loadAuthorisingAgent(final long authAgentOid) {
		return aaOids.get(authAgentOid);
	}

	/**
	 * Check that the Authorising Agent name is unique.
	 * @param oid  The OID of the authorising agent, if available.
	 * @param name The name of the authorising agent.
	 * @return True if unique; otherwise false.
	 */
    public boolean isAuthAgencyNameUnique(Long oid, String name) 
    {
    	if(oid == null)
    	{
    		return aaNames.containsKey(name);
    	}
    	else
    	{
    		return !(aaOids.get(oid).equals(aaNames.get(name)));
    	}
    }
	
    private List<Site> loadSitesFromNodeList(NodeList siteNodes)
    {
    	List<Site> sites = new ArrayList<Site>();
    	for (int i = 0; i < siteNodes.getLength(); i++)
    	{
    		Node siteNode = siteNodes.item(i);
    		if(siteNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			sites.add(loadSiteFromNode(siteNode));
    		}
    	}
    	
    	return sites;
    }
    
     private Site loadSiteFromNode(Node siteNode)
    {
    	//Check the oid first
    	Long oid = getOid(siteNode);
    	if(oid != null && siteNode.hasChildNodes() && !siteOids.containsKey(oid))
    	{
    		Site site = new Site();
    		site.setOid(oid);
    		
	 		NodeList children = siteNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("title"))
					{
						site.setTitle(getString(child));
					}
					else if(child.getNodeName().equals("description"))
					{
						site.setDescription(getString(child));
					}
					else if(child.getNodeName().equals("owningAgency"))
					{
						site.setOwningAgency(userRoleDAO.getAgencyByOid(getOid(child)));
					}
					else if(child.getNodeName().equals("notes"))
					{
						site.setNotes(getString(child));
					}
					else if(child.getNodeName().equals("libraryOrderNo"))
					{
						site.setLibraryOrderNo(getString(child));
					}
					else if(child.getNodeName().equals("published"))
					{
						site.setPublished(getBool(child));
					}
					else if(child.getNodeName().equals("active"))
					{
						site.setActive(getBool(child));
					}
					else if(child.getNodeName().equals("authorisingAgents"))
					{
						site.setAuthorisingAgents(loadAuthorisingAgentsFromNodeList(child.getChildNodes()));
					}
					else if(child.getNodeName().equals("urlPatterns"))
					{
						site.setUrlPatterns(loadUrlPatternsFromNodeList(site, child.getChildNodes()));
					}
					else if(child.getNodeName().equals("permissions"))
					{
						Set<Permission> permissions = loadPermissionsFromNodeList(site, child.getChildNodes());
						site.setPermissions(permissions);
					}
					else if(child.getNodeName().equals("annotations"))
					{
						site.setAnnotations(annotationDAO.loadAnnotationsFromNodeList(oid, site, child.getChildNodes()));
					}
				}
			}
			
			siteOids.put(oid, site);
    	}
    	
    	return siteOids.get(oid);
    }

    private Set<AuthorisingAgent> loadAuthorisingAgentsFromNodeList(NodeList aaNodes)
    {
    	Set<AuthorisingAgent> authorisingAgents = new HashSet<AuthorisingAgent>();
    	for (int i = 0; i < aaNodes.getLength(); i++)
    	{
    		Node aaNode = aaNodes.item(i);
    		if(aaNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			authorisingAgents.add(loadAuthorisingAgentFromNode(aaNode));
    		}
    	}
    	
    	return authorisingAgents;
    }
    
    private AuthorisingAgent loadAuthorisingAgentFromNode(Node aaNode)
    {
       	//Check the oid first
    	Long oid = getOid(aaNode);
    	if(oid != null && aaNode.hasChildNodes() && !aaOids.containsKey(oid))
    	{
    		AuthorisingAgent aa = factory.newAuthorisingAgent();
    		aa.setOid(oid);
    		
	 		NodeList children = aaNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("name"))
					{
						aa.setName(getString(child));
					}
					else if(child.getNodeName().equals("description"))
					{
						aa.setDescription(getString(child));
					}
					else if(child.getNodeName().equals("contact"))
					{
						aa.setContact(getString(child));
					}
					else if(child.getNodeName().equals("phoneNumber"))
					{
						aa.setPhoneNumber(getString(child));
					}
					else if(child.getNodeName().equals("email"))
					{
						aa.setEmail(getString(child));
					}
					else if(child.getNodeName().equals("address"))
					{
						aa.setAddress(getString(child));
					}
					else if(child.getNodeName().equals("annotations"))
					{
						aa.setAnnotations(annotationDAO.loadAnnotationsFromNodeList(oid, aa, child.getChildNodes()));
					}
				}
			}
			
			aaOids.put(oid, aa);
			aaNames.put(aa.getName(), aa);
    	}
     	
    	return aaOids.get(oid);
    }
    
    private Set<UrlPattern> loadUrlPatternsFromNodeList(Site site, NodeList urlNodes)
    {
    	Set<UrlPattern> urlPatterns = new HashSet<UrlPattern>();
    	for (int i = 0; i < urlNodes.getLength(); i++)
    	{
    		Node urlNode = urlNodes.item(i);
    		if(urlNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			urlPatterns.add(loadUrlPatternFromNode(site, urlNode));
    		}
    	}
    	
    	return urlPatterns;
    }
    
    private UrlPattern loadUrlPatternFromNode(Site site, Node urlNode)
    {
       	//Check the oid first
    	Long oid = getOid(urlNode);
    	if(oid != null && urlNode.hasChildNodes() && !urlOids.containsKey(oid))
    	{
    		UrlPattern url = factory.newUrlPattern(site);
    		url.setOid(oid);
    		
	 		NodeList children = urlNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("pattern"))
					{
						url.setPattern(getString(child));
					}
					else if(child.getNodeName().equals("permissions"))
					{
						Set<Permission> permissions = new HashSet<Permission>();
						NodeList permissionNodes = child.getChildNodes();
						for(int j = 0; j < permissionNodes.getLength(); j++)
						{
							Node permissionNode = permissionNodes.item(j);
							if(permissionNode.getNodeType() == Node.ELEMENT_NODE)
							{
								permissions.add(pOids.get(getOid(permissionNode)));
							}
						}
						
						url.setPermissions(permissions);
					}
				}
			}
			
			urlOids.put(oid, url);
    	}
     	
    	return urlOids.get(oid);
    }
    
    private Set<Permission> loadPermissionsFromNodeList(Site site, NodeList pNodes)
    {
    	Set<Permission> permissions = new HashSet<Permission>();
    	for (int i = 0; i < pNodes.getLength(); i++)
    	{
    		Node pNode = pNodes.item(i);
    		if(pNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			permissions.add(loadPermissionFromNode(site, pNode));
    		}
    	}
    	
     	return permissions;
    }
    
    private Permission loadPermissionFromNode(Site site, Node pNode)
    {
       	//Check the oid first
    	Long oid = getOid(pNode);
    	if(oid != null && pNode.hasChildNodes() && !pOids.containsKey(oid))
    	{
    		Permission p = factory.newPermission(site);
    		p.setOid(oid);
    		
	 		NodeList children = pNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("authorisingAgent"))
					{
						p.setAuthorisingAgent(aaOids.get(getOid(child)));
					}
					else if(child.getNodeName().equals("urlPatterns"))
					{
						Set<UrlPattern> urlPatterns = new HashSet<UrlPattern>();
						NodeList urlPatternNodes = child.getChildNodes();
						for(i = 0; i < urlPatternNodes.getLength(); i++)
						{
							Node urlPatternNode = urlPatternNodes.item(i);
							if(urlPatternNode.getNodeType() == Node.ELEMENT_NODE)
							{
								urlPatterns.add(urlOids.get(getOid(urlPatternNode)));
							}
						}
						p.setUrls(urlPatterns);
					}
					if(child.getNodeName().equals("startDate"))
					{
						p.setStartDate(getDate(child));
					}
					else if(child.getNodeName().equals("endDate"))
					{
						p.setEndDate(getDate(child));
					}
					else if(child.getNodeName().equals("approved"))
					{
						p.setApproved(getBool(child));
					}
					else if(child.getNodeName().equals("status"))
					{
						p.setStatus(getInteger(child));
					}
					else if(child.getNodeName().equals("authResponse"))
					{
						p.setAuthResponse(getString(child));
					}
					else if(child.getNodeName().equals("accessStatus"))
					{
						p.setAccessStatus(getString(child));
					}
					else if(child.getNodeName().equals("openAccessDate"))
					{
						p.setOpenAccessDate(getDate(child));
					}
					else if(child.getNodeName().equals("availableFlag"))
					{
						p.setAvailableFlag(getBool(child));
					}
					else if(child.getNodeName().equals("specialRequirements"))
					{
						p.setSpecialRequirements(getString(child));
					}
					else if(child.getNodeName().equals("creationDate"))
					{
						p.setCreationDate(getDate(child));
					}
					else if(child.getNodeName().equals("copyrightUrl"))
					{
						p.setCopyrightUrl(getString(child));
					}
					else if(child.getNodeName().equals("copyrightStatement"))
					{
						p.setCopyrightStatement(getString(child));
					}
					else if(child.getNodeName().equals("permissionSentDate"))
					{
						p.setPermissionSentDate(getDate(child));
					}
					else if(child.getNodeName().equals("permissionGrantedDate"))
					{
						p.setPermissionGrantedDate(getDate(child));
					}
					else if(child.getNodeName().equals("quickPick"))
					{
						p.setQuickPick(getBool(child));
					}
					else if(child.getNodeName().equals("displayName"))
					{
						p.setDisplayName(getString(child));
					}
					if(child.getNodeName().equals("owningAgency"))
					{
						p.setOwningAgency(userRoleDAO.getAgencyByOid(getOid(child)));
					}
					else if(child.getNodeName().equals("fileReference"))
					{
						p.setFileReference(getString(child));
					}
					else if(child.getNodeName().equals("exclusions"))
					{
						//TODO: handle exclusions
					}
					else if(child.getNodeName().equals("annotations"))
					{
						p.setAnnotations(annotationDAO.loadAnnotationsFromNodeList(oid, p, child.getChildNodes()));
					}
				}
			}
			
			pOids.put(oid, p);
    	}
     	
    	return pOids.get(oid);
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
	
}
