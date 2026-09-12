# UI Test Plan

Run each test case in a fresh instance of `Pixel`. Output comparisons are exact except that CRLF and LF line endings are considered equivalent. Each command below is followed by Enter.

## UI-00: Reject an empty ToDo and an unknown command

**Aim:** Verify that Pixel starts normally when no data file exists, and that a ToDo without a description and an unrecognized command produce the required error messages without being added as tasks.

**Storage setup:** Ensure `data/pixel.txt` does not exist before starting Pixel.

**Input commands:**

```text
todo
blah
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Oops! Please give me a description for the todo.
____________________________________________________________
____________________________________________________________
Sorry, I don't recognise that command.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-01: Add and list tasks

**Aim:** Verify that Pixel stores entered task descriptions, lists them in order as not done, and exits on `bye`.

**Input commands:**

```text
todo read book
todo return book
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-02: Mark and unmark a task

**Aim:** Verify that `mark` completes the selected task and `unmark` reverses its done status.

**Input commands:**

```text
todo read book
todo return book
mark 2
list
unmark 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][X] return book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-03: Add and manage typed tasks

**Aim:** Verify that Pixel stores Deadline dates in ISO format, displays them as `MMM dd yyyy`, and preserves typed-task formatting when marking and unmarking.

**Input commands:**

```text
todo borrow book
deadline do homework /by 2019-12-02
event project meeting /from Mon 2pm /to 4pm
mark 2
list
unmark 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] do homework (by: Dec 02 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] do homework (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][X] do homework (by: Dec 02 2019)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [D][ ] do homework (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] do homework (by: Dec 02 2019)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-04: Reject malformed dated tasks

**Aim:** Verify that incomplete Deadline and Event commands show usage guidance without terminating Pixel or adding invalid tasks.

**Input commands:**

```text
deadline do homework
deadline /by Sunday
event project meeting /from Monday
event project meeting /from /to Friday
todo valid task
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Oops! Please specify the deadline using /by.
____________________________________________________________
____________________________________________________________
Oops! The deadline description and date cannot be empty.
____________________________________________________________
____________________________________________________________
Oops! Please specify the event using /from and /to.
____________________________________________________________
____________________________________________________________
Oops! The event description and times cannot be empty.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] valid task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] valid task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-05: Preserve task state across rejected creation commands

**Aim:** Verify that missing and invalid Deadline data, malformed Events, and unknown commands interleaved with valid additions neither create tasks nor disturb task order.

**Input commands:**

```text
todo alpha
deadline missing deadline
deadline impossible /by 2023-02-29
deadline beta /by 2026-08-23
event broken /from /to Friday
event gamma /from Monday /to Tuesday
blah
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] alpha
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Oops! Please specify the deadline using /by.
____________________________________________________________
____________________________________________________________
Oops! Please enter the deadline date in YYYY-MM-DD format.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] beta (by: Aug 23 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Oops! The event description and times cannot be empty.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] gamma (from: Monday to: Tuesday)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Sorry, I don't recognise that command.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] alpha
2.[D][ ] beta (by: Aug 23 2026)
3.[E][ ] gamma (from: Monday to: Tuesday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-06: Preserve completion state across invalid task numbers

**Aim:** Verify that invalid, out-of-range, zero, and nonnumeric mark/unmark commands do not modify the completion state of existing tasks.

**Input commands:**

```text
todo alpha
mark 1
mark 0
mark 2
mark abc
unmark 1
unmark 0
unmark abc
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] alpha
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] alpha
____________________________________________________________
____________________________________________________________
Please specify a valid task number after mark.
____________________________________________________________
____________________________________________________________
That task number does not exist.
____________________________________________________________
____________________________________________________________
Please specify a valid task number after mark.
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] alpha
____________________________________________________________
____________________________________________________________
Please specify a valid task number after unmark.
____________________________________________________________
____________________________________________________________
Please specify a valid task number after unmark.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] alpha
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-07: Recover after missing task fields

**Aim:** Verify that empty descriptions and missing date/time fields are rejected and that a subsequent valid task can still be added and listed.

**Input commands:**

```text
todo
deadline
deadline homework /by
event
event meeting /from Monday
event meeting /from Monday /to
todo recovered
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Oops! Please give me a description for the todo.
____________________________________________________________
____________________________________________________________
Oops! Please give me a description and deadline.
____________________________________________________________
____________________________________________________________
Oops! The deadline description and date cannot be empty.
____________________________________________________________
____________________________________________________________
Oops! Please give me an event description and time.
____________________________________________________________
____________________________________________________________
Oops! Please specify the event using /from and /to.
____________________________________________________________
____________________________________________________________
Oops! The event description and times cannot be empty.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] recovered
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] recovered
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-08: Delete tasks and preserve list order

