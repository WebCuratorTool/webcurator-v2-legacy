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

import java.util.Iterator;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.profiles.DuplicateNameException;
import org.webcurator.core.profiles.HeritrixProfile;

import java.util.LinkedList;

/**
 * Represents a set of overrides that can be applied to a Heritrix profile.
 * Each overridable setting has a value and an on/off setting, allowing 
 * the overrides to override something in the default profile with an empty
 * setting.
 * 
 * @hibernate.class table="PROFILE_OVERRIDES" lazy="false"
 */
public class ProfileOverrides {
	/** The logger for this class */
	private static Log log = LogFactory.getLog(ProfileOverrides.class);

	/** The Heritrix x-path to the max-time-sec element */
	public static final String ELEM_MAX_TIME_SEC = "/crawl-order/max-time-sec";
	/** The Heritrix x-path to the max-bytes-download element */
	public static final String ELEM_MAX_BYTES_DOWNLOAD = "/crawl-order/max-bytes-download";
	/** The Heritrix x-path to the max-document-download element */
	public static final String ELEM_MAX_DOCUMENT_DOWNLOAD = "/crawl-order/max-document-download";
	/** The Heritrix x-path to the robots-honoring-policy element */
	public static final String ELEM_ROBOTS_HONOURING_POLICY = "/crawl-order/robots-honoring-policy/type";
	/** The Heritrix x-path to the max-links-hops element */
	public static final String ELEM_MAX_LINK_HOPS = "/crawl-order/scope/max-link-hops";
	/** The Heritrix x-path to the max-trans-hops element */
	public static final String ELEM_MAX_TRANSITIVE_HOPS = "/crawl-order/scope/max-trans-hops";
	/** The Heritrix x-path to the exclude-filter element */
	public static final String ELEM_SCOPE_EXCLUDE_FILTER = "/crawl-order/scope/exclude-filter/filters";
	/** The Heritrix x-path to the force-accept-filter element */
	public static final String ELEM_SCOPE_FORCE_ACCEPT_FILTER = "/crawl-order/scope/force-accept-filter/filters";
	/** The Heritrix x-path to the write-processor filters element */
	public static final String ELEM_WRITE_PROCESSORS_FILTER = "/crawl-order/write-processors/Archiver/filters";
	/** The Heritrix x-path to the http fetch exclude filters element */
	public static final String ELEM_FETCH_HTTP_EXCLUDE_FILTER = "/crawl-order/fetch-processors/HTTP/filters";
	/** The Heritrix x-path to the scope decide rules element */
	public static final String ELEM_SCOPE_DECIDE_RULES = "/crawl-order/scope/decide-rules/rules";
	/** The Heritrix x-path to the write-processor decide rules element */
	public static final String ELEM_WRITE_PROCESSORS_DECIDE_RULES = "/crawl-order/write-processors/Archiver/Archiver#decide-rules/rules";
	
	/** The unique database ID of the profile. */
	private Long oid;
	
	/** The robots honouring policy override */
	private String robotsHonouringPolicy = null;
	/** True to override the robots policy; otherwise false */
	private boolean overrideRobotsHonouringPolicy = false;
	
	/** The maximum seconds to run the harvest */
	private Long maxTimeSec = null;
	/** True to override the maximum seconds; otherwise false */
	private boolean overrideMaxTimeSec = false;
	
	/** The maximum numbers of bytes to download */
	private Long maxBytesDownload = null;
	/** True to override the maximum download size; otherwise false */
	private boolean overrideMaxBytesDownload = false;
	
	/** The maximum number of documents to harvest */
	private Long maxHarvestDocuments = null;
	/** True to override the maximum number of documents; otherwise false */
	private boolean overrideMaxHarvestDocuments = false;
	
	/** The maximum path depth to consider for the harvest */
	private Integer maxPathDepth = null;
	/** True to override the maximum path depth setting; otherwise false */
	private boolean overrideMaxPathDepth = false;
	
	/** The maximum hops to travel in the harvest */
	private Integer maxLinkHops = null;
	/** True of override the hops maximum; otherwise false */
	private boolean overrideMaxLinkHops = false;
	
	/** The list of URIs to exclude */
	private List<String> excludeUriFilters = new LinkedList<String>();
	/** True to override the exclude filters; otherwise false */
	private boolean overrideExcludeUriFilters = false;
	
	/** The list of URIs to forcefully include */
	private List<String> includeUriFilters = new LinkedList<String>();
	/** True to override the include filters; otherwise false */
	private boolean overrideIncludeUriFilters = false;
	
