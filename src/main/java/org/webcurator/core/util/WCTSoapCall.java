/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.core.util;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.domain.model.core.ArcHarvestFileDTO;
import org.webcurator.domain.model.core.ArcHarvestResourceDTO;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.LogFilePropertiesDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;

/**
 * <code>WCTSoapCall</code> abstracts some of the SOAP mechanics from the 
 * rest of the WCT, allowing the WCT to be relatively free of SOAP related
 * functionality. This class should be used for all SOAP calls between the 
 * core, harvest agents, and digital asset store.  
 * 
 * @author bbeaumont
 */
public class WCTSoapCall {
	private static Log log = LogFactory.getLog(WCTSoapCall.class);
	
	/** the name of the harvest agent SOAP service. */
    public static final String HARVEST_AGENT = "/wct-harvest-agent/services/urn:HarvestAgent";
    /** the name of the harvest agent listener SOAP service. */
	public static final String WCT_HARVEST_LISTENER = "/wct/services/urn:WebCuratorTool";
	/** The name of the Digital Asset Store SOAP service. */
	public static final String DAS_SERVICE = "/wct-store/services/urn:DigitalAssetStore";
	/** The name of the Harvesy Agent Log Reader SOAP service. */
	public static final String AGENT_LOG_READER = "/wct-harvest-agent/services/urn:LogReader";
	/** The name of the Digital Asset Store SOAP service. */
	public static final String DAS_LOG_READER = "/wct-store/services/urn:LogReader";
	/** The map of class type mappings . */
	private static Map<Class, Mapping> REG_CLASS_TYPES = new HashMap<Class,Mapping>();
	
