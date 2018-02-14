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
package org.webcurator.core.store.arc;

import static org.webcurator.core.archive.Constants.ARC_FILE;
import static org.webcurator.core.archive.Constants.LOG_FILE;
import static org.webcurator.core.archive.Constants.REPORT_FILE;
import static org.webcurator.core.archive.Constants.ROOT_FILE;

import it.unipi.di.util.ExternalSort;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.archive.format.warc.WARCConstants;
import org.archive.io.*;
import org.archive.io.arc.ARCReader;
import org.archive.io.arc.ARCRecord;
import org.archive.io.arc.ARCRecordMetaData;
import org.archive.io.arc.ARCWriter;
import org.archive.io.arc.WriterPoolSettingsData;
import org.archive.io.warc.WARCReader;
import org.archive.io.warc.WARCRecord;
import org.archive.io.warc.WARCRecordInfo;
import org.archive.io.warc.WARCWriter;
import org.archive.io.warc.WARCWriterPoolSettings;
import org.archive.io.warc.WARCWriterPoolSettingsData;
import org.archive.uid.UUIDGenerator;
import org.archive.util.anvl.ANVLRecord;
import org.webcurator.core.archive.Archive;
import org.webcurator.core.archive.ArchiveFile;
import org.webcurator.core.archive.file.FileArchive;
import org.webcurator.core.common.Constants;
import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.core.harvester.agent.HarvesterStatusUtil;
import org.webcurator.core.reader.LogProvider;
import org.webcurator.core.store.DigitalAssetStore;
import org.webcurator.core.store.Indexer;
import org.webcurator.core.util.WCTSoapCall;
import org.webcurator.core.util.WebServiceEndPoint;
import org.webcurator.domain.model.core.ArcHarvestFileDTO;
import org.webcurator.domain.model.core.ArcHarvestResourceDTO;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResultDTO;
import org.webcurator.domain.model.core.LogFilePropertiesDTO;

/**
 * The ArcDigitalAssetStoreService is used for storing and accessing the
 * completed harvest data from the digital asset store.
 *
 * @author bbeaumont
 */
