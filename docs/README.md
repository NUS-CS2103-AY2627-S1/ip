# Zucc User Guide

**Zucc** is a desktop chatbot that helps you capture, schedule, find, and complete tasks using short typed commands.

![The Zucc chatbot showing a conversation and task list](Ui.png)

## Quick start

1. Ensure that Java 25 is installed.
1. Place `zucc.jar` in the folder where you want Zucc to keep its data.
1. Open a terminal in that folder and run `java -jar zucc.jar`.
1. Type a command in the box at the bottom, then press **Enter** or select **Send**.

Try `todo Buy groceries`, followed by `list`.

Zucc saves changes automatically in `data/zucc.txt`. Your tasks will still be there the next time you start the app from the same folder.

## Understanding commands

In the command formats below:

* Words in `UPPER_CASE` are values you supply.
* Items in `[square brackets]` are optional. Do not type the brackets.
* Dates use `d/M/yyyy`; dates with times use `d/M/yyyy HHmm` and the 24-hour clock. For example, `18/9/2026 2359` means 11:59 PM on 18 September 2026.
* Task numbers are the numbers shown by `list`, `find`, or `on`.

## Adding tasks

### To-dos: `todo`

Use a to-do for a task without a specific date or time.

Format: `todo DESCRIPTION [/priority PRIORITY]`

Example: `todo Buy groceries`

### Deadlines: `deadline`

Use a deadline for a task that must be completed by a particular time.

Format: `deadline DESCRIPTION /by DATE_TIME [/priority PRIORITY]`

Example: `deadline Submit report /by 18/9/2026 2359`

### Events: `event`

Use an event for an activity with a start and end time. The end must be after the start.

Format: `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME [/priority PRIORITY]`

Example: `event Project meeting /from 19/9/2026 1400 /to 19/9/2026 1530`

### Priorities

Add `/priority` when creating any task. Use `high`, `medium`, or `low`; the equivalent values `1`, `2`, and `3` also work. Priority 1 is the highest.

Example: `todo Review pull request /priority high`

Zucc displays the priority as `[P1]`, `[P2]`, or `[P3]`. If you omit `/priority`, the task has no priority marker. For deadlines and events, the named options may be in any order after the description.

## Viewing and finding tasks

### View everything: `list`

Format: `list`

Zucc shows every task with a number. A result such as `2.[D][ ][P1] Submit report` means:

* `2.` is the task number used by commands such as `mark 2`.
* `[T]`, `[D]`, and `[E]` mean to-do, deadline, and event.
* `[ ]` means incomplete; `[X]` means completed.
* `[P1]`, `[P2]`, and `[P3]` are optional priority markers.

### Search descriptions: `find`

Format: `find KEYWORD`

Example: `find report`

Zucc lists tasks whose descriptions contain the exact text supplied. The search is case-sensitive: `report` does not match `Report`.

### View a date: `on`

Format: `on DATE`

Example: `on 19/9/2026`

Zucc shows deadlines due on that date and events occurring on that date. A multi-day event appears on every date from its start date through its end date. To-dos are not included because they have no date.

## Updating tasks

Run `list`, `find`, or `on` first if you need to check a task's number.

* Complete a task with `mark TASK_NUMBER` (for example, `mark 2`).
* Make a completed task incomplete again with `unmark TASK_NUMBER` (for example, `unmark 2`).
* Permanently remove a task with `delete TASK_NUMBER` (for example, `delete 2`).

Task numbers can change after deletion, so check the list again before acting on another task.

## Ending the session

Format: `bye`

`bye` ends the current session and disables further commands. Close and reopen Zucc to begin another session.

## Command summary

| Purpose | Command |
| --- | --- |
| Add a to-do | `todo DESCRIPTION [/priority PRIORITY]` |
| Add a deadline | `deadline DESCRIPTION /by DATE_TIME [/priority PRIORITY]` |
| Add an event | `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME [/priority PRIORITY]` |
| View all tasks | `list` |
| Search descriptions | `find KEYWORD` |
| View scheduled tasks | `on DATE` |
| Mark as complete | `mark TASK_NUMBER` |
| Mark as incomplete | `unmark TASK_NUMBER` |
| Delete a task | `delete TASK_NUMBER` |
| End the session | `bye` |
