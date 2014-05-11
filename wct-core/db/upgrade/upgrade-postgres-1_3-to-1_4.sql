-- New Column: db_wct.site.st_owning_agency_id
ALTER TABLE db_wct.site ADD COLUMN st_owning_agency_id bigint;
ALTER TABLE db_wct.site
  ADD CONSTRAINT fk_owning_agency_id FOREIGN KEY (st_owning_agency_id)
      REFERENCES db_wct.agency (agc_oid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- New Column: db_wct.target_instance.ti_flagged
ALTER TABLE db_wct.target_instance ADD COLUMN ti_flagged boolean NOT NULL DEFAULT false;
CREATE INDEX idx_flagged
   ON db_wct.target_instance (ti_flagged);
ALTER TABLE db_wct.target_instance CLUSTER ON idx_flagged;

--bandwidth_restrictions bug - new check constraints
-- Check: db_wct.bandwidth_restrictions.chk_day

ALTER TABLE db_wct.bandwidth_restrictions
  ADD CONSTRAINT chk_day CHECK (br_day::text = ANY (ARRAY['MONDAY'::character varying, 'TUESDAY'::character varying, 'WEDNESDAY'::character varying, 'THURSDAY'::character varying, 'FRIDAY'::character varying, 'SATURDAY'::character varying, 'SUNDAY'::character varying]::text[]));

-- Check: db_wct.bandwidth_restrictions.chk_end_time

ALTER TABLE db_wct.bandwidth_restrictions
  ADD CONSTRAINT chk_end_time CHECK (br_end_time >= '1972-11-09 00:00:00'::timestamp without time zone AND br_end_time <= '1972-11-09 23:59:59'::timestamp without time zone);

-- Check: db_wct.bandwidth_restrictions.chk_start_time

ALTER TABLE db_wct.bandwidth_restrictions
  ADD CONSTRAINT chk_start_time CHECK (br_start_time >= '1972-11-09 00:00:00'::timestamp without time zone AND br_start_time <= '1972-11-09 23:59:59'::timestamp without time zone);

-- View: "db_wct.url_permission_mapping_view"

-- DROP VIEW db_wct.url_permission_mapping_view;

CREATE OR REPLACE VIEW db_wct.url_permission_mapping_view AS 
 SELECT upm.upm_oid, upm.upm_domain, p.pe_oid, p.pe_end_date, p.pe_owning_agency_id, up.up_pattern, st.st_active
   FROM db_wct.url_permission_mapping upm
   JOIN db_wct.permission p ON upm.upm_permission_id = p.pe_oid
   JOIN db_wct.url_pattern up ON upm.upm_url_pattern_id = up.up_oid
   JOIN db_wct.site st ON p.pe_site_id = st.st_oid;

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE db_wct.url_permission_mapping_view TO usr_wct;

-- View: "db_wct.abstract_target_schedule_view"

-- DROP VIEW db_wct.abstract_target_schedule_view;

CREATE OR REPLACE VIEW db_wct.abstract_target_schedule_view AS 
 SELECT (abt.at_oid::character varying::text || ','::text) || s.s_oid::character varying::text AS thekey, 
        CASE abt.at_object_type
            WHEN 1 THEN 'Target'::text
            ELSE 'Group'::text
        END AS at_object_type_desc, abt.at_name, abt.at_state, u.usr_username, a.agc_name, s.s_oid, s.s_start, s.s_end, s.s_type, s.s_cron
   FROM db_wct.abstract_target abt
   RIGHT JOIN db_wct.schedule s ON s.s_abstract_target_id = abt.at_oid
   JOIN db_wct.wctuser u ON abt.at_owner_id = u.usr_oid
   JOIN db_wct.agency a ON u.usr_agc_oid = a.agc_oid
  ORDER BY abt.at_name, s.s_oid;

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE db_wct.abstract_target_schedule_view TO usr_wct;

-- Add a new original oid column to the profile table
ALTER TABLE db_wct.profile ADD COLUMN p_orig_oid bigint;

--Add new profile id column to target instance
ALTER TABLE db_wct.target_instance ADD COLUMN ti_profile_id bigint;

