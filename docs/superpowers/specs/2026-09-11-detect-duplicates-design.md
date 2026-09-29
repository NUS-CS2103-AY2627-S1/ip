# C-DetectDuplicates Design

## Goal

Warn when a user adds a task whose details already exist, while still adding
and saving the duplicate task.

## Behaviour

- Duplicate detection applies only to task-creation commands.
- Two tasks match when they have the same concrete task type and the same
  description, ignoring letter case.
- Deadlines must also have the same date.
- Events must also have the same start and end text, ignoring letter case.
- Completion status does not affect whether task details match.
- An existing match changes only the success introduction. The task is still
  appended, persisted, and included in the new task count.
- The duplicate response is:

  ```text
  This task already exists in your list, but I've added it again:
    [TASK]
  Now you have N tasks in the list.
  ```

- Non-duplicate additions retain the existing response.
- No command syntax or storage-format changes are introduced.

## Design

`Task.hasSameDetails(Task)` defines common matching semantics. `Deadline` and
`Event` extend that comparison with their subtype-specific fields.
`TaskList.containsSameDetails(Task)` checks the collection without exposing its
mutable representation. `Pavanmaxxer.addTask` checks before insertion, then
always adds and saves the task and selects the appropriate response text.

## Verification

- Unit tests cover case-insensitive todo matching, type-sensitive matching,
  deadline dates, and event times.
- An application-level test verifies the warning, insertion, count, and
  persistence of a duplicate.
- Existing non-duplicate output remains covered.
- JUnit, Checkstyle, `git diff --check`, and the fat-JAR build must pass.
