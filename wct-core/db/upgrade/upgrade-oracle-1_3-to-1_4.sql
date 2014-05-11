-- New Column: db_wct.site.st_owning_agency_id
ALTER TABLE db_wct.site ADD (st_owning_agency_id NUMBER(19,0));
ALTER TABLE db_wct.site
  ADD CONSTRAINT fk_owning_agency_id FOREIGN KEY (st_owning_agency_id)
      REFERENCES db_wct.agency (agc_oid);

-- New Column: db_wct.target_instance.ti_flagged
ALTER TABLE db_wct.target_instance ADD (ti_flagged NUMBER(1,0) DEFAULT 0 NOT NULL);
CREATE INDEX idx_flagged ON db_wct.target_instance (ti_flagged);

--bandwidth_restrictions bug - new check constraints
-- Check: db_wct.bandwidth_restrictions.chk_day

ALTER TABLE db_wct.bandwidth_restrictions
  ADD CONSTRAINT chk_day CHECK (br_day IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'));

-- Check: db_wct.bandwidth_restrictions.chk_end_time

ALTER TABLE db_wct.bandwidth_restrictions
  ADD CONSTRAINT chk_end_time CHECK (br_end_time >= TO_DATE('1972-11-09 00:00:00', 'YYYY-MM-DD HH24:MI:SS') AND br_end_time <= TO_DATE('1972-11-09 23:59:59', 'YYYY-MM-DD HH24:MI:SS'));

-- Check: db_wct.bandwidth_restrictions.chk_start_time

ALTER TABLE db_wct.bandwidth_restrictions
  ADD CONSTRAINT chk_start_time CHECK (br_start_time >= TO_DATE('1972-11-09 00:00:00', 'YYYY-MM-DD HH24:MI:SS') AND br_start_time <= TO_DATE('1972-11-09 23:59:59', 'YYYY-MM-DD HH24:MI:SS'));

-- View: "db_wct.url_permission_mapping_view"

CREATE OR REPLACE VIEW db_wct.url_permission_mapping_view AS 
 SELECT upm.upm_oid, upm.upm_domain, p.pe_oid, p.pe_end_date, p.pe_owning_agency_id, up.up_pattern, st.st_active
   FROM db_wct.url_permission_mapping upm
   JOIN db_wct.permission p ON upm.upm_permission_id = p.pe_oid
   JOIN db_wct.url_pattern up ON upm.upm_url_pattern_id = up.up_oid
   JOIN db_wct.site st ON p.pe_site_id = st.st_oid;

GRANT SELECT, UPDATE, INSERT, DELETE ON db_wct.url_permission_mapping_view TO usr_wct;

-- View: "db_wct.abstract_target_schedule_view"

CREATE OR REPLACE VIEW db_wct.abstract_target_schedule_view AS 
 SELECT (abt.at_oid || ',') || s.s_oid AS thekey, 
        CASE abt.at_object_type
            WHEN 1 THEN 'Target'
            ELSE 'Group'
        END AS at_object_type_desc, abt.at_name, abt.at_state, u.usr_username, a.agc_name, s.s_oid, s.s_start, s.s_end, s.s_type, s.s_cron
   FROM db_wct.abstract_target abt
   RIGHT JOIN db_wct.schedule s ON s.s_abstract_target_id = abt.at_oid
   JOIN db_wct.wctuser u ON abt.at_owner_id = u.usr_oid
   JOIN db_wct.agency a ON u.usr_agc_oid = a.agc_oid
  ORDER BY abt.at_name, s.s_oid;

GRANT SELECT, UPDATE, INSERT, DELETE ON db_wct.abstract_target_schedule_view TO usr_wct;

-- Add a new original oid column to the profile table
ALTER TABLE db_wct.profile ADD (p_orig_oid NUMBER(19,0));

--Add new profile id column to target instance
ALTER TABLE db_wct.target_instance ADD (ti_profile_id NUMBER(19,0));
