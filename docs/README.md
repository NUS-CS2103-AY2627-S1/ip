# Esther User Guide

Esther is a playful task assistant that helps you keep track of todos,
deadlines, and events. She saves your tasks automatically and is always ready
to help you organise your day.

![Esther user interface](Ui.png)

## Getting started

1. Install Java 25. Open a terminal (PowerShell on Windows) and run
   `java -version` to check that it reports version 25.
2. Download `esther.jar` from the assets in the
   [latest Esther release](https://github.com/yangzhiloh/ip/releases/latest).
3. Put the JAR in a folder where you want to keep your tasks. Open a terminal
   in that folder and run:

   ```shell
   java -jar esther.jar
   ```

4. When Esther opens, type `todo read book` into the input box and press Enter
   or click Send. Enter `list` to see your tasks.

Esther stores your tasks in `data/esther.txt`, relative to the folder from
which you run the command. Launch her from the same folder each time to
reload those tasks. Keep the `data` folder when replacing the JAR with a
new release.

If Java reports `Unable to access jarfile`, check that your terminal is in
the folder containing `esther.jar` and that the filename matches exactly.

## Adding tasks

### Todo

Use `todo` followed by a task description:

```text
todo read the project notes
```

### Deadline

Use `deadline`, a description, and a date in `yyyy-MM-dd` format:

```text
deadline submit report /by 2026-09-18
```

### Event

Use `event` with a description, start value, and end value:

```text
event tutorial /from 2pm /to 4pm
```

When ISO dates are used for an event, the end date must be after the start
date.

## Viewing tasks

List all tasks:

```text
list
```

Find tasks whose descriptions contain a keyword:

```text
find project
```

## Updating tasks

Commands use the task number shown by `list`.

Mark a task as completed:

```text
mark 1
```

Mark a completed task as incomplete:

```text
unmark 1
```

Delete a task:

```text
delete 1
```

Sort tasks so incomplete tasks appear before completed tasks:

```text
sort
```

The sorted order is saved automatically.

## Error handling

Esther highlights invalid responses and explains how to correct them. Common
mistakes include:

- omitting a task description
- using an invalid deadline date
- repeating `/by`, `/from`, or `/to`
- using a task number that does not exist
- providing a search command without a keyword

For example, entering `find` without a keyword prompts you to provide one.
Leading and trailing spaces around commands are also handled automatically.

## Exiting Esther

Use `bye` to close Esther:

```text
bye
```

Your tasks are stored in `data/esther.txt` and restored the next time Esther
starts.
