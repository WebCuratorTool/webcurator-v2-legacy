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

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author GuyP
 * @author Nicolai Moles-Benfell - added a helper 'addElement' method.
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class DublinCore {

    private static class DCElement {
        public int namespace;
        public String xsiType;
        public String key;
        public String val;

        public String toString() {
            return "namespace=" + namespace + ";key=" + key + ";xsiType=" + xsiType + ";val=" + val;
        }
    }


    public static final int DC_NAMESPACE = 10;
    public static final int DCTERMS_NAMESPACE = 20;
    public static final String DC_PREFIX = "dc";
    public static final String DCTERMS_PERFIX = "dcterms";
    public static final String XSI_TYPE = "xsi:type";

    private Document m_document = null;
    private Element m_record = null;
    Namespace dcNamespace = new Namespace("dc", "http://purl.org/dc/elements/1.1/");
    Namespace dctermsNamespace = new Namespace("dcterms", "http://purl.org/dc/terms/");
    Namespace xsiNamespace = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    QName XsiQName = new QName("type", xsiNamespace);

    /**
     *
     */
    protected DublinCore() {
        super();
        createNew();
    }

    protected DublinCore(String xml) throws DocumentException {
        super();
        createFromXml(xml);
    }

    public DublinCore copyDC() throws Exception {
        return new DublinCore(toXml());
    }

    private void createFromXml(String xml) throws DocumentException {
        //sm_document = DocumentFactory.getInstance().createDocument();
        SAXReader reader = new SAXReader();
        m_document = reader.read(new StringReader(xml));
        m_record = m_document.getRootElement();
    }

    private void createNew() {

        m_document = DocumentFactory.getInstance().createDocument();

        m_record = m_document.addElement("record");
        m_record.add(dcNamespace);
        m_record.add(dctermsNamespace);
        m_record.add(xsiNamespace);
    }

    public void addElement(int namespace, String key, String value) {
        addElement(namespace, key, null, value);
    }

    /**
     * Add a DC element using the enum <code>DCElementSet</code>
     * <p/>
     * Added by @author Nicolai Moles-Benfell
     *
     * @param element
     * @param value
     */
    public void addElement(DCElementSet element, String value) {
        QName qname = new QName(element.getName(), element.getNameSpace());
        Element elem = m_record.addElement(qname);
        elem.setText(value);
    }

    public void addElement(int namespace, String key, String xsiType, String value) {

        QName qname = null;

        if (namespace == DC_NAMESPACE) {
            qname = new QName(key, dcNamespace);
        } else if (namespace == DCTERMS_NAMESPACE) {
            qname = new QName(key, dctermsNamespace);
        } else {
            return;
        }

        Element elem = m_record.addElement(qname);
        elem.setText(value);

        if (xsiType != null) {
            elem.addAttribute(XsiQName, xsiType);
        }
    }

    /**
     * @param xPathKey (eg.dc:title@DCMIType:LCSH)
     * @param value
     */
    public void addElement(String xPathKey, String value) {
        DCElement dcElem = parseKey(xPathKey);
        if (dcElem != null) {
            addElement(dcElem.namespace, dcElem.key, dcElem.xsiType, value);
        }
    }

    public String getDcValue(String key) {
        return getValue(DC_NAMESPACE, key, null);
    }

    public String getDctermsValue(String key) {
        return getValue(DCTERMS_NAMESPACE, key, null);
    }

    public String getValue(int namespace, String key) {
        return getValue(namespace, key, null);
    }

    /**
     * @param xPathKey (eg. dc:title@DCMIType:LCSH)
     * @return
     */
    public String getValue(String xPathKey) {
        DCElement dcElem = parseKey(xPathKey);
        if (dcElem == null) {
            return null;

        }


        return getValue(dcElem.namespace, dcElem.key, dcElem.xsiType);
    }

    @SuppressWarnings("unchecked")
    public List getValues(String xPathKey) {
        DCElement dcElem = parseKey(xPathKey);
        if (dcElem == null) {
            return null;
        }
        return getValues(dcElem.namespace, dcElem.key, dcElem.xsiType);
    }


    /**
     * @param namespace (DC_NAMESPACE | DCTERMS_NAMESPACE)
     * @param key       (eg. alternative)
     * @param xsiType   (eg. DCMIType:LCSH)
     * @return
     */
    @SuppressWarnings("unchecked")
    public String getValue(int namespace, String key, String xsiType) {
        StringBuffer xpath = new StringBuffer("//");
        if (namespace == DC_NAMESPACE) {
            xpath.append(DC_PREFIX);
        } else if (namespace == DCTERMS_NAMESPACE) {
            xpath.append(DCTERMS_PERFIX);
        } else {
            return null;
        }

        if ((key == null) || (key.length() == 0)) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(key, "/");
        String index = null;
        if (st.hasMoreElements())
            key = (String) st.nextElement();
        if (st.hasMoreElements())
            index = (String) st.nextElement();


        xpath.append(":").append(key);

        if ((xsiType == null) || (xsiType.length() == 0)) {
            xpath.append("[not(@*)]");
        } else {
            xpath.append("[@xsi:type='").append(xsiType).append("']");
        }
        if ((index == null) || (index.length() == 0)) {
            return getXPathValue(xpath.toString());
        } else {
            List list = getXPathValues(xpath.toString());
            int i = Integer.parseInt(index) - 1;
            return (String) list.get(i);
        }

    }

    @SuppressWarnings("unchecked")
    public List getValues(int namespace, String key, String xsiType) {
        StringBuffer xpath = new StringBuffer("//");
        if (namespace == DC_NAMESPACE) {
            xpath.append(DC_PREFIX);
        } else if (namespace == DCTERMS_NAMESPACE) {
            xpath.append(DCTERMS_PERFIX);
        } else {
            return null;
        }

        if ((key == null) || (key.length() == 0)) {
            return null;
        }

        xpath.append(":").append(key);

        if ((xsiType == null) || (xsiType.length() == 0)) {
            xpath.append("[not(@*)]");
        } else {
            xpath.append("[@xsi:type='").append(xsiType).append("']");
        }

        return getXPathValues(xpath.toString());

    }


    public void save(String fileName) throws IOException {
        FileWriter out = new FileWriter(fileName);
        m_document.write(out);
        out.flush();
        out.close();
    }

    public String toXml() throws IOException {
        StringWriter sw = new StringWriter();
        m_document.write(sw);
        sw.flush();
        sw.close();
        return sw.getBuffer().toString();
    }

    public Document getDocument() {
        return m_document;
    }


    private String getXPathValue(String xPath) {
        Node node = m_document.selectSingleNode(xPath);
        if (node != null) {
            return node.getText();
        } else {

            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List getXPathValues(String xPath) {
        List nodes = m_document.selectNodes(xPath);

        if (nodes != null) {
            List l = new ArrayList();
            Iterator i = nodes.iterator();

            while (i.hasNext()) {
                l.add(((Node) i.next()).getText());

            }
            return l;
        } else {
            return null;
        }
    }


    private String getXPath(int namespace, String key, String xsiType) {
        StringBuffer xpath = new StringBuffer("//");
        if (namespace == DC_NAMESPACE) {
            xpath.append(DC_PREFIX);
        } else if (namespace == DCTERMS_NAMESPACE) {
            xpath.append(DCTERMS_PERFIX);
        } else {
            return null;
        }

        if ((key == null) || (key.length() == 0)) {
            return null;
        }

        xpath.append(":").append(key);

        if ((xsiType == null) || (xsiType.length() == 0)) {
            xpath.append("[not(@*)]");
        } else {
            xpath.append("[@xsi:type='").append(xsiType).append("']");
        }
        return xpath.toString();
    }

    public void removeElemet(int namespace, String key) {
        removeElemet(namespace, key, null);
    }

    /**
     * @param xPathKey (eg. dc:title@DCMIType:LCSH)
     * @return
     */
    public void removeElemet(String xPathKey) {
        DCElement dcElem = parseKey(xPathKey);
        if (dcElem == null) {
            return;
        }
        removeElemet(dcElem.namespace, dcElem.key, dcElem.xsiType);
    }

    public void removeElemet(int namespace, String key, String xsiType) {
        String path = getXPath(namespace, key, xsiType);
        removeNodeByXPath(path);
    }

    private void removeNodeByXPath(String xPath) {
        Node node = m_document.selectSingleNode(xPath);
        if (node != null) {
            node.detach();
        }
    }

    private DCElement parseKey(String key) {

        if ((key == null) || (key.length() == 0)) {
            return null;
        }

        DCElement dcElem = new DCElement();
        if (key.startsWith("dcterms:")) {
            dcElem.namespace = DublinCore.DCTERMS_NAMESPACE;
            key = key.substring(8);
        } else {
            if (key.startsWith("dc:")) {
                dcElem.namespace = DublinCore.DC_NAMESPACE;
                key = key.substring(3);

            } else {
                return null;
            }
        }

        int pos = key.indexOf("@");
        if (pos == -1) {
            if (key.length() > 0) {
                dcElem.key = key;
            }


        } else {

            dcElem.key = key.substring(0, pos);
            dcElem.xsiType = key.substring(pos + 1);


        }

        return dcElem;
    }

}
