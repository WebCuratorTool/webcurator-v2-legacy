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
package org.webcurator.core.archive; 

import java.util.List; 
import java.util.Map;

import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;

/**
 * The top level interface to submit results to a archival repository
 * Implemented for any specific repositories. 
 * @author AParker
 */
public interface Archive {
	/**
	 * @param targetInstanceOID The target instance oid 
	 * @param SIP The METS xml structure for completion and archival
	 * @param xAttributes Any extra attributes that may be required for archival
	 * @param fileList A list of files (@see org.webcurator.core.archive.ArchiveFile) to archive 
	 * @return A unique archive identifier
	 * @throws DigitalAssetStoreException
	 */
	public String submitToArchive(String targetInstanceOID, String SIP, Map xAttributes, List<ArchiveFile> fileList)throws DigitalAssetStoreException;

	/**
	 * Determine whether a custom deposit form is required to be shown before
	 * submitting a harvest to a specific digital asset store.
	 * 
	 * @param criteria Provides parameters that are required for DAS to make this decision.
	 * @return The response DTO that indicates whether a custom form is required, 
	 * and if so, the location/content of the custom form.
	 * @throws Exception thrown if there is an error
	 */
	public CustomDepositFormResultDTO getCustomDepositFormDetails(CustomDepositFormCriteriaDTO criteria)throws DigitalAssetStoreException;
}
