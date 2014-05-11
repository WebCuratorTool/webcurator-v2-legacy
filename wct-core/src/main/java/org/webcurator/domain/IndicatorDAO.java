package org.webcurator.domain;

import java.util.List;

import org.webcurator.domain.model.core.Indicator;

/**
 * The object for accessing <code>Indicator</code>s from the persistent store.
 */
public interface IndicatorDAO {

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
     * gets the specific <code>Indicator</code> based on the provided oid
     * @param oid the <code>Indicator</code> Oid 
     * @return the populated <code>Indicator</code> object
     */
    public Indicator getIndicatorByOid(Long oid);
 
    /**
     * gets the <code>Indicator</code> for a selected <code>TargetInstance</code>
     * @param agencyOid the Oid of the <code>TargetInstance</code> in which to search for <code>Indicator</code>s
     * @return a List of fully populated <code>Indicator</code> objects
     */
    public List<Indicator> getIndicatorsByTargetInstanceOid(Long targetInstanceOid);
    
}
