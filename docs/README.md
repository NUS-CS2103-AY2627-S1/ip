# Genie User Guide

> **Genie** is your friendly desktop task assistant. Use simple commands to
> keep track of todos, deadlines, and events in one place.

Genie saves your task list automatically, displays the conversation in a
chat style JavaFX interface, and highlights error messages so they are easy to
spot.

## Showcase

![Genie user interface](Ui.png)

## Getting started

### Requirements

- Java Development Kit (JDK) 25
- A desktop environment that supports JavaFX

Download `genie.jar` from the project's GitHub Releases page. Place it in
your preferred folder and launch Genie with:

```text
java -jar genie.jar
```

The application opens with a greeting from Genie. Type a command into the
input field and press **Enter** or click **Send**.

> **Tip:** Use `list` first if you need to see the task numbers required by
> commands such as `mark`, `edit`, and `delete`.

## Command overview

| Command                                       | What it does                                           |
| --------------------------------------------- | ------------------------------------------------------ |
| `todo <description>`                          | Adds a task without a date or time.                    |
| `deadline <description> /by <date and time>`  | Adds a task with a deadline.                           |
| `event <description> /from <start> /to <end>` | Adds an event with a start and end time.               |
| `list`                                        | Displays every task and its list number.               |
| `mark <number>`                               | Marks a task as completed.                             |
| `unmark <number>`                             | Marks a task as incomplete.                            |
| `edit <number> <field> <new value>`           | Changes one detail without deleting the task.          |
| `find <keyword>`                              | Displays tasks whose descriptions contain the keyword. |
| `delete <number>`                             | Removes a task from the list.                          |
| `bye`                                         | Displays a farewell and closes the application.        |

Command names are not case-sensitive. For example, `LIST` and `list` work in
the same way.

## Adding tasks

### Todos

Use a todo for something that does not have a specific date or time:

```text
todo buy groceries
```

### Deadlines

Use a deadline for a task that must be completed by a particular time:

```text
deadline submit project report /by 30/9/2026 1800
```

### Events

Use an event when you need to record both a start and an end time:

```text
event team meeting /from 1/10/2026 1400 /to 1/10/2026 1530
```

Dates and times must use this format:

```text
d/M/yyyy HHmm
```

For example, `30/9/2026 1800` means **30 September 2026 at 6:00 PM**.
The time uses the 24-hour clock and does not contain a colon.

Deadlines and events cannot be scheduled in the past. An event's start
time must also be earlier than its end time. Calendar dates must exist;
for example, `31/9/2026` is not valid.

## Viewing and managing tasks

### View the task list

```text
list
```

Genie displays each task with a number. Use that number for later commands:

```text
Here are your tasks:
1.[T][ ] buy groceries
2.[D][ ] submit project report (by: Sep 30 2026, 6:00 PM)
```

### Mark tasks as done or undone

```text
mark 1
unmark 1
```

Completed tasks are shown with an `[X]` status marker. Incomplete tasks use
`[ ]`.

### Edit a task

The `edit` command changes one task detail while keeping the task itself:

```text
edit <task number> <field> <new value>
```

| Task type                | Editable field | Example                                     |
| ------------------------ | -------------- | ------------------------------------------- |
| Todo, deadline, or event | `description`  | `edit 1 description buy groceries and milk` |
| Deadline                 | `by`           | `edit 2 by 1/10/2026 1800`                  |
| Event                    | `from`         | `edit 3 from 1/10/2026 1430`                |
| Event                    | `to`           | `edit 3 to 1/10/2026 1600`                  |

Only fields appropriate for the task type can be edited. For example, a todo
does not have a `by` field.

### Find tasks

Search task descriptions with a keyword:

```text
find meeting
```

The search matches text in the task description. The keyword is case-sensitive,
so `meeting` and `Meeting` may produce different results.

### Delete tasks

Use the number shown by `list`:

```text
delete 2
```

Deletion is permanent for the saved task list, so check the task number before
confirming the command.

## A typical session

The following sequence demonstrates a small workflow:

```text
todo prepare presentation
deadline submit slides /by 5/10/2026 2359
event presentation rehearsal /from 4/10/2026 1000 /to 4/10/2026 1100
list
mark 1
edit 2 by 6/10/2026 1800
find presentation
```

Genie shows the user's command on the right and its response on the left. New
messages are added to the conversation automatically, and the chat can be
scrolled when it becomes long.

## GUI features

- **Resizable window:** Resize the window to give yourself more space.
- **Conversation history:** Scroll through earlier commands and responses.
- **Profile pictures:** User and Genie messages have distinct profile images.
- **Error highlighting:** Invalid commands and input formats appear in a
  different error style.
- **Automatic saving:** Changes are saved to `data/genie.txt` after tasks are
  added, edited, marked, or deleted.

## Errors and troubleshooting

If Genie does not recognize a command, it displays an error response. Check
that:

1. The command keyword is spelled correctly.
2. You supplied a valid task number from `list`.
3. Dates use `d/M/yyyy HHmm`.
4. Deadlines use `/by`, while events use both `/from` and `/to`.

For example, this is valid:

```text
deadline renew passport /by 12/11/2026 0900
```

This is invalid because the time contains a colon:

```text
deadline renew passport /by 12/11/2026 09:00
```

To run the automated checks after making code changes:

```bash
./gradlew test
```

To compile, run tests, and perform the configured quality checks:

```bash
./gradlew build
```

## Ending a session

When you are finished, type:

```text
bye
```

Genie displays a farewell message and closes the window shortly afterwards.
