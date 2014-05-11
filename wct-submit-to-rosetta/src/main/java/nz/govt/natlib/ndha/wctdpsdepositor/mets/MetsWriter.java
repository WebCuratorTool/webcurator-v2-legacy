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
import com.exlibris.core.sdk.consts.Enum;
import gov.loc.mets.*;
import org.apache.xmlbeans.XmlOptions;


public interface MetsWriter {
    DublinCore getDublinCoreParser();

    DnxDocument getDnxParser();

    MetsType.FileSec.FileGrp addNewFileGrp(Enum.UsageType usageType, Enum.PreservationType preservationType, String s);

    FileType addNewFile(MetsType.FileSec.FileGrp fileGrp, String s, String s1, String s2);

    StructMapType addNewStructMap(String s);

    DublinCore getIeDublinCore();

    MetsType.FileSec getFileSec();

    MetsType.FileSec.FileGrp getFileGrp(String s);

    MetsType.FileSec.FileGrp[] getFileGrpArray();

    FileType getFile(String s);

    FileType[] getFileArray(String s);

    DnxDocument getIeDnx();

    DnxDocument getFileGrpDnx(String s);

    DnxDocument getFileDnx(String s);

    StructMapType[] getStructMapArray();

    MdSecType[] getDmdSecArray();

    AmdSecType[] getAmdSecArray();

    void setIEDublinCore(DublinCore dublinCore);

    void setIeDnx(DnxDocument dnxDocument);

    void setFileGrpDnx(DnxDocument dnxDocument, String s);

    void setFileDnx(DnxDocument dnxDocument, String s);

    void generateChecksum(String s, String s1);

    void updateSize(String s);

    void generateStructMap(MetsType.FileSec.FileGrp fileGrp);

    void generateGID();

    void fixIdNaming();

    boolean validate(XmlOptions xmlOptions);

    boolean validate();

    String toXML();

    void export(String s) throws Exception;
}
