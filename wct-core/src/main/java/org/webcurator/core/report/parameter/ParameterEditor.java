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
package org.webcurator.core.report.parameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;

/**
 * Property editor for Parameter, 
 * 
 * @author MDubos
 *
 */
public class ParameterEditor extends CustomCollectionEditor { 
	
	private Log log = LogFactory.getLog(ParameterEditor.class);
	
	/**
	 * Simple constructor refering to CustomCollectionEditor
	 * @see CustomCollectionEditor#CustomCollectionEditor(java.lang.Class)
	 */
	public ParameterEditor(Class arg0, boolean arg1) {
		super(arg0, arg1);
	}

	/**
	 * Simple constructor refering to CustomCollectionEditor
	 * @see CustomCollectionEditor#CustomCollectionEditor(java.lang.Class, boolean)
	 */
	public ParameterEditor(Class arg0) {
		super(arg0);
	}

	/** (non-Javadoc)
	 * @see org.springframework.beans.propertyeditors.CustomCollectionEditor#convertElement(java.lang.Object)
	 * @ Override
	 */	
	protected Object convertElement(Object arg0) {
		try {
			
			log.debug("class=" + arg0.getClass().getName());
			
			String val = (String)arg0;
			
			return val;
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException(" must be a String");
		}
	}

	
}
