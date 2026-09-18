# Waffles UI test plan

The test runner starts one fresh Waffles process for each test case. The cases use one shared test data file so the second case verifies loading from the previous process; each later case removes the tasks left by the preceding case before testing its own behavior. Expected-output blocks list complete output lines that must appear in order; the startup banner and divider-only lines are intentionally omitted.

- Compile command: `gradlew.bat classes --console=plain`
- Run command: `gradlew.bat run --args="--cli" --console=plain`

## Test case: add and list a todo

### Aim

Verify that a todo is stored, displayed with the `[T]` type marker, and counted.

### Inputs

```text
todo borrow book
list
bye
```

### Expected output

```text
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
Here are the tasks in your list:
1.[T][ ] borrow book
```

## Test case: reject empty and unknown input

### Aim

Verify that an empty todo and an unknown command get friendly errors and do not become tasks.

### Inputs

```text
list
delete 1
todo
blah
todo borrow book
list
bye
```

### Expected output

```text
Here are the tasks in your list:
1.[T][ ] borrow book
Oops, a todo needs a description. Try `todo something to do`.
Oops, I don't recognise that command. Try todo, deadline, event, list, mark, unmark, delete, find, or bye.
Got it. I've added this task:
Now you have 1 tasks in the list.
1.[T][ ] borrow book
```

## Test case: reject malformed and invalid dates

### Aim

Verify that missing descriptions, markers, and invalid ISO dates are reported without adding invalid tasks.

### Inputs

```text
delete 1
deadline
deadline report /by
deadline report /by 2019-02-30
deadline report /by 2019-02-28
event meeting
event /from 2019-03-01 /to 2019-03-02
event meeting /from /to 2019-03-02
event meeting /from 2019-02-28 /to
event meeting /from 2019-02-30 /to 2019-03-01
event meeting /from 2019-02-28 /to 2019-03-01
list
bye
```

### Expected output

```text
Oops, a deadline needs a due time after `/by`, like `deadline report /by Friday`.
Oops, a deadline needs something after `/by`.
Oops, a deadline date must use yyyy-MM-dd, like 2019-10-15.
Got it. I've added this deadline:
Now you have 1 tasks in the list.
Oops, an event needs both `/from` and `/to`, like `event meeting /from 2pm /to 4pm`.
Oops, an event needs a description before `/from`.
Oops, an event needs something after `/from`.
Oops, an event needs something after `/to`.
Oops, an event date must use yyyy-MM-dd, like 2019-10-15.
Got it. I've added this event:
Now you have 2 tasks in the list.
1.[D][ ] report (by: Feb 28 2019)
2.[E][ ] meeting (from: Feb 28 2019 | to: Mar 01 2019)
```

## Test case: reject invalid mark and delete task numbers

### Aim

Verify that malformed and out-of-range mark and delete commands do not change the task list.

### Inputs

```text
delete 1
delete 1
todo keep list safe
mark
mark nope
mark 2
mark 1
unmark 0
unmark 1
delete
delete nope
delete 2
list
bye
```

### Expected output

```text
Oops, use mark followed by a task number, like mark 1.
Oops, that task number looks odd. Use a number, like mark 1.
Oops, that task number is out of range. Pick a number from 1 to 1.
Nice! I've marked this task as done:
  [T][X] keep list safe
Oops, that task number is out of range. Pick a number from 1 to 1.
OK, I've marked this task as not done yet:
  [T][ ] keep list safe
Oops, use delete followed by a task number, like delete 1.
Oops, that task number looks odd. Use a number, like delete 1.
Oops, that task number is out of range. Pick a number from 1 to 1.
1.[T][ ] keep list safe
```

## Test case: add a deadline

### Aim

Verify that a deadline preserves its description and `/by` value in the confirmation and list output.

### Inputs

```text
delete 1
deadline return book /by 2019-06-06
list
bye
```

### Expected output

