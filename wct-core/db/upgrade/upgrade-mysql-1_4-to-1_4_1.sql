ALTER TABLE db_wct.permission_template ADD COLUMN prt_template_subject varchar(255);
ALTER TABLE db_wct.permission_template ADD COLUMN prt_template_overwrite_from bit DEFAULT 0 NOT NULL;
ALTER TABLE db_wct.permission_template ADD COLUMN prt_template_from varchar(255);
ALTER TABLE db_wct.permission_template ADD COLUMN prt_template_cc text;
ALTER TABLE db_wct.permission_template ADD COLUMN prt_template_bcc text;

UPDATE db_wct.permission_template SET prt_template_subject = 'Web Preservation Programme' WHERE prt_template_Type like 'E%' ;

CREATE TABLE db_wct.SEED_HISTORY (SH_OID bigint not null, SH_TI_OID bigint, SH_SEED text not null, SH_PRIMARY bit not null, primary key (SH_OID));
ALTER TABLE db_wct.SEED_HISTORY add index FK_SEED_HISTORY_TI_OID (SH_TI_OID), add constraint FK_SEED_HISTORY_TI_OID foreign key (SH_TI_OID) references db_wct.TARGET_INSTANCE (TI_OID);
GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.SEED_HISTORY TO usr_wct@localhost;

ALTER TABLE db_wct.target_instance ADD COLUMN ti_archived_time datetime;


