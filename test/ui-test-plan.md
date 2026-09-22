# UI Test Plan

The `test-ui` skill uses this file as the source of truth for console UI tests.
Each test case records its aim, the console inputs, and the expected output after each input.

```program
java -cp "%REPO_ROOT%\build\classes\java\test;%REPO_ROOT%\build\libs\nico.jar" nico.ConsoleTestAdapter
```

```build
gradlew.bat testClasses shadowJar
```

## Test Case: Singlish task list and deletion

Aim: Verify Nico's Singlish empty list, task counts, list introduction, and deletion response.

```input
list
todo buy kopi
list
delete 1
bye
```

```expected
Your list empty lah. Add a task to get started!
---
Can lah! I've added this task:
[T][ ] buy kopi
Now you got 1 task(s) on your list lah.
---
Here's your task list lah:
1. [T][ ] buy kopi
---
Can, I've removed this task:
[T][ ] buy kopi
Now you got 0 task(s) on your list lah.
---
Okay lah, see you again! Take care hor!
```

## Test Case: Assign, replace, and filter priorities

Aim: Verify all priorities, multiple matches, completed tasks, and empty results.

```input
todo report
todo book
addpriority 1 high
addpriority 2 high
showpriority high
addpriority 1 medium
showpriority medium
addpriority 1 low
mark 1
showpriority low
showpriority medium
bye
```

```expected
[T][ ] report
---
[T][ ] book
---
Can lah, I've set this task's priority to high:
[T][ ][high] report
---
Can lah, I've set this task's priority to high:
[T][ ][high] book
---
These tasks got priority high:
- [T][ ][high] report
- [T][ ][high] book
---
Can lah, I've set this task's priority to medium:
[T][ ][medium] report
---
These tasks got priority medium: [T][ ][medium] report
---
Can lah, I've set this task's priority to low:
[T][ ][low] report
---
Steady lah, this task is done already:
[T][X][low] report
---
These tasks got priority low: [T][X][low] report
---
These tasks got priority medium: Don't have lah
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject invalid priority commands

Aim: Verify missing arguments, invalid priorities, extra arguments, and invalid task numbers.

```input
addpriority
addpriority 1
addpriority 1 high extra
addpriority 1 urgent
addpriority one high
addpriority 0 high
addpriority 99 high
showpriority
showpriority urgent
showpriority high extra
bye
```

```expected
Aiyoh! Choose a task number and priority.
Try this lah: addpriority TASK_NUMBER PRIORITY
---
Aiyoh! Choose a task number and priority.
Try this lah: addpriority TASK_NUMBER PRIORITY
---
Aiyoh! Choose a task number and priority.
Try this lah: addpriority TASK_NUMBER PRIORITY
---
Aiyoh! Choose high, medium, or low lah.
Try this lah: high, medium, or low
---
Aiyoh! A task number must be a whole number.
Try this lah: addpriority TASK_NUMBER
---
Aiyoh! That task number is not in the list.
Try this lah: addpriority TASK_NUMBER
---
Aiyoh! That task number is not in the list.
Try this lah: addpriority TASK_NUMBER
---
Aiyoh! Choose a priority to show.
Try this lah: showpriority PRIORITY
---
Aiyoh! Choose high, medium, or low lah.
Try this lah: high, medium, or low
---
Aiyoh! Choose high, medium, or low lah.
Try this lah: high, medium, or low
---
Okay lah, see you again! Take care hor!
```

## Test Case: Find tasks case-insensitively

Aim: Verify that `find` returns every task description containing its keyword, regardless of letter case.

```input
todo Borrow Book
deadline return book /by 25-08-2026 1900
find BOOK
bye
```

```expected
Can lah! I've added this task:
[T][ ] Borrow Book
---
Can lah! I've added this task:
[D][ ] return book (by: Aug 25 2026 19:00)
---
Found these matching tasks for you lah:
- [T][ ] Borrow Book
- [D][ ] return book (by: Aug 25 2026 19:00)
---
Okay lah, see you again! Take care hor!
```

## Test Case: Find with no matching tasks

Aim: Verify that `find` reports no matching tasks when no description contains the keyword.

```input
todo borrow book
find report
bye
```

```expected
Can lah! I've added this task:
[T][ ] borrow book
---
Found these matching tasks for you lah: Don't have lah
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject find without keyword

Aim: Verify that `find` rejects a missing keyword with a usage message.

```input
find
bye
```

