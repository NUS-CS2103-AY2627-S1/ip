# Mr Chatbot User Guide

Mr Chatbot helps you keep track of things to do, deadlines, events, and daily or weekly routines. Type a command and let it handle your task list.

## Get started

With Java 25 installed, open a terminal in the project folder and run `./gradlew run` (`gradlew.bat run` on Windows). The chat window opens when the build finishes.

Type in the box at the bottom, then press **Enter** or click **Send**. The shortcut buttons help you start a command. **daily task** and **recurring task** insert templates: replace the selected `<description>` with your own text before sending. Clicking a shortcut fills the input box; it does not add a task until you send the command. For a weekly template, you can append `/on monday` (or another weekday). Type `help` whenever you need a reminder.

## Add tasks

Each command below adds one incomplete task and confirms it in the chat. Replace the example descriptions and dates with your own.

| What you need | Example command |
| --- | --- |
| Something to do | `todo read a book` |
| A deadline | `deadline submit report /by 2026-10-05` |
| An event spanning dates | `event study trip /from 2026-10-05 /to 2026-10-07` |
| A daily routine | `recurring exercise /every day` |
| A weekly routine | `recurring project meeting /every week /on monday` |

Use real dates in `yyyy-mm-dd` format, with spaces around `/by`, `/from`, and `/to`. An event's end date must be **later than** its start date; same-day events are not supported. Descriptions cannot be empty. You can keep up to 100 tasks, including completed tasks.

## View and manage tasks

| Command | What happens |
| --- | --- |
| `list` | Shows all tasks with their current numbers. |
| `mark 2` | Marks task 2 as done. |
| `unmark 2` | Marks task 2 as incomplete again. |
| `delete 2` | Removes task 2. There is no undo. |
| `find book` | Finds descriptions containing “book”, ignoring letter case. |
| `help` | Shows command formats. |
| `bye` | Closes the app. |

Use task numbers from the latest **`list`**, starting at 1. Numbers change after deletion, and the numbers shown by `find` refer only to its search results, not the full list.

`[X]` means done and `[ ]` means incomplete. The letters identify the task type: `[T]` todo, `[D]` deadline, `[E]` event, and `[R]` recurring.

## How recurring tasks work

Daily tasks reset tomorrow. Weekly tasks reset on the next chosen weekday; use a full name from `monday` to `sunday`, in any letter case. If today is that weekday, the first reset is next week. Leave out `/on monday` to repeat on the weekday you create the task. Daily tasks do not accept `/on`.

Tasks start incomplete. Marking one done completes its current occurrence without changing its schedule. At startup and before each command, expired recurring tasks become incomplete again. Missed periods are skipped without creating extra tasks. The next reset date appears beside the task, using your computer's local date. An idle window refreshes when you send a command.

## Saving and problems

Changes save automatically to `data/mr-chatbot.txt` inside the folder you launch the app from. Start it from the same folder each time to see the same tasks. A missing save file starts an empty list.

Upgrading from an older version? Close the app and copy `data/duke.txt` to `data/mr-chatbot.txt` before starting the new version, but only if the new file does not already exist. Keep the old file as a backup; the app does not migrate it automatically.

If a command is rejected, read the message, correct its format or dates, and send it again. For a task-number error, run `list` first. If saving fails, check that the launch folder is writable; your latest changes may not survive closing the app. If loading fails, keep a backup of `data/mr-chatbot.txt` before editing it or adding new tasks.
