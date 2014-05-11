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

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.exceptions.NotOwnerRuntimeException;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.util.Auditor;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.InTrayDAO;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.UserRoleDAO;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.HarvestResult;
import org.webcurator.domain.model.core.Notification;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.core.Task;
import org.webcurator.domain.model.dto.UserDTO;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.site.command.DefaultSiteCommand;
import org.webcurator.ui.target.command.LogReaderCommand;
import org.webcurator.ui.target.command.TargetDefaultCommand;
import org.webcurator.ui.target.command.TargetInstanceCommand;

/**
 * The implementation of the InTrayManager interface.
 * @see InTrayManager
 * @author bprice
 */
public class InTrayManagerImpl implements InTrayManager{

    private static Log log = LogFactory.getLog(InTrayManagerImpl.class);
    
    private InTrayDAO inTrayDAO = null;
    
    private UserRoleDAO userRoleDAO = null;
    
    private AgencyUserManager agencyUserManager = null;
    
    private MailServer mailServer = null;
    
    private String sender;
    
    private String wctBaseUrl;
    
    private MessageSource messageSource;
    
    private Auditor audit;
    
    public InTrayManagerImpl() {

    }
    
    public void generateNotification(List privileges, String allOrOneFlag, int notificationCategory, String subject, String message) {
        Map usersToNotify = null;
        if (ALL_PRIVILEGES.equals(allOrOneFlag)) {
            usersToNotify = agencyUserManager.getUsersWithAllPrivilege(privileges);
        } else {
            usersToNotify = agencyUserManager.getUsersWithAtLeastOnePrivilege(privileges);
        }
        log.debug("Obtaining keys from the UsersToNotify map");
        Set keys = usersToNotify.keySet();
        Iterator it = keys.iterator();
        
            while (it.hasNext()) {
                
                Long userOid = (Long) it.next();
                UserDTO effectedUser = (UserDTO)usersToNotify.get(userOid);
                if(effectedUser.shouldSendNotification(notificationCategory)) {
	                log.debug("Found a key so creating Notification for user with oid "+userOid);
	                
	                Notification notify = new Notification();
	                notify.setSubject(subject);
	                notify.setMessage(message);
	                notify.setSentDate(new Date());
	                notify.setRecipientOid(userOid);
	                notify.setSender(this.sender);
	                
	                inTrayDAO.saveOrUpdate(notify);
	                
	                send(effectedUser, notify);
                }
                else {
                	log.debug("Not sending notification to " + effectedUser.getNiceName() + " because level " + notificationCategory + " is off");
                }
            }      
    }

    public void generateNotification(List privileges, int notificationCategory, String subject, String message) {
        generateNotification(privileges,ONE_OR_MORE_PRIVILEGES,notificationCategory,subject,message);
    }

    public void generateNotification(Long userOid, int notificationCategory, String notificationType, InTrayResource wctResource) {
        String subject = lookupSubject(notificationType, wctResource);
        String message = lookupMessage(notificationType, wctResource);
        
        generateNotification(userOid, notificationCategory, subject, message);
    }
    
	public void generateNotification(Long userOid, int notificationCategory, String subjectKey, Object[] subjectSubst, String messageKey, Object[] messageSubst, InTrayResource wctResource, boolean editMode) {
		Object[] newMessageSubst = new Object[messageSubst.length + 1];
		int i = 0;
		for(i=0; i<messageSubst.length; i++) { 
			newMessageSubst[i] = messageSubst[i];
		}
		newMessageSubst[i] = lookupLink(wctResource, editMode);
		
		String subjectLine = messageSource.getMessage(subjectKey, subjectSubst, Locale.getDefault());
		String messageLine = messageSource.getMessage(messageKey, newMessageSubst, Locale.getDefault());		

        generateNotification(userOid, notificationCategory, subjectLine, messageLine);
	}        

