# PotatoBot User Guide

PotatoBot is your trusty spud assistant for keeping track of tasks, deadlines, and events. Type a short command into the chat to organize your day, find a task, or undo a mistake.

- [Quick start](#quick-start)
- [Features](#features)
- [Saving your tasks](#saving-your-tasks)
- [Troubleshooting](#troubleshooting)
- [Command summary](#command-summary)

<details>
<summary>See PotatoBot in action</summary>

<img src="Ui.png" alt="PotatoBot showing task creation, completion, undo, search, and an error reply" width="540">

</details>

## Quick start

1. Install **Java 25**. Run `java -version` in a terminal to check your version.
2. Place `potatobot.jar` in a folder where you want to keep your tasks. Building from source? Follow the JAR build instructions below first.
3. Open a terminal in that folder and run:

   ```shell
   java -jar potatobot.jar
   ```

4. PotatoBot opens with a greeting. Type `todo Prepare slides` and press **Enter** or click **Send**.
5. Try `list` to see your task, then `mark 1` when you finish it. Use `bye` to save and exit.

Scroll through the conversation to revisit earlier replies.

### Building the JAR from source

Skip this step if you already have `potatobot.jar`.

1. Download or clone the PotatoBot source code and open a terminal in the project folder (the one containing `build.gradle`).
2. With Java 25 installed, build the application using the command for your operating system. The first build needs an internet connection to download dependencies.

   **macOS / Linux:**

   ```shell
   ./gradlew shadowJar
   ```

   **Windows:**

   ```powershell
   .\gradlew.bat shadowJar
   ```

3. Find the generated `potatobot.jar` in `build/libs/`. It includes the application's dependencies. Copy it to your chosen task folder, then continue with the quick-start steps above.

## Features

### Command basics

- Replace placeholders such as `DESCRIPTION`, `DATE`, and `NUMBER` with your own values.
- Use lowercase command words and the spacing shown. Descriptions may contain spaces.
- Enter one command at a time. `list`, `undo`, and `bye` take no extra arguments.
- Enter dates as `YYYY-MM-DD`, for example `2026-09-30`. Times of day are not supported.
- Your potato sack holds **100 tasks**, including completed tasks.

### Add a task: `todo` or `add`

Use `todo DESCRIPTION` for an undated to-do, or `add DESCRIPTION` for a plain task without a type label. Both start incomplete.

Examples:

- `todo Prepare slides` → `Added: Prepare slides (Todo)`
- `add Buy potatoes` → `Added: Buy potatoes`

### Add a deadline: `deadline`

Record a task that is due on a particular date.

Format: `deadline DESCRIPTION /by DATE`

Example: `deadline Submit report /by 2026-09-30`

Reply: `Added: Submit report (Deadline, by: Sep 30 2026)`

### Add an event: `event`

Record an event with start and end dates. The end may be the same day as the start, but cannot be earlier. Keep `/from` before `/to`.

Format: `event DESCRIPTION /from START_DATE /to END_DATE`

Example: `event Team demo /from 2026-09-28 /to 2026-09-29`

Reply: `Added: Team demo (Event, from: Sep 28 2026 to: Sep 29 2026)`

### View your tasks: `list`

Shows all tasks in their current order. `[ ]` means incomplete; `[X]` means completed.

Format: `list`

Example result:

```text
Here are the tasks in your potato sack:
  1.[X] Prepare slides (Todo)
  2.[ ] Submit report (Deadline, by: Sep 30 2026)
```

An empty list displays `Nothing to see here...`.

### Complete or reopen a task: `mark` and `unmark`

Formats: `mark NUMBER` and `unmark NUMBER`

- `mark 1` marks the first task completed.
- `unmark 1` marks it incomplete again.

Use a positive whole number from the latest full `list`. Completed tasks remain until you delete them.

### Find a task: `find`

Searches descriptions, ignoring capitalization. Partial text matches too: `find port` matches “Submit report”. Multiple words form one phrase; dates and type labels are not searched.

Format: `find KEYWORD_OR_PHRASE`

Example: `find REPORT` shows tasks containing “report”. No matches displays `Nothing to see here...`.

**Run `list` before marking, unmarking, or deleting a search result.** Search results are numbered separately, but those commands always use positions in the full task list.

### Delete a task: `delete`

Removes a task and renumbers the remaining tasks.

Format: `delete NUMBER`

Example: run `list`, then `delete 2` to remove its second task. Deleted the wrong one? Enter `undo` immediately.

### Undo a change: `undo`

Reverses up to **five successful task-changing commands** in the current session, newest first.

Format: `undo`

- Adding is reversed by removing the task; deleting is reversed by restoring its original position.
- Undoing `mark` makes a task incomplete; undoing `unmark` makes it completed. Avoid repeating either command on a task already in that state if you intend to undo it.
- `list`, `find`, and rejected commands do not use an undo slot.
- Undo history disappears when you close the app. There is no redo.

Example: `delete 2`, followed by `undo`, restores the deleted task.

## Saving your tasks

Enter `bye` to save and exit. Closing the GUI window also saves. Changes are not saved after each command, so close normally before shuttinag down your computer.

By default, tasks are stored in `data/potatabot.txt` under the folder you launched PotatoBot from. Always launch from the same folder. Chat messages and undo history are not saved.

To back up or move your tasks, close PotatoBot and copy this file. Place it in the same relative location on the other computer before starting the app.

## Troubleshooting

- **“Me no gets?”** Check spelling, lowercase command words, required arguments, and spaces around date markers. Commands such as `hello` are not supported.
- **Task number rejected?** Run `list` and use a whole number from `1` to the number of tasks shown.
- **Date rejected?** Use a real date in `YYYY-MM-DD` format and ensure the event ends on or after its start date.
- **Sack full?** Delete unwanted tasks; completing them does not free space.
- **Saved tasks missing?** Check your launch folder. If a loading error appears, back up the save file before exiting: PotatoBot starts with an empty list and saving can replace the old file. Restore a valid backup before restarting.

Errors appear in red reply bubbles. Correct the command and try again.

## Command summary

| Action               | Command                                           |
| -------------------- | ------------------------------------------------- |
| Add a plain task     | `add DESCRIPTION`                                 |
| Add a to-do          | `todo DESCRIPTION`                                |
| Add a deadline       | `deadline DESCRIPTION /by DATE`                   |
| Add an event         | `event DESCRIPTION /from START_DATE /to END_DATE` |
| View all tasks       | `list`                                            |
| Complete a task      | `mark NUMBER`                                     |
| Reopen a task        | `unmark NUMBER`                                   |
| Search descriptions  | `find KEYWORD_OR_PHRASE`                          |
| Delete a task        | `delete NUMBER`                                   |
| Undo the last change | `undo`                                            |
| Save and exit        | `bye`                                             |
