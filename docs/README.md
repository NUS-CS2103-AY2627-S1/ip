# Jason User Guide

Jason is a JSON-inspired task manager for organizing your tasks through typed
commands. The command-line interface is the default interface, and an optional
JavaFX chatbot GUI is also available.

## Getting started

Run the command-line interface from the project root:

```bash
./gradlew run
```

Run the optional graphical interface with:

```bash
./gradlew runGui
```

## Adding tasks

### Todo

Add a normal todo with:

```text
todo <description>
```

Example:

```text
todo read book
```

Add an unscheduled todo with a fixed duration using `/for`:

```text
todo <description> /for <duration>
```

Supported duration formats include `2h`, `90m`, and `1h 30m`. Durations must
be between 1 minute and 24 hours.

Example:

```text
todo read sales report /for 90m
```

Jason displays the normalized duration:

```text
Added to your task list: [T] [ ] read sales report (for: 1h 30m)
```

### Deadline

Add a task that must be completed by a date and time:

```text
deadline <description> /by <YYYY-MM-DD HH:MM>
```

Example:

```text
deadline submit report /by 2026-10-15 18:00
```

### Event

Add an event with a start and end date and time:

```text
event <description> /from <YYYY-MM-DD HH:MM> /to <YYYY-MM-DD HH:MM>
```

Example:

```text
event project meeting /from 2026-10-15 14:00 /to 2026-10-15 16:00
```

Dates are entered as `yyyy-MM-dd HH:mm` and displayed as `MMM dd yyyy HH:mm`.

## Managing tasks

Task numbers are one-based, starting from `1` in the order shown by `list`.

Display all tasks:

```text
list
```

Mark a task as complete:

```text
mark <task number>
```

Mark a task as incomplete again:

```text
unmark <task number>
```

Delete a task:

```text
delete <task number>
```

Search task descriptions for a keyword, ignoring letter case:

```text
find <keyword>
```

For example:

```text
find report
```

Exit Jason with:

```text
bye
```

Jason saves task changes automatically in `data/jason.txt` and loads existing
tasks when the application starts. If the file does not exist, Jason starts
with an empty task list and creates the file when a task is saved.

## Help for invalid commands

Commands with missing or invalid arguments produce an error explaining the
expected format. For example, a duration must be between 1 minute and 24 hours
and use a format such as `2h`, `90m`, or `1h 30m`.
