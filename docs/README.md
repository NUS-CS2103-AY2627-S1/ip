# Altair User Guide

Altair is a task-manager chatbot. Type a short command, and Altair adds, lists,
finds, updates, or removes tasks for you. Every change is saved automatically to
`./data/altair.txt`, so your list is still there the next time you start it.

Altair can run as a JavaFX GUI or as a text UI in the terminal. Both understand
exactly the same commands, so this guide applies to either.

## Quick start

1. Make sure you have **JDK 25** installed.
2. From the project folder, start Altair:
   - GUI: `./gradlew run`
   - or build a JAR with `./gradlew shadowJar` and run `java -jar build/libs/altair.jar`
3. Type a command and press Enter. Altair replies with a confirmation.
4. Type `bye` to exit.

## Command summary

| Command | Purpose |
| --- | --- |
| `todo <description>` | Add a task with no date. |
| `deadline <description> /by <yyyy-MM-dd>` | Add a task due by a date. |
| `event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>` | Add a task with a start and end date. |
| `list` | Show every task, numbered. |
| `mark <task number>` | Mark a task as done. |
| `unmark <task number>` | Mark a task as not done. |
| `delete <task number>` | Remove a task. |
| `find <keyword>` | Show tasks whose description contains the keyword. |
| `bye` | Exit Altair. |

Notes that apply to every command:

- Dates must be in `yyyy-MM-dd` form (for example `2025-10-15`). Altair shows
  them back as `MMM dd yyyy` (for example `Oct 15 2025`).
- Task descriptions and dates may contain spaces, but not the `|` character
  (it is used in the save file).
- Task numbers are the numbers shown by `list`, starting from 1.
- Adding a task whose description matches one already in the list asks for
  confirmation first (see [Duplicate tasks](#duplicate-tasks) below).

## Adding a todo: `todo`

Adds a task with no date attached.

Format: `todo <description>`

Example: `todo read book`

Expected outcome:

```
Alright, I have added it to your list:
  [T][ ] read book
Now you have 1 tasks in the list.
```

## Adding a deadline: `deadline`

Adds a task that must be done by a given date.

Format: `deadline <description> /by <yyyy-MM-dd>`

Example: `deadline return book /by 2025-10-15`

Expected outcome:

```
Alright, I have added it to your list:
  [D][ ] return book (by: Oct 15 2025)
Now you have 2 tasks in the list.
```

## Adding an event: `event`

Adds a task that spans a start date and an end date.

Format: `event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>`

Example: `event orientation /from 2025-08-01 /to 2025-08-03`

Expected outcome:

```
Alright, I have added it to your list:
  [E][ ] orientation (from: Aug 01 2025 to: Aug 03 2025)
Now you have 3 tasks in the list.
```

## Duplicate tasks

Before a `todo`, `deadline`, or `event` is added, Altair checks whether its
description matches a task already in the list (ignoring case and extra
spacing). If it does, Altair asks for confirmation instead of adding it right
away.

Two tasks of the *same* type (two Deadlines, or two Events) with the same
description but different dates are **not** flagged &mdash; different dates
mean a different commitment. Tasks of *different* types with the same
description are always flagged, since a due date or time range isn't what
makes them the same task or not.

Example: adding `buy milk` a second time.

```
I believe this already appears on the books:
  [T][ ] buy milk
Add it anyway? (y/n)
```

Answer `y` or `yes` to add it anyway, or `n`/`no` to leave the list unchanged:

```
Very well. I shall leave it be.
```

Typing anything else instead of an answer (including a new command) drops the
pending task and is handled as that new command instead.

## Listing all tasks: `list`

Shows every task with its number, type marker (`T`/`D`/`E`) and done marker
(`X` when done).

Format: `list`

Expected outcome:

```
The following are your tasks
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2025)
3.[E][ ] orientation (from: Aug 01 2025 to: Aug 03 2025)
```

## Marking and unmarking tasks: `mark`, `unmark`

`mark` sets a task to done; `unmark` sets it back to not done.

Format: `mark <task number>` / `unmark <task number>`

Example: `mark 1`

Expected outcome:

```
Duly noted. Marked as complete:
  [T][X] read book
```

Example: `unmark 1`

Expected outcome:

```
As you wish. Returned to the unfinished pile:
  [T][ ] read book
```

## Deleting a task: `delete`

Removes the task at the given number. The remaining tasks are renumbered.

Format: `delete <task number>`

Example: `delete 2`

Expected outcome:

```
Consider it done. I have removed the following:
  [D][ ] return book (by: Oct 15 2025)
Now you have 2 tasks in the list.
```

## Finding tasks by keyword: `find`

Shows the tasks whose description contains the keyword. The match ignores case.

Format: `find <keyword>`

Example: `find book`

Expected outcome:

```
Here are the matching tasks in your list:
1.[T][ ] read book
```

If nothing matches:

```
Nothing of that description turns up, I'm afraid.
```

## Exiting: `bye`

Format: `bye`

Expected outcome:

```
Very well. I shall be here when next you require me.
```

## Saving the data

There is nothing to save by hand. Altair writes the whole task list to
`./data/altair.txt` after every add, mark, unmark, delete, and reads it back when
it starts. The file and its folder are created automatically the first time you
add a task.

If `./data/altair.txt` is missing, Altair simply starts with an empty task list.
If the file exists but is corrupted (a row Altair cannot read) or cannot be read
at all (for example, a permissions problem), Altair shows an error message
explaining what went wrong and still starts normally with an empty task list, so
a bad save file never leaves you unable to use the app.
