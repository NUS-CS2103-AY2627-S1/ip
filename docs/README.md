# Bot User Guide

**Bot** is a desktop task manager for people who prefer typing short commands.
It keeps track of todos, deadlines, and events, and saves every change
automatically so that your tasks are available the next time you start it.

![Bot user interface](Ui.png)

## Contents

- [Quick start](#quick-start)
- [Understanding the task list](#understanding-the-task-list)
- [Features](#features)
- [Saving your tasks](#saving-your-tasks)
- [Command summary](#command-summary)

## Quick start

1. Ensure that **Java 25** is installed on your computer.
2. Download `Bot.jar` from the
   [latest release](https://github.com/chzzzzzzzzzz/ip/releases).
3. Place `Bot.jar` in an empty folder that you want Bot to use for its data.
4. Open a terminal in that folder.
5. Run Bot:

   ```shell
   java -jar "Bot.jar"
   ```

6. Type a command in the text box and press <kbd>Enter</kbd>, or select
   **Send**.

> Command words such as `todo` and `list` are case-insensitive. Command markers
> such as `/by`, `/from`, and `/to` must be lowercase. Bot ignores extra spaces
> at the start, end, or between words.

## Understanding the task list

Each task starts with two symbols:

| Symbol | Meaning |
| --- | --- |
| `[T]` | Todo |
| `[D]` | Deadline |
| `[E]` | Event |
| `[ ]` | Not done |
| `[X]` | Done |

For example, `[D][X] return book (by: Dec 02 2019)` is a completed deadline.
Task numbers shown by `list`, `on`, and `sort` can be used with `mark`,
`unmark`, and `delete`.

### Command notation

Replace words in `UPPER_CASE` below with your own values; do not type those
placeholder words literally.

- Dates use `yyyy-MM-dd`, for example `2019-12-02`.
- Event times use the 24-hour `HHmm` format, for example `0900` or `1730`.
- Task numbers start from `1`.

## Features

### Adding a todo: `todo`

Adds a task without a date or time.

Format: `todo DESCRIPTION`

Example:

```text
todo borrow book
```

Expected response:

```text
Got it. I've added this task:
    [T][ ] borrow book
Now you have 1 tasks in the list.
```

### Adding a deadline: `deadline`

Adds a task that must be completed by a particular date.

Format: `deadline DESCRIPTION /by DATE`

Example:

```text
deadline return book /by 2019-12-02
```

Expected response:

```text
Got it. I've added this task:
    [D][ ] return book (by: Dec 02 2019)
Now you have 1 tasks in the list.
```

### Adding an event: `event`

Adds a task with a start and end date and time. The end must be later than the
start.

Format: `event DESCRIPTION /from START /to END`

Example:

```text
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
```

Expected response:

```text
Got it. I've added this task:
    [E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)
Now you have 1 tasks in the list.
```

Events can span several days. For example:

```text
event orientation /from 2019-12-02 0900 /to 2019-12-05 1700
```

### Viewing all tasks: `list`

Displays every task in its current order.

Format: `list`

Example output:

```text
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][X] return book (by: Dec 02 2019)
```

### Marking a task as done: `mark`

Marks the task at the given task number as completed.

Format: `mark TASK_NUMBER`

Example: `mark 2`

```text
Nice! I've marked this task as done:
    [D][X] return book (by: Dec 02 2019)
```

### Marking a task as not done: `unmark`

Changes a completed task back to not done.

Format: `unmark TASK_NUMBER`

Example: `unmark 2`

```text
OK, I've marked this task as not done yet:
    [D][ ] return book (by: Dec 02 2019)
```

### Deleting a task: `delete`

Removes the task at the given task number. Tasks after it are renumbered.

Format: `delete TASK_NUMBER`

Example: `delete 2`

```text
Noted. I've removed this task:
    [D][ ] return book (by: Dec 02 2019)
Now you have 1 tasks in the list.
```

### Finding tasks by description: `find`

Displays tasks whose descriptions contain the keyword. Matching is
case-insensitive.

Format: `find KEYWORD`

Example: `find book`

```text
Here are the matching tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 02 2019)
```

The numbers in these results count only the matching tasks. Run `list` before
using `mark`, `unmark`, or `delete` to confirm a task's number in the full list.

### Finding tasks on a date: `on`

Displays deadlines due on the given date and events that occur on that date.
For a multi-day event, both its start and end dates are included.

Format: `on DATE`

Example: `on 2019-12-02`

```text
Here are the deadlines and events on Dec 02 2019:
2.[D][ ] return book (by: Dec 02 2019)
3.[E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)
```

The numbers are the tasks' positions in the full list, so you can use them
directly with commands such as `mark 2` or `delete 3`.

### Sorting tasks chronologically: `sort`

Sorts deadlines by their due dates and events by their start dates and times.
Todos, which have no date, are placed after dated tasks. The new order is saved.

Format: `sort`

Example output:

```text
Here are your tasks sorted chronologically:
1.[D][ ] return book (by: Dec 02 2019)
2.[E][ ] project meeting (from: Dec 03 2019, 2:00PM to: Dec 03 2019, 4:00PM)
3.[T][ ] borrow book
```

### Exiting Bot: `bye`

Displays a farewell message and closes Bot.

Format: `bye`

```text
Bye. Hope to see you again soon!
```

## Saving your tasks

Bot automatically saves changes made by `todo`, `deadline`, `event`, `mark`,
`unmark`, `delete`, and `sort`. When Bot starts, it loads the saved tasks.

The data is stored in `data/bot.txt`, relative to the folder from which you
run `Bot.jar`. You do not need to create this file or folder yourself.

> Avoid editing `data/bot.txt` manually. If its contents do not follow Bot's
> storage format, Bot reports the affected line and starts with an empty list.

## Input errors

Bot highlights invalid commands and explains how to correct them. Common
causes include:

- leaving out a task description, task number, date, or time;
- using a date or time in the wrong format;
- entering a task number that is outside the list;
- ending an event before or at its start time;
- adding a duplicate task; or
- using `|` in a task description, as Bot reserves it for saved data.

An invalid command does not close Bot, so you can correct it and try again.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add a todo | `todo DESCRIPTION` | `todo borrow book` |
| Add a deadline | `deadline DESCRIPTION /by DATE` | `deadline return book /by 2019-12-02` |
| Add an event | `event DESCRIPTION /from START /to END` | `event meeting /from 2019-12-02 1400 /to 2019-12-02 1600` |
| View all tasks | `list` | `list` |
| Mark a task | `mark TASK_NUMBER` | `mark 2` |
| Unmark a task | `unmark TASK_NUMBER` | `unmark 2` |
| Delete a task | `delete TASK_NUMBER` | `delete 2` |
| Find by description | `find KEYWORD` | `find book` |
| Find by date | `on DATE` | `on 2019-12-02` |
| Sort tasks | `sort` | `sort` |
| Exit Bot | `bye` | `bye` |
