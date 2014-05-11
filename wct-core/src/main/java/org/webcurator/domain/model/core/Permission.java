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

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.webcurator.core.notification.AgencyInTrayResource;
import org.webcurator.core.notification.InTrayResource;
import org.webcurator.core.util.Utils;
import org.webcurator.domain.AgencyOwnable;
import org.webcurator.domain.model.auth.Agency;

/**
 * Represents a Permission.
 * 
 * @hibernate.class table="PERMISSION" lazy="false"
 */
public class Permission extends AbstractIdentityObject implements Annotatable, AgencyOwnable, InTrayResource, AgencyInTrayResource {
	/** Maximum length for the File Reference */
	public static int MAX_FILE_REF_LENGTH = 255;
	
	/** Status constant for a permission that has not yet been sent to the 
	 * authorising agency.
	 */
	public static int STATUS_PENDING = 0;
	
	/** Status constant for a permission that has been set to an agency, but 
	 * for which no response has yet been received. */
	public static int STATUS_REQUESTED = 1;
	
	/** Status constant for a permission that has been approved by the agency. */
	public static int STATUS_APPROVED = 2;
	
	/** Status constant for a permission that has been denied by the agency. */
	public static int STATUS_DENIED = 3;
	
	/** Status constant for a permission that is approved for a period in the future. */
	public static int EXT_STATUS_APPROVED_FUTURE = 4;
	
	/** Status constant for a permission that is approved for a period in the past. */
	public static int EXT_STATUS_APPROVED_EXPIRED = 5;
	
	
	/** The database id of the permission. */
	private Long oid;
	/** The agent granting the permission. */
	private AuthorisingAgent authorisingAgent;
	/** The set of UrlPatterns covered by this permission. */
	private Set<UrlPattern> urls;
	/** The date this permission starts. */
	private Date startDate;
	/** The date this permission ends. */
	private Date endDate;
	/** Whether this permission is approved. */
	//TODO Check what this means.
	private boolean approved;
	/** The status of the permission. One of the STATUS_ constants */
	private int status;
	/** Any authorising agency acceptance notes attached to the permission. */
	private String authResponse;
	/** The access status */
	private String accessStatus;
	/** The date at which this permission will be open access */
	private Date openAccessDate;
	/** Whether this permission is publicly available */
	private boolean availableFlag;
	/** Any special requirements attached to this permission. */
	private String specialRequirements;
	/** The creation date of this permission. */
	private Date creationDate;
	/** The copyright URL to use during the access component. */
	private String copyrightUrl;
	/** The copyright statement to display in the access system. */
	private String copyrightStatement;
	/** The date that a permission requested was sent to the authorising agent. */
	private Date permissionSentDate;
	/** The date that permission was granted/refused. */
	private Date permissionGrantedDate;
	/** The site that this permission belongs to. */
	private Site site;
	/** Whether the permission is marked as a quick pick. */
	private boolean quickPick;
	/** Quick Pick Display Name */
	private String displayName;
	/** The agency that owns this permission. */
	private Agency owningAgency;
	/** A file reference */
	private String fileReference;
	/** A dirty flag to track if this permission has been updated. This is 
	 * managed by the SiteController, not self-managed. */
	private boolean dirty = false;
	/** 
	 * The initial state of the permission. This is initialised when setStatus 
	 * is called for the first time, which will either be by Hibernate or the
	 * BusinessObjectFactory. 
	 */
	private int originalStatus = -1;
	/** Flag to determine if a "Seek Permission" task should be created. */
	private boolean createSeekPermissionTask = false;
	/** List of excluded URLs */
	private List<PermissionExclusion> exclusions = new LinkedList<PermissionExclusion>();
	

    /** The list of annotations. */
    private List<Annotation> annotations = new LinkedList<Annotation>();
    /** The list of deleted annotations. */
    private List<Annotation> deletedAnnotations = new LinkedList<Annotation>();
    /** True if the annotations have been loaded */
    private boolean annotationsSet = false;
    /** Flag to state if the annotations have been sorted */
    private boolean annotationsSorted = false;    

	
	/**
	 * Create a new Permission. This method is protected as Permission objects
	 * should be created through the <code>BusinessObjectFactory</code>
	 */
	protected Permission() {
		urls = new HashSet<UrlPattern>();
	}
	
	
	/**
	 * Get the database OID of this object.
	 * @return Returns the oid.
     * @hibernate.id column="PE_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="General"  
	 */
	public Long getOid() {
		return oid;
	}
	
