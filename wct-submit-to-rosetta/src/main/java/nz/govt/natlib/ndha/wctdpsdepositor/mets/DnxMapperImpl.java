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

package nz.govt.natlib.ndha.wctdpsdepositor.mets;

import com.exlibris.digitool.common.dnx.DNXConstants;
import com.exlibris.digitool.common.dnx.DnxDocument;
import com.exlibris.digitool.common.dnx.DnxDocumentHelper;
import com.exlibris.digitool.common.dnx.DnxDocumentHelper.Event;
import com.exlibris.core.sdk.formatting.DublinCore;
import com.exlibris.core.sdk.consts.Enum;
import com.google.inject.Inject;
import gov.loc.mets.MetsType;
import nz.govt.natlib.ndha.wctdpsdepositor.WctDepositParameter;
import nz.govt.natlib.ndha.wctdpsdepositor.WctDepositParameterValidationException;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.ArchiveFile;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.WctDataExtractor;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.WctRequiredData.SeedUrl;
import nz.govt.natlib.ndha.common.dublincore.DCElementSet;
import nz.govt.natlib.ndha.common.dublincore.DCFormatElement;
import nz.govt.natlib.ndha.common.dublincore.DCTypeElement;
import nz.govt.natlib.ndha.common.mets.OmsCodeToMetsMapping;
import nz.govt.natlib.ndha.common.mets.OmsCodeToMetsMapping.ObjectTypeCodeMapping;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlOptions;
import org.webcurator.core.archive.dps.DpsDepositFacade.HarvestType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The class builds a DPS compliant MET's document using metadata harvested by
 * an instance of the class @{link WctDataExtractor}.
 */
public class DnxMapperImpl implements DnxMapper {
    private static final Log log = LogFactory.getLog(DnxMapperImpl.class);
    private static final String PROV_EVENT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final MetsWriterFactory metsWriterFactory;

    @Inject
    public DnxMapperImpl(MetsWriterFactory metsWriterFactory) {
        this.metsWriterFactory = metsWriterFactory;
    }

    public MetsDocument generateDnxFrom(WctDataExtractor wctData) {
    	
        MetsWriter metsWriter = metsWriterFactory.createMetsWriter();
        populateIeDc(wctData, metsWriter);
        populateIeDnx(wctData, metsWriter);
        populateFileSections(wctData, metsWriter);

        metsWriter.fixIdNaming();
        metsWriter.generateStructMap(null);
        metsWriter.generateGID();

        checkForErrors(wctData, metsWriter);

        return new MetsDocument("dps_mets.xml", metsWriter);
    }

    private void populateFileSections(WctDataExtractor wctData, MetsWriter metsWriter) {
        MetsType.FileSec.FileGrp metsRepresentation = metsWriter.addNewFileGrp(Enum.UsageType.VIEW, Enum.PreservationType.PRESERVATION_MASTER, "");

        String grpId = metsRepresentation.getID();
        DnxDocument dnx = metsWriter.getFileGrpDnx(grpId);
        dnx.updateSectionKey(DNXConstants.GENERALREPCHARACTERISTICS.PRESERVATIONTYPE, Enum.PreservationType.PRESERVATION_MASTER.toString());
        dnx.updateSectionKey(DNXConstants.GENERALREPCHARACTERISTICS.DIGITALORIGINAL, "true");
        metsWriter.setFileGrpDnx(dnx, grpId);

        addFilesToRepresentation(metsWriter, metsRepresentation, wctData.getArchiveFiles());
        addFilesToRepresentation(metsWriter, metsRepresentation, wctData.getHomeDirectoryFiles());
        addFilesToRepresentation(metsWriter, metsRepresentation, wctData.getLogFiles());
        addFilesToRepresentation(metsWriter, metsRepresentation, wctData.getReportFiles());
        addFileToRepresentation(metsWriter, metsRepresentation, wctData.getArcIndexFile());
        addFileToRepresentation(metsWriter, metsRepresentation, wctData.getWctMetsFile());
        
        log.debug(wctData.getHomeDirectoryFiles());
        //metsWriter.generateChecksum(wctData., "MD5");
    }

    private void addFilesToRepresentation(MetsWriter metsWriter, MetsType.FileSec.FileGrp metsRepresenation, List<ArchiveFile> archiveFiles) {
        for (ArchiveFile archiveFile : archiveFiles)
            addFileToRepresentation(metsWriter, metsRepresenation, archiveFile);
    }

