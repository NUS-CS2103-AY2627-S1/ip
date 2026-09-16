# Tasque Console UI Test Plan

This file records persistent console regression tests. Expected-output blocks
contain Tasque process stdout only; input echo is shown separately by the test
runner.

## TC-EH-01: Preserve internal text while accepting command-edge whitespace

**Aim:** Accept padded commands and aliases, retain meaningful internal spaces, and stop at padded bye.

### Inputs

```console-input
   todo read  book   
   l   
   m   1   
   u 1   
   f read  book   
   bye   
todo should not be added
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[T][ ] read  book
Tasks: 1
Tasks:
1.[T][ ] read  book
Task completed.
[T][X] read  book
Task reopened.
[T][ ] read  book
Matches:
1.[T][ ] read  book

Whew that was exhausting! Good luck with the tasks!
```

## TC-EH-02: Reject reversed Events and accept same-day Events

**Aim:** Report reversed dates without adding a task, while accepting equal dates and distinct date ranges.

### Inputs

```console-input
event meeting /from 2026-08-29 /to 2026-08-28
event meeting /from 2026-08-28 /to 2026-08-28
e meeting /from 2026-08-28 /to 2026-08-29
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Unless you've invented time travel, the end date can't be before the start date.
Task registered.
[E][ ] meeting (from: Aug 28 2026 to: Aug 28 2026)
Tasks: 1
Task registered.
[E][ ] meeting (from: Aug 28 2026 to: Aug 29 2026)
Tasks: 2
Tasks:
1.[E][ ] meeting (from: Aug 28 2026 to: Aug 28 2026)
2.[E][ ] meeting (from: Aug 28 2026 to: Aug 29 2026)

Whew that was exhausting! Good luck with the tasks!
```

## TC-EH-03: Reject exact active duplicates and allow a completed task to repeat

**Aim:** Reject duplicate additions of all three task types through aliases and allow a Todo to repeat after completion.

### Inputs

```console-input
todo read book
t read book
deadline report /by 2026-08-26
d report /by 2026-08-26
event meeting /from 2026-08-26 /to 2026-08-27
e meeting /from 2026-08-26 /to 2026-08-27
mark 1
todo read book
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[T][ ] read book
Tasks: 1
Already got that one.
Task registered.
[D][ ] report (by: Aug 26 2026)
Tasks: 2
Already got that one.
Task registered.
[E][ ] meeting (from: Aug 26 2026 to: Aug 27 2026)
Tasks: 3
Already got that one.
Task completed.
[T][X] read book
Task registered.
[T][ ] read book
Tasks: 4
Tasks:
1.[T][X] read book
2.[D][ ] report (by: Aug 26 2026)
3.[E][ ] meeting (from: Aug 26 2026 to: Aug 27 2026)
4.[T][ ] read book

Whew that was exhausting! Good luck with the tasks!
```

## TC-EH-04: Exit with a padded short alias

**Aim:** Recognize padded b as exit and never process the later task addition.

### Inputs

```console-input
   b   
todo should not be added
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.

Whew that was exhausting! Good luck with the tasks!
```

## TC-L9-01: Find matching tasks

**Aim:** Find all tasks whose descriptions contain a keyword and number only the matching results.

### Inputs

```console-input
todo read book
deadline return book /by 2026-08-26
todo submit report
find book
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[T][ ] read book
Tasks: 1
Task registered.
[D][ ] return book (by: Aug 26 2026)
Tasks: 2
Task registered.
[T][ ] submit report
Tasks: 3
Matches:
1.[T][ ] read book
2.[D][ ] return book (by: Aug 26 2026)

Whew that was exhausting! Good luck with the tasks!
```

## TC-L9-02: Find with no matches

**Aim:** Show an empty matching-task list without treating a no-match search as an error.

### Inputs

```console-input
todo read book
find report
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[T][ ] read book
Tasks: 1
Matches:

Whew that was exhausting! Good luck with the tasks!
```

## TC-L9-03: Reject blank find keyword

**Aim:** Reject missing or blank Find keywords while keeping Tasque running.

### Inputs

