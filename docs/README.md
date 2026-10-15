# Otaku User Guide

Otaku is an anime-inspired task-tracking chatbot that turns everyday work into a quest log. It manages todos,
deadlines, and events through short text commands.

![Otaku's graphical interface](Ui.png)

## Quick start

1. Make sure Java 25 with JavaFX support is installed.
2. Download the project and open a terminal in its root folder.
3. Run `./gradlew run` on macOS or Linux, or `gradlew.bat run` on Windows.
4. Type a command into the input box, then press **Enter** or click **Log quest**.

To build and run a standalone JAR instead, use:

```shell
./gradlew shadowJar
java -jar build/libs/otaku.jar
```

Otaku saves the quest log automatically in `data/otaku.txt`. The same tasks are restored the next time the app is
opened from that folder.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add a todo | `todo DESCRIPTION` | `todo read book` |
| Add a deadline | `deadline DESCRIPTION /by DATE` | `deadline submit report /by 2026-09-30` |
| Add an event | `event DESCRIPTION /from START /to END` | `event workshop /from 2026-10-01 /to 2026-10-02` |
| Show all tasks | `list` | `list` |
| Find tasks | `find KEYWORD` | `find book` |
| Complete a task | `mark NUMBER` | `mark 2` |
| Reopen a task | `unmark NUMBER` | `unmark 2` |
| Delete a task | `delete NUMBER` | `delete 2` |
| Sort tasks by date | `sort` | `sort` |
| Exit | `bye` | `bye` |

Dates must use the `yyyy-MM-dd` format, such as `2026-09-30`. Task numbers come from the latest `list` output and
start at 1.

Otaku labels todos with `[T]`, deadlines with `[D]`, and events with `[E]`. An incomplete task shows `[ ]`, while a
completed task shows `[X]`.

## Adding tasks

### Todo

Use a todo for a task without a specific date.

```text
todo read book
```

### Deadline

Use a deadline for a task that must be completed by a date.

```text
deadline submit report /by 2026-09-30
```

Provide exactly one `/by` value and enter a valid date.

### Event

Use an event for an activity with a start and end date.

```text
event project workshop /from 2026-10-01 /to 2026-10-02
```

Provide exactly one `/from` and one `/to` value. The end date must be after the start date.

Otaku rejects a task if an identical task is already in the quest log.

## Viewing and finding tasks

Use `list` to display the whole quest log:

```text
list
```

Use `find` to display tasks whose descriptions contain a keyword:

```text
find book
```

Keyword matching is case-sensitive. For example, `find book` and `find Book` can return different results.

## Updating tasks

Use the number shown by `list` to change a task.

```text
mark 2
unmark 2
delete 2
```

- `mark` records the selected task as complete.
- `unmark` makes the selected task active again.
- `delete` permanently removes the selected task from the quest log.

If a number is not present in the current list, Otaku reports an error and leaves the quest log unchanged.

## Sorting tasks chronologically

Use `sort` to place dated tasks in chronological order:

```text
sort
```

Deadlines use their due date, events use their start date, and todos without dates appear last. Tasks with the same
date retain their existing order. The command does not accept additional arguments.

## Exiting

Use `bye` to end the session:

```text
bye
```

After the farewell appears, the input box and button are disabled. All successful changes have already been saved.

## Understanding errors

Invalid commands appear in a red response bubble with a `!` badge. The response explains what needs correcting,
such as a missing description, an invalid date, or a task number that does not exist. Rejected commands do not
change the quest log.
