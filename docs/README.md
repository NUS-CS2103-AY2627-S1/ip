# Charlie User Guide

Charlie is a task chatbot for keeping track of todos, deadlines, and events. Type a command and Charlie will remember it for you.

## Quick start

1. Install Java 25. In the project folder, run `gradlew.bat run` on Windows or `./gradlew run` on macOS or Linux.
2. Type a command in the chat box and press **Enter** or click **Roar!**. Try `todo borrow book`, then `list` to see your task.

Charlie saves changes automatically to `data/charlie.txt` in the folder you run it from. Your tasks will be there when you reopen the app.

![Charlie chat window showing a deadline and an event](Ui.png)

## Features

**Command format:** Replace words in `UPPER_CASE` with your own values. For example, use `mark 2` in place of `mark TASK_NUMBER`. Enter each command on one line. Dates use `yyyy-MM-dd` (for example, `2026-09-20`); times use four digits on a 24-hour clock (for example, `1400`).

### Add a todo: `todo`

Adds a task without a date. Format: `todo DESCRIPTION`

Example: `todo borrow book`

### Add a deadline: `deadline`

Adds a task due on a date. Format: `deadline DESCRIPTION /by yyyy-MM-dd`

Example: `deadline return book /by 2026-09-20`

### Add an event: `event`

Adds a task with a start and end time. Format: `event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm`

Example: `event study group /from 2026-09-21 1400 /to 2026-09-21 1600`

The end must be after the start. Descriptions cannot be empty or contain `|`. Charlie will explain if an entry needs correcting.

### See all tasks: `list`

Shows every task with its task number. Format: `list`

If you have no tasks, Charlie replies, `Your task nest is empty.`

`[T]`, `[D]`, and `[E]` mean todo, deadline, and event. `[ ]` means unfinished; `[X]` means done. Use the number shown here for `mark`, `unmark`, `update`, and `delete`.

### Find tasks by description: `find`

Finds tasks whose descriptions contain a word or phrase, regardless of letter case. Format: `find KEYWORD_OR_PHRASE`

Example: `find book`

Results from `find` and `on` have their own numbering. Run `list` to get the task number before changing a task.

### Find tasks on a date: `on`

Shows deadlines due on a date and events spanning that date. Format: `on yyyy-MM-dd`

Example: `on 2026-09-21`

### Mark a task as done: `mark`

Changes a task's status to done (`[X]`). Format: `mark TASK_NUMBER`

Example: `mark 2` marks the second task shown by `list`.

### Mark a task as unfinished: `unmark`

Changes a task's status back to unfinished (`[ ]`). Format: `unmark TASK_NUMBER`

Example: `unmark 2`

### Update a task: `update`

Changes one detail without changing the task's position or completion status. Format: `update TASK_NUMBER FIELD NEW_VALUE`

| Field | Works for | Example |
| --- | --- | --- |
| `description` | Any task | `update 1 description borrow library book` |
| `deadline` | Deadline | `update 2 deadline 2026-09-22` |
| `from` | Event | `update 3 from 2026-09-21 1500` |
| `to` | Event | `update 3 to 2026-09-21 1700` |

Use the task number from `list`. An updated event must still end after it starts.

### Delete a task: `delete`

Removes a task permanently. Format: `delete TASK_NUMBER`

Example: `delete 2` removes the second task shown by `list`. Run `list` first to check its number.

### Exit Charlie: `bye`

Closes the chat window. Format: `bye`

Your task changes are saved as you make them.
