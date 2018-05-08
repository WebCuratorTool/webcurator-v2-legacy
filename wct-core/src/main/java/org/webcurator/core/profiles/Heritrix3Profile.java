package org.webcurator.core.profiles;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;

/**
 * The <code>Heritrix3Profile</code> class wraps the Heritrix3ProfileOptions object
 * to allow the WCT a degree of separation from the Heritrix implementation.
 *
 */
public class Heritrix3Profile {
    private Log log = LogFactory.getLog(Heritrix3Profile.class);
    private String profileXml;
    private Heritrix3ProfileOptions heritrix3ProfileOptions;
    private final static String BEAN_ID_PROPERTY_NAME_XPATH = "/beans/bean[@id=''{0}'']/property[@name=''{1}'']";
    private final static String SCOPE_RULES_BEAN_CLASS_PROPERTY_NAME_XPATH = "/beans/bean[@id=''scope'']/property[@name=''rules'']/list/bean[@class=''{0}'']/property[@name=''{1}'']";

    /**
     * Default constructor - read the default xml file.
     */
    public Heritrix3Profile() {
        // convert xml into profile options
        this.profileXml = defaultXML();
        this.heritrix3ProfileOptions = convertXmlToProfileOptions(this.profileXml);
    }

    public Heritrix3Profile(Heritrix3ProfileOptions heritrix3ProfileOptions) {
        this.heritrix3ProfileOptions = heritrix3ProfileOptions;
        // options need to be set first.
        this.profileXml = toProfileXml();
    }

    /**
     * Parses the profile xml into the profile options
     * @param profileXml
     */
    public Heritrix3Profile(String profileXml) {
        this.profileXml = profileXml;
        // Convert xml to profile options
        this.heritrix3ProfileOptions = convertXmlToProfileOptions(profileXml);
    }

    /**
     * Convert the profile options into XML.
     * @return the xml
     */
    public String toProfileXml() {
        String xml = "";
        try {
            // Load default xml into DOM
            Document xmlDocument = loadXmlDocument(this.profileXml);
            // Search for xml elements and modify
            modifyBeanIDPropertyNameAttributeValue("metadata", "operatorContactUrl", xmlDocument, heritrix3ProfileOptions.getContactURL());
            modifyBeanIDPropertyNameAttributeValue("crawlLimiter", "maxDocumentsDownload", xmlDocument, Long.toString(heritrix3ProfileOptions.getDocumentLimit()));
            modifyBeanIDPropertyNameAttributeValue("crawlLimiter", "maxBytesDownload", xmlDocument, Long.toString(heritrix3ProfileOptions.getDataLimit()));
            modifyBeanIDPropertyNameAttributeValue("crawlLimiter", "maxTimeSeconds", xmlDocument, Long.toString(heritrix3ProfileOptions.getTimeLimit()));
            modifyScopeRulesBeanClassPropertyNameAttributeValue("org.archive.modules.deciderules.TooManyPathSegmentsDecideRule", "maxPathDepth", xmlDocument, Long.toString(heritrix3ProfileOptions.getMaxPathDepth()));
            modifyScopeRulesBeanClassPropertyNameAttributeValue("org.archive.modules.deciderules.TooManyHopsDecideRule", "maxHops", xmlDocument, Long.toString(heritrix3ProfileOptions.getMaxHops()));
            modifyScopeRulesBeanClassPropertyNameAttributeValue("org.archive.modules.deciderules.TransclusionDecideRule", "maxTransHops", xmlDocument, Long.toString(heritrix3ProfileOptions.getMaxTransitiveHops()));
            modifyBeanIDPropertyNameAttributeValue("fetchHttp", "ignoreCookies", xmlDocument, Boolean.toString(heritrix3ProfileOptions.isIgnoreCookies()));
            modifyBeanIDPropertyNameAttributeValue("fetchHttp", "defaultEncoding", xmlDocument, heritrix3ProfileOptions.getDefaultEncoding());
            modifyBeanIDPropertyNameAttributeValue("warcWriter", "maxFileSizeBytes", xmlDocument, Long.toString(heritrix3ProfileOptions.getMaxFileSize()));
            modifyBeanIDPropertyNameAttributeValue("warcWriter", "compress", xmlDocument, Boolean.toString(heritrix3ProfileOptions.isCompress()));
            modifyBeanIDPropertyNameAttributeValue("warcWriter", "prefix", xmlDocument, heritrix3ProfileOptions.getPrefix());
            // Convert DOM to xml string
            xml = domToXml(xmlDocument);
        } catch (Exception e) {
            log.error("Exception converting profile options to XML", e);
        }
        // set instance variable xml for consistency
        this.profileXml = xml;
        return xml;
    }

