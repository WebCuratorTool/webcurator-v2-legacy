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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.management.MBeanAttributeInfo;

import org.archive.crawler.settings.ComplexType;
import org.archive.crawler.settings.ModuleAttributeInfo;

/**
 * The <code>ComplexProfileElement</code> wraps the Heritrix 
 * elements that contain child elements.  * 
 * 
 * @author bbeaumont
 */
public class ComplexProfileElement extends ProfileElement {
	/** The Heritrix ComplexType object */
	private ComplexType element = null;
	
	/**
	 * Construct a ComplexProfileElement from the Heritrix element.
	 * @param anElement The Heritrix element to create the object from.
	 */
	public ComplexProfileElement(ComplexType anElement) {
		element = anElement;
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.profiles.ProfileElement#getName()
	 */
	@Override
	public String getName() {
		return element.getName();
	}


	/* (non-Javadoc)
	 * @see org.webcurator.core.profiles.ProfileElement#getAbsoluteName()
	 */
	@Override
	public String getAbsoluteName() {
		return element.getAbsoluteName();
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.profiles.ProfileElement#getDescription()
	 */
	@Override
	public String getDescription() {
		return element.getDescription();
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.profiles.ProfileElement#getType()
	 */
	@Override
	public String getType() {
		return element.getLegalValueType().toString();
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.profiles.ProfileElement#getValue()
	 */
	@Override
	public Object getValue() {
		return element.getValue();
	}

	/**
	 * Indicates if this is a complex element.
	 * @return always returns true.
	 */
	public boolean isComplex() {
		return true; 
	}
	
	/**
	 * Get all the simple children of the element.
	 * @return A List of all the simple children.
	 */
	public List<ProfileElement> getSimpleChildren() {
		return getChildren(false);
	}
	
	/**
	 * Get all the complex children of this element.
	 * @return A List of the complex child elements.
	 */
	public List<ProfileElement> getComplexChildren() {
		return getChildren(true);
	}
	
	/**
	 * Get the simple or complex children of the element.
	 * @param getComplex true to retrieve the complex children; false to get
	 * 					 the simple children.
	 * @return A List of child elements.
	 */
	@SuppressWarnings("unchecked")
	public List<ProfileElement> getChildren(boolean getComplex) {
		if(!isComplex()) {
			return Collections.EMPTY_LIST;
		}
		else {
			MBeanAttributeInfo info[] = ((ComplexType) getValue()).getMBeanInfo().getAttributes();
			List<ProfileElement> children = new LinkedList<ProfileElement>();
			for(int i=0;i<info.length;i++) {
				ModuleAttributeInfo modAttrInfo = (ModuleAttributeInfo) info[i];
				
				// Collect the children based on the getComplex flag.
				if(getComplex && modAttrInfo.isComplexType() || !getComplex && !modAttrInfo.isComplexType()) {
					ComplexType ctype = (ComplexType) getValue();
					
					if(modAttrInfo.isComplexType()) {
						try {
							ComplexType child = (ComplexType) ctype.getAttribute(modAttrInfo.getName());
							children.add(new ComplexProfileElement(child));
						} catch (Exception e) {
							// TODO Handle this.
						}
					}
					else {
						children.add(new SimpleProfileElement(ctype, info[i].getName()));
					}
				}
			}
			return children;
		}
	}				

}