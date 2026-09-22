# Tony User Guide

Tony is a desktop task assistant that helps you organize to-dos, deadlines, and events through simple text commands.
Your tasks are saved automatically between sessions.

<img width="969" height="772" alt="Screenshot 2026-09-18 at 20 25 09" src="https://github.com/user-attachments/assets/95f07954-09ed-4832-a7b9-e6b21a4a631c" />


## Quick start

1. Install Java 25 if it is not already available on your computer.
2. Download `tony.jar` from the [latest release](https://github.com/Totonyny511/ip/releases/latest).
3. Open a terminal in the folder containing the downloaded JAR file.
4. Run `java -jar tony.jar`.
5. Type a command in the input box, then press <kbd>Enter</kbd> or select **Send**.

Use the **List**, **Help**, and **Bye** buttons for quick access to common actions.

To run Tony from its source code instead, open the project folder in a terminal and run `./gradlew run`.

## Command format

> **Command notation**
>
> - Words in `UPPER_CASE` are values you provide. For example, replace `DESCRIPTION` with `read a book`.
> - Items in square brackets are optional.
> - Enter dates in `yyyy-MM-dd` format, such as `2026-09-20`.
> - Command words should be entered in lowercase.

## Adding tasks

| Task type | Command format | Example |
| --- | --- | --- |
| To-do | `todo DESCRIPTION` | `todo read chapter 3` |
| Deadline | `deadline DESCRIPTION /by DATE` | `deadline submit report /by 2026-09-20` |
| Event | `event DESCRIPTION /from START_DATE /to END_DATE` | `event orientation /from 2026-09-21 /to 2026-09-22` |

### Date rules

- Use real calendar dates in `yyyy-MM-dd` format; for example, `2026-02-30` is rejected.
- A deadline may be today or a future date, but it cannot be in the past.
- An event's start and end dates cannot be in the past, and its end date must be later than its start date.

Tony will not add a duplicate task with the same type and details.

## Viewing and finding tasks

- Show every task: `list`
- Find tasks whose descriptions contain a keyword: `find KEYWORD`

For example, enter `find report` to find tasks containing "report". Searches are not case-sensitive.

## Updating tasks

Task numbers refer to the positions shown by `list`.

| Action | Command format | Example |
| --- | --- | --- |
| Mark as complete | `mark TASK_NUMBER` | `mark 2` |
| Mark as incomplete | `unmark TASK_NUMBER` | `unmark 2` |
| Delete tasks | `delete TASK_NUMBER [TASK_NUMBER ...]` | `delete 2 4` |

When deleting several tasks, the numbers may be entered in any order. If a number is invalid or repeated, Tony will
not delete any of the selected tasks.

## Understanding the task list

- `[T]` — To-do
- `[D]` — Deadline
- `[E]` — Event
- `[ ]` — Incomplete
- `[X]` — Complete

## Saving tasks

Tony saves the task list after every successful add, mark, unmark, or delete command. The data is stored in
`data/tony.txt`, relative to the folder from which Tony was started. Start Tony from the same folder each time to load
the same task list.

If Tony cannot read or write the data file, it displays a warning and keeps the current session usable. Changes made
after a write warning are available only until the application closes.

## Troubleshooting

- **A command is rejected:** Select **Help** and compare the command with the displayed format. Tony's error response
  also explains which value is missing or invalid.
- **A task number is rejected:** Run `list` and use the number shown beside the task. Task numbers can change after a
  deletion.
- **Saved tasks are missing:** Close Tony and start it again from the folder that contains the expected `data`
  directory.
- **The application does not start:** Run `java -version` and confirm that Java 25 is active before running the JAR
  again from a terminal.

## Ending the session

Enter `bye` or select **Bye** to end the conversation. Your latest task changes will already have been saved.
