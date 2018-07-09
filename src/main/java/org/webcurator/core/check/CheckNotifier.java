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
 * The CheckNotifier is responsible for sending notification messages 
 * back to the core from the check process.
 * @author nwaight
 */
public interface CheckNotifier {
	/**
     * Notify the WCT that check event has occurred that may be of interest.
     * @param aSubject the subject of the message
     * @param notificationCategory The category of the notification.
     * @param aMessage the message associated with the event 
     */
    void notification(String aSubject, int notificationCategory, String aMessage);
}
