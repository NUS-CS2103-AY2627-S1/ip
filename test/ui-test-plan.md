# UI Test Plan

## Program command

```text
java -cp build/classes/java/main;build/classes/java/test rei.UiTestLauncher
```

The command assumes the project has been compiled with `gradlew test` using Java 25.
The test launcher removes the test data before each case so cases remain independent.

## Test case: Add tasks and list them

**Aim:** Verify that entered task text is stored and later displayed in entry order.

**Input:**

```text
todo read book
todo return book
list
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
------------------------------------------------------------
One less thing to remember. I've added: [T][ ] read book
You have a total of 1 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [T][ ] return book
You have a total of 2 tasks in the list.
------------------------------------------------------------
Here are the tasks in your list:
No. of tasks: 2
1.[T][ ] read book
2.[T][ ] return book
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```

## Test case: Tag and find tasks

**Aim:** Verify that tags are normalized, displayed, searchable, persisted, and validated without duplicating tags.

**Input:**

```text
todo prepare slides
todo buy snacks
tag 1 #School
tag 2 #personal
tag 1 #SCHOOL
tag 2 personal
list
find #school
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
One less thing to remember. I've added: [T][ ] prepare slides
You have a total of 1 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [T][ ] buy snacks
You have a total of 2 tasks in the list.
------------------------------------------------------------
Added #school to task 1:
[T][ ] prepare slides #school
------------------------------------------------------------
Added #personal to task 2:
[T][ ] buy snacks #personal
------------------------------------------------------------
Task 1 already has tag #school.
------------------------------------------------------------
A tag must start with # and contain only letters, numbers, _ or -. Try: tag 1 #school
------------------------------------------------------------
Here are the tasks in your list:
No. of tasks: 2
1.[T][ ] prepare slides #school
2.[T][ ] buy snacks #personal
------------------------------------------------------------
Here are the matching tasks in your list:
1.[T][ ] prepare slides #school
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```

## Test case: Preserve state for empty-list, repeated-status, and overflow operations

**Aim:** Verify that invalid operations leave an empty list unchanged, repeated status commands do not duplicate tasks, deleting a completed task works, and an oversized task number does not crash or mutate the list.

**Input:**

```text
delete 1
mark 1
unmark 1
list
todo alpha
mark 1
mark 1
list
delete 1
list
todo beta
unmark 1
unmark 1
delete 999999999999999999999
list
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
Task 1 does not exist.
Let's try that again!
------------------------------------------------------------
Task 1 does not exist.
Let's try that again!
------------------------------------------------------------
Task 1 does not exist.
Let's try that again!
------------------------------------------------------------
Your list is clear. Add a task whenever you are ready.
------------------------------------------------------------
One less thing to remember. I've added: [T][ ] alpha
You have a total of 1 tasks in the list.
------------------------------------------------------------
A little progress counts. Marked as done!

[T][X] alpha
------------------------------------------------------------
A little progress counts. Marked as done!

[T][X] alpha
------------------------------------------------------------
Here are the tasks in your list:
No. of tasks: 1
1.[T][X] alpha
------------------------------------------------------------
Making room. Removed this task from your list:
  [T][X] alpha
Now you have 0 tasks in the list.
------------------------------------------------------------
Your list is clear. Add a task whenever you are ready.
------------------------------------------------------------
One less thing to remember. I've added: [T][ ] beta
You have a total of 1 tasks in the list.
------------------------------------------------------------
No rush. Put it back on your list.

[T][ ] beta
------------------------------------------------------------
No rush. Put it back on your list.

[T][ ] beta
------------------------------------------------------------
Try a number from 1 to 1!
------------------------------------------------------------
Here are the tasks in your list:
No. of tasks: 1
1.[T][ ] beta
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```

## Test case: Handle whitespace and case-sensitive commands without corrupting state

**Aim:** Verify that leading, trailing, and repeated spaces are accepted while an unsupported command casing does not add a task.

**Input:**

