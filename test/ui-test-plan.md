# Orbit UI Test Plan

Run each case in a clean folder. Restart inputs within a case reuse that folder. Expected lines are matched as ordered fragments so optional banners, separators, and indentation can change without making the tests brittle.

These command-line behaviors are unchanged by Level 10. The GUI uses the same command processor;
its additional interaction and layout checks are in [gui-test-plan.md](gui-test-plan.md).

## TC1: Add and list every task type

Aim: Verify every task type retains its marker and displays stored dates in a friendly format.

### Input

```text
todo borrow book
deadline return book /by 2026-09-06
event project meeting /from 2026-09-07 /to 2026-09-08
list
bye
```

### Expected output (ordered fragments)

```text
[T][ ] borrow book
Now you have 1 tasks in the list.
[D][ ] return book (by: Sep 6 2026)
Now you have 2 tasks in the list.
[E][ ] project meeting (from: Sep 7 2026 to: Sep 8 2026)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sep 6 2026)
3.[E][ ] project meeting (from: Sep 7 2026 to: Sep 8 2026)
Bye. Hope to see you again soon!
```

## TC2: Reject unknown commands and continue

Aim: Verify blank, unknown, and near-miss commands report errors without exiting or creating tasks.

### Input

```text
blah

todofoo
list extra
bye now
todo recovered
list
bye
```

### Expected output (ordered fragments)

```text
OOPS! I don't know that command.
OOPS! Please enter a command.
OOPS! I don't know that command.
OOPS! I don't know that command.
OOPS! I don't know that command.
[T][ ] recovered
Now you have 1 tasks in the list.
1.[T][ ] recovered
Bye. Hope to see you again soon!
```

## TC3: Reject malformed typed tasks

Aim: Verify missing descriptions, markers, dates, and times are handled without terminating Orbit.

### Input

```text
todo
deadline
deadline write report
deadline /by 2026-09-06
deadline write report /by
deadline write report /by 2026-09-06 /by 2026-09-07
event
event meeting /from 2026-09-07
event meeting /to 2026-09-08
event meeting /to 2026-09-08 /from 2026-09-07
event /from 2026-09-07 /to 2026-09-08
event meeting /from /to 2026-09-08
event meeting /from 2026-09-07 /to
todo valid task
list
bye
```

### Expected output (ordered fragments)

```text
OOPS! The description of a todo cannot be empty.
OOPS! Use: deadline <description> /by <yyyy-MM-dd>.
OOPS! Use: deadline <description> /by <yyyy-MM-dd>.
OOPS! The description of a deadline cannot be empty.
OOPS! The date of a deadline cannot be empty.
OOPS! Use: deadline <description> /by <yyyy-MM-dd>.
OOPS! Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.
OOPS! Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.
OOPS! Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.
OOPS! Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.
OOPS! The description of an event cannot be empty.
OOPS! The start of an event cannot be empty.
OOPS! The end of an event cannot be empty.
[T][ ] valid task
Now you have 1 tasks in the list.
1.[T][ ] valid task
Bye. Hope to see you again soon!
```

## TC4: Preserve task state across index errors

Aim: Verify invalid mark and unmark commands do not mutate tasks and valid commands still work afterward.

### Input

```text
todo alpha
deadline beta /by 2026-09-04
mark 2
mark
mark abc
mark 1 2
mark 0
unmark 3
deadline broken
event broken /from 2026-09-07
list
unmark 2
list
bye
```

### Expected output (ordered fragments)

```text
[T][ ] alpha
Now you have 1 tasks in the list.
[D][ ] beta (by: Sep 4 2026)
Now you have 2 tasks in the list.
[D][X] beta (by: Sep 4 2026)
OOPS! Please provide exactly one task number.
OOPS! Please provide a valid task number.
OOPS! Please provide exactly one task number.
OOPS! Task number 0 is out of range.
OOPS! Task number 3 is out of range.
OOPS! Use: deadline <description> /by <yyyy-MM-dd>.
OOPS! Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.
1.[T][ ] alpha
2.[D][X] beta (by: Sep 4 2026)
[D][ ] beta (by: Sep 4 2026)
1.[T][ ] alpha
2.[D][ ] beta (by: Sep 4 2026)
Bye. Hope to see you again soon!
```

## TC5: Mark and unmark a typed task

Aim: Verify completion changes are reflected without losing the task type.

### Input

```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected output (ordered fragments)

```text
[T][ ] read book
[T][X] read book
[T][ ] read book
1.[T][ ] read book
Bye. Hope to see you again soon!
```

## TC6: Parse real dates and reject invalid dates

Aim: Verify ISO dates become calendar values while malformed and impossible dates are rejected.

### Input

```text
deadline do homework /by no idea :-p
deadline impossible /by 2026-02-29
event invalid start /from Monday /to 2026-09-02
event invalid end /from 2026-09-01 /to Tuesday
event same day /from 2026-09-02 /to 2026-09-02
event backwards /from 2026-09-03 /to 2026-09-02
deadline leap day /by 2028-02-29
event release /from 2026-09-01 /to 2026-09-02
list
bye
```

### Expected output (ordered fragments)

```text
OOPS! Please enter dates as yyyy-MM-dd.
OOPS! Please enter dates as yyyy-MM-dd.
OOPS! Please enter dates as yyyy-MM-dd.
OOPS! Please enter dates as yyyy-MM-dd.
OOPS! The end date of an event must be after its start date.
OOPS! The end date of an event must be after its start date.
[D][ ] leap day (by: Feb 29 2028)
[E][ ] release (from: Sep 1 2026 to: Sep 2 2026)
1.[D][ ] leap day (by: Feb 29 2028)
2.[E][ ] release (from: Sep 1 2026 to: Sep 2 2026)
Bye. Hope to see you again soon!
```

## TC7: Delete typed tasks and renumber the list

Aim: Verify deleting a marked middle task preserves its displayed state and renumbers the remaining tasks.

### Input

```text
todo alpha
deadline beta /by 2026-09-04
event gamma /from 2026-09-07 /to 2026-09-08
mark 2
delete 2
list
delete 1
delete 1
list
bye
```

### Expected output (ordered fragments)

```text
[T][ ] alpha
[D][ ] beta (by: Sep 4 2026)
[E][ ] gamma (from: Sep 7 2026 to: Sep 8 2026)
[D][X] beta (by: Sep 4 2026)
Noted. I've removed this task:
[D][X] beta (by: Sep 4 2026)
Now you have 2 tasks in the list.
1.[T][ ] alpha
2.[E][ ] gamma (from: Sep 7 2026 to: Sep 8 2026)
Noted. I've removed this task:
[T][ ] alpha
Now you have 1 tasks in the list.
Noted. I've removed this task:
[E][ ] gamma (from: Sep 7 2026 to: Sep 8 2026)
Now you have 0 tasks in the list.
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

