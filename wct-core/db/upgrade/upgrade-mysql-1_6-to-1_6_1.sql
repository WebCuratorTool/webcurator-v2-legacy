alter table db_wct.PERMISSION_TEMPLATE add column PRT_TEMPLATE_REPLY_TO varchar(255);
alter table db_wct.AGENCY add column AGC_SHOW_TASKS boolean not null default true;

create table DB_WCT.HEATMAP_CONFIG (HM_OID bigint not null, HM_NAME varchar(255) not null, HM_DISPLAY_NAME varchar(255) not null, HM_COLOR varchar(6) not null, HM_THRESHOLD_LOWEST integer not null, primary key (HM_OID));
insert into DB_WCT.HEATMAP_CONFIG (HM_OID, HM_NAME, HM_DISPLAY_NAME, HM_COLOR, HM_THRESHOLD_LOWEST) values (1, "low","Low","8FBC8F",1);
insert into DB_WCT.HEATMAP_CONFIG (HM_OID, HM_NAME, HM_DISPLAY_NAME, HM_COLOR, HM_THRESHOLD_LOWEST) values (2, "medium","Medium","F0E68C",7);
insert into DB_WCT.HEATMAP_CONFIG (HM_OID, HM_NAME, HM_DISPLAY_NAME, HM_COLOR, HM_THRESHOLD_LOWEST) values (3, "high","High","FF6347",12);

GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.HEATMAP_CONFIG TO usr_wct@localhost;

alter table db_wct.target add column T_ALLOW_OPTIMIZE boolean not null default false;
alter table db_wct.target_instance add column TI_ALLOW_OPTIMIZE boolean not null default false;

alter table DB_WCT.role_privilege drop foreign key FK_PRIV_ROLE_OID;

-- delete orphan records
GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.ROLE_PRIVILEGE TO usr_wct@localhost;
delete from DB_WCT.ROLE_PRIVILEGE where PRV_ROLE_OID is null;
alter table DB_WCT.ROLE_PRIVILEGE add constraint FK_PRIV_ROLE_OID foreign key (PRV_ROLE_OID) references DB_WCT.WCTROLE (ROL_OID) on delete cascade;

alter table DB_WCT.BANDWIDTH_RESTRICTIONS add column BR_OPTIMIZATION_ALLOWED boolean not null default true;


alter table db_wct.AGENCY add column AGC_DEFAULT_DESC_TYPE varchar(50) default "";

