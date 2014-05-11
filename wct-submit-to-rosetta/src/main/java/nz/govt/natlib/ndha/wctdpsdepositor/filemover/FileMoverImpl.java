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

package nz.govt.natlib.ndha.wctdpsdepositor.filemover;

import com.google.inject.Inject;

import nz.govt.natlib.ndha.wctdpsdepositor.WctDepositParameter;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.ArchiveFile;
import nz.govt.natlib.ndha.wctdpsdepositor.mets.MetsDocument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileMoverImpl implements FileMover {
    private static final Log log = LogFactory.getLog(FileMoverImpl.class);
    private static final String STREAMS_DIRECTORY = "streams";
    private static final String CONTENT_DIRECTORY = "content";

    private FileMoverStrategy fileMover;
    private static final String DEPOSIT_DIRECTORY = "deposit";

    @Inject
    public FileMoverImpl(FileMoverStrategy fileMover) {
        this.fileMover = fileMover;
    }


    public void move(MetsDocument metsDoc, List<ArchiveFile> archiveFiles, WctDepositParameter depositParameter) {
        try {
            fileMover.connect(depositParameter);
            String depositDirectoryName = createUniqueDepositName();
            metsDoc.setDepositDirectoryName(depositDirectoryName);

            String ftpDirectory = depositParameter.getFtpDirectory();
            ftpDirectory = (ftpDirectory == null || ftpDirectory.trim().equals("")) ? "" : (ftpDirectory + "/");
            String depositRootDirectory = ftpDirectory + depositDirectoryName;
            log.info("Copying the target instance files to the deposit directory " + depositRootDirectory);

            fileMover.createAndChangeToDirectory(depositRootDirectory);
            fileMover.createAndChangeToDirectory(CONTENT_DIRECTORY);

            InputStream metsStream = new ByteArrayInputStream(metsDoc.toXML().getBytes());
            fileMover.storeFile(metsDoc.getFileName(), metsStream);

            fileMover.createAndChangeToDirectory(STREAMS_DIRECTORY);

            for (ArchiveFile archiveFile : archiveFiles)
                fileMover.storeFile(archiveFile.getFileName(), archiveFile.toStream());

        }
        catch (IOException ioe) {
            throw new RuntimeException("An exception occured while uploading archiveFiles to the server", ioe);
        }
        finally {
            fileMover.close();
        }
    }

    private String createUniqueDepositName() {
        String directoryName = DEPOSIT_DIRECTORY + "-" + Long.toString(new Date().getTime());
        return directoryName;
    }

}
