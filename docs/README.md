# Minerva User Guide

Minerva is a friendly task assistant that helps you keep track of todos,
deadlines, events, and general tasks. You can use it through the graphical
interface or the console application.

## Getting started

### Option 1: Run the released JAR

Download the latest `minerva.jar` from the project's [GitHub Releases](https://github.com/Rio-maker/ip/releases).
Place it in an empty folder, open a terminal in that folder, and run:

```bash
java -jar minerva.jar
```

Java 25 is required.

### Option 2: Run from the source code with Gradle

Run the graphical application with Gradle:

```bash
./gradlew run
```

On Windows, use `gradlew.bat run` instead.

Type a command in the input box and press **Enter** or click **Send**.

## Commands

| Command | What it does | Example |
| --- | --- | --- |
| `help` | Shows all available commands. | `help` |
| `list` | Displays all saved tasks. | `list` |
| `todo` | Adds a todo task. | `todo read chapter 3` |
| `task` | Adds a general task. | `task plan weekend study` |
| `deadline` | Adds a task with a due date. | `deadline submit report /by 2026-09-30` |
| `event` | Adds an event with a start and end time. | `event project meeting /from 2026-09-20 /to 3pm` |
| `mark` | Marks a task as complete. | `mark 1` |
| `unmark` | Marks a task as incomplete. | `unmark 1` |
| `delete` | Removes a task. | `delete 1` |
| `find` | Finds tasks containing a word. | `find study` |
| `undo` | Restores the previous task-list state. | `undo` |
| `bye` | Ends the console session. | `bye` |

## Examples

Add and complete a task:

```text
todo revise lecture notes
mark 1
list
```

Add a deadline and search for it:

```text
deadline submit report /by 2026-09-30
find report
```

Undo the most recent successful task operation:

```text
undo
```

## Helpful tips

- Task numbers start at `1`, matching the numbers shown by `list`.
- Use the date format `yyyy-MM-dd` for deadline and event start dates.
- Minerva responds with a friendly explanation when a command is invalid.
- Use `help` whenever you need a reminder of the available commands.