	/**
	 * Set the database OID of this object.
	 * @param oid The oid to set.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}	
	

	/**
	 * Get the string that describes the general access status for this permission.
	 * @return Returns the accessStatus.
     * @hibernate.property column="PE_ACCESS_STATUS" length="255"
	 */
	public String getAccessStatus() {
		return accessStatus;
	}
	/**
	 * Set the string that describes the general access status for this permission.
	 * @param accessStatus The accessStatus to set.
	 */
	public void setAccessStatus(String accessStatus) {
		this.accessStatus = accessStatus;
	}
	
	/**
	 * Returns whether this permission is approved.
	 * @return true if approved; otherwise false.
     * @hibernate.property column="PE_APPROVED_YN"  
	 */
	public boolean isApproved() {
		return approved;
	}
	
	/**
	 * Sets if this permission is approved.
	 * @param approved true to mark as approved; false to mark as non-approved.
	 */
	public void setApproved(boolean approved) {
		this.approved = approved;
	}
	
	/**
	 * Returns true if the site is available.
	 * @return true if available; otherwise false.
     * @hibernate.property column="PE_AVAILABLE_YN" 
	 */
	public boolean isAvailableFlag() {
		return availableFlag;
	}
	
	/**
	 * Sets whether this site is available.
	 * @param availabilityFlag true for available; false for not available.
	 */
	public void setAvailableFlag(boolean availabilityFlag) {
		this.availableFlag = availabilityFlag;
	}
	
	/**
	 * Retrieves the copyright statement that should be used in the access
	 * layer.
	 * @return Returns the copyrightStatement.
     * @hibernate.property column="PE_COPYRIGHT_STATEMENT" length="2048" 
	 */
	public String getCopyrightStatement() {
		return copyrightStatement;
	}
	
	/**
	 * Sets the copyright statement that should be used on the access layer.
	 * @param copyrightStatement The copyrightStatement to set.
	 */
	public void setCopyrightStatement(String copyrightStatement) {
		this.copyrightStatement = copyrightStatement;
	}
	
	/**
	 * Returns the URL that the access layer should point to for the full
	 * copyright statement.
	 * @return Returns the copyrightUrl.
     * @hibernate.property column="PE_COPYRIGHT_URL" length="2048" 
	 */
	public String getCopyrightUrl() {
		return copyrightUrl;
	}
	
	/**
	 * Sets the URL that the access layer should point to for the full
	 * copyright statement.
	 * @param copyrightUrl The copyrightUrl to set.
	 */
	public void setCopyrightUrl(String copyrightUrl) {
		this.copyrightUrl = copyrightUrl;
	}
	
	/**
	 * Gets the creation date of this permission record.
	 * @return Returns the creationDate.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="PE_CREATION_DATE" sql-type="TIMESTAMP(9)"   
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	
	/**
	 * Set the creation date of the harvest result.
	 * @param creationDate The creationDate to set.
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	/**
	 * Return the date that the permission ends.
	 * @return Returns the endDate.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="PE_END_DATE" sql-type="TIMESTAMP(9)"  
	 */
	public Date getEndDate() {
		return endDate;
	}
	
	/**
	 * Set the end date of the permission. The code ensures that the date is
	 * set to the end of the given day.
	 * @param anEndDate The endDate to set.
	 */
	public void setEndDate(Date anEndDate) {
		endDate = anEndDate == null ? null : Utils.endOfDay(anEndDate);
	}
	
	/**
	 * Checks if the permission is currently active by checking the start and
	 * end dates.
	 * @return true if the permission is active; otherwise false.
	 */
	public boolean isActive() {
		Date now = new Date();
		return now.compareTo(startDate) >= 0 && 
		       (endDate == null || now.compareTo(endDate) <= 0);
	}
	
	/**
	 * Checks if the permission is currently active by checking the start and
	 * end dates.
	 * @return true if the permission is active; otherwise false.
	 */
	public boolean isActiveNowOrInFuture() {
		Date now = new Date();
		return endDate == null || now.compareTo(endDate) <= 0;
	}	
	
