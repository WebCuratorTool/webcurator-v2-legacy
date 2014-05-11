/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.core.profiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.archive.crawler.admin.CrawlJobHandler;
import org.archive.crawler.settings.ComplexType;
import org.archive.crawler.settings.CrawlSettingsSAXHandler;
import org.archive.crawler.settings.CrawlSettingsSAXSource;
import org.archive.crawler.settings.CrawlerSettings;
import org.archive.crawler.settings.ListType;
import org.archive.crawler.settings.MapType;
import org.archive.crawler.settings.SettingsHandler;
import org.archive.crawler.settings.SimpleType;
import org.archive.crawler.settings.XMLSettingsHandler;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * The <code>HeritrixProfile</code> class wraps the Heritrix XMLSettingsHandler
 * and CrawlerSettings object to allow the WCT a degree of separation from the 
 * Heritrix implementation.
 * 
 * Despite this degree of separation, there is still a close relationship 
 * between Heritrix and the WCT.
 * 
 * @author bbeaumont
 *
 */
public class HeritrixProfile {
	/** The logger for this class */
	private Log log = LogFactory.getLog(HeritrixProfile.class);
	
	/** The Heritrix XMLSettingsHandler */
	private XMLSettingsHandler settingsHandler = null;
	/** The domain these settings apply to */
	private String domain = null;
	/** The Heritrix CrawlerSettings obbject */
	private CrawlerSettings crawlerSettings = null;
	
	/**
	 * Construct a new HeritrixProfile object.
	 * @param aSettingsHandler The Heritrix SettingsHandler.
	 * @param aDomain          The domain the settings apply to.
	 */
	public HeritrixProfile(XMLSettingsHandler aSettingsHandler, String aDomain) {
		settingsHandler = aSettingsHandler;
		domain = aDomain;
		crawlerSettings = settingsHandler.getSettingsObject(domain);
	}
	
	public boolean elementExists(String anAbsoluteName)
	{
		try {
			settingsHandler.getComplexTypeByAbsoluteName(crawlerSettings, anAbsoluteName);
			return true;
		}
		catch(AttributeNotFoundException ex) {
			return false;
		}
	}
	
	/**
	 * Get an element based on its absolute name.
	 * @param anAbsoluteName The absolute name of the element to retrieve.
	 * @return The element.
	 * @throws AttributeNotFoundException if the element cannot be found.
	 */
	public ProfileElement getElement(String anAbsoluteName) throws AttributeNotFoundException {
		try {
			return new ComplexProfileElement(settingsHandler.getComplexTypeByAbsoluteName(crawlerSettings, anAbsoluteName));
		}
		catch(AttributeNotFoundException ex) {
			// Perhaps it is not a complex element.
			String parentName = anAbsoluteName.substring(0, anAbsoluteName.lastIndexOf('/'));
			String myName = anAbsoluteName.substring(anAbsoluteName.lastIndexOf('/') + 1);
			ComplexType parent = settingsHandler.getComplexTypeByAbsoluteName(crawlerSettings, parentName);
			return new SimpleProfileElement(parent, myName);
		}
	}
	
	/**
	 * Move a map element up.
	 * @param complexElementName The name of the map.
	 * @param simpleElementName  The name of the element to move up.
	 * @return true if successful.
	 */
	public boolean moveMapElementUp(String complexElementName, String simpleElementName) {
		MapType mt;
		try {
			mt = (MapType) getElement(complexElementName).getValue();
			return mt.moveElementUp(crawlerSettings, simpleElementName);
		} 
		catch (AttributeNotFoundException e) {
			throw new WCTRuntimeException(e);
		}			
	}
	
	/**
	 * Move a map element down.
	 * @param complexElementName The name of the map.
	 * @param simpleElementName  The name of the element to move down.
	 * @return true if successful.
	 */
	public boolean moveMapElementDown(String complexElementName, String simpleElementName) {
		MapType mt;
		try {
			mt = (MapType) getElement(complexElementName).getValue();
			return mt.moveElementDown(crawlerSettings, simpleElementName);
		} 
		catch (AttributeNotFoundException e) {
			log.error("Could not find map with name " + complexElementName, e);
			return false;
		}			
	}
	
