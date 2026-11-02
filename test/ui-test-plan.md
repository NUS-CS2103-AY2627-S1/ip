# UI Test Plan

The test-ui skill gives each test case isolated storage. Every session ends
with `bye` so the application exits cleanly.

## Test case 1: Add a todo

- Aim: Verify that a todo is added.

### Inputs

```text
todo borrow book
bye
```

### Expected output

```text
All set. I've added this task:
  [T][ ] borrow book
Your task list now has 1 tasks.
```

## Test case 2: Add a typed deadline

- Aim: Verify strict date parsing and 24-hour to 12-hour display conversion.

### Inputs

```text
deadline submit report /by 2019-12-02 1800
bye
```

### Expected output

```text
All set. I've added this task:
  [D][ ] submit report (by: Dec 02 2019, 6:00 PM)
Your task list now has 1 tasks.
```

## Test case 3: Add a same-day event

- Aim: Verify the `/on` shorthand and time-only endpoints.

### Inputs

```text
event meeting /on 2/12/2019 /from 1400 /to 1600
bye
```

### Expected output

```text
All set. I've added this task:
  [E][ ] meeting (on: Dec 02 2019, from: 2:00 PM to: 4:00 PM)
Your task list now has 1 tasks.
```

## Test case 4: Add a multi-day event

- Aim: Verify complete date-time endpoints.

### Inputs

```text
event conference /from 2019-12-02 1400 /to 2019-12-03 1600
bye
```

### Expected output

```text
All set. I've added this task:
  [E][ ] conference (from: Dec 02 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
Your task list now has 1 tasks.
```

## Test case 5: Reject impossible values

- Aim: Verify invalid calendar dates and times are rejected.

### Inputs

```text
deadline report /by 2019-02-30 1800
event meeting /on 2019-12-02 /from 2460 /to 1600
bye
```

### Expected output

```text
Enter a valid date and time after "/by" using YYYY-MM-DD HHmm or D/M/YYYY HHmm.
Enter a valid start time using HHmm, such as 1400.
```

## Test case 6: Reject an event whose end is not later

- Aim: Verify strict event ordering.

### Inputs

```text
event meeting /on 2019-12-02 /from 1600 /to 1600
event conference /from 2019-12-03 1600 /to 2019-12-02 1400
bye
```

### Expected output

```text
An event's end must be strictly after its start.
An event's end must be strictly after its start.
```

## Test case 7: Persist typed tasks

- Aim: Verify ISO persistence across an application restart.

### Inputs (session 1)

```text
deadline report /by 2/12/2019 1800
bye
```

### Expected output (session 1)

```text
All set. I've added this task:
  [D][ ] report (by: Dec 02 2019, 6:00 PM)
Your task list now has 1 tasks.
```

### Inputs (session 2)

```text
list
bye
```

### Expected output (session 2)

```text
Here is your task list:
1.[D][ ] report (by: Dec 02 2019, 6:00 PM)
```

## Test case 8: Reject a deadline without required syntax

- Aim: Verify that deadline marker, description, and value errors remain specific.

### Inputs

```text
deadline submit report 2019-12-02 1800
deadline /by 2019-12-02 1800
deadline submit report /by
bye
```

### Expected output

```text
A deadline needs "/by" followed by its date and time. Try: deadline submit report /by 2019-12-02 1800
A deadline needs a description before "/by". Try: deadline submit report /by 2019-12-02 1800
Enter a date and time after "/by". Try: deadline submit report /by 2019-12-02 1800
```

## Test case 9: Reject duplicate deadline markers

- Aim: Verify that a marker is not silently interpreted as deadline text.

### Inputs

```text
deadline submit report /by 2019-12-02 1800 /by 2019-12-03 1800
bye
```

### Expected output

```text
"/by" can only appear once. Remove the extra marker and try again.
```

## Test case 10: Reject incomplete event syntax

- Aim: Verify that missing event markers and endpoint values are identified.

### Inputs

```text
event meeting /to 2019-12-02 1600
event meeting /from 2019-12-02 1400
event meeting /on 2019-12-02 /from /to 1600
event meeting /on 2019-12-02 /from 1400 /to
bye
```

### Expected output

```text
An event needs "/from". Try: event meeting /on 2019-12-02 /from 1400 /to 1600
An event needs "/to". Try: event meeting /on 2019-12-02 /from 1400 /to 1600
Enter a start value after "/from". Try: event meeting /on 2019-12-02 /from 1400 /to 1600
Enter an end value after "/to". Try: event meeting /on 2019-12-02 /from 1400 /to 1600
```

## Test case 11: Reject mixed and malformed event date forms

- Aim: Verify that an event cannot omit its date or combine a date with time-only shorthand.

