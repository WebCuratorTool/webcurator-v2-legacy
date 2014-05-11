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

import nz.govt.natlib.ndha.wctdpsdepositor.extractor.ArchiveFile;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.FileSystemArchiveFile;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.WctDataExtractor;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ArcIndexProcessorTest {

    private static final String TEST_ARC_1 = "TestArc1.arc";
    private static final String TEST_ARC_2 = "TestArc2.arc";
    private static final String DIRECTORY = "src/test/resources";
    private static final String tempDirectory = "src/test/resources/temp";

    @Test
    public void test_cdx_index_created_for_arc_file() throws IOException {
        Mockery mockContext = new Mockery();
        final WctDataExtractor dataExtractor = mockContext.mock(WctDataExtractor.class);

        final List<ArchiveFile> arcFiles = new ArrayList<ArchiveFile>();
        ArchiveFile archive1File = new FileSystemArchiveFile("application/octet-stream", "", TEST_ARC_1, DIRECTORY);
        arcFiles.add(archive1File);

        ArchiveFile archive2File = new FileSystemArchiveFile("application/octet-stream", "", TEST_ARC_2, DIRECTORY);
        arcFiles.add(archive2File);

        mockContext.checking(new Expectations() {
            {
                allowing(dataExtractor).getArchiveFiles();
                will(returnValue(arcFiles));

                one(dataExtractor).setArcIndexFile(with(any(ArchiveFile.class)));

            }
        });


        ArcIndexProcessor processor = new ArcIndexProcessor();

        File cdxFile = processor.process(tempDirectory, dataExtractor);

        BufferedReader br = new BufferedReader(new FileReader(cdxFile));
        String line = null;

        int lineCount = 0;
        br.readLine(); // skip first line
        while ((line = br.readLine()) != null) {
            lineCount++;
            if (lineCount < 12)
                assertThat(line, containsString("TestArc1"));
            else
                assertThat(line, containsString("TestArc2"));
        }

        assertThat(lineCount, is(equalTo(22)));


        mockContext.assertIsSatisfied();

    }

}