	/**
	 * Returns the authorising agency's response notes associated with approving the permission.
	 * @return Returns the authResponse.
     * @hibernate.property column="PE_NOTES" type="text" 
	 */
	public String getAuthResponse() {
		return authResponse;
	}
	/**
	 * Sets the authResponse on the permission.
	 * @param authResponse The authResponse to set.
	 */
	public void setAuthResponse(String authResponse) {
		this.authResponse = authResponse;
	}

	/**
	 * Get the date when the permission access becomes open.
	 * @return Returns the openAccessDate.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="PE_OPEN_ACCESS_DATE" sql-type="TIMESTAMP(9)"  
     */
	public Date getOpenAccessDate() {
		return openAccessDate;
	}
	/**
	 * Sets the open access date.
	 * @param openAccessDate The openAccessDate to set.
	 */
	public void setOpenAccessDate(Date openAccessDate) {
		this.openAccessDate = openAccessDate == null ? null : Utils.clearTime(openAccessDate);
	}
	/**
	 * Get the date the permission was granted.
	 * @return Returns the permissionGrantedDate.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="PE_PERMISSION_GRANTED_DATE" sql-type="TIMESTAMP(9)"  
	 */
	public Date getPermissionGrantedDate() {
		return permissionGrantedDate;
	}
	
	/**
	 * Set the date the permission was granted.
	 * @param permissionGrantedDate The permissionGrantedDate to set.
	 */
	public void setPermissionGrantedDate(Date permissionGrantedDate) {
		this.permissionGrantedDate = permissionGrantedDate;
	}
	
	/**
	 * Return the date the permission was sent.
	 * @return Returns the permissionSentDate.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="PE_PERMISSION_REQUESTED_DATE" sql-type="TIMESTAMP(9)"  
	 */
	public Date getPermissionSentDate() {
		return permissionSentDate;
	}
	/**
	 * Set the date the permission was sent.
	 * @param permissionSentDate The permissionSentDate to set.
	 */
	public void setPermissionSentDate(Date permissionSentDate) {
		this.permissionSentDate = permissionSentDate;
	}
	/**
	 * Get the free-text special requirements associated with the permission.
	 * @return Returns the specialRequirements.
     * @hibernate.property column="PE_SPECIAL_REQUIREMENTS" length="2048" 
	 */
	public String getSpecialRequirements() {
		return specialRequirements;
	}
	
	/**
	 * Set the special requirements for the permission.
	 * @param specialRequirements The specialRequirements to set.
	 */
	public void setSpecialRequirements(String specialRequirements) {
		this.specialRequirements = specialRequirements;
	}
	
	/**
	 * Check whether special requirements 
	 * @return true if there are special requirements; otherwise false.
	 */
	public boolean isRestricted() {
		return specialRequirements != null && !"".equals(specialRequirements.trim());
	}
	
	
	/**
	 * Return the state date of the permission.
	 * @return Returns the startDate.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="PE_START_DATE" sql-type="TIMESTAMP(9)" 
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * Set the start date of the permission.
	 * @param aStartDate The startDate to set.
	 */
	public void setStartDate(Date aStartDate) {
		startDate = aStartDate == null ? null : Utils.clearTime(aStartDate);
	}
	
	/**
	 * Get the status of the permission.
	 * @return Returns the status.
     * @hibernate.property column="PE_STATUS"  
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * Get the current status of the permission, checking whether approved
	 * permissions are approved in the current time period or not.
	 * @return Returns the status.
	 */
	public int getCurrentStatus() {
		if(status == STATUS_APPROVED) {
			Date now = new Date();
			
			if(startDate.after(now)) {
				return EXT_STATUS_APPROVED_FUTURE;
			}
			else if(endDate != null && endDate.before(now)) {
				return EXT_STATUS_APPROVED_EXPIRED;
			}
			else {
				return STATUS_APPROVED;
			}
		}
		else {
			return status;
		}
	}	
	
	/**
	 * Set the status of the permission.
	 * @param status The status to set.
	 */
	public void setStatus(int status) {
		if(originalStatus == -1) { originalStatus = status; }
		this.status = status;
	}
	
	/**
	 * Get the authorising agent that has approved (or is to approve) this 
	 * permission.
	 * @return Returns the authorisingAgent.
	 * @hibernate.many-to-one column="PE_AUTH_AGENT_ID" 
	 */
	public AuthorisingAgent getAuthorisingAgent() {
		return authorisingAgent;
	}
	
