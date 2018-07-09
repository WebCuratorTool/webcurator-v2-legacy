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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;

/**
 * Parameter representing a Texfield input
 * 
 * @author MDubos
 *
 */
public class StringParameter implements Parameter {

	protected String name;
	protected String text;
	protected String description;
	protected Boolean optional;
	
	private Log log = LogFactory.getLog(StringParameter.class);
	
	protected String selectedText; 
	
	/**
	 * Default constructor
	 */
	public StringParameter(){
	}
	
	/**
	 * Constructor with all properties
	 * @param name
	 * @param value
	 * @param description
	 * @param optional
	 */
	public StringParameter(String name, String value, String description, Boolean optional){
		this.name = name;
		this.text = value;
		this.description = description;
		this.optional = optional;
	}
	
	/**
	 * <code>text INPUT</code> rendering 
	 */
	public String getInputRendering() throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append("<input type=\"text\" name=\"parameters\" >");
		if(!optional){
			sb.append("<font color=red size=2>&nbsp;<strong>*</strong>&nbsp;</font>");
		}		
		if(optional){
			sb.append("<i><font size=\"1\">&nbsp;(Optional)</font></i>");
		}		
		return sb.toString();
	}

	/**
	 * Set the value 
	 */
	public void setValue(Object text) {
		this.text = (String)text;
	}

	/**
	 * Display the parameter properties
	 */
	public String toString(){
		return getName() + "|" + getValue() + "|" + getDescription() + "|" + getOptional();
	}
	
	/** Validation of the parameter */
	public void validate(Errors errors,  
			String name, Object value, String description, Boolean optional){
		
		// null/blank 
		log.debug("validate StringParameter: value=[" + value + "]");
		if( !optional.booleanValue() && (value == null || ((String)value).equals("")) ){
			errors.reject("required", 
					new Object[]{"The " + description}, 
					"A mandatory date is missing");
		}
		
	}
	
	/**
	 * @param value Value to set
	 */
	public void setSelectedValue(Object value){
		selectedText = (String) value;
	}
	
	/**
	 * @return Returns the selected value
	 */
	public Object getSelectedValue(){
		return selectedText;
	}
	/**
	 * @return Returns the selected value as a String
	 */
	public String getDisplayableSelectedValue(){
		return (selectedText == null ? "" : selectedText);
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the optional.
	 */
	public Boolean getOptional() {
		return optional;
	}

	/**
	 * @param optional The optional to set.
	 */
	public void setOptional(Boolean optional) {
		this.optional = optional;
	}
	
	/**
	 * @return Returns the type
	 */
	public String getType() {
		return this.getClass().getName();
	}


	/**
	 * @return Returns the value 
	 */
	public String getValue() {
		return text;
	}
}
