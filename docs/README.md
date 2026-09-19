# Nori User Guide

Nori is a desktop task companion for keeping to-dos, deadlines, and events in
one calm, searchable list. Type a command in the field at the bottom and press
Enter or select **Send**. Nori saves every change as you make it, so your tasks
are there the next time you open it.

![Nori's window after adding a to-do, a deadline, and an event](Ui.png)

## Getting started

1. Check that Java 25 is installed: `java -version` should report version 25.
2. Download `Nori.jar` from the
   [latest release](https://github.com/darrenori/ip/releases/latest) and put it
   in an empty folder.
3. Open a terminal in that folder and run `java -jar "Nori.jar"`.

When the window opens, add a few tasks, then use `list` to see them:

```
todo review the CS2103 tutorial
deadline submit reflection /by 2026-09-18
event project stand-up /from 2026-09-16 1000 /to 2026-09-16 1030
list
```

All commands are lowercase. Use dates in `yyyy-MM-dd` form, for example
`2026-09-18`. Type `help` at any time for the built-in command reminder.

## Add tasks

`todo <description>` adds a task without a date.

Example: `todo review lecture notes`

`deadline <description> /by <date>` adds a task due on one date.

Example: `deadline submit reflection /by 2026-09-18`

`event <description> /from <start> /to <end>` adds an event. Its start and end
can be dates, times, or both. Add a date and time to `/from` when you want the
event to appear in time order in a schedule.

Example: `event project stand-up /from 2026-09-16 1000 /to 2026-09-16 1030`

Nori understands common times such as `0900`, `9:30`, `2pm`, and `2:30 PM`,
and rejects a time no clock can show, such as `25:00` or `2400`.

Nori won't add a task you already have: one of the same type, with the same
description and dates. Extra spaces don't make a task new, but letter case does,
so `Draft` and `draft` can both be kept.

## View and find tasks

`list` shows every task. `list /from <date> /to <date>` shows dated tasks in an
inclusive date range.

Examples:

```
list
list /from 2026-09-15 /to 2026-09-21
```

`on <date>` finds the deadlines and events that fall on that date.

Example: `on 2026-09-16`

`find <keyword>` searches task descriptions without regard to letter case.

Example: `find project`

`schedule [date]` lays out one day: timed events come first, followed by all-day
events, deadlines, and to-dos. Omit the date to see today.

Examples:

```
schedule
schedule 2026-09-16
```

## Update tasks

Use the number shown by `list`, `find`, `on`, or `schedule`.

| Command | What it does | Example |
| --- | --- | --- |
| `mark <number>` | Marks a task complete. | `mark 2` |
| `unmark <number>` | Marks a task incomplete. | `unmark 2` |
| `delete <number>` | Removes a task. | `delete 2` |

## Helpful keyboard shortcuts

After entering commands, press the up and down arrow keys in the command field
to recall them. Recalled commands are editable before you send them again. The
orange outline around the composer shows where keyboard input will go.

## Finish a session

`bye` closes Nori. Your saved tasks remain available when you launch it again.

## Your saved tasks

Nori saves your tasks to `data/nori.txt` in the folder you launched it from,
and keeps a backup copy, `data/nori.txt.bak`, beside it. To move your tasks to
another computer, copy the `data` folder along with `Nori.jar`.

If `nori.txt` is damaged, Nori restores the backup and keeps the damaged file
as `data/nori.txt.corrupt`. If the backup can't be used either, Nori opens with
an empty list and won't save anything until you repair `data/nori.txt` or move
it out of the `data` folder and reopen Nori, so the tasks in it are never
overwritten.

## Command reference

| Command | Purpose |
| --- | --- |
| `todo <description>` | Add a to-do. |
| `deadline <description> /by <date>` | Add a deadline. |
| `event <description> /from <start> /to <end>` | Add an event. |
| `list [ /from <date> /to <date> ]` | List all tasks or dated tasks in a range. |
| `on <date>` | Show tasks falling on a date. |
| `schedule [date]` | Show one day's schedule. |
| `find <keyword>` | Search task descriptions. |
| `mark <number>` | Complete a task. |
| `unmark <number>` | Reopen a task. |
| `delete <number>` | Delete a task. |
| `help` | Show the in-app command summary. |
| `bye` | Close Nori. |
