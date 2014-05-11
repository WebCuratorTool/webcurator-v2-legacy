package org.webcurator.bl;

import javax.xml.namespace.QName;
import javax.xml.xpath.*;
import org.w3c.dom.*;

	public class XPathReader {
	    
	    private Element xmlRootElement;
	    private Document xmlDocument;
	    private XPath xPath;
	    
	    public XPathReader(Element xmlRootElement) {
	        this.xmlRootElement = xmlRootElement;
	        initObjects();
	    }
	    
	    private void initObjects(){        
	        try {
	        	xmlDocument = xmlRootElement.getOwnerDocument();
	            xPath =  XPathFactory.newInstance().
				newXPath();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        } 
	    }
	    
	    public Object read(String expression, 
				QName returnType){
	        try {
	            XPathExpression xPathExpression = 
				xPath.compile(expression);
		        return xPathExpression.evaluate
				(xmlDocument, returnType);
	        } catch (XPathExpressionException ex) {
	            ex.printStackTrace();
	            return null;
	        }
	    }
	}
