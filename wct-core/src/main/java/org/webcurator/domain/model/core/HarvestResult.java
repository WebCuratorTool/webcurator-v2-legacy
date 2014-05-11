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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.webcurator.core.notification.UserInTrayResource;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.RejReason;

/**
 * The <code>HarvestResult</code> class describes the result of a harvest. It
 * contains and manages the resources. 
 * 
 * @hibernate.class table="HARVEST_RESULT" lazy="false"
 **/
public class HarvestResult implements UserInTrayResource {
	private static final int MAX_MOD_NOTE_LENGTH = 2000;
	
	/** The state for an unassessed HarvestResult - neither endorsed, rejected, indexed nor aborted */
	public static final int STATE_UNASSESSED = 0;
	/** The state for an Endorsed HarvestResult - ready for archiving */
	public static final int STATE_ENDORSED = 1;
	/** The state constant for a Rejected HarvestResult - one that should not be archived */
	public static final int STATE_REJECTED = 2;	
	/** The state constant for a Harvest Result that is being indexed. */
	public static final int STATE_INDEXING = 3;
	/** The state constant for a Harvest Result that has been aborted in indexing. */
	public static final int STATE_ABORTED = 4;
	
	/** The TargetInstance that this belongs to */
	protected TargetInstance targetInstance;
	/** The Harvest number; the original harvest is always number 1, the prune tool can created additional harvest results */
	protected int harvestNumber = 1;
	/** The primary key of the harvest result */
	protected Long oid = null;
	/** An index of the resources within this harvest */
	protected Map<String,HarvestResource> resources = new HashMap<String,HarvestResource>();
	/** The provenance note (how this harvest result was created */
	protected String provenanceNote;
	/** The creation date of this harvest result */
	protected Date creationDate;
	/** Who created this harvest result */
	protected User createdBy;
	/** The state of the HarvestResult - see the STATE_xxx constants */
	protected int state = 0;
	/** A list of Harvest Modification Notes */
	protected List<String> modificationNotes = new LinkedList<String>();
	/** The Harvest ID that this harvest was derived from */
	private Integer derivedFrom;
	/** Why this harvest result was rejected */
	protected RejReason rejReason;
	
	/**
	 * Construct a new HarvestResult.
	 */
	public HarvestResult() {
        super();
		this.creationDate = new Date();
	}

	/**
	 * Create a new HarvestResult from its DTO.
	 * @param aResult The DTO.
	 * @param aTargetInstance The TargetInstance that this HarvestResult belongs to.
	 */
    public HarvestResult(HarvestResultDTO aResult, TargetInstance aTargetInstance) {
        super();
        targetInstance = aTargetInstance;
        harvestNumber = aResult.getHarvestNumber();
        provenanceNote = aResult.getProvenanceNote();
        creationDate = aResult.getCreationDate();
        createdBy = aTargetInstance.getOwner();
        
        HarvestResource harvestResource = null;
        HarvestResourceDTO harvestResourceDTO = null;
        String key = "";        
        Map resourceDtos = aResult.getResources();
        Iterator it = resourceDtos.keySet().iterator();
        while (it.hasNext()) {
            key = (String) it.next();
            harvestResourceDTO = (HarvestResourceDTO) resourceDtos.get(key);
            harvestResource = new HarvestResource(harvestResourceDTO, this);
            resources.put(key, harvestResource);
        }
    }

	/**
	 * Get the number of the harvest result. This is 1 for the original harvest.
	 * Additional harvests may be created by the quality review tools.
	 * @return the number of the harvest  result.
	 * @hibernate.property column="HR_HARVEST_NO"
	 */		
	public int getHarvestNumber() {
		return harvestNumber;
	}

	/**
	 * Set the number of the harvest result.
	 * @param harvestNumber The number of the harvest result.
	 */
	public void setHarvestNumber(int harvestNumber) {
		this.harvestNumber = harvestNumber;
	}

	/**
	 * Get the primary key of the HarvestResult object.
	 * @return the primary key
     * @hibernate.id column="HR_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="HarvestResource" 
	 */		
	public Long getOid() {
		return oid;
	}

	/**
	 * Set the oid of the HarvestResult object.
	 * @param oid the primary key of the HarvestResult.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}

	/**
	 * Get the target instance that this object belongs to.
	 * @return The target instance that this object belongs to.	  
	 * @hibernate.many-to-one column="HR_TARGET_INSTANCE_ID" foreign-key="FK_HRC_TARGET_INSTANCE_ID"
	 */
	public TargetInstance getTargetInstance() {
		return targetInstance;
	}

	/**
	 * Set the target instance that this belongs to.
	 * @param targetInstance The target instance that this belongs to.
	 */
	public void setTargetInstance(TargetInstance targetInstance) {
		this.targetInstance = targetInstance;
	}

