package org.webcurator.domain;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.domain.model.auth.*;
import org.webcurator.domain.model.dto.UserDTO;

//XML file imports
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 



public class MockUserRoleDAO implements UserRoleDAO {

	private static Log log = LogFactory.getLog(MockUserRoleDAO.class);
	private Document theFile = null; 
	private Long currentUserOid = null;
	private Map<Long,Role> roleOids = new HashMap<Long, Role>();
	private Map<Long,Set<User>> roleUserOids = new HashMap<Long, Set<User>>();
	private Map<Long,User> userOids = new HashMap<Long, User>();
	private Map<String,User> userNames = new HashMap<String, User>();
	private Map<Long, Agency> agencyOids = new HashMap<Long,Agency>();
	private Map<Long,UserDTO> userDtoOids = new HashMap<Long, UserDTO>();
    private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

	public MockUserRoleDAO(String filename) 
	{
		try
		{
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        theFile = docBuilder.parse (new File(filename));

	    	NodeList currentUserNodes = theFile.getElementsByTagName("current-user");
	    	Node currentUserNode = currentUserNodes.item(0);
	    	if(currentUserNode != null)
	    	{
		    	Node idNode = currentUserNode.getAttributes().getNamedItem("id");
		    	this.currentUserOid = new Long(idNode.getNodeValue());
	    	}
	        
	    	NodeList agencyNodes = theFile.getElementsByTagName("agency");
	    	//force a nested load of everything
	        loadAgenciesFromNodeList(agencyNodes);
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
	    	log.debug(e.getMessage ());
	    }
	}

	/**
	 * Save or update the specified object to the persistent data store.
	 * @param aObject the object to save or update
	 */
    public void saveOrUpdate(Object aObject)
    {
    	log.debug("saveOrUpdate object: "+aObject.toString());
    	if(aObject instanceof User)
    	{
    		User user = (User)aObject;
    		if(userOids.containsKey(user.getOid()))
    		{
    			userOids.remove(user.getOid());
    		}
    		
    		userOids.put(user.getOid(), user);
    	}
    	else if(aObject instanceof Agency)
    	{
    		Agency agency = (Agency)aObject;
    		if(agencyOids.containsKey(agency.getOid()))
    		{
        		agencyOids.remove(agency.getOid());
    		}
    		
    		agencyOids.put(agency.getOid(), agency);
    	}
    	else if(aObject instanceof Role)
    	{
    		Role role = (Role)aObject;
    		if(roleOids.containsKey(role.getOid()))
    		{
        		roleOids.remove(role.getOid());
    		}
    		
    		roleOids.put(role.getOid(), role);
    	}
    }
    
    /**
     * Remove the specified object from the persistent data store.
     * @param aObject the object to remove
     */
    public void delete(Object aObject)
    {
    	log.debug("delete object: "+aObject.toString());
    	if(aObject instanceof User)
    	{
    		User user = (User)aObject;
    		userOids.remove(user.getOid());
    	}
    	else if(aObject instanceof Agency)
    	{
    		Agency agency = (Agency)aObject;
    		agencyOids.remove(agency.getOid());
    	}
    	else if(aObject instanceof Role)
    	{
    		Role role = (Role)aObject;
    		roleOids.remove(role.getOid());
    	}
    }
    
    /**
     * gets a List of RolePrivilege objects. The RolePrivilege contains both 
     * the Privilege and the scope of the Privilege
     * @param username the username
     * @return a List of RolePrivileges
     */
    public List getUserPrivileges(String username)
    {
    	User user = userNames.get(username);
    	Set roles = user.getRoles();
    	
    	List privs = new ArrayList();
    	Iterator it = roles.iterator();
     	while(it.hasNext())
    	{
    		Role role = (Role)it.next();
    		Set roleprivs = role.getRolePrivileges();
    		Iterator itr = roleprivs.iterator();
    	   	while(itr.hasNext())
        	{
    	   		RolePrivilege rp = (RolePrivilege)itr.next();
    	   		privs.add(rp);
        	}
    	}
    	return privs;
    }
    
