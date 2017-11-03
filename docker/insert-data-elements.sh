#!/bin/sh
set -e

SOURCE=$1
TARGET_TABLE=$2
HISTOGRAM_GROUPINGS=$3
JSON_HIERARCHY=`cat /src/variables/$SOURCE.json`

# Create a repeatable migration for Flyway
SOURCE="$SOURCE" \
TARGET_TABLE="$TARGET_TABLE" \
HISTOGRAM_GROUPINGS="$HISTOGRAM_GROUPINGS" \
JSON_HIERARCHY="$JSON_HIERARCHY" \
  dockerize -template /src/data-elements.sql.tmpl:/flyway/sql/R__$SOURCE.sql /bin/true