```console-input
find
find   
todo read book
find book
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
The find command needs a keyword.
The find command needs a keyword.
Task registered.
[T][ ] read book
Tasks: 1
Matches:
1.[T][ ] read book

Whew that was exhausting! Good luck with the tasks!
```

## TC-L3-01: Add, list, mark, and unmark a task

**Aim:** Preserve the completed Level-0 to Level-3 console workflow.

### Inputs

```console-input
todo read book
list
mark 1
unmark 1
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[T][ ] read book
Tasks: 1
Tasks:
1.[T][ ] read book
Task completed.
[T][X] read book
Task reopened.
[T][ ] read book

Whew that was exhausting! Good luck with the tasks!
```

## TC-L8-01: Parse and format ISO deadline dates

**Aim:** Verify that a valid ISO deadline date is parsed as a date and displayed in the user-facing `MMM d yyyy` format.

### Inputs

```console-input
deadline submit report /by 2026-08-26
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[D][ ] submit report (by: Aug 26 2026)
Tasks: 1
Tasks:
1.[D][ ] submit report (by: Aug 26 2026)

Whew that was exhausting! Good luck with the tasks!
```

## TC-L8-02: Persist ISO deadline dates across restart

**Aim:** Verify that a deadline is stored in ISO form and restored with the same formatted display after restarting Tasque.

### Inputs

```console-input
deadline submit report /by 2026-08-26
bye
--- RESTART ---
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[D][ ] submit report (by: Aug 26 2026)
Tasks: 1

Whew that was exhausting! Good luck with the tasks!
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Tasks:
1.[D][ ] submit report (by: Aug 26 2026)

Whew that was exhausting! Good luck with the tasks!
```

## TC-L8-03: Reject invalid deadline dates and continue

**Aim:** Reject textual and impossible ISO-looking dates while keeping Tasque running and allowing a later valid deadline.

### Inputs

```console-input
deadline invalid text /by tomorrow
deadline invalid date /by 2026-99-99
deadline valid date /by 2026-08-26
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Enter a valid date in yyyy-MM-dd format.
Enter a valid date in yyyy-MM-dd format.
Task registered.
[D][ ] valid date (by: Aug 26 2026)
Tasks: 1
Tasks:
1.[D][ ] valid date (by: Aug 26 2026)

Whew that was exhausting! Good luck with the tasks!
```

## TC-L8-04: Parse, validate, and persist event dates

**Aim:** Reject invalid Event dates, format valid ISO dates, and restore them after restarting Tasque.

### Inputs

```console-input
event invalid text /from tomorrow /to 2026-08-29
event invalid date /from 2026-99-99 /to 2026-08-29
event project meeting /from 2026-08-28 /to 2026-08-29
bye
--- RESTART ---
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Enter valid dates in yyyy-MM-dd format.
Enter valid dates in yyyy-MM-dd format.
Task registered.
[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)
Tasks: 1

Whew that was exhausting! Good luck with the tasks!
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Tasks:
1.[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)

Whew that was exhausting! Good luck with the tasks!
```

## TC-L7-01: Persist tasks across restart

**Aim:** Verify that Todo, Deadline, and Event tasks and their details are restored after restarting Tasque.

### Inputs

```console-input
todo read book
deadline submit report /by 2026-08-26
event team meeting /from 2026-08-28 /to 2026-08-29
bye
--- RESTART ---
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[T][ ] read book
Tasks: 1
Task registered.
[D][ ] submit report (by: Aug 26 2026)
Tasks: 2
Task registered.
[E][ ] team meeting (from: Aug 28 2026 to: Aug 29 2026)
Tasks: 3

Whew that was exhausting! Good luck with the tasks!
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Tasks:
1.[T][ ] read book
2.[D][ ] submit report (by: Aug 26 2026)
3.[E][ ] team meeting (from: Aug 28 2026 to: Aug 29 2026)

Whew that was exhausting! Good luck with the tasks!
```

## TC-L7-02: Persist task descriptions containing pipes

**Aim:** Preserve pipe characters in Todo, Deadline, and Event descriptions across a restart.

### Inputs