public class ArcDigitalAssetStoreService implements DigitalAssetStore,
        LogProvider {
    /**
     * The logger.
     */
    private static Log log = LogFactory
            .getLog(ArcDigitalAssetStoreService.class);
    /**
     * the base directory for the digital asset stores harvest files.
     */
    private File baseDir = null;
    /**
     * Constant for the size of a buffer.
     */
    private final int BYTE_BUFF_SIZE = 1024;
    /**
     * the archive service to use.
     */
    private Archive archive = null;
    /**
     * Arc files meta data date format.
     */
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat writerDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    /**
     * The Indexer
     */
    private Indexer indexer = null;
    /**
     * The DAS File Mover
     */
    private DasFileMover dasFileMover = null;
    /**
     * The core
     */
    private WebServiceEndPoint wsEndPoint = null;

    private FileArchive fileArchive = null;

    private String pageImagePrefix = "PageImage";
    private String aqaReportPrefix = "aqa-report";


    static {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        writerDF.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public void save(String targetInstanceName, String directory, File file)
            throws DigitalAssetStoreException {
        save(targetInstanceName, directory, new File[]{file});
    }

    /**
     * @see DigitalAssetStore#save(String, String, File[]).
     */
    public void save(String targetInstanceName, String directory, File[] files)
            throws DigitalAssetStoreException {
        // Target destination is always baseDir plus targetInstanceName.
        File targetDir = new File(baseDir, targetInstanceName);
        String dir = directory + "/";

        // Create the target dir if is doesn't exist. This will also
        // create the parent if necessary.
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        // Move the ARC files into the /1 directory.
        new File(targetDir, dir).mkdirs();
        boolean success = true;
        Exception failureException = null;

        // Loop through all the files, but stop if any of them fail.
        for (int i = 0; success && i < files.length; i++) {
            File destination = new File(targetDir, "/" + dir
                    + files[i].getName());
            log.debug("Moving File to Store: " + files[i].getAbsolutePath()
                    + " -> " + destination.getAbsolutePath());

            try {
                // FileUtils.copyFile(files[i], destination);
                // DasFileMover fileMover = new InputStreamDasFileMover();
                dasFileMover.moveFile(files[i], destination);
            } catch (IOException ex) {
                log.error("Failed to move file " + files[i].getAbsolutePath()
                        + " to " + destination.getAbsolutePath(), ex);
                failureException = ex;
                success = false;
            }
        }

        // If the copy failed, throw an exception.
        if (!success) {
            throw new DigitalAssetStoreException(
                    "Failed to move Archive files to " + targetDir + "/" + dir,
                    failureException);
        }
    }


    /**
     * @see DigitalAssetStore#save(String, File[]).
     */
    public void save(String targetInstanceName, File[] files)
            throws DigitalAssetStoreException {
        save(targetInstanceName, "1", files);
    }

    public void save(String targetInstanceName, File file)
            throws DigitalAssetStoreException {
        save(targetInstanceName, "1", new File[]{file});
    }

    /**
     * @see DigitalAssetStore#getResource(String, int, HarvestResourceDTO).
     */
    @SuppressWarnings("finally")
    public File getResource(String targetInstanceName, int harvestResultNumber,
                            HarvestResourceDTO resourcex) throws DigitalAssetStoreException {
        FileOutputStream fos = null;
        ArchiveReader reader = null;
        ArchiveRecord record = null;
        File source = null;
        File dest = null;
        ArcHarvestResourceDTO resource = null;
        try {
            resource = (ArcHarvestResourceDTO) resourcex;
            source = new File(this.baseDir, "/" + targetInstanceName + "/"
                    + harvestResultNumber + "/" + resource.getArcFileName());

            try {
                reader = ArchiveReaderFactory.get(source);
            } catch (IOException ex) {
                if (log.isWarnEnabled()) {
                    log.warn("Failed to get resource : " + ex.getMessage());
                }
                source = new File(fileArchive.getArchiveRepository() + "/"
                        + targetInstanceName + "/"
                        + fileArchive.getArchiveArcDirectory() + "/"
                        + resource.getArcFileName());
                if (log.isWarnEnabled()) {
                    log.info("trying filestore " + source.getAbsolutePath());
                }
                try {
                    reader = ArchiveReaderFactory.get(source);
                } catch (IOException e) {
                    throw new DigitalAssetStoreException(
                            "Failed to get resource : " + e.getMessage());
                }
            }

            record = reader.get(resource.getResourceOffset());

            dest = File.createTempFile("wct", "tmp");
            if (log.isDebugEnabled()) {
                log.debug("== Temp file: " + dest.getAbsolutePath());
            }

            fos = new FileOutputStream(dest);

            if (record instanceof ARCRecord) {
                ((ARCRecord) record).skipHttpHeader();
            } else {
                skipStatusLine(record);
                skipHeaders(record);
            }

            int bytesRead = 0;
            byte[] byteBuffer = new byte[BYTE_BUFF_SIZE];
            while ((bytesRead = record.read(byteBuffer, 0, BYTE_BUFF_SIZE)) != -1) {
                fos.write(byteBuffer, 0, bytesRead);
            }
            if (fos != null)
                fos.close();

        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to get resource : " + e.getMessage(), e);
            }
            throw new DigitalAssetStoreException("Failed to get resource : "
                    + e.getMessage());
        } catch (RuntimeException ex) {
            if (log.isErrorEnabled()) {
                log.error("Failed to get resource : " + ex.getMessage(), ex);
            }
            throw new DigitalAssetStoreException("Failed to get resource : "
                    + ex.getMessage());
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("close fos failed " + ex.getMessage(), ex);
                }
            }
            try {
                if (record != null)
                    record.close();
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("close record failed " + ex.getMessage(), ex);
                }
            }
            try {
                if (reader != null)
                    reader.close();
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("close reader failed " + ex.getMessage(), ex);
                }
            }
        }

        return dest;
    }

    /**
     * @see DigitalAssetStore#getSmallResource(String, int, HarvestResourceDTO).
     */
    public byte[] getSmallResource(String targetInstanceName,
                                   int harvestResultNumber, HarvestResourceDTO resourcex)
            throws DigitalAssetStoreException {
        ArchiveRecord record = null;
        ArchiveReader reader = null;
        File source = null;
        try {
            ArcHarvestResourceDTO resource = (ArcHarvestResourceDTO) resourcex;
            source = new File(this.baseDir, "/" + targetInstanceName + "/"
                    + harvestResultNumber + "/" + resource.getArcFileName());
            try {
                reader = ArchiveReaderFactory.get(source);

            } catch (IOException e) {
                if (log.isWarnEnabled()) {

                    log.error("Failed to get resource from "
                            + source.getAbsolutePath() + " from local store");
                }
                source = new File(fileArchive.getArchiveRepository() + "/"
                        + targetInstanceName + "/"
                        + fileArchive.getArchiveArcDirectory() + "/"
                        + resource.getArcFileName());
                if (log.isWarnEnabled()) {
                    log.info("trying filestore " + source.getAbsolutePath());
                }
                reader = ArchiveReaderFactory.get(source);
            }
            record = reader.get(resource.getResourceOffset());

            if (record instanceof ARCRecord) {
                ((ARCRecord) record).skipHttpHeader();
            } else {
                skipStatusLine(record);
                skipHeaders(record);
            }

            ByteArrayOutputStream fos = new ByteArrayOutputStream(1024 * 1024);

            int bytesRead = 0;
            byte[] byteBuffer = new byte[BYTE_BUFF_SIZE];
            while ((bytesRead = record.read(byteBuffer, 0, BYTE_BUFF_SIZE)) != -1) {
                fos.write(byteBuffer, 0, bytesRead);
            }
            fos.close();

            // 5. Return the result.
            return fos.toByteArray();
        } catch (IOException ex) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to get resource : " + ex.getMessage());
            }
        } catch (RuntimeException ex) {
            if (log.isErrorEnabled()) {
                log.error("Failed to get resource : " + ex.getMessage(), ex);
            }
            throw new DigitalAssetStoreException("Failed to get resource : "
                    + ex.getMessage(), ex);
        } finally {
            try {
                if (record != null)
                    record.close();
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("close record failed " + ex.getMessage(), ex);
                }
            }
            try {
                if (record != null)
                    reader.close();
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("close reader failed " + ex.getMessage(), ex);
                }
            }
        }
        return new byte[0];
    }

    /**
     * @see DigitalAssetStore#getHeaders(String, int, HarvestResourceDTO).
     */
    public Header[] getHeaders(String targetInstanceName,
                               int harvestResultNumber, HarvestResourceDTO resourcex)
            throws DigitalAssetStoreException {
        if (log.isDebugEnabled()) {
            log.debug("Start of getHeaders()");
            log.debug("Casting the DTO to ArcHarvestResult");
        }

        Header[] headers = new Header[0];
        ArchiveRecord record = null;
        ArchiveReader reader = null;
        ArcHarvestResourceDTO resource = (ArcHarvestResourceDTO) resourcex;

        if (log.isDebugEnabled()) {
            log.debug("Determining the filename");
        }
        File source = new File(this.baseDir, "/" + targetInstanceName + "/"
                + harvestResultNumber + "/" + resource.getArcFileName());

        try {
            if (log.isDebugEnabled()) {
                log.debug("Create the Archive File Reader");
            }
            try {
                reader = ArchiveReaderFactory.get(source);
            } catch (IOException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Could not read headers for ArchiveRecord from "
                            + source.getAbsolutePath() + " from local store");
                }
                source = new File(fileArchive.getArchiveRepository() + "/"
                        + targetInstanceName + "/"
                        + fileArchive.getArchiveArcDirectory() + "/"
                        + resource.getArcFileName());
                if (log.isWarnEnabled()) {
                    log.info("trying filestore " + source.getAbsolutePath());
                }
                reader = ArchiveReaderFactory.get(source);
            }

            if (log.isDebugEnabled()) {
                log.debug("Skipping to the appropriate record at offset: "
                        + resource.getResourceOffset());
            }
            record = reader.get(resource.getResourceOffset());

            if (record instanceof ARCRecord) {
                log.debug("Reading the headers");
                ((ARCRecord) record).skipHttpHeader();
                headers = ((ARCRecord) record).getHttpHeaders();
            } else {
                log.debug("Reading the headers");
                skipStatusLine(record);
                headers = HttpParser.parseHeaders(record,
                        WARCConstants.DEFAULT_ENCODING);
            }

            return headers;
        } catch (IOException ex) {
            if (log.isWarnEnabled()) {
                log.warn("Error reading headers from ArchiveRecord: "
                        + ex.getMessage());
            }
        } finally {
            try {
                if (record != null)
                    record.close();
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("close record failed " + ex.getMessage(), ex);
                }
            }
            try {
                if (record != null)
                    reader.close();
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("close reader failed " + ex.getMessage(), ex);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("End of method");
            }
        }
        return null;
    }

    /**
     * @see DigitalAssetStore#copyAndPrune(String, int, int, List, List)
     */
    public HarvestResultDTO copyAndPrune(String targetInstanceName,
                                         int orgHarvestResultNum, int newHarvestResultNum,
                                         List<String> urisToDelete, List<HarvestResourceDTO> hrsToImport)
            throws DigitalAssetStoreException {
        try {
            // Calculate the source and destination directories.
            File sourceDir = new File(baseDir, targetInstanceName + "/"
                    + orgHarvestResultNum);
            File destDir = new File(baseDir, targetInstanceName + "/"
                    + newHarvestResultNum);

            // Ensure the destination directory exists.
            destDir.mkdirs();

            // Get all the files from the source dir.
            File[] arcFiles = sourceDir.listFiles();

            List<File> dirs = new LinkedList<File>();
            dirs.add(destDir);

            boolean compressed = false;
            AtomicInteger aint = new AtomicInteger();

            String impArcPrefix = null;
            String impArcSuffix = null;
            String impArcType = null;
            String impArcCompressed = null;
            String strippedImpArcFilename = null;
            List<String> impArcHeader = new ArrayList<String>();

            // Copy them into the destination directory.
            for (int i = 0; i < arcFiles.length; i++) {

                // If this is a CDX file, ignore it, another will be created for
                // the new file
                if (arcFiles[i].getName().toUpperCase().endsWith("CDX")) {
                    continue;
                }

                // Get the reader for this ARC File
                ArchiveReader reader = ArchiveReaderFactory.get(arcFiles[i]);

                // use the prefix and suffix off the original file
                String prefixSuffixRegex = "[-][12][0-9][0-9][0-9][01][0-9][0-3][0-9][0-5][0-9][0-5][0-9][0-5][0-9][-][0-9]*[-]";

                strippedImpArcFilename = reader.getStrippedFileName();
                String[] prefixSuffix = strippedImpArcFilename.split(prefixSuffixRegex);

                String prefix;
                String suffix;
                if (prefixSuffix.length == 2) {
                    prefix = prefixSuffix[0];
                    suffix = prefixSuffix[1];
                } else {
                    prefix = ARCWriter.DEFAULT_PREFIX;
                    suffix = strippedImpArcFilename.substring(strippedImpArcFilename.lastIndexOf("-") + 1,
                            strippedImpArcFilename.length());

                }
                compressed = reader.isCompressed();
                if (impArcPrefix == null) {
                    impArcPrefix = prefix;
                }
                if (impArcSuffix == null) {
                    impArcSuffix = suffix;
                }
                if (impArcCompressed == null) {
                    impArcCompressed = compressed ? "true" : "false";
                }

                Iterator<ArchiveRecord> archiveRecordsIt = reader.iterator();

                if (reader instanceof ARCReader) {
                    if (impArcType == null) {
                        impArcType = "ARC";
                    }

                    // Read the Meta Data
                    ARCRecord headerRec = (ARCRecord) archiveRecordsIt.next();
                    byte[] buff = new byte[1024];
                    StringBuffer metaData = new StringBuffer();
                    int bytesRead = 0;
                    while ((bytesRead = headerRec.read(buff)) != -1) {
                        metaData.append(new String(buff, 0, bytesRead));
                    }
                    List<String> l = new ArrayList<String>();
                    l.add(metaData.toString());

                    if (impArcHeader.isEmpty()) {
                        impArcHeader.add(metaData.toString());
                    }

                    // Create an ARC Writer
                    WriterPoolSettings settings = new WriterPoolSettingsData(strippedImpArcFilename, "${prefix}",
                            ARCReader.DEFAULT_MAX_ARC_FILE_SIZE, compressed, dirs, l);
                    ARCWriter writer = new ARCWriter(aint, settings);

                    // Iterate through all the records, skipping deleted or
                    // imported URLs.
                    while (archiveRecordsIt.hasNext()) {
                        ARCRecord record = (ARCRecord) archiveRecordsIt.next();
                        ARCRecordMetaData meta = record.getMetaData();
                        Date dt;
                        try {
                            dt = sdf.parse(meta.getDate());
                        } catch (ParseException ex) {
                            dt = new Date();
                            if (log.isWarnEnabled()) {
                                log.warn("Couldn't parse date from ARCRecord: "
                                        + record.getMetaData().getUrl(), ex);
                                log.warn("Setting to the current date.");
                            }
                        }

                        if (!urisToDelete.contains(meta.getUrl())) {

                            // this record is not in the delete list so we
                            // should
                            // copy it forward to the arc file, but is there a
                            // match
                            // in the import list?
                            // If the record's Url is in the imports list, then
                            // the user
                            // is opting to replace the content for the
                            // specified Url so
                            // we won't copy this record forward into the target
                            // arc
                            // file, rather we'll add all imported Urls and
                            // their associated
                            // content into an additional newly created arc file
                            // at the end.
                            if (!listContainsURL(hrsToImport, meta.getUrl())) {
                                writer.write(meta.getUrl(), meta.getMimetype(),
                                        meta.getIp(), dt.getTime(),
                                        (int) meta.getLength(), record);
                            }
                        }
                    }

                    writer.close();
                } else if (reader instanceof WARCReader) {
                    if (impArcType == null) {
                        impArcType = "WARC";
                    }

                    /*
                     * Post 1.6.1 code.
                     *
                     * Problem:
                     * The correct number of bytes/characters are being read from the header record, and saved in the
                     * buffer array. But the input stream appears (for some unknown reason) to read or mark one character further
                     * than the length that was read into the array.
                     *
                     * For example, with content-length: 398, the stream should be stopping at the <|> below. So the next character read
                     * would be a carriage return "\r". This is what the WarcReader (line 65 - gotoEOR()) is expecting in order to move
                     * the marker to the start of the next record.
                     *
                     * http-header-from: youremail@yourdomain.com\r\n
                     * \r\n<|>
                     * \r\n
                     * \r\n
                     * WARC/0.18\r\n
                     *
                     * Instead the stream is reading up until the marker in the following example, and throwing a runtime error.
                     *
                     * http-header-from: youremail@yourdomain.com\r\n
                     * \r\n
                     * \r<|>\n
                     * \r\n
                     * WARC/0.18\r\n
                     *
                     *
                     * Workaround/Fix:
                     * Create a duplicate ArchiveReader (headerRecordIt) for just the warc header metadata, that is then closed after
                     * the metadata is read. The archiveRecordsIt ArchiveReader is still used to read the rest of the records. However
                     * the first record (which we read with the other ArchiveReader) still has an issue with the iterator hasNext()
                     * call. So it is skipped before entering the loop that copies each record.
                     *
                     *
                     */

                    // Get a another reader for the warc header metadata
                    ArchiveReader headerReader = ArchiveReaderFactory.get(arcFiles[i]);
                    Iterator<ArchiveRecord> headerRecordIt = headerReader.iterator();

                    // Read the Meta Data
                    WARCRecord headerRec = (WARCRecord) headerRecordIt.next();
                    byte[] buff = new byte[1024];
                    StringBuffer metaData = new StringBuffer();
                    int bytesRead = 0;


                    while ((bytesRead = headerRec.read(buff)) != -1) {
                        metaData.append(new String(buff, 0, bytesRead));
                    }


                    List<String> l = new ArrayList<String>();
                    l.add(metaData.toString());

                    if (impArcHeader.isEmpty()) {
                        impArcHeader.add(metaData.toString());
                    }

                    headerRec.close();
                    headerReader.close();


                    // Bypass warc header metadata as it has been read above from a different ArchiveReader
                    archiveRecordsIt.next();

                    // Create a WARC Writer
                    WARCWriterPoolSettings settings = new WARCWriterPoolSettingsData(strippedImpArcFilename, "${prefix}",
                            ARCReader.DEFAULT_MAX_ARC_FILE_SIZE, compressed, dirs, l, new UUIDGenerator());
                    WARCWriter writer = new WARCWriter(aint, settings);

                    // Iterate through all the records, skipping deleted or
                    // imported URLs.
                    while (archiveRecordsIt.hasNext()) {
                        WARCRecord record = (WARCRecord) archiveRecordsIt
                                .next();
                        ArchiveRecordHeader header = record.getHeader();
                        String WARCType = (String) header
                                .getHeaderValue(WARCConstants.HEADER_KEY_TYPE);
                        String strRecordId = (String) header
                                .getHeaderValue(WARCConstants.HEADER_KEY_ID);
                        URI recordId = new URI(strRecordId.substring(
                                strRecordId.indexOf("<") + 1,
                                strRecordId.lastIndexOf(">") - 1));
                        long contentLength = header.getLength()
                                - header.getContentBegin();


                        if (!WARCType.equals(WARCConstants.WARCRecordType.warcinfo)
                                && (urisToDelete.contains(header.getUrl()) || listContainsURL(
                                hrsToImport, header.getUrl()))) {
                            continue;
                        }

                        ANVLRecord namedFields = new ANVLRecord();
                        Iterator hdrFieldsIt = header.getHeaderFieldKeys()
                                .iterator();
                        while (hdrFieldsIt.hasNext()) {
                            String key = (String) hdrFieldsIt.next();
                            String value = header.getHeaderValue(key)
                                    .toString();
                            if (key.equals(WARCConstants.ABSOLUTE_OFFSET_KEY)) {
                                value = new Long(writer.getPosition())
                                        .toString();
                            }
                            // we exclude all but three fields to avoid
                            // duplication / erroneous data
                            if (key.equals("WARC-IP-Address")
                                    || key.equals("WARC-Payload-Digest")
                                    || key.equals("WARC-Concurrent-To"))
                                namedFields.addLabelValue(key, value);
                        }


                        WARCRecordInfo warcRecordInfo = new WARCRecordInfo();
                        switch (WARCConstants.WARCRecordType.valueOf(WARCType)) {
                            case warcinfo:
                                warcRecordInfo.setType(WARCConstants.WARCRecordType.warcinfo);
                                break;
                            case response:
                                warcRecordInfo.setType(WARCConstants.WARCRecordType.response);
                                warcRecordInfo.setUrl(header.getUrl());
                                break;
                            case metadata:
                                warcRecordInfo.setType(WARCConstants.WARCRecordType.metadata);
                                warcRecordInfo.setUrl(header.getUrl());
                                break;
                            case request:
                                warcRecordInfo.setType(WARCConstants.WARCRecordType.request);
                                warcRecordInfo.setUrl(header.getUrl());
                                break;
                            case resource:
                                warcRecordInfo.setType(WARCConstants.WARCRecordType.resource);
                                warcRecordInfo.setUrl(header.getUrl());
                                break;
                            case revisit:
                                warcRecordInfo.setType(WARCConstants.WARCRecordType.revisit);
                                warcRecordInfo.setUrl(header.getUrl());
                                break;
                            default:
                                if (log.isWarnEnabled()) {
                                    log.warn("Ignoring unrecognised type for WARCRecord: "
                                            + WARCType);
                                }
                        }
                        warcRecordInfo.setCreate14DigitDate(header.getDate());
                        warcRecordInfo.setMimetype(header.getMimetype());
                        warcRecordInfo.setRecordId(recordId);
                        warcRecordInfo.setExtraHeaders(namedFields);
                        warcRecordInfo.setContentStream(record);
                        warcRecordInfo.setContentLength(contentLength);

                        writer.writeRecord(warcRecordInfo);

                        /* old H 1.14.1 WARCWriter API
                        if (WARCType.equals(WARCConstants.WARCRecordType.warcinfo)) {
                            writer.writeWarcinfoRecord(header.getDate(),
                                    header.getMimetype(), recordId,
                                    namedFields, record, contentLength);
                        } else if (WARCType.equals(WARCConstants.WARCRecordType.response)) {
                            writer.writeResponseRecord(header.getUrl(),
                                    header.getDate(), header.getMimetype(),
                                    recordId, namedFields, record,
                                    contentLength);
                        } else if (WARCType.equals(WARCConstants.WARCRecordType.metadata)) {
                            writer.writeMetadataRecord(header.getUrl(),
                                    header.getDate(), header.getMimetype(),
                                    recordId, namedFields, record,
                                    contentLength);
                        } else if (WARCType.equals(WARCConstants.WARCRecordType.request)) {
                            writer.writeRequestRecord(header.getUrl(),
                                    header.getDate(), header.getMimetype(),
                                    recordId, namedFields, record,
                                    contentLength);
                        } else if (WARCType.equals(WARCConstants.WARCRecordType.resource)) {
                            writer.writeResourceRecord(header.getUrl(),
                                    header.getDate(), header.getMimetype(),
                                    recordId, namedFields, record,
                                    contentLength);
                        } else if (WARCType.equals(WARCConstants.WARCRecordType.revisit)) {
                            writer.writeRevisitRecord(header.getUrl(),
                                    header.getDate(), header.getMimetype(),
                                    recordId, namedFields, record,
                                    contentLength);
                        } else {
                            if (log.isWarnEnabled()) {
                                log.warn("Ignoring unrecognised type for WARCRecord: "
                                        + WARCType);
                            }
                        }
                        */
                    }

                    writer.close();
                }

                reader.close();
            }

            // add any imported content to a new arc or warc file as
            // appropriate..
            if (!hrsToImport.isEmpty()) {
                boolean compressit;
                if (impArcCompressed.equals("true")) {
                    compressit = true;
                } else {
                    compressit = false;
                }
                if (impArcType.equals("ARC")) {
                    // Create an ARC Writer
                    WriterPoolSettings settings = new WriterPoolSettingsData(strippedImpArcFilename, "${prefix}",
                            ARCReader.DEFAULT_MAX_ARC_FILE_SIZE, compressit, dirs, impArcHeader);
                    ARCWriter arcWriter = new ARCWriter(aint, settings);
                    for (Iterator<HarvestResourceDTO> it = hrsToImport
                            .iterator(); it.hasNext(); ) {
                        HarvestResourceDTO hr = it.next();
                        if (hr.getLength() > 0L) {
                            File fin = new File(this.baseDir, "/uploadedFiles/"
                                    + hr.getTempFileName());
                            Date dtNow = new Date();
                            arcWriter.write(hr.getName(), hr.getContentType(),
                                    "0.0.0.0", dtNow.getTime(), hr.getLength(),
                                    new java.io.FileInputStream(fin));
                        }
                    }
                    arcWriter.close();

                } else {
                    // Create a WARC Writer
                    WARCWriterPoolSettings settings = new WARCWriterPoolSettingsData(strippedImpArcFilename, "${prefix}",
                            WARCReader.DEFAULT_MAX_WARC_FILE_SIZE, compressit, dirs, impArcHeader, new UUIDGenerator());
                    WARCWriter warcWriter = new WARCWriter(aint, settings);
                    for (Iterator<HarvestResourceDTO> it = hrsToImport
                            .iterator(); it.hasNext(); ) {
                        HarvestResourceDTO hr = it.next();
                        if (hr.getLength() > 0L) {
                            File fin = new File(this.baseDir, "/uploadedFiles/"
                                    + hr.getTempFileName());
                            Date dtNow = new Date();
                            URI recordId = new URI("urn:uuid:"
                                    + hr.getTempFileName());
                            ANVLRecord namedFields = new ANVLRecord();
                            // WARC-Type, reader-identifier, WARC-Date,
                            // absolute-offset, Content-Length, WARC-Record-ID,
                            // WARC-IP-Address, WARC-Payload-Digest,
                            // WARC-Target-URI, Content-Type
                            // namedFields.addLabelValue(WARCConstants.HEADER_KEY_TYPE,
                            // WARCConstants.RESPONSE);
                            // namedFields.addLabelValue("reader-identifier",
                            // warcWriter.someMethod()?);
                            // namedFields.addLabelValue(WARCConstants.HEADER_KEY_DATE,
                            // dtNow.toString());
                            // namedFields.addLabelValue(WARCConstants.ABSOLUTE_OFFSET_KEY,
                            // new Long(warcWriter.getPosition()).toString());
                            // namedFields.addLabelValue(WARCConstants.CONTENT_LENGTH,
                            // String.valueOf(hr.getLength()));
                            // namedFields.addLabelValue(WARCConstants.HEADER_KEY_ID,
                            // "<"+recordId+">");
                            namedFields.addLabelValue(
                                    WARCConstants.HEADER_KEY_IP, "0.0.0.0");
                            // namedFields.addLabelValue(WARCConstants.HEADER_KEY_PAYLOAD_DIGEST,
                            // "sha1:");
                            // namedFields.addLabelValue(WARCConstants.HEADER_KEY_URI,
                            // hr.getName());
                            // namedFields.addLabelValue(WARCConstants.CONTENT_TYPE,
                            // hr.getContentType());
                            WARCRecordInfo warcRecordInfo = new WARCRecordInfo();
                            warcRecordInfo.setUrl(hr.getName());
                            warcRecordInfo.setCreate14DigitDate(writerDF.format(dtNow));
                            warcRecordInfo.setMimetype(hr.getContentType());
                            warcRecordInfo.setRecordId(recordId);
                            warcRecordInfo.setExtraHeaders(namedFields);
                            warcRecordInfo.setContentStream(new java.io.FileInputStream(fin));
                            warcRecordInfo.setContentLength(hr.getLength());
                            warcWriter.writeRecord(warcRecordInfo);
                        }
                    }
                    warcWriter.close();
                }
            }

            log.info("copyAndPrune - Now time to reindex.");
            // Now re-index the files.
            ArcHarvestResultDTO ahr = new ArcHarvestResultDTO();
            File[] fileList = destDir.listFiles();
            Set<ArcHarvestFileDTO> fileset = new HashSet<ArcHarvestFileDTO>();
            for (File f : fileList) {
                ArcHarvestFileDTO ahf = new ArcHarvestFileDTO();
                ahf.setCompressed(compressed);
                ahf.setName(f.getName());
                fileset.add(ahf);
            }

            ahr.setArcFiles(fileset);
            ahr.setCreationDate(new Date());
            ahr.setHarvestNumber(newHarvestResultNum);

            ahr.index(destDir);

            log.info("copyAndPrune - Now returning the ArcHarvestResult: " + ahr.getOid());
            return ahr;

        } catch (URISyntaxException e) {
            if (log.isErrorEnabled()) {
                log.error("Prune and Copy Failed : " + e.getMessage(), e);
            }
            throw new DigitalAssetStoreException("Prune and Copy Failed : "
                    + e.getMessage(), e);
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Prune and Copy Failed : " + e.getMessage(), e);
            }
            throw new DigitalAssetStoreException("Prune and Copy Failed : "
                    + e.getMessage(), e);
        } catch (ParseException e) {
            if (log.isErrorEnabled()) {
                log.error("Prune and Copy Failed : " + e.getMessage(), e);
            }
            throw new DigitalAssetStoreException("Prune and Copy Failed : "
                    + e.getMessage(), e);
        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
            throw new DigitalAssetStoreException("Prune and Copy Failed : "
                    + e.getMessage(), e);
        }
    }

    /**
     * Search the passed in list for an item matching the passed in Url.
     *
     * @param hrs - A list of HarvestResourceDTO objects
     * @param Url - The Url to check for.
     * @return true if a list item's name is equal to the Url, or else false
     */
    private boolean listContainsURL(List<HarvestResourceDTO> hrs, String Url) {
        for (Iterator<HarvestResourceDTO> it = hrs.iterator(); it.hasNext(); ) {
            HarvestResourceDTO hr = it.next();
            if (hr.getName().equals(Url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.webcurator.core.reader.LogProvider#getLogFile(java.lang.String,
     * java.lang.String)
     */
    public File getLogFile(String aJob, String aFileName) {
        File file = null;

        File targetDir = new File(baseDir, aJob);
        File logsDir = new File(targetDir, Constants.DIR_LOGS);

        file = new File(logsDir.getAbsolutePath() + File.separator + aFileName);

        if (!file.exists()
                && aFileName.equalsIgnoreCase(Constants.SORTED_CRAWL_LOG_FILE)) {
            // we need to create sorted crawl.log from crawl.log.
            createSortedCrawlLogFile(logsDir);
            file = new File(logsDir.getAbsolutePath() + File.separator
                    + aFileName);
        }
        if (!file.exists()) {
            logsDir = new File(targetDir, Constants.DIR_REPORTS);
            file = new File(logsDir.getAbsolutePath() + File.separator
                    + aFileName);
        }
        if (!file.exists()) {
            logsDir = new File(targetDir, Constants.DIR_CONTENT);
            file = new File(logsDir.getAbsolutePath() + File.separator
                    + aFileName);
        }
        if (!file.exists()) {
            file = null;
        }

        return file;
    }

    private void createSortedCrawlLogFile(File logsDir) {

        // sort the crawl.log file to create a sorted crawl.log file in the same
        // directory.

        // write new 'stripped' crawl.log, replacing multiple spaces with a
        // single space in each record..
        try {

            BufferedReader inputStream = new BufferedReader(new FileReader(
                    logsDir.getAbsolutePath() + File.separator
                            + Constants.CRAWL_LOG_FILE));
            PrintWriter outputStream = new PrintWriter(new FileWriter(
                    logsDir.getAbsolutePath() + File.separator
                            + Constants.STRIPPED_CRAWL_LOG_FILE));

            String inLine = null;

            while ((inLine = inputStream.readLine()) != null) {
                outputStream.println(inLine.replaceAll(" +", " "));
            }

            outputStream.close();
            inputStream.close();

        } catch (IOException e) {
            return;
        }

        // sort the 'stripped' crawl.log file to create a 'sorted' crawl.log
        // file...
        ExternalSort sort = new ExternalSort();
        try {
            sort.setInFile(logsDir.getAbsolutePath() + File.separator
                    + Constants.STRIPPED_CRAWL_LOG_FILE);
        } catch (FileNotFoundException e1) {
            return;
        }
        try {
            sort.setOutFile(logsDir.getAbsolutePath() + File.separator
                    + Constants.SORTED_CRAWL_LOG_FILE);
        } catch (FileNotFoundException e1) {
            return;
        }
        // sort on fourth column (url) then first column (timestamp)..
        int[] cols = {3, 0};
        sort.setColumns(cols);
        sort.setSeparator(' '); // space

        try {
            sort.run();
        } catch (IOException e1) {
            return;
        }
    }

    /**
     * @see org.webcurator.core.reader.LogProvider#getLogFileNames(java.lang.String)
     */
    public List<String> getLogFileNames(String aJob) {
        List<String> logFiles = new ArrayList<String>();

        File targetDir = new File(baseDir, aJob);
        File logsDir = new File(targetDir, Constants.DIR_LOGS);
        File[] fileList = null;

        if (logsDir.exists()) {
            fileList = logsDir.listFiles();
            for (File f : fileList) {
                logFiles.add(f.getName());
            }
        }

        logsDir = new File(targetDir, Constants.DIR_REPORTS);
        if (logsDir.exists()) {
            fileList = logsDir.listFiles();
            for (File f : fileList) {
                logFiles.add(f.getName());
            }
        }

        return logFiles;
    }

    /**
     * @see org.webcurator.core.reader.LogProvider#getLogFileAttributes(java.lang.String)
     */
    public LogFilePropertiesDTO[] getLogFileAttributes(String aJob) {

        List<LogFilePropertiesDTO> logFiles = new ArrayList<LogFilePropertiesDTO>();

        File targetDir = new File(baseDir, aJob);
        File logsDir = new File(targetDir, Constants.DIR_LOGS);
        File[] fileList = null;

        if (logsDir.exists()) {
            fileList = logsDir.listFiles();
            for (File f : fileList) {
                LogFilePropertiesDTO lf = new LogFilePropertiesDTO();
                lf.setName(f.getName());
                lf.setPath(f.getAbsolutePath());
                lf.setLengthString(HarvesterStatusUtil.formatData(f.length()));
                lf.setLastModifiedDate(new Date(f.lastModified()));
                logFiles.add(lf);
            }
        }

        logsDir = new File(targetDir, Constants.DIR_REPORTS);
        if (logsDir.exists()) {
            fileList = logsDir.listFiles();
            for (File f : fileList) {
                LogFilePropertiesDTO lf = new LogFilePropertiesDTO();
                lf.setName(f.getName());
                lf.setPath(f.getAbsolutePath());
                lf.setLengthString(HarvesterStatusUtil.formatData(f.length()));
                lf.setLastModifiedDate(new Date(f.lastModified()));

                // Special case for AQA reports and images
                if (f.getName().startsWith(pageImagePrefix)) {
                    lf.setViewer("content-viewer.html");
                } else if (f.getName().startsWith(aqaReportPrefix)) {
                    lf.setViewer("aqa-viewer.html");
                }

                logFiles.add(lf);
            }
        }

        LogFilePropertiesDTO[] result = new LogFilePropertiesDTO[logFiles
                .size()];
        int i = 0;
        for (LogFilePropertiesDTO r : logFiles) {
            result[i] = r;
            i++;
        }
        return result;
    }

    /**
     * @see DigitalAssetStore#purge(String[]).
     */
    public void purge(String[] targetInstanceNames)
            throws DigitalAssetStoreException {
        if (null == targetInstanceNames || targetInstanceNames.length == 0) {
            return;
        }

        try {
            for (String tiName : targetInstanceNames) {
                File toPurge = new File(baseDir, tiName);
                if (log.isDebugEnabled()) {
                    log.debug("About to purge dir " + toPurge.toString());
                }
                try {
                    FileUtils.deleteDirectory(toPurge);
                } catch (IOException e) {
                    if (log.isWarnEnabled()) {
                        log.warn("Unable to purge target instance folder: " + toPurge.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            throw new DigitalAssetStoreException("Failed to complete purge : "
                    + e.getMessage(), e);
        }
    }

    /**
     * @see DigitalAssetStore#purgeAbortedTargetInstances(String[]).
     */
    public void purgeAbortedTargetInstances(String[] targetInstanceNames)
            throws DigitalAssetStoreException {
        if (null == targetInstanceNames || targetInstanceNames.length == 0) {
            return;
        }

        try {
            for (String tiName : targetInstanceNames) {
                File toPurge = new File(baseDir, tiName);
                if (log.isDebugEnabled()) {
                    log.debug("About to purge dir " + toPurge.toString());
                }
                try {
                    FileUtils.deleteDirectory(toPurge);
                } catch (IOException e) {
                    if (log.isWarnEnabled()) {
                        log.warn("Unable to purge target instance folder: " + toPurge.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            throw new DigitalAssetStoreException("Failed to complete purge : "
                    + e.getMessage(), e);
        }
    }

    /**
     * Return a list of all the ARC files for the specified target instance and
     * harvest result
     *
     * @param targetInstanceName  the name of the target instance
     * @param harvestResultNumber the harvest result number
     * @return the list of all the ARC files
     * @throws DigitalAssetStoreException throw if there is a problem
     */
    private List<File> getAllARCFiles(String targetInstanceName,
                                      int harvestResultNumber) throws DigitalAssetStoreException {
        ArrayList<File> arcFiles = new ArrayList<File>();
        try {
            File sourceDir = new File(this.baseDir, "/" + targetInstanceName
                    + "/" + harvestResultNumber);
            for (File file : sourceDir.listFiles()) {
                arcFiles.add(file);
            }
            return arcFiles;
        } catch (RuntimeException ex) {
            if (log.isErrorEnabled()) {
                log.error("Failed to get archive files : " + ex.getMessage(),
                        ex);
            }
            throw new DigitalAssetStoreException(
                    "Failed to get archive files : " + ex.getMessage(), ex);
        }
    }

    /**
     * Return a list of all the log files for the specified target instance.
     *
     * @param targetInstanceName the name of the target instance
     * @return the list of log files
     * @throws DigitalAssetStoreException thrown if there is an error
     */
    private List<File> getLogFiles(String targetInstanceName)
            throws DigitalAssetStoreException {
        List<File> logFiles = new ArrayList<File>();
        File targetDir = new File(baseDir, targetInstanceName);
        File logsDir = new File(targetDir, Constants.DIR_LOGS);
        File[] fileList = null;

        if (logsDir.exists()) {
            fileList = logsDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".log");
                }
            });
            for (File f : fileList) {
                logFiles.add(f);
            }
        }
        return logFiles;
    }

    /**
     * Return a list of report files for the specified target instance
     *
     * @param targetInstanceName the name of the target instance
     * @return the list of report files
     * @throws DigitalAssetStoreException thrown if there is an error
     */
    private List<File> getReportFiles(String targetInstanceName)
            throws DigitalAssetStoreException {
        List<File> reportFiles = new ArrayList<File>();
        File targetDir = new File(baseDir, targetInstanceName);
        File reportsDir = new File(targetDir, Constants.DIR_REPORTS);
        File[] fileList = null;

        if (reportsDir.exists()) {
            fileList = reportsDir.listFiles();
            for (File f : fileList) {
                reportFiles.add(f);
            }
        }
        return reportFiles;
    }

    /**
     * @see DigitalAssetStore#submitToArchive(String, String, Map, int).
     */
    public void submitToArchive(String targetInstanceOid, String SIP,
                                Map xAttributes, int harvestNumber)
            throws DigitalAssetStoreException {
        // Kick off the archiving in a separate thread.
        ArchivingThread thread = new ArchivingThread(targetInstanceOid, SIP,
                xAttributes, harvestNumber, wsEndPoint);
        new Thread(thread).start();
    }

    private class ArchivingThread implements Runnable {
        private String targetInstanceOid = null;
        private String SIP = null;
        private Map xAttributes = null;
        private int harvestNumber;
        private WebServiceEndPoint wsEndPoint = null;


        public ArchivingThread(String targetInstanceOid, String sip,
                               Map attributes, int harvestNumber, WebServiceEndPoint wsEndPoint) {
            super();
            this.targetInstanceOid = targetInstanceOid;
            SIP = sip;
            xAttributes = attributes;
            this.harvestNumber = harvestNumber;
            this.wsEndPoint = wsEndPoint;
        }

        public void run() {
            try {

                String targetID = targetInstanceOid + "";
                ArrayList<ArchiveFile> fileList = new ArrayList<ArchiveFile>();
                // Get log files
                for (File f : getLogFiles(targetID)) {
                    fileList.add(new ArchiveFile(f, LOG_FILE));
                }
                // Get report files
                for (File f : getReportFiles(targetID)) {
                    if (f.getName().endsWith("order.xml")) {
                        fileList.add(new ArchiveFile(f, ROOT_FILE));
                    } else {
                        fileList.add(new ArchiveFile(f, REPORT_FILE));
                    }
                }
                // Get arc files
                for (File f : getAllARCFiles(targetID, harvestNumber)) {
                    fileList.add(new ArchiveFile(f, ARC_FILE));
                }

                String archiveIID = archive.submitToArchive(targetInstanceOid,
                        SIP, xAttributes, fileList);

                WCTSoapCall call = new WCTSoapCall(wsEndPoint,
                        "completeArchiving");
                call.invoke(Long.parseLong(targetInstanceOid), archiveIID);
            } catch (Throwable t) {
                log.error("Could not archive " + targetInstanceOid, t);

                try {
                    WCTSoapCall call = new WCTSoapCall(wsEndPoint,
                            "failedArchiving");
                    call.invoke(Long.parseLong(targetInstanceOid),
                            t.getMessage());
                } catch (Exception ex) {
                    log.error(
                            "Got error trying to send \"failedArchiving\" to server",
                            ex);
                }
            }
        }

    }

    public CustomDepositFormResultDTO getCustomDepositFormDetails(
            CustomDepositFormCriteriaDTO criteria)
            throws DigitalAssetStoreException {
        return archive.getCustomDepositFormDetails(criteria);
    }

    /**
     * @param archive the archive to use.
     */
    public void setArchive(Archive archive) {
        this.archive = archive;
    }

    /**
     * @param baseDir the base directory for the digital asset stores harvest files.
     */
    public void setBaseDir(String baseDir) {
        this.baseDir = new File(baseDir);
    }

    public void initiateIndexing(ArcHarvestResultDTO harvestResult)
            throws DigitalAssetStoreException {
        // Determine the source directory.
        File sourceDir = new File(this.baseDir, "/"
                + harvestResult.getTargetInstanceOid() + "/"
                + harvestResult.getHarvestNumber());

        // Kick of the indexer.
        indexer.runIndex(harvestResult, sourceDir);
    }

    public void initiateRemoveIndexes(ArcHarvestResultDTO harvestResult)
            throws DigitalAssetStoreException {
        // Determine the source directory.
        File sourceDir = new File(this.baseDir, "/"
                + harvestResult.getTargetInstanceOid() + "/"
                + harvestResult.getHarvestNumber());

        // Kick of the indexer.
        indexer.removeIndex(harvestResult, sourceDir);
    }

    public Boolean checkIndexing(Long harvestResultOid)
            throws DigitalAssetStoreException {

        return indexer.checkIndexing(harvestResultOid);
    }

    /**
     * @param indexer the indexer to set
     */
    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    /**
     * @param fileMover the fileMover to set
     */
    public void setDasFileMover(DasFileMover fileMover) {
        this.dasFileMover = fileMover;
    }

    /**
     * @param wsEndPoint the wsEndPoint to set
     */
    public void setWsEndPoint(WebServiceEndPoint wsEndPoint) {
        this.wsEndPoint = wsEndPoint;
    }

    // Moves the ArchiveRecord stream past the HTTP status line;
    // checks if it starts with HTTP and ends with CRLF
    private void skipStatusLine(ArchiveRecord record) throws IOException {
        if (record.available() > 0) {
            int i;
            char[] proto = new char[4];
            for (int c = 0; c < 4; c++) {
                if ((i = record.read()) == -1) {
                    throw new IOException("Malformed HTTP Status-Line");
                }
                proto[c] = (char)i;
            }
            if (!"HTTP".equals(new String(proto))) {
                throw new IOException("Malformed HTTP Status-Line");
            }
            char c0 = '0';
            char c1;
            while ((i = record.read()) != -1) {
                c1 = (char)i;
                if (c0 == '\r' && c1 == '\n') {
                    break;
                }
                c0 = c1;
            }
        }
    }

    private void skipHeaders(ArchiveRecord record) throws IOException {
        HttpParser.parseHeaders(record, WARCConstants.DEFAULT_ENCODING);
    }

    public void setPageImagePrefix(String pageImagePrefix) {
        this.pageImagePrefix = pageImagePrefix;
    }

    public String getPageImagePrefix() {
        return pageImagePrefix;
    }

    public void setAqaReportPrefix(String aqaReportPrefix) {
        this.aqaReportPrefix = aqaReportPrefix;
    }

    public String getAqaReportPrefix() {
        return aqaReportPrefix;
    }

    /**
     * @param fileArchive the fileArchive to set
     */
    public void setFileArchive(FileArchive fileArchive) {
        this.fileArchive = fileArchive;
    }

}
