alter table DB_WCT.PROFILE add (P_HARVESTER_TYPE varchar2(40));
update DB_WCT.PROFILE set P_HARVESTER_TYPE='HERITRIX1';
alter table DB_WCT.PROFILE modify P_HARVESTER_TYPE varchar2(40) not null;


