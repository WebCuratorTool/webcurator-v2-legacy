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

import org.hibernate.annotations.Cascade;
import org.webcurator.domain.AgencyOwnable;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Role;
import org.webcurator.domain.model.auth.User;

import javax.persistence.*;

/**
 * Represents a Site object. A site is a collection of related URL patterns
 * and maintains a record of what agencies have or have not given permission
 * to harvest the site.
 *
 * Note that Sites were renamed to HarvestAuthorisations midway through this
 * project. They are thus called in the User Interface but keep the "Site"
 * names throughout the code-base.
 *
 */
@Entity
@Table(name = "SITE")
public class Site extends AbstractIdentityObject implements AgencyOwnable {
    /** The database oid of the site */
    @Id
    @Column(name = "ST_OID", nullable = false)
    @GeneratedValue(generator = "siteGen", strategy = GenerationType.TABLE)
    @TableGenerator(name = "siteGen", table = "ID_GENERATOR", pkColumnName = "IG_TYPE",
            valueColumnName = "IG_VALUE", pkColumnValue = "General")
    private Long oid;
    /** The title of the site */
    @Column(name = "ST_TITLE", unique = true, length = 255, nullable = false)
    private String title;
    /** A description of the site */
    @Column(name = "ST_DESC", length = 4000, columnDefinition = "text")
    private String description;
    /** A set of notes about the site. */
    @Column(name = "ST_NOTES", length = 4000, columnDefinition = "text")
    private String notes;
    /** A library order no. */
    @Column(name = "ST_LIBRARY_ORDER_NO", length = 32)
    private String libraryOrderNo;
    /** Whether the site has been published or not. */
    @Column(name = "ST_PUBLISHED", nullable = false)
    private boolean published;
    /** Site is active flag. */
    @Column(name = "ST_ACTIVE", nullable = false)
    private boolean active = true;
    /** The date the Site was created */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ST_CREATION_DATE")
    private Date creationDate;
    /** The set of authorising agents (those who must provide permission to
     * harvest the site.
     */
    @ManyToMany(targetEntity = AuthorisingAgent.class)
    @JoinTable(name = "SITE_AUTH_AGENCY", joinColumns = {@JoinColumn(name = "SA_SITE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "SA_AGENT_ID")}, inverseForeignKey = @ForeignKey(name = "FK_SA_AGENT_ID"))
    private Set<AuthorisingAgent> authorisingAgents = new HashSet<AuthorisingAgent>();
    /** A set of URL patterns that are encompassed by this site. */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "site", targetEntity = UrlPattern.class)
    private Set<UrlPattern> urlPatterns = new HashSet<UrlPattern>();
    /** A set of permissions that have been requested, granted, or refused by
     * the authorising agents.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "site", targetEntity = Permission.class)
    private Set<Permission> permissions = new HashSet<Permission>();
    /**
     * A set of permissions that have been removed.
     */
    @Transient
    private Set<Permission> removedPermissions = new HashSet<Permission>();
    
    /** The owning agency */
    @ManyToOne(targetEntity = Agency.class)
    @JoinColumn(name = "ST_OWNING_AGENCY_ID")
    private Agency owningAgency = null;
    
    
    /**
     * Get the database OID of the site.
     * @return Returns the oid.
     */
    public Long getOid() {
        return oid;
    }
    
    /**
     * Sets the database oid of the site object.
     * @param oid
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }
    
    /**
     * Get the title of the site.
     * @return Returns the name of the site.
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the title of the site.
     * @param title The title of the site.
     */
    public void setTitle(String title) {
        this.title = title;
        if (this.title != null) {
            this.title = this.title.trim();
        }
    }
    
    /**
     * Get the description of the site.
     * @return Returns the description of the site.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description of the site.
     * @param description The description.
     */
    public void setDescription(String description) {
        this.description = description;
        if (this.description != null) {
            this.description = this.description.trim();
        }
    }
    
    /**
     * Returns the library order number for the site.
     * @return The library order number.
     */
    public String getLibraryOrderNo() {
        return libraryOrderNo;
    }
    
    /**
     * Sets the library number for the site.
     * @param libraryOrderNo The library order number for the site.
     */
    public void setLibraryOrderNo(String libraryOrderNo) {
        this.libraryOrderNo = libraryOrderNo;
        if (this.libraryOrderNo != null) {
            this.libraryOrderNo = this.libraryOrderNo.trim();
        }
    }
    
    /**
     * Gets the notes for the site.
     * @return The notes for the site.
     */
    public String getNotes() {
        return notes;
    }
    
    /**
     * Sets the notes for the site.
     * @param notes The notes for the site.
     */
    public void setNotes(String notes) {
        this.notes = notes;
        if (this.notes != null) {
            this.notes = this.notes.trim();
        }
    }
    
    /**
     * Checks if the site is published.
     * @return true if the site is published; otherwise false.
     */
    public boolean isPublished() {
        return published;
    }
    
    /**
     * Sets if the site is published.
     * @param published
     */
    public void setPublished(boolean published) {
        this.published = published;
    }
 
	/**
	 * Get the date that the Site was created.
	 * @return Returns the creation date.
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
     * Returns the associated permissions.
     * @return The associated permissions.
     */
    public Set<Permission> getPermissions() {
        return permissions;
    }
    
    /**
     * Sets the permissions on the site.
     * @param permissions The permissions to set.
     */
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
    
    /**
     * Add a permission to the site.
     * @param aPermission The permission to add.
     */
    public void addPermission(Permission aPermission) {
        this.permissions.add(aPermission);
    }
    
    /**
     * Remove a permission from the site.
     * @param aPermission The permission to be removed.
     */
    public void removePermission(Permission aPermission) {
        this.permissions.remove(aPermission);
        
        // Disassociate permissions and urls.
        Iterator<UrlPattern> it = aPermission.getUrls().iterator();
        UrlPattern up = null;
        while(it.hasNext()) {
            up = it.next();
            up.getPermissions().remove(aPermission);
            it.remove();
        }
        
        removedPermissions.add(aPermission);
    }
    
    /**
     * Gets the set of authorising agents.
     */
    public Set<AuthorisingAgent> getAuthorisingAgents() {
        return authorisingAgents;
    }
    
    /**
     * Sets the set of authorising agents.
     * @param authorisingAgents The new list of authorising agents.
     */
    public void setAuthorisingAgents(Set<AuthorisingAgent> authorisingAgents) {
        this.authorisingAgents = authorisingAgents;
    }
    
    /**
     * Gets the set of UrlPatterns related to this site. This set should
     * be treated as read only. To add or remove URL patterns, use the
     * addUrlPattern/removeUrlPattern methods.
     * @return The set of URL Patterns associated with this site.
     */
    public Set<UrlPattern> getUrlPatterns() {
        return urlPatterns;
    }
    
    /**
     * Set the UrlPatterns that belong to the site.
     * @param urls The Set of UrlPattern object to set.
     */
    public void setUrlPatterns(Set<UrlPattern> urls) {
        this.urlPatterns = urls;
    }
    
    /**
     * Adds a url pattern to this site.
     * @param url The UrlPattern to add.
     */
    public void addUrlPattern(UrlPattern url) {
        urlPatterns.add(url);
        url.setSite(this);
    }
    /**
     * Remove UrlPattern from the site.
     * @param url The UrlPattern to remove.
     */
    public void removeUrlPattern(UrlPattern url) {
        urlPatterns.remove(url);
        url.setSite(null);
    }

    /**
     * Get whether this site is active. Sites that are not active can be filtered
     * out of searches.
     * @return true if active; otherwise false.
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets whether this site is active or not.
     * @param active true to set the site active; false to make it inactive.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Return the set of permissions that have been removed from this
     * permission object since it was loaded from the database. This is a
     * management function so that the SiteManager can control the saving
     * of permissions.
     *
     * @return Returns the removedPermissions.
     */
    public Set<Permission> getRemovedPermissions() {
        return removedPermissions;
    }
    
	/**
	 * Returns the agency to which this site belongs.
	 * @return Returns the owner.
	 */
	public Agency getOwningAgency() {
		return owningAgency;
	}	
	
	/**
	 * Sets the agency that owns this site.
	 * @param anAgency The agency that owns this site.
	 */
	public void setOwningAgency(Agency anAgency) {
		this.owningAgency = anAgency;
	}

    
}
