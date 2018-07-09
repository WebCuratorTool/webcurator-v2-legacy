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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.notification.MessageType;

/**
 * The abstract base class for all Checkers.
 * @author nwaight
 */
public abstract class AbstractChecker implements Checker {
	/** subject to prepend to the notification. */
	private String notificationSubject = "";
	/** the type of this checker. */
	private String checkType = "";
	/** the object to use to send notifications to the core. */
	private CheckNotifier notifier = null;
	/** the logger. */
	private static Log log = LogFactory.getLog(AbstractChecker.class);
	
	/**
	 * @see org.webcurator.core.check.Checker#check()
	 */
	public abstract void check();
	
	/**
	 * @see org.webcurator.core.check.Checker#setCheckType(java.lang.String)
	 */
	public void setCheckType(String aType) {
		checkType = aType;
	}
	
	/**
	 * @see org.webcurator.core.check.Checker#setNotificationSubject(java.lang.String)
	 */
	public void setNotificationSubject(String aSubject) {
		notificationSubject = aSubject;
	}
	
	/**
	 * @see org.webcurator.core.check.Checker#setNotifier(org.webcurator.core.check.CheckNotifier)
	 */
	public void setNotifier(CheckNotifier aNotifier) {
		this.notifier = aNotifier;
	}

	/**
	 * Send a notification to the core using the notifier.
	 * @param aLevel the level of the notification
	 * @param aMessage the message
	 */
	protected void notify(String aLevel, String aMessage) {
		try {
			notifier.notification(notificationSubject + " " + aLevel + " - " + checkType, MessageType.CATEGORY_HARVESTER_WARNING, aMessage);
		} 
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Failed to send notification to core : " + e.getMessage(), e);
			}
		}
	}
}
