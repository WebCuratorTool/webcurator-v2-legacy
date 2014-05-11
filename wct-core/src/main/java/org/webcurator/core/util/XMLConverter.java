package org.webcurator.core.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLConverter {

	private static DocumentBuilder getBuilder() throws ParserConfigurationException
	{
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        return dbfac.newDocumentBuilder();
	}
	
	public static Document newDocument() throws ParserConfigurationException
	{
        return getBuilder().newDocument();
	}
	
	public static Document StringToDocument(String xmlString) throws SAXException, IOException, ParserConfigurationException
	{
		StringReader reader = null;
		try
		{
	        reader = new StringReader(xmlString);
	        InputSource inputSource = new InputSource(reader);
	
	        return getBuilder().parse(inputSource);
		} 
		finally
		{
			if(reader != null)
			{
				reader.close();
			}
		}
	}
	
	public static String DocumentToString(Document xmlDoc) throws TransformerException
	{
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(xmlDoc);
        trans.transform(source, result);
        return sw.toString();
	}
}
