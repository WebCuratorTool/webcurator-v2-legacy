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

public class MetsDocument {
    private String xml;
    private String fileName;
    private String depositDirectoryName;
    private String depositSetId;

    public MetsDocument(String fileName, MetsWriter metsWriter) {
        this(fileName, metsWriter.toXML());
    }

    public MetsDocument(String fileName, String xml) {
        this.fileName = fileName;
        this.xml = xml;
    }

    public String toXML() {
        return xml;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDepositDirectoryName() {
        return depositDirectoryName;
    }

    public void setDepositDirectoryName(String depositDirectoryName) {
        this.depositDirectoryName = depositDirectoryName;
    }

    public String getDepositSetId() {
        return depositSetId;
    }

    public void setDepositSetId(String depositSetId) {
        this.depositSetId = depositSetId;
    }
}
