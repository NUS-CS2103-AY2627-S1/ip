# Test Plan: Snooze and Reschedule Deadlines

## Scope

Verify that deadline dates can be postponed by whole days or replaced by a
valid non-past date without changing completion status or other task types.

## Automated tests

- `DeadlineTest`: deadline date replacement.
- `TrackerTest`: snooze persistence and completed-task rescheduling.
- `ParserTest`: command dispatch and invalid duration/date handling.
- Verify repeated snoozing uses the latest stored date.
- Verify todos and events are rejected.
- Verify updated dates are used by `upcoming` and `due`.
- Verify existing save files remain loadable without migration.

## Manual tests

1. Add a deadline and run `snooze 1 3d`; verify the date advances.
2. Snooze it again; verify the second operation uses the new date.
3. Reschedule a completed deadline; verify `[X]` remains displayed.
4. Try snoozing a todo and rescheduling an event; verify an error appears.
5. Restart Bo and verify the updated date is retained.
