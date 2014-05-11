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
package org.webcurator.core.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.webcurator.domain.model.core.LockSubject;
import org.webcurator.domain.model.core.Lockable;

/**
 * The LockManager is responsible for managing Pessimistic locks.
 * This has not been used in version 1.0 of the Web Curator Tool and must 
 * be thoroughly tested before use.
 * @author bbeaumont
 *
 */
public class LockManager {
	/** The map of locked objects and who they are locked by */
	private Map<LockSubject,String> locksByObject = new HashMap<LockSubject,String>();
	/** The map of users and their locked object */
	private Map<String, HashSet<LockSubject>> locksByUser = new HashMap<String, HashSet<LockSubject>>();
	
	/**
	 * Acquire a lock for the given subject.
	 * @param subject A populated LockSubject for the object on which to acquire the lock.
	 * @param username The name of the user wanting to acquire the lock.
	 * @return true the the lock was acquired; false if it was not.
	 */
	public synchronized boolean acquireLock(LockSubject subject, String username) {
		if( locksByObject.containsKey(subject)) {
			return locksByObject.get(subject).equals(username);
		}
		else {
			// Add to the object map.
			locksByObject.put(subject, username);
			
			// Add to the user set.
			HashSet<LockSubject> userLocks = locksByUser.get(username);
			if(userLocks==null) {
				userLocks = new HashSet<LockSubject>();
				locksByUser.put(username, userLocks);
			}
			userLocks.add(subject);
			
			return true;
		}
	}	
	
	/**
	 * Acquire a lock on the given object.
	 * @param target   The item to lock.
	 * @param username The name of the use who wants the lock.
	 * @return true if the lock was acquired; otherwise false.
	 */
	public synchronized boolean acquireLock(Lockable target, String username) {
		return acquireLock(new LockSubject(target), username);
	}
	
	/**
	 * Acquire the lock on the populated LockSubject object. The username
	 * is determined from the AuthUtil class.
	 * 
	 * @see AuthUtil#getRemoteUser()
	 * 
	 * @param subject A LockSubject representing the object to be locked.
	 * @return true if the lock was acquired; otherwise false.
	 */
	public synchronized boolean acquireLock(LockSubject subject) {
		return acquireLock(subject, AuthUtil.getRemoteUser());
	}
	
	/**
	 * Acquire a lock on the object with the given classname and OID.
	 * @param clazz The class on which to acquire the lock.
	 * @param oid   The OID of which to acquire the lock.
	 * @return true if the lock was acquired; otherwise false.
	 */
	public synchronized boolean acquireLock(Class clazz, Long oid) {
		return acquireLock(new LockSubject(clazz, oid), AuthUtil.getRemoteUser());
	}	
	
	/**
	 * Get the user who currently has the object locked.
	 * @param target The locked object. 
	 * @return The username of the user holding the lock; null if not locked.
	 */
	public String getLockOwner(Lockable target) {
		return locksByObject.get(new LockSubject(target));
	}
	
	/**
	 * Get the user who currently has the object locked.
	 * @param clazz The classname of the locked object.
	 * @param oid   The OID of the locked object. 
	 * @return The username of the user holding the lock; null if not locked.
	 */
	public String getLockOwner(Class clazz, Long oid) {
		return locksByObject.get(new LockSubject(clazz, oid));
	}	
	
	/**
	 * Release all locks held by the user. This is to be called when their
	 * session is destroyed to ensure that they do not hold locks 
	 * unnecessarily.
	 * @param owner The user to release the locks for.
	 */
	public void releaseLocksForOwner(String owner) {
		HashSet<LockSubject> locks = locksByUser.get(owner);
		
		if( locks != null) {
			for(LockSubject ls: locks) {
				locksByObject.remove(ls);
			}
			locksByUser.remove(owner);
		}
	}
	
	/**
	 * Release the lock on the target.
	 * @param target The object on which to release the lock.
	 */
	public void releaseLock(Lockable target) {
		LockSubject subject = new LockSubject(target);
		String username = locksByObject.remove(subject);
		locksByUser.get(username).remove(subject);
	}
}
