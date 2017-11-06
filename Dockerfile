# Build stage for Java classes
FROM bigtruedata/sbt:0.13.15-2.11.8 as build-scala-env

ENV HOME=/root
COPY project/ /sources/project/
COPY build.sbt /sources/
WORKDIR /sources

# Run sbt on an empty project and force it to download most of its dependencies to fill the cache
RUN sbt compile

COPY src/ /sources/src/

RUN sbt assembly

# Final image
FROM hbpmip/flyway:4.2.0-5
MAINTAINER Ludovic Claude <ludovic.claude@chuv.ch>

ARG BUILD_DATE
ARG VCS_REF
ARG VERSION

COPY --from=build-scala-env /sources/target/scala-2.12/meta-db-setup.jar /flyway/jars/

COPY sql/V1_0__create.sql \
     sql/V2_0__add_target_table.sql \
     sql/V2_1__add_hierarchy_patch_table.sql \
     sql/V2_2__add_histogram_groupings.sql \
     sql/V2_2_1__add_histogram_groupings.sql \
       /flyway/sql/

COPY docker/data-elements.sql.tmpl docker/patch-hierarchy.sql.tmpl /src/
COPY docker/run.sh docker/insert-data-elements.sh docker/insert-patched-hierarchy.sh /
COPY variables_schema.json /src/

RUN chmod +x /run.sh /insert-data-elements.sh /insert-patched-hierarchy.sh

ENV FLYWAY_DBMS=postgresql \
    FLYWAY_HOST=db \
    FLYWAY_PORT=5432 \
    FLYWAY_DATABASE_NAME=meta \
    FLYWAY_USER=meta \
    FLYWAY_PASSWORD=meta \
    FLYWAY_SCHEMAS=public \
    FLYWAY_MIGRATION_PACKAGE="eu/humanbrainproject/mip/migrations/meta" \
    DATA_ELEMENTS="" \
    PATCHED_HIERARCHIES=""

ENV IMAGE="hbpmip/data-db-setup:$VERSION"

WORKDIR /flyway
CMD ["migrate"]

LABEL org.label-schema.build-date=$BUILD_DATE \
      org.label-schema.name="hbpmip/meta-db-setup" \
      org.label-schema.description="Meta database setup" \
      org.label-schema.url="https://github.com/LREN-CHUV/meta-db-setup" \
      org.label-schema.vcs-type="git" \
      org.label-schema.vcs-url="https://github.com/LREN-CHUV/meta-db-setup" \
      org.label-schema.vcs-ref=$VCS_REF \
      org.label-schema.version="$VERSION" \
      org.label-schema.vendor="LREN CHUV" \
      org.label-schema.license="Apache2.0" \
      org.label-schema.docker.dockerfile="Dockerfile" \
      org.label-schema.schema-version="1.0"
