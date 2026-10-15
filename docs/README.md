# Olaf User Guide

Olaf is a friendly desktop chatbot that helps you track ToDos, deadlines, and events across sessions.

## Getting Started

You need **JDK 25** to run Olaf.

1. Open a terminal in the project root folder (the folder containing `build.gradle`).
1. Run `java --version` and check that it reports Java 25.
1. Launch Olaf using the command for your terminal:

   | Terminal | Command |
   |---|---|
   | Windows PowerShell | `.\gradlew.bat run` |
   | Windows Command Prompt | `gradlew.bat run` |
   | macOS, Linux, Git Bash, or WSL | `./gradlew run` |

   On macOS or Linux, if `gradlew` is not executable, run `chmod +x gradlew` and try again.
1. Type a command in the chat box, then press **Enter** or click **Send**.

## Commands

| What you want to do | Command | Example |
|---|---|---|
| Add a ToDo | `todo <description>` | `todo borrow book` |
| Add a deadline | `deadline <description> /by <yyyy-MM-dd>` | `deadline return book /by 2026-09-20` |
| Add an event | `event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>` | `event project meeting /from 2026-09-20 /to 2026-09-22` |
| Show all tasks | `list` | `list` |
| Find tasks by description | `find <keyword>` | `find book` |
| Mark a task as done | `mark <task number>` | `mark 2` |
| Mark a task as not done | `unmark <task number>` | `unmark 2` |
| Delete a task | `delete <task number>` | `delete 2` |
| Reschedule a deadline | `reschedule <task number> /by <yyyy-MM-dd>` | `reschedule 2 /by 2026-09-25` |
| Reschedule an event | `reschedule <task number> /from <yyyy-MM-dd> /to <yyyy-MM-dd>` | `reschedule 3 /from 2026-09-25 /to 2026-09-27` |
| Exit Olaf | `bye` | `bye` |

## Useful Notes

- Enter dates as `yyyy-MM-dd`, such as `2026-09-20`. Olaf displays them as `Sep 20 2026`.
- An event's end date must be the same as or later than its start date.
- Use the task numbers shown by `list` or `find`. Search is case-insensitive and keeps each task's number
  from the full list. After deleting a task, run `list` or `find` again because later tasks are renumbered.
- Only deadlines and events can be rescheduled; ToDos do not have dates.
- Commands and date markers such as `/by`, `/from`, and `/to` are case-insensitive.
- `[T]`, `[D]`, and `[E]` identify ToDos, deadlines, and events. `[ ]` means incomplete and `[X]`
  means complete.
- Olaf automatically saves successful changes to `data/olaf.txt` and loads them the next time it starts.
  Invalid commands or failed saves display an error without changing your tasks.

## AI Use Declaration

As my intended career path is NOT related to software engineering, I choose AI-5 as my AI use level, where I get Codex to do the tasks, and then I myself review the results.