	/** The list of MIME types to exclude */
	private String excludedMimeTypes = null;
	/** True to override the MIME type filters; otherwise false */
	private boolean overrideExcludedMimeTypes = false;
	
	/** The list of credentials to add to the profile */
	private List<ProfileCredentials> credentials = new LinkedList<ProfileCredentials>();
	/** True to override the credentials; otherwise false */
	private boolean overrideCredentials = false;

	
    /**
     * Gets the database OID of the object.
     * @return Returns the oid.
     * @hibernate.id column="PO_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="PROFILE_OVERRIDE" 
     */	
	public Long getOid() {
		return oid;
	}
	
	/**
	 * Sets the OID of the object.
	 * @param oid The oid to set.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}		
	
	/**
	 * Create a deep copy of the profile overrides that can be used on another
	 * AbstractTarget. 
	 * @return A copy of the ProfileOverrides.
	 */
	public ProfileOverrides copy() {
		ProfileOverrides copy = new ProfileOverrides();
		
		copy.robotsHonouringPolicy = robotsHonouringPolicy;
		copy.overrideRobotsHonouringPolicy = overrideRobotsHonouringPolicy;
		copy.maxTimeSec = maxTimeSec;
		copy.overrideMaxTimeSec = overrideMaxTimeSec;
		copy.maxBytesDownload = maxBytesDownload;
		copy.overrideMaxBytesDownload = overrideMaxBytesDownload;
		copy.maxHarvestDocuments = maxHarvestDocuments;
		copy.overrideMaxHarvestDocuments = overrideMaxHarvestDocuments;
		copy.maxPathDepth = maxPathDepth;
		copy.overrideMaxPathDepth = overrideMaxPathDepth;
		copy.maxLinkHops = maxLinkHops;
		copy.overrideMaxLinkHops = overrideMaxLinkHops;
		
		copy.excludeUriFilters = new LinkedList<String>();
		copy.excludeUriFilters.addAll(excludeUriFilters);
		copy.overrideExcludeUriFilters = overrideExcludeUriFilters;
		copy.includeUriFilters = new LinkedList<String>();
		copy.includeUriFilters.addAll(includeUriFilters);
		copy.overrideIncludeUriFilters = overrideIncludeUriFilters;
		copy.excludedMimeTypes = excludedMimeTypes;
		copy.overrideExcludedMimeTypes = overrideExcludedMimeTypes;
		
		copy.overrideCredentials = overrideCredentials;
		for(ProfileCredentials creds : credentials) {
			copy.credentials.add(creds.copy());
		}

		return copy;
	}
	