```text
Got it. I've added this deadline:
  [D][ ] return book (by: Jun 06 2019)
Now you have 1 tasks in the list.
1.[D][ ] return book (by: Jun 06 2019)
```

## Test case: add an event

### Aim

Verify that an event preserves both time values and displays the divider between them.

### Inputs

```text
delete 1
event project meeting /from 2019-08-06 /to 2019-08-07
list
bye
```

### Expected output

```text
Got it. I've added this event:
  [E][ ] project meeting (from: Aug 06 2019 | to: Aug 07 2019)
1.[E][ ] project meeting (from: Aug 06 2019 | to: Aug 07 2019)
```

## Test case: mark and unmark typed tasks

### Aim

Verify that completion status is managed through the shared `Task` behavior for a typed task.

### Inputs

```text
delete 1
todo clean room
mark 1
unmark 1
list
bye
```

### Expected output

```text
Nice! I've marked this task as done:
  [T][X] clean room
OK, I've marked this task as not done yet:
  [T][ ] clean room
1.[T][ ] clean room
```

## Test case: delete a task and renumber the list

### Aim

Verify that a typed task can be deleted, the task count decreases, and later tasks move up in the list.

### Inputs

```text
delete 1
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
todo join sports club
todo borrow book
mark 1
mark 2
delete 3
list
bye
```

### Expected output

```text
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 06 2019 | to: Aug 07 2019)
Now you have 4 tasks in the list.
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2019)
3.[T][ ] join sports club
4.[T][ ] borrow book
```

## Test case: find tasks by keyword

### Aim

Verify that find returns matching task descriptions case-insensitively, reports no matches, and rejects a missing keyword.

### Inputs

```text
delete 1
delete 1
delete 1
delete 1
todo read book
deadline return book /by 2019-06-06
todo attend class
find BOOK
find spaceship
find
bye
```

### Expected output

```text
Got it. I've added this task:
Got it. I've added this deadline:
Got it. I've added this task:
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
Here are the matching tasks in your list:
No matching tasks found.
Oops, find needs a keyword. Try `find book`.
```

## GUI smoke test (manual)

Run `gradlew.bat run --console=plain`, enter `todo try the GUI`, and verify
that Waffles' welcome message appears when the window opens, and that the user
message and Waffles response appear in the scrollable window.
Also verify that pressing Enter and clicking Send both submit commands, that
the Send button is disabled for blank input, that an invalid command appears
in a red error card, and that resizing the window keeps the conversation and
composer usable.

## Test case: warn about scheduling clashes

### Aim

Verify that an overlapping event produces a warning while both scheduled tasks remain in the list.

### Inputs

```text
delete 1
delete 1
delete 1
event project meeting /from 2019-08-06 /to 2019-08-07
event overlapping meeting /from 2019-08-07 /to 2019-08-08
list
bye
```

### Expected output

```text
Warning: this task clashes with existing scheduled tasks:
  [E][ ] project meeting (from: Aug 06 2019 | to: Aug 07 2019)
Got it. I've added this event:
Now you have 2 tasks in the list.
1.[E][ ] project meeting (from: Aug 06 2019 | to: Aug 07 2019)
2.[E][ ] overlapping meeting (from: Aug 07 2019 | to: Aug 08 2019)
```

## Test case: reject reversed event dates

### Aim

Verify an event whose end date precedes its start date is rejected and does not
alter the task list.

### Inputs

```text
delete 1
delete 1
event backwards /from 2026-09-20 /to 2026-09-19
list
bye
```

### Expected output

```text
Oops, an event must end on or after it starts.
Here are the tasks in your list:
```

## Test case: normalize spacing and reject blank input

### Aim

Verify repeated spaces are normalized and blank input receives a helpful error.

### Inputs

```text

todo    spaced   task
list
bye
```

### Expected output

```text
Oops, please enter a command, such as `list` or `todo read book`.
Got it. I've added this task:
  [T][ ] spaced task
1.[T][ ] spaced task
```
