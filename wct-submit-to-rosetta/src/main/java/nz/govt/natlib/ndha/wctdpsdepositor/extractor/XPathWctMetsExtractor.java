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

//import nz.govt.natlib.ndha.common.FixityUtils;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.filefinder.FileArchiveBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.webcurator.core.archive.dps.DpsDepositFacade.HarvestType;
import org.xml.sax.SAXException;

import com.exlibris.core.sdk.formatting.DublinCore;
import com.exlibris.core.sdk.formatting.DublinCoreFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for parsing a Wct Met's document and extracting required data.
 * This class uses XPath queries to retrive the data.
 */
public class XPathWctMetsExtractor implements WctDataExtractor {
    private static final Log log = LogFactory.getLog(XPathWctMetsExtractor.class);
    private static final String harvestDateQuery = "//mets:mets/mets:amdSec/mets:techMD/mets:mdWrap/mets:xmlData/wct:wct/wct:TargetInstance/wct:Crawl/wct:StartDate";
    private static final String seedUrlsQuery = "//mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/wct:wct/wct:Target/wct:Seeds/wct:Seed";
    private static final String targetNameQuery = "//mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/wct:wct/wct:Target/wct:Name";
    private static final String createdByQuery = "//mets:mets/mets:amdSec/mets:digiprovMD/mets:mdWrap/mets:xmlData/wct:wct/wct:TargetInstance/wct:Owner/wct:UID";
    private static final String creationDateQuery = "//mets:mets/mets:amdSec/mets:digiprovMD/mets:mdWrap/mets:xmlData/wct:wct/wct:TargetInstance/wct:HarvestResult/wct:CreationDate";
    private static final String provenanceNoteQuery = "//mets:mets/mets:amdSec/mets:digiprovMD/mets:mdWrap/mets:xmlData/wct:wct/wct:TargetInstance/wct:HarvestResult/wct:ProvenanceNote";
    private static final String copyrightStatementQuery = "//mets:mets/mets:amdSec/mets:rightsMD/mets:mdWrap/mets:xmlData/wct:wct/wct:Permissions/wct:Permission/wct:CopyrightStatement";
    private static final String copyrightURLQuery = "//mets:mets/mets:amdSec/mets:rightsMD/mets:mdWrap/mets:xmlData/wct:wct/wct:Permissions/wct:Permission/wct:CopyrightURL";
    private static final String accessRestrictionQuery = "//mets:mets/mets:amdSec/mets:rightsMD/mets:mdWrap/mets:xmlData/wct:wct/wct:Permissions/wct:Permission/wct:AccessStatus";

    private static final String metsArchiveFilesQuery = "//mets:mets/mets:fileSec/mets:fileGrp/mets:fileGrp[@USE='ARCHIVE']/mets:file";
    private static final String metsLogFilesQuery = "//mets:mets/mets:fileSec/mets:fileGrp/mets:fileGrp[@USE='LOGS']/mets:file";
    private static final String metsReportFilesQuery = "//mets:mets/mets:fileSec/mets:fileGrp/mets:fileGrp[@USE='REPORTS']/mets:file";
    private static final String metsHomeDirectoryFilesQuery = "//mets:mets/mets:fileSec/mets:fileGrp/mets:fileGrp[@USE='HOME DIRECTORY']/mets:file";

    private static final String ARC_FILE_LOCATION_PREFIX = "file://./";

    private String harvestDate;
    private List<SeedUrl> seedUrls = new ArrayList<SeedUrl>();
    private String targetName;
    private String ilsReference;
    private String createdBy;
    private String creationDate;
    private String provenanceNote;
    private String copyrightStatement;
    private String copyrightURL;
    private String accessRestriction;
    private List<ArchiveFile> archiveFiles = new ArrayList<ArchiveFile>();
    private List<ArchiveFile> logFiles = new ArrayList<ArchiveFile>();
    private List<ArchiveFile> reportFiles = new ArrayList<ArchiveFile>();
    private List<ArchiveFile> homeDirectoryFiles = new ArrayList<ArchiveFile>();
    private ArchiveFile wctMetsFile;
    private ArchiveFile arcIndex;
    private String wctTargetInstanceID;
    private DublinCore additionalDublinCoreElements;
    private HarvestType harvestType;
    private String ieEntityType;


    private String cmsSection;
    private String cmsSystem;
    
    private static final String XML_MIME_TYPE = "text/xml";


