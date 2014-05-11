ALTER TABLE db_wct.abstract_target ADD COLUMN AT_DISPLAY_CHG_REASON varchar(1000);
ALTER TABLE db_wct.annotations ADD COLUMN AN_ALERTABLE boolean DEFAULT FALSE NOT NULL;
ALTER TABLE db_wct.site ADD COLUMN ST_CREATION_DATE timestamp without time zone;
ALTER TABLE db_wct.target_instance ADD COLUMN TI_FIRST_FROM_TARGET boolean DEFAULT FALSE NOT NULL;
ALTER TABLE db_wct.target_instance ADD COLUMN TI_DISPLAY_CHG_REASON varchar(1000);
ALTER TABLE db_wct.target ALTER COLUMN T_EVALUATION_NOTE TYPE varchar(1000);
ALTER TABLE db_wct.target ALTER COLUMN T_SELECTION_NOTE TYPE varchar(1000);

ALTER TABLE db_wct.abstract_target DROP CONSTRAINT abstract_target_at_name_key;
ALTER TABLE db_wct.abstract_target ADD CONSTRAINT AT_NAME_AND_TYPE UNIQUE (AT_NAME, AT_OBJECT_TYPE);

CREATE INDEX IX_AUD_DATE ON db_wct.wctaudit(AUD_DATE);
ALTER TABLE db_wct.wctaudit ADD CONSTRAINT FK_AUD_USER_OID foreign key (AUD_USER_OID) references db_wct.WCTUSER (USR_OID);
ALTER TABLE db_wct.wctaudit ADD CONSTRAINT FK_AUD_AGENCY_OID foreign key (AUD_AGENCY_OID) references db_wct.AGENCY (AGC_OID);

update db_wct.site set st_creation_date = (select min(pe_creation_date) from db_wct.permission where pe_site_id=st_oid);

CREATE OR REPLACE VIEW db_wct.abstract_target_grouptype_view AS 
 SELECT a.at_oid, a.at_desc, a.at_name, a.at_owner_id, a.at_prof_override_oid, a.at_state, a.t_profile_id, a.at_object_type, a.at_creation_date, a.at_reference, a.at_profile_note, a.at_dublin_core_oid, a.at_access_zone, a.at_display_target, a.at_display_note, tg.tg_type
   FROM db_wct.abstract_target a
   LEFT JOIN db_wct.target_group tg ON a.at_oid = tg.tg_at_oid;

ALTER TABLE db_wct.abstract_target_grouptype_view OWNER TO postgres;
GRANT ALL ON TABLE db_wct.abstract_target_grouptype_view TO postgres;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE db_wct.abstract_target_grouptype_view TO usr_wct;

ALTER TABLE db_wct.harvest_status ADD COLUMN HS_APP_VERSION varchar(255);
ALTER TABLE db_wct.harvest_status ADD COLUMN HS_HRTX_VERSION varchar(255);

ALTER TABLE db_wct.schedule ADD COLUMN S_LAST_PROCESSED_DATE timestamp without time zone default '2001-01-01 00:00:00';
CREATE INDEX IX_SHED_PROC_DATE ON db_wct.schedule(S_LAST_PROCESSED_DATE);
CREATE INDEX IX_SHED_NEXT_TIME ON db_wct.schedule(S_NEXT_SCHEDULE_TIME);

