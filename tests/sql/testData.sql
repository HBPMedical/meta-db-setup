BEGIN;

-- Plan the tests
SELECT plan( 5 );

SELECT is(source::VARCHAR, 'test', 'Test source should be present')
  FROM meta_variables where source='test';

SELECT is(target_table::VARCHAR, 'test_table', 'Test target table should be present')
  FROM meta_variables where source='test';

SELECT is(histogram_groupings::VARCHAR, 'montrealcognitiveassessment,minimentalstate', 'Test histogram groupings should be present')
  FROM meta_variables where source='test';

SELECT is(target_table::VARCHAR, 'test_table', 'Target table for new test should be defined')
  FROM meta_variables where source='new-test';

SELECT is(histogram_groupings::VARCHAR, 'montrealcognitiveassessment,minimentalstate', 'Histogram groupings for new test should be present')
  FROM meta_variables where source='new-test';

-- Clean up
SELECT * FROM finish();
ROLLBACK;