	/**
	 * Remove an element from a map.
	 * @param complexElementName The name of the map.
	 * @param simpleElementName  The name of the element to remove.
	 */
	public void removeMapElement(String complexElementName, String simpleElementName) {
		MapType mt;
		try {
			mt = (MapType) getElement(complexElementName).getValue();
			mt.removeElement(crawlerSettings, simpleElementName);
		} 
		catch (AttributeNotFoundException e) {
			log.error("Could not find map with name " + complexElementName, e);
			throw new WCTRuntimeException("Could not find map with name " + complexElementName, e);
		}			
	}		
	
	/**
	 * Clear all elements out of a map.
	 * @param complexElementName The name of the map to clear.
	 */
	public void clearMap(String complexElementName) {
		try {
			ComplexProfileElement ct = (ComplexProfileElement) getElement(complexElementName);
			List<ProfileElement> childList = ct.getChildren(true);
			Iterator<ProfileElement> children = childList.iterator();
			while(children.hasNext()) {
				removeMapElement(complexElementName, children.next().getName());
			}
		}
		catch(AttributeNotFoundException ex) {
			log.error("Could not find map with name " + complexElementName, ex);
		}		
	}
	
	/**
	 * Set the number of toe thread to run.
	 * @param toeThreadCount The number of toe threads to run.
	 */
	public void setToeThreads(int toeThreadCount) {
		try {
			setSimpleType("/crawl-order/max-toe-threads", toeThreadCount);
		}
		catch(AttributeNotFoundException ex) {
			throw new WCTRuntimeException("Attribute max-toe-threads not found in profile", ex);
		}
	}
	
	/**
	 * Remove items from a map if they are of the given type. This is 
	 * used to remove overridden filters from the Heritrix profile before
	 * applying the overrides. 
	 * 
	 * @param complexElementName The name of the map.
	 * @param type				 The classname of the type of object to remove.
	 */
	public void removeFromMapByType(String complexElementName, String type) {
		MapType mt = null;
		try {
			ComplexProfileElement ct = (ComplexProfileElement) getElement(complexElementName);
			mt = (MapType) ct.getValue();
			List<ProfileElement> childList = ct.getChildren(true);
			Iterator<ProfileElement> children = childList.iterator();
			while(children.hasNext()) {
				ProfileElement elem = children.next();
				if(elem.getValue().getClass().getName().equals(type)) {
					mt.removeElement(crawlerSettings, elem.getName());
				}
			}
		}
		catch(AttributeNotFoundException ex) {
			//TODO Should this be thrown or ignored?
			log.error("Could not find map with name " + complexElementName);
		}
		
	}
	
	/**
	 * Add a simple element to a map.
	 * @param complexElementName The name of the map.
	 * @param key				 The key of the simple simple element.
	 * @param value				 The value of the simple element.
	 * @throws InvalidAttributeValueException if the map doesn't accept simple elements.
	 * @throws DuplicateNameException if the key is a duplicate of an existing key.
	 */
	public void addSimpleMapElement(String complexElementName, String key, String value) throws InvalidAttributeValueException, DuplicateNameException { 
		SimpleType child = new SimpleType(key, "", value);
		addMapElement(complexElementName, child);
	}
	
