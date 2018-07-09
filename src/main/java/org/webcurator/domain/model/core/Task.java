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

import org.webcurator.domain.model.auth.Agency;

/**
 * A Task represents an action that must be taken by a user within the WCT system.
 * A Task can be assigned directly to a User of the system, or viewable by all Users with
 * a specified privilege.
 * @author bprice
 * @hibernate.class table="TASK" lazy="false"
 */
public class Task {
    /** The database OID of the task */
    private Long oid;
    /** The sender of the task */
    private String sender;
    /** The OID of the user the task is assigned to */
    private Long assigneeOid;
    /** The date the task was sent */
    private Date sentDate;
    /** The subject of the task */
    private String subject;
    /** The message of the task */
    private String message;
    /** The privilege required to complete the task */
    private String privilege;
    /** The agency the task should be visible to */
    private Agency agency;
    /** The database OID of the resource to which the task is related */
    private Long resourceOid;
    /** The classname of the resource to which the task is related */
    private String resourceType;
    /** The type of message */
    private String messageType;
    
    /** The name of the owner of the task */
    private transient String owner;
    
    /**
     * No-arg constructor.
     */
    public Task() {
    }
    
    /**
     * gets the Assignee Oid for this task
     * @return the Asignee Oid of the Task
     * @hibernate.property column="TSK_USR_OID" not-null="false"
     */
    public Long getAssigneeOid() {
        return assigneeOid;
    }
    
    /** 
     * Sets the assignee of the task.
     * @param assigneeOid The OID of the User that has claimed the tsak.
     */
    public void setAssigneeOid(Long assigneeOid) {
        this.assigneeOid = assigneeOid;
    }
    
    /**
     * gets the Message to display
     * @return the Message
     * @hibernate.property column="TSK_MESSAGE" length="2000" 
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Sets the message body.
     * @param message The message body.
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Gets the database OID of the task.
     * @return Returns the oid.
     * @hibernate.id column="TSK_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="Task" 
     */
    public Long getOid() {
        return oid;
    }
    
    /**
     * Sets the OID of the task.
     * @param oid The new database OID.
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }
    
    /**
     * gets the Sender of the Task, this can be a User or a System component
     * @return the Sender name as a String
     * @hibernate.property column="TSK_SENDER" not-null="true" length="80"
     */
    public String getSender() {
        return sender;
    }
    
    /**
     * Sets the send of the task.
     * @param sender The name of the send as a string.
     */
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    /**
     * gets the Date and Time this Task was sent
     * @return the Date/Time of the Task
     * @hibernate.property type="timestamp" not-null="true"
     * @hibernate.column name="TSK_SENT_DATE" sql-type="timestamp(9)"
     */
    public Date getSentDate() {
        return sentDate;
    }
    
    /**
     * Sets the date the task was sent.
     * @param sentDate The date the task was sent.
     */
    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }
    
    /**
     * gets the Subject line of the Task, this is a summary of the
     * Task. For full details refer to the getMessage() method.
     * @return the Task subject
     * @hibernate.property column="TSK_SUBJECT" length="255" not-null="true"
     */
    public String getSubject() {
        return subject;
    }
    
    /**
     * Set the subject line of the task. This is a summary of the task.
     * @param subject The subject line.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * gets the Privilege for which this Task is appropriate
     * @return the Privilege code
     * @hibernate.property column="TSK_PRIVILEGE" length="40" not-null="false"
     */
    public String getPrivilege() {
        return privilege;
    }

    /**
     * Set the privilege required to complete this task. This is used to 
     * determine which users should see which tasks.
     * @param privilege The privilege required to complete the task.
     */
    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    /**
     * gets the Agency for which this Task is appropriate. Usually used
     * in combination with the Privilege attribute to determine who can
     * see the Task within the Agency.
     * @return the Agency object
     * @hibernate.many-to-one not-null="true" class="org.webcurator.domain.model.auth.Agency" column="TSK_AGC_OID" foreign-key="FK_TASK_AGENCY_OID"
     */
    public Agency getAgency() {
        return agency;
    }

    /**
     * Set the agency that should be able to see this task.
     * @param agency The agency that should be able to see this task.
     */
    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    /**
     * Get the owner of this task.
     * @return The name of the owner of the task.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Set the owner of this task.
     * @param owner The name of the owner of this task.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

	/**
	 * Gets the message type for this message.
	 * @see org.webcurator.core.notification.MessageType
	 * @return the messageType
	 * @hibernate.property column="TSK_MSG_TYPE" length="40" not-null="true"
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * Set the message type for this message.
	 * @see org.webcurator.core.notification.MessageType
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	/**
	 * Get the OID of the resource associated with this task.
	 * @return the resourceOid
	 * @hibernate.property column="TSK_RESOURCE_OID" not-null="true"
	 */
	public Long getResourceOid() {
		return resourceOid;
	}

	/**
	 * Set the OID of the resource associated with this task.
	 * @param resourceOid the resourceOid to set
	 */
	public void setResourceOid(Long resourceOid) {
		this.resourceOid = resourceOid;
	}

	/**
	 * Get the classname of the resource related to this task.
	 * @return the resourceType
	 * @hibernate.property column="TSK_RESOURCE_TYPE" length="80" not-null="true"
	 */
	public String getResourceType() {
		return resourceType;
	}

	/**
	 * Set the classname of the resource associated with this task.
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
}
