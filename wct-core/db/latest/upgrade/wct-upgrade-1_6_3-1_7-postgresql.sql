alter table DB_WCT.PROFILE add column P_HARVESTER_TYPE varchar(40);
update DB_WCT.PROFILE set P_HARVESTER_TYPE='HERITRIX1';
alter table DB_WCT.PROFILE  alter column P_HARVESTER_TYPE set not null;

alter table DB_WCT.PROFILE add column P_DATA_LIMIT_UNIT varchar(40);
update DB_WCT.PROFILE set P_DATA_LIMIT_UNIT='B' where P_HARVESTER_TYPE='HERITRIX3';

alter table DB_WCT.PROFILE add column P_MAX_FILE_SIZE_UNIT varchar(40);
update DB_WCT.PROFILE set P_MAX_FILE_SIZE_UNIT='B' where P_HARVESTER_TYPE='HERITRIX3';

alter table DB_WCT.PROFILE add column P_TIME_LIMIT_UNIT varchar(40);
update DB_WCT.PROFILE set P_TIME_LIMIT_UNIT='SECOND' where P_HARVESTER_TYPE='HERITRIX3';
