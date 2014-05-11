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

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import java.io.IOException;
import java.util.ArrayList;


public class WctDataExtractValidatorTest {
    public static final String EMPTY_METS_GENERATED_FROM_HTTRACK_WRITER = "src/test/resources/HTtrack_empty_Mets.xml";
    private WctDataExtractorStub extractor;

    private ArrayList<SeedUrl> seedUrls = new ArrayList<SeedUrl>();

    private String seedUrl = "seedUrl";
    private String ilsReference = "ilsReference";
    private String createdBy = "createdBy";
    private String copyrightStatement = "copyrightStatement";
    private String copyrightUrl = "copyrightUrl";
    private String accessRestriction = "accessRestriction";
    private String harvestDate = "harvestDate";

    @Before
    public void setUp() throws IOException {
        extractor = new WctDataExtractorStub();
        SeedUrl url = new SeedUrl(seedUrl, SeedUrl.Type.Primary);
        seedUrls.add(url);

        extractor.setSeedUrls(seedUrls);
        extractor.setIlsReference(ilsReference);
        extractor.setCreatedBy(createdBy);
        extractor.setCopyrightStatement(copyrightStatement);
        extractor.setCopyrightURL(copyrightUrl);
        extractor.setAccessRestriction(accessRestriction);
        extractor.setHarvestDate(harvestDate);
    }

    @Test
    public void test_document_validates_when_all_properties_set() {
        WctDataExtractValidator validator = new WctDataExtractValidatorImpl();
        assertThat(validator.validate(extractor), is(true));
    }

    @Test(expected = WctMetsValidationException.class)
    public void test_document_contains_seed_urls() {
        extractor.setSeedUrls(new ArrayList<SeedUrl>());
        WctDataExtractValidator validator = new WctDataExtractValidatorImpl();
        assertThat(validator.validate(extractor), is(false));
    }

    @Test(expected = WctMetsValidationException.class)
    public void test_document_contains_ils_reference() {
        extractor.setIlsReference("");
        WctDataExtractValidator validator = new WctDataExtractValidatorImpl();
        assertThat(validator.validate(extractor), is(false));
    }

   @Test(expected = WctMetsValidationException.class)
    public void test_document_contains_copyright_statement() {
       extractor.setCopyrightURL("");
        extractor.setCopyrightStatement("");
        WctDataExtractValidator validator = new WctDataExtractValidatorImpl();
        assertThat(validator.validate(extractor), is(false));
    }

    @Test(expected = WctMetsValidationException.class)
    public void test_document_contains_access_restriction() {
        extractor.setAccessRestriction("");
        WctDataExtractValidator validator = new WctDataExtractValidatorImpl();
        assertThat(validator.validate(extractor), is(false));
    }

    @Test(expected = WctMetsValidationException.class)
    public void test_document_contains_access_seed_url() {
        extractor.setAccessRestriction("");
        WctDataExtractValidator validator = new WctDataExtractValidatorImpl();
        assertThat(validator.validate(extractor), is(false));
    }

    @Test(expected = WctMetsValidationException.class)
    public void test_document_contains_harvest_date() {
        extractor.setHarvestDate("");
        WctDataExtractValidator validator = new WctDataExtractValidatorImpl();
        assertThat(validator.validate(extractor), is(false));
    }

}
