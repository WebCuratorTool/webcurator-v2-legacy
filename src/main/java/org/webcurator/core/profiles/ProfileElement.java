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

/**
 * Abstract class to represent an element in the Heritrix profile.
 * @author bbeaumont
 */
public abstract class ProfileElement {
	
	/**
	 * Get the value of this element.
	 * @return The value of this element.
	 */
	public abstract Object getValue();
	
	/**
	 * Get the name of the element.
	 * @return The name of the element.
	 */	
	public abstract String getName();

	/**
	 * Get the absolute x-path name of the element (where the element is in the
	 * Heritrix XML file.
	 * @return the absolute name of the element.
	 */
	public abstract String getAbsoluteName();
	
	
	/**
	 * Get the description of the element.
	 * @return The description of the element.
	 */	
	public abstract String getDescription();
	
	/**
	 * Get the classname of the type that is accepted as the value for
	 * this element.
	 * @return The classname of the type that is accepted as the value for
	 * 		   this element.
	 */
	public abstract String getType();
	
	

}