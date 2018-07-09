-- New Column: DB_WCT.AT_ACCESS_ZONE
alter table DB_WCT.ABSTRACT_TARGET add column AT_ACCESS_ZONE integer default 0 not null;
alter table DB_WCT.ABSTRACT_TARGET add constraint CHK_ACCESS_ZONE check (AT_ACCESS_ZONE in (0,1,2));

-- New Column: DB_WCT.AT_DISPLAY_TARGET
alter table DB_WCT.ABSTRACT_TARGET add column AT_DISPLAY_TARGET bit default 1 not null;

-- New Column: DB_WCT.AT_DISPLAY_NOTE
alter table DB_WCT.ABSTRACT_TARGET add column AT_DISPLAY_NOTE varchar(4000);

-- New Column: DB_WCT.TI_DISPLAY_TARGET_INSTANCE
alter table DB_WCT.TARGET_INSTANCE add column TI_DISPLAY_TARGET_INSTANCE bit default 1 not null;

-- New Column: DB_WCT.TI_DISPLAY_NOTE
alter table DB_WCT.TARGET_INSTANCE add column TI_DISPLAY_NOTE varchar(4000);
