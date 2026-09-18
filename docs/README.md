# Sozius User Guide

Sozius is a task assistant that lets you manage todos, deadlines, and events by typing commands into a chat window. You can view your tasks, mark them as done, search their descriptions, and delete tasks you no longer need.

## Using Sozius

Type a command into the input field and press **Enter** or click **Send**. The **Send** button is disabled while the input field is empty.

Use lowercase command words. In the command formats below, text inside angle brackets, such as `<description>`, represents a value you must supply. Do not type the angle brackets.

Every command has a short alias, listed in the summary table below. Aliases work exactly like the full command words.

If a command fails, Sozius shows the error message and keeps your input selected, so you can correct it and resend without retyping.

## Command summary

| Action | Command format | Alias |
| --- | --- | --- |
| Add a todo | `todo <description>` | `td` |
| Add a deadline | `deadline <description> /by <date>` | `dl` |
| Add an event | `event <description> /from <start> /to <end>` | `e` |
| List tasks | `list` | `ls` |
| Mark a task as done | `mark <index>` | `m` |
| Mark a task as not done | `unmark <index>` | `um` |
| Delete a task | `delete <index>` | `del` |
| Search descriptions | `find <keyword>` | `f` |
| Show the command list | `help` | `h` |
| Save and exit | `bye` | — |

## Dates and times

Deadlines and events take dates in the format `yyyy-MM-dd`, optionally followed by a time in 24-hour `HHmm` format, separated by a space.

- `2026-09-19` — date only
- `2026-09-19 2359` — date with time

The date must be a real calendar date, and times must be within `0000`–`2359` (hours `00`–`23`, minutes `00`–`59`). Other date formats, such as `19/09/2026` or `Sep 19 2026`, are rejected.

In the task list, dates are displayed in a friendlier form, for example `Sep 19 2026 2359`.

## Task display

Tasks are shown with a type tag and a checkbox:

- `[T]`, `[D]`, or `[E]` for todo, deadline, or event
- `[X]` if the task is done, `[ ]` if it is not

Deadlines also show `(by: <date>)` and events show `(from: <start> to: <end>)`. For example:

```text
[D][ ] submit report (by: Sep 19 2026 2359)
```

## Adding a todo

A todo is a task without a date or time.

**Format:** `todo <description>`

**Example:**

```text
todo read a book
```

Sozius adds the task, displays its details, and reports the total number of tasks in your list:

```text
Got it. I've added this task:
[T][ ] read a book
Now you have 1 tasks in the list
```

## Adding a deadline

A deadline is a task with a due date.

**Format:** `deadline <description> /by <date>`

Supply both a description and a date, separated by `/by`. The date follows the format described in [Dates and times](#dates-and-times).

**Example:**

```text
deadline submit report /by 2026-09-19 2359
```

Sozius adds the deadline and displays the updated task count.

## Adding an event

An event is a task with a start and an end.

**Format:** `event <description> /from <start> /to <end>`

Supply the description, followed by `/from` and the start, then `/to` and the end. Both dates follow the format described in [Dates and times](#dates-and-times).

**Example:**

```text
event team meeting /from 2026-09-20 1000 /to 2026-09-20 1200
```

Sozius adds the event and displays the updated task count.

## Listing tasks

**Command:** `list`

Displays all tasks in a numbered list, for example:

```text
1. [T][ ] read a book
2. [D][X] submit report (by: Sep 19 2026 2359)
```

If the list is empty, Sozius replies:

```text
No tasks found
```

Use the numbers from this list when marking, unmarking, or deleting tasks. Numbering starts at **1**. After deleting a task, run `list` again to check the updated numbers.

## Marking a task as done

**Format:** `mark <index>`

**Example:**

```text
mark 1
```

Marks the first task as done and displays the updated task. If the task is already marked as done, Sozius tells you instead of changing anything.

## Marking a task as not done

**Format:** `unmark <index>`

**Example:**

```text
unmark 1
```

Marks the first task as not done and displays the updated task. If the task is not marked as done yet, Sozius tells you instead of changing anything.

## Deleting a task

**Format:** `delete <index>`

**Example:**

```text
delete 2
```

Removes the second task and displays the task that was deleted.

## Finding tasks

**Format:** `find <keyword>`

**Example:**

```text
find book
```

Searches task descriptions for text containing `book` and displays the matches and how many were found. The search is case-insensitive, so it also matches descriptions containing `Book` or `BOOK`.

The matching tasks are shown without their list numbers. To mark or delete a matching task, use `list` to find its task number.

## Showing the command list

**Command:** `help`

Displays the list of available commands, their formats, and their aliases, without leaving the application.

## Saving and exiting

**Command:** `bye`

Sozius saves your tasks and then closes the window. Closing the window using its close button also saves your tasks.

Tasks are stored in `tasks.txt` in the application's working directory and loaded when Sozius starts. Use the same working directory between launches to load the same task file. Keep a backup of this file if you want to preserve a separate copy of your tasks.

If some lines in `tasks.txt` are corrupted, Sozius skips them at startup, shows a warning with the number of skipped lines, and loads the remaining tasks. Saving afterward rewrites the file without the corrupted lines.

## Correcting command errors

If a command is rejected, check the following:

- Use a command from the summary table or its alias, with the command word in lowercase.
- Include a description when adding a task.
- Include `/by` and a date for deadlines.
- Include `/from` and `/to`, with both values, for events.
- Use dates in `yyyy-MM-dd` format, with an optional time in `HHmm` format.
- Use an existing task number for `mark`, `unmark`, and `delete`.
- Use a whole-number index starting at 1, not 0 or a negative number.

For example, an unknown command produces:

```text
Unknown command. Try "help" for help.
```

An out-of-range task number produces a message showing the valid range:

```text
Invalid task number. You have 3 tasks (1 to 3)
```
