#!/usr/bin/env bash


export MYSQL_PWD=password


cat setup/wct-create-mysql.sql | mysql -u root
cat sql/wct-schema-mysql.sql | mysql -u root -D DB_WCT
cat sql/wct-schema-grants-mysql.sql | mysql -u root -D DB_WCT
cat sql/wct-indexes-mysql.sql | mysql -u root -D DB_WCT
cat sql/wct-bootstrap-mysql.sql | mysql -u root -D DB_WCT
cat sql/wct-qa-data-mysql.sql | mysql -u root -D DB_WCT
