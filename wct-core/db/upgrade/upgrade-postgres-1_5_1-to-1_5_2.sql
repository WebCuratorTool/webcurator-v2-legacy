create table db_wct.rejection_reason (RR_OID int8 not null, RR_NAME varchar(100) not null, RR_AVAILABLE_FOR_TARGET bool default false not null, RR_AVAILABLE_FOR_TI bool default false not null, RR_AGC_OID int8 not null, primary key (RR_OID));
alter table db_wct.rejection_reason add constraint FK_RR_AGENCY_OID foreign key (RR_AGC_OID) references db_wct.agency (agc_oid) match SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
alter table db_wct.rejection_reason add constraint RR_NAME_AND_AGENCY unique (RR_NAME, RR_AGC_OID);

ALTER TABLE db_wct.rejection_reason OWNER TO postgres;
GRANT ALL ON TABLE db_wct.rejection_reason TO postgres;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE db_wct.rejection_reason TO usr_wct;

ALTER TABLE db_wct.harvest_result ADD COLUMN hr_rr_oid int8;

ALTER TABLE db_wct.harvest_result ADD CONSTRAINT fk_hr_rr_oid FOREIGN KEY (hr_rr_oid)
      REFERENCES db_wct.rejection_reason (rr_oid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
	  
ALTER TABLE db_wct.abstract_target ADD COLUMN at_rr_oid int8;

ALTER TABLE db_wct.abstract_target ADD CONSTRAINT fk_at_rr_oid FOREIGN KEY (at_rr_oid)
      REFERENCES db_wct.rejection_reason (rr_oid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;	  
	  
ALTER TABLE db_wct.target ADD COLUMN T_USE_AQA boolean DEFAULT FALSE NOT NULL;
ALTER TABLE db_wct.target_instance ADD COLUMN TI_USE_AQA boolean DEFAULT FALSE NOT NULL;

ALTER TABLE db_wct.target_instance_orig_seed ALTER COLUMN TIOS_SEED TYPE varchar(1024);

-- The following query is optional and may be used to update the database of targets that are incorrectly set to complete after harvesting.
-- This is a bug arisig in release 1.5.1 and is fixed in the next version. Run at your own peril!
UPDATE abstract_target at SET at_state = 5 WHERE at_state = 7 
AND EXISTS(SELECT s_oid FROM schedule WHERE s_target_id = at.at_oid AND (s_end IS NULL OR s_end > now()));

 


