#!/usr/bin/env bash

export PGPASSWORD=password

cat latest/setup/wct-create-postgres.sql | psql -U postgres
cat latest/sql/wct-schema-postgres.sql | psql -U postgres --dbname=Dwct
cat latest/sql/wct-schema-grants-postgres.sql | psql -U postgres --dbname=Dwct
cat latest/sql/wct-indexes-postgres.sql | psql -U postgres --dbname=Dwct
cat latest/sql/wct-bootstrap-postgres.sql | psql -U postgres --dbname=Dwct
cat latest/sql/wct-qa-data-postgres.sql | psql -U postgres --dbname=Dwct
