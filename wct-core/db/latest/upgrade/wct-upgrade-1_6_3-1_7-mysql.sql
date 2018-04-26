alter table DB_WCT.PROFILE add column P_HARVESTER_TYPE varchar(40) not null;
update DB_WCT.PROFILE set P_HARVESTER_TYPE='HERITRIX1';
