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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[T][ ] read  book
Now you have 1 tasks in the list
Here are the tasks in your list:
1.[T][ ] read  book
Nice! I've marked this task as done:
[T][X] read  book
OK, I've marked this task as not done yet:
[T][ ] read  book
Here are the matching tasks in your list:
1.[T][ ] read  book

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
OOPS!!! The end date of an event cannot be before its start date.
Got it. I've added this task:
[E][ ] meeting (from: Aug 28 2026 to: Aug 28 2026)
Now you have 1 tasks in the list
Got it. I've added this task:
[E][ ] meeting (from: Aug 28 2026 to: Aug 29 2026)
Now you have 2 tasks in the list
Here are the tasks in your list:
1.[E][ ] meeting (from: Aug 28 2026 to: Aug 28 2026)
2.[E][ ] meeting (from: Aug 28 2026 to: Aug 29 2026)

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list
OOPS!!! That task already exists in your task list.
Got it. I've added this task:
[D][ ] report (by: Aug 26 2026)
Now you have 2 tasks in the list
OOPS!!! That task already exists in your task list.
Got it. I've added this task:
[E][ ] meeting (from: Aug 26 2026 to: Aug 27 2026)
Now you have 3 tasks in the list
OOPS!!! That task already exists in your task list.
Nice! I've marked this task as done:
[T][X] read book
Got it. I've added this task:
[T][ ] read book
Now you have 4 tasks in the list
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] report (by: Aug 26 2026)
3.[E][ ] meeting (from: Aug 26 2026 to: Aug 27 2026)
4.[T][ ] read book

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list
Got it. I've added this task:
[D][ ] return book (by: Aug 26 2026)
Now you have 2 tasks in the list
Got it. I've added this task:
[T][ ] submit report
Now you have 3 tasks in the list
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Aug 26 2026)

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list
Here are the matching tasks in your list:

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
OOPS!!! The find command needs a keyword.
OOPS!!! The find command needs a keyword.
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list
Here are the matching tasks in your list:
1.[T][ ] read book

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list
Here are the tasks in your list:
1.[T][ ] read book
Nice! I've marked this task as done:
[T][X] read book
OK, I've marked this task as not done yet:
[T][ ] read book

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[D][ ] submit report (by: Aug 26 2026)
Now you have 1 tasks in the list
Here are the tasks in your list:
1.[D][ ] submit report (by: Aug 26 2026)

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[D][ ] submit report (by: Aug 26 2026)
Now you have 1 tasks in the list

Goodbye! See you again soon.
========================================
                 TASQUE                 
========================================

Hello! I'm Tasque.
What can I do for you?
Here are the tasks in your list:
1.[D][ ] submit report (by: Aug 26 2026)

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
OOPS!!! Please enter the date as yyyy-MM-dd.
OOPS!!! Please enter the date as yyyy-MM-dd.
Got it. I've added this task:
[D][ ] valid date (by: Aug 26 2026)
Now you have 1 tasks in the list
Here are the tasks in your list:
1.[D][ ] valid date (by: Aug 26 2026)

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
OOPS!!! Please enter the dates as yyyy-MM-dd.
OOPS!!! Please enter the dates as yyyy-MM-dd.
Got it. I've added this task:
[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)
Now you have 1 tasks in the list

Goodbye! See you again soon.
========================================
                 TASQUE                 
========================================

Hello! I'm Tasque.
What can I do for you?
Here are the tasks in your list:
1.[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list
Got it. I've added this task:
[D][ ] submit report (by: Aug 26 2026)
Now you have 2 tasks in the list
Got it. I've added this task:
[E][ ] team meeting (from: Aug 28 2026 to: Aug 29 2026)
Now you have 3 tasks in the list

Goodbye! See you again soon.
========================================
                 TASQUE                 
========================================

Hello! I'm Tasque.
What can I do for you?
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] submit report (by: Aug 26 2026)
3.[E][ ] team meeting (from: Aug 28 2026 to: Aug 29 2026)

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[T][ ] read | book
Now you have 1 tasks in the list
Got it. I've added this task:
[D][ ] submit | report (by: Aug 28 2026)
Now you have 2 tasks in the list
Got it. I've added this task:
[E][ ] team | meeting (from: Aug 28 2026 to: Aug 29 2026)
Now you have 3 tasks in the list

Goodbye! See you again soon.
========================================
                 TASQUE                 
========================================

