-- New Column: db_wct.at_access_zone
alter table db_wct.ABSTRACT_TARGET add column AT_ACCESS_ZONE integer default 0 not null;
alter table db_wct.ABSTRACT_TARGET add constraint CHK_ACCESS_ZONE check (AT_ACCESS_ZONE in (0,1,2));

-- New Column: db_wct.at_display_target
alter table db_wct.ABSTRACT_TARGET add column AT_DISPLAY_TARGET bit default 1 not null;

-- New Column: db_wct.at_display_note
alter table db_wct.ABSTRACT_TARGET add column AT_DISPLAY_NOTE varchar(4000);

-- New Column: db_wct.ti_display_target_instance
alter table db_wct.TARGET_INSTANCE add column TI_DISPLAY_TARGET_INSTANCE bit default 1 not null;

-- New Column: db_wct.ti_display_note
alter table db_wct.TARGET_INSTANCE add column TI_DISPLAY_NOTE varchar(4000);