	/**
	 * Apply the profile overrides to a Heritrix profile. For each element in
	 * the profile that is overriden (the flag set to true), this method 
	 * deletes the value from the base profile and replaces it with the value
	 * from the overrides.
	 * 
	 * @param profile The Heritrix Profile to override.
	 */
	public void apply(HeritrixProfile profile) {
		try {
			// Elements on the base.
			if(overrideMaxTimeSec) {
				profile.setSimpleType(ELEM_MAX_TIME_SEC, maxTimeSec);
			}
			if(overrideMaxBytesDownload) {
				profile.setSimpleType(ELEM_MAX_BYTES_DOWNLOAD, maxBytesDownload);
			}
			if(overrideMaxHarvestDocuments) {
				profile.setSimpleType(ELEM_MAX_DOCUMENT_DOWNLOAD, maxHarvestDocuments);
			}
			
			if(profile.elementExists(ELEM_SCOPE_DECIDE_RULES))
			{
				if(overrideMaxLinkHops) {
					profile.removeFromMapByType(ELEM_SCOPE_DECIDE_RULES, "org.archive.crawler.deciderules.TooManyHopsDecideRule");
					profile.addMapElement(ELEM_SCOPE_DECIDE_RULES, "_wct_max_hops", "org.archive.crawler.deciderules.TooManyHopsDecideRule");
					profile.setSimpleType(ELEM_SCOPE_DECIDE_RULES + "/_wct_max_hops/max-hops", maxLinkHops);

					profile.removeFromMapByType(ELEM_SCOPE_DECIDE_RULES, "org.archive.crawler.deciderules.TransclusionDecideRule");
					profile.addMapElement(ELEM_SCOPE_DECIDE_RULES, "_wct_max_trans_hops", "org.archive.crawler.deciderules.TransclusionDecideRule");
					profile.setSimpleType(ELEM_SCOPE_DECIDE_RULES + "/_wct_max_trans_hops/max-trans-hops", maxLinkHops);
				}
				
				if(overrideMaxPathDepth) {
					profile.removeFromMapByType(ELEM_SCOPE_DECIDE_RULES, "org.archive.crawler.deciderules.TooManyPathSegmentsDecideRule");
					profile.addMapElement(ELEM_SCOPE_DECIDE_RULES, "_wct_max_depth", "org.archive.crawler.deciderules.TooManyPathSegmentsDecideRule");
					profile.setSimpleType(ELEM_SCOPE_DECIDE_RULES + "/_wct_max_depth/max-path-depth", maxPathDepth);
				}
	
				if(overrideExcludeUriFilters || overrideIncludeUriFilters)
				{
					profile.removeFromMapByType(ELEM_SCOPE_DECIDE_RULES, "org.archive.crawler.deciderules.MatchesListRegExpDecideRule");
				}
				
				if(overrideExcludeUriFilters) {
					profile.addMapElement(ELEM_SCOPE_DECIDE_RULES, "_wct_excl_uris", "org.archive.crawler.deciderules.MatchesListRegExpDecideRule");
					profile.setListType(ELEM_SCOPE_DECIDE_RULES + "/_wct_excl_uris/regexp-list", excludeUriFilters);
					profile.setSimpleType(ELEM_SCOPE_DECIDE_RULES + "/_wct_excl_uris/list-logic", "OR");
					profile.setSimpleType(ELEM_SCOPE_DECIDE_RULES + "/_wct_excl_uris/decision", "REJECT");
				}
				
				if(overrideIncludeUriFilters) {
					profile.addMapElement(ELEM_SCOPE_DECIDE_RULES, "_wct_incl_uris", "org.archive.crawler.deciderules.MatchesListRegExpDecideRule");
					profile.setListType(ELEM_SCOPE_DECIDE_RULES + "/_wct_incl_uris/regexp-list", includeUriFilters);
					profile.setSimpleType(ELEM_SCOPE_DECIDE_RULES + "/_wct_incl_uris/list-logic", "OR");
					profile.setSimpleType(ELEM_SCOPE_DECIDE_RULES + "/_wct_incl_uris/decision", "ACCEPT");
				}	
			}
			else
			{
				//this is an old profile
				// Set the hops filter
				if(overrideMaxLinkHops) {
					profile.removeFromMapByType(ELEM_SCOPE_EXCLUDE_FILTER, "org.archive.crawler.filter.HopsFilter");
					profile.setSimpleType(ELEM_MAX_LINK_HOPS, maxLinkHops);
					profile.setSimpleType(ELEM_MAX_TRANSITIVE_HOPS, maxLinkHops);
					
					profile.addMapElement(ELEM_SCOPE_EXCLUDE_FILTER, "_wct_max_links", "org.archive.crawler.filter.HopsFilter");
				}
				
				if(overrideMaxPathDepth) {
					profile.removeFromMapByType(ELEM_SCOPE_EXCLUDE_FILTER, "org.archive.crawler.filter.PathDepthFilter");
					profile.addMapElement(ELEM_SCOPE_EXCLUDE_FILTER, "_wct_max_depth", "org.archive.crawler.filter.PathDepthFilter");
					profile.setSimpleType(ELEM_SCOPE_EXCLUDE_FILTER + "/_wct_max_depth/max-path-depth", maxPathDepth);
				}
	
				if(overrideExcludeUriFilters) {
					profile.removeFromMapByType(ELEM_SCOPE_EXCLUDE_FILTER, "org.archive.crawler.filter.URIListRegExpFilter");
					profile.addMapElement(ELEM_SCOPE_EXCLUDE_FILTER, "_wct_excl_uris", "org.archive.crawler.filter.URIListRegExpFilter");
					profile.setListType(ELEM_SCOPE_EXCLUDE_FILTER + "/_wct_excl_uris/regexp-list", excludeUriFilters);
				}
				
				if(overrideIncludeUriFilters) {
					if(log.isWarnEnabled())
					{
						log.warn(ELEM_SCOPE_FORCE_ACCEPT_FILTER + " is no longer valid and has been ignored.");
					}
				}	
			}

			
			if(overrideExcludedMimeTypes) {
				if(profile.elementExists(ELEM_WRITE_PROCESSORS_DECIDE_RULES))
				{
					profile.removeFromMapByType(ELEM_WRITE_PROCESSORS_DECIDE_RULES, "org.archive.crawler.deciderules.ContentTypeMatchesRegExpDecideRule");
					profile.addMapElement(ELEM_WRITE_PROCESSORS_DECIDE_RULES, "_wct_content_type", "org.archive.crawler.deciderules.ContentTypeMatchesRegExpDecideRule");
					profile.setSimpleType(ELEM_WRITE_PROCESSORS_DECIDE_RULES + "/_wct_content_type/regexp", excludedMimeTypes);
					profile.setSimpleType(ELEM_WRITE_PROCESSORS_DECIDE_RULES + "/_wct_content_type/decision", "REJECT");
				}
				else
				{
					//this is an old profile
					profile.removeFromMapByType(ELEM_WRITE_PROCESSORS_FILTER, "org.archive.crawler.filter.ContentTypeRegExpFilter");
					profile.addMapElement(ELEM_WRITE_PROCESSORS_FILTER, "_wct_content_type", "org.archive.crawler.filter.ContentTypeRegExpFilter");
					profile.setSimpleType(ELEM_WRITE_PROCESSORS_FILTER + "/" + "_wct_content_type" + "/regexp", excludedMimeTypes);
				}
			}
			
			
			if(overrideRobotsHonouringPolicy) {
				profile.setSimpleType(ELEM_ROBOTS_HONOURING_POLICY, robotsHonouringPolicy);
			}
			
			if(overrideCredentials) {
				profile.clearMap(ProfileCredentials.ELEM_CREDENTIALS);
				
				Iterator<ProfileCredentials> it = credentials.iterator();
				for(int i=0; it.hasNext(); i++) {
					it.next().addToProfile(profile, "_creds_" + i);
				}
			}
		}
		catch(AttributeNotFoundException ex) {
			throw new WCTRuntimeException("Failed to find correct profile elements", ex);
		}
		catch(DuplicateNameException ex) {
			log.error("Duplicate name for Profile Override", ex);
			throw new WCTRuntimeException("Duplicate name for auto defined exclude filter", ex);
		} 
		catch (InvalidAttributeValueException e) {
			log.error("Invalid Attribute Value for Profile Override", e);
			throw new WCTRuntimeException("Invalid Attribute Exception", e);
		}
	}
	
