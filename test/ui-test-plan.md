# UI Test Plan

This file records console UI test cases for the project. Run test cases in the listed order and stop at the first failure.

## Program setup

- **Compile command:** `./gradlew classes` using Java 25 in PowerShell
- **Run command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/classes/java/main meka.Meka` using Java 25
- **Comparison rules:** Exact text comparison, normalizing only CRLF/LF line endings unless stated otherwise.
- **Archive setup:** Before UI-11, use an empty archive path at `data/meka-archive.txt`. Preserve and restore any existing archive after testing. Compare its expected contents using the same file comparison rules.
- **Other setup:** Before each test, remove the `data/meka.txt` file or directory if it exists. If a test specifies a pre-test data file, copy that fixture to `data/meka.txt` before launching the program. If a test specifies an unavailable data path, create a directory at `data/meka.txt`. Capture standard output as UTF-8.
- **File comparison rules:** When a test specifies expected file content, compare `data/meka.txt` exactly after normalizing only CRLF/LF line endings and allowing the final newline written by `Files.write`.

## Test cases

### UI-01: Add and list all task types

- **Aim:** Verify that Todo, Deadline, and Event commands create correctly formatted tasks, update the task count, and appear in the list.
- **Inputs (in order):**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 4/12/2019 1730
list
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2019, 6:00 PM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 03 2019, 9:00 AM to: Dec 04 2019, 5:30 PM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 1. [T][ ] borrow book
 2. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
 3. [E][ ] project meeting (from: Dec 03 2019, 9:00 AM to: Dec 04 2019, 5:30 PM)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected `data/meka.txt`:**

```text
T | 0 | borrow book
D | 0 | return book | 2019-12-02T18:00
E | 0 | project meeting | 2019-12-03T09:00 | 2019-12-04T17:30
```

### UI-02: Reject invalid commands without changing task state

- **Aim:** Verify that missing or non-numeric task numbers, empty task descriptions, and unknown commands produce the specified errors. Valid commands are interleaved with invalid commands, and the final list confirms that rejected commands did not add or modify tasks.
- **Inputs (in order):**

```text
todo first task
mark
todo
todo second task
mark one
mark 1
unmark
deadline /by 2/12/2019 1800
event team sync /from 3/12/2019 0900 /to 4/12/2019 1730
unmark none
event /from 4/12/2019 0900 /to 5/12/2019 1730
dance
list
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] first task
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 The following command requires a number to proceed.
____________________________________________________________
____________________________________________________________
 The following command requires a task description to proceed.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] second task
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 The following command requires a number to proceed.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] first task
____________________________________________________________
____________________________________________________________
 The following command requires a number to proceed.
