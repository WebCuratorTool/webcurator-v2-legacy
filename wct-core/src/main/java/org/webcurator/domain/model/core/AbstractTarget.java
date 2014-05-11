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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Collections;

import org.webcurator.core.notification.UserInTrayResource;
import org.webcurator.core.util.Utils;
import org.webcurator.domain.UserOwnable;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.RejReason;

/**
 * Base Target object to capture the common behaviour between groups and 
 * targets.
 * 
 * @author bbeaumont
 * @hibernate.class table="ABSTRACT_TARGET" lazy="true"
 * @hibernate.query name="org.webcurator.domain.model.core.AbstractTarget.getAllDTOsByName" query="SELECT new org.webcurator.domain.model.dto.AbstractTargetDTO(t.oid, t.name, t.owner.oid, t.owner.username, t.owner.agency.name, t.state, t.profile.oid, t.objectType) FROM AbstractTarget t where lower(t.name) like lower(?) ORDER BY UPPER(t.name), t.objectType"
 * @hibernate.query name="org.webcurator.domain.model.core.AbstractTarget.cntAllDTOsByName" query="SELECT count(*) FROM AbstractTarget t where lower(t.name) like lower(?)"
 * @hibernate.query name="org.webcurator.domain.model.core.AbstractTarget.getGroupDTOsByName" query="SELECT new org.webcurator.domain.model.dto.AbstractTargetDTO(t.oid, t.name, t.owner.oid, t.owner.username, t.owner.agency.name, t.state, t.profile.oid, t.objectType) FROM AbstractTarget t where t.objectType = 0 and lower(t.name) like lower(?) ORDER BY UPPER(t.name), t.objectType"
 * @hibernate.query name="org.webcurator.domain.model.core.AbstractTarget.cntGroupDTOsByName" query="SELECT count(*) FROM AbstractTarget t where t.objectType = 0 and lower(t.name) like lower(?)"
 * @hibernate.query name="org.webcurator.domain.model.core.AbstractTarget.getDTOByOid" query="SELECT new org.webcurator.domain.model.dto.AbstractTargetDTO(t.oid, t.name, t.owner.oid, t.owner.username, t.owner.agency.name, t.state, t.profile.oid, t.objectType) FROM AbstractTarget t where t.oid=:oid"
 * @hibernate.query name="org.webcurator.domain.model.core.AbstractTarget.getTargetDTOsByProfileOid" query="SELECT new org.webcurator.domain.model.dto.AbstractTargetDTO(t.oid, t.name, t.owner.oid, t.owner.username, t.owner.agency.name, t.state, t.creationDate, t.profile.oid, t.objectType) FROM AbstractTarget t where t.objectType = 1 and t.profile.oid=:profileoid ORDER BY UPPER(t.name)"
 * @hibernate.query name="org.webcurator.domain.model.core.AbstractTarget.cntTargetDTOsByProfileOid" query="SELECT count(*) FROM AbstractTarget t where t.objectType = 1 and t.profile.oid=:profileoid"
 */
public abstract class AbstractTarget extends AbstractIdentityObject implements UserOwnable, Annotatable, Overrideable, UserInTrayResource {
	/** Query identifier for retrieving AbstractTargetDTOs by name */
	public static final String QUERY_DTO_BY_NAME = "org.webcurator.domain.model.core.AbstractTarget.getAllDTOsByName";
	public static final String QUERY_CNT_DTO_BY_NAME = "org.webcurator.domain.model.core.AbstractTarget.cntAllDTOsByName";
	
	/** Query identifier for retrieving Group DTOs by name */
	public static final String QUERY_GROUP_DTOS_BY_NAME = "org.webcurator.domain.model.core.AbstractTarget.getGroupDTOsByName";
	public static final String QUERY_CNT_GROUP_DTOS_BY_NAME = "org.webcurator.domain.model.core.AbstractTarget.cntGroupDTOsByName";	
	
	/** Query identifier for retrieving an AbstractTargetDTO by OID. */
	public static final String QUERY_DTO_BY_OID = "org.webcurator.domain.model.core.AbstractTarget.getDTOByOid";
	
	/** Query identifier for retrieving Target DTOs by agency and profileOid */
	public static final String QUERY_TARGET_DTOS_BY_PROFILE = "org.webcurator.domain.model.core.AbstractTarget.getTargetDTOsByProfileOid";
	public static final String QUERY_CNT_TARGET_DTOS_BY_PROFILE = "org.webcurator.domain.model.core.AbstractTarget.cntTargetDTOsByProfileOid";

