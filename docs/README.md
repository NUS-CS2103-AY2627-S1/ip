# Pip User Guide

**Small paws. Big plans.** Pip is your chipmunk task buddy: gather tasks, keep track
of deadlines and events, and chip away at your day.

## Quick start

1. Install Java 25 and open a terminal in the project folder.
2. Run `./gradlew run` on macOS/Linux or `./gradlew.bat run` on Windows.
3. In **Pip | Your task stash**, type `gather read a book` and press **Enter** or click **Send**.
4. Try `stash`, then `mark 1` to complete your first task.

You can resize the window; messages wrap and the input stays at the bottom.
Pip uses the commands below. Replace uppercase placeholders with your own text.

## Features

### Add a task: `todo`

**Format:** `todo DESCRIPTION`

**Example:** `todo read a book`

Pip adds an incomplete task and confirms the new task count:

```text
Acorn secured! I've added this task:
    [T][ ] read a book
Now you have 1 task(s) in your stash.
```

### Add a deadline: `deadline`

**Format:** `deadline DESCRIPTION /by YYYY-MM-DD`

**Example:** `deadline return book /by 2026-10-01`

Adds a task due on the given date, displayed as `Oct 01 2026`.
Use real dates in `yyyy-MM-dd` format; times and words such as `tomorrow` are not supported.

### Add an event: `event`

**Format:** `event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD`

**Example:** `event woodland picnic /from 2026-10-01 /to 2026-10-02`

Adds an event with start and end dates. Both markers are required, in either order.
The end cannot precede the start; same-day events are allowed.

### View your stash: `list`

**Example:** `list`

Displays every task in insertion order. `[T]` means todo, `[D]` deadline, and `[E]` event.
`[ ]` means incomplete; `[X]` means complete. An empty stash shows only the list heading.

### Find tasks: `find`

**Format:** `find KEYWORD`

**Example:** `find book`

Finds descriptions containing the supplied text, ignoring case. No matches means only
the search heading is shown. Searching does not change your tasks.

### Complete or reopen a task: `mark` / `unmark`

**Examples:** `mark 1`, `unmark 1`

Marks the first task complete or incomplete. Pip confirms the updated task and status.
Use a positive task number from the full `list` output. Search results have separate
numbering, so run `list` before changing a task found through search.

### Remove a task: `delete`

**Example:** `delete 1`

Removes the first task and confirms the remaining count. Later numbers shift after
deletion. There is no undo command, so check the number with `list` first.

### Say goodbye: `bye`

**Example:** `bye`

Pip replies, `Scampering off! See you next time, task buddy.` Close the GUI window to
exit. In console mode, this command also ends the program.

## Shortcuts and woodland aliases

Use these in place of the command word, with the same arguments:

| Command | Alternatives |
| --- | --- |
| `todo` | `t`, `gather` |
| `deadline` | `dl` |
| `event` | `e` |
| `list` | `l`, `stash` |
| `find` | `f`, `sniff` |
| `mark` / `unmark` | `m` / `um` |
| `delete` | `d` |
| `bye` | `b`, `scamper` |

For example, `gather read a book` works just like `todo read a book`.

## Saving and recovering your tasks

Pip automatically saves task changes to `data/Pip.txt` in the folder from which you
launch it. Always launch from the same
folder; copy the `data` folder too if you move the app.

Upgrading an older installation? Back up its task file, then rename it to `Pip.txt`
inside the `data` folder before starting Pip. Do not overwrite an existing stash.

| Problem | What to do |
| --- | --- |
| Blank input | Enter a command; Send is disabled while the input is blank. |
| Wrong command, missing details, invalid date or task number | Read the error panel, correct the command, and send it again. Pip stays open. |
| Data file missing | Pip starts with an empty stash and creates the folder and file on the next successful save. |
| Loading warning | Pip starts empty. Check or restore your data file before changing tasks. |
| Some saved tasks missing | Malformed records are skipped. Restore a backup if needed. |
| Save error | Keep Pip open: the change is in memory only. Fix the folder or write permissions, then run a task-changing command to save again. |

Back up your data file before editing it manually. To retry a save without adding a
task, you can mark an already completed task again. Do not close Pip while changes
are still unsaved.
