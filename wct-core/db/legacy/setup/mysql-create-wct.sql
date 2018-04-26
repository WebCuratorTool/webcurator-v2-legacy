CREATE DATABASE DB_WCT;
\u DB_WCT
create user usr_wct@localhost identified by 'usr_wct';
grant all on DB_WCT.* to usr_wct@localhost;
set password for usr_wct@localhost = PASSWORD('password');

