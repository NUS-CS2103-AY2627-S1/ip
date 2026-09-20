# Goop User Guide

**Goop** is a desktop task manager you control by typing commands in a chat window.
Keep track of to-dos, deadlines, and events; mark work as complete; find tasks;
and assign priorities. Your tasks are saved locally after each successful change.

## Contents

- [Getting started](#getting-started)
- [Command overview](#command-overview)
- [Entering commands](#entering-commands)
- [Adding tasks](#adding-tasks)
- [Viewing and finding tasks](#viewing-and-finding-tasks)
- [Updating tasks](#updating-tasks)
- [Saving and restoring tasks](#saving-and-restoring-tasks)
- [Handling errors](#handling-errors)
- [Exiting Goop](#exiting-goop)

## Getting started

1. Install **Java 25**. Check your version with `java -version` in a terminal.
2. Obtain the application JAR, `duke.jar`, or build it using the
   [project's build instructions](../README.md#creating-and-running-the-fat-jar).
3. Put the JAR in a folder where you can create and update files. Open a terminal
   in that folder and run:

   ```shell
   java -jar duke.jar
   ```

4. In the Goop window, type a command in **Type a command...**, then press
   **Enter** or click **Send**.

Try these commands one at a time, starting with an empty task list:

```text
todo read book
deadline return book /by 2026-09-20 1800
event study group /from 2026-09-18 1400 /to 2026-09-18 1600
priority 2 high
mark 1
list
```

The final response is:

```text
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] [high] return book (by: Sep 20 2026, 6:00 PM)
3.[E][ ] study group (from: 2026-09-18 1400 to: 2026-09-18 1600)
```

Your commands appear on the right and Goop's replies on the left. The chat
scrolls to new messages automatically; scroll upward to review earlier replies.
The input field clears after each submitted command.

> [!NOTE]
> Launch Goop from the same folder each time. Its saved tasks are in
> `data/goop.txt` relative to the terminal's working folder, which is not
> necessarily the folder containing the JAR.

## Command overview

Replace uppercase placeholders with your own values. Do not type the placeholder
names or surround descriptions with quotation marks.

| Action | Command | Example |
| --- | --- | --- |
| [Add a to-do](#to-dos) | `todo DESCRIPTION` | `todo read book` |
| [Add a deadline](#deadlines) | `deadline DESCRIPTION /by DATE TIME` | `deadline return book /by 20/9/2026 1800` |
| [Add an event](#events) | `event DESCRIPTION /from START /to END` | `event study group /from 2pm /to 4pm` |
| [Show all tasks](#listing-tasks) | `list` | `list` |
| [Find tasks](#finding-tasks) | `find KEYWORD` | `find book` |
| [Mark complete](#marking-tasks-complete-or-incomplete) | `mark NUMBER` | `mark 1` |
| [Mark incomplete](#marking-tasks-complete-or-incomplete) | `unmark NUMBER` | `unmark 1` |
| [Set or clear priority](#setting-priorities) | `priority NUMBER LEVEL` | `priority 2 high` |
| [Delete a task](#deleting-tasks) | `delete NUMBER` | `delete 1` |
| [Exit](#exiting-goop) | `bye` | `bye` |

## Entering commands

- Command words and parameter markers are **case-sensitive**: use `todo`, not
  `TODO`, and `/by`, not `/BY`. Priority levels are case-insensitive.
- Leading and trailing whitespace is ignored. Tabs and repeated spaces become
  single spaces, including inside task descriptions and search text.
- Keep each command on one line. Embedded line breaks and control characters
  are rejected. Ordinary punctuation, Unicode, pipes (`|`), and backslashes
  (`\`) are allowed in descriptions; Goop handles saving them for you.
- Separate `/by`, `/from`, and `/to` from surrounding text with spaces. Each
  marker may appear only once in its corresponding command. For events, `/from`
  must come before `/to`.
- `NUMBER` is a positive whole number from the latest full `list`, such as `1`.
  Zero, negative values, decimals, leading zeros, and multiple numbers are invalid.
- `list` and `bye` take no extra arguments.

## Adding tasks

New tasks are incomplete and have no assigned priority. A successful add displays
the task and the total number of tasks in the list.

### To-dos

Use a to-do for work without a date or time:

```text
todo read book
```

For an empty task list, Goop replies:

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

A description is required. `todo` by itself produces an error with the correct syntax.

### Deadlines

Use `deadline DESCRIPTION /by DATE TIME` when a task must be completed by a
specific date and time:

```text
deadline return book /by 20/9/2026 1800
```

| Accepted format | Example | Meaning |
| --- | --- | --- |
| `d/M/yyyy HHmm` | `20/9/2026 1800` | September 20, 2026 at 18:00 |
| `yyyy-MM-dd HHmm` | `2026-09-20 1800` | The same date and time |

`HHmm` uses four digits and a 24-hour clock: `0900` is 9 AM and `1800` is 6 PM.
Both the date and time are required for deadlines. Dates without times,
`tomorrow`, impossible dates such as February 30, and invalid times such as
`2400` are rejected. Past deadlines are allowed.

The task is displayed with a readable date and 12-hour time:

```text
[D][ ] return book (by: Sep 20 2026, 6:00 PM)
```

### Events

Use `event DESCRIPTION /from START /to END` for tasks with a duration:

```text
event study group /from 2026-09-18 1400 /to 2026-09-18 1600
```

The description, start, and end are all required. Event schedules are displayed
using the entered text, with normalized spacing.

| Schedule style | Example command |
| --- | --- |
| Dates with times | `event workshop /from 18/9/2026 0900 /to 18/9/2026 1700` |
| Dates only | `event conference /from 2026-09-18 /to 2026-09-20` |
| 24-hour times only | `event lunch /from 1200 /to 13:00` |
| 12-hour times only | `event study group /from 2:30 pm /to 4pm` |
| Overnight | `event maintenance /from 2026-09-18 2300 /to 2026-09-19 0100` |
| Free-form text | `event meeting /from Mon 2pm /to 4pm` |

For numeric dates, use `d/M/yyyy` or `yyyy-MM-dd`, optionally followed by `HHmm`.
If either endpoint includes a numeric date, **both must include dates**. A date
without a time is treated as midnight when comparing endpoints.

The end must be strictly later than the start when both endpoints can be parsed.
Time-only ranges are compared within the same day: `2300` to `0100` is rejected.
Use full dates and times for overnight events. Impossible numeric dates and
invalid numeric times are rejected.

> [!IMPORTANT]
> Free-form schedules such as `Mon 2pm` remain supported, but Goop cannot reliably
> check their chronological order. Identical start and end text is rejected,
> ignoring letter case. Use numeric dates and times for calendar validation.

### Duplicate tasks

Goop rejects a task when its **type, description, and schedule** match an existing
task. Description comparisons are case-sensitive and ignore repeated whitespace.
Equivalent numeric schedule formats count as the same date or time.

Completion status and priority do not make a task unique. For example, adding
`todo read book` again is rejected even after the existing task is completed:

```text
ERROR: This task already exists. Use list to find it.
```

A different task type or schedule is allowed. You can also delete the existing
task before adding it again.

## Viewing and finding tasks

### Listing tasks

Type `list` to show all tasks, including completed ones, in their current list
order. Goop does not sort tasks by deadline or priority.

For example:

```text
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] [high] return book (by: Sep 20 2026, 6:00 PM)
```

| Marker | Meaning |
| --- | --- |
| `[T]` | To-do |
| `[D]` | Deadline |
| `[E]` | Event |
| `[ ]` | Incomplete |
| `[X]` | Complete |
| `[high]`, `[medium]`, `[low]` | Assigned priority; omitted when unassigned |

An empty list displays only `Here are the tasks in your list:`.

### Finding tasks

Use `find KEYWORD` to search **task descriptions**:

```text
find book
```

The search matches a case-sensitive substring. `book` matches both `read book`
and `return book`; `Book` does not match those lowercase descriptions. A phrase
such as `find read book` is matched as one continuous phrase. Dates, priorities,
and completion markers are not searched.

Matching tasks appear under `Here are the matching tasks in your list:` and keep
their relative order. If there are no matches, only that heading is displayed.
Searching does not change the task list.

> [!WARNING]
> Search results are numbered afresh starting at 1. These numbers may differ
> from the full list. Run `list` before using `mark`, `unmark`, `priority`, or
> `delete`, because those commands always use full-list numbers.

## Updating tasks

### Marking tasks complete or incomplete

Run `list`, then use the number of the task you want to update:

```text
mark 1
```

If task 1 is `read book`, the response is:

```text
Nice! I've marked this task as done:
  [T][X] read book
```

Use `unmark 1` to make it incomplete again. Completion changes preserve the
schedule and priority. Marking an already complete task, or unmarking an already
incomplete task, leaves it in that state and confirms the command.

### Setting priorities

Use `priority NUMBER LEVEL` to assign or change a task's priority:

```text
priority 2 high
```

| Level | Numeric alias | Effect |
| --- | --- | --- |
| `high` | `1` | Set high priority |
| `medium` | `2` | Set medium priority |
| `low` | `3` | Set low priority |
| `none` | None | Clear the priority |

`priority 2 1` has the same effect as `priority 2 high`. Level names accept any
letter case, such as `HIGH`. Use `priority 2 none` to remove the marker.

Priorities appear in `list` and `find`, survive completion changes and restarts,
and do not reorder tasks. New tasks start without a priority; assign one after adding.

### Deleting tasks

Use `delete NUMBER` to remove one task:

```text
delete 1
```

Goop shows the removed task and the remaining count. Later tasks shift up one
position, so run `list` again before choosing another task number.

> [!CAUTION]
> Deletion is saved immediately and has no undo command. Verify the task number
> in the full list first. To change a description or schedule, delete the old
> task and add its replacement; new tasks start incomplete without a priority.

## Saving and restoring tasks

Goop automatically saves successful additions, deletions, completion changes,
and priority changes to `data/goop.txt`. No `save` command is needed. On the next
launch from the same working folder, it restores task details, ordering,
completion states, and priorities. Chat messages are not saved.

- A missing data file means an empty task list. The file and its parent folder
  are created on the first successful task change.
- Keep a backup by closing Goop and copying `data/goop.txt` somewhere safe.
  Restore it by replacing the file while Goop is closed, then restarting.
- Prefer managing tasks through commands. Hand-editing saved records can make
  the file invalid.
- If saving fails, Goop reports an error and rolls back the attempted task
  change. It writes through a temporary file and atomic replacement to protect
  the previous saved file.

## Handling errors

Errors start with **`ERROR:`**. In the GUI, they use bold dark-red text, a pale-red
background, and a strong red accent border. Read the guidance, correct the
command, and submit it again. An invalid command does not end the session.
Blank submissions in the GUI are ignored; the console interface reports a
missing-command error.

| Problem | How to recover |
| --- | --- |
| Unknown command or wrong case | Use a lowercase command from the [overview](#command-overview). |
| Missing or repeated parameters | Include each required parameter once, with spaces around its marker. |
| Extra arguments to `list` or `bye` | Submit just the command word. |
| Invalid or out-of-range task number | Run `list` and choose a displayed positive whole number. |
| Duplicate task | Find the existing task with `list`; change its status or priority as needed. |
| Invalid date, time, or event order | Use the documented formats and an end strictly after the start. |
| File cannot be saved | Check file/folder permissions and available disk space, then retry the command. |
| Saved data cannot be loaded | Follow the recovery steps below and restart Goop. |

### Recovering from a load failure

An unreadable file, invalid UTF-8 text, malformed record, duplicate task, or
invalid stored schedule can prevent loading. Goop displays a warning and starts
with an empty in-memory list; it does not load a partial list.

**Saving is disabled after a load failure**, preserving the original file.

1. Close Goop and make a backup of `data/goop.txt` if you can access it.
2. Check the warning for a path, line number, or invalid field. Restore a known
   good backup, correct the record, or fix read permissions as appropriate.
3. If you want to start fresh, move the original file aside rather than
   overwriting it.
4. Restart Goop from the same working folder, then run `list` to check the result.

### Recovering from a save failure

A save error ending in `No changes were made.` means the attempted task change
was rolled back. Fix the storage problem and resubmit that command.

The data path must be a writable regular file, not a directory or symbolic link.
Its parent folder must allow creating files. Goop reports a save error if the
filesystem does not support atomic replacement; use a local folder that does.

## Exiting Goop

Type `bye` without arguments:

```text
Bye. Hope to see you again soon!
```

The GUI briefly displays this farewell, disables further input, and closes.
Successful task changes have already been saved.
