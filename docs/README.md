# Baron User Guide

Baron is a desktop task manager for to-dos, deadlines, and events. Tasks are saved automatically in `data/tasks.json` and restored when Baron starts.

## Getting started

Requires JDK 25. From the `ip` directory, run `./gradlew run` (or `gradlew.bat run` on Windows). Run tests with `./gradlew test` or `gradlew.bat test`.

## Commands

| Command | Format | Purpose |
| --- | --- | --- |
| `todo` | `todo <description>` | Adds a task without a date. |
| `deadline` | `deadline <description> /by <date>` | Adds a dated task. |
| `event` | `event <description> /from <start> /to <end>` | Adds an event range. |
| `list` | `list` | Displays all tasks. |
| `mark` | `mark <task number>` | Marks a task complete. |
| `unmark` | `unmark <task number>` | Marks a task incomplete. |
| `delete` | `delete <task number>` | Removes a task. |
| `find` | `find <keyword>` | Finds tasks containing a keyword. |
| `reminder` | `reminder` | Lists incomplete dated tasks chronologically. |
| `bye` | `bye` | Closes Baron. |

Task numbers are the one-based numbers shown by `list`.

## Examples

```text
todo read chapter 4
deadline submit report /by 2025-08-20 14:30
event team meeting /from 2025-08-21 10:00 /to 2025-08-21 11:00
```

Descriptions must not be empty or contain line breaks.

## Date and time formats

Accepted date formats include `yyyy-MM-dd`, `dd-MM-yyyy`, `yyyy/MM/dd`, `dd/MM/yyyy`, `MMM dd yyyy`, and `MM dd yyyy`. Append `HH:mm` or `HH:mm:ss` for a time; dates without a time default to midnight.

Dates are validated strictly, so nonexistent dates such as February 30 are rejected. An event’s start must be earlier than its end.

## Error handling and storage

Invalid commands, missing parameters, invalid task numbers, malformed dates, and invalid event ranges show an error without changing the task list. File read/write failures are also reported.

Tasks are stored as JSON in `data/tasks.json`, relative to the directory from which Baron is launched, and are saved after every add or update.