    public void generateNotification(Long userOid, int notificationCategory, String subject, String message) {
    	UserDTO effectedUser = agencyUserManager.getUserDTOByOid(userOid);
    	
    	if(effectedUser.shouldSendNotification(notificationCategory)) {
	        Notification notify = new Notification();
	        notify.setSubject(subject);
	        notify.setMessage(message);
	        notify.setSentDate(new Date());
	        notify.setRecipientOid(userOid);
	        notify.setSender(this.sender);
	        
	        inTrayDAO.saveOrUpdate(notify);
	        
	        send(effectedUser, notify);
    	}
        else {
        	log.debug("Not sending notification to " + effectedUser.getNiceName() + " because level " + notificationCategory + " is off");
        }
    }

    public Pagination getNotifications(User user, int pageNum, int pageSize) {
        return inTrayDAO.getNotifications(user.getOid(), pageNum, pageSize);
    }

    public int countNotifications(User user) {
    	return inTrayDAO.countNotifications(user.getOid());
    }
    
    @SuppressWarnings("unchecked")
	public int countTasks(User user) {
    	return inTrayDAO.countTasks(user, userRoleDAO.getUserPrivileges(user.getUsername()));
    }
    
    public void setInTrayDAO(InTrayDAO inTrayDAO) {
        this.inTrayDAO = inTrayDAO;
    }

