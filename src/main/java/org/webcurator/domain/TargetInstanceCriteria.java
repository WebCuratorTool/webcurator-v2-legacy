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
package org.webcurator.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.webcurator.domain.model.core.Flag;
/** 
 * The criteria to be used when searching for a TargetInstance.
 * @author nwaight
 */
public class TargetInstanceCriteria {
    private Date from;
    private Date to;
    private Set<String> states = new HashSet<String>();
    private String owner = "";
    private String agency = "";
    private String name = "";
    private Long searchOid = null;
    private boolean flagged = false;
    private Flag flag = null;
    private boolean nondisplayonly = false;
    private String sortorder = "";
    private Long targetSearchOid = null;
    private Set<String> recommendationFilter = new HashSet<String>();
    
    
	/**
	 * @return the agency
	 */
	public String getAgency() {
		return agency;
	}
	/**
	 * @param agency the agency to set
	 */
	public void setAgency(String agency) {
		this.agency = agency;
	}
	/**
     * @return Returns the from.
     */
    public Date getFrom() {        
        return from;
    }
    /**
     * @param from The from to set.
     */
    public void setFrom(Date from) {
        this.from = from;
    }
    /**
     * @return Returns the owner.
     */
    public String getOwner() {
        return owner;
    }
    /**
     * @param owner The owner to set.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }
    /**
     * @return Returns the states.
     */
    public Set<String> getStates() {
        return states;
    }
    /**
     * @param states The states to set.
     */
    public void setStates(Set<String> states) {
        this.states = states;
    }
    /**
     * @return Returns the to.
     */
    public Date getTo() {
        return to;
    }
    /**
     * @param to The to to set.
     */
    public void setTo(Date to) {
        this.to = to;
    }
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the search Oid
	 */
	public Long getSearchOid() {
		return searchOid;
	}
	/**
	 * @param searchOid the search oid to set
	 */
	public void setSearchOid(Long searchOid) {
		this.searchOid = searchOid;
	}
	/**
	 * @return the flagged flag
	 */
	public boolean getFlagged() {
		return flagged;
	}
	/**
	 * @param flagged the flag to set
	 */
	public void setFlagged(boolean flagged) {
		this.flagged = flagged;
	}

	/**
	 * @return the nondisplayonly flag
	 */
	public boolean getNondisplayonly() {
		return nondisplayonly;
	}
	/**
	 * @param nondisplayonly the flag to set
	 */
	public void setNondisplayonly(boolean nondisplayonly) {
		this.nondisplayonly = nondisplayonly;
	}
	
	/**
	 * @return Returns the sortorder.
	 */
	public String getSortorder() {
		return sortorder;
	}

	/**
	 * @param sortorder The sortorder to set.
	 */

	public void setSortorder(String sortorder) {
		this.sortorder = sortorder;
	}
	
	/**
	 * Fetch the <code>Target</code> oid to search for
	 * @return the Oid of the <code>Target</code>
	 */
	public Long getTargetSearchOid() {
		return targetSearchOid;
	}
	
	/**
	 * Set the <code>Target</code> oid to search for
	 * @param targetSearchOid the <code>Target</code> search oid to set
	 */
	public void setTargetSearchOid(Long targetSearchOid) {
		this.targetSearchOid = targetSearchOid;
	}
	public Flag getFlag() {
		return flag;
	}
	public void setFlag(Flag flag) {
		this.flag = flag;
	}
	public Set<String> getRecommendationFilter() {
		return recommendationFilter;
	}
	public void setRecommendationFilter(Set<String> recommendationFilter) {
		this.recommendationFilter = recommendationFilter;
	}
	
}
