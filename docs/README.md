# Herta User Guide

## Contents

- [Quick start](#quick-start)
- [Understanding Herta](#understanding-herta)
- [Features](#features)
- [GUI shortcuts](#gui-shortcuts)
- [Saving and transferring data](#saving-and-transferring-data)
- [Command summary](#command-summary)

## Introduction

Herta helps you manage everyday tasks using short commands in a chat-style
desktop interface. Create todos, deadlines, and events, then find, filter, sort,
complete, archive, and restore them. It is designed for people who prefer fast
keyboard-based interaction.

## Quick start

1. Install JDK 25 or later.
2. Download `Herta.jar` from the [latest release](https://github.com/hhkennn/ip/releases/latest).
3. Put `Herta.jar` in the folder where you want Herta to store its data.
4. Open a terminal in that folder and run:

   ```text
   java -jar Herta.jar
   ```

5. Wait for the Herta window to appear.

   If Herta does not start, confirm that `Herta.jar` is in the current
   folder and rerun `java -jar Herta.jar`.

6. Enter a command and press Enter or click **Send**.
7. Try these commands first:

   ```text
   todo read the user guide
   list
   ```

## Understanding Herta

This session shows active tasks. Enter commands at the bottom; responses
appear in the conversation area.

<p align="center">
  <a href="Ui.png">
    <img src="Ui.png" alt="Herta's main window showing active tasks and example commands" width="480">
  </a>
</p>

### Command notation

This guide uses the following notation:

- `UPPER_CASE` represents a value supplied by the user.
- `[square brackets]` mark optional items.
- `...` means that the preceding item may be repeated.
- Enter each command on one line.
- Command names and markers such as `/by` must be lowercase.
- Search keywords passed to `find` are matched case-insensitively.
- Commands that take no arguments reject extra arguments.

For example, in `deadline DESCRIPTION /by DATE_OR_DATE_TIME`, replace
`DESCRIPTION` and `DATE_OR_DATE_TIME` with your own values, but type `deadline`
and `/by` exactly as shown.

When creating a task, its description must contain visible text. Ordinary
spaces and Unicode text are supported, but descriptions cannot contain the
vertical-bar character, line breaks, control characters, or unsupported
invisible spacing characters. Keep descriptions to 1,000 characters or fewer.
Herta permits duplicate tasks in either list and across both lists.

### Task types and statuses

Tasks show a number, type, status, and description. Dated tasks also show their
date or time. For example:

`2. [D][X] submit report (by: Oct 15 2026, 6:00 PM)`

| Symbol | Meaning |
| --- | --- |
| `2.` | The task's number in the active list |
| `[T]` | Todo |
| `[D]` | Deadline |
| `[E]` | Event |
| `[ ]` | Incomplete |
| `[X]` | Complete |

Newly created tasks are incomplete.

### Task numbers

> **Important:** `mark`, `delete`, and `archive` use active-list numbers.
> Results from `find`, `filter`, `upcoming`, and `sort` retain the original
> active-task numbers even when their displayed order changes.

Task numbers begin at 1. Deleting or archiving can renumber later active tasks.
Archived tasks use a separate numbering system shown by `archived`;
`restore` uses an archive number, not an active-task number.

### Supported date and time formats

Accepted formats:

| Input | Format | Example | Used by |
| --- | --- | --- | --- |
| Date only | `d/M/yyyy` | `2/12/2026` | `deadline`, `event`, `filter` |
| Date only | `yyyy-MM-dd` | `2026-12-02` | `deadline`, `event`, `filter` |
| Date and time | `d/M/yyyy HHmm` | `2/12/2026 1800` | `deadline`, `event` |
| Date and time | `yyyy-MM-dd HHmm` | `2026-12-02 1800` | `deadline`, `event` |
| Date and time | `yyyy-MM-dd HH:mm` | `2026-12-02 18:00` | `deadline`, `event` |

Times use 24-hour notation: `1800` and `18:00` both mean 6:00 PM.

- A date without a time means midnight at the start of that date.
- An event must end after it starts.
- Dates must be valid calendar dates between `0001-01-01` and `9999-12-31`.
- Past dates are accepted.
- Herta displays dates using English month names.
- Natural-language dates such as `tomorrow` and `next Friday` are not supported.

## Features

### Adding a todo: `todo`

Adds an incomplete task without a date or time.

Format: `todo DESCRIPTION`

Example: `todo buy groceries`

The description is required. A todo has no date, so `filter` and `upcoming`
exclude it; `sort date` places it after dated tasks.

### Adding a deadline: `deadline`

Adds an incomplete task with a due date or time.

Format: `deadline DESCRIPTION /by DATE_OR_DATE_TIME`

Example: `deadline submit report /by 2026-10-15 18:00`

`/by` is required and may appear only once. A date-only value may use either
`d/M/yyyy` or `yyyy-MM-dd`.

### Adding an event: `event`

Adds an incomplete task with a start and end date or time.

Format: `event DESCRIPTION /from START /to END`

Example: `event project meeting /from 2026-10-15 18:00 /to 2026-10-15 20:00`

`/from` must appear before `/to`, and both markers may appear only once. Each
start or end value may be a date or a date and time; the end must be later than
the start.

### Listing active tasks: `list`

Displays all active tasks in their saved order.

Format: `list`

Archived tasks are excluded. With no active tasks, Herta displays
`No tasks to show. Give me something to organize.` and suggests adding a task.

### Finding tasks by description: `find`

Searches active-task descriptions for a keyword or phrase.

Format: `find KEYWORD`

Example: `find report`

Matching is case-insensitive and uses substrings, so `book` matches `read book`.
The whole keyword or phrase is one substring, not an OR search over separate
words. Results retain their original active-task numbers.

### Viewing tasks on a date: `filter`

Shows dated active tasks that occur on a specified calendar date.

Format: `filter /on DATE`

Examples: `filter /on 2026-10-15` and `filter /on 15/10/2026`

It includes deadlines due on that date and events overlapping any part of it.
Both complete and incomplete tasks are included; todos are excluded because
they have no date. Results retain their original active-task numbers.

### Viewing upcoming tasks: `upcoming`

Shows incomplete dated tasks in a future time window.

Format: `upcoming DAYS`

Example: `upcoming 7`

`DAYS` must be a positive whole number. It includes deadlines due within the
window and events whose start falls within it, but excludes todos and completed
tasks. The window begins now, includes its starting instant, and ends just
before `DAYS` days later. Results retain their original active-task numbers.

### Displaying tasks in date order: `sort`

Displays active tasks chronologically without changing their saved order.

Format: `sort date`

Dated tasks are ordered by a deadline's due time or event's start time. Todos
appear after dated tasks, and equal times retain their existing order. Original
active-task numbers remain visible.

### Marking a task complete: `mark`

Marks an active task as complete.

Format: `mark INDEX`

Example: `mark 2`

`INDEX` must be a positive number currently present in the active list.

### Marking a task incomplete: `unmark`

Marks a completed active task as incomplete again.

Format: `unmark INDEX`

Example: `unmark 2`

It remains in its existing active-list position.

### Deleting a task: `delete`

Permanently removes an active task.

Format: `delete INDEX`

Example: `delete 2`

Remaining tasks are renumbered after deletion. Herta has no undo command, so
check the number before deleting.

### Archiving completed tasks: `archive`

Moves completed active tasks to the archive using numbers, inclusive ranges, or
`archive all`.

Formats:

- `archive TASK_NUMBER_OR_RANGE [MORE_TASK_NUMBERS_OR_RANGES]...`
- `archive all`

Examples: `archive 2`, `archive 1 3-5 8`, and `archive all`

Explicit selections may mix numbers and ranges. Every selected task must be
complete and valid; otherwise nothing is archived. Repeated or overlapping
selectors are de-duplicated, and archive order follows active-list order.
`archive all` moves every completed active task and leaves incomplete tasks
active; do not combine `all` with selectors.

### Viewing archived tasks: `archived`

Displays the archive.

Format: `archived`

Archived tasks use independent numbers beginning at 1; active tasks are not
displayed.

### Restoring an archived task: `restore`

Moves one archived task back to the active list.

Format: `restore ARCHIVE_INDEX`

Example: `restore 1`

Use the number shown by `archived`, not an active-task number. The task is
appended to the active list with its type, description, dates, and completion
status preserved. Remaining archived tasks are renumbered.

### Exiting Herta: `bye`

Exits the application.

Format: `bye`

The GUI briefly displays Herta's goodbye response before closing.

## GUI shortcuts

| Action | Shortcut |
| --- | --- |
| Submit a command | Enter or click **Send** |
| Move focus between the command box, **Send** button, and conversation area | Tab |
| Recall an older submitted command | Up arrow |
| Move toward newer commands or restore the unfinished draft | Down arrow |
| Increase message and avatar size | `Ctrl` + `+` or hold `Ctrl` and scroll up |
| Decrease message and avatar size | `Ctrl` + `-` or hold `Ctrl` and scroll down |
| Reset zoom | `Ctrl` + `0` |
| Copy a message's text | Right-click it and select **Copy** |

Up/Down history requires focus in the command box.

## Saving and transferring data

Changes save automatically. By default, Herta stores active tasks in
`data/herta.txt` and archived tasks in `data/archive.txt` inside the folder you
run Herta from.

Herta creates the `data` folder and files after the first successful task
change, so a fresh installation may have none.

To transfer tasks:

1. Close Herta.
2. Back up the destination `data` folder, if it exists.
3. Create `data` if needed.
4. Copy `herta.txt` and, if present, `archive.txt` from the source `data` to
   the destination.
5. Start Herta there.

No source files means nothing to transfer. Without `archive.txt`, remove any
old destination copy after backing it up to avoid stale archived tasks. Copying
replaces same-named files.

> **Caution:** Always close Herta and back up existing data before editing or
> replacing task files. Invalid or inaccessible data prevents Herta from
> accepting commands.

## Command summary

| Action | Format |
| --- | --- |
| Add a todo | `todo DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by DATE_OR_DATE_TIME` |
| Add an event | `event DESCRIPTION /from START /to END` |
| List active tasks | `list` |
| Find tasks by description | `find KEYWORD` |
| View tasks on a date | `filter /on DATE` |
| View upcoming tasks | `upcoming DAYS` |
| Display tasks chronologically | `sort date` |
| Mark a task complete | `mark INDEX` |
| Mark a task incomplete | `unmark INDEX` |
| Delete an active task | `delete INDEX` |
| Archive selected tasks | `archive TASK_NUMBER_OR_RANGE [MORE_TASK_NUMBERS_OR_RANGES]...` |
| Archive all | `archive all` |
| View archived tasks | `archived` |
| Restore an archived task | `restore ARCHIVE_INDEX` |
| Exit Herta | `bye` |