    private void addFileToRepresentation(MetsWriter metsWriter, MetsType.FileSec.FileGrp metsRepresenation, ArchiveFile archiveFile) {
        metsWriter.addNewFile(metsRepresenation, archiveFile.getMimeType(), archiveFile.getFileName(), archiveFile.getFileName());
    }

    private void populateIeDc(WctDataExtractor wctData, MetsWriter metsWriter) {
        DublinCore ieDc = metsWriter.getDublinCoreParser();
        if (HarvestType.HtmlSerialHarvest.equals(wctData.getHarvestType())) {
            // Set HTML Harvest specific DC fields such as title and date
            addHTMLSerialHarvestSpecificDc(wctData, ieDc);
        } else {
            // Set Web Harvest specific title and date
            addWebHarvestSpecificDc(wctData, ieDc);
        }
        addDcElement(ieDc, DCElementSet.Rights, determineAccessRightsCode(wctData));

        ObjectTypeCodeMapping mapping = OmsCodeToMetsMapping.getObjectTypeCodeMapping(OmsCodeToMetsMapping.OT_WWW);
        DCTypeElement type = mapping.type;
        DCFormatElement format = mapping.format;
        if (type != null) {
            addDcElement(ieDc, DCElementSet.Type, type.toString());
        }
        if (format != null) {
            addDcElement(ieDc, DCElementSet.Format, format.toString());
        }

        metsWriter.setIEDublinCore(ieDc);
    }

    private void populateDcDateFromHarvestDate(WctDataExtractor wctData, DublinCore ieDc) {
        if (StringUtils.isBlank(wctData.getHarvestDate()))
            throw new RuntimeException("The harvest date of the harvest was not specified.");

        addDcElement(ieDc, DCElementSet.Date, wctData.getHarvestDate());
        addDcTermsElement(ieDc, "available", wctData.getHarvestDate());
    }

    private void populateDcTitleFromSeedUrls(WctDataExtractor wctData, DublinCore ieDc) {
        if (wctData.getSeedUrls().size() == 0)
            throw new RuntimeException("The seed URL of the harvest was not specified.");

        String title = null;
        boolean isFirstSeed = true;
        for (SeedUrl seedUrlObj : wctData.getSeedUrls()) {
            String seedUrl = seedUrlObj.getUrl();
            if (isFirstSeed) {
                title = seedUrl;
                isFirstSeed = false;
            } else {
                title += ", " + seedUrl;
            }
        }

        addDcElement(ieDc, DCElementSet.Title, title);
    }

    private void addDcElement(DublinCore dc, DCElementSet element, String value) {
        String key = (new StringBuilder()).append(element.getNameSpace().getPrefix()).append(":").append(element.getName()).toString();
        dc.addElement(key, value);
    }

    private void addDcTermsElement(DublinCore dc, String key, String value) {
        dc.addElement(DublinCore.DCTERMS_NAMESPACE, key, value);
    }
    
    private DnxDocument getDnxDoc(MetsWriter metsWriter) {
    	log.debug("getDnxDoc");
        DnxDocument ieDnx = metsWriter.getIeDnx();
        if (ieDnx == null) {
        	log.debug("It's null, so re-create it");
            ieDnx = metsWriter.getDnxParser();
        }
        return ieDnx;
    }
    
    private void addProvenanceNote(MetsWriter metsWriter
    		, String eventDescription
    		, String eventDateTime
    		, String eventIdentifierValue
    		, String eventOutcomeDetail1
    		, String eventOutcomeDetail2
    		, String eventOutcomeDetail3) {
        DnxDocument dnx = getDnxDoc(metsWriter);
        DnxDocumentHelper helper = new DnxDocumentHelper(dnx);
        Event event = helper.new Event();
        event.setEventDescription(eventDescription);
        event.setEventDateTime(eventDateTime);
        event.setEventIdentifierType("WCT");
        event.setEventIdentifierValue(eventIdentifierValue);
        event.setEventType("CREATION");
        event.setEventOutcome1("SUCCESS");
        event.setEventOutcomeDetail1(eventOutcomeDetail1);
        event.setEventOutcomeDetail2(eventOutcomeDetail2);
        event.setEventOutcomeDetail3(eventOutcomeDetail3);
        List<Event> events = helper.getEvents();
        if (events == null) {
        	events = new ArrayList<Event>();
        }
        events.add(event);
        helper.setEvents(events);
        metsWriter.setIeDnx(dnx);
    }

