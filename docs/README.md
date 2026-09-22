# Mono User Guide

**Mono** is a JavaFX task manager for to-dos, deadlines, and events. You can
add, view, search, complete, reopen, and delete tasks. Mono saves changes
automatically in `data/tasks.txt`.

![Mono user interface](Ui.png)

## Getting started

Download the latest mono.jar from the [Mono releases page](https://github.com/zyangchuan/ip/releases).

Mono requires JDK 25 and a desktop environment that supports JavaFX. Check
your Java version before starting:

```text
java -version
```

Run the downloaded JAR from the folder where you want Mono to store its data:

```text
java -jar mono.jar
```

Mono creates and updates `data/tasks.txt` in that folder. Keep the JAR and its
`data` folder in a location where you have permission to read and write files.

Enter commands in the window's command field. Mono displays each command and
its result in the conversation. Invalid commands are rejected with an error
message; they do not end the session.

## Commands

| Command | Purpose | Example |
| --- | --- | --- |
| `todo <description>` | Add a task without a date | `todo read a book` |
| `deadline <description> /by <date>` | Add a task due on a date | `deadline submit report /by 2026-09-15` |
| `event <description> /from <date> /to <date>` | Add a task covering a date range | `event project retreat /from 2026-09-16 /to 2026-09-18` |
| `list` | Show all tasks and their IDs | `list` |
| `find <keyword>` | Search task descriptions | `find report` |
| `mark <id>` | Mark a task as done | `mark 1` |
| `unmark <id>` | Mark a task as not done | `unmark 1` |
| `delete <id>` | Delete a task | `delete 1` |
| `bye` | End the session | `bye` |

Commands and keywords are case-sensitive. Commands that do not take arguments,
such as `list` and `bye`, must be entered without additional text.

## Dates and task IDs

Enter dates in ISO format, `yyyy-MM-dd`, for example `2026-09-15`. Dates must
be real calendar dates, so `2026-02-30` is invalid.

Mono assigns one-based IDs according to the current list order:

```text
1.[T][ ] read a book
2.[D][ ] submit report (by: Sep 15 2026)
3.[E][ ] project retreat (from: Sep 16 2026 to: Sep 18 2026)
```

Use the ID shown by `list` with `mark`, `unmark`, or `delete`. An ID must be a
positive integer that exists in the current list.

## Task status and persistence

`[ ]` means a task is incomplete and `[X]` means it is complete. The `mark`
and `unmark` commands update both the displayed status and the saved task list.
Adding or deleting a task also saves immediately.

Task descriptions cannot contain the pipe character (`|`) or control
characters because Mono uses a pipe-delimited text file for persistence.

## Ending Mono

Enter `bye` to end the session. The command field and send button are disabled
after Mono says goodbye. Close the application window when you are ready.

JDK 25 and a desktop environment that supports JavaFX are required. To get
updates, download the newest `mono.jar` from the [releases page](https://github.com/zyangchuan/ip/releases).
