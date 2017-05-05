#!/bin/sh

export CDE_NAME=$1
export CDE_HIERARCHY_PATH=$2

export CDE_JSON_HIERARCHY=`cat $CDE_HIERARCHY_PATH`

# Create a repeatable migration for Flyway
dockerize -template /flyway/CDE-definition.sql.tmpl:/flyway/sql/R__$CDE_NAME.sql
