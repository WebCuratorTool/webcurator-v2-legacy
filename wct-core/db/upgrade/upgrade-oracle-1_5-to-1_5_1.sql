ALTER TABLE db_wct.abstract_target ADD (AT_DISPLAY_CHG_REASON varchar2(1000));
ALTER TABLE db_wct.annotations ADD (AN_ALERTABLE NUMBER(1,0) DEFAULT 0 NOT NULL);
ALTER TABLE db_wct.site ADD (ST_CREATION_DATE TIMESTAMP(9));
ALTER TABLE db_wct.target_instance ADD (TI_FIRST_FROM_TARGET NUMBER(1,0) DEFAULT 0 NOT NULL);
ALTER TABLE db_wct.target_instance ADD (TI_DISPLAY_CHG_REASON varchar2(1000));
ALTER TABLE db_wct.target MODIFY T_EVALUATION_NOTE VARCHAR2(1000) NULL;
ALTER TABLE db_wct.target MODIFY T_SELECTION_NOTE VARCHAR2(1000) NULL;

ALTER TABLE db_wct.abstract_target DROP CONSTRAINT AT_NAME;
ALTER TABLE db_wct.abstract_target ADD CONSTRAINT AT_NAME_AND_TYPE UNIQUE (AT_NAME, AT_OBJECT_TYPE);

CREATE INDEX IX_AUD_DATE ON db_wct.wctaudit (AUD_DATE);
ALTER TABLE db_wct.wctaudit ADD CONSTRAINT FK_AUD_USER_OID foreign key (AUD_USER_OID) references db_wct.WCTUSER (USR_OID);
ALTER TABLE db_wct.wctaudit ADD CONSTRAINT FK_AUD_AGENCY_OID foreign key (AUD_AGENCY_OID) references db_wct.AGENCY (AGC_OID);

update db_wct.site s set s.st_creation_date = (select min(pe_creation_date) from permission where pe_site_id=s.st_oid);

CREATE OR REPLACE VIEW db_wct.abstract_target_grouptype_view AS 
 SELECT a.at_oid, a.at_desc, a.at_name, a.at_owner_id, a.at_prof_override_oid, a.at_state, a.t_profile_id, a.at_object_type, a.at_creation_date, a.at_reference, a.at_profile_note, a.at_dublin_core_oid, a.at_access_zone, a.at_display_target, a.at_display_note, tg.tg_type
   FROM db_wct.abstract_target a
   LEFT JOIN db_wct.target_group tg ON a.at_oid = tg.tg_at_oid;

GRANT SELECT, UPDATE, INSERT, DELETE ON db_wct.abstract_target_grouptype_view TO usr_wct;

ALTER TABLE db_wct.harvest_status ADD COLUMN HS_APP_VERSION varchar2(255);
ALTER TABLE db_wct.harvest_status ADD COLUMN HS_HRTX_VERSION varchar2(255);

ALTER TABLE db_wct.schedule ADD (S_LAST_PROCESSED_DATE TIMESTAMP(9) DEFAULT to_timestamp('2001-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'));
CREATE INDEX IX_SHED_PROC_DATE ON db_wct.schedule (S_LAST_PROCESSED_DATE);
CREATE INDEX IX_SHED_NEXT_TIME ON db_wct.schedule (S_NEXT_SCHEDULE_TIME);
