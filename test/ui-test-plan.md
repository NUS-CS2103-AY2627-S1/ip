# UI Test Plan

This is the canonical record of scripted command-line UI test cases for Luna.
Run the cases and commands in the order shown. Deadline and event date inputs
use the `YYYY-MM-DD` format. Parenthetical notes from the requested input list
describe checks and are not entered into the program.

## Test environment

- Java version: Java 25
- Entry point: `luna.Luna`
- Working directory: one isolated temporary directory shared by all five
  sessions, initially without `data/luna.txt`
- Comparison: exact text, punctuation, line order, and blank lines; only LF/CRLF
  differences and one final newline are ignored
- Session 3 continues from the persisted data created by session 2

## UI-001: Unknown command and exit

**Aim:** Verify that an unknown command is rejected and that `bye` exits the
application cleanly.

**Preconditions:** No `data/luna.txt` file.

**Inputs and expected outputs:**

### Command 1

**Input:**

```text
asdkjgsd
```

**Expected output:**

```text
Hello! I'm Luna!
What can I do for you?

I'm sorry, I don't know what that means.
____________________________________________________________

```

### Command 2

**Input:**

```text
bye
```

**Expected output:**

```text
Bye. Hope to see you again soon!
```

## UI-002: Task commands and validation

**Aim:** Verify validation for todo, deadline, event, mark, and unmark commands;
verify adding, listing, marking, unmarking, and deleting tasks; and leave the
resulting task list saved for the persistence test.

**Preconditions:** Start a new process in the same isolated working directory
used for UI-001. UI-001 must not have created `data/luna.txt`.

**Inputs and expected outputs:**

### Command 1

**Input:**

```text
todo
```

**Expected output:**

```text
Hello! I'm Luna!
What can I do for you?

The description of a todo cannot be empty. Example: todo read book
____________________________________________________________

```

### Command 2

**Input:**

```text
todo read book
```

**Expected output:**

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________

```

### Command 3

**Input:**

```text
deadline
```

**Expected output:**

```text
A deadline must include a description and end date in this format: deadline <desc> /by <end>
Example: deadline return book /by 2019-10-15
____________________________________________________________

```

### Command 4

**Input:**

```text
deadline asdkjgsd
```

**Expected output:**

```text
A deadline must include a description and end date in this format: deadline <desc> /by <end>
Example: deadline return book /by 2019-10-15
____________________________________________________________

