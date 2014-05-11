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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;


/**
 * Parameter for a list of elements.<br>
 * <br>
 * When rendered as a HTML input, it is displayed as a <code>SELECT</code> filled with element names.
 * <br><br> All <code>ListParameter</code> parameters, the option selected is then seen 
 * as a <code>StringParameter</code>. 
 * @see org.webcurator.core.report.parameter.ListParameter#getType()
 * 
 */
public class ListParameter implements Parameter {

	protected String name;
	protected List<String> options;
	protected String description;
	protected Boolean optional;

	protected String validOption;
	private Log log = LogFactory.getLog(ListParameter.class);
	
	
	/**
	 * Rendering as a <code>SELECT</code> 
	 */
	public String getInputRendering() throws IOException {
		StringBuffer sb = new StringBuffer();
		String preselected = getSelectedValue() != null ? (String)getSelectedValue() : "";
		
		sb.append("<select name=\"parameters\">\n");
		for(String option : options){
			sb.append("<option " + (option.equals(preselected) ? "SELECTED" : "") + ">" + option + " </option>");
			sb.append("\n");
		}
		sb.append("</select>");
		if(!optional){
			sb.append("<font color=red size=2>&nbsp;<strong>*</strong>&nbsp;</font>");
		}		
		if(optional){
			sb.append("<i><font size=\"1\">&nbsp;(Optional)</font></i>");
		}
		sb.append("\n");
		return sb.toString();

	}
	

	/**
	 * Usefull method to display all the properties, 
	 * including all options of the list (inside curly brackets}
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(getName() + "|");
		if(getValue() == null){
			sb.append("null");
		} else{
			for(String s : getValue()){
				sb.append("{" + s + "}");
			}
		}
		sb.append("|" + getDescription() + "|" + getOptional());
		return sb.toString();
	}
	
	
	/**
	 * @return The List of values as <code>String</Code>
	 */
	public List<String> getValue() {
		return options;
	}
	
	/**
	 * @param value The value to set 
	 */
	@SuppressWarnings("unchecked")
	public void setValue(Object value){
		this.options = (List<String>)value;
	}
	
	/**
	 * The option selected can be seen as a String.
	 * Therefore <code>getType()</code> returns a 
	 * <code>StringParameter</code>
	 */
	public String getType() {
		return StringParameter.class.getName();
	}
	
	
	/** Validation of the parameter */
	public void validate(Errors aErrors, 
			String name, Object value, String description, Boolean optional){
		log.debug("validate ListParameter:");
		
		// No validation necessary as long as there's is the option 'All' in first in the list
	}
	
	/**
	 * @param value the <code>String</code> value
	 */
	public void setSelectedValue(Object value){
		validOption = (String) value;
	}
	
	/**
	 * @return Returns the selected value 
	 */
	public Object getSelectedValue(){
		return validOption;
	}
	
	/**  Return the <code>getSelectedValue</code> as a 
	 * displayable <code>String</code> */
	public String getDisplayableSelectedValue(){
		return (validOption == null ? "" : validOption);
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
	


	public void setOptions(List<String> options) {
		this.options = options;
	}
	
}
