# Milo UI Test Plan

## Configuration

- Main class: `milo.Milo`
- Source directory: `src/main/java`
- Java release: `25`

Each test case starts Milo in a fresh process. Start the ordered suite without `data/duke.txt`; the "Add and list all task types" and "Load saved tasks after restart" cases intentionally share saved state, and every later stateful case removes its tasks before exiting. The runner compares complete standard output after normalizing platform line endings and trailing spaces. Leading whitespace remains significant. It stops immediately after the first failing case and records the visible session in `_temp/ui-test/session.log`.

Save failures are covered by `SaveFailureTest` using a file where a storage directory is required. These tests verify that add, delete, mark, and unmark preserve the original task state, retain the underlying filesystem exception, and produce no success output when saving fails. The command loop reports `OOPS!!! I couldn't save your tasks.`. The console cases below retain their successful-save expectations.

`StorageTest` simulates access-denied errors at file replacement to verify successful retries, a five-attempt limit, preservation of the original file on persistent failure, immediate failure for other I/O errors, and interruption handling. Replacement retries wait 50 ms between attempts, for at most 200 ms of waiting.

`MiloTest` also checks that reversed and same-day event ranges create no data file, and that invalid task numbers do not change tasks after a restart.

## Test case: Reject an event whose end is not after its start

### Aim

Verify that a reversed or same-day event range is rejected without adding a task.

### Inputs

```text
event meeting /from 2026-09-18 /to 2026-09-17
event meeting /from 2026-09-18 /to 2026-09-18
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   OOPS!!! An event's end date must be after its start date.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! An event's end date must be after its start date.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Add and list all task types

### Aim

Verify that todos, deadlines, and events retain their type-specific details and appear in insertion order.

### Inputs

```text
todo borrow book
deadline return book /by 2019-12-02
event project meeting /from 2019-08-06 /to 2019-08-07
list
mark 2
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] borrow book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [D][ ] return book (by: Dec 02 2019)
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
                   Now you have 3 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] borrow book
                   2.[D][ ] return book (by: Dec 02 2019)
                   3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [D][X] return book (by: Dec 02 2019)
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Show command help

### Aim

Verify that `help` lists every supported command and its expected syntax without changing task state.

### Inputs

```text
help
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Here are the commands you can use:
                   todo <description> - add a todo
                   deadline <description> /by <yyyy-MM-dd> - add a deadline
                   event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd> - add an event
                   list - show all tasks
                   find <keyword> - find matching tasks
                   mark <number> - mark a task as done
                   unmark <number> - mark a task as not done
                   delete <number> - remove a task
                   stats - show task statistics
                   help - show this help message
                   bye - exit Milo
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Load saved tasks after restart

### Aim

Verify that a fresh Milo process loads each saved task type in order, including its completion state, and can continue modifying the loaded list.

### Inputs

```text
list
delete 3
delete 2
delete 1
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] borrow book
                   2.[D][X] return book (by: Dec 02 2019)
                   3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [D][X] return book (by: Dec 02 2019)
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][ ] borrow book
                   Now you have 0 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Reject invalid dates

### Aim

Verify that deadlines and events reject malformed or impossible dates without adding tasks.

### Inputs

```text
deadline return book /by 2019-02-29
event project meeting /from 2019-10-15 /to 15/10/2019
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/ 
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   OOPS!!! Dates must use the format yyyy-MM-dd.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! Dates must use the format yyyy-MM-dd.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Save task list after changes

### Aim

Verify that adding, marking, and deleting tasks completes normally while each list change is persisted.

### Inputs

```text
todo read book
mark 1
delete 1
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] read book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] read book
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][X] read book
                   Now you have 0 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Mark a task as done

### Aim

Verify that `mark` changes the selected task to done and that `list` retains the status.

### Inputs

```text
todo read book
mark 1
list
delete 1
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] read book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] read book
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][X] read book
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][X] read book
                   Now you have 0 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Unmark a completed task

### Aim

Verify that `unmark` reverses a task's done status and that `list` retains the change.

### Inputs

```text
todo read book
mark 1
unmark 1
list
delete 1
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] read book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] read book
                   -----------------------------------
                   -----------------------------------
                   OK, I've marked this task as not done yet:
                     [T][ ] read book
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] read book
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][ ] read book
                   Now you have 0 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Recover from invalid commands

### Aim

Verify that an empty todo and an unknown command produce clear errors without terminating Milo.

### Inputs