    private String domToXml(Document xmlDocument) {
        String xml = "";
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource domSource = new DOMSource(xmlDocument);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(domSource, result);
            xml = writer.toString();
        } catch (Exception e) {
            log.error("Exception converting XML DOM Document to XML", e);
        }
        return xml;
    }

    /**
     * Get the default XML from the default H3 profile file.
     * @return
     */
    private String defaultXML() {
        String defaultXml = "";
        BufferedReader profileReader = null;
        try {
            String line = null;
            StringBuffer xmlBuffer = new StringBuffer();
            profileReader = new BufferedReader(new InputStreamReader(Heritrix3Profile.class.getResourceAsStream("/defaultH3Profile.cxml")));

            while ((line = profileReader.readLine()) != null) {
                xmlBuffer.append(line);
                xmlBuffer.append("\n");
            }
            defaultXml = xmlBuffer.toString();
        } catch(Exception ex) {
            log.error("Exception processing defaultH3Profile.cxml", ex);
        } finally {
            try {
                if (profileReader != null) {
                    profileReader.close();
                }
            } catch(Exception ex) {
                log.error("Exception closing defaultH3Profile.cxml", ex);
            }
        }
        return defaultXml;
    }

    private Heritrix3ProfileOptions convertXmlToProfileOptions(String xml) {
        Heritrix3ProfileOptions profileOptions = new Heritrix3ProfileOptions();
        try {
            Document xmlDocument = loadXmlDocument(xml);
            profileOptions.setContactURL(getBeanIDPropertyNameAttributeValue("metadata", "operatorContactUrl", xmlDocument));
            profileOptions.setDocumentLimit(Long.parseLong(getBeanIDPropertyNameAttributeValue("crawlLimiter", "maxDocumentsDownload", xmlDocument)));
            profileOptions.setDataLimit(Long.parseLong(getBeanIDPropertyNameAttributeValue("crawlLimiter", "maxBytesDownload", xmlDocument)));
            profileOptions.setTimeLimit(Long.parseLong(getBeanIDPropertyNameAttributeValue("crawlLimiter", "maxTimeSeconds", xmlDocument)));
            profileOptions.setMaxPathDepth(Long.parseLong(getScopeRulesBeanClassPropertyNameAttributeValue("org.archive.modules.deciderules.TooManyPathSegmentsDecideRule", "maxPathDepth", xmlDocument)));
            profileOptions.setMaxHops(Long.parseLong(getScopeRulesBeanClassPropertyNameAttributeValue("org.archive.modules.deciderules.TooManyHopsDecideRule", "maxHops", xmlDocument)));
            profileOptions.setMaxTransitiveHops(Long.parseLong(getScopeRulesBeanClassPropertyNameAttributeValue("org.archive.modules.deciderules.TransclusionDecideRule", "maxTransHops", xmlDocument)));
            profileOptions.setIgnoreCookies(Boolean.parseBoolean(getBeanIDPropertyNameAttributeValue("fetchHttp", "ignoreCookies", xmlDocument)));
            profileOptions.setDefaultEncoding(getBeanIDPropertyNameAttributeValue("fetchHttp", "defaultEncoding", xmlDocument));
            profileOptions.setMaxFileSize(Long.parseLong(getBeanIDPropertyNameAttributeValue("warcWriter", "maxFileSizeBytes", xmlDocument)));
            profileOptions.setCompress(Boolean.parseBoolean(getBeanIDPropertyNameAttributeValue("warcWriter", "compress", xmlDocument)));
            profileOptions.setPrefix(getBeanIDPropertyNameAttributeValue("warcWriter", "prefix", xmlDocument));
        } catch (Exception e) {
            log.error("Exception converting XML to profile options", e);
        }
        return profileOptions;
    }

    private void modifyBeanIDPropertyNameAttributeValue(String beanID, String propertyName, Document xmlDocument, String newValue) throws Exception {
        String path = MessageFormat.format(BEAN_ID_PROPERTY_NAME_XPATH, beanID, propertyName);
        modifyElement(path, xmlDocument, newValue);
    }

    private void modifyScopeRulesBeanClassPropertyNameAttributeValue(String beanClass, String propertyName, Document xmlDocument, String newValue) throws Exception {
        String path = MessageFormat.format(SCOPE_RULES_BEAN_CLASS_PROPERTY_NAME_XPATH, beanClass, propertyName);
        modifyElement(path, xmlDocument, newValue);
    }

    private void modifyElement(String path, Document xmlDocument, String newValue) throws Exception {
        Element elementToModify = xPathSearch(path, xmlDocument);
        NamedNodeMap attributes = elementToModify.getAttributes();
        Node valueAttribute = attributes.getNamedItem("value");
        valueAttribute.setTextContent(newValue);
    }

    private String getBeanIDPropertyNameAttributeValue(String beanID, String propertyName, Document xmlDocument) throws Exception {
        String path = MessageFormat.format(BEAN_ID_PROPERTY_NAME_XPATH, beanID, propertyName);
        return xPathSearch(path, xmlDocument).getAttribute("value");
    }

    private String getScopeRulesBeanClassPropertyNameAttributeValue(String beanClass, String propertyName, Document xmlDocument) throws Exception {
        String path = MessageFormat.format(SCOPE_RULES_BEAN_CLASS_PROPERTY_NAME_XPATH, beanClass, propertyName);
        return xPathSearch(path, xmlDocument).getAttribute("value");
    }

    private Element xPathSearch(String path, Document xmlDocument) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate(path, xmlDocument.getDocumentElement(), XPathConstants.NODESET);
        Element firstElement = (Element) nodes.item(0);
        return firstElement;
    }

    private Document loadXmlDocument(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    public Heritrix3ProfileOptions getHeritrix3ProfileOptions() {
        return heritrix3ProfileOptions;
    }
}
