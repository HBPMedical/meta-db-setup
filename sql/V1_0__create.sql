CREATE TABLE IF NOT EXISTS meta_variables (
  id serial NOT NULL PRIMARY KEY,
  source varchar(256) UNIQUE NOT NULL,
  hierarchy json NOT NULL
);
