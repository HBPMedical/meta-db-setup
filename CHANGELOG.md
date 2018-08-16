
# Changelog

## 2.3.0 - 2018-08-16

* Update variables_schema.json to validate better the Json files for variable hierarchies
* Automatically validate variable hierarchies Jsons when inserting them into the database

## 2.2.0 - 2018-05-02

* Support big Json patches
* Add datasets to variables and define an enum for sql types
* Update libraries

## 2.1.0 - 2017-11-04

* Add column histogram_groupings to provide a default breakdown of a dataset into several histograms with groupings on one column each.

## 2.0.0 - 2017-08-17

* Support patching hierarchy jsons to generate new sets of variables definitions
* Changed configuration by environment variables for the list of variables definitions to insert. It now uses DATA_ELEMENTS environment variable.
* Add sql_type to variables

## 1.1.1 - 2017-05-24

* Support multiple data elements definitions and target tables

## 1.1.0 - 2017-05-24

* Use jsonb to store hierarchy of variables in the database
* Add target_table column

## 1.0.0 - 2017-05-11

* First stable version
