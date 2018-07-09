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
package org.webcurator.core.notification;

/**
 * Provides the appropriate information required by the InTrayManager to
 * build automated hyperlinks to the resource in question. For example this
 * interface should be implemented by any WCT object that a Task or Notification
 * is refers to.
 * @author bprice
 */
public interface InTrayResource {

    /**
     * gets the Primary key of the WCT object
     * @return the Primary key of the object
     */
    Long getOid();
    
    /**
     * gets the Resource name of the WCT object, this may be different 
     * to the persisted name of the object
     * @return the Resource Name
     */
    String getResourceName();
    
    /**
     * gets the Class of the WCT object. For example
     * @code this.class.getName();
     * @return the class of the object
     */
    String getResourceType();
}
