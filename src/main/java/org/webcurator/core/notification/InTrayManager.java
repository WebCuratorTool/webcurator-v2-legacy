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

import java.util.List;

import org.webcurator.domain.Pagination;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Notification;
import org.webcurator.domain.model.core.Task;

/**
 * The InTrayManager is responsible for managing and creating System Notifications and Tasks.
 * The InTrayManager is also responsible for determining which users receive their Notifications
 * via email or their in tray.
 * @author bprice
 */
public interface InTrayManager {
    /**
     * This flag specifices if the List of privileges must all exist for a User to receive the Notification
     */
    public final static  String ALL_PRIVILEGES = "AND";
    /**
     * This flag specifies that at least one or more privileges must exist before the User receives the Notification
     */
    public final static  String ONE_OR_MORE_PRIVILEGES = "OR";
    
    /**
     * generates an automated Notification for a specified user based on the notificationType and
     * the wctResource passed in. The wctResource must implement the InTrayResource Interface.
     * @param userOid the Users oid to create the notification for
     * @param notificationType the type of Notification to generate
     * @param wctResource the WCT resource that is effected by this notification.
     */
    void generateNotification(Long userOid, int notificationCategory, String notificationType, InTrayResource wctResource);
    
    /**
     * generates a Notification for the specified User
     * @param userOid the Users oid to create the notification for
     * @param subject the Subject of the Notification
     * @param message the detailed message of the Notification
     */
    void generateNotification(Long userOid, int notificationCategory, String subject, String message);
    
    void generateNotification(Long userOid, int notificationCategory, String subjectKey, Object[] subjectSubst, String messageKey, Object[] messageSubst, InTrayResource wctResource, boolean editMode);
    
    /**
     * generates a list of Notifications in the system to users that have at least one of the privileges
     * specified in the privilege list.
     * @param privileges the List of Privileges this Notification is appropriate for
     * @param andOrFlag the Flag to specify if the privileges suppplied are all required ALL_PRIVILEGES or just ONE_OR_MORE_PRIVILEGES
     * @param subject the Subject of the Notification
     * @param message the detailed message of the Notification
     */
    void generateNotification(List privileges, String andOrFlag, int notificationCategory, String subject, String message);
    
    /**
     * generates a list of Notifications in the system to users that have at least one of the privileges
     * specified in the privilege list.
     * @param privileges the List of Privileges this Notification is appropriate for
     * @param subject the Subject of the Notification
     * @param message the detailed message of the Notification
     */
    void generateNotification(List privileges, int notificationCategory, String subject, String message);
    
    /**
     * gets a List of Notification objects appropriate for the User
     * @param user the User 
     * @param pageNum the the current page of the results to get
     * @return the Pagination object containing the Notifications
     */
    Pagination getNotifications(User user, int pageNum, int pageSize);
    
    /**
     * Count the notifications available for the specified user.
     * @param user the user to count the notifications for
     * @return a count of notifications
     */
    int countNotifications(User user);
    
    /**
     * gets the List of Tasks appropriate for the user
     * @param user the User
     * @param pageNum the the current page of the results to get
     * @return the Pagination object containing the Tasks
     */
    Pagination getTasks(User user, int pageNum, int pageSize);
    
    /**
     * Return a count of tasks for the specified user.
     * @param user the user to count tasks for
     * @return the count of tasks
     */
    int countTasks(User user);
    
    /**
     *  generates a Task in the System for all Users within an Agnecy with a particular privilege
     * @param privilege the Privilege
     * @param messageType the type of message to create for this task
     * @param wctResource the WCT resource that is the source of this Task.
     */
    void generateTask(String privilege, String messageType, InTrayResource wctResource);
    
    
    /**
     * Create a unique task for the given privilege/messageType/resource. If any
     * tasks already exist for the messageType and resource, then the task will
     * not be created.
     * @param privilege   The permission to allocate the task to if it needs
     * 					  to be created.
     * @param messageType The type of the task.
     * @param wctResource The resource that the task relates to.
     */
    public void generateUniqueTask(String privilege, String messageType, InTrayResource wctResource);

    /**
     * Count the number of tasks of the given type, related to a particular
     * resource.
     * @param messageType The task type.
     * @param wctResource The related resource.
     * @return The number of tasks.
     */
    public int countTasks(String messageType, InTrayResource wctResource);
    
    
    /**
     * deletes a specified Notification object
     * @param notificationOid the Oid of the Notification to delete
     */
    void deleteNotification(Long notificationOid);
    
    /**
     * deletes a specified Task object
     * @param taskOid the Oid of the Task to delete
     */
    void deleteTask(Long taskOid);
    
    /**
     * Deletes a task for the specified resource or the specified type.
     * @param aResourceOid the id of the resource
     * @param aResourceType the type of the resource
     * @param aTaskType the type of the task
     */
    void deleteTask(Long aResourceOid, String aResourceType, String aTaskType);
    
    /**
     * Deletes all tasks for the specified resource or the specified type.
     * @param aResourceOid the id of the resource
     * @param aResourceType the type of the resource
     * @param aTaskType the type of the task
     */
    void deleteTasks(Long aResourceOid, String aResourceType, String aTaskType);

    /**
     * gets a fully populated Notification object
     * @param notificationOid the oid of the Notification to load
     * @return the Notification object
     */
    Notification getNotification(Long notificationOid);
    
    /**
     * gets a fully populated Task object
     * @param taskOid the oid of the task to load
     * @return the Task object
     */
    Task getTask(Long taskOid);
    
    /**
     * The default system email address to be used if the Task or Notification is
     * sent by the system.
     * @param systemSender the system email address Notifications/Tasks get sent from
     */
    void setSender(String systemSender);
    
    /**
     * lets the User Claim the specified task
     * @param user the user claiming the task
     * @param taskOid the task oid in question
     */
    void claimTask(User user, Long taskOid);
    
    /**
     * lets the User un-claim the specified task
     * @param user the user returning the task
     * @param taskOid the task oid in question
     */
    void unclaimTask(User user, Long taskOid);
    
    /**
     * Deletes all notifications sent to the given user.
     * @param userOid The OID of the user to delete notifications for.
     */
    public void deleteAllNotifications(Long userOid);

    /**
     * Deletes all tasks 
     */
	public void deleteAllTasks();
}
