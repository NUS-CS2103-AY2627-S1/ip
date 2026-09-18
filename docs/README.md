# Daddy

Daddy is a task manager with a straight-talking, supportive coaching style. It
keeps todos, deadlines, and events in one place, remembers them between
sessions, and helps you find space in a busy schedule.

![Daddy showing a realistic task list](Ui.png)

## Why Daddy?

- **Fast command entry:** manage tasks without navigating layers of forms.
- **Clear task types:** distinguish todos, deadlines, and scheduled events at
  a glance.
- **Persistent storage:** reload your task list automatically after restarting.
- **Useful scheduling:** find the earliest working-hours slot long enough for
  focused work or recovery.
- **Specific guidance:** receive actionable corrections when a command is
  incomplete or malformed.
- **Friendly GUI:** use a compact, resizable conversation view with visual task
  cards and highlighted errors.

## Getting started

1. Install Java 25.
2. Download `Daddy.jar` from the
   [latest GitHub release](https://github.com/YUJ1N06/ip/releases).
3. Put the JAR in an empty folder.
4. Open a terminal in that folder and run:

   ```shell
   java -jar Daddy.jar
   ```

Daddy creates its `data` folder automatically when the first task is saved.

## Commands

| Purpose | Example |
| --- | --- |
| Add a todo | `todo Review software engineering notes` |
| Add a deadline | `deadline Submit project report /by 25-09-2026 1800` |
| Add an event | `event Team meeting /from 22-09-2026 1400 /to 22-09-2026 1530` |
| Show all tasks | `list` |
| Show tasks on a date | `list on 25-09-2026` |
| Find matching tasks | `find report` |
| Find free time | `free 2h` |
| Mark a task done | `mark 1` |
| Mark a task not done | `unmark 1` |
| Delete a task | `delete 1` |
| Exit | `bye` |

Dates use `dd-MM-yyyy`. Add a time in `HHmm` format when needed.

## Finding the earliest free time

Use `free DURATION` to find the earliest working-hours slot that is long enough
for you:

```text
free 4h
free 90m
```

The duration must be a positive whole number followed immediately by `h` for
hours or `m` for minutes. Units are case-insensitive, and a request cannot be
longer than nine hours.

Daddy searches using these rules:

- The search starts from the current local time, rounded up to the next whole
  minute when necessary.
- Working hours are 09:00 to 18:00, Monday to Friday.
- Only events occupy time. Todos and deadlines do not block a free slot.
- Completed events still occupy their scheduled time.
- Overlapping or touching events are treated as one continuous busy period.
- A free slot may start at the exact time an event ends.

Finding free time does not add, remove, mark, or otherwise modify any task.

## Input and data safety

Daddy accepts harmless leading, trailing, repeated, or tab whitespace in
commands. It rejects malformed input without changing the task list and
explains how to correct the command. In particular:

- Dates and times must be real calendar values; values such as `30-02-2026`
  are rejected.
- A deadline accepts exactly one `/by` value.
- An event accepts exactly one `/from` and one `/to` value, in that order, and
  must end after it starts.
- Task descriptions cannot contain `|`, because that character separates
  fields in Daddy's data file.
- A task with the same type, description, and date or time details cannot be
  added twice. Descriptions are matched case-insensitively for this check.

It is normal for `data/daddy.txt` to be missing on the first run. Malformed
saved records are skipped while valid records still load. Daddy preserves the
original file in a `.backup` file and places rejected records in a `.corrupt`
file for repair. Permission and invalid-path problems are reported without
crashing the application, and a failed save leaves the in-memory task list
unchanged.

## Developer testing

Run `./gradlew check jacocoTestReport` with Java 25 to compile the project, run
JUnit, enforce Checkstyle, and verify at least 90% line coverage across the
automatically testable non-GUI core. The HTML coverage report is generated at
`build/reports/jacoco/test/html/index.html`.

Use [`test/manual-test-plan.md`](../test/manual-test-plan.md) for JavaFX,
resizing, display scaling, operating-system, locale, and file-permission checks.
