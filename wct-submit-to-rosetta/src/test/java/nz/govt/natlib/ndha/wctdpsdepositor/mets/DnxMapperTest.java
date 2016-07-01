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

package nz.govt.natlib.ndha.wctdpsdepositor.mets;

import nz.govt.natlib.ndha.wctdpsdepositor.Constants;
import nz.govt.natlib.ndha.wctdpsdepositor.CustomDepositField;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.ArchiveFile;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.FileSystemArchiveFile;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.WctDataExtractor;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.WctRequiredData.SeedUrl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import static org.junit.Assert.*;
import org.webcurator.core.archive.dps.DpsDepositFacade.HarvestType;

import com.exlibris.core.sdk.formatting.DublinCore;

import java.util.ArrayList;
import java.util.List;

public class DnxMapperTest {
    private Mockery mockContext;
    private WctDataExtractor mockedStrategy;
    private List<String> values = new ArrayList<String>();
    private HarvestType harvestType;
    private String ieEntityType;
    private static final String TEST_DIRECTORY = "src/test/resources/";
    private static final String TARGET_INSTANCE_ID = "1234567890";

    public void setUpMockContext() {
        mockContext = new Mockery() {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        mockedStrategy = mockContext.mock(WctDataExtractor.class);

        final ArchiveFile cdxIndexFile = new FileSystemArchiveFile(Constants.FILE_MIME_TYPE, Constants.FILE_FIXITY, Constants.FILE_LOCATION, TEST_DIRECTORY);
        final ArchiveFile archiveFile = new FileSystemArchiveFile(Constants.FILE_MIME_TYPE, Constants.FILE_FIXITY, Constants.FILE_LOCATION, TEST_DIRECTORY);
        final ArchiveFile wctMetsArchiveFile = new FileSystemArchiveFile(Constants.WCT_METS_FILE_MIME_TYPE, Constants.WCT_METS_FILE_FIXITY, Constants.WCT_METS_FILE_LOCATION, TEST_DIRECTORY);

        final List<SeedUrl> seedUrls = new ArrayList<SeedUrl>();
        SeedUrl url = new SeedUrl(Constants.SEED_URL, SeedUrl.Type.Primary);
        seedUrls.add(url);

        final List<ArchiveFile> archiveFiles = new ArrayList<ArchiveFile>();
        archiveFiles.add(archiveFile);

        final List<CustomDepositField> customDepositFields = new ArrayList<CustomDepositField>();
        customDepositFields.add(new CustomDepositField("customDepositForm_bibliographicCitation", "DctermsBibliographicCitation", "bibliographicCitation", "dcterms"));
        customDepositFields.add(new CustomDepositField("customDepositForm_dctermsAvailable", "DctermsAvailable", "available", "dcterms"));

        mockContext.checking(new Expectations() {
            {

                atLeast(1).of(mockedStrategy).getWctTargetInstanceID();
                will(returnValue(TARGET_INSTANCE_ID));
                values.add(TARGET_INSTANCE_ID);

                atLeast(1).of(mockedStrategy).getAccessRestriction();
                will(returnValue(Constants.ACCESS_RESTRICTION));
                values.add(Constants.ACCESS_RESTRICTION);

                one(mockedStrategy).getCreatedBy();
                will(returnValue(Constants.CREATED_BY));
                values.add(Constants.CREATED_BY);

                atLeast(1).of(mockedStrategy).getCreationDate();
                will(returnValue(Constants.CREATION_DATE));
                values.add(Constants.CREATION_DATE);

                atLeast(1).of(mockedStrategy).getArchiveFiles();
                will(returnValue(archiveFiles));

                atLeast(1).of(mockedStrategy).getArcIndexFile();
                will(returnValue(cdxIndexFile));

                atLeast(1).of(mockedStrategy).getHomeDirectoryFiles();
                will(returnValue(new ArrayList<FileSystemArchiveFile>()));

                atLeast(1).of(mockedStrategy).getLogFiles();
                will(returnValue(new ArrayList<FileSystemArchiveFile>()));

                atLeast(1).of(mockedStrategy).getReportFiles();
                will(returnValue(new ArrayList<FileSystemArchiveFile>()));

                atLeast(1).of(mockedStrategy).getWctMetsFile();
                will(returnValue(wctMetsArchiveFile));

                atLeast(2).of(mockedStrategy).getHarvestDate();
                will(returnValue(Constants.HARVEST_DATE));
                values.add(Constants.HARVEST_DATE);

                one(mockedStrategy).getILSReference();
                will(returnValue(Constants.ILS_REFERENCE));
                values.add(Constants.ILS_REFERENCE);

                one(mockedStrategy).getProvenanceNote();
                will(returnValue(Constants.PROVENANCE_NOTE));
                values.add(Constants.PROVENANCE_NOTE);

                atLeast(1).of(mockedStrategy).getSeedUrls();
                will(returnValue(seedUrls));

                atLeast(1).of(mockedStrategy).getTargetName();
                will(returnValue(Constants.TARGET_NAME));
                values.add(Constants.TARGET_NAME);

                atLeast(1).of(mockedStrategy).getCmsSection();
                will(returnValue(Constants.CMS_SECTION));

                atLeast(1).of(mockedStrategy).getCmsSystem();
                will(returnValue(Constants.CMS_SYSTEM));

                atLeast(1).of(mockedStrategy).getHarvestType();
                will(returnValue(harvestType));
                if (HarvestType.HtmlSerialHarvest.equals(harvestType)) {
                    atLeast(1).of(mockedStrategy).getDcFieldsAdditional();
                    will(returnValue(customDepositFields));

                    DublinCore dc = mockContext.mock(DublinCore.class);
                    one(mockedStrategy).getAdditionalDublinCoreElements();
                    will(returnValue(dc));
                    one(dc).getDctermsValue("bibliographicCitation");
                    will(returnValue("January 2001"));
                    one(dc).getDctermsValue("available");
                    will(returnValue("31/12/2000"));
                    never(dc).getDctermsValue("accrualPeriodicity");
                    never(dc).getDctermsValue("issued");
                    one(mockedStrategy).getIeEntityType();
                    will(returnValue(ieEntityType));
                } else if (HarvestType.TraditionalWebHarvest.equals(harvestType)) {
                    one(mockedStrategy).getIeEntityType();
                    will(returnValue("WebHarvestIE"));
                }
            }
        });
    }

    @Test
    public void test_each_property_mapped_to_dnx_web_harvest() {
        harvestType = HarvestType.TraditionalWebHarvest;
        setUpMockContext();
        DnxMapper dnxMapper = new DnxMapperImpl(new MetsWriterFactoryImpl());
        String metsXml = dnxMapper.generateDnxFrom(mockedStrategy).toXML();
        System.out.println(metsXml);
        mockContext.assertIsSatisfied();
        verifyCommonElementsInsideXML(metsXml);
        verifyInsideXML(new String[] {
                "<dc:title>http://www.nzcee.co.nz/</dc:title>",
                "<dcterms:available>2007-08-13 20:00:24.61</dcterms:available>",
                "<key id=\"IEEntityType\">WebHarvestIE</key>",
            }, metsXml);
    }

    @Test
    public void test_each_property_mapped_to_dnx_html_serial_harvest() {
        harvestType = HarvestType.HtmlSerialHarvest;
        ieEntityType = "HTMLSerialIE";
        setUpMockContext();
        DnxMapper dnxMapper = new DnxMapperImpl(new MetsWriterFactoryImpl());
        String metsXml = dnxMapper.generateDnxFrom(mockedStrategy).toXML();
        System.out.println(metsXml);
        mockContext.assertIsSatisfied();
        verifyCommonElementsInsideXML(metsXml);
        verifyInsideXML(new String[] {
                "<dc:title>New Zealand Centre for Ecological Economics ; NZCEE</dc:title>",
                "<dcterms:available>31/12/2000</dcterms:available>",
                "<dcterms:bibliographicCitation>January 2001</dcterms:bibliographicCitation>",
//                "<dcterms:accrualPeriodicity>Monthly</dcterms:accrualPeriodicity>",
//                "<dcterms:issued>2001</dcterms:issued>",
                "<key id=\"IEEntityType\">HTMLSerialIE</key>",
            }, metsXml);
    }

    @Test(expected=RuntimeException.class)
    public void test_each_property_mapped_to_dnx_html_serial_harvest_entityTypeNotGiven() {
        harvestType = HarvestType.HtmlSerialHarvest;
        ieEntityType = null;
        setUpMockContext();
        DnxMapper dnxMapper = new DnxMapperImpl(new MetsWriterFactoryImpl());
        dnxMapper.generateDnxFrom(mockedStrategy).toXML();
        mockContext.assertIsSatisfied();
    }

    private void verifyCommonElementsInsideXML(String metsXml) {
        verifyInsideXML(new String[] {
                "<dc:date>2007-08-13 20:00:24.61</dc:date>",
                "<key id=\"system\">ilsdb</key>",
                "<key id=\"recordId\">1010528</key>",
                "<dc:rights>100</dc:rights>",
                "<dc:type>InteractiveResource</dc:type>",
                "<dc:format>text</dc:format>",
                "<key id=\"primarySeedURL\">http://www.nzcee.co.nz/</key>",
                "<key id=\"harvestDate\">2007-08-13 20:00:24.61</key>",
                "<key id=\"eventIdentifierType\">WCT</key>",
                "<key id=\"eventDescription\">IE Created in NLNZ WCT</key>",
                "<key id=\"eventDateTime\">2007-08-13 20:00:24</key>",
                "<key id=\"eventDateTime\">2007-08-13 00:00:00</key>",
                "<key id=\"targetName\">New Zealand Centre for Ecological Economics ; NZCEE</key>"
            }, metsXml);
        
    }

    private void verifyInsideXML(String[] snippets, String metsXml) {
        for (String snippet: snippets) {
            assertTrue("Verify the presence of " + snippet, metsXml.contains(snippet));
        }
    }

}
