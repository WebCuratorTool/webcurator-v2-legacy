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
package org.webcurator.core.harvester.coordinator;

/**
 * Callback interface for the DAS. The DAS calls this interface when it
 * has completed/failed to archive a harvest.
 * 
 * @author beaumontb
 *
 */
public interface DasCallback {
	/**
	 * Advises the Core that the harvest has been archived successfully.
	 * @param targetInstanceOid The OID of the instance being archived.
	 * @param archiveIID The IID returned by the archive system.
	 */
	public void completeArchiving(Long targetInstanceOid, String archiveIID);

	/**
	 * Advises the Core taht the harvest failed to be archived.
	 * @param targetInstanceOid The OID of the instance being archived.
	 * @param message The error message received from the archive.
	 */
	public void failedArchiving(Long targetInstanceOid, String message);
}
