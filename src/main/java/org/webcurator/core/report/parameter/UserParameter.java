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
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;

/**
 * Parameter for Users<br>
 * <br>
 * When rendered as a HTML input, it is displayed as a <code>SELECT</code> filled 
 * with users names, with also the option to select 'All users'.<br>
 * <br>As all other <code>ListParameter</code> parameters, the option selected 
 * is then seen as a <code>StringParameter</code>.  
 * See {@link org.webcurator.core.report.parameter.ListParameter#getType()}
 * 
 * @author MDubos
 *
 */
public class UserParameter extends ListParameter {

	protected AgencyUserManager agencyUserManager = null;
	
	protected AuthorityManager authorityManager = null;
	
	private Log log = LogFactory.getLog(UserParameter.class);

	/**
	 * Rendering as a <code>SELECT</code> filled with Users names.
	 * Contains the option 'All users'
	 */ 
	@SuppressWarnings("unchecked")
	public String getInputRendering() throws IOException {
		StringBuffer sb = new StringBuffer();
		List<User> users = null;
		
		
		// Retreive users
		
		if(authorityManager.hasPrivilege(Privilege.SYSTEM_REPORT_LEVEL_1, Privilege.SCOPE_ALL)){
			users = agencyUserManager.getUsers();
			log.debug("  SYSTEM_REPORT_LEVEL_1 SCOPE_ALL");
		} 
		else if(authorityManager.hasPrivilege(Privilege.SYSTEM_REPORT_LEVEL_1, Privilege.SCOPE_AGENCY)){			
			User user = AuthUtil.getRemoteUserObject();
			users = agencyUserManager.getUsers(user.getAgency().getOid());
			
			log.debug("  SYSTEM_REPORT_LEVEL_1 SCOPE_AGENCY");
		}
		else{
			// SCOPE_OWNER or SCOPE_NONE
			log.warn("No user (SCOPE_OWNER or SCOPE_NONE)");
		}
		
		
		
		// SELECT
		
		String selected = getSelectedValue() != null ? (String)getSelectedValue() : ""; 
		
		sb.append("<select name=\"parameters\">\n");
		if(users.size() > 1){
			sb.append("<option>All users </option>\n");			
		}
		for(User user : users){
			sb.append("<option " + (user.getUsername().equals(selected) ? " SELECTED " : "") + ">" + user.getUsername() + "</option>\n");
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
	 * @param agencyUserManager Set the agencyUserManager
	 */
	public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
		this.agencyUserManager = agencyUserManager;
	}

	/**
	 * @return Returns the agencyUserManager
	 */
	public AgencyUserManager getAgencyUserManager() {
		return agencyUserManager;
	}

	/**
	 * @return Returns the authorityManager
	 */
	public AuthorityManager getAuthorityManager() {
		return authorityManager;
	}

	/**
	 * @param authorityManager Set the authorityManager
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}
	
}
