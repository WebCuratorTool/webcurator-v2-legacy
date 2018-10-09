

set foreign_key_checks=0;

drop table if exists DB_WCT.PERMISSION;
drop table if exists DB_WCT.PERMISSION_EXCLUSION;
drop table if exists DB_WCT.PERMISSION_TEMPLATE;
drop table if exists DB_WCT.PERMISSION_URLPATTERN;
drop table if exists DB_WCT.PROFILE;
drop table if exists DB_WCT.PROFILE_BASIC_CREDENTIALS;
drop table if exists DB_WCT.PROFILE_CREDENTIALS;
drop table if exists DB_WCT.PROFILE_FORM_CREDENTIALS;
drop table if exists DB_WCT.PROFILE_OVERRIDES;
drop table if exists DB_WCT.REJECTION_REASON;
drop table if exists DB_WCT.TASK;
drop table if exists DB_WCT.AGENCY;
drop table if exists DB_WCT.ABSTRACT_TARGET;
drop table if exists DB_WCT.ANNOTATIONS;
drop table if exists DB_WCT.ARC_HARVEST_FILE;
drop table if exists DB_WCT.ARC_HARVEST_RESOURCE;
drop table if exists DB_WCT.ARC_HARVEST_RESULT;
drop table if exists DB_WCT.AUTHORISING_AGENT;
drop table if exists DB_WCT.BANDWIDTH_RESTRICTIONS;
drop table if exists DB_WCT.DUBLIN_CORE;
drop table if exists DB_WCT.GROUP_MEMBER;
drop table if exists DB_WCT.HARVEST_RESOURCE;
drop table if exists DB_WCT.HARVEST_RESULT;
drop table if exists DB_WCT.HARVEST_STATUS;
drop table if exists DB_WCT.HR_MODIFICATION_NOTE;
drop table if exists DB_WCT.NOTIFICATION;
drop table if exists DB_WCT.PO_EXCLUSION_URI;
drop table if exists DB_WCT.PO_INCLUSION_URI;
drop table if exists DB_WCT.ROLE_PRIVILEGE;
drop table if exists DB_WCT.SCHEDULE;
drop table if exists DB_WCT.SEED;
drop table if exists DB_WCT.SEED_HISTORY;
drop table if exists DB_WCT.SEED_PERMISSION;
drop table if exists DB_WCT.SIP_PART_ELEMENT;
drop table if exists DB_WCT.SITE;
drop table if exists DB_WCT.SITE_AUTH_AGENCY;
drop table if exists DB_WCT.TARGET;
drop table if exists DB_WCT.TARGET_GROUP;
drop table if exists DB_WCT.TARGET_INSTANCE;
drop table if exists DB_WCT.TARGET_INSTANCE_ORIG_SEED;
drop table if exists DB_WCT.URL_PATTERN;
drop table if exists DB_WCT.URL_PERMISSION_MAPPING;
drop table if exists DB_WCT.USER_ROLE;
drop table if exists DB_WCT.WCTAUDIT;
drop table if exists DB_WCT.WCTROLE;
drop table if exists DB_WCT.WCTUSER;
drop table if exists DB_WCT.WCT_LOGON_DURATION;
drop table if exists DB_WCT.ID_GENERATOR;
drop table if exists DB_WCT.HEATMAP_CONFIG;
drop view if exists DB_WCT.ABSTRACT_TARGET_SCHEDULE_VIEW;
drop view if exists DB_WCT.URL_PERMISSION_MAPPING_VIEW;
drop view if exists DB_WCT.ABSTRACT_TARGET_GROUPTYPE_VIEW;
drop table if exists DB_WCT.FLAG;
drop table if exists DB_WCT.INDICATOR_CRITERIA;
drop table if exists DB_WCT.INDICATOR_REPORT_LINE;
drop table if exists DB_WCT.INDICATOR;
drop table if exists DB_WCT.PO_H3_BLOCK_URL;
drop table if exists DB_WCT.PO_H3_INCLUDE_URL;

set foreign_key_checks=1;

