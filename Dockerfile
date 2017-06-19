FROM hbpmip/flyway:4.2.0-4
MAINTAINER Ludovic Claude <ludovic.claude@chuv.ch>

ARG BUILD_DATE
ARG VCS_REF
ARG VERSION

ENV FLYWAY_DBMS=postgresql \
    FLYWAY_HOST=db \
    FLYWAY_PORT=5432 \
    FLYWAY_DATABASE_NAME=meta \
    FLYWAY_USER=meta \
    FLYWAY_PASSWORD=meta \
    FLYWAY_SCHEMAS=public \
    CDE_DEFINITIONS=""

COPY sql/V1_0__create.sql /flyway/sql/V1_0__create.sql
COPY sql/V2_0__add_target_table.sql /flyway/sql/V2_0__add_target_table.sql
COPY docker/CDE-definition.sql.tmpl /src/
COPY docker/run.sh docker/insert-CDE-definition.sh /
COPY variables_schema.json /src/
COPY tests/test-variables.json /src/variables/test.json

RUN chmod +x /run.sh /insert-CDE-definition.sh

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
