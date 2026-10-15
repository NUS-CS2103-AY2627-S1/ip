# UI Test Plan

Each test case starts with an empty in-memory task list. Expected output omits
the startup banner, indentation, blank lines, and underscore separators because
the runner normalizes those presentation details before comparison. Storage
tests run in isolated temporary directories and may specify the expected
contents of `data/bot.txt`. Loading tests may seed an initial data file before
the chatbot starts.

## TC1 — Add and list all task types

**Aim:** Verify that todo, deadline, and event commands create the correct task types and that list preserves their order and details.

### Inputs

```text
todo borrow book
deadline return book /by 2019-12-02
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 02 2019)
3.[E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)
Bye. Hope to see you again soon!
```

## TC2 — Mark and unmark typed tasks

**Aim:** Verify that mark and unmark update task status without losing type-specific information.

### Inputs

```text
todo read book
deadline return book /by 2019-12-06
mark 1
mark 2
unmark 2
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: Dec 06 2019)
Now you have 2 tasks in the list.
Nice! I've marked this task as done:
[T][X] read book
Nice! I've marked this task as done:
[D][X] return book (by: Dec 06 2019)
OK, I've marked this task as not done yet:
[D][ ] return book (by: Dec 06 2019)
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Dec 06 2019)
Bye. Hope to see you again soon!
```

## TC3 — Reject invalid deadline dates

**Aim:** Verify that deadline dates must use the supported format and represent real calendar dates.

### Inputs

```text
deadline do homework /by no idea :-p
deadline submit report /by 2019-02-29
bye
```

### Expected output

```text
OOPS!!! Use yyyy-MM-dd for deadline dates, e.g. 2019-12-02.
OOPS!!! Use yyyy-MM-dd for deadline dates, e.g. 2019-12-02.
Bye. Hope to see you again soon!
```

## TC4 — Reject empty descriptions and unknown commands

**Aim:** Verify that missing task descriptions and unsupported commands produce errors without terminating the chatbot.

### Inputs

```text
todo
deadline
event
blah
bye
```

### Expected output

```text
OOPS!!! The description of a todo cannot be empty.
OOPS!!! The description of a deadline cannot be empty.
OOPS!!! The description of an event cannot be empty.
OOPS!!! I don't know what "blah" means.
Bye. Hope to see you again soon!
```

## TC5 — Reject malformed date and time commands

**Aim:** Verify that deadline and event commands explain which required marker or date/time value is missing.

### Inputs

```text
deadline do 2103 /from now /to now
deadline do homework /by
event meeting /to 4pm
event meeting /from Mon
event meeting /from /to 4pm
event meeting /from Mon /to
deadline do homework /by 02/12/2019
event meeting /from 2019-12-02 1400 /to 4pm
event meeting /from 2019-12-02 1600 /to 2019-12-02 1400
bye
```

### Expected output

```text
OOPS!!! A deadline must include /by followed by its date.
OOPS!!! The date of a deadline cannot be empty.
OOPS!!! An event must include /from followed by its start time.
OOPS!!! An event must include /to followed by its end time.
OOPS!!! The start time of an event cannot be empty.
OOPS!!! The end time of an event cannot be empty.
OOPS!!! Use yyyy-MM-dd for deadline dates, e.g. 2019-12-02.
OOPS!!! Use yyyy-MM-dd HHmm for event dates and times, e.g. 2019-12-02 1400.
OOPS!!! The event end date and time must be after its start date and time.
Bye. Hope to see you again soon!
```

## TC6 — Reject invalid mark and unmark task numbers

**Aim:** Verify that missing, nonnumeric, empty-list, and out-of-range task numbers produce clear errors and that later valid commands still work.

### Inputs

```text
mark
mark two
mark 1
todo read book
mark 2
unmark 0
mark 1
list
bye
```

### Expected output

```text
OOPS!!! Tell me which task number to mark.
OOPS!!! The task number must be a whole number.
OOPS!!! The task list is empty.
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
OOPS!!! Task number 2 does not exist. Choose a number from 1 to 1.
OOPS!!! Task number 0 does not exist. Choose a number from 1 to 1.
Nice! I've marked this task as done:
[T][X] read book
Here are the tasks in your list:
1.[T][X] read book
Bye. Hope to see you again soon!
```

## TC7 — Reject iclang -o lab2c -DTEST0 lab2c.cnvalid command structure

**Aim:** Verify that blank commands and extra information supplied to argument-free commands are explained without terminating the chatbot.

### Inputs

```text
list extra information

bye later
bye
```

### Expected output

```text
OOPS!!! The list command does not take extra information.
OOPS!!! Please enter a command.
OOPS!!! Use bye without any extra words.
Bye. Hope to see you again soon!
```

