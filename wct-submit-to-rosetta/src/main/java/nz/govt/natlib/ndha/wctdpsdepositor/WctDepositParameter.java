/**
 * nz.govt.natlib.ndha.wctdpsdepositor - Software License
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

package nz.govt.natlib.ndha.wctdpsdepositor;

import org.apache.commons.lang.StringUtils;

/**
 * This class is a 'Parameter Object' that contains the various data required by the DPS deposit library.
 */
public class WctDepositParameter {
    private String pdsUrl;
    private String ftpHost;
    private String ftpUserName;
    private String ftpPassword;
    private String ftpDirectory;
    private String dpsWsdlUrl;

    private String dpsInstitution;
    private String dpsUserName;
    private String dpsPassword;

    private String materialFlowId;
    private String producerId;
    
    private String omsOpenAccess = "";
    private String omsPublishedRestricted = "";
    private String omsUnpublishedRestrictedByLocation = "";
    private String omsUnpublishedRestrictedByPersion = "";

    private String dcTitleSource = "";

    
    public void isValid() throws WctDepositParameterValidationException {
        isFieldValid("PDS URL", pdsUrl);
        isFieldValid("FTP host", ftpHost);
        isFieldValid("FTP user name", ftpUserName);
        isFieldValid("FTP password", ftpPassword);
        isFieldValid("DPS WSDL URL", dpsWsdlUrl);

        isFieldValid("DPS institution", dpsInstitution);
        isFieldValid("DPS user name", dpsUserName);
        isFieldValid("DPS password", dpsPassword);

        isFieldValid("material flow ID", materialFlowId);
        isFieldValid("producer ID", producerId);
    }

    private void isFieldValid(String displayableFieldName, String field) {
        if (StringUtils.isBlank(field))
            throw new WctDepositParameterValidationException("The property " + displayableFieldName + " was not populated, the DPS deposit service requires a value for this property to be specified.");
    }
    
    public String getPdsUrl() {
        return pdsUrl;
    }

    public void setPdsUrl(String pds_url) {
        this.pdsUrl = pds_url;
    }

    public String getFtpHost() {
        return ftpHost;
    }

    public void setFtpHost(String ftpHost) {
        this.ftpHost = ftpHost;
    }

    public String getFtpUserName() {
        return ftpUserName;
    }

    public void setFtpUserName(String ftpUserName) {
        this.ftpUserName = ftpUserName;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public String getFtpDirectory() {
        return ftpDirectory;
    }

    public void setFtpDirectory(String ftpDirectory) {
        this.ftpDirectory = ftpDirectory;
    }

    public String getDpsWsdlUrl() {
        return dpsWsdlUrl;
    }

    public void setDpsWsdlUrl(String dpsWsdlUrl) {
        this.dpsWsdlUrl = dpsWsdlUrl;
    }


    public String getDpsInstitution() {
        return dpsInstitution;
    }

    public void setDpsInstitution(String dpsInstitution) {
        this.dpsInstitution = dpsInstitution;
    }

    public String getDpsUserName() {
        return dpsUserName;
    }

    public void setDpsUserName(String dpsUserName) {
        this.dpsUserName = dpsUserName;
    }

    public String getDpsPassword() {
        return dpsPassword;
    }

    public void setDpsPassword(String dpsPassword) {
        this.dpsPassword = dpsPassword;
    }

    public String getMaterialFlowId() {
        return materialFlowId;
    }

    public void setMaterialFlowId(String materialFlowId) {
        this.materialFlowId = materialFlowId;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }
    
    public String getOmsOpenAccess() {
        return omsOpenAccess;
    }
    
    public void setOmsOpenAccess(String omsOpenAccess){
    	if(omsOpenAccess != null)
    		this.omsOpenAccess = omsOpenAccess;
    }

	public String getOmsPublishedRestricted() {
		return omsPublishedRestricted;
	}
	
	public void setOmsPublishedRestricted(String omsPublishedRestricted){
		if(omsPublishedRestricted != null)
			this.omsPublishedRestricted = omsPublishedRestricted;
    }

	public String getOmsUnpublishedRestrictedByLocation() {
		return omsUnpublishedRestrictedByLocation;
	}
	
	public void setOmsUnpublishedRestrictedByLocation(String omsUnpublishedRestrictedByLocation){
		if(omsUnpublishedRestrictedByLocation != null)
			this.omsUnpublishedRestrictedByLocation = omsUnpublishedRestrictedByLocation;
    }

	public String getOmsUnpublishedRestrictedByPersion() {
		return omsUnpublishedRestrictedByPersion;
	}
	
	public void setOmsUnpublishedRestrictedByPersion(String omsUnpublishedRestrictedByPersion){
		if(omsUnpublishedRestrictedByPersion != null)
			this.omsUnpublishedRestrictedByPersion = omsUnpublishedRestrictedByPersion;
    }

    public String getDCTitleSource() {return dcTitleSource;}

    public void setDCTitleSource(String setDCTitleSource) {this.dcTitleSource = dcTitleSource;}

}
