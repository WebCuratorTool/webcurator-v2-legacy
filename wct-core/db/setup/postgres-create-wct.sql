CREATE DATABASE "Dwct" WITH ENCODING='UTF8';

\c Dwct

CREATE SCHEMA db_wct;

CREATE ROLE usr_wct LOGIN PASSWORD 'password'
  NOINHERIT
   VALID UNTIL 'infinity';

grant usage on schema db_wct to usr_wct;
