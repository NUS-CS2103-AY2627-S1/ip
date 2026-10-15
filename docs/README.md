# Cooper User Guide

Cooper is your task co-pilot: keep track of todos, deadlines, events, and short notes by typing commands
in a chat window or terminal. Changes are saved automatically.

- [Quick start](#quick-start)
- [Features](#features)
- [Notes](#notes)
- [Saving and recovery](#saving-and-recovery)
- [Command summary](#command-summary)

## Quick start

1. Install **Java 25** and place `cooper.jar` in a folder where you can save files
   (Mac users should use the specific Azul JDK 25 distribution recommended).
2. Open a terminal in that folder and run:

   ```shell
   java -jar cooper.jar
   ```

3. Type `todo read book` in the input box and press **Enter** or click **Send**.
4. Enter `list`, then `mark 1` to complete your first task. Enter `bye` to exit.

**Building from source:** in the project folder, run `./gradlew shadowJar`
(Windows: `.\gradlew.bat shadowJar`). The JAR is created at `build/libs/cooper.jar`.

**Terminal interface:** run `java -cp cooper.jar cooper.Cooper` from the folder containing the JAR.
The same commands work; end-of-input also exits cleanly.

## Features

### Command format

- Replace uppercase placeholders such as `DESCRIPTION` and `NUMBER` with your own values.
- Enter one command per line. Command words ignore case; extra spaces or tabs between components are accepted.
- Descriptions and note text must not be blank. Internal spacing is preserved; quotes are literal characters.
- Numbers start at **1** and must refer to an existing item. Deleting an item renumbers those after it.
- `list`, `note list`, and `bye` take no extra arguments. Duplicate tasks and notes are allowed.

### Adding a todo: `todo`

Adds a task without a date.

**Format:** `todo DESCRIPTION`

**Example:** `todo read book`

### Adding a deadline: `deadline`

Adds a task with a due date.

**Format:** `deadline DESCRIPTION /by DATE [TIME]`

**Example:** `deadline submit report /by 2026-10-01 23:59`

**Date rules:** use `yyyy-MM-dd` or `dd-MM-yyyy`; slash separators also work, such as `01/10/2026`.
`[TIME]` means an optional 24-hour `HH:mm` value; do not type the brackets. Without a time, midnight is used.
An event must end strictly after it starts. Past dates are allowed; impossible dates such as February 30 are rejected.
Use lowercase `/by` exactly once in the order shown, separated from values by spaces.
These delimiter tokens are reserved in dated commands.

### Adding an event: `event`

Adds a task with a start and end time.

**Format:** `event DESCRIPTION /from DATE [TIME] /to DATE [TIME]`

**Example:** `event team meeting /from 2026-10-02 14:00 /to 2026-10-02 16:00`

**Date rules:** use `yyyy-MM-dd` or `dd-MM-yyyy`; slash separators also work, such as `01/10/2026`.
`[TIME]` means an optional 24-hour `HH:mm` value; do not type the brackets. Without a time, midnight is used.
An event must end strictly after it starts. Past dates are allowed; impossible dates such as February 30 are rejected.
Use lowercase `/from`, and `/to` exactly once in the order shown, separated from values by spaces.
These delimiter tokens are reserved in dated commands.

### Viewing tasks: `list`

**Format:** `list`

Shows all tasks in their current order. For example:

```text
Here's your flight plan:
1.[T][ ] read book
2.[D][X] submit report (by: Oct 01 2026 23:59)
```

`[T]` means todo, `[D]` deadline, and `[E]` event. `[ ]` means unfinished; `[X]` means done.

### Updating completion: `mark` and `unmark`

**Formats:** `mark NUMBER` and `unmark NUMBER`

**Examples:** `mark 1` completes task 1; `unmark 1` makes it unfinished again.

Use the number from the latest full `list`.

### Finding tasks: `find`

**Format:** `find KEYWORD`

**Example:** `find book` finds descriptions containing `book`, including `books`.

Task search is **case-sensitive**. Multiple words form one literal phrase; there are no wildcards.
No matches produces `No matching tasks found!`.

> **Important:** Task search results are renumbered from 1. Run `list` again before using `mark`, `unmark`,
> or `delete`: those commands always use the full task list's numbers, not search-result positions.

### Deleting a task: `delete`

**Format:** `delete NUMBER`

**Example:** `delete 2` removes task 2 from the full list.

Deletion is immediate, with no confirmation or undo. Task descriptions and dates cannot be edited directly;
add a replacement task and delete the old one.

## Notes

Notes store single-line snippets, such as a size or a movie to remember. They are separate from tasks and have
no completion status or dates. Task commands never modify notes.

| Action | Format | Example |
| --- | --- | --- |
| Add | `note add TEXT` | `note add Watch Arrival` |
| List | `note list` | `note list` |
| Find | `note find KEYWORD` | `note find ARRIVAL` |
| Replace text | `note edit NUMBER TEXT` | `note edit 1 Watch Dune` |
| Delete | `note delete NUMBER` | `note delete 1` |

Note search is **case-insensitive** and matches a literal phrase anywhere in the text. Results retain the original
note numbers, which you can use directly for editing or deletion. Editing replaces the whole text without moving
the note. Deletion is immediate, has no undo, and renumbers later notes.

For example, after `note add Watch Arrival`, `note find ARRIVAL` returns:

```text
Here are the matching notes:
1.[N] Watch Arrival
```

### Exiting: `bye`

**Format:** `bye`

Cooper signs off and closes. In the GUI, the farewell appears briefly before the window closes.

## Saving and recovery

Tasks and notes are saved after each successful change in `data/cooper.txt` and `data/notes.txt`, relative to the
**folder you launched Cooper from**. Always launch from the same folder to use the same data. Missing files start
empty. To back up or transfer your data, close Cooper and copy the entire `data` folder.

| Problem | What to do |
| --- | --- |
| Invalid command or number | Follow the suggested format; use `list` or `note list` to check numbers. Errors leave data unchanged. |
| Cannot load tasks or notes | Close Cooper, back up the affected file, then restore a valid backup or correct the file and restart. The affected collection stays unavailable until restart; the other works if its file is valid. |
| Cannot save | Check folder permissions and available disk space. Use a local writable folder if necessary. The failed change is not applied; retry after fixing the cause. |
| Tasks appear missing | Check that you launched Cooper from the folder containing your usual `data` directory. |

Prefer commands over manual file edits. Older saved events whose end precedes or equals their start must be corrected
before loading. Avoid running multiple instances against the same files. Tasks containing a pipe character use a newer
storage format that older Cooper versions cannot read.

## Command summary

| Purpose | Command |
| --- | --- |
| Add todo | `todo DESCRIPTION` |
| Add deadline | `deadline DESCRIPTION /by DATE [TIME]` |
| Add event | `event DESCRIPTION /from DATE [TIME] /to DATE [TIME]` |
| List / search tasks | `list` / `find KEYWORD` |
| Complete / reopen task | `mark NUMBER` / `unmark NUMBER` |
| Delete task | `delete NUMBER` |
| Add / edit note | `note add TEXT` / `note edit NUMBER TEXT` |
| List / search notes | `note list` / `note find KEYWORD` |
| Delete note | `note delete NUMBER` |
| Exit | `bye` |
