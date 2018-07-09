package org.webcurator.domain.model.dto;

/**
 * DTO for efficient retrieval and display of <code>Indicator</code> objects.
 * @author twoods
 *
 */
public class IndicatorDTO {
	/** constants used to denote indicator names that are defined in the rules engine **/
	public static final String LONG_URIS = "Long URIs";
	public static final String CONTENT_DOWNLOADED = "Content Downloaded";
	public static final String URLS_DOWNLOADED = "URLs Downloaded";
	public static final String CRAWL_RUNTIME = "Crawl Runtime";
	public static final String NEW_URIS = "New URIs";
	public static final String MATCHING_URIS = "Matching URIs";
	public static final String MISSING_URIS = "Missing URIs";
	public static final String OFF_SCOPE_URIS = "Off Scope URIs";
	public static final String SUB_DOMANS = "Sub Domains";

	/** indicator name suffix used by the rules engine to generate an
	 * indicator for a reference crawl **/
	public static final String RCI_SUFFIX = " (RCI)";
	
	/** unique identifier **/
	private Long oid;
	
	/** The <code>IndicatorCriteria</code> associated with this <code>Indicator</code> */
	private Long indicatorCriteriaOid;
	
	/** The <code>TargetInstance</code> associated with this <code>Indicator</code> */
	private Long targetInstanceOid;
	
	/** The name of the <code>Indicator</code> that will be displayed **/
	private String name;
	
	/** The floating point value calculated for this <code>Indicator</code> by the rules engine **/
	private Float floatValue;
	
	/** The upper limit set for this <code>Indicator</code> as a percentage (eg: +10%) **/
	private Float upperLimitPercentage;

	/** The lower limit set for this <code>Indicator</code> as a percentage (eg: -10%) **/
	private Float lowerLimitPercentage;
	
	/** The upper limit set for this <code>Indicator</code> as a floating point number (some <code>Indicators</code> do not have associated percentage limits) **/
	private Float upperLimit;

	/** The lower limit set for this <code>Indicator</code> as a floating point number (some <code>Indicators</code> do not have associated percentage limits) **/
	private Float lowerLimit;
	
	/** The advice issued for this <code>Indicator</code>
	 * based on the supporting facts established by the rules engine.
	**/
	private String advice = null;
	
	/**
	 * The advice justification for this <code>Indicator</code>
	 * based on the supporting facts established by the rules engine.
	 */
	private String justification;
	
	/**
	 * Constructor for Hibernate queries
	 * @param oid					The OID of the <code>Indicator</code>
	 * @param indicatorCriteriaOid	The OID of the <code>IndicatorCriteria</code> associated with this <code>Indicator</code>
	 * @param targetInstanceOid		The OID of the associated <code>TargetInstance</code>
	 * @param name					The textual name of the <code>Indicator</code>
	 * @param floatValue			The numerical value of the <code>Indicator</code>
	 * @param upperLimitPercentage	The upper limit percentage value for this <code>Indicator</code> (used to compute a tolerance against a reference crawl)
	 * @param lowerLimitPercentage	The lower limit percentage value for this <code>Indicator</code> (used to compute a tolerance against a reference crawl)
	 * @param upperLimit				The upper limit specified as a numerical value for this <code>Indicator</code> (some <code>Indicator</code> do not depend on percentage tolerances) 
	 * @param lowerLimit			The lower limit specified as a numerical value for this <code>Indicator</code> (some <code>Indicator</code> do not depend on percentage tolerances)
	 * @param advice				The advice (Archive, Investigate or Reject) derived by the rules engine for this <code>Indicator</code>
	 * @param justification			The corresponding justification for advice derived (based on facts deduced by the rules engine)
	 */
	public IndicatorDTO(Long oid, 
						Long indicatorCriteriaOid,
						Long targetInstanceOid, 
						String name, 
						Float floatValue, 
						Float upperLimitPercentage, 
						Float lowerLimitPercentage, 
						Float upperLimit, 
						Float lowerLimit, 
						String advice, 
						String justification) {
		super();
		this.oid = oid;
		this.indicatorCriteriaOid = indicatorCriteriaOid;
		this.targetInstanceOid = targetInstanceOid;
		this.name = name;
		this.floatValue = floatValue;
		this.upperLimitPercentage = upperLimitPercentage;
		this.lowerLimitPercentage = lowerLimitPercentage;
		this.upperLimit = upperLimit;
		this.lowerLimit = lowerLimit;
		this.advice = advice;
		this.justification = justification;
		
	}
	
