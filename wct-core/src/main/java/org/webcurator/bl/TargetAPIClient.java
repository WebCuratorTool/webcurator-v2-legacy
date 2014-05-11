/**
 * FILE: TargetAPIClient.java
 *
 * DATE: 18/08/2010
 *
 * <p>This class implements a simple client for the TargetAPI web service
 * allowing the passing of parameters within an XML document embedded 
 * in the body of a SOAP message.
 * 
 * <p>It is designed for deployment with Apache Axis 1.4.
 *
 */

package org.webcurator.bl;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Service;
import org.apache.axis.client.Call;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.utils.XMLUtils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Vector;

public class TargetAPIClient {

   /**
   * Invoke the service by passing across an XML document named on the
   * command line.
   */
  public static void main(String [] args) throws Exception {

    try {
      // Where is the service located?
      String endpointURL = 
        "http://localhost:8080/wct/services/urn:TargetAPI";

      // Set up the infrastructure to contact the service
      Service service = new Service();
      Call call = (Call) service.createCall();
      call.setTargetEndpointAddress(new URL(endpointURL));

      // Set up the input message for the service
      // First, create an empty message body
      SOAPBodyElement[] requestSBElts = new SOAPBodyElement[1];

      // Now get the Xml data from a file, read it into a document and put the document
      // into the message body

      File paramFile = new File("c:\\temp\\WCT_API.xml");

      FileInputStream fis = new FileInputStream(paramFile);
      requestSBElts[0] = 
        new SOAPBodyElement(XMLUtils.newDocument(fis).getDocumentElement());

      // Make the call to the service
      Vector resultSBElts = (Vector) call.invoke(requestSBElts);

      // Get the response message, extract the return document 
      // from the message body and print it out as XML.
      //
      SOAPBodyElement resElt = null;
      resElt = (SOAPBodyElement)resultSBElts.get(0);
      System.out.println(XMLUtils.ElementToString(resElt.getAsDOM()));
    }
    catch (AxisFault fault) {
      System.err.println("\nRECEIVED A FAULT FROM THE SERVICE:");
      System.err.println("Fault actor:   " + fault.getFaultActor());
      System.err.println("Fault code:    " + fault.getFaultCode());
      System.err.println("Fault string:\n" + fault.getFaultString());

    }
  }
}
