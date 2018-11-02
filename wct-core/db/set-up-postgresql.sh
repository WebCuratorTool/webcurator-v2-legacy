#!/usr/bin/env bash

export PGPASSWORD=password

cat setup/postgres-create-wct.sql | psql -U postgres
cat sql/wct-schema-postgresql.sql | psql -U postgres --dbname=Dwct
cat sql/wct-schema-grants.sql | psql -U postgres --dbname=Dwct
cat sql/wct-indexes-postgresql.sql | psql -U postgres --dbname=Dwct
cat sql/wct-postgres-bootstrap.sql | psql -U postgres --dbname=Dwct
cat sql/wct-qa-data-postgres.sql | psql -U postgres --dbname=Dwct