## TC8 — Delete a task and renumber the remaining list

**Aim:** Verify that delete removes the selected task, reports the new task count, and shifts later tasks to the correct list numbers.

### Inputs

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo borrow book
delete 2
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
Now you have 3 tasks in the list.
Got it. I've added this task:
[T][ ] borrow book
Now you have 4 tasks in the list.
Noted. I've removed this task:
[D][ ] return book (by: Jun 06 2019)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] read book
2.[E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
3.[T][ ] borrow book
Bye. Hope to see you again soon!
```

## TC9 — Reject invalid delete task numbers

**Aim:** Verify that delete handles missing, nonnumeric, empty-list, and out-of-range task numbers and can remove the final task safely.

### Inputs

```text
delete
delete two
delete 1
todo read book
delete 0
delete 2
delete 1
list
bye
```

### Expected output

```text
OOPS!!! Tell me which task number to delete.
OOPS!!! The task number must be a whole number.
OOPS!!! The task list is empty.
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
OOPS!!! Task number 0 does not exist. Choose a number from 1 to 1.
OOPS!!! Task number 2 does not exist. Choose a number from 1 to 1.
Noted. I've removed this task:
[T][ ] read book
Now you have 0 tasks in the list.
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

## TC10 — Save task types and done status

**Aim:** Verify that adding each task type and marking a task writes its current state to the data file.

### Inputs

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
mark 1
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
Now you have 3 tasks in the list.
Nice! I've marked this task as done:
[T][X] read book
Bye. Hope to see you again soon!
```

### Expected data file

```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

## TC11 — Save after unmarking and deleting

**Aim:** Verify that unmark and delete overwrite the data file with only the remaining tasks and their latest statuses.

### Inputs

```text
todo read book
deadline return book /by 2019-12-06
mark 1
unmark 1
delete 2
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: Dec 06 2019)
Now you have 2 tasks in the list.
Nice! I've marked this task as done:
[T][X] read book
OK, I've marked this task as not done yet:
[T][ ] read book
Noted. I've removed this task:
[D][ ] return book (by: Dec 06 2019)
Now you have 1 tasks in the list.
Bye. Hope to see you again soon!
```

### Expected data file

```text
T | 0 | read book
```

## TC12 — Load all task types and done statuses

**Aim:** Verify that startup loading reconstructs todo, deadline, and event tasks with their saved done statuses and details.

### Initial data file

```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 1 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

### Inputs

```text
list
bye
```

### Expected output

```text
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][X] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
Bye. Hope to see you again soon!
```

## TC13 — Modify and save loaded tasks

**Aim:** Verify that loaded tasks can be unmarked, deleted, extended, and then saved back in their updated state.

### Initial data file

```text
T | 0 | read book
D | 1 | return book | 2019-12-06
```

### Inputs

```text
unmark 2
delete 1
event team meeting /from 2019-12-09 1400 /to 2019-12-09 1600
list
bye
```

### Expected output

```text
OK, I've marked this task as not done yet:
[D][ ] return book (by: Dec 06 2019)
Noted. I've removed this task:
[T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
[E][ ] team meeting (from: Dec 09 2019, 2:00PM to: Dec 09 2019, 4:00PM)
Now you have 2 tasks in the list.
Here are the tasks in your list:
1.[D][ ] return book (by: Dec 06 2019)
2.[E][ ] team meeting (from: Dec 09 2019, 2:00PM to: Dec 09 2019, 4:00PM)
Bye. Hope to see you again soon!
```

### Expected data file

```text
D | 0 | return book | 2019-12-06
E | 0 | team meeting | 2019-12-09T14:00 | 2019-12-09T16:00
```

## TC14 — Start safely when the data file is missing

**Aim:** Verify that the chatbot starts with an empty task list when no data file exists.

### Inputs

```text
list
bye
```

### Expected output

```text
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

## TC15 — Reject a saved task with missing fields

**Aim:** Verify that an incomplete saved event produces a clear loading error and an empty task list instead of crashing.

### Initial data file

```text
E | 0 | project meeting | 2019-12-02T14:00
```

### Inputs

```text
list
bye
```

### Expected output

```text
OOPS!!! I couldn't load your data file: invalid data on line 1 (event must contain 5 fields). I started with an empty list.
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

## TC16 — Reject an invalid saved status

**Aim:** Verify that a done status other than 0 or 1 produces a clear loading error and an empty task list.

### Initial data file

```text
T | yes | read book
```

### Inputs

```text
list
bye
```

### Expected output

```text
OOPS!!! I couldn't load your data file: invalid data on line 1 (status must be 0 or 1). I started with an empty list.
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

## TC17 — Reject an unknown saved task type

**Aim:** Verify that an unknown type on a later line reports its line number and discards tasks loaded before the error.

### Initial data file

```text
T | 0 | read book
X | 0 | mystery task
```

### Inputs

```text
list
bye
```

### Expected output

```text
OOPS!!! I couldn't load your data file: invalid data on line 2 (unknown task type 'X'). I started with an empty list.
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

## TC18 — Reject an invalid saved deadline date

**Aim:** Verify that a malformed saved deadline date produces a clear loading error instead of crashing.

### Initial data file

```text
D | 0 | return book | 06-06-2019
```

### Inputs

```text
list
bye
```

### Expected output

```text
OOPS!!! I couldn't load your data file: invalid data on line 1 (deadline date must use yyyy-MM-dd). I started with an empty list.
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

## TC19 — Reject an invalid saved event range

**Aim:** Verify that a saved event whose end is not after its start is rejected during loading.

### Initial data file

```text
E | 0 | project meeting | 2019-12-02T16:00 | 2019-12-02T14:00
```

### Inputs

```text
list
bye
```

### Expected output

```text
OOPS!!! I couldn't load your data file: invalid data on line 1 (event end must be after its start). I started with an empty list.
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

## TC20 — Find deadlines and events on a date

**Aim:** Verify that the on command finds deadlines on the exact date and events spanning that date while preserving their original task numbers.

### Inputs

```text
todo buy milk
deadline submit report /by 2019-12-02
event conference /from 2019-12-01 0900 /to 2019-12-03 1700
event workshop /from 2019-12-04 0900 /to 2019-12-04 1200
on 2019-12-02
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] buy milk
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] submit report (by: Dec 02 2019)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] conference (from: Dec 01 2019, 9:00AM to: Dec 03 2019, 5:00PM)
Now you have 3 tasks in the list.
Got it. I've added this task:
[E][ ] workshop (from: Dec 04 2019, 9:00AM to: Dec 04 2019, 12:00PM)
Now you have 4 tasks in the list.
Here are the deadlines and events on Dec 02 2019:
2.[D][ ] submit report (by: Dec 02 2019)
3.[E][ ] conference (from: Dec 01 2019, 9:00AM to: Dec 03 2019, 5:00PM)
Bye. Hope to see you again soon!
```

## TC21 — Handle invalid and unmatched date searches

**Aim:** Verify that the on command handles missing, malformed, impossible, and unmatched dates without terminating the chatbot.

### Inputs

```text
on
on 02/12/2019
on 2019-02-29
on 2019-12-02
bye
```

### Expected output

```text
OOPS!!! Tell me which date to search using yyyy-MM-dd.
OOPS!!! Use yyyy-MM-dd when searching by date, e.g. 2019-12-02.
OOPS!!! Use yyyy-MM-dd when searching by date, e.g. 2019-12-02.
Here are the deadlines and events on Dec 02 2019:
No deadlines or events found.
Bye. Hope to see you again soon!
```

## TC22 — Find tasks by description keyword

**Aim:** Verify that find matches task descriptions case-insensitively, preserves their order, and does not search date fields.

### Inputs

```text
todo Read Book
deadline return book /by 2019-06-06
event book club /from 2019-08-06 1400 /to 2019-08-06 1600
todo buy milk
find BoO
find 2019
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] Read Book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] book club (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
Now you have 3 tasks in the list.
Got it. I've added this task:
[T][ ] buy milk
Now you have 4 tasks in the list.
Here are the matching tasks in your list:
1.[T][ ] Read Book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] book club (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
Here are the matching tasks in your list:
No matching tasks found.
Bye. Hope to see you again soon!
```

## TC23 — Handle a missing or multi-word find keyword

**Aim:** Verify that find rejects an empty keyword without terminating and accepts a multi-word keyword afterward.

### Inputs

```text
find
todo read book today
find read book
bye
```

### Expected output

```text
OOPS!!! Tell me what keyword to find.
Got it. I've added this task:
[T][ ] read book today
Now you have 1 tasks in the list.
Here are the matching tasks in your list:
1.[T][ ] read book today
Bye. Hope to see you again soon!
```

## TC24 — Sort dated tasks chronologically

**Aim:** Verify that sort orders events by start time and deadlines by due date, places todos afterward, and saves the new order.

### Inputs

```text
todo buy milk
event later /from 2019-12-10 1400 /to 2019-12-10 1600
deadline submit report /by 2019-12-05
event earlier /from 2019-12-01 0900 /to 2019-12-01 1000
todo read book
sort
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] buy milk
Now you have 1 tasks in the list.
Got it. I've added this task:
[E][ ] later (from: Dec 10 2019, 2:00PM to: Dec 10 2019, 4:00PM)
Now you have 2 tasks in the list.
Got it. I've added this task:
[D][ ] submit report (by: Dec 05 2019)
Now you have 3 tasks in the list.
Got it. I've added this task:
[E][ ] earlier (from: Dec 01 2019, 9:00AM to: Dec 01 2019, 10:00AM)
Now you have 4 tasks in the list.
Got it. I've added this task:
[T][ ] read book
Now you have 5 tasks in the list.
Here are your tasks sorted chronologically:
1.[E][ ] earlier (from: Dec 01 2019, 9:00AM to: Dec 01 2019, 10:00AM)
2.[D][ ] submit report (by: Dec 05 2019)
3.[E][ ] later (from: Dec 10 2019, 2:00PM to: Dec 10 2019, 4:00PM)
4.[T][ ] buy milk
5.[T][ ] read book
Here are the tasks in your list:
1.[E][ ] earlier (from: Dec 01 2019, 9:00AM to: Dec 01 2019, 10:00AM)
2.[D][ ] submit report (by: Dec 05 2019)
3.[E][ ] later (from: Dec 10 2019, 2:00PM to: Dec 10 2019, 4:00PM)
4.[T][ ] buy milk
5.[T][ ] read book
Bye. Hope to see you again soon!
```

### Expected data file

```text
E | 0 | earlier | 2019-12-01T09:00 | 2019-12-01T10:00
D | 0 | submit report | 2019-12-05
E | 0 | later | 2019-12-10T14:00 | 2019-12-10T16:00
T | 0 | buy milk
T | 0 | read book
```

## TC25 — Reject extra sort information

**Aim:** Verify that sort rejects unexpected arguments without terminating the chatbot.

### Inputs

```text
sort later
list
bye
```

### Expected output

```text
OOPS!!! The sort command does not take extra information.
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

