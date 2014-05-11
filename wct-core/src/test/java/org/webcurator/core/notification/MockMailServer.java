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

import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * The implementation of the MailServer interface.
 * @see MailServer
 * @author bprice
 */
public class MockMailServer implements MailServer {

    private Properties mailConfig = null;

    /**
     * Constructor.
     * @param aMailConfig mail config
     */
    public MockMailServer(Properties aMailConfig) {
        mailConfig = aMailConfig;
    }
    
    private Mailable email;
    
    public Mailable getEmailResult()
    {
    	return email;
    }
    
    
    public void send(Mailable email, String filename, String mimeType, InputStream file) throws MessagingException {
        this.email = email;
    }


    public void send(Mailable email) throws MessagingException {
    	this.email = email;
    }
    
    /**
     * Return a mail session for this helpers config.
     * @return the session
     */
    public Session getSession() {
        return null;
    }

    public void send(Mailable email, String filename, String mimeType, String file) throws MessagingException {
    	this.email = email;
    }
    
    public void sendHTML(Mailable email) throws MessagingException {
    	this.email = email;
    }

}
