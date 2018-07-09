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

/** 
 * The Command object for modifying a Flag.
 * @author twoods
 */
public class FlagCommand {
    /** The name of the edit role action. */
    public static final String ACTION_EDIT = "edit";
    /** The name of the save role action. */
    public static final String ACTION_SAVE = "save";
    /** The name of the delete role action. */
    public static final String ACTION_DELETE = "delete";
    /** The name of the view role action. */
    public static final String ACTION_VIEW = "view";
    /** The name of the New Role action */
    public static final String ACTION_NEW ="new";
    /** The name of the filter action */
    public static final String ACTION_FILTER = "filter";
    
    /** The constant name of the user oid field. */    
    public static final String PARAM_OID = "oid";
    /** The constant name of the action command field. */
    public static final String PARAM_CMD = "cmd";
    /** The constant name of the agency filter field. */
    public static final String PARAM_AGENCY_FILTER = "agencyFilter";
    
    /** the name of the rejection reason DTO's model object. */
    public static final String MDL_FLAGS = "flags";
    /** the name of the agency filter model object. */
    public static final String MDL_AGENCYFILTER = "agencyfilter";
    /** the name of the agencies model object. */
    public static final String MDL_AGENCIES = "agencies";
    /** the name of the logged in user object. */
    public static final String MDL_LOGGED_IN_USER = "loggedInUser";
    
    /** the action command. */
    private String cmd;
    /** the user oid. */
    private Long oid;
    /** the current agency filter. */
    private String agencyFilter = "";
    
	/**
	 * @return the cmd
	 */
	public String getCmd() {
		return cmd;
	}
	/**
	 * @param cmd the cmd to set
	 */
	public void setCmd(String cmd) {
		this.cmd = cmd;
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
	 * @return the filter
	 */
	public String getAgencyFilter() {
		return agencyFilter;
	}
	/**
	 * @param agencyFilter the filter to set
	 */
	public void setAgencyFilter(String agencyFilter) {
		this.agencyFilter = agencyFilter;
	}
}
