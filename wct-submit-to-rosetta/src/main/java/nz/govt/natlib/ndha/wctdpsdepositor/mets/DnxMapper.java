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

import com.exlibris.digitool.common.dnx.DnxDocument;
import com.exlibris.core.sdk.formatting.DublinCore;
import com.google.inject.ImplementedBy;
import nz.govt.natlib.ndha.wctdpsdepositor.extractor.WctDataExtractor;

/**
 * The class builds a DPS compliant MET's document using metadata harvested by
 * an instance of the class @{link WctDataExtractor}.
 */
@ImplementedBy(DnxMapperImpl.class)
public interface DnxMapper {

    MetsDocument generateDnxFrom(WctDataExtractor wctData);
    void addWebHarvestSpecificDc(WctDataExtractor wctData, DublinCore ieDc);
    void addWebHarvestSpecificDnx(WctDataExtractor wctData, DnxDocument dnx);

}
