# Tangent User Guide

Tangent is a chatbot for managing tasks, deadlines, and events. It can be launched with a command line
interface or a GUI. Type a command for the chatbot to function. Changes save automatically.

## Dates

Dates must be entered in the format `d/M/yyyy HHmm`, such as `25/9/2026 2359`.

## Task indexes

For `delete`, `mark` and `unmark` commands that require task indexes as inputs, note the following:

- Task numbers start at `1`.
- Use spaces to separate indexes.
- Use a hyphen to denote index ranges.
- Index ranges include both ends.
- If a `delete`, `mark` or `unmark` command is ran **directly after a `find` command**, it will use the indexes of the `find` command's output.

Example: `mark 2 5 7-9` will mark task 2, 5, 7, 8 and 9.

## Adding a todo: `todo`

Adds a task without a date or time.

Format: `todo DESCRIPTION`

Example:

- `todo finish project`

## Adding a deadline: `deadline`

Adds a task that must be completed by a specific time.

Format: `deadline DESCRIPTION /by DATE TIME`

Example:

- `deadline submit report /by 25/9/2026 2359` 

## Adding an event: `event`

Adds task with a start and end time.

Format: `event DESCRIPTION /from DATE TIME /to DATE TIME`

End time must be later than the start time.

Example:

- `event team meeting /from 26/9/2026 1400 /to 26/9/2026 1500` 

## Listing all tasks: `list`

Shows all saved tasks with task numbers.

Format: `list`

## Finding tasks by keyword: `find`

Shows tasks whose descriptions contain the specified keyword, case-insensitive.

Format: `find KEYWORD`

Example:

- `find report`

## Marking tasks as done: `mark`

Marks one or more tasks as done.

Format: `mark INDEX [MORE_INDEXES]`

Examples:

- `mark 6` marks task 6 as done.
- `mark 2 5 7-9` marks task 2, 5, 7, 8 and 9 as done.

## Marking tasks as undone: `unmark`

Marks one or more tasks as undone.

Format: `unmark INDEX [MORE_INDEXES]`

Examples:

- `unmark 6` marks task 6 as not done.
- `unmark 2 5 7-9` marks task 2, 5, 7, 8 and 9 as not done.

## Deleting tasks: `delete`

Permanently deletes one or more tasks.

Format: `delete INDEX [MORE_INDEXES]`

Examples:

- `delete 6` deletes task 6.
- `delete 2 5 7-9` deletes task 2, 5, 7, 8 and 9.

## Exiting the program: `bye`

Closes Tangent.

Format: `bye`

## Command summary

| Action | Command | Example |
| --- | --- | --- |
| Add todo | `todo DESCRIPTION` | `todo finish project` |
| Add deadline | `deadline DESCRIPTION /by DATE TIME` | `deadline submit report /by 25/9/2026 2359` |
| Add event | `event DESCRIPTION /from DATE TIME /to DATE TIME` | `event team meeting /from 26/9/2026 1400 /to 26/9/2026 1500` |
| List | `list` | `list` |
| Find | `find KEYWORD` | `find report` |
| Mark done | `mark INDEX [MORE_INDEXES]` | `mark 6` |
| Mark not done | `unmark INDEX [MORE_INDEXES]` | `unmark 2 5 7-9` |
| Delete | `delete INDEX [MORE_INDEXES]` | `delete 3-5` |
| Exit | `bye` | `bye` |