```console-input
todo read | book
deadline submit | report /by 2026-08-28
event team | meeting /from 2026-08-28 /to 2026-08-29
bye
--- RESTART ---
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[T][ ] read | book
Tasks: 1
Task registered.
[D][ ] submit | report (by: Aug 28 2026)
Tasks: 2
Task registered.
[E][ ] team | meeting (from: Aug 28 2026 to: Aug 29 2026)
Tasks: 3

Whew that was exhausting! Good luck with the tasks!
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Tasks:
1.[T][ ] read | book
2.[D][ ] submit | report (by: Aug 28 2026)
3.[E][ ] team | meeting (from: Aug 28 2026 to: Aug 29 2026)

Whew that was exhausting! Good luck with the tasks!
```

## TC-L5-01: Reject empty Todo descriptions

**Aim:** Reject missing or blank Todo descriptions without terminating Tasque or adding a task.

### Inputs

```console-input
todo
todo   
todo read book
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
The description of a todo cannot be empty.
The description of a todo cannot be empty.
Task registered.
[T][ ] read book
Tasks: 1
Tasks:
1.[T][ ] read book

Whew that was exhausting! Good luck with the tasks!
```

## TC-L5-02: Reject incomplete Deadlines

**Aim:** Reject Deadlines with missing descriptions, missing `/by`, or empty `/by` values.

### Inputs

```console-input
deadline
deadline /by Sunday
deadline return book
deadline return book /by
deadline return book /by 2026-08-26
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
The description of a deadline cannot be empty.
The description of a deadline cannot be empty.
A deadline must include /by followed by when it is due.
The /by value of a deadline cannot be empty.
Task registered.
[D][ ] return book (by: Aug 26 2026)
Tasks: 1
Tasks:
1.[D][ ] return book (by: Aug 26 2026)

Whew that was exhausting! Good luck with the tasks!
```

## TC-L5-03: Reject incomplete Events

**Aim:** Reject Events with missing descriptions, missing delimiters, or empty start/end values.

### Inputs

```console-input
event
event /from 2026-08-28 /to 2026-08-29
event project meeting /to 2026-08-29
event project meeting /from 2026-08-28
event project meeting /from /to 2026-08-29
event project meeting /from 2026-08-28 /to
event project meeting /from 2026-08-28 /to 2026-08-29
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
The description of an event cannot be empty.
The description of an event cannot be empty.
An event must include /from followed by its start.
An event must include /to followed by its end.
The /from value of an event cannot be empty.
The /to value of an event cannot be empty.
Task registered.
[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)
Tasks: 1
Tasks:
1.[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)

Whew that was exhausting! Good luck with the tasks!
```

## TC-L5-04: Reject invalid commands and task numbers

**Aim:** Reject unknown commands and invalid mark/unmark task numbers without changing tasks or terminating Tasque.

### Inputs

```console-input
blah
mark
unmark
mark abc
unmark xyz
todo read book
mark 0
unmark -1
mark 2
unmark 2
mark 1
unmark 1
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Yeah, I don't know what that means.
The mark command needs a task number.
The unmark command needs a task number.
The task number must be a positive whole number.
The task number must be a positive whole number.
Task registered.
[T][ ] read book
Tasks: 1
The task number must be a positive whole number.
The task number must be a positive whole number.
Task 2 doesn't exist.
Task 2 doesn't exist.
Task completed.
[T][X] read book
Task reopened.
[T][ ] read book
Tasks:
1.[T][ ] read book

Whew that was exhausting! Good luck with the tasks!
```

## TC-L4-01: Create and list a Todo using aliases

**Aim:** Verify Todo creation and list output using aliases, and exit at b without processing later input.

### Inputs

```console-input
t borrow book
l
b
todo should not be added
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[T][ ] borrow book
Tasks: 1
Tasks:
1.[T][ ] borrow book

Whew that was exhausting! Good luck with the tasks!
```

## TC-L4-02: Create and list a Deadline

**Aim:** Verify Deadline creation, the `[D]` type marker, and `/by` information.

### Inputs

```console-input
deadline return book /by 2026-08-26
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[D][ ] return book (by: Aug 26 2026)
Tasks: 1
Tasks:
1.[D][ ] return book (by: Aug 26 2026)

Whew that was exhausting! Good luck with the tasks!
```