## TC26 — Handle malformed command structure

**Aim:** Verify that extra whitespace is normalized while reserved separators, repeated markers, reversed markers, and multiple task numbers produce specific errors.

### Inputs

```text
todo     read    book
todo plan | review
deadline submit report /by 2019-12-02 /by 2019-12-03
event meeting /from 2019-12-02 1400 /from 2019-12-02 1500 /to 2019-12-02 1600
event meeting /from 2019-12-02 1400 /to 2019-12-02 1500 /to 2019-12-02 1600
event meeting /to 2019-12-02 1600 /from 2019-12-02 1400
mark 1 2
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
OOPS!!! Task descriptions cannot contain the | character.
OOPS!!! A deadline can contain only one /by marker.
OOPS!!! An event can contain only one /from marker.
OOPS!!! An event can contain only one /to marker.
OOPS!!! The /from marker must appear before the /to marker.
OOPS!!! Enter only one task number for the mark command.
Here are the tasks in your list:
1.[T][ ] read book
Bye. Hope to see you again soon!
```

## TC27 — Reject duplicate tasks

**Aim:** Verify that equivalent tasks are rejected regardless of letter case or completion status while tasks with different dates remain valid.

### Inputs

```text
todo Read Book
mark 1
todo read book
deadline submit report /by 2019-12-02
deadline SUBMIT REPORT /by 2019-12-02
deadline submit report /by 2019-12-03
event meeting /from 2019-12-04 1400 /to 2019-12-04 1600
event MEETING /from 2019-12-04 1400 /to 2019-12-04 1600
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] Read Book
Now you have 1 tasks in the list.
Nice! I've marked this task as done:
[T][X] Read Book
OOPS!!! That task is already in your list.
Got it. I've added this task:
[D][ ] submit report (by: Dec 02 2019)
Now you have 2 tasks in the list.
OOPS!!! That task is already in your list.
Got it. I've added this task:
[D][ ] submit report (by: Dec 03 2019)
Now you have 3 tasks in the list.
Got it. I've added this task:
[E][ ] meeting (from: Dec 04 2019, 2:00PM to: Dec 04 2019, 4:00PM)
Now you have 4 tasks in the list.
OOPS!!! That task is already in your list.
Here are the tasks in your list:
1.[T][X] Read Book
2.[D][ ] submit report (by: Dec 02 2019)
3.[D][ ] submit report (by: Dec 03 2019)
4.[E][ ] meeting (from: Dec 04 2019, 2:00PM to: Dec 04 2019, 4:00PM)
Bye. Hope to see you again soon!
```

## TC28 — Reject duplicate tasks in saved data

**Aim:** Verify that equivalent saved tasks report the duplicate line and cause a safe fallback to an empty list.

### Initial data file

```text
T | 1 | Read Book
T | 0 | read book
```

### Inputs

```text
list
bye
```

### Expected output

```text
OOPS!!! I couldn't load your data file: invalid data on line 2 (duplicates an earlier task). I started with an empty list.
Here are the tasks in your list:
Bye. Hope to see you again soon!
```
