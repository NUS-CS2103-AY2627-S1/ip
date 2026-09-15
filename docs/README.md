# Cookie User Guide

**Cookie** is a friendly task manager that helps you record todos, deadlines, and events
using short text commands.

![Cookie's chat window showing sorted tasks and a keyword search](Ui.png)

## Quick start

Cookie supports Windows x64, macOS Apple Silicon (M1 or newer), and Linux x64.
Java 25 is required.

1. Download `cookie.jar`.
2. Open a terminal in the folder containing the JAR.
3. Confirm that Java 25 is active:

   ```text
   java -version
   ```

4. Run Cookie:

   ```text
   java -jar cookie.jar
   ```

On macOS, ensure that you are using an ARM64 build of Java 25.

Type a command in the box at the bottom of the window, then press <kbd>Enter</kbd> or
select **Send**. For example, enter `todo read book`, followed by `list`.

## Command summary

Words in angle brackets, such as `<description>`, are required. Words in square
brackets, such as `[asc|desc]`, are optional. Do not type the brackets.

| Command | What it does |
| --- | --- |
| `todo <description>` | Adds a task without a date or time. |
| `deadline <description> /by <date/time>` | Adds a task with a due date, time, or both. |
| `event <description> /from <date/time> /to <date/time>` | Adds an event with a start and end. |
| `list` | Shows every task in its saved order. |
| `find <keyword>` | Finds tasks containing the keyword. |
| `on <date>` | Shows deadlines and events occurring on the date. |
| `sort <description\|date> [asc\|desc]` | Shows a temporarily sorted task list. |
| `mark <task number>` | Marks a task as done. |
| `unmark <task number>` | Marks a task as not done. |
| `delete <task number>` | Deletes a task. |
| `bye` | Says goodbye and ends a console session. |

Command words are not case-sensitive. Descriptions and search keywords keep their
capitalisation.

## Adding tasks

### Adding a todo: `todo`

Use a todo for a task without a date or time.

**Format:** `todo <description>`

**Example:**

```text
todo read book
```

### Adding a deadline: `deadline`

Use a deadline for something that must be completed by a date or time.

**Format:** `deadline <description> /by <date/time>`

**Examples:**

```text
deadline submit report /by 2026-09-15 1800
deadline pay bill /by 15/9/2026
deadline take medicine /by 2100
```

### Adding an event: `event`

Use an event for something with a start and end.

**Format:** `event <description> /from <date/time> /to <date/time>`

**Example:**

```text
event project meeting /from 2026-09-15 1400 /to 2026-09-15 1600
```

The start and end must both be dates, both be times, or both be date-times. The end
must be later than the start.

### Date and time formats

Cookie accepts the following input formats:

| Value | Accepted format | Example |
| --- | --- | --- |
| Date | `yyyy-MM-dd` or `d/M/yyyy` | `2026-09-15` or `15/9/2026` |
| Time | `HHmm` using the 24-hour clock | `1800` |
| Date and time | A supported date, a space, then a time | `2026-09-15 1800` |

Keep `/by`, `/from`, and `/to` lowercase and separated from surrounding text by spaces.

## Viewing and finding tasks

### Viewing all tasks: `list`

Enter `list` to see all tasks. Cookie gives each task a number and displays its type and
status:

- `[T]`, `[D]`, and `[E]` mean todo, deadline, and event respectively.
- `[ ]` means not done; `[X]` means done.

Use the displayed task number with `mark`, `unmark`, or `delete`.

### Finding tasks by keyword: `find`

**Format:** `find <keyword>`

**Example:** `find book`

The keyword must be one word. Matching is a case-sensitive substring search of task
descriptions, so `find book` matches `read book` but not `read Book`.

### Finding tasks on a date: `on`

**Format:** `on <date>`

**Example:** `on 2026-09-15`

This shows deadlines due on that date and events whose date range includes it. Todos and
time-only tasks are not shown because they have no calendar date.

Results from `find` and `on` keep the original task numbers shown by `list`.

## Sorting tasks: `sort`

**Format:** `sort <description|date> [asc|desc]`

Ascending order is used when the direction is omitted.

```text
sort description
sort description desc
sort date
sort date desc
```

Description sorting is alphabetical and case-insensitive. Date sorting compares a
deadline's due value with an event's start value. Dated tasks appear before time-only
tasks, followed by todos, in either direction.

Sorting only changes the current view; it does not rearrange the saved list. The original
task numbers remain displayed, so `mark 3` still updates task 3 even if sorting places it
first.

## Updating and deleting tasks

Use the task number shown by Cookie:

```text
mark 2
unmark 2
delete 2
```

`mark` records a task as done, `unmark` restores it to not done, and `delete` removes it.
Adding, updating, and deleting tasks are saved automatically to `data/cookie.txt`.

## Exiting Cookie

Close the desktop window when you are finished. If you are running Cookie in console mode,
enter `bye` to end the session.
