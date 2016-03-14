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

package nz.govt.natlib.ndha.wctdpsdepositor.preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import nz.govt.natlib.ndha.common.FixityUtils;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.ArchiveFile;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.FileSystemArchiveFile;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.WctDataExtractor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.archive.io.arc.ARCReader;
import org.archive.io.arc.ARCReaderFactory;
import org.archive.io.warc.WARCReader;
import org.archive.io.warc.WARCReaderFactory;

public class ArcIndexProcessor implements PreDepositProcessor {
    private static final Log log = LogFactory.getLog(ArcIndexProcessor.class);

    private static final boolean APPEND_TO_FILE = true;
    private static final String CDX_MIME_TYPE = "text/plain";

    public File process(WctDataExtractor data) {
        return process(null, data);
    }

    public File process(String tempDirectory, WctDataExtractor data) {
        if (log.isDebugEnabled())
            log.debug("Creating index of arc files.");

        List<ArchiveFile> arcFiles = data.getArchiveFiles();

        boolean isFirstArc = true;
        boolean copyArcFiles = (tempDirectory == null) ? false : true;

        FileWriter cdxOfFirstArcWriter = null;
        File cdxOfFirstArcFile = null;
        List<File> tempArcFileList = new ArrayList<File>();
        List<File> tempCdxFileList = new ArrayList<File>();

        try {
            for (ArchiveFile arcFile : arcFiles) {
                File arcFileToWorkWith;

                if (copyArcFiles) {
                    arcFileToWorkWith = arcFile.copyStreamToDirectory(tempDirectory);
                    tempArcFileList.add(arcFileToWorkWith);
                } else {
                    arcFileToWorkWith = new File(((FileSystemArchiveFile)arcFile).generateFilePath());
                }
                try {
                    /*
                     * Sometimes, there may be other files in the arc file list with extensions such as
                     * ".arc.invalid" pr ".cdx" etc. So just to make sure we don't try to compute CDX
                     * from these files, do a check.
                     */
                     String path = arcFileToWorkWith.getPath();
                     if(path.startsWith("C:"))
                    	 path = path.replace("C:","");
                     String lcPath = path.toLowerCase();
					if(lcPath.endsWith(".arc") || lcPath.endsWith(".arc.gz")) {
						ARCReaderFactory.get(path);
					} else if(lcPath.endsWith(".warc") || lcPath.endsWith(".warc.gz")) {
						WARCReaderFactory.get(path);
					} else {
						continue;
					}
                } catch (Exception ex) {
                    log.warn("The format of arc file " + arcFileToWorkWith.getAbsolutePath() 
                            + " may not be a supported one. Continuing with next arc file. Exception:", ex);
                    continue;
                }
                if (isFirstArc) {
                    isFirstArc = false;
                    cdxOfFirstArcFile = createCdxFrom(arcFileToWorkWith);
                    cdxOfFirstArcWriter = openFileWriter(cdxOfFirstArcFile);
                } else {
                    File cdxFile = createCdxFrom(arcFileToWorkWith);
                    tempCdxFileList.add(cdxFile);
                    BufferedReader cdxReader = readCDXFile(arcFileToWorkWith);
                    appendCdxToWriter(cdxOfFirstArcWriter, cdxReader);
                    cdxReader.close();
                }
            }
        }
        catch (IOException ioe) {
            log.error("Exception occurred while calculating CDX indexies of ARC files.", ioe);
            throw new RuntimeException(ioe);
        }
        finally {
            closeStream(cdxOfFirstArcWriter);
            cleanUpTemporaryFiles(tempCdxFileList);
            if (copyArcFiles)
                cleanUpTemporaryFiles(tempArcFileList);
        }

        addCdxToWctData(data, cdxOfFirstArcFile);
        return cdxOfFirstArcFile;
    }

    private void deleteFile(File file) {
        if (file.exists() == false) return;
        String errorText = "Error deleting the file " + file.getAbsolutePath() + " - it needs to be manually deleted";
        try {
            boolean status = file.delete();
            if (status == true) {
                log.info("The file " + file.getAbsolutePath() + " has been deleted successfully");
            } else {
                log.error(errorText);
            }
        } catch (RuntimeException ex) {
            log.error(errorText, ex);
        }
    }

