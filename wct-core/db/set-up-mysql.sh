#!/usr/bin/env bash


export MYSQL_PWD=password


cat latest/setup/wct-create-mysql.sql | mysql -u root
cat latest/sql/wct-schema-mysql.sql | mysql -u root -D DB_WCT
cat latest/sql/wct-schema-grants-mysql.sql | mysql -u root -D DB_WCT
cat latest/sql/wct-indexes-mysql.sql | mysql -u root -D DB_WCT
cat latest/sql/wct-bootstrap-mysql.sql | mysql -u root -D DB_WCT
cat latest/sql/wct-qa-data-mysql.sql | mysql -u root -D DB_WCT