**Aim:** Verify that deleting a middle task removes the correct object, shifts later tasks forward, updates the count, and uses the new numbering for a subsequent deletion.

**Input commands:**

```text
todo alpha
deadline beta /by 2026-08-23
event gamma /from Monday /to Tuesday
mark 2
delete 2
list
delete 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] alpha
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] beta (by: Aug 23 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] gamma (from: Monday to: Tuesday)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] beta (by: Aug 23 2026)
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [D][X] beta (by: Aug 23 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] alpha
2.[E][ ] gamma (from: Monday to: Tuesday)
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] gamma (from: Monday to: Tuesday)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] alpha
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-09: Preserve state across invalid delete commands

**Aim:** Verify that zero, out-of-range, and nonnumeric delete commands are rejected without removing or modifying existing tasks.

**Input commands:**

```text
todo alpha
mark 1
delete 0
delete 2
delete abc
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] alpha
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] alpha
____________________________________________________________
____________________________________________________________
Please specify a valid task number after delete.
____________________________________________________________
____________________________________________________________
That task number does not exist.
____________________________________________________________
____________________________________________________________
Please specify a valid task number after delete.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] alpha
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-10: Save task changes to disk

**Aim:** Verify that adding, marking, unmarking, and deleting typed tasks saves the resulting list to `data/pixel.txt` without changing console behavior.

**Input commands:**

```text
todo alpha
deadline beta /by 2026-08-23
event gamma /from Monday /to Tuesday
mark 1
unmark 1
mark 2
delete 3
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] alpha
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] beta (by: Aug 23 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] gamma (from: Monday to: Tuesday)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] alpha
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] alpha
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] beta (by: Aug 23 2026)
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] gamma (from: Monday to: Tuesday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

**Expected saved file (`data/pixel.txt`):**

```text
T | 0 | alpha
D | 1 | beta | 2026-08-23
```

## UI-11: Load tasks and continue saving changes

**Aim:** Verify that Pixel restores ToDos, Deadlines, Events, and their done statuses at startup, then correctly saves later changes to those loaded tasks.

**Initial saved file (`data/pixel.txt`):**

```text
T | 1 | alpha
D | 0 | beta | 2026-08-23
E | 1 | gamma | Monday | Tuesday
```

**Input commands:**

```text
list
mark 2
unmark 3
delete 1
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] alpha
2.[D][ ] beta (by: Aug 23 2026)
3.[E][X] gamma (from: Monday to: Tuesday)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] beta (by: Aug 23 2026)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [E][ ] gamma (from: Monday to: Tuesday)
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][X] alpha
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][X] beta (by: Aug 23 2026)
2.[E][ ] gamma (from: Monday to: Tuesday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

**Expected saved file (`data/pixel.txt`):**

```text
D | 1 | beta | 2026-08-23
E | 0 | gamma | Monday | Tuesday
```

## UI-12: Recover valid tasks from a partially corrupted file

**Aim:** Verify that blank and malformed saved records are skipped with line-specific warnings while valid neighboring records remain usable and are cleaned into the next saved file.

**Initial saved file (`data/pixel.txt`):**

```text
T | 1 | valid todo

X | 0 | unknown
D | 2 | invalid status | Sunday
D | 0 | invalid date | Friday
D | 0 | valid deadline | 2026-08-23
E | 0 | missing end | Monday
T | 0 | 
E | 1 | valid event | Mon | Tue
```

**Input commands:**

```text
list
todo recovered
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
Oops! I skipped invalid saved task on line 3: unknown task type 'X'.
Oops! I skipped invalid saved task on line 4: status must be 0 or 1.
Oops! I skipped invalid saved task on line 5: deadline must be a valid date in YYYY-MM-DD format.
Oops! I skipped invalid saved task on line 7: expected 5 fields but found 4.
Oops! I skipped invalid saved task on line 8: task description cannot be empty.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] valid todo
2.[D][ ] valid deadline (by: Aug 23 2026)
3.[E][X] valid event (from: Mon to: Tue)
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] recovered
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] valid todo
2.[D][ ] valid deadline (by: Aug 23 2026)
3.[E][X] valid event (from: Mon to: Tue)
4.[T][ ] recovered
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

**Expected saved file (`data/pixel.txt`):**

```text
T | 1 | valid todo
D | 0 | valid deadline | 2026-08-23
E | 1 | valid event | Mon | Tue
T | 0 | recovered
```

## UI-13: Round-trip storage separator characters

**Aim:** Verify that literal pipe and backslash characters in saved fields load correctly and remain escaped after subsequent task changes.

**Initial saved file (`data/pixel.txt`):**

```text
T | 0 | plan A \| plan B
D | 1 | use C:\\temp | 2026-08-23
E | 0 | sync \| review | Mon \\ morning | Tue \| night
```

**Input commands:**

```text
list
mark 1
delete 2
todo path C:\work | docs
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] plan A | plan B
2.[D][X] use C:\temp (by: Aug 23 2026)
3.[E][ ] sync | review (from: Mon \ morning to: Tue | night)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] plan A | plan B
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [D][X] use C:\temp (by: Aug 23 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] path C:\work | docs
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] plan A | plan B
2.[E][ ] sync | review (from: Mon \ morning to: Tue | night)
3.[T][ ] path C:\work | docs
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

