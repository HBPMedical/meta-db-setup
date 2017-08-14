# Build stage for Java classes
FROM maven:3.5.0-jdk-8-alpine as build-java-env

ENV HOME=/root
COPY docker/seed-src /project/docker/seed-src
COPY pom.xml /project/
WORKDIR /project

# Run Maven on an empty project and force it to download most of its dependencies to fill the cache
RUN mkdir -p /usr/share/maven/ref/repository \
    && cp /usr/share/maven/ref/settings-docker.xml /root/.m2/settings.xml \
    && mvn -DSEED=true clean \
        resources:resources \
        compiler:compile \
        surefire:test \
        jar:jar \
        package

COPY src/ /project/src/

# Repeating the file copy works better. I dunno why.
RUN cp /usr/share/maven/ref/settings-docker.xml /root/.m2/settings.xml \
    && mvn clean package

# Final image
FROM hbpmip/flyway:4.2.0-5
MAINTAINER Ludovic Claude <ludovic.claude@chuv.ch>

ARG BUILD_DATE
ARG VCS_REF
ARG VERSION

COPY --from=build-java-env /project/target/data-db-setup.jar /usr/share/jars/
COPY --from=build-java-env \
        /usr/share/maven/ref/repository/net/sf/supercsv/super-csv/2.4.0/super-csv-2.4.0.jar \
        /usr/share/maven/ref/repository/org/apache/commons/commons-lang3/3.5/commons-lang3-3.5.jar \
        /usr/share/maven/ref/repository/com/github/spullara/mustache/java/compiler/0.9.5/compiler-0.9.5.jar \
        /flyway/jars/

COPY sql/V1_0__create.sql /flyway/sql/V1_0__create.sql
COPY sql/V2_0__add_target_table.sql /flyway/sql/V2_0__add_target_table.sql
COPY docker/CDE-definition.sql.tmpl /src/
COPY docker/run.sh docker/insert-CDE-definition.sh /
COPY variables_schema.json /src/
COPY tests/test-variables.json /src/variables/test.json

RUN chmod +x /run.sh /insert-CDE-definition.sh

ENV FLYWAY_DBMS=postgresql \
    FLYWAY_HOST=db \
    FLYWAY_PORT=5432 \
    FLYWAY_DATABASE_NAME=meta \
    FLYWAY_USER=meta \
    FLYWAY_PASSWORD=meta \
    FLYWAY_SCHEMAS=public \
    CDE_DEFINITIONS=""

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
