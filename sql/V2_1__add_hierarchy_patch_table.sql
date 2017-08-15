CREATE TABLE IF NOT EXISTS hierarchy_patches (
  new_source varchar(256) NOT NULL PRIMARY KEY,
  original_source varchar(256) UNIQUE NOT NULL,
  hierarchy_patch jsonb NOT NULL,
  target_table varchar(256) NOT NULL
);
