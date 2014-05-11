package org.webcurator.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.*;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.domain.model.core.Annotatable;
import org.webcurator.domain.model.core.Annotation;

public class MockAnnotationDAO implements AnnotationDAO {

	private static Log log = LogFactory.getLog(MockAnnotationDAO.class);
	private Document theFile = null; 
    private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private static Map<Long,Annotation> aOids = new HashMap<Long, Annotation>();
	private static Map<String,List<Annotation>> oaOids = new HashMap<String, List<Annotation>>();
	
	private MockUserRoleDAO userRoleDAO = null;
	
	public MockAnnotationDAO(String filename)
	{
		try
		{
			userRoleDAO = new MockUserRoleDAO(filename);
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        theFile = docBuilder.parse (new File(filename));
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
	
	public void deleteAnnotations(List<Annotation> annotations) {
		// TODO Auto-generated method stub

	}

	public List<Annotation> loadAnnotations(String type, Long oid) {
		return oaOids.get(oid.toString());
	}

	public void saveAnnotations(List<Annotation> annotations) {
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

    
    protected List<Annotation> loadAnnotationsFromNodeList(Long ownerOid, Annotatable obj, NodeList aNodes)
    {
    	List<Annotation> annotations = new ArrayList<Annotation>();
    	for (int i = 0; i < aNodes.getLength(); i++)
    	{
    		Node aNode = aNodes.item(i);
    		if(aNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			annotations.add(loadAnnotationFromNode(ownerOid, obj, aNode));
    		}
    	}
    	
    	return annotations;
    }
    
    protected Annotation loadAnnotationFromNode(Long ownerOid, Annotatable obj, Node aNode)
    {
       	//Check the oid first
    	Long oid = getOid(aNode);
    	if(oid != null && !aOids.containsKey(oid))
    	{
    		Annotation a = new Annotation();
    		a.setOid(oid);
    		a.setObjectType(obj.getClass().getName());
   			a.setObjectOid(obj.getOid());
    		
	 		NodeList children = aNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE)
				{
					if(child.getNodeName().equals("date"))
					{
						a.setDate(getDate(child));
					}
					else if(child.getNodeName().equals("note"))
					{
						a.setNote(getString(child));
					}
					else if(child.getNodeName().equals("user"))
					{
						a.setUser(userRoleDAO.getUserByOid(getOid(child)));
					}
				}
			}
			
			aOids.put(oid, a);
			
			if(oaOids.containsKey(ownerOid.toString()))
			{
				List<Annotation> list = oaOids.get(ownerOid);
				list.add(a);
			}
			else
			{
				List<Annotation> list = new ArrayList<Annotation>();
				list.add(a);
				oaOids.put(ownerOid.toString(), list);
			}
    	}
     	
    	return aOids.get(oid);
    }

	
    private String getString(Node child)
    {
    	return child.getTextContent();
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
