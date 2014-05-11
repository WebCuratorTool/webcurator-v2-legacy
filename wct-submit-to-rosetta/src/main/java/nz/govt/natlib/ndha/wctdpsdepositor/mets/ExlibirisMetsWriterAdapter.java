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
import com.exlibris.dps.sdk.deposit.IEParser;
import com.exlibris.core.sdk.parser.IEParserException;
import gov.loc.mets.*;
import org.apache.xmlbeans.XmlOptions;

/**
 * At time of writing this class, the DPS MET's writer (@{link IEParser} part of the SDK)
 * throws checked exceptions on each method call, we have been told this will change. To hide
 * this exception behaviour and the expected changes to the SDK we've  encapsulated access
 * to @{link IEParser} with this adapter.
 */
public class ExlibirisMetsWriterAdapter implements MetsWriter {
    private final IEParser ieParser;


    public ExlibirisMetsWriterAdapter() {
        try {
            ieParser = com.exlibris.dps.sdk.deposit.IEParserFactory.create();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DublinCore getDublinCoreParser() {
        try {
            return ieParser.getDublinCoreParser();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DnxDocument getDnxParser() {
        try {
            return ieParser.getDnxParser();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MetsType.FileSec.FileGrp addNewFileGrp(Enum.UsageType usageType, Enum.PreservationType preservationType, String s) {
        try {
            return ieParser.addNewFileGrp(usageType, preservationType);
        } catch (IEParserException e) {
            throw new RuntimeException(e);
        }
    }

    public MetsType.FileSec.FileGrp addNewFileGrp(Enum.UsageType usageType, Enum.PreservationType preservationType) {
        try {
            return ieParser.addNewFileGrp(usageType, preservationType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public FileType addNewFile(MetsType.FileSec.FileGrp fileGrp, String s, String s1, String s2) {
        try {
            return ieParser.addNewFile(fileGrp, s, s1, s2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public StructMapType addNewStructMap(String s) {
        try {
            return ieParser.addNewStructMap(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DublinCore getIeDublinCore() {
        try {
            return ieParser.getIeDublinCore();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MetsType.FileSec getFileSec() {
        try {
            return ieParser.getFileSec();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MetsType.FileSec.FileGrp getFileGrp(String s) {
        try {
            return ieParser.getFileGrp(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MetsType.FileSec.FileGrp[] getFileGrpArray() {
        try {
            return ieParser.getFileGrpArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public FileType getFile(String s) {
        try {
            return ieParser.getFile(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public FileType[] getFileArray(String s) {
        try {
            return ieParser.getFileArray(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DnxDocument getIeDnx() {
        try {
            return ieParser.getIeDnx();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DnxDocument getFileGrpDnx(String s) {
        try {
            return ieParser.getFileGrpDnx(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DnxDocument getFileDnx(String s) {
        try {
            return ieParser.getFileDnx(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public StructMapType[] getStructMapArray() {
        try {
            return ieParser.getStructMapArray();
        } catch (IEParserException e) {
            throw new RuntimeException(e);
        }
    }

    public MdSecType[] getDmdSecArray() {
        try {
            return ieParser.getDmdSecArray();
        } catch (IEParserException e) {
            throw new RuntimeException(e);
        }
    }

    public AmdSecType[] getAmdSecArray() {
        try {
            return ieParser.getAmdSecArray();
        } catch (IEParserException e) {
            throw new RuntimeException(e);
        }
    }

    public void setIEDublinCore(DublinCore dublinCore) {
        try {
            ieParser.setIEDublinCore(dublinCore);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setIeDnx(DnxDocument dnxDocument) {
        try {
            ieParser.setIeDnx(dnxDocument);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setFileGrpDnx(DnxDocument dnxDocument, String s) {
        try {
            ieParser.setFileGrpDnx(dnxDocument, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setFileDnx(DnxDocument dnxDocument, String s) {
        try {
            ieParser.setFileDnx(dnxDocument, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void generateChecksum(String s, String s1) {
        try {
            ieParser.generateChecksum(s, s1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSize(String s) {
        try {
            ieParser.updateSize(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void generateStructMap(MetsType.FileSec.FileGrp fileGrps) {
        try {
            ieParser.generateStructMap(fileGrps, "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void generateGID() {
        try {
            ieParser.generateGID();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void fixIdNaming() {
        try {
            ieParser.fixIdNaming();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validate(XmlOptions xmlOptions) {
        try {
            return ieParser.validate(xmlOptions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validate() {
        try {
            return ieParser.validate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toXML() {
        try {
            return ieParser.toXML();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void export(String s) throws Exception {
        ieParser.export(s);
    }


}