Hello! I'm Tasque.
What can I do for you?
Here are the tasks in your list:
1.[T][ ] read | book
2.[D][ ] submit | report (by: Aug 28 2026)
3.[E][ ] team | meeting (from: Aug 28 2026 to: Aug 29 2026)

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
OOPS!!! The description of a todo cannot be empty.
OOPS!!! The description of a todo cannot be empty.
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list
Here are the tasks in your list:
1.[T][ ] read book

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
OOPS!!! The description of a deadline cannot be empty.
OOPS!!! The description of a deadline cannot be empty.
OOPS!!! A deadline must include /by followed by when it is due.
OOPS!!! The /by value of a deadline cannot be empty.
Got it. I've added this task:
[D][ ] return book (by: Aug 26 2026)
Now you have 1 tasks in the list
Here are the tasks in your list:
1.[D][ ] return book (by: Aug 26 2026)

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
OOPS!!! The description of an event cannot be empty.
OOPS!!! The description of an event cannot be empty.
OOPS!!! An event must include /from followed by its start.
OOPS!!! An event must include /to followed by its end.
OOPS!!! The /from value of an event cannot be empty.
OOPS!!! The /to value of an event cannot be empty.
Got it. I've added this task:
[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)
Now you have 1 tasks in the list
Here are the tasks in your list:
1.[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
OOPS!!! I do not recognize that command.
OOPS!!! The mark command needs a task number.
OOPS!!! The unmark command needs a task number.
OOPS!!! The task number must be a positive whole number.
OOPS!!! The task number must be a positive whole number.
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list
OOPS!!! The task number must be a positive whole number.
OOPS!!! The task number must be a positive whole number.
OOPS!!! Task 2 does not exist in the list.
OOPS!!! Task 2 does not exist in the list.
Nice! I've marked this task as done:
[T][X] read book
OK, I've marked this task as not done yet:
[T][ ] read book
Here are the tasks in your list:
1.[T][ ] read book

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list
Here are the tasks in your list:
1.[T][ ] borrow book

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[D][ ] return book (by: Aug 26 2026)
Now you have 1 tasks in the list
Here are the tasks in your list:
1.[D][ ] return book (by: Aug 26 2026)

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)
Now you have 1 tasks in the list
Here are the tasks in your list:
1.[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list
Got it. I've added this task:
[D][ ] return book (by: Aug 26 2026)
Now you have 2 tasks in the list
Got it. I've added this task:
[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)
Now you have 3 tasks in the list
Nice! I've marked this task as done:
[T][X] borrow book
Nice! I've marked this task as done:
[D][X] return book (by: Aug 26 2026)
Nice! I've marked this task as done:
[E][X] project meeting (from: Aug 28 2026 to: Aug 29 2026)
Here are the tasks in your list:
1.[T][X] borrow book
2.[D][X] return book (by: Aug 26 2026)
3.[E][X] project meeting (from: Aug 28 2026 to: Aug 29 2026)
OK, I've marked this task as not done yet:
[T][ ] borrow book
OK, I've marked this task as not done yet:
[D][ ] return book (by: Aug 26 2026)
OK, I've marked this task as not done yet:
[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Aug 26 2026)
3.[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list
Noted. I've removed this task:
[T][ ] read book
Now you have 0 tasks in the list
Here are the tasks in your list:

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
Got it. I've added this task:
[T][ ] first task
Now you have 1 tasks in the list
Got it. I've added this task:
[D][ ] second task (by: Aug 26 2026)
Now you have 2 tasks in the list
Got it. I've added this task:
[E][ ] third task (from: Aug 28 2026 to: Aug 29 2026)
Now you have 3 tasks in the list
Got it. I've added this task:
[T][ ] fourth task
Now you have 4 tasks in the list
Noted. I've removed this task:
[T][ ] first task
Now you have 3 tasks in the list
Here are the tasks in your list:
1.[D][ ] second task (by: Aug 26 2026)
2.[E][ ] third task (from: Aug 28 2026 to: Aug 29 2026)
3.[T][ ] fourth task
Noted. I've removed this task:
[E][ ] third task (from: Aug 28 2026 to: Aug 29 2026)
Now you have 2 tasks in the list
Here are the tasks in your list:
1.[D][ ] second task (by: Aug 26 2026)
2.[T][ ] fourth task
Nice! I've marked this task as done:
[T][X] fourth task
OK, I've marked this task as not done yet:
[T][ ] fourth task
Here are the tasks in your list:
1.[D][ ] second task (by: Aug 26 2026)
2.[T][ ] fourth task

Goodbye! See you again soon.
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

Hello! I'm Tasque.
What can I do for you?
OOPS!!! The delete command needs a task number.
OOPS!!! The task number must be a positive whole number.
OOPS!!! The task number must be a positive whole number.
OOPS!!! The task number must be a positive whole number.
OOPS!!! Task 1 does not exist in the list.
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list
OOPS!!! Task 2 does not exist in the list.
Here are the tasks in your list:
1.[T][ ] read book

Goodbye! See you again soon.
```
