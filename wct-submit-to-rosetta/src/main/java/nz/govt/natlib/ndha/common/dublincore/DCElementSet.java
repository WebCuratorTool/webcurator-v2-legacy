/**
 * nz.govt.natlib.ndha.common.Common - Software License
 *
 * Copyright 2007/2008 National Library of New Zealand.
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

package nz.govt.natlib.ndha.common.dublincore;

import org.dom4j.Namespace;

/**
 * Provides the names of each of the 15 Dublin Core elements.
 * User: MolesbeN
 * Date: 4/12/2007
 * Time: 10:40:53
 */
public enum DCElementSet {
    Title("title"),
    Creator("creator"),
    Subject("subject"),
    Description("description"),
    Publisher("publisher"),
    Contributor("contributor"),
    Date("date"),
    Type("type"),
    Format("format"),
    Identifier("identifier"),
    Source("source"),
    Language("language"),
    Relation("relation"),
    Coverage("coverage"),
    Rights("rights");

    private static final Namespace namespace = new Namespace("dc", "http://purl.org/dc/elements/1.1/");

    private String name;

    DCElementSet(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getNameSpace() + ":" + getName();
    }

    public Namespace getNameSpace() {
        return namespace;
    }

    public String getName() {
        return name;
    }
}
