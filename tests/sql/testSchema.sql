BEGIN;

-- Plan the tests
SELECT plan( 11 );

SELECT has_table( 'meta_variables' );

SELECT has_column( 'meta_variables', 'id' );
SELECT has_column( 'meta_variables', 'source' );
SELECT has_column( 'meta_variables', 'target_table' );
SELECT has_column( 'meta_variables', 'hierarchy' );
SELECT col_is_pk(  'meta_variables', 'id' );

SELECT has_table( 'hierarchy_patches' );

SELECT has_column( 'hierarchy_patches', 'new_source' );
SELECT has_column( 'hierarchy_patches', 'original_source' );
SELECT has_column( 'hierarchy_patches', 'hierarchy_patch' );
SELECT has_column( 'hierarchy_patches', 'target_table' );

-- Clean up
SELECT * FROM finish();
ROLLBACK;
