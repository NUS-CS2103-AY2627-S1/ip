# ET User Guide

ET is a task manager with a JavaFX chat interface and a curious, slightly
homesick alien personality. Use it to remember to-dos, deadlines, and events.

## Getting started

Use Java 25 to run ET. From the project root, build and launch the application:

```bash
./gradlew shadowJar
java -jar build/libs/et.jar
```

On Windows, use `gradlew.bat shadowJar` for the build step. Alternatively, run
`./gradlew run` from the project root. See the [project README](../README.md)
for IntelliJ setup instructions.

Type a command in the input field and press **Enter** or click **BEAM IT**.
Command words and markers such as `todo`, `/by`, `/from`, and `/to` are
case-sensitive; use lowercase. Replace placeholders such as `<description>`
with your own text, without the angle brackets.

### GUI shortcuts

The **EARTH SHORTCUTS** row provides these controls:

- **LIST** and **SORT** immediately display the corresponding task view.
- **+ TODO**, **+ DEADLINE**, **+ EVENT**, and **FIND** fill the input field
  with a command template. Replace every placeholder before sending it.

Tasks appear as cards with a type icon, completion indicator, and date or time
badges where applicable. Numbered cards have **✓** (mark as done), **↶**
(mark as not done), and **×** (delete) controls as appropriate. Controls on
older responses become disabled when you send another command. Use **LIST**
or **SORT** to get fresh controls. For search results, see the numbering
limitation under [Finding tasks](#finding-tasks).

The examples below show task text; the GUI presents that information as cards.
In text output, `[T]`, `[D]`, and `[E]` identify to-dos, deadlines, and events;
`[ ]` means incomplete and `[X]` means complete.

## Date and time formats

ET accepts dates with `-`, `/`, or `.` separators, such as `2019-01-05`,
`2019/1/5`, and `5/1/2019`. You can also use month names, such as
`5 Jan 2019`, `January 5, 2019`, or `5-Jan-2019`. Ambiguous numeric dates are
interpreted as day-first.

An optional time must follow the date. Both 24-hour and 12-hour forms are
accepted, including `1800`, `18:00`, `6pm`, and `6:30 PM`.

Always supply a full date including the year. Relative dates and weekday names
such as `tomorrow`, `Sunday`, and `Mon 2pm`, as well as a time alone such as
`4pm`, are not supported. Both event endpoints need their own full date, and
the end must not be earlier than the start.

ET displays dates as `Jan 05 2019` and date-times as `Jan 05 2019 6:30 PM`.
Date-only values are treated as midnight when comparing or sorting tasks.

## Adding tasks

### ToDos

Use `todo <description>` for a task without a date or time:

```
todo borrow book
```

ET displays it as:

```
[T][ ] borrow book
```

### Deadlines

Use `deadline <description> /by <date> [time]` to specify when a task must
be completed. The time is optional; do not type the square brackets.

```
deadline return book /by 2019-01-05 1800
```

ET displays it as:

```
[D][ ] return book (by: Jan 05 2019 6:00 PM)
```

### Events

Use `event <description> /from <date> [time] /to <date> [time]` to specify
an event's start and end:

```
event project meeting /from 5 Jan 2019 2pm /to 5 Jan 2019 4pm
```

ET displays it as:

```
[E][ ] project meeting (from: Jan 05 2019 2:00 PM to: Jan 05 2019 4:00 PM)
```

## Listing and completing tasks

Use `list` to display every task in its stored order, numbered from 1.
Use `mark <number>` to complete a task and `unmark <number>` to mark it as
not done again. The number must refer to an existing task in the full list.

For example, after adding the three tasks above to an empty list:

```text
list
mark 1
unmark 1
```

`mark 1` changes `borrow book` to `[T][X] borrow book`. `unmark 1` changes it
back to `[T][ ] borrow book`.

## Deleting tasks

Use `delete <number>` to remove a task immediately:

```text
delete 1
```

Later tasks are renumbered after deletion. Run `list` again before choosing
another task number. There is no undo command.

## Sorting tasks

Use `sort` to display a temporary chronological view of your tasks. Incomplete
tasks appear before completed tasks. Within each group, deadlines are ordered
by their due date, events by their start date, and ToDos appear after tasks
with dates. Tasks with equal sort values retain their original list order.

```text
sort
```

The numbers shown are the tasks' original numbers, so you can use them with
`mark`, `unmark`, or `delete`. Sorting does not change the order shown by
`list` or `find`, and it does not change the saved task order.

## Finding tasks

Use `find <keyword>` to display tasks whose descriptions contain the keyword.
The search matches any part of a description, ignores letter case, and
preserves the matching tasks' list order:

```
find book
```

You can also search for a phrase, for example `find project meeting`.
The entire text after `find` is treated as one search phrase. Dates and
completion status are not searched. With no matches, ET displays the search
heading without any task entries.

**Current numbering limitation:** search results are numbered from 1 within
the results, but `mark`, `unmark`, and `delete` always use full-list numbers.
Run `list` or `sort` before acting on a search result, and use that view's
numbers or card controls. The controls on search-result cards also use the
displayed result numbers and may otherwise act on a different task.

## Exiting

Use `bye` to display ET's farewell and close the application:

```text
bye
```

## Saving tasks

ET automatically saves tasks in `data/tasks.txt` whenever you add, mark,
unmark, or delete one. This path is relative to the directory from which you
launch ET, which is the project root when following the steps above. Start
ET from the same directory each time to load the same task list.

On startup, ET reloads saved tasks. A missing file starts an empty list;
the file and its directory are created when tasks are saved. If ET reports
that its memory box would not save, the change remains in the current session
but has not been saved to disk.

## Command summary

| Action | Command |
| --- | --- |
| Add a to-do | `todo <description>` |
| Add a deadline | `deadline <description> /by <date> [time]` |
| Add an event | `event <description> /from <date> [time] /to <date> [time]` |
| List all tasks | `list` |
| Show chronological order | `sort` |
| Search descriptions | `find <keyword or phrase>` |
| Mark as complete | `mark <number>` |
| Mark as incomplete | `unmark <number>` |
| Delete a task | `delete <number>` |
| Exit | `bye` |

`[time]` means an optional time following a required date. Task descriptions
and search text must not be blank. Missing details, invalid dates, unknown
commands, and task numbers outside the full list produce an error message;
correct the input and send the command again.

See the [AI assistance acknowledgement](../README.md#acknowledgement-of-ai-assistance)
for the tools used, who used them, and the extent of their use in this project.
