package org.webcurator.domain;

import java.util.List;

import org.webcurator.domain.model.core.IndicatorCriteria;

/**
 * The object for accessing <code>IndicatorCriteria</code>s from the persistent store.
 */
public interface IndicatorCriteriaDAO {

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
     * gets the specific <code>IndicatorCriteria</code> based on the provided oid
     * @param oid the <code>IndicatorCriteria</code> Oid 
     * @return the populated <code>IndicatorCriteria</code> object
     */
    public IndicatorCriteria getIndicatorCriteriaByOid(Long oid);
	
    /**
     * gets all <code>IndicatorCriteria</code>s in the system
     * @return a List of fully populated <code>IndicatorCriteria</code> objects
     */
    public List<IndicatorCriteria> getIndicatorCriterias();
 
    /**
     * gets the <code>IndicatorCriteria</code> for a selected Agency
     * @param agencyOid the Oid of the Agency in which to search for <code>IndicatorCriteria</code>s
     * @return a List of fully populated <code>IndicatorCriteria</code> objects
     */
    public List<IndicatorCriteria> getIndicatorCriteriasByAgencyOid(Long agencyOid);
    
}
