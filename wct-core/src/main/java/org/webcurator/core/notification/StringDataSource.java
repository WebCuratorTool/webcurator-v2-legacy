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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;

import javax.activation.DataSource;

/**
 * Allows a String's contents to be passed around as an InputStream
 * @author bprice
 */
public class StringDataSource implements DataSource {

    private String data = null;
    private String mimeType = null;
    private String attachName = null;
    
    public StringDataSource(String attachName, String data, String mimeType) {
        this.data = data;
        this.mimeType = mimeType;
        this.attachName = attachName;
    }
    
    /* (non-Javadoc)
     * @see javax.activation.DataSource#getInputStream()
     */
    public InputStream getInputStream() throws IOException {
         return new StringBufferInputStream(this.data);
    }

    /* (non-Javadoc)
     * @see javax.activation.DataSource#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("StringDataSource is read only.");
    }

    /* (non-Javadoc)
     * @see javax.activation.DataSource#getContentType()
     */
    public String getContentType() {
        return this.mimeType;
    }

    /* (non-Javadoc)
     * @see javax.activation.DataSource#getName()
     */
    public String getName() {
        return this.attachName;
    }
}


   