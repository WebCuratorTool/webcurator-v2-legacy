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

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

/**
 * Required by the class @{link XPathWctMetsExtractor} to define the XML Namespaces used
 * in Wct Met's documents. 
 */
public class WctNamespaceContext implements NamespaceContext {

    public String getNamespaceURI(String prefix) {
        if (prefix == null) throw new NullPointerException("Null prefix");
        else if ("mets".equals(prefix)) return "http://www.loc.gov/METS/";
        else if ("dc".equals(prefix)) return "http://purl.org/dc/elements/1.1/";
        else if ("wct".equals(prefix)) return "http://webcurator.sourceforge.net/schemata/webcuratortool-1.0.dtd";
        else if ("xlink".equals(prefix)) return "http://www.w3.org/1999/xlink";
        else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
        return XMLConstants.NULL_NS_URI;
    }

    // This method isn't necessary for XPath processing.
    public String getPrefix(String uri) {
        throw new UnsupportedOperationException();
    }

    // This method isn't necessary for XPath processing either.
    @SuppressWarnings("unchecked")
	public Iterator getPrefixes(String uri) {
        throw new UnsupportedOperationException();
    }

}