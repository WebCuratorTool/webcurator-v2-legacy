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
package org.webcurator.ui.profiles.command;

import org.webcurator.domain.model.core.Profile;

/**
 * The command for editing the general information about a profile.
 * @author bbeaumont
 *
 */
public class GeneralCommand {
	/** The name of the profile. **/
	private String name;
	
	/** The description of the profile. **/
	private String description;
	
	/** The current status of the profile. **/
	private int status;
	
	/** The profile selection level required for a user to be able
	 * to use this profile on a target. */
	private int requiredLevel;

	/**
	 * Build a command object from the Profile.
	 * @param profile The business model object.
	 * @return A new GeneralCommand object.
	 */
	public static GeneralCommand buildFromModel(Profile profile) {
		GeneralCommand command = new GeneralCommand();
		command.setName( profile.getName() );
		command.setDescription( profile.getDescription() );
		command.setStatus( profile.getStatus() );
		command.setRequiredLevel( profile.getRequiredLevel() );
		
		return command;
	}
	
	/**
	 * Update the business object.
	 * @param profile The profile to update.
	 */
	public void updateBusinessModel(Profile profile) {
		profile.setName(name);
		profile.setDescription(description);
		profile.setStatus(status);
		profile.setRequiredLevel(requiredLevel);
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
	 * @return Returns the requiredLevel.
	 */
	public int getRequiredLevel() {
		return requiredLevel;
	}

	/**
	 * @param requiredLevel The requiredLevel to set.
	 */
	public void setRequiredLevel(int requiredLevel) {
		this.requiredLevel = requiredLevel;
	}

	/**
	 * @return Returns the status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status The status to set.
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	

}