	/** 
	 * Return flag to indicate that there are overrides set in this profile
	 * overrides object
	 * @return flag to indicat there are overrides
	 */
	public boolean hasOverrides() {
		if (isOverrideCredentials() || 
		    isOverrideExcludedMimeTypes() ||
		    isOverrideExcludeUriFilters() ||
		    isOverrideIncludeUriFilters() ||
		    isOverrideMaxBytesDownload() ||
		    isOverrideMaxHarvestDocuments() ||
		    isOverrideMaxLinkHops() ||
		    isOverrideMaxPathDepth() ||
		    isOverrideMaxTimeSec() ||
		    isOverrideRobotsHonouringPolicy()) {
			return true;
		}
		
		return false;
	}	
	
	
	
	/**
	 * @return Returns the contentTypeRegexp.
	 * @hibernate.property column="PO_EXCL_MIME_TYPES"
	 */
	public String getExcludedMimeTypes() {
		return excludedMimeTypes;
	}

	/**
	 * @param contentTypeRegexp The contentTypeRegexp to set.
	 */
	public void setExcludedMimeTypes(String contentTypeRegexp) {
		this.excludedMimeTypes = contentTypeRegexp;
	}


	/**
	 * @return Returns the maxBytesDownload.
	 * @hibernate.property column="PO_MAX_BYES" 
	 */
	public Long getMaxBytesDownload() {
		return maxBytesDownload;
	}

	/**
	 * @param maxBytesDownload The maxBytesDownload to set.
	 */
	public void setMaxBytesDownload(Long maxBytesDownload) {
		this.maxBytesDownload = maxBytesDownload;
	}

	/**
	 * @return Returns the maxHarvestDocuments.
	 * @hibernate.property column="PO_MAX_DOCS"
	 */
	public Long getMaxHarvestDocuments() {
		return maxHarvestDocuments;
	}

	/**
	 * @param maxHarvestDocuments The maxHarvestDocuments to set.
	 */
	public void setMaxHarvestDocuments(Long maxHarvestDocuments) {
		this.maxHarvestDocuments = maxHarvestDocuments;
	}

