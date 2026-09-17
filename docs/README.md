# Murphy User Guide

Murphy is a friendly task archive with a JavaFX chat interface. Use it to
remember errands, deadlines, events, and the occasional strategic nap.

![Murphy full-window screenshot](Ui.png)

## Getting started

Use Java 25. From the project root, launch the GUI with:

```bash
./gradlew run
```

The window can be resized. Murphy's messages appear on the left and your
messages on the right. The sidebar shows task counts and command formats.
Click a command button to insert its format into the input field.

Murphy saves tasks in `data/murphy.txt` and creates the folder when needed.

## Commands

### Add a todo

```text
todo <description>
```

Example: `todo buy treats for Murphy`

### Add a deadline

```text
deadline <description> /by <date>
```

Dates use `yyyy-MM-dd` format. Example: `deadline submit report /by 2026-09-20`

### Add an event

```text
event <description> /from <start time> /to <end time>
```

The `/from` and `/to` markers are required:

```text
event strategic nap /from 2pm /to 4pm
```

### View and search tasks

```text
list
find <keyword>
on <yyyy-MM-dd>
```

`list` displays every task. `find` searches descriptions without regard to
letter case. `on` displays deadlines and events occurring on a date.

### Change or remove a task

Tasks are numbered starting from 1 in the order shown by `list`.

```text
mark <task number>
unmark <task number>
delete <task number>
```

Examples:

```text
mark 2
delete 4
```

### Exit

```text
bye
```

## Errors and recovery

Murphy reports mistakes with an `OOPS!` message and keeps running. Common
errors include unknown commands, missing arguments, invalid dates, invalid
task numbers, duplicate tasks, repeated command markers, and missing,
unreadable, or corrupted save files.

This is invalid because the event markers are missing:

```text
event meet mom from 2pm to 4pm
```

Use this format instead:

```text
event meet mom /from 2pm /to 4pm
```

Corrupted records are skipped when possible while valid records are kept. If
Murphy cannot read the file, it starts with an empty list and explains the
problem.

## Duplicate tasks

Murphy rejects duplicate `todo`, `deadline`, and `event` tasks. Comparison is
case-insensitive and ignores completion status. Different types, dates, or
event times are allowed.

```text
todo buy milk
todo Buy milk
```

The second command is rejected while the original remains unchanged.

## Building the application

```bash
./gradlew shadowJar
java -jar build/libs/murphy.jar
```

The generated JAR should not be committed to the repository.
