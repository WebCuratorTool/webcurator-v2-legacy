package org.webcurator.domain.model.core;

/**
 * Defines a seed that was historically used by the Target Instance. Similar
 * to the 'originalSeeds' collection but not constrained by implementation
 * to the use of a single string. This is currently 'write only' for use externally 
 * to the application
 * 
 * @author kurwin
 * @hibernate.class table="SEED_HISTORY" lazy="false"
 */
public class SeedHistory extends AbstractIdentityObject {
	/** The unique ID of the seed **/
	private Long oid;
	/** The seed itself **/
	private String seed;
	/** The seed's target instance**/
	private Long targetInstanceOid;
	/** Sets if the seed is primary or secondary. */
	private boolean primary; 
	
	/**
	 * Don't allow empty instantiation of this object as it would
	 * break DB NOT NULL constraints when saved
	 */
	private SeedHistory()
	{
	}
	
	/**
	 * Create the history from a real Seed (use BusinessObjectFactory)
	 */
	protected SeedHistory(TargetInstance aTargetInstance, Seed seed)
	{
		this.seed = seed.getSeed();
		this.primary = seed.isPrimary();
		this.targetInstanceOid = aTargetInstance.getOid();
	}
	
    /**
     * Returns the database OID of the seed.
     * @return Returns the oid.
     * @hibernate.id column="SH_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="General" 
     */
    public Long getOid() {
        return oid;
    }	
	
    /**
     * Set the OID of the seed.
     * @param anOid The OID.
     */
    public void setOid(Long anOid) {
    	this.oid = anOid;
    }
	
	/**
	 * Gets the seed URL.
	 * @return Returns the seed.
     * @hibernate.property column="SH_SEED" length="1024" 
	 */
	public String getSeed() {
		return seed;
	}

	/**
	 * Sets the Seed URL.
	 * @param seed The seed to set.
	 */
	public void setSeed(String seed) {
		this.seed = seed;
	}
	
	/**
	 * Get the Target Instance to which this seed belongs.
	 * @return Returns the Target Instance Oid.
     * @hibernate.property column="SH_TI_OID" 
	 */
	public Long getTargetInstanceOid() {
		return targetInstanceOid;
	}
	
	/**
	 * Set the Target Instance to which this seed belongs.
	 * @param aTargetInstanceOid The target instance oid to set.
	 */
	public void setTargetInstanceOid(Long aTargetInstanceOid) {
		this.targetInstanceOid = aTargetInstanceOid;
	}

	/**
	 * Checks if the seed is defined as a primary seed.
	 * @return true if primary; otherwise false.
	 * @hibernate.property column="SH_PRIMARY"
	 */
	public boolean isPrimary() {
		return primary;
	}


	/**
	 * Sets whether this seed should be primary.
	 * @param primary true to set as primary; otherwise false.
	 */
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
}
