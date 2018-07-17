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
package org.webcurator.core.permissionmapping;

import java.util.Date;
import org.webcurator.core.util.Utils;

import javax.persistence.*;


/**
 * The MappingView class records mappings between UrlPatterns and 
 * Permissions based on effective base domains. The goal is to 
 * provide fast lookups for the HierarchicalPermissionMappingStrategy.
 * 
 * @author bbeaumont
 */
@Entity
@Table(name = "URL_PERMISSION_MAPPING_VIEW")
@NamedQueries({@NamedQuery(name = "org.webcurator.core.permissionmapping.MappingView.LIST", query = "from MappingView where domain=?")})
public class MappingView {
	/** Query identifier for listing Mappings by domain */
	public static final String QUERY_BY_DOMAIN = "org.webcurator.core.permissionmapping.MappingView.LIST";
	
	/** The oid of the mapping view record. */
	@Id
	@Column(name = "UPM_OID", nullable = false)
	@GeneratedValue(generator = "mappingViewGen", strategy = GenerationType.TABLE)
	@TableGenerator(name = "mappingViewGen", table = "ID_GENERATOR", pkColumnName = "IG_TYPE",
			valueColumnName = "IG_VALUE", pkColumnValue = "General")
	private Long oid;

	/** The UrlPattern */
	@Column(name = "UP_PATTERN", length = 2048)
	private String urlPattern;
	/** The Permission end date*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PE_END_DATE")
	private Date endDate;
	/** The calculated base domain */
	@Column(name = "UPM_DOMAIN", length = 1024)
	private String domain;
	/** The database id of the owning agency. */
	@Column(name = "PE_OWNING_AGENCY_ID")
	private Long owningAgencyId;
	/** The permission id of the permission. */
	@Column(name = "PE_OID")
	private Long permissionOId;
    /** Site is active flag. */
	@Column(name = "ST_ACTIVE", nullable = false)
    private boolean siteActive = true;
	
	
	
	/**
	 * Private constructor for Hibernate.
	 */
	//private MappingView() {}
	
	protected MappingView() { }

	/**
	 * Standard constructor for WCT usage.
	 * @param aUrlPattern The UrlPattern.
	 * @param aPermission The CutdownPermission.
	public MappingView(UrlPattern aUrlPattern, CutdownPermission aPermission) {
		urlPattern = aUrlPattern;
		permission = aPermission;
		
		domain = HierarchicalPermissionMappingStrategy.calculateDomain(aUrlPattern.getPattern());
	}
	 */

    /**
     * Get the OID of the MappingView record.
     * @return Returns the oid.
     */
    public Long getOid() {
        return oid;
    }

    /**
     * Hibernate method to set the OID.
     * @param aOid The oid to set.
     */
    public void setOid(Long aOid) {
        this.oid = aOid;
    }	
	
	/**
	 * Return the date that the permission ends.
	 * @return Returns the endDate.
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
	public boolean isActiveNowOrInFuture() {
		Date now = new Date();
		return endDate == null || now.compareTo(endDate) <= 0;
	}	

    
	/**
	 * Returns the UrlPattern
	 * @return Returns the urlPattern.
	 */
	public String getUrlPattern() {
		return urlPattern;
	}

	/**
	 * Sets the UrlPattern. Private as this should only be called 
	 * from Hibernate.
	 * @param urlPattern The urlPattern to set. 
	 */
	@SuppressWarnings("unused")
	private void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	/**
	 * Returns the owningAgencyId
	 * @return Returns the owningAgencyId.
	 */
    public Long getOwningAgencyId() {
        return owningAgencyId;
    }

    /**
     * Hibernate method to set the owningAgencyId.
     * @param owningAgencyId The owningAgencyId to set.
     */
    public void setOwningAgencyId(Long owningAgencyId) {
        this.owningAgencyId = owningAgencyId;
    }	

	/**
	 * Returns the permission's OId
	 * @return Returns the permission's OId.
	 */
    public Long getPermissionOId() {
        return permissionOId;
    }

    /**
     * Hibernate method to set the owningAgencyId.
     * @param owningAgencyId The owningAgencyId to set.
     */
    public void setPermissionOId(Long permissionOId) {
        this.permissionOId = permissionOId;
    }	


    /**
     * Get whether the permissions site is active.
     * @return true if active; otherwise false.
     */
    public boolean isSiteActive() {
        return siteActive;
    }
    
    /**
     * Sets whether this site is active or not.
     * @param active true to set the site active; false to make it inactive.
     */
    public void setSiteActive(boolean siteActive) {
        this.siteActive = siteActive;
    }

    
	/**
	 * Gets the effective base domain.
	 * @return Returns the effective base domain.
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * Sets the effective base domain. Private as this should only
	 * be called from Hibernate.
	 * @param domain The domain to set.
	 */
	@SuppressWarnings("unused")
	private void setDomain(String domain) {
		this.domain = domain;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	//@Override
	//public int hashCode() {
	//	final int PRIME = 31;
	//	int result = 1;
	//	result = PRIME * result + ((permission == null) ? 0 : permission.getOid().hashCode());
	//	result = PRIME * result + ((urlPattern == null) ? 0 : urlPattern.getOid().hashCode());
	//	return result;
	//}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	//@Override
	/*
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Mapping other = (Mapping) obj;
		if (permission == null) {
			if (other.permission != null)
				return false;
		} else if (!permission.getOid().equals(other.permission.getOid()))
			return false;
		if (urlPattern == null) {
			if (other.urlPattern != null)
				return false;
		} else if (!urlPattern.getOid().equals(other.urlPattern.getOid()))
			return false;
		return true;
	}
	*/	
}