	/**
	 * Set the authorising agent that has approved (or is to approve) this 
	 * permission. 
	 * @param authorisingAgent The authorisingAgent to set.
	 */
	public void setAuthorisingAgent(AuthorisingAgent authorisingAgent) {
		this.authorisingAgent = authorisingAgent;
	}
	
	/**
	 * Return the Set of URL Patterns that this permission applies to.
	 * @return The Set of URL Patterns that this permission applies to.
	 * 
	 * @hibernate.set table="PERMISSION_URLPATTERN" cascade="save-update" inverse="true"
     * @hibernate.collection-key column="PU_PERMISSION_ID"
     * @hibernate.collection-many-to-many class="org.webcurator.domain.model.core.UrlPattern" column="PU_URLPATTERN_ID" foreign-key="PU_FK_1"
	 */
	public Set<UrlPattern> getUrls() {
		return urls;
	}
	
	/**
	 * Set the set of UrlPatterns that this permission applies to.
	 * @param urls The set of <code>UrlPattern</code>s that this permission applies to.
	 */
	public void setUrls(Set<UrlPattern> urls) {
		this.urls = urls;
	}	
	
	/**
	 * Gets the site that owns this permission.
     * @return Returns the site.
     * @hibernate.many-to-one column="PE_SITE_ID" foreign-key="FK_PE_SITE_ID"
     */
    public Site getSite() {
        return site;
    }
    
    /**
     * Sets the site that this permission is part of.
     * @param aSite The site that owns this permission.
     */
    public void setSite(Site aSite) {
        this.site = aSite;
    }	
	
	
	/*
	 * Annotatable Implementation
	 */
	
	/** @see Annotatable#addAnnotation(Annotation). */
	public void addAnnotation(Annotation annotation) {
		annotations.add(annotation);	
		annotationsSorted = false;
	}
	
	/** @see Annotatable#getAnnotation(int). */
	public Annotation getAnnotation(int index) {
		return annotations.get(index);	
	}
		
	/** @see Annotatable#deleteAnnotation(int). */
	public void deleteAnnotation(int index)
	{
		Annotation annotation = annotations.get(index);
		if(annotation != null)
		{
			deletedAnnotations.add(annotation);
			annotations.remove(index);
		}
	}
	
	/** @see Annotatable#getAnnotations(). */
	public List<Annotation> getAnnotations() {		
		return annotations;
	}
	
	/** @see Annotatable#getDeletedAnnotations(). */
	public List<Annotation> getDeletedAnnotations() {		
		return deletedAnnotations;
	}
	
	public void setAnnotations(List<Annotation> aAnnotations) {		
		annotations = aAnnotations;
		deletedAnnotations.clear();
		annotationsSet = true;
		annotationsSorted = false;
	}
	
	/**
	 * @return the annotationsSet
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
	 * Adjust the UrlPattern set to be the new set. This manages and tracks
	 * which items have been added and/or removed.
	 * @param targetSet The new set of UrlPatterns.
	 */
	public void adjustUrlPatternSet(Set<UrlPattern> targetSet) {
		// Create a temporary working set and initialise it to have the
		// same members as the target set.
		Set<UrlPattern> workingSet = new HashSet<UrlPattern>();
		workingSet.addAll(targetSet);
		
		
		// Iterate through all the items already being tracked to determine
		// if anything has been removed.
		Iterator<UrlPattern> iter = urls.iterator();
		while (iter.hasNext()) {
			UrlPattern pattern = iter.next();

			// If the item is in the working set, then the item has been 
			// retained. Take it out of the working set to mark that it has
			// been handled.
			if( workingSet.contains(pattern)) {
				workingSet.remove(pattern);
			}
			
			// If it is not in the temporary set, then the object has been 
			// removed from our session tracking list. Remove the item from
			// the tracking list. Then remove from the working set to 
			// indicate that it has been handled.
			else {
				iter.remove();
				workingSet.remove(pattern);
				pattern.getPermissions().remove(this);
			}
		}
		
		// Everything left in the temporary set is something that was in the
		// target set, but not in the tracked list. We need to add all of those
		// items into the tracked list.
		for(UrlPattern p : workingSet) {
			p.getPermissions().add(this);
			urls.add(p);
		}
	}	
	