    private void populateIeDnx(WctDataExtractor wctData, MetsWriter metsWriter) {

    	addProvenanceNote(metsWriter
    			, "IE Created in NLNZ WCT"
    			, convertDateFormat(wctData, wctData.getHarvestDate(), "yyyy-MM-dd HH:mm:ss.SSSSSS", PROV_EVENT_DATE_FORMAT)
        		, "WCT_1"
        		, "Created by " + wctData.getCreatedBy()
        		, "Created on " + convertDateFormat(wctData, wctData.getHarvestDate(), "yyyy-MM-dd HH:mm:ss.SSSSSS", "yyyy-MM-dd")
        		, "");
    	String provNoteFromWCT = wctData.getProvenanceNote();
    	if (!StringUtils.isEmpty(provNoteFromWCT)) {
    		addProvenanceNote(metsWriter
    				, "Provenance Note from NLNZ WCT"
    				, convertDateFormat(wctData, wctData.getCreationDate(), "yyyy-MM-dd", PROV_EVENT_DATE_FORMAT)
    				, "WCT_2"
    				, provNoteFromWCT
    				, ""
    				, "");
    	}

        DnxDocument dnx = getDnxDoc(metsWriter);

        dnx.updateSectionKey(DNXConstants.ACCESSRIGHTSPOLICY.POLICYID,
                determineAccessRightsCode(wctData));

        dnx.updateSectionKey(DNXConstants.WEBHARVESTING.HARVESTDATE, wctData.getHarvestDate());

        dnx.updateSectionKey(DNXConstants.CMS.SYSTEM, "ilsdb");
        dnx.updateSectionKey(DNXConstants.CMS.RECORDID, wctData.getILSReference());

        addWebHarvestSpecificDnx(wctData, dnx);

        metsWriter.setIeDnx(dnx);
    }

