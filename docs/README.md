# Chausistant User Guide

**Chausistant** is a chatbot that helps you organise todos, deadlines, and
events. Add a task, then let Chausistant cheer you on while you work through your
list.

## Table of Contents

- [Quick Start](#quick-start)
- [Command Format](#command-format)
- [Features](#features)
  - [Adding Tasks](#adding-tasks)
  - [Viewing Tasks](#viewing-tasks)
  - [Managing Tasks](#managing-tasks)
  - [Finding Tasks](#finding-tasks)
  - [Viewing a Day's Schedule](#viewing-a-days-schedule)
  - [Viewing Upcoming Deadlines](#viewing-upcoming-deadlines)
  - [Saving Data](#saving-data)
- [Command Summary](#command-summary)

## Quick Start

1. Build the application JAR by following the [project setup instructions](https://github.com/chausarahnusitems-tech/ip#creating-and-running-the-executable-jar).
1. Run the generated JAR with Java 25:

   ```sh
   java -jar build/libs/chausistant.jar
   ```

1. Enter a command in the input box and press <kbd>Enter</kbd>.

For example, enter the following command to add a todo:

```text
todo read chapter 1
```

> **Tip:** Use `list` at any time to see your tasks and their task numbers.

## Command Format

- Words in `UPPER_CASE` are values that you supply. For example, `todo TASK` means that you replace `TASK` with a task description.
- Items in square brackets are optional. For example, `deadline TASK /by DATE [HHmm]` accepts a date with or without a time.
- Dates use `DD/MM/YYYY`, for example `05/10/2026`.
- Times use 24-hour `HHmm`, for example `1830` for 6:30 pm.
- Command keywords are case-insensitive. For example, `TODO read chapter 1` works the same as `todo read chapter 1`.
- Task numbers are one-based: the first task shown by `list` is task `1`.

## Features

### Adding Tasks

#### Adding a todo: `todo`

Adds a task without a date or time.

Format: `todo TASK`

Example:

```text
todo read chapter 1
```

Chausistant adds the todo to your list:

```text
[T][ ] read chapter 1
```

#### Adding a deadline: `deadline`

Adds a task that is due on a specified date, optionally at a specified time.

Format: `deadline TASK /by DATE [HHmm]`

Examples:

```text
deadline submit assignment /by 05/10/2026
deadline submit assignment /by 05/10/2026 1800
```

If you omit the time, the deadline is due by the end of that date.

#### Adding an event: `event`

Adds an event with a start and end date, each optionally including a time.

Format: `event TASK /from DATE [HHmm] /to DATE [HHmm]`

Examples:

```text
event project meeting /from 06/10/2026 1400 /to 06/10/2026 1600
event orientation camp /from 10/10/2026 /to 12/10/2026
```

The end of an event must not be before its start.

### Viewing Tasks

#### Listing all tasks: `list`

Shows every task in the order it was added. Each task is prefixed with a number
that you can use with `mark`, `unmark`, and `delete`.

Format: `list`

Example output:

```text
here's your tiny adventure list:
1.[T][ ] read chapter 1
2.[D][X] submit assignment (by: Oct 5 2026 1800)
3.[E][ ] project meeting (from: Oct 6 2026 1400 to: Oct 6 2026 1600)
```

`[T]`, `[D]`, and `[E]` identify todos, deadlines, and events respectively.
`[ ]` means incomplete, while `[X]` means completed.

### Managing Tasks

#### Marking a task as complete: `mark`

Marks the specified task as completed.

Format: `mark TASK_NUMBER`

Example:

```text
mark 2
```

#### Marking a task as incomplete: `unmark`

Marks the specified task as incomplete again.

Format: `unmark TASK_NUMBER`

Example:

```text
unmark 2
```

#### Deleting a task: `delete`

Removes the specified task from the list.

Format: `delete TASK_NUMBER`

Example:

```text
delete 3
```

> **Note:** Task numbers change after a task is deleted. Run `list` before acting on a task number if you are unsure.

### Finding Tasks

#### Finding tasks by keyword: `find`

Shows tasks whose descriptions contain the keyword or phrase. The search is case-insensitive.

Format: `find KEYWORD`

Example:

```text
find assignment
```

> **Note:** Use `list` to confirm a task's number before using `mark`, `unmark`, or `delete`. Search results are numbered independently of the full task list.

### Viewing a Day's Schedule

#### Viewing events and deadlines on a date: `what's on:`

Shows events that occur on the specified date and deadlines due on that date.
An event spanning multiple days appears on every date it spans.

Format: `what's on: DATE`

Example:

```text
what's on: 06/10/2026
```

### Viewing Upcoming Deadlines

#### Viewing deadlines due within seven days: `remind`

Shows incomplete deadlines due from now until seven days from now, ordered by due time.
Todos, events, completed deadlines, and overdue deadlines are not included.

Format: `remind`

Example:

```text
remind
```

### Saving Data

Chausistant saves changes automatically after you add, mark, unmark, or delete a task.
When you start the application again, it restores your saved tasks from
`data/chausistant.txt`, relative to the folder where the JAR is run.

#### Exiting the application: `bye`

Closes the current Chausistant session.

Format: `bye`

## Command Summary

| Action | Format | Example |
| --- | --- | --- |
| Add a todo | `todo TASK` | `todo read chapter 1` |
| Add a deadline | `deadline TASK /by DATE [HHmm]` | `deadline submit assignment /by 05/10/2026 1800` |
| Add an event | `event TASK /from DATE [HHmm] /to DATE [HHmm]` | `event project meeting /from 06/10/2026 1400 /to 06/10/2026 1600` |
| List tasks | `list` | `list` |
| Mark a task complete | `mark TASK_NUMBER` | `mark 2` |
| Mark a task incomplete | `unmark TASK_NUMBER` | `unmark 2` |
| Delete a task | `delete TASK_NUMBER` | `delete 3` |
| Find tasks | `find KEYWORD` | `find assignment` |
| View a day's schedule | `what's on: DATE` | `what's on: 06/10/2026` |
| View upcoming deadlines | `remind` | `remind` |
| Exit | `bye` | `bye` |
