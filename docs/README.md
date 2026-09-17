# Lobby User Guide

Lobby is a task-management chatbot that helps you keep track of to-dos, deadlines, and events. Type a command, and Lobby will update your list for you.

## Getting started

1. Make sure Java 25 is installed. Check by running `java -version` in a terminal.
2. Put `Lobby.jar` in a folder where you want to keep your tasks. If you are starting from the source project, run `.\gradlew.bat shadowJar` on Windows or `./gradlew shadowJar` on macOS/Linux from the project folder; the JAR is created at `build/libs/Lobby.jar`.
3. Open a terminal in the folder containing the JAR and run `java -jar Lobby.jar`.
4. In the chat window, type `todo read book` and press **Enter** or click **Send**. Then type `list` to see your first task.

Enter one command at a time. Command words and markers such as `/by` are case-insensitive. In the formats below, replace text in `<angle brackets>` with your own details; do not type the brackets.

## Adding tasks

New tasks start as incomplete and appear at the end of your list.

| Task type | Format | Example |
| --- | --- | --- |
| To-do: a task without a date | `todo <description>` | `todo read book` |
| Deadline: a task due on a date | `deadline <description> /by <yyyy-MM-dd>` | `deadline return book /by 2026-09-30` |
| Event: a task with a start and end | `event <description> /from <start> /to <end>` | `event study group /from Mon 2pm /to Mon 4pm` |

Deadline dates must be valid dates in `yyyy-MM-dd` format. For example, `2026-09-30` is displayed as `Sep 30 2026`. Event start and end values are free text: Lobby keeps what you type and does not check their order or validity.

Include a description and all required values. Separate markers from other text with spaces, use each marker exactly once, and put `/from` before `/to`.

## Viewing your tasks

Type `list` to see all tasks, including completed ones. After adding the three examples above to an empty list, you will see:

```text
Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Sep 30 2026)
 3.[E][ ] study group (from: Mon 2pm to: Mon 4pm)
```

`[T]`, `[D]`, and `[E]` mean to-do, deadline, and event. `[ ]` means incomplete; `[X]` means completed.

## Completing or removing tasks

Use a task number from the latest `list`, starting at 1.

| Action | Format | Example and result |
| --- | --- | --- |
| Mark completed | `mark <number>` | `mark 1` changes task 1 to `[X]`. |
| Mark incomplete | `unmark <number>` | `unmark 1` changes task 1 back to `[ ]`. |
| Delete | `delete <number>` | `delete 1` removes task 1 and shows the remaining task count. |

Deletion is immediate and has no undo command. Remaining tasks are renumbered, so use `list` again before changing another task.

## Finding tasks

Use `find <keywords>`, for example `find book` or `find LAB mark`.

Every keyword must appear in the description, ignoring case and word order. Partial words match too: `find LAB mark` matches `Finish marking lab reports`. Both completed and incomplete tasks are searched; dates and event times are not. If nothing matches, Lobby replies `No matching tasks found.`

**Search results are numbered from 1 within the results.** Run `list` to get the full-list task number before using `mark`, `unmark`, or `delete`.

## Saving and leaving

Lobby automatically saves every successful task change to `data/lobby.txt`, relative to the folder you launched it from, and loads those tasks the next time you start it. Always launch from the same folder to use the same list. You can back up your tasks by copying this file while Lobby is closed.

Type `bye` to end the chat, then close the window. To enter more commands, reopen Lobby. In console mode, `bye` exits the program. Both `list` and `bye` take no extra arguments.

## If something goes wrong

- **Command error:** Follow Lobby's message and try again. Recognized commands with invalid details remain in the input box for editing.
- **Saving error:** The attempted task change is undone. Check that the save file and its folder are writable, then retry.
- **Loading warning:** Some saved tasks could not be read. Keep the original file for recovery. Before the next successful save, Lobby preserves it beside the save file as `lobby.txt.recovery-<unique number>.bak`; keep this backup if tasks are missing.