```expected
Aiyoh! Enter a keyword to search for.
Try this lah: find KEYWORD
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject unknown command

Aim: Verify that an unrecognized command is rejected instead of being added as a task.

```input
return book
bye
```

```expected
Aiyoh! I don't recognise that command.
Try this lah: todo DESCRIPTION, list, mark TASK_NUMBER, or bye
---
Okay lah, see you again! Take care hor!
```

## Test Case: Add a todo task

Aim: Verify that a `todo` command creates a nico.Todo task formatted with `[T]`.

```input
todo borrow book
bye
```

```expected
Can lah! I've added this task:
[T][ ] borrow book
---
Okay lah, see you again! Take care hor!
```

## Test Case: Add a deadline task

Aim: Verify that a `deadline` command parses the description and `/by` date-time value.

```input
deadline return book /by 25-08-2026 1900
bye
```

```expected
Can lah! I've added this task:
[D][ ] return book (by: Aug 25 2026 19:00)
---
Okay lah, see you again! Take care hor!
```

## Test Case: Add an event task

Aim: Verify that an `event` command parses the description, `/from` date-time, and `/to` date-time.

```input
event project meeting /from 25-08-2026 1400 /to 25-08-2026 1600
bye
```

```expected
Can lah! I've added this task:
[E][ ] project meeting (from: Aug 25 2026 14:00 to: Aug 25 2026 16:00)
---
Okay lah, see you again! Take care hor!
```

## Test Case: List mixed task types

Aim: Verify that `list` displays nico.Todo, nico.Deadline, and nico.Event tasks with their indices.

```input
todo borrow book
deadline submit report /by 25-08-2026 2300
event project meeting /from 25-08-2026 1400 /to 25-08-2026 1600
list
bye
```

```expected
Can lah! I've added this task:
[T][ ] borrow book
---
Can lah! I've added this task:
[D][ ] submit report (by: Aug 25 2026 23:00)
---
Can lah! I've added this task:
[E][ ] project meeting (from: Aug 25 2026 14:00 to: Aug 25 2026 16:00)
---
1. [T][ ] borrow book
2. [D][ ] submit report (by: Aug 25 2026 23:00)
3. [E][ ] project meeting (from: Aug 25 2026 14:00 to: Aug 25 2026 16:00)
---
Okay lah, see you again! Take care hor!
```

## Test Case: Show urgent event and deadline

Aim: Verify that `urgent` displays the incomplete event with the earliest start time and the incomplete deadline with the earliest due time.

```input
event later meeting /from 30-08-2026 1000 /to 30-08-2026 1100
deadline later report /by 30-08-2026 2359
event standup /from 25-08-2026 0900 /to 25-08-2026 0930
deadline submit draft /by 26-08-2026 2359
mark 3
mark 4
urgent
bye
```

```expected
Can lah! I've added this task:
[E][ ] later meeting (from: Aug 30 2026 10:00 to: Aug 30 2026 11:00)
---
Can lah! I've added this task:
[D][ ] later report (by: Aug 30 2026 23:59)
---
Can lah! I've added this task:
[E][ ] standup (from: Aug 25 2026 09:00 to: Aug 25 2026 09:30)
---
Can lah! I've added this task:
[D][ ] submit draft (by: Aug 26 2026 23:59)
---
Steady lah, this task is done already:
[E][X] standup (from: Aug 25 2026 09:00 to: Aug 25 2026 09:30)
---
Steady lah, this task is done already:
[D][X] submit draft (by: Aug 26 2026 23:59)
---
Event to settle first hor: [E][ ] later meeting (from: Aug 30 2026 10:00 to: Aug 30 2026 11:00)
Deadline to settle first hor: [D][ ] later report (by: Aug 30 2026 23:59)
---
Okay lah, see you again! Take care hor!
```

## Test Case: Show urgent with missing task types

Aim: Verify that `urgent` reports `None` for missing event and deadline task types.

```input
todo borrow book
urgent
bye
```

```expected
Can lah! I've added this task:
[T][ ] borrow book
---
Event to settle first hor: Don't have lah
Deadline to settle first hor: Don't have lah
---
Okay lah, see you again! Take care hor!
```

## Test Case: Show tied urgent tasks as bullets

Aim: Verify that `urgent` displays all incomplete events and deadlines that tie for the most urgent time.

```input
event demo /from 25-08-2026 0900 /to 25-08-2026 1000
event interview /from 25-08-2026 0900 /to 25-08-2026 0930
event later meeting /from 30-08-2026 1000 /to 30-08-2026 1100
deadline draft /by 26-08-2026 2359
deadline slides /by 26-08-2026 2359
deadline final report /by 30-08-2026 2359
urgent
bye
```

```expected
Can lah! I've added this task:
[E][ ] demo (from: Aug 25 2026 09:00 to: Aug 25 2026 10:00)
---
Can lah! I've added this task:
[E][ ] interview (from: Aug 25 2026 09:00 to: Aug 25 2026 09:30)
---
Can lah! I've added this task:
[E][ ] later meeting (from: Aug 30 2026 10:00 to: Aug 30 2026 11:00)
---
Can lah! I've added this task:
[D][ ] draft (by: Aug 26 2026 23:59)
---
Can lah! I've added this task:
[D][ ] slides (by: Aug 26 2026 23:59)
---
Can lah! I've added this task:
[D][ ] final report (by: Aug 30 2026 23:59)
---
Event to settle first hor:
- [E][ ] demo (from: Aug 25 2026 09:00 to: Aug 25 2026 10:00)
- [E][ ] interview (from: Aug 25 2026 09:00 to: Aug 25 2026 09:30)
Deadline to settle first hor:
- [D][ ] draft (by: Aug 26 2026 23:59)
- [D][ ] slides (by: Aug 26 2026 23:59)
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject urgent arguments