### Inputs

```text
event meeting /from 1400 /to 1600
event meeting /on 2019-12-02 /from 2019-12-02 1400 /to 1600
event meeting /on 30/2/2019 /from 1400 /to 1600
event meeting /from 2019-12-02 1400 /to 2019-12-02 2460
bye
```

### Expected output

```text
Enter a valid event date. With "/on", use YYYY-MM-DD or D/M/YYYY; otherwise use YYYY-MM-DD HHmm or D/M/YYYY HHmm.
Enter a valid start time using HHmm, such as 1400.
Enter a valid event date. With "/on", use YYYY-MM-DD or D/M/YYYY; otherwise use YYYY-MM-DD HHmm or D/M/YYYY HHmm.
Enter a valid end time using HHmm, such as 1600.
```

## Test case 12: Reject duplicate and out-of-order event markers

- Aim: Verify that event markers occur once and in the documented order.

### Inputs

```text
event meeting /to 2019-12-02 1600 /from 2019-12-02 1400
event meeting /on 2019-12-02 /from 1400 /to 1600 /to 1700
event meeting /from 2019-12-02 1400 /on 2019-12-02 /to 2019-12-02 1600
bye
```

### Expected output

```text
Use "/on", then "/from", then "/to". Try: event meeting /on 2019-12-02 /from 1400 /to 1600
"/to" can only appear once. Remove the extra marker and try again.
Use "/on", then "/from", then "/to". Try: event meeting /on 2019-12-02 /from 1400 /to 1600
```

## Test case 13: Reject an event without a description

- Aim: Verify that descriptions must precede every event form.

### Inputs

```text
event /on 2019-12-02 /from 1400 /to 1600
bye
```

### Expected output

```text
An event needs a description before its date or time markers. Try: event meeting /on 2019-12-02 /from 1400 /to 1600
```

## Test case 14: List, mark, unmark, and delete typed tasks

- Aim: Verify that normal task operations preserve typed deadline output.

### Inputs

```text
todo read book
deadline return book /by 2/12/2019 1800
mark 1
unmark 1
delete 1
list
bye
```

### Expected output

```text
All set. I've added this task:
  [T][ ] read book
Your task list now has 1 tasks.
All set. I've added this task:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
Your task list now has 2 tasks.
Good progress. I've marked this task as complete:
[T][X] read book
No problem. I've marked this task as incomplete:
[T][ ] read book
All set. I've removed this task:
[T][ ] read book
Here is your task list:
1.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
```

## Test case 15: Reject invalid task IDs

- Aim: Verify that missing and malformed IDs include the command name.

### Inputs

```text
mark
mark abc
mark 0
delete 999999999999999999999999999999999999
bye
```

### Expected output

```text
Enter the task number to mark. Try: mark 1
"abc" is not a valid task number. Enter a positive whole number.
Task numbers start from 1. Use "list" to see the available task numbers.
The task number "999999999999999999999999999999999999" is too large. Use "list" to see the available task numbers.
```

## Test case 16: Explain operations on unavailable tasks

- Aim: Verify that empty and out-of-range task lists are distinguished.

### Inputs

```text
delete 1
todo first task
mark 2
bye
```

### Expected output

```text
There are no tasks to delete. Add one first, for example: todo borrow a book
All set. I've added this task:
  [T][ ] first task
Your task list now has 1 tasks.
Task 2 does not exist. Choose a number from 1 to 1, or use "list" to view your tasks.
```

## Test case 17: Reject unknown and empty commands

- Aim: Verify that parsing passes the actual command name into localized errors.

### Inputs

```text
remember borrow book

bye
```

### Expected output

```text
"remember" is not a recognized command. Enter "help" for all commands and accepted formats.
You didn't enter a command. Try "list" to view your tasks.
```

## Test case 18: Keep list without a date compatible

- Aim: Verify that `list` without a date still shows every task.

### Inputs

```text
todo borrow book
list
bye
```

### Expected output

```text
All set. I've added this task:
  [T][ ] borrow book
Your task list now has 1 tasks.
Here is your task list:
1.[T][ ] borrow book
```

## Test case 19: List matching tasks by either date format

- Aim: Verify that date-filtered lists include deadlines and overlapping events while preserving task IDs.

### Inputs

```text
todo unplanned task
deadline due today /by 2019-12-02 1800
deadline due tomorrow /by 2019-12-03 1800
event meeting /on 2/12/2019 /from 1400 /to 1600
event conference /from 2019-12-01 0900 /to 2019-12-03 1700
list 2019-12-02
list 3/12/2019
bye
```

### Expected output

