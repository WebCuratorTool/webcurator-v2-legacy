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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;



import org.webcurator.core.archive.BaseArchive;
import org.webcurator.core.archive.ArchiveFile;
import static org.webcurator.core.archive.Constants.ACCESS_RESTRICTION;
import static org.webcurator.core.archive.Constants.HARVEST_TYPE;
import static org.webcurator.core.archive.Constants.REFERENCE_NUMBER;

import nz.govt.natlib.ndha.wctdpsdepositor.DpsDepositProxy;
import nz.govt.natlib.ndha.wctdpsdepositor.CustomDepositFormMapping;
import nz.govt.natlib.ndha.wctdpsdepositor.CustomDepositField;
import org.webcurator.core.archive.SIPUtils;
import org.webcurator.core.archive.dps.DpsDepositFacade.DepositResult;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;

import com.exlibris.digitool.deposit.service.xmlbeans.DepositDataDocument;
import com.exlibris.digitool.deposit.service.xmlbeans.DepositDataDocument.DepositData;
//import com.exlibris.digitool.locator.WebServiceLocator;
import com.exlibris.dps.sdk.producer.ProducerWebServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;



/**
 * A specific DPS-based archiver for the National Library of New Zealand's DPS archival system.
 *
 * @author Nicolai Moles-Benfell
 */
public class DPSArchive extends BaseArchive {

    /**
     * A very light-weight version of the com.exlibris.digitool.deposit.service.xmlbeans.DepData
     * class, so that this can be stored in the cache much more efficiently and with less
     * memory. 
     * @author pushpar
     *
     */
    public static class DepData {
        public final String id;
        public final String description;
        protected DepData(String id, String description) {
            this.id = id;
            this.description = description;
        }
    }
    private static Log log = LogFactory.getLog(DPSArchive.class);

    private static Cache producerCache;
    private java.lang.String pdsUrl;
    private java.lang.String ftpHost;
    private java.lang.String ftpUserName;
    private java.lang.String ftpPassword;
    private java.lang.String ftpDirectory;
    private java.lang.String depositServerBaseUrl;
    private java.lang.String producerWsdlRelativePath;
    private java.lang.String depositWsdlRelativePath;
    private java.lang.String producerWsdlUrl;
    private java.lang.String dpsUserInstitution;
    private java.lang.String dpsUserName;
    private java.lang.String dpsUserPassword;
    private java.lang.String materialFlowId;
    private java.lang.String producerId;
    private java.lang.String omsOpenAccess;  
    private java.lang.String omsPublishedRestricted = "";
	private java.lang.String omsUnpublishedRestrictedByLocation = "";
    private java.lang.String omsUnpublishedRestrictedByPersion = "";
    private java.lang.String cmsSection = "";
    private java.lang.String cmsSystem = "";
    private List<String> targetDCTypesOfCustomWebHarvest = new ArrayList<String>();
    private List<String> materialFlowsOfCustomWebHarvest = new ArrayList<String>();
    private List<String> ieEntityTypesOfCustomWebHarvest = new ArrayList<String>();
    private List<String> DCTitleSourceOfCustomWebHarvest = new ArrayList<String>();
    private List<String> agenciesResponsibleForHtmlSerials = new ArrayList<String>();
    private List<String> targetDCTypesOfHtmlSerials = new ArrayList<String>();
    private List<String> materialFlowsOfHtmlSerials = new ArrayList<String>();
    private List<String> producerIdsOfHtmlSerials = new ArrayList<String>();
    private List<String> ieEntityTypesOfHtmlSerials = new ArrayList<String>();
    private List<String> customDepositFormURLsForHtmlSerialIngest;
//    private Map<String, Map<String, String>> customDepositFormFieldMaps = new HashMap<String, Map<String, String>>();
    private CustomDepositFormMapping customDepositFormMapping;

    private static final String DPS_SIPID_PREFIX = "dps-sipid-";

    static {
        // Set up ehcache for the use of custom deposit form to store mapping between producer agents and producers
        try {
            String cacheName = "RosettaProducerCache";
            int maxElementsInMemory = 500; // Hopefully, there are only these many producer agents using this custom form facility
            boolean overflowToDisk = false;
            boolean eternal = false;
            long timeToLiveSeconds = 604800; // Each object will live for 7 days == 7 * 60 * 60 * 24 seconds
            long timeToIdleSeconds = 0;
            producerCache = new Cache(cacheName, maxElementsInMemory, overflowToDisk, eternal, timeToLiveSeconds, timeToIdleSeconds);
            CacheManager cacheManager = CacheManager.create();
            cacheManager.addCache(producerCache);
        } catch (Exception e) {
            log.error("Error creating an ehCache for producer/producer agent caching", e);
        }
    }
    
