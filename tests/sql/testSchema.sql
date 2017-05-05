BEGIN;
SELECT plan( 5 );

SELECT has_table( 'meta_variables' );

SELECT has_column( 'meta_variables', 'id' );
SELECT has_column( 'meta_variables', 'source' );
SELECT has_column( 'meta_variables', 'hierarchy' );
SELECT col_is_pk(  'meta_variables', 'id' );

SELECT * FROM finish();
ROLLBACK;
