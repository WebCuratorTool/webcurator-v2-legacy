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
package org.webcurator.ui.admin.command;

import java.util.List;

/**
 * The create rejection reason command object.
 * @author oakleigh_sk
 */
public class CreateQaIndicatorCommand {
	/** the name of the agencies model object. */
    public static final String MDL_AGENCIES = "agencies";
	/** the name of the units model object. */
    public static final String MDL_UNITS = "units";
    /** The name of the save role action. */
    public static final String ACTION_SAVE = "save";
    /** The name of the New Role action */
    public static final String ACTION_NEW ="new";
    /** The name of the View user action */
    public static final String ACTION_VIEW ="view";
    /** The name of the Edit user action */
    public static final String ACTION_EDIT ="edit";
    
    /** the constant name of the user oid object. */
    public static final String PARAM_OID = "oid";
    /** the constant name of the action command object. */
    public static final String PARAM_ACTION = "action";
    /** the constant name of the name field. */
    public static final String PARAM_NAME = "name";
    /** the constant name of the user name object. */
    public static final String PARAM_DESCRIPTION = "description";
    
    public static final String PARAM_UPPER_LIMIT = "upperLimit";
    
    public static final String PARAM_LOWER_LIMIT = "lowerLimit";
    
    public static final String PARAM_UPPER_LIMIT_PERCENTAGE = "upperLimitPercentage";
    
    public static final String PARAM_LOWER_LIMIT_PERCENTAGE = "lowerLimitPercentage";
    
    public static final String PARAM_UNIT = "unit";
    
    public static final String PARAM_SHOW_DELTA = "showDelta";
    
    /** the constant name of the agency oid object. */
    public static final String PARAM_AGENCY_OID = "agencyOid";
    /** the constant name of the mode object. */
    public static final String PARAM_MODE = "mode";
    
    /** the unique rejection reason oid field. */
    private Long oid;
    /** the action field. */
    private String action;
    /** the name field. */
    private String name;
    
    private String description;
    
    private Float upperLimit;
    
    private Float lowerLimit;
    
    private Float upperLimitPercentage;
    
    private Float lowerLimitPercentage;
    
    private String unit;
    
    private Boolean showDelta;
    
    private Boolean enableReport;
    
    /** the reason's agency oid field. */
    private Long agencyOid;
    /** the agencies field. */
    private List agencies;
    /** the mode field. */
    private String mode;

    private CreateQaIndicatorCommand command;
    
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return the agencies
	 */
	public List getAgencies() {
		return agencies;
	}
	/**
	 * @param agencies the agencies to set
	 */
	public void setAgencies(List agencies) {
		this.agencies = agencies;
	}
	/**
	 * @return the agencyOid
	 */
	public Long getAgencyOid() {
		return agencyOid;
	}
	/**
	 * @param agencyOid the agencyOid to set
	 */
	public void setAgencyOid(Long agencyOid) {
		this.agencyOid = agencyOid;
	}
	/**
	 * @return the command
	 */
	public CreateQaIndicatorCommand getCommand() {
		return command;
	}
	/**
	 * @param command the command to set
	 */
	public void setCommand(CreateQaIndicatorCommand command) {
		this.command = command;
	}
	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
	/**
	 * @return the oid
	 */
	public Long getOid() {
		return oid;
	}
	/**
	 * @param oid the oid to set
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param descripton the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	public Float getUpperLimit() {
		return upperLimit;
	}
	
	public void setUpperLimit(Float upperLimit) {
		this.upperLimit = upperLimit;
	}
	
	public Float getLowerLimit() {
		return lowerLimit;
	}
	
	public void setLowerLimit(Float lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	
	public Float getUpperLimitPercentage() {
		return upperLimitPercentage;
	}
	
	public void setUpperLimitPercentage(Float upperLimitPercentage) {
		this.upperLimitPercentage = upperLimitPercentage;
	}
	
	public Float getLowerLimitPercentage() {
		return lowerLimitPercentage;
	}
	
	public void setLowerLimitPercentage(Float lowerLimitPercentage) {
		this.lowerLimitPercentage = lowerLimitPercentage;
	}
	
	public String getUnit(){
		return unit;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
    public Boolean getShowDelta() {
    	return showDelta;
    }

    public void setShowDelta(Boolean showDelta) {
    	this.showDelta = showDelta;
    }
	public Boolean getEnableReport() {
		return enableReport;
	}
	public void setEnableReport(Boolean enableReport) {
		this.enableReport = enableReport;
	}
}
