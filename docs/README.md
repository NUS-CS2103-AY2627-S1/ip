# Rambo User Guide

Rambo is a task management chatbot that helps you keep track of to-dos, deadlines,
and events. Use its chat window to add and find tasks, track completion, and assign
priorities. Your tasks are saved automatically between sessions.

## Contents

- [Quick start](#quick-start)
- [Features](#features)
- [Terminal interface](#terminal-interface)
- [FAQ](#faq)
- [Known limitations](#known-limitations)
- [Command summary](#command-summary)

## Quick start

1. Install **JDK 25** and download or clone this project. See the
   [project README](../README.md#setting-up-in-intellij) for IDE setup instructions.
2. Open a terminal in the project folder. Check that `java -version` reports Java 25.
3. Start the chat window:

   ```shell
   ./gradlew run
   ```

   On Windows, use `gradlew.bat run` instead.
4. Type `todo buy milk` in the input box and press **Enter** or click **Send**.
   Rambo confirms that it has added your task.
5. Enter `list` to see your saved tasks, or `help` for a command reminder.

## Features

### Command format

- Enter one command at a time in the chat window.
- Replace uppercase placeholders such as `TASK_NAME` with your own values.
  For example, use `todo buy milk` for `todo TASK_NAME`.
- Task names can contain spaces. Do not surround names or search phrases with quotation marks.
- Enter arguments in the order shown. Date markers must be lowercase: `/by`, `/from`, and `/to`.
- Command words are case-insensitive: `TODO buy milk` also works.
- Dates must be valid calendar dates in `YYYY-MM-DD` format, such as `2026-09-20`.
- Task numbers start at **1**. Use the numbers shown by `list` or `find`.
- Commands without arguments, such as `list` and `help`, must be entered on their own.

### Viewing help: `help`

Displays the built-in command reminder in the chat window.

**Format:** `help`

### Adding a to-do: `todo`

Adds a task without a date. New tasks are unfinished and have no assigned priority.

**Format:** `todo TASK_NAME`

**Example:** `todo buy milk`

If your list was empty, Rambo responds:

```text
Got it. I've added this task:
  [T][] buy milk
Now you have 1 task(s) in the list.
```

### Adding a deadline: `deadline`

Adds a task with a due date. Supply both a nonempty task name and a date after `/by`.

**Format:** `deadline TASK_NAME /by YYYY-MM-DD`

**Example:** `deadline submit assignment /by 2026-09-20`

Rambo confirms the addition and shows the task as:

```text
[D][] submit assignment (by: Sep 20 2026)
```

### Adding an event: `event`

Adds a task with a start date and an end date. Supply a nonempty name and both dates,
with `/from` before `/to`.

**Format:** `event TASK_NAME /from YYYY-MM-DD /to YYYY-MM-DD`

**Example:** `event study camp /from 2026-09-21 /to 2026-09-23`

Rambo confirms the addition and shows the event as:

```text
[E][] study camp (from: Sep 21 2026 to: Sep 23 2026)
```

### Listing tasks: `list`

Displays all tasks in their current order, including completed tasks.

**Format:** `list`

After adding the three examples above to an empty list, the result is:

```text
1. [T][] buy milk
2. [D][] submit assignment (by: Sep 20 2026)
3. [E][] study camp (from: Sep 21 2026 to: Sep 23 2026)
```

| Tag | Meaning |
| --- | --- |
| `[T]` | To-do |
| `[D]` | Deadline |
| `[E]` | Event |
| `[]` | Not completed |
| `[X]` | Completed |
| `[P1]`, `[P2]`, `[P3]` | Assigned priority, with 1 highest and 3 lowest |

If you have no tasks, Rambo responds with `Your task list is empty.`

### Finding tasks: `find`

Displays tasks whose names contain the supplied text.

**Format:** `find SEARCH_TEXT`

- Search is case-insensitive and matches partial words: `find mil` matches `buy milk`.
- A multiword search matches the whole phrase in that order: `find buy milk` matches
  `buy milk today`, but not `buy fresh milk`.
- Only task names are searched, not dates, completion status, or priority tags.
- Results retain their original task numbers. Use those numbers when updating or deleting tasks.

**Example:** `find assignment` returns the following task from the example list:

```text
2. [D][] submit assignment (by: Sep 20 2026)
```

If nothing matches, Rambo reports `No tasks found matching "assignment".`
If the entire list is empty, it reports `Your task list is empty.`

### Changing completion status: `done`

Toggles a task between completed and unfinished. Completed tasks remain in the list.

**Format:** `done TASK_NUMBER` or `toggle TASK_NUMBER`

**Example:** `done 1` changes the unfinished `buy milk` task to:

```text
Nice! I've updated this task:
  [T][X] buy milk
```

> **Note:** Entering `done 1` again marks the same task as unfinished.

### Assigning a priority: `priority`

Assigns a priority level to an existing task.

**Format:** `priority TASK_NUMBER LEVEL`

- **1** is the highest priority, **2** is medium priority, and **3** is the lowest priority.
- Assigning another level replaces the current priority.
- Priorities do not change the task order.

**Example:** `priority 2 1` assigns the highest priority to task 2:

```text
Got it. I've updated this task's priority:
  [D][][P1] submit assignment (by: Sep 20 2026)
```

### Deleting a task: `delete`

Removes the task with the specified number and displays the removed task as confirmation.

**Format:** `delete TASK_NUMBER`

**Example:** `list` followed by `delete 2` removes the task numbered 2.
You can also use `find assignment` followed by `delete 2` if the matching task is numbered 2.

> **Note:** Deletion is saved immediately and has no undo command. Remaining tasks are
> renumbered, so run `list` again before selecting another task to update or delete.

### Exiting Rambo: `bye`

Closes the chat window.

**Format:** `bye` or `q`

### Saving tasks

Rambo automatically saves after each addition, completion change, priority change,
and deletion. It loads saved tasks when you next start the application; no save
command is needed.

Tasks are stored in `data/Rambo.txt`, relative to the folder from which Rambo runs.
Start Rambo from the same folder each time to load the same list. Both interfaces
use this file when run from the same folder.

## Terminal interface

To use the terminal interface, run `rambo.Rambo.main()` in your IDE as described in
the [project README](../README.md#setting-up-in-intellij). Use the menu options below
instead of the chat window's word commands.

| Input | Action and next steps |
| --- | --- |
| `1` | Start Echo mode. Rambo repeats each line. Enter `/exit` to return to the menu. |
| `2` | Add a task. Choose type `1` (to-do), `2` (deadline), or `3` (event), then answer the prompts. |
| `3` | List all tasks. |
| `3 SEARCH_TEXT` | Search task names, for example `3 milk` or `3 buy milk`. |
| `4` | Toggle completion. Enter the task number when prompted. |
| `5` | Delete a task. Enter the task number when prompted. |
| `6` | Set priority. Enter the task number, then a level from 1 to 3 when prompted. |
| `q` or `bye` | Exit Rambo from the main menu. |

For example, to add a deadline, enter each line below separately as Rambo prompts you:

```text
2
2
submit assignment
2026-09-20
```

For an event, enter its name, start date, and end date at the respective prompts.
Use `YYYY-MM-DD` for all dates. Task numbering, searching, completion, and priorities
behave as described in the feature sections above.

## FAQ

### Why was my command rejected?

Check that you are using the correct commands for your interface. Supply all required
arguments, a nonempty task name, valid dates, and a priority level from 1 to 3.
If a task number is rejected, run `list` (or `3` in the terminal) and use an existing number.

### How do I back up or transfer my tasks?

Close Rambo and copy `data/Rambo.txt` to a safe location. To transfer tasks, close Rambo
on the destination computer and place the saved file in its `data` folder before restarting.
Back up any existing destination file before replacing it.

### Why are my saved tasks missing or failing to load?

Check that you started Rambo from the same folder as before and that `data/Rambo.txt`
is readable. If Rambo reports an invalid task record, restore an undamaged backup.
For a save error, check that the data folder is writable.

## Known limitations

- Dates support calendar days only, not times. Rambo does not check whether an event's
  end date comes before its start date, so check the range when entering it.
- Avoid the pipe character (`|`) in task names. It separates fields in the save file
  and can prevent tasks from loading next time.
- An assigned priority can be changed to another level, but cannot be cleared by a command.
- The built-in help reminder omits `find` and the `toggle` alias; both are supported as documented above.

## Command summary

These commands are for the chat window. For numeric menu options, see
[Terminal interface](#terminal-interface).

| Action | Format | Example |
| --- | --- | --- |
| Help | `help` | `help` |
| Add to-do | `todo TASK_NAME` | `todo buy milk` |
| Add deadline | `deadline TASK_NAME /by YYYY-MM-DD` | `deadline submit assignment /by 2026-09-20` |
| Add event | `event TASK_NAME /from YYYY-MM-DD /to YYYY-MM-DD` | `event study camp /from 2026-09-21 /to 2026-09-23` |
| List tasks | `list` | `list` |
| Find tasks | `find SEARCH_TEXT` | `find buy milk` |
| Toggle completion | `done TASK_NUMBER` or `toggle TASK_NUMBER` | `done 1` |
| Assign priority | `priority TASK_NUMBER LEVEL` | `priority 2 1` |
| Delete task | `delete TASK_NUMBER` | `delete 2` |
| Exit | `bye` or `q` | `bye` |
