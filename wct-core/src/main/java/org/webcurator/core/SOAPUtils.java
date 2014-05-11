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
package org.webcurator.core;

import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory;
import org.webcurator.domain.model.core.ArcHarvestResource;
import org.webcurator.domain.model.core.ArcHarvestResult;
import org.webcurator.domain.model.core.HarvestResourceDTO;

/**
 * SOAP Utilities class. This class abstracts the mechanism for making
 * SOAP calls.
 */
public class SOAPUtils {
	/** The argument types that have been registered */
	private static Map<Class, Mapping> REG_CLASS_TYPES = new HashMap<Class,Mapping>();
	
	static {
		REG_CLASS_TYPES.put(DataHandler.class, new Mapping(new QName("urn:WebCuratorTool", "DataHandler"), JAFDataHandlerSerializerFactory.class, JAFDataHandlerDeserializerFactory.class));
		REG_CLASS_TYPES.put(ArcHarvestResource.class, new Mapping( new QName("urn:WebCuratorTool", "ArcHarvestResource"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
		REG_CLASS_TYPES.put(ArcHarvestResult.class, new Mapping( new QName("urn:WebCuratorTool", "ArcHarvestResult"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
		REG_CLASS_TYPES.put(HarvestResourceDTO.class, new Mapping( new QName("urn:WebCuratorTool", "HarvestResourceDTO"), BeanSerializerFactory.class, BeanDeserializerFactory.class));
	}
	
	
	private static class Mapping {
		private QName qname;
		private Class serializer;
		private Class deserializer;
		
		public Mapping(QName qname, Class ser, Class des) {
			this.qname = qname;
			serializer = ser;
			deserializer = des;
		}
	}

	/**
	 * Register a set of types against the SOAP call.
	 * @param call The call to register against.
	 * @param classes The classes to register.
	 */
	public static void regTypes(Call call, Class...classes) {
		for(int i=0; i<classes.length; i++) {
			Mapping mapping = REG_CLASS_TYPES.get(classes[i]);
			if(mapping != null) {
				call.registerTypeMapping(classes[i], mapping.qname, mapping.serializer, mapping.deserializer);
			}
		}
	}
	
	/**
	 * Register a type against the SOAP call.
	 * @param call The call to register against.
	 * @param clazz The class to register.
	 */	
	public static void regType(Call call, Class clazz) {
		Mapping mapping = REG_CLASS_TYPES.get(clazz);
		if(mapping != null) {
			call.registerTypeMapping(clazz, mapping.qname, mapping.serializer, mapping.deserializer);
		}
	}
	
	/**
	 * Register types against the SOAP call.
	 * @param call The call to register against.
	 * @param objects The objects that are to be passed to the Call object. This method 
	 *                will determine the type of the object and register those types.
	 */		
	public static void regTypes(Call call, Object...objects) {
		for(int i=0; i<objects.length; i++) {
			Mapping mapping = REG_CLASS_TYPES.get(objects[i].getClass());
			if(mapping != null) {
				call.registerTypeMapping(objects[i].getClass(), mapping.qname, mapping.serializer, mapping.deserializer);
			}
		}
	}

}