## TC-L4-03: Create and list an Event

**Aim:** Verify Event creation, the `[E]` type marker, and `/from` and `/to` information.

### Inputs

```console-input
event project meeting /from 2026-08-28 /to 2026-08-29
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)
Tasks: 1
Tasks:
1.[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)

Whew that was exhausting! Good luck with the tasks!
```

## TC-L4-04: Mark and unmark a mixed task list

**Aim:** Verify polymorphic list display and mark/unmark behavior for Todo, Deadline, and Event.

### Inputs

```console-input
todo borrow book
deadline return book /by 2026-08-26
event project meeting /from 2026-08-28 /to 2026-08-29
mark 1
mark 2
mark 3
list
unmark 1
unmark 2
unmark 3
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[T][ ] borrow book
Tasks: 1
Task registered.
[D][ ] return book (by: Aug 26 2026)
Tasks: 2
Task registered.
[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)
Tasks: 3
Task completed.
[T][X] borrow book
Task completed.
[D][X] return book (by: Aug 26 2026)
Task completed.
[E][X] project meeting (from: Aug 28 2026 to: Aug 29 2026)
Tasks:
1.[T][X] borrow book
2.[D][X] return book (by: Aug 26 2026)
3.[E][X] project meeting (from: Aug 28 2026 to: Aug 29 2026)
Task reopened.
[T][ ] borrow book
Task reopened.
[D][ ] return book (by: Aug 26 2026)
Task reopened.
[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)
Tasks:
1.[T][ ] borrow book
2.[D][ ] return book (by: Aug 26 2026)
3.[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)

Whew that was exhausting! Good luck with the tasks!
```

## TC-L6-01: Delete the only task

**Aim:** Delete the only task, report the removed task, and leave an empty task list.

### Inputs

```console-input
todo read book
delete 1
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[T][ ] read book
Tasks: 1
Task removed.
[T][ ] read book
Tasks: 0
Tasks:

Whew that was exhausting! Good luck with the tasks!
```

## TC-L6-02: Delete first and middle tasks

**Aim:** Preserve order, renumber remaining tasks, and support mark/unmark after deletion.

### Inputs

```console-input
todo first task
deadline second task /by 2026-08-26
event third task /from 2026-08-28 /to 2026-08-29
todo fourth task
delete 1
list
delete 2
list
mark 2
unmark 2
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
Task registered.
[T][ ] first task
Tasks: 1
Task registered.
[D][ ] second task (by: Aug 26 2026)
Tasks: 2
Task registered.
[E][ ] third task (from: Aug 28 2026 to: Aug 29 2026)
Tasks: 3
Task registered.
[T][ ] fourth task
Tasks: 4
Task removed.
[T][ ] first task
Tasks: 3
Tasks:
1.[D][ ] second task (by: Aug 26 2026)
2.[E][ ] third task (from: Aug 28 2026 to: Aug 29 2026)
3.[T][ ] fourth task
Task removed.
[E][ ] third task (from: Aug 28 2026 to: Aug 29 2026)
Tasks: 2
Tasks:
1.[D][ ] second task (by: Aug 26 2026)
2.[T][ ] fourth task
Task completed.
[T][X] fourth task
Task reopened.
[T][ ] fourth task
Tasks:
1.[D][ ] second task (by: Aug 26 2026)
2.[T][ ] fourth task

Whew that was exhausting! Good luck with the tasks!
```

## TC-L6-03: Reject invalid delete commands

**Aim:** Reject invalid delete task numbers without modifying tasks or terminating Tasque.

### Inputs

```console-input
delete
delete abc
delete 0
delete -1
delete 1
todo read book
delete 2
list
bye
```

### Expected output

```console-output
========================================
                 TASQUE                 
========================================

Hey, I'm Tasque.
Ready when you are.
The delete command needs a task number.
The task number must be a positive whole number.
The task number must be a positive whole number.
The task number must be a positive whole number.
Task 1 doesn't exist.
Task registered.
[T][ ] read book
Tasks: 1
Task 2 doesn't exist.
Tasks:
1.[T][ ] read book

Whew that was exhausting! Good luck with the tasks!
```