```text
  todo   spaced task  
TODO uppercase task
list
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
One less thing to remember. I've added: [T][ ] spaced task
You have a total of 1 tasks in the list.
------------------------------------------------------------
I'm sorry, I don't know what is 'TODO'. Try todo, deadline, event, list, find, delete, mark, unmark, tag, or bye.
------------------------------------------------------------
Here are the tasks in your list:
No. of tasks: 1
1.[T][ ] spaced task
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```

## Test case: Reject malformed deadline and event markers

**Aim:** Verify that `/by`, `/from`, and `/to` must be standalone markers and malformed input does not add a task.

**Input:**

```text
deadline task /byFriday
event meeting /fromMonday /to Tuesday
list
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
A deadline needs a description and a by date. 
Try: deadline {your task} /by {deadline}
------------------------------------------------------------
An event needs a description, start, and end time. 
Try: event {task} /from {start} /to {end}
------------------------------------------------------------
Your list is clear. Add a task whenever you are ready.
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```

## Test case: Delete tasks without corrupting task order

**Aim:** Verify that deletion removes the selected task, shifts later tasks into the correct positions, and rejects invalid deletion requests without changing the list.

**Input:**

```text
todo read book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 1000 /to 3/12/2019 1200
todo borrow book
delete 3
list
delete 3
list
delete 4
delete first
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
One less thing to remember. I've added: [T][ ] read book
You have a total of 1 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [D][ ] return book (by: Dec 02 2019, 6:00 PM)
You have a total of 2 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [E][ ] project meeting (from: Dec 03 2019, 10:00 AM to: Dec 03 2019, 12:00 PM)
You have a total of 3 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [T][ ] borrow book
You have a total of 4 tasks in the list.
------------------------------------------------------------
Making room. Removed this task from your list:
  [E][ ] project meeting (from: Dec 03 2019, 10:00 AM to: Dec 03 2019, 12:00 PM)
Now you have 3 tasks in the list.
------------------------------------------------------------
Here are the tasks in your list:
No. of tasks: 3
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
3.[T][ ] borrow book
------------------------------------------------------------
Making room. Removed this task from your list:
  [T][ ] borrow book
Now you have 2 tasks in the list.
------------------------------------------------------------
Here are the tasks in your list:
No. of tasks: 2
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
------------------------------------------------------------
Task 4 does not exist.
Let's try that again!
------------------------------------------------------------
The task number must be a whole number. Try: delete 1
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```

## Test case: Preserve task state after rejected task and status commands

**Aim:** Verify that invalid commands do not add tasks or change an existing task's completion status.

**Input:**

```text
todo alpha
todo
list
mark 1
unmark 3
list
unmark 1
mark 0
list
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
One less thing to remember. I've added: [T][ ] alpha
You have a total of 1 tasks in the list.
------------------------------------------------------------
A todo needs a description. 
Try: todo {your task}
------------------------------------------------------------
Here are the tasks in your list:
No. of tasks: 1
1.[T][ ] alpha
------------------------------------------------------------
A little progress counts. Marked as done!

[T][X] alpha
------------------------------------------------------------
Task 3 does not exist.
Let's try that again!
------------------------------------------------------------
Here are the tasks in your list:
No. of tasks: 1
1.[T][X] alpha
------------------------------------------------------------
No rush. Put it back on your list.

[T][ ] alpha
------------------------------------------------------------
The task number must be at least 1. Try: mark 1
Let's try that again!
------------------------------------------------------------
Here are the tasks in your list:
No. of tasks: 1
1.[T][ ] alpha
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```

## Test case: Preserve task state after malformed deadline and event commands

**Aim:** Verify that invalid scheduled-task formats do not affect later valid tasks or the task count.

**Input:**

```text
deadline /by Friday
deadline return book /by 2019-12-02 1800
event meeting /from Monday
event meeting /from 2019-12-03 1000 /to 2019-12-03 1200
deadline submit report /by
list
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
A deadline needs a description and a by date. 
Try: deadline {your task} /by {deadline}
------------------------------------------------------------
One less thing to remember. I've added: [D][ ] return book (by: Dec 02 2019, 6:00 PM)
You have a total of 1 tasks in the list.
------------------------------------------------------------
An event needs a description, start, and end time. 
Try: event {task} /from {start} /to {end}
------------------------------------------------------------
One less thing to remember. I've added: [E][ ] meeting (from: Dec 03 2019, 10:00 AM to: Dec 03 2019, 12:00 PM)
You have a total of 2 tasks in the list.
------------------------------------------------------------
A deadline needs a description and a by date. 
Try: deadline {your task} /by {deadline}
------------------------------------------------------------
Here are the tasks in your list:
No. of tasks: 2
1.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
2.[E][ ] meeting (from: Dec 03 2019, 10:00 AM to: Dec 03 2019, 12:00 PM)
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```