	/**
	 * Adds a simple element to a map. 
	 * @param complexElementName The name of the map.
	 * @param child				 The child element to add.
	 * @throws InvalidAttributeValueException if the map doesn't accept simple elements.
	 * @throws DuplicateNameException if the key is a duplicate of an existing key.
	 */
	public void addMapElement(String complexElementName, SimpleType child) throws InvalidAttributeValueException, DuplicateNameException {
		try {
			ComplexProfileElement elem = (ComplexProfileElement) getElement(complexElementName);
			MapType map = (MapType) elem.getValue();	
			map.addElement(crawlerSettings, child);
		}
		catch(AttributeNotFoundException ex) {
			throw new WCTRuntimeException("Element " + complexElementName + " not found in profile");
		}
		catch(IllegalArgumentException ex) {
			if(ex.getMessage().startsWith("Duplicate field:")) {
				log.warn(ex);
				throw new DuplicateNameException(ex, ex.getMessage().substring("Duplicate field: ".length()));
			}
			else {
				throw new WCTRuntimeException(ex);
			}
		}		
		
	}
	
	
	/**
	 * Add an element to the map.
	 * @param complexElementName The name of the map.
	 * @param newElemName		 The name of the new element.
	 * @param newElemType	     The type of the new element.
	 * @throws DuplicateNameException
	 */
	public void addMapElement(String complexElementName, String newElemName, String newElemType) throws DuplicateNameException {
		try {
			ComplexProfileElement elem = (ComplexProfileElement) getElement(complexElementName);
			MapType map = (MapType) elem.getValue();
			map.addElement(crawlerSettings, SettingsHandler.instantiateModuleTypeFromClassName(newElemName,newElemType));
		} 
		catch (AttributeNotFoundException e) {
			throw new WCTRuntimeException(e);
		} 
		catch (InvalidAttributeValueException e) {
			throw new WCTRuntimeException(e);
		} 
		catch (InvocationTargetException e) {
			throw new WCTRuntimeException(e);
		}
		catch(IllegalArgumentException ex) {
			if(ex.getMessage().startsWith("Duplicate field:")) {
				log.warn(ex,ex);
				throw new DuplicateNameException(ex, ex.getMessage().substring("Duplicate field: ".length()));
			}
			else {
				throw new WCTRuntimeException(ex);				
			}
		}
		
	}
	
	
	/**
	 * Get the valid options for a given type. This is used for Filters etc.
	 * where the Profile Editor will provide a list of acceptable classnames.
	 * @param type The class to get the options from.
	 * @return A list of legal options.
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getOptionsForType(Class type) {
        String typeName = type.getName();
        String simpleName = typeName.substring(typeName.lastIndexOf(".")+1);
        String optionsFilename = simpleName+".options";
        try {
            return CrawlJobHandler.loadOptions(optionsFilename);
        } catch (IOException e) {
            return new ArrayList<String>();
        }
    }
	
	/**
	 * Given the attributes in the request, attempt to set all the simple 
	 * types, including lists (which aren't technically simple, but are close
	 * enough). 
	 * 
	 * The element passed in serves as the root of the recursion. This method
	 * will set all the simple children of the element, and then recurse to
	 * set all the simple children of this element's children (and so on).
	 * 
	 * @param request The HttpServletRequest to get the attributes from.
	 * @param element The complex element from which to start setting the values.
	 */
    public void setAllSimpleTypes(HttpServletRequest request, ComplexProfileElement element) {
    	// Set all the simple child elements.
    	Iterator simpleChildren = element.getSimpleChildren().iterator();
    	while(simpleChildren.hasNext()) {
    		setAllSimpleTypes(request, (SimpleProfileElement) simpleChildren.next());
    	}
    	
    	// Recurse to all the complex elements.
    	Iterator complexChildren = element.getComplexChildren().iterator();
    	while(complexChildren.hasNext()) {
    		setAllSimpleTypes(request, (ComplexProfileElement) complexChildren.next());
    	}    	
    }    
    
    /**
     * Set the values in a list.
     * @param elementName The name of the list.
     * @param values      The values to set.
     * @throws AttributeNotFoundException if the list cannot be found.
     */
    public void setListType(String elementName, List values) throws AttributeNotFoundException {
    	ListType list = (ListType) getElement(elementName).getValue();
    	setListType(list, values);
    }
    
    /**
     * Set the values in a list.
     * @param aListType The list to set the values on.
     * @param values    The values to set.
     */
    public void setListType(ListType aListType, List values) {
    	// Clear the list.
    	aListType.clear();
    	
    	// Add all the elements.
    	Iterator it = values.iterator();
    	while(it.hasNext()) {
    		aListType.add(it.next());
    	}
    }
    
    /**
     * Set the simple type. 
     * @param request The HttpServletRequest object.
     * @param element The element to set.
     */
    public void setAllSimpleTypes(HttpServletRequest request, SimpleProfileElement element) {
    	// If the element name happens to be in the request, then we need to
    	// set its value.
    	if(request.getParameter(element.getAbsoluteName()) != null) {

    		// If the complex type is a list, then lets populate (even though it's not 
	    	// really simple, it's close enough).
	    	if(element.getValue() instanceof ListType) {
	    		ListType list = (ListType) element.getValue();
	    		list.clear();
	    		StringTokenizer tokenizer = new StringTokenizer(request.getParameter(element.getAbsoluteName()), "\n\r");
	    		while(tokenizer.hasMoreTokens()) {
	    			list.add(tokenizer.nextToken());
	    		}
	    	}
	    	
	    	// Otherwise we just need to set the value.
	    	else {
    			element.setValue(request.getParameter(element.getAbsoluteName()));
	    	}
    	}
    }
    
