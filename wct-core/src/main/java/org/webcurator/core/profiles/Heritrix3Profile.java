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
import java.io.*;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The <code>Heritrix3Profile</code> class wraps the Heritrix3ProfileOptions object
 * to allow the WCT a degree of separation from the Heritrix implementation.
 *
 */
public class Heritrix3Profile {
    private Log log = LogFactory.getLog(Heritrix3Profile.class);
    private String profileXml;
    private Heritrix3ProfileOptions heritrix3ProfileOptions;
    private final static String SIMPLE_OVERRIDES_PROPERTIES_TEXT_XPATH = "/beans/bean[@id='simpleOverrides']/property[@name='properties']/value/text()";
    private final static String SIMPLE_OVERRIDES_PROPERTIES_XPATH = "/beans/bean[@id='simpleOverrides']/property[@name='properties']/value";
    private final static String BEAN_ID_PROPERTY_NAME_XPATH = "/beans/bean[@id=''{0}'']/property[@name=''{1}'']";
    private final static String SCOPE_RULES_BEAN_CLASS_PROPERTY_NAME_XPATH = "/beans/bean[@id=''scope'']/property[@name=''rules'']/list/bean[@class=''{0}'']/property[@name=''{1}'']";
    private final static String MATCHES_LIST_REGEX_DECIDE_RULE_XPATH = "/beans/bean[@id=''scope'']/property[@name=''rules'']/list/bean[@class=''org.archive.modules.deciderules.MatchesListRegexDecideRule'']/property[@name=''decision'' and @value=''{0}'']";

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
            updateContactURL(xmlDocument, heritrix3ProfileOptions.getContactURL());
            modifyBeanIDPropertyNameAttributeValue("metadata", "userAgentTemplate", xmlDocument, heritrix3ProfileOptions.getUserAgentTemplate());
            modifyBeanIDPropertyNameAttributeValue("crawlLimiter", "maxDocumentsDownload", xmlDocument, Long.toString(heritrix3ProfileOptions.getDocumentLimit()));
            modifyBeanIDPropertyNameAttributeValue("crawlLimiter", "maxBytesDownload", xmlDocument, heritrix3ProfileOptions.getDataLimitAsBytes().toString());
            modifyBeanIDPropertyNameAttributeValue("crawlLimiter", "maxTimeSeconds", xmlDocument, heritrix3ProfileOptions.getTimeLimitAsSeconds().toString());
            modifyScopeRulesBeanClassPropertyNameAttributeValue("org.archive.modules.deciderules.TooManyPathSegmentsDecideRule", "maxPathDepth", xmlDocument, Long.toString(heritrix3ProfileOptions.getMaxPathDepth()));
            modifyScopeRulesBeanClassPropertyNameAttributeValue("org.archive.modules.deciderules.TooManyHopsDecideRule", "maxHops", xmlDocument, Long.toString(heritrix3ProfileOptions.getMaxHops()));
            modifyScopeRulesBeanClassPropertyNameAttributeValue("org.archive.modules.deciderules.TransclusionDecideRule", "maxTransHops", xmlDocument, Long.toString(heritrix3ProfileOptions.getMaxTransitiveHops()));
            // Map ignore robots
            String robotsPolicyNameValue = heritrix3ProfileOptions.isIgnoreRobotsTxt() ? "ignore" : "obey";
            modifyBeanIDPropertyNameAttributeValue("metadata", "robotsPolicyName", xmlDocument, robotsPolicyNameValue);
            modifyBeanIDPropertyNameAttributeValue("fetchHttp", "ignoreCookies", xmlDocument, Boolean.toString(heritrix3ProfileOptions.isIgnoreCookies()));
            modifyBeanIDPropertyNameAttributeValue("fetchHttp", "defaultEncoding", xmlDocument, heritrix3ProfileOptions.getDefaultEncoding());
            modifyMatchesDecideRulePropertyNameList("REJECT", xmlDocument, heritrix3ProfileOptions.getBlockURLsAsList());
            modifyMatchesDecideRulePropertyNameList("ACCEPT", xmlDocument, heritrix3ProfileOptions.getIncludeURLsAsList());
            modifyBeanIDPropertyNameAttributeValue("warcWriter", "maxFileSizeBytes", xmlDocument, heritrix3ProfileOptions.getMaxFileSizeAsBytes().toString());
            modifyBeanIDPropertyNameAttributeValue("warcWriter", "compress", xmlDocument, Boolean.toString(heritrix3ProfileOptions.isCompress()));
            modifyBeanIDPropertyNameAttributeValue("warcWriter", "prefix", xmlDocument, heritrix3ProfileOptions.getPrefix());
            // Map politeness
            PolitenessOptions politenessOptions = heritrix3ProfileOptions.getPolitenessOptions();
            modifyBeanIDPropertyNameAttributeValue("disposition", "delayFactor", xmlDocument, Double.toString(politenessOptions.getDelayFactor()));
            modifyBeanIDPropertyNameAttributeValue("disposition", "minDelayMs", xmlDocument, Long.toString(politenessOptions.getMinDelayMs()));
            modifyBeanIDPropertyNameAttributeValue("disposition", "maxDelayMs", xmlDocument, Long.toString(politenessOptions.getMaxDelayMs()));
            modifyBeanIDPropertyNameAttributeValue("disposition", "respectCrawlDelayUpToSeconds", xmlDocument, Long.toString(politenessOptions.getRespectCrawlDelayUpToSeconds()));
            modifyBeanIDPropertyNameAttributeValue("disposition", "maxPerHostBandwidthUsageKbSec", xmlDocument, Long.toString(politenessOptions.getMaxPerHostBandwidthUsageKbSec()));
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
            profileOptions.setContactURL(findContactURL(xmlDocument));
            profileOptions.setUserAgentTemplate(getBeanIDPropertyNameAttributeValue("metadata", "userAgentTemplate", xmlDocument));
            profileOptions.setDocumentLimit(Long.parseLong(getBeanIDPropertyNameAttributeValue("crawlLimiter", "maxDocumentsDownload", xmlDocument)));
            profileOptions.setDataLimitAsBytes(new BigInteger(getBeanIDPropertyNameAttributeValue("crawlLimiter", "maxBytesDownload", xmlDocument)));
            profileOptions.setTimeLimitAsSeconds(new BigInteger(getBeanIDPropertyNameAttributeValue("crawlLimiter", "maxTimeSeconds", xmlDocument)));
            profileOptions.setMaxPathDepth(Long.parseLong(getScopeRulesBeanClassPropertyNameAttributeValue("org.archive.modules.deciderules.TooManyPathSegmentsDecideRule", "maxPathDepth", xmlDocument)));
            profileOptions.setMaxHops(Long.parseLong(getScopeRulesBeanClassPropertyNameAttributeValue("org.archive.modules.deciderules.TooManyHopsDecideRule", "maxHops", xmlDocument)));
            profileOptions.setMaxTransitiveHops(Long.parseLong(getScopeRulesBeanClassPropertyNameAttributeValue("org.archive.modules.deciderules.TransclusionDecideRule", "maxTransHops", xmlDocument)));
            // Map ignore robots
            String robotsPolicyName = getBeanIDPropertyNameAttributeValue("metadata", "robotsPolicyName", xmlDocument);
            if (robotsPolicyName.equals("ignore")) {
                profileOptions.setIgnoreRobotsTxt(true);
            } else if (robotsPolicyName.equals("obey")) {
                profileOptions.setIgnoreRobotsTxt(false);
            }
            profileOptions.setIgnoreCookies(Boolean.parseBoolean(getBeanIDPropertyNameAttributeValue("fetchHttp", "ignoreCookies", xmlDocument)));
            profileOptions.setDefaultEncoding(getBeanIDPropertyNameAttributeValue("fetchHttp", "defaultEncoding", xmlDocument));
            profileOptions.setBlockURLsAsList(getMatchesDecideRulePropertyNameList("REJECT", xmlDocument));
            profileOptions.setIncludeURLsAsList(getMatchesDecideRulePropertyNameList("ACCEPT", xmlDocument));
            profileOptions.setMaxFileSizeAsBytes(new BigInteger(getBeanIDPropertyNameAttributeValue("warcWriter", "maxFileSizeBytes", xmlDocument)));
            profileOptions.setCompress(Boolean.parseBoolean(getBeanIDPropertyNameAttributeValue("warcWriter", "compress", xmlDocument)));
            profileOptions.setPrefix(getBeanIDPropertyNameAttributeValue("warcWriter", "prefix", xmlDocument));
            // map xml politeness values
            double delayFactor = Double.parseDouble(getBeanIDPropertyNameAttributeValue("disposition", "delayFactor", xmlDocument));
            long minDelayMs = Long.parseLong(getBeanIDPropertyNameAttributeValue("disposition", "minDelayMs", xmlDocument));
            long maxDelayMs = Long.parseLong(getBeanIDPropertyNameAttributeValue("disposition", "maxDelayMs", xmlDocument));
            long respectCrawlDelayUpToSeconds = Long.parseLong(getBeanIDPropertyNameAttributeValue("disposition", "respectCrawlDelayUpToSeconds", xmlDocument));
            long maxPerHostBandwidthUsageKbSec = Long.parseLong(getBeanIDPropertyNameAttributeValue("disposition", "maxPerHostBandwidthUsageKbSec", xmlDocument));
            PolitenessOptions politenessOptions = new PolitenessOptions(delayFactor, minDelayMs, maxDelayMs, respectCrawlDelayUpToSeconds, maxPerHostBandwidthUsageKbSec);
            profileOptions.setPolitenessOptions(politenessOptions);
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

