# UI test plan

Run the cases in order from an empty `data` directory. The first creates the data file; the second verifies it reloads.
Output comparison normalizes CRLF/LF line endings; all other whitespace and line breaks remain exact.

## Launch command

```text
java '-Dstdout.encoding=UTF-8' -cp C:\Users\tangs\Downloads\ip\build\classes\java\main tangent.Tangent
```

## Test case: create and list dated tasks

- **Aim:** Verify first-run persistence setup, valid date parsing, and formatted task output.
- **Inputs:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[T][ ] borrow book
you now have 1 task in the list!
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[D][ ] return book (by: Dec 02 2019, 6:00PM)
you now have 2 tasks in the list!
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
you now have 3 tasks in the list!
____________________________________________________________
____________________________________________________________
hello! here are your current tasks:
1. [T][ ] borrow book
2. [D][ ] return book (by: Dec 02 2019, 6:00PM)
3. [E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

## Test session record — 2026-08-31

Both test cases were compiled and run with Java 25 in an isolated working directory, in the order shown above.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
Matches the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Matches the complete expected-output block for this test case above.
```

- **Result:** PASS

## Test session record — 2026-09-14 (storage error handling)

Java 25.0.4 direct console compilation + exact comparison passed all three planned cases in isolated `ui-test-storage-error-workspace-3`.

### Attempted tests: all planned cases

- **Aim:** Verify storage-backed create, reload, mutation, invalid-date handling, and status persistence after atomic-save changes.
- **Console input sent:**

```text
Case 1: todo borrow book; deadline return book /by 2/12/2019 1800; event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100; list; bye
Case 2: mark 2-3; unmark 2-3; delete 1 3; deadline impossible /by 31/2/2019 1800; list; bye
Case 3: unmark 1; list; bye
```

- **Expected output:**

```text
Complete expected-output blocks for corresponding planned cases above.
```

- **Actual output:**

```text
Exact match for all three cases; all processes exited with code 0.
```

- **Result:** PASS (3 cases)

## Test session record — 2026-09-14 (GUI polish)

Java 25.0.4 direct console compilation + isolated execution passed all three planned cases in order. Console UI behavior stayed unchanged.

### Attempted tests: all planned cases

- **Aim:** Verify existing console behavior after GUI-only changes.
- **Console input sent:** Exact inputs from each planned test case above, in declared order.
- **Expected output:** Complete expected-output block for each corresponding case above.
- **Actual output:** Exact match for each case; all processes exited with code 0.
- **Result:** PASS (3 cases)

## Test session record — 2026-09-12 (mass operations)

Java 25.0.4 direct console compilation + exact comparison passed all three current UI-plan cases in order in isolated `ui-test-batch-plan-workspace-7`.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:** Complete expected-output block for this case above.
- **Actual output:** Exact match; process exited with code 0.
- **Result:** PASS

### Attempted test: reload and perform batch mutations

- **Console input sent:**

```text
mark 2-3
unmark 2-3
delete 1 3
deadline impossible /by 32/01/2024 1800
list
bye
```

- **Expected output:** Complete expected-output block for this case above.
- **Actual output:** Exact match; process exited with code 0.
- **Result:** PASS

### Attempted test: unmark and reload status

- **Console input sent:**

```text
unmark 1
list
bye
```

- **Expected output:** Complete expected-output block for this case above.
- **Actual output:** Exact match; process exited with code 0.
- **Result:** PASS (3 cases)

## Test session record — 2026-09-11 (storage format constants)

Java 25.0.4 direct compilation of console sources + first planned case passed in isolated `ui-test-storage-constants-workspace`.

- **Actual output:**

```text
Application exited with code 0 after creating and listing ToDo, Deadline, and Event records.
```

- **Result:** PASS

## Test session record — 2026-09-11 (TaskList.find normalization)

Java 25.0.4 direct compilation of console sources + focused find scenario passed in isolated `ui-test-tasklist-find-workspace`.

- **Actual output:**

```text
Application exited with code 0 and returned matching task for case-insensitive FIND command.
```

- **Result:** PASS

## Test session record — 2026-09-11 (Tangent command-loop extraction)

Java 25.0.4 direct compilation of console sources + focused add/list/exit scenario passed.

- **Actual output:**

```text
Application exited with code 0 after adding, listing, and exiting normally.
```

- **Result:** PASS

## Test session record — 2026-09-11 (UI task-count formatter)

Java 25.0.4 direct compilation of console sources + focused add/delete/exit scenario passed.

- **Actual output:**

```text
Application exited with code 0; add and delete confirmations displayed correct task counts.
```

- **Result:** PASS

## Test session record — 2026-09-11 (status-command consolidation)

Java 25.0.4 direct compilation of console sources + focused mark/unmark/exit scenario passed.

- **Actual output:**

```text
Application exited with code 0; mark and unmark confirmations displayed correctly.
```

- **Result:** PASS

## Test session record — 2026-09-11 (quality pass)

Java 25.0.4 direct compilation of console sources + two focused scenarios passed.

- **Actual output:**

```text
Both cases exited with code 0: dated-task creation/listing, FIND, mark, unmark, delete, and exit.
```

- **Result:** PASS (2 cases)

## Test session record — 2026-09-11 (storage format constants)

Java 25.0.4 direct compilation of console sources + first planned case passed in isolated `ui-test-storage-constants-workspace`.

- **Actual output:**

```text
Application exited with code 0 after creating and listing ToDo, Deadline, and Event records.
```

- **Result:** PASS

## Test session record — 2026-09-11 (TaskList.find normalization)

Java 25.0.4 direct compilation of console sources + focused find scenario passed in isolated `ui-test-tasklist-find-workspace`.

- **Actual output:**

```text
Application exited with code 0 and returned matching task for case-insensitive FIND command.
```

- **Result:** PASS

## Test session record — 2026-09-11 (Tangent command-loop extraction)

Java 25.0.4 direct compilation of console sources + focused add/list/exit scenario passed.

- **Actual output:**

```text
Application exited with code 0 after adding, listing, and exiting normally.
```

- **Result:** PASS

## Test session record — 2026-09-11 (UI task-count formatter)

Java 25.0.4 direct compilation of console sources + focused add/delete/exit scenario passed.

- **Actual output:**

```text
Application exited with code 0; add and delete confirmations displayed correct task counts.
```

- **Result:** PASS

## Test session record — 2026-09-11 (status-command consolidation)

Java 25.0.4 direct compilation of console sources + focused mark/unmark/exit scenario passed.

- **Actual output:**

```text
Application exited with code 0; mark and unmark confirmations displayed correctly.
```

- **Result:** PASS

## Test session record — 2026-09-11 (quality pass)

Java 25.0.4 direct compilation of console sources + two focused scenarios passed.

- **Actual output:**

```text
Both cases exited with code 0: dated-task creation/listing, FIND, mark, unmark, delete, and exit.
```

- **Result:** PASS (2 cases)

## Test session record — 2026-09-11 (remaining quality fixes)

Java 25.0.4 direct compilation of console sources + combined command scenario passed.

- **Actual output:**

```text
Application exited with code 0; dated tasks, FIND, mark, unmark, delete, and exit worked.
```

- **Result:** PASS

## Test session record — 2026-09-11 (parseEvent readability refactor)

Java 25.0.4 direct compilation of console sources + both planned cases passed in isolated `ui-test-code-quality-workspace`.

### Attempted tests: create/list + reload/save mutations

- **Console input sent:**

```text
The exact inputs from the two planned test cases above, run in order.
```

- **Expected output:**

```text
The complete expected-output blocks from the corresponding planned test cases above.
```

- **Actual output:**

```text
Both cases matched their expected behavior; processes exited with code 0.
```

- **Result:** PASS (2 cases)

## Test session record — 2026-09-01 (parser refactor)

All three test cases passed in order with Java 25.0.4 in an isolated `ui-test-parser-refactor-workspace` directory. The actual output for each case exactly matched its complete expected-output block above.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: reload and save mutations

- **Console input sent:**

```text
mark 2
delete 1
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

## Test session record — 2026-09-11 (assertions)

Java 25.0.4 direct compilation and both planned console cases ran successfully. Gradle wrapper could not start because its lock directory was unavailable at `C:\.gradle`.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
Complete expected-output block for this test case above.
```

- **Actual output:**

```text
Process exited 0; welcome, task creation, list, and bye output produced.
```

- **Result:** PASS

### Attempted test: reload and save mutations

- **Console input sent:**

```text
mark 2
delete 1
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
Complete expected-output block for this test case above.
```

- **Actual output:**

```text
Process exited 0; mark, delete, invalid-date error, list, and bye output produced.
```

- **Result:** PASS

### Attempted test: unmark and reload status

- **Console input sent:**

```text
unmark 1
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: reload and save mutations

- **Console input sent:**

```text
mark 2
delete 1
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
Matches the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Matches the complete expected-output block for this test case above.
```

- **Result:** PASS

## Test case: reload and perform batch mutations

- **Aim:** Verify tasks reload, batch mark/unmark/delete use original indexes, idempotent status operations, and invalid calendar dates are rejected.
- **Inputs:**

```text
mark 2-3
mark 1-3
unmark 2-3
unmark 2-3
delete 1 3
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
i've marked 2 tasks as done!
[D][X] return book (by: Dec 02 2019, 6:00PM)
[E][X] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
i've marked 1 task as done!
[T][X] borrow book
the following task(s) were already marked as done:
[D][X] return book (by: Dec 02 2019, 6:00PM)
[E][X] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
i've marked 2 tasks as undone!
[D][ ] return book (by: Dec 02 2019, 6:00PM)
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
all selected tasks are already marked as undone!
____________________________________________________________
____________________________________________________________
got it! i've removed these tasks:
[T][X] borrow book
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
removed 2 tasks, 1 task(s) remaining!
____________________________________________________________
____________________________________________________________
invalid date :( ensure your date is a real calendar date (example: 31/02/2024 does not exist)
____________________________________________________________
____________________________________________________________
hello! here are your current tasks:
1. [D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

## Test case: unmark and reload status

- **Aim:** Verify unmark idempotence and incomplete status reload correctly.
- **Inputs:**

```text
unmark 1
list
bye
```

- **Expected output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
the selected task is already marked as undone!
____________________________________________________________
____________________________________________________________
hello! here are your current tasks:
1. [D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

## Test case: list with no tasks

- **Aim:** Verify listing an empty task list shows only the empty-list message.
- **Inputs:**

```text
delete 1
list
bye
```

- **Expected output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
got it! i've removed this task:
[D][ ] return book (by: Dec 02 2019, 6:00PM)
you now have no tasks in the list!
____________________________________________________________
____________________________________________________________
no tasks yet!
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

## Test session record — 2026-08-31 (tangent.ui.Ui extraction)

Both test cases passed in order with Java 25.0.4 in an isolated `ui-test-workspace` directory.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: reload and save mutations

- **Console input sent:**

```text
mark 2
delete 1
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

## Test session record — 2026-08-31 (tangent.storage.Storage extraction)

Both test cases passed in order with Java 25.0.4 in an isolated `ui-test-storage-workspace` directory.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: reload and save mutations

- **Console input sent:**

```text
mark 2
delete 1
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

## Test session record — 2026-08-31 (tangent.task.TaskList extraction)

Both test cases passed in order with Java 25.0.4 in an isolated `ui-test-tasklist-workspace` directory.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: reload and save mutations

- **Console input sent:**

```text
mark 2
delete 1
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

## Test session record — 2026-08-31 (tangent.parser.Parser extraction)

Both test cases passed in order with Java 25.0.4 in an isolated `ui-test-parser-workspace` directory.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: reload and save mutations

- **Console input sent:**

```text
mark 2
delete 1
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

## Test session record — 2026-08-31 (basic command dispatch)

Both test cases passed in order with Java 25.0.4 in an isolated `ui-test-command-basics-workspace` directory.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: reload and save mutations

- **Console input sent:**

```text
mark 2
delete 1
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

## Test session record — 2026-08-31 (status command dispatch)

All three test cases passed in order with Java 25.0.4 in an isolated `ui-test-command-status-final-workspace` directory.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: reload and save mutations

- **Console input sent:**

```text
mark 2
delete 1
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: unmark and reload status

- **Console input sent:**

```text
unmark 1
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

## Test session record — 2026-08-31 (delete command dispatch)

All three test cases passed in order with Java 25.0.4 in an isolated `ui-test-delete-command-workspace` directory.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: reload and save mutations

- **Console input sent:**

```text
mark 2
delete 1
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: unmark and reload status

- **Console input sent:**

```text
unmark 1
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

## Test session record — 2026-08-31 (add command dispatch)

All three test cases passed in order with Java 25.0.4 in an isolated `ui-test-add-command-workspace` directory.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: reload and save mutations

- **Console input sent:**

```text
mark 2
delete 1
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: unmark and reload status

- **Console input sent:**

```text
unmark 1
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

## Test session record — 2026-08-31 (parser command dispatch)

All three test cases passed in order with Java 25.0.4 in an isolated `ui-test-parser-dispatch-workspace` directory.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: reload and save mutations

- **Console input sent:**

```text
mark 2
delete 1
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS

### Attempted test: unmark and reload status

- **Console input sent:**

```text
unmark 1
list
bye
```

- **Expected output:**

```text
Exactly the complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exactly matched the complete expected-output block for this test case above.
```

- **Result:** PASS



## Test session record — 2026-09-18 (GUI command hint)

Java 25.0.4 direct console execution passed all three planned cases in order in isolated `ui-test-command-hint-workspace-3`. CRLF/LF line endings were normalized per plan rule; all other output matched exactly.

### Attempted test: create and list dated tasks

- **Aim:** Verify console output remains unchanged after adding GUI command hint.
- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[T][ ] borrow book
you now have 1 task in the list!
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[D][ ] return book (by: Dec 02 2019, 6:00PM)
you now have 2 tasks in the list!
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
you now have 3 tasks in the list!
____________________________________________________________
____________________________________________________________
1. [T][ ] borrow book
2. [D][ ] return book (by: Dec 02 2019, 6:00PM)
3. [E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

- **Actual output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[T][ ] borrow book
you now have 1 task in the list!
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[D][ ] return book (by: Dec 02 2019, 6:00PM)
you now have 2 tasks in the list!
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
you now have 3 tasks in the list!
____________________________________________________________
____________________________________________________________
1. [T][ ] borrow book
2. [D][ ] return book (by: Dec 02 2019, 6:00PM)
3. [E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

- **Result:** PASS

## Test session record — 2026-09-20 (find-result indexes)

Java 25.0.4 direct console compilation + focused UI scenario passed.

### Attempted scenario

- **Aim:** Use indexes from `find` result for mark, unmark, and delete.
- **Console input sent:**

```text
find wordle
mark 1
find wordle
unmark 1
find wordle
delete 1
list
bye
```

- **Expected output:** `mark 1` + `unmark 1` target `play wordle`; `delete 1` removes `play wordle`; list retains `first task` + `last task`.
- **Actual output:** Exact focused scenario match.
- **Result:** PASS

## Test session record — 2026-09-20 (stored invalid-date context)

Java 25.0.4 direct console compilation + exact comparison passed first planned case. Changed stored-invalid-date path was also smoke-tested: it reports data-file record instead of command-input date guidance.

### Attempted test: create and list dated tasks

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
Matches complete expected-output block for this test case above.
```

- **Actual output:**

```text
Exact match; process exited with code 0.
```

- **Result:** PASS

## Test session record — 2026-09-20 (date validation split)

Java 25.0.4 direct console compilation + exact comparison passed all four planned cases in order. Invalid calendar input `32/01/2024 1800` produced the new invalid-date message; malformed input retained bad-date-format message.

### Attempted tests

- **Inputs:** Complete input blocks from four planned cases above, in declared order.
- **Expected output:** Complete expected-output blocks from four planned cases above.
- **Actual output:** Exact match for each expected-output block.
- **Result:** PASS (4/4)

## Test session record — 2026-09-18 (idempotent status commands)

Java 25.0.4 exact console comparison passed all four planned cases in order in isolated `ui-test-status-idempotence-workspace-3`. CRLF/LF line endings normalized per plan rule; all other output matched exactly.

### Attempted tests: all planned cases

- **Aim:** Verify mark/unmark skip tasks already at requested status, report all-selected no-ops, preserve batch behavior, and keep persistence working.
- **Console input sent:** Exact input blocks from all four planned cases above, in declared order.
- **Expected output:** Complete expected-output blocks for corresponding planned cases above.
- **Actual output:** Exact match for all four cases; all processes exited with code 0.
- **Result:** PASS (4 cases)

## Test session record — 2026-09-18 (single + mixed status commands)

Java 25.0.4 exact console comparison passed all four planned cases in order in isolated `ui-test-status-idempotence-workspace-6`. CRLF/LF line endings normalized per plan rule; all other output matched exactly.

### Attempted tests: all planned cases

- **Aim:** Verify single-index no-op messages, mixed batch reporting, all-selected no-op messages, + persistence.
- **Console input sent:** Exact input blocks from all four planned cases above, in declared order.
- **Expected output:** Complete expected-output blocks for corresponding planned cases above.
- **Actual output:** Exact match for all four cases; all processes exited with code 0.
- **Result:** PASS (4 cases)

## Test session record — 2026-09-18 (empty list greeting fix)

Java 25.0.4 direct console execution passed all four planned cases in order in isolated `ui-test-empty-list-workspace`. CRLF/LF line endings normalized per plan rule; all other output matched expected output exactly.

### Attempted test: create and list dated tasks

- **Console input sent:** Same input block as planned.
- **Expected output:** Complete expected-output block under test case.
- **Actual output:** Complete output matched expected output.
- **Result:** PASS

### Attempted test: reload and perform batch mutations

- **Console input sent:** Same input block as planned.
- **Expected output:** Complete expected-output block under test case.
- **Actual output:** Complete output matched expected output.
- **Result:** PASS

### Attempted test: unmark and reload status

- **Console input sent:** Same input block as planned.
- **Expected output:** Complete expected-output block under test case.
- **Actual output:** Complete output matched expected output.
- **Result:** PASS

### Attempted test: list with no tasks

- **Console input sent:**

```text
delete 1
list
bye
```

- **Expected output:** Complete expected-output block under test case.
- **Actual output:** Complete output matched expected output; empty `list` output was `no tasks yet!` with no greeting.
- **Result:** PASS

### Attempted test: reload and perform batch mutations

- **Aim:** Verify reload, batch mutation, invalid-date handling, + unchanged console output.
- **Console input sent:**

```text
mark 2-3
unmark 2-3
delete 1 3
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
i've marked 2 tasks as done!
[D][X] return book (by: Dec 02 2019, 6:00PM)
[E][X] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
i've marked 2 tasks as undone!
[D][ ] return book (by: Dec 02 2019, 6:00PM)
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
got it! i've removed these tasks:
[T][ ] borrow book
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
removed 2 tasks, 1 tasks remaining!
____________________________________________________________
____________________________________________________________
bad date format :( ensure your dates are in the format DD/MM/YYYY HHmm (example: 07/06/2026 2200)
____________________________________________________________
____________________________________________________________
1. [D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

- **Actual output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
i've marked 2 tasks as done!
[D][X] return book (by: Dec 02 2019, 6:00PM)
[E][X] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
i've marked 2 tasks as undone!
[D][ ] return book (by: Dec 02 2019, 6:00PM)
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
got it! i've removed these tasks:
[T][ ] borrow book
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
removed 2 tasks, 1 tasks remaining!
____________________________________________________________
____________________________________________________________
bad date format :( ensure your dates are in the format DD/MM/YYYY HHmm (example: 07/06/2026 2200)
____________________________________________________________
____________________________________________________________
1. [D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

- **Result:** PASS

### Attempted test: unmark and reload status

- **Aim:** Verify persisted task status + unchanged console output.
- **Console input sent:**

```text
unmark 1
list
bye
```

- **Expected output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
i've marked 1 task as undone!
[D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
1. [D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

- **Actual output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
i've marked 1 task as undone!
[D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
1. [D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

- **Result:** PASS



## Test session record — 2026-09-18 (list command greeting)

Java 25.0.4 direct console execution passed all three planned cases in order in isolated `ui-test-list-greeting-workspace-2`. CRLF/LF line endings normalized per plan rule; all other output matched exactly.

### Attempted test: create and list dated tasks

- **Aim:** Verify list command greeting appears without changing other console output.

- **Console input sent:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
list
bye
```

- **Expected output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[T][ ] borrow book
you now have 1 task in the list!
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[D][ ] return book (by: Dec 02 2019, 6:00PM)
you now have 2 tasks in the list!
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
you now have 3 tasks in the list!
____________________________________________________________
____________________________________________________________
hello! here are your current tasks:
1. [T][ ] borrow book
2. [D][ ] return book (by: Dec 02 2019, 6:00PM)
3. [E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

- **Actual output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[T][ ] borrow book
you now have 1 task in the list!
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[D][ ] return book (by: Dec 02 2019, 6:00PM)
you now have 2 tasks in the list!
____________________________________________________________
____________________________________________________________
got it! you have a new task: 
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
you now have 3 tasks in the list!
____________________________________________________________
____________________________________________________________
hello! here are your current tasks:
1. [T][ ] borrow book
2. [D][ ] return book (by: Dec 02 2019, 6:00PM)
3. [E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

- **Result:** PASS

### Attempted test: reload and perform batch mutations

- **Aim:** Verify reload, batch mutation, invalid-date handling, + list greeting.

- **Console input sent:**

```text
mark 2-3
unmark 2-3
delete 1 3
deadline impossible /by 31/2/2019 1800
list
bye
```

- **Expected output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
i've marked 2 tasks as done!
[D][X] return book (by: Dec 02 2019, 6:00PM)
[E][X] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
i've marked 2 tasks as undone!
[D][ ] return book (by: Dec 02 2019, 6:00PM)
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
got it! i've removed these tasks:
[T][ ] borrow book
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
removed 2 tasks, 1 task(s) remaining!
____________________________________________________________
____________________________________________________________
bad date format :( ensure your dates are in the format DD/MM/YYYY HHmm (example: 07/06/2026 2200)
____________________________________________________________
____________________________________________________________
hello! here are your current tasks:
1. [D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

- **Actual output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
i've marked 2 tasks as done!
[D][X] return book (by: Dec 02 2019, 6:00PM)
[E][X] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
i've marked 2 tasks as undone!
[D][ ] return book (by: Dec 02 2019, 6:00PM)
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
____________________________________________________________
____________________________________________________________
got it! i've removed these tasks:
[T][ ] borrow book
[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)
removed 2 tasks, 1 task(s) remaining!
____________________________________________________________
____________________________________________________________
bad date format :( ensure your dates are in the format DD/MM/YYYY HHmm (example: 07/06/2026 2200)
____________________________________________________________
____________________________________________________________
hello! here are your current tasks:
1. [D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

- **Result:** PASS

### Attempted test: unmark and reload status

- **Aim:** Verify persisted task status + list greeting.

- **Console input sent:**

```text
unmark 1
list
bye
```

- **Expected output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
i've marked 1 task as undone!
[D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
hello! here are your current tasks:
1. [D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

- **Actual output:**

```text
____________________________________________________________
████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
   ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
   ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
   ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝
good morning/afternoon/evening ^-^ I'm TANGENT.
what do you want me to do?
____________________________________________________________
____________________________________________________________
i've marked 1 task as undone!
[D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
hello! here are your current tasks:
1. [D][ ] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
bye o/ hope to see you again soon
____________________________________________________________
```

- **Result:** PASS