    private String convertDateFormat(WctDataExtractor wctData, String dateStr, String inputFormatStr, String outputFormatStr) {
        String outputDate;
        try {
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputFormatStr);
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputFormatStr);
            Date date = inputFormat.parse(dateStr);
            outputDate = outputFormat.format(date);
        } catch (Exception e) {
            log.error("Error parsing/formating the date string " + dateStr + " for Target Instance " + wctData.getWctTargetInstanceID(), e);
            outputDate = dateStr; // Return input date string as it is
        }
        return outputDate;
    }

    /**
     * This method is made public so that other projects (specifically, OMS Extractor) can call this
     * to populate WCT-specific metadata information.
     * 
     * @param wctData
     * @param dnx
     */
    public void addWebHarvestSpecificDc(WctDataExtractor wctData, DublinCore ieDc) {
        populateDcTitleFromSeedUrls(wctData, ieDc);
        populateDcDateFromHarvestDate(wctData, ieDc);
    }

    private void addHTMLSerialHarvestSpecificDc(WctDataExtractor wctData, DublinCore ieDc) {
        String title = wctData.getTargetName();
        if (StringUtils.isBlank(title))
            throw new RuntimeException("Target name of the harvest was not specified.");
        addDcElement(ieDc, DCElementSet.Title, title);
        String harvestDate = wctData.getHarvestDate();
        if (StringUtils.isBlank(harvestDate))
            throw new RuntimeException("The harvest date of the harvest was not specified.");
        addDcElement(ieDc, DCElementSet.Date, harvestDate);
        // Add the additional DC elements that are required by HTML Serial Deposit
        DublinCore dc = wctData.getAdditionalDublinCoreElements();
        if (dc == null)
            throw new RuntimeException("The DC/DCTERMS elements required for HTML Serial Deposit were not speficied.");
        addAdditionalDcElement(dc, "bibliographicCitation", ieDc);
        addAdditionalDcElement(dc, "available", ieDc);

    }

    private void addAdditionalDcElement(DublinCore dc, String key, DublinCore ieDc) {
        String value = dc.getDctermsValue(key);
        if (StringUtils.isBlank(value))
            throw new RuntimeException("The DC/DCTERMS element " + key + " was not speficied for the HTML Serial Deposit.");
        addDcTermsElement(ieDc, key, value);
    }

    /**
     * This method is made public so that other projects (specifically, OMS Extractor) can call this
     * to populate WCT-specific metadata information.
     * 
     * @param wctData
     * @param dnx
     */
    public void addWebHarvestSpecificDnx(WctDataExtractor wctData, DnxDocument dnx) {
        String ieEntityTypeToUse = wctData.getIeEntityType();
        if (ieEntityTypeToUse == null) {
            if (HarvestType.HtmlSerialHarvest.equals(wctData.getHarvestType())) {
                // For an HTML Serial, the IE Entity Type needs to be specified explicitly.
                throw new RuntimeException("The IE Entity Type was not speficied for the HTML Serial Deposit. " +
                        "Please check the DAS configuration file to make sure this is configured correctly");
            }
            ieEntityTypeToUse = OmsCodeToMetsMapping.getObjectTypeCodeMapping(OmsCodeToMetsMapping.OT_WWW).ieEntityType;
        }
        dnx.updateSectionKey(DNXConstants.GENERALIECHARACTERISTICS.IEENTITYTYPE, ieEntityTypeToUse);
        dnx.updateSectionKey(DNXConstants.GENERALIECHARACTERISTICS.SUBMISSIONREASON, "Web Harvesting");

        /*
         * The primarySeedUrl element in DNX doesn't accept multiple seed URLs. If a given
         * web archive contains multiple seed urls, put them as space-separated strings in
         * this element. Also, any space within the URL needs to be replaced with %20.
         */
        StringBuffer seedUrls = new StringBuffer();
        String space = " ";
        for (SeedUrl seedUrlObj : wctData.getSeedUrls()) {
            String seedUrl = seedUrlObj.getUrl();
            if (seedUrl.contains(space)) seedUrl = seedUrl.replaceAll(space, "%20");
            seedUrls.append(seedUrl).append(space);
        }
        dnx.updateSectionKey(DNXConstants.WEBHARVESTING.PRIMARYSEEDURL, seedUrls.toString());
        dnx.updateSectionKey(DNXConstants.WEBHARVESTING.TARGETNAME, wctData.getTargetName());
    }

    private void checkForErrors(WctDataExtractor wctData, MetsWriter metsWriter) {
        List<XmlError> errors = new ArrayList<XmlError>();
        XmlOptions opts = new XmlOptions();
        opts.setErrorListener(errors);
        if (!metsWriter.validate(opts)) {
            StringBuilder errorMessage = new StringBuilder();

            for (XmlError error : errors)
                errorMessage.append(error.toString());

            String msg = String.format("WCT Harvest Instance %s: The METs writer failed to produce a valid document, error message: %s",
                    wctData.getWctTargetInstanceID(), errorMessage);
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }
    
    public void populateAccessRightsCodes(WctDepositParameter depositParameter){
    	if(!depositParameter.getOmsOpenAccess().isEmpty()){
    		OmsCodeToMetsMapping.setOmsAccessRestrictionCode("ACR_OPA", depositParameter.getOmsOpenAccess());
    	}
    	if(!depositParameter.getOmsPublishedRestricted().isEmpty()){
    		OmsCodeToMetsMapping.setOmsAccessRestrictionCode("ACR_OSR", depositParameter.getOmsPublishedRestricted());
    	}
    	if(!depositParameter.getOmsUnpublishedRestrictedByLocation().isEmpty()){
    		OmsCodeToMetsMapping.setOmsAccessRestrictionCode("ACR_ONS", depositParameter.getOmsUnpublishedRestrictedByLocation());
    	}
    	if(!depositParameter.getOmsUnpublishedRestrictedByPersion().isEmpty()){
    		OmsCodeToMetsMapping.setOmsAccessRestrictionCode("ACR_RES", depositParameter.getOmsUnpublishedRestrictedByPersion());
    	}
    }

    private String determineAccessRightsCode(WctDataExtractor wctData) {
        String accessCodeId = wctData.getAccessRestriction();
        String dnxAccessCode = OmsCodeToMetsMapping.getMappedOmsAccessCode(accessCodeId);
        log.info("For the target instance " 
            + wctData.getWctTargetInstanceID() 
            + " with WCT-provided access restriction (OMS-style) of " + accessCodeId 
            + ", DNX access rights code has been set to " + dnxAccessCode);
        if (dnxAccessCode == null)
            throw new WctDepositParameterValidationException("A DPS DNX access restriction was not defined to match the WCT access restriction " + accessCodeId);
        return dnxAccessCode;
    }

}
