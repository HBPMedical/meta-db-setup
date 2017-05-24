ALTER TABLE meta_variables
  ADD COLUMN target_table varchar(256) NOT NULL,
  ALTER COLUMN hierarchy TYPE jsonb
;
