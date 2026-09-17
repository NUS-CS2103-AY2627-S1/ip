# C-Update test plan

## Scope

Verify that the `update` command edits Todo, Deadline, and Event tasks without
changing unrelated task state, and that the JavaFX edit controls use the same
command path as the console interface.

## JUnit coverage

| Area | Scenarios |
| --- | --- |
| Description | Update a Todo description; preserve the task position and type. |
| Deadline | Update a date; reject malformed dates; preserve display formatting. |
| Event | Update one boundary; update multiple fields together; retain unchanged boundaries. |
| Type conversion | Convert Todo to Deadline/Event and preserve completion status. |
| No-op | Report that nothing changed when supplied values already match. |
| Validation | Missing number, missing fields, blank values, unknown fields, duplicates, incompatible fields, and invalid indexes. |
| State safety | Invalid input leaves the task list and persisted file unchanged. |
| Persistence | Updated tasks reload correctly from the existing storage format. |
| Compatibility | Existing task commands, list numbering, and task display formats remain unchanged. |

## Manual GUI checks

1. Start the JavaFX application with at least one task.
2. Open `Edit task` and confirm the current task values are pre-filled.
3. Change the task type and verify only the relevant date/time fields are enabled.
4. Save a valid edit and confirm the generated update command and response appear in the conversation.
5. Enter an invalid value and confirm the error is shown while the edit panel remains open.
6. Cancel an edit and confirm no task or storage-file change occurs.

## Regression command

Run `gradlew check` with Java 25. This executes JUnit tests and Checkstyle.
