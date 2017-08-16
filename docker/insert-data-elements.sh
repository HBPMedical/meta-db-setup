#!/bin/sh
set -e

SOURCE=$1
TARGET_TABLE=$2
JSON_HIERARCHY=`cat /src/variables/$SOURCE.json`

# Create a repeatable migration for Flyway
SOURCE="$SOURCE" \
TARGET_TABLE="$TARGET_TABLE" \
JSON_HIERARCHY="$JSON_HIERARCHY" \
  dockerize -template /src/data-elements.sql.tmpl:/flyway/sql/R__$SOURCE.sql /bin/true
