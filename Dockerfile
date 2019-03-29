# Build stage for Java classes
FROM hbpmip/scala-base-build:1.2.6-8 as scala-build-env

ENV HOME=/root
COPY project/ /build/project/
COPY build.sbt /build/

# Run sbt on an empty project and force it to download most of its dependencies to fill the cache
RUN sbt compile

COPY src/ /build/src/
COPY .git/ /build/.git/
COPY .scalafmt.conf /build/

RUN sbt assembly

# Final image
FROM hbpmip/flyway:5.2.4-0

COPY --from=scala-build-env /build/target/scala-2.12/meta-db-setup.jar /flyway/jars/

COPY sql/V1_0__create.sql \
     sql/V2_0__add_target_table.sql \
     sql/V2_1__add_hierarchy_patch_table.sql \
     sql/V2_2__add_histogram_groupings.sql \
     sql/V2_2_1__add_histogram_groupings.sql \
     sql/V2_3__drop_hierarchy_patch_table.sql \
       /flyway/sql/

COPY docker/run.sh /
COPY variables_schema.json /src/

ENV FLYWAY_DBMS=postgresql \
    FLYWAY_HOST=db \
    FLYWAY_PORT=5432 \
    FLYWAY_DATABASE_NAME=meta \
    FLYWAY_USER=meta \
    FLYWAY_PASSWORD=meta \
    FLYWAY_SCHEMAS=public \
    FLYWAY_MIGRATION_PACKAGE="eu/humanbrainproject/mip/migrations/meta" \
    TAXONOMIES="" \
    TAXONOMY_PATCHES=""

ARG BUILD_DATE
ARG VCS_REF
ARG VERSION

ENV IMAGE="hbpmip/data-db-setup:$VERSION"

WORKDIR /flyway
ENTRYPOINT ["/run.sh"]
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
