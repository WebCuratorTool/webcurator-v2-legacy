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

import org.apache.commons.lang.StringUtils;
import java.util.List;


public class WctDataExtractValidatorImpl implements WctDataExtractValidator {

    /**
     * Validates that all required properties of the Mets document were populated in the extract.
     */
    public boolean validate(WctRequiredData extractor) {
        isFieldValid("harvest date", extractor.getHarvestDate());
        isFieldValid("seed url", extractor.getSeedUrls());
        // isFieldValid("created by", extractor.getCreatedBy());

        if (StringUtils.isBlank(extractor.getCopyrightURL()))
            isFieldValid("copyright statement (or copyright URL)", extractor.getCopyrightStatement());

        isFieldValid("access restriction", extractor.getAccessRestriction());
        isFieldValid("ILS reference", extractor.getILSReference());

        return true;
    }

    private void isFieldValid(String displayableFieldName, List<SeedUrl> list) {
        if (list.isEmpty())
            throw new WctMetsValidationException("The property \"" + displayableFieldName + "\" was not populated, the DPS deposit service requires a value for this property to be specified.");

        for (SeedUrl item : list)
            isFieldValid(displayableFieldName, item.url);
        
    }

    private void isFieldValid(String displayableFieldName, String field) {
        if (StringUtils.isBlank(field))
            throw new WctMetsValidationException("The property " + displayableFieldName + " was not populated, the DPS deposit service requires a value for this property to be specified.");
        
    }

}
