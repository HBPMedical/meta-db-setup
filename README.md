[![CHUV](https://img.shields.io/badge/CHUV-LREN-AF4C64.svg)](https://www.unil.ch/lren/en/home.html) [![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](https://github.com/LREN-CHUV/meta-db-setup/blob/master/LICENSE) [![DockerHub](https://img.shields.io/badge/docker-hbpmip%2Fmeta--db--setup-008bb8.svg)](https://hub.docker.com/r/hbpmip/meta-db-setup/) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/f3d7d66596844196bb8912f18bb33931)](https://www.codacy.com/app/hbpmip/meta-db-setup?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=LREN-CHUV/meta-db-setup&amp;utm_campaign=Badge_Grade)
[![CircleCI](https://circleci.com/gh/LREN-CHUV/meta-db-setup.svg?style=svg)](https://circleci.com/gh/LREN-CHUV/meta-db-setup)

# Setup for database 'meta-db'

## Introduction

This Docker image manages the database migration scripts for the 'meta-db' database used by MIP.

This database contains the metadata used for reference, including:

* the list of variables and groups for the Common Data Elements (CDE) defined by MIP.
* the list of variables and groups for other datasets available in MIP.

It uses Flyway to perform the versioned database migrations.

## Usage

Run:

```console
$ docker run -i -t --rm -e FLYWAY_HOST=`hostname` hbpmip/meta-db-setup:2.0.0 migrate
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

### Child images

Child images should follow the following procedure to be able to load their metadata:

#### List of Data Elements (aka hierarchy of variables)

Define the environment variable DATA_ELEMENTS defining a space-separated list of data elements definitions, where each element definition is of the form <data elements name>|<target table>|<list of groupings for histograms view>.

* __data element name__ is the name of this organisation of variables. It should point to file /src/variables/<data elements name>.json located inside the Docker image.
* __target table__ should be the name of the table or view to use to retrieve features for algorithms and data exploration.
* __list of groupings for histograms view__ should be a comma separated list of columns in the target table that defines a default breakdown of a dataset into several histograms with groupings, where each column defined here will create an histogram grouping values on the group by column.

For example,

```
  ENV DATA_ELEMENTS=test-set|main_features_table|dataset,gender,agegroup,alzheimerbroadcategory
```

Then for each data element definition, place a file named [data elements name].json into folder /src/variables/ describing the hierarchy of variables and following schema [variables_schema.json](variables_schema.json)

#### List of Json patches to apply to existing Data Elements definitions

Define the environment variable HIERARCHY_PATCHES defining a comma-separated list of Json patches to apply to existing data elements, where each patch description has the form <data elements name to patch>|<new data elements name>|<target table>.

For example,

```
  ENV HIERARCHY_PATCHES=test-set|test-set-with-custom-vars|main_features_table
```

Each new data elements definition, target table and hierarchy json will be inserted into the meta table. For each new data elements, we generate the json describing the hierarchy of variables by taking the hierarchy json from the original data elements definition and applying to it a [Json patch](http://jsonpatch.com/) loaded from the container folder /src/patches/ and named [new data elements name].json

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

[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0)

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


## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with
any pull requests, please state that the contribution is your original work and that you license
the work to the project under the project's open source license. Whether or not you state this
explicitly, by submitting any copyrighted material via pull request, email, or other means you
agree to license the material under the project's open source license and warrant that you have the
legal authority to do so.