	/**
	 * @return Returns the maxLinkHops.
	 * @hibernate.property column="PO_MAX_HOPS"
	 */
	public Integer getMaxLinkHops() {
		return maxLinkHops;
	}

	/**
	 * @param maxLinkHops The maxLinkHops to set.
	 */
	public void setMaxLinkHops(Integer maxLinkHops) {
		this.maxLinkHops = maxLinkHops;
	}

	/**
	 * @return Returns the maxPathDepth.
	 * @hibernate.property column="PO_MAX_PATH_DEPTH"
	 */
	public Integer getMaxPathDepth() {
		return maxPathDepth;
	}

	/**
	 * @param maxPathDepth The maxPathDepth to set.
	 */
	public void setMaxPathDepth(Integer maxPathDepth) {
		this.maxPathDepth = maxPathDepth;
	}

	/**
	 * @return Returns the maxTimeSec.
	 * @hibernate.property column="PO_MAX_TIME_SEC" 
	 */
	public Long getMaxTimeSec() {
		return maxTimeSec;
	}

	/**
	 * @param maxTimeSec The maxTimeSec to set.
	 */
	public void setMaxTimeSec(Long maxTimeSec) {
		this.maxTimeSec = maxTimeSec;
	}

	/**
	 * @return Returns the uriFilters.
	 * @hibernate.list table="PO_EXCLUSION_URI"
	 * @hibernate.collection-key column="PEU_PROF_OVER_OID"
	 * @hibernate.collection-index column="PEU_IX"
	 * @hibernate.collection-element type="string" column="PEU_FILTER"
	 */
	public List<String> getExcludeUriFilters() {
		return excludeUriFilters;
	}

	/**
	 * @param uriFilters The uriFilters to set.
	 */
	public void setExcludeUriFilters(List<String> uriFilters) {
		this.excludeUriFilters = uriFilters;
	}
	/**
	 * @return Returns the includeUriFilters.
	 * @hibernate.list table="PO_INCLUSION_URI" lazy="false" 
	 * @hibernate.collection-key column="PEU_PROF_OVER_OID"
	 * @hibernate.collection-index column="PEU_IX"
	 * @hibernate.collection-element type="string" column="PEU_FILTER"
	 */
	public List<String> getIncludeUriFilters() {
		return includeUriFilters;
	}

	/**
	 * @param includeUriFilters The includeUriFilters to set.
	 */
	public void setIncludeUriFilters(List<String> includeUriFilters) {
		this.includeUriFilters = includeUriFilters;
	}

	/**
	 * @return Returns the robotsHonouringPolicy.
	 * @hibernate.property column="PO_ROBOTS_POLICY" length="10"
	 */
	public String getRobotsHonouringPolicy() {
		return robotsHonouringPolicy;
	}

	/**
	 * @param robotsHonouringPolicy The robotsHonouringPolicy to set.
	 */
	public void setRobotsHonouringPolicy(String robotsHonouringPolicy) {
		this.robotsHonouringPolicy = robotsHonouringPolicy;
	}

	/**
	 * @return Returns the credentials.
	 * @hibernate.list cascade="all-delete-orphan"
	 * @hibernate.collection-key column="PC_PROFILE_OVERIDE_OID" 
	 * @hibernate.collection-index column="PC_INDEX"
	 * @hibernate.collection-one-to-many class="org.webcurator.domain.model.core.ProfileCredentials" 
	 */
	public List<ProfileCredentials> getCredentials() {
		return credentials;
	}

	/**
	 * @param credentials The credentials to set.
	 */
	public void setCredentials(List<ProfileCredentials> credentials) {
		this.credentials = credentials;
	}
	



	/**
	 * @return Returns the overrideCredentials.
	 * @hibernate.property column="PO_OR_CREDENTIALS"
	 */
	public boolean isOverrideCredentials() {
		return overrideCredentials;
	}

	/**
	 * @param overrideCredentials The overrideCredentials to set.
	 */
	public void setOverrideCredentials(boolean overrideCredentials) {
		this.overrideCredentials = overrideCredentials;
	}

	/**
	 * @return Returns the overrideExcludedMimeTypes.
	 * @hibernate.property column="PO_OR_EXCL_MIME_TYPES"
	 */
	public boolean isOverrideExcludedMimeTypes() {
		return overrideExcludedMimeTypes;
	}

	/**
	 * @param overrideExcludedMimeTypes The overrideExcludedMimeTypes to set.
	 */
	public void setOverrideExcludedMimeTypes(boolean overrideExcludedMimeTypes) {
		this.overrideExcludedMimeTypes = overrideExcludedMimeTypes;
	}

