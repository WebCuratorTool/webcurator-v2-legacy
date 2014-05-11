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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.webcurator.ui.util.DateUtils;


/**
 * Parameter representing a Date.<br>
 * <br>
 * The date format is by default set to European format <code>dd/MM/yyyy</code>.  
 * 
 * @author MDubos
 */
public class DateParameter implements Parameter {

	protected String name;
	protected Date date;
	protected String description;
	protected Boolean optional;
	
	private SimpleDateFormat dateFormat = DateUtils.get().getDateFormat("core.common.fullDateMask"); 
	protected Log log = LogFactory.getLog(DateParameter.class);
	protected Date selectedDate;	
	
	protected Date validDate; 
	
	/**
	 * Default constructor
	 */
	public DateParameter(){
	}

	/**
	 * Constructor with full parameters
	 * @param name Name of parameter
	 * @param date Date of parameter
	 * @param description Description
	 * @param optional Is it an optional parameter?
	 */
	public DateParameter(String name, String date, String description, Boolean optional){
		this.name = name;
		this.description = description;
		this.optional = optional;
		setValue(date);
	}
	

	// A ID counter for allocating unique ID numbers 
	private static int inputCt = 0;//TODO still necessary as long as we keep the order of the String parameters???
	
	
	/**
	 * Rendering of a Date as a text INPUT
	 */
	public String getInputRendering() throws IOException {
		StringBuffer sb = new StringBuffer();
		
		String dateInputID = "input_date_" + inputCt++ ;
		//String dateImgID = "input_button_" + inputCt++ ;
		
		sb.append("<input type=\"text\" name=\"parameters\" id=\"" + dateInputID + "\" value=\"" + getDisplayableSelectedValue() + "\" >");
			        
		// Optional
		if(!optional){
			sb.append("<font color=red size=2>&nbsp;<strong>*</strong>&nbsp;</font>");
		}		
		sb.append("<font size=\"1\">" + dateFormat.toPattern() + "</font>");
		if(optional){
			sb.append("<i><font size=\"1\">&nbsp;(Optional)</font></i>");
		}		
		
		return sb.toString();
	}

	
	public String getType() {
		return this.getClass().getName();
	}
	
	public Date getValue() {
		return date;
	}

	public void setValue(Object date) {
		if(date instanceof Date){
			this.date = (Date)date;
		}
		else if(date instanceof String){
			try{
				this.date = dateFormat.parse((String)date);
			}catch(Exception ex){
				log.warn("Error in setting date value: " + ex.getMessage());
			}
		}
	}


	
	/**
	 * Usefull displaying all properties
	 */
	public String toString(){
		return getName() + "|" + getValue() + "|" + getDescription() + "|" + getOptional();
	}

	/** Validation of the parameter */
	public void validate(Errors errors, 
			String name, Object value, String description, Boolean optional){
			
		// Optional
		if( optional.booleanValue() ){
			return;
		}
		
		// null/blank 
		log.debug("validate DateParameter: value=[" + value + "]");//TODO now s String but should be a date when I improve the code
		if( value == null || ((String)value).equals("") ){
			errors.reject("required", 
					new Object[]{"The " + description}, 
					"A mandatory date is missing");
		}
		
		else{
			try{
				dateFormat.parse((String)value);
			} catch (ParseException e) {
				errors.reject("typeMismatch.java.text.DateFormat", 
						new Object[]{description, dateFormat.toPattern()},
						"A date does not have the correct format " + dateFormat.toPattern()
						);;
			}
			
		}
	}

	/**
	 * @return Returns the dateFormat.
	 */
	public SimpleDateFormat getDateFormat() {
		return this.dateFormat;
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
	 * @param value Set the selected value (Date)
	 */
	public void setSelectedValue(Object value){
		validDate = (Date) value;
	}
	
	/**
	 * @return The selected value
	 */
	public Object getSelectedValue(){
		return validDate;
	}
	
	/**  Return the <code>getSelectedValue</code> as a 
	 * displayable <code>String</code> */
	public String getDisplayableSelectedValue(){
		return (validDate == null ? "" : getDateFormat().format(validDate));
	}

}