	/**
	 * Static construtor sets up all of the class types and their handlers. Most
	 * types are handled using the Axis BeanSerializerFactory.
	 * 
	 * @see BeanSerializerFactory
	 * @see BeanDeserializerFactory
	 */
	static {
		REG_CLASS_TYPES.put(DataHandler.class, new Mapping(new QName("urn:WebCuratorTool", "DataHandler"), JAFDataHandlerSerializerFactory.class, JAFDataHandlerDeserializerFactory.class));
		REG_CLASS_TYPES.put(ArcHarvestResourceDTO.class, new Mapping( new QName("urn:WebCuratorTool", "ArcHarvestResourceDTO"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
		REG_CLASS_TYPES.put(HarvestResourceDTO.class, new Mapping( new QName("urn:WebCuratorTool", "HarvestResourceDTO"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
		//REG_CLASS_TYPES.put(HarvestResourceDTO.class, new Mapping( new QName("urn:DigitalAssetStore", "HarvestResourceDTO"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
		
		REG_CLASS_TYPES.put(ArcHarvestResultDTO.class, new Mapping( new QName("urn:WebCuratorTool", "ArcHarvestResultDTO"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
        REG_CLASS_TYPES.put(HarvestAgentStatusDTO.class, new Mapping( new QName("urn:WebCuratorTool", "HarvestAgentStatusDTO"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
        REG_CLASS_TYPES.put(HarvesterStatusDTO.class, new Mapping( new QName("urn:WebCuratorTool", "HarvesterStatusDTO"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
        
        REG_CLASS_TYPES.put(ArcHarvestFileDTO.class, new Mapping( new QName("urn:WebCuratorTool", "ArcHarvestFileDTO"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
        
        REG_CLASS_TYPES.put(Header.class, new Mapping( new QName("urn:WebCuratorTool", "Header"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
        REG_CLASS_TYPES.put(HeaderElement.class, new Mapping( new QName("urn:WebCuratorTool", "HeaderElement"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
        REG_CLASS_TYPES.put(HeaderElement.class, new Mapping( new QName("urn:WebCuratorTool", "NameValuePair"), BeanSerializerFactory.class, BeanDeserializerFactory.class));              
        REG_CLASS_TYPES.put(CustomDepositFormCriteriaDTO.class, new Mapping( new QName("urn:WebCuratorTool", "CustomDepositFormCriteriaDTO"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
        REG_CLASS_TYPES.put(CustomDepositFormResultDTO.class, new Mapping( new QName("urn:WebCuratorTool", "CustomDepositFormResultDTO"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
        REG_CLASS_TYPES.put(LogFilePropertiesDTO.class, new Mapping( new QName("urn:LogReader", "LogFilePropertiesDTO"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
	}
	
	/**
	 * Private class to hold a mapping between the QName, serialiser and deserializer. 
	 * @author bbeaumont.
	 */
	private static class Mapping {
		/** The QName of the type */
		private QName qname;
		/** The serializer class */
		private Class serializer;
		/** The deserializer class */
		private Class deserializer;
		
		/**
		 * Construct a mapping.
		 * @param qname The QName of the type.
		 * @param ser   The serializer class.
		 * @param des   The deserializer class.
		 */
		public Mapping(QName qname, Class ser, Class des) {
			this.qname = qname;
			serializer = ser;
			deserializer = des;
		}
	}
	
	/** The end point of the SOAP call */
	private String endPoint;
	/** The operation to invoke */
	private String operation;
	/** The Axis Call object */
	private Call call;
	
	/**
	 * Create a new SOAP call.
	 * @param host       The host to send the message to.
	 * @param service    The name of the service to send the message to.
	 * @param operation  The operation to invoke.
	 * @throws ServiceException if there is a SOAP error.
	 */
	public WCTSoapCall(String host, String service, String operation) throws ServiceException {
		this(host,8080,service,operation);
	}
	
	
	public WCTSoapCall(WebServiceEndPoint wsEndPoint, String operation) throws ServiceException { 
		this(wsEndPoint.getHost(), wsEndPoint.getPort(), wsEndPoint.getService(), operation);
	}
	
	/**
	 * Create a new SOAP call.
	 * @param host       The host to send the message to.
	 * @param port		 The port the end point is listening on.
	 * @param service    The name of the service to send the message to.
	 * @param operation  The operation to invoke.
	 * @throws ServiceException if there is a SOAP error.
	 */
	public WCTSoapCall(String host, int port, String service, String operation) throws ServiceException {
		this.endPoint = "http://" + host + ":" + port + service;
		this.operation = operation;
		
		Service serv = new Service();
	    call = (Call) serv.createCall();
	}	
	
	/**
	 * Register a set of classes against this call so that they can be correctly
	 * serialized.
	 * @param classes The classes to register.
	 */
	public void regTypes(Class...classes) {
		for(int i=0; i<classes.length; i++) {
			Mapping mapping = REG_CLASS_TYPES.get(classes[i]);
			if(mapping != null) {
				call.registerTypeMapping(classes[i], mapping.qname, mapping.serializer, mapping.deserializer);
			}
		}
	}
	
	/**
	 * Invoke the SOAP call.
	 * @param objects The operation's arguments.
	 * @return The return value of the SOAP call.
	 * @throws RemoteException if there are any exceptions.
	 */
	public Object invoke(Object...objects) throws RemoteException {
        call.setTargetEndpointAddress(endPoint);
        call.setOperationName(operation);
        return call.invoke(objects);
	}
	
	/**
	 * Invoke the SOAP call asynchronously.
	 * @param objects The operation's arguments.
	 * @throws RemoteException if there are any exceptions.
	 */
	public void invokeOneWay(Object...objects) throws RemoteException {
        call.setTargetEndpointAddress(endPoint);
        call.setOperationName(operation);
        call.invokeOneWay(objects);
	}	
	
	/**
	 * Invoke the SOAP call.
	 * @param attempts The number of attempts to make. Set to 0 for infinite.
	 * @param retryDelay The number of milliseconds to wait between attempts.
	 * @param objects The operation's arguments.
	 * @return The return value of the SOAP call.
	 * @throws RemoteException if there are any exceptions.
	 */
	public Object retryingInvoke(int attempts, long retryDelay, Object...objects) throws RemoteException {
		int currentAttempts = attempts;
		
		while(true) {
			try {
		        call.setTargetEndpointAddress(endPoint);
		        call.setOperationName(operation);
		        return call.invoke(objects);
			}
			catch(RemoteException ex) {
				log.error("Failed to make call to " + operation, ex);
				currentAttempts--;
				if(currentAttempts == 0) { 
					throw new RemoteException("Failed after " + attempts + " attempts", ex);
				}
				log.info("Will retry in " + retryDelay + "ms");
				try { Thread.sleep(retryDelay); } catch(Exception e) { }
			}
		}
	}	
	
	/**
	 * Invoke the SOAP call.
	 * @param attempts The number of attempts to make. Set to 0 for infinite.
	 * @param retryDelay The number of milliseconds to wait between attempts.
	 * @param objects The operation's arguments.
	 * @return The return value of the SOAP call.
	 * @throws RemoteException if there are any exceptions.
	 */
	public Object infiniteRetryingInvoke(long retryDelay, Object...objects) {
		while(true) {
			try {
		        call.setTargetEndpointAddress(endPoint);
		        call.setOperationName(operation);
		        return call.invoke(objects);
			}
			catch(Exception ex) {
				log.error("Failed to make call to " + operation, ex);
				log.info("Will retry in " + retryDelay + "ms");
				try { Thread.sleep(retryDelay); } catch(Exception e) { }
			}
		}
	}	
}