**Expected saved file (`data/pixel.txt`):**

```text
T | 1 | plan A \| plan B
E | 0 | sync \| review | Mon \\ morning | Tue \| night
T | 0 | path C:\\work \| docs
```

## UI-14: Recover from missing arguments and flexible whitespace

**Aim:** Verify that leading/trailing whitespace, repeated delimiter whitespace, missing task numbers, and an overflowing task number are handled without corrupting the task list.

**Input commands:**

```text
   todo alpha   
mark
unmark
delete
mark 999999999999999999999
deadline beta   /by   2026-08-23
event gamma   /from   Mon   /to   Tue
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] alpha
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Please specify a valid task number after mark.
____________________________________________________________
____________________________________________________________
Please specify a valid task number after unmark.
____________________________________________________________
____________________________________________________________
Please specify a valid task number after delete.
____________________________________________________________
____________________________________________________________
Please specify a valid task number after mark.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] beta (by: Aug 23 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] gamma (from: Mon to: Tue)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] alpha
2.[D][ ] beta (by: Aug 23 2026)
3.[E][ ] gamma (from: Mon to: Tue)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-15: Recover when the save path cannot be read

**Aim:** Verify that Pixel reports an unreadable save path, starts with a safe empty list, and remains usable.

**Storage setup:** Create an empty directory at `data/pixel.txt` before starting Pixel.

**Input commands:**

```text
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
Oops! I couldn't read the saved tasks. Starting with an empty list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-16: Keep working when task changes cannot be saved

**Aim:** Verify that a write failure is reported while the in-memory task change remains available for the rest of the session.

**Storage setup:** Create an empty directory at `data/pixel.txt.tmp` before starting Pixel so the temporary save file cannot be written.

**Input commands:**