```text
todo
blah
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   OOPS!!! A todo needs a description.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! I don't recognize that command :-(
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Reject incomplete typed tasks

### Aim

Verify that deadlines and events missing their required date/time fields are rejected.

### Inputs

```text
deadline return book
event project meeting /from Mon 2pm
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   OOPS!!! A deadline needs a description followed by /by and a date or time.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! An event needs a description, /from start, and /to end.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Rejected task creation preserves list state

### Aim

Verify that invalid task commands interleaved with valid additions do not consume list positions or alter later tasks.

### Inputs

```text
todo read book
todo
deadline submit report /by 2026-08-28
deadline prepare slides
event project meeting /from 2026-08-29 /to 2026-08-30
event lunch /from noon
mark 3
list
delete 3
delete 2
delete 1
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] read book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! A todo needs a description.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [D][ ] submit report (by: Aug 28 2026)
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! A deadline needs a description followed by /by and a date or time.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [E][ ] project meeting (from: Aug 29 2026 to: Aug 30 2026)
                   Now you have 3 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! An event needs a description, /from start, and /to end.
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [E][X] project meeting (from: Aug 29 2026 to: Aug 30 2026)
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] read book
                   2.[D][ ] submit report (by: Aug 28 2026)
                   3.[E][X] project meeting (from: Aug 29 2026 to: Aug 30 2026)
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [E][X] project meeting (from: Aug 29 2026 to: Aug 30 2026)
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [D][ ] submit report (by: Aug 28 2026)
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][ ] read book
                   Now you have 0 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Invalid status changes preserve done states

### Aim

Verify that missing, non-numeric, zero, and out-of-range task numbers do not change task completion states between valid mark and unmark commands.

### Inputs

```text
todo first task
todo second task
mark 1
unmark 0
mark 3
unmark two
unmark 1
mark
mark 2
unmark
list
delete 2
delete 1
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] first task
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] second task
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] first task
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! There is no task numbered 0.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! There is no task numbered 3.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! Please specify a task number, for example: unmark 2
                   -----------------------------------
                   -----------------------------------
                   OK, I've marked this task as not done yet:
                     [T][ ] first task
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! Please specify a task number, for example: mark 2
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] second task
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! Please specify a task number, for example: unmark 2
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] first task
                   2.[T][X] second task
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][X] second task
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][ ] first task
                   Now you have 0 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Whitespace and extra command text preserve state

### Aim

Verify that leading whitespace is accepted while an unexpected command suffix and a blank command are rejected without affecting an existing task.

### Inputs

```text
   todo trim leading spaces
list extra
list

mark 1
list
delete 1
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] trim leading spaces
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! I don't recognize that command :-(
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] trim leading spaces
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! I don't recognize that command :-(
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] trim leading spaces
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][X] trim leading spaces
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][X] trim leading spaces
                   Now you have 0 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Delete a task from the middle of the list

### Aim

Verify that deleting a typed task reports the removed task, reduces the count, and shifts later tasks forward while preserving their done states.

### Inputs

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 2026-08-06 /to 2026-08-07
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
delete 4
delete 3
delete 2
delete 1
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] read book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [D][ ] return book (by: Jun 06 2026)
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)
                   Now you have 3 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] join sports club
                   Now you have 4 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] borrow book
                   Now you have 5 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] read book
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [D][X] return book (by: Jun 06 2026)
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] join sports club
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][X] read book
                   2.[D][X] return book (by: Jun 06 2026)
                   3.[E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)
                   4.[T][X] join sports club
                   5.[T][ ] borrow book
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)
                   Now you have 4 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][X] read book
                   2.[D][X] return book (by: Jun 06 2026)
                   3.[T][X] join sports club
                   4.[T][ ] borrow book
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][ ] borrow book
                   Now you have 3 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][X] join sports club
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [D][X] return book (by: Jun 06 2026)
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][X] read book
                   Now you have 0 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Invalid deletions preserve list state

### Aim

Verify that missing, non-numeric, zero, and out-of-range delete arguments leave the list unchanged, while deleting the last and only remaining tasks works.

### Inputs

```text
todo keep first
todo remove second
delete
delete 0
delete three
delete 3
list
delete 2
delete 1
delete 1
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] keep first
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] remove second
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! Please specify a task number, for example: delete 2
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! There is no task numbered 0.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! Please specify a task number, for example: delete 2
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! There is no task numbered 3.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] keep first
                   2.[T][ ] remove second
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][ ] remove second
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][ ] keep first
                   Now you have 0 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! There is no task numbered 1.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Find tasks by keyword

