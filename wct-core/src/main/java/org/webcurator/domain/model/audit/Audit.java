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
package org.webcurator.domain.model.audit;

import java.util.Date;

/**
 * Audit object holds a single Audit action 
 * and can persist this to the database
 * @author bprice
 * @hibernate.class table="WCTAUDIT" lazy="false"
 * @hibernate.query name="org.webcurator.domain.model.audit.Audit.getAllByPeriodByAgencyByUser" query="SELECT au FROM Audit au, org.webcurator.domain.model.auth.User u, org.webcurator.domain.model.auth.Agency ag WHERE au.userOid=u.oid AND u.agency.oid=ag.oid AND au.dateTime>=? AND au.dateTime<=? AND ( ?='All agencies' OR ( ?!='All agencies' AND ? = u.agency.name) ) AND ( ?='All users' OR ( ? !='All users' AND au.userOid=u.oid AND ? = u.username) ) ORDER BY au.dateTime"
 */
public class Audit {
		
	/** Query to retrieve Audit messages for a given user. */
	public static final String QRY_GET_ALL_BY_PERIOD_BY_AGENCY_BY_USER = "org.webcurator.domain.model.audit.Audit.getAllByPeriodByAgencyByUser";

	/** The database OID of the audit message */
    private Long oid;
    /** The date/time at which the event took place */
    private Date dateTime;
    /** The OID of the user that performed this action */
    private Long userOid;
    /** The OID of the agency that performed this action */
    private Long agencyOid;
    /** The username of the user that performed this action */
    private String userName;
    /** The first name of the user that performed this action */
    private String firstname;
    /** The last name ofthe user that performed this action */
    private String lastname;
    /** The action that was performed */
    private String action;
    /** The type of object this event acted on */
    private String subjectType;
    /** The OID of the object that was affected */
    private Long subjectOid;
    /** The message string to go with the audit log */
    private String message;
    
    /**
     * gets the Audit Action 
     * @return the Audit Action
     * @hibernate.property column="AUD_ACTION" not-null="true" length="40"
     */
    public String getAction() {
        return action;
    }
    
    /**
     * Sets the audit action.
     * @param action The audit action.
     */
    public void setAction(String action) {
        this.action = action;
    }
    
    /**
     * gets the Date and Time of the Audit entry
     * @return the DateTime
     * @hibernate.property type="timestamp"
     * @hibernate.column name="AUD_DATE" not-null="true" sql-type="TIMESTAMP(9)"
     */
    public Date getDateTime() {
        return dateTime;
    }
    
    /**
     * Sets the date/time of the audit event.
     * @param dateTime The date/time of the Audit event.
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
    
    /**
     * gets the Users firstname
     * @return the users firstname
     * @hibernate.property column="AUD_FIRSTNAME" not-null="false" length="50"
     */
    public String getFirstname() {
        return firstname;
    }
    
    /**
     * Sets the first name attribute on the audit message.
     * @param firstname The user's first name.
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    /**
     * gets the Users lastname
     * @return the users lastname
     * @hibernate.property column="AUD_LASTNAME" not-null="false" length="50"
     */
    public String getLastname() {
        return lastname;
    }
    
    /**
     * Sets the last name of the user on the audit message.
     * @param lastname The User's last name.
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    /**
     * gets the Audit messgae
     * @return the message
     * @hibernate.property column="AUD_MESSAGE" not-null="true" length="2000"
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Sets the audit message 
     * @param message The message to log.
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * gets the affected subjectType type, this is most likely a class
     * @return the subjectType type
     * @hibernate.property column="AUD_SUBJECT_TYPE" not-null="true" length="255"
     */
    public String getSubjectType() {
        return subjectType;
    }
    
    /**
     * Set the affected subject type. This is usually the name of the class
     * of the object that was acted upon.
     * @param subjectType The affected subject type.
     */
    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }
    
    /**
     * gets the Username of the user who issued the action
     * @return the Username
     * @hibernate.property column="AUD_USERNAME" not-null="false" length="80"
     */
    public String getUserName() {
        return userName;
    }
    
    /**
     * Set the username on the audit record.
     * @param userName The username to associate with the record.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    /**
     * gets the User oid of the user who issued this action
     * @return the User Oid
     * @hibernate.property column="AUD_USER_OID" not-null="false"
     */
    public Long getUserOid() {
        return userOid;
    }
    
    /**
     * Set the OID of the user that performed the action.
     * @param userOid The OID of the user that issued this action.
     */
    public void setUserOid(Long userOid) {
        this.userOid = userOid;
    }
    
    /** 
     * gets the affected Subject Oid, this can be used in conjunction with
     * the subject type to reconstruct the affected object at a later stage
     * @return the subject oid
     * @hibernate.property column="AUD_SUBJECT_OID" not-null="false"
     */
    public Long getSubjectOid() {
        return subjectOid;
    }
    
    /**
     * Get the OID of the object that was affected.
     * @param subjectOid The OID of the object affected.
     */
    public void setSubjectOid(Long subjectOid) {
        this.subjectOid = subjectOid;
    }
    
    /**
     * gets the Audit message oid, this is its primary key
     * @return the Audit oid
     * @hibernate.id column="AUD_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="Audit"
     */
    public Long getOid() {
        return oid;
    }
    
    /** 
     * Set the dataase OID of this audit message.
     * @param oid The database OID of this message.
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }
    
    /**
     * gets the Agency oid of the user who issued this action
     * @return the Agency Oid
     * @hibernate.property column="AUD_AGENCY_OID" not-null="false"
     */
    public Long getAgencyOid() {
        return agencyOid;
    }
    
    /**
     * Stores the agency OID of the user that issued this action.
     * @param agencyOid The OID of the user's agency.
     */
    public void setAgencyOid(Long agencyOid) {
        this.agencyOid = agencyOid;
    }
    
    
}
