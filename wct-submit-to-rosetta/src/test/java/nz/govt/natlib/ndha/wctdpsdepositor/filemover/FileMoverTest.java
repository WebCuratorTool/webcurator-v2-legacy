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

import nz.govt.natlib.ndha.common.FixityUtils;
import nz.govt.natlib.ndha.wctdpsdepositor.Constants;
import nz.govt.natlib.ndha.wctdpsdepositor.WctDepositParameter;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.ArchiveFile;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.FileSystemArchiveFile;
import nz.govt.natlib.ndha.wctdpsdepositor.mets.MetsDocument;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class FileMoverTest {

    private static final String TEST_DIRECTORY = "src/test/resources/WctFiles";

    @Test
    public void test_moving_file_to_server() throws IOException {
        Mockery mockContext = new Mockery();
        final FileMoverStrategy mockedFileMoverStrategy = mockContext.mock(FileMoverStrategy.class);
        final WctDepositParameter depositParameter = new WctDepositParameter();

        createExpectations(mockContext, mockedFileMoverStrategy, depositParameter);

        List<ArchiveFile> files = populateListWithOneArchiveFile();

        FileMover fileMover = new FileMoverImpl(mockedFileMoverStrategy);
        MetsDocument metsDoc = new MetsDocument("mets.xml", "the xml");
        fileMover.move(metsDoc, files, depositParameter);

        assertThat(metsDoc.getDepositDirectoryName(), containsString("deposit"));

        mockContext.assertIsSatisfied();
    }


    private void createExpectations(Mockery mockContext, final FileMoverStrategy mockedFileMoverStrategy, final WctDepositParameter depositParameter) throws IOException {
        mockContext.checking(new Expectations() {
            {
                one(mockedFileMoverStrategy).connect(depositParameter);

                // 1. change to deposit, 2. change to content, 3. change to streams
                exactly(3).of(mockedFileMoverStrategy).createAndChangeToDirectory(with(any(String.class)));

                exactly(2).of(mockedFileMoverStrategy).storeFile(with(any(String.class)), with(any(InputStream.class)));

                one(mockedFileMoverStrategy).close();
            }
        });
    }

    private List<ArchiveFile> populateListWithOneArchiveFile() {
        String fullPath = TEST_DIRECTORY + "/" + Constants.LOG_FILE_NAME;
        File archiveFile = new File(fullPath);
        String originalMd5;
        try {
            originalMd5 = FixityUtils.calculateMD5(archiveFile);
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException("FileNotFoundException thrown by MD5 fixity utility.", fnfe);
        }
        ArchiveFile af = new FileSystemArchiveFile(Constants.LOG_FILE_MIME_TYPE, originalMd5, Constants.LOG_FILE_NAME, TEST_DIRECTORY);
        List<ArchiveFile> files = new ArrayList<ArchiveFile>();
        files.add(af);
        return files;
    }

}
