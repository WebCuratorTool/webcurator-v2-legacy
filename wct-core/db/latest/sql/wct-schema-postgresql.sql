drop view if exists DB_WCT.ABSTRACT_TARGET_GROUPTYPE_VIEW cascade;
drop view if exists DB_WCT.ABSTRACT_TARGET_SCHEDULE_VIEW cascade;
drop view if exists DB_WCT.URL_PERMISSION_MAPPING_VIEW cascade;
drop table if exists DB_WCT.ABSTRACT_TARGET cascade;
drop table if exists DB_WCT.AGENCY cascade;
drop table if exists DB_WCT.ANNOTATIONS cascade;
drop table if exists DB_WCT.ARC_HARVEST_FILE cascade;
drop table if exists DB_WCT.ARC_HARVEST_RESOURCE cascade;
drop table if exists DB_WCT.ARC_HARVEST_RESULT cascade;
drop table if exists DB_WCT.AUTHORISING_AGENT cascade;
drop table if exists DB_WCT.BANDWIDTH_RESTRICTIONS cascade;
drop table if exists DB_WCT.DUBLIN_CORE cascade;
drop table if exists DB_WCT.GROUP_MEMBER cascade;
drop table if exists DB_WCT.HARVEST_RESOURCE cascade;
drop table if exists DB_WCT.HARVEST_RESULT cascade;
drop table if exists DB_WCT.HARVEST_STATUS cascade;
drop table if exists DB_WCT.HR_MODIFICATION_NOTE cascade;
drop table if exists DB_WCT.NOTIFICATION cascade;
drop table if exists DB_WCT.PERMISSION cascade;
drop table if exists DB_WCT.PERMISSION_EXCLUSION cascade;
drop table if exists DB_WCT.PERMISSION_TEMPLATE cascade;
drop table if exists DB_WCT.PERMISSION_URLPATTERN cascade;
drop table if exists DB_WCT.PO_EXCLUSION_URI cascade;
drop table if exists DB_WCT.PO_INCLUSION_URI cascade;
drop table if exists DB_WCT.PROFILE cascade;
drop table if exists DB_WCT.PROFILE_BASIC_CREDENTIALS cascade;
drop table if exists DB_WCT.PROFILE_CREDENTIALS cascade;
drop table if exists DB_WCT.PROFILE_FORM_CREDENTIALS cascade;
drop table if exists DB_WCT.PROFILE_OVERRIDES cascade;
drop table if exists DB_WCT.REJECTION_REASON cascade;
drop table if exists DB_WCT.ROLE_PRIVILEGE cascade;
drop table if exists DB_WCT.SCHEDULE cascade;
drop table if exists DB_WCT.SEED cascade;
drop table if exists DB_WCT.SEED_HISTORY cascade;
drop table if exists DB_WCT.SEED_PERMISSION cascade;
drop table if exists DB_WCT.SIP_PART_ELEMENT cascade;
drop table if exists DB_WCT.SITE cascade;
drop table if exists DB_WCT.SITE_AUTH_AGENCY cascade;
drop table if exists DB_WCT.TARGET cascade;
drop table if exists DB_WCT.TARGET_GROUP cascade;
drop table if exists DB_WCT.TARGET_INSTANCE cascade;
drop table if exists DB_WCT.TARGET_INSTANCE_ORIG_SEED cascade;
drop table if exists DB_WCT.TASK cascade;
drop table if exists DB_WCT.URL_PATTERN cascade;
drop table if exists DB_WCT.URL_PERMISSION_MAPPING cascade;
drop table if exists DB_WCT.USER_ROLE cascade;
drop table if exists DB_WCT.WCTAUDIT cascade;
drop table if exists DB_WCT.WCTROLE cascade;
drop table if exists DB_WCT.WCTUSER cascade;
drop table if exists DB_WCT.WCT_LOGON_DURATION cascade;
drop table if exists DB_WCT.ID_GENERATOR cascade;
drop table if exists DB_WCT.FLAG cascade;
drop table if exists DB_WCT.INDICATOR_CRITERIA cascade;
drop table if exists DB_WCT.INDICATOR_REPORT_LINE cascade;
drop table if exists DB_WCT.INDICATOR cascade;
drop table if exists DB_WCT.PO_H3_BLOCK_URL cascade;
drop table if exists DB_WCT.PO_H3_INCLUDE_URL cascade;

