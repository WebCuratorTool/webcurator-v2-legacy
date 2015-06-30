CREATE DATABASE "Dwct" WITH ENCODING='UTF8';

\c Dwct

CREATE SCHEMA DB_WCT;

CREATE ROLE usr_wct LOGIN PASSWORD 'password'
  NOINHERIT
   VALID UNTIL 'infinity';

grant usage on schema DB_WCT to usr_wct;