	/** The maximum length of the target name */
	public static final int CNST_MAX_NAME_LENGTH = 255;
	/** The maximum length of the reference number string */
	public static final int MAX_REFERENCE_LENGTH = 50;
	
	/** The maximum length of the profile note. */
	public static final int MAX_PROFILE_NOTE_LENGTH = 255;

	/** Maximum length for the Display Note */
	public static int MAX_DISPLAY_NOTE_LENGTH = 4000;
	
	/** Maximum length for the Display Change Reason */
	public static int MAX_DISPLAY_CHANGE_REASON_LENGTH = 1000;

	/** Target Group Type */
	public static final int TYPE_GROUP = 0;
	/** Target Type */
	public static final int TYPE_TARGET = 1;

    /** the primary key of the Target. */
    private Long oid;
    /** The targets name. */
    private String name;
    /** the targets description. */
    private String description;
    /** The schedules related to the target. */
    private Set<Schedule> schedules = new HashSet<Schedule>();
    /** Owner of the target **/
    private User owner;    
    /** Profile Overrides */
    private ProfileOverrides overrides = new ProfileOverrides();
    /** The loaded state of the target **/
    private int originalState = -1;    
    /** The state of the target **/
    private int state; 
    /** The list of annotations. */
    private List<Annotation> annotations = new LinkedList<Annotation>();
    /** The list of deleted annotations. */
    private List<Annotation> deletedAnnotations = new LinkedList<Annotation>();
    /** True if the annotations have been loaded */
    private boolean annotationsSet = false;    
    /** Flag to state if the annotations have been sorted */
    private boolean annotationsSorted = false;    
    /** Flag to state if the annotations contain any flagged as alertable, making the whole target/group alertable */
    private boolean alertable = false;    
    /** Removed Schedules */
    private Set<Schedule> removedSchedules = new HashSet<Schedule>();    
    /** The target's base profile. */
    private Profile profile;
    /** The date the Target was created */
    private Date creationDate;
    /** The parents of this group */
    private Set<GroupMember> parents = new HashSet<GroupMember>(); 
    /** Flag to state if the object is "dirty" */
    private boolean dirty = false;    
     /** 
     * Identifies whether this is a target or group without needing to use
     * the instanceof operator, which can be important if the object is not
     * fully initialised by Hibernate.
     */
    protected int objectType;
    /** reference number to use when storing instances to the SIP.*/
    private String referenceNumber;
    /** A cross-domain information resource description of the target.*/
    private DublinCore dublinCoreMetaData;    
    /** The Profile Note */
    private String profileNote = null;
    
    
    private List<GroupMember> newParents = new LinkedList<GroupMember>();
    private Set<Long> removedParents = new HashSet<Long>();
    
    private boolean displayTarget = true;

	/** Why this target was rejected */
	protected RejReason rejReason;
	
    /** The total number of crawls (<code>TargetInstance</code>s) associated with the Target **/
    private int crawls = 0;
    
    /** The oid of the <code>TargetInstance</code> denoted as the <code>Target</code>s reference crawl **/
    private Long referenceCrawlOid = null;
    
    /** Determines if the any new target instances should be auto-pruned **/
    private boolean autoPrune = false;
    
    /** Determines if a target instance should be denoted as a reference crawl when it is archived **/
    private boolean autoDenoteReferenceCrawl = false;
    
    /** Any information that should be given to the Archivists **/
    private String requestToArchivists;
    
    /** The access zone of the target **/
    public static class AccessZone {
        public final static int PUBLIC=0, ONSITE=1, RESTRICTED=2;
    	private static String[] accessZoneText = {"Public","Onsite","Restricted"};
    	public static int getCount(){return accessZoneText.length;};
        public static String getText(int accessZone)
        {
        	if(accessZone < accessZoneText.length)
        	{
	         	return accessZoneText[accessZone];
        	}
        	
        	return "";
        }
    }
    
    private int accessZone; 

    /** The Display Note */
    private String displayNote = null;
    
    /** The Display Change Reason */
    private String displayChangeReason = null;

    /**
     * Base constructor to prevent no-arg instantiation. 
     */
    protected AbstractTarget() {
    }
    
    
    /**
     * Constructor that sets the Object type.
     * @param objectType Either TYPE_TARGET or TYPE_GROUP
     */
    protected AbstractTarget(int objectType) {
    	this.objectType = objectType;
    }
    
