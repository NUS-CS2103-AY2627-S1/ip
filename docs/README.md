# Minty User Guide

Minty is your task-management chatbot for keeping track of todos, deadlines, and events.

## Getting started

Launch Minty with Java 25 using `./gradlew run` from the project folder
(see [setup instructions](../README.md#setting-up-in-intellij) if needed).
Type a command in the chat box, then press **Enter** or click **Send**.
Use lowercase command names and enter one command at a time.

## Add tasks

Replace the example descriptions and dates with your own:

| Task type | Example command |
| --- | --- |
| Todo: a task without a date | `todo read a book` |
| Deadline: a task with a due date | `deadline submit report /by 2026-09-25` |
| Event: a task spanning dates | `event study camp /from 2026-09-25 /to 2026-09-27` |

Use dates in `yyyy-MM-dd` format, with spaces around `/by`, `/from`, and `/to`.
An event may start and end on the same day, but cannot end before it starts.
Minty confirms each addition. Identical tasks are rejected, even if the existing task is complete.

## View and manage tasks

| Command | What it does |
| --- | --- |
| `list` | Shows all tasks and their numbers. |
| `mark 2` | Marks task 2 as complete. |
| `unmark 2` | Marks task 2 as incomplete. |
| `delete 2` | Removes task 2. |
| `find book` | Finds descriptions containing `book`; matching is case-sensitive. |
| `on 2026-09-25` | Shows deadlines due that day and events covering that day, including their start and end dates. |
| `bye` | Ends the chat; you can then close the window. |

Task labels are `[T]` for todos, `[D]` for deadlines, and `[E]` for events.
`[ ]` means incomplete; `[X]` means complete.

Replace `2` with the task's number from `list`. Numbers change after deletion.
**Run `list` before changing a task found with `find` or `on`: their result numbers
are separate from the full task list.** Both searches include completed tasks.

## Reminders

Enter `reminders` (or `remind`) with no arguments to see incomplete deadlines due
and events starting **today through the next six days**, using your computer's local date.
Results appear earliest first and keep their full-list task numbers.
Todos, overdue deadlines, and events that have already started are excluded.

Matching reminders also appear when Minty starts. There are no background alerts;
run the command again to refresh reminders after changing tasks.

## Saving and errors

Minty automatically saves task changes to `data/minty.txt` in the folder you launch it
from and loads them next time. No save command is needed.

If a command is rejected, follow Minty's message and try again.
If Minty cannot load saved tasks, saving is paused to protect the file: repair or move
`data/minty.txt`, then restart. Tasks added while saving is paused will not be saved.
