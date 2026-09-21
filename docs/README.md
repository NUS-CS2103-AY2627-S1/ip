# Thai's Bot User Guide

![Thai's Bot screenshot](Ui.png)

**Thai's Bot** is a desktop chatbot for keeping track of your todos, deadlines and events. It has a playful academic personality. You type short commands into a chat box, and it keeps your tasks saved between sessions.

- [Quick start](#quick-start)
- [Features](#features)
- [Saving your data](#saving-your-data)
- [FAQ](#faq)
- [Known issues](#known-issues)
- [Command summary](#command-summary)

## Quick start

1. **Install Java 25.** Check your version by opening a terminal and running:

   ```
   java -version
   ```

   The first line should show version `25`.
   - **Mac users:**
     - Apple Silicon Macs (M1 or later): install the [Azul Zulu JDK 25 **JDK FX**](https://www.azul.com/downloads/?version=java-25-lts&package=jdk-fx#zulu) package, which includes JavaFX.
     - Intel Macs: a standard JDK 25 (e.g., [Temurin](https://adoptium.net/temurin/releases/?version=25)) should work.

2. **Download** the latest `duke.jar` from the [Releases page](https://github.com/ThaiNguyen0806/ip/releases).

3. **Move** `duke.jar` into an empty folder of your choice, e.g., `ThaisBot`. Your tasks will be saved inside this folder.

4. **Open a terminal in that folder** and run:

   ```
   java -jar duke.jar
   ```

   The Thai's Bot window should appear after a few seconds.

   > On Windows, you can open a terminal in the folder by typing `cmd` into the File Explorer address bar and pressing **Enter**.

5. **Type a command** into the box at the bottom and press **Enter** (or click **Send**). Try these:
   - `todo read book #school` adds a todo with the tag `school`
   - `list` shows all your tasks
   - `mark 1` marks the first task as done
   - `bye` closes the app

   You can also click the **Add task**, **Add deadline**, **Find task** and **Show list** buttons to fill the command box with an example, which you can then edit.

6. See [Features](#features) below for details of every command.

## Features

**Notes about the command format:**

- Words in `<angle brackets>` are values you supply.<br>
  e.g., in `todo <description>`, `<description>` can be `read book`, giving `todo read book`.
- Items in `[square brackets]` are optional.<br>
  e.g., `todo <description> [#tag ...]` can be used as `todo read book` or `todo read book #school`.
- Items followed by `...` can be repeated, e.g., `#school #revision`.
- Command words are lowercase: `list` works, but `LIST` does not.
- **Dates** use the format `yyyy-MM-dd`, e.g., `2026-09-18`.
- **Date-times** use `yyyy-MM-dd HHmm` (24-hour clock), e.g., `2026-09-18 2359`. `2026-09-18 23:59` is also accepted.
- Task descriptions cannot contain the `|` character.

### Adding a todo: `todo`

Adds a task that has no date.

Format: `todo <description> [#tag ...]`

Examples:
- `todo read book`
- `todo read book #school #revision`

Expected output:

```
Nice! I've tucked this one into the lineup:
  [T][ ] read book #school #revision
Now you have 1 tasks in the list.
```

### Adding a deadline: `deadline`

Adds a task that must be done by a certain date, or date and time.

Format: `deadline <description> /by <date or date-time> [#tag ...]`

Examples:
- `deadline submit lab report /by 2026-09-18`
- `deadline submit lab report /by 2026-09-18 2359 #school #lab`

Expected output:

```
Nice! I've tucked this one into the lineup:
  [D][ ] submit lab report (by: Sep 18 2026 2359) #school #lab
Now you have 2 tasks in the list.
```

### Adding an event: `event`

Adds a task that starts and ends at certain dates, or dates and times.

Format: `event <description> /from <start> /to <end> [#tag ...]`

- `/from` must come before `/to`.
- The end cannot be before the start.

Examples:
- `event project meeting /from 2026-09-15 1400 /to 2026-09-15 1600 #project`
- `event recess week /from 2026-09-19 /to 2026-09-27`

Expected output:

```
Nice! I've tucked this one into the lineup:
  [E][ ] project meeting (from: Sep 15 2026 1400 to: Sep 15 2026 1600) #project
Now you have 3 tasks in the list.
```

### Understanding the task list

Each task is shown in this form:

```
1.[D][X] submit lab report (by: Sep 18 2026 2359) #school
```

| Part | Meaning |
|------|---------|
| `1.` | Task number, used by `mark`, `unmark` and `delete` |
| `[T]` / `[D]` / `[E]` | Todo / Deadline / Event |
| `[X]` / `[ ]` | Done / Not done |
| `#school` | Tags |

### Listing all tasks: `list`

Shows every task, with its task number.

Format: `list`

### Finding tasks by keyword: `find`

Shows tasks whose description or tags contain the keyword.

Format: `find <keyword>`

- The search is **case-sensitive**: `find book` does not match `Book`.
- Partial words match: `find boo` matches `read book`.
- Searching by tag works with or without `#`, e.g., `find school` or `find #school`.
- Each result shows its number in the full list, so you can use it directly with `mark`, `unmark` or `delete`.

Examples:
- `find book`
- `find #school`

### Finding tasks on a date: `on`

Shows deadlines due on the given date, and events that are happening on that date (including multi-day events).

Format: `on <date>`

Each result shows its number in the full list, like `find`.

Example: `on 2026-09-15`

### Marking a task as done: `mark`

Format: `mark <task number>`

Example: `mark 2` marks the 2nd task in `list` as done.

### Marking a task as not done: `unmark`

Format: `unmark <task number>`

Example: `unmark 2`

### Deleting a task: `delete`

Deletes a task permanently. The numbers of the tasks after it will shift up by one, so use `list` to check the new numbers.

Format: `delete <task number>`

Example: `delete 3`

### Tagging tasks: `#tag`

Add tags to the end of a `todo`, `deadline` or `event` command to group related tasks.

- Tags must start with `#` and contain only letters and numbers, e.g., `#cs2103` works but `#cs-2103` does not.
- Tags must come after all other parts of the command.
- Duplicate tags are ignored: `#school #school` is saved as a single `#school`.
- Use `find #tag` to see all tasks with a tag.

### Exiting the app: `bye`

Format: `bye`

You can also close the window directly. Your tasks are already saved.

### Handling mistakes

If a command is not valid, Thai's Bot replies with an error message highlighted in a different color, often showing the correct format. Nothing is changed, so you can just fix the command and try again.

## Saving your data

- Tasks are saved automatically after every change. There is no need to save manually.
- The data file is `data/tasks.txt`, inside the folder you ran `java -jar duke.jar` from.
- If the data file becomes unreadable (e.g., after editing it by hand), Thai's Bot copies it to `data/tasks.txt.bak`, shows an error, and starts with an empty list. You can fix the backup file and copy it back to `data/tasks.txt` while the app is closed.

> **Caution:** Edit `data/tasks.txt` by hand only if you are confident. An invalid line makes the whole file unreadable at the next start-up.

## FAQ

**Q: How do I move my tasks to another computer?**<br>
A: Copy the `data` folder next to `duke.jar` on the new computer.

**Q: My tasks disappeared!**<br>
A: Make sure you ran `java -jar duke.jar` from the same folder as before. The data is saved relative to that folder. If you see a "corrupted" error, see [Saving your data](#saving-your-data).

**Q: The terminal shows `WARNING: Unsupported JavaFX configuration`. Is that a problem?**<br>
A: No. The app works normally and you can ignore this warning.

**Q: Double-clicking `duke.jar` does nothing.**<br>
A: Run it from a terminal with `java -jar duke.jar` instead, as described in the [Quick start](#quick-start).

## Known issues

- On Apple Silicon Macs, the app may not start with a standard JDK. Use the Azul Zulu **JDK FX** 25 package mentioned in the [Quick start](#quick-start).
- On some Intel Macs, the app may not start with the Azul Zulu **JDK FX** package. If this happens, use a standard JDK 25 (e.g., Temurin) instead.

## Command summary

| Action | Format | Example |
|--------|--------|---------|
| Add todo | `todo <description> [#tag ...]` | `todo read book #school` |
| Add deadline | `deadline <description> /by <date> [#tag ...]` | `deadline submit report /by 2026-09-18 2359 #school` |
| Add event | `event <description> /from <start> /to <end> [#tag ...]` | `event meeting /from 2026-09-15 1400 /to 2026-09-15 1600` |
| List | `list` | `list` |
| Find by keyword | `find <keyword>` | `find book` |
| Find by date | `on <date>` | `on 2026-09-15` |
| Mark done | `mark <task number>` | `mark 2` |
| Mark not done | `unmark <task number>` | `unmark 2` |
| Delete | `delete <task number>` | `delete 3` |
| Exit | `bye` | `bye` |
