-- New Column: DB_WCT.SITE.st_owning_agency_id
ALTER TABLE DB_WCT.SITE ADD (st_owning_agency_id NUMBER(19,0));
ALTER TABLE DB_WCT.SITE
  ADD CONSTRAINT fk_owning_agency_id FOREIGN KEY (st_owning_agency_id)
      REFERENCES DB_WCT.AGENCY (agc_oid);

-- New Column: DB_WCT.TARGET_INSTANCE.ti_flagged
ALTER TABLE DB_WCT.TARGET_INSTANCE ADD (ti_flagged NUMBER(1,0) DEFAULT 0 NOT NULL);
CREATE INDEX idx_flagged ON DB_WCT.TARGET_INSTANCE (ti_flagged);

--bandwidth_restrictions bug - new check constraints
-- Check: DB_WCT.BANDWIDTH_RESTRICTIONS.CHK_DAY

ALTER TABLE DB_WCT.BANDWIDTH_RESTRICTIONS
  ADD CONSTRAINT chk_day CHECK (br_day IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'));

-- Check: DB_WCT.BANDWIDTH_RESTRICTIONS.CHK_END_TIME

ALTER TABLE DB_WCT.BANDWIDTH_RESTRICTIONS
  ADD CONSTRAINT chk_end_time CHECK (br_end_time >= TO_DATE('1972-11-09 00:00:00', 'YYYY-MM-DD HH24:MI:SS') AND br_end_time <= TO_DATE('1972-11-09 23:59:59', 'YYYY-MM-DD HH24:MI:SS'));

-- Check: DB_WCT.BANDWIDTH_RESTRICTIONS.CHK_START_TIME

ALTER TABLE DB_WCT.BANDWIDTH_RESTRICTIONS
  ADD CONSTRAINT chk_start_time CHECK (br_start_time >= TO_DATE('1972-11-09 00:00:00', 'YYYY-MM-DD HH24:MI:SS') AND br_start_time <= TO_DATE('1972-11-09 23:59:59', 'YYYY-MM-DD HH24:MI:SS'));

-- View: "DB_WCT.URL_PERMISSION_MAPPING_VIEW"

CREATE OR REPLACE VIEW DB_WCT.URL_PERMISSION_MAPPING_VIEW AS 
 SELECT upm.upm_oid, upm.upm_domain, p.pe_oid, p.pe_end_date, p.pe_owning_agency_id, up.up_pattern, st.st_active
   FROM DB_WCT.URL_PERMISSION_MAPPING upm
   JOIN DB_WCT.PERMISSION p ON upm.upm_permission_id = p.pe_oid
   JOIN DB_WCT.URL_PATTERN up ON upm.upm_url_pattern_id = up.up_oid
   JOIN DB_WCT.SITE st ON p.pe_site_id = st.st_oid;

GRANT SELECT, UPDATE, INSERT, DELETE ON DB_WCT.URL_PERMISSION_MAPPING_VIEW TO usr_wct;

-- View: "DB_WCT.ABSTRACT_TARGET_SCHEDULE_VIEW"

CREATE OR REPLACE VIEW DB_WCT.ABSTRACT_TARGET_SCHEDULE_VIEW AS 
 SELECT (abt.at_oid || ',') || s.s_oid AS thekey, 
        CASE abt.at_object_type
            WHEN 1 THEN 'Target'
            ELSE 'Group'
        END AS at_object_type_desc, abt.at_name, abt.at_state, u.usr_username, a.agc_name, s.s_oid, s.s_start, s.s_end, s.s_type, s.s_cron
   FROM DB_WCT.ABSTRACT_TARGET abt
   RIGHT JOIN DB_WCT.SCHEDULE s ON s.s_abstract_target_id = abt.at_oid
   JOIN DB_WCT.WCTUSER u ON abt.at_owner_id = u.usr_oid
   JOIN DB_WCT.AGENCY a ON u.usr_agc_oid = a.agc_oid
  ORDER BY abt.at_name, s.s_oid;

GRANT SELECT, UPDATE, INSERT, DELETE ON DB_WCT.ABSTRACT_TARGET_SCHEDULE_VIEW TO usr_wct;

-- Add a new original oid column to the profile table
ALTER TABLE DB_WCT.PROFILE ADD (p_orig_oid NUMBER(19,0));

--Add new profile id column to target instance
ALTER TABLE DB_WCT.TARGET_INSTANCE ADD (ti_profile_id NUMBER(19,0));
