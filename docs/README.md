# YourHand User Guide

YourHand is a friendly desktop task manager that helps you keep track of everyday responsibilities, deadlines, and events. Type a command in the chat box and press **Enter** or click **Send**.

## Setup

YourHand requires **JDK 25**. Download or clone the project, then open the inner `ip` folder in IntelliJ IDEA or a terminal.

### IntelliJ IDEA

1. Open the project folder containing `build.gradle` in IntelliJ IDEA.
2. Set the project SDK and language level to JDK 25.
3. Run `yourhand.Launcher` from `src/main/java/yourhand/Launcher.java` to open the GUI.

### Desktop GUI from the terminal

Open a terminal in the project root (the folder containing `gradlew.bat`) and run:

```powershell
.\gradlew.bat run
```

This opens the YourHand desktop application. The window displays a welcome message and a chat area. Enter commands in the input box and press **Enter** or click **Send**; responses and task cards appear in the conversation.

### Optional command-line interface

YourHand also includes a terminal interface for users who prefer text-only interaction. From the project root, compile and run it with:

```powershell
javac -d _temp/classes src/main/java/yourhand/YourHand.java src/main/java/yourhand/tasks/*.java src/main/java/yourhand/exceptions/*.java src/main/java/yourhand/storage/*.java src/main/java/yourhand/ui/*.java src/main/java/yourhand/commands/*.java
java -cp _temp/classes yourhand.YourHand
```

In CLI mode, commands and responses are printed in the terminal. In both modes, tasks are saved automatically and `bye` exits the application.

For additional IntelliJ IDEA details, packaging instructions, and testing instructions, see the project [README](../README.md).

If you prefer to run the packaged application, build it with:

```powershell
.\gradlew.bat shadowJar
java -jar build\libs\yourhand.jar
```

The project README linked above is one level outside this `docs` folder and is included in the repository.

## Features

- Create to-dos, deadlines, and events.
- View all tasks in one organised list.
- Search for tasks by keyword.
- View deadlines and events scheduled for a particular date.
- Mark tasks as done or restore them when plans change.
- Delete tasks that are no longer relevant.
- Save tasks automatically between sessions.

## Quick start

1. Start YourHand as described in the project [README](../README.md).
2. Type `help` to see the available commands.
3. Add a few tasks using the commands below.
4. Type `list` whenever you want to review your responsibilities.
5. Type `bye` when you are finished.

Task numbers are assigned by `list` and start at 1. They may change after a task is deleted.

## Command reference

### Add a to-do

Use a to-do for something that does not have a specific date or time.

```text
todo review lecture notes
```

### Add a deadline

Use a deadline for something that must be completed by a particular date or time.

```text
deadline submit project report /by 2026-09-28 2359
```

### Add an event

Use an event for an activity with a start and end time.

```text
event team planning meeting /from 2026-09-16 1800 /to 2026-09-16 1930
```

### List tasks

Use `list` to show every saved task.

```text
list
```

Each task shows its type and status:

- `[T]` is a to-do.
- `[D]` is a deadline.
- `[E]` is an event.
- `[X]` means the task is completed; `[ ]` means it is still pending.

### Find tasks

Use `find KEYWORD` to search task descriptions. Searches are case-insensitive.

```text
find project
```

### View a schedule

Use `view schedule DATE` to show deadlines due on that date and events that occur on or overlap it. Results are displayed in chronological order.

```text
view schedule 2026-09-16
```

To-dos without dates are not included in a schedule.

### Mark a task as done

Use `mark TASK_NUMBER` after completing a task.

```text
mark 2
```

### Mark a task as pending again

Use `unmark TASK_NUMBER` if a completed task needs to be put back on your list.

```text
unmark 2
```

### Delete a task

Use `delete TASK_NUMBER` to remove a task permanently.

```text
delete 3
```

### Show help or exit

Use `help` to display the command guide, or `bye` to close YourHand.

```text
help
bye
```

## Dates and times

YourHand accepts the following formats. Zero-padding the month and day is optional:

- `yyyy-M-d`, for example `2026-09-16`
- `yyyy-M-d HHmm`, for example `2026-09-16 1800`
- `yyyy-M-d HH:mm`, for example `2026-09-16 18:00`
- `d/M/yyyy HHmm`, for example `16/9/2026 1800`

Dates and times must be valid. An event's end must be later than its start.

## Saving your tasks

YourHand saves tasks automatically in `data/yourhand.txt` and restores them the next time the application starts. Task descriptions cannot contain the `|` character because it is reserved for the saved-data format.

## Troubleshooting

YourHand stays open when a command is invalid and explains what needs to be corrected. Common fixes include:

- Include a description after `todo`, `deadline`, or `event`.
- Include a valid date after `/by`, `/from`, or `/to`.
- Use a whole-number task number for `mark`, `unmark`, and `delete`.
- Use one space between command parts and remove spaces at the beginning or end.

If a saved data file is malformed, YourHand starts with an empty task list rather than loading incomplete data.