### Aim

Verify that `find` matches descriptions case-insensitively in their original order, supports multi-word keywords, handles no matches and missing keywords, and leaves the task list unchanged.

### Inputs

```text
todo borrow book
deadline return BOOK /by 2026-10-01
event project meeting /from 2026-10-02 /to 2026-10-03
find book
find project meeting
find missing
find
list
delete 3
delete 2
delete 1
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] borrow book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [D][ ] return BOOK (by: Oct 01 2026)
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [E][ ] project meeting (from: Oct 02 2026 to: Oct 03 2026)
                   Now you have 3 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Here are the matching tasks in your list:
                   1.[T][ ] borrow book
                   2.[D][ ] return BOOK (by: Oct 01 2026)
                   -----------------------------------
                   -----------------------------------
                   Here are the matching tasks in your list:
                   1.[E][ ] project meeting (from: Oct 02 2026 to: Oct 03 2026)
                   -----------------------------------
                   -----------------------------------
                   Here are the matching tasks in your list:
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! Please specify a keyword to find.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] borrow book
                   2.[D][ ] return BOOK (by: Oct 01 2026)
                   3.[E][ ] project meeting (from: Oct 02 2026 to: Oct 03 2026)
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [E][ ] project meeting (from: Oct 02 2026 to: Oct 03 2026)
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [D][ ] return BOOK (by: Oct 01 2026)
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][ ] borrow book
                   Now you have 0 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Statistics on an empty list and invalid arguments

### Aim

Verify zero counts, surrounding whitespace, and rejection of extra arguments and lookalike commands.

### Inputs

```text
stats
  stats
stats week
statsmore
STATS
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Here are your task statistics:
                   Total: 0
                   Completed: 0
                   Pending: 0
                   Completion: 0.0%
                   Todos: 0
                   Deadlines: 0
                   Events: 0
                   -----------------------------------
                   -----------------------------------
                   Here are your task statistics:
                   Total: 0
                   Completed: 0
                   Pending: 0
                   Completion: 0.0%
                   Todos: 0
                   Deadlines: 0
                   Events: 0
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! Use stats without additional arguments.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! I don't recognize that command :-(
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! I don't recognize that command :-(
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Statistics reflect changes to all task types

### Aim

Verify type counts, fractional completion, full-list scope after find, unmarking, and deletion back to an empty list.

### Inputs

```text
todo read book
deadline return book /by 2026-09-15
event meeting /from 2026-09-16 /to 2026-09-17
stats
mark 1
stats
find missing
stats
unmark 1
stats
delete 3
delete 2
delete 1
stats
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] read book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [D][ ] return book (by: Sep 15 2026)
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [E][ ] meeting (from: Sep 16 2026 to: Sep 17 2026)
                   Now you have 3 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Here are your task statistics:
                   Total: 3
                   Completed: 0
                   Pending: 3
                   Completion: 0.0%
                   Todos: 1
                   Deadlines: 1
                   Events: 1
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] read book
                   -----------------------------------
                   -----------------------------------
                   Here are your task statistics:
                   Total: 3
                   Completed: 1
                   Pending: 2
                   Completion: 33.3%
                   Todos: 1
                   Deadlines: 1
                   Events: 1
                   -----------------------------------
                   -----------------------------------
                   Here are the matching tasks in your list:
                   -----------------------------------
                   -----------------------------------
                   Here are your task statistics:
                   Total: 3
                   Completed: 1
                   Pending: 2
                   Completion: 33.3%
                   Todos: 1
                   Deadlines: 1
                   Events: 1
                   -----------------------------------
                   -----------------------------------
                   OK, I've marked this task as not done yet:
                     [T][ ] read book
                   -----------------------------------
                   -----------------------------------
                   Here are your task statistics:
                   Total: 3
                   Completed: 0
                   Pending: 3
                   Completion: 0.0%
                   Todos: 1
                   Deadlines: 1
                   Events: 1
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [E][ ] meeting (from: Sep 16 2026 to: Sep 17 2026)
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [D][ ] return book (by: Sep 15 2026)
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][ ] read book
                   Now you have 0 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Here are your task statistics:
                   Total: 0
                   Completed: 0
                   Pending: 0
                   Completion: 0.0%
                   Todos: 0
                   Deadlines: 0
                   Events: 0
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```