	/**
	 * Get the database OID of the <code>Indicator</code>.
	 * @return the primary key
	 */	
	public Long getOid() {
		return oid;
	}
	
	/**
	 * Set the database oid of the <code>Indicator</code>.
	 * @param oid The new database oid.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}
	
    /**
     * Gets the name of the <code>Indicator</code>.
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the <code>Indicator</code>.
     * @param name The new name for the <code>Indicator</code>.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value of the <code>Indicator</code>.
     * @return Returns the floating point value.
     */
    public Float getFloatValue() {
        return floatValue;
    }

    /**
     * Sets the value of the <code>Indicator</code>.
     * @param name The new value for the <code>Indicator</code>.
     */
    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
    }
    
    /**
     * Gets the upper limit percentage value of the <code>Indicator</code>.
     * @return Returns the upper limit percentage value.
     */
    public Float getUpperLimitPercentage() {
        return upperLimitPercentage;
    }

    /**
     * Sets the upper limit percentage value of the <code>Indicator</code>.
     * @param name The new upper limit percentage value for the <code>Indicator</code>.
     */
    public void setUpperLimitPercentage(Float floatValue) {
        this.upperLimitPercentage = floatValue;
    }
    
    /**
     * Gets the lower limit percentage value of the <code>Indicator</code>.
     * @return Returns the lower limit percentage value.
     */
    public Float getLowerLimitPercentage() {
        return lowerLimitPercentage;
    }

    /**
     * Sets the lower limit percentage value of the <code>Indicator</code>.
     * @param name The new lower limit percentage value for the <code>Indicator</code>.
     */
    public void setLowerLimitPercentage(Float floatValue) {
        this.lowerLimitPercentage = floatValue;
    }
    
    /**
     * Gets the upper limit value of the <code>Indicator</code>.
     * @return Returns the upper limit value.
     */
    public Float getUpperLimit() {
        return upperLimit;
    }
    
    /**
     * Sets the upper limit value of the <code>Indicator</code>.
     * @param name The new upper limit value for the <code>Indicator</code>.
     */
    public void setUpperLimit(Float floatValue) {
        this.upperLimit = floatValue;
    }

    /**
     * Gets the lower limit value of the <code>Indicator</code>.
     * @return Returns the lower limit value.
     */
    public Float getLowerLimit() {
        return lowerLimit;
    }

    /**
     * Sets the lower limit value of the <code>Indicator</code>.
     * @param name The new lower limit value for the <code>Indicator</code>.
     */
    public void setLowerLimit(Float floatValue) {
        this.lowerLimit = floatValue;
    }

	/**
	 * Fetches the advice set by the QA recommendation service
	 * @return the advised action
	 */
	public String getAdvice() {
		return advice;
	}
	
	/**
	 * Used by the QA Recommendation service to set the QA advice
	 * @param advice the advised action
	 */
	public void setAdvice(String advice) {
		this.advice = advice;
	}
	
	/**
	 * Fetches the justification set by the QA recommendation service
	 * @return the rationale for the advice
	 */
	public String getJustification() {
		return justification;
	}
	
	/**
	 * Used by the QA Recommendation service to set the QA justification
	 * @param the advice justification
	 */
	public void setJustification(String justification) {
		this.justification = justification;
	}
}
