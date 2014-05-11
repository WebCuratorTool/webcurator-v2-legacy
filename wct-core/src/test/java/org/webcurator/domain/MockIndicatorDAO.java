package org.webcurator.domain;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.webcurator.domain.model.core.Indicator;
import org.webcurator.domain.model.core.TargetInstance;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MockIndicatorDAO implements IndicatorDAO {

	private static Log log = LogFactory.getLog(MockIndicatorDAO.class);
	private Document theFile = null; 
    private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private static Map<Long,Indicator> iOids = new HashMap<Long, Indicator>();
	
	private MockUserRoleDAO userRoleDAO = null;
	
	public MockIndicatorDAO(String filename)
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
	
	public void deleteIndicators(List<Indicator> indicators) {

	}
	
	public void deleteIndicators() {
		iOids.clear();
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

    protected List<Indicator> loadIndicatorsFromNodeList(TargetInstance targetInstance, NodeList aNodes)
    {
    	List<Indicator> indicators = new ArrayList<Indicator>();
    	for (int i = 0; i < aNodes.getLength(); i++)
    	{
    		Node aNode = aNodes.item(i);
    		if(aNode.getNodeType() == Node.ELEMENT_NODE)
    		{
    			indicators.add(loadIndicatorFromNode(targetInstance, aNode));
    		}
    	}
    	
    	return indicators;
    }
    
    protected Indicator loadIndicatorFromNode(TargetInstance targetInstance, Node aNode)
    {
       	//Check the oid first
    	Long oid = getOid(aNode);
    	if(oid != null && !iOids.containsKey(oid))
    	{
    		Indicator a = new Indicator();
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
					else if(child.getNodeName().equals("float-value"))
					{
						a.setFloatValue(getFloat(child));
					}
					else if(child.getNodeName().equals("unit"))
					{
						a.setUnit(getString(child));
					}
				}
			}
			
			// set the ti
			a.setTargetInstanceOid(targetInstance.getOid());
			
			iOids.put(oid, a);
			//log.info("Indicator instance " + a.getOid() + " has been addded");
			
			
    	}
     	
    	return iOids.get(oid);
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
    	if (getString(child) != null) {
    		return new Float(getString(child));
    	} else 
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
	public void saveOrUpdate(Object aObject) {
		if (aObject instanceof Indicator) {
			Indicator indicator = (Indicator)aObject;
			if (indicator.getOid() == null) {
				indicator.setOid(new Long(iOids.size()));
			} 
			iOids.put(indicator.getOid(), indicator);
		}
		
	}

	@Override
	public void delete(Object aObject) {
		
		if (aObject instanceof TargetInstance) {
			TargetInstance ti = (TargetInstance)aObject;
			List<Indicator> indicators = getIndicatorsByTargetInstanceOid(ti.getOid());
			for (Indicator indicator : indicators) {
				iOids.remove(indicator.getOid());
			}
		}
		
		if (aObject instanceof Indicator) {
			Indicator indicator = (Indicator)aObject;
			iOids.remove(indicator.getOid());
			//log.info("Indicator instance " + indicator.getOid() + " has been deleted");
		}
		
		if (aObject == null) {
			iOids.clear();
		}
		
	}

	@Override
	public Indicator getIndicatorByOid(Long oid) {
		return iOids.get(oid);
	}

	@Override
	public List<Indicator> getIndicatorsByTargetInstanceOid(
			Long targetInstanceOid) {
		ArrayList<Indicator> indicators = new ArrayList<Indicator>(iOids.values());
		ArrayList<Indicator> tiIndicators = new ArrayList<Indicator>();
		for (Indicator indicator : indicators) {
			//TODO: replace the method indicator.getTargetInstance()
			// with indicator.getTargetInstanceOid()
			if (indicator.getTargetInstanceOid().equals(targetInstanceOid)) {
				tiIndicators.add(indicator);
			}
		}
		return tiIndicators;
	}

}