    /**
     * gets a specific User based on the users login name
     * @param username the login username
     * @return a populated User object
     */
    public User getUserByName(String username)
    {
    	return userNames.get(username);
    }
    
    /**
     * gets the specific user based on the provided oid
     * @param oid the Users Oid 
     * @return the populated User object
     */
    public User getUserByOid(Long oid)
    {
    	return userOids.get(oid);
    }
    
    /**
     * gets the Defined roles in the system
     * @return a List of Roles
     */
    public List getRoles()
    {
    	List r = new ArrayList();
    	Iterator<Role> it = roleOids.values().iterator();
    	while(it.hasNext())
    	{
    		Role role = it.next(); 
    		r.add(role);
    	}
    	return r;
    }
    
    /**
     * gets a Defined list of roles for the specified agency
     * @param agencyOid the oid of the agency
     * @return a List of Roles
     */
    public List getRoles(Long agencyOid)
    {
       	List r = new ArrayList();
    	Agency agency = agencyOids.get(agencyOid);
    	Set roleSet = agency.getRoles();
    	Iterator<Role> it = roleSet.iterator();
    	while(it.hasNext())
    	{
        	Role role = it.next();
    		r.add(role);
    	}
    	return r;
    }
    
    /**
     * gets the Roles associated with this user
     * @param  userOid the users primary key
     * @return a List of associated Roles for this user
     */
    public List getAssociatedRolesForUser(Long userOid)
    {
       	List r = new ArrayList();
    	User user = userOids.get(userOid);
    	Set roleSet = user.getRoles();
    	Iterator<Role> it = roleSet.iterator();
    	while(it.hasNext())
    	{
        	Role role = it.next();
    		r.add(role);
    	}
    	return r;
    }
    
    /**
     * gets all users of the WTC system
     * @return a List of full populated User objects
     */
    public List getUsers()
    {
    	List users = new ArrayList();
    	Iterator<User> it = userOids.values().iterator();
    	while(it.hasNext())
    	{
        	User user = it.next();
    		users.add(user);
    	}
    	return users;
    }
    
    /**
     * gets the users of the WTC system for a selected Agency
     * @param agencyOid the Oid of the AGency in which to search for users
     * @return a List of full populated User objects
     */
    public List getUsers(Long agencyOid)
    {
       	List users = new ArrayList();
    	Agency agency = agencyOids.get(agencyOid);
    	Set userSet = agency.getUsers();
    	Iterator<User> it = userSet.iterator();
    	while(it.hasNext())
    	{
        	User user = it.next();
    		users.add(user);
    	}
    	return users;
    }
    
    /**
     * gets a the WCT Users in the system, but only as Data Transfer Objects
     * @return a List of UserDTO objects
     */
    public List getUserDTOs()
    {
    	List userDTOs = new ArrayList();
       	Iterator<UserDTO> it = userDtoOids.values().iterator();
    	while(it.hasNext())
    	{
        	UserDTO userDto = it.next();
    		userDTOs.add(userDto);
    	}
     	return userDTOs;
    }
    
    /**
     * gets a the WCT Users for a given agency, but only as Data Transfer Objects
     * @param agencyOid a List of UserDTO objects
     * @return a List of UserDTO objects for the agency
     */
    public List getUserDTOs(Long agencyOid)
    {
    	List userDTOs = new ArrayList();
    	Agency agency = agencyOids.get(agencyOid);
       	Iterator<User> it = agency.getUsers().iterator();
     	while(it.hasNext())
    	{
           	User user = it.next();
     	   	UserDTO userDto = userDtoOids.get(user.getOid());
     	   	userDTOs.add(userDto);
    	}
     	return userDTOs;
    }
    
    /**
     * gets a UserDTO object based on the Users oid
     * @param userOid the oid to load
     * @return a populated UserDTO object
     */
    public UserDTO getUserDTOByOid(Long userOid)
    {
    	return userDtoOids.get(userOid);
    }
  