create table DB_WCT.ABSTRACT_TARGET (AT_OID int8 not null, AT_DESC varchar(4000), AT_NAME varchar(255), AT_OWNER_ID int8, AT_PROF_OVERRIDE_OID int8, AT_STATE int4, T_PROFILE_ID int8, AT_OBJECT_TYPE int4, AT_CREATION_DATE TIMESTAMP, AT_REFERENCE varchar(255), AT_PROFILE_NOTE varchar(255), AT_DUBLIN_CORE_OID int8, AT_ACCESS_ZONE int8 default 0 not null, AT_DISPLAY_TARGET bool default true not null, AT_DISPLAY_NOTE varchar(4000), AT_DISPLAY_CHG_REASON varchar(1000), AT_RR_OID int8, AT_CRAWLS int8, AT_REFERENCE_CRAWL_OID int8, AT_AUTO_PRUNE bool not null default false, AT_AUTO_DENOTE_REFERENCE_CRAWL bool not null default false, AT_REQUEST_TO_ARCHIVISTS varchar(4000), primary key (AT_OID));
create table DB_WCT.AGENCY (AGC_OID int8 not null, AGC_NAME varchar(80) not null unique, AGC_ADDRESS varchar(255) not null, AGC_LOGO_URL varchar(255), AGC_URL varchar(255), AGC_EMAIL varchar(80), AGC_FAX varchar(20), AGC_PHONE varchar(20), AGC_SHOW_TASKS bool default true not null, AGC_DEFAULT_DESC_TYPE varchar(255), primary key (AGC_OID));
create table DB_WCT.ANNOTATIONS (AN_OID int8 not null, AN_DATE timestamp not null, AN_NOTE varchar(1000) not null, AN_USER_OID int8 not null, AN_OBJ_OID int8 not null, AN_OBJ_TYPE varchar(500) not null, AN_ALERTABLE boolean not null default false, primary key (AN_OID));
create table DB_WCT.ARC_HARVEST_FILE (AHF_OID int8 not null, AHF_COMPRESSED bool not null, AHF_NAME varchar(100) not null unique, AHF_ARC_HARVEST_RESULT_ID int8, primary key (AHF_OID));
create table DB_WCT.ARC_HARVEST_RESOURCE (AHRC_HARVEST_RESOURCE_OID int8 not null, AHRC_RESOURCE_LENGTH int8 not null, AHRC_RESOURCE_OFFSET int8 not null, AHRC_ARC_FILE_NAME varchar(100) not null, AHRC_COMPRESSED_YN bool not null, primary key (AHRC_HARVEST_RESOURCE_OID));
create table DB_WCT.ARC_HARVEST_RESULT (AHRS_HARVEST_RESULT_OID int8 not null, primary key (AHRS_HARVEST_RESULT_OID));
create table DB_WCT.AUTHORISING_AGENT (AA_OID int8 not null, AA_NAME varchar(255) unique, AA_ADRESS varchar(2048), AA_CONTACT varchar(255), AA_EMAIL varchar(255), AA_PHONE_NUMBER varchar(32), AA_DESC varchar(2048), primary key (AA_OID));
create table DB_WCT.BANDWIDTH_RESTRICTIONS (BR_OID int8 not null, BR_BANDWIDTH int8 not null, BR_DAY varchar(9) not null, BR_END_TIME timestamp not null, BR_START_TIME timestamp not null, BR_OPTIMIZATION_ALLOWED bool default false not null, primary key (BR_OID));
create table DB_WCT.DUBLIN_CORE (DC_OID int8 not null, DC_CONTRIBUTOR varchar(255), DC_COVERAGE varchar(255), DC_CREATOR varchar(255), DC_DESCRIPTION varchar(2000), DC_FORMAT varchar(255), DC_IDENTIFIER varchar(255), DC_IDENTIFIER_ISBN varchar(13), DC_IDENTIFIER_ISSN varchar(9), DC_LANGUAGE varchar(255), DC_PUBLISHER varchar(255), DC_RELATION varchar(255), DC_SOURCE varchar(255), DC_SUBJECT varchar(2000), DC_TITLE varchar(255), DC_TYPE varchar(50), primary key (DC_OID));
create table DB_WCT.GROUP_MEMBER (AT_OID int8 not null, GM_CHILD_ID int8, GM_PARENT_ID int8, primary key (AT_OID));
create table DB_WCT.HARVEST_RESOURCE (HRC_OID int8 not null, HRC_LENGTH int8, HRC_NAME varchar(1020) not null, HRC_HARVEST_RESULT_OID int8, HRC_STATUS_CODE int4 not null, primary key (HRC_OID));
create table DB_WCT.HARVEST_RESULT (HR_OID int8 not null, HR_HARVEST_NO int4, HR_TARGET_INSTANCE_ID int8, HR_PROVENANCE_NOTE varchar(1024) not null, HR_CREATED_DATE timestamp, HR_CREATED_BY_ID int8, HR_STATE int4, HR_DERIVED_FROM int4, HR_INDEX int4, HR_RR_OID int8, primary key (HR_OID));
create table DB_WCT.HARVEST_STATUS (HS_OID int8 not null, HS_AVG_KB float8, HS_AVG_URI float8, HS_DATA_AMOUNT int8, HS_ELAPSED_TIME int8, HS_JOB_NAME varchar(500), HS_STATUS varchar(255), HS_URLS_DOWN int8, HS_URLS_FAILED int8, HS_ALERTS int4, HS_APP_VERSION varchar(255), HS_HRTX_VERSION varchar(255), primary key (HS_OID));
create table DB_WCT.HR_MODIFICATION_NOTE (HMN_HR_OID int8 not null, HMN_NOTE varchar(2000), HMN_INDEX int4 not null, primary key (HMN_HR_OID, HMN_INDEX));
create table DB_WCT.NOTIFICATION (NOT_OID int8 not null, NOT_MESSAGE varchar(2000), NOT_USR_OID int8 not null, NOT_SENDER varchar(80) not null, NOT_SENT_DATE timestamp, NOT_SUBJECT varchar(255) not null, primary key (NOT_OID));
create table DB_WCT.PERMISSION (PE_OID int8 not null, PE_ACCESS_STATUS varchar(255), PE_APPROVED_YN bool, PE_AVAILABLE_YN bool, PE_COPYRIGHT_STATEMENT varchar(2048), PE_COPYRIGHT_URL varchar(2048), PE_CREATION_DATE TIMESTAMP, PE_END_DATE TIMESTAMP, PE_NOTES text, PE_OPEN_ACCESS_DATE TIMESTAMP, PE_PERMISSION_GRANTED_DATE TIMESTAMP, PE_PERMISSION_REQUESTED_DATE TIMESTAMP, PE_SPECIAL_REQUIREMENTS varchar(2048), PE_START_DATE TIMESTAMP, PE_STATUS int4, PE_AUTH_AGENT_ID int8, PE_SITE_ID int8, PE_QUICK_PICK bool, PE_DISPLAY_NAME varchar(32), PE_OWNING_AGENCY_ID int8, PE_FILE_REFERENCE varchar(255), primary key (PE_OID));
create table DB_WCT.PERMISSION_EXCLUSION (PEX_OID int8 not null, PEX_REASON varchar(255), PEX_URL varchar(1024), PEX_PERMISSION_OID int8, PEX_INDEX int4, primary key (PEX_OID));
create table DB_WCT.PERMISSION_TEMPLATE (PRT_OID int8 not null, PRT_AGC_OID int8 not null, PRT_TEMPLATE_TEXT text not null, PRT_TEMPLATE_NAME varchar(80) not null, PRT_TEMPLATE_TYPE varchar(40) not null, PRT_TEMPLATE_DESC varchar(255), PRT_TEMPLATE_SUBJECT varchar(255), PRT_TEMPLATE_OVERWRITE_FROM boolean not null default false, PRT_TEMPLATE_FROM varchar(255), PRT_TEMPLATE_CC varchar(2048), PRT_TEMPLATE_BCC varchar(2048), PRT_TEMPLATE_REPLY_TO varchar(255), primary key (PRT_OID));
create table DB_WCT.PERMISSION_URLPATTERN (PU_PERMISSION_ID int8 not null, PU_URLPATTERN_ID int8 not null, primary key (PU_URLPATTERN_ID, PU_PERMISSION_ID));
create table DB_WCT.PO_EXCLUSION_URI (PEU_PROF_OVER_OID int8 not null, PEU_FILTER varchar(255), PEU_IX int4 not null, primary key (PEU_PROF_OVER_OID, PEU_IX));
create table DB_WCT.PO_INCLUSION_URI (PEU_PROF_OVER_OID int8 not null, PEU_FILTER varchar(255), PEU_IX int4 not null, primary key (PEU_PROF_OVER_OID, PEU_IX));
create table DB_WCT.PROFILE (P_OID int8 not null, P_VERSION int4 not null, P_DESC varchar(255), P_NAME varchar(255), P_PROFILE_STRING text, P_PROFILE_LEVEL int4, P_STATUS int4, P_DEFAULT bool, P_AGECNY_OID int8, P_ORIG_OID int8, P_HARVESTER_TYPE varchar(40) not null, P_DATA_LIMIT_UNIT varchar(40), P_MAX_FILE_SIZE_UNIT varchar(40), P_TIME_LIMIT_UNIT varchar(40), P_IMPORTED bool default false not null, primary key (P_OID));
create table DB_WCT.PROFILE_BASIC_CREDENTIALS (PBC_PC_OID int8 not null, PBC_REALM varchar(255), primary key (PBC_PC_OID));
create table DB_WCT.PROFILE_CREDENTIALS (PC_OID int8 not null, PC_DOMAIN varchar(255), PC_PASSWORD varchar(255), PC_USERNAME varchar(255), PC_PROFILE_OVERIDE_OID int8, PC_INDEX int4, primary key (PC_OID));
create table DB_WCT.PROFILE_FORM_CREDENTIALS (PRC_PC_OID int8 not null, PFC_METHOD varchar(4), PFC_LOGIN_URI varchar(255), PFC_PASSWORD_FIELD varchar(255), PFC_USERNAME_FIELD varchar(255), primary key (PRC_PC_OID));
create table DB_WCT.PROFILE_OVERRIDES (PO_OID int8 not null, PO_EXCL_MIME_TYPES varchar(255), PO_MAX_BYES int8, PO_MAX_DOCS int8, PO_MAX_HOPS int4, PO_MAX_PATH_DEPTH int4, PO_MAX_TIME_SEC int8, PO_ROBOTS_POLICY varchar(10), PO_H3_DOC_LIMIT int4, PO_H3_DATA_LIMIT float8, PO_H3_DATA_LIMIT_UNIT varchar(40), PO_H3_TIME_LIMIT float8, PO_H3_TIME_LIMIT_UNIT varchar(40), PO_H3_MAX_PATH_DEPTH int4, PO_H3_MAX_HOPS int4, PO_H3_MAX_TRANS_HOPS int4, PO_H3_IGNORE_ROBOTS varchar(10), PO_H3_IGNORE_COOKIES bool, PO_OR_CREDENTIALS bool, PO_OR_EXCL_MIME_TYPES bool, PO_OR_EXCLUSION_URI bool, PO_OR_INCLUSION_URI bool, PO_OR_MAX_BYTES bool, PO_OR_MAX_DOCS bool, PO_OR_MAX_HOPS bool, PO_OR_MAX_PATH_DEPTH bool, PO_OR_MAX_TIME_SEC bool, PO_OR_ROBOTS_POLICY bool, PO_H3_OR_DOC_LIMIT bool, PO_H3_OR_DATA_LIMIT bool, PO_H3_OR_TIME_LIMIT bool, PO_H3_OR_MAX_PATH_DEPTH bool, PO_H3_OR_MAX_HOPS bool, PO_H3_OR_MAX_TRANS_HOPS bool, PO_H3_OR_IGNORE_ROBOTS bool, PO_H3_OR_IGNORE_COOKIES bool, PO_H3_OR_BLOCK_URL bool, PO_H3_OR_INCL_URL bool, PO_H3_OR_RAW_PROFILE bool, PO_H3_RAW_PROFILE text, primary key (PO_OID));
create table DB_WCT.REJECTION_REASON (RR_OID int8 not null, RR_NAME varchar(100) not null, RR_AVAILABLE_FOR_TARGET bool default false not null, RR_AVAILABLE_FOR_TI bool default false not null, RR_AGC_OID int8 not null, primary key (RR_OID));
create table DB_WCT.ROLE_PRIVILEGE (PRV_OID int8 not null, PRV_CODE varchar(40) not null, PRV_ROLE_OID int8, PRV_SCOPE int4 not null, primary key (PRV_OID));
create table DB_WCT.SCHEDULE (S_OID int8 not null, S_CRON varchar(255) not null, S_START TIMESTAMP not null, S_END TIMESTAMP, S_TARGET_ID int8, S_TYPE int4 not null, S_OWNER_OID int8, S_NEXT_SCHEDULE_TIME TIMESTAMP, S_ABSTRACT_TARGET_ID int8, S_LAST_PROCESSED_DATE timestamp without time zone default '2001-01-01 00:00:00', primary key (S_OID));
create table DB_WCT.SEED (S_OID int8 not null, S_SEED varchar(1024), S_TARGET_ID int8, S_PRIMARY bool, primary key (S_OID));
create table DB_WCT.SEED_HISTORY (SH_OID bigint not null, SH_TI_OID bigint, SH_SEED character varying(1024) not null, SH_PRIMARY boolean not null, primary key (SH_OID));
create table DB_WCT.SEED_PERMISSION (SP_SEED_ID int8 not null, SP_PERMISSION_ID int8 not null, primary key (SP_SEED_ID, SP_PERMISSION_ID));
create table DB_WCT.SIP_PART_ELEMENT (SPE_TARGET_INSTANCE_OID int8 not null, SPE_VALUE text, SPE_KEY varchar(255) not null, primary key (SPE_TARGET_INSTANCE_OID, SPE_KEY));
create table DB_WCT.SITE (ST_OID int8 not null, ST_TITLE varchar(255) not null unique, ST_DESC varchar(4000), ST_LIBRARY_ORDER_NO varchar(32), ST_NOTES text, ST_PUBLISHED bool not null, ST_ACTIVE bool not null, ST_OWNING_AGENCY_ID int8, ST_CREATION_DATE timestamp, primary key (ST_OID));
create table DB_WCT.SITE_AUTH_AGENCY (SA_SITE_ID int8 not null, SA_AGENT_ID int8 not null, primary key (SA_SITE_ID, SA_AGENT_ID));
create table DB_WCT.TARGET (T_AT_OID int8 not null, T_RUN_ON_APPROVAL bool, T_EVALUATION_NOTE varchar(1000), T_SELECTION_DATE TIMESTAMP, T_SELECTION_NOTE varchar(1000), T_SELECTION_TYPE varchar(255), T_HARVEST_TYPE varchar(255), T_USE_AQA bool default false not null, T_ALLOW_OPTIMIZE boolean default false not null, primary key (T_AT_OID));
create table DB_WCT.TARGET_GROUP (TG_AT_OID int8 not null, TG_SIP_TYPE int4, TG_START_DATE DATE, TG_END_DATE DATE, TG_OWNERSHIP_METADATA varchar(255), TG_TYPE varchar(255), primary key (TG_AT_OID));
create table DB_WCT.TARGET_INSTANCE (TI_OID int8 not null, TI_VERSION int4 not null, TI_SCHEDULE_ID int8, TI_TARGET_ID int8, TI_PRIORITY int4 not null, TI_SCHEDULED_TIME TIMESTAMP not null, TI_STATE varchar(50) not null, TI_BANDWIDTH_PERCENT int4, TI_ALLOCATED_BANDWIDTH int8, TI_START_TIME timestamp, TI_OWNER_ID int8, TI_DISPLAY_ORDER int4, TI_PROF_OVERRIDE_OID int8, TI_PURGED bool not null, TI_ARCHIVE_ID varchar(40) unique, TI_REFERENCE varchar(255), TI_HARVEST_SERVER varchar(255), TI_DISPLAY_TARGET_INSTANCE bool default true not null, TI_DISPLAY_NOTE varchar(4000), TI_FLAGGED bool default false not null, TI_PROFILE_ID int8, TI_ARCHIVED_TIME timestamp, TI_FIRST_FROM_TARGET boolean not null default false, TI_DISPLAY_CHG_REASON varchar(1000), TI_USE_AQA bool default false not null, TI_ALLOW_OPTIMIZE bool default false not null, TI_FLAG_OID int4, TI_RECOMMENDATION varchar(255), primary key (TI_OID));
create table DB_WCT.TARGET_INSTANCE_ORIG_SEED (TIOS_TI_OID int8 not null, TIOS_SEED varchar(1024));
create table DB_WCT.TASK (TSK_OID int8 not null, TSK_USR_OID int8, TSK_MESSAGE varchar(2000), TSK_SENDER varchar(80) not null, TSK_SENT_DATE timestamp, TSK_SUBJECT varchar(255) not null, TSK_PRIVILEGE varchar(40), TSK_AGC_OID int8 not null, TSK_MSG_TYPE varchar(40) not null, TSK_RESOURCE_OID int8 not null, TSK_RESOURCE_TYPE varchar(80) not null, primary key (TSK_OID));
create table DB_WCT.URL_PATTERN (UP_OID int8 not null, UP_PATTERN varchar(2048), UP_SITE_ID int8, primary key (UP_OID));
create table DB_WCT.URL_PERMISSION_MAPPING (UPM_OID int8 not null, UPM_PERMISSION_ID int8, UPM_URL_PATTERN_ID int8, UPM_DOMAIN varchar(1024), primary key (UPM_OID));
create table DB_WCT.USER_ROLE (URO_ROL_OID int8 not null, URO_USR_OID int8 not null, primary key (URO_USR_OID, URO_ROL_OID));
create table DB_WCT.WCTAUDIT (AUD_OID int8 not null, AUD_ACTION varchar(40) not null, AUD_DATE TIMESTAMP not null, AUD_FIRSTNAME varchar(50), AUD_LASTNAME varchar(50), AUD_MESSAGE varchar(2000) not null, AUD_SUBJECT_TYPE varchar(255) not null, AUD_USERNAME varchar(80), AUD_USER_OID int8, AUD_SUBJECT_OID int8, AUD_AGENCY_OID int8, primary key (AUD_OID));
create table DB_WCT.WCTROLE (ROL_OID int8 not null, ROL_DESCRIPTION varchar(255), ROL_NAME varchar(80) not null, ROL_AGENCY_OID int8 not null, primary key (ROL_OID));
create table DB_WCT.WCTUSER (USR_OID int8 not null, USR_ACTIVE bool not null, USR_ADDRESS varchar(200), USR_EMAIL varchar(100) not null, USR_EXTERNAL_AUTH bool not null, USR_FIRSTNAME varchar(50) not null, USR_FORCE_PWD_CHANGE bool not null, USR_LASTNAME varchar(50) not null, USR_NOTIFICATIONS_BY_EMAIL bool not null, USR_PASSWORD varchar(255), USR_PHONE varchar(16), USR_TITLE varchar(10), USR_USERNAME varchar(80) not null unique, USR_AGC_OID int8 not null, USR_DEACTIVATE_DATE TIMESTAMP, USR_TASKS_BY_EMAIL bool not null, USR_NOTIFY_ON_GENERAL bool not null, USR_NOTIFY_ON_WARNINGS bool not null, primary key (USR_OID));
create table DB_WCT.WCT_LOGON_DURATION (LOGDUR_OID int8 not null, LOGDUR_DURATION int8, LOGDUR_LOGON_TIME TIMESTAMP not null, LOGDUR_LOGOUT_TIME TIMESTAMP, LOGDUR_USERNAME varchar(80), LOGDUR_USER_OID int8 not null, LOGDUR_USER_REALNAME varchar(100), LOGDUR_SESSION_ID varchar(32) not null, primary key (LOGDUR_OID));
create table DB_WCT.HEATMAP_CONFIG (HM_OID int8 not null, HM_NAME varchar(255) not null, HM_COLOR varchar(255) not null, HM_THRESHOLD_LOWEST int4 not null, HM_DISPLAY_NAME varchar(255) not null, primary key (HM_OID));
create table DB_WCT.FLAG (F_OID int8 not null, F_NAME varchar(255) not null, F_RGB varchar(6) not null, F_COMPLEMENT_RGB varchar(6) not null, F_AGC_OID int8 not null, primary key (F_OID));
create table DB_WCT.INDICATOR_CRITERIA (IC_OID int8 not null, IC_NAME varchar(255) not null, IC_DESCRIPTION varchar(255), IC_UPPER_LIMIT_PERCENTAGE float8, IC_LOWER_LIMIT_PERCENTAGE float8, IC_UPPER_LIMIT float8, IC_LOWER_LIMIT float8, IC_AGC_OID int8 not null, primary key (IC_OID), IC_UNIT varchar(20) not null, IC_SHOW_DELTA bool not null, IC_ENABLE_REPORT bool not null);
create table DB_WCT.INDICATOR (I_OID int8 not null, I_IC_OID int8 not null, I_TI_OID int8 not null, I_NAME varchar(255) not null, I_FLOAT_VALUE float8, I_UPPER_LIMIT_PERCENTAGE float8, I_LOWER_LIMIT_PERCENTAGE float8, I_UPPER_LIMIT float8, I_LOWER_LIMIT float8, I_ADVICE varchar(255), I_JUSTIFICATION varchar(255), I_AGC_OID int8 not null , primary key (I_OID), I_UNIT varchar(20) not null, I_SHOW_DELTA bool not null, I_INDEX int4, I_DATE TIMESTAMP not null);
create table DB_WCT.INDICATOR_REPORT_LINE (IRL_OID int8, IRL_I_OID int8, IRL_LINE varchar(1024), IRL_INDEX int4);
create table DB_WCT.PO_H3_BLOCK_URL (PBU_PROF_OVER_OID int8 not null, PBU_FILTER varchar(255), PBU_IX int4 not null, primary key (PBU_PROF_OVER_OID, PBU_IX));
create table DB_WCT.PO_H3_INCLUDE_URL (PIU_PROF_OVER_OID int8 not null, PIU_FILTER varchar(255), PIU_IX int4 not null, primary key (PIU_PROF_OVER_OID, PIU_IX));


