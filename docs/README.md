# Margit User Guide

![Margit interface](Ui.png)

Margit, the Fell Task Keeper, is a desktop task manager for recording,
reviewing, and planning the burdens before thee. Commands are entered in the
chat input at the bottom of the window. Margit saves the task list after every
change, so it is available the next time the application opens.

## Getting started

Install Java 25, then launch Margit from your IDE by running `margit.Launcher`,
or build the project with Gradle and run the generated JAR file. Type a command
and press Enter or select **Send**. Margit greets you when the application
opens.

## Command summary

| Command | Format | Purpose |
| --- | --- | --- |
| Add todo | `todo DESCRIPTION` | Adds an undated task. |
| Add deadline | `deadline DESCRIPTION /by DATE_OR_TIME` | Adds a task due by a date or date-time. |
| Add event | `event DESCRIPTION /from DATE_OR_TIME /to DATE_OR_TIME` | Adds an event with a start and end. |
| List tasks | `list` | Shows all saved tasks. |
| Mark task | `mark INDEX` | Marks a listed task as complete. |
| Unmark task | `unmark INDEX` | Restores a listed task as incomplete. |
| Delete task | `delete INDEX` | Permanently removes a listed task. |
| Find tasks | `find KEYWORD` | Finds task descriptions containing a keyword. |
| View a date | `on DATE` | Shows scheduled tasks that occur on a date. |
| Sort tasks | `sort` | Permanently sorts tasks into chronological order. |
| Exit | `bye` | Ends the terminal session or shows a GUI farewell. |

`INDEX` is the task number shown by `list`. Commands and dates are described in
more detail below.

## Command conventions

- Text in `UPPERCASE` is a value that you supply; do not type the capitalised
  placeholder itself.
- Task descriptions may contain spaces.
- Use a task number from the most recent `list` output for commands that take
  an `INDEX`.
- Margit ignores leading and trailing spaces in a command, but command words
  such as `todo` and `list` must be lowercase.

## Task types

Margit manages three kinds of task:

- **Todo**: a task without a date or time.
- **Deadline**: a task due by a date or date-time.
- **Event**: a task that has a start and end date or date-time.

Tasks are saved automatically in `data/Margit.txt` relative to the folder from
which the application is launched.

## Date and time formats

Use either a date or a date-time when creating deadlines and events. Margit
accepts the following common formats:

```text
19/9/2026
2026-09-19
19/9/2026 1900
2026-09-19 19:00
```

A deadline given as a date only is treated as due at 23:59 on that date when
tasks are sorted.

## Managing tasks

### Add a todo

Adds a task without a date or time.

```text
todo Review week 5 lecture notes
```

### Add a deadline

Adds a task with a required `/by` date or date-time.

```text
deadline Submit weekly reflection /by 19/9/2026 2359
```

### Add an event

Adds a task with required `/from` and `/to` dates or date-times.

```text
event Project planning meeting /from 18/9/2026 1900 /to 18/9/2026 2030
```

### List tasks

Shows every task in its current saved order.

```text
list
```

### Mark or unmark a task

Marks a numbered task as complete, or restores it as incomplete. Use the task
number shown by `list`.

```text
mark 2
unmark 2
```

### Delete a task

Removes a numbered task permanently.

```text
delete 3
```

## Finding and planning tasks

### Find by keyword

Shows tasks whose descriptions contain the given keyword. Matching is
case-sensitive.

```text
find project
```

### View a date

Shows deadlines and events that fall on the given date. Events that span the
date are included.

```text
on 19/9/2026
```

### Sort tasks

Permanently sorts tasks in ascending order. Todos remain first, followed by
deadline and event tasks in chronological order. Events are ordered by their
start time and deadlines by their due time. If two scheduled tasks have the
same time, deadlines appear before events.

```text
sort
```

### End a terminal session

Use `bye` to end Margit's terminal session. In the GUI, it displays Margit's
farewell while leaving the application window open.

```text
bye
```
