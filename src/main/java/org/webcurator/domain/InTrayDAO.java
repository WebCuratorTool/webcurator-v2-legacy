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
package org.webcurator.domain;

import java.util.List;

import org.webcurator.core.notification.InTrayResource;
import org.webcurator.domain.model.auth.RolePrivilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Task;

/**
 * Persistence methods commonly used by the In-tray
 * @author bprice
 */
public interface InTrayDAO {

	/**
	 * Save or update the object in the persitant store.
	 * @param aObject the object to save or update
	 */
    void saveOrUpdate(final Object aObject);
    
    /**
     * gets all Notifications for the specified User.
     * @param userOid the oid of the user to get the Notifications for
     * @param pageNum the specified page of the results to return
     * @return a Paginated set of Notification objects
     */
    Pagination getNotifications(Long userOid, int pageNum, int pageSize);
    
    /**
     * counts the Notifications for the specified User
     * @param userOid the oid of the user to count the Notifications for
     * @return a count of Notification objects
     */
    int countNotifications(final Long userOid);
    
    /**
     * loads a generic object, this could be a Notification or
     * a Task object based on the objects oid
     * @param clazz the Class of this object to load
     * @param oid the oid of this object
     * @return the loaded object, as an Object with no Casting applied
     */
    Object load(Class clazz, Long oid);
    
    /**
     * deletes a generic object, this could be a Notification
     * or a Task. 
     * @param obj the object to delete
     */
    void delete(final Object obj);
    
    /**
     * populates the Owner of the specified resource whether or not it is
     * a User or Agency owned object
     * @param wctResource the Resource to load the owning object on
     * @return the populated resource with a populated owner
     */
    InTrayResource populateOwner(InTrayResource wctResource);
    
    /**
     * obtains a List of Task objects based on the User and
     * the User's privileges within their Agency
     * @param user the User object
     * @param privs a List of RolePrivileges
     * @param pageNum the specified page of the results to return
     * @return the paginated set of Tasks
     */
    Pagination getTasks(User user, List<RolePrivilege> privs, int pageNum, int pageSize);
    
    /**
     * Count the tasks objects in the users view based on the User and
     * the User's privileges within their Agency
     * @param user the User object
     * @param privs a List of RolePrivileges
     * @return the count of tasks
     */
    int countTasks(final User user, final List<RolePrivilege> privs);
    
    /**
     * Return the Task that matches the specified criteria. 
     * @param aResourceOid the resource id
     * @param aResourceType the resource type
     * @param aTaskType the task type
     * @return the Task
     */
    Task getTask(final Long aResourceOid, final String aResourceType, final String aTaskType);
    
    /**
     * Return List of the Tasks that matche the specified criteria. 
     * @param aResourceOid the resource id
     * @param aResourceType the resource type
     * @param aTaskType the task type
     * @return the Task
     */
    List<Task> getTasks(final Long aResourceOid, final String aResourceType, final String aTaskType);

    /**
     * lets the specified User claim the task
     * @param user the User claiming the task
     * @param task the task
     */
    void claimTask(User user, Task task);
    
    /**
     * lets the specified User claim the task
     * @param user the User claiming the task
     * @param task the task
     */
    void unclaimTask(User user, Task task);
    
    /**
     * Count the number of tasks of a given type associated with a particular
     * resource. This can be used to ensure that we do not create two of the 
     * same tasks against a single object.
     * @param messageType The type of the task.
     * @param wctResource The intray resource the task is related to.
     * @return The number of tasks.
     */
    int countTasks(String messageType, InTrayResource wctResource);
    
    /**
     * Deletes all notifications for the specified user.
     * @param userOid The OID of the user.
     */
    void deleteNotificationsByUser(Long userOid);

	void deleteAllTasks();
}
