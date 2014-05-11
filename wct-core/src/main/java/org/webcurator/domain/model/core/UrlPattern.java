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

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a pattern that can identify a site, or portion of a site, to 
 * which a Permission record applies.
 * 
 * @hibernate.class table="URL_PATTERN" lazy="false"
 */
public class UrlPattern extends AbstractIdentityObject {
	/** The database oid of the UrlPattern. */
	private Long oid;
	/** The url pattern. */
	private String pattern;
	/** A reference to the owning site. */
	private Site site;
	/** A set of permissions. */
	private Set<Permission> permissions = new HashSet<Permission>();
	
	/**
	 * Comparator of used to sort UrlPattern objects.
	 * @author bbeaumont
	 */
	public static class UrlComparator implements Comparator<UrlPattern> {
		/**
		 * Compares two UrlPatterns.
		 * @see Comparator#compare(Object, Object)
		 */
		public int compare(UrlPattern o1, UrlPattern o2) {
			return o1.pattern.compareToIgnoreCase(o2.pattern);
		}
	}
	
	/**
	 * Protected constructor to ensure that instances are created through the
	 * BusinessObjectFactory.
	 *
	 */
	protected UrlPattern() {}

	/**
	 * Get the OID of the UrlPattern.
     * @hibernate.id column="UP_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="General"  
	 */
	public Long getOid() {
		return oid;
	}

	/**
	 * Sets the OID of the UrlPattern.
	 * @param oid
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}	
	
	/**
	 * Gets the url pattern.
	 * @return The pattern.
     * @hibernate.property column="UP_PATTERN" length="2048"
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Sets the pattern.
	 * @param url The pattern.
	 */
	public void setPattern(String url) {
		this.pattern = url;
	}


	
	/**
	 * Get the site that owns this UrlPattern object.
     * @return Returns the site.
     * @hibernate.many-to-one column="UP_SITE_ID" foreign-key="FK_UP_SITE_ID"
     */
    public Site getSite() {
        return site;
    }
    
    /**
     * Set the site that owns this UrlPattern object.
     * @param aSite The site to set.
     */
    public void setSite(Site aSite) {
        this.site = aSite;
    }	
	
	/**
	 * Two UrlPattern objects are equivalent if their patterns are the 
	 * same.
	 * @param o The Object to compare with.
	 */
	public boolean equals(Object o) {
		if( o == null || !(o instanceof UrlPattern)) { 
			return false; 
		}
		else {
			UrlPattern url2 = (UrlPattern) o;
			
			return pattern == null && url2.getPattern() == null ||
			       pattern != null && pattern.equals(url2.getPattern());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return pattern == null ? 0 : pattern.hashCode();
	}

	/**
	 * Get the set of permissions associated with this UrlPattern.
	 * @return Returns the permissions.
	 * 
	 * @hibernate.set table="PERMISSION_URLPATTERN" cascade="save-update"
     * @hibernate.collection-key column="PU_URLPATTERN_ID"
     * @hibernate.collection-many-to-many class="org.webcurator.domain.model.core.Permission" column="PU_PERMISSION_ID" foreign-key="PU_FK_2"
 	 */
	public Set<Permission> getPermissions() {
		return permissions;
	}

	
	/**
	 * Set the permissions associated with this site.
	 * @param permissions The permissions to set.
	 */
	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}
}
