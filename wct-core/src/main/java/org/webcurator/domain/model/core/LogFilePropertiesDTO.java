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
package org.webcurator.domain.model.core;

import java.util.Date;

/**
 * The Object for transferring Log File Properties between the Asset store and the 
 * other web curator components
 * @author oakleigh_sk
 */
public class LogFilePropertiesDTO {

    /** The name of the file. */
    private String name = "";

    /** The absolute path of the file. */
    private String path = "";

    /** The formatted byte length of the file. */
    private String lengthString = "";

    /** The date the file was last modified. */
    private Date lastModifiedDate = null;
    
    private String viewer = "log-viewer.html";
    private String retriever = "log-retriever.html";

	/**
	 * Default constructor.
	 */
	public LogFilePropertiesDTO() {
	}

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
	
    /**
     * @return Returns the path.
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path The path to set.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return Returns the length.
     */
    public String getLengthString() {
        return lengthString;
    }

    /**
     * @param length The length to set.
     */
    public void setLengthString(String lengthString) {
        this.lengthString = lengthString;
    }

    /**
     * @return Returns the lastModifiedDate. 
     */
    public Date getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    /**
     * @param path The path to set.
     */
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

	public void setViewer(String viewer) {
		this.viewer = viewer;
	}

	public String getViewer() {
		return viewer;
	}

	public void setRetriever(String retriever) {
		this.retriever = retriever;
	}

	public String getRetriever() {
		return retriever;
	}
    
}
