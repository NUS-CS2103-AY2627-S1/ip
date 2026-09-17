# Yachiyo User Guide

Yachiyo is a friendly desktop chatbot for managing to-dos, deadlines, and events. Enter a command
in the message field, then press **Enter** or select **Send** to receive Yachiyo's response.

![Yachiyo displaying a realistic task lineup](images/Ui.png)

## Contents

- [Quick start](#quick-start)
- [Guided feature tour](#guided-feature-tour)
- [Understanding Yachiyo's visual feedback](#understanding-yachiyos-visual-feedback)
- [Features](#features)
- [Data storage and recovery](#data-storage-and-recovery)
- [Command summary](#command-summary)

## Quick start

1. Ensure that Java 25 is installed on your computer.
2. Open a terminal in the project folder.
3. Run `./gradlew run` on macOS or Linux, or `gradlew.bat run` on Windows.
4. Wait for the Yachiyo window and its greeting to appear.
5. Enter `todo Read book`, then press **Enter**.
6. Enter `list` to see the task and its number.
7. Enter `mark 1` to mark it as complete.
8. Enter `delete 1` to return to an empty lineup for the guided tour.
9. Enter `bye` when you are finished. The window closes after Yachiyo's farewell.

For other ways to build and run Yachiyo, refer to the
[project README](../README.md#running-with-gradle). Continue with the guided tour below to explore
all of Yachiyo's main commands and visual features.

## Guided feature tour

This tour assumes that Yachiyo starts with an empty lineup, so the example task numbers are
predictable. If you already have important tasks, either substitute the numbers shown by your own
`list` command or run `yachiyo.jar` from a separate empty folder. Yachiyo creates a separate `data`
folder beside that copy of the application.

### 1. Meet Yachiyo

Start Yachiyo and inspect the window:

- The greeting begins with `Ohayou`, `Konnichiwa`, or `Konbanwa`, depending on the time of day.
- The pinned header reads `0 tasks · 0 remaining` for an empty lineup.
- Yachiyo's messages and avatar appear on the left; your messages and avatar appear on the right.
- Both profile pictures are cropped into circles.

### 2. Add every task type

Enter the following commands one at a time:

```text
todo Read book
deadline Submit report /by 20/9/2026 1700
event Project meeting /from 20/9/2026 1400 /to 20/9/2026 1600
```

Use **Enter** for at least one command and select **Send** for another to try both submission methods.

After each command, Yachiyo confirms the task and updates the pinned summary. It progresses through
`1 task · 1 remaining`, `2 tasks · 2 remaining`, and `3 tasks · 3 remaining`. The task labels `[T]`,
`[D]`, and `[E]` identify the to-do, deadline, and event.

### 3. List and search for tasks

Enter:

```text
list
```

Yachiyo displays all three tasks with their task numbers. Next, try a matching and a non-matching
description search:

```text
find report
find holiday
```

Then search by date:

```text
on 20/9/2026
on 21/9/2026
```

The first date search returns both the deadline and event. The second shows the friendly no-results
response. Search results retain their task numbers from the complete lineup.

### 4. Edit task details

Each `edit` command changes one detail while preserving the task type, completion status, and all
other details:

```text
edit 1 /description Read Kaguya
edit 2 /by 21/9/2026 1800
edit 3 /from 20/9/2026 1300
edit 3 /to 20/9/2026 1700
list
```

The final `list` output shows every change. Editing does not change the total or remaining task
counts.

### 5. See the error presentations

Enter an unknown command:

```text
dance
```

Yachiyo displays a gentle `Check command` warning. Then enter a valid command with a task number that
does not exist:

```text
mark 99
```

Yachiyo displays the stronger `Cannot complete command` style. Neither error changes your tasks.

![Yachiyo distinguishing an input warning from an invalid operation](images/ErrorPresentation.png)

### 6. Track progress and complete the lineup

First, mark and then unmark a task to see the remaining count change in both directions:

```text
mark 1
unmark 1
```

The summary decreases to `3 tasks · 2 remaining`, then returns to `3 tasks · 3 remaining`.

Now complete every task:

```text
mark 1
mark 2
mark 3
```

After the final command, the summary reads `3 tasks · 0 remaining` and briefly glows and pulses.
Yachiyo ends the response with:

```text
Yayyy! Everything in our lineup is complete!🥳🎉
Good job!
```

The screenshot below shows the same celebration with a five-task lineup.

![Yachiyo celebrating a completed task lineup](images/Completion.png)

Enter `mark 3` again to see Yachiyo's friendly already-complete response. The animation does not
repeat because the lineup did not transition from incomplete to complete.

### 7. Try scrolling and automatic navigation

The conversation should now be taller than the visible chat area.

1. Without dragging the vertical scroll bar, use the mouse wheel or trackpad to scroll upward.
2. Enter `list` again.
3. Observe that Yachiyo automatically moves the conversation to the newest response.
4. Submit an empty or whitespace-only message and observe that no dialog is added.

### 8. Exit and confirm persistence

Enter:

```text
bye
```

Yachiyo displays a farewell, immediately disables the message field and Send button, and closes the
window after a short delay.

Start Yachiyo again. The summary should read `3 tasks · 0 remaining`, and `list` should show the same
three completed tasks. This confirms that task changes are saved automatically.

### 9. Return the disposable lineup to an empty state

If you performed the tour using a disposable lineup, delete its tasks. Task numbers change after a
deletion, so repeatedly delete the new first task:

```text
delete 1
delete 1
delete 1
```

The final deletion produces Yachiyo's empty-lineup response and returns the summary to
`0 tasks · 0 remaining`.

### 10. Optionally explore storage recovery

Only perform this exercise on a disposable copy of Yachiyo or after backing up `data/yachiyo.txt`.

1. Close Yachiyo.
2. Replace the contents of `data/yachiyo.txt` with `malformed data`.
3. Start Yachiyo again.
4. Observe the prominent red `Storage error` and the `Tasks unavailable` summary.
5. Enter `list` and confirm that only the storage error appears; no list response follows it.
6. Enter `bye` and confirm that Yachiyo can still display its farewell and close normally.
7. Restore the backup or remove the disposable malformed file before using Yachiyo again.

## Understanding Yachiyo's visual feedback

| Visual element | Meaning |
| --- | --- |
| `3 tasks · 2 remaining` | Three tasks exist and two have not been completed. |
| `[T]`, `[D]`, and `[E]` | The task is a to-do, deadline, or event. |
| `[ ]` and `[X]` | The task is incomplete or complete. |
| `Check command` | The input needs a correction before Yachiyo can understand it. |
| `Cannot complete command` | The command is understood but cannot be applied in the current context. |
| `Storage error` | Yachiyo cannot safely load or save its task data. |
| Glowing and pulsing summary | The lineup has just changed from having unfinished tasks to being complete. |

Yachiyo selects its opening greeting using the computer's local time:

| Local time | Greeting |
| --- | --- |
| Before 12:00 PM | `Ohayou` |
| 12:00 PM to 5:59 PM | `Konnichiwa` |
| 6:00 PM onward | `Konbanwa` |

## Features

### Command conventions

- Words in `UPPER_CASE` represent values that you must supply. For example, replace `DESCRIPTION`
  with a description such as `Read book`.
- Command words are not case-sensitive. Descriptions and search terms retain the text you enter.
- Dates use `d/M/yyyy`, for example `2/12/2026`.
- Date-times use `d/M/yyyy HHmm`, for example `2/12/2026 1800`.
- Task numbers start from 1 and appear in the results of `list`, `find`, and `on`.
- Descriptions cannot contain `|`, which is reserved for Yachiyo's data-file format.
- The `list` and `bye` commands do not accept additional arguments.

### Adding a to-do: `todo`

Adds a task without a date or time.

Format: `todo DESCRIPTION`

Example:

```text
todo Read book
```

`DESCRIPTION` must not be empty or contain `|`. Yachiyo adds a `[T]` task and updates the total and
remaining counts.

### Adding a deadline: `deadline`

Adds a task that must be completed by a particular date and time.

Format: `deadline DESCRIPTION /by DATE_TIME`

Example:

```text
deadline Submit report /by 20/9/2026 1700
```

`DESCRIPTION` and `DATE_TIME` are required. Yachiyo rejects malformed or non-existent dates such as
30 February.

### Adding an event: `event`

Adds a task that takes place between two date-times.

Format: `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME`

Example:

```text
event Project meeting /from 20/9/2026 1400 /to 20/9/2026 1600
```

The start and end are required, and the end date-time must be later than the start date-time.

### Listing every task: `list`

Displays every task with its number from the complete lineup.

Format: `list`

Example:

```text
list
```

If the lineup is empty, Yachiyo invites you to add a task. Additional arguments are rejected with a
correction suggesting `list`.

### Finding tasks by description: `find`

Finds tasks whose descriptions contain the supplied keyword or phrase.

Format: `find KEYWORD`

Example:

```text
find report
```

Matching is case-insensitive and can occur anywhere in the description. Results retain their task
numbers from the complete lineup. A missing keyword produces a `Check command` warning.

### Finding tasks on a date: `on`

Finds deadlines due on a date and events taking place on that date.

Format: `on DATE`

Example:

```text
on 20/9/2026
```

A multi-day event appears on its start date, end date, and every date in between. To-dos do not have
dates and are therefore not included.

### Marking a task as complete: `mark`

Marks the selected task as complete.

Format: `mark TASK_NUMBER`

Example:

```text
mark 2
```

Yachiyo changes the task indicator to `[X]` and decreases the remaining count. Completing the final
unfinished task triggers the celebratory response and summary animation. Marking an already-complete
task produces a friendly acknowledgement without saving or replaying the animation.

### Marking a task as incomplete: `unmark`

Returns the selected task to the incomplete state.

Format: `unmark TASK_NUMBER`

Example:

```text
unmark 2
```

Yachiyo changes the task indicator to `[ ]` and increases the remaining count. Unmarking an already
incomplete task leaves the task unchanged.

### Editing a task: `edit`

Changes one detail of an existing task.

Format: `edit TASK_NUMBER FIELD VALUE`

| Field | Supported task types | Value |
| --- | --- | --- |
| `/description` | To-do, deadline, and event | A non-empty description |
| `/by` | Deadline | A date-time in `d/M/yyyy HHmm` format |
| `/from` | Event | A date-time earlier than the event's unchanged end |
| `/to` | Event | A date-time later than the event's unchanged start |

Examples:

```text
edit 2 /description Submit final report
edit 2 /by 21/9/2026 1800
edit 3 /from 21/9/2026 1400
edit 3 /to 21/9/2026 1600
```

Each command accepts one field selector. Yachiyo preserves the task type, completion status, and all
fields that were not selected. It rejects fields that do not apply to the task and event time edits
that would place the end at or before the start.

### Deleting a task: `delete`

Removes the selected task from the lineup.

Format: `delete TASK_NUMBER`

Example:

```text
delete 2
```

Yachiyo displays the deleted task and updates the total count. The remaining task numbers may change,
so run `list` again before using another task number.

### Exiting Yachiyo: `bye`

Displays Yachiyo's farewell and closes the application.

Format: `bye`

Example:

```text
bye
```

The message field and Send button are disabled immediately, and the window closes after the farewell
remains visible briefly. Additional arguments are rejected with a correction suggesting `bye`.
The command remains available when task data cannot be loaded.

## Data storage and recovery

Yachiyo saves every successful add, edit, mark, unmark, and delete operation automatically. No manual
save command is needed.

Task data is stored in `data/yachiyo.txt`, relative to the directory from which Yachiyo is launched.
If the file does not exist, Yachiyo starts with an empty lineup and creates the file when a change is
first saved.

Yachiyo loads the file into memory once after the application starts successfully. If you edit the
file externally while Yachiyo is running, restart the application before expecting those changes to
appear. Close Yachiyo before manually editing the file and create a backup first.

If the file is unreadable or contains malformed task data:

1. Yachiyo displays a red `Storage error` and changes the summary to `Tasks unavailable`.
2. Task-related commands remain blocked so malformed data is not overwritten.
3. Close Yachiyo with `bye`.
4. Correct the file, restore a backup, or move the invalid file aside.
5. Restart Yachiyo to load the corrected data.

If saving fails, Yachiyo displays a storage error and keeps the in-memory lineup unchanged for that
operation.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add a to-do | `todo DESCRIPTION` | `todo Read book` |
| Add a deadline | `deadline DESCRIPTION /by DATE_TIME` | `deadline Submit report /by 20/9/2026 1700` |
| Add an event | `event DESCRIPTION /from START /to END` | `event Meeting /from 20/9/2026 1400 /to 20/9/2026 1600` |
| List all tasks | `list` | `list` |
| Find by description | `find KEYWORD` | `find report` |
| Find on a date | `on DATE` | `on 20/9/2026` |
| Mark as complete | `mark TASK_NUMBER` | `mark 2` |
| Mark as incomplete | `unmark TASK_NUMBER` | `unmark 2` |
| Edit one task detail | `edit TASK_NUMBER FIELD VALUE` | `edit 2 /description Submit final report` |
| Delete a task | `delete TASK_NUMBER` | `delete 2` |
| Exit Yachiyo | `bye` | `bye` |
