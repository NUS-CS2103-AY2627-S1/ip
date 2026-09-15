# Luke User Guide

Luke is a friendly desktop chatbot for tracking todos, deadlines, and events.
It saves your task list automatically, so your tasks are still available the
next time you start the app.

![Luke displaying a realistically populated task list](Ui.png)

## Quick Start

Download `luke.jar` from the latest release, open a terminal in the folder
containing it, and run:

```bash
java -jar luke.jar
```

Type one command in the input box and press Enter or click the arrow button.
The command reference in the left sidebar provides a quick reminder. Use
`bye` when you are finished.

## Command Summary

| Command | Format | Purpose |
| --- | --- | --- |
| `todo` | `todo DESCRIPTION` | Adds a todo task. |
| `deadline` | `deadline DESCRIPTION /by TIME` | Adds a deadline task. |
| `event` | `event DESCRIPTION /from START /to END` | Adds an event task. |
| `list` | `list` | Shows all tasks. |
| `list` | `list /sort alpha` | Shows tasks sorted alphabetically by description. |
| `list` | `list /sort time` | Shows tasks sorted by parseable date or time values. |
| `find` | `find SEARCH_TEXT` | Shows tasks whose descriptions contain the search text. |
| `mark` | `mark INDEX` | Marks a task as done. |
| `unmark` | `unmark INDEX` | Marks a task as not done. |
| `delete` | `delete INDEX` | Deletes a task. |
| `bye` | `bye` | Exits Luke. |

`DESCRIPTION`, `TIME`, `START`, and `END` can contain spaces. For example, `return book`, `next Sunday`, and `Mon 2pm` are all valid values.

## Adding Todos

Use `todo` followed by the task description:

```text
todo read book
```

Expected output:

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

Todos do not accept flags. For example, `todo read book /by Sunday` is rejected because `/by` belongs to deadlines.

## Adding Deadlines

Use `deadline`, a description, and the required `/by` flag:

```text
deadline return book /by Sunday
```

Expected output:

```text
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 tasks in the list.
```

The description must appear before `/by`. This is invalid because the description is missing:

```text
deadline /by Sunday
```

## Adding Events

Use `event`, a description, and both required flags: `/from` and `/to`.

```text
event project meeting /from Mon 2pm /to 4pm
```

Expected output:

```text
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4:00 PM)
Now you have 1 tasks in the list.
```

Both `/from` and `/to` are required. Event flags can contain spaces in their values.
When Luke recognizes both values as dates or times, the end must be later than
the start.

## Listing Tasks

Use `list` with no extra arguments:

```text
list
```

Example output:

```text
1. [T][ ] read book
2. [D][ ] return book (by: Sunday)
3. [E][ ] project meeting (from: Mon 2pm to: 4pm)
```

Use `list /sort alpha` to sort tasks alphabetically by their descriptions.
Sorting is case-insensitive:

```text
list /sort alpha
```

Example sorted output:

```text
1. [D][ ] apply for internship (by: Friday)
2. [E][ ] project meeting (from: Mon 2pm to: 4pm)
3. [T][ ] read book
```

Use `list /sort time` to show tasks with parseable date or time values first:

```text
list /sort time
```

Example sorted output:

```text
1. [E][ ] consultation (from: 4:00 PM to: 5:00 PM)
2. [D][ ] submit report (by: Dec 2 2019 6:00 PM)
3. [T][ ] read book
4. [D][ ] return book (by: Sunday)
```

Tasks with parseable values are sorted from earliest to latest. Tasks without a
parseable date or time stay after them in their original order.

## Finding Tasks

Use `find` followed by search text:

```text
find book
```

Example output:

```text
Here are the matching tasks in your list:
1. [T][ ] read book
2. [D][ ] return book (by: Sunday)
```

Search is case-insensitive and ignores repeated spaces.

Task display format:

```text
[TYPE][STATUS] DESCRIPTION (flags)
```

Types:

| Symbol | Meaning |
| --- | --- |
| `T` | Todo |
| `D` | Deadline |
| `E` | Event |

Statuses:

| Symbol | Meaning |
| --- | --- |
| blank space | Not done |
| `X` | Done |

## Marking and Unmarking Tasks

Use the number shown by `list`.

```text
mark 1
```

Expected output:

```text
Nice! I've marked this task as done:
 [T][X] read book
```

Use `unmark` to mark it as not done:

```text
unmark 1
```

Expected output:

```text
OK! I've marked this task as not done yet:
 [T][ ] read book
```

Indexes are 1-based, so `1` means the first task in the list.

## Deleting Tasks

Use `delete` with the number shown by `list`.

```text
delete 1
```

Expected output:

```text
Noted. I've removed this task:
 [T][ ] read book
Now you have 0 tasks in the list.
```

After a task is deleted, the remaining tasks are renumbered the next time you
use `list`.

## Exiting

Use `bye` with no extra arguments:

```text
bye
```

Expected output:

```text
Bye. Hope to see you again soon!
```

## Input Validation

Luke reports user input problems as normal chatbot errors. Common examples:

| Input | Error |
| --- | --- |
| `list something` | `` `list` command does not take arguments: something `` |
| `bye later` | `` `bye` command does not take arguments: later `` |
| `todo` | `Missing description for todo task.` |
| `deadline return book` | `Missing required flag: /by` |
| `deadline /by Sunday` | `Missing description for deadline task.` |
| `deadline return book /by Sunday /by Monday` | `Duplicate flag: /by` |
| `deadline return book /er idk` | `Unidentified flag: /er` |
| `todo read book /by Sunday` | `Unsupported flag: /by` |
| `list /sort name` | `Unsupported value for /sort: name` |
| `find` | `` `find` command requires an argument. `` |
| `find book /sort time` | `Unsupported flag: /sort` |
| `mark banana` | `` `mark` command received invalid index: banana `` |
| `mark 999` | `` `mark` command received out-of-bounds index: 999 `` |
| `delete 999` | `` `delete` command received out-of-bounds index: 999 `` |
| `event call /from 5pm /to 4pm` | `Event end must be after its start.` |