```text
todo unsaved
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Oops! I couldn't save your tasks. Your changes will only last until Pixel exits.
Got it. I've added this task:
  [T][ ] unsaved
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] unsaved
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-17: Find deadlines occurring on a date

**Aim:** Verify that `date` lists Deadlines and ISO-dated Events occurring on a date using original task numbers, includes Events spanning that date, excludes undated tasks, handles no matches, rejects invalid or missing dates, and does not modify task state.

**Input commands:**

```text
todo undated
deadline submit report /by 2019-12-02
deadline return book /by 2019-12-02
event conference /from 2019-12-01 09:00 /to 2019-12-03 17:00
deadline later task /by 2019-12-03
date 2019-12-02
date 2019-12-04
date 2023-02-29
date
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] undated
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: Dec 02 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] conference (from: 2019-12-01 09:00 to: 2019-12-03 17:00)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] later task (by: Dec 03 2019)
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks occurring on Dec 02 2019:
2.[D][ ] submit report (by: Dec 02 2019)
3.[D][ ] return book (by: Dec 02 2019)
4.[E][ ] conference (from: 2019-12-01 09:00 to: 2019-12-03 17:00)
____________________________________________________________
____________________________________________________________
There are no tasks occurring on Dec 04 2019.
____________________________________________________________
____________________________________________________________
Oops! Please enter a valid date after date in YYYY-MM-DD format.
____________________________________________________________
____________________________________________________________
Oops! Please enter a valid date after date in YYYY-MM-DD format.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] undated
2.[D][ ] submit report (by: Dec 02 2019)
3.[D][ ] return book (by: Dec 02 2019)
4.[E][ ] conference (from: 2019-12-01 09:00 to: 2019-12-03 17:00)
5.[D][ ] later task (by: Dec 03 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-18: Update task details without changing other state

**Aim:** Verify that `update` changes only the selected detail, preserves task type,
completion status, position, and unspecified details, saves valid updates, and rejects
invalid updates without changing the list.

**Input commands:**

```text
todo read book
mark 1
update 1 /description read novel
update 1 /to 5pm
deadline submit report /by 2026-09-05
update 2 /by 2026-09-10
update 2 /by tomorrow
event meeting /from 2pm /to 4pm
update 3 /to 5pm
update 3 /from 3pm
update 9 /description missing
update abc /description invalid
update 1 description invalid
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Got it. I've updated this task:
  [T][X] read novel
____________________________________________________________
____________________________________________________________
Only events have a /to time.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: Sep 05 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've updated this task:
  [D][ ] submit report (by: Sep 10 2026)
____________________________________________________________
____________________________________________________________
Oops! Please enter the deadline date in YYYY-MM-DD format.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've updated this task:
  [E][ ] meeting (from: 2pm to: 5pm)
____________________________________________________________
____________________________________________________________
Got it. I've updated this task:
  [E][ ] meeting (from: 3pm to: 5pm)
____________________________________________________________
____________________________________________________________
That task number does not exist.
____________________________________________________________
____________________________________________________________
Please specify a valid task number after update.
____________________________________________________________
____________________________________________________________
Use /description, /by, /from, or /to followed by a new value.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read novel
2.[D][ ] submit report (by: Sep 10 2026)
3.[E][ ] meeting (from: 3pm to: 5pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

**Expected saved file (`data/pixel.txt`):**

```text
T | 1 | read novel
D | 0 | submit report | 2026-09-10
E | 0 | meeting | 3pm | 5pm
```

## UI-19: Reject duplicate and contradictory input without corrupting state

**Aim:** Verify that flexible outer whitespace remains accepted while duplicate tasks, repeated parameters,
invalid dates, reversed event ranges, invalid task numbers, and multi-field updates are rejected without changing
the valid task list.

**Input commands:**

```text
   todo   alpha
todo alpha
deadline report /by 2026-02-30
deadline report /by 2026-12-01 /by 2026-12-02
deadline report /by 2026-12-01
event sprint /from 2026-12-02 /to 2026-12-01
event sprint /to 2026-12-02 /from 2026-12-01
event sprint /from 4pm /to 2pm
event sprint /from 2026-12-01 /to 2026-12-02
event sprint /from 2026-12-01 /to 2026-12-02
mark 0
mark 1 extra
update 1 /description beta /to 5pm
todo beta
update 4 /description alpha
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____  _          _ 
|  _ \(_)_  _____| |
| |_) | \ \/ / _ \ |
|  __/| |>  <  __/ |
|_|   |_/_/\_\___|_|
Hello! I'm Pixel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] alpha
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
This task is already in your list.
____________________________________________________________
____________________________________________________________
Oops! Please enter the deadline date in YYYY-MM-DD format.
____________________________________________________________
____________________________________________________________
Oops! Please specify /by exactly once.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] report (by: Dec 01 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Oops! The event start must be before the event end.
____________________________________________________________
____________________________________________________________
Oops! Please place /from before /to.
____________________________________________________________
____________________________________________________________
Oops! The event start must be before the event end.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] sprint (from: 2026-12-01 to: 2026-12-02)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
This task is already in your list.
____________________________________________________________
____________________________________________________________
Please specify a valid task number after mark.
____________________________________________________________
____________________________________________________________
Please specify a valid task number after mark.
____________________________________________________________
____________________________________________________________
Please update exactly one detail at a time.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] beta
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
That update would duplicate another task in your list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] alpha
2.[D][ ] report (by: Dec 01 2026)
3.[E][ ] sprint (from: 2026-12-01 to: 2026-12-02)
4.[T][ ] beta
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
