# Janet User Guide

**Janet** is a cheerful desktop chatbot that helps you manage to-dos, deadlines, and events using short typed
commands. Janet saves your changes automatically, so your tasks are waiting when you return.

## Quick start

1. Install Java 25.
2. Place `janet.jar` in a folder where Janet may create a `data` subfolder.
3. Open a terminal in that folder and run:

   ```bash
   java -jar janet.jar
   ```

4. Type a command in the message box, then press <kbd>Enter</kbd> or click **Send**.

For a quick tour, try `todo read book`, `deadline return book /by 2099-09-30`, and then `list`.

## Command format

- Words in `UPPER_CASE` are values that you supply. For example, replace `DESCRIPTION` with `read book`.
- Items in square brackets are optional. `delete NUMBER [NUMBER ...]` accepts one or more task numbers.
- Command names are not case-sensitive, but keywords used with `find` are case-sensitive.
- Use the task numbers shown by `list` when marking, unmarking, or deleting tasks.
- Janet rejects an exact duplicate with the same task type, description, and date or time details.

## Features

### Adding a to-do: `todo`

Adds a task without a date or time.

Format: `todo DESCRIPTION`

Example: `todo read book`

### Adding a deadline: `deadline`

Adds a task that must be completed by a specific date. Enter the date in `YYYY-MM-DD` format.

Format: `deadline DESCRIPTION /by YYYY-MM-DD`

Example: `deadline return book /by 2099-09-30`

Janet accepts only real calendar dates. A past date is still added, but Janet includes a warning in its response.

### Adding an event: `event`

Adds a task with a start and end. Use `YYYY-MM-DD`, 24-hour `HH:mm`, or `YYYY-MM-DD HH:mm` for both values. The start
and end must use the same format, and the end must be equal to or later than the start. Janet rejects impossible dates
and times. A past value is still added, but Janet includes a warning in its response.

Format: `event DESCRIPTION /from START /to END`

Example: `event project meeting /from 2099-09-30 14:00 /to 2099-09-30 16:00`

### Listing tasks: `list`

Shows every task and its current number.

Format: `list`

Janet uses these symbols:

| Symbol | Meaning |
| --- | --- |
| `[T]` | To-do |
| `[D]` | Deadline |
| `[E]` | Event |
| `[ ]` | Not completed |
| `[X]` | Completed |

### Marking a task as completed: `mark`

Marks the task with the specified number as completed.

Format: `mark NUMBER`

Example: `mark 2`

### Marking a task as not completed: `unmark`

Returns the task with the specified number to the not-completed state.

Format: `unmark NUMBER`

Example: `unmark 2`

### Finding tasks: `find`

Shows tasks whose descriptions contain the exact keyword or phrase. Matching is case-sensitive and does not search
dates or event times.

Format: `find KEYWORD`

Example: `find book`

Matches always remain in their original task-list order.

> **Note:** Search-result numbers show the order of the results, not necessarily the tasks' numbers in the full list.
> Run `list` before using `mark`, `unmark`, or `delete` on a task you found.

### Deleting tasks: `delete`

Deletes one or more tasks using the numbers shown by `list`.

Format: `delete NUMBER [NUMBER ...]`

Examples:

- `delete 2` deletes task 2.
- `delete 2 4 5` deletes tasks 2, 4, and 5 together.

Each number must identify an existing task and may appear only once. Janet checks every number first; if any number is
invalid, no tasks are deleted.

### Exiting Janet: `bye`

Shows Janet's goodbye message and closes the application.

Format: `bye`

## Saving data

Janet automatically saves successful changes to `data/janet.txt`, relative to the folder from which you launched the
app. To move your tasks to another computer, copy both `janet.jar` and the `data` folder.

If Janet warns that the data file cannot be read or contains malformed content, saving is disabled for that session
to protect the original file. Close Janet, repair or restore `data/janet.txt`, and then restart the app.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add a to-do | `todo DESCRIPTION` | `todo read book` |
| Add a deadline | `deadline DESCRIPTION /by YYYY-MM-DD` | `deadline return book /by 2099-09-30` |
| Add an event | `event DESCRIPTION /from START /to END` | `event meeting /from 14:00 /to 16:00` |
| List tasks | `list` | `list` |
| Mark as completed | `mark NUMBER` | `mark 2` |
| Mark as not completed | `unmark NUMBER` | `unmark 2` |
| Find tasks | `find KEYWORD` | `find book` |
| Delete tasks | `delete NUMBER [NUMBER ...]` | `delete 2 4 5` |
| Exit | `bye` | `bye` |
