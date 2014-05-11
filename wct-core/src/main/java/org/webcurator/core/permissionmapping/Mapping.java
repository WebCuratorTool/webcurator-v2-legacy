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

import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.UrlPattern;


/**
 * The Mapping class records mappings between UrlPatterns and 
 * Permissions based on effective base domains. The goal is to 
 * provide fast lookups for the HierarchicalPermissionMappingStrategy.
 * 
 * @author bbeaumont
 * @hibernate.class table="URL_PERMISSION_MAPPING" lazy="true"
 * @hibernate.query name="org.webcurator.core.permissionmapping.Mapping.LIST" query="from Mapping where domain=?"
 * @hibernate.query name="org.webcurator.core.permissionmapping.Mapping.FETCH" query="from Mapping where oid=?"
 * @hibernate.query name="org.webcurator.core.permissionmapping.Mapping.DELETE" query="delete from Mapping where urlPattern.oid = :urlPatternOid and permission.oid = :permissionOid"
 * @hibernate.query name="org.webcurator.core.permissionmapping.Mapping.DELETE_BY_SITE" query="delete from Mapping where permission.site.oid = :siteOid"
 */
public class Mapping {
	/** Query identifier for fetching Mapping by oid */
	public static final String QUERY_BY_OID = "org.webcurator.core.permissionmapping.Mapping.FETCH";
	/** Query identifier for listing Mappings by domain */
	public static final String QUERY_BY_DOMAIN = "org.webcurator.core.permissionmapping.Mapping.LIST";
	/** Query identifier for deleting mappings for a given URL Pattern and Permission  */
	public static final String DELETE = "org.webcurator.core.permissionmapping.Mapping.DELETE";
	/** Query identifier for deleting all mappings related to the specified site */
	public static final String QUIERY_DELETE_BY_SITE = "org.webcurator.core.permissionmapping.Mapping.DELETE_BY_SITE";
	
	/** The UrlPattern */
	private UrlPattern urlPattern;
	/** The Permission */
	private Permission permission;
	/** The calculate base domain */
	private String domain;
	/** The Oid */
	private Long oid;

	/**
	 * Private constructor for Hibernate.
	 */
	private Mapping() {}

	/**
	 * Standard constructor for WCT usage.
	 * @param aUrlPattern The UrlPattern.
	 * @param aPermission The Permission.
	 */
	public Mapping(UrlPattern aUrlPattern, Permission aPermission) {
		urlPattern = aUrlPattern;
		permission = aPermission;
		
		domain = HierarchicalPermissionMappingStrategy.calculateDomain(aUrlPattern.getPattern());
	}
	
    /**
     * Get the OID of the Mapping.
     * @return Returns the oid.
     * @hibernate.id column="UPM_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="General" 
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
	 * Returns the permission.
	 * @return Returns the permission.
	 * @hibernate.many-to-one column="UPM_PERMISSION_ID" foreign-key="FK_UPM_PERMISSION_ID" cascade="none"
	 */
	public Permission getPermission() {
		return permission;
	}

	/**
	 * Sets the permission. Private as this should only
	 * be called from Hibernate.
	 * @param permission The permission to set. 
	 */
	@SuppressWarnings("unused")
	private void setPermission(Permission permission) {
		this.permission = permission;
	}

	/**
	 * Returns the UrlPattern
	 * @return Returns the urlPattern.
	 * @hibernate.many-to-one column="UPM_URL_PATTERN_ID" foreign-key="FK_UPM_URL_PATTERN_ID" cascade="none"
	 */
	public UrlPattern getUrlPattern() {
		return urlPattern;
	}

	/**
	 * Sets the UrlPattern. Private as this should only be called 
	 * from Hibernate.
	 * @param urlPattern The urlPattern to set. 
	 */
	@SuppressWarnings("unused")
	private void setUrlPattern(UrlPattern urlPattern) {
		this.urlPattern = urlPattern;
	}



	/**
	 * Gets the effective base domain.
	 * @return Returns the effective base domain.
	 * @hibernate.property column="UPM_DOMAIN" length="1024"
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
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((permission == null) ? 0 : permission.getOid().hashCode());
		result = PRIME * result + ((urlPattern == null) ? 0 : urlPattern.getOid().hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
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
}
