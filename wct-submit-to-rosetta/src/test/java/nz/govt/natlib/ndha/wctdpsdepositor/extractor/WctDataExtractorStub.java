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

package nz.govt.natlib.ndha.wctdpsdepositor.extractor;

import java.util.List;
import java.util.ArrayList;

import nz.govt.natlib.ndha.wctdpsdepositor.CustomDepositField;
import org.webcurator.core.archive.dps.DpsDepositFacade.HarvestType;

import com.exlibris.core.sdk.formatting.DublinCore;


public class WctDataExtractorStub implements WctDataExtractor {
    private ArchiveFile arcIndexFile;
    private String targetName;
    private String harvestDate;
    private String events;
    private String accessRestriction;
    private String ilsReference;
    private String createdBy;
    private String creationDate;
    private String provenanceNote;
    private String copyrightURL;
    private String cmsSystem;
    private String cmsSection;
    private List<ArchiveFile> archiveFiles = new ArrayList<ArchiveFile>();
    private List<ArchiveFile> homeDirectoryFiles = new ArrayList<ArchiveFile>();
    private List<ArchiveFile> logFiles = new ArrayList<ArchiveFile>();
    private ArchiveFile wctMetsFile;
    private List<ArchiveFile> allFiles = new ArrayList<ArchiveFile>();
    private String copyrightStatement;
    private List<ArchiveFile> reportFiles = new ArrayList<ArchiveFile>();
    private List<SeedUrl> seedUrls = new ArrayList<SeedUrl>();
    private String wctTargetInstanceID;
    private List<CustomDepositField> dcFieldsAdditional = new ArrayList<CustomDepositField>();


    public String getTargetName() {
        return targetName;
    }

    public String getHarvestDate() {
        return harvestDate;
    }

    public List<SeedUrl> getSeedUrls() {
        return seedUrls;
    }

    public String getEvents() {
        return events;
    }

    public String getAccessRestriction() {
        return accessRestriction;
    }

    public String getILSReference() {
        return ilsReference;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getProvenanceNote() {
        return provenanceNote;
    }

    public String getCopyrightURL() {
        return copyrightURL;
    }

    public List<ArchiveFile> getArchiveFiles() {
        return archiveFiles;
    }

    public ArchiveFile getArcIndexFile() {
        return arcIndexFile;
    }

    public void setArcIndexFile(ArchiveFile arcIndex) {

    }

    public List<ArchiveFile> getLogFiles() {
        return logFiles;
    }

    public List<ArchiveFile> getReportFiles() {
        return reportFiles;
    }

    public List<ArchiveFile> getHomeDirectoryFiles() {
        return homeDirectoryFiles;
    }

    public ArchiveFile getWctMetsFile() {
        return wctMetsFile;
    }

    public List<ArchiveFile> getAllFiles() {
        return allFiles;
    }

    public String getCopyrightStatement() {
        return copyrightStatement;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setHarvestDate(String harvestDate) {
        this.harvestDate = harvestDate;
    }

    public void setEvents(String events) {
        this.events = events;
    }

    public void setAccessRestriction(String accessRestriction) {
        this.accessRestriction = accessRestriction;
    }

    public void setIlsReference(String ilsReference) {
        this.ilsReference = ilsReference;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setProvenanceNote(String provenanceNote) {
        this.provenanceNote = provenanceNote;
    }

    public void setCopyrightURL(String copyrightURL) {
        this.copyrightURL = copyrightURL;
    }

    public void setArchiveFiles(List<ArchiveFile> archiveFiles) {
        this.archiveFiles = archiveFiles;
    }

    public void setHomeDirectoryFiles(List<ArchiveFile> homeDirectoryFiles) {
        this.homeDirectoryFiles = homeDirectoryFiles;
    }

    public void setLogFiles(List<ArchiveFile> logFiles) {
        this.logFiles = logFiles;
    }

    public void setWctMetsFile(ArchiveFile wctMetsFile) {
        this.wctMetsFile = wctMetsFile;
    }

    public void setAllFiles(List<ArchiveFile> allFiles) {
        this.allFiles = allFiles;
    }

    public void setCopyrightStatement(String copyrightStatement) {
        this.copyrightStatement = copyrightStatement;
    }

    public void setReportFiles(List<ArchiveFile> reportFiles) {
        this.reportFiles = reportFiles;
    }

    public void setSeedUrls(List<SeedUrl> seedUrls) {
        this.seedUrls = seedUrls;
    }

    public String getWctTargetInstanceID() {
        return wctTargetInstanceID;
    }

    public void setWctTargetInstanceID(String targetInstanceID) {
        this.wctTargetInstanceID = targetInstanceID;
    }

    public void cleanUpCdxFile() {
    }

    public String getCmsSystem() {
        return cmsSystem;
    }

    public String getCmsSection() {
        return cmsSection;
    }

    public String getDCTitleSource() { return null; }

    public List<CustomDepositField> getDcFieldsAdditional() {
        return dcFieldsAdditional;
    }

    public DublinCore getAdditionalDublinCoreElements() {
        return null;
    }

    public HarvestType getHarvestType() {
        return null;
    }

    public String getIeEntityType() {
        return null;
    }
}