## Test case: List an empty task collection

**Aim:** Verify that `list` does not display task entries before any task has been added.

**Input:**

```text
list
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
Your list is clear. Add a task whenever you are ready.
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```

## Test case: Parse, format, and validate dates and times

**Aim:** Verify that all four supported date formats become typed dates, impossible dates are rejected, and an event cannot end before it starts.

**Input:**

```text
deadline hyphen year first /by 2019-12-01 1800
deadline slash year first /by 2019/12/02 1800
deadline slash day first /by 03/12/2019 1800
event hyphen day first /from 04-12-2019 0900 /to 04-12-2019 1100
deadline impossible /by 31/2/2019 1800
event backwards /from 2019-12-04 1200 /to 2019-12-04 1000
list
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
One less thing to remember. I've added: [D][ ] hyphen year first (by: Dec 01 2019, 6:00 PM)
You have a total of 1 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [D][ ] slash year first (by: Dec 02 2019, 6:00 PM)
You have a total of 2 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [D][ ] slash day first (by: Dec 03 2019, 6:00 PM)
You have a total of 3 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [E][ ] hyphen day first (from: Dec 04 2019, 9:00 AM to: Dec 04 2019, 11:00 AM)
You have a total of 4 tasks in the list.
------------------------------------------------------------
Please use a valid date and time in yyyy-MM-dd, yyyy/MM/dd, dd/MM/yyyy, or dd-MM-yyyy format, followed by a 24-hour time such as 1800.
------------------------------------------------------------
An event's end time must be after its start time.
------------------------------------------------------------
Here are the tasks in your list:
No. of tasks: 4
1.[D][ ] hyphen year first (by: Dec 01 2019, 6:00 PM)
2.[D][ ] slash year first (by: Dec 02 2019, 6:00 PM)
3.[D][ ] slash day first (by: Dec 03 2019, 6:00 PM)
4.[E][ ] hyphen day first (from: Dec 04 2019, 9:00 AM to: Dec 04 2019, 11:00 AM)
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```

## Test case: Find scheduled tasks occurring on a date

**Aim:** Verify that `find` accepts all four date formats and lists matching deadlines and single-day or multi-day events using their original task numbers, while excluding todos and other dates.

**Input:**

```text
todo buy snacks
deadline return book /by 2/12/2019 1800
deadline submit report /by 5/12/2019 1200
event conference /from 2019-12-02 2300 /to 2019-12-04 0100
find 2019-12-02
find 2019/12/02
find 02/12/2019
find 02-12-2019
find 2019-12-03
find 2019-12-06
find 31/02/2019
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
One less thing to remember. I've added: [T][ ] buy snacks
You have a total of 1 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [D][ ] return book (by: Dec 02 2019, 6:00 PM)
You have a total of 2 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [D][ ] submit report (by: Dec 05 2019, 12:00 PM)
You have a total of 3 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [E][ ] conference (from: Dec 02 2019, 11:00 PM to: Dec 04 2019, 1:00 AM)
You have a total of 4 tasks in the list.
------------------------------------------------------------
Tasks occurring on Dec 02 2019:
2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
4.[E][ ] conference (from: Dec 02 2019, 11:00 PM to: Dec 04 2019, 1:00 AM)
------------------------------------------------------------
Tasks occurring on Dec 02 2019:
2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
4.[E][ ] conference (from: Dec 02 2019, 11:00 PM to: Dec 04 2019, 1:00 AM)
------------------------------------------------------------
Tasks occurring on Dec 02 2019:
2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
4.[E][ ] conference (from: Dec 02 2019, 11:00 PM to: Dec 04 2019, 1:00 AM)
------------------------------------------------------------
Tasks occurring on Dec 02 2019:
2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
4.[E][ ] conference (from: Dec 02 2019, 11:00 PM to: Dec 04 2019, 1:00 AM)
------------------------------------------------------------
Tasks occurring on Dec 03 2019:
4.[E][ ] conference (from: Dec 02 2019, 11:00 PM to: Dec 04 2019, 1:00 AM)
------------------------------------------------------------
Tasks occurring on Dec 06 2019:
No deadlines or events occur on this date.
------------------------------------------------------------
Please provide a valid date in yyyy-MM-dd, yyyy/MM/dd, dd/MM/yyyy, or dd-MM-yyyy format. Try: find 2019-12-02
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```

