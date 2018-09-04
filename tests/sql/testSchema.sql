BEGIN;

-- Plan the tests
SELECT plan( 6 );

SELECT has_table( 'meta_variables' );

SELECT has_column( 'meta_variables', 'id' );
SELECT has_column( 'meta_variables', 'source' );
SELECT has_column( 'meta_variables', 'target_table' );
SELECT has_column( 'meta_variables', 'hierarchy' );
SELECT col_is_pk(  'meta_variables', 'id' );

-- Clean up
SELECT * FROM finish();
ROLLBACK;
