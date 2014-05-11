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

import java.io.Serializable;
import org.webcurator.domain.model.auth.Agency;

/**
 * A Rejection Reason represents the justification a user specified when setting a Target's 
 * status to 'Rejected' or when rejecting a harvested Target Instance.
 * Agencies can maintain their own lists of Rejection Reasons.
 * An administrator can specify if a Rejection Reason applies to Targets or Target Instances, both or neither.
 * @author oakleigh_sk
 * @hibernate.class table="REJECTION_REASON" lazy="false"
 * @hibernate.query name="org.webcurator.domain.model.core.RejReason.getReasons" query="SELECT rr FROM RejReason rr ORDER BY rr_agc_oid, rr.name"
 * @hibernate.query name="org.webcurator.domain.model.core.RejReason.getReasonByOid" query="SELECT rr FROM RejReason rr WHERE rr_oid=?"
 * @hibernate.query name="org.webcurator.domain.model.core.RejReason.getReasonsByAgency" query="SELECT rr FROM RejReason rr WHERE rr.agency.oid=? ORDER BY rr.name"
 */
public class RejReason implements Serializable {
    
   	/** Version ID for serialization */
    private static final long serialVersionUID = -9216399037972509158L;

	/** Query key for retrieving all reason objects */
    public static final String QRY_GET_REASONS = "org.webcurator.domain.model.core.RejReason.getReasons";
	/** Query key for retrieving a reason objects by oid*/
    public static final String QRY_GET_REASON_BY_OID = "org.webcurator.domain.model.core.RejReason.getReasonByOid";
	/** Query key for retrieving reason objects by agency OID */
    public static final String QRY_GET_REASONS_BY_AGENCY = "org.webcurator.domain.model.core.RejReason.getReasonsByAgency";
	
	
	/** The database OID of the reason */
    private Long oid;
    /** The name of the reason */
    private String name;
    /** The agency the reason belongs to */
    private Agency agency;
	/** Determines if the rejection reason is applicable to Targets. */
	private boolean availableForTargets; 
	/** Determines if the rejection reason is applicable to Targets. */
	private boolean availableForTIs; 
    
    /**
     * No-arg constructor.
     */
    public RejReason() {
    }
    
    
    /**
     * Gets the database OID of the reason.
     * @return Returns the oid.
     * @hibernate.id column="RR_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="RejectionReason" 
     */
    public Long getOid() {
        return oid;
    }
    
    /**
     * Sets the OID of the task.
     * @param oid The new database OID.
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }
    
    
    /**
     * gets the name of the reason, this is a description of the reason.
     * Task. For full details refer to the getMessage() method.
     * @return the name
     * @hibernate.property column="RR_NAME" length="100" not-null="true"
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the name of the reason, this is a description of the reason.
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets the Agency to which this reason belongs. 
     * @return the Agency object
     * @hibernate.many-to-one not-null="true" class="org.webcurator.domain.model.auth.Agency" column="RR_AGC_OID" foreign-key="FK_RR_AGENCY_OID"
     */
    public Agency getAgency() {
        return agency;
    }

    /**
     * Set the agency which can use this reason.
     * @param agency The agency that can use this reason.
     */
    public void setAgency(Agency agency) {
        this.agency = agency;
    }

	/**
	 * Checks if the reason is available as a Target rejection reason.
	 * @return true if available; otherwise false.
	 * @hibernate.property column="RR_AVAILABLE_FOR_TARGET"
	 */
	public boolean isAvailableForTargets() {
		return availableForTargets;
	}

	/**
	 * Sets whether this reason should be available as a Target rejection reason.
	 * @param availableForTargets, true to set as available; otherwise false.
	 */
	public void setAvailableForTargets(boolean availableForTargets) {
		this.availableForTargets = availableForTargets;
	}

	/**
	 * Checks if the reason is available as a TI (Target Instance) rejection reason.
	 * @return true if available; otherwise false.
	 * @hibernate.property column="RR_AVAILABLE_FOR_TI"
	 */
	public boolean isAvailableForTIs() {
		return availableForTIs;
	}

	/**
	 * Sets whether this reason should be available as a TI (Target Instance) rejection reason.
	 * @param availableForTIs, true to set as available; otherwise false.
	 */
	public void setAvailableForTIs(boolean availableForTIs) {
		this.availableForTIs = availableForTIs;
	}
}
