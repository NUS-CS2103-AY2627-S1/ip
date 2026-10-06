# Gihun456 User Guide

## Introduction

**Gihun456** is a chat-style task manager for keeping track of todos,
deadlines, and events. Enter commands in the chat box and Gihun456 will
display the result immediately.

Your task list is saved automatically in `data/Gihun456.txt`, so it is
available the next time you start the application.

## Getting started

1. Make sure you have Java 25 installed.
2. Install the Jar file and run `java -jar Gihun456.jar` from the directory.
3. Enter a command in the input box and send it with the **"Play Game"** button.
4. Enter `bye` to exit.

Commands are not case-sensitive. For example, `LIST` and `list` have the same
effect. Extra spaces before or after a command are ignored.

## Features

### Adding tasks

#### Adding a todo

Use a todo for a task without a date or time.

```text
todo <description>
```

Example:

```text
todo Buy groceries
```

#### Adding a deadline

Use a deadline for a task that must be completed by a specific date or time.

```text
deadline <description> /by <date and time>
```

Examples:

```text
deadline Submit project report /by 15/09/2026 1900
deadline Renew passport /by 2026-09-30
```

#### Adding an event

Use an event for something that takes place over a time range.

```text
event <description> /from <start date and time> /to <end date and time>
```

Example:

```text
event Team meeting /from 15/09/2026 1000 /to 15/09/2026 1100
```

The `/from` part must come before `/to`. An event cannot have an end date/time before its start date/time.
### Viewing tasks

#### Listing all tasks

Use `list` to see every task in the order it was added:

```text
list
```

Each task has a number. Use this number with `mark`, `unmark`, or `delete`.
Tasks are shown with a type marker:

```text
1. [T][ ] Buy groceries
2. [D][ ] Submit project report (by: Sep 15 2026, 7:00 pm)
3. [E][ ] Team meeting (from: Sep 15 2026, 10:00 am to: Sep 15 2026, 11:00 am)
```

`[ ]` means incomplete and `[X]` means complete. The type markers are `[T]` for todo, `[D]`
for deadline and `[E]` for event.
#### Finding tasks

Use `find` to search task descriptions. The search is case-insensitive and
matches any part of a description.

```text
find <keyword>
```

Example:

```text
find report
```

### Updating tasks

#### Marking a task as complete

```text
mark <task number>
```

Example:

```text
mark 2
```

#### Marking a task as incomplete

```text
unmark <task number>
```

Example:

```text
unmark 2
```

#### Deleting a task

```text
delete <task number>
```

Example:

```text
delete 1
```

### Checking reminders

Use `reminders` to see incomplete dated tasks that need attention:

```text
reminders
```

The report contains:

* **Upcoming games**: deadlines due or events starting within the next 72
  hours, including the current time.
* **Missed games**: deadlines whose due time or events whose start time has
  already passed.

Completed tasks and todos without dates are not included. For events, the
start time determines when the reminder appears, but the full event range is
shown. The same report is displayed automatically when the application starts.
`reminders` does not accept arguments.

### Handling scheduling conflicts

When you add a deadline or event that overlaps with an existing incomplete
deadline or event, Gihun456 shows the proposed task and the conflicting tasks,
then asks:

```text
Careful, Player 456. This game overlaps with:
Your proposed game:
[D][ ] Submit report (by: Sep 15 2026, 10:30 am)
Games already in this time slot:
1. [E][ ] Team meeting (from: Sep 15 2026, 10:00 am to Sep 15 2026, 11:00 am)
Time to make a choice, Player 456. (yes/no)
```

Reply with:

* `yes` to add the proposed task anyway.
* `no` to cancel the proposed task.

Replies are case-insensitive. If the reply is not one of these choices,
Gihun456 asks again. Todos and completed dated tasks do not trigger conflict
warnings. A deadline conflicts with an event only when its date and time are
strictly inside the event. Two deadlines conflict when their date and time
are equal.

### Exiting

Enter `bye` to end the session:

```text
bye
```

The application displays a farewell message before exiting.

## Date and time format

Dates may use either of these formats:

```text
dd/MM/yyyy
yyyy-MM-dd
```

You may add a 24-hour time in `HHmm` format, separated from the date by a
space:

```text
15/09/2026 1900
2026-09-15 1900
```

If the time is omitted, it is treated as midnight (`0000`) on that date.

## Command summary

| Command | Format | Purpose |
| --- | --- | --- |
| `todo` | `todo <description>` | Add a todo |
| `deadline` | `deadline <description> /by <date and time>` | Add a deadline |
| `event` | `event <description> /from <start> /to <end>` | Add an event |
| `list` | `list` | Show all tasks |
| `find` | `find <keyword>` | Find tasks by description |
| `mark` | `mark <task number>` | Mark a task as complete |
| `unmark` | `unmark <task number>` | Mark a task as incomplete |
| `delete` | `delete <task number>` | Delete a task |
| `reminders` | `reminders` | Show upcoming and missed dated tasks |
| `bye` | `bye` | Exit Gihun456 |

## Troubleshooting

* **"This isn't a valid game number."** Use a task number shown by `list`.
* **"The keyword cannot be empty."** Add a keyword after `find`.
* **Date format errors:** Check that the date uses one of the supported
  formats above and that the time is four digits in `HHmm` format.
* **Reminder command errors:** Use `reminders` by itself, without a date,
  keyword, or other argument.
* **Empty list:** Add a task first. `list`, `find`, and `delete` does not work on
  an empty task list.
