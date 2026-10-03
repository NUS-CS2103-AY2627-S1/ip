# Pip Error Handling and C-Undo Test Plan

## Setup

1. Start Pip with an empty task data file.
2. Enter each command exactly as shown.
3. Restart Pip or restore the empty data file before each test case unless the steps say otherwise.

## Command parsing and validation

### Valid undo command

1. Enter `todo buy milk`.
2. Enter `undo`.

Expected: Pip displays `One step back—I've removed the task you added:`, removes `buy milk`, and reports that the
list contains zero tasks.

### Undo with arguments

1. Enter `undo 1`.

Expected: Pip displays `Use this format: undo`.

### Incorrect capitalization

1. Enter `Undo`.
2. Enter `UNDO`.

Expected: Pip displays `I lost that trail. Check the command and try again!` after each command.

## Undoing task additions

Repeat the following test with a todo, deadline, and event:

1. Add the task.
2. Enter `undo`.
3. Enter `list`.

Expected: The added task is absent, the reported task count is correct, and the storage file no longer contains the
task.

## Undoing deletion

1. Add tasks named `first`, `second`, and `third` in that order.
2. Enter `delete 2`.
3. Enter `undo`.
4. Enter `list`.

Expected: `second` is restored between `first` and `third` with its original task type, details, and completion state.

## Undoing status changes

### Mark

1. Add an incomplete todo.
2. Mark it.
3. Enter `undo`.

Expected: The todo is incomplete again.

### Unmark

1. Add and mark a todo.
2. Unmark it.
3. Enter `undo`.

Expected: The todo is complete again.

### No-op status commands

1. Mark an already-complete task, then enter `undo`.
2. Unmark an already-incomplete task, then enter `undo`.

Expected: Each status command creates and consumes an undo opportunity while restoring the same prior status.

## History behavior

### Read-only and invalid commands

1. Add a todo.
2. Enter `list`, `find todo`, an invalid task-number command, and an unknown command.
3. Enter `undo`.

Expected: The original add command is undone.

### Replacement by a newer mutation

1. Add `first`.
2. Add `second`.
3. Enter `undo`.

Expected: Only `second` is removed.

### Single-use undo

1. Add a task.
2. Enter `undo` twice.

Expected: The first undo removes the task. The second displays `No steps to retrace yet.`

### Session-only history

1. Add a task.
2. Restart Pip.
3. Enter `undo`.

Expected: Pip displays `No steps to retrace yet.` The task remains loaded from storage.

## Persistence and compatibility

1. Undo each supported mutation and inspect the task data file.
2. Start Pip using a task data file created before C-Undo.

Expected: Successful undo operations are saved immediately. The existing todo, deadline, and event record formats are
unchanged, and no undo-history record or separate history file is created.

## Flexible command whitespace

1. Enter `  todo   buy    milk  `.
2. Enter `list`.

Expected: Pip accepts the command and displays one task named `buy milk`.

Repeat representative commands using tabs between their parts. Expected: tabs are treated as separators.

## Missing, repeated, and misplaced parameters

Enter each of the following independently:

- `todo`
- `deadline report`
- `deadline report /by`
- `deadline report /by 2026-10-01 /by 2026-10-02`
- `event meeting /from 2026-10-01 0900`
- `event meeting /to 2026-10-01 1000 /from 2026-10-01 0900`
- `event meeting /from 2026-10-01 0900 /to 2026-10-01 1000 /to later`
- `find`
- `list extra`
- `bye now`

Expected: Pip rejects every command with a message showing the valid format. The task list and undo history remain
unchanged.

## Task-number validation

Try `mark 0`, `mark -1`, `mark 1.5`, `delete +1`, `unmark two`, an integer larger than Java's supported integer
range, and a positive number greater than the number of tasks.

Expected: Pip distinguishes an invalid number, a number that is too large, and a valid number that does not identify
an existing task. No task changes state.

## Text validation

1. Try descriptions containing `|`, line breaks, or control characters.
2. Try a description longer than 200 characters.
3. Try a find keyword longer than 100 characters.
4. Try a command longer than 500 characters.
5. Add `todo Buy Milk`, then try `todo buy   milk`.

Expected: Pip rejects unsafe or oversized input with a specific explanation. The second todo is rejected as a
normalized duplicate, while the first remains available to undo.

## Date and event validation

1. Enter `deadline report /by 2026-02-30`.
2. Enter an event with an impossible boundary date.
3. Enter an event whose start equals its end.
4. Enter an event whose start is after its end.
5. Enter a valid event using `yyyy-MM-dd HHmm`, then another using `d/M/yyyy HHmm`.

Expected: The first four commands are rejected without changing storage. Both valid formats are accepted when the
start is earlier than the end.

## Storage errors and recovery

### Missing storage

Start Pip when the configured directory and task file do not exist.

Expected: Pip creates both and starts with an empty task list.

### Malformed storage

Prepare a file containing valid records mixed with an unknown type, invalid completion status, missing or extra
fields, impossible date, reversed parseable event range, and duplicate task.

Expected: Pip loads valid unique records, reports every skipped line number, and creates a neighboring `.bak` copy
that preserves the original file exactly. Legacy free-text event records still load.

### Unavailable storage

Start Pip with a directory in place of the task file or with a file that cannot be read.

Expected: Pip reports the storage problem and does not accept commands that could create an unpersisted state. The
GUI input is disabled.

### Failed save

Cause a write failure, then attempt a task mutation.

Expected: Pip reports `No changes were applied.` The task list, data file, and previously available undo command all
remain in their pre-command state. No partial data file replaces the original.

## GUI

1. Start the JavaFX interface.
2. Confirm that the welcome message lists `undo`.
3. Execute an undo through the existing command field.

Expected: No new GUI control is present, and all undo response lines appear in one Pip dialog bubble.