    /**
     * @param targetInstanceOID The target instance oid
     * @param SIP               The METS xml structure for completion and archival
     * @param xAttributes       Any extra attributes that may be required for archival (generally contains parameters for OMS meta-data)
     * @param fileList          A list of files (@see org.webcurator.core.archive.ArchiveFile) to archive
     * @return A unique archive identifier (IID returned form the OMS system)
     * @throws DPSUploadException
     */
    @SuppressWarnings("unchecked")
    public String submitToArchive(String targetInstanceOID, String SIP, Map xAttributes, List<ArchiveFile> fileList) throws DPSUploadException {
        String IID = null;

        try {
            if (targetInstanceOID != null) {
                for(ArchiveFile f : fileList){
                    f.setMd5(calculateMD5(f.getFile()));
                }
                String finalSIP = getFinalSIP(SIP, targetInstanceOID, fileList);

                DpsDepositProxy dpsDeposit = getDpsDepositFacade();

                List<File> files = extractFileDetailsFrom(fileList);

                Map<String, String> parameters = populateDepositParameterFromFields(xAttributes, finalSIP, targetInstanceOID);

                dpsDeposit.setCustomDepositFormMapping(customDepositFormMapping);

                DepositResult depositResult = dpsDeposit.deposit(parameters, files);

                if (depositResult.isError())
                    throw new DPSUploadException("Submission to DPS failed for Target Instance " + targetInstanceOID 
                            + ", message from DPS: " + depositResult.getMessageDesciption());

                IID = DPS_SIPID_PREFIX + Long.toString(depositResult.getSipId());
            }
            return IID;
        }
        catch (Exception ex) {
            log.error("Error submitting to DPS", ex);
            throw new DPSUploadException(ex);
        }

    }

    public CustomDepositFormResultDTO getCustomDepositFormDetails(CustomDepositFormCriteriaDTO criteria) {
        log.debug("DPSArchive: getCustomDepositFormDetails() invoked with " + criteria);
        CustomDepositFormResultDTO response = new CustomDepositFormResultDTO();
        if (criteria == null) return response; 
        /*
         * Current logic of determining whether there is a need to fill a custom deposit form:
         * 
         * A custom deposit form is required if:
         * - You are archiving a harvest of (target) type "HTML Serial", as
         *   dictated by the configured target type names in targetTypesOfHtmlSerials.
         * OR,
         * - Your login id belongs to the agency responsible for harvesting/archiving 
         *   HTML Serials, as dictated by the configured agency names in
         *   agenciesResponsibleForHtmlSerials.
         * 
         * Also, if your login belongs to the agency responsible for harvesting/archiving
         * HTML Serials and if you are NOT archiving a harvest of (target) type "HTML Serial",
         * the custom form selected will be an "Error" custom form which is configured in 
         * the system as rosetta_custom_deposit_form_invalid_dctype.jsp
         * 
         * Note: All comparisons of target types or agency names are case-insensitive.
         */
        int targetTypeIndex = getIndexInList(criteria.getTargetType(), targetDCTypesOfHtmlSerials);
        boolean isAgencyResponsibleForHtmlSerials = containsInList(criteria.getAgencyName(), agenciesResponsibleForHtmlSerials);
        boolean isTargetDCTypeOfHtmlSerials = (targetTypeIndex >= 0);
        response.setCustomDepositFormRequired(false);
        if (isTargetDCTypeOfHtmlSerials) {
            /*
             * Target type is of HTML Serial - will show custom form irrespective of which agency you belong to.
             * The selected custom form will be based on the type of target, as configured in the wct-das.xml
             * and wct-das.properties.
             */
            response.setCustomDepositFormRequired(true);
            response.setUrlForCustomDepositForm(customDepositFormURLsForHtmlSerialIngest.get(targetTypeIndex));
            response.setProducerId(getProdcuerIdOfTargetDCType(criteria.getTargetType()));
        } else {
            /*
             * Do not show custom form if you do not belong to the agency responsible for HTML serials.
             * But if you do belong, then show an "invalid/error" page. 
             */
            if (isAgencyResponsibleForHtmlSerials) {
                response.setCustomDepositFormRequired(true);
                response.setUrlForCustomDepositForm("/wct-store/customDepositForms/rosetta_custom_deposit_form_invalid_dctype.jsp");
            }
        }
        return response;
    }

