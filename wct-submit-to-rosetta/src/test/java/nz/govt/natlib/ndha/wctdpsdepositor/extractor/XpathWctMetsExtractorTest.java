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

import nz.govt.natlib.ndha.wctdpsdepositor.extractor.WctRequiredData.SeedUrl;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.filefinder.FileArchiveBuilder;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.filefinder.FileSystemArchiveBuilder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
//import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;


public class XpathWctMetsExtractorTest {
    private static final String testMetsPath = "src/test/resources/METS-11141123.xml";
    private static final String TEST_DIRECTORY = "src/test/resources/WctFiles";

    private XPathWctMetsExtractor extractor;

    public static final SeedUrl SEED_URL = new SeedUrl("http://www.nzcee.co.nz/", SeedUrl.Type.Primary);
    public static final String ACCESS_RESTRICTION = "Open (unrestricted) access";
    public static final String COPYRIGHT_URL = "a CopyrightURL";
    public static final String COPYRIGHT_STATEMENT = "a copyright statement";
    public static final String PROVENANCE_NOTE = "Original Harvest";
    public static final String CREATION_DATE = "2007-08-13";
    public static final String CREATED_BY = "LeeG";
    public static final String ILS_REFERENCE = "1010528";
    public static final String TARGET_NAME = "New Zealand Centre for Ecological Economics ; NZCEE";
    public static final String HARVEST_DATE = "2007-08-13 20:00:24.61";

    public static final String FILE_LOCATION = "order.xml";
    public static final String FILE_MIME_TYPE = "text/xml";
    public static final String FILE_FIXITY = "94e0ea31f0d9403ff75b75ff10ec1912";

    public static final String ARCHIVE_FILE_LOCATION = "WCT-20070813080023-00003-skynet.arc";
    public static final String ARCHIVE_FILE_MIME_TYPE = "application/octet-stream";
    public static final String ARCHIVE_FILE_FIXITY = "cfdcd7b83795d949c1311723b3a670c1";

    public static final String LOG_FILE_LOCATION = "crawl.log";
    public static final String LOG_FILE_MIME_TYPE = "text/plain";
    public static final String LOG_FILE_FIXITY = "67ce4ff04a5c833aa5c06c10ad110c49";

    private static final String REPORT_FILE_LOCATION = "seeds.txt";
    private static final String REPORT_FILE_MIME_TYPE = "text/plain";

    private static final String HOME_DIRECTORY_FILE_LOCATION = "order.xml";
    private static final String HOME_DIRECTORY_FILE_MIME_TYPE = "text/xml";

    public static final String WCT_METS_FILE_LOCATION = "METS-11141123.xml";
    public static final String WCT_METS_FILE_MIME_TYPE = "text/xml";

    public static final String EMPTY_METS_GENERATED_FROM_HTTRACK_WRITER = "src/test/resources/HTtrack_empty_Mets.xml";

    @Before
    public void setUp() throws IOException {
        File file = new File(testMetsPath);
        FileArchiveBuilder fileFinder = new FileSystemArchiveBuilder(TEST_DIRECTORY);
        this.extractor = new XPathWctMetsExtractor();
        this.extractor.setAccessRestriction(ACCESS_RESTRICTION);
        extractor.parseFile(file, fileFinder);
        extractor.setILSReference(ILS_REFERENCE);
    }

    @Test
    public void test_extract_harvest_date() {
        assertThat(extractor.getHarvestDate(), is(equalTo(HARVEST_DATE)));
    }

    @Test
    public void test_extract_target_name() {
        assertThat(extractor.getTargetName(), is(equalTo(TARGET_NAME)));
    }

    @Test
    public void test_extract_ILS_reference() {
        assertThat(extractor.getILSReference(), is(equalTo(ILS_REFERENCE)));
    }

    @Test
    public void test_extract_created_by() {
        assertThat(extractor.getCreatedBy(), is(equalTo(CREATED_BY)));
    }

    @Test
    public void test_extract_creation_date() {
        assertThat(extractor.getCreationDate(), is(equalTo(CREATION_DATE)));
    }


    @Test
    public void test_extract_provenance_note() {
        assertThat(extractor.getProvenanceNote(), is(equalTo(PROVENANCE_NOTE)));
    }

    @Test
    public void test_extract_copyright_statement() {
        assertThat(extractor.getCopyrightStatement(), is(equalTo(COPYRIGHT_STATEMENT)));
    }

    @Test
    public void test_extract_copyright_url() {
        assertThat(extractor.getCopyrightURL(), is(equalTo(COPYRIGHT_URL)));
    }

