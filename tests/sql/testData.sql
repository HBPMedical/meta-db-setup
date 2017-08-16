BEGIN;

-- Plan the tests
SELECT plan( 3 );

SELECT is(source::VARCHAR, 'test', 'Test variables should be present')
  FROM meta_variables where source='test';

SELECT is(target_table::VARCHAR, 'test_table', 'Test table should be present')
  FROM meta_variables where source='test';

  SELECT is(target_table::VARCHAR, 'test_table', 'New test variables should be defined')
    FROM meta_variables where source='new-test';

-- Clean up
SELECT * FROM finish();
ROLLBACK;
