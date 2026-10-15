# Ubis User Guide

Ubis helps you keep track of tasks, deadlines, and events through a simple chat window.
Type a command, and Ubis will show the result and save your task changes automatically.

## Getting started

1. Install **Java 25** (JDK 25). Open a terminal or Command Prompt and run `java -version`
   to check that it reports version 25.
2. Download **`ubis.jar`** from the assets of a release on the
   [Ubis releases page](https://github.com/JellehBelleh/ip/releases).
3. Place `ubis.jar` in a folder where you want to keep Ubis and its task data.
4. Open a terminal or Command Prompt in that folder and run:

   ```text
   java -jar ubis.jar
   ```

5. In the chat window, type `todo read a book` and press **Enter** or click **Send**.
6. Type `list` to see your tasks. Type `help` whenever you need a reminder of the commands.

The JAR includes Ubis's application dependencies. You do not need Gradle or the project source code.

## Writing commands

- Use lowercase command words: `list` works; `LIST` does not.
- Replace values in `<angle brackets>` with your own text. Do not type the brackets.
- Task names can contain spaces, but `{` and `}` are reserved and cannot be used in commands.
- Enter dates as `YYYY-MM-DD`, such as `2026-09-30`. Dates must exist in the calendar; times are not supported.
- Separate `/by`, `/from`, and `/to` from the surrounding text with spaces.

## Features

### Adding tasks

| Task type | Format | Example |
| --- | --- | --- |
| Todo: a task without a date | `todo <name>` | `todo read a book` |
| Deadline: a task due on a date | `deadline <name> /by <date>` | `deadline submit report /by 2026-09-30` |
| Event: a task spanning dates | `event <name> /from <start date> /to <end date>` | `event school camp /from 2026-10-01 /to 2026-10-03` |

Ubis confirms the added task. For example, `todo read a book` produces:

```text
added: [T][ ] read a book
```

A deadline needs exactly one `/by`. An event needs exactly one `/from` followed by exactly one `/to`.
The event's start date must be **earlier than** its end date; same-day events are not supported.

### Viewing and finding tasks

- **`list`** shows all tasks in their current order, including completed tasks.
- **`find <keyword>`** shows tasks whose names contain that text. For example, `find book` matches
  both `read a book` and `buy bookshelf`. Search is **case-sensitive**: `book` does not match `Book`.
  You can also search for a phrase, such as `find read a book`.

Example list:

```text
1: [T][ ] read a book
2: [D][X] submit report (by: Sep 30 2026)
3: [E][ ] school camp (from: Oct 1 2026 to: Oct 3 2026)
```

`T`, `D`, and `E` mean todo, deadline, and event. `[X]` means done; `[ ]` means not done.

**Search results retain their numbers from the full list.** If `find camp` shows task `3`,
use `mark 3` or `delete 3` to act on that task.

### Completing and deleting tasks

| Action | Format | Example and result |
| --- | --- | --- |
| Mark as done | `mark <task number>` | `mark 1` changes task 1 to `[X]`. |
| Mark as not done | `unmark <task number>` | `unmark 1` changes task 1 back to `[ ]`. |
| Delete a task | `delete <task number>` | `delete 1` removes task 1 immediately. |

Use a positive whole number shown by `list` or `find`. After deletion, later tasks shift up by one number.
Run `list` again before acting on another task if you are unsure. There is no undo command for deletion.

### Reusing commands and reading replies

- With the input field focused, press **Up** to recall earlier commands, including failed ones.
- Press **Down** to move toward newer commands. Moving past the newest restores your unfinished draft.
- Edit a recalled command, then press **Enter** to submit it. The input clears after every submission.
- Command history lasts for the current session only.
- Resize the window to suit your screen. Scroll to read earlier replies; long new replies open at their beginning.

### Getting help and exiting

Use **`help`** to display command formats and examples. Use **`bye`** to close Ubis.
`help`, `list`, and `bye` take no extra arguments.

## Saved data and common problems

Ubis stores tasks in **`data/data.txt`**, relative to the folder from which it runs.
When launched with the steps above, this is a `data` folder beside `ubis.jar`. Tasks load when Ubis starts;
missing folders and files are created automatically. Keep launching from the same location to use the same data.

- **A command was rejected:** read the explanation, press Up, correct the command, and submit it again.
- **A task number is invalid:** use `list` to check the current numbers.
- **A save warning appears:** your change is available for this session but may be lost when Ubis closes.
  Check that the data folder is writable.
- **A startup warning reports unreadable or damaged data:** Ubis may start empty or skip invalid records.
  Copy the existing data file somewhere safe before changing tasks, because later saves can replace its contents.
