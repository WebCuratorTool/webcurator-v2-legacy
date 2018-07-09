package org.webcurator.domain;

import java.util.List;

import org.webcurator.domain.model.core.IndicatorReportLine;

/**
 * The object for accessing <code>IndicatorReportLine</code>s from the persistent store.
 */
public interface IndicatorReportLineDAO {

	/**
	 * Save or update the specified object to the persistent data store.
	 * @param aObject the object to save or update
	 */
    public void saveOrUpdate(Object aObject);
    
	/**
	 * Remove the specified object from the persistent data store.
	 * @param aObject the object to remove
	 */
	void delete(Object aObject);
	
	 /**
     * gets the specific <code>IndicatorReportLine</code> based on the provided oid
     * @param oid the <code>IndicatorReportLine</code> Oid 
     * @return the populated <code>IndicatorReportLine</code> object
     */
    public IndicatorReportLine getIndicatorReportLineByOid(Long oid);
 
    /**
     * gets the <code>IndicatorReportLine</code>s for a selected <code>Indicator</code>
     * @param indicatorOid the Oid of the <code>TargetInstance</code> in which to search for <code>Indicator</code>s
     * @return a List of fully populated <code>IndicatorReportLine</code> objects
     */
    public List<IndicatorReportLine> getIndicatorReportLinesByIndicatorOid(Long indicatorOid);
    
}
