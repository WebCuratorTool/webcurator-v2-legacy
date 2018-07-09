package org.webcurator.domain.model.core;

import org.webcurator.domain.model.auth.Agency;

/**
 * An <code>Flag</code> represents an arbitrary grouping of <code>TargetInstance</code>s
 * 
 * @author twoods
 *
 * @hibernate.class table="FLAG" lazy="true" 
 * @hibernate.query name="org.webcurator.domain.model.core.Flag.getFlags" query="SELECT f FROM Flag f ORDER BY f_agc_oid, f.name"
 * @hibernate.query name="org.webcurator.domain.model.core.Flag.getFlagsByAgency" query="SELECT f FROM Flag f WHERE f.agency.oid=? ORDER BY f.name"
 * @hibernate.query name="org.webcurator.domain.model.core.Flag.getFlagByOid" query="SELECT f FROM Flag f WHERE f_oid=?"
*/
public class Flag {
		
	/** Query key for retrieving all flag objects */
    public static final String QRY_GET_FLAGS = "org.webcurator.domain.model.core.Flag.getFlags";
	/** Query key for retrieving a flag objects by oid*/
    public static final String QRY_GET_FLAG_BY_OID = "org.webcurator.domain.model.core.Flag.getFlagByOid";
	/** Query key for retrieving reason objects by agency OID */
    public static final String QRY_GET_FLAGS_BY_AGENCY = "org.webcurator.domain.model.core.Flag.getFlagsByAgency";

	/** unique identifier **/
	private Long oid;
	
	/** The name of the <code>Flag</code> that will be displayed **/
	private String name;
	
	/** The colour components for the flag **/
	private String rgb;
	
	/** The complement colour components for the flag (used for a contrasting font colour) **/
	private String complementRgb;

    /** The agency the <code>Flag</code> belongs to */
    private Agency agency;
	
	/**
	 * Get the database OID of the <code>Flag</code>.
	 * @return the primary key
     * @hibernate.id column="F_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="General" 
	 */	
	public Long getOid() {
		return oid;
	}
	
	/**
	 * Set the database oid of the <code>Flag</code>.
	 * @param oid The new database oid.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}
	
    /**
     * Gets the name of the <code>Flag</code>.
     * @return Returns the name.
     * @hibernate.property column="F_NAME" not-null="true" 
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the <code>Flag</code>.
     * @param name The new name for the <code>Flag</code>.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value of the colour components for the <code>Flag</code>.
     * @return Returns the floating point value.
     * @hibernate.property column="F_RGB" not-null="true" 
     */
    public String getRgb() {
        return rgb;
    }

    /**
     * Sets the value of the colour components for the <code>Flag</code>.
     * @param name The new value for the <code>Flag</code>.
     */
    public void setRgb(String rgb) {
        this.rgb = rgb;
    }
    
    /**
     * Gets the value of the complement colour components for the <code>Flag</code>.
     * @return Returns the floating point value.
     * @hibernate.property column="F_COMPLEMENT_RGB" not-null="true" 
     */
    public String getComplementRgb() {
        return complementRgb;
    }

    /**
     * Sets the value of the complement colour components for the <code>Flag</code>.
     * @param name The new value for the <code>Flag</code>.
     */
    public void setComplementRgb(String rgb) {
        this.complementRgb = rgb;
    }
    
    /**
     * gets the Agency to which this <code>Flag</code> belongs. 
     * @return the Agency object
     * @hibernate.many-to-one not-null="true" class="org.webcurator.domain.model.auth.Agency" column="F_AGC_OID" foreign-key="FK_F_AGENCY_OID"
     */
    public Agency getAgency() {
        return agency;
    }

    /**
     * Set the agency which can use this <code>Flag</code>.
     * @param agency The agency that can use this <code>Flag</code>.
     */
    public void setAgency(Agency agency) {
        this.agency = agency;
    }
    
}