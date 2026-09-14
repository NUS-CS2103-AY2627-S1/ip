# Arthur Dent User Guide

![Arthur Dent application window](Ui.png)

Arthur Dent is a desktop task manager inspired by the protagonist of *The Hitchhiker's Guide to the Galaxy*. It lets you add to-do tasks, deadlines, and events; view and manage your task list; search and filter tasks; and create shortcuts for commonly used commands.

## Formats

### Date and time formats
Use `yyyy-MM-dd HHmm` for every deadline and event date and time. For example, `2026-09-15 1430` represents 15 September 2026 at 2:30 PM.

### Command formats
The command consists of three parts:
- The command word, e.g. `todo`. 
  - This is a lowercase word which is case-sensitive (e.g. `Todo` would not work).
  - You can use [command aliases](#using-command-aliases) to shorten the command word.
  - Note: if there is only one command which starts with the user's inputted command word, then it automatically uses that command. For example, `t ABC` is parsed as `todo ABC`.
- The command argument, e.g. `read book`. 
- The command flags, e.g. `/by 2026-09-15 1430`. 
  - A flag is a word that starts with a slash `/` (e.g. `/by`) and is followed by the argument
  - Some flags are required to be present per the command
  - The flag's argument is the entire sentence following the flag until the next flag or the end of the command
  - E.g. `/from 2026-09-15 0900 /to 2026-09-15 1000` is a valid flag argument for the `event` command. It would be parsed as `from 2026-09-15 0900` and  `to 2026-09-15 1000`.
  - Flags can be in any order but must be separated by a space and are case-sensitive.
  - Note: only the flags required by the command are parsed. E.g. `alias add /from /by /to xyz` is parsed as `from /by` and `to xyz`.
All the parts of the command are separated by spaces.

## Adding to-do tasks
Adds a task without a date or time requirement.
Format: `todo DESCRIPTION`
Example: `todo read book`

The task is added to the current list.

```
added:
[T][ ] read book
```

## Adding deadlines

Adds a task that must be completed by a specified date and time. The date and time must be in the future.

Format: `deadline DESCRIPTION /by yyyy-MM-dd HHmm`

Example: `deadline submit report /by 2026-09-15 1430`

The task is added with its deadline displayed in a readable format.

```
added:
[D][ ] submit report (by Sep 15 2026 @ 14:30)
```

## Adding events

Adds a task with a start and end date and time. The event must not end in the past, and its start must not be after its end.

Format: `event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm`

Example: `event team meeting /from 2026-09-15 0900 /to 2026-09-15 1000`

The task is added with its full time range.

```
added:
[E][ ] team meeting (from Sep 15 2026 @ 09:00, to Sep 15 2026 @ 10:00)
```

## Listing tasks

Displays every task in the current list, numbered from one.

Format: `list`

Example: `list`

```
1. [T][ ] read book
2. [D][ ] submit report (by Sep 15 2026 @ 14:30)
```

If there are no tasks, the application displays `No tasks to display.`

## Using command aliases

Creates a shortcut for a command, lists saved shortcuts, or removes one. Aliases are stored for the current session.

Formats:

- `alias add /from ALIAS /to COMMAND`
- `alias list`
- `alias remove /name ALIAS`

Example: `alias add /from x /to todo`

```
alias added:
x -> todo
```

Afterward, `x read book` works the same as `todo read book`.

To view saved aliases, use `alias list`.

```
aliases:
x -> todo
```

To remove an alias, use `alias remove /name x`.

```
alias removed:
x
```

## Marking tasks as complete

Marks a task as complete. Use the one-based task number shown by `list`.

Format: `mark INDEX`

Example: `mark 1`

```
marked:
[T][X] read book
```

## Unmarking tasks

Marks a completed task as incomplete again.

Format: `unmark INDEX`

Example: `unmark 1`

```
unmarked:
[T][ ] read book
```

## Removing tasks

Removes a task from the current list. Use the one-based task number shown by `list`.

Format: `remove INDEX`

Example: `remove 1`

```
removed:
[T][ ] read book
```

## Finding tasks

Displays tasks whose descriptions contain a keyword.

Matching is case-sensitive. For example, `find report` matches `submit report` but not `Submit Report`.

Format: `find KEYWORD`

Example: `find report`

```
1. [D][ ] submit report (by Sep 15 2026 @ 14:30)
```

If no task matches, the application displays `No tasks to display.`

## Filtering tasks by date

Displays deadlines and events that occur on a particular calendar date. To-dos are not included in date filtering.

Format: `filter /on yyyy-MM-dd`

Example: `filter /on 2026-09-15`

```
1. [D][ ] submit report (by Sep 15 2026 @ 14:30)
2. [E][ ] team meeting (from Sep 15 2026 @ 09:00, to Sep 15 2026 @ 10:00)
```

## Exiting the application

Closes Arthur Dent after displaying a farewell message. There is a 1 second delay between the farewell message and the app closing.

Format: `bye`

```
So long, and thanks for all the fish.
```
