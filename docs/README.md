# Turtley User Guide

Turtley is a command-line task manager for organising to-do items, deadlines,
events, and tags.

## Table of contents

- [Quick start](#quick-start)
- [Basic commands](#basic-commands)
  - [`todo`](#todo)
  - [`deadline`](#deadline)
  - [`event`](#event)
  - [`list`](#list)
  - [`mark`](#mark)
  - [`unmark`](#unmark)
  - [`delete`](#delete)
  - [`help`](#help)
  - [`bye`](#bye)
- [Advanced commands](#advanced-commands)
  - [Adding tags with `/tag`](#adding-tags-with-tag)
  - [`tag`](#tag)
  - [`untag`](#untag)
  - [`find`](#find)
  - [`filter`](#filter)
  - [`timecheck`](#timecheck)
- [Date and time format](#date-and-time-format)
- [Saving the data](#saving-the-data)
- [Tag rules](#tag-rules)
- [Editing the data file](#editing-the-data-file)
- [FAQ](#faq)
- [Known issues](#known-issues)
- [Command summary](#command-summary)

## Quick start

Turtley can be run from the Java source project or from the packaged `Turtley.jar`
file.

### Running `Turtley.jar`

1. Install Java 25 or later.
2. Download the latest .jar file from [here](https://github.com/SpoinorNUS/ip/releases)
3. Place `Turtley.jar` in the folder you want to use as Turtley's home folder.
4. Open a terminal, cd to the folder containing the JAR file, and run java -jar addressbook.jar.
   A GUI similar to the one below should appear in a few seconds.
Note that below also shows how Turtley reacts to commands.<br/>
![Screenshot of Turtley being used to mark and list out tasks in a list](https://spoinornus.github.io/ip/Ui.png)
5. When the Turtley window appears, type a command in the command box and press
   **Enter**. Try `help` first to view all available commands.
6. Use `list` to view your tasks. Refer to the Basic commands and Advanced
   commands sections below for details. Press the 'x' on the top right of the window to exit Turtley.
7. Refer to the command sections below for details of each command.

## Basic commands

### `todo`

Adds a task without a deadline or scheduled time.

```text
todo <description>
```

Example:

```text
todo Prepare presentation slides
```

### `deadline`

Adds a task that should be completed by a particular date or date and time.

```text
deadline <description> /by <date>
```

Example:

```text
deadline Submit project report /by 2026-09-18 18:00
```

### `event`

Adds an event with a start and end date or date and time.

```text
event <description> /from <start> /to <end>
```

Example:

```text
event Product photography session /from 2026-09-19 10:00 /to 2026-09-19 12:00
```

The event start must be earlier than its end.

### `list`

Displays all tasks and their one-based task numbers. Use these numbers with
commands such as `mark`, `tag`, and `delete`.

```text
list
```

### `mark`

Marks a task as completed.

```text
mark <task number>
```

Example:

```text
mark 2
```

### `unmark`

Marks a completed task as not completed.

```text
unmark <task number>
```

Example:

```text
unmark 2
```

### `delete`

Deletes a task permanently. Run `list` again afterwards because the remaining
task numbers may change.

```text
delete <task number>
```

Example:

```text
delete 3
```

### `help`

Displays the complete command list and tag rules inside Turtley.

```text
help
```

### `bye`

Saves the task list and exits Turtley.
(**Currently doesn't work in the latest version**, for now it is just easter egg dialog)

```text
bye
```

## Advanced commands

### Adding tags with `/tag`

Tags can be added when creating a task. Put `/tag` at the end of the command,
followed by one or more tags.

```text
todo <description> /tag #tag1 #tag2
deadline <description> /by <date> /tag #tag1 #tag2
event <description> /from <start> /to <end> /tag #tag1 #tag2
```

Examples:

```text
todo Prepare presentation slides /tag #school #priority
deadline Submit project report /by 2026-09-18 18:00 /tag #school #urgent
event Product photography session /from 2026-09-19 10:00 /to 2026-09-19 12:00 /tag #personal #photo
```

### `tag`

Adds one or more tags to an existing task.

```text
tag <task number> #tag1 [#tag2 ...]
```

Example:

```text
tag 1 #important #school
```

### `untag`

Removes one or more tags from an existing task.

```text
untag <task number> #tag1 [#tag2 ...]
```

Example:

```text
untag 1 #school
```

### `find`

Searches for a keyword in both task descriptions and tags. The search is
case-insensitive and can match part of a word.

```text
find <keyword>
```

Example:

```text
find project
```

### `filter`

Shows tasks with a tag containing the supplied substring. The filter must begin
with `#`; tag matching is case-insensitive.

```text
filter #tag-substring
```

Example:

```text
filter #school
```

### `timecheck`

Shows deadlines due on or before a specified date/time and events that start on
or before that date/time. To include every task on a date, provide a date
without a time.

```text
timecheck <date/time>
```

Examples:

```text
timecheck 2026-09-20
timecheck 2026-09-20 17:00
```

## Date and time format

Turtley accepts these date formats:

- `yyyy-MM-dd`, such as `2026-09-18`
- `dd-MM-yyyy`, such as `18-09-2026`
- `yyyy/MM/dd`, such as `2026/09/18`
- `dd/MM/yyyy`, such as `18/09/2026`

For date and time values, append a 24-hour time using either `HH:mm` or
`HHmm`. For example, `2026-09-18 18:00` and `18/09/2026 1800` are valid.
A `T` may also separate the date and time, such as `2026-09-18T18:00`.

## Saving the data
Turtley automatically saves data after every command. You do not need to save manually.

## Tag rules

- Every tag must start with `#` and contain 1 to 10 non-whitespace characters.
- Each task can have at most 10 tags.
- Tags are displayed alphabetically.
- Tags are case-sensitive when stored, but searches are case-insensitive.

## Editing the data file
AddressBook data is saved automatically in a txt file `[JAR file location]/data/turtley.txt`.
Advanced users are welcome to update data directly by editing that data file.
Caution: Invalid entries may cause the entire txt file to be rejected, so it is
recommended to back up the file before you choose to edit it.

## FAQ
**Q**: How do I transfer my data to another computer?<br/>
**A**: Install the app on the other computer and overwrite the data file it creates
with the data file from your previous Turtley home folder.

## Known issues
**The talking sound effect sometimes not play** when Turtley is speaking. In order to not stall Turtley's
response timing for advanced users, the text display does not wait for the audio player before starting.
Therefore, the sound effect may lag or even not play entirely, depending on the current state of your computer.
This is unfortunately intended design to accommodate fast typing.

**Bye command currently does nothing**, this is due to the transition from CLI to GUI design,
the bye command can potentially be changed to close the window as well in the future.

## Command summary

| Command | Format | Description |
| --- | --- | --- |
| `todo` | `todo <description> [/tag #tag1 #tag2 ...]` | Adds a task without a deadline or scheduled time. |
| `deadline` | `deadline <description> /by <date> [/tag #tag1 #tag2 ...]` | Adds a task with a deadline. |
| `event` | `event <description> /from <start> /to <end> [/tag #tag1 #tag2 ...]` | Adds an event with a start and end time. |
| `tag` | `tag <task number> #tag1 [#tag2 ...]` | Adds tags to an existing task. |
| `untag` | `untag <task number> #tag1 [#tag2 ...]` | Removes tags from an existing task. |
| `filter` | `filter #tag-substring` | Shows tasks with tags matching a substring. |
| `list` | `list` | Displays all tasks and their task numbers. |
| `find` | `find <keyword>` | Searches task descriptions and tags for a keyword. |
| `mark` | `mark <task number>` | Marks a task as completed. |
| `unmark` | `unmark <task number>` | Marks a completed task as not completed. |
| `delete` | `delete <task number>` | Permanently deletes a task. |
| `timecheck` | `timecheck <date/time>` | Shows deadlines and event starts on or before a date/time. |
| `help` | `help` | Displays the command list and tag rules. |
| `bye` | `bye` | Saves the data and exits Turtley. |
