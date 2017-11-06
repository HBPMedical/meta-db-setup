#!/bin/sh
set -e

SOURCE=$1
NEW_SOURCE=$2
TARGET_TABLE=$3
HISTOGRAM_GROUPINGS=$4
JSON_PATCH=`cat /src/patches/$NEW_SOURCE.patch.json`

# Create a repeatable migration for Flyway
SOURCE="$SOURCE" \
NEW_SOURCE="$NEW_SOURCE" \
TARGET_TABLE="$TARGET_TABLE" \
HISTOGRAM_GROUPINGS="$HISTOGRAM_GROUPINGS" \
JSON_PATCH="$JSON_PATCH" \
  dockerize -template /src/patch-hierarchy.sql.tmpl:/flyway/sql/R__patch_$NEW_SOURCE.sql /bin/true
