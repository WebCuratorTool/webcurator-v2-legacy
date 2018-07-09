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
 * represents the Notification details in the system. A notification is a message
 * sent to an individual to inform them of what has happened. A Notification 
 * requires no direct action.
 * @author bprice
 * @hibernate.class table="NOTIFICATION" lazy="false"
 * @hibernate.query name="org.webcurator.domain.model.core.Notification.getUserNotifications" query="from Notification ntfy where ntfy.recipientOid=:recipientOid order by ntfy.sentDate desc"
 * @hibernate.query name="org.webcurator.domain.model.core.Notification.cntUserNotifications" query="select count(*) from Notification ntfy where ntfy.recipientOid=:recipientOid" 
 */
public class Notification {
    /** Query identifier to retrieve notifications for a given user */
    public static final String QRY_GET_USER_NOTIFICATIONS = "org.webcurator.domain.model.core.Notification.getUserNotifications";
    public static final String QRY_CNT_USER_NOTIFICATIONS = "org.webcurator.domain.model.core.Notification.cntUserNotifications";
    
    /** The database OID of the notification */
    private Long oid;
    /** The sender of the notification */
    private String sender;
    /** The recipient of the notification */
    private Long recipientOid;
    /** The date the notification was sent */
    private Date sentDate;
    /** The subject of the notification */
    private String subject;
    /** The message of the notification */
    private String message;
    /** The name of the recipient */
    private transient String recipientName;
    
    /**
     * Constructor.
     */
    public Notification() {
        super();
    }

    /**
     * gets the Message to display
     * @return the Message
     * @hibernate.property column="NOT_MESSAGE" length="2000" 
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message to be sent.
     * @param message The message text.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * gets the Recipient of this Notification
     * @return the Recipient of the Notification
     * @hibernate.property column="NOT_USR_OID" not-null="true"
     */
    public Long getRecipientOid() {
        return recipientOid;
    }

    /**
     * Sets the recipient of the notification.
     * @param recipientOid The recipient User's database OID.
     */
    public void setRecipientOid(Long recipientOid) {
        this.recipientOid = recipientOid;
    }

    /**
     * gets the Sender of the Notification, this can be a User or a System component
     * @return the Sender name as a String
     * @hibernate.property column="NOT_SENDER" not-null="true" length="80"
     */
    public String getSender() {
        return sender;
    }

    /**
     * Set the sender of the notification. 
     * @param sender The display name for the sender of the notification.
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * gets the Date and Time this Notification was sent
     * @return the Date/Time of the Notification
     * @hibernate.property type="timestamp" not-null="true"
     * @hibernate.column name="NOT_SENT_DATE" sql-type="timestamp(9)"
     */
    public Date getSentDate() {
        return sentDate;
    }

    /**
     * Set the sent date for this notification.
     * @param sentDate The sent date of this notification.
     */
    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    /**
     * gets the Subject line of the Notification, this is a summary of the
     * Notification event. For full details refer to the getMessage() method.
     * @return the Notification subject
     * @hibernate.property column="NOT_SUBJECT" length="255" not-null="true"
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set the subject line for this notification.
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Return the database OID of this notification.
     * @return Returns the oid.
     * @hibernate.id column="NOT_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="Notification" 
     */
    public Long getOid() {
        return oid;
    }

    /**
     * Set the database OID of this notification.
     * @param oid The database OID of this notification.
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }

    /**
     * Get the name of the recipient.
     * @return The name of the recipient.
     */
    public String getRecipientName() {
        return recipientName;
    }

    /**
     * Set the name of the recipient.
     * @param recipientName The name of the recipient.
     */
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
    
}
