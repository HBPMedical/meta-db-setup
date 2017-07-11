[![CHUV](https://img.shields.io/badge/CHUV-LREN-AF4C64.svg)](https://www.unil.ch/lren/en/home.html) [![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](https://github.com/LREN-CHUV/meta-db-setup/blob/master/LICENSE) [![DockerHub](https://img.shields.io/badge/docker-hbpmip%2Fmeta--db--setup-008bb8.svg)](https://hub.docker.com/r/hbpmip/meta-db-setup/) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/f3d7d66596844196bb8912f18bb33931)](https://www.codacy.com/app/hbpmip/meta-db-setup?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=LREN-CHUV/meta-db-setup&amp;utm_campaign=Badge_Grade)
[![CircleCI](https://circleci.com/gh/LREN-CHUV/meta-db-setup.svg?style=svg)](https://circleci.com/gh/LREN-CHUV/meta-db-setup)

# Setup for database 'meta-db'

## Introduction

This project uses Flyway to manage the database migration scripts for the 'meta-db' database used by MIP.

This database contains the metadata used for reference, including:

* the list of variables and groups for the Common Data Elements (CDE) defined by MIP.

## Usage

Run:

```console
$ docker run -i -t --rm -e FLYWAY_HOST=`hostname` hbpmip/meta-db-setup:1.1.3 migrate
```

where the environment variables are:

* FLYWAY_HOST: database host, default to 'db'.
* FLYWAY_PORT: database port, default to 5432.
* FLYWAY_DATABASE_NAME: name of the database or schema, default to 'meta'
* FLYWAY_URL: JDBC url to the database, constructed by default from FLYWAY_DBMS, FLYWAY_HOST, FLYWAY_PORT and FLYWAY_DATABASE_NAME
* FLYWAY_DRIVER: Fully qualified classname of the jdbc driver (autodetected by default based on flyway.url)
* FLYWAY_USER: database user, default to 'meta'.
* FLYWAY_PASSWORD: database password, default to 'meta'.
* FLYWAY_SCHEMAS: Optional, comma-separated list of schemas managed by Flyway
* FLYWAY_TABLE: Optional, name of Flyway's metadata table (default: schema_version)

Child images should follow the following procedure to be able to load their metadata:

* Define the environment variable CDE_DEFINITIONS defining a comma-separated list of definitions to load.
* Define the environment variable CDE_TARGET_TABLES defining a comma-separated list of target tables maching each definitions.
* For each definition, place a file named [definition].json into folder /src/variables/

Each definition, target table and hierarchy json will be inserted into the meta table.

## Build

Run: `./build.sh`

## Publish on Docker Hub

Run: `./publish.sh`

## License

### Meta-db-setup

(this project)

Copyright (C) 2017 [LREN CHUV](https://www.unil.ch/lren/en/home.html)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

### Flyway

Copyright (C) 2016-2017 [Boxfuse GmbH](https://boxfuse.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

## Trademark
Flyway is a registered trademark of [Boxfuse GmbH](https://boxfuse.com).