    public boolean validateProducerAgentName(String producerAgent) {
        try {
            ProducerWebServices _pws = getProducerWebServices();
            String producerAgentId = _pws.getInternalUserIdByExternalId(producerAgent);
            if (producerAgentId == null) return false;
            else return true;
        } catch (Exception e) {
            log.error("Error getting producer agent id for agent user name " + producerAgent + " from URL " + producerWsdlUrl, e);
            return false;
        }
    }

    public boolean isLoginSuccessful(String producerAgent, String agentPassword) {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put(DpsDepositFacade.DPS_INSTITUTION, this.dpsUserInstitution);
        parameterMap.put(DpsDepositFacade.DPS_USER_NAME, producerAgent);
        parameterMap.put(DpsDepositFacade.DPS_PASSWORD, agentPassword);
        parameterMap.put(DpsDepositFacade.PDS_URL, this.pdsUrl);
        DpsDepositFacade dpsDeposit = getDpsDepositFacade();
        String pdsSessionId = null;
        try {
            pdsSessionId = dpsDeposit.loginToPDS(parameterMap);
        } catch (Exception e) {
            log.error("Error logging in to PDS for agent " + producerAgent + ". Exception: " + e);
        }
        if (pdsSessionId == null) return false;
        return true;
    }

    public DepData[] getProducer(String producerAgentUserId, boolean fromCache) {
        if (fromCache) {
            DepData[] depData = getProducerDataFromCache(producerAgentUserId);
            if (depData != null && depData.length > 0) {
                log.debug("Retrieved producer data from ehcache for producer agent " + producerAgentUserId);
                return depData;
            }
        }
        try {
            ProducerWebServices _pws = getProducerWebServices();
            String producerAgentId = _pws.getInternalUserIdByExternalId(producerAgentUserId);
            log.debug("Producer agent id for the user id " + producerAgentUserId + ": " + producerAgentId);
            if (producerAgentId == null) return null;
            String xmlReply = _pws.getProducersOfProducerAgent(producerAgentId);
            DepData[] depData = xmlReplyToDepData(xmlReply);
            putProducerDataToCache(producerAgentUserId, depData);
            if (log.isDebugEnabled()) {
                log.debug("Producers for agent " + producerAgentId + ":");
                for (DepData data : depData) {
                    log.debug("\tdata.id = " + data.id + ", description: " + data.description);
                }
            }
            return depData;
        } catch (Exception e) {
            log.error("Error getting producers for agent " + producerAgentUserId + " from URL " + producerWsdlUrl, e);
        }
        return null;
    }

    public DepData[] getMaterialFlows(String producerID) {
        try {
            ProducerWebServices _pws = getProducerWebServices();
            String xmlReply = _pws.getMaterialFlowsOfProducer(producerID);
            DepData[] depData = xmlReplyToDepData(xmlReply);
            if (log.isDebugEnabled()) {
                log.debug("Material flows for producers " + producerID + ":");
                for (DepData data : depData) {
                    log.debug("\tdata.id = " + data.id + ", description: " + data.description);
                }
            }
            return depData;
        } catch (Exception e) {
            log.error("Error getting material flows for producer " + producerID  + " from URL " + producerWsdlUrl, e);
        }
        return null;
    }

    public boolean validateMaterialFlowAssociation(String producerId, String targetDcType) {
        String materialFlowOfTargetDcType = getMaterialFlowOfTargetDCType(targetDcType);
        if (materialFlowOfTargetDcType == null || materialFlowOfTargetDcType.length() <= 0)
            return false;
        DepData[] materialFlows = getMaterialFlows(producerId);
        if (materialFlows == null || materialFlows.length <=0) {
            log.error("Could not get any material flows from Rosetta for producer " + producerId + " using URL " + producerWsdlUrl);
            return false;
        }
        for (int i = 0; i < materialFlows.length; i++) {
            DepData anMF = materialFlows[i];
            if (materialFlowOfTargetDcType.equals(anMF.id)) {
                return true;
            }
        }
        return false;
    }

    private String getMaterialFlowOfTargetDCType(String targetDcType) {
        return locatePropertyAgainstTargetDCType(targetDcType, targetDCTypesOfHtmlSerials, materialFlowsOfHtmlSerials);
    }

