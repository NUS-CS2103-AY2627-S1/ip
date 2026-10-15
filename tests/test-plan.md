# Test Plan

The detailed console UI test cases are maintained in `test/ui-test-plan.md`.

## Statistics And Insights

Test coverage for the `stats` feature includes:

- JUnit tests for task completion timestamps and calendar week/month counting.
- JUnit tests for parser validation of the no-argument `stats` command.
- JUnit tests for end-to-end `Dukey#getResponse` output and GUI response style.
- JUnit workflow tests for add/list/find, mark/unmark/delete, and `on` command interactions.
- Storage tests for old saved rows without completion timestamps and new rows with completion timestamps.
- UI tests for valid `stats` output and invalid `stats` arguments.
