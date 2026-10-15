# Zaru User Guide

Zaru is a friendly desktop chatbot that keeps track of your todos, deadlines, and events. Enter a command in the
message box and press **Enter**; Zaru saves every change automatically so your tasks are ready the next time you
start the app.

![Zaru's main window](Ui.png)

## Quick start

1. Ensure that Java 25 or later is installed.
2. Download `zaru.jar` and place it in the folder where you want Zaru to store its data.
3. Open a terminal in that folder and run `java -jar zaru.jar`.
4. Try `todo read chapter 1`, then enter `list` to see your task.

## Features

### Command format

- Words in `UPPER_CASE` are values you supply. For example, replace `DESCRIPTION` with `read chapter 1`.
- Parameters beginning with `/` must include a value. Spaces around `/` are allowed.
- Keyed parameters such as `/from` and `/to` may be given in either order.
- Dates use `yyyy-MM-dd`; add a 24-hour time as `HHmm` when needed. A date without a time means midnight.
- `TASK_NUMBER` is the number shown by `list`. Task numbers start at 1 and can change after deleting or sorting.
- Task descriptions can contain up to 500 characters, but cannot contain `|`, `/`, or line breaks.

### Adding a todo: `todo`

Adds a task without a date or time.

Format: `todo DESCRIPTION`

Example: `todo read chapter 1`

### Adding a deadline: `deadline`

Adds a task that must be completed by a particular date or time.

Format: `deadline DESCRIPTION /by DATE_TIME`

Examples:

- `deadline submit report /by 2026-09-20`
- `deadline submit report /by 2026-09-20 2359`

### Adding an event: `event`

Adds an event with a start and end. The end must be later than the start.

Format: `event DESCRIPTION /from DATE_TIME /to DATE_TIME`

Example: `event project meeting /from 2026-09-20 1400 /to 2026-09-20 1530`

### Viewing all tasks: `list`

Shows every task with its task number. `[T]`, `[D]`, and `[E]` identify todos, deadlines, and events; `[x]` means
complete and `[ ]` means incomplete.

Format: `list`

### Finding tasks: `find`

Shows tasks whose descriptions contain the search text. The search ignores letter case but otherwise matches the
exact text you enter.

Format: `find SEARCH_TEXT`

Example: `find report`

### Sorting tasks: `sort`

Reorders and displays all tasks. Todos come first and are ordered alphabetically, followed by deadlines in due-date
order, then events in start-time order. The new order is saved.

Format: `sort`

### Marking a task as complete: `mark`

Marks the numbered task as complete.

Format: `mark TASK_NUMBER`

Example: `mark 2`

### Marking a task as incomplete: `unmark`

Marks the numbered task as incomplete again.

Format: `unmark TASK_NUMBER`

Example: `unmark 2`

### Deleting a task: `delete`

Permanently removes the numbered task.

Format: `delete TASK_NUMBER`

Example: `delete 2`

### Exiting Zaru: `bye`

Closes Zaru after a short goodbye. Your earlier changes have already been saved automatically.

Format: `bye`

## Saving data

Zaru automatically saves changes to `data/zaru.txt`, relative to the folder from which you launch the app. There is
no save command. To move your tasks to another computer, copy this file into the same location beside your JAR's
folder structure. Avoid editing it manually, as invalid data can prevent Zaru from loading your task list.

## Command summary

| Action | Format |
| --- | --- |
| Add a todo | `todo DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by DATE_TIME` |
| Add an event | `event DESCRIPTION /from DATE_TIME /to DATE_TIME` |
| View tasks | `list` |
| Find tasks | `find SEARCH_TEXT` |
| Sort tasks | `sort` |
| Complete a task | `mark TASK_NUMBER` |
| Reopen a task | `unmark TASK_NUMBER` |
| Delete a task | `delete TASK_NUMBER` |
| Exit Zaru | `bye` |
