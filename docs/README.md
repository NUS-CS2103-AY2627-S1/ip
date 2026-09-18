# Waffles User Guide

Waffles is an upbeat planning co-pilot for keeping everyday plans clear. It
helps you capture tasks, manage dates, and stay aware of scheduling clashes.

![Waffles full GUI](Ui.png)

## Getting started

Use Java 25, then launch the desktop app:

```text
gradlew.bat run --console=plain
```

Type a command in the message box and press Enter or click **Send**. Waffles
shows your command and its response in the conversation. Invalid commands are
shown as highlighted errors with a correction hint.

For a command-line session instead, run:

```text
gradlew.bat run --args="--cli" --console=plain
```

## Commands

| Command | Example | What it does |
| --- | --- | --- |
| `todo` | `todo plan the week` | Adds a task without a date |
| `deadline` | `deadline submit report /by 2026-09-30` | Adds a task with a due date |
| `event` | `event design review /from 2026-09-22 /to 2026-09-23` | Adds an event across a date range |
| `list` | `list` | Displays all tasks and their numbers |
| `find` | `find report` | Finds tasks whose descriptions contain a keyword |
| `mark` | `mark 1` | Marks task 1 as complete |
| `unmark` | `unmark 1` | Marks task 1 as incomplete |
| `delete` | `delete 1` | Removes task 1 |
| `bye` | `bye` | Ends a CLI session |

Dates use `yyyy-MM-dd`, for example `2026-09-30`. Event end dates must be on
or after their start dates. Task numbers are one-based and refer to the order
shown by `list`.

## Helpful behavior

- Repeated spaces and accidental leading or trailing spaces are normalized.
- Missing descriptions, parameters, invalid dates, duplicate date markers, and
  invalid task numbers receive friendly explanations.
- Waffles warns when a new deadline or event overlaps an existing scheduled
  task, but still lets you decide whether to keep it.
- Tasks are saved locally in `data/waffles.txt`. A missing file starts with an
  empty list and is created when the first task is saved. Damaged records are
  skipped so valid tasks can still be recovered.

## Product website

Open [index.html](index.html) for the Waffles product overview.