## TC8: Reject invalid delete commands without mutation

Aim: Verify malformed and out-of-range delete commands report errors while leaving valid tasks available.

### Input

```text
delete
delete abc
delete 1
todo only task
delete 0
delete 2
delete 1 2
delete 999999999999999999999999
list
delete 1
delete 1
bye
```

### Expected output (ordered fragments)

```text
OOPS! Please provide exactly one task number.
OOPS! Please provide a valid task number.
OOPS! Task number 1 is out of range.
[T][ ] only task
Now you have 1 tasks in the list.
OOPS! Task number 0 is out of range.
OOPS! Task number 2 is out of range.
OOPS! Please provide exactly one task number.
OOPS! Please provide a valid task number.
1.[T][ ] only task
Noted. I've removed this task:
[T][ ] only task
Now you have 0 tasks in the list.
OOPS! Task number 1 is out of range.
Bye. Hope to see you again soon!
```

## TC9: Persist every task type and mutation

Aim: Verify tasks, punctuation, order, and completion status survive restarts, including later mutations.

### Input

```text
todo save | punctuation
deadline saved deadline /by 2026-09-04
event saved event /from 2026-09-01 /to 2026-09-02
mark 2
bye
```

### Restart input 1

```text
list
unmark 2
delete 1
bye
```

### Restart input 2

```text
list
bye
```

### Expected output (ordered fragments)

```text
[T][ ] save | punctuation
[D][ ] saved deadline (by: Sep 4 2026)
[E][ ] saved event (from: Sep 1 2026 to: Sep 2 2026)
[D][X] saved deadline (by: Sep 4 2026)
Bye. Hope to see you again soon!
1.[T][ ] save | punctuation
2.[D][X] saved deadline (by: Sep 4 2026)
3.[E][ ] saved event (from: Sep 1 2026 to: Sep 2 2026)
[D][ ] saved deadline (by: Sep 4 2026)
Noted. I've removed this task:
[T][ ] save | punctuation
Now you have 2 tasks in the list.
Bye. Hope to see you again soon!
1.[D][ ] saved deadline (by: Sep 4 2026)
2.[E][ ] saved event (from: Sep 1 2026 to: Sep 2 2026)
Bye. Hope to see you again soon!
```

## TC10: Find tasks by description without mutation

Aim: Verify find supports keywords and phrases, preserves match order, renumbers results, and leaves the full list unchanged.

### Input

```text
todo read book
deadline return book /by 2026-09-06
event book club /from 2026-09-07 /to 2026-09-08
todo read notes
find book
find read book
find missing
find
finder book
list
bye
```

### Expected output (ordered fragments)

```text
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Sep 6 2026)
3.[E][ ] book club (from: Sep 7 2026 to: Sep 8 2026)
Here are the matching tasks in your list:
1.[T][ ] read book
Here are the matching tasks in your list:
OOPS! The keyword for a find command cannot be empty.
OOPS! I don't know that command.
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Sep 6 2026)
3.[E][ ] book club (from: Sep 7 2026 to: Sep 8 2026)
4.[T][ ] read notes
Bye. Hope to see you again soon!
```

## TC11: Sort tasks chronologically and persist their order

Aim: Verify sorting uses dates, preserves equal-date and todo order, rejects invalid syntax, and
saves the new task numbers across restarts.

### Input

```text
todo undated first
deadline later /by 2026-09-09
event earliest /from 2026-09-02 /to 2026-09-03
deadline middle /by 2026-09-04
todo undated last
mark 2
sort
sort extra
sorter
bye
```

### Restart input 1

```text
list
bye
```

### Expected output (ordered fragments)

```text
[D][X] later (by: Sep 9 2026)
Here are your tasks sorted chronologically:
1.[E][ ] earliest (from: Sep 2 2026 to: Sep 3 2026)
2.[D][ ] middle (by: Sep 4 2026)
3.[D][X] later (by: Sep 9 2026)
4.[T][ ] undated first
5.[T][ ] undated last
OOPS! I don't know that command.
OOPS! I don't know that command.
Bye. Hope to see you again soon!
Here are the tasks in your list:
1.[E][ ] earliest (from: Sep 2 2026 to: Sep 3 2026)
2.[D][ ] middle (by: Sep 4 2026)
3.[D][X] later (by: Sep 9 2026)
4.[T][ ] undated first
5.[T][ ] undated last
Bye. Hope to see you again soon!
```