    /**
     * Returns the OID of the AbstractTarget.
     * @return Returns the oid.
     * @hibernate.id column="AT_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="General" 
     */
    public Long getOid() {
        return oid;
    }

    /**
     * Sets the OID of the AbstractTarget.
     * @param aOid The oid to set.
     */
    public void setOid(Long aOid) {
        this.oid = aOid;
    }
    
    /**
     * Returns the description of the AbstractTarget.
     * @return Returns the description.
     * @hibernate.property column="AT_DESC" length="4000"
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the AbstractTarget.
     * @param aDescription The description to set.
     */
    public void setDescription(String aDescription) {
    	if(!Utils.hasChanged(description, aDescription)) { dirty = true; }
        this.description = aDescription;
    }

    /**
     * Returns the name of the AbstractTarget.
     * @return the name of the AbstractTarget.
     * @hibernate.property column="AT_NAME" length="255" unique="true"
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the abstract target.
     * @param aName The name to set.
     */
    public void setName(String aName) {
    	if(!Utils.hasChanged(name, aName)) { dirty = true; }
        this.name = aName;
        if (name != null) {
        	this.name = this.name.trim();
        }
    }    

    
	/**
	 * Returns whether the object has been modified since being loaded from
	 * the database, or initialised from the BusinessObjectFactory.
	 * @return true if changed; otherwise false.
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Sets the object to be "dirty" (modified since initialisation).
	 * @param dirty true to indicate it has been changed; false to indicate it
	 * 				has not.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	/* (Non-javadoc)
	 *  @see Annotatable#addAnnotation(Annotation). 
	 */
	public void addAnnotation(Annotation annotation) {
		annotations.add(annotation);
		annotationsSorted = false;
		alertable=false;
		for (Annotation ann: annotations) {
			if (ann.isAlertable()) {
				alertable=true;
				break;
			}
		}
	}
	
	/* (Non-javadoc)
	 *  @see Annotatable#getAnnotation(int). 
	 */
	public Annotation getAnnotation(int index) {
		return annotations.get(index);	
	}
		