## Test case: Find tasks by description keyword

**Aim:** Verify that `find` matches partial descriptions case-insensitively, preserves original task numbers, ignores date metadata, reports no matches, and rejects an empty query.

**Input:**

```text
todo read book
deadline return Library Book /by 2/12/2019 1800
event book club /from 3/12/2019 1000 /to 3/12/2019 1200
todo submit report
find book
find LIBRARY
find 2019
find missing
find
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
One less thing to remember. I've added: [T][ ] read book
You have a total of 1 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [D][ ] return Library Book (by: Dec 02 2019, 6:00 PM)
You have a total of 2 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [E][ ] book club (from: Dec 03 2019, 10:00 AM to: Dec 03 2019, 12:00 PM)
You have a total of 3 tasks in the list.
------------------------------------------------------------
One less thing to remember. I've added: [T][ ] submit report
You have a total of 4 tasks in the list.
------------------------------------------------------------
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return Library Book (by: Dec 02 2019, 6:00 PM)
3.[E][ ] book club (from: Dec 03 2019, 10:00 AM to: Dec 03 2019, 12:00 PM)
------------------------------------------------------------
Here are the matching tasks in your list:
2.[D][ ] return Library Book (by: Dec 02 2019, 6:00 PM)
------------------------------------------------------------
Here are the matching tasks in your list:
No matching tasks found.
------------------------------------------------------------
Here are the matching tasks in your list:
No matching tasks found.
------------------------------------------------------------
Please provide a keyword or date. Try: find book
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```

## Test case: Explain invalid commands and task details

**Aim:** Verify that invalid input is handled without crashing and each message explains how to correct it.

**Input:**

```text

todo
deadline read book
event meeting /from Monday
mark
mark first
mark 1
unmark 0
list extra
bye later
blah
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
Please enter a command. Try: todo read book
------------------------------------------------------------
A todo needs a description. 
Try: todo {your task}
------------------------------------------------------------
A deadline needs a description and a by date. 
Try: deadline {your task} /by {deadline}
------------------------------------------------------------
An event needs a description, start, and end time. 
Try: event {task} /from {start} /to {end}
------------------------------------------------------------
Please provide a task number! Try: mark {task no.}
------------------------------------------------------------
The task number must be a whole number! Try: mark {task no.}
------------------------------------------------------------
Task 1 does not exist.
Let's try that again!
------------------------------------------------------------
The task number must be at least 1. Try: unmark 1
Let's try that again!
------------------------------------------------------------
The list command does not take any extra text. Try: list
------------------------------------------------------------
The bye command does not take any extra text. Try: bye
------------------------------------------------------------
I'm sorry, I don't know what is 'blah'. Try todo, deadline, event, list, find, delete, mark, unmark, tag, or bye.
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```

## Test case: Reject equal event times and recover

**Aim:** Reject zero-length events while accepting extra whitespace before a time.

**Input:**

```text
event study /from 2026-09-18 1800 /to 2026-09-18 1800
deadline report /by 2026-09-18   1800
bye
```

**Expected output:**

```text
{{ANY_PREFIX}}
An event's end time must be after its start time.
------------------------------------------------------------
One less thing to remember. I've added: [D][ ] report (by: Sep 18 2026, 6:00 PM)
You have a total of 1 tasks in the list.
------------------------------------------------------------
See you soon. Make room for a little rest, too.
------------------------------------------------------------
```