	/**
	 * Retrieve the map of resource names to HarvestResource objects. This can
	 * be used for generating a tree of the resources or developing the 
	 * browse tool. This is essentially a name-based index of the harvest
	 * result.
	 *
	 * @return the map of resource names to HarvestResource objects.
	 * @hibernate.map cascade="save-update" lazy="true"
	 * @hibernate.collection-one-to-many class="org.webcurator.domain.model.core.HarvestResource"
	 * @hibernate.collection-key column="HRC_HARVEST_RESULT_OID"
	 * @hibernate.collection-index column="HRC_NAME" type="java.lang.String"
	 */
	public Map<String, HarvestResource> getResources() {
		return resources;
	}

	/**
	 * Set the Map of resource name to HarvestResource objects.
	 * @param resources The Map of resource name to HarvestResource objects.
	 */
	public void setResources(Map<String, HarvestResource> resources) {
		this.resources = resources;
	}

	/**
	 * Get the provenance note that explains why this harvest was created.
	 * @return the note that explains why this harvest was created.
	 * @hibernate.property length="1024" not-null="true" column="HR_PROVENANCE_NOTE"
	 */	
	public String getProvenanceNote() {
		return provenanceNote;
	}

	/**
	 * Set the provenance note on this HarvestResult.
	 * @param provenanceNote The note that explains why this result was created.
	 */
	public void setProvenanceNote(String provenanceNote) {
		this.provenanceNote = provenanceNote;
	}

    /**
     * Get the date the result was created.
     * @return the date the record was created
     * @hibernate.property column="HR_CREATED_DATE" type="timestamp"
     */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Set the creation date of the harvest result.
	 * @param creationDate The creation date of the harvest result.
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Get the User that created the harvest result.
	 * @return The User object for the user that created this object.
	 * @hibernate.many-to-one column="HR_CREATED_BY_ID" foreign-key="FK_HR_CREATED_BY_ID"
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * Set the creator of this harvest result.
	 * @param createdBy The User object for the user that created this object.
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Get the Rejection Reason of this harvest result (if any).
	 * @return The RejReason object corresponding to the reason specified when a
	 * harvest is rejected.
	 * @hibernate.many-to-one column="HR_RR_OID" foreign-key="FK_HR_RR_OID"
	 */
	public RejReason getRejReason() {
		return rejReason;
	}

	/**
	 * Set the rejection reason for this harvest result.
	 * @param rejReason The RejReason object.
	 */
	public void setRejReason(RejReason rejReason) {
		this.rejReason = rejReason;
	}

	/**
	 * Get the state of this Harvest Result.
	 * @return the state
	 * @hibernate.property column="HR_STATE"
	 */
	public int getState() {
		return state;
	}

	/**
	 * Set the state of this harvest result.
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}
	

	/**
	 * Safe way to add modification notes and ensure that they are truncated
	 * to the appropriate length to fit in the database.
	 * @param notes A List of notes to add.
	 */
	public void addModificationNotes(List<String> notes) { 
		for(String s: notes) {
			addModificationNote(s);
		}
	}
	
	/**
	 * Safe way to add modification notes and ensure that they are truncated
	 * to the appropriate length to fit in the database.
	 * @param str The note to add.
	 */
	public void addModificationNote(String str) { 
		if(str.length() > HarvestResult.MAX_MOD_NOTE_LENGTH) {
			str = str.substring(0, HarvestResult.MAX_MOD_NOTE_LENGTH);
		}
		modificationNotes.add(str);
	}
	
	
	/**
	 * Get the list of modification notes. 
	 * @return The list of modification notes.
	 * @hibernate.list table="HR_MODIFICATION_NOTE" cascade="all-delete-orphan"  lazy="false"
	 * @hibernate.collection-key column="HMN_HR_OID"
	 * @hibernate.collection-index column="HMN_INDEX"
	 * @hibernate.collection-element type="string" column="HMN_NOTE" length="2000"
	 */
	public List<String> getModificationNotes() {
		return modificationNotes;
	}

	public void setModificationNotes(List<String> modificationNotes) {
		this.modificationNotes = modificationNotes;
	}

	/**
	 * Get the Harvest Number that this harvest was derived from.
	 * @return The harvest number that this harvest was derived from. If original, then
	 *         this will be null.
	 * @hibernate.property column="HR_DERIVED_FROM"
	 */
	public Integer getDerivedFrom() {
		return derivedFrom;
	}

	public void setDerivedFrom(Integer derivedFrom) {
		this.derivedFrom = derivedFrom;
	}

    /* (non-Javadoc)
     * @see org.webcurator.core.notification.InTrayResource#getResourceName()
     */
    public String getResourceName() {
        return this.getTargetInstance().getOid().toString()+"("+this.harvestNumber+")";
    }

    /* (non-Javadoc)
     * @see org.webcurator.core.notification.InTrayResource#getResourceType()
     */
    public String getResourceType() {
        return this.getClass().getName(); //May be an ArcHarvestResult
    }
    
    /* (non-Javadoc)
     * @see org.webcurator.core.notification.InTrayResource#getOwningUser()
     */
	@Override
	public User getOwningUser() {
		return getCreatedBy();
	}	
}