    /**
     * gets all the Agencies defined within the WCT system
     * @return a List of Agencies
     */
    public List getAgencies()
    {
    	List<Agency> a = new ArrayList();
    	Iterator<Agency> it = agencyOids.values().iterator();
    	while(it.hasNext())
    	{
    		a.add(it.next());
    	}
    	return a;
    }
    
    /**
     * gets an agency using its oid (primary key)
     * @param oid the Agency Oid
     * @return the populated Agency Object
     */
    public Agency getAgencyByOid(Long oid)
    {
    	return agencyOids.get(oid);
    }
    
    /**
     * gets a Role object based on its primary key
     * @param oid the roles primary key
     * @return the populated Role object
     */
    public Role getRoleByOid(Long oid)
    {
    	return roleOids.get(oid);
    }
    
    /**
     * gets a List of UserDTO's that have this privilege across
     * all Agencies
     * @param privilege the Privilge code to look for
     * @return  a List of UserDTO objects
     */
    public List<UserDTO> getUserDTOsByPrivilege(String privilege)
    {
    	List<UserDTO> userDTOs = new ArrayList<UserDTO>();
    	
    	Iterator<User> it = userOids.values().iterator();
     	while(it.hasNext())
    	{
        	User user = it.next();
     		boolean doNext = false;
     		Iterator<Role> itr = user.getRoles().iterator();
         	while(itr.hasNext() && !doNext)
        	{
         		Role role = itr.next();
         		Iterator<RolePrivilege> itrp = role.getRolePrivileges().iterator();
             	while(itrp.hasNext() && !doNext)
            	{
             		RolePrivilege rp = itrp.next();
             		if(rp.getPrivilege().equals(privilege))
             		{
                	   	UserDTO userDto = userDtoOids.get(user.getOid());
                 	   	userDTOs.add(userDto);
                 	   	doNext = true;
            		}
            	}
        	}
     	}
    	return userDTOs;
    }
    
    /**
     * gets a List of UserDTO's that have this privilege within the 
     * specified Agency
     * @param privilege the Privilge code to look for
     * @param agencyOid the AGency to limit the search to
     * @return a List of UserDTO objects
     */
    public List<UserDTO> getUserDTOsByPrivilege(String privilege, Long agencyOid)
    {
    	List<UserDTO> userDTOs = new ArrayList<UserDTO>();
    	
    	Iterator<User> it = agencyOids.get(agencyOid).getUsers().iterator();
     	while(it.hasNext())
    	{
        	User user = it.next();
     		boolean doNext = false;
     		Iterator<Role> itr = user.getRoles().iterator();
         	while(itr.hasNext() && !doNext)
        	{
         		Role role = itr.next();
         		Iterator<RolePrivilege> itrp = role.getRolePrivileges().iterator();
             	while(itrp.hasNext() && !doNext)
            	{
             		RolePrivilege rp = itrp.next();
             		if(rp.getPrivilege().equals(privilege))
             		{
                	   	UserDTO userDto = userDtoOids.get(user.getOid());
                 	   	userDTOs.add(userDto);
                 	   	doNext = true;
            		}
            	}
        	}
     	}
    	return userDTOs;
    }
    
    /**
     * Get all the User DTOs who own targets associated with a given permission.
     * @param permissionOid The OID of the permission.
     * @return A list of UserDTOs.
     */
    public List<UserDTO> getUserDTOsByTargetPrivilege(Long permissionOid)
    {
    	List<UserDTO> userDTOs = new ArrayList<UserDTO>();
    	return userDTOs;
    }
    
    public User getCurrentUser()
    {
    	if(this.currentUserOid != null)
    	{
    		return userOids.get(this.currentUserOid);
    	}
    	else
    	{
    		return null;
    	}
    }
    
    private Set<Agency> loadAgenciesFromNodeList(NodeList agencyNodes)
    {
    	Set<Agency> agencies = new HashSet<Agency>();
    	for (int i = 0; i < agencyNodes.getLength(); i++)
    	{
    		Node agencyNode = agencyNodes.item(i);
    		if(agencyNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			agencies.add(loadAgencyFromNode(agencyNode));
    		}
    	}
    	
    	return agencies;
    }
    
