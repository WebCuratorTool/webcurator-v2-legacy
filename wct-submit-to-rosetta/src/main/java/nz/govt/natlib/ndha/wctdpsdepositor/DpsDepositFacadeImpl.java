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

import com.exlibris.dps.sdk.deposit.DepositWebServices;
import com.exlibris.dps.sdk.pds.PdsClient;
import com.google.inject.Inject;
import nz.govt.natlib.ndha.wctdpsdepositor.dpsdeposit.DepositWebServicesFactory;
import nz.govt.natlib.ndha.wctdpsdepositor.dpsdeposit.dspresult.DepositResultConverter;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.ArchiveFile;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.WctDataExtractor;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.XPathWctMetsExtractor;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.filefinder.CollectionFileArchiveBuilder;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.filefinder.FileArchiveBuilder;
import nz.govt.natlib.ndha.wctdpsdepositor.filemover.FileMover;
import nz.govt.natlib.ndha.wctdpsdepositor.mets.DnxMapper;
import nz.govt.natlib.ndha.wctdpsdepositor.mets.MetsDocument;
import nz.govt.natlib.ndha.wctdpsdepositor.pds.PdsClientFactory;
import nz.govt.natlib.ndha.wctdpsdepositor.preprocessor.PreDepositProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.archive.dps.DpsDepositFacade;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DpsDepositFacadeImpl implements DpsDepositFacade {
    private static final Log log = LogFactory.getLog(DpsDepositFacadeImpl.class);

    private final DepositWebServicesFactory dwsFactory;
    private final FileMover fileMover;
    private final PdsClientFactory pdsClientFactory;
    private final DnxMapper dnxMapper;
    private final PreDepositProcessor preDepositProcessor;
    private static PdsClient pdsClient;

    private DepositResultConverter resultConverter = new DepositResultConverter();

    @Inject
    public DpsDepositFacadeImpl(DepositWebServicesFactory dwsFactory, PdsClientFactory pdsClientFactory, FileMover fileMover, DnxMapper dnxMapper, PreDepositProcessor preDepositProcessor) {
        this.dwsFactory = dwsFactory;
        this.pdsClientFactory = pdsClientFactory;
        this.fileMover = fileMover;
        this.dnxMapper = dnxMapper;
        this.preDepositProcessor = preDepositProcessor;
    }

   
    public DepositResult deposit(Map<String, String> parameters, List<File> fileList) throws WctDepositParameterValidationException {
    	DepositResult depositResultAdapter = null;
    	try {
    		String targetInstanceOID = parameters.get(DpsDepositFacade.TARGET_INSTANCE_ID);
            String finalSIP = parameters.get(DpsDepositFacade.WCT_METS_XML_DOCUMENT);
            String ilsReference = parameters.get(DpsDepositFacade.ILS_REFERENCE);
            String accessRestriction = parameters.get(DpsDepositFacade.ACCESS_RESTRICTION);

            FileArchiveBuilder archiveBuilder = populateFileArchiveBuilderFrom(fileList);
            XPathWctMetsExtractor wctDataExtractor = new XPathWctMetsExtractor();
            String metsFileName = "METS-" + targetInstanceOID + ".xml";
            wctDataExtractor.parseFile(finalSIP.getBytes(), metsFileName, archiveBuilder);
            setHarvestType(wctDataExtractor, parameters.get(HARVEST_TYPE));
            setAdditionalDublinCoreElements(wctDataExtractor, parameters);
            wctDataExtractor.setIeEntityType(parameters.get(IE_ENTITY_TYPE));
            wctDataExtractor.setWctTargetInstanceID(targetInstanceOID);
            wctDataExtractor.setILSReference(ilsReference);
            wctDataExtractor.setAccessRestriction(accessRestriction);

            WctDepositParameter depositParameter = populateDepositParameterFromMap(parameters);

            depositResultAdapter = deposit(wctDataExtractor, depositParameter);
    	} finally {
    	}
		return depositResultAdapter;
    }

    public String loginToPDS(Map<String, String> parameters) throws RuntimeException {
        WctDepositParameter depositParameter = populateDepositParameterFromMap(parameters);
        return authenticate(depositParameter);
    }

    private DepositResult deposit(WctDataExtractor wctData, WctDepositParameter depositParameter) throws WctDepositParameterValidationException {
        log.debug("Deposit started");
        try {
            depositParameter.isValid();
            preDepositProcessor.process(wctData);
            MetsDocument dpsMetsDocument = dnxMapper.generateDnxFrom(wctData);
            moveFilesToServer(dpsMetsDocument, wctData.getAllFiles(), depositParameter);
            String pdsSessionId = authenticate(depositParameter);
            DepositResult depositResultAdapter = callDepositService(pdsSessionId, dpsMetsDocument, depositParameter);
            if (log.isDebugEnabled())
                log.debug("Deposit finished, SipId: " + depositResultAdapter.getSipId());
            return depositResultAdapter;
        } finally {
            wctData.cleanUpCdxFile();
        }
    }

    private void moveFilesToServer(MetsDocument metsDocument, List<ArchiveFile> archiveFiles, WctDepositParameter depositParameter) {
        fileMover.move(metsDocument, archiveFiles, depositParameter);
    }

    private String authenticate(WctDepositParameter depositParameter) {
        initPdsClient(depositParameter.getPdsUrl());

        String pdsHandle = authenticateWithPDS(depositParameter);
        return pdsHandle;
    }

    private void initPdsClient(String pdsUrl) {
        if (pdsClient == null) {
            synchronized(DpsDepositFacadeImpl.class) {
                pdsClient = pdsClientFactory.createInstance();
                pdsClient.init(pdsUrl, false);
            }
        }
    }

    private String authenticateWithPDS(WctDepositParameter depositParameter) {
        try {
            return pdsClient.login(depositParameter.getDpsInstitution(), depositParameter.getDpsUserName(), depositParameter.getDpsPassword());
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while authenticating with the PDS service.", e);
        }
    }

    private DepositResult callDepositService(String pdsSessionId, MetsDocument metsDocument, WctDepositParameter depositParameter) {
        DepositWebServices dws = dwsFactory.createInstance(depositParameter);
        String xmlFragmentResult = dws.submitDepositActivity(pdsSessionId, depositParameter.getMaterialFlowId(), metsDocument.getDepositDirectoryName(), depositParameter.getProducerId(), metsDocument.getDepositSetId());
        return resultConverter.unmarshalFrom(xmlFragmentResult);
    }

    private FileArchiveBuilder populateFileArchiveBuilderFrom(List<File> fileList) {
        Map<String, File> archiveFileMap = new HashMap<String, File>();

        for (File archiveFile : fileList)
            archiveFileMap.put(archiveFile.getName(), archiveFile);

        return new CollectionFileArchiveBuilder(archiveFileMap);
    }

    private WctDepositParameter populateDepositParameterFromMap(Map<String, String> parameters) {
        WctDepositParameter depositParameter = new WctDepositParameter();

        depositParameter.setDpsInstitution(parameters.get(DpsDepositFacade.DPS_INSTITUTION));
        depositParameter.setDpsUserName(parameters.get(DpsDepositFacade.DPS_USER_NAME));
        depositParameter.setDpsPassword(parameters.get(DpsDepositFacade.DPS_PASSWORD));
        depositParameter.setFtpHost(parameters.get(DpsDepositFacade.FTP_HOST));
        depositParameter.setFtpPassword(parameters.get(DpsDepositFacade.FTP_PASSWORD));
        depositParameter.setFtpUserName(parameters.get(DpsDepositFacade.FTP_USER_NAME));
        depositParameter.setFtpDirectory(parameters.get(DpsDepositFacade.FTP_DIRECTORY));
        depositParameter.setMaterialFlowId(parameters.get(DpsDepositFacade.MATERIAL_FLOW_ID));
        depositParameter.setPdsUrl(parameters.get(DpsDepositFacade.PDS_URL));
        depositParameter.setProducerId(parameters.get(DpsDepositFacade.PRODUCER_ID));
        depositParameter.setDpsWsdlUrl(parameters.get(DpsDepositFacade.DPS_WSDL_URL));

        return depositParameter;
    }

    private void setHarvestType(XPathWctMetsExtractor wctDataExtractor, String harvestTypeString) {
        HarvestType type = HarvestType.TraditionalWebHarvest;
        try {
            type = HarvestType.valueOf(harvestTypeString);
        } catch (Exception e) {
        }
        wctDataExtractor.setHarvestType(type);
    }

    private void setAdditionalDublinCoreElements(XPathWctMetsExtractor wctDataExtractor, Map<String, String> parameters) {
        if (HarvestType.HtmlSerialHarvest.equals(wctDataExtractor.getHarvestType()) == false) return;
        wctDataExtractor.setAdditionalDCTermElement("bibliographicCitation", parameters.get(DpsDepositFacade.DCTERMS_BIBLIOGRAPHIC_CITATION));
        wctDataExtractor.setAdditionalDCTermElement("available", parameters.get(DpsDepositFacade.DCTERMS_AVAILABLE));
    }

}
