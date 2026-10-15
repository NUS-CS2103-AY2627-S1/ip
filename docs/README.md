# JassaBot User Guide

JassaBot helps you keep track of tasks, deadlines, and events through simple typed commands.
Add what you need to do, find it later, and mark it done as you make progress.

![JassaBot chat window](Ui.png)

## Quick start

1. Install **Java 25** and check that `java -version` reports version 25.
2. Download `jassabot.jar` from the [latest release](https://github.com/JackyJiangCS/ip/releases/latest).
   Choose the `.jar` file under **Assets** and place it in a folder where you want to keep your tasks.
3. Open a terminal in that folder and run:

   ```sh
   java -jar jassabot.jar
   ```

4. In the chat window, type `todo read book` and press **Enter** or click **Send**.
5. Try `list` to see your task, then `mark 1` to mark it done. Type `help` for a command reminder.

Prefer a terminal? Run `java -cp jassabot.jar jassabot.JassaBot` from the same folder.
All commands below work in both interfaces.

## Features

### Command basics

- Use lowercase command names, with spaces between the command and its arguments.
- Replace uppercase placeholders such as `DESCRIPTION` with your own text. Descriptions cannot be empty.
- `[TIME]` means an optional time; do not type the square brackets.
- Dates use `yyyy-MM-dd` (for example, `2026-09-21`) or `d/M/yyyy` (`21/9/2026`).
  Times use four digits in 24-hour `HHmm` format: `0930` or `1800`.
  Omitting a time means midnight; midnight is displayed as just the date.
- Keep `/by`, `/from`, and `/to` in the positions shown, with spaces around them.

### Add a task: `todo`

Use this for something without a date.

**Format:** `todo DESCRIPTION`

**Example:** `todo read book` adds `[T][ ] read book`.

### Add a deadline: `deadline`

Use this for something due on a particular date, optionally at a particular time.

**Format:** `deadline DESCRIPTION /by DATE [TIME]`

**Example:** `deadline return book /by 2026-09-21 1800` adds:

```text
[D][ ] return book (by: Sep 21 2026, 6:00 PM)
```

For a date without a time, try `deadline submit report /by 22/9/2026`.

### Add an event: `event`

Use this for an activity with a start and an end. Include a date for both, even on the same day.

**Format:** `event DESCRIPTION /from DATE [TIME] /to DATE [TIME]`

**Example:** `event project meeting /from 2026-09-21 1400 /to 2026-09-21 1600` adds:

```text
[E][ ] project meeting (from: Sep 21 2026, 2:00 PM to: Sep 21 2026, 4:00 PM)
```

### View all tasks: `list`

Type `list` to see all tasks, including completed ones, with their current numbers.
For example, after adding the book task, book deadline, and meeting above:

```text
1.[T][ ] read book
2.[D][ ] return book (by: Sep 21 2026, 6:00 PM)
3.[E][ ] project meeting (from: Sep 21 2026, 2:00 PM to: Sep 21 2026, 4:00 PM)
```

`[T]`, `[D]`, and `[E]` mean task, deadline, and event. `[ ]` means not done; `[X]` means done.

### Find tasks: `find`

**Format:** `find KEYWORD`

**Example:** `find book` finds both `read book` and `return book`.
Search checks descriptions, ignores letter case, and matches parts of words.
Multiple words are searched as one phrase: `find project meeting` looks for that whole phrase.
If nothing matches, try a shorter keyword.

> **Task numbers:** Search results have their own numbering. Always use `list` to get the
> current task number before using `mark`, `unmark`, or `delete`.

### Mark or unmark a task: `mark`, `unmark`

**Formats:** `mark NUMBER` and `unmark NUMBER`

Use a positive task number from `list`, starting at `1`.
For example, `mark 1` changes the first task to `[T][X] read book`.
Use `unmark 1` to change it back to `[T][ ] read book`. Completed tasks stay in your list.

### Delete a task: `delete`

**Format:** `delete NUMBER`

For example, `delete 2` removes the second task in `list` immediately.
There is no undo command. Run `list` again after deleting because task numbers may change.

### Get help: `help`

Type `help` to display the command reference in the conversation. In the chat window,
scroll up if needed to read the whole reply. Help works offline and leaves your tasks unchanged.

### Exit: `bye`

Type `bye` to exit. The chat window closes after a brief farewell; the console exits immediately.
Use `help`, `list`, and `bye` on their own, without extra arguments.

## Saving your tasks

JassaBot automatically saves each successful addition, deletion, or completion change to
`data/jassabot.txt`, relative to the folder you launched it from, and reloads tasks on startup.
There is no save command. Launch from the same folder each time, and run only one JassaBot
session at a time to avoid overwriting changes from another session.

To back up your tasks, close JassaBot and copy `data/jassabot.txt` somewhere safe.

## If something goes wrong

- **Command not recognised:** Check the spelling and lowercase command name, or type `help`.
- **Invalid task number:** Run `list` and choose a number shown there.
- **Invalid date or missing details:** Use a real calendar date in a supported format and
  include the description and required markers. For example, use `2026-09-21 1800`, not `tomorrow`.
- **Tasks missing after restarting:** Check that you launched from the same folder.
  If JassaBot reports unreadable or skipped data, back up the data file before changing tasks.
- **Tasks could not be saved:** The attempted change is cancelled. Check that the data folder
  and file are writable, then try the command again.
