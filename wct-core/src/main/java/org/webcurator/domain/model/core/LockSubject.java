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
 * Class to create a pessimistic lock on an object.
 * 
 * @author bbeaumont
 */
public class LockSubject {
	/** The OID of the object to lock */
	private Long id;
	/** The classname of the object to be locked */
	private String classname;

	/**
	 * Create a new lock on a lockable object.
	 * @param l The object to lock.
	 */
	public LockSubject(Lockable l) {
		this.id = l.getOid();
		this.classname = l.getClass().getName();
	}
	
	/**
	 * Create a lock on an object.
	 * @param clazz The classname of the object to be locked.
	 * @param oid   The OID of the object to be locked.
	 */
	public LockSubject(Class clazz, Long oid) {
		this.id = oid;
		this.classname = clazz.getName();
	}
	
	/**
	 * Get the classname of the object that this lock is on.
	 * @return The classname of the object that this lock is on.
	 */
	public String getClassname() {
		return classname;
	}
	
	/**
	 * Set the classname of the object that is locked.
	 * @param classname The classname of the object that is locked.
	 */
	public void setClassname(String classname) {
		this.classname = classname;
	}
	
	/**
	 * Get the database OID of the object that is locked.
	 * @return The database OID of the object that is locked.  
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * Set the database OID of the object that is locked.
	 * @param id The database OID of the object that is locked.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return classname.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return o instanceof LockSubject &&
		       ((LockSubject)o).getClassname().equals(this.classname) &&
		       ((LockSubject)o).id.equals(this.id);
	}
}
