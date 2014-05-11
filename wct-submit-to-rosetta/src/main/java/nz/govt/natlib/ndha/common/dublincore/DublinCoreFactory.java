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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author GuyP
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class DublinCoreFactory {
    private static DublinCoreFactory m_instance = new DublinCoreFactory();


    /**
     *
     */
    private DublinCoreFactory() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static DublinCoreFactory getInstance() {
        return m_instance;
    }

    @SuppressWarnings("unchecked")
    public DublinCore createDocument(Map map) {
        DublinCore dc = new DublinCore();
        if (map == null) {
            return dc;
        }

        Iterator itr = map.keySet().iterator();
        String key;
        String val;

        while (itr.hasNext()) {
            key = itr.next().toString();
            val = ((String) map.get(key));
            dc.addElement(key, val);
        }

        return dc;
    }

    public DublinCore createDocument() {
        return new DublinCore();
    }

    public DublinCore createDocument(String xml) throws DocumentException {
        return new DublinCore(xml);
    }

    @SuppressWarnings("unchecked")
    public DublinCore normalizeRoot(String xml) throws DocumentException {
        DublinCore dc = new DublinCore();
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(xml));
        Element record = document.getRootElement();
        Iterator eitr = record.elementIterator();
        Element dcDoc = dc.getDocument().getRootElement();
        while (eitr.hasNext()) {
            Element element = (Element) eitr.next();
            dcDoc.add(element.createCopy());
        }
        return dc;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        HashMap hash = new HashMap();
        String s[] = new String[1];
        s[0] = "I am alt";
        hash.put("dcterms:alternative@DCMIType:hello", s);
        s = new String[1];
        s[0] = "I am subject";
        hash.put("dc:subject", s);

        s = new String[1];
        s[0] = "I am kuku";
        hash.put("dc:title@DCMIType:kuku", s);

        //Create Document
        DublinCore dc = DublinCoreFactory.getInstance().createDocument(hash);
        // adding elements
        dc.addElement(DublinCore.DC_NAMESPACE, "title", "hello title");
        dc.addElement(DublinCore.DCTERMS_NAMESPACE, "alternative", "hello alternative");
        // <dc:identifier xsi:type="dcterms:ISBN">hello identifier</dc:identifier>
        dc.addElement(DublinCore.DC_NAMESPACE, "identifier", "dcterms:ISBN", "hello identifier");

        // getting elements
        dc.getDcValue("title");
        dc.getDctermsValue("alternative");
        dc.getValue(DublinCore.DC_NAMESPACE, "identifier", "dcterms:ISBN");

        //getting the xml
        //dc.toXml();


        try {
            System.out.println(dc.toXml());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
