alter table DB_WCT.PROFILE add column P_HARVESTER_TYPE varchar(40);
update DB_WCT.PROFILE set P_HARVESTER_TYPE='HERITRIX1';
alter table DB_WCT.PROFILE  alter column P_HARVESTER_TYPE set not null;