	/**
	 * Checks whether this permission applies at the given date by checking that
	 * the date falls between the start and end dates.
	 * @param when The date to test.
	 * @return true if the permission applies to the given date; otherwise false.
	 */
	public boolean containsTime(Date when) {
		return (startDate == null || startDate.equals(when) || when.after(startDate)) 
				&& (endDate == null || endDate.equals(when) || when.before(endDate));
	}
		
	/**
	 * Checks if the permission applies to now by checking the start and end
	 * dates.
	 * @return true if the permission applies now; otherwise false.
	 */
	public boolean isCurrent() {
		return containsTime(new Date());
		
	}
	
	/**
	 * Checks if this permission has been set up as a quick pick. Quick Pick
	 * permissions are available in the drop-down of permissions to associate
	 * to a new seed.
	 * 
	 * @return true if this is a quick pick permission; otherwise false.
	 * @hibernate.property column="PE_QUICK_PICK"
	 */
	public boolean isQuickPick() {
		return quickPick;
	}


	/**
	 * Sets whether this should be a quick-pick permission.
	 * @param quickPick true to make this a quick-pick permissoin; otherwise false.
	 */
	public void setQuickPick(boolean quickPick) {
		this.quickPick = quickPick;
	}


	/**
	 * Returns the display name of the permission. The display name is used
	 * in the quick-pick drop-down for associating seeds with permissions.
	 * @return Returns the displayName.
	 * @hibernate.property column="PE_DISPLAY_NAME" length="32"
	 */
	public String getDisplayName() {
		return displayName;
	}


	/**
	 * Set the display name for this permission.
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	/**
	 * Returns the agency to which this permission belongs.
	 * @return Returns the owner.
	 * @hibernate.many-to-one column="PE_OWNING_AGENCY_ID"
	 */
	public Agency getOwningAgency() {
		return owningAgency;
	}	
	
	/**
	 * Sets the agency that owns this permission.
	 * @param anAgency The agency that owns this permission.
	 */
	public void setOwningAgency(Agency anAgency) {
		this.owningAgency = anAgency;
	}


	/**
	 * Checks if this permission object has been modified.
	 * @return true if modified; otherwise false.
	 */
	public boolean isDirty() {
		return dirty;
	}


	/**
	 * Sets whether this permission record has been changed.
	 * @param dirty The dirty to set.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	/**
	 * Checks if this permission has changed from its originally loaded state.
	 * @return true if the state has changed; otherwise false.
	 */
	public boolean hasChangedState() {
		return originalStatus != status;
	}


	/* (non-Javadoc)
	 * @see org.webcurator.core.notification.InTrayResource#getResourceName()
	 */
	public String getResourceName() {
		return "Permission for " + site.getTitle();
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.notification.InTrayResource#getResourceType()
	 */
	public String getResourceType() {
		return Permission.class.getName();
	}


	/**
	 * Tests whether the user has requested a "Seek Permission" task to be 
	 * created. 
	 * @return true if the task should be created; otherwise false.
	 */
	public boolean isCreateSeekPermissionTask() {
		return createSeekPermissionTask;
	}


	/**
	 * Sets whether the "Seek Permission" task should be created.
	 * @param createSeekPermissionTask true to create the task; otherwise false.
	 */
	public void setCreateSeekPermissionTask(boolean createSeekPermissionTask) {
		this.createSeekPermissionTask = createSeekPermissionTask;
	}


	/**
	 * @return Returns the exclusions.
	 * @hibernate.list cascade="all-delete-orphan" lazy="false"
	 * @hibernate.collection-key column="PEX_PERMISSION_OID" 
	 * @hibernate.collection-index column="PEX_INDEX"
	 * @hibernate.collection-one-to-many class="org.webcurator.domain.model.core.PermissionExclusion" 
	 */
	public List<PermissionExclusion> getExclusions() {
		return exclusions;
	}


	/**
	 * @param exclusions The exclusions to set.
	 */
	public void setExclusions(List<PermissionExclusion> exclusions) {
		this.exclusions = exclusions;
	}


	/**
	 * @return Returns the fileReference.
     * @hibernate.property column="PE_FILE_REFERENCE" length="255"
	 */
	public String getFileReference() {
		return fileReference;
	}


	/**
	 * @param fileReference The fileReference to set.
	 */
	public void setFileReference(String fileReference) {
		this.fileReference = fileReference;
	}
	

	
}
