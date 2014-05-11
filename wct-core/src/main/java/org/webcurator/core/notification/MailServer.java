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

import javax.mail.MessagingException;

/**
 * Provides access to a SMTP mail gateway for sending emails.
 * @author bprice
 */
public interface MailServer {

    /**
     * sends an email. 
     * @param email the Mailable object, containing the sender and recipients etc.
     * @throws MessagingException
     */
    void send(Mailable email) throws MessagingException;
    
    /**
     * sends an email with associated attachment.
     * @param email the Mailable object, containing the sender and recipients etc.
     * @param filename the name of file to attach
     * @param mimeType the mime type of the attached file
     * @param file an InputStream on the file to attach
     * @throws MessagingException
     */
    void send(Mailable email, String filename, String mimeType, InputStream file) throws MessagingException;
    
    /**
     * sends an email with associated attachment. Only use this for small attachment
     * as this will load the entire file attachment into memory before sending.
     * @param email the Mailable object, containing the sender and recipients etc.
     * @param filename the name of file to attach
     * @param mimeType the mime type of the attached file
     * @param file as a String to attach
     * @throws MessagingException
     */
    void send(Mailable email, String filename, String mimeType, String file) throws MessagingException;
    
    /**
     * sends an HTML based email. Also sends a plain text component in case the
     * recipients mail client doesn't allow HTML emails.  
     * @param email the Mailable object, containing the sender and recipients etc.
     * @throws MessagingException
     */
    void sendHTML(Mailable email) throws MessagingException; 
}
