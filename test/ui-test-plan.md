# UI Test Plan

## Test setup

- Application: Friday console chatbot
- Working directory: a fresh temporary directory for each isolated case, so its `data/tasks.txt` starts absent
- Java version: 25 (`sdk use java 25.0.3.fx-zulu` on macOS)
- Compile command: `javac -d out $(find src/main/java -name "*.java")` (verified with Java 25.0.3 active)
- Launch command: `java -cp <absolute-repository-path>/out friday.Friday`
- Starting state: Fresh program launch with no `data/tasks.txt`, unless the case documents a prepared file or a restart.
- Comparison: Compare exact output after each input. Normalize only CRLF to LF and one terminal-added echo of the submitted input unless a test case explicitly states another rule.
- Failure behavior: Stop on the first mismatch, terminate the program, and do not run later commands or cases.

## Test cases

Add cases in execution order using this structure.

### UI-001: Todo and unknown commands preserve the task list

**Aim:** Verify that empty and unknown commands are rejected without adding tasks, while valid todos still work.

**Starting state:** Fresh program launch.

#### Step 1

**Input**

```text
todo buy milk
```

**Expected output**

```text
____________________________________________________________
okay okay, i add buy milk to the list lor.
you have 1 tasks lah.
____________________________________________________________
```

#### Step 2

**Input**

```text
todo
```

**Expected output**

```text
____________________________________________________________
SIALA!!! todo need description leh.
____________________________________________________________
```

#### Step 3

**Input**

```text
list
```

**Expected output**

```text
____________________________________________________________
Here are your tasks:
1.[T][ ] buy milk
____________________________________________________________
```

#### Step 4

**Input**

```text
blah
```

**Expected output**

```text
____________________________________________________________
SIALA!!! Eh? Sorry i don't understand that bro :-(
____________________________________________________________
```

#### Step 5

**Input**

```text
todo return book
```

**Expected output**

```text
____________________________________________________________
okay okay, i add return book to the list lor.
you have 2 tasks lah.
____________________________________________________________
```

#### Step 6

**Input**

```text
list
```

**Expected output**

```text
____________________________________________________________
Here are your tasks:
1.[T][ ] buy milk
2.[T][ ] return book
____________________________________________________________
```

### UI-002: Deadline validation preserves task count and order

**Aim:** Verify that missing deadline fields do not add partial tasks and that a valid deadline is stored correctly.

**Starting state:** Fresh program launch.

#### Step 1

**Input**

```text
todo read book
```

**Expected output**

```text
____________________________________________________________
okay okay, i add read book to the list lor.
you have 1 tasks lah.
____________________________________________________________
```

#### Step 2

**Input**

```text
deadline submit report
```

**Expected output**

```text
____________________________________________________________
SIALA!!! Tolong, write this format: deadline DESCRIPTION /by DATE
____________________________________________________________
```

#### Step 3

**Input**

```text
deadline submit report /by 2026-08-30
```

**Expected output**

```text
____________________________________________________________
Remember to finish hor: 
[D][ ] submit report (by: Aug 30 2026)
you have 2 tasks lah.
____________________________________________________________
```

#### Step 4

**Input**

```text
deadline /by Friday
```

**Expected output**

```text
____________________________________________________________
SIALA!!! Deadline's description and date must fill up lei.
____________________________________________________________
```

#### Step 5

**Input**

```text
deadline submit homework /by Friday
```

**Expected Output**

```text
____________________________________________________________
SIALA!!! Please enter dates as yyyy-mm-dd, e.g. 2019-10-15.
____________________________________________________________
```

#### Step 6

**Input**

```text
list
```

**Expected output**

```text
____________________________________________________________
Here are your tasks:
1.[T][ ] read book
2.[D][ ] submit report (by: Aug 30 2026)
____________________________________________________________
```

### UI-003: Event validation preserves task count and order

**Aim:** Verify that malformed or incomplete events are rejected and a valid event is stored correctly.

**Starting state:** Fresh program launch.

#### Step 1

**Input**

```text
event project meeting /from 2pm /to 3pm
```

**Expected output**

```text
____________________________________________________________
orh, don't forget to attend ah: 
[E][ ] project meeting (from: 2pm to: 3pm)
you have 1 tasks lah.
____________________________________________________________
```

#### Step 2

**Input**

```text
event lunch /from 12pm
```

**Expected output**