    private void modifyMatchesDecideRulePropertyNameList(String decisionValue, Document xmlDocument, List<String> newUrls) throws Exception {
        String path = MessageFormat.format(MATCHES_LIST_REGEX_DECIDE_RULE_XPATH, decisionValue);
        Element matchesDecideRulePropertyNameListElement = xPathSearch(path, xmlDocument.getDocumentElement());
        Element parent = (Element) matchesDecideRulePropertyNameListElement.getParentNode();
        // Get the list element
        Element listElement = xPathSearch("property[@name='regexList']/list", parent);
        // Delete all the value nodes
        while (listElement.hasChildNodes()) {
            listElement.removeChild(listElement.getFirstChild());
        }
        // create new value nodes
        for (String url : newUrls) {
            Element urlElement = xmlDocument.createElement("value");
            urlElement.appendChild(xmlDocument.createTextNode(url));
            listElement.appendChild(urlElement);
        }
    }

    private void modifyElement(String path, Document xmlDocument, String newValue) throws Exception {
        Element elementToModify = xPathSearch(path, xmlDocument.getDocumentElement());
        NamedNodeMap attributes = elementToModify.getAttributes();
        Node valueAttribute = attributes.getNamedItem("value");
        valueAttribute.setTextContent(newValue);
    }

    private Properties getSimpleOverridesProperties(Document xmlDocument) throws Exception {
        String propertiesText = xPathTextSearch(SIMPLE_OVERRIDES_PROPERTIES_TEXT_XPATH, xmlDocument.getDocumentElement());
        Properties properties = new Properties();
        Reader reader = new StringReader(propertiesText);
        properties.load(reader);
        return properties;
    }

