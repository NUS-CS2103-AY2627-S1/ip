# Loki UI Test Plan

Run this plan from the repository root with Java 25.

- Program: `java -cp _temp/ui-classes loki.Loki`
- Working directory: `.`
- Setup: `javac -d _temp/ui-classes src/main/java/loki/model/Task.java src/main/java/loki/model/ToDo.java src/main/java/loki/model/Deadline.java src/main/java/loki/model/Event.java src/main/java/loki/parser/DateTimeParser.java src/main/java/loki/parser/TaskUpdate.java src/main/java/loki/storage/Storage.java src/main/java/loki/model/TaskList.java src/main/java/loki/parser/Parser.java src/main/java/loki/ui/Ui.java src/main/java/loki/exception/LokiExceptions.java src/main/java/loki/Loki.java`

## JavaFX smoke test

Run `./gradlew run` with Java 25. A JavaFX window titled `Loki — Task Forge` should open.

The window header should read `✦ LOKI'S TASK FORGE` with the subtitle `Mischief,
momentum, and mortal obligations.`. The Loki greeting should identify him as the
God of Mischief. The command field should use the prompt `Whisper a command...`,
and the action button should read `Cast ✦`.

Enter `todo Buy groceries` and press Enter. The window should display the user command and a Loki response containing `[T][ ] Buy groceries`.
Enter `list` and press Send. The window should display `1. [T][ ] Buy groceries` and scroll to the newest response.
The opening greeting should suggest typing `help`, and entering `help` should list supported commands, usage, and examples.
The Send button should be disabled for blank or whitespace-only input and enabled once a command is entered.
The input bar should remain at the bottom and the chat area should expand when the window is resized between its minimum and default sizes.
User and Loki messages should use distinct alignments, speaker labels, and bubble styles; Loki replies should include a small circular Loki icon, and invalid-command responses should be visibly marked as errors.
Entering `exit` or `faretheewell` should save the task list, show the farewell response briefly, and then close the JavaFX window.
For malformed `todo`, `deadline`, or `event` commands, Loki should include the required syntax and a valid example.
Normal Loki responses should use green message bubbles, user messages should use blue message bubbles, error responses should use a red accent and red text, the comic background should remain low-contrast, and the visible text should remain readable at the compact default size.

Expected-output lines are checked in order. The test runner ignores surrounding whitespace and treats each expected line as a required substring, so randomized flavour text does not make the tests brittle.

## Test Case UI-001: Add a task and list it

### Aim

Verify that the `todo` command creates an unfinished task and that `list` displays it.

### Inputs

```text
todo Buy groceries
list
exit
```

### Expected output

```text
You have 1 tasks left to conquer.
1. [T][ ] Buy groceries
```

## Test Case UI-002: Mark a task as complete

### Aim

Verify that `mark 1` marks the first task as complete.

### Inputs

```text
todo Write report
mark 1
list
exit
```

### Expected output

```text
You have 1 tasks left to conquer.
[T][X] Write report
```

## Test Case UI-003: Unmark a completed task

### Aim

Verify that `unmark 1` changes a completed task back to unfinished.

### Inputs

```text
todo Write report
mark 1
unmark 1
list
exit
```

### Expected output

```text
You have 1 tasks left to conquer.
[T][X] Write report
[T][ ] Write report
```

## Test Case UI-004: Commands are case-insensitive

### Aim

Verify that command keywords can be entered in uppercase while task titles retain their original capitalization.

### Inputs

```text
todo Read Documentation
MARK 1
LIST
EXIT
```

### Expected output

```text
You have 1 tasks left to conquer.
[T][X] Read Documentation
```

## Test Case UI-005: Add a deadline task

### Aim

Verify that a `/by` marker creates a deadline task and preserves its deadline text.

### Inputs

```text
deadline Return book /by 2019-06-06
list
exit
```

### Expected output

```text
[D][ ] Return book (by: Jun 06 2019)
You have 1 tasks left to conquer.
```

## Test Case UI-006: Add an event task

### Aim

Verify that `/from` and `/to` markers create an event task with both times.

### Inputs

```text
event Project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
list
exit
```

### Expected output

```text
[E][ ] Project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
You have 1 tasks left to conquer.
```

## Test Case UI-007: Reject an unrecognised command

### Aim

Verify that an unrecognised keyword does not create a task and produces a randomly selected Loki exception response.

### Inputs

```text
dance wildly
exit
```

### Expected output

```text
Loki error:
```

## Test Case UI-011: Add multiple tasks

### Aim

Verify that two valid tasks are stored independently and listed in insertion order.

### Inputs

```text
todo First task
todo Second task
list
exit
```

### Expected output

```text
You have 2 tasks left to conquer.
1. [T][ ] First task
2. [T][ ] Second task
```

## Test Case UI-012: Invalid command does not change task state

### Aim

Verify that an invalid command between two valid task commands does not create an extra task.

### Inputs

```text
todo First task
unknown command
todo Second task
list
exit
```

### Expected output

```text
Loki error:
You have 2 tasks left to conquer.
1. [T][ ] First task
2. [T][ ] Second task
```

## Test Case UI-013: Mark and unmark a valid task

### Aim

Verify that valid status changes work in sequence and leave the task unfinished after `unmark`.

### Inputs

```text
todo Complete me
mark 1
unmark 1
list
exit
```

### Expected output

```text
You have 1 tasks left to conquer.
[T][X] Complete me
[T][ ] Complete me
1. [T][ ] Complete me
```

## Test Case UI-014: Invalid task indices do not change task state

### Aim

Verify that zero, non-numeric, and out-of-range indices all produce errors without changing the task.

### Inputs

```text
todo Keep pending
mark 0
mark nope
mark 2
list
exit
```