    private void cleanUpTemporaryFiles(List<File> tempFileList) {
        for (File file: tempFileList) {
            deleteFile(file);
        }
    }

    private void appendCdxToWriter(FileWriter cdxOfFirstArcWriter, BufferedReader cdxReader) throws IOException {
        skipFirstLine(cdxReader);

        String line;
        while ((line = cdxReader.readLine()) != null) {
            cdxOfFirstArcWriter.append(line);
            cdxOfFirstArcWriter.append('\n');
        }
    }

    private void addCdxToWctData(WctDataExtractor data, File cdxOfFirstArcFile) {
        String cdxFixity = calculateFixityOfCDX(cdxOfFirstArcFile);
        ArchiveFile cdxArchiveFile = new FileSystemArchiveFile(CDX_MIME_TYPE, cdxFixity, cdxOfFirstArcFile.getName(), cdxOfFirstArcFile.getParent());
        data.setArcIndexFile(cdxArchiveFile);
    }


    private void closeStream(FileWriter cdxOfFirstArcWriter) {
        try {
            if (cdxOfFirstArcWriter != null)
                cdxOfFirstArcWriter.close();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private BufferedReader readCDXFile(File tempArcFile) throws IOException {
        String arcFileName = tempArcFile.getPath();
        String cdxFile = determineCdxNameFrom(arcFileName);
        return new BufferedReader(new FileReader(cdxFile));
    }

    private File createCdxFrom(File arcFile) throws IOException {
        try {
            String arcFilePath = arcFile.getPath();
            if(arcFilePath.startsWith("C:"))
            	arcFilePath = arcFilePath.replace("C:", "");
            String lcPath = arcFilePath.toLowerCase();
			if(lcPath.endsWith(".arc") || lcPath.endsWith(".arc.gz")) {
            ARCReader.createCDXIndexFile(arcFilePath);
			} else if(lcPath.endsWith(".warc") || lcPath.endsWith(".warc.gz")) {
				WARCReader.createCDXIndexFile(arcFilePath);
			}

            String cdxFileName = determineCdxNameFrom(arcFilePath);

            File cdxFile = new File(cdxFileName);
            if (!cdxFile.exists())
                throw new RuntimeException("The temporary CDX file: " + cdxFile.getName() + " created from the arc file: " + arcFile.getName() + " was not found");

            return cdxFile;
        } catch (ParseException pe) {
            throw new RuntimeException("Exception occurred while creating the CDX index of the ARC file: " + arcFile.getName(), pe);
        }

    }

    private String determineCdxNameFrom(String arcFileName) {
        if (arcFileName.endsWith(".arc"))
            return replaceFileExtension(arcFileName, ".arc", ".cdx");
        else if (arcFileName.endsWith(".arc.gz"))
            return replaceFileExtension(arcFileName, ".arc.gz", ".cdx");
        else if (arcFileName.endsWith(".warc"))
            return replaceFileExtension(arcFileName, ".warc", ".cdx");
        else if (arcFileName.endsWith(".warc.gz"))
            return replaceFileExtension(arcFileName, ".warc.gz", ".cdx");
        else {
            throw new UnsupportedOperationException("The arc file extension is neither .arc nor .arc.gz. The current implementation does not support any other extensions");
        }
    }

    private String replaceFileExtension(String str, String oldExtension, String newExtension) {
        int index = str.lastIndexOf(oldExtension);
        return str.substring(0, index) + newExtension;
    }

    private FileWriter openFileWriter(File cdxFile) {
        try {
            return new FileWriter(cdxFile.getPath(), APPEND_TO_FILE);
        } catch (IOException ioe) {
            throw new RuntimeException("Failed to read the CDX file: " + cdxFile.getName(), ioe);
        }

    }

    private String calculateFixityOfCDX(File cdxOfFirstArcFile) {
        try {
            return FixityUtils.calculateMD5(cdxOfFirstArcFile);
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException(fnfe);
        }
    }

    private void skipFirstLine(BufferedReader cdxReader) throws IOException {
        cdxReader.readLine();
    }
}