    /**
     * Direct method for setting a specific simple element.
     * @param elementPath The name of the element.
     * @param value		  The value to set.
     * @throws AttributeNotFoundException if the element cannot be found.
     */
    public void setSimpleType(String elementPath, Object value) throws AttributeNotFoundException {
    	ProfileElement elem = getElement(elementPath);
    	if( elem instanceof SimpleProfileElement) {
    		((SimpleProfileElement)elem).setValue(value);
    	}
    }
    
    /**
     * Set a scope for the Heritrix profile.
     * @param scopeClass The class of the scope.
     */
    public void setScopeClass(String scopeClass) {
    	try {
			settingsHandler.getOrder().setAttribute(SettingsHandler.instantiateModuleTypeFromClassName("scope", scopeClass));
		} catch (Exception e) {
			throw new WCTRuntimeException(e);
		}
    }
    
    /**
     * Convert the HeritrixProfile object to its XML string.
     * @return the XML string that represents the Heritrix profile.
     */
    public String toString() {
    	try {
	    	StringWriter stringWriter = new StringWriter(4096);
	        StreamResult result = new StreamResult(stringWriter);
	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        Source source = new CrawlSettingsSAXSource(crawlerSettings);
	        transformer.transform(source, result);
	        return stringWriter.toString();
    	}
    	catch(TransformerException ex) {
    		ex.printStackTrace();
    		return "[ERROR CONVERTING PROFILE TO STRING]";
    	}
    }
    
    /**
     * Create a base profile. This profile is created from the 
     * <code>default-profile.xml</code> file in the root of the classpath. To
     * customise the base profile, edit this file and restart the web 
     * application.
     * @return A new base profile.
     */
    public static HeritrixProfile create() {
    	BufferedReader profileReader = null;
    	try {
    		String line = null;
    		StringBuffer buffer = new StringBuffer();
    		profileReader = new BufferedReader(new InputStreamReader(HeritrixProfile.class.getResourceAsStream("/default-profile.xml")));
    		
    		while( (line=profileReader.readLine()) != null) {
    			buffer.append(line);
    			buffer.append("\n");
    		}
    		
    		return HeritrixProfile.fromString(buffer.toString());
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
			throw new WCTRuntimeException(ex);
    	}
    	finally {
    		try { profileReader.close(); } catch(Exception ex) {}
    	}
    }
    
    /**
     * Construct a profile from a file.
     * @return A new base profile.
     */
    public static HeritrixProfile create(File f) {
    	BufferedReader profileReader = null;
    	try {
    		String line = null;
    		StringBuffer buffer = new StringBuffer();
    		profileReader = new BufferedReader(new FileReader(f));
    		
    		while( (line=profileReader.readLine()) != null) {
    			buffer.append(line);
    			buffer.append("\n");
    		}
    		
    		return HeritrixProfile.fromString(buffer.toString());
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
			throw new WCTRuntimeException(ex);
    	}
    	finally {
    		try { profileReader.close(); } catch(Exception ex) {}
    	}
    }    
    
    /**
     * Reconstruct a profile from an XML string.
     * @param str The XML string to create the profile from.
     * @return The object representation of the profile string.
     */
    public static HeritrixProfile fromString(String str) {
    	try {
		    XMLReader parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
		    StringReader reader = new StringReader(str);
		    
		    // Create a settings handler. The file is a dummy file simply to allow
		    // us to construct the object.
		    XMLSettingsHandler settingsHandler = new XMLSettingsHandler(new File("dummy_file"));
		    
			parser.setContentHandler(new CrawlSettingsSAXHandler(settingsHandler.getSettings(null)));
			InputSource source = new InputSource(reader);
			parser.parse(source);
			
			HeritrixProfile profile = new HeritrixProfile(settingsHandler, null);
			return profile;
    	}
    	catch(SAXException ex) {
			throw new WCTRuntimeException(ex);
    	}
		catch(ParserConfigurationException pcex) {
			throw new WCTRuntimeException(pcex);
		} 
		catch (IOException e) {
			throw new WCTRuntimeException(e);
		} 
		catch (InvalidAttributeValueException e) {
			throw new WCTRuntimeException(e);
		}
    }
}