____________________________________________________________
____________________________________________________________
 The following command requires a task description to proceed.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] team sync (from: Dec 03 2019, 9:00 AM to: Dec 04 2019, 5:30 PM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 The following command requires a number to proceed.
____________________________________________________________
____________________________________________________________
 The following command requires a task description to proceed.
____________________________________________________________
____________________________________________________________
 I do not understand this command. Please input a valid command.
____________________________________________________________
____________________________________________________________
 1. [T][X] first task
 2. [T][ ] second task
 3. [E][ ] team sync (from: Dec 03 2019, 9:00 AM to: Dec 04 2019, 5:30 PM)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected `data/meka.txt`:**

```text
T | 1 | first task
T | 0 | second task
E | 0 | team sync | 2019-12-03T09:00 | 2019-12-04T17:30
```

### UI-03: Mark and unmark a valid task

- **Aim:** Verify that valid numeric task references change completion state correctly and that the final task list reflects the latest state.
- **Inputs (in order):**

```text
todo reversible task
mark 1
unmark 1
list
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] reversible task
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] reversible task
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] reversible task
____________________________________________________________
____________________________________________________________
 1. [T][ ] reversible task
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected `data/meka.txt`:**

```text
T | 0 | reversible task
```

### UI-04: Delete a task from the list

- **Aim:** Verify that deletion removes the selected task, reports the updated count, rejects an out-of-range task number without changing the list, and shifts later tasks into the correct positions.
- **Inputs (in order):**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 4/12/2019 1730
delete 99
delete 2
list
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2019, 6:00 PM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 03 2019, 9:00 AM to: Dec 04 2019, 5:30 PM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 The task number does not exist in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: Dec 02 2019, 6:00 PM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 1. [T][ ] borrow book
 2. [E][ ] project meeting (from: Dec 03 2019, 9:00 AM to: Dec 04 2019, 5:30 PM)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected `data/meka.txt`:**

```text
T | 0 | borrow book
E | 0 | project meeting | 2019-12-03T09:00 | 2019-12-04T17:30
```

### UI-05: Load saved tasks on startup

- **Aim:** Verify that Todo, Deadline, and Event tasks, including their completion states, are restored from the data file when the program starts.
- **Pre-test data file:** Copy `test/data/load-all-task-types.txt` to `data/meka.txt`.
- **Inputs (in order):**

```text
list
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 1. [T][X] read book
 2. [D][ ] return book (by: Jun 06 2019, 6:00 PM)
 3. [E][X] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected `data/meka.txt`:**

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T18:00
E | 1 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

### UI-06: Reject invalid task details

- **Aim:** Verify that missing date-times, invalid date-times, and the reserved file delimiter are rejected without changing the task list or data file.
- **Inputs (in order):**

```text
deadline submit report /by
event team meeting /from /to 4pm
event team meeting /from 2pm /to
todo left | right
deadline clean room /by next | week
deadline submit report /by 29/2/2019 1800
deadline submit report /by 2/12/2019 2500
event conference /from 1/12/2019 0900 /to 2/12/2019
todo valid task
list
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 The following command requires date/time details to proceed.
____________________________________________________________
____________________________________________________________
 The following command requires date/time details to proceed.
____________________________________________________________
____________________________________________________________
 The following command requires date/time details to proceed.
____________________________________________________________
____________________________________________________________
 Task details cannot contain " | " because it is reserved for saved data.
____________________________________________________________
____________________________________________________________
 Task details cannot contain " | " because it is reserved for saved data.
____________________________________________________________
____________________________________________________________
 Please enter date and time as d/M/yyyy HHmm (for example, 2/12/2019 1800).
____________________________________________________________
____________________________________________________________
 Please enter date and time as d/M/yyyy HHmm (for example, 2/12/2019 1800).
____________________________________________________________
____________________________________________________________
 Please enter date and time as d/M/yyyy HHmm (for example, 2/12/2019 1800).
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] valid task
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 1. [T][ ] valid task
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected `data/meka.txt`:**

```text
T | 0 | valid task
```

### UI-07: Recover from corrupted saved data

- **Aim:** Verify that invalid saved data produces a friendly warning, starts with an empty list, preserves the original file, and keeps later changes in memory only.
- **Pre-test data file:** Copy `test/data/invalid-status.txt` to `data/meka.txt`.
- **Inputs (in order):**

```text
list
todo recovered task
list
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
 I could not load the saved tasks, so I started with an empty task list.
____________________________________________________________
____________________________________________________________
____________________________________________________________
____________________________________________________________
 I could not save the task list. Your changes are available only for this session.
____________________________________________________________
____________________________________________________________
 1. [T][ ] recovered task
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected `data/meka.txt`:**

```text
T | 2 | invalid status
```

### UI-08: Continue when the data path is unavailable

- **Aim:** Verify that read and write failures produce friendly warnings while the chatbot continues to maintain its in-memory task list.
- **Pre-test unavailable data path:** Create a directory at `data/meka.txt`.
- **Inputs (in order):**

```text
todo session-only task
list
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
 I could not load the saved tasks, so I started with an empty task list.
____________________________________________________________
____________________________________________________________
 I could not save the task list. Your changes are available only for this session.
____________________________________________________________
____________________________________________________________
 1. [T][ ] session-only task
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected data path:** `data/meka.txt` remains a directory.

### UI-09: Find tasks by description keyword

- **Aim:** Verify that `find` lists only tasks whose descriptions contain the keyword, ignores letter case, and numbers the matching results consecutively.
- **Pre-test data file:** Copy `test/data/load-all-task-types.txt` to `data/meka.txt`.
- **Inputs (in order):**

```text
find BOOK
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1. [T][X] read book
 2. [D][ ] return book (by: Jun 06 2019, 6:00 PM)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected `data/meka.txt`:**

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T18:00
E | 1 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

### UI-10: Recover from ambiguous parameters and invalid event ranges

- **Aim:** Accept extra spaces, reject repeated date parameters and non-increasing event times, and confirm rejected commands leave the task list and saved data unchanged.
- **Inputs (in order):**

```text
todo   read book
deadline report /by 2/12/2019 1800 /by 3/12/2019 1800
event meeting /from 2/12/2019 0900 /from 2/12/2019 1000 /to 2/12/2019 1100
event meeting /from 2/12/2019 0900 /to 2/12/2019 1000 /to 2/12/2019 1100
event meeting /from 2/12/2019 0900 /to 2/12/2019 0900
event meeting /from 2/12/2019 0900 /to 2/12/2019 0800
list
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Specify /by only once.
____________________________________________________________
____________________________________________________________
 Specify /from only once.
____________________________________________________________
____________________________________________________________
 Specify /to only once.
____________________________________________________________
____________________________________________________________
 The event end must be after its start.
____________________________________________________________
____________________________________________________________
 The event end must be after its start.
____________________________________________________________
____________________________________________________________
 1. [T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected `data/meka.txt`:**

```text
T | 0 | read book
```

### UI-11: Archive all active tasks

- **Aim:** Verify that `archive all` preserves every task in order, clears the active list, and reports an empty list on a second archive attempt.
- **Inputs (in order):**

```text
todo first task
deadline second task /by 14/9/2026 1800
archive all
list
archive all
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] first task
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] second task (by: Sep 14 2026, 6:00 PM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've archived all 2 tasks.
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
____________________________________________________________
____________________________________________________________
 There are no tasks to archive.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

- **Expected `data/meka.txt`:** Empty file.

- **Expected `data/meka-archive.txt`:**

```text
T | 0 | first task
D | 0 | second task | 2026-09-14T18:00
```
