
ALTER TABLE DB_WCT.PERMISSION_template ADD COLUMN prt_template_subject character varying(255);
ALTER TABLE DB_WCT.PERMISSION_template ADD COLUMN prt_template_overwrite_from boolean DEFAULT FALSE NOT NULL;
ALTER TABLE DB_WCT.PERMISSION_template ADD COLUMN prt_template_from character varying(255);
ALTER TABLE DB_WCT.PERMISSION_template ADD COLUMN prt_template_cc character varying(2048);
ALTER TABLE DB_WCT.PERMISSION_template ADD COLUMN prt_template_bcc character varying(2048);

UPDATE DB_WCT.PERMISSION_template SET prt_template_subject = 'Web Preservation Programme' WHERE prt_template_Type like 'E%' ;

CREATE TABLE DB_WCT.SEED_HISTORY (SH_OID bigint not null, SH_TI_OID bigint, SH_SEED character varying(1024) not null, SH_PRIMARY boolean not null, primary key (SH_OID));
ALTER TABLE DB_WCT.SEED_HISTORY add constraint FK_SEED_HISTORY_TI_OID foreign key (SH_TI_OID) references DB_WCT.TARGET_INSTANCE (TI_OID);
GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.SEED_HISTORY TO USR_WCT;

ALTER TABLE DB_WCT.TARGET_INSTANCE ADD COLUMN ti_archived_time timestamp without time zone;