### Expected output

```text
You have 1 tasks left to conquer.
Loki error:
1. [T][ ] Keep pending
```

## Test Case UI-015: Add deadline and event tasks together

### Aim

Verify that different task subclasses can coexist and retain their individual metadata.

### Inputs

```text
deadline Submit report /by 2019-06-06
event Project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
list
exit
```

### Expected output

```text
[D][ ] Submit report (by: Jun 06 2019)
[E][ ] Project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
1. [D][ ] Submit report (by: Jun 06 2019)
2. [E][ ] Project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
```

## Test Case UI-016: Malformed task commands do not create tasks

### Aim

Verify that malformed deadline and event commands are rejected before a valid task is added.

### Inputs

```text
deadline Missing deadline
event Missing end /from 2pm
todo Valid after errors
list
exit
```

### Expected output

```text
Loki error:
Loki error:
You have 1 tasks left to conquer.
1. [T][ ] Valid after errors
```

## Test Case UI-017: Delete a task and reindex the list

### Aim

Verify that deleting the first task removes it and shifts the remaining task to index 1.

### Inputs

```text
todo First task
todo Second task
delete 1
list
exit
```

### Expected output

```text
You have 2 tasks left to conquer.
[T][ ] First task
You have 1 tasks left to conquer.
1. [T][ ] Second task
```

## Test Case UI-018: Invalid deletion does not change the list

### Aim

Verify that an invalid deletion index reports an error and leaves the task list unchanged.

### Inputs

```text
todo Keep this task
delete 0
list
exit
```

### Expected output

```text
You have 1 tasks left to conquer.
Loki error:
1. [T][ ] Keep this task
```

## Test Case UI-008: Reject an invalid task number

### Aim

Verify that a non-numeric task number is handled without terminating the session.

### Inputs

```text
mark nope
exit
```

### Expected output

```text
Loki error:
```

## Test Case UI-009: Reject a malformed deadline

### Aim

Verify that a deadline without a `/by` time reports the correct usage message.

### Inputs

```text
deadline Return book
exit
```

### Expected output

```text
Loki error:
```

## Test Case UI-010: Reject a malformed event

### Aim

Verify that an event without both `/from` and `/to` times reports the correct usage message.

### Inputs

```text
event Project meeting /from Aug 6th 2pm
exit
```

### Expected output

```text
Loki error:
```

## Test Case UI-019: Save tasks when exiting

### Aim

Verify that exiting after creating different task types saves successfully and does not produce a storage error.

### Inputs

```text
todo read book
mark 1
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
exit
```

### Expected output

```text
[T][ ] read book
[T][X] read book
[D][ ] return book (by: Jun 06 2019)
[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
```

## Test Case UI-020: Reject an impossible date

### Aim

Verify that a date containing an invalid calendar day is rejected without terminating the application.

### Inputs

```text
deadline Return book /by 2019-02-30
exit
```

### Expected output

```text
Loki error:
```

## Test Case UI-021: Reject a reversed event interval

### Aim

Verify that an event whose start occurs after its end is rejected.

### Inputs

```text
event Project meeting /from 2019-08-06 1600 /to 2019-08-06 1400
exit
```

### Expected output

```text
Loki error:
```

## Test Case UI-022: Parse day-first date and time input

### Aim

Verify that `2/12/2019 1800` is interpreted as 2 December 2019 at 6:00 PM.

### Inputs

```text
deadline Return book /by 2/12/2019 1800
exit
```

### Expected output

```text
[D][ ] Return book (by: Dec 02 2019, 6:00 PM)
```

## Test Case UI-023: Update a completed to-do title

### Aim

Verify that updating a to-do title preserves its completion status and list position.

### Inputs

```text
todo Buy groceries
mark 1
update 1 /title Buy groceries and toiletries
list
exit
```

### Expected output

```text
[T][X] Buy groceries
Updated: [T][X] Buy groceries and toiletries
1. [T][X] Buy groceries and toiletries
```

## Test Case UI-024: Reject an invalid update atomically

### Aim

Verify that an invalid update reports the stable usage text and leaves the task unchanged.

### Inputs

```text
deadline Submit report /by 2026-09-20 1800
update 1 /by 2026-02-30
list
exit
```

### Expected output

```text
Loki error:
Usage: update <task number> <field> [<field>...]
Fields: todo=/title; deadline=/title,/by; event=/title,/from,/to
1. [D][ ] Submit report (by: Sep 20 2026, 6:00 PM)
```

## Test Case UI-025: Update event fields in any order

### Aim

Verify that event update markers are case-insensitive, may appear in any order, and preserve omitted values.

### Inputs

```text
event Project consultation /from 20/9/2026 1400 /to 20/9/2026 1600
UPDATE 1 /TO 20/9/2026 1700 /TITLE Final consultation
list
exit
```

### Expected output

```text
Updated: [E][ ] Final consultation (from: Sep 20 2026, 2:00 PM to: Sep 20 2026, 5:00 PM)
1. [E][ ] Final consultation (from: Sep 20 2026, 2:00 PM to: Sep 20 2026, 5:00 PM)
```

## Test Case UI-026: Show command help

### Aim

Verify that `help` lists supported commands, usage, and examples without changing task state.

### Inputs

```text
help
help todo
exit
```

### Expected output

```text
Commands:
todo <task description>
Example: todo Buy groceries
update <task number> <field> [<field>...]
To-do fields: /title <new title>
Deadline fields: /title <new title>, /by <date/time>
Event fields: /title <new title>, /from <date/time>, /to <date/time>
update 3 /title Final consultation /from 20/9/2026 1400 /to 20/9/2026 1600
exit (alias: faretheewell)
Loki error:
```