```text
____________________________________________________________
SIALA!!! ARE YOU DONE?! write like this lah: event DESCRIPTION /from START /to END
____________________________________________________________
```

#### Step 3

**Input**

```text
list
```

**Expected output**

```text
____________________________________________________________
Here are your tasks:
1.[E][ ] project meeting (from: 2pm to: 3pm)
____________________________________________________________
```

#### Step 4

**Input**

```text
event /from 2pm /to 3pm
```

**Expected output**

```text
____________________________________________________________
SIALA!!! Tolong, an event's description, start, and end cannot be empty lei.
____________________________________________________________
```

#### Step 5

**Input**

```text
event dinner /from 7pm /to 8pm
```

**Expected output**

```text
____________________________________________________________
orh, don't forget to attend ah: 
[E][ ] dinner (from: 7pm to: 8pm)
you have 2 tasks lah.
____________________________________________________________
```

#### Step 6

**Input**

```text
list
```

**Expected output**

```text
____________________________________________________________
Here are your tasks:
1.[E][ ] project meeting (from: 2pm to: 3pm)
2.[E][ ] dinner (from: 7pm to: 8pm)
____________________________________________________________
```

### UI-004: Mark and unmark validation preserves completion state

**Aim:** Verify that missing, nonnumeric, and out-of-range task numbers do not change completion state.

**Starting state:** Fresh program launch.

#### Step 1

**Input**

```text
todo submit assignment
```

**Expected output**

```text
____________________________________________________________
okay okay, i add submit assignment to the list lor.
you have 1 tasks lah.
____________________________________________________________
```

#### Step 2

**Input**

```text
mark
```

**Expected output**

```text
____________________________________________________________
SIALA!!! Please specify a task number.
____________________________________________________________
```

#### Step 3

**Input**

```text
mark one
```

**Expected output**

```text
____________________________________________________________
SIALA!!! The task number must be a whole number.
____________________________________________________________
```

#### Step 4

**Input**

```text
list
```

**Expected output**

```text
____________________________________________________________
Here are your tasks:
1.[T][ ] submit assignment
____________________________________________________________
```

#### Step 5

**Input**

```text
mark 1
```

**Expected output**

```text
____________________________________________________________
Good! This task done liao: 
[T][X] submit assignment
____________________________________________________________
```

#### Step 6

**Input**

```text
unmark 2
```

**Expected output**

```text
____________________________________________________________
SIALA!!! That task number does not exist.
____________________________________________________________
```

#### Step 7

**Input**

```text
list
```

**Expected output**

```text
____________________________________________________________
Here are your tasks:
1.[T][X] submit assignment
____________________________________________________________
```

#### Step 8

**Input**

```text
unmark 1
```

**Expected output**

```text
____________________________________________________________
Never mind! Can do later: 
[T][ ] submit assignment
____________________________________________________________
```

#### Step 9

**Input**

```text
list
```

**Expected output**

```text
____________________________________________________________
Here are your tasks:
1.[T][ ] submit assignment
____________________________________________________________
```

### UI-005: Blank input and an empty list are handled safely

**Aim:** Verify that blank input receives a useful error and listing an empty task collection does not fail.

**Starting state:** Fresh program launch with no `data/tasks.txt`.

#### Step 1

**Input**

```text

```

**Expected output**

```text
____________________________________________________________
SIALA!!! Please enter a command.
____________________________________________________________
```

#### Step 2

**Input**

```text
list
```

**Expected output**

```text
____________________________________________________________
Here are your tasks:
No tasks yet.
____________________________________________________________
```

### UI-006: Delete rejects invalid positions and renumbers remaining tasks

**Aim:** Verify deletion boundaries and confirm that removing one task leaves a correctly numbered list.

**Starting state:** Fresh program launch with no `data/tasks.txt`.

#### Step 1

**Input**

```text
todo first task
```

**Expected output**

```text
____________________________________________________________
okay okay, i add first task to the list lor.
you have 1 tasks lah.
____________________________________________________________
```

#### Step 2

**Input**

```text
todo second task
```

**Expected output**

```text
____________________________________________________________
okay okay, i add second task to the list lor.
you have 2 tasks lah.
____________________________________________________________
```

#### Step 3

**Input**

```text
delete 0
```

**Expected output**

```text
____________________________________________________________
SIALA!!! That task number does not exist.
____________________________________________________________
```

