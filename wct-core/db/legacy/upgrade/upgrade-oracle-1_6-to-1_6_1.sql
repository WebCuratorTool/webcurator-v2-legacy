alter table DB_WCT.PERMISSION_TEMPLATE add (PRT_TEMPLATE_REPLY_TO varchar(255));
alter table DB_WCT.AGENCY add (AGC_SHOW_TASKS number(1,0) default 1 not null);

create table DB_WCT.HEATMAP_CONFIG (HM_OID number not null, HM_NAME varchar(255) not null, HM_DISPLAY_NAME varchar(255) not null, HM_COLOR varchar(6) not null, HM_THRESHOLD_LOWEST number not null, primary key (HM_OID));
insert into DB_WCT.HEATMAP_CONFIG (HM_OID, HM_NAME, HM_DISPLAY_NAME, HM_COLOR, HM_THRESHOLD_LOWEST) values (1, 'low','Low','8FBC8F',1);
insert into DB_WCT.HEATMAP_CONFIG (HM_OID, HM_NAME, HM_DISPLAY_NAME, HM_COLOR, HM_THRESHOLD_LOWEST) values (2, 'medium','Medium','F0E68C',7);
insert into DB_WCT.HEATMAP_CONFIG (HM_OID, HM_NAME, HM_DISPLAY_NAME, HM_COLOR, HM_THRESHOLD_LOWEST) values (3, 'high','High','FF6347',12);

GRANT SELECT, INSERT, UPDATE, DELETE ON DB_WCT.HEATMAP_CONFIG TO USR_WCT;

alter table DB_WCT.TARGET add (T_ALLOW_OPTIMIZE number(1,0) default 0 not null);
alter table DB_WCT.TARGET_INSTANCE add (TI_ALLOW_OPTIMIZE number(1,0) default 0 not null);

alter table DB_WCT.ROLE_PRIVILEGE drop constraint FK_PRIV_ROLE_OID;

--delete orphan records
GRANT SELECT, INSERT, UPDATE,DELETE ON DB_WCT.ROLE_PRIVILEGE TO USR_WCT;
delete from DB_WCT.ROLE_PRIVILEGE where PRV_ROLE_OID is null;
alter table DB_WCT.ROLE_PRIVILEGE add constraint FK_PRIV_ROLE_OID foreign key (PRV_ROLE_OID) references DB_WCT.WCTROLE (ROL_OID) on delete cascade;

alter table DB_WCT.BANDWIDTH_RESTRICTIONS add (BR_OPTIMIZATION_ALLOWED number(1,0) default 0 not null);


alter table DB_WCT.AGENCY add AGC_DEFAULT_DESC_TYPE varchar(50) default '';

