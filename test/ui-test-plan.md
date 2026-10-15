# UI Test Plan

This file records the command, console input, and exact expected stdout for
each command-line UI test. Run it with:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

Timeout seconds: 10

## Test case: Start and exit

### Aim

Verify that the chatbot starts and exits when the user enters `bye`.

### Command

```shell
java -cp build/classes/java/main jason.Main
```

### Input

```text
bye
```

### Expected output

```text
Hello! My name is Jason, inspired by JSON files used by software engineers.
Send me a command, and I will keep your tasks neatly structured.
_______
_______
Goodbye! Your task data is ready for the next session.
```

## Test case: Incomplete event command

### Aim

Verify that an event with missing date-time values shows the correct event
command format instead of an internal Java exception.

### Command

```shell
java -cp build/classes/java/main jason.Main
```

### Input

```text
event do hw /from 1 /to
bye
```

### Expected output

```text
Hello! My name is Jason, inspired by JSON files used by software engineers.
Send me a command, and I will keep your tasks neatly structured.
_______
Event commands are in the form "event {description} /from {YYYY-MM-DD HH:MM} /to {YYYY-MM-DD HH:MM}"
_______
_______
_______
Goodbye! Your task data is ready for the next session.
```

Add further `## Test case:` sections below as UI behavior is implemented. Each
case must include an aim, command, input, and exact expected output.

## Test case: Find matching tasks

### Aim

Verify that `find` displays matching task descriptions in their list order and
that a later command can still be processed.

### Command

```shell
java -cp build/classes/java/main jason.Main
```

### Input

```text
find lunch
bye
```

### Expected output

```text
Hello! My name is Jason, inspired by JSON files used by software engineers.
Send me a command, and I will keep your tasks neatly structured.
_______
Here are the matching task objects:
1. [T] [ ] eat lunch
2. [E] [ ] lunch (from: Aug 29 2026 16:00 to: Aug 29 2026 18:00)
_______
_______
_______
Goodbye! Your task data is ready for the next session.
```

## Test case: Todo with a fixed duration

### Aim

Verify that a todo accepts a duration in minutes and displays the normalized
hours-and-minutes value.

### Command

```shell
java -cp build/classes/java/main jason.Main
```

### Input

```text
todo read sales report /for 90m
bye
```

### Expected output

```text
Hello! My name is Jason, inspired by JSON files used by software engineers.
Send me a command, and I will keep your tasks neatly structured.
_______
Added to your task list: [T] [ ] read sales report (for: 1h 30m)
_______
_______
_______
Goodbye! Your task data is ready for the next session.
```

## Test case: Invalid fixed duration

### Aim

Verify that an invalid duration reports the accepted format and the 24-hour
limit.

### Command

```shell
java -cp build/classes/java/main jason.Main
```

### Input

```text
todo read sales report /for 25h
bye
```

### Expected output

```text
Hello! My name is Jason, inspired by JSON files used by software engineers.
Send me a command, and I will keep your tasks neatly structured.
_______
Todo commands are in the form "todo {description} [/for {duration}]". Duration must be between 1 minute and 24 hours, using formats such as "2h", "90m", or "1h 30m".
_______
_______
_______
Goodbye! Your task data is ready for the next session.
```

## Test case: Whitespace around an exit command

### Aim

Verify that leading whitespace does not prevent the chatbot from recognizing
an exit command.

### Command

```shell
java -cp build/classes/java/main jason.Main
```

### Input

```text
  bye
```

### Expected output

```text
Hello! My name is Jason, inspired by JSON files used by software engineers.
Send me a command, and I will keep your tasks neatly structured.
_______
_______
Goodbye! Your task data is ready for the next session.
```

## Test case: Index command with repeated spaces

### Aim

Verify that an index command accepts repeated whitespace before the index.

### Command

```shell
java -cp build/classes/java/main jason.Main
```

### Input

```text
delete    999
bye
```

### Expected output

```text
Hello! My name is Jason, inspired by JSON files used by software engineers.
Send me a command, and I will keep your tasks neatly structured.
_______
No such index in list.
_______
_______
_______
Goodbye! Your task data is ready for the next session.
```

## Test case: Todo command with repeated spaces

### Aim

Verify that repeated whitespace after `todo` is not included in the task
description.

### Command

```shell
java -cp build/classes/java/main jason.Main
```

### Input

```text
todo    read book
bye
```

### Expected output

```text
Hello! My name is Jason, inspired by JSON files used by software engineers.
Send me a command, and I will keep your tasks neatly structured.
_______
Added to your task list: [T] [ ] read book
_______
_______
_______
Goodbye! Your task data is ready for the next session.

## Test case: Incomplete deadline command

### Aim

Verify that a deadline with a missing date-time value shows the deadline
command format instead of an internal Java exception.

### Command

```shell
java -cp build/classes/java/main jason.Main
```

### Input

```text
deadline submit report /by
bye
```

### Expected output

```text
Hello! My name is Jason, inspired by JSON files used by software engineers.
Send me a command, and I will keep your tasks neatly structured.
_______
Deadline commands are in the form "deadline {description} /by {YYYY-MM-DD HH:MM}"
_______
_______
_______
Goodbye! Your task data is ready for the next session.
```

## Test case: Incomplete todo command

### Aim

Verify that a todo with no description shows the todo command format instead of
an internal Java exception.

### Command

```shell
java -cp build/classes/java/main jason.Main
```

### Input

```text
todo
bye
```

### Expected output

```text
Hello! My name is Jason, inspired by JSON files used by software engineers.
Send me a command, and I will keep your tasks neatly structured.
_______
Todo commands are in the form "todo {description} [/for {duration}]". Duration must be between 1 minute and 24 hours, using formats such as "2h", "90m", or "1h 30m".
_______
_______
_______
Goodbye! Your task data is ready for the next session.
```
```
