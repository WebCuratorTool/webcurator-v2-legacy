create user db_wct identified by password default tablespace wct_data quota unlimited on wct_data; 

create user usr_wct identified by password default tablespace wct_data quota unlimited on wct_data; 

grant create session to usr_wct; 

grant connect,resource to db_wct;
grant create view to db_wct;