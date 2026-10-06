# Reminder feature test plan

## Reminder classification

- Incomplete deadlines from now through exactly 72 hours later are upcoming.
- Incomplete events use their start time and follow the same 72-hour rule.
- A timestamp exactly equal to now is upcoming.
- A timestamp exactly 72 hours from now is upcoming.
- Timestamps strictly before now are missed, with no maximum age.
- Tasks more than 72 hours in the future are not reported.
- Completed tasks are excluded from both categories.

## Report formatting

- Upcoming and missed items are shown in separate sections.
- Each section is ordered by relevant timestamp.
- Equal timestamps are ordered by original task number.
- Original one-based task numbers are preserved after sorting.
- Deadline due dates and complete event ranges are displayed.
- An empty report is returned when no reminders apply.

## Command behavior

- `reminders` returns the current report.
- `reminders` with non-blank arguments returns a validation error.
- An explicit `reminders` command with no reminders returns the no-reminders message.
- Startup displays reminders only when at least one reminder applies.

## Compatibility

- Existing task commands continue to work.
- Existing storage records load without migration.
- Reminder evaluation does not modify task status, order, or storage.

## Scheduling conflict detection

- Deadline additions compare equal timestamps.
- A deadline conflicts with an event only when it falls strictly inside the event.
- Events conflict only when their intervals have positive-duration overlap.
- Events and deadlines that touch at an endpoint do not conflict.
- Todos and completed dated tasks are ignored.
- Incomplete dated tasks in the past still participate in conflict detection.
- Reversed newly entered events are rejected, while malformed legacy events remain
  loadable and are ignored for conflict detection.

## Conflict confirmation

- A conflicting task is shown with every conflict and is not saved before confirmation.
- `yes` adds and saves the pending task.
- `no` leaves the task list and storage unchanged.
- Invalid confirmation input shows an error and keeps the confirmation pending.
- `bye` is rejected as invalid confirmation input and keeps the confirmation pending.