alter table DB_WCT.ABSTRACT_TARGET add constraint AT_NAME_AND_TYPE unique (AT_NAME, AT_OBJECT_TYPE);
alter table DB_WCT.ABSTRACT_TARGET add constraint FK_AT_DUBLIN_CORE_OID foreign key (AT_DUBLIN_CORE_OID) references DB_WCT.DUBLIN_CORE;
alter table DB_WCT.ABSTRACT_TARGET add constraint FK_T_PROF_OVERRIDE_OID foreign key (AT_PROF_OVERRIDE_OID) references DB_WCT.PROFILE_OVERRIDES;
alter table DB_WCT.ABSTRACT_TARGET add constraint FKB6DD784E5C2C497 foreign key (AT_OWNER_ID) references DB_WCT.WCTUSER;
alter table DB_WCT.ABSTRACT_TARGET add constraint FKB6DD784E3A83A603 foreign key (T_PROFILE_ID) references DB_WCT.PROFILE;
alter table DB_WCT.ABSTRACT_TARGET add constraint CHK_ACCESS_ZONE check (AT_ACCESS_ZONE in (0, 1, 2));
alter table DB_WCT.ABSTRACT_TARGET add constraint FK_AT_RR_OID foreign key (AT_RR_OID) references DB_WCT.REJECTION_REASON (RR_OID);	
alter table DB_WCT.ANNOTATIONS add constraint FK_NOTE_USER_OID foreign key (AN_USER_OID) references DB_WCT.WCTUSER;
alter table DB_WCT.ARC_HARVEST_FILE add constraint FK_AHR_ARC_HARVEST_RESULT_ID foreign key (AHF_ARC_HARVEST_RESULT_ID) references DB_WCT.ARC_HARVEST_RESULT;
alter table DB_WCT.ARC_HARVEST_RESOURCE add constraint FK6D84FEB12FF8F14B foreign key (AHRC_HARVEST_RESOURCE_OID) references DB_WCT.HARVEST_RESOURCE;
alter table DB_WCT.ARC_HARVEST_RESULT add constraint FKE39C5380C88A38D9 foreign key (AHRS_HARVEST_RESULT_OID) references DB_WCT.HARVEST_RESULT;
alter table DB_WCT.BANDWIDTH_RESTRICTIONS add constraint CHK_DAY check (br_day::text = ANY (ARRAY['MONDAY'::character varying, 'TUESDAY'::character varying, 'WEDNESDAY'::character varying, 'THURSDAY'::character varying, 'FRIDAY'::character varying, 'SATURDAY'::character varying, 'SUNDAY'::character varying]::text[]));
alter table DB_WCT.BANDWIDTH_RESTRICTIONS add constraint CHK_END_TIME check (br_end_time >= '1972-11-09 00:00:00'::timestamp without time zone AND br_end_time <= '1972-11-09 23:59:59'::timestamp without time zone);
alter table DB_WCT.BANDWIDTH_RESTRICTIONS add constraint CHK_START_TIME check (br_start_time >= '1972-11-09 00:00:00'::timestamp without time zone AND br_start_time <= '1972-11-09 23:59:59'::timestamp without time zone);
alter table DB_WCT.GROUP_MEMBER add constraint FK_GM_PARENT_ID foreign key (GM_PARENT_ID) references DB_WCT.TARGET_GROUP;
alter table DB_WCT.GROUP_MEMBER add constraint FK_GM_CHILD_ID foreign key (GM_CHILD_ID) references DB_WCT.ABSTRACT_TARGET;
alter table DB_WCT.HARVEST_RESOURCE add constraint FK5BA2B04431A1C148 foreign key (HRC_HARVEST_RESULT_OID) references DB_WCT.HARVEST_RESULT;
alter table DB_WCT.HARVEST_RESULT add constraint FK_HR_CREATED_BY_ID foreign key (HR_CREATED_BY_ID) references DB_WCT.WCTUSER;
alter table DB_WCT.HARVEST_RESULT add constraint FK_HRC_TARGET_INSTANCE_ID foreign key (HR_TARGET_INSTANCE_ID) references DB_WCT.TARGET_INSTANCE;
alter table DB_WCT.HARVEST_RESULT add constraint FK_HR_RR_OID foreign key (HR_RR_OID) references DB_WCT.REJECTION_REASON (RR_OID);
alter table DB_WCT.HR_MODIFICATION_NOTE add constraint FKA908CCC03E1474AF foreign key (HMN_HR_OID) references DB_WCT.HARVEST_RESULT;
alter table DB_WCT.PERMISSION add constraint FKFE0FB1CFEE52493C foreign key (PE_AUTH_AGENT_ID) references DB_WCT.AUTHORISING_AGENT;
alter table DB_WCT.PERMISSION add constraint FKFE0FB1CFA1E5D89A foreign key (PE_OWNING_AGENCY_ID) references DB_WCT.AGENCY;
alter table DB_WCT.PERMISSION add constraint FK_PE_SITE_ID foreign key (PE_SITE_ID) references DB_WCT.SITE;
alter table DB_WCT.PERMISSION_EXCLUSION add constraint FK2DB3C33EB558CEC7 foreign key (PEX_PERMISSION_OID) references DB_WCT.PERMISSION;
alter table DB_WCT.PERMISSION_TEMPLATE add constraint FK_TEMPLATE_AGENCY_OID foreign key (PRT_AGC_OID) references DB_WCT.AGENCY;
alter table DB_WCT.PERMISSION_URLPATTERN add constraint PU_FK_1 foreign key (PU_URLPATTERN_ID) references DB_WCT.URL_PATTERN;
alter table DB_WCT.PERMISSION_URLPATTERN add constraint PU_FK_2 foreign key (PU_PERMISSION_ID) references DB_WCT.PERMISSION;
alter table DB_WCT.PO_EXCLUSION_URI add constraint FKFF4AB0FBBC3C926 foreign key (PEU_PROF_OVER_OID) references DB_WCT.PROFILE_OVERRIDES;
alter table DB_WCT.PO_INCLUSION_URI add constraint FK6C665549BC3C926 foreign key (PEU_PROF_OVER_OID) references DB_WCT.PROFILE_OVERRIDES;
alter table DB_WCT.PROFILE add constraint FK_P_AGENCY_OID foreign key (P_AGECNY_OID) references DB_WCT.AGENCY;
alter table DB_WCT.PROFILE_BASIC_CREDENTIALS add constraint FKE72A5AF5BD9AB61 foreign key (PBC_PC_OID) references DB_WCT.PROFILE_CREDENTIALS;
alter table DB_WCT.PROFILE_CREDENTIALS add constraint FK317A252682C63D7F foreign key (PC_PROFILE_OVERIDE_OID) references DB_WCT.PROFILE_OVERRIDES;
alter table DB_WCT.PROFILE_FORM_CREDENTIALS add constraint FK6B1303D750209B71 foreign key (PRC_PC_OID) references DB_WCT.PROFILE_CREDENTIALS;
alter table DB_WCT.REJECTION_REASON add constraint FK_RR_AGENCY_OID foreign key (RR_AGC_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.REJECTION_REASON add constraint RR_NAME_AND_AGENCY unique (RR_NAME, RR_AGC_OID);
alter table DB_WCT.ROLE_PRIVILEGE add constraint FK_PRIV_ROLE_OID foreign key (PRV_ROLE_OID) references DB_WCT.WCTROLE;
alter table DB_WCT.SCHEDULE add constraint FK50C8297BE49544D foreign key (S_ABSTRACT_TARGET_ID) references DB_WCT.ABSTRACT_TARGET;
alter table DB_WCT.SCHEDULE add constraint FK_S_OWNER_OID foreign key (S_OWNER_OID) references DB_WCT.WCTUSER;
alter table DB_WCT.SCHEDULE add constraint FK_S_TARGET_ID foreign key (S_TARGET_ID) references DB_WCT.ABSTRACT_TARGET;
alter table DB_WCT.SEED add constraint FK_SEED_TARGET_ID foreign key (S_TARGET_ID) references DB_WCT.TARGET;
alter table DB_WCT.SEED_HISTORY add constraint FK_SEED_HISTORY_TI_OID foreign key (SH_TI_OID) references DB_WCT.TARGET_INSTANCE (TI_OID);
alter table DB_WCT.SEED_PERMISSION add constraint FK9659805D68A4F4BA foreign key (SP_SEED_ID) references DB_WCT.SEED;
alter table DB_WCT.SEED_PERMISSION add constraint FK_SP_PERMISSION_ID foreign key (SP_PERMISSION_ID) references DB_WCT.PERMISSION;
alter table DB_WCT.SIP_PART_ELEMENT add constraint FK4998B1F5F51BBD3F foreign key (SPE_TARGET_INSTANCE_OID) references DB_WCT.TARGET_INSTANCE;
alter table DB_WCT.SITE add constraint FK_OWNING_AGENCY_ID foreign key (ST_OWNING_AGENCY_ID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.SITE_AUTH_AGENCY add constraint FKDF808D84C049BBEB foreign key (SA_SITE_ID) references DB_WCT.SITE;
alter table DB_WCT.SITE_AUTH_AGENCY add constraint FK_SA_AGENT_ID foreign key (SA_AGENT_ID) references DB_WCT.AUTHORISING_AGENT;
alter table DB_WCT.TARGET add constraint FK931165917947B83E foreign key (T_AT_OID) references DB_WCT.ABSTRACT_TARGET;
alter table DB_WCT.TARGET_GROUP add constraint FKB444963161DABD5F foreign key (TG_AT_OID) references DB_WCT.ABSTRACT_TARGET;
alter table DB_WCT.TARGET_INSTANCE add constraint FK_TI_TARGET_ID foreign key (TI_TARGET_ID) references DB_WCT.ABSTRACT_TARGET;
alter table DB_WCT.TARGET_INSTANCE add constraint FK_TI_USER_ID foreign key (TI_OWNER_ID) references DB_WCT.WCTUSER;
alter table DB_WCT.TARGET_INSTANCE add constraint FK_TI_PROF_OVERRIDE_OID foreign key (TI_PROF_OVERRIDE_OID) references DB_WCT.PROFILE_OVERRIDES;
alter table DB_WCT.TARGET_INSTANCE add constraint FK_TI_SCHEDULE_ID foreign key (TI_SCHEDULE_ID) references DB_WCT.SCHEDULE;
alter table DB_WCT.TARGET_INSTANCE_ORIG_SEED add constraint FKD47ACFF36748402E foreign key (TIOS_TI_OID) references DB_WCT.TARGET_INSTANCE;
alter table DB_WCT.TASK add constraint FK_TASK_AGENCY_OID foreign key (TSK_AGC_OID) references DB_WCT.AGENCY;
alter table DB_WCT.URL_PATTERN add constraint FK_UP_SITE_ID foreign key (UP_SITE_ID) references DB_WCT.SITE;
alter table DB_WCT.URL_PERMISSION_MAPPING add constraint FK_UPM_URL_PATTERN_ID foreign key (UPM_URL_PATTERN_ID) references DB_WCT.URL_PATTERN;
alter table DB_WCT.URL_PERMISSION_MAPPING add constraint FK_UPM_PERMISSION_ID foreign key (UPM_PERMISSION_ID) references DB_WCT.PERMISSION;
alter table DB_WCT.WCTAUDIT add constraint FK_AUD_USER_OID foreign key (AUD_USER_OID) references DB_WCT.WCTUSER (USR_OID);
alter table DB_WCT.WCTAUDIT add constraint FK_AUD_AGENCY_OID foreign key (AUD_AGENCY_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.USER_ROLE add constraint FK_USERROLE_TO_ROLE foreign key (URO_ROL_OID) references DB_WCT.WCTROLE;
alter table DB_WCT.USER_ROLE add constraint FK_USERROLE_TO_USER foreign key (URO_USR_OID) references DB_WCT.WCTUSER;
alter table DB_WCT.WCTROLE add constraint FK_ROLE_AGENCY_OID foreign key (ROL_AGENCY_OID) references DB_WCT.AGENCY;
alter table DB_WCT.WCTUSER add constraint FK_USER_AGENCY_OID foreign key (USR_AGC_OID) references DB_WCT.AGENCY;
alter table DB_WCT.FLAG add constraint FK_F_AGENCY_OID foreign key (F_AGC_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.TARGET_INSTANCE add constraint FK_F_OID foreign key (TI_FLAG_OID) references DB_WCT.FLAG (F_OID);
alter table DB_WCT.INDICATOR_CRITERIA add constraint FK_IC_AGENCY_OID foreign key (IC_AGC_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.INDICATOR add constraint FK_I_TI_OID foreign key (I_TI_OID) references DB_WCT.TARGET_INSTANCE (TI_OID) on delete cascade;
alter table DB_WCT.INDICATOR add constraint FK_I_IC_OID foreign key (I_IC_OID) references DB_WCT.INDICATOR_CRITERIA (IC_OID);
alter table DB_WCT.INDICATOR add constraint FK_I_AGENCY_OID foreign key (I_AGC_OID) references DB_WCT.AGENCY (AGC_OID);
alter table DB_WCT.INDICATOR_REPORT_LINE add constraint FK_IRL_I_OID foreign key (IRL_I_OID) references DB_WCT.INDICATOR (I_OID);
alter table DB_WCT.PO_H3_BLOCK_URL add constraint PBU_FK_1 foreign key (PBU_PROF_OVER_OID) references DB_WCT.PROFILE_OVERRIDES;
alter table DB_WCT.PO_H3_INCLUDE_URL add constraint PIU_FK_1 foreign key (PIU_PROF_OVER_OID) references DB_WCT.PROFILE_OVERRIDES;

create table DB_WCT.ID_GENERATOR ( IG_TYPE varchar(255),  IG_VALUE int4 ) ;

create view DB_WCT.URL_PERMISSION_MAPPING_VIEW as 
 SELECT upm.upm_oid, upm.upm_domain, p.pe_oid, p.pe_end_date, p.pe_owning_agency_id, up.up_pattern, st.st_active
   FROM DB_WCT.URL_PERMISSION_MAPPING upm
   JOIN DB_WCT.PERMISSION p ON upm.upm_permission_id = p.pe_oid
   JOIN DB_WCT.URL_PATTERN up ON upm.upm_url_pattern_id = up.up_oid
   JOIN DB_WCT.SITE st ON p.pe_site_id = st.st_oid;

create view DB_WCT.ABSTRACT_TARGET_SCHEDULE_VIEW as 
 SELECT (abt.at_oid::character varying::text || ','::text) || s.s_oid::character varying::text AS thekey, 
        CASE abt.at_object_type
            WHEN 1 THEN 'Target'::text
            ELSE 'Group'::text
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
insert into DB_WCT.HEATMAP_CONFIG (HM_OID, HM_NAME, HM_DISPLAY_NAME, HM_COLOR, HM_THRESHOLD_LOWEST) values (1, 'low','Low','8FBC8F',1);
insert into DB_WCT.HEATMAP_CONFIG (HM_OID, HM_NAME, HM_DISPLAY_NAME, HM_COLOR, HM_THRESHOLD_LOWEST) values (2, 'medium','Medium','F0E68C',7);
insert into DB_WCT.HEATMAP_CONFIG (HM_OID, HM_NAME, HM_DISPLAY_NAME, HM_COLOR, HM_THRESHOLD_LOWEST) values (3, 'high','High','FF6347',12);
