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

import java.io.IOException;
import org.springframework.validation.Errors;

/**
 * Generic parameter for a report.<br>
 * 
 * @author MDubos
 *
 */
public interface Parameter {

	public String getName();
	public Object getValue();
	public String getType();
	public String getDescription();
	public Boolean getOptional();
	
	public void setName(String name);
	public void setValue(Object value);
	public void setDescription(String description);
	public void setOptional(Boolean optional);
	
	public String getInputRendering() throws IOException;
	
	/** Convenient method for displaying all parameters **/
	public String toString();
	
	/** Validation of the parameter and write into 
	 * <code>errors</code> if errors occur */
	public void validate(Errors errors,
			String name, Object value, String description, Boolean optional);
	
	/** Set a value already entered */	
	public void setSelectedValue(Object value);
	/**  Return the valid value which has been entered. 
	 * Is <code>null</code> if there is no valid value.
	 * This method is to help getInputRendering method */
	public Object getSelectedValue();
	/**  Return the <code>getSelectedValue</code> as a 
	 * displayable <code>String</code> */
	public String getDisplayableSelectedValue();
	
}
