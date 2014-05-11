create table db_wct.rejection_reason (RR_OID number(19,0) not null, RR_NAME varchar2(100) not null, RR_AVAILABLE_FOR_TARGET number(1,0) default 0 not null, RR_AVAILABLE_FOR_TI number(1,0) default 0 not null, RR_AGC_OID number(19,0) not null, primary key (RR_OID));
alter table db_wct.rejection_reason add constraint FK_RR_AGENCY_OID foreign key (RR_AGC_OID) references db_wct.agency (agc_oid) match SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
alter table db_wct.rejection_reason add constraint RR_NAME_AND_AGENCY unique (RR_NAME, RR_AGC_OID);


GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE db_wct.rejection_reason TO usr_wct;

ALTER TABLE db_wct.harvest_result ADD COLUMN hr_rr_oid number(19,0);

ALTER TABLE db_wct.harvest_result ADD CONSTRAINT fk_hr_rr_oid FOREIGN KEY (hr_rr_oid)
      REFERENCES db_wct.rejection_reason (rr_oid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
	  
ALTER TABLE db_wct.abstract_target ADD COLUMN at_rr_oid number(19,0);

ALTER TABLE db_wct.abstract_target ADD CONSTRAINT fk_at_rr_oid FOREIGN KEY (at_rr_oid)
      REFERENCES db_wct.rejection_reason (rr_oid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;	  
	  
ALTER TABLE db_wct.target ADD (T_USE_AQA NUMBER(1,0) DEFAULT 0 NOT NULL);
ALTER TABLE db_wct.target_instance ADD (TI_USE_AQA NUMBER(1,0) DEFAULT 0 NOT NULL);

ALTER TABLE db_wct.target_instance_orig_seed MODIFY TIOS_SEED VARCHAR2(1024) NULL;

-- The following query is optional and may be used to update the database of targets that are incorrectly set to complete after harvesting.
-- This is a bug arisig in release 1.5.1 and is fixed in the next version. Run at your own peril!
-- NB: The syntax of this query is tested on Postgres but may need adjusting for Oracle databases.
UPDATE abstract_target at SET at_state = 5 WHERE at_state = 7 
AND EXISTS(SELECT s_oid FROM schedule WHERE s_target_id = at.at_oid AND (s_end IS NULL OR s_end > now()));

 


