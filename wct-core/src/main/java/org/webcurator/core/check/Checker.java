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
package org.webcurator.core.check;

/**
 * The Checker interface specifies the methods that must be implemented by a 
 * system check process. 
 * @author nwaight
 */
public interface Checker {
	/** Constant for a warning level message. */
	String LEVEL_WARNING = "Warning";
	/** Constant for a error level message. */
	String LEVEL_ERROR = "Error";
	
	/**
	 * Perform the check process.
	 */
	void check();
	
	/** 
	 * @param aSubject the subject of the check notification message.
	 */
	void setNotificationSubject(String aSubject);
	
	/** 
	 * @param aType the type of the check.
	 */
	void setCheckType(String aType);
	
	/**  
	 * @param aNotifier the notifier used to send the notification messages. 
	 */
	void setNotifier(CheckNotifier aNotifier);
}
