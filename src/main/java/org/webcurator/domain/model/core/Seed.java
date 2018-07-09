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
import java.util.HashSet;
import java.util.Set;



/**
 * Defines a seed that is within the Target.
 * 
 * @author bbeaumont
 * @hibernate.class table="SEED" lazy="false"
 */
public class Seed extends AbstractIdentityObject {
	/** The unique ID of the seed **/
	private Long oid;
	/** The seed itself **/
	private String seed;
	/** The seed's target **/
	private Target target;
	/** The set of related permissions */
	private Set<Permission> permissions = new HashSet<Permission>();
	/** Sets if the seed is primary or secondary. */
	private boolean primary; 
	
	/**
	 * Protected constructor to prevent instantiation by non-application
	 * components. This class should be instantiated by the 
	 * BusinessObjectFactory.
	 */
	protected Seed() {}
	

    /**
     * Returns the database OID of the seed.
     * @return Returns the oid.
     * @hibernate.id column="S_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="General" 
     */
    public Long getOid() {
        return oid;
    }	
	
    /**
     * Set the OID of the seed.
     * @param anOid The OID.
     */
    public void setOid(Long anOid) {
    	this.oid = anOid;
    }
	
	/**
	 * Gets the seed URL.
	 * @return Returns the seed.
     * @hibernate.property column="S_SEED" length="1024" 
	 */
	public String getSeed() {
		return seed;
	}

	/**
	 * Sets the Seed URL.
	 * @param seed The seed to set.
	 */
	public void setSeed(String seed) {
		this.seed = seed;
	}
	
	/**
	 * Get the Target to which this seed belongs.
	 * @return Returns the target.
	 * @hibernate.many-to-one column="S_TARGET_ID" foreign-key="FK_SEED_TARGET_ID" 
	 */
	public Target getTarget() {
		return target;
	}
	
	/**
	 * Set the Target to which this seed belongs.
	 * @param aTarget The target to set.
	 */
	public void setTarget(Target aTarget) {
		this.target = aTarget;
	}
	
	/**
	 * Get the set of permissions associated with this seed.
	 * 
	 * @return Returns the permissions.
	 * @hibernate.set table="SEED_PERMISSION"
     * @hibernate.collection-key column="SP_SEED_ID"
     * @hibernate.collection-many-to-many class="org.webcurator.domain.model.core.Permission" column="SP_PERMISSION_ID" foreign-key="FK_SP_PERMISSION_ID"
	 */
	public Set<Permission> getPermissions() {
		return permissions;
	}

	/**
	 * Set the permissions associated with this seed.
	 * @param permissions The permissions to set.
	 */
	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}
	
	/** 
	 * Checks if the seed is approved for harvest at the given time. The seed
	 * is approved if:
	 * <ul>
	 *   <li>There is at least one permission;</li>
	 *   <li>At least one of those permissions is an approved permission;</li>
	 *   <li>It is not attached to any denied permissions;</li>
	 * </ul>
	 *   
	 * @param dt The date/time to check that the seed is approved.
	 */
	public boolean isApproved(Date dt) { 
		boolean approved = false;
		
		for(Permission p : permissions) {
			if( p.containsTime(dt) ) {
				if( p.getStatus() == Permission.STATUS_DENIED) {
					return false;
				}
				else if( p.getStatus() == Permission.STATUS_APPROVED){
					approved = true;
				}
			}
		}
		
		return approved;
	}

	/** 
	 * Checks if the seed is approved for harvest at the given time. The seed
	 * is approved if:
	 * <ul>
	 *   <li>There is at least one permission;</li>
	 *   <li>At least one of those permissions is an approved permission;</li>
	 *   <li>It is not attached to any denied permissions;</li>
	 * </ul>
	 *   
	 * @param dt The date/time to check that the seed is approved.
	 */
	public boolean isHarvestable(Date dt) {
		boolean approved = false;		
		for(Permission p : permissions) { 			
			if( p.containsTime(dt) ) {				
				if( p.getStatus() != Permission.STATUS_APPROVED) {
					return false;
				}
				else {
					approved = true;
				}
			}
		}
		
		return approved;
	}	
	
	/**
	 * Checks if the seed is defined as a primary seed.
	 * @return true if primary; otherwise false.
	 * @hibernate.property column="S_PRIMARY"
	 */
	public boolean isPrimary() {
		return primary;
	}


	/**
	 * Sets whether this seed should be primary.
	 * @param primary true to set as primary; otherwise false.
	 */
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	
	
	/**
	 * Add a permission to the seed.
	 * @param aPermission The permission to add.
	 */
	public void addPermission(Permission aPermission) {
		permissions.add(aPermission);
		target.setDirty(true);
	}
	
	/**
	 * Remove a permission from the seed.
	 * @param aPermission The permission to remove.
	 */
	public void removePermission(Permission aPermission) {
		permissions.remove(aPermission);
		target.setDirty(true);
	}
	
}