    private Agency loadAgencyFromNode(Node agencyNode)
    {
    	//Check the oid first
    	Node idNode = agencyNode.getAttributes().getNamedItem("id");
    	Long oid = new Long(idNode.getNodeValue());
    	if(oid != null && agencyNode.hasChildNodes() && !agencyOids.containsKey(oid))
    	{
    		Agency agency = new Agency();
    		agency.setOid(oid);
    		
    		Node rolesNode = null;
    		Node usersNode = null;
    		
	 		NodeList children = agencyNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("name"))
					{
						agency.setName(child.getTextContent());
					}
					else if(child.getNodeName().equals("address"))
					{
						agency.setAddress(child.getTextContent());
					}
					else if(child.getNodeName().equals("phone"))
					{
						agency.setPhone(child.getTextContent());
					}
					if(child.getNodeName().equals("agencyURL"))
					{
						agency.setAgencyURL(child.getTextContent());
					}
					else if(child.getNodeName().equals("agencyLogoURL"))
					{
						agency.setAgencyLogoURL(child.getTextContent());
					}
					else if(child.getNodeName().equals("email"))
					{
						agency.setEmail(child.getTextContent());
					}
					else if(child.getNodeName().equals("fax"))
					{
						agency.setFax(child.getTextContent());
					}
					else if(child.getNodeName().equals("roles"))
					{
						rolesNode = child;
					}
					else if(child.getNodeName().equals("users"))
					{
						usersNode = child;
					}
				}
			}
			
			//ensure that roles are loaded before users as users will reference the roles
			if(rolesNode != null)
			{
				agency.setRoles(loadRolesFromNodeList(agency, rolesNode.getChildNodes()));
			}
			if(usersNode != null)
			{
				agency.setUsers(loadUsersFromNodeList(agency, usersNode.getChildNodes()));
			}
			
			//Now fix up the roles with their users
			Iterator<Role> itr = roleOids.values().iterator();
			while(itr.hasNext())
			{
				Role role = itr.next();
				Set<User> users = roleUserOids.get(role.getOid());
				if(users != null)
				{
					role.setUsers(users);
				}
			}
	
			
			agencyOids.put(oid, agency);
    	}
    	
    	return agencyOids.get(oid);
    }

    private Set<Role> loadRolesFromNodeList(Agency agency, NodeList roleNodes)
    {
    	Set<Role> roles = new HashSet<Role>();
    	for (int i = 0; i < roleNodes.getLength(); i++)
    	{
    		Node roleNode = roleNodes.item(i);
    		if(roleNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			roles.add(loadRoleFromNode(agency, roleNode));
    		}
    	}
    	
    	return roles;
    }
    
    private Role loadRoleFromNode(Agency agency, Node roleNode)
    {
    	//Check the oid first
    	Node idNode = roleNode.getAttributes().getNamedItem("id");
    	Long oid = new Long(idNode.getNodeValue());
    	if(oid != null && roleNode.hasChildNodes() && !roleOids.containsKey(oid))
    	{
        	Role role = new Role();

        	role.setOid(oid);
        	role.setAgency(agency);
        	role.setUsers(new HashSet<User>());
	    	
			NodeList children = roleNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("name"))
					{
						role.setName(child.getTextContent());
					}
					else if(child.getNodeName().equals("description"))
					{
						role.setDescription(child.getTextContent());
					}
					else if(child.getNodeName().equals("role-privileges"))
					{
						role.setRolePrivileges(loadRolePrivilegesFromNodeList(role, child.getChildNodes()));
					}
				}
			}
			
			roleOids.put(oid, role);
    	}
    	
    	return roleOids.get(oid);
    }
    
    private Set<RolePrivilege> loadRolePrivilegesFromNodeList(Role role, NodeList rolePrivNodes)
    {
    	Set<RolePrivilege> rolePrivs = new HashSet<RolePrivilege>();
    	for (int i = 0; i < rolePrivNodes.getLength(); i++)
    	{
    		Node rolePrivNode = rolePrivNodes.item(i);
    		if(rolePrivNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			rolePrivs.add(loadRolePrivFromNode(role, rolePrivNode));
    		}
    	}
    	
    	return rolePrivs;
    }
    
    private RolePrivilege loadRolePrivFromNode(Role role, Node privNode)
    {
    	RolePrivilege rp = null;

    	Node idNode = privNode.getAttributes().getNamedItem("id");
    	Long oid = new Long(idNode.getNodeValue());
    	if(oid != null)
    	{
        	rp = new RolePrivilege();
	    	rp.setOid(oid);
	    	rp.setRole(role);
	    	
			NodeList children = privNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("scope"))
					{
						rp.setPrivilegeScope(new Integer(child.getTextContent()));
					}
					else if(child.getNodeName().equals("privilege"))
					{
						rp.setPrivilege(child.getTextContent());
					}
				}
			}
    	}

    	return rp;
    }
    
    private Set<User> loadUsersFromNodeList(Agency agency, NodeList userNodes)
    {
    	Set<User> users = new HashSet<User>();
    	for (int i = 0; i < userNodes.getLength(); i++)
    	{
    		Node userNode = userNodes.item(i);
    		if(userNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			users.add(loadUserFromNode(agency, userNode));
    		}
    	}
    	
    	return users;
    }
    
    private User loadUserFromNode(Agency agency, Node userNode)
    {
    	//Check the oid first
    	Node idNode = userNode.getAttributes().getNamedItem("id");
    	Long oid = new Long(idNode.getNodeValue());
    	if(oid != null && userNode.hasChildNodes() && !userOids.containsKey(oid))
    	{
			User user = new User();
			UserDTO userDto = new UserDTO();

			user.setAgency(agency);
			user.setOid(oid);
	    	Node usernameNode = userNode.getAttributes().getNamedItem("username");
			user.setUsername(usernameNode.getNodeValue());
			
			userDto.setAgencyName(agency.getName());
			userDto.setOid(oid);
			userDto.setUsername(usernameNode.getNodeValue());
	
			NodeList children = userNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("active"))
					{
						user.setActive((child.getTextContent().equals("true"))?true:false);
						userDto.setActive((child.getTextContent().equals("true"))?true:false);
					}
					else if(child.getNodeName().equals("email"))
					{
						user.setEmail(child.getTextContent());
						userDto.setEmail(child.getTextContent());
					}
					if(child.getNodeName().equals("externalAuth"))
					{
						user.setExternalAuth((child.getTextContent().equals("true"))?true:false);
					}
					else if(child.getNodeName().equals("firstname"))
					{
						user.setFirstname(child.getTextContent());
						userDto.setFirstname(child.getTextContent());
					}
					else if(child.getNodeName().equals("lastname"))
					{
						user.setLastname(child.getTextContent());
						userDto.setLastname(child.getTextContent());
					}
					else if(child.getNodeName().equals("roles"))
					{
						Set<Role> roles = new HashSet<Role>();
						NodeList roleNodes = child.getChildNodes();
						for(int r = 0; r < roleNodes.getLength(); r++)
						{
							Node roleNode = roleNodes.item(r);
							if(roleNode.getNodeName().equals("role"))
							{
								Node roleIdNode = roleNode.getAttributes().getNamedItem("id");
								Role role = roleOids.get(new Long(roleIdNode.getNodeValue()));
								
								if(roleUserOids.containsKey(role.getOid()))
								{
									Set<User> users = roleUserOids.get(role.getOid());
									users.add(user);
								}
								else
								{
									Set<User> users = new HashSet<User>();
									users.add(user);
									roleUserOids.put(role.getOid(), users);
								}
								
								roles.add(role);
							}
						}
						
						user.setRoles(roles);
					}
				}
			}
			
			userOids.put(oid, user);
			userNames.put(user.getUsername(), user);
			userDtoOids.put(oid, userDto);
    	}
    	
		return userOids.get(oid);
    }
}
