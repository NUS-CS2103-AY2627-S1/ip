---
title: Shan User Guide
---

# Shan User Guide

Shan is a friendly desktop chatbot that helps you keep track of tasks, deadlines,
and events using short text commands. 👋

## Quick start

1. Ensure [Java 25](https://www.oracle.com/java/technologies/downloads/) is installed.
2. Open a terminal in the folder containing `shan.jar`.
3. Start Shan:

   ```bash
   java -jar shan.jar
   ```

4. Type a command into the text box and press <kbd>Enter</kbd>.

Try this first-use checklist:

- [ ] Add a task with `todo read book`.
- [ ] View it with `list`.
- [ ] Complete it with `mark 1`.

> **Tip:** Command words are case-insensitive. For example, `list`, `LIST`, and
> `List` are equivalent.

## Command summary

| Action | Command format | Example |
|---|---|---|
| Add a task | `todo DESCRIPTION` | `todo read book` |
| Add a deadline | `deadline DESCRIPTION /by DATE_TIME` | `deadline submit report /by 2026-09-20 18:00` |
| Add an event | `event DESCRIPTION /from DATE_TIME /to DATE_TIME` | `event tutorial /from 2026-09-20 14:00 /to 2026-09-20 16:00` |
| List tasks | `list` | `list` |
| Mark a task | `mark TASK_NUMBER` | `mark 1` |
| Unmark a task | `unmark TASK_NUMBER` | `unmark 1` |
| Delete a task | `delete TASK_NUMBER` | `delete 2` |
| Find tasks | `find KEYWORD` | `find report` |
| Show dated tasks | `on DATE` | `on 2026-09-20` |
| Show a date range | `on START_DATE /to END_DATE` | `on 2026-09-20 /to 2026-09-22` |
| Undo the last change | `undo` | `undo` |
| Exit | `bye` | `bye` |

`TASK_NUMBER` refers to the number shown by `list`. Parameters such as `/by`,
`/from`, and `/to` must be separated from their values by spaces.

## Features

### Adding a task

Use `todo` for a task without a specific date or time.

```text
todo read book
```

Shan adds the task and assigns it the next available task number.

### Adding a deadline

Use `deadline` for something that must be completed by a particular time.

```text
deadline submit report /by 2026-09-20 18:00
```

### Adding an event

Use `event` for something with a start and end time. The end must be later than
the start.

```text
event project meeting /from 2026-09-20 14:00 /to 2026-09-20 16:00
```

Shan accepts these date-time formats:

- `yyyy-MM-dd HH:mm`, such as `2026-09-20 18:00`
- `d/M/yyyy HHmm`, such as `20/9/2026 1800`

Dates are checked strictly, so impossible dates such as `2026-02-30` are rejected.

### Listing tasks

Use `list` to see every task and its task number.

```text
list
```

The status symbols mean:

- `[ ]` — incomplete
- `[X]` — completed
- `[T]` — todo
- `[D]` — deadline
- `[E]` — event

### Marking and unmarking tasks

Use the task number shown by `list`:

```text
mark 1
unmark 1
```

Shan reports an error if the task does not exist or is already in the requested
state.

### Deleting a task

```text
delete 2
```

The remaining tasks are renumbered automatically.

### Finding tasks

Use `find` to search task descriptions. Matching is case-insensitive.

```text
find report
```

Dates and times are not searched.

### Viewing a schedule

Show deadlines and events on one date:

```text
on 2026-09-20
```

Or show those overlapping an inclusive date range:

```text
on 2026-09-20 /to 2026-09-22
```

Dates used with `on` must follow the `yyyy-MM-dd` format.

### Undoing a change

```text
undo
```

`undo` reverses the most recent successful `todo`, `deadline`, `event`, `mark`,
`unmark`, or `delete` command. Shan supports one undo at a time.

### Exiting Shan

```text
bye
```

## Saving and recovering data

Shan saves changes automatically in `data/shan.txt`. You do not need to save
manually.

**Good to know:** *There is no Save command.* ~~Remember to save before exiting.~~

- If the file is missing, Shan starts with an empty task list and creates it when
  a task change is saved.
- If some saved entries are malformed, Shan skips them and displays a warning.
- If the file cannot be read or written, Shan displays an error instead of
  crashing. A failed change is rolled back so the in-memory task list remains
  consistent.

> Do not edit `data/shan.txt` while Shan is running.

## Common command errors

Shan explains invalid input and remains open so you can try again. Common causes
include:

- a missing description, task number, date, or command parameter;
- a task number that is zero, negative, or not a whole number;
- repeated or incorrectly ordered `/by`, `/from`, or `/to` parameters;
- an invalid date, time, or event range; and
- extra arguments after `list`, `undo`, or `bye`.

Leading, trailing, and repeated spaces are accepted.
