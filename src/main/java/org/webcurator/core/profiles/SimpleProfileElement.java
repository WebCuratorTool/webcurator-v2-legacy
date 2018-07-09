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

import javax.management.Attribute;

import org.archive.crawler.settings.ComplexType;
import org.archive.crawler.settings.ModuleAttributeInfo;

/**
 * A simple element in a Heritrix profile.
 * @author bbeaumont
 */
public class SimpleProfileElement extends ProfileElement {
	/** The parent element */
	private ComplexType parent;
	/** The name of the element */
	private String name;
	/** The absolute name of the element */
	private String absoluteName;
	
	/**
	 * Create a new SimpleProfileElement.
	 * @param aParent The parent element.
	 * @param aName   The name of the element.
	 */
	public SimpleProfileElement(ComplexType aParent, String aName) {
		parent = aParent;
		name = aName;
		absoluteName = aParent.getAbsoluteName() + "/" + aName;
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.profiles.ProfileElement#getValue()
	 */
	public Object getValue() {
		try {
			return parent.getAttribute(name);
		}
		catch(Exception ex) {
			// TODO Handle this better
			return null;
		}
	}
	
	/**
	 * Set the value of the object.
	 * @param o The value to set.
	 */
	public void setValue(Object o) {
		try {
			parent.setAttribute(new Attribute(name, o));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.profiles.ProfileElement#getName()
	 */
	public String getName() {
		return parent.getAttributeInfo(name).getName();
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.profiles.ProfileElement#getDescription()
	 */
	public String getDescription() {
		return parent.getAttributeInfo(name).getDescription();
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.profiles.ProfileElement#getType()
	 */
	public String getType() {
		return parent.getAttributeInfo(name).getType();
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.profiles.ProfileElement#getAbsoluteName()
	 */
	public String getAbsoluteName() {
		return absoluteName;
	}
	
	/**
	 * Return true if there is a set of legal values for this element. 
	 * @return true if there is a legal set of options; otherwise false.
	 */
	public boolean hasOptions() {
		Object o[] = getLegalValues();
		return o != null && o.length > 0;
	}
	
	/**
	 * Look for the legal values that this element can take.
	 * @return An array of legal values.
	 */
	public Object[] getLegalValues() {
		return ((ModuleAttributeInfo) parent.getAttributeInfo(name)).getLegalValues();
	}
	
	/**
	 * Return true if this element is marked as transient. 
	 * @return true if this element is marked as transient; otherwise false.
	 */
	public boolean isTransient() {
		return ((ModuleAttributeInfo) parent.getAttributeInfo(name)).isTransient();
	}
}