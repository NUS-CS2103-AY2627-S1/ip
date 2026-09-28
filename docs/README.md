# Kotha User Guide

Kotha is a friendly task manager with a royal-or-rude personality. Use it to record tasks, deadlines, events, and tasks that depend on another task or date.

## Getting started

### Requirements

- Java Development Kit (JDK) 25
- IntelliJ IDEA, or a terminal with Gradle available

### Running Kotha

1. Open the project in IntelliJ IDEA.
2. Set the project SDK and language level to JDK 25.
3. Run `kotha.ui.Launcher` to start the graphical application.

Alternatively, from the project directory run:

```bash
./gradlew run
```

On Windows, use `gradlew.bat run`.

Kotha loads saved tasks from `data/kotha.txt` when it starts and saves changes automatically.

## Commands

Enter a command in the chat box and press Enter. Task numbers are one-based: the first task is task `1`.

| Command | What it does | Example |
| --- | --- | --- |
| `list` | Displays all saved tasks. | `list` |
| `find KEYWORD` | Finds tasks whose descriptions contain the keyword, ignoring letter case. | `find report` |
| `todo DESCRIPTION` | Adds a task without a date. | `todo read chapter 1` |
| `deadline DESCRIPTION /by DATE` | Adds a task with a deadline. | `deadline submit report /by 16/05/2027 1159` |
| `event DESCRIPTION /from DATE /to DATE` | Adds a task that occurs between two times. | `event team meeting /from 16/05/2027 0900 /to 16/05/2027 1000` |
| `constraint DESCRIPTION /after TRIGGER` | Adds a task that is associated with a text trigger. | `constraint return book /after exam` |
| `constraint DESCRIPTION /afterdate DATE` | Adds a task associated with a date trigger. | `constraint call doctor /afterdate 16/05/2027 0830` |
| `mark NUMBER` | Marks a task as completed. | `mark 1` |
| `unmark NUMBER` | Marks a completed task as incomplete again. | `unmark 1` |
| `delete NUMBER` | Removes a task permanently from the list. | `delete 1` |
| `bye` | Closes the conversation. | `bye` |

Commands must be written in lowercase. Extra spaces at the beginning or end are ignored.

## Task types

Choose the task type based on the kind of information you want to remember:

### To-do

A to-do is a simple task with no date attached. Use it for an action that you want to keep in your task list, but that does not need a deadline or scheduled time.

```text
todo read chapter 1
```

The task is stored as `[T]` and remains in the list until you mark or delete it.

### Deadline

A deadline is a task that must be completed by a particular date or time. Use it when the important information is the latest acceptable completion time, rather than an appointment that takes place over a period.

```text
deadline submit report /by 16/05/2027 1159
```

The task is stored as `[D]` and displays its deadline. If you omit the time, Kotha assumes `23:59` on that date.

### Event

An event represents something scheduled between a start and an end time. Use it for meetings, appointments, classes, or other activities that occupy a time range.

```text
event team meeting /from 16/05/2027 0900 /to 16/05/2027 1000
```

The task is stored as `[E]` and displays both times. The `/to` time must be later than the `/from` time.

### Constraint

A constraint is a task with a condition or prerequisite written alongside it. It is useful when a task makes sense only after another task, activity, or date. For example, returning a library book may be relevant only after an exam, or calling someone may be relevant only after a particular date.

Use `/after` when the prerequisite is a piece of text:

```text
constraint return book /after exam
```

This means: “Remember `return book`, with `exam` as the condition or context that comes before it.” The text after `/after` is stored as the task's trigger and can be any meaningful description, such as `finish project`, `team meeting`, or `salary received`.

Use `/afterdate` when the prerequisite is a date or date-time:

```text
constraint call doctor /afterdate 16/05/2027 0830
```

This means: “Remember `call doctor` after 16 May 2027 at 08:30.” If no time is supplied, the trigger date uses `23:59`.

Constraints are stored as `[C]` and show their trigger in the task list. They are reference information: Kotha does not automatically wait for the trigger, send a notification, or prevent you from marking the task as done. You should use `mark` when you have completed the task and `unmark` if it needs to be reopened.

## Entering dates and times

Dates can be entered in any of these formats:

- `d/M`, such as `16/5`
- `d/M/yy`, such as `16/05/27`
- `d/M/yyyy`, such as `16/05/2027`
- Any of the above followed by a 24-hour time in `HHmm` format, such as `16/05/2027 1430`

If no time is supplied, Kotha uses `23:59` for that date. For an event, the `/to` time must be later than the `/from` time.

## Understanding the task list

- `[ ]` means the task is incomplete.
- `[X]` means the task is complete.
- `[T]` identifies a to-do.
- `[D]` identifies a deadline.
- `[E]` identifies an event.
- `[C]` identifies a constraint.

For example:

```text
1. [T][ ] read chapter 1
2. [D][X] submit report (by: May 16 2027 11:59pm)
3. [E][ ] team meeting (from: May 16 2027 09:00am to: May 16 2027 10:00am)
```

## Errors and tips

Kotha explains what went wrong when a command is incomplete or invalid. Check that:

- A task has a description after its command word.
- Deadlines include `/by` and events include both `/from` and `/to`.
- `mark`, `unmark`, and `delete` use an existing task number.
- Dates use one of the supported formats.
- An event ends after it starts.

Two tasks with the same description are not allowed, even if their letter casing differs.

## Manual testing

The JavaFX interface should also be checked manually on the supported operating systems, with different screen resolutions, and with different system language settings. Confirm that the chat bubbles, profile-picture chooser, scrolling, date display, and error messages remain readable and usable.