    static {
        /*
         * Temporary fix, until we find a permanent solution, to get rid of the following error from wct-store in Tomcat:
         * 
         * org.webcurator.core.archive.dps.DPSUploadException: java.lang.RuntimeException: 
         * XPathFactory#newInstance() failed to create an XPathFactory for the default object 
         * model: http://java.sun.com/jaxp/xpath/dom with the XPathFactoryConfigurationException: 
         * javax.xml.xpath.XPathFactoryConfigurationException: No XPathFctory implementation 
         * found for the object model: http://java.sun.com/jaxp/xpath/dom
         * 
         */
        System.setProperty("javax.xml.xpath.XPathFactory:http://java.sun.com/jaxp/xpath/dom", "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl");
    }

    public void parseFile(File wctMets, FileArchiveBuilder fileBuilder) throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(wctMets)));
        byte[] buff = new byte[(int) wctMets.length()];
        dis.readFully(buff);
        dis.close();

        String xmlEscapedString = escapeXml(buff);
        parseFile(xmlEscapedString.getBytes(), wctMets.getName(), fileBuilder);
    }

    public void parseFile(byte[] wctMets, String fileName, FileArchiveBuilder fileBuilder) {
        try {

            InputStream inputStream = populateInputStreamFrom(wctMets);

            Document doc = createXmlDocumentFrom(inputStream);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            xpath.setNamespaceContext(new WctNamespaceContext());

            preprocess(doc, xpath, fileBuilder);
            popualteHarvestDate(doc, xpath);
            populateSeedUrlsFrom(doc, xpath);
            populateTargetName(doc, xpath);
            populateCreatedBy(doc, xpath);
            populateCreationDate(doc, xpath);
            populateProvenanceNote(doc, xpath);
            populateCopyrightStatement(doc, xpath);
            populateCopyrightURL(doc, xpath);
            populateAccessRestrictions(doc, xpath);

            populateArchiveFiles(doc, xpath, fileBuilder);
            populateLogFiles(doc, xpath, fileBuilder);
            populateReportFiles(doc, xpath, fileBuilder);
            populateHomeDirectoryFiles(doc, xpath, fileBuilder);

            // Populate anything additional
            populateAdditional(doc, xpath, fileBuilder);

            inputStream = populateInputStreamFrom(wctMets);
            populateWctMets(inputStream, fileName);

        } catch (XPathExpressionException xpe) {
            throw new RuntimeException("An exception occurred while parsing the WCT METS document for " + fileName, xpe);
        }
    }

    /**
     * An empty implementation allowing the subclasses to remove any unnessary nodes
     * to speed up XPath processing.
     * @param doc
     */
    protected void preprocess(Document doc, XPath xpath, FileArchiveBuilder fileBuilder) throws XPathExpressionException {
        // empty implementation
    }

    /**
     * An empty implementation allowing the subclasses to parse any additional
     * details from WCT METS document.
     * @param doc
     * @param xpath
     * @param fileBuilder
     */
    protected void populateAdditional(Document doc, XPath xpath, FileArchiveBuilder fileBuilder)  throws XPathExpressionException {
        // empty implementation
    }

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
        return null;
    }

    public String getAccessRestriction() {
        return accessRestriction;
    }

    public void setAccessRestriction(String ar) {
        accessRestriction = ar;
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

    public String getCopyrightStatement() {
        return copyrightStatement;
    }

    public String getCopyrightURL() {
        return copyrightURL;
    }

    public List<ArchiveFile> getArchiveFiles() {
        return archiveFiles;
    }

    public ArchiveFile getArcIndexFile() {
        return arcIndex;
    }

    public void setArcIndexFile(ArchiveFile arcIndex) {
        this.arcIndex = arcIndex;
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
        List<ArchiveFile> files = new ArrayList<ArchiveFile>();

        if (getArcIndexFile() != null)
            files.add(getArcIndexFile());

        if (getWctMetsFile() != null)
            files.add(getWctMetsFile());
        
        files.addAll(getHomeDirectoryFiles());
        files.addAll(getReportFiles());
        files.addAll(getLogFiles());
        files.addAll(getArchiveFiles());

        return files;
    }

    public String getWctTargetInstanceID() {
        return wctTargetInstanceID;
    }

    public void setWctTargetInstanceID(String targetInstanceID) {
        this.wctTargetInstanceID = targetInstanceID;
    }

    public void setILSReference(String ilsReference) {
        this.ilsReference = ilsReference;
    }

    public String getIeEntityType() {
        return ieEntityType;
    }

    public void setIeEntityType(String ieEntityType) {
        this.ieEntityType = ieEntityType;
    }

    public String getCmsSection() {
        return cmsSection;
    }

    public void setCmsSection(String cmsSection) {
        this.cmsSection = cmsSection;
    }

    public String getCmsSystem() {
        return cmsSystem;
    }

    public void setCmsSystem(String cmsSystem) {
        this.cmsSystem = cmsSystem;
    }

    public DublinCore getAdditionalDublinCoreElements() {
        return additionalDublinCoreElements;
    }

    public void setAdditionalDCTermElement(String name, String value) {
        setAdditionalDublinCoreElement(DublinCore.DCTERMS_NAMESPACE, name, value);
    }

    public void setAdditionalDCElement(String name, String value) {
        setAdditionalDublinCoreElement(DublinCore.DC_NAMESPACE, name, value);
    }

    private void setAdditionalDublinCoreElement(int namespace, String name, String value) {
        if (name == null || value == null) return;
        if (additionalDublinCoreElements == null)
            additionalDublinCoreElements = DublinCoreFactory.getInstance().createDocument();
        additionalDublinCoreElements.addElement(namespace, name, value);
    }

    public HarvestType getHarvestType() {
        return harvestType;
    }

    public void setHarvestType(HarvestType harvestType) {
        this.harvestType = harvestType;
    }

    public void cleanUpCdxFile() {
        String errorText = "WCT Target " + wctTargetInstanceID + ": Error deleting the arc index file ";// + file.getAbsolutePath() + " - it needs to be manually deleted";
        try {
            String indexFilePath = ((FileSystemArchiveFile)arcIndex).generateFilePath();
            errorText =  errorText + indexFilePath + " - ";
            File indexFile = new File(indexFilePath);
            if (indexFile.exists()) {
                boolean status = indexFile.delete();
                if (status == true) {
                    log.info("WCT Target " + wctTargetInstanceID + ": Arc Index file " + indexFilePath + " has been deleted successfully");
                } else {
                    log.error(errorText);
                }
            } else {
                log.error(errorText + " file does not exit");
            }
        } catch (RuntimeException ex) {
            log.error(errorText, ex);
        }
    }

    private InputStream populateInputStreamFrom(byte[] wctMets) {
        InputStream inputStream = new ByteArrayInputStream(wctMets.clone());
        return inputStream;
    }

    private void populateSeedUrlsFrom(Document doc, XPath xpath) throws XPathExpressionException {
        NodeList seedNodes = (NodeList) xpath.evaluate(seedUrlsQuery, doc, XPathConstants.NODESET);
        //int l = seedNodes.getLength();
        for (int i = 0; i < seedNodes.getLength(); i++) {
            Node node = seedNodes.item(i);
            if (node == null) continue;
            NodeList childNodes = node.getChildNodes();
            if (childNodes.getLength() <= 0) continue;
            String url = null;
            String type = null;
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node childNode = childNodes.item(j);
                String nodeName = childNode.getLocalName();
                if ("SeedURL".equals(nodeName))
                    url = childNode.getTextContent();
                else if ("SeedType".equals(nodeName))
                    type = childNode.getTextContent();
            }
            SeedUrl.Type typeAsEnum;
            if (url != null) {
                try {
                    typeAsEnum = SeedUrl.Type.valueOf(type);
                } catch (RuntimeException e) {
                    typeAsEnum = SeedUrl.Type.Primary;
                }
                SeedUrl seedUrl = new SeedUrl(url, typeAsEnum);
                seedUrls.add(seedUrl);
            }
        }
    }

    private void popualteHarvestDate(Document doc, XPath xpath) throws XPathExpressionException {
        harvestDate = (String) xpath.evaluate(harvestDateQuery, doc, XPathConstants.STRING);
    }

    private void populateTargetName(Document doc, XPath xpath) throws XPathExpressionException {
        targetName = (String) xpath.evaluate(targetNameQuery, doc, XPathConstants.STRING);
    }

    private void populateCreatedBy(Document doc, XPath xpath) throws XPathExpressionException {
        createdBy = (String) xpath.evaluate(createdByQuery, doc, XPathConstants.STRING);
    }

    private void populateCreationDate(Document doc, XPath xpath) throws XPathExpressionException {
        creationDate = (String) xpath.evaluate(creationDateQuery, doc, XPathConstants.STRING);
    }

    private void populateProvenanceNote(Document doc, XPath xpath) throws XPathExpressionException {
        provenanceNote = (String) xpath.evaluate(provenanceNoteQuery, doc, XPathConstants.STRING);
    }

    private void populateCopyrightStatement(Document doc, XPath xpath) throws XPathExpressionException {
        copyrightStatement = (String) xpath.evaluate(copyrightStatementQuery, doc, XPathConstants.STRING);
    }

    private void populateCopyrightURL(Document doc, XPath xpath) throws XPathExpressionException {
        copyrightURL = (String) xpath.evaluate(copyrightURLQuery, doc, XPathConstants.STRING);
    }

    private void populateAccessRestrictions(Document doc, XPath xpath) throws XPathExpressionException {
    	accessRestriction = (String) xpath.evaluate(accessRestrictionQuery, doc, XPathConstants.STRING);
    }

    private void populateArchiveFiles(Document doc, XPath xpath, FileArchiveBuilder fileBuilder) throws XPathExpressionException {
        populateFileValueObjectCollectionFrom(doc, xpath, metsArchiveFilesQuery, archiveFiles, fileBuilder);
    }

    private void populateLogFiles(Document doc, XPath xpath, FileArchiveBuilder fileBuilder) throws XPathExpressionException {
        populateFileValueObjectCollectionFrom(doc, xpath, metsLogFilesQuery, logFiles, fileBuilder);
    }

    private void populateReportFiles(Document doc, XPath xpath, FileArchiveBuilder fileBuilder) throws XPathExpressionException {
        populateFileValueObjectCollectionFrom(doc, xpath, metsReportFilesQuery, reportFiles, fileBuilder);
    }

    private void populateHomeDirectoryFiles(Document doc, XPath xpath, FileArchiveBuilder fileBuilder) throws XPathExpressionException {
        populateFileValueObjectCollectionFrom(doc, xpath, metsHomeDirectoryFilesQuery, homeDirectoryFiles, fileBuilder);
    }

    private void populateFileValueObjectCollectionFrom(Document doc, XPath xpath, String nodeQuery, List<ArchiveFile> fileCollection, FileArchiveBuilder fileBuilder) throws XPathExpressionException {
        NodeList seedNodes = (NodeList) xpath.evaluate(nodeQuery, doc, XPathConstants.NODESET);
        for (int i = 0; i < seedNodes.getLength(); i++) {
            Node metsFileNode = seedNodes.item(i);
            ArchiveFile af = populateFileValueObjectFrom(xpath, metsFileNode, fileBuilder);
            fileCollection.add(af);
        }
    }

    private ArchiveFile populateFileValueObjectFrom(XPath xpath, Node metsFileNode, FileArchiveBuilder fileBuilder) throws XPathExpressionException {
        String mimeType = (String) xpath.evaluate("@MIMETYPE", metsFileNode, XPathConstants.STRING);
        String checkSum = (String) xpath.evaluate("@CHECKSUM", metsFileNode, XPathConstants.STRING);
        String fileLocation = (String) xpath.evaluate("mets:FLocat/@xlink:href", metsFileNode, XPathConstants.STRING);
        String fileName = getFileName(fileLocation);
        return fileBuilder.createFileFrom(mimeType, checkSum, fileName);
    }

    private String getFileName(String xmlLocation) {
        if (!xmlLocation.startsWith(ARC_FILE_LOCATION_PREFIX))
            throw new RuntimeException("Arc file location was expected to begin with " + ARC_FILE_LOCATION_PREFIX + ", instead: " + xmlLocation);

        return xmlLocation.substring(ARC_FILE_LOCATION_PREFIX.length());
    }

    private Document createXmlDocumentFrom(InputStream wctMets) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            return builder.parse(wctMets);
        }
        catch (ParserConfigurationException pce) {
            throw new RuntimeException(pce);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        catch (SAXException se) {
            throw new RuntimeException(se);
        }
    }

    private void populateWctMets(InputStream mets, String fileName) {
        //String wctMetsDigest = FixityUtils.calculateMD5(mets);
        String fileLocation = fileName;
        this.wctMetsFile = new InputStreamArchiveFile(XML_MIME_TYPE, fileLocation, mets);
    }

    protected String escapeXml(byte[] buff) {
        String xml = new String(buff);
        return xml.replaceAll("&", "&amp;");
    }

}
