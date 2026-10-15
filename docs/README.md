# Ralph — User Guide

Ralph is a small, focused task manager (todos, deadlines, events). This document is a concise reference for using the GUI or CLI, with examples and the precise formats the app accepts.

---

## Contents

- Overview
- Quick start
- GUI usage
- CLI commands (with examples)
- Date/time formats
- Storage format
- Behavioural notes & limits
- Examples
- Contributing & testing
- Troubleshooting

---

## Overview

- Supported task types: Todo, Deadline, Event.
- Basic operations: add, list, find, mark/unmark, delete.
- Persisted to disk (`data/ralph.txt` by default).

## Quick start

Prerequisites
- Java 25
- Gradle wrapper is included

Run from source

```bash
# Windows
gradlew.bat run

# macOS/Linux
./gradlew run
```

Build a runnable JAR

```bash
./gradlew shadowJar
java -jar build/libs/ralph.jar
```

When run normally the GUI window is started; the app also supports interactive CLI usage in a terminal.

## GUI usage

- Type commands into the text box and press Enter (or click Send).
- The conversation area shows your input and Ralph's reply.
- The GUI uses the same command syntax as the CLI.

## CLI commands

Commands are shown with syntax and a short example. Task indices shown by `list` are 1-based.

### list
- Syntax: `list` or `list /by <key>`
- Sort keys: `deadline`, `status`
- Examples:
  - `list`
  - `list /by deadline` — dated tasks first (earliest → latest), then undated
  - `list /by status` — incomplete first, completed after

### todo
- Syntax: `todo <description>`
- Example: `todo buy milk`

### deadline
- Syntax: `deadline <description> /by <date|datetime>`
- Example: `deadline submit report /by 2026-10-15 23:59`

### event
- Syntax: `event <description> /from <date|datetime> /to <date|datetime>`
- Example: `event conference /from 2026-11-20 /to 2026-11-22`

### mark / unmark
- Syntax: `mark <n>` or `unmark <n>` (n is 1-based index)
- Example: `mark 2`

### delete
- Syntax: `delete <n>`
- Example: `delete 3`

### find
- Syntax: `find <keyword>`
- Finds tasks whose descriptions contain the keyword (case-insensitive).
- Example: `find report`

### bye
- Syntax: `bye`
- Exits the application.

---

## Date/time formats accepted

- Date only: `yyyy-MM-dd` — interpreted as start of the day (midnight). Example: `2026-10-15`.
- Date + time: `yyyy-MM-dd HH:mm` or `yyyy-MM-ddTHH:mm`. Example: `2026-10-15 18:00` or `2026-10-15T18:00`.
- Compact time: `HHmm` (e.g., `2359`) is accepted when separated by a space from the date: `2026-10-15 2359`.
- The parser attempts several common variations and returns a helpful error when parsing fails.

## Storage format (on disk)

- Default file: `data/ralph.txt` (configurable by the Ralph constructor).
- Each task is saved as a single pipe-separated line:

```
TYPE | DONE | DESCRIPTION | [DATE1] | [DATE2]
```

- TYPE: `T` (todo), `D` (deadline), `E` (event)
- DONE: `1` = completed, `0` = not completed
- DATE fields (for deadlines/events) are saved as ISO LocalDateTime (e.g. `2026-10-15T23:59`) to ensure reliable reload.

Example:
```
D | 0 | submit report | 2026-10-15T23:59
```

## Behavioural notes & limits

- Maximum tasks: 100 (Command.MAX_TASKS). Adding above this limit raises an error.
- `list /by deadline` orders tasks with dates first (Deadline/Event) by date/time; Todos (undated) follow. Sort is stable and does not modify persisted order.
- `list /by status` shows incomplete tasks before completed tasks.
- `find` uses case-insensitive substring matching on descriptions.
- On persistent save failure an error message is printed and changes remain in memory.
- Loading malformed saved lines: the loader skips them and prints a warning; valid lines are still loaded.

## Examples

Add a todo

```text
> todo read book
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

Add a deadline (date-only)

```text
> deadline project /by 2026-10-20
# Display will show the date formatted like: Oct 20 2026
```

List sorted by deadline

```text
> list /by deadline
 1.[D][ ] submit report (by: Oct 15 2026 23:59)
 2.[E][ ] conference (from: Nov 20 2026 to: Nov 22 2026)
 3.[T][ ] buy milk
```

## Contributing & testing

- Tests: `src/test/java` (JUnit 5). Run:

```bash
./gradlew test
```

- Follow the project's SE-EDU Java coding standard and commit guidelines when contributing.

## Troubleshooting

- Date parsing errors: reformat to `yyyy-MM-dd` or `yyyy-MM-dd HH:mm`.
- If data file is corrupted, backup/delete `data/ralph.txt` and restart (the app will create a fresh file).
- Save errors: check file permissions and available disk space.

---

For quick reference, the unit tests in `src/test/java` show expected behaviours and are a good source of examples.