	/**
	 * @return Returns the overrideExcludeUriFilters.
	 * @hibernate.property column="PO_OR_EXCLUSION_URI"
	 */
	public boolean isOverrideExcludeUriFilters() {
		return overrideExcludeUriFilters;
	}

	/**
	 * @param overrideExcludeUriFilters The overrideExcludeUriFilters to set.
	 */
	public void setOverrideExcludeUriFilters(boolean overrideExcludeUriFilters) {
		this.overrideExcludeUriFilters = overrideExcludeUriFilters;
	}

	/**
	 * @return Returns the overrideIncludeUriFilters.
	 * @hibernate.property column="PO_OR_INCLUSION_URI"
	 */
	public boolean isOverrideIncludeUriFilters() {
		return overrideIncludeUriFilters;
	}

	/**
	 * @param overrideIncludeUriFilters The overrideIncludeUriFilters to set.
	 */
	public void setOverrideIncludeUriFilters(boolean overrideIncludeUriFilters) {
		this.overrideIncludeUriFilters = overrideIncludeUriFilters;
	}

	/**
	 * @return Returns the overrideMaxBytesDownload.
	 * @hibernate.property column="PO_OR_MAX_BYTES"
	 */
	public boolean isOverrideMaxBytesDownload() {
		return overrideMaxBytesDownload;
	}

	/**
	 * @param overrideMaxBytesDownload The overrideMaxBytesDownload to set.
	 */
	public void setOverrideMaxBytesDownload(boolean overrideMaxBytesDownload) {
		this.overrideMaxBytesDownload = overrideMaxBytesDownload;
	}

	/**
	 * @return Returns the overrideMaxHarvestDocuments.
	 * @hibernate.property column="PO_OR_MAX_DOCS"
	 */
	public boolean isOverrideMaxHarvestDocuments() {
		return overrideMaxHarvestDocuments;
	}

	/**
	 * @param overrideMaxHarvestDocuments The overrideMaxHarvestDocuments to set.
	 */
	public void setOverrideMaxHarvestDocuments(boolean overrideMaxHarvestDocuments) {
		this.overrideMaxHarvestDocuments = overrideMaxHarvestDocuments;
	}

	/**
	 * @return Returns the overrideMaxLinkHops.
	 * @hibernate.property column="PO_OR_MAX_HOPS"
	 */
	public boolean isOverrideMaxLinkHops() {
		return overrideMaxLinkHops;
	}

	/**
	 * @param overrideMaxLinkHops The overrideMaxLinkHops to set.
	 */
	public void setOverrideMaxLinkHops(boolean overrideMaxLinkHops) {
		this.overrideMaxLinkHops = overrideMaxLinkHops;
	}

	/**
	 * @return Returns the overrideMaxPathDepth.
	 * @hibernate.property column="PO_OR_MAX_PATH_DEPTH"
	 */
	public boolean isOverrideMaxPathDepth() {
		return overrideMaxPathDepth;
	}

	/**
	 * @param overrideMaxPathDepth The overrideMaxPathDepth to set.
	 */
	public void setOverrideMaxPathDepth(boolean overrideMaxPathDepth) {
		this.overrideMaxPathDepth = overrideMaxPathDepth;
	}

	/**
	 * @return Returns the overrideMaxTimeSec.
	 * @hibernate.property column="PO_OR_MAX_TIME_SEC"
	 */
	public boolean isOverrideMaxTimeSec() {
		return overrideMaxTimeSec;
	}

	/**
	 * @param overrideMaxTimeSec The overrideMaxTimeSec to set.
	 */
	public void setOverrideMaxTimeSec(boolean overrideMaxTimeSec) {
		this.overrideMaxTimeSec = overrideMaxTimeSec;
	}

	/**
	 * @return Returns the overrideRobotsHonouringPolicy.
	 * @hibernate.property column="PO_OR_ROBOTS_POLICY"
	 */
	public boolean isOverrideRobotsHonouringPolicy() {
		return overrideRobotsHonouringPolicy;
	}

	/**
	 * @param overrideRobotsHonouringPolicy The overrideRobotsHonouringPolicy to set.
	 */
	public void setOverrideRobotsHonouringPolicy(
			boolean overrideRobotsHonouringPolicy) {
		this.overrideRobotsHonouringPolicy = overrideRobotsHonouringPolicy;
	}




}