    private String getProdcuerIdOfTargetDCType(String targetDcType) {
        return locatePropertyAgainstTargetDCType(targetDcType, targetDCTypesOfHtmlSerials, producerIdsOfHtmlSerials);
    }

    private String getIeEntityTypeOfTargetDCType(String targetDcType) {
        return locatePropertyAgainstTargetDCType(targetDcType, targetDCTypesOfHtmlSerials, ieEntityTypesOfHtmlSerials);
    }

    private String getMaterialFlowOfCustomTargetDCType(String targetDcType) {
        return locatePropertyAgainstTargetDCType(targetDcType, targetDCTypesOfCustomWebHarvest, materialFlowsOfCustomWebHarvest);
    }

    private String getIeEntityTypeOfCustomTargetDCType(String targetDcType) {
        return locatePropertyAgainstTargetDCType(targetDcType, targetDCTypesOfCustomWebHarvest, ieEntityTypesOfCustomWebHarvest);
    }

    private String getDCTitleSourceOfCustomTargetDCType(String targetDcType) {
        return locatePropertyAgainstTargetDCType(targetDcType, targetDCTypesOfCustomWebHarvest, DCTitleSourceOfCustomWebHarvest);
    }

    private String locatePropertyAgainstTargetDCType(String targetDcType, List<String> indexList, List<String> propertyList) {
        String propertyAgainstTargetDCType = null;
        int targetTypeIndex = getIndexInList(targetDcType, indexList);
        if (targetTypeIndex < 0) {
            log.error("DC Type " + targetDcType + " of the target instance is not of an HTML serial type");
            return null;
        }
        if(propertyList.isEmpty()){
            log.info("DC Type " + targetDcType + " of the target instance does not have a preset Producer Id");
            return null;
        }
        try {
            propertyAgainstTargetDCType = propertyList.get(targetTypeIndex);
        } catch (Exception ex) {
            log.error("Error getting a property corresponding to the DC Type " + targetDcType + " from list " + propertyList, ex);
        }
        return (propertyAgainstTargetDCType == null) ? null: propertyAgainstTargetDCType.trim();
    }

    private DepData[] xmlReplyToDepData(String xmlReply) throws XmlException {
        DepositDataDocument depositReply = DepositDataDocument.Factory.parse(xmlReply);
        DepositData depositData = depositReply.getDepositData();
        DepData[] depData = sortByDescription(depositData);
        return depData;
    }

    private ProducerWebServices getProducerWebServices() {
        synchronized(this) {
            if (producerWsdlUrl == null) {
                if (this.depositServerBaseUrl != null && producerWsdlRelativePath != null) {
                    producerWsdlUrl = this.depositServerBaseUrl + producerWsdlRelativePath;
                }
            }
        }
        /*
         * Ideally, we should be using the following call:
         * return WebServiceLocator.getInstance().lookUp(ProducerWebServices.class, producerWsdlUrl);
         * which uses the DPS SDK class com.exlibris.digitool.locator.WebServiceLocator.
         * 
         * However, the WebServiceLocator class from DPS SDK seems to be caching the web service
         * handle and this handle, when not used for several hours, seems to become stale, throwing
         * up exceptions.
         * 
         * So instead of using the DPS SDK's WebServiceLocator class, we are forced to use the 
         * following low-level logic to bind to the Producer web service. The following code 
         * has been created by copying the required code segment from the
         * WebServiceLocator.lookUp() method by ExLibris.
         */
        String serviceEndpointInterfaceName = ProducerWebServices.class.getSimpleName();
        URL wsdlUrl = null;
        String wsdlUrlStr = producerWsdlUrl;
        try {
            wsdlUrl = new URL(wsdlUrlStr);
        } catch(Exception e) {
            log.error("Failed to build WSDL URL from " + wsdlUrlStr + " - Web service lookup of " + serviceEndpointInterfaceName + " will fail", e);
            e.printStackTrace();
        }
        QName serviceName = new QName("http://dps.exlibris.com/", serviceEndpointInterfaceName);
        Service service = Service.create(wsdlUrl, serviceName);
        QName portName = new QName("http://dps.exlibris.com/", (new StringBuilder()).append(serviceEndpointInterfaceName).append("Port").toString());
        return service.getPort(portName, ProducerWebServices.class);
    }