```text
All set. I've added this task:
  [T][ ] unplanned task
Your task list now has 1 tasks.
All set. I've added this task:
  [D][ ] due today (by: Dec 02 2019, 6:00 PM)
Your task list now has 2 tasks.
All set. I've added this task:
  [D][ ] due tomorrow (by: Dec 03 2019, 6:00 PM)
Your task list now has 3 tasks.
All set. I've added this task:
  [E][ ] meeting (on: Dec 02 2019, from: 2:00 PM to: 4:00 PM)
Your task list now has 4 tasks.
All set. I've added this task:
  [E][ ] conference (from: Dec 01 2019, 9:00 AM to: Dec 03 2019, 5:00 PM)
Your task list now has 5 tasks.
Here are your tasks for 2019-12-02:
2.[D][ ] due today (by: Dec 02 2019, 6:00 PM)
4.[E][ ] meeting (on: Dec 02 2019, from: 2:00 PM to: 4:00 PM)
5.[E][ ] conference (from: Dec 01 2019, 9:00 AM to: Dec 03 2019, 5:00 PM)
Here are your tasks for 2019-12-03:
3.[D][ ] due tomorrow (by: Dec 03 2019, 6:00 PM)
5.[E][ ] conference (from: Dec 01 2019, 9:00 AM to: Dec 03 2019, 5:00 PM)
```

## Test case 20: Reject invalid date queries and report no matches

- Aim: Verify strict query-date parsing and the empty filtered-list message.

### Inputs

```text
todo unplanned task
list 4/12/2019
list 2019-02-30
list 2019-12-02 1800
bye
```

### Expected output

```text
All set. I've added this task:
  [T][ ] unplanned task
Your task list now has 1 tasks.
No tasks are scheduled for 2019-12-04.
Enter a valid date using YYYY-MM-DD or D/M/YYYY. Try: list 2019-12-02
Enter a valid date using YYYY-MM-DD or D/M/YYYY. Try: list 2019-12-02
```

## Test case 21: Filter persisted tasks by date

- Aim: Verify that date-filtering works after typed tasks are loaded from storage.

### Inputs (session 1)

```text
deadline report /by 2/12/2019 1800
event workshop /from 2019-12-01 1500 /to 2019-12-03 1100
bye
```

### Expected output (session 1)

```text
All set. I've added this task:
  [D][ ] report (by: Dec 02 2019, 6:00 PM)
Your task list now has 1 tasks.
All set. I've added this task:
  [E][ ] workshop (from: Dec 01 2019, 3:00 PM to: Dec 03 2019, 11:00 AM)
Your task list now has 2 tasks.
```

### Inputs (session 2)

```text
list 2019-12-02
bye
```

### Expected output (session 2)

```text
Here are your tasks for 2019-12-02:
1.[D][ ] report (by: Dec 02 2019, 6:00 PM)
2.[E][ ] workshop (from: Dec 01 2019, 3:00 PM to: Dec 03 2019, 11:00 AM)
```

## Test case 22: Find tasks by description

- Aim: Verify literal, case-sensitive matching across task types while preserving task IDs.

### Inputs

```text
todo review PR #42 [urgent]
deadline review report /by 2019-12-02 1800
event team review /on 2019-12-02 /from 1400 /to 1600
find review
find PR #42 [urgent]
find Review
find
bye
```

### Expected output

```text
All set. I've added this task:
  [T][ ] review PR #42 [urgent]
Your task list now has 1 tasks.
All set. I've added this task:
  [D][ ] review report (by: Dec 02 2019, 6:00 PM)
Your task list now has 2 tasks.
All set. I've added this task:
  [E][ ] team review (on: Dec 02 2019, from: 2:00 PM to: 4:00 PM)
Your task list now has 3 tasks.
Here are the matching tasks:
1.[T][ ] review PR #42 [urgent]
2.[D][ ] review report (by: Dec 02 2019, 6:00 PM)
3.[E][ ] team review (on: Dec 02 2019, from: 2:00 PM to: 4:00 PM)
Here are the matching tasks:
1.[T][ ] review PR #42 [urgent]
I couldn't find any tasks containing "Review".
A find command needs a search term. Try: find borrow a book
```

## Test case 23: Show localized in-app help

- Aim: Verify that users can discover every supported command and input format.

### Inputs

```text
help
bye
```

### Expected output

```text
Available commands:
help - show this guide
todo <description>
deadline <description> /by <date and time>
event <description> /on <date> /from <time> /to <time>
event <description> /from <date and time> /to <date and time>
list [date]
find <query>
mark <task number>
unmark <task number>
delete <task number>
bye - exit Megia
Dates: YYYY-MM-DD or D/M/YYYY. Times: 24-hour HHmm.
Use a space to combine a date and time.
```
