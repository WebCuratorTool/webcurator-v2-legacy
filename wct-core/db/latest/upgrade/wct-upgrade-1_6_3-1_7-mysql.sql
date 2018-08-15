-- PROFILE

alter table DB_WCT.PROFILE add column P_HARVESTER_TYPE varchar(40) not null;
update DB_WCT.PROFILE set P_HARVESTER_TYPE='HERITRIX1';

alter table DB_WCT.PROFILE add column P_DATA_LIMIT_UNIT varchar(40);
update DB_WCT.PROFILE set P_DATA_LIMIT_UNIT='B' where P_HARVESTER_TYPE='HERITRIX3';

alter table DB_WCT.PROFILE add column P_MAX_FILE_SIZE_UNIT varchar(40);
update DB_WCT.PROFILE set P_MAX_FILE_SIZE_UNIT='B' where P_HARVESTER_TYPE='HERITRIX3';

alter table DB_WCT.PROFILE add column P_TIME_LIMIT_UNIT varchar(40);
update DB_WCT.PROFILE set P_TIME_LIMIT_UNIT='SECOND' where P_HARVESTER_TYPE='HERITRIX3';

-- PROFILE_OVERRIDES

alter table DB_WCT.PROFILE_OVERRIDES add column PO_H3_DOC_LIMIT integer, add column PO_H3_DATA_LIMIT double precision, add column PO_H3_DATA_LIMIT_UNIT varchar(40),
add column PO_H3_TIME_LIMIT double precision, add column PO_H3_TIME_LIMIT_UNIT varchar(40), add column PO_H3_MAX_PATH_DEPTH integer, add column PO_H3_MAX_HOPS integer,
add column PO_H3_MAX_TRANS_HOPS integer, add column PO_H3_IGNORE_ROBOTS varchar(10), add column PO_H3_IGNORE_COOKIES bit, add column PO_H3_OR_DOC_LIMIT bit,
add column PO_H3_OR_DATA_LIMIT bit, add column PO_H3_OR_TIME_LIMIT bit, add column PO_H3_OR_MAX_PATH_DEPTH bit, add column PO_H3_OR_MAX_HOPS bit,
add column PO_H3_OR_MAX_TRANS_HOPS bit, add column PO_H3_OR_IGNORE_ROBOTS bit, add column PO_H3_OR_IGNORE_COOKIES bit, add column PO_H3_OR_BLOCK_URL bit,
add column PO_H3_OR_INCL_URL bit;

update DB_WCT.PROFILE_OVERRIDES set PO_H3_IGNORE_COOKIES = 0, PO_H3_OR_DOC_LIMIT = 0, PO_H3_OR_DATA_LIMIT = 0, PO_H3_OR_TIME_LIMIT = 0, PO_H3_OR_MAX_PATH_DEPTH = 0,
PO_H3_OR_MAX_HOPS = 0, PO_H3_OR_MAX_TRANS_HOPS = 0, PO_H3_OR_IGNORE_ROBOTS = 0, PO_H3_OR_IGNORE_COOKIES = 0, PO_H3_OR_BLOCK_URL = 0, PO_H3_OR_INCL_URL = 0;

create table DB_WCT.PO_H3_BLOCK_URL (PBU_PROF_OVER_OID bigint not null, PBU_FILTER varchar(255), PBU_IX integer not null, primary key (PBU_PROF_OVER_OID, PBU_IX));
create table DB_WCT.PO_H3_INCLUDE_URL (PIU_PROF_OVER_OID bigint not null, PIU_FILTER varchar(255), PIU_IX integer not null, primary key (PIU_PROF_OVER_OID, PIU_IX));

alter table DB_WCT.PO_H3_BLOCK_URL add index PBU_FK_1 (PBU_PROF_OVER_OID), add constraint PBU_FK_1 foreign key (PBU_PROF_OVER_OID) references DB_WCT.PROFILE_OVERRIDES (PO_OID);
alter table DB_WCT.PO_H3_INCLUDE_URL add index PIU_FK_1 (PIU_PROF_OVER_OID), add constraint PIU_FK_1 foreign key (PIU_PROF_OVER_OID) references DB_WCT.PROFILE_OVERRIDES (PO_OID);


GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.PO_H3_BLOCK_URL TO usr_wct@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.PO_H3_INCLUDE_URL TO usr_wct@localhost;

alter table DB_WCT.PROFILE add column IMPORTED bit not null default 0;