#### Step 4

**Input**

```text
delete 1
```

**Expected output**

```text
____________________________________________________________
Okay, I removed this task:
[T][ ] first task
you have 1 tasks lah.
____________________________________________________________
```

#### Step 5

**Input**

```text
list
```

**Expected output**

```text
____________________________________________________________
Here are your tasks:
1.[T][ ] second task
____________________________________________________________
```

### UI-007: Leading whitespace is ignored

**Aim:** Verify that accidental spaces before a command do not alter the stored task description.

**Starting state:** Fresh program launch with no `data/tasks.txt`.

#### Step 1

**Input**

```text
   todo revise notes
```

**Expected output**

```text
____________________________________________________________
okay okay, i add revise notes to the list lor.
you have 1 tasks lah.
____________________________________________________________
```

#### Step 2

**Input**

```text
   list
```

**Expected output**

```text
____________________________________________________________
Here are your tasks:
1.[T][ ] revise notes
____________________________________________________________
```

### UI-008: Commas survive saving and restarting

**Aim:** Verify that a comma in a description is escaped in `data/tasks.txt` and restored on the next launch.

**Starting state:** Fresh program launch with no `data/tasks.txt`. Restart in the same temporary directory after Step 2.

#### Step 1

**Input**

```text
todo buy milk, eggs
```

**Expected output**

```text
____________________________________________________________
okay okay, i add buy milk, eggs to the list lor.
you have 1 tasks lah.
____________________________________________________________
```

#### Step 2

**Input**

```text
bye
```

**Expected output**

```text
____________________________________________________________
Bye. See you next time lah!
____________________________________________________________
```

#### Step 3

**Action:** Restart Friday in the same working directory.

**Input**

```text
list
```

**Expected output**

```text
____________________________________________________________
Here are your tasks:
1.[T][ ] buy milk, eggs
____________________________________________________________
```

### UI-009: Malformed save records do not hide valid tasks

**Aim:** Verify that blank, unknown, incomplete, and invalid-status records are skipped while valid records load.

**Starting state:** Create `data/tasks.txt` in a fresh temporary directory with exactly:

```text
[T],0,valid task

[X],0,unknown task
[T],maybe,bad status
[E],0,incomplete event,2pm
[D],1,submit report,2026-08-30
[D],0,invalid date,Friday
[T],0,incomplete escape\
```

#### Step 1

**Input**

```text
list
```

**Expected output**

```text
____________________________________________________________
Here are your tasks:
1.[T][ ] valid task
2.[D][X] submit report (by: Aug 30 2026)
____________________________________________________________
```

### UI-010: Deadlines can be found by date

**Aim:** Verify that the `on` command finds deadlines on an ISO date, preserves their original task numbers, and handles dates with no deadlines.

**Starting state:** Fresh program launch with no `data/tasks.txt`.

#### Step 1

**Input**

```text
deadline submit report /by 2026-08-30
```

**Expected output**

```text
____________________________________________________________
Remember to finish hor: 
[D][ ] submit report (by: Aug 30 2026)
you have 1 tasks lah.
____________________________________________________________
```

#### Step 2

**Input**

```text
todo buy stationery
```

**Expected output**

```text
____________________________________________________________
okay okay, i add buy stationery to the list lor.
you have 2 tasks lah.
____________________________________________________________
```

#### Step 3

**Input**

```text
deadline rehearse presentation /by 2026-08-30
```

**Expected output**

```text
____________________________________________________________
Remember to finish hor: 
[D][ ] rehearse presentation (by: Aug 30 2026)
you have 3 tasks lah.
____________________________________________________________
```

#### Step 4

**Input**

```text
on 2026-08-30
```

**Expected output**

```text
____________________________________________________________
Your deadlines on 2026-08-30 ah:
1.[D][ ] submit report (by: Aug 30 2026)
3.[D][ ] rehearse presentation (by: Aug 30 2026)
____________________________________________________________
```

#### Step 5

**Input**

```text
on 2026-08-31
```

**Expected output**

```text
____________________________________________________________
Your deadlines on 2026-08-31 ah:
Got nothing due. Heng ah!
____________________________________________________________
```

#### Step 6

**Input**

```text
on Friday
```

**Expected output**

```text
____________________________________________________________
SIALA!!! Please enter dates as yyyy-mm-dd, e.g. 2019-10-15.
____________________________________________________________
```
