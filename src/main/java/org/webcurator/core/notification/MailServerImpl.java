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
import javax.mail.Address;
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
public class MailServerImpl implements MailServer {

    private Properties mailConfig = null;

    /**
     * Constructor.
     * @param aMailConfig mail config
     */
    public MailServerImpl(Properties aMailConfig) {
        mailConfig = aMailConfig;
    }
    
    public void send(Mailable email, String filename, String mimeType, InputStream file) throws MessagingException {
        Session mailSession = Session.getInstance(this.mailConfig, null);
        Message message = new MimeMessage(mailSession);

        message.setFrom(new InternetAddress(email.getSender()));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getRecipients()));
        
        if(email.getReplyTo()!=null && email.getReplyTo().trim().length()!=0) {
            message.setReplyTo(new Address[] {new InternetAddress(email.getReplyTo())});
        }
        
        setUpCCandBCC(email, message);
        
        message.setSubject(email.getSubject());
        message.setSentDate(new Date());
        
//      Create the message part 
        BodyPart messageBodyPart = new MimeBodyPart();

//      Fill the message
        messageBodyPart.setText(email.getMessage());

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

//      Part two is the attachment
        messageBodyPart = new MimeBodyPart();
        DataSource source = new InputStreamDataSource(filename, file, mimeType);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);

//         Put parts in message
        message.setContent(multipart);
        
        Transport.send(message); 
    }

	private void setUpCCandBCC(Mailable email, Message message)
			throws MessagingException, AddressException {
		if (email.getCcs() != null && email.getCcs().length() > 0)
        {
			addRecipient(message, email.getCcs(),Message.RecipientType.CC);
        }
        
        if (email.getBccs() != null && email.getBccs().length() > 0)
        {
        	addRecipient(message, email.getBccs(),Message.RecipientType.BCC);
        }
	}
	
	private void addRecipient(Message message, String emails, Message.RecipientType rType) 
	throws MessagingException, AddressException
	{
		if (emails != null && emails.length() > 0) {
			if (emails.contains(";"))
			{
		    	for (String email: emails.split(";"))
		    	{
		    		message.addRecipient(rType, new InternetAddress(email));
		    	}
			}
			else
			{
				message.addRecipient(rType, new InternetAddress(emails));
			}
		}
	}

    public void send(Mailable email) throws MessagingException {
        Session mailSession = Session.getInstance(this.mailConfig, null);
        Message message = new MimeMessage(mailSession);

        message.setFrom(new InternetAddress(email.getSender()));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getRecipients()));
        setUpCCandBCC(email, message);

        if(email.getReplyTo()!=null && email.getReplyTo().trim().length()!=0) {
            message.setReplyTo(new Address[] {new InternetAddress(email.getReplyTo())});
        }

        message.setSubject(email.getSubject());
        message.setSentDate(new Date());
        message.setContent(email.getMessage(), "text/plain; charset=UTF-8");

        Transport.send(message); 
    }
    
    /**
     * Return a mail session for this helpers config.
     * @return the session
     */
    public Session getSession() {
        return Session.getInstance(this.mailConfig, null);
    }

    public void send(Mailable email, String filename, String mimeType, String file) throws MessagingException {
        Session mailSession = Session.getInstance(this.mailConfig, null);
        Message message = new MimeMessage(mailSession);

        message.setFrom(new InternetAddress(email.getSender()));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getRecipients()));
        message.setSubject(email.getSubject());
        message.setSentDate(new Date());
        
//      Create the message part 
        BodyPart messageBodyPart = new MimeBodyPart();

//      Fill the message
        messageBodyPart.setText(email.getMessage());

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

//      Part two is the attachment
        messageBodyPart = new MimeBodyPart();
        DataSource source = new StringDataSource(filename, file, mimeType);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);

//         Put parts in message
        message.setContent(multipart);
        
        Transport.send(message); 
    }
    
    public void sendHTML(Mailable email) throws MessagingException {
        Session mailSession = Session.getInstance(this.mailConfig, null);
        MimeMessage message = new MimeMessage(mailSession);
        MimeMultipart multipart = new MimeMultipart("alternative");
        BodyPart bp = new MimeBodyPart();
        bp.setContent(email.getMessage(), "text/plain; charset=UTF-8");
        BodyPart bp2 = new MimeBodyPart();
        bp2.setContent(email.getMessage(), "text/html; charset=UTF-8");
        
        multipart.addBodyPart(bp2);
        multipart.addBodyPart(bp);
        message.setContent(multipart);
        message.setSentDate(new java.util.Date());
        message.setFrom(new InternetAddress(email.getSender()));
        message.setSubject(email.getSubject());
        //FIXME if more than one recipient break it appart before setting the recipient field
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getRecipients()));
        Transport.send(message);
    }

}
