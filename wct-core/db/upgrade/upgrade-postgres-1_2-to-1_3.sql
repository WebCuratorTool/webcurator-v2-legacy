-- New Column: db_wct.at_access_zone
ALTER TABLE db_wct.abstract_target ADD COLUMN at_access_zone integer NOT NULL DEFAULT 0;
ALTER TABLE db_wct.abstract_target ADD CONSTRAINT chk_access_zone CHECK (at_access_zone in (0, 1, 2));

-- New Column: db_wct.at_display_target
ALTER TABLE db_wct.abstract_target ADD COLUMN at_display_target boolean NOT NULL DEFAULT true;

-- New Column: db_wct.at_display_note
ALTER TABLE db_wct.abstract_target ADD COLUMN at_display_note character varying(4000);

-- New Column: db_wct.ti_display_target_instance
ALTER TABLE db_wct.target_instance ADD COLUMN ti_display_target_instance boolean NOT NULL DEFAULT true;

-- New Column: db_wct.ti_display_note
ALTER TABLE db_wct.target_instance ADD COLUMN ti_display_note character varying(4000);
