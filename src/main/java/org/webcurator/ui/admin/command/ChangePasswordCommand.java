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
package org.webcurator.ui.admin.command;

/**
 * The command object for changing passwords.
 * @author bprice
 */
public class ChangePasswordCommand {
    /** The name of the save action. */
    public static final String ACTION_SAVE = "save";
    
    /** the constant name of the confirm password field. */
    public static final String PARAM_CONFIRM_PWD = "confirmPwd";
    /** the constant name of the new password field. */
    public static final String PARAM_NEW_PWD = "newPwd";
    /** the constant name of the user oid field. */
    public static final String PARAM_USER_OID = "userOid";
    /** the constant name of the action field. */
    public static final String PARAM_ACTION = "action";
    
    /** the user oid field. */
    private Long userOid;
    /** the new password field. */
    private String newPwd;
    /** the confirm password field. */
    private String confirmPwd;
    /** the action field. */
    private String action;
    
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return the confirmPwd
	 */
	public String getConfirmPwd() {
		return confirmPwd;
	}
	/**
	 * @param confirmPwd the confirmPwd to set
	 */
	public void setConfirmPwd(String confirmPwd) {
		this.confirmPwd = confirmPwd;
	}
	/**
	 * @return the newPwd
	 */
	public String getNewPwd() {
		return newPwd;
	}
	/**
	 * @param newPwd the newPwd to set
	 */
	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}
	/**
	 * @return the userOid
	 */
	public Long getUserOid() {
		return userOid;
	}
	/**
	 * @param userOid the userOid to set
	 */
	public void setUserOid(Long userOid) {
		this.userOid = userOid;
	}
}
