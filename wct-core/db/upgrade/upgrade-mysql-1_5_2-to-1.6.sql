-- WCT 1.6 UPGRADE   
alter table db_wct.TARGET_INSTANCE add column TI_FLAG_OID bigint;
alter table db_wct.TARGET_INSTANCE add column TI_RECOMMENDATION varchar(255);
create table db_wct.FLAG (F_OID bigint not null, F_NAME varchar(255) not null, F_RGB varchar(6) not null, F_COMPLEMENT_RGB varchar(6) not null, F_AGC_OID bigint not null, primary key (F_OID));
alter table db_wct.FLAG add constraint FK_F_AGENCY_OID foreign key (F_AGC_OID) references db_wct.AGENCY (AGC_OID);
alter table db_wct.TARGET_INSTANCE add constraint FK_F_OID foreign key (TI_FLAG_OID) references db_wct.FLAG (F_OID);
create table db_wct.INDICATOR_CRITERIA (IC_OID bigint not null, IC_NAME varchar(255) not null, IC_DESCRIPTION varchar(255), IC_UPPER_LIMIT_PERCENTAGE double precision, IC_LOWER_LIMIT_PERCENTAGE double precision, IC_UPPER_LIMIT double precision, IC_LOWER_LIMIT double precision, IC_AGC_OID bigint not null, primary key (IC_OID), IC_UNIT varchar(20) not null, IC_SHOW_DELTA bit not null, IC_ENABLE_REPORT bit not null);
alter table db_wct.INDICATOR_CRITERIA add constraint FK_IC_AGENCY_OID foreign key (IC_AGC_OID) references db_wct.AGENCY (AGC_OID);
create table db_wct.INDICATOR (I_OID bigint not null, I_IC_OID bigint not null, I_TI_OID bigint not null, I_NAME varchar(255) not null, I_FLOAT_VALUE double precision, I_UPPER_LIMIT_PERCENTAGE double precision, I_LOWER_LIMIT_PERCENTAGE double precision, I_UPPER_LIMIT double precision, I_LOWER_LIMIT double precision, I_ADVICE varchar(255), I_JUSTIFICATION varchar(255), I_AGC_OID bigint not null , primary key (I_OID), I_UNIT varchar(20) not null, I_SHOW_DELTA bit not null, I_INDEX integer, I_DATE TIMESTAMP(9) not null);
alter table db_wct.INDICATOR add constraint FK_I_TI_OID foreign key (I_TI_OID) references db_wct.TARGET_INSTANCE (TI_OID) on delete cascade;
alter table db_wct.INDICATOR add constraint FK_I_IC_OID foreign key (I_IC_OID) references db_wct.INDICATOR_CRITERIA (IC_OID);
alter table db_wct.INDICATOR add constraint FK_I_AGENCY_OID foreign key (I_AGC_OID) references db_wct.AGENCY (AGC_OID);
create table db_wct.INDICATOR_REPORT_LINE (IRL_OID bigint, IRL_I_OID bigint, IRL_LINE varchar(1024), IRL_INDEX integer);
alter table db_wct.INDICATOR_REPORT_LINE add constraint FK_IRL_I_OID foreign key (IRL_I_OID) references db_wct.INDICATOR (I_OID);
alter table db_wct.ABSTRACT_TARGET add column AT_CRAWLS bigint;
alter table db_wct.ABSTRACT_TARGET add column AT_REFERENCE_CRAWL_OID bigint;
alter table db_wct.ABSTRACT_TARGET add column AT_AUTO_PRUNE bit not null default 0;
alter table db_wct.ABSTRACT_TARGET add column AT_AUTO_DENOTE_REFERENCE_CRAWL bit not null default 0;
alter table DB_WCT.ABSTRACT_TARGET add column AT_REQUEST_TO_ARCHIVISTS varchar(4000);
-- Commented as they produce errors, and appear duplicated with the statements following..
-- GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.INDICATOR TO USR_WCT;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.INDICATOR_CRITERIA TO USR_WCT;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.INDICATOR_REPORT_LINE TO USR_WCT;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.FLAG TO USR_WCT;
-- CREATE UNIQUE INDEX IX_I_OID ON DB_WCT.INDICATOR(I_OID);
-- CREATE UNIQUE INDEX IX_IRL_OID ON DB_WCT.INDICATOR_REPORT_LINE(IRL_OID);
-- CREATE INDEX IX_IRL_I_OID ON DB_WCT.INDICATOR_REPORT_LINE(IRL_I_OID);

-- The following 7 lines are a duplicate of the above with changes to make them work
GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.INDICATOR TO usr_wct@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.INDICATOR_CRITERIA TO usr_wct@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.INDICATOR_REPORT_LINE TO usr_wct@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.FLAG TO usr_wct@localhost;
CREATE UNIQUE INDEX IX_I_OID ON DB_WCT.INDICATOR(I_OID);
CREATE UNIQUE INDEX IX_IRL_OID ON DB_WCT.INDICATOR_REPORT_LINE(IRL_OID);
CREATE INDEX IX_IRL_I_OID ON DB_WCT.INDICATOR_REPORT_LINE(IRL_I_OID);

-- added for performance	
CREATE INDEX IX_TI_TARGET_ID ON DB_WCT.TARGET_INSTANCE(TI_TARGET_ID);
CREATE INDEX IX_TI_SCHEDULE_ID ON DB_WCT.TARGET_INSTANCE(TI_SCHEDULE_ID);
CREATE INDEX IX_TI_PROFILE_ID ON DB_WCT.TARGET_INSTANCE(TI_PROFILE_ID);
CREATE INDEX IX_TI_FLAG_OID ON DB_WCT.TARGET_INSTANCE(TI_FLAG_OID);
CREATE INDEX IX_HR_TARGET_INSTANCE_ID ON DB_WCT.HARVEST_RESULT(HR_TARGET_INSTANCE_ID);
CREATE INDEX IX_S_TARGET_ID ON DB_WCT.SEED(S_TARGET_ID);
CREATE INDEX IX_PU_PERMISSION_ID ON DB_WCT.PERMISSION_URLPATTERN(PU_PERMISSION_ID);
CREATE INDEX IX_AHF_ARC_HARVEST_RESULT_ID ON DB_WCT.ARC_HARVEST_FILE(AHF_ARC_HARVEST_RESULT_ID);
CREATE INDEX IX_TIOS_TI_OID ON DB_WCT.TARGET_INSTANCE_ORIG_SEED(TIOS_TI_OID);
CREATE INDEX IX_SH_TI_OID ON DB_WCT.SEED_HISTORY(SH_TI_OID);
