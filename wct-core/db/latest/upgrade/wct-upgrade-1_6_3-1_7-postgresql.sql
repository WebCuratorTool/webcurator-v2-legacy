-- PROFILE

alter table DB_WCT.PROFILE add column P_HARVESTER_TYPE varchar(40);
update DB_WCT.PROFILE set P_HARVESTER_TYPE='HERITRIX1';
alter table DB_WCT.PROFILE  alter column P_HARVESTER_TYPE set not null;

alter table DB_WCT.PROFILE add column P_DATA_LIMIT_UNIT varchar(40);
update DB_WCT.PROFILE set P_DATA_LIMIT_UNIT='B' where P_HARVESTER_TYPE='HERITRIX3';

alter table DB_WCT.PROFILE add column P_MAX_FILE_SIZE_UNIT varchar(40);
update DB_WCT.PROFILE set P_MAX_FILE_SIZE_UNIT='B' where P_HARVESTER_TYPE='HERITRIX3';

alter table DB_WCT.PROFILE add column P_TIME_LIMIT_UNIT varchar(40);
update DB_WCT.PROFILE set P_TIME_LIMIT_UNIT='SECOND' where P_HARVESTER_TYPE='HERITRIX3';

-- PROFILE_OVERRIDES

alter table DB_WCT.PROFILE_OVERRIDES add column PO_H3_DOC_LIMIT int4, add column PO_H3_DATA_LIMIT float8, add column PO_H3_DATA_LIMIT_UNIT varchar(40),
add column PO_H3_TIME_LIMIT float8, add column PO_H3_TIME_LIMIT_UNIT varchar(40), add column PO_H3_MAX_PATH_DEPTH int4, add column PO_H3_MAX_HOPS int4,
add column PO_H3_MAX_TRANS_HOPS int4, add column PO_H3_IGNORE_ROBOTS varchar(10), add column PO_H3_IGNORE_COOKIES bool, add column PO_H3_OR_DOC_LIMIT bool,
add column PO_H3_OR_DATA_LIMIT bool, add column PO_H3_OR_TIME_LIMIT bool, add column PO_H3_OR_MAX_PATH_DEPTH bool, add column PO_H3_OR_MAX_HOPS bool,
add column PO_H3_OR_MAX_TRANS_HOPS bool, add column PO_H3_OR_IGNORE_ROBOTS bool, add column PO_H3_OR_IGNORE_COOKIES bool, add column PO_H3_OR_BLOCK_URL bool,
add column PO_H3_OR_INCL_URL bool;

update DB_WCT.PROFILE_OVERRIDES set PO_H3_IGNORE_COOKIES = 0, PO_H3_OR_DOC_LIMIT = 0, PO_H3_OR_DATA_LIMIT = 0, PO_H3_OR_TIME_LIMIT = 0, PO_H3_OR_MAX_PATH_DEPTH = 0,
PO_H3_OR_MAX_HOPS = 0, PO_H3_OR_MAX_TRANS_HOPS = 0, PO_H3_OR_IGNORE_ROBOTS = 0, PO_H3_OR_IGNORE_COOKIES = 0, PO_H3_OR_BLOCK_URL = 0, PO_H3_OR_INCL_URL = 0;

create table DB_WCT.PO_H3_BLOCK_URL (PBU_PROF_OVER_OID int8 not null, PBU_FILTER varchar(255), PBU_IX int4 not null, primary key (PBU_PROF_OVER_OID, PBU_IX));
create table DB_WCT.PO_H3_INCLUDE_URL (PIU_PROF_OVER_OID int8 not null, PIU_FILTER varchar(255), PIU_IX int4 not null, primary key (PIU_PROF_OVER_OID, PIU_IX));

alter table DB_WCT.PO_H3_BLOCK_URL add constraint PBU_FK_1 foreign key (PBU_PROF_OVER_OID) references DB_WCT.PROFILE_OVERRIDES;
alter table DB_WCT.PO_H3_INCLUDE_URL add constraint PIU_FK_1 foreign key (PIU_PROF_OVER_OID) references DB_WCT.PROFILE_OVERRIDES;

GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.PO_H3_BLOCK_URL TO USR_WCT;
GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.PO_H3_INCLUDE_URL TO USR_WCT;

alter table DB_WCT.PROFILE add column IMPORTED bool not null default false;


