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

/**
 * Defines the MessageTypes for the In-Tray Manager. A MessageType specifies what message text
 * to create when building a Notification or Task object.
 * @author bprice
 */
public interface MessageType {
    public static final String TARGET_INSTANCE_COMPLETE = "tiComplete";
    public static final String TARGET_INSTANCE_QUEUED = "tiQueued";
    public static final String TARGET_INSTANCE_RESCHEDULED = "tiRescheduled";
    public static final String TARGET_INSTANCE_PROCESSING_ERROR = "tiProcessError";
    public static final String NOTICE_OF_PERMISSION_CONFIRMATION = "permConfirm";
    public static final String DELEGATE_CONFIRMATION_FOR_PERMISSION = "delegatePermission";
    public static final String DELEGATE_TARGET = "delegateTarget";
    public static final String TRANSFER_TARGET = "transferTarget";
    public static final String TARGET_INSTANCE_ENDORSE = "endorseHarvest";
    public static final String TARGET_INSTANCE_ARCHIVE = "archiveHarvest";
    public static final String TARGET_SCHEDULE_ADDED = "scheduleAdded";
    public static final String TASK_SEEK_PERMISSON = "requestApproval";
    public static final String TASK_APPROVE_TARGET = "approveTarget";
    
    /** Message type for archive successful. */
    public static final String NOTIFICATION_ARCHIVE_SUCCESS = "archiveSuccess";
    /** Message type for AQA complete. */
	public static final String NOTIFICATION_AQA_COMPLETE = "notifyAQAComplete";
    
    /** Notification to users who own targets associated with a permission when that permission gets approved */
    public static final String NOTIFICATION_PERMISSION_APPROVED = "permissionApproved";
    /** Notification to users who own targets associated with a permission when that permission gets denied */
    public static final String NOTIFICATION_PERMISSION_DENIED = "permissionDenied";
	/** Notification category for Warnings */
	public static final int CATEGORY_HARVESTER_WARNING = 0;
	/** Notification category for non-harvester-warning messages. */
	public static final int CATEGORY_MISC = 1;

}
