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

import java.util.List;


public interface WctRequiredData {
    public class SeedUrl {
        protected String url;
        protected Type type;
        public enum Type {Primary, Secondary};
        public String getUrl() { return url;}
        public Type getType() {return type;}
        public SeedUrl(String url, Type type) {
            this.url = url;
            this.type = type;
        }
        public String toString() {return "SeedUrl(url = " + url + ", type = " + type + ")";}
        public boolean equals(Object obj) {
            if (obj instanceof SeedUrl) {
                SeedUrl anObj = (SeedUrl) obj;
                return url.equals(anObj.url) && type.equals(anObj.type);
            }
            return false;
        }
    }

    String getHarvestDate();

    List<SeedUrl> getSeedUrls();

    /**
     * Returns OMS-style access restrictions: ACR_ONS, ACR_OPA, ACR_OSR, ACR_RES
     * @return
     */
    String getAccessRestriction();

    String getILSReference();

    String getCreatedBy();

    String getCopyrightURL();

    String getCopyrightStatement();
}
