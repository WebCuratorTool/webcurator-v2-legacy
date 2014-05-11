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

import java.util.ArrayList;
import java.util.List;

import org.webcurator.core.notification.InTrayManager;
import org.webcurator.domain.model.auth.Privilege;

/**
 * The CheckNotifier used in the Core.
 * @author nwaight
 */
public class CoreCheckNotifier implements CheckNotifier {
	/** The intray manager to use to send notifications. */
	InTrayManager inTrayManager;
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.check.CheckNotifier#notification(java.lang.String, java.lang.String)
	 */
	public void notification(String aSubject, int notificationCategory, String aMessage) {
		List<String> privs = new ArrayList<String>();
        privs.add(Privilege.MANAGE_WEB_HARVESTER);
        
		inTrayManager.generateNotification(privs, notificationCategory, aSubject, aMessage);
	}

	/**
	 * @param inTrayManager the inTrayManager to set
	 */
	public void setInTrayManager(InTrayManager inTrayManager) {
		this.inTrayManager = inTrayManager;
	}
}