create table DB_WCT.ABSTRACT_TARGET (AT_OID bigint not null, AT_DESC text, AT_NAME varchar(191), AT_OWNER_ID bigint, AT_PROF_OVERRIDE_OID bigint, AT_STATE integer, T_PROFILE_ID bigint, AT_OBJECT_TYPE integer, AT_CREATION_DATE TIMESTAMP NULL, AT_REFERENCE varchar(255), AT_PROFILE_NOTE varchar(255), AT_DUBLIN_CORE_OID bigint, AT_ACCESS_ZONE integer default 0 not null, AT_DISPLAY_TARGET bit default 1 not null, AT_DISPLAY_NOTE text, AT_DISPLAY_CHG_REASON text, AT_RR_OID bigint, AT_CRAWLS bigint, AT_REFERENCE_CRAWL_OID bigint, AT_AUTO_PRUNE boolean not null default false, AT_AUTO_DENOTE_REFERENCE_CRAWL boolean not null default false, AT_REQUEST_TO_ARCHIVISTS varchar(4000), primary key (AT_OID));
create table DB_WCT.AGENCY (AGC_OID bigint not null, AGC_NAME varchar(80) not null unique, AGC_ADDRESS varchar(255) not null, AGC_LOGO_URL varchar(255), AGC_URL varchar(255), AGC_EMAIL varchar(80), AGC_FAX varchar(20), AGC_PHONE varchar(20), AGC_SHOW_TASKS boolean not null default true, AGC_DEFAULT_DESC_TYPE varchar(50) default "", primary key (AGC_OID));
create table DB_WCT.ANNOTATIONS (AN_OID bigint not null, AN_DATE datetime not null, AN_NOTE text not null, AN_USER_OID bigint not null, AN_OBJ_OID bigint not null, AN_OBJ_TYPE text not null, AN_ALERTABLE bit not null default 0, primary key (AN_OID));
create table DB_WCT.ARC_HARVEST_FILE (AHF_OID bigint not null, AHF_COMPRESSED bit not null, AHF_NAME varchar(100) not null unique, AHF_ARC_HARVEST_RESULT_ID bigint, primary key (AHF_OID));
create table DB_WCT.ARC_HARVEST_RESOURCE (AHRC_HARVEST_RESOURCE_OID bigint not null, AHRC_RESOURCE_LENGTH bigint not null, AHRC_RESOURCE_OFFSET bigint not null, AHRC_ARC_FILE_NAME varchar(100) not null, AHRC_COMPRESSED_YN bit not null, primary key (AHRC_HARVEST_RESOURCE_OID));
create table DB_WCT.ARC_HARVEST_RESULT (AHRS_HARVEST_RESULT_OID bigint not null, primary key (AHRS_HARVEST_RESULT_OID));
create table DB_WCT.AUTHORISING_AGENT (AA_OID bigint not null, AA_NAME varchar(191) unique, AA_ADRESS text, AA_CONTACT varchar(255), AA_EMAIL varchar(255), AA_PHONE_NUMBER varchar(32), AA_DESC text, primary key (AA_OID));
create table DB_WCT.BANDWIDTH_RESTRICTIONS (BR_OID bigint not null, BR_BANDWIDTH bigint not null, BR_DAY varchar(9) not null, BR_END_TIME datetime not null, BR_START_TIME datetime not null, BR_OPTIMIZATION_ALLOWED boolean not null default false, primary key (BR_OID));
create table DB_WCT.DUBLIN_CORE (DC_OID bigint not null, DC_CONTRIBUTOR varchar(255), DC_COVERAGE varchar(255), DC_CREATOR varchar(255), DC_DESCRIPTION text, DC_FORMAT varchar(255), DC_IDENTIFIER varchar(255), DC_IDENTIFIER_ISBN varchar(13), DC_IDENTIFIER_ISSN varchar(9), DC_LANGUAGE varchar(255), DC_PUBLISHER varchar(255), DC_RELATION varchar(255), DC_SOURCE varchar(255), DC_SUBJECT text, DC_TITLE varchar(255), DC_TYPE varchar(50), primary key (DC_OID));
create table DB_WCT.GROUP_MEMBER (AT_OID bigint not null, GM_CHILD_ID bigint, GM_PARENT_ID bigint, primary key (AT_OID));
create table DB_WCT.HARVEST_RESOURCE (HRC_OID bigint not null, HRC_LENGTH bigint, HRC_NAME text not null, HRC_HARVEST_RESULT_OID bigint, HRC_STATUS_CODE integer not null, primary key (HRC_OID));
create table DB_WCT.HARVEST_RESULT (HR_OID bigint not null, HR_HARVEST_NO integer, HR_TARGET_INSTANCE_ID bigint, HR_PROVENANCE_NOTE text not null, HR_CREATED_DATE datetime, HR_CREATED_BY_ID bigint, HR_STATE integer, HR_DERIVED_FROM integer, HR_INDEX integer, HR_RR_OID bigint, primary key (HR_OID));
create table DB_WCT.HARVEST_STATUS (HS_OID bigint not null, HS_AVG_KB double precision, HS_AVG_URI double precision, HS_DATA_AMOUNT bigint, HS_ELAPSED_TIME bigint, HS_JOB_NAME text, HS_STATUS varchar(255), HS_URLS_DOWN bigint, HS_URLS_FAILED bigint, HS_ALERTS integer, HS_APP_VERSION varchar(255), HS_HRTX_VERSION varchar(255), primary key (HS_OID));
create table DB_WCT.HR_MODIFICATION_NOTE (HMN_HR_OID bigint not null, HMN_NOTE text, HMN_INDEX integer not null, primary key (HMN_HR_OID, HMN_INDEX));
create table DB_WCT.NOTIFICATION (NOT_OID bigint not null, NOT_MESSAGE text, NOT_USR_OID bigint not null, NOT_SENDER varchar(80) not null, NOT_SENT_DATE timestamp, NOT_SUBJECT varchar(255) not null, primary key (NOT_OID));
create table DB_WCT.PERMISSION (PE_OID bigint not null, PE_ACCESS_STATUS varchar(255), PE_APPROVED_YN bit, PE_AVAILABLE_YN bit, PE_COPYRIGHT_STATEMENT text, PE_COPYRIGHT_URL text, PE_CREATION_DATE TIMESTAMP NULL, PE_END_DATE TIMESTAMP NULL, PE_NOTES text, PE_OPEN_ACCESS_DATE TIMESTAMP NULL, PE_PERMISSION_GRANTED_DATE TIMESTAMP NULL, PE_PERMISSION_REQUESTED_DATE TIMESTAMP NULL, PE_SPECIAL_REQUIREMENTS text, PE_START_DATE TIMESTAMP NULL, PE_STATUS integer, PE_AUTH_AGENT_ID bigint, PE_SITE_ID bigint, PE_QUICK_PICK bit, PE_DISPLAY_NAME varchar(32), PE_OWNING_AGENCY_ID bigint, PE_FILE_REFERENCE varchar(255), primary key (PE_OID));
create table DB_WCT.PERMISSION_EXCLUSION (PEX_OID bigint not null, PEX_REASON varchar(255), PEX_URL text, PEX_PERMISSION_OID bigint, PEX_INDEX integer, primary key (PEX_OID));
create table DB_WCT.PERMISSION_TEMPLATE (PRT_OID bigint not null, PRT_AGC_OID bigint not null, PRT_TEMPLATE_TEXT text not null, PRT_TEMPLATE_NAME varchar(80) not null, PRT_TEMPLATE_TYPE varchar(40) not null, PRT_TEMPLATE_DESC varchar(255), PRT_TEMPLATE_SUBJECT varchar(255), PRT_TEMPLATE_OVERWRITE_FROM bit not null default 0, PRT_TEMPLATE_FROM varchar(255), PRT_TEMPLATE_CC text, PRT_TEMPLATE_BCC text, PRT_TEMPLATE_REPLY_TO varchar(255), primary key (PRT_OID));
create table DB_WCT.PERMISSION_URLPATTERN (PU_PERMISSION_ID bigint not null, PU_URLPATTERN_ID bigint not null, primary key (PU_URLPATTERN_ID, PU_PERMISSION_ID));
create table DB_WCT.PO_EXCLUSION_URI (PEU_PROF_OVER_OID bigint not null, PEU_FILTER varchar(255), PEU_IX integer not null, primary key (PEU_PROF_OVER_OID, PEU_IX));
create table DB_WCT.PO_INCLUSION_URI (PEU_PROF_OVER_OID bigint not null, PEU_FILTER varchar(255), PEU_IX integer not null, primary key (PEU_PROF_OVER_OID, PEU_IX));
create table DB_WCT.PROFILE (P_OID bigint not null, P_VERSION integer not null, P_DESC varchar(255), P_NAME varchar(255), P_PROFILE_STRING text, P_PROFILE_LEVEL integer, P_STATUS integer, P_DEFAULT bit, P_AGECNY_OID bigint, P_ORIG_OID bigint, P_HARVESTER_TYPE varchar(40) not null, P_DATA_LIMIT_UNIT varchar(40), P_MAX_FILE_SIZE_UNIT varchar(40), P_TIME_LIMIT_UNIT varchar(40), P_IMPORTED bit default 0 not null, primary key (P_OID));
create table DB_WCT.PROFILE_BASIC_CREDENTIALS (PBC_PC_OID bigint not null, PBC_REALM varchar(255), primary key (PBC_PC_OID));
create table DB_WCT.PROFILE_CREDENTIALS (PC_OID bigint not null, PC_DOMAIN varchar(255), PC_PASSWORD varchar(255), PC_USERNAME varchar(255), PC_PROFILE_OVERIDE_OID bigint, PC_INDEX integer, primary key (PC_OID));
create table DB_WCT.PROFILE_FORM_CREDENTIALS (PRC_PC_OID bigint not null, PFC_METHOD varchar(4), PFC_LOGIN_URI varchar(255), PFC_PASSWORD_FIELD varchar(255), PFC_USERNAME_FIELD varchar(255), primary key (PRC_PC_OID));
create table DB_WCT.PROFILE_OVERRIDES (PO_OID bigint not null, PO_EXCL_MIME_TYPES varchar(255), PO_MAX_BYES bigint, PO_MAX_DOCS bigint, PO_MAX_HOPS integer, PO_MAX_PATH_DEPTH integer, PO_MAX_TIME_SEC bigint, PO_ROBOTS_POLICY varchar(10), PO_H3_DOC_LIMIT integer, PO_H3_DATA_LIMIT double precision, PO_H3_DATA_LIMIT_UNIT varchar(40), PO_H3_TIME_LIMIT double precision, PO_H3_TIME_LIMIT_UNIT varchar(40), PO_H3_MAX_PATH_DEPTH integer, PO_H3_MAX_HOPS integer, PO_H3_MAX_TRANS_HOPS integer, PO_H3_IGNORE_ROBOTS varchar(10), PO_H3_IGNORE_COOKIES bit, PO_OR_CREDENTIALS bit, PO_OR_EXCL_MIME_TYPES bit, PO_OR_EXCLUSION_URI bit, PO_OR_INCLUSION_URI bit, PO_OR_MAX_BYTES bit, PO_OR_MAX_DOCS bit, PO_OR_MAX_HOPS bit, PO_OR_MAX_PATH_DEPTH bit, PO_OR_MAX_TIME_SEC bit, PO_OR_ROBOTS_POLICY bit, PO_H3_OR_DOC_LIMIT bit, PO_H3_OR_DATA_LIMIT bit, PO_H3_OR_TIME_LIMIT bit, PO_H3_OR_MAX_PATH_DEPTH bit, PO_H3_OR_MAX_HOPS bit, PO_H3_OR_MAX_TRANS_HOPS bit, PO_H3_OR_IGNORE_ROBOTS bit, PO_H3_OR_IGNORE_COOKIES bit, PO_H3_OR_BLOCK_URL bit, PO_H3_OR_INCL_URL bit, PO_H3_OR_RAW_PROFILE bit, PO_H3_RAW_PROFILE text, primary key (PO_OID));
CREATE TABLE DB_WCT.REJECTION_REASON (RR_OID bigint not null, RR_NAME varchar(100) not null, RR_AVAILABLE_FOR_TARGET bit default 0 not null, RR_AVAILABLE_FOR_TI bit default 0 not null, RR_AGC_OID bigint not null, primary key (RR_OID));
create table DB_WCT.ROLE_PRIVILEGE (PRV_OID bigint not null, PRV_CODE varchar(40) not null, PRV_ROLE_OID bigint, PRV_SCOPE integer not null, primary key (PRV_OID));
create table DB_WCT.SCHEDULE (S_OID bigint not null, S_CRON varchar(255) not null, S_START TIMESTAMP not null, S_END TIMESTAMP NULL, S_TARGET_ID bigint, S_TYPE integer not null, S_OWNER_OID bigint, S_NEXT_SCHEDULE_TIME TIMESTAMP NULL, S_ABSTRACT_TARGET_ID bigint, S_LAST_PROCESSED_DATE DATETIME DEFAULT '2001-01-01 00:00:00', primary key (S_OID));
create table DB_WCT.SEED (S_OID bigint not null, S_SEED text, S_TARGET_ID bigint, S_PRIMARY bit, primary key (S_OID));
create table DB_WCT.SEED_HISTORY (SH_OID bigint not null, SH_TI_OID bigint, SH_SEED text not null, SH_PRIMARY bit not null, primary key (SH_OID));
create table DB_WCT.SEED_PERMISSION (SP_SEED_ID bigint not null, SP_PERMISSION_ID bigint not null, primary key (SP_SEED_ID, SP_PERMISSION_ID));
create table DB_WCT.SIP_PART_ELEMENT (SPE_TARGET_INSTANCE_OID bigint not null, SPE_VALUE text, SPE_KEY varchar(191) not null, primary key (SPE_TARGET_INSTANCE_OID, SPE_KEY));
create table DB_WCT.SITE (ST_OID bigint not null, ST_TITLE varchar(191) not null unique, ST_DESC text, ST_LIBRARY_ORDER_NO varchar(32), ST_NOTES text, ST_PUBLISHED bit not null, ST_ACTIVE bit not null, ST_OWNING_AGENCY_ID bigint, ST_CREATION_DATE datetime, primary key (ST_OID));
create table DB_WCT.SITE_AUTH_AGENCY (SA_SITE_ID bigint not null, SA_AGENT_ID bigint not null, primary key (SA_SITE_ID, SA_AGENT_ID));
create table DB_WCT.TARGET (T_AT_OID bigint not null, T_RUN_ON_APPROVAL bit, T_EVALUATION_NOTE text, T_SELECTION_DATE TIMESTAMP NULL, T_SELECTION_NOTE text, T_SELECTION_TYPE varchar(255), T_HARVEST_TYPE varchar(255), T_USE_AQA bit not null default 0, T_ALLOW_OPTIMIZE boolean not null default false, primary key (T_AT_OID));
create table DB_WCT.TARGET_GROUP (TG_AT_OID bigint not null, TG_SIP_TYPE integer, TG_START_DATE DATE, TG_END_DATE DATE, TG_OWNERSHIP_METADATA varchar(255), TG_TYPE varchar(255), primary key (TG_AT_OID));
create table DB_WCT.TARGET_INSTANCE (TI_OID bigint not null, TI_VERSION integer not null, TI_SCHEDULE_ID bigint, TI_TARGET_ID bigint, TI_PRIORITY integer not null, TI_SCHEDULED_TIME TIMESTAMP not null, TI_STATE varchar(50) not null, TI_BANDWIDTH_PERCENT integer, TI_ALLOCATED_BANDWIDTH bigint, TI_START_TIME datetime, TI_OWNER_ID bigint, TI_DISPLAY_ORDER integer, TI_PROF_OVERRIDE_OID bigint, TI_PURGED bit not null, TI_ARCHIVE_ID varchar(40) unique, TI_REFERENCE varchar(255), TI_HARVEST_SERVER varchar(255), TI_DISPLAY_TARGET_INSTANCE bit default 1 not null, TI_DISPLAY_NOTE text, TI_FLAGGED bit default 0 not null, TI_PROFILE_ID bigint, TI_ARCHIVED_TIME datetime, TI_FIRST_FROM_TARGET bit not null default 0, TI_DISPLAY_CHG_REASON text, TI_USE_AQA bit not null default 0, TI_ALLOW_OPTIMIZE boolean not null default false, TI_FLAG_OID bigint, TI_RECOMMENDATION varchar(255), primary key (TI_OID));
create table DB_WCT.TARGET_INSTANCE_ORIG_SEED (TIOS_TI_OID bigint not null, TIOS_SEED varchar(1024));
create table DB_WCT.TASK (TSK_OID bigint not null, TSK_USR_OID bigint, TSK_MESSAGE text, TSK_SENDER varchar(80) not null, TSK_SENT_DATE timestamp, TSK_SUBJECT varchar(255) not null, TSK_PRIVILEGE varchar(40), TSK_AGC_OID bigint not null, TSK_MSG_TYPE varchar(40) not null, TSK_RESOURCE_OID bigint not null, TSK_RESOURCE_TYPE varchar(80) not null, primary key (TSK_OID));
create table DB_WCT.URL_PATTERN (UP_OID bigint not null, UP_PATTERN text, UP_SITE_ID bigint, primary key (UP_OID));
create table DB_WCT.URL_PERMISSION_MAPPING (UPM_OID bigint not null, UPM_PERMISSION_ID bigint, UPM_URL_PATTERN_ID bigint, UPM_DOMAIN text, primary key (UPM_OID));
create table DB_WCT.USER_ROLE (URO_ROL_OID bigint not null, URO_USR_OID bigint not null, primary key (URO_USR_OID, URO_ROL_OID));
create table DB_WCT.WCTAUDIT (AUD_OID bigint not null, AUD_ACTION varchar(40) not null, AUD_DATE TIMESTAMP not null, AUD_FIRSTNAME varchar(50), AUD_LASTNAME varchar(50), AUD_MESSAGE text not null, AUD_SUBJECT_TYPE varchar(255) not null, AUD_USERNAME varchar(80), AUD_USER_OID bigint, AUD_SUBJECT_OID bigint, AUD_AGENCY_OID bigint, primary key (AUD_OID));
create table DB_WCT.WCTROLE (ROL_OID bigint not null, ROL_DESCRIPTION varchar(255), ROL_NAME varchar(80) not null, ROL_AGENCY_OID bigint not null, primary key (ROL_OID));
create table DB_WCT.WCTUSER (USR_OID bigint not null, USR_ACTIVE bit not null, USR_ADDRESS varchar(200), USR_EMAIL varchar(100) not null, USR_EXTERNAL_AUTH bit not null, USR_FIRSTNAME varchar(50) not null, USR_FORCE_PWD_CHANGE bit not null, USR_LASTNAME varchar(50) not null, USR_NOTIFICATIONS_BY_EMAIL bit not null, USR_PASSWORD varchar(255), USR_PHONE varchar(16), USR_TITLE varchar(10), USR_USERNAME varchar(80) not null unique, USR_AGC_OID bigint not null, USR_DEACTIVATE_DATE TIMESTAMP NULL, USR_TASKS_BY_EMAIL bit not null, USR_NOTIFY_ON_GENERAL bit not null, USR_NOTIFY_ON_WARNINGS bit not null, primary key (USR_OID));
create table DB_WCT.WCT_LOGON_DURATION (LOGDUR_OID bigint not null, LOGDUR_DURATION bigint, LOGDUR_LOGON_TIME TIMESTAMP not null, LOGDUR_LOGOUT_TIME TIMESTAMP NULL, LOGDUR_USERNAME varchar(80), LOGDUR_USER_OID bigint not null, LOGDUR_USER_REALNAME varchar(100), LOGDUR_SESSION_ID varchar(32) not null, primary key (LOGDUR_OID));
create table DB_WCT.HEATMAP_CONFIG (HM_OID bigint not null, HM_NAME varchar(255) not null, HM_COLOR varchar(255) not null, HM_THRESHOLD_LOWEST integer not null, HM_DISPLAY_NAME varchar(255) not null, primary key (HM_OID));
create table DB_WCT.FLAG (F_OID bigint not null, F_NAME varchar(255) not null, F_RGB varchar(6) not null, F_COMPLEMENT_RGB varchar(6) not null, F_AGC_OID bigint not null, primary key (F_OID));
create table DB_WCT.INDICATOR_CRITERIA (IC_OID bigint not null, IC_NAME varchar(255) not null, IC_DESCRIPTION varchar(255), IC_UPPER_LIMIT_PERCENTAGE double precision, IC_LOWER_LIMIT_PERCENTAGE double precision, IC_UPPER_LIMIT double precision, IC_LOWER_LIMIT double precision, IC_AGC_OID bigint not null, primary key (IC_OID), IC_UNIT varchar(20) not null, IC_SHOW_DELTA bit not null, IC_ENABLE_REPORT bit not null);
create table DB_WCT.INDICATOR (I_OID bigint not null, I_IC_OID bigint not null, I_TI_OID bigint not null, I_NAME varchar(255) not null, I_FLOAT_VALUE double precision, I_UPPER_LIMIT_PERCENTAGE double precision, I_LOWER_LIMIT_PERCENTAGE double precision, I_UPPER_LIMIT double precision, I_LOWER_LIMIT double precision, I_ADVICE varchar(255), I_JUSTIFICATION varchar(255), I_AGC_OID bigint not null , primary key (I_OID), I_UNIT varchar(20) not null, I_SHOW_DELTA bit not null, I_INDEX integer, I_DATE TIMESTAMP(6) not null);
create table DB_WCT.INDICATOR_REPORT_LINE (IRL_OID bigint, IRL_I_OID bigint, IRL_LINE varchar(1024), IRL_INDEX integer);
create table DB_WCT.ID_GENERATOR ( IG_TYPE varchar(255),  IG_VALUE integer ) ;
create table DB_WCT.PO_H3_BLOCK_URL (PBU_PROF_OVER_OID bigint not null, PBU_FILTER varchar(255), PBU_IX integer not null, primary key (PBU_PROF_OVER_OID, PBU_IX));
create table DB_WCT.PO_H3_INCLUDE_URL (PIU_PROF_OVER_OID bigint not null, PIU_FILTER varchar(255), PIU_IX integer not null, primary key (PIU_PROF_OVER_OID, PIU_IX));