    private DepData[] sortByDescription(DepositData depositData) {
        com.exlibris.digitool.deposit.service.xmlbeans.DepData[] depDataFromRosetta = depositData.getDepDataArray();
        if (depDataFromRosetta == null) return null;
        DepData depdata[] = new DepData[depDataFromRosetta.length];
        for (int i = 0; i < depDataFromRosetta.length; i++) {
            depdata[i] = new DepData(depDataFromRosetta[i].getId(), depDataFromRosetta[i].getDescription());
        }
        Arrays.sort(depdata, new DepDataComparator());
        return depdata;
    }

    private static DepData[] getProducerDataFromCache(String producerAgentUserId) {
        try {
            if (producerCache == null) return null;
            Element cacheElement;
            synchronized (producerCache) {
                cacheElement = producerCache.get(producerAgentUserId);
            }
            if (cacheElement == null) return null;
            return (DepData[]) cacheElement.getValue();
        } catch (Exception e) {
            log.warn("Error getting producer data from cache for the producer agent " + producerAgentUserId, e);
            return null;
        }
    }

    private static void putProducerDataToCache(String producerAgentUserId, DepData[] depData) {
        try {
            if (producerCache == null) return;
            Element cacheElement = new Element(producerAgentUserId, depData);
            synchronized (producerCache) {
                producerCache.put(cacheElement);
            }
        } catch (Exception e) {
            log.warn("Error putting producer data into cache for the producer agent " + producerAgentUserId, e);
        }
    }

    protected String calculateMD5(File file) throws FileNotFoundException {
        return FixityUtils.calculateMD5(file);
    }

    /**
     * Gets an instance of DpsDepositFacade. Make sure that the wct-submit-to-rosetta.jar
     * JAR file (created by building WCTSubmitToRosetta module) is in the class path.
     */
    protected DpsDepositProxy getDpsDepositFacade() {
        DpsDepositProxy dpsDepositProxy = new nz.govt.natlib.ndha.wctdpsdepositor.DpsDepositProxy();
        // Create a new DpsDepositFacade instance
        dpsDepositProxy.createInstance();
        return dpsDepositProxy;
    }

    protected List<File> extractFileDetailsFrom(List<ArchiveFile> fileList) {
        List<File> files = new ArrayList<File>(fileList.size());
        for (ArchiveFile archiveFile : fileList)
            files.add(archiveFile.getFile());
        return files;
    }

    protected String getFinalSIP(String sip, String targetInstanceOID, List<ArchiveFile> files) {
        return SIPUtils.finishSIP(sip, targetInstanceOID, files, true);
    }

    public void setPdsUrl(String pdsUrl) {
        this.pdsUrl = pdsUrl;
    }

    public void setFtpHost(String ftpHost) {
        this.ftpHost = ftpHost;
    }

