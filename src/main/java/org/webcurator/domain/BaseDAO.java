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

import java.util.Collection;

/**
 * Base DAO interface. Over time common methods should be 
 * moved into this dao so they can be reused in all dao
 * implementations. 
 * 
 * @author bbeaumont
 */
public interface BaseDAO {
	
	/**
	 * Evict the object from the session.
	 * @param anObject The object to evict.
	 */
	public void evict(Object anObject);
	
	/**
	 * Delete all objects in the collection.
	 * @param aCollection The collection of objects to remove.
	 */
	public void deleteAll(Collection aCollection);
	
	/**
	 * Delete an object from the database.
	 * @param anObject The object to delete
	 */
	public void delete(final Object anObject);
	

	/**
	 * Initialize an object from Hibernate.
	 * @param anObject The object to initialize.
	 */
	public void initialize(Object anObject);
	
}
