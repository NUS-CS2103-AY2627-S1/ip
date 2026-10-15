# PeanutButterCat User Guide

PeanutButterCat is a cozy task keeper that helps you record, organize, search, and complete tasks through short
commands. It supports simple todos, deadlines, and events, and saves every change automatically.

## Table of contents

- [Quick start](#quick-start)
- [Reading this guide](#reading-this-guide)
- [Command summary](#command-summary)
- [Adding a todo](#adding-a-todo)
- [Adding a deadline](#adding-a-deadline)
- [Adding an event](#adding-an-event)
- [Viewing all tasks](#viewing-all-tasks)
- [Finding tasks](#finding-tasks)
- [Viewing tasks on a date](#viewing-tasks-on-a-date)
- [Marking and unmarking tasks](#marking-and-unmarking-tasks)
- [Deleting a task](#deleting-a-task)
- [Viewing recent completion statistics](#viewing-recent-completion-statistics)
- [Exiting the application](#exiting-the-application)
- [Saving and recovering data](#saving-and-recovering-data)
- [Troubleshooting](#troubleshooting)

## Quick start

1. Install Java 25.
2. Download `peanutbuttercat.jar`.
3. Open a terminal in the folder containing the JAR file.
4. Run:

   ```text
   java -jar peanutbuttercat.jar
   ```

5. Enter a command in the text field, then press <kbd>Enter</kbd> or select **Send 🐾**.

PeanutButterCat creates `data/peanutbuttercat.txt` beside the folder from which you launch it. Keep that file if you
want your tasks to remain available the next time you open the application.

## Reading this guide

Command words and examples appear in `monospace`. Words in `UPPER_CASE` are values that you should replace.

For example, in `todo DESCRIPTION`, replace `DESCRIPTION` with the task you want to record:

```text
todo buy cat food
```

Task numbers are the numbers shown by the `list`, `find`, and `on` commands. Use `list` before a numbered command if
you are unsure which task to select.

### Accepted date and time formats

PeanutButterCat accepts the following formats:

Format | Example
--- | ---
`yyyy-MM-dd` | `2026-09-20`
`yyyy-MM-dd HHmm` | `2026-09-20 1800`
`yyyy-MM-dd HH:mm` | `2026-09-20 18:00`
`d/M/yyyy HHmm` | `20/9/2026 1800`
`d/M/yyyy HH:mm` | `20/9/2026 18:00`

Times use the 24-hour clock. If you enter an ISO date without a time, PeanutButterCat uses `00:00` (midnight).

## Command summary

Command | Purpose | Format
--- | --- | ---
`todo` | Add a task without a date | `todo DESCRIPTION`
`deadline` | Add a task with a due date or time | `deadline DESCRIPTION /by DATE_TIME`
`event` | Add an event with a start and end | `event DESCRIPTION /from START /to END`
`list` | Show every task | `list`
`find` | Find descriptions containing a keyword | `find KEYWORD`
`on` | Show deadlines and events occurring on a date | `on yyyy-MM-dd`
`mark` | Mark a task as complete | `mark TASK_NUMBER`
`unmark` | Mark a task as incomplete | `unmark TASK_NUMBER`
`delete` | Remove a task | `delete TASK_NUMBER`
`stats` | Show completions from the last seven calendar days | `stats`
`bye` | Display PeanutButterCat's farewell | `bye`

## Adding a todo

Use `todo` for a task that does not need a date or time.

Format: `todo DESCRIPTION`

Example:

```text
todo buy cat food
```

Expected response:

```text
Spread the word - this task is in the jar:
[T][ ] buy cat food
The task jar now holds 1 task.
```

`[T]` identifies a todo. `[ ]` means that the task is incomplete.

## Adding a deadline

Use `deadline` for a task that must be completed by a particular date or time.

Format: `deadline DESCRIPTION /by DATE_TIME`

Example:

```text
deadline submit report /by 2026-09-20 1800
```

Expected response:

```text
Spread the word - this task is in the jar:
[D][ ] submit report (by: Sep 20 2026 6:00 PM)
The task jar now holds 1 task.
```

`[D]` identifies a deadline. The `/by` separator is required.

## Adding an event

Use `event` for an activity with a start and end. An event can span more than one day.

Format: `event DESCRIPTION /from START /to END`

Example:

```text
event project meeting /from 2026-09-20 1400 /to 2026-09-20 1600
```

Expected response:

```text
Spread the word - this task is in the jar:
[E][ ] project meeting (from: Sep 20 2026 2:00 PM to: Sep 20 2026 4:00 PM)
The task jar now holds 1 task.
```

`[E]` identifies an event. The `/from` and `/to` separators are required, and the end cannot be earlier than the
start.

## Viewing all tasks

Use `list` to display every saved task and its task number.

Format: `list`

Example response:

```text
Here's what's tucked in the task jar:
1.[T][ ] buy cat food
2.[D][ ] submit report (by: Sep 20 2026 6:00 PM)
```

The status box is `[ ]` for an incomplete task and `[X]` for a completed task.

## Finding tasks

Use `find` to search task descriptions. The search is case-insensitive and matches descriptions containing the
keyword.

Format: `find KEYWORD`

Example:

```text
find REPORT
```

Example response:

```text
I sniffed out these matching tasks:
1.[D][ ] submit report (by: Sep 20 2026 6:00 PM)
```

If there are no matches, PeanutButterCat displays `No matching crumbs found.`

> **Note:** Numbers in search results number the matches, not necessarily the tasks in the full list. Run `list`
> before using `mark`, `unmark`, or `delete`.

## Viewing tasks on a date

Use `on` to display deadlines due on a date and events that include that date. Todos are not shown because they have
no date.

Format: `on yyyy-MM-dd`

Example:

```text
on 2026-09-20
```

Example response:

```text
Here's what's on the plate for Sep 20 2026:
2.[D][ ] submit report (by: Sep 20 2026 6:00 PM)
3.[E][ ] project meeting (from: Sep 20 2026 2:00 PM to: Sep 20 2026 4:00 PM)
```

The displayed numbers are the tasks' positions in the full task list, so you can use them directly with `mark`,
`unmark`, and `delete`.

## Marking and unmarking tasks

Use `mark` to record that a task is complete.

Format: `mark TASK_NUMBER`

Example:

```text
mark 1
```

Expected response:

```text
Paw-some! That's one smooth finish:
  [T][X] buy cat food
```

Use `unmark` if a completed task becomes active again.

Format: `unmark TASK_NUMBER`

Example:

```text
unmark 1
```

Expected response:

```text
Back on the plate! This task is active again:
  [T][ ] buy cat food
```

## Deleting a task

Use `delete` to permanently remove a task from the task jar.

Format: `delete TASK_NUMBER`

Example:

```text
delete 1
```

Expected response:

```text
Scoop complete! I've removed this task from the jar:
  [T][ ] buy cat food
The task jar now holds 0 tasks.
```

Task numbers can change after a deletion. Run `list` again before using another numbered command.

## Viewing recent completion statistics

Use `stats` to see how many tasks were completed today or during the previous six calendar days.

Format: `stats`

Example response:

```text
In the last 7 calendar days, you finished 2 tasks. Nice spread!
```

The command does not accept extra arguments. Unmarking a task removes its completion date, so it is no longer
included in the count.

Completed tasks saved by an older version may not have a completion date. PeanutButterCat excludes them from the
count and reports how many were excluded. To give one of these tasks a completion date, unmark it and mark it again.

## Exiting the application

Enter `bye` to display PeanutButterCat's farewell:

```text
The task jar is safe with me. Stay smooth, and see you soon!
```

In the graphical application, close the window when you are finished. Your saved tasks remain available the next
time you launch PeanutButterCat.

## Saving and recovering data

PeanutButterCat saves task changes automatically in `data/peanutbuttercat.txt`. You do not need to use a save
command.

The application can read task files created by older versions. Completion dates are stored as an optional final
`yyyy-MM-dd` field. An older version may discard these dates if it opens and saves the newer file, so keep a backup
before moving a task file back to an older version.

If part of the saved data is invalid, PeanutButterCat loads the valid tasks and displays a warning. If a task change
cannot be saved, the application cancels that change and asks you to check whether it can write to the data folder.

## Troubleshooting

Problem | What to do
--- | ---
PeanutButterCat does not recognize a command | Check the spelling and use one of the commands in the [command summary](#command-summary).
A command says that a description is missing | Add text after `todo`, `deadline`, `event`, or `find`.
A task number is rejected | Run `list`, then enter a whole number shown beside a task.
A date is rejected | Use one of the [accepted date and time formats](#accepted-date-and-time-formats) and check that the date exists.
An event is rejected | Include both `/from` and `/to`, and make sure the end is not earlier than the start.
Changes cannot be saved | Check that the application has permission to create and update the `data` folder.
