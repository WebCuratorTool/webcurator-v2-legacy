package org.webcurator.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.io.*;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Role;
import org.webcurator.domain.model.auth.RolePrivilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Annotatable;
import org.webcurator.domain.model.core.Indicator;
import org.webcurator.domain.model.core.IndicatorCriteria;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.UserDTO;

public class MockIndicatorCriteriaDAO implements IndicatorCriteriaDAO {

	private static Log log = LogFactory.getLog(MockIndicatorCriteriaDAO.class);
	private Document theFile = null; 
    private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private static Map<Long, IndicatorCriteria> icOids = new HashMap<Long, IndicatorCriteria>();
	private Map<Long, Agency> agencyOids = new HashMap<Long,Agency>();
	private Map<Long,Role> roleOids = new HashMap<Long, Role>();
	private Map<Long,Set<User>> roleUserOids = new HashMap<Long, Set<User>>();
	private Map<Long,User> userOids = new HashMap<Long, User>();
	private Map<String,User> userNames = new HashMap<String, User>();
	private MockUserRoleDAO userRoleDAO = null;
	private Map<Long,UserDTO> userDtoOids = new HashMap<Long, UserDTO>();
	
	public MockIndicatorCriteriaDAO(String filename)
	{
		try
		{
			userRoleDAO = new MockUserRoleDAO(filename);
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        theFile = docBuilder.parse (new File(filename));
	        
	    	NodeList indicatorCriteriaNodes = theFile.getElementsByTagName("indicator-criteria");
	    	
	    	//force a nested load of everything
	        loadIndicatorCriteriasFromNodeList(indicatorCriteriaNodes);
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
	
	public void deleteIndicatorCriterias() {
		icOids.clear();
	}

	public void saveIndicators(List<IndicatorCriteria> indicators) {
		// TODO Auto-generated method stub

	}

	public <T extends java.lang.annotation.Annotation> T getAnnotation(
			Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public java.lang.annotation.Annotation[] getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	public java.lang.annotation.Annotation[] getDeclaredAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAnnotationPresent(
			Class<? extends java.lang.annotation.Annotation> arg0) {
		// TODO Auto-generated method stub
		return false;
	}


    
    protected List<IndicatorCriteria> loadIndicatorCriteriasFromNodeList(NodeList aNodes)
    {
    	List<IndicatorCriteria> indicators = new ArrayList<IndicatorCriteria>();
    	for (int i = 0; i < aNodes.getLength(); i++)
    	{
    		Node aNode = aNodes.item(i);
    		if(aNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			indicators.add(loadIndicatorCriteriaFromNode(aNode));
    		}
    	}
    	
    	return indicators;
    }
    
    protected IndicatorCriteria loadIndicatorCriteriaFromNode(Node aNode)
    {
       	//Check the oid first
    	Long oid = getOid(aNode);
    	if(oid != null && !icOids.containsKey(oid))
    	{
    		IndicatorCriteria a = new IndicatorCriteria();
    		a.setOid(oid);
    		
	 		NodeList children = aNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("name"))
					{
						a.setName(getString(child));
					}
					else if(child.getNodeName().equals("upper-limit"))
					{
						a.setUpperLimit(getFloat(child));
					}
					else if(child.getNodeName().equals("lower-limit"))
					{
						a.setLowerLimit(getFloat(child));
					}
					else if(child.getNodeName().equals("upper-limit-percentage"))
					{
						a.setUpperLimitPercentage(getFloat(child));
					}
					else if(child.getNodeName().equals("lower-limit-percentage"))
					{
						a.setLowerLimitPercentage(getFloat(child));
					}
					else if(child.getNodeName().equals("agency"))
					{
				    	//force a nested load of everything
				        a.setAgency(loadAgencyFromNode(child));
					}
					else if(child.getNodeName().equals("unit"))
					{
						a.setUnit(getString(child));
					}

				}
			}
			
			icOids.put(oid, a);
			//log.info("IndicatorCriteria instance " + a.getOid() + " has been addded " + a);
    	}
     	
    	return icOids.get(oid);
    }

	
    private String getString(Node child)
    {
    	if (child.getTextContent() == null || child.getTextContent().equals("")) {
    		return null;
    	} else
    		return child.getTextContent();
    }
    
    private Float getFloat(Node child)
    {
    	return new Float(getString(child));
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
	public void saveOrUpdate(Object aObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Object aObject) {
		
		if (aObject instanceof Agency) {
			Agency agency = (Agency)aObject;
			List<IndicatorCriteria> criterias = getIndicatorCriteriasByAgencyOid(agency.getOid());
			for (IndicatorCriteria criteria : criterias) {
				delete(criteria);
			}
		}
		
		if (aObject instanceof IndicatorCriteria) {
			IndicatorCriteria criteria = (IndicatorCriteria)aObject;
			icOids.remove(criteria.getOid());
			//log.info("IndicatorCriteria instance " + criteria.getOid() + " has been deleted");
		}
		
		if (aObject == null) {
			icOids.clear();
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
    	if(oid != null && !agencyOids.containsKey(oid))
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

	@Override
	public IndicatorCriteria getIndicatorCriteriaByOid(Long oid) {
		return icOids.get(oid);
	}

	@Override
	public List<IndicatorCriteria> getIndicatorCriterias() {
		return new ArrayList<IndicatorCriteria>(icOids.values());
	}

	@Override
	public List<IndicatorCriteria> getIndicatorCriteriasByAgencyOid(Long agencyOid) {

		ArrayList<IndicatorCriteria> criterias = new ArrayList<IndicatorCriteria>(icOids.values());
		ArrayList<IndicatorCriteria> agenciesIcs = new ArrayList<IndicatorCriteria>();
		for (IndicatorCriteria criteria : criterias) {
			if (criteria.getAgency().getOid().equals(agencyOid)) {
				agenciesIcs.add(criteria);
			}
		}
		return agenciesIcs;
	}
   
}
