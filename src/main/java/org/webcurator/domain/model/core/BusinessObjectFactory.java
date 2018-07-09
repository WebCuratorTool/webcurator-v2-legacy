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
package org.webcurator.domain.model.core;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.profiles.ProfileManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.core.util.Utils;
import org.webcurator.domain.model.auth.User;


/**
 * The BusinessObjectFactory is responsible for instantiating objects and giving
 * them unique identifiers. It also ensures that all critical properties are
 * initialised correctly. While in some cases this can be done in the 
 * constructors, some of the initial values are dependent on other business
 * objects.
 *  
 * @author bbeaumont
 *
 */
public class BusinessObjectFactory {
	/** The log file. */
	private Log log = LogFactory.getLog(BusinessObjectFactory.class);
	/** The current identity value for creating new identifiers. */
	private long identity;
	/** The profile manager */
	private ProfileManager profileManager = null;
	
	/**
	 * Get a new identity value.
	 * @return A temporary identity value, always starting with "t".
	 */
	private synchronized String getNewIdentity() {
		return "t" + identity++;
	}
	
	/**
	 * Create a new Schedule.
	 * @param owner The AbstractTarget the schedule belongs to.
	 * @return A new schedule object.
	 */
	public Schedule newSchedule(AbstractTarget owner) {
		Schedule s = new Schedule();
		s.setIdentity(getNewIdentity());
		s.setOwningUser(AuthUtil.getRemoteUserObject());
		s.setTarget(owner);

		// Set a default start date of today.
		s.setStartDate(Utils.clearTime(new Date()));
		return s;
	}
	
	/**
	 * Create a new Authorising Agent.
	 * @return A new authorising agent.
	 */
	public AuthorisingAgent newAuthorisingAgent() {
		AuthorisingAgent agent = new AuthorisingAgent();
		agent.setIdentity(getNewIdentity());
		return agent;
	}
	
	/**
	 * Create a new seed.
	 * @param owner The target that this seed belongs to.
	 * @return A new seed.
	 */
	public Seed newSeed(Target owner) {
		Seed seed = new Seed();
		seed.setIdentity(getNewIdentity());
		seed.setTarget(owner);
		return seed;
	}
	
	/**
	 * Create a new seed history.
	 * @param owner The targetInstance that this seed history belongs to.
	 * @param seed The seed that this seed history relates to.
	 * @return A new seed history.
	 */
	public SeedHistory newSeedHistory(TargetInstance owner, Seed seed) {
		SeedHistory seedHistory = new SeedHistory(owner, seed);
		seedHistory.setIdentity(getNewIdentity());
		return seedHistory;
	}
	
	/**
	 * Create a new URL pattern.
	 * @param owner The site that owns the URL pattern.
	 * @return A new URL pattern.
	 */
	public UrlPattern newUrlPattern(Site owner) {
		UrlPattern pattern = new UrlPattern();
		pattern.setIdentity(getNewIdentity());
		pattern.setSite(owner);
		return pattern;
	}
	
	/**
	 * Create a new Permission object.
	 * @param owner The site that the permission object belongs to.
	 * @return A new permission object.
	 */
	public Permission newPermission(Site owner) {
		Permission permission = new Permission();
		permission.setIdentity(getNewIdentity());
		permission.setStatus(Permission.STATUS_PENDING);
		permission.setSite(owner);
		permission.setOwningAgency(owner.getOwningAgency());
		permission.setCreationDate(new Date());
		return permission;
	}
	
	/**
	 * Create a new target.
	 * @return A new target.
	 */
	public Target newTarget() {
		Target target = new Target();
		target.setIdentity(getNewIdentity());
		target.setOwner(AuthUtil.getRemoteUserObject());
		target.setState(Target.STATE_PENDING);
		target.setCreationDate(new Date());

		// Set the default profile on the target.
		Profile profile = profileManager.getDefaultProfile(AuthUtil.getRemoteUserObject().getAgency());
		target.setProfile(profile);
		
		
		return target;
	}
	
	/**
	 * Create a new target.
	 * Override method passing the User object to use for cases 
	 * (e.g in the context of a soap server to server call)
	 * when the caller is not an authenticated web-page.
	 * @return A new target.
	 */
	public Target newTarget(User theUser) {
		Target target = new Target();
		target.setIdentity(getNewIdentity());
		target.setOwner(theUser);
		target.setState(Target.STATE_PENDING);
		target.setCreationDate(new Date());

		// Set the default profile on the target.
		Profile profile = profileManager.getDefaultProfile(theUser.getAgency());
		target.setProfile(profile);
		
		return target;
	}

	/**
	 * Create a new target group.
	 * @return A new target group.
	 */
	public TargetGroup newTargetGroup() {
		TargetGroup group = new TargetGroup();
		group.setIdentity(getNewIdentity());
		group.setOwner(AuthUtil.getRemoteUserObject());
		group.setState(TargetGroup.STATE_PENDING);
		group.setSipType(TargetGroup.MANY_SIP);
		group.setCreationDate(new Date());
		group.setFromDate(new Date());
		
		// Set the default profile on the target group.
		Profile profile = profileManager.getDefaultProfile(AuthUtil.getRemoteUserObject().getAgency());
		group.setProfile(profile);
		
		return group;
	}
	
	/**
	 * Spring setter method to inject the ProfileManager.
	 * @param profileManager The profileManager to set.
	 */
	public void setProfileManager(ProfileManager profileManager) {
		this.profileManager = profileManager;
	}	
}