    public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
        this.agencyUserManager = agencyUserManager;
    }

    public void setMailServer(MailServer mailServer) {
        this.mailServer = mailServer;
    }
    
    
    public void generateUniqueTask(String privilege, String messageType, InTrayResource wctResource) {
    	if( countTasks(messageType, wctResource) == 0) {
    		generateTask(privilege, messageType, wctResource);
    	}
    }
    
    public int countTasks(String messageType, InTrayResource wctResource) {
    	return inTrayDAO.countTasks(messageType, wctResource);
    }
    
    
    public void generateTask(String privilege, String messageType, InTrayResource wctResource) {
        String subject = lookupSubject(messageType, wctResource);
        String message = lookupMessage(messageType, wctResource);
        
        Task task = new Task();
        task.setAssigneeOid(null);
        
        task.setMessage(message);
        task.setSender(this.sender);
        task.setSentDate(new Date());
        task.setSubject(subject);
        task.setPrivilege(privilege);
        task.setMessageType(messageType);
        task.setResourceOid(wctResource.getOid());
        task.setResourceType(wctResource.getResourceType());
        
        try {
            Agency agency = null;
            wctResource = populateOwnerAgencyOfResource(wctResource);
            if (wctResource instanceof UserInTrayResource) {
                agency = ((UserInTrayResource)wctResource).getOwningUser().getAgency();
            } else if (wctResource instanceof AgencyInTrayResource) {
                agency = ((AgencyInTrayResource)wctResource).getOwningAgency();
            } else {
                throw new WCTRuntimeException("Unknown instance type "+wctResource.getClass().getName());
            }
            
            task.setAgency(agency);
            
            inTrayDAO.saveOrUpdate(task);
            
            send(privilege, task);
        }
        catch (Throwable e) {
            log.error("Failed to create Task for "+wctResource.getResourceName(),e);
        }
    }

    public void deleteNotification(Long notificationOid) {
        Notification notify = (Notification)inTrayDAO.load( Notification.class, notificationOid);
        inTrayDAO.delete(notify);
    }
    
    public void deleteAllNotifications(Long userOid) {
    	inTrayDAO.deleteNotificationsByUser(userOid);
    }

    public void deleteTask(Long taskOid) {
        Task task = (Task)inTrayDAO.load(Task.class, taskOid);
        User currentUser = AuthUtil.getRemoteUserObject();              
        if (task.getAssigneeOid() != null && !task.getAssigneeOid().equals(currentUser.getOid())) {
        	throw new NotOwnerRuntimeException(currentUser.getNiceName() + " is not the owner of the task " + task.getOid() + " so cannot delete it.");
        }        
        inTrayDAO.delete(task);       
    }
    
    /** @see InTrayManager#deleteTask(Long, String, String). */
	public void deleteTask(Long aResourceOid, String aResourceType, String aTaskType) {
		Task toDelete = inTrayDAO.getTask(aResourceOid, aResourceType, aTaskType);
		if (toDelete != null) {
			inTrayDAO.delete(toDelete);
		}
	}

    /** @see InTrayManager#deleteTasks(Long, String, String). */
	public void deleteTasks(Long aResourceOid, String aResourceType, String aTaskType) {
		List<Task> tasksToDelete = inTrayDAO.getTasks(aResourceOid, aResourceType, aTaskType);
		if ( tasksToDelete != null ) {
			for (Iterator<Task> it = tasksToDelete.iterator(); it.hasNext(); ) {
				Task toDelete = (Task)it.next();
				inTrayDAO.delete(toDelete);
			}
		}
	}

	public Notification getNotification(Long notificationOid) {
        Notification notify = (Notification)inTrayDAO.load( Notification.class, notificationOid);
        User loggedInUser = AuthUtil.getRemoteUserObject();
        String recipientName = "";
        if (loggedInUser.getOid() == notify.getRecipientOid()) {
            //The Notification should always be for the logged in user 
            //but we should just check to make sure
            recipientName = loggedInUser.getNiceName();
        } else {
            //The Notification is for another user that we can see
            User specifiedUser = userRoleDAO.getUserByOid(notify.getRecipientOid());
            recipientName = specifiedUser.getNiceName();
        }
        notify.setRecipientName(recipientName);
        return notify; 
    }

    public Task getTask(Long taskOid) {
        Task task = (Task)inTrayDAO.load(Task.class, taskOid);
        Long assigneeOid = task.getAssigneeOid();
        User loggedInUser = AuthUtil.getRemoteUserObject();
        String owner = "Unclaimed";
        if (assigneeOid != null) {
            if (loggedInUser.getOid() == task.getAssigneeOid()) {
                //The Notification should always be for the logged in user 
                //but we should just check to make sure
                owner = loggedInUser.getNiceName();
            } else {
                //The Notification is for another user that we can see
                User specifiedUser = userRoleDAO.getUserByOid(task.getAssigneeOid());
                owner = specifiedUser.getNiceName();
            }
        }
        task.setOwner(owner);
        return task;
    }

    public void setUserRoleDAO(UserRoleDAO userRoleDAO) {
        this.userRoleDAO = userRoleDAO;
    }

    /**
     * @return Returns the sender.
     */
    public String getSender() {
        return sender;
    }

    /**
     * @param sender The sender to set.
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @param messageSource The messageSource to set.
     */
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    /**
     * sets the base URL of the web curator system. This is the URL base that will be pre-appended
     * to the automatically generated URL's for Notifications and Tasks 
     * @param wctBaseUrl the wct Core machines base URL, e.g. https://www.wct.org/wct/
     */
    public void setWctBaseUrl(String wctBaseUrl) {
        this.wctBaseUrl = wctBaseUrl;
    }
    
    @SuppressWarnings("unchecked")
	public Pagination getTasks(User user, int pageNum, int pageSize) {
        return inTrayDAO.getTasks(user, userRoleDAO.getUserPrivileges(user.getUsername()), pageNum, pageSize);
    }
    
    public void claimTask(User user, Long taskOid) {
        Task task = (Task)inTrayDAO.load(Task.class,taskOid);
        inTrayDAO.claimTask(user,task);
        audit.audit(Task.class.getName(), taskOid, Auditor.ACTION_CLAIM_TASK, "The task " + task.getSubject() + " has been un-claimed by " + user.getNiceName());
    }
        
    public void unclaimTask(User user, Long taskOid) {
        Task task = (Task)inTrayDAO.load(Task.class,taskOid);
        inTrayDAO.unclaimTask(user,task);
        audit.audit(Task.class.getName(), taskOid, Auditor.ACTION_UNCLAIM_TASK, "The task " + task.getSubject() + " has been claimed by " + user.getNiceName());
    }
        
    /**
     * looks up the Subject text for this type of Message
     * @param messageType the MessageType as defined in the MessageType class
     * @param wctResource the wctResource effected. The wctResource must implement the InTrayResource Interface.
     * @return the subject text for the Notification or Task
     */
    private String lookupSubject(String messageType, InTrayResource wctResource) {
        if (MessageType.TARGET_INSTANCE_COMPLETE.equals(messageType)) {
            return messageSource.getMessage("subject.ti.complete", new Object[] { ((TargetInstance) wctResource).getTarget().getName(), wctResource.getResourceName() }, Locale.getDefault());
        } else if (MessageType.TARGET_INSTANCE_QUEUED.equals(messageType)) {
            return messageSource.getMessage("subject.ti.queued", new Object[] { wctResource.getResourceName() }, Locale.getDefault());
        } else if (MessageType.TARGET_INSTANCE_RESCHEDULED.equals(messageType)) {
            return messageSource.getMessage("subject.ti.rescheduled", new Object[] { wctResource.getResourceName() }, Locale.getDefault());
        } else if (MessageType.TARGET_INSTANCE_PROCESSING_ERROR.equals(messageType)) {
            return messageSource.getMessage("subject.ti.error", new Object[] { wctResource.getResourceName() }, Locale.getDefault());
        } else if (MessageType.TARGET_INSTANCE_ENDORSE.equals(messageType)) {
            return messageSource.getMessage("subject.ti.endorse", new Object[] { ((TargetInstance) wctResource).getTarget().getName(), wctResource.getResourceName() }, Locale.getDefault());     
        } else if (MessageType.TARGET_INSTANCE_ARCHIVE.equals(messageType)) {
            return messageSource.getMessage("subject.ti.archive", new Object[] { ((TargetInstance) wctResource).getTarget().getName(), wctResource.getResourceName() }, Locale.getDefault());     
        } else if (MessageType.TARGET_SCHEDULE_ADDED.equals(messageType)) {
        	return messageSource.getMessage("subject.target.schedule_added", new Object[] { wctResource.getResourceName() }, Locale.getDefault());
        } else if (MessageType.TASK_SEEK_PERMISSON.equals(messageType)) {
        	return messageSource.getMessage("subject.permission.seek_approval", new Object[] { wctResource.getResourceName() }, Locale.getDefault());
        } else if (MessageType.TASK_APPROVE_TARGET.equals(messageType)) {
        	return messageSource.getMessage("subject.target.approve", new Object[] { wctResource.getResourceName() }, Locale.getDefault());
        } else if (MessageType.DELEGATE_TARGET.equals(messageType)) {
            return messageSource.getMessage("subject.target.delegate", new Object[] { wctResource.getResourceName() }, Locale.getDefault());
        } else if (MessageType.TRANSFER_TARGET.equals(messageType)) { 
        	return messageSource.getMessage("subject.target.transfer", new Object[] { wctResource.getResourceName(), ((Target) wctResource).getOwner().getFullName() }, Locale.getDefault());
        } else if (MessageType.NOTIFICATION_PERMISSION_APPROVED.equals(messageType)) {
        	return messageSource.getMessage("subject.permission.approved", new Object[] { wctResource.getResourceName() }, Locale.getDefault());
        } else if (MessageType.NOTIFICATION_PERMISSION_DENIED.equals(messageType)) {
        	return messageSource.getMessage("subject.permission.denied", new Object[] { wctResource.getResourceName() }, Locale.getDefault());
        } else if (MessageType.NOTIFICATION_ARCHIVE_SUCCESS.equals(messageType)) { 
        	return messageSource.getMessage("subject.archived.success", new Object[] { ((TargetInstance) wctResource).getTarget().getName(), wctResource.getResourceName() }, Locale.getDefault());
        } else if (MessageType.NOTIFICATION_AQA_COMPLETE.equals(messageType)) { 
        	return messageSource.getMessage("subject.aqa.complete", new Object[] { ((HarvestResult) wctResource).getTargetInstance().getTarget().getName(), ((HarvestResult) wctResource).getTargetInstance().getResourceName() }, Locale.getDefault());
        } else {
            throw new WCTRuntimeException("MessageType "+messageType+" is invalid.");           
        }
    }
    
    /**
     * looks up the Message text for this type of Message
     * @param messageType the MessageType as defined in the MessageType class
     * @param wctResource the wctResource effected. The wctResource must implement the InTrayResource Interface.
     * @return the message text for the Notification or Task
     */
    private String lookupMessage(String messageType, InTrayResource wctResource) {
        String url = generateURL(messageType, wctResource);
        if (MessageType.TARGET_INSTANCE_COMPLETE.equals(messageType)) {
            return messageSource.getMessage("message.ti.complete", new Object[] {((TargetInstance) wctResource).getTarget().getName(), wctResource.getResourceName(), lookupLink(messageType, wctResource) }, Locale.getDefault());
        } else if (MessageType.TARGET_INSTANCE_QUEUED.equals(messageType)) {
            return messageSource.getMessage("message.ti.queued", new Object[] { wctResource.getResourceName(),url }, Locale.getDefault());
        } else if (MessageType.TARGET_INSTANCE_RESCHEDULED.equals(messageType)) {
            return messageSource.getMessage("message.ti.rescheduled", new Object[] { wctResource.getResourceName(), url }, Locale.getDefault());
        } else if (MessageType.TARGET_INSTANCE_PROCESSING_ERROR.equals(messageType)) {
            return messageSource.getMessage("message.ti.error", new Object[] { wctResource.getResourceName(), url }, Locale.getDefault());
        } else if (MessageType.TARGET_INSTANCE_ENDORSE.equals(messageType)) {
            return messageSource.getMessage("message.ti.endorse", new Object[] { ((TargetInstance) wctResource).getTarget().getName(), wctResource.getResourceName(), lookupLink(messageType, wctResource) }, Locale.getDefault());     
        } else if (MessageType.TARGET_INSTANCE_ARCHIVE.equals(messageType)) {
            return messageSource.getMessage("message.ti.archive", new Object[] { ((TargetInstance) wctResource).getTarget().getName(), wctResource.getResourceName(), lookupLink(messageType, wctResource)  }, Locale.getDefault());
        } else if (MessageType.TARGET_SCHEDULE_ADDED.equals(messageType)) {
          	return messageSource.getMessage("message.target.schedule_added", new Object[] { wctResource.getResourceName(), url }, Locale.getDefault());
        } else if (MessageType.TASK_SEEK_PERMISSON.equals(messageType)) {
        	return messageSource.getMessage("message.permission.seek_approval", new Object[] { wctResource.getResourceName(), url }, Locale.getDefault());
        } else if (MessageType.TASK_APPROVE_TARGET.equals(messageType)) {
        	return messageSource.getMessage("message.target.approve", new Object[] { wctResource.getResourceName(), url }, Locale.getDefault());
        } else if (MessageType.DELEGATE_TARGET.equals(messageType)) {
            return messageSource.getMessage("message.target.delegate", new Object[] { wctResource.getResourceName(), url }, Locale.getDefault());
        } else if (MessageType.TRANSFER_TARGET.equals(messageType)) { 
        	return messageSource.getMessage("message.target.transfer", new Object[] { wctResource.getResourceName(), ((Target) wctResource).getOwner().getFullName(), url }, Locale.getDefault());            
        } else if (MessageType.NOTIFICATION_PERMISSION_APPROVED.equals(messageType)) {
        	return messageSource.getMessage("message.permission.approved", new Object[] { wctResource.getResourceName(), url }, Locale.getDefault());
        } else if (MessageType.NOTIFICATION_PERMISSION_DENIED.equals(messageType)) {
        	return messageSource.getMessage("message.permission.denied", new Object[] { wctResource.getResourceName(), url }, Locale.getDefault());
        } else if (MessageType.NOTIFICATION_ARCHIVE_SUCCESS.equals(messageType)) { 
        	return messageSource.getMessage("message.archived.success", new Object[] { ((TargetInstance) wctResource).getTarget().getName(), wctResource.getResourceName(), ((TargetInstance) wctResource).getArchiveIdentifier(), url }, Locale.getDefault());
        } else if (MessageType.NOTIFICATION_AQA_COMPLETE.equals(messageType)) { 
        	return messageSource.getMessage("message.aqa.complete", new Object[] { ((HarvestResult) wctResource).getTargetInstance().getTarget().getName(), wctResource.getResourceName(), generateURLBlankTarget(messageType, wctResource) }, Locale.getDefault());
        } else {
            throw new WCTRuntimeException("MessageType "+messageType+" is invalid.");           
        }
    }
    
    private String generateURL(String messageType, InTrayResource wctResource) {
        String resourceName = wctResource.getResourceName();
        StringBuffer url = new StringBuffer();
        
        url.append("<a href=\""+lookupLink(messageType, wctResource)+"\">");
        url.append(resourceName);
        url.append("</a>");
        
        
        return url.toString();
    }
    
    private String generateURLBlankTarget(String messageType, InTrayResource wctResource) {
        String resourceName = wctResource.getResourceName();
        StringBuffer url = new StringBuffer();
        
        url.append("<a href=\""+lookupLink(messageType, wctResource)+"\" target=\"_blank\">");
        url.append(resourceName);
        url.append("</a>");
        
        
        return url.toString();
    }
    
    
    private String lookupLink(InTrayResource wctResource, boolean editMode) { 
    	if(wctResource instanceof TargetInstance) {
    		if(editMode) { 
    			return wctBaseUrl + Constants.CNTRL_TI+"?"+TargetInstanceCommand.PARAM_OID+"="+wctResource.getOid()+"&cmd=edit";
    		}
    		else {
    			return wctBaseUrl + Constants.CNTRL_TI+"?"+TargetInstanceCommand.PARAM_OID+"="+wctResource.getOid();
    		}
    	}
    	else {
    		return "";
    	}
    }
    
    private String lookupLink(String messageType, InTrayResource wctResource) {
    	return lookupLink(messageType, wctResource.getResourceType(), wctResource.getOid(), wctResource);
    }
    
    private String lookupLink(String messageType, String resourceType, Long oid, InTrayResource wctResource) {
        if (TargetInstance.class.getName().equals(resourceType)) {
            //Create TargetInstance hyperlink
        	if (MessageType.TARGET_INSTANCE_QUEUED.equals(messageType)) {
                return wctBaseUrl + Constants.CNTRL_TI_QUEUE+"?"+TargetInstanceCommand.PARAM_OID+"="+oid;
            } else {
                return wctBaseUrl + Constants.CNTRL_TI+"?"+TargetInstanceCommand.PARAM_OID+"="+oid+"&cmd=edit";
            }
        } 
        if(Target.class.getName().equals(resourceType)) {
        	if (MessageType.TARGET_SCHEDULE_ADDED.equals(messageType)) {
        		return wctBaseUrl + Constants.CNTRL_TARGET+"?"+ TargetDefaultCommand.PARAM_OID +"="+oid;
        	}
        	if (MessageType.TASK_APPROVE_TARGET.equals(messageType) || MessageType.DELEGATE_TARGET.equals(messageType)) {
        		return wctBaseUrl + Constants.CNTRL_TARGET+"?"+ TargetDefaultCommand.PARAM_OID + "=" + oid + "&mode=" + TargetDefaultCommand.MODE_EDIT;
        	}
        }
        
        if(Permission.class.getName().equals(resourceType)) {
        	if( MessageType.TASK_SEEK_PERMISSON.equals(messageType) ||
        		MessageType.NOTIFICATION_PERMISSION_APPROVED.equals(messageType) ||
        		MessageType.NOTIFICATION_PERMISSION_DENIED.equals(messageType)) {
        		long siteOid = ((Permission) wctResource).getSite().getOid();
        		return wctBaseUrl + Constants.CNTRL_SITE + "?" + DefaultSiteCommand.PARAM_SITE_OID +"=" + siteOid + "&" + DefaultSiteCommand.PARAM_EDIT_MODE + "=true"; 
        	}
        }
        
    	if(wctResource instanceof HarvestResult)
    	{
    		HarvestResult result = (HarvestResult)wctResource;
			return wctBaseUrl + Constants.CNTRL_AQA+"?"+LogReaderCommand.PARAM_OID+"="+result.getTargetInstance().getOid()+"&"+LogReaderCommand.PARAM_LOGFILE+"=aqa-report("+result.getHarvestNumber()+").xml";
    	}
        
        return "";
    }
    
    /**
     * Identifies if the User should be informed of the Notification via email.
     * If the User is to be notified by email, then the email will be sent to the users email address.
     * @param effectedUser the User effected by the Notification
     * @param notify the Notification to send
     */
    private void send(UserDTO effectedUser, Notification notify) {
        if (effectedUser.isNotificationsByEmail()) {
            //This user needs to be notified by email as well
            try {
                mailServer.sendHTML(convertNotificationToMail(notify, effectedUser.getEmail()));
            } catch (MessagingException e) {
                log.error("MailServer failure occurred during email of Notification with message "+e.getMessage());
            }
        }
    }
    
    private void send(String privilege, Task task) {
        List<UserDTO> userDTOs = userRoleDAO.getUserDTOsByPrivilege(privilege, task.getAgency().getOid());
        for (UserDTO user: userDTOs) {
            if (user.isTasksByEmail()) {
                //This user needs to be notified by email as well
                try {
                    mailServer.sendHTML(convertTaskToMail(task, user.getEmail()));
                } catch (MessagingException e) {
                    log.error("MailServer failure occurred during email of Task with message "+e.getMessage());
                }
            }
        }
    }
    
    private InTrayResource populateOwnerAgencyOfResource(InTrayResource wctResource) {
        
        if (wctResource instanceof UserInTrayResource || wctResource instanceof AgencyInTrayResource) {
            wctResource = inTrayDAO.populateOwner(wctResource);            
        } else {
            throw new WCTRuntimeException("InTrayResource of unknown instance type "+wctResource.getClass().getName());
        }
        
        return wctResource;
    }
    
    /**
     * converts a Notification Object into something that is mailable.
     * @param notify the Notification Object
     * @param emailAddress the email address of the user to send this to 
     * @return a Mailable object appropriate for the MailServer
     */
    private Mailable convertNotificationToMail(Notification notify, String emailAddress) {
        Mailable email = new Mailable();
        email.setMessage(notify.getMessage());
        email.setRecipients(emailAddress);
        email.setSender(notify.getSender());
        email.setSubject(notify.getSubject());
        
        return email;
    }
    
    /**
     * converts a Task Object into something that is mailable.
     * @param task the Task Object
     * @param emailAddress the email address of the user to send this to 
     * @return a Mailable object appropriate for the MailServer
     */
    private Mailable convertTaskToMail(Task task, String emailAddress) {
        Mailable email = new Mailable();
        email.setMessage(task.getMessage());
        email.setRecipients(emailAddress);
        email.setSender(task.getSender());
        email.setSubject(task.getSubject());
        
        return email;
    }

	/**
	 * @param audit the audit to set
	 */
	public void setAudit(Auditor audit) {
		this.audit = audit;
	}

	@Override
	public void deleteAllTasks() {
    	inTrayDAO.deleteAllTasks();
	}
}
