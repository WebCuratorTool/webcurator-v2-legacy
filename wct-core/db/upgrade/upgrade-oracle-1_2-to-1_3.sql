-- New Column: db_wct.at_access_zone
ALTER TABLE db_wct.abstract_target ADD(at_access_zone NUMBER(2,0) DEFAULT 0 NOT NULL) ;
ALTER TABLE db_wct.abstract_target ADD CONSTRAINT chk_access_zone CHECK (at_access_zone in (0, 1, 2));

-- New Column: db_wct.at_display_target
ALTER TABLE db_wct.abstract_target ADD (at_display_target NUMBER(1,0) DEFAULT 1 NOT NULL);

-- New Column: db_wct.at_display_note
ALTER TABLE db_wct.abstract_target ADD (at_display_note VARCHAR2(4000));

-- New Column: db_wct.ti_display_target_instance
ALTER TABLE db_wct.target_instance ADD(ti_display_target_instance NUMBER(1,0) DEFAULT 1 NOT NULL);

-- New Column: db_wct.ti_display_note
ALTER TABLE db_wct.target_instance ADD(ti_display_note VARCHAR2(4000));

COMMIT;