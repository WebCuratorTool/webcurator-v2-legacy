package org.webcurator.domain;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.domain.model.auth.*;
import org.webcurator.domain.model.core.RejReason;
import org.webcurator.domain.model.dto.UserDTO;

//XML file imports
import java.io.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 



public class MockRejReasonDAO implements RejReasonDAO {

	private static Log log = LogFactory.getLog(MockUserRoleDAO.class);
	private Document theFile = null; 
	private Long currentUserOid = null;
	private Map<Long,Role> roleOids = new HashMap<Long, Role>();
	private Map<Long,Set<User>> roleUserOids = new HashMap<Long, Set<User>>();
	private Map<Long,User> userOids = new HashMap<Long, User>();
	private Map<String,User> userNames = new HashMap<String, User>();
	private Map<Long, Agency> agencyOids = new HashMap<Long,Agency>();
	private Map<Long,UserDTO> userDtoOids = new HashMap<Long, UserDTO>();
	private Map<Long,RejReason> rejReasonOids = new HashMap<Long, RejReason>();
    private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

	public MockRejReasonDAO(String filename) 
	{
		try
		{
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        theFile = docBuilder.parse (new File(filename));

	    	NodeList agencyNodes = theFile.getElementsByTagName("agency");
	    	//force a nested load of everything
	        loadAgenciesFromNodeList(agencyNodes);
	    	
	    	NodeList reasonNodes = theFile.getElementsByTagName("rejreason");
	    	//force a nested load of everything
	        loadRejReasonsFromNodeList(reasonNodes);
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
    	if(aObject instanceof RejReason)
    	{
    		RejReason rejReason = (RejReason)aObject;
    		if(rejReasonOids.containsKey(rejReason.getOid()))
    		{
    			rejReasonOids.remove(rejReason.getOid());
    		}
    		
    		rejReasonOids.put(rejReason.getOid(), rejReason);
    	}
    }
    
    /**
     * Remove the specified object from the persistent data store.
     * @param aObject the object to remove
     */
    public void delete(Object aObject)
    {
    	log.debug("delete object: "+aObject.toString());
    	if(aObject instanceof RejReason)
    	{
    		RejReason rejReason = (RejReason)aObject;
    		rejReasonOids.remove(rejReason.getOid());
    	}
    }
    
    /**
     * gets the specific reason based on the provided oid
     * @param oid the Rejection Reason's Oid 
     * @return the populated RejReason object
     */
    public RejReason getRejReasonByOid(Long oid) {
    	return rejReasonOids.get(oid);
    }
    
    /**
     * gets all reasons in the system
     * @return a List of fully populated RejReason objects
     */
    public List getRejReasons() {
    	List rejReasons = new ArrayList();
    	Iterator<RejReason> it = rejReasonOids.values().iterator();
    	while(it.hasNext())
    	{
        	RejReason rejReason = it.next();
        	rejReasons.add(rejReason);
    	}
    	return rejReasons;
    }
    
    /**
     * gets the reasons for a selected Agency
     * @param agencyOid the Oid of the Agency in which to search for reasons
     * @return a List of fully populated RejReason objects
     */
    public List getRejReasons(Long agencyOid) {
    	List rejReasons = new ArrayList();
    	Iterator<RejReason> it = rejReasonOids.values().iterator();
    	while(it.hasNext())
    	{
        	RejReason rejReason = it.next();
        	if (rejReason.getAgency().getOid()==agencyOid) {
            	rejReasons.add(rejReason);
        	}
    	}
    	return rejReasons;
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
    
    private Set<RejReason> loadRejReasonsFromNodeList(NodeList reasonNodes)
    {
    	Set<RejReason> rejReasons = new HashSet<RejReason>();
    	for (int i = 0; i < reasonNodes.getLength(); i++)
    	{
    		Node reasonNode = reasonNodes.item(i);
    		if(reasonNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			rejReasons.add(loadRejReasonFromNode(reasonNode));
    		}
    	}
    	
    	return rejReasons;
    }
    
    private RejReason loadRejReasonFromNode(Node reasonNode)
    {
    	//Check the oid first
    	Node idNode = reasonNode.getAttributes().getNamedItem("id");
    	Long oid = new Long(idNode.getNodeValue());
    	if(oid != null && reasonNode.hasChildNodes() && !rejReasonOids.containsKey(oid))
    	{
    		RejReason rejReason = new RejReason();
    		rejReason.setOid(oid);
    		
    		Node rolesNode = null;
    		Node usersNode = null;
    		
	 		NodeList children = reasonNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("name"))
					{
						rejReason.setName(child.getTextContent());
					}
					else if(child.getNodeName().equals("availableForTargets"))
					{
						rejReason.setAvailableForTargets(child.getTextContent().equals("true")?true:false);
					}
					else if(child.getNodeName().equals("availableForTIs"))
					{
						rejReason.setAvailableForTIs(child.getTextContent().equals("true")?true:false);
					}
					else if(child.getNodeName().equals("agencyId"))
					{
						rejReason.setAgency(agencyOids.get(new Long(child.getTextContent())));
					}
				}
			}
			
			rejReasonOids.put(oid, rejReason);
    	}
    	
    	return rejReasonOids.get(oid);
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
