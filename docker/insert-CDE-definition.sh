#!/bin/sh
set -e

export CDE_NAME=$1
export CDE_TARGET_TABLE=$2

export CDE_JSON_HIERARCHY=`cat /src/variables/$CDE_NAME.json`

# Create a repeatable migration for Flyway
dockerize -template /src/CDE-definition.sql.tmpl:/flyway/sql/R__$CDE_NAME.sql /bin/true
