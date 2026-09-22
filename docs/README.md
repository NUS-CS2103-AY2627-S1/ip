# Lebron User Guide

![Lebron application](Ui.png)

Lebron is a desktop task manager with the energy of a basketball coach. It
helps you track todos, deadlines, events, and repeating commitments through
short commands while saving your tasks between sessions.

## Quick start

1. Install Java 25.
2. Put `Lebron.jar` in an empty folder.
3. Open a terminal in that folder.
4. Run `java -jar "Lebron.jar"`.
5. Type a command in the input box and press **Enter** or click **Send**.

Lebron creates `data/lebron.txt` beside the JAR to store your tasks. Keep this
file if you want your tasks to appear the next time you open the application.

## Command summary

- `todo DESCRIPTION`: add a todo.
- `deadline DESCRIPTION /by DATE`: add a deadline.
- `event DESCRIPTION /from DATE /to DATE`: add an event.
- `list`: show every task and its number.
- `mark NUMBER`: mark a task as done.
- `unmark NUMBER`: mark a completed task as not done.
- `delete NUMBER`: remove a task.
- `find KEYWORD`: find tasks containing the keyword.
- `bye`: close Lebron.

Words written in uppercase above are values that you replace. Do not type the
uppercase placeholder itself.

## Date and time formats

Lebron accepts either of these date formats:

- `YYYY-MM-DD`, such as `2026-09-18`
- `D/M/YYYY`, such as `18/9/2026`

You can optionally add a 24-hour time in `HHmm` format:

- `2026-09-18 1830`
- `18/9/2026 1830`

Dates must exist on the calendar. For an event, `/to` must be later than
`/from`.

## Adding a todo

Format: `todo DESCRIPTION`

Example:

```text
todo read textbook
```

Lebron adds the new todo to the end of the playbook.

## Adding a deadline

Format: `deadline DESCRIPTION /by DATE`

Examples:

```text
deadline submit assignment /by 2026-09-18
deadline attend consultation /by 18/9/2026 1400
```

The `/by` field is required and must appear exactly once.

## Adding an event

Format: `event DESCRIPTION /from START /to END`

Example:

```text
event project meeting /from 2026-09-18 1400 /to 2026-09-18 1600
```

Both `/from` and `/to` are required. The event's end must be later than its
start.

## Listing tasks

Format: `list`

Lebron displays all tasks in their current order. The number beside each task
is used by the `mark`, `unmark`, and `delete` commands.

```text
Here's the playbook:
1.[ ] read textbook
2.[D][ ] submit assignment (by: Sep 18 2026)
```

## Marking and unmarking tasks

Formats:

```text
mark NUMBER
unmark NUMBER
```

Examples:

```text
mark 2
unmark 2
```

Task numbers start from 1 and can be seen using `list`.

## Deleting a task

Format: `delete NUMBER`

Example:

```text
delete 2
```

Lebron removes the selected task. The remaining tasks are renumbered.

## Finding tasks

Format: `find KEYWORD`

Example:

```text
find book
```

Lebron displays tasks whose descriptions contain the keyword. Matching is
case-sensitive, so `book` and `Book` are different searches.

## Creating recurring deadlines and events

Add `/every day`, `/every week`, or `/every month` to the end of a `deadline`
or `event` command.

Examples:

```text
deadline pay rent /by 2026-10-01 /every month
event standup /from 2026-09-21 0900 /to 2026-09-21 0930 /every week
```

`/every` must come after `/by` for a deadline and after `/to` for an event.

When you mark a recurring task, Lebron moves it to its next occurrence instead
of leaving it marked as done. For a recurring event, both its start and end
move forward together.

## Exiting the application

Format: `bye`

Lebron saves changes as you make them. `bye` closes the application after its
farewell message.

## Invalid commands and data

Lebron highlights command errors in red and explains how to correct them. It
rejects missing parameters, invalid task numbers, impossible dates, repeated
command fields, and descriptions containing `|`. If the saved data file
contains an unreadable line, Lebron skips that line and loads the remaining
valid tasks.

Adding a task identical to an existing task is allowed, but Lebron warns you
so that accidental duplicates do not go unnoticed.

## Image credit

Lebron's profile photo is adapted from
[a photograph by Erik Drost](https://commons.wikimedia.org/wiki/File:LeBron_James,_25_November_2023_02_(cropped).jpg),
cropped by CavsFan45 and licensed under
[CC BY 2.0](https://creativecommons.org/licenses/by/2.0/). The application
displays a circular crop of the photograph.
