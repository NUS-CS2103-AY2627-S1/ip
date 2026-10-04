# Shrek User Guide

Shrek is a friendly desktop task manager for todos, deadlines, and events.
Type commands into the chat window, then press **Enter** or click **Send**.
Tasks are saved automatically in `data/shrek.txt`.

## Getting started

1. Make sure Java `25` is installed on your computer.
2. Place `Shrek.jar` in a folder of your choice.
3. Open a new terminal window in that folder.
4. Run:

```bash
java -jar Shrek.jar
```

5. The Shrek GUI should open.
6. Enter a command in the command box and press **Enter** or click **Send**.
7. Refer to the command summary and features below for the available commands.

## Commands

| Command | What it does | Example |
| --- | --- | --- |
| `todo <description>` | Adds a task with no date | `todo read a book` |
| `deadline <description> /by <date>` | Adds a task due on a date | `deadline return book /by 2026-09-30` |
| `event <description> /from <time> /to <time>` | Adds a time-based event | `event meeting /from 2pm /to 3pm` |
| `list` | Shows all tasks | `list` |
| `mark <number>` | Marks a task as complete | `mark 1` |
| `unmark <number>` | Marks a task as incomplete | `unmark 1` |
| `delete <number>` | Removes a task | `delete 1` |
| `find <keyword>` | Finds matching descriptions or tags | `find book` |
| `tag <number> <tags>` | Adds one or more tags | `tag 1 #school #urgent` |
| `untag <number> <tags>` | Removes one or more tags | `untag 1 #urgent` |
| `bye` | Closes the chatbot | `bye` |

Task numbers are the one-based numbers shown by `list`.

## Adding tasks

Use a short description for a todo:

```text
todo prepare presentation
```

Deadlines require a date in `yyyy-mm-dd` format. Real calendar dates are
validated, so dates such as February 30 are rejected:

```text
deadline submit report /by 2026-09-30
```

Events accept time-only values such as `2pm`, `2:30pm`, and `14:00`, or ISO
local date-times such as `2026-09-13T14:00`:

```text
event team meeting /from 2026-09-13T14:00 /to 2026-09-13T15:30
```

The start and end must use the same format, and the end must be later than the
start. Use only one `/from`, `/to`, or `/by` marker in a command.

## Tags

Tags are optional and must appear at the end of a task-creation command. A tag
starts with `#` and contains 1–10 letters, digits, hyphens, or underscores.
Tags are case-insensitive, displayed in lowercase alphabetical order, and a
task can have at most three unique tags.

```text
todo revise notes #school #important
tag 1 #exam
untag 1 #important
find school
```

For example, `todo revise #school notes` is rejected because tags must be a
contiguous suffix. Duplicate, invalid, or excessive tags are rejected as one
operation, leaving the task unchanged.

## Helpful input rules

- Leading and trailing whitespace is ignored, and repeated spaces are treated
  as one separator.
- `list` and `bye` do not accept arguments.
- `mark`, `unmark`, and `delete` require exactly one valid task number.
- Adding the same task twice is rejected. The comparison uses task type,
  description, dates or times, and tags, and ignores completion status.
- Errors are displayed in the chat so that the original task list is not
  changed by an invalid command.

## Saving and loading

Tasks are saved after successful additions, updates, and deletions. Existing
untagged save records remain readable, and malformed records are skipped so
valid tasks can still be loaded.
