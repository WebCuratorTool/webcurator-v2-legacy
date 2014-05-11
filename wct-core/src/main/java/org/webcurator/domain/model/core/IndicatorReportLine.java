package org.webcurator.domain.model.core;

import java.util.Date;

import org.webcurator.core.harvester.agent.HarvesterStatusUtil;
import org.webcurator.core.util.ConverterUtil;
import org.webcurator.domain.model.auth.Agency;

/**
 * An <code>Indicator</code> represents a metric used to measure the
 * quality of a harvest and is associated with a specific <code>TargetInstance</code>
 * 
 * @author twoods
 *
 * @hibernate.class table="INDICATOR_REPORT_LINE" lazy="true" 
 * @hibernate.query name="org.webcurator.domain.model.core.IndicatorReportLine.getIndicatorReportLines" query="SELECT irl FROM IndicatorReportLine irl ORDER BY irl.line"
 * @hibernate.query name="org.webcurator.domain.model.core.IndicatorReportLine.getIndicatorReportLinesByIndicator" query="SELECT irl FROM IndicatorReportLine irl WHERE irl_i_oid=? ORDER BY irl.line"
 * @hibernate.query name="org.webcurator.domain.model.core.IndicatorReportLine.getIndicatorReportLineByOid" query="SELECT irl FROM IndicatorReportLine irl WHERE irl_oid=?"
*/
public class IndicatorReportLine {
		
	/** Query key for retrieving all IndicatorReportLine objects */
    public static final String QRY_GET_INDICATOR_REPORT_LINES = "org.webcurator.domain.model.core.IndicatorReportLine.getIndicatorReportLines";
	/** Query key for retrieving IndicatorReportLine objects by agency OID */
    public static final String QRY_GET_INDICATOR_REPORT_LINES_BY_I_OID = "org.webcurator.domain.model.core.IndicatorReportLine.getIndicatorReportLinesByIndicator";
	/** Query key for retrieving a IndicatorReportLine objects by oid*/
    public static final String QRY_GET_INDICATOR_REPORT_LINE_BY_OID = "org.webcurator.domain.model.core.IndicatorReportLine.getIndicatorReportLineByOid";

	/** unique identifier **/
	private Long oid = null;
	
	/** The <code>Indicator</code> on which this <code>IndicatorReportLine</code> is based.**/
	private Indicator indicator = null;
	
	/** The report line (resource name etc) for this <code>IndicatorReportLine</code> **/
	private String line = null;
	
	/**
	 * Get the database OID of the <code>IndicatorReportLine</code>.
	 * @return the primary key
     * @hibernate.id column="IRL_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="General" 
	 */	
	public Long getOid() {
		return oid;
	}
	
	/**
	 * Set the database oid of the <code>IndicatorReportLine</code>.
	 * @param oid The new database oid.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}
	
    /**
     * Gets the <code>Indicator</code> for the associated <code>Indicator</code>.
     * @return Returns the value.
     * @hibernate.many-to-one column="IRL_I_OID" foreign-key="FK_IRL_I_OID"
     */
    public Indicator getIndicator() {
        return indicator;
    }

    /**
     * Sets the <code>Indicator</code> oid for the associated <code>Indicator</code>.
     * @param indicatorOid The new <code>Indicator</code>.
     */
    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }
	
    /**
     * Gets the report line of the <code>IndicatorReportLine</code>.
     * @return Returns the line value.
     * @hibernate.property column="IRL_LINE" 
     */
    public String getLine() {
        return line;
    }

    /**
     * Sets the report line of the <code>IndicatorReportLine</code>.  Removes all quote, double quote and comma characters.
     * @param name The new report line for the <code>IndicatorReportLine</code>.
     */
    public void setLine(String line) {
        this.line = line.replaceAll("'", "").replaceAll(",", "").replaceAll("\"", "");
    }

}
