package org.webcurator.domain;

import java.util.List;

import org.webcurator.domain.model.core.Flag;

/**
 * The object for accessing <code>Flag</code>s from the persistent store.
 */
public interface FlagDAO {

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
     * gets the specific <code>Flag</code> based on the provided oid
     * @param oid the <code>Flag</code> Oid 
     * @return the populated <code>Flag</code> object
     */
    public Flag getFlagByOid(Long oid);
	
    /**
     * gets all <code>Flag</code>s in the system
     * @return a List of fully populated <code>Flag</code> objects
     */
    public List<Flag> getFlags();
 
    /**
     * gets the <code>Flag</code> for a selected Agency
     * @param agencyOid the Oid of the Agency in which to search for <code>Flag</code>s
     * @return a List of fully populated <code>Flag</code> objects
     */
    public List<Flag> getFlagsByAgencyOid(Long agencyOid);
    
}
