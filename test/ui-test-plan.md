# Bo UI test plan

These tests exercise Bo through its standard input and output. Each test case
starts a fresh Bo process. The runner checks the expected non-empty output
lines in order, ignoring only surrounding whitespace; decorative separators and
the startup banner therefore do not need to be repeated in every expectation.

Run the complete plan with:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner compiles with Java 25, prints the full console input/output for each
case, and stops immediately if a case fails.

## Test case 1: Add each task type and list tasks

Aim: Verify that todo, deadline, and event commands create the correct task types and that `list` displays them in order.

### Input

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: June 6th)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Bye. Hope to see you again soon!
```

## Test case 2: Skip malformed saved records

Aim: Verify that malformed records do not crash Bo or prevent valid records later in the file from loading.

### Initial saved file

```text
T | 1 | valid todo
not a task record
D | 0 | valid deadline | Friday
E | 2 | invalid status | Monday | Tuesday
E | 0 | valid event | Monday | Tuesday
```

### Input

```text
list
bye
```

### Expected output

```text
Warning: I skipped 2 invalid task line(s) in the saved file.
Here are the tasks in your list:
1.[T][X] valid todo
2.[D][ ] valid deadline (by: Friday)
3.[E][ ] valid event (from: Monday to: Tuesday)
Bye. Hope to see you again soon!
```

## Test case 3: Preserve separator characters in task fields

Aim: Verify that task descriptions and date fields containing the storage separator or a backslash survive saving and loading.

### Input

```text
todo compare A | B \\ C
deadline review A | B /by Friday | 5pm
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] compare A | B \\ C
Got it. I've added this task:
[D][ ] review A | B (by: Friday | 5pm)
Here are the tasks in your list:
1.[T][ ] compare A | B \\ C
2.[D][ ] review A | B (by: Friday | 5pm)
Bye. Hope to see you again soon!
```

### Expected saved file

```text
T | 0 | compare A \| B \\\\ C
D | 0 | review A \| B | Friday \| 5pm
```

## Test case 4: Mark and unmark a task

Aim: Verify that `mark` changes a task's status to done and `unmark` changes it back.

### Input

```text
todo read book
todo return book
mark 2
unmark 2
list
bye
```

### Expected output

```text
Nice! I've marked this task as done:
[T][X] return book
OK, I've marked this task as not done yet:
[T][ ] return book
1.[T][ ] read book
2.[T][ ] return book
Bye. Hope to see you again soon!
```

## Test case 5: Delete a task and renumber the list

Aim: Verify that deleting a task removes the selected task, shifts later tasks, and updates the task count.

### Input

```text
todo read book
todo return book
todo buy bread
mark 1
delete 2
list
bye
```

### Expected output

```text
Nice! I've marked this task as done:
[T][X] read book
Noted. I've removed this task:
[T][ ] return book
Now you have 2 tasks in the list.
1.[T][X] read book
2.[T][ ] buy bread
Bye. Hope to see you again soon!
```

## Test case 6: Reject invalid commands and arguments

Aim: Verify that malformed input produces friendly errors and does not terminate Bo or add invalid tasks.

### Input

```text
todo
deadline return book
event project meeting /from Monday
blah
delete abc
delete 99
mark
list
bye
```

### Expected output

```text
OOPS!!! The description of a todo cannot be empty.
OOPS!!! A deadline must include a /by date, e.g. deadline return book /by Friday.
OOPS!!! An event must include a /to time.
OOPS!!! I'm sorry, but I don't know what that means :-(
OOPS!!! The task number must be a whole number.
OOPS!!! I couldn't find a task with that number.
OOPS!!! Please use mark followed by one task number, e.g. mark 1.
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

## Test case 7: Save the task list after changes

Aim: Verify that adding, marking, unmarking, and deleting tasks writes the current task list to disk.

### Input

```text
todo write report
deadline submit form /by Friday
event project sync /from Monday /to Tuesday
mark 1
unmark 1
delete 2
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] write report
Got it. I've added this task:
[D][ ] submit form (by: Friday)
Got it. I've added this task:
[E][ ] project sync (from: Monday to: Tuesday)
Nice! I've marked this task as done:
[T][X] write report
OK, I've marked this task as not done yet:
[T][ ] write report
Noted. I've removed this task:
[D][ ] submit form (by: Friday)
Bye. Hope to see you again soon!
```

### Expected saved file

```text
T | 0 | write report
E | 0 | project sync | Monday | Tuesday
```

## Test case 8: Load tasks at startup

Aim: Verify that Bo loads todo, deadline, and event tasks, including their completion status, from the saved file when it starts.

### Initial saved file

```text
T | 1 | read book
D | 0 | return book | Friday
E | 0 | project sync | Monday | Tuesday
```

### Input

```text
list
bye
```

### Expected output

```text
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Friday)
3.[E][ ] project sync (from: Monday to: Tuesday)
Bye. Hope to see you again soon!
```

## Test case 9: Parse and format dates and times

Aim: Verify that ISO dates and day/month/year date-times are stored as typed values and displayed in a human-readable format.

### Input

```text
deadline return book /by 2019-10-15
deadline return book /by 2/12/2019 1800
event project meeting /from 2019-10-15 /to 2019-10-16 0905
list
bye
```

### Expected output

```text
Got it. I've added this task:
[D][ ] return book (by: Oct 15 2019)
Got it. I've added this task:
[D][ ] return book (by: Dec 02 2019 18:00)
Got it. I've added this task:
[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019 09:05)
Here are the tasks in your list:
1.[D][ ] return book (by: Oct 15 2019)
2.[D][ ] return book (by: Dec 02 2019 18:00)
3.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019 09:05)
Bye. Hope to see you again soon!
```

### Expected saved file

```text
D | 0 | return book | 2019-10-15
D | 0 | return book | 2019-12-02T18:00:00
E | 0 | project meeting | 2019-10-15 | 2019-10-16T09:05:00
```

## Test case 10: Read typed dates from the storage file

Aim: Verify that dates and date-times saved in ISO form are loaded into task objects and displayed using Bo's human-readable format.

### Initial saved file

```text
D | 1 | submit report | 2019-10-15
D | 0 | return book | 2019-12-02T18:00:00
E | 0 | project meeting | 2019-10-15T08:00:00 | 2019-10-16T09:05:00
```

### Input

```text
list
bye
```

### Expected output

```text
Here are the tasks in your list:
1.[D][X] submit report (by: Oct 15 2019)
2.[D][ ] return book (by: Dec 02 2019 18:00)
3.[E][ ] project meeting (from: Oct 15 2019 08:00 to: Oct 16 2019 09:05)
Bye. Hope to see you again soon!
```

## Test case 11: Rewrite typed dates after task mutations

Aim: Verify that reading typed dates and then marking, deleting, and adding tasks rewrites the complete storage file in canonical ISO form.

### Initial saved file

```text
D | 0 | old deadline | 2019-10-15
E | 0 | old event | 2019-10-16T10:00:00 | 2019-10-16T11:00:00
```

### Input

```text
mark 1
unmark 1
delete 2
deadline new deadline /by 2/12/2019 1800
event new event /from 2019-11-01 /to 2019-11-02
list
bye
```

### Expected output

```text
Nice! I've marked this task as done:
[D][X] old deadline (by: Oct 15 2019)
OK, I've marked this task as not done yet:
[D][ ] old deadline (by: Oct 15 2019)
Noted. I've removed this task:
[E][ ] old event (from: Oct 16 2019 10:00 to: Oct 16 2019 11:00)
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] new deadline (by: Dec 02 2019 18:00)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] new event (from: Nov 01 2019 to: Nov 02 2019)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[D][ ] old deadline (by: Oct 15 2019)
2.[D][ ] new deadline (by: Dec 02 2019 18:00)
3.[E][ ] new event (from: Nov 01 2019 to: Nov 02 2019)
Bye. Hope to see you again soon!
```

### Expected saved file

```text
D | 0 | old deadline | 2019-10-15
D | 0 | new deadline | 2019-12-02T18:00:00
E | 0 | new event | 2019-11-01 | 2019-11-02
```

## Test case 12: Skip malformed storage records around valid dates

Aim: Verify that invalid escapes and wrong field counts are skipped without preventing valid typed deadline and event records from loading.

### Initial saved file

```text
D | 0 | valid deadline | 2019-10-15
D | 0 | invalid escape | 2019-10-15\q
E | 0 | valid event | 2019-10-16T08:00:00 | 2019-10-16T09:00:00
E | 0 | missing end | 2019-10-16T08:00:00
```

### Input

```text
list
bye
```

### Expected output

```text
Warning: I skipped 2 invalid task line(s) in the saved file.
Here are the tasks in your list:
1.[D][ ] valid deadline (by: Oct 15 2019)
2.[E][ ] valid event (from: Oct 16 2019 08:00 to: Oct 16 2019 09:00)
Bye. Hope to see you again soon!
```

## Test case 13: Find tasks by keyword

Aim: Verify that find displays only tasks whose descriptions contain the keyword, ignoring case.

### Input

```text
todo read book
todo buy milk
deadline return book /by Friday
find BOOK
bye
```

### Expected output

```text
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Friday)
Bye. Hope to see you again soon!
```

## Test case 14: Sort tasks by date

Aim: Verify that sort orders deadlines by date, events by start time, and keeps undated tasks last in their original relative order.

### Input

```text
todo undated first
deadline later deadline /by 2026-08-28
event earlier event /from 2026-08-20 /to 2026-08-21
deadline earlier deadline /by 2026-08-15
todo undated second
sort
bye
```

### Expected output

```text
Here are your tasks sorted by date:
1.[D][ ] earlier deadline (by: Aug 15 2026)
2.[E][ ] earlier event (from: Aug 20 2026 to: Aug 21 2026)
3.[D][ ] later deadline (by: Aug 28 2026)
4.[T][ ] undated first
5.[T][ ] undated second
Bye. Hope to see you again soon!
```

## Test case 15: Handle whitespace, impossible dates, and duplicate parameters

Aim: Verify that harmless whitespace is normalized and malformed structured dates or duplicate parameters are rejected without terminating Bo.

### Input

```text
  todo   read   book
deadline invalid date /by 2019-02-29
deadline duplicate /by Friday /by Monday
event reversed /from 2019-10-16 1000 /to 2019-10-16 0900
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] read book
OOPS!!! The /by date of a deadline is invalid. Please use a valid date or time.
OOPS!!! A deadline can include only one /by date.
OOPS!!! The /from time of an event must be before its /to time.
Here are the tasks in your list:
1.[T][ ] read book
Bye. Hope to see you again soon!
```
