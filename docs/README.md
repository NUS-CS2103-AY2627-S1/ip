# Arin User Guide

Arin is your task-tracking Wilderness Explorer. Use short commands to keep
to-dos, deadlines, and events together, mark work as done, and find or sort
your tasks.

> **Before you begin:** Task commands currently work in the **console**, not
> the JavaFX window. Saved tasks are **not reloaded when you restart Arin**.
> Back up any existing `data/arin.txt` before starting another session; the
> next successful change or sort replaces that file with the current list.

## Contents

- [Quick start](#quick-start)
- [Command format](#command-format)
- [Features](#features)
- [Saving your tasks](#saving-your-tasks)
- [Troubleshooting](#troubleshooting)
- [Command summary](#command-summary)

## Quick start

1. Install **Java 25** and check that `java -version` reports version 25.
2. Obtain `arin-all.jar`. There is currently no downloadable release; if you
   have not been given the JAR, build it from the
   [Arin repository](https://github.com/Goh-Wei-Chong/ip):

   - Download and extract the source using **Code → Download ZIP**, or clone
     the repository with Git.
   - Open a terminal in the project folder containing `build.gradle`.
   - Run `./gradlew shadowJar` on macOS/Linux, or `gradlew.bat shadowJar` on
     Windows Command Prompt (`.\gradlew.bat shadowJar` in PowerShell).
   - Find the bundled JAR at `build/libs/arin-all.jar`. Building requires
     a JDK 25 installation and internet access to obtain dependencies.

3. Copy `arin-all.jar` into a new, empty folder and open a terminal there.
   Start the console with:

   ```bash
   java -ea -cp arin-all.jar arin.Arin
   ```

   Keep the terminal open. Arin shows its welcome message and waits for a
   command. Double-clicking the JAR or using `java -jar arin-all.jar` opens
   only the current `Hello World!` JavaFX placeholder, not the task console.
4. Type these commands **one at a time**, pressing Enter after each:

   ```text
   todo read book
   deadline return book /by 2026-09-20
   event project meeting /from Sep 18 2pm /to Sep 18 4pm
   list
   ```

   The list contains:

   ```text
   1. [T][ ] read book
   2. [D][ ] return book (by: Sep 20 2026)
   3. [E][ ] project meeting (from: Sep 18 2pm to: Sep 18 4pm)
   ```
5. Try `mark 1` to complete the first task. Use `bye` when finished.

## Command format

- Use lowercase command words and options, such as `todo` and `sort name`.
  Task descriptions may contain uppercase letters and spaces.
- Type commands without leading spaces. Enter `list` and `bye` exactly,
  without trailing spaces. Use ordinary spaces, not tabs, between parts.
- Uppercase words in the formats below, such as `DESCRIPTION` and `NUMBER`,
  are placeholders: replace them with your own values. Do not type them
  literally. Every shown argument is required.
- Keep spaces around `/by`, `/from`, and `/to`; put `/from` before `/to`.
- `NUMBER` is a whole number from 1 to the size of the **full task list**.
  Run `list` before changing a task if you are unsure of its number.

## Features

### Add a to-do: `todo`

Add a task that does not need a date. Format: `todo DESCRIPTION`.

Example: `todo read book`

Arin confirms the addition and shows the new task and list size. The task
appears as `[T][ ] read book`. New tasks are incomplete and go at the end of
the list. Repeating an add command creates another task, even if its details
are identical.

### Add a deadline: `deadline`

Add a task with a due date. Format: `deadline DESCRIPTION /by YYYY-MM-DD`.

Example: `deadline return book /by 2026-09-20`

The new task appears as `[D][ ] return book (by: Sep 20 2026)`.
Use a real calendar date with a four-digit year and two-digit month and day.
Times and words such as `tomorrow` are not supported for deadlines.

### Add an event: `event`

Record an activity with a start and end time.
Format: `event DESCRIPTION /from START /to END`.

Example: `event project meeting /from Sep 18 2pm /to Sep 18 4pm`

The new task appears as
`[E][ ] project meeting (from: Sep 18 2pm to: Sep 18 4pm)`.
Start and end values are free text: Arin displays them as entered and does
not check chronological order, detect clashes, or send reminders.

### View all tasks: `list`

Enter `list` to see every task in its current order, numbered from 1.
`[T]` means to-do, `[D]` deadline, and `[E]` event. `[ ]` means incomplete;
`[X]` means complete. With no tasks, Arin shows the list heading without any
numbered entries.

### Complete or reopen a task: `mark` and `unmark`

Formats: `mark NUMBER` and `unmark NUMBER`.

With `read book` at position 1, `mark 1` produces this confirmation
(divider lines omitted):

```text
Nice! I've marked this task as done:
[T][X] read book
```

`unmark 1` reopens that task and restores `[ ]`. Neither command removes the
task or changes its position. Marking an already completed task, or
unmarking an incomplete task, leaves it in that state.

### Find tasks: `find`

Find descriptions containing a word or phrase. Format: `find KEYWORD`.

Example: `find BOOK` matches both `read book` and `return book`. Matching
ignores case and includes partial words: `find boo` also matches them.
Dates and event times are not searched unless they are part of the
description. No matches produces a heading with no numbered entries.

> **Search numbers are not task numbers.** Search results are numbered from
> 1 within the results only. Run `list` and use the number from that full
> list for `mark`, `unmark`, or `delete`.

### Sort tasks: `sort`

Choose one of two orders:

- `sort name`: alphabetically by description, ignoring case.
- `sort deadline`: earliest deadlines first, followed by to-dos and events
  in their existing order. Free-text event times are not used for sorting.

For the three tasks in the quick start, `sort deadline` displays:

```text
1. [D][ ] return book (by: Sep 20 2026)
2. [T][ ] read book
3. [E][ ] project meeting (from: Sep 18 2pm to: Sep 18 4pm)
```

This example assumes the tasks are still incomplete. Sorting preserves
their details and completion states, keeps ties in their current relative
order, and saves the new order. **Task numbers change**: in this example,
`mark 1` now completes `return book`. Newly added tasks still go at the end;
run a sort command again when needed. Sorting an empty list is allowed.

### Delete a task: `delete`

Permanently remove a task from the current list. Format: `delete NUMBER`.

Example: `delete 1` removes the first task in `list`. Arin confirms which
task was removed and shows the remaining count. Later tasks move up one
position. **There is no undo**, so check the full list before deleting.

### Exit: `bye`

Enter `bye` to close the console session. Arin replies:

```text
Bye. Hope to see you again soon!
```

Changes are saved when made, not by `bye`. See the restart limitation below
before closing a session you want to keep.

## Saving your tasks

After adding, marking, unmarking, deleting, or sorting, Arin writes the
current list to `data/arin.txt` under the folder from which you launched it.
The `data` folder and file are created when first needed. Viewing or finding
tasks does not write the file. No `save` command is needed.

**This version saves a record but does not load it back.** Every launch
starts with an empty list, even if `data/arin.txt` exists. A successful
change or sort then replaces the previously saved contents. Copy the file
somewhere safe before another session, or use a different working folder
for each session. Editing or copying a file back does not import its tasks;
you must re-enter them as commands.

If Arin reports a save error, do not assume the file contains your latest
changes. Check `list` and keep a separate record before closing the app.
An add, mark, unmark, or delete may already have changed the in-memory list;
a failed sort leaves the in-memory order unchanged. Check that the working
folder is writable and that `data/arin.txt` is a file, not a directory.

## Troubleshooting

| Problem | What to do |
| --- | --- |
| Only a `Hello World!` window appears | Close it and use `java -ea -cp arin-all.jar arin.Arin` in a terminal. |
| Java reports an incompatible class version | Run `java -version` in the same terminal and switch to Java 25. |
| Java cannot find `arin.Arin` | Check that you are in the folder containing the bundled `arin-all.jar`, and use the command above exactly. |
| Arin does not recognize a command | Check lowercase spelling and remove leading spaces. Use the command summary below. |
| A deadline date is rejected | Use a real date such as `2026-09-20`, not `20/09/2026`, `tomorrow`, or `2026-02-30`. |
| An event command is rejected | Supply a description, `/from START`, and `/to END` in that order, with spaces around the separators. |
| A task number is rejected | Run `list`, then supply an existing whole-number position, such as `mark 1`. |
| `sort` shows a usage message | Supply exactly one supported option: `sort name` or `sort deadline`. |
| Tasks are missing after a restart | Automatic loading is not implemented. Keep backups and re-enter tasks; see [Saving your tasks](#saving-your-tasks). |

For an unexpected problem, [report an issue](https://github.com/Goh-Wei-Chong/ip/issues)
with the command, error message, Java version, and operating system. Avoid
including private task descriptions or your full data file.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add a to-do | `todo DESCRIPTION` | `todo read book` |
| Add a deadline | `deadline DESCRIPTION /by YYYY-MM-DD` | `deadline return book /by 2026-09-20` |
| Add an event | `event DESCRIPTION /from START /to END` | `event meeting /from 2pm /to 4pm` |
| List all tasks | `list` | `list` |
| Complete a task | `mark NUMBER` | `mark 1` |
| Reopen a task | `unmark NUMBER` | `unmark 1` |
| Find descriptions | `find KEYWORD` | `find book` |
| Sort by description | `sort name` | `sort name` |
| Sort by due date | `sort deadline` | `sort deadline` |
| Remove a task | `delete NUMBER` | `delete 1` |
| Exit | `bye` | `bye` |
