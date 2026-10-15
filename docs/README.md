# Probe User Guide

Probe is a lightweight task-management chatbot with terminal and JavaFX interfaces.
It supports todo, deadline, event, search, completion, and task-tagging commands.

## Getting started

Requirements: Java 25 and Gradle.

Run the terminal interface with `./gradlew run` or the JavaFX interface with `./gradlew javafxRun`.
Tasks are saved automatically in `probe.txt`.

## Commands

| Command | Example | Description |
| --- | --- | --- |
| `list` | `list` | Shows all tasks. |
| `todo` | `todo read a book` | Adds a todo task. |
| `deadline` | `deadline submit report /by 30/9/2026 1800` | Adds a deadline. |
| `event` | `event meeting /from 1/10/2026 1000 /to 1/10/2026 1100` | Adds an event. |
| `find` | `find report` | Finds matching tasks. |
| `mark` | `mark 1` | Marks a task as done. |
| `unmark` | `unmark 1` | Marks a task as not done. |
| `delete` | `delete 1` | Deletes a task. |
| `tag` | `tag 1 #school #urgent` | Adds one or more tags. |
| `untag` | `untag 1 #urgent` | Removes tags. |
| `tag clear` | `tag clear` | Removes all tags from all tasks. |
| `bye` | `bye` | Exits Probe. |

Task numbers start from `1`. Tag names may contain letters, numbers, underscores, and hyphens.

## Example

```text
todo finish assignment
tag 1 #school #important
list
untag 1 #important
find assignment
mark 1
bye
```

Invalid commands are reported without terminating the application. Probe rejects missing
descriptions, invalid task numbers, invalid dates, and events whose end time is not after their
start time.

## Building and testing

Run `./gradlew check` to compile the project, run the JUnit tests, and run Checkstyle.
