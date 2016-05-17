/**
 * org.webcurator.core.archive.dps - Software License
 *
 * Copyright 2007/2009 National Library of New Zealand.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * or the file "LICENSE.txt" included with the software.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package org.webcurator.core.archive.dps;

import java.io.File;
import java.util.List;
import java.util.Map;


public interface DpsDepositFacade {
	/**
	 * The different types of web harvests that can be deposited to DPS/Rosetta.
	 * Based on the type, some of the parameters such as DC/DNX metadata and the 
	 * producer information may change.
	 *  
	 * @author pushpar
	 *
	 */
	public enum HarvestType {
		TraditionalWebHarvest,
		HtmlSerialHarvest,
		CustomWebHarvest
	};

	/*
	 * Used as keys in the parameter map when a client calls the deposit() method
	 */
	public static final String DPS_WSDL_URL = "DpsWsdlUrl";
	public static final String DPS_INSTITUTION = "DpsInstitution";
	public static final String DPS_USER_NAME = "DpsUserName";
	public static final String DPS_PASSWORD = "DpsPassword";
	public static final String FTP_HOST = "FtpHost";
	public static final String FTP_PASSWORD = "FtpPassword";
	public static final String FTP_USER_NAME = "FtpUserName";
	public static final String FTP_DIRECTORY = "FtpDirectory";
	public static final String MATERIAL_FLOW_ID = "MaterialFlowId";
	public static final String IE_ENTITY_TYPE = "IEEntityType";
	public static final String TITLE_SOURCE = "DCTitleSource";
	public static final String PDS_URL = "PdsUrl";
	public static final String PRODUCER_ID = "ProducerId";
	public static final String ILS_REFERENCE = "IlsReference";
	public static final String ACCESS_RESTRICTION = "AccessRestriction";
	public static final String TARGET_INSTANCE_ID = "TargetInstanceId";
	public static final String WCT_METS_XML_DOCUMENT = "wctMetsXmlDocument";
	public static final String HARVEST_TYPE = "HarvestType";
	public static final String DCTERMS_BIBLIOGRAPHIC_CITATION = "DctermsBibliographicCitation";
	public static final String DCTERMS_AVAILABLE = "DctermsAvailable";
	public static final String OMS_OPEN_ACCESS = "omsOpenAccess";
	public static final String OMS_PUBLISHED_RESTRICTED = "omsPublishedRestricted";
	public static final String OMS_UNPUBLISHED_RESTRICTED_BY_LOCATION = "omsUnpublishedRestrictedByLocation";
	public static final String OMS_UNPUBLISHED_RESTRICTED_BY_PERSON = "omsUnpublishedRestrictedByPersion";
	public static final String CMS_SECTION = "cmsSection";
	public static final String CMS_SYSTEM = "cmsSystem";
	
	/**
	 * Interface to the information returned by the DPS Deposit service.
	 */
	public interface DepositResult {
		public boolean isError();
		public String getMessageCode();
		public String getMessageDesciption();
		public long getSipId();
		public long getDepositActivityId();
		public String getUserParameters();
		public String getCreationDate();
	}

	/**
	 * Deposits a web harvest into Rosetta (DPS) and returns the status.
	 * 
	 * @param parameters
	 * @param fileList
	 * @return
	 * @throws RuntimeException
	 */
	public DepositResult deposit(Map<String, String> parameters, List<File> fileList) throws RuntimeException;

	public String loginToPDS(Map<String, String> parameters) throws RuntimeException;
    
}