    @Test
    public void test_extract_access_restriction() {
        assertThat(extractor.getAccessRestriction(), is(equalTo(ACCESS_RESTRICTION)));
    }

    @Test
    public void test_extract_seedUrls() {
        List<SeedUrl> seedUrls = extractor.getSeedUrls();

        assertThat(seedUrls.size(), is(equalTo(1)));
        assertThat(seedUrls, hasItem(SEED_URL));
    }

    @Test
    public void test_wct_mets_file_included_in_dps_mets() {
        ArchiveFile fvo = extractor.getWctMetsFile();

        assertThat(fvo.getMimeType(), is(equalTo(WCT_METS_FILE_MIME_TYPE)));
    }

    @Test
    public void test_extract_archive_files() {
        List<ArchiveFile> files = extractor.getArchiveFiles();

        assertThat(files.size(), is(equalTo(1)));

        ArchiveFile fvo = files.get(0);
        assertThat(fvo.getFileName(), is(equalTo(ARCHIVE_FILE_LOCATION)));
        assertThat(fvo.getMimeType(), is(equalTo(ARCHIVE_FILE_MIME_TYPE)));
    }

    @Test
    public void test_extract_log_files() {
        List<ArchiveFile> files = extractor.getLogFiles();

        assertThat(files.size(), is(equalTo(2)));

        ArchiveFile fvo = files.get(0);
        assertThat(fvo.getFileName(), is(equalTo(LOG_FILE_LOCATION)));
        assertThat(fvo.getMimeType(), is(equalTo(LOG_FILE_MIME_TYPE)));
    }

    @Test
    public void test_extract_report_files() {
        List<ArchiveFile> files = extractor.getReportFiles();

        assertThat(files.size(), is(equalTo(9)));

        ArchiveFile fvo = files.get(0);
        assertThat(fvo.getFileName(), is(equalTo(REPORT_FILE_LOCATION)));
        assertThat(fvo.getMimeType(), is(equalTo(REPORT_FILE_MIME_TYPE)));
    }

    @Test
    public void test_extract_home_directory_files() {
        List<ArchiveFile> files = extractor.getHomeDirectoryFiles();

        assertThat(files.size(), is(equalTo(1)));

        ArchiveFile fvo = files.get(0);
        assertThat(fvo.getFileName(), is(equalTo(HOME_DIRECTORY_FILE_LOCATION)));
        assertThat(fvo.getMimeType(), is(equalTo(HOME_DIRECTORY_FILE_MIME_TYPE)));
    }

    @Test
    public void test_arc_index_returned_in_all_archive_files_getter() {
        final String fakeArcIndes = "a index";

        ArchiveFile arcIndex = new ArchiveFile() {
            public InputStream toStream() throws IOException {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fakeArcIndes.getBytes());
                return byteArrayInputStream;
            }

            public File copyStreamToDirectory(String directory) {
                return null;
            }
            
        };

        int beforeSize = extractor.getAllFiles().size();
        extractor.setArcIndexFile(arcIndex);
        int afterSize = extractor.getAllFiles().size();
        assertThat(beforeSize < afterSize, is(true));
    }

    @Test
    public void test_multiple_seed_urls() throws IOException {
        File file = new File("src/test/resources/METS-622593.xml");
        FileArchiveBuilder fileFinder = new FileSystemArchiveBuilder(TEST_DIRECTORY) {
            public FileSystemArchiveFile createFileFrom(String mimeType, String expectedCheckSum, String fileName) {
                return null;
            }
        };
        this.extractor = new XPathWctMetsExtractor();
        extractor.parseFile(file, fileFinder);
        List<SeedUrl> seedUrls = extractor.getSeedUrls();
        assertThat(seedUrls.size(), is(equalTo(3)));
        int primaryCount = 0;
        int secondaryCount = 0;
        List<String> secondaryUrls = new ArrayList<String>();
        for (SeedUrl url: seedUrls) {
            assertNotNull(url.type);
            if (url.type.equals(SeedUrl.Type.Primary)) {
                assertEquals("http://cyfswatch.blogspot.com/", url.url);
                primaryCount++;
            } else if (url.type.equals(SeedUrl.Type.Secondary)) {
                secondaryUrls.add(url.url);
                secondaryCount++;
            }
        }
        assertThat(primaryCount, is(equalTo(1)));
        assertThat(secondaryCount, is(equalTo(2)));
        assertThat(secondaryUrls, hasItem("http://cyfswatchnz.wordpress.com/"));
        assertThat(secondaryUrls, hasItem("http://www2.blogger.com/profile/07356437267198812221"));
    }
}