    public void setFtpUserName(String ftpUserName) {
        this.ftpUserName = ftpUserName;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public void setFtpDirectory(String ftpDirectory) {
        this.ftpDirectory = ftpDirectory;
    }

    public void setDepositServerBaseUrl(String depositServerBaseUrl) {
        this.depositServerBaseUrl = depositServerBaseUrl;
    }

    public void setDpsUserInstitution(String dpsUserInstitution) {
        this.dpsUserInstitution = dpsUserInstitution;
    }

    public void setDpsUserName(String dpsUserName) {
        this.dpsUserName = dpsUserName;
    }

    public void setDpsUserPassword(String dpsUserPassword) {
        this.dpsUserPassword = dpsUserPassword;
    }

    public void setMaterialFlowId(String materialFlowId) {
        this.materialFlowId = materialFlowId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public void setProducerWsdlRelativePath(String producerWsdlRelativePath) {
        this.producerWsdlRelativePath = producerWsdlRelativePath;
    }

    public void setDepositWsdlRelativePath(String depositWsdlRelativePath) {
        this.depositWsdlRelativePath = depositWsdlRelativePath;
    }

    public void setAgenciesResponsibleForHtmlSerials(String agenciesResponsibleForHtmlSerials) {
        this.agenciesResponsibleForHtmlSerials = toListOfLowerCaseValues(agenciesResponsibleForHtmlSerials);
    }

    public void setTargetDCTypesOfHtmlSerials(String targetDCTypesOfHtmlSerials) {
        this.targetDCTypesOfHtmlSerials = toListOfLowerCaseValues(targetDCTypesOfHtmlSerials);
    }

    public void setMaterialFlowsOfHtmlSerials(String materialFlowsOfHtmlSerials) {
        this.materialFlowsOfHtmlSerials = toList(materialFlowsOfHtmlSerials);
    }

    public void setProducerIdsOfHtmlSerials(String producerIdsOfHtmlSerials) {
        this.producerIdsOfHtmlSerials = toList(producerIdsOfHtmlSerials);
    }

    public void setIeEntityTypesOfHtmlSerials(String ieEntityTypesOfHtmlSerials) {
        this.ieEntityTypesOfHtmlSerials = toList(ieEntityTypesOfHtmlSerials);
    }

    public void setCustomDepositFormURLsForHtmlSerialIngest(String customDepositFormURLsForHtmlSerialIngest) {
        this.customDepositFormURLsForHtmlSerialIngest = toList(customDepositFormURLsForHtmlSerialIngest);
    }

    public void setTargetDCTypesOfCustomWebHarvest(String targetDCTypesOfCustomWebHarvest) {
        this.targetDCTypesOfCustomWebHarvest = toListOfLowerCaseValues(targetDCTypesOfCustomWebHarvest);
    }

    public void setMaterialFlowsOfCustomWebHarvest(String materialFlowsOfCustomWebHarvest) {
        this.materialFlowsOfCustomWebHarvest = toList(materialFlowsOfCustomWebHarvest);
    }

    public void setIeEntityTypesOfCustomWebHarvest(String ieEntityTypesOfCustomWebHarvest) {
        this.ieEntityTypesOfCustomWebHarvest = toList(ieEntityTypesOfCustomWebHarvest);
    }

    public void setDCTitleSourceOfCustomWebHarvest(String DCTitleSourceOfCustomWebHarvest) {
        this.DCTitleSourceOfCustomWebHarvest = toList(DCTitleSourceOfCustomWebHarvest);
    }

    public void setOmsOpenAccess(String omsOpenAccess) {
        this.omsOpenAccess = omsOpenAccess;
    }
    
    public void setOmsPublishedRestricted(String omsPublishedRestricted) {
		this.omsPublishedRestricted = omsPublishedRestricted;
	}

	public void setOmsUnpublishedRestrictedByLocation(String omsUnpublishedRestrictedByLocation) {
		this.omsUnpublishedRestrictedByLocation = omsUnpublishedRestrictedByLocation;
	}

	public void setOmsUnpublishedRestrictedByPersion(String omsUnpublishedRestrictedByPersion) {
		this.omsUnpublishedRestrictedByPersion = omsUnpublishedRestrictedByPersion;
	}

    public void setCmsSection(String cmsSection) {
        this.cmsSection = cmsSection;
    }

    public void setCmsSystem(String cmsSystem) {
        this.cmsSystem = cmsSystem;
    }

    public void setCustomDepositFormMapping(CustomDepositFormMapping customDepositFormMapping) {
        this.customDepositFormMapping = customDepositFormMapping;
    }

    /**
     * For determining if a harvest is using a custom Target DC Type.
     *
     * @param harvestType the Target DC Type of the harvest
     * @return whether type is in Custom Target DC Types list
     */
    private boolean isCustomTargetDCType(String harvestType) {
        return containsInList(harvestType, targetDCTypesOfCustomWebHarvest);
    }

    /**
     * Converts a comma-separated string into a list of lower-case letter strings.
     * 
     * @param commaSeparatedString
     * @return
     */
    protected static List<String> toListOfLowerCaseValues(String commaSeparatedString) {
        List<String> theList = new ArrayList<String>();
        if (commaSeparatedString == null) return theList;
        
        String[] tokens = commaSeparatedString.split("[,]");
        for (int i = 0; i< tokens.length; i++) {
            String token = tokens[i];
            if (token == null || token.trim().length() <= 0) continue;
            theList.add(token.trim().toLowerCase());
        }
        return theList;
    }

    /**
     * Converts a comma-separated string into a list of strings.
     * 
     * @param commaSeparatedString
     * @return
     */
    protected static List<String> toList(String commaSeparatedString) {
        List<String> theList = new ArrayList<String>();
        if (commaSeparatedString == null) return theList;
        
        String[] tokens = commaSeparatedString.split("[,]");
        for (int i = 0; i< tokens.length; i++) {
            String token = tokens[i];
            if (token == null || token.trim().length() <= 0) continue;
            theList.add(token.trim());
        }
        return theList;
    }

    /**
     * Makes a case-insensitive search inside <code>aList</code> to see 
     * if <code>aValue</code> is present in the list, and returns the index
     * of the item. 
     * @param aList
     * @param aValue
     * @return Index of the item if it is present in the list, -1 otherwise
     */
    protected int getIndexInList(String aValue, List<String> aList) {
        if (aValue == null || aValue.trim().length() <= 0) return -1;
        if (aList == null || aList.isEmpty()) return -1;
        String aValueLC = aValue.toLowerCase();
        return aList.indexOf(aValueLC);
    }

    /**
     * Makes a case-insensitive search inside <code>aList</code> to see 
     * if <code>aValue</code> is present in the list. 
     * @param aList
     * @param aValue
     * @return
     */
    protected boolean containsInList(String aValue, List<String> aList) {
        return (getIndexInList(aValue, aList) >= 0) ? true : false;
    }

    protected Map<String, String> populateDepositParameterFromFields(Map<?, ?> attributes, String finalSIP, String targetInstanceOID) {
        Map<String, String> parameterMap = new HashMap<String, String>();

        /*
         * Add the parameters read from wct-das.xml file. However, if the
         * custom deposit form is filled by the user, get some of these 
         * parameters from what the user filled in the custom form.
         */
        String dpsUserNameToUse = this.dpsUserName;
        String dpsUserPasswordToUse = this.dpsUserPassword;
        String producerIdToUse = this.producerId;
        String materialFlowIdToUse = this.materialFlowId;
        String ieEntityTypeToUse = null;
        String dcTitleSourceToUse = null;
        if (Boolean.parseBoolean((String)attributes.get("customDepositForm_customFormPopulated"))) {

            int targetTypeIndex = getIndexInList((String) attributes.get(HARVEST_TYPE), targetDCTypesOfHtmlSerials);

            List<CustomDepositField> customDepositFormFieldMapping = null;
            String customFormURL = customDepositFormURLsForHtmlSerialIngest.get(targetTypeIndex);
            if(customDepositFormMapping.hasFormMapping(customFormURL)){
                customDepositFormFieldMapping = customDepositFormMapping.getFormMapping(customFormURL);
            }
            else {
                throw new RuntimeException("Could not retrieve the Custom Deposit Form mappings for " + customFormURL + ".");
            }


            /*
             * This is an HTML Serial Deposit harvest. So do not use the 
             * default values, but use those entered by the user
             */
            dpsUserNameToUse = (String) attributes.get("customDepositForm_producerAgent");
            dpsUserPasswordToUse = (String) attributes.get("customDepositForm_producerAgentPassword");
            producerIdToUse = (String) attributes.get("customDepositForm_producerId");
            String targetDcType = (String) attributes.get("customDepositForm_targetDcType");
            materialFlowIdToUse = getMaterialFlowOfTargetDCType(targetDcType);
            ieEntityTypeToUse = getIeEntityTypeOfTargetDCType(targetDcType);

            // If the custom deposit form is filled, this is an HTML Serial harvest.
            parameterMap.put(DpsDepositFacade.HARVEST_TYPE, DpsDepositFacade.HarvestType.HtmlSerialHarvest.name());
            parameterMap.put(DpsDepositFacade.CUSTOM_DEPOSIT_FORM_URL, customDepositFormURLsForHtmlSerialIngest.get(targetTypeIndex));

            // Capture Dublin Core values in the parameters map
            for(CustomDepositField field : customDepositFormFieldMapping){
                parameterMap.put(field.getFieldReference(), (String) attributes.get(field.getFormFieldLabel()));
            }
//            parameterMap.put(DpsDepositFacade.DCTERMS_BIBLIOGRAPHIC_CITATION, (String) attributes.get("customDepositForm_bibliographicCitation"));
////            parameterMap.put(DpsDepositFacade.DCTERMS_ACCRUAL_PERIODICITY, (String) attributes.get("customDepositForm_dctermsAccrualPeriodicity"));
//            parameterMap.put(DpsDepositFacade.DCTERMS_AVAILABLE, (String) attributes.get("customDepositForm_dctermsAvailable"));
////            parameterMap.put(DpsDepositFacade.DCTERMS_ISSUED, (String) attributes.get("customDepositForm_dctermsIssued"));
        } else {


            // Check if the Harvest Type matches any of the Custom Types
            if(isCustomTargetDCType((String) attributes.get(HARVEST_TYPE))){
                // Get custom target DC type
                 String targetDcType = (String) attributes.get(HARVEST_TYPE);
                 materialFlowIdToUse = getMaterialFlowOfCustomTargetDCType(targetDcType);
                 ieEntityTypeToUse = getIeEntityTypeOfCustomTargetDCType(targetDcType);
                 dcTitleSourceToUse= getDCTitleSourceOfCustomTargetDCType(targetDcType);
                if(dcTitleSourceToUse == null) dcTitleSourceToUse = "";
                parameterMap.put(DpsDepositFacade.HARVEST_TYPE, DpsDepositFacade.HarvestType.CustomWebHarvest.name());
            }
            else {
                // The custom deposit form is not filled AND is not a Custom Target DC Type; So this is a traditional web harvest.
                parameterMap.put(DpsDepositFacade.HARVEST_TYPE, DpsDepositFacade.HarvestType.TraditionalWebHarvest.name());
            }
        }
        parameterMap.put(DpsDepositFacade.DPS_INSTITUTION, this.dpsUserInstitution);
        parameterMap.put(DpsDepositFacade.DPS_USER_NAME, dpsUserNameToUse);
        parameterMap.put(DpsDepositFacade.DPS_PASSWORD, dpsUserPasswordToUse);
        parameterMap.put(DpsDepositFacade.FTP_HOST, this.ftpHost);
        parameterMap.put(DpsDepositFacade.FTP_PASSWORD, this.ftpPassword);
        parameterMap.put(DpsDepositFacade.FTP_USER_NAME, this.ftpUserName);
        parameterMap.put(DpsDepositFacade.FTP_DIRECTORY, this.ftpDirectory);
        parameterMap.put(DpsDepositFacade.MATERIAL_FLOW_ID, materialFlowIdToUse);
        parameterMap.put(DpsDepositFacade.IE_ENTITY_TYPE, ieEntityTypeToUse);
        parameterMap.put(DpsDepositFacade.TITLE_SOURCE, dcTitleSourceToUse);
        parameterMap.put(DpsDepositFacade.PDS_URL, this.pdsUrl);
        parameterMap.put(DpsDepositFacade.PRODUCER_ID, producerIdToUse);
        parameterMap.put(DpsDepositFacade.DPS_WSDL_URL, depositServerBaseUrl + depositWsdlRelativePath);
        parameterMap.put(DpsDepositFacade.OMS_OPEN_ACCESS, this.omsOpenAccess);
        parameterMap.put(DpsDepositFacade.OMS_PUBLISHED_RESTRICTED, this.omsPublishedRestricted);
        parameterMap.put(DpsDepositFacade.OMS_UNPUBLISHED_RESTRICTED_BY_LOCATION, this.omsUnpublishedRestrictedByLocation);
        parameterMap.put(DpsDepositFacade.OMS_UNPUBLISHED_RESTRICTED_BY_PERSON, this.omsUnpublishedRestrictedByPersion);
        parameterMap.put(DpsDepositFacade.CMS_SECTION, this.cmsSection);
        parameterMap.put(DpsDepositFacade.CMS_SYSTEM, this.cmsSystem);

        /*
         * Add target reference number.
         * 
         * The target reference number needs to be taken from the xAttributes map
         * and not from the WCT METS document. A bug in the WCT causes the METS
         * to not contain the target reference number if it was entered into the
         * target after a given target instance is endorsed. 
         */
        String ilsReference = (String)attributes.get(REFERENCE_NUMBER);
        if (ilsReference != null) ilsReference = ilsReference.trim();
        parameterMap.put(DpsDepositFacade.ILS_REFERENCE, ilsReference);

        /*
         * Add OMS-style access restriction string
         */
        parameterMap.put(DpsDepositFacade.ACCESS_RESTRICTION, (String)attributes.get(ACCESS_RESTRICTION));

        /*
         * Add the WCT METS XML string
         */
        parameterMap.put(DpsDepositFacade.WCT_METS_XML_DOCUMENT, finalSIP);

        /*
         * Add target instance id
         */
        parameterMap.put(DpsDepositFacade.TARGET_INSTANCE_ID, targetInstanceOID);

        return parameterMap;
    }

    private static class DepDataComparator implements Comparator<DepData> {
        @Override
        public int compare(DepData o1, DepData o2) {
            if (isEmpty(o1) || isEmpty(o2)) return 0;
            if (isEmpty(o1)) return 1;
            if (isEmpty(o2)) return -1;
            return o1.description.compareToIgnoreCase(o2.description);
        }
        private boolean isEmpty(DepData o) {
            if (o == null || o.description == null) return true;
            return false;
        }

    }
}
