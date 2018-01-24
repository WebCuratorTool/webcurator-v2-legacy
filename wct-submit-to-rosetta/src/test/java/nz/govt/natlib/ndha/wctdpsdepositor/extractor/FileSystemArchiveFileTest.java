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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import nz.govt.natlib.ndha.common.FixityUtils;
import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.*;


public class FileSystemArchiveFileTest {
    private static final String TEST_DIRECTORY = "src/test/resources/WctFiles";
    private final String FILE_NAME = "order.xml";
    private static final String FIXITY = "5b8e0ef130911c544e406f99cb5eb90a";


    @Test
    public void test_add_directory_to_file_location() {
        FileSystemArchiveFile af = new FileSystemArchiveFile("mime", FIXITY, FILE_NAME, TEST_DIRECTORY);
        assertThat(af.generateFilePath(), is(equalTo(TEST_DIRECTORY + "/" + FILE_NAME)));
    }

    @Test
    public void test_file_name_parsed_from_arcs_location_name() {
        FileSystemArchiveFile af = new FileSystemArchiveFile("mime", FIXITY, FILE_NAME, TEST_DIRECTORY);
        assertThat(af.getFileName(), is(equalTo(FILE_NAME)));
    }

    @Test(expected = RuntimeException.class)
    public void test_exception_thrown_on_bad_fixity() throws IOException {
        FileSystemArchiveFile af = new FileSystemArchiveFile("mime", "badFixity", FILE_NAME, TEST_DIRECTORY);
        af.toStream();
    }

    @Test
    public void test_that_toStream_returns_contents_of_file() throws IOException {

        String fullPath = TEST_DIRECTORY + "/" + FILE_NAME;
        File preArchiveFile = new File(fullPath);
        String originalMd5;
        try {
            originalMd5 = FixityUtils.calculateMD5(preArchiveFile);
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException("FileNotFoundException thrown by MD5 fixity utility.", fnfe);
        }

        InputStream is = null;
        try {
            FileSystemArchiveFile af = new FileSystemArchiveFile("mime", originalMd5, FILE_NAME, TEST_DIRECTORY);
            is = af.toStream();

            String valueFromStream = buildStringFromStream(is);
            assertThat(valueFromStream, containsString("<string name=\"settings-directory\">settings</string>"));
        }
        finally {
            is.close();
        }

    }

    @Test
    public void test_copy_stream_to_file() throws IOException {
        FileSystemArchiveFile fsaf = new FileSystemArchiveFile("mime", FIXITY, FILE_NAME, TEST_DIRECTORY);
        String tempDirectory = "src/test/resources/temp";

        File tempFile = new File(tempDirectory + "/" + FILE_NAME);
        if (tempFile.exists())
            tempFile.delete();

        fsaf.copyStreamToDirectory(tempDirectory);
        assertThat(tempFile.exists(), is(true));
    }

    private String buildStringFromStream(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }

        br.close();
        String valueFromStream = sb.toString();
        return valueFromStream;
    }


}