-- NOTE: constraints are not enforced in MySQL, but they are in MariaDB starting with 10.2.1.
alter table DB_WCT.ABSTRACT_TARGET add unique key AT_NAME_AND_TYPE (AT_NAME, AT_OBJECT_TYPE);
alter table DB_WCT.ABSTRACT_TARGET add index FK_AT_DUBLIN_CORE_OID (AT_DUBLIN_CORE_OID), add constraint FK_AT_DUBLIN_CORE_OID foreign key (AT_DUBLIN_CORE_OID) references DB_WCT.DUBLIN_CORE (DC_OID);
alter table DB_WCT.ABSTRACT_TARGET add index FK_T_PROF_OVERRIDE_OID (AT_PROF_OVERRIDE_OID), add constraint FK_T_PROF_OVERRIDE_OID foreign key (AT_PROF_OVERRIDE_OID) references DB_WCT.PROFILE_OVERRIDES (PO_OID);
alter table DB_WCT.ABSTRACT_TARGET add index FKB6DD784E5C2C497 (AT_OWNER_ID), add constraint FKB6DD784E5C2C497 foreign key (AT_OWNER_ID) references DB_WCT.WCTUSER (USR_OID);
alter table DB_WCT.ABSTRACT_TARGET add index FKB6DD784E3A83A603 (T_PROFILE_ID), add constraint FKB6DD784E3A83A603 foreign key (T_PROFILE_ID) references DB_WCT.PROFILE (P_OID);
alter table DB_WCT.ABSTRACT_TARGET add constraint CHK_ACCESS_ZONE check (AT_ACCESS_ZONE in (0,1,2));
alter table DB_WCT.ABSTRACT_TARGET add index FK_AT_RR_OID (AT_RR_OID), add constraint FK_AT_RR_OID foreign key (AT_RR_OID) references DB_WCT.REJECTION_REASON (RR_OID);
alter table DB_WCT.ANNOTATIONS add index FK_NOTE_USER_OID (AN_USER_OID), add constraint FK_NOTE_USER_OID foreign key (AN_USER_OID) references DB_WCT.WCTUSER (USR_OID);
alter table DB_WCT.ARC_HARVEST_FILE add index FK_AHR_ARC_HARVEST_RESULT_ID (AHF_ARC_HARVEST_RESULT_ID), add constraint FK_AHR_ARC_HARVEST_RESULT_ID foreign key (AHF_ARC_HARVEST_RESULT_ID) references DB_WCT.ARC_HARVEST_RESULT (AHRS_HARVEST_RESULT_OID);
alter table DB_WCT.ARC_HARVEST_RESOURCE add index FK6D84FEB12FF8F14B (AHRC_HARVEST_RESOURCE_OID), add constraint FK6D84FEB12FF8F14B foreign key (AHRC_HARVEST_RESOURCE_OID) references DB_WCT.HARVEST_RESOURCE (HRC_OID);
alter table DB_WCT.ARC_HARVEST_RESULT add index FKE39C5380C88A38D9 (AHRS_HARVEST_RESULT_OID), add constraint FKE39C5380C88A38D9 foreign key (AHRS_HARVEST_RESULT_OID) references DB_WCT.HARVEST_RESULT (HR_OID);
alter table DB_WCT.BANDWIDTH_RESTRICTIONS add constraint CHK_DAY check (br_day IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'));
alter table DB_WCT.BANDWIDTH_RESTRICTIONS add constraint CHK_END_TIME check (br_end_time >= TO_DATE('1972-11-09 00:00:00', 'YYYY-MM-DD HH24:MI:SS') AND br_end_time <= TO_DATE('1972-11-09 23:59:59', 'YYYY-MM-DD HH24:MI:SS'));
alter table DB_WCT.BANDWIDTH_RESTRICTIONS add constraint CHK_START_TIME check (br_start_time >= TO_DATE('1972-11-09 00:00:00', 'YYYY-MM-DD HH24:MI:SS') AND br_start_time <= TO_DATE('1972-11-09 23:59:59', 'YYYY-MM-DD HH24:MI:SS'));
alter table DB_WCT.GROUP_MEMBER add index FK_GM_PARENT_ID (GM_PARENT_ID), add constraint FK_GM_PARENT_ID foreign key (GM_PARENT_ID) references DB_WCT.TARGET_GROUP (TG_AT_OID);
alter table DB_WCT.GROUP_MEMBER add index FK_GM_CHILD_ID (GM_CHILD_ID), add constraint FK_GM_CHILD_ID foreign key (GM_CHILD_ID) references DB_WCT.ABSTRACT_TARGET (AT_OID);
alter table DB_WCT.HARVEST_RESOURCE add index FK5BA2B04431A1C148 (HRC_HARVEST_RESULT_OID), add constraint FK5BA2B04431A1C148 foreign key (HRC_HARVEST_RESULT_OID) references DB_WCT.HARVEST_RESULT (HR_OID);
alter table DB_WCT.HARVEST_RESULT add index FK_HR_CREATED_BY_ID (HR_CREATED_BY_ID), add constraint FK_HR_CREATED_BY_ID foreign key (HR_CREATED_BY_ID) references DB_WCT.WCTUSER (USR_OID);
alter table DB_WCT.HARVEST_RESULT add index FK_HRC_TARGET_INSTANCE_ID (HR_TARGET_INSTANCE_ID), add constraint FK_HRC_TARGET_INSTANCE_ID foreign key (HR_TARGET_INSTANCE_ID) references DB_WCT.TARGET_INSTANCE (TI_OID);
alter table DB_WCT.HARVEST_RESULT add index FK_HR_RR_OID (HR_RR_OID), ADD CONSTRAINT FK_HR_RR_OID foreign key (HR_RR_OID) REFERENCES DB_WCT.REJECTION_REASON (RR_OID);
alter table DB_WCT.HR_MODIFICATION_NOTE add index FKA908CCC03E1474AF (HMN_HR_OID), add constraint FKA908CCC03E1474AF foreign key (HMN_HR_OID) references DB_WCT.HARVEST_RESULT (HR_OID);
alter table DB_WCT.PERMISSION add index FKFE0FB1CFEE52493C (PE_AUTH_AGENT_ID), add constraint FKFE0FB1CFEE52493C foreign key (PE_AUTH_AGENT_ID) references DB_WCT.AUTHORISING_AGENT (AA_OID);
alter table DB_WCT.PERMISSION add index FKFE0FB1CFA1E5D89A (PE_OWNING_AGENCY_ID), add constraint FKFE0FB1CFA1E5D89A foreign key (PE_OWNING_AGENCY_ID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.PERMISSION add index FK_PE_SITE_ID (PE_SITE_ID), add constraint FK_PE_SITE_ID foreign key (PE_SITE_ID) references DB_WCT.SITE (ST_OID);
alter table DB_WCT.PERMISSION_EXCLUSION add index FK2DB3C33EB558CEC7 (PEX_PERMISSION_OID), add constraint FK2DB3C33EB558CEC7 foreign key (PEX_PERMISSION_OID) references DB_WCT.PERMISSION (PE_OID);
alter table DB_WCT.PERMISSION_TEMPLATE add index FK_TEMPLATE_AGENCY_OID (PRT_AGC_OID), add constraint FK_TEMPLATE_AGENCY_OID foreign key (PRT_AGC_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.PERMISSION_URLPATTERN add index PU_FK_1 (PU_URLPATTERN_ID), add constraint PU_FK_1 foreign key (PU_URLPATTERN_ID) references DB_WCT.URL_PATTERN (UP_OID);
alter table DB_WCT.PERMISSION_URLPATTERN add index PU_FK_2 (PU_PERMISSION_ID), add constraint PU_FK_2 foreign key (PU_PERMISSION_ID) references DB_WCT.PERMISSION (PE_OID);
alter table DB_WCT.PO_EXCLUSION_URI add index FKFF4AB0FBBC3C926 (PEU_PROF_OVER_OID), add constraint FKFF4AB0FBBC3C926 foreign key (PEU_PROF_OVER_OID) references DB_WCT.PROFILE_OVERRIDES (PO_OID);
alter table DB_WCT.PO_INCLUSION_URI add index FK6C665549BC3C926 (PEU_PROF_OVER_OID), add constraint FK6C665549BC3C926 foreign key (PEU_PROF_OVER_OID) references DB_WCT.PROFILE_OVERRIDES (PO_OID);
alter table DB_WCT.PROFILE add index FK_P_AGENCY_OID (P_AGECNY_OID), add constraint FK_P_AGENCY_OID foreign key (P_AGECNY_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.PROFILE_BASIC_CREDENTIALS add index FKE72A5AF5BD9AB61 (PBC_PC_OID), add constraint FKE72A5AF5BD9AB61 foreign key (PBC_PC_OID) references DB_WCT.PROFILE_CREDENTIALS (PC_OID);
alter table DB_WCT.PROFILE_CREDENTIALS add index FK317A252682C63D7F (PC_PROFILE_OVERIDE_OID), add constraint FK317A252682C63D7F foreign key (PC_PROFILE_OVERIDE_OID) references DB_WCT.PROFILE_OVERRIDES (PO_OID);
alter table DB_WCT.PROFILE_FORM_CREDENTIALS add index FK6B1303D750209B71 (PRC_PC_OID), add constraint FK6B1303D750209B71 foreign key (PRC_PC_OID) references DB_WCT.PROFILE_CREDENTIALS (PC_OID);
alter table DB_WCT.REJECTION_REASON add index FK_RR_AGENCY_OID (RR_AGC_OID), add constraint FK_RR_AGENCY_OID foreign key (RR_AGC_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.REJECTION_REASON add unique key RR_NAME_AND_AGENCY (RR_NAME, RR_AGC_OID);
alter table DB_WCT.ROLE_PRIVILEGE add index FK_PRIV_ROLE_OID (PRV_ROLE_OID), add constraint FK_PRIV_ROLE_OID foreign key (PRV_ROLE_OID) references DB_WCT.WCTROLE (ROL_OID);
alter table DB_WCT.SCHEDULE add index FK50C8297BE49544D (S_ABSTRACT_TARGET_ID), add constraint FK50C8297BE49544D foreign key (S_ABSTRACT_TARGET_ID) references DB_WCT.ABSTRACT_TARGET (AT_OID);
alter table DB_WCT.SCHEDULE add index FK_S_OWNER_OID (S_OWNER_OID), add constraint FK_S_OWNER_OID foreign key (S_OWNER_OID) references DB_WCT.WCTUSER (USR_OID);
alter table DB_WCT.SCHEDULE add index FK_S_TARGET_ID (S_TARGET_ID), add constraint FK_S_TARGET_ID foreign key (S_TARGET_ID) references DB_WCT.ABSTRACT_TARGET (AT_OID);
alter table DB_WCT.SEED add index FK_SEED_TARGET_ID (S_TARGET_ID), add constraint FK_SEED_TARGET_ID foreign key (S_TARGET_ID) references DB_WCT.TARGET (T_AT_OID);
alter table DB_WCT.SEED_HISTORY add index FK_SEED_HISTORY_TI_OID (SH_TI_OID), add constraint FK_SEED_HISTORY_TI_OID foreign key (SH_TI_OID) references DB_WCT.TARGET_INSTANCE (TI_OID);
alter table DB_WCT.SEED_PERMISSION add index FK9659805D68A4F4BA (SP_SEED_ID), add constraint FK9659805D68A4F4BA foreign key (SP_SEED_ID) references DB_WCT.SEED (S_OID);
alter table DB_WCT.SEED_PERMISSION add index FK_SP_PERMISSION_ID (SP_PERMISSION_ID), add constraint FK_SP_PERMISSION_ID foreign key (SP_PERMISSION_ID) references DB_WCT.PERMISSION (PE_OID);
alter table DB_WCT.SIP_PART_ELEMENT add index FK4998B1F5F51BBD3F (SPE_TARGET_INSTANCE_OID), add constraint FK4998B1F5F51BBD3F foreign key (SPE_TARGET_INSTANCE_OID) references DB_WCT.TARGET_INSTANCE (TI_OID);
alter table DB_WCT.SITE add index FK_OWNING_AGENCY_ID (ST_OWNING_AGENCY_ID), add constraint FK_OWNING_AGENCY_ID foreign key (ST_OWNING_AGENCY_ID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.SITE_AUTH_AGENCY add index FKDF808D84C049BBEB (SA_SITE_ID), add constraint FKDF808D84C049BBEB foreign key (SA_SITE_ID) references DB_WCT.SITE (ST_OID);
alter table DB_WCT.SITE_AUTH_AGENCY add index FK_SA_AGENT_ID (SA_AGENT_ID), add constraint FK_SA_AGENT_ID foreign key (SA_AGENT_ID) references DB_WCT.AUTHORISING_AGENT (AA_OID);
alter table DB_WCT.TARGET add index FK931165917947B83E (T_AT_OID), add constraint FK931165917947B83E foreign key (T_AT_OID) references DB_WCT.ABSTRACT_TARGET (AT_OID);
alter table DB_WCT.TARGET_GROUP add index FKB444963161DABD5F (TG_AT_OID), add constraint FKB444963161DABD5F foreign key (TG_AT_OID) references DB_WCT.ABSTRACT_TARGET (AT_OID);
alter table DB_WCT.TARGET_INSTANCE add index FK_TI_TARGET_ID (TI_TARGET_ID), add constraint FK_TI_TARGET_ID foreign key (TI_TARGET_ID) references DB_WCT.ABSTRACT_TARGET (AT_OID);
alter table DB_WCT.TARGET_INSTANCE add index FK_TI_USER_ID (TI_OWNER_ID), add constraint FK_TI_USER_ID foreign key (TI_OWNER_ID) references DB_WCT.WCTUSER (USR_OID);
alter table DB_WCT.TARGET_INSTANCE add index FK_TI_PROF_OVERRIDE_OID (TI_PROF_OVERRIDE_OID), add constraint FK_TI_PROF_OVERRIDE_OID foreign key (TI_PROF_OVERRIDE_OID) references DB_WCT.PROFILE_OVERRIDES (PO_OID);
alter table DB_WCT.TARGET_INSTANCE add index FK_TI_SCHEDULE_ID (TI_SCHEDULE_ID), add constraint FK_TI_SCHEDULE_ID foreign key (TI_SCHEDULE_ID) references DB_WCT.SCHEDULE (S_OID);
alter table DB_WCT.TARGET_INSTANCE_ORIG_SEED add index FKD47ACFF36748402E (TIOS_TI_OID), add constraint FKD47ACFF36748402E foreign key (TIOS_TI_OID) references DB_WCT.TARGET_INSTANCE (TI_OID);
alter table DB_WCT.TASK add index FK_TASK_AGENCY_OID (TSK_AGC_OID), add constraint FK_TASK_AGENCY_OID foreign key (TSK_AGC_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.URL_PATTERN add index FK_UP_SITE_ID (UP_SITE_ID), add constraint FK_UP_SITE_ID foreign key (UP_SITE_ID) references DB_WCT.SITE (ST_OID);
alter table DB_WCT.URL_PERMISSION_MAPPING add index FK_UPM_URL_PATTERN_ID (UPM_URL_PATTERN_ID), add constraint FK_UPM_URL_PATTERN_ID foreign key (UPM_URL_PATTERN_ID) references DB_WCT.URL_PATTERN (UP_OID);
alter table DB_WCT.URL_PERMISSION_MAPPING add index FK_UPM_PERMISSION_ID (UPM_PERMISSION_ID), add constraint FK_UPM_PERMISSION_ID foreign key (UPM_PERMISSION_ID) references DB_WCT.PERMISSION (PE_OID);
alter table DB_WCT.USER_ROLE add index FK_USERROLE_TO_ROLE (URO_ROL_OID), add constraint FK_USERROLE_TO_ROLE foreign key (URO_ROL_OID) references DB_WCT.WCTROLE (ROL_OID);
alter table DB_WCT.USER_ROLE add index FK_USERROLE_TO_USER (URO_USR_OID), add constraint FK_USERROLE_TO_USER foreign key (URO_USR_OID) references DB_WCT.WCTUSER (USR_OID);
alter table DB_WCT.WCTAUDIT add index FK_AUD_USER_OID (AUD_USER_OID), add constraint FK_AUD_USER_OID foreign key (AUD_USER_OID) references DB_WCT.WCTUSER (USR_OID);
alter table DB_WCT.WCTAUDIT add index FK_AUD_AGENCY_OID (AUD_AGENCY_OID), add constraint FK_AUD_AGENCY_OID foreign key (AUD_AGENCY_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.WCTROLE add index FK_ROLE_AGENCY_OID (ROL_AGENCY_OID), add constraint FK_ROLE_AGENCY_OID foreign key (ROL_AGENCY_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.WCTUSER add index FK_USER_AGENCY_OID (USR_AGC_OID), add constraint FK_USER_AGENCY_OID foreign key (USR_AGC_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.FLAG add constraint FK_F_AGENCY_OID foreign key (F_AGC_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.TARGET_INSTANCE add constraint FK_F_OID foreign key (TI_FLAG_OID) references DB_WCT.FLAG (F_OID);
alter table DB_WCT.INDICATOR_CRITERIA add constraint FK_IC_AGENCY_OID foreign key (IC_AGC_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.INDICATOR add constraint FK_I_TI_OID foreign key (I_TI_OID) references DB_WCT.TARGET_INSTANCE (TI_OID) on delete cascade;
alter table DB_WCT.INDICATOR add constraint FK_I_IC_OID foreign key (I_IC_OID) references DB_WCT.INDICATOR_CRITERIA (IC_OID);
alter table DB_WCT.INDICATOR add constraint FK_I_AGENCY_OID foreign key (I_AGC_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.INDICATOR_REPORT_LINE add constraint FK_IRL_I_OID foreign key (IRL_I_OID) references DB_WCT.INDICATOR (I_OID);
alter table DB_WCT.PO_H3_BLOCK_URL add index PBU_FK_1 (PBU_PROF_OVER_OID), add constraint PBU_FK_1 foreign key (PBU_PROF_OVER_OID) references DB_WCT.PROFILE_OVERRIDES (PO_OID);
alter table DB_WCT.PO_H3_INCLUDE_URL add index PIU_FK_1 (PIU_PROF_OVER_OID), add constraint PIU_FK_1 foreign key (PIU_PROF_OVER_OID) references DB_WCT.PROFILE_OVERRIDES (PO_OID);


create view DB_WCT.URL_PERMISSION_MAPPING_VIEW as 
 SELECT upm.upm_oid, upm.upm_domain, p.pe_oid, p.pe_end_date, p.pe_owning_agency_id, up.up_pattern, st.st_active
   FROM DB_WCT.URL_PERMISSION_MAPPING upm
   JOIN DB_WCT.PERMISSION p ON upm.upm_permission_id = p.pe_oid
   JOIN DB_WCT.URL_PATTERN up ON upm.upm_url_pattern_id = up.up_oid
   JOIN DB_WCT.SITE st ON p.pe_site_id = st.st_oid;

create view DB_WCT.ABSTRACT_TARGET_SCHEDULE_VIEW as
 SELECT (abt.at_oid || ',') || s.s_oid AS thekey,
        CASE abt.at_object_type
            WHEN 1 THEN 'Target'
            ELSE 'Group'
        END AS at_object_type_desc, abt.at_name, abt.at_state, u.usr_username, a.agc_name, s.s_oid, s.s_start, s.s_end, s.s_type, s.s_cron
   FROM DB_WCT.ABSTRACT_TARGET abt
   RIGHT JOIN DB_WCT.SCHEDULE s ON s.s_abstract_target_id = abt.at_oid
   JOIN DB_WCT.WCTUSER u ON abt.at_owner_id = u.usr_oid
   JOIN DB_WCT.AGENCY a ON u.usr_agc_oid = a.agc_oid
  ORDER BY abt.at_name, s.s_oid;
  
create view DB_WCT.ABSTRACT_TARGET_GROUPTYPE_VIEW as 
 SELECT a.at_oid, a.at_desc, a.at_name, a.at_owner_id, a.at_prof_override_oid, a.at_state, a.t_profile_id, a.at_object_type, a.at_creation_date, a.at_reference, a.at_profile_note, a.at_dublin_core_oid, a.at_access_zone, a.at_display_target, a.at_display_note, tg.tg_type
   FROM DB_WCT.ABSTRACT_TARGET a
   LEFT JOIN DB_WCT.TARGET_GROUP tg ON a.at_oid = tg.tg_at_oid;  



-- Fixed data
insert into DB_WCT.HEATMAP_CONFIG (HM_OID, HM_NAME, HM_DISPLAY_NAME, HM_COLOR, HM_THRESHOLD_LOWEST) values (1, "low","Low","8FBC8F",1);
insert into DB_WCT.HEATMAP_CONFIG (HM_OID, HM_NAME, HM_DISPLAY_NAME, HM_COLOR, HM_THRESHOLD_LOWEST) values (2, "medium","Medium","F0E68C",7);
insert into DB_WCT.HEATMAP_CONFIG (HM_OID, HM_NAME, HM_DISPLAY_NAME, HM_COLOR, HM_THRESHOLD_LOWEST) values (3, "high","High","FF6347",12);
