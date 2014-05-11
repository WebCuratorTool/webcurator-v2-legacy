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

package nz.govt.natlib.ndha.wctdpsdepositor.extractor.filearchivebuilder;

import nz.govt.natlib.ndha.wctdpsdepositor.extractor.FileSystemArchiveFile;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.filefinder.CollectionFileArchiveBuilder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class CollectionFileArchiveBuilderTest {
    private static final String TEST_DIRECTORY = "src/test/resources/WctFiles";
    private final String FILE_NAME = "order.xml";

    private Map<String, File> archiveFileMap;
    private CollectionFileArchiveBuilder builder;

    @Before
    public void setUp() {
        File testFile = new File(TEST_DIRECTORY + "/" + FILE_NAME);
        archiveFileMap = new HashMap<String, File>();
        archiveFileMap.put(testFile.getName(), testFile);
        builder = new CollectionFileArchiveBuilder(archiveFileMap);
    }

    @Test
    public void test_file_system_archive_built() throws IOException {
        FileSystemArchiveFile archiveFile = builder.createFileFrom("test/xml", "5b8e0ef130911c544e406f99cb5eb90a", FILE_NAME);
        assertThat(archiveFile.toStream(), is(instanceOf(FileInputStream.class)));
    }

    @Test(expected = RuntimeException.class)
    public void test_exception_thrown_on_unknown_file_name() throws IOException {
        builder.createFileFrom("test/xml", "124abc", "nosuchfile.xml");
    }

    @Test(expected = RuntimeException.class)
    public void test_exception_thrown_on_null_file() throws IOException {
        archiveFileMap.put("nullFile.xml", null);
        builder.createFileFrom("test/xml", "124abc", "nullFile.xml");
    }


}
