CREATE TABLE db_wct.rejection_reason (rr_oid bigint NOT NULL, rr_name varchar(100) NOT NULL, rr_available_for_target bit default 0 not null, rr_available_for_ti bit default 0 not null, rr_agc_oid bigint NOT NULL, primary key (rr_oid));

alter table db_wct.rejection_reason add index fk_rr_agency_oid (rr_agc_oid), add constraint fk_rr_agency_oid foreign key (rr_agc_oid) references db_wct.agency (agc_oid);
alter table db_wct.rejection_reason add unique key rr_name_and_agency (rr_name, rr_agc_oid);

ALTER TABLE db_wct.harvest_result ADD COLUMN hr_rr_oid bigint;

ALTER TABLE db_wct.harvest_result add index fk_hr_rr_oid (hr_rr_oid), ADD CONSTRAINT fk_hr_rr_oid FOREIGN KEY (hr_rr_oid) REFERENCES db_wct.rejection_reason (rr_oid);
	  
ALTER TABLE db_wct.abstract_target ADD COLUMN at_rr_oid bigint;

ALTER TABLE db_wct.abstract_target add index fk_at_rr_oid (at_rr_oid), ADD CONSTRAINT fk_at_rr_oid FOREIGN KEY (at_rr_oid) REFERENCES db_wct.rejection_reason (rr_oid);
	  
ALTER TABLE db_wct.target ADD COLUMN T_USE_AQA BIT(1) NOT NULL DEFAULT b'0';
ALTER TABLE db_wct.target_instance ADD COLUMN TI_USE_AQA BIT(1) NOT NULL DEFAULT b'0';

ALTER TABLE db_wct.target_instance_orig_seed MODIFY COLUMN TIOS_SEED VARCHAR(1024) DEFAULT NULL;


-- The following query is optional and may be used to update the database of targets that are incorrectly set to complete after harvesting.
-- This is a bug arisig in release 1.5.1 and is fixed in the next version. Run at your own peril!
-- NB: The syntax of this query is tested on Postgres but may need adjusting for MySQL databases.
UPDATE abstract_target at SET at_state = 5 WHERE at_state = 7 
AND EXISTS(SELECT s_oid FROM schedule WHERE s_target_id = at.at_oid AND (s_end IS NULL OR s_end > now()));


