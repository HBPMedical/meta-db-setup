#!/usr/bin/env bash
set -e

DOCKERIZE_OPTS=""

if [ ! -z "$@" ]; then
    if [ -z "$FLYWAY_HOST" ] || [ -z "$FLYWAY_DBMS" ]
    then
        echo "Usage: docker run $IMAGE with the following environment variables"
        echo
        echo "FLYWAY_DBMS: [required] Type of the database (oracle, postgres...)."
        echo "FLYWAY_HOST: [required] database host."
        echo "FLYWAY_PORT: database port."
        echo "FLYWAY_USER: database user."
        echo "FLYWAY_PASSWORD: database password."
        exit 1
    else
        DOCKERIZE_OPTS="-wait tcp://${FLYWAY_HOST}:${FLYWAY_PORT:-5432} -template /flyway/conf/flyway.conf.tmpl:/flyway/conf/flyway.conf"
    fi
fi

if [ -n "$DATA_ELEMENTS" ]; then
  IFS=" " read -a specs <<<"$DATA_ELEMENTS"
  for ((i = 0; i < "${#specs[@]}"; i++))
  do
    IFS="|" read -a spec <<<"${specs[$i]}"
    source="${spec[0]}"
    target_table="${spec[1]}"
    histogram_groupings="${spec[2]}"
    /insert-data-elements.sh "$source" "$target_table" "$histogram_groupings"
  done
fi

if [ -n "$HIERARCHY_PATCHES" ]; then
  IFS=" " read -a specs <<<"$HIERARCHY_PATCHES"
  for ((i = 0; i < "${#specs[@]}"; i++))
  do
    IFS="|" read -a spec <<<"${specs[$i]}"
    source="${spec[0]}"
    patch="${spec[1]}"
    target_table="${spec[2]}"
    histogram_groupings="${spec[3]}"
    /insert-patched-hierarchy.sh "$source" "$patch" "$target_table" "$histogram_groupings"
  done
fi

exec dockerize $DOCKERIZE_OPTS flyway -callbacks=eu.humanbrainproject.mip.migrations.meta.CheckHierarchyCallback,eu.humanbrainproject.mip.migrations.meta.ApplyHierarchyPatchesCallback $@