    private String findContactURL(Document xmlDocument) throws Exception {
        // find the metadata.operatorContactUrl value
        Properties properties = getSimpleOverridesProperties(xmlDocument);
        return properties.getProperty("metadata.operatorContactUrl");
    }

    private void updateContactURL(Document xmlDocument, String newContactURL) throws Exception {
        Properties properties = getSimpleOverridesProperties(xmlDocument);
        // update property
        properties.setProperty("metadata.operatorContactUrl", newContactURL);
        // list
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        properties.store(writer, "");
        String updatedPropertiesText = out.toString();
        System.out.println(updatedPropertiesText);
        // update XML
        xPathSearch(SIMPLE_OVERRIDES_PROPERTIES_XPATH, xmlDocument.getDocumentElement()).setTextContent(updatedPropertiesText);
    }

    private String getBeanIDPropertyNameAttributeValue(String beanID, String propertyName, Document xmlDocument) throws Exception {
        String path = MessageFormat.format(BEAN_ID_PROPERTY_NAME_XPATH, beanID, propertyName);
        return xPathSearch(path, xmlDocument.getDocumentElement()).getAttribute("value");
    }

    private String getScopeRulesBeanClassPropertyNameAttributeValue(String beanClass, String propertyName, Document xmlDocument) throws Exception {
        String path = MessageFormat.format(SCOPE_RULES_BEAN_CLASS_PROPERTY_NAME_XPATH, beanClass, propertyName);
        return xPathSearch(path, xmlDocument.getDocumentElement()).getAttribute("value");
    }

    private List<String> getMatchesDecideRulePropertyNameList(String decisionValue, Document xmlDocument) throws Exception {
        String path = MessageFormat.format(MATCHES_LIST_REGEX_DECIDE_RULE_XPATH, decisionValue);
        Element matchesDecideRulePropertyNameListElement = xPathSearch(path, xmlDocument.getDocumentElement());
        Element parent = (Element) matchesDecideRulePropertyNameListElement.getParentNode();
        List<String> propertyNameList = xPathSearchPropertyList("property[@name='regexList']/list/value", parent);
        return propertyNameList;
    }

    private Element xPathSearch(String path, Element element) throws Exception {
        NodeList nodes = xPathSearchNodeList(path, element);
        Element firstElement = (Element) nodes.item(0);
        return firstElement;
    }

    private NodeList xPathSearchNodeList(String path, Element element) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate(path, element, XPathConstants.NODESET);
        return nodes;
    }

    private List<String> xPathSearchPropertyList(String path, Element element) throws Exception {
        List<String> propertyList = new ArrayList<String>();
        NodeList nodes = xPathSearchNodeList(path, element);
        // Convert nodes to a List<String>
        for (int i = 0; i < nodes.getLength(); i++) {
            Node listItem = nodes.item(i);
            propertyList.add(listItem.getTextContent());

        }
        return propertyList;
    }

    private String xPathTextSearch(String path, Element element) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate(path, element, XPathConstants.NODESET);
        return nodes.item(0).getNodeValue();
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

    public void setHeritrix3ProfileOptions(Heritrix3ProfileOptions heritrix3ProfileOptions) {
        this.heritrix3ProfileOptions = heritrix3ProfileOptions;
    }

    public String getProfileXml() {
        return profileXml;
    }

    public void setProfileXml(String profileXml) {
        this.profileXml = profileXml;
    }

}
