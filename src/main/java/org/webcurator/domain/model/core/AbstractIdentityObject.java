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

/**
 * Abstract base object that provides equivalence testing for database persisted
 * domain objects.
 * 
 * All objects have an identity value, which may be their OID (for persisted
 * objects) or an alpha-prefixed string for objects that have not yet been 
 * persisted.
 * 
 * @author bbeaumont
 */
public abstract class AbstractIdentityObject implements HasDatabaseIdentity {
	/** The identity of the object */
	private String identity = null;

	/**
	 * Gets the identity value of the object. This is either the database OID
	 * or a temporary identifier assigned by the BusinessObjectFactory.
	 */
	public String getIdentity() {
		return getOid() == null ? identity : getOid().toString();
	}
	
	/**
	 * Set the temporary identity of an object. This method is package 
	 * accessible only and should only be used by the BusinessObjectFactory.
	 * @param anIdentity
	 */
	void setIdentity(String anIdentity) {
		identity = anIdentity;
	}
	
    /* (non-Javadoc)
     * @see java.lang.Object#equals(Object)
     */
	@Override	
	public boolean equals(Object other) { 
		// Only equal if we are the same class type and our 
		// identities are equal.
		return other != null &&
		       getClass().equals(other.getClass()) &&
		       getIdentity().equals(((HasIdentity)other).getIdentity());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getIdentity().hashCode();
	}

	/**
	 * Checks if the object is new by seeing if it has a database OID.
	 * @return true if new; otherwise false.
	 */
	public boolean isNew() {
		return getOid() == null;
	}
}
