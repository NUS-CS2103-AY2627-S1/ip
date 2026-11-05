# Larp User Guide

Larp is a calm, keyboard-first task manager. Type short commands to capture todos,
deadlines, and events, then let Larp save them automatically between sessions.

![The Larp task manager](Ui.png)

## Quick start

1. Download `larp.jar` from the
   [latest GitHub release](https://github.com/hphehe/ip/releases/latest).
2. Open a terminal in the folder containing the JAR file.
3. Check that Java 25 is installed by running `java -version`.
4. Start Larp with `java -jar larp.jar`.
5. Type a command in the field at the bottom of the window and press Enter.

Commands and keywords are lowercase. Extra spaces around task details and date
separators are accepted.

## Command summary

| Action | Command |
| --- | --- |
| Add a todo | `todo DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by yyyy-MM-dd` |
| Add an event | `event DESCRIPTION /from START /to END` |
| List all tasks | `list` |
| Mark a task complete | `mark NUMBER` |
| Mark a task incomplete | `unmark NUMBER` |
| Delete a task | `delete NUMBER` |
| Find tasks | `find KEYWORD` |
| Exit Larp | `bye` |

## Adding tasks

### Todos

Use `todo DESCRIPTION` for a task without a date.

```text
todo read the CS2103 textbook
```

### Deadlines

Use `deadline DESCRIPTION /by yyyy-MM-dd`. The date must exist and use the exact
year-month-day format shown below.

```text
deadline submit iP /by 2026-09-18
```

### Events

Use `event DESCRIPTION /from START /to END`. Start and end values are short text,
so you can use a format that is meaningful to you.

```text
event project meeting /from Monday 2pm /to Monday 3pm
```

Descriptions and event times cannot contain `|` or line breaks because Larp uses
a line-based storage format.

## Viewing and updating tasks

Use `list` to display every task with its number. Use that number with `mark`,
`unmark`, or `delete`.

```text
list
mark 2
unmark 2
delete 3
```

Larp confirms each successful change and saves the updated list immediately.

## Finding tasks

Use `find KEYWORD` to display tasks whose descriptions contain the keyword.
Matching is case-insensitive.

```text
find book
```

This finds both `read book` and `Return Book`.

## Recovering from errors

Invalid commands appear in a distinct red response marked with `!`. The response
explains what needs to change, and your task list is left untouched.

If Larp detects an unreadable or malformed data file during startup, it will not
overwrite that file. Fix or remove `data/larp.txt`, then restart Larp.

## Exiting

Use `bye` to close Larp. Successful task changes are already saved, so no separate
save command is needed.