	/* (Non-javadoc)
	 *  @see Annotatable#deleteAnnotation(int).
	 */
	public void deleteAnnotation(int index)
	{
		Annotation annotation = annotations.get(index);
		if(annotation != null)
		{
			deletedAnnotations.add(annotation);
			annotations.remove(index);
		}
		alertable=false;
		for (Annotation ann: annotations) {
			if (ann.isAlertable()) {
				alertable=true;
				break;
			}
		}

	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.domain.model.core.Annotatable#getAnnotations()
	 */
	public List<Annotation> getAnnotations() {		
		return annotations;
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.domain.model.core.Annotatable#getDeletedAnnotations()
	 */
	public List<Annotation> getDeletedAnnotations() {		
		return deletedAnnotations;
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.domain.model.core.Annotatable#setAnnotations(java.util.List)
	 */
	public void setAnnotations(List<Annotation> aAnnotations) {		
		annotations = aAnnotations;
		deletedAnnotations.clear();
		annotationsSet = true;
		annotationsSorted = false;
		alertable=false;
		for (Annotation ann: annotations) {
			if (ann.isAlertable()) {
				alertable=true;
				break;
			}
		}
	}
	
	/**
	 * Returns true if the annotations have been initialised.
	 * @return true if the annotations have been initialised; otherwise false.
	 */
	public boolean isAnnotationsSet() {
		return annotationsSet;
	}	
    
	/*(non-Javadoc)
	 * @see org.webcurator.domain.model.core.Annotatable#getSortedAnnotations()
	 */
	public List<Annotation> getSortedAnnotations()
	{
		if(!annotationsSorted)
		{
			sortAnnotations();
		}
		return getAnnotations();
	}
		
	/*(non-Javadoc)
	 * @see org.webcurator.domain.model.core.Annotatable#sortAnnotations()
	 */
	public void sortAnnotations()
	{
		Collections.sort(annotations);
		annotationsSorted = true;
	}
		
	/**
	 * Returns the owner of the AbstractTarget.
	 * @return Returns the owner.
	 * @hibernate.many-to-one column="AT_OWNER_ID"
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * Sets the owner of the AbstractTarget.
	 * @param owner The owner to set.
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	/**
	 * Gets the owner of the AbstractTarget
	 * @return The owner of the AbstractTarget.
	 */
	public User getOwningUser() {
		return owner;
	}
	
    /**
     * Retrieves the profile overrides of the AbstractTarget.
	 * @return Returns the overrides.
	 * @hibernate.many-to-one column="AT_PROF_OVERRIDE_OID" cascade="save-update" class="org.webcurator.domain.model.core.ProfileOverrides" foreign-key="FK_T_PROF_OVERRIDE_OID" 
	 */
	public ProfileOverrides getOverrides() {
		return overrides;
	}
	
    /**
     * Retrieves the profile overrides of the AbstractTarget.
	 * @return Returns the overrides.
	 */
	public ProfileOverrides getProfileOverrides() {
		return getOverrides();
	}	

	/**
	 * Sets the profile overrides of the AbstractTarget.
	 * @param overrides The overrides to set.
	 */
	public void setOverrides(ProfileOverrides overrides) {
		this.overrides = overrides;
	}	
	
	/**
	 * Gets the schedules of the AbstractTarget.
     * @return Returns the schedules.
     * @hibernate.set cascade="all-delete-orphan"
     * @hibernate.collection-key column="S_ABSTRACT_TARGET_ID"
     * @hibernate.collection-one-to-many class="org.webcurator.domain.model.core.Schedule"
     */
    public Set<Schedule> getSchedules() {
        return schedules;
    }

    /**
     * Sets the schedules on the AbstractTarget.
     * @param aSchedules The schedules to set.
     */
    public void setSchedules(Set<Schedule> aSchedules) {
        this.schedules = aSchedules;
    }
    
	/**
	 * Retrieves the original state, which is the state of initialisation or 
	 * the state that was loaded from the database. This is used to detect that
	 * the state has changed so that the TargetManager can perform the 
	 * necessary persistence logic.
	 * @return Returns the original state.
	 */
	public int getOriginalState() {
		return originalState;
	}

	/**
	 * Returns the state of the AbstractTarget.
	 * @return Returns the state.
	 * @hibernate.property column="AT_STATE"
	 */
	public int getState() {
		return state;
	}
	
	
	/**
	 * Updates the state of the AbstractTarget. This should be used by non-
	 * internal classes instead of setState, allowing additional logic to be
	 * tied into this point without affecting Hibernate. 
	 * 
	 * @param newState The new state of the AbstractTarget.
	 */
	public void changeState(int newState) {
		setState(newState);
	}
	
	
	/**
	 * Private setter for Hibernate access.
	 * @param state The state of the object.
	 */
	protected void setState(int state) {
		this.state = state;
		if(originalState == -1) {
			originalState = state;
		}
	}
	
	/**
	 * Add a schedule to the AbstractTarget.
	 * @param aSchedule The schedule to add.
	 */
	public void addSchedule(Schedule aSchedule) {
		// Set the bi-directional navigation.
		aSchedule.setTarget(this);
		schedules.add(aSchedule);
	}
	
	/**
	 * Remove a schedule from the AbstractTarget.
	 * @param aSchedule The schedule to remove.
	 */
	public void removeSchedule(Schedule aSchedule) {
		// Remove the schedule
		this.schedules.remove(aSchedule);
		
		// If the schedule is an original, then we need to track its removal
		// to perform the business logic on save.
		if(!aSchedule.isNew()) {
			// Add record that fact that it has been removed.
			removedSchedules.add(aSchedule);
		}
	}	
	
	/**
	 * Gets the set of persisted schedules that have been removed from the 
	 * AbstractTarget and therefore need to be removed from the database. 
	 * @return A Set of persisted Schedule objects that have been removed from  
	 * 		   the AbstractTarget
	 */
	public Set<Schedule> getRemovedSchedules() {
		return removedSchedules;
	}
	
	
	/**
	 * Gets the associated harvest profile for this AbstractTarget.
	 * @return Returns the harvest profile associated with this AbstractTarget.
	 * @hibernate.many-to-one column="T_PROFILE_ID"
	 */
	public Profile getProfile() {
		return profile;
	}
	
	/**
	 * Returns the set of groups to which this AbstractTarget belongs.
	 * @return Returns a Set of GroupMember objects that identify child/parent
	 * 		   relationships.
     * @hibernate.set cascade="none"
     * @hibernate.collection-key column="GM_CHILD_ID"
     * @hibernate.collection-one-to-many class="org.webcurator.domain.model.core.GroupMember"
     */	
	public Set<GroupMember> getParents() {
		return parents;
	}

	/**
	 * Hibernate required method. Sets the parents of the object. 
	 * @param parents The parents to set.
	 */
	public void setParents(Set<GroupMember> parents) {
		this.parents = parents;
	}	
	
	
	/**
	 * Set the profile associated with this AbstractTarget.
	 * @param aProfile The profile to associate this target with.
	 */
	public void setProfile(Profile aProfile) {
		// don't setDirty here anymore..
		//if(!Utils.hasChanged(profile, aProfile)) { setDirty(true); }
		this.profile = aProfile;
	}   	
	
	
	/**
	 * Get the set of seeds that belong to this target.
	 * @return The set of seeds that belong to this target.
	 */
	public abstract Set<Seed> getSeeds();
	
	/**
	 * Is the target now schedulable?
	 * @return True if the new state of the target is schedulable.
	 */
	public abstract boolean isSchedulable();	
	
	/**
	 * Returns the object type (TYPE_TARGET or TYPE_GROUP). This can be used
	 * instead of instanceof, which is useful if the object isn't fully 
	 * initialised by Hibernate.
	 * @return Either TYPE_TARGET or TYPE_GROUP.
	 * @hibernate.property column="AT_OBJECT_TYPE"
	 */
	public int getObjectType() {
		return objectType;
	}
	
	/**
	 * setObjectType is required for Hibernate. It is not used
	 * elsewhere.
	 * @param type The object type.
	 */
	@SuppressWarnings("unused")
	private void setObjectType(int type) {
		objectType = type;
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.notification.InTrayResource#getResourceName()
	 */
    public String getResourceName() {
    	return name;
    }
    
    /* (non-Javadoc)
     * @see org.webcurator.core.notification.InTrayResource#getResourceType()
     */
    public String getResourceType() {
    	if(this instanceof TargetGroup)
    	{
    		//special case for lazy loaded TargetGroup
    		return TargetGroup.class.getName();
    	}
    	else
    	{
    		return this.getClass().getName();
    	}
    }
    
	/**
	 * Get the date that the AbstractTarget was created.
	 * @return Returns the creation date.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="AT_CREATION_DATE" sql-type="TIMESTAMP(9)"   
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	
	/**
	 * Set the date the object was created.
	 * @param creationDate The creationDate to set.
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the referenceNumber used for the instances stored in the archive.
	 * @hibernate.property column="AT_REFERENCE" length="255"
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @param referenceNumber the referenceNumber used for the instances stored in the archive to set.
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}  	


	/**
	 * @return Returns the profileNote.
	 * @hibernate.property column="AT_PROFILE_NOTE" length="255"
	 */
	public String getProfileNote() {
		return profileNote;
	}


	/**
	 * @param profileNote The profileNote to set.
	 */
	public void setProfileNote(String profileNote) {
		this.profileNote = profileNote;
	}    
 
	
		
	/**
	 * @return the dublinCoreMetaData
	 * @hibernate.many-to-one column="AT_DUBLIN_CORE_OID" cascade="all" class="org.webcurator.domain.model.core.DublinCore" foreign-key="FK_AT_DUBLIN_CORE_OID"
	 */
	public DublinCore getDublinCoreMetaData() {
		return dublinCoreMetaData;
	}

	/**
	 * @param dublinCoreMetaData the dublinCoreMetaData to set
	 */
	public void setDublinCoreMetaData(DublinCore dublinCoreMetaData) {
		this.dublinCoreMetaData = dublinCoreMetaData;
	}

	/**
	 * Get the Rejection Reason of this target (if any).
	 * @return The RejReason object corresponding to the reason specified when a
	 * target is rejected.
	 * @hibernate.many-to-one column="AT_RR_OID" foreign-key="FK_AT_RR_OID"
	 */
	public RejReason getRejReason() {
		return rejReason;
	}

	/**
	 * Set the rejection reason for this target.
	 * @param rejReason The RejReason object.
	 */
	public void setRejReason(RejReason rejReason) {
		this.rejReason = rejReason;
	}

	/**
	 * @return Returns the displayTarget boolean.
     * @hibernate.property column="AT_DISPLAY_TARGET" 
	 */
	public boolean isDisplayTarget() {
		return displayTarget;
	}

	/**
	 * @param displayTarget The displayTarget to set.
	 */
	public void setDisplayTarget(boolean displayTarget) {
		this.displayTarget = displayTarget;
	}

	/**
	 * @return Returns the alertable boolean.
	 */
	public boolean getAlertable() {
		return alertable;
	}

	/**
	 * Returns the access zone of the AbstractTarget.
	 * @return Returns the access zone.
	 * @hibernate.property column="AT_ACCESS_ZONE"
	 */
	public int getAccessZone() {
		return accessZone;
	}
	
	/**
	 * Updates the access zone of the AbstractTarget. 
	 * 
	 * @param accessZone The new access zone of the AbstractTarget.
	 */
	public void setAccessZone(int accessZone) {
		this.accessZone = accessZone;
	}

	/**
	 * Returns the text for the access zone of the AbstractTarget. 
	 * 
	 * @return accessZoneText The text for the current access zone of the AbstractTarget.
	 */
    public String getAccessZoneText()
    {
    	return AccessZone.getText(this.accessZone);
    }
	
	/**
	 * @return Returns the displayNote.
	 * @hibernate.property column="AT_DISPLAY_NOTE" length="4000"
	 */
	public String getDisplayNote() {
		return displayNote;
	}


	/**
	 * @param displayNote The displayNote to set.
	 */
	public void setDisplayNote(String displayNote) {
		this.displayNote = displayNote;
	}    
	
	/**
	 * @return Returns the displayChangeReason.
	 * @hibernate.property column="AT_DISPLAY_CHG_REASON" length="1000"
	 */
	public String getDisplayChangeReason() {
		return displayChangeReason;
	}

	/**
	 * @param displayChangeReason The displayChangeReason to set.
	 */
	public void setDisplayChangeReason(String displayChangeReason) {
		this.displayChangeReason = displayChangeReason;
	}    
	
	public List<GroupMember> getNewParents() {
		return newParents;
	}


	public Set<Long> getRemovedParents() {
		return removedParents;
	}
	
	/**
	 * Get the number of previous crawls for the <code>Target</code>
	 * @return The total number of previous crawls
	 * @hibernate.property column="AT_CRAWLS"
	 * @hibernate.property formula="(SELECT COUNT(*) FROM DB_WCT.TARGET_INSTANCE TI WHERE TI.TI_TARGET_ID=AT_OID)" type="int"  
	 */
	public int getCrawls() {
		return this.crawls;
	}
	
	/**
	 * Set the number of previous crawls for the <code>Target</code>
	 * @param crawls the number of previous crawls
	 */
	public void setCrawls(int crawls) {
		this.crawls = crawls;
	}
	
	/**
	 * @return The oid of the <code>TargetInstance</code> that has been denoted as this <code>Target</code>s a reference crawl, null otherwise
	 * @hibernate.property column="AT_REFERENCE_CRAWL_OID"
	 */
	public Long getReferenceCrawlOid() {
		return this.referenceCrawlOid;
	}
	
	/**
	 * Denotes the specified <code>TargetInstance</code> as a reference crawl
	 * @param targetInstanceOid the oid of the <code>TargetInstance</code> to denote as the reference crawl
	 */
	public void setReferenceCrawlOid(Long targetInstanceOid) {
		this.referenceCrawlOid = targetInstanceOid;
	}
	
	/**
	 * @return true if new <code>TargetInstance</code>s should be autopruned, false otherwise
	 * @hibernate.property column="AT_AUTO_PRUNE"
	 */
	public Boolean isAutoPrune() {
		return this.autoPrune;
	}
	
	/**
	 * Sets the auto prune state for this <code>Target</code>
	 * @param autoPrune the state to set
	 */
	public void setAutoPrune(Boolean autoPrune) {
		this.autoPrune = autoPrune;
	}
	
	/**
	 * @return true if new <code>TargetInstance</code>s should be automatically denoted as a reference crawl, false otherwise
	 * @hibernate.property column="AT_AUTO_DENOTE_REFERENCE_CRAWL"
	 */
	public Boolean isAutoDenoteReferenceCrawl() {
		return this.autoDenoteReferenceCrawl;
	}
	
	/**
	 * Sets the auto denote reference crawl state for this <code>Target</code>
	 * @param autoDenoteReferenceCrawl the state to set
	 */
	public void setAutoDenoteReferenceCrawl(Boolean autoDenoteReferenceCrawl) {
		this.autoDenoteReferenceCrawl = autoDenoteReferenceCrawl;
	}

    /**
     * Returns the descriptive request for the Archivists.
     * @return Returns the request.
     * @hibernate.property column="AT_REQUEST_TO_ARCHIVISTS" length="4000"
     */
	public String getRequestToArchivists() {
		return requestToArchivists;
	}


	/**
	 * @param requestToArchivists the requestToArchivists to set
	 */
	public void setRequestToArchivists(String requestToArchivists) {
		this.requestToArchivists = requestToArchivists;
	}
}