Aim: Verify that `urgent` rejects extra arguments with the expected usage message.

```input
urgent now
bye
```

```expected
Aiyoh! `urgent` does not take extra text.
Try this lah: urgent
---
Okay lah, see you again! Take care hor!
```

## Test Case: Mark a task as done

Aim: Verify that `mark 1` marks the first task as done.

```input
todo borrow book
mark 1
bye
```

```expected
Can lah! I've added this task:
[T][ ] borrow book
---
Steady lah, this task is done already:
[T][X] borrow book
---
Okay lah, see you again! Take care hor!
```

## Test Case: Unmark a task as not done

Aim: Verify that `unmark 1` changes a previously marked task back to not done.

```input
todo borrow book
mark 1
unmark 1
bye
```

```expected
Can lah! I've added this task:
[T][ ] borrow book
---
Steady lah, this task is done already:
[T][X] borrow book
---
Okay lah, this task not done yet:
[T][ ] borrow book
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject invalid mark index

Aim: Verify that `mark` reports an error when the task number is outside the list.

```input
todo borrow book
mark 2
bye
```

```expected
Can lah! I've added this task:
[T][ ] borrow book
---
Aiyoh! That task number is not in the list.
Try this lah: mark TASK_NUMBER
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject non-numeric task number

Aim: Verify that a task-number command rejects a non-numeric task number without a package prefix in its message.

```input
mark one
bye
```

```expected
Aiyoh! A task number must be a whole number.
Try this lah: mark TASK_NUMBER
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject malformed deadline

Aim: Verify that a `deadline` command without `/by` shows the expected usage message.

```input
deadline return book
bye
```

```expected
Aiyoh! I could not find a due time.
Try this lah: deadline DESCRIPTION /by dd-MM-yyyy HHmm
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject malformed event

Aim: Verify that an `event` command without `/from` and `/to` shows the expected usage message.

```input
event project meeting
bye
```

```expected
Aiyoh! I could not find both event times.
Try this lah: event DESCRIPTION /from dd-MM-yyyy HHmm /to dd-MM-yyyy HHmm
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject invalid deadline date-time

Aim: Verify that a `deadline` command rejects a due time that does not match the required date-time format.

```input
deadline return book /by Sunday
bye
```

```expected
Aiyoh! The date and time is invalid.
Try this lah: dd-MM-yyyy HHmm
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject invalid event date-times

Aim: Verify that an `event` command rejects start and end times that do not match the required date-time format.

```input
event project meeting /from Mon /to 4pm
bye
```

```expected
Aiyoh! The date and time is invalid.
Try this lah: dd-MM-yyyy HHmm
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject empty todo description

Aim: Verify that a `todo` command with no description is rejected.

```input
todo
bye
```

```expected
Aiyoh! A to-do needs a description.
Try this lah: todo DESCRIPTION
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject empty deadline description

Aim: Verify that a `deadline` command with no description is rejected.

```input
deadline /by 25-08-2026 1900
bye
```

```expected
Aiyoh! A deadline needs a description.
Try this lah: deadline DESCRIPTION /by dd-MM-yyyy HHmm
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject empty event description

Aim: Verify that an `event` command with no description is rejected.

```input
event /from 25-08-2026 1400 /to 25-08-2026 1600
bye
```

```expected
Aiyoh! An event needs a description.
Try this lah: event DESCRIPTION /from dd-MM-yyyy HHmm /to dd-MM-yyyy HHmm
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject empty event start time

Aim: Verify that an `event` command with no start time shows the usage message.

```input
event main game /from /to 25-08-2026 1900
bye
```

```expected
Aiyoh! An event needs both a start time and an end time.
Try this lah: event DESCRIPTION /from dd-MM-yyyy HHmm /to dd-MM-yyyy HHmm
---
Okay lah, see you again! Take care hor!
```

## Test Case: Reject empty event end time

Aim: Verify that an `event` command with no end time shows the same usage message.

```input
event main game /from 25-08-2026 1900 /to
bye
```

```expected
Aiyoh! An event needs both a start time and an end time.
Try this lah: event DESCRIPTION /from dd-MM-yyyy HHmm /to dd-MM-yyyy HHmm
---
Okay lah, see you again! Take care hor!
```
