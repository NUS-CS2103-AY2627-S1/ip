# Moon User Guide

Moon is a friendly task-management chatbot with a console interface and a JavaFX GUI. Use it to keep track of to-dos, deadlines, and events without leaving your command window.

![Moon GUI](Ui.png)

## Getting started

### GUI

1. Build the executable JAR by following the instructions in the project [README](https://github.com/zzzhiyuan-77/ip#building-the-executable-jar).
2. Copy `build/libs/moon.jar` into an empty folder.
3. Open a command window in that folder and run:

   ```powershell
   java -jar "moon.jar"
   ```

4. Type a command in the input box and press **Send** or **Enter**.

### Console

Run `Moon.main()` from your IDE. Type a command and press **Enter**. Type `bye` when you are finished.

Moon saves your tasks in `data/moon.txt`, so your list is available the next time you start the application. The data folder is created automatically.

## Commands

### Add a to-do

Use a to-do for a task without a date.

```text
todo <description>
```

Example:

```text
todo revise lecture notes
```

### Add a deadline

Use a deadline for a task that must be completed by a date. Dates use `yyyy-MM-dd` format.

```text
deadline <description> /by <date>
```

Example:

```text
deadline submit project report /by 2026-10-15
```

### Add an event

Use an event for something with a start and end date. The `/from` date must be earlier than the `/to` date.

```text
event <description> /from <start-date> /to <end-date>
```

Example:

```text
event software engineering workshop /from 2026-10-20 /to 2026-10-22
```

### View all tasks

```text
list
```

Moon displays each task with its type, completion status, and task number. For example:

```text
[T][ ] revise lecture notes
[D][X] submit project report (by: Oct 15 2026)
[E][ ] software engineering workshop (from: Oct 20 2026 to: Oct 22 2026)
```

### Find tasks

Search task descriptions for a keyword. The search ignores letter case.

```text
find <keyword>
```

Example:

```text
find report
```

### Mark or unmark a task

Use the task number shown by `list`.

```text
mark <task-number>
unmark <task-number>
```

Examples:

```text
mark 2
unmark 2
```

### Delete a task

Delete a task by its number.

```text
delete <task-number>
```

Example:

```text
delete 3
```

### Undo the most recent change

`undo` restores the task list to its state before the most recent task addition, deletion, marking, or unmarking command.

```text
undo
```

Only the most recent change can be undone. If there is no previous change, Moon will tell you.

### Exit Moon

```text
bye
```

## Helpful tips

- Extra spaces at the beginning, end, or between words are cleaned up automatically.
- Use one `/by`, `/from`, or `/to` parameter per command.
- Do not use `|` in a task description because it is reserved for Moon's saved-data format.
- If a command is invalid, Moon explains what is missing or how to correct it instead of closing.
- Use ISO dates such as `2026-10-15`; invalid dates and reversed event ranges are rejected.