```

### Command 5

**Input:**

```text
deadline return book /by 2019-10-15
```

**Expected output:**

```text
Got it. I've added this task:
[D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________

```

### Command 6

**Input:**

```text
event
```

**Expected output:**

```text
An event must include a description, start date, and end date in this format: event <desc> /from <start> /to <end>
Example: event project meeting /from 2019-10-15 /to 2019-10-16
____________________________________________________________

```

### Command 7

**Input:**

```text
event asdkjgsd
```

**Expected output:**

```text
An event must include a description, start date, and end date in this format: event <desc> /from <start> /to <end>
Example: event project meeting /from 2019-10-15 /to 2019-10-16
____________________________________________________________

```

### Command 8

**Input:**

```text
event project meeting /from 2019-10-15 /to 2019-10-16
```

**Expected output:**

```text
Got it. I've added this task:
[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Now you have 3 tasks in the list.
____________________________________________________________

```

### Command 9 — Check the three added tasks

**Input:**

```text
list
```

**Expected output:**

```text
Here are the tasks in your list:
1. [T][ ] read book
2. [D][ ] return book (by: Oct 15 2019)
3. [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________

```

### Command 10

**Input:**

```text
mark
```

**Expected output:**

```text
Please provide a task number. Example: mark 2
____________________________________________________________

```

### Command 11

**Input:**

```text
mark asdkjgsd
```

**Expected output:**

```text
Task number must be an integer. Example: mark 2
____________________________________________________________

```

### Command 12

**Input:**

```text
mark 0
```

**Expected output:**

```text
Task number is out of range. Use 1 to 3.
____________________________________________________________

```

### Command 13

**Input:**

```text
mark 4
```

**Expected output:**

```text
Task number is out of range. Use 1 to 3.
____________________________________________________________

```

### Command 14

**Input:**

```text
unmark
```

**Expected output:**

```text
Please provide a task number. Example: unmark 2
____________________________________________________________

```

### Command 15

**Input:**

```text
unmark asdkjgsd
```

**Expected output:**

```text
Task number must be an integer. Example: unmark 2
____________________________________________________________

```

### Command 16

**Input:**

```text
unmark 0
```

**Expected output:**

```text
Task number is out of range. Use 1 to 3.
____________________________________________________________

```

### Command 17

**Input:**

```text
unmark 4
```

**Expected output:**

```text
Task number is out of range. Use 1 to 3.
____________________________________________________________

```

### Command 18

**Input:**

```text
unmark 1
```

**Expected output:**

```text
This task is not yet marked as done.
____________________________________________________________

```

### Command 19

**Input:**

```text
mark 1
```

**Expected output:**

```text
Nice! I've marked this task as done:
[T][X] read book
____________________________________________________________

```

### Command 20

**Input:**

```text
mark 2
```

**Expected output:**

```text
Nice! I've marked this task as done:
[D][X] return book (by: Oct 15 2019)
____________________________________________________________

```

### Command 21

**Input:**

```text
unmark 2
```

**Expected output:**

```text
OK, I've marked this task as not done yet:
[D][ ] return book (by: Oct 15 2019)
____________________________________________________________

```

### Command 22 — Check task statuses

**Input:**

```text
list
```

**Expected output:**

```text
Here are the tasks in your list:
1. [T][X] read book
2. [D][ ] return book (by: Oct 15 2019)
3. [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________

```

### Command 23

**Input:**

```text
delete 3
```

**Expected output:**

```text
Noted. I've removed this task:
[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Now you have 2 tasks in the list.
____________________________________________________________

```

### Command 24 — Check deletion

**Input:**

```text
list
```

**Expected output:**

```text
Here are the tasks in your list:
1. [T][X] read book
2. [D][ ] return book (by: Oct 15 2019)
____________________________________________________________

```

### Command 25

**Input:**

```text
bye
```

**Expected output:**

```text
Bye. Hope to see you again soon!
```

## UI-003: Reload persisted tasks

**Aim:** Reopen Luna after UI-002 exits and verify that the remaining tasks and
their completion states were stored and restored.

**Preconditions:** Start a new process in the same isolated working directory
immediately after UI-002. Its `data/luna.txt` must contain the state saved by
UI-002.

**Inputs and expected outputs:**

### Command 1 — Check restored tasks

**Input:**

```text
list
```

**Expected output:**

```text
Hello! I'm Luna!
What can I do for you?

Here are the tasks in your list:
1. [T][X] read book
2. [D][ ] return book (by: Oct 15 2019)
____________________________________________________________

```

After this expected output is observed, the test harness terminates the process
without sending another application command. This cleanup action is not part of
the ordered input list.

## UI-004: Find tasks by keyword

**Aim:** Verify that `find` requires a keyword, searches task descriptions by
case-insensitive substring, displays every matching task, and reports when no
task matches.

**Preconditions:** Start a new process in the same isolated working directory
after UI-003. Its `data/luna.txt` must still contain the two tasks restored in
UI-003.

**Inputs and expected outputs:**

### Command 1 — Reject a missing keyword

**Input:**

```text
find
```

**Expected output:**

```text
Hello! I'm Luna!
What can I do for you?

Please provide a keyword to search for. Example: find book
____________________________________________________________

```

### Command 2 — Find multiple tasks using different letter case

**Input:**

```text
find BOOK
```

**Expected output:**

```text
Here are the matching tasks in your list:
1. [T][X] read book
2. [D][ ] return book (by: Oct 15 2019)
____________________________________________________________

```

### Command 3 — Find one task using a partial description

**Input:**

```text
find read
```

**Expected output:**

```text
Here are the matching tasks in your list:
1. [T][X] read book
____________________________________________________________

```

### Command 4 — Report no matching tasks

**Input:**

```text
find homework
```

**Expected output:**

```text
(No matching tasks found.)
____________________________________________________________

```

### Command 5

**Input:**

```text
bye
```

**Expected output:**

```text
Bye. Hope to see you again soon!
```

## UI-005: Short command aliases

**Aim:** Verify that every short command alias behaves like its corresponding
full command, including aliases that accept arguments.

**Preconditions:** Start a new process in the same isolated working directory
after UI-004. Its `data/luna.txt` must still contain the two tasks restored in
UI-003.

**Inputs and expected outputs:**

### Command 1 — List tasks with `ls`

**Input:**

```text
ls
```

**Expected output:**

```text
Hello! I'm Luna!
What can I do for you?

Here are the tasks in your list:
1. [T][X] read book
2. [D][ ] return book (by: Oct 15 2019)
____________________________________________________________

```

### Command 2 — Add a todo with `t`

**Input:**

```text
t buy milk
```

**Expected output:**

```text
Got it. I've added this task:
[T][ ] buy milk
Now you have 3 tasks in the list.
____________________________________________________________

```

### Command 3 — Add a deadline with `d`

**Input:**

```text
d submit report /by 2026-11-30
```

**Expected output:**

```text
Got it. I've added this task:
[D][ ] submit report (by: Nov 30 2026)
Now you have 4 tasks in the list.
____________________________________________________________

```

### Command 4 — Add an event with `e`

**Input:**

```text
e conference /from 2026-10-01 /to 2026-10-02
```

**Expected output:**

```text
Got it. I've added this task:
[E][ ] conference (from: Oct 01 2026 to: Oct 02 2026)
Now you have 5 tasks in the list.
____________________________________________________________

```

### Command 5 — Mark a task with `m`

**Input:**

```text
m 3
```

**Expected output:**

```text
Nice! I've marked this task as done:
[T][X] buy milk
____________________________________________________________

```

### Command 6 — Unmark a task with `u`

**Input:**

```text
u 3
```

**Expected output:**

```text
OK, I've marked this task as not done yet:
[T][ ] buy milk
____________________________________________________________

```

### Command 7 — Find a task with `f`

**Input:**

```text
f report
```

**Expected output:**

```text
Here are the matching tasks in your list:
1. [D][ ] submit report (by: Nov 30 2026)
____________________________________________________________

```

### Command 8 — Delete a task with `del`

**Input:**

```text
del 5
```

**Expected output:**

```text
Noted. I've removed this task:
[E][ ] conference (from: Oct 01 2026 to: Oct 02 2026)
Now you have 4 tasks in the list.
____________________________________________________________

```

### Command 9 — Exit with `q`

**Input:**

```text
q
```

**Expected output:**

```text
Bye. Hope to see you again soon!
```
