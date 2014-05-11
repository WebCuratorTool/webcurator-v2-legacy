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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;

/**
 * Parameter for Agencies<br>
 * <br>
 * When rendered as a HTML input, it is displayed as a <code>SELECT</code> 
 * filled with agencies names, with also the option to select 'All agencies'. 
 * As all other <code>ListParameter</code> parameters, the option selected  
 * is then seen as a <code>StringParameter</code>.  
 * @see ListParameter#getType
 * 
 * @author MDubos
 *
 */
public class AgencyParameter extends ListParameter {
	
	protected AgencyUserManager agencyUserManager = null;

	protected AuthorityManager authorityManager = null;
	
	protected Log log = LogFactory.getLog(UserParameter.class);

	/**
	 * Rendering as a <code>SELECT</code> filled with
	 * agencies names
	 */
	@SuppressWarnings("unchecked")
	public String getInputRendering() throws IOException {
		StringBuffer sb = new StringBuffer();
		List<Agency> agencies = null;
		
		
		// Retreive Agencies
		
		if(authorityManager.hasPrivilege(Privilege.SYSTEM_REPORT_LEVEL_1, Privilege.SCOPE_ALL)){
			agencies = agencyUserManager.getAgencies();
			log.debug("  SYSTEM_REPORT_LEVEL_1 SCOPE_ALL");
		} 
		else if(authorityManager.hasPrivilege(Privilege.SYSTEM_REPORT_LEVEL_1, Privilege.SCOPE_AGENCY)){
			agencies = new ArrayList<Agency>();
			agencies.add(AuthUtil.getRemoteUserObject().getAgency());
			log.debug("  SYSTEM_REPORT_LEVEL_1 SCOPE_AGENCY");
		}
		else{
			// SCOPE_OWNER or SCOPE_NONE
			log.warn("No agency (SCOPE_OWNER or SCOPE_NONE)");
		}
		
		
		// SELECT
		
		String preselected = getSelectedValue() != null ? (String)getSelectedValue() : "";
		sb.append("<select name=\"parameters\">\n");
		if(agencies.size() > 1){
			sb.append("<option>All agencies</option>\n");			
		}
		for(Agency agency : agencies){
			sb.append("<option " + (agency.getName().equals(preselected) ? "SELECTED" : "") + ">" + agency.getName() + " </option>");
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
	 * @return Returns the agencyUserManager.
	 */
	public AgencyUserManager getAgencyUserManager() {
		return agencyUserManager;
	}

	/**
	 * @param agencyUserManager The agencyUserManager to set.
	 */
	public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
		this.agencyUserManager = agencyUserManager;
	}

	/**
	 * @return Returns the authorityManager.
	 */
	public AuthorityManager getAuthorityManager() {
		return authorityManager;
	}

	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}

		
}
