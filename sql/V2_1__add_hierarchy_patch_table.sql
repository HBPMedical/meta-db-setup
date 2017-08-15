CREATE TABLE IF NOT EXISTS meta_variables (
  id serial NOT NULL PRIMARY KEY,
  source varchar(256) UNIQUE NOT NULL,
  hierarchy_patch jsonb NOT NULL,
  target_table varchar(256) NOT NULL
);
