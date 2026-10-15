# Tuesday User Guide

Tuesday is a desktop task-management chatbot. Type commands to keep track of todos, deadlines, and events,
search your tasks, and mark work as complete. Your tasks are saved automatically between sessions.

## Contents

- [Quick start](#quick-start)
- [Command conventions](#command-conventions)
- [Features and command examples](#features-and-command-examples)
- [Saving your tasks](#saving-your-tasks)
- [Troubleshooting](#troubleshooting)
- [Command reference](#command-reference)

## Quick start

1. Install **JDK 25** and use a computer with a graphical desktop.
2. Save the provided `tuesday.jar` in a folder of your choice that you have permission to write to.
3. Open a terminal in that folder and check that `java -version` reports version 25.
4. Start Tuesday with:

   ```text
   java -jar tuesday.jar
   ```

   This command works in Windows PowerShell and macOS/Linux terminals. If your JAR has a different filename,
   replace `tuesday.jar` with that filename; enclose it in double quotes if it contains spaces.
5. In the Tuesday window, type a command in the bottom text field and press **Enter** or click **Send**.
6. Try these commands **one at a time**:

   ```text
   todo read book
   deadline submit report /by 2026-09-20 18:00
   event team meeting /from 2026-09-18 14:00 /to 2026-09-18 15:00
   list
   mark 1
   find report
   ```

   With an initially empty task list, these commands create three tasks, show them, mark `read book` as done,
   and find `submit report`. If you already have tasks, use the numbers shown by `list`.
7. Enter `bye` to close Tuesday. Your saved tasks load the next time you start it from the same folder.

## Command conventions

- Commands and markers are **case-sensitive**: use `todo`, `/by`, `/from`, and `/to` in lowercase.
- Uppercase words such as `DESCRIPTION` and `NUMBER` below are placeholders: replace them with your own values.
- Enter one command at a time, without the Markdown backticks or surrounding quotation marks.
- Descriptions can contain spaces and must not be empty.
- Task numbers start at **1**. Use the current number shown by `list` or `find` for `mark`, `unmark`, and `delete`.
- New tasks start as **not done**.

## Features and command examples

### Add a todo: `todo`

Create a task without a scheduled date or time.

**Format:** `todo DESCRIPTION`

**Example:**

```text
todo read book
```

Tuesday adds `[T][ ] read book` and reports the new task count.

### Add a deadline: `deadline`

Create a task with a due date, time, or both. Include exactly one `/by` marker.

**Format:** `deadline DESCRIPTION /by WHEN`

**Date and time restrictions for `WHEN`:**

| Value | Input format | Example |
| --- | --- | --- |
| Date only | `YYYY-MM-DD` | `2026-09-20` |
| Time only | `HH:mm` (24-hour clock) | `18:00` |
| Date and time | `YYYY-MM-DD HH:mm` | `2026-09-20 18:00` |

Use leading zeros and exactly one space between a date and time. Dates must exist and times must be between
`00:00` and `23:59`. Words such as `tomorrow` and times such as `6pm` are not accepted.
A time-only value does not acquire a date automatically.
Tuesday displays dates as `Sep 20 2026`, but you must enter them as `2026-09-20`.

**Examples:**

```text
deadline submit report /by 2026-09-20 18:00
deadline return library books /by 2026-09-22
deadline call dentist /by 09:00
```

The first example adds `[D][ ] submit report (by: Sep 20 2026 18:00)`.

### Add an event: `event`

Create a task with a start and end. Include `/from` followed by `/to`, each exactly once.

**Format:** `event DESCRIPTION /from START /to END`

**Date and time restrictions for both `START` and `END`:**

| Value | Input format | Example |
| --- | --- | --- |
| Date only | `YYYY-MM-DD` | `2026-09-20` |
| Time only | `HH:mm` (24-hour clock) | `18:00` |
| Date and time | `YYYY-MM-DD HH:mm` | `2026-09-20 18:00` |

Use leading zeros and exactly one space between a date and time. Dates must exist and times must be between
`00:00` and `23:59`. Words such as `tomorrow` and times such as `6pm` are not accepted.
A time-only value does not acquire a date automatically.
Tuesday displays dates as `Sep 20 2026`, but you must enter them as `2026-09-20`.
Check that the end is after the start; Tuesday currently validates each date/time but does not check their order.

**Examples:**

```text
event team meeting /from 2026-09-18 14:00 /to 2026-09-18 15:00
event holiday /from 2026-12-21 /to 2026-12-28
event lunch /from 12:00 /to 13:00
```

The first example adds:

```text
[E][ ] team meeting (from: Sep 18 2026 14:00 to: Sep 18 2026 15:00)
```

### View all tasks: `list`

**Format and example:** `list`

Tuesday shows all tasks, including completed tasks, with their current numbers. For example, after adding the
three quick-start tasks to an empty list:

```text
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] submit report (by: Sep 20 2026 18:00)
3.[E][ ] team meeting (from: Sep 18 2026 14:00 to: Sep 18 2026 15:00)
```

`[T]` means todo, `[D]` means deadline, and `[E]` means event. `[ ]` means not done; `[X]` means done.
An empty list displays the heading without any task rows.

### Find tasks: `find`

Search for text anywhere in a task description. Matching ignores letter case and includes completed tasks.
Dates and times are not searched. Multiple words are treated as one phrase.

**Format:** `find KEYWORD`

**Examples:**

```text
find report
find TEAM MEETING
```

With the quick-start tasks, `find report` produces:

```text
Here are the matching tasks in your list:
2.[D][ ] submit report (by: Sep 20 2026 18:00)
```

Results keep their original list numbers, so `mark 2` selects this report. If nothing matches, only the heading
is shown. A keyword is required.

### Mark a task as done: `mark`

**Format:** `mark NUMBER`

**Example:** `mark 1`

For the quick-start todo, Tuesday responds:

```text
Nice! I've marked this task as done:
  [T][X] read book
```

The task remains in your list.

### Mark a task as not done: `unmark`

**Format:** `unmark NUMBER`

**Example:** `unmark 1`

This changes the selected task back to not done:

```text
OK, I've marked this task as not done yet:
  [T][ ] read book
```

### Delete a task: `delete`

**Format:** `delete NUMBER`

**Example:** `delete 1`

Tuesday removes the selected task and reports how many tasks remain. Deletion happens immediately, with no
confirmation prompt or undo command. Remaining tasks are renumbered, so use `list` before deleting another task.

### Resolve duplicate tasks: `new` and `old`

When you add a task whose description matches an existing task, Tuesday asks which one to keep. Matching ignores
letter case, leading/trailing whitespace, and repeated internal whitespace. Task type, dates, times, and completion
status do not affect duplicate detection.

For example, enter these commands one at a time, assuming `buy milk` is not already in your list:

```text
todo buy milk
deadline buy milk /by 2026-09-20
```

The second command prompts you to reply with one of these commands:

| Command and example | Outcome |
| --- | --- |
| `new` | Replace the existing todo with the proposed deadline in the same list position. |
| `old` | Keep the existing todo and discard the proposed deadline. |

The task count stays the same. Choosing `new` resets the task to **not done**, even if the old task was complete.
If several matching tasks exist in saved data, Tuesday replaces only the first match.

Reply with exactly `new` or `old` before continuing with other commands. These commands only work while a duplicate
prompt is pending. You can also enter `bye` to discard the proposed task and exit.

### Exit Tuesday: `bye`

**Format and example:** `bye`

Tuesday displays `Bye. Hope to see you again soon!` and closes after about two seconds.

## Saving your tasks

Successful additions, deletions, completion changes, and duplicate replacements are saved automatically to
`data/Tuesday.txt`, relative to the folder from which you launch Tuesday. No save command is needed.
The file is created when you first save a task, and saved tasks load at startup.

Launch Tuesday from the same folder each time to use the same task file. To back up your tasks, close Tuesday
and copy `data/Tuesday.txt` somewhere safe.

## Troubleshooting

Tuesday explains rejected commands so you can correct and submit them again. Rejected commands do not change
your tasks or saved data.

- **Unknown command:** `task abc` reports that `task` is not recognized and shows a clickable
  [user-guide address](https://soy046.github.io/ip/). Click it to open the guide in your browser.
  Tuesday does not suggest alternative command names.
- **Missing time information:** `event meeting /from 14:00` asks for `/to` with a date or time value,
  then shows the event syntax, accepted date/time formats, and an example.
- **Invalid date/time:** `deadline work /by tomorrow` explains that the `/by` value is invalid and shows
  `YYYY-MM-DD`, `HH:mm` (24-hour), and `YYYY-MM-DD HH:mm`, with a complete example. Impossible dates and
  times such as `2026-02-30` and `25:00` receive the same guidance.
- **Malformed syntax:** repeated `/by` markers or repeated/reversed event markers receive marker guidance.
  `mark abc` shows `mark NUMBER`; use one whole task number with no extra arguments.
- **Duplicate choices:** entering `new` or `old` outside a duplicate prompt explains when these choices apply.
  While a duplicate prompt is pending, resolve it before entering another command.

If a task command has several problems, Tuesday reports the missing description first, then marker problems,
empty date/time values, and invalid date/time values. For events, it checks invalid start values before end values.
Correct the reported problem and submit the command again.

| Problem | What to do |
| --- | --- |
| `java` is not recognized or is not found | Install JDK 25, add its `bin` folder to your `PATH`, and reopen the terminal. |
| Java reports an unsupported class version | Check `java -version` reports 25. If it shows another version, update your `PATH` to use JDK 25. |
| `Unable to access jarfile` | Open the terminal in the folder containing the provided JAR and use its exact filename in `java -jar tuesday.jar`. Quote filenames containing spaces. |
| Tuesday does not start when double-clicked | Run `java -jar tuesday.jar` from a terminal to see any error message. Use a computer with a graphical desktop. |
| A command is not recognized | Check lowercase spelling, required markers, and the formats above. |
| A description or date/time is missing | Supply a nonempty description and the required `/by`, `/from`, or `/to` values. |
| A task number is rejected | Run `list` and use an existing positive whole number, with no extra arguments. |
| Tuesday keeps asking for `new` or `old` | Resolve the pending duplicate before entering another command. |
| Adding a task returns `Sir, this will cost too much time` | The list has reached 100 tasks. Delete a task first; duplicate replacement prompts are also blocked at this limit. |
| Changes cannot be saved | The attempted change is not applied. Check that the launch folder and `data/Tuesday.txt` are writable, then retry. |
| Tasks seem to be missing after restarting | Check that you launched from the same folder and that its `data/Tuesday.txt` is present. |

## Command reference

Replace uppercase placeholders with your own values. `NUMBER` is a task number shown by `list` or `find`.

- `todo DESCRIPTION` — Add a todo.
- `deadline DESCRIPTION /by WHEN` — Add a deadline.
- `event DESCRIPTION /from START /to END` — Add an event.
- `list` — View all tasks.
- `find KEYWORD` — Find tasks by description.
- `mark NUMBER` — Mark a task as done.
- `unmark NUMBER` — Mark a task as not done.
- `new` — Keep the proposed task when prompted about a duplicate.
- `old` — Keep the existing task when prompted about a duplicate.
- `delete NUMBER` — Delete a task.
- `bye` — Exit Tuesday.
