# Daddy User Guide

Daddy is a task manager that accepts commands through its command-line or JavaFX chat interface. Its supportive,
straight-talking coach personality keeps you accountable with a friendly "my friend," celebrates progress, and
turns mistakes into specific advice for the next attempt.

## Testing

Run `./gradlew check jacocoTestReport` with Java 25 to compile the project, run JUnit, enforce Checkstyle, and
verify at least 90% line coverage across the automatically testable non-GUI core. The detailed HTML coverage
report is generated at `build/reports/jacoco/test/html/index.html`.

JavaFX launcher and view classes are excluded from the automated coverage threshold because they require a
graphical desktop. Use [`test/manual-test-plan.md`](../test/manual-test-plan.md) to check GUI startup, resizing,
display scaling, operating systems, locales, and file permissions.

## Input and data safety

Daddy accepts harmless leading, trailing, repeated, or tab whitespace in commands. It rejects malformed input
without changing the task list and explains how to correct the command. In particular:

- Dates and times must be real calendar values in the documented format; values such as `30-02-2026` are rejected.
- A deadline accepts exactly one `/by` value.
- An event accepts exactly one `/from` and one `/to` value, in that order, and must end after it starts.
- Task descriptions cannot contain `|`, because that character separates fields in Daddy's data file.
- A task with the same type, description, and date or time details cannot be added twice. Description matching is
  case-insensitive for this duplicate check.

It is normal for `data/daddy.txt` to be missing on the first run; Daddy creates it when the first task is saved.
Malformed saved records are skipped while valid records still load. Daddy preserves the original file in a
`.backup` file and places rejected records in a `.corrupt` file for repair. Permission and invalid-path problems
are reported without crashing the application, and a failed save leaves the in-memory task list unchanged.

## Finding the earliest free time

Use `free DURATION` to find the earliest working-hours slot that is long enough for you.

Examples:

```text
free 4h
free 90m
```

The duration must be a positive whole number followed immediately by `h` for hours or `m` for minutes. Units are
case-insensitive. A request cannot be longer than nine hours.

Daddy searches using these rules:

- The search starts from the current local time, rounded up to the next whole minute when necessary.
- Working hours are 09:00 to 18:00, Monday to Friday.
- Only events occupy time. Todos and deadlines do not block a free slot.
- Completed events still occupy their scheduled time.
- Events may overlap or touch each other; Daddy treats those events as one continuous busy period.
- A free slot may start at the exact time an event ends.

For example, Daddy can respond with:

```text
Daddy found you a recovery window:
  Mon, 14 Sep 2026, 09:00–13:00
That's a 4-hour slot. Protect it, then get back to the mission.
```

Finding a free time does not add, remove, mark, or otherwise modify any task.
