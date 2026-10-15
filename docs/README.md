# DEVICE_G User Guide

DEVICE_G (display name Evil Roaring Group Chat) is a task manager with a chat interface. Give the bot a command to keep track of todos, deadlines, events, and tasks that unlock later.

![Full DEVICE_G window](Ui.png)

## Getting started

1. Install Java 25.
2. Download the latest `device-g.jar` from the project's GitHub release.
3. Open a terminal in the folder containing the JAR and run `java -jar device-g.jar`.
4. Type a command in the box at the bottom of the window and press **Enter** or **Send**.

Tasks are saved in `data/PERSIST.txt` relative to the folder where you run the app and loaded again on the next launch. If saving fails, the bot warns you; changes remain available for the current session. Fix file access or disk space and make another change to retry saving.

## Commands

Text in `<angle brackets>` is a value you supply. Task numbers come from `list` and start at 1. Dates accept `yyyy-M-d`, `yyyy/M/d`, `d-M-yyyy`, or `d/M/yyyy`; add a 24-hour time as `HH:mm` or `HHmm`. A date without a time means midnight.

| What you want to do | Command | Example |
| --- | --- | --- |
| Add a todo | `todo <description>` | `todo read chapter 3` |
| Add a deadline | `deadline <description> /by <date and optional time>` | `deadline submit report /by 2026-9-18 23:59` |
| Add an event | `event <description> /from <start> /to <end>` | `event study group /from 2026-9-17 14:00 /to 2026-9-17 16:00` |
| Show all tasks | `list` | `list` |
| Mark a task done | `mark <number>` | `mark 1` |
| Mark a task undone | `unmark <number>` | `unmark 1` |
| Delete a task | `delete <number>` | `delete 1` |
| Search descriptions | `find <keyword>` | `find report` |
| Show tasks on a date | `date <date>` | `date 2026-9-18` |
| Exit | `bye` | `bye` |

An event must start before it ends. `date` includes deadlines due that day and events that span that day. `find` ignores letter case and displays matching descriptions; run `list` before using a task number, since search results are numbered separately.

In the task list, `[T]` means todo, `[D]` deadline, `[E]` event, and `[A]` pending task. `[X]` means done, `[ ]` means undone, and `[L]` means a pending task is locked.

## Tasks that unlock later

Create a pending task with **one** of these conditions:

| Condition | Command | Example |
| --- | --- | --- |
| A date or time has arrived | `pending <description> /after <date and optional time>` | `pending collect parcel /after 2026-9-18 09:00` |
| Another task is done | `pending <description> /aftertask <task number>` | `pending send receipt /aftertask 1` |

Use a number from `list` for `/aftertask`. A locked pending task cannot be marked done until its condition is met. If you unmark a parent task, its dependent tasks are also unmarked; deleting a parent also deletes its dependent tasks.

## If a command is rejected

The bot shows an error for unknown commands, missing descriptions, invalid task numbers, impossible calendar dates, and events whose end is not after their start. If a deadline or time-based pending task has an unrecognized date format, the app may save the whole input as a plain todo instead. Check the bot's reply and use `list` to confirm what was saved.
