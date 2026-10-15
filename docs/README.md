# Judey User Guide

**Judey** is a friendly astronaut-cat chatbot for organising your tasks. You can use it to record todos, deadlines, and events, then manage them from the task list.

## Appendix: Quick navigation

- [Getting started](#getting-started)
- [Using Judey](#using-judey)
  - [Quick start](#quick-start)
  - [Command format](#command-format)
- [Command summary](#command-summary)
- [Command details](#command-details)
  - [Add tasks](#add-tasks)
  - [Manage tasks](#manage-tasks)
  - [Find events by date](#find-events-by-date)
  - [Find tasks by description](#find-tasks-by-description)
  - [The `help` command](#the-help-command)
  - [The `bye` command](#the-bye-command)
- [Common problems](#common-problems)
  - [Missing or invalid command arguments](#missing-or-invalid-command-arguments)
  - [Other common problems](#other-common-problems)
- [Tips](#tips)

## Getting started

### Prerequisite

- Java Development Kit (JDK) 25

To get started:

1. Download the latest `judey.jar` from the project's [Releases page](https://github.com/Umaiza-15/ip/releases).
1. Create an empty folder where you want Judey to store your task data.
1. Place the downloaded JAR file inside that folder.
1. Open a terminal in that folder.
1. Run the following command:

   ```text
   java -jar judey.jar
   ```

The Judey window will open with a chat box for entering commands. Your tasks will be saved in a `data` subfolder as `data/judey.txt`.

Your tasks are saved automatically in `data/judey.txt`, so they will still be available the next time you start Judey from the same folder.

## Using Judey

Type one command at a time, then press **Enter** or click **Send**. Task numbers are assigned by the order shown by `list`, starting from 1.

### Quick start

Try these commands in order:

```text
todo buy groceries
deadline submit assignment /by 25/9/2026 2359
event project meeting /from 26/9/2026 1400 /to 26/9/2026 1500
list
mark 1
```

Use the task number shown by `list` when marking, unmarking, or deleting a task.

### Command format

- Commands are **case-sensitive**. Use lowercase commands such as `todo`, `list`, and `bye`.
- Text in angle brackets, such as `<description>` or `<task number>`, is a placeholder. Replace it with your own value, and do not type the angle brackets.
- Dates and times use `d/M/yyyy HHmm`, with a 24-hour clock. For example, `2/12/2026 1800` means 2 December 2026 at 6:00 PM.
- Event start times cannot be in the past, and an event's start must be before its end.
- Use the markers exactly as shown: `/by`, `/from`, and `/to`. Do not insert spaces inside them or change their spelling.
- A deadline needs one `/by` marker. An event needs both `/from` and `/to`, in that order. Include a description before the marker.
- Separate the command and its arguments with spaces, but do not add extra characters or punctuation to the command names or markers.

## Command summary

| Command | Purpose |
| --- | --- |
| `todo <description>` | Add a todo task. |
| `deadline <description> /by <date and time>` | Add a task with a deadline. |
| `event <description> /from <date and time> /to <date and time>` | Add an event with a start and end time. |
| `list` | Display all tasks, their numbers, types, and completion status. |
| `mark <task number>` | Mark a task as completed. |
| `unmark <task number>` | Mark a task as incomplete. |
| `delete <task number>` | Delete a task. |
| `events-on <date>` | Show deadlines and events on a specific date. |
| `find <keyword>` | Find tasks whose descriptions contain the keyword. |
| `help` | Display Judey's built-in command guide. |
| `bye` | Exit Judey. |

## Command details

### Add tasks

| Command | What it does | Example |
| --- | --- | --- |
| `todo <description>` | Adds a task with no date. | `todo read a book` |
| `deadline <description> /by <date and time>` | Adds a task with a due date. | `deadline submit report /by 2/12/2026 2359` |
| `event <description> /from <date and time> /to <date and time>` | Adds a task with a start and end time. | `event team meeting /from 15/10/2026 1400 /to 15/10/2026 1600` |

For dates and times, use `d/M/yyyy HHmm` format:

```text
2/12/2026 1800
```

The time uses 24-hour notation. For example, `1800` means 6:00 PM.

### Manage tasks

- `list` — shows every task, its number, completion status, and task type. The type tag is `[T]` for a todo, `[D]` for a deadline, and `[E]` for an event. An incomplete task has `[ ]`; a completed task has `[X]`.
- `mark <task number>` — marks a task as done. When you run `list`, its status changes to `[X]`. Example: `mark 1`
- `unmark <task number>` — marks a completed task as incomplete again. When you run `list`, its status changes back to `[ ]`. Example: `unmark 1`
- `delete <task number>` — permanently removes the selected task from the list. Example: `delete 1`

For example, this `list` output means task 1 is an incomplete todo, task 2 is a completed deadline, and task 3 is an incomplete event:

```text
1.[T][ ] buy groceries
2.[D][X] submit assignment (by: Sep 25 2026, 11:59PM)
3.[E][ ] project meeting (from: Sep 26 2026, 2:00PM to Sep 26 2026, 3:00PM)
```

### Find events by date

Use `events-on <date: d/M/yyyy>` to show events occurring on a particular date. The date must use `d/M/yyyy` format:

```text
events-on 2/12/2026
```

An event appears for every date from its start date through its end date, inclusive. Tasks shown by `events-on` keep
their original task numbers so that the displayed number can be used with `mark`, `unmark`, or `delete`.

### Find tasks by description

Use `find <keyword>` to search task descriptions. Searches are case-insensitive and may contain multiple words.
Matching tasks are shown in their original task-list order and keep their original task numbers.

```text
find project meeting
```

If no task matches, Judey displays:

```text
Here are the matching tasks in your mission log:
No matching tasks found in this sector.
```

The search keyword is required. For example, entering only `find` displays an error with the correct format.

### The `help` command

Enter `help` when you need a reminder of the available commands. Judey displays the command groups, expected formats, and examples for adding, managing, and filtering tasks. Use the examples as templates: replace values such as `<description>` and `<task number>` with your own information, but keep the command names and `/by`, `/from`, and `/to` markers unchanged.

`help` does not change your task list and does not accept extra text. Enter only:

```text
help
```

### The `bye` command

Enter `bye` when you are finished. Judey displays a goodbye message and closes the application.

Tasks that you added or changed during the session are saved automatically as each command is completed. They remain in `data/judey.txt` and will be loaded the next time you start Judey from the same folder.

```text
bye
```

## Common problems

### Missing or invalid command arguments

The `mark`, `unmark`, `delete`, and `events-on` commands report missing or invalid arguments with a correction
example. Judey highlights the missing or invalid value in that example. For example, entering `mark`
shows:

```text
The mark command is missing a task number.

Try: mark <task number>
```

For `events-on`, the correction includes the required date format:

```text
The events-on command is missing a date.

Try: events-on <date: d/M/yyyy>
```

Task numbers must be positive whole numbers written using digits. For example, use `mark 1`, not `mark one`.
This rule also applies to `unmark` and `delete`. A positive number that is not present in the task list produces a
separate task-not-found error.

### Other common problems

| Problem | What to do |
| --- | --- |
| Judey does not start | Check that JDK 25 is installed, that your terminal is open in the folder containing `judey.jar`, and that you ran `java -jar judey.jar`. |
| A command is not recognised | Commands are case-sensitive. Use the lowercase command names shown in this guide, such as `todo` or `list`. |
| A date or time is rejected | Use a real date in `d/M/yyyy HHmm` format, such as `25/9/2026 2359`, and use a 24-hour clock. |
| An event is rejected | Make sure its start time is not in the past and that its start date/time is before its end date/time. |
| An event or deadline is rejected | Check that the description is present and that you used the markers exactly as `/by`, `/from`, and `/to`. Do not type the angle brackets from the examples. |
| A task number is rejected | Run `list` and use the current number shown beside the task. Enter the number using digits, starting from 1; for example, use `mark 1`, not `mark one`. |
| The task list is empty | Add a task first, then run `list`. If previously saved tasks are missing, make sure you started Judey from the same folder as before so it can find `data/judey.txt`. |
| An error message is unclear | When a command has missing or invalid input, Judey highlights the corresponding missing or incorrect value in the suggested command. Replace that part and submit the command again. |

## Tips

- Use `list` before `mark`, `unmark`, or `delete` if you are unsure of a task number.
- If a command is invalid or missing information, Judey shows an example of the expected format and highlights what was wrong or missing. Correct that part and try again.
- Keep the `/by`, `/from`, and `/to` markers exactly as shown in the examples.
