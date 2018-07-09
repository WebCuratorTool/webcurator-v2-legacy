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
package org.webcurator.ui.common;

/**
 * Constants used by the WCT UI.
 * @author nwaight
 */
public interface Constants {
    // Global constants
    /** Key to the command object. */
    public static final String GBL_CMD_DATA = "command";
    /** default time pattern. */
    public static final String GBL_DFLT_TIME_PTRN = "HH:mm:ss";
    /** Key to the errors object. */
    public static final String GBL_ERRORS = "org.springframework.validation.BindException.command";
    /** Key to the display messages. */
    public static final String GBL_MESSAGES = "page_message";       
    /** Default page size. */
    int GBL_PAGE_SIZE = 10;
    
    /** Default page size. */
    String GBL_DEFAULT_PAGE_SIZE = "10";
    /** name of cookie holding user selected page size. */
    String PAGE_SIZE_COOKIE_NAME = "wctCurrentPageSize";
    /** path name for cookie holding user selected page size. */
    String PAGE_SIZE_COOKIE_DOMAIN_PATH = "/";
    /** default age of cookie holding user selected page size. */
    int DEFAULT_PAGE_SIZE_COOKIE_AGE = (60*60*24*365); // 1 year
    
    /** Key to acess the session edit mode. */
    public static final String GBL_SESS_EDIT_MODE = "gblEditMode";
    /** Key to access the session "can edit?" variable. */
    public static final String GBL_SESS_CAN_EDIT = "gblCanEdit";
    
    /** Annotation requests via Ajax **/
    public static final String AJAX_REQUEST_TYPE = "ajax_request_type";
    public static final String AJAX_REQUEST_FOR_TI_ANNOTATIONS = "ajax_ti_annotations";
    public static final String AJAX_REQUEST_FOR_TARGET_ANNOTATIONS = "ajax_target_annotations";
    
