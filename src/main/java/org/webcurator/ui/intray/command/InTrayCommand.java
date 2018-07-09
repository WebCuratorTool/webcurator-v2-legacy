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
package org.webcurator.ui.intray.command;

/**
 * The command object for the intray.
 * 
 * @author bprice
 */
public class InTrayCommand {
	public static final String ACTION_VIEW = "view";
	public static final String ACTION_DELETE_NOTIFICATION = "deleteNotification";
	public static final String ACTION_DELETE_ALL_NOTIFICATIONS = "deleteAllNotifications";
	public static final String ACTION_VIEW_NOTIFICATION = "viewNotification";

	public static final String ACTION_DELETE_TASK = "deleteTask";
	public static final String ACTION_DELETE_ALL_TASKS = "deleteAllTasks";
	public static final String ACTION_VIEW_TASK = "viewTask";
	public static final String ACTION_CLAIM_TASK = "claimTask";
	public static final String ACTION_UNCLAIM_TASK = "unclaimTask";
	public static final String ACTION_NEXT = "next";
	public static final String ACTION_PREVIOUS = "previous";

	public static final String MDL_NOTIFICATIONS = "notifications";
	public static final String MDL_TASKS = "tasks";

	public static final String MDL_NOTIFICATION = "notification";
	public static final String MDL_TASK = "task";

	public static final String MDL_CURRENT_USER = "user";

	public static final String MDL_SHOW_TASKS = "showTasks";

	public static final String PARAM_ACTION = "action";
	public static final String PARAM_NOTIFICATION_OID = "notificationOid";
	public static final String PARAM_TASK_OID = "taskOid";
	public static final String PARAM_NOTIFICATION_PAGE = "notificationPage";
	public static final String PARAM_TASK_PAGE = "taskPage";

	private String action;
	private Long notificationOid;
	private Long taskOid;
	private int notificationPage = 0;
	private int taskPage = 0;
	private String selectedPageSize;
	private Boolean showTasks;

	public InTrayCommand() {
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Long getNotificationOid() {
		return notificationOid;
	}

	public void setNotificationOid(Long notificationOid) {
		this.notificationOid = notificationOid;
	}

	public Long getTaskOid() {
		return taskOid;
	}

	public void setTaskOid(Long taskOid) {
		this.taskOid = taskOid;
	}

	public int getNotificationPage() {
		return notificationPage;
	}

	public void setNotificationPage(int notificationPage) {
		this.notificationPage = notificationPage;
	}

	/**
	 * @return the selectedPageSize
	 */
	public String getSelectedPageSize() {
		return selectedPageSize;
	}

	/**
	 * @param selectedPageSize
	 *            the selectedPageSize to set
	 */
	public void setSelectedPageSize(String selectedPageSize) {
		this.selectedPageSize = selectedPageSize;
	}

	public int getTaskPage() {
		return taskPage;
	}

	public void setTaskPage(int taskPage) {
		this.taskPage = taskPage;
	}

	public Boolean getShowTasks() {
		return showTasks;
	}

	public void setShowTasks(Boolean showTasks) {
		this.showTasks = showTasks;
	}

}
