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
package org.webcurator.ui.site.command;

import org.webcurator.domain.model.core.AuthorisingAgent;

/**
 * Command object used for the harvest autorisations authorising agency tab.
 * @author bbeaumont
 */
public class SiteAuthorisingAgencyCommand {
	private String identity;
	private String name;
	private String description;
	private String contact;
	private String phoneNumber;
	private String email;	
	private String address;
	private String cmdAction;
	
	public static SiteAuthorisingAgencyCommand buildFromModel(AuthorisingAgent agent) {
		SiteAuthorisingAgencyCommand command = new SiteAuthorisingAgencyCommand();
		command.identity = agent.getIdentity();
		command.name = agent.getName();
		command.description = agent.getDescription();
		command.contact = agent.getContact();
		command.phoneNumber = agent.getPhoneNumber();
		command.email = agent.getEmail();
		command.address = agent.getAddress();
		
		return command;
	}
	
	public void updateBusinessModel(AuthorisingAgent agent) {
		agent.setName(name);
		agent.setDescription(description);
		agent.setContact(contact);
		agent.setPhoneNumber(phoneNumber);
		agent.setEmail(email);
		agent.setAddress(address);
	}
	
	
	/**
	 * @return Returns the address.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address The address to set.
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return Returns the contact.
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * @param contact The contact to set.
	 */
	public void setContact(String contact) {
		this.contact = contact;
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
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
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
	 * @return Returns the phoneNumber.
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber The phoneNumber to set.
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return Returns the temporaryIdentity.
	 */
	public String getIdentity() {
		return identity;
	}

	/**
	 * @param temporaryIdentity The temporaryIdentity to set.
	 */
	public void setIdentity(String temporaryIdentity) {
		this.identity = temporaryIdentity;
	}

	/**
	 * @return the cmdAction
	 */
	public String getCmdAction() {
		return cmdAction;
	}

	/**
	 * @param cmdAction the cmdAction to set
	 */
	public void setCmdAction(String cmdAction) {
		this.cmdAction = cmdAction;
	}
}