    // View names.
    /** the name of the manage harvest agents view. */
    String VIEW_MNG_AGENTS = "ManageHarvestAgents";
    /** The name of the view agent details view. */
    String VIEW_AGENT = "HarvestAgentDetail";
    /** the name of the manage bandwidth restrictions view. */
    String VIEW_MNG_BANDWIDTH = "ManageBandwidth";
    /** the name of the edit bandwidth restrictions view. */
    String VIEW_EDIT_BANDWIDTH = "EditBandwidth";   
    /** the name of the reset password view */
    String VIEW_RESET_PWD = "ResetPassword";
    /** The generic message screen */
    String VIEW_MESSAGE = "MessageView";
    /** The home page */
    String VIEW_HOME = "HomeView";
    /** The home page when the QA Module is enabled */
    String VIEW_QA_HOME = "QaHomeView";
    /** The target instance queue page */
    String VIEW_TARGET_INSTANCE_QUEUE = "TargetInstanceQueue";
    /** The target instance QA summary page */
    String VIEW_TARGET_INSTANCE_QA_SUMMARY = "TargetInstanceQaSummary";
    /** The target instance QA queue page */
    String VIEW_TARGET_INSTANCE_QA_QUEUE = "TargetInstanceQaQueue";
    /** The target annotation history (via Ajax) page */
    String VIEW_TARGET_ANNOTATION_HISTORY = "TargetAnnotationHistory";
    /** The Management tab **/
    String VIEW_MANAGEMENT = "management";
    /** The Management tab (when the QA Module is enabled) **/
    String VIEW_QA_MANAGEMENT = "Qamanagement";
    /** The target instance annotation history (via Ajax) page */
    String VIEW_TI_ANNOTATION_HISTORY = "TIAnnotationHistory";
    /** The target instance page */
    String VIEW_TARGET_INSTANCE = "target-instance";
    /** The logout page */
    String VIEW_LOGOUT = "Logout";
    /** The harvest target instance NOW page */
    String VIEW_HARVEST_NOW = "HarvestNow";
    /** The log reader page */
    String VIEW_LOG_READER = "LogReader";
    /** The hop path reader page */
    String HOP_PATH__READER = "HopPathReader";
    /** The AQA reader page */
    String VIEW_AQA_READER = "AQAReader";
    /** The password changed success view */
    String VIEW_PASSWORD_RESET_SUCCESS = "ResetPasswordSuccess";
    /** The site authorising agencies view */
    String VIEW_SITE_AGENCIES = "site-agencies";
    /** The site permissions view */
    String VIEW_SITE_PERMISSIONS = "site-permissions";
    /** The edit schedules view */
    String VIEW_EDIT_SCHEDULE = "schedule-edit";
    
        
    // Controller names.
    /** the name of the controller for managing harvest agents. */
    String CNTRL_MNG_AGENTS = "curator/agent/harvest-agent.html";
    /** the name of the controller for managing bandwidth restrictions. */
    String CNTRL_MNG_BANDWIDTH = "curator/agent/bandwidth-restrictions.html";
    /** the name of the controller for managing password resets as used by the Filter */
    String CNTRL_RESET_PWD = "curator/credentials/reset-password.html";
    /** the administration based controller for changing password, not to be confused with the RESET_PWD used by the Filter */ 
    String CNTRL_CHANGE_PWD = "curator/admin/change-password.html";
    /** Home page controller */
    String CNTRL_HOME = "curator/home.html";
    /** Target Instance Queue controller */
    String CNTRL_TI_QUEUE = "curator/target/queue.html";
    /** Target Instance Queue controller */
    String CNTRL_TI = "curator/target/target-instance.html"; 
    /** A Logout controller */
    String CNTRL_LOGOUT = "curator/logout.html";
    /** The controller for managing user roles */
    String CNTRL_ROLES = "curator/admin/role.html";
    /** A harvest now controller */
    String CNTRL_HARVEST_NOW = "curator/target/harvest-now.html";
    /** The controller for managing users */
    String CNTRL_USER = "curator/admin/user.html";
    /** The controller for creating new users */
    String CNTRL_CREATE_USER = "curator/admin/create-user.html";
    /** The controller for associating users to roles */
    String CNTRL_ASSOCIATE_USERROLE = "curator/admin/associate-userroles.html";
    /** The controller for the agency administration */
    String CNTRL_AGENCY = "curator/admin/agency.html";
    /** The controller for rejection reason administration */
    String CNTRL_REASONS = "curator/admin/rejreason.html";
    /** The controller for creating new rejection reasons */
    String CNTRL_CREATE_REASON = "curator/admin/create-rejreason.html";
    /** The controller for QA Indicator administration */
    String CNTRL_QA_INDICATORS = "curator/admin/qaindicators.html";
    /** The controller for creating new QA Indicators */
    String CNTRL_CREATE_QA_INDICATOR = "curator/admin/create-qaindicator.html";
    /** The controller for QA Indicator administration */
    String CNTRL_FLAGS = "curator/admin/flags.html";
    /** The controller for creating new QA Indicators */
    String CNTRL_CREATE_FLAG = "curator/admin/create-flag.html";
    /** The controller for the log viewer */
    String CNTRL_LOG_READER = "curator/target/log-viewer.html";
    /** The controller for the hop path viewer */
    String CNTRL_HOP_PATH_READER = "curator/target/show-hop-path.html";
    /** The controller for the aqa viewer */
    String CNTRL_AQA_READER = "curator/target/aqa-viewer.html";
    /** The controller for the create site */
    String CNTRL_ADD_SITE = "curator/site/site.html";
    /** The controller for the create site authorising agencies */
    String CNTRL_SITE_AGENCY = "curator/site/agencies.html";
    /** The controller for the create site permissions */
    String CNTRL_SITE_PERMISSIONS = "curator/site/permissions.html";
    /** The controller for the create site */
    String CNTRL_SEARCH_SITE = "curator/site/search.html";
    /** The controller for the site tabs. */
    String CNTRL_SITE = "curator/site/site.html";
    /** The controller for the In Tray */
    String CNTRL_INTRAY = "curator/intray/intray.html";
    /** The controller for the Management section */
    String CNTRL_MANAGEMENT = "curator/admin/management.html";
    /** The controller for managing Request Permission Templates */
    String CNTRL_PERMISSION_TEMPLATE = "curator/admin/templates.html";
    /** The controller for managing targets. */
    String CNTRL_TARGET = "curator/target/target.html";
    /** The controller for generating Permission request letters and emails */
    String CNTRL_GENERATE_TEMPLATE = "curator/site/generate.html";
    /** The aqa viewer controller */
    String CNTRL_AQA = "curator/target/aqa-viewer.html";
    
    //Global constants for the UI
    /** The parameter that holds the message text for the message view */
    String MESSAGE_TEXT = "messageText";
    
    //Privilege Delimiter character sequence 
    String PRIVILEGE_DELIMITER = "|";
    
    /** The global size for any annotation field */
    int ANNOTATION_COLS = 80;
    int ANNOTATION_ROWS = 6;

    /** The global size for the display change reason field */
    int DISPLAY_CHANGE_REASON_COLS = 60;
    int DISPLAY_CHANGE_REASON_ROWS = 3;
    
    /** informs the view that the QA module is enabled **/
    String ENABLE_QA_MODULE = "enableQaModule";
    
    /** global ids for the thumb-nail preview size **/
    String THUMBNAIL_WIDTH = "thumbnailWidth";
    String THUMBNAIL_HEIGHT = "thumbnailHeight";
}
