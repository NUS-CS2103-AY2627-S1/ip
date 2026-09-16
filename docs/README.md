# Crow User Guide

**Crow** is a desktop chatbot that helps you manage to-dos, deadlines, and
events using short text commands. Your tasks are saved automatically, so they
remain available the next time you start Crow.

## Quick start

1. Ensure Java 25 is installed.
2. Open a terminal in the project root.
3. Run Crow with `./gradlew run`, or build it with `./gradlew shadowJar` and run
   `java -jar build/libs/crow.jar`.
4. Type a command into the input box and press <kbd>Enter</kbd>.

> **Tip:** Commands are case-insensitive, and extra spaces around words are
> accepted.

## Reading the task list

Crow displays each task with a type and status:

- `[T]` — ToDo
- `[D]` — Deadline
- `[E]` — Event
- `[ ]` — not done
- `[X]` — done

Tasks are automatically grouped by type. ToDos are sorted alphabetically,
deadlines by deadline, and events by start time.

## Features

### Add a ToDo: `todo`

Adds a task without a date or time.

Format: `todo DESCRIPTION`

Example:

```text
todo read book
```

### Add a deadline: `deadline`

Adds a task that must be completed by a specific date and time.

Format: `deadline DESCRIPTION /by d/M/yyyy HHmm`

Example:

```text
deadline submit report /by 2/12/2019 1800
```

The example represents 2 December 2019 at 6:00 PM. Crow rejects invalid dates,
such as 31 February.

### Add an event: `event`

Adds a task with a start and end date and time. The start must be earlier than
the end.

Format: `event DESCRIPTION /from d/M/yyyy HHmm /to d/M/yyyy HHmm`

Example:

```text
event project meeting /from 3/12/2019 1400 /to 3/12/2019 1600
```

### View all tasks: `list`

Displays every task in its automatically sorted group.

Format: `list`

### Find tasks: `find`

Displays tasks whose descriptions contain the keyword. Matching is
case-insensitive.

Format: `find KEYWORD`

Example:

```text
find book
```

### Mark a task as done: `mark`

Marks the task at the specified number as done. Use the number shown by
`list`.

Format: `mark TASK_NUMBER`

Example: `mark 2`

### Mark a task as not done: `unmark`

Marks the task at the specified number as not done. Use the number shown by
`list`.

Format: `unmark TASK_NUMBER`

Example: `unmark 2`

### Delete a task: `delete`

Permanently removes the task at the specified number. Use the number shown by
`list`.

Format: `delete TASK_NUMBER`

Example: `delete 3`

### Exit Crow: `bye`

Closes the application.

Format: `bye`

## Saving data

Crow automatically saves the task list whenever you add, mark, unmark, or
delete a task. Data is stored in `data/crow.txt`, relative to the directory from
which Crow is started. The folder and file are created automatically when
needed.

Avoid editing the data file manually, as invalid content may prevent Crow from
loading the saved tasks.

## Command summary

| Action | Format |
| --- | --- |
| Add ToDo | `todo DESCRIPTION` |
| Add deadline | `deadline DESCRIPTION /by d/M/yyyy HHmm` |
| Add event | `event DESCRIPTION /from d/M/yyyy HHmm /to d/M/yyyy HHmm` |
| List tasks | `list` |
| Find tasks | `find KEYWORD` |
| Mark task | `mark TASK_NUMBER` |
| Unmark task | `unmark TASK_NUMBER` |
| Delete task | `delete TASK_NUMBER` |
| Exit | `bye` |
