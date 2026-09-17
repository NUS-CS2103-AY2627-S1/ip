# UI Test Plan

Text-UI tests for the Kiwi chatbot. The `/test-ui` skill reads this file,
runs each test case against the program, shows the console session, and
**stops at the first failure**.

## Program launch

- **Main class:** `kiwi.Kiwi`
- **Source directory:** `src/main/java`
- **Java version:** `25`
- **Packages:** `kiwi` (app wiring, UI, storage, parser), `kiwi.task` (task model + list), `kiwi.command` (Command hierarchy)
- **Task storage:** `ArrayList<Task>` (dynamic size; add/delete via list operations)
- **Task kinds:** `TaskType` enum (`TODO`, `DEADLINE`, `EVENT`) for type icons `[T]` / `[D]` / `[E]`
- **Hard-disk file:** `./data/kiwi.txt` — loaded at startup; rewritten (via temp file + replace) whenever the list changes. Missing save → empty list. Unreadable path, a folder in place of the file, or I/O errors → empty list with a message. Corrupted, duplicated, or impossible-date lines are skipped with a warning; valid lines still load. Save failures surface as an error and the in-memory change is rolled back.
- **Deadline/event dates:** accepted/stored as unsigned `yyyy-MM-dd` (year 0000-9999), shown as `MMM dd uuuu` (year 0000 displays as `0000`, not `0001`). Signed or extra-digit years (e.g. `-0005-03-01`, `+10000-01-01`) and impossible calendar dates (e.g. 2019-02-30) are rejected. Event end date cannot be before the start date (same-day events are allowed). Command `on yyyy-MM-dd` lists deadlines due that day and events whose range covers it.
- **Duplicates:** adding a task with the same type, description, and dates as one already in the list is rejected.
- **Command format:** leading, trailing, and repeated spaces are treated as a single space. Extra words on `list`/`bye`/`mark`/`unmark`/`delete`/`on`, missing or repeated `/by` `/from` `/to`, and descriptions containing ` | ` are rejected with a usage hint.
- **Find:** `find KEYWORD` lists tasks whose description contains the keyword (case-sensitive substring). Matching tasks are numbered from 1 in the result; no matches prints `None found.`
- **Free time:** `free HOURS` (optionally ` /from yyyy-MM-dd`) finds the nearest work day with that many consecutive free hours. Work day is `08:00-18:00` (10 hours). Only events occupy the calendar (the whole work day for each day in their range); deadlines and to-dos do not. Omitting `/from` starts the search from today. No slot within 365 days prints `Couldn't find a …`.
- **How tests are run:** compile all `*.java` under the source directory (recursively, for packages), then for each test case reset `./data/kiwi.txt` (delete unless a **Seed file** is given), pipe **Inputs** to stdin with Java assertions enabled (`-ea`), and compare full stdout to **Expected output**. Save-file lines use `|` separators, e.g. `T | 1 | read book`, `D | 0 | return book | 2019-12-02`, `E | 0 | meeting | 2019-10-04 | 2019-10-11`.

Suggested command (used by the skill runner):

```bash
python3 .cursor/skills/test-ui/scripts/run-ui-tests.py
```

## Test case format

Each case under `## Test cases` must include:

1. **Aim** — purpose of the case
2. **Inputs** — commands sent to the program (one per line)
3. **Expected output** — exact console output for that session

Optional:

4. **Seed file** — contents written to `./data/kiwi.txt` before the case starts (for load/persistence tests). If omitted, the save file is deleted so the case starts with an empty disk.

## Test cases

### TC01: Add todo, deadline, and event then list

**Aim:** Check that `todo`, `deadline` (yyyy-MM-dd → MMM dd yyyy), and `event` create typed tasks, and that `list` shows them.

**Inputs:**
```text
todo borrow book
deadline return book /by 2019-12-02
event project meeting /from 2019-12-01 /to 2019-12-03
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Dec 01 2019 to: Dec 03 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 02 2019)
3.[E][ ] project meeting (from: Dec 01 2019 to: Dec 03 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC02: Mark and unmark a todo

**Aim:** Check that `mark` / `unmark` update the status icon on a typed task.

**Inputs:**
```text
todo read book
mark 1
list
unmark 1
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Marked this task as done:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Marked this task as not done yet:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC03: Reject non yyyy-MM-dd deadline dates

**Aim:** Free-form `/by` text is no longer accepted; Kiwi asks for `yyyy-MM-dd` and does not add the task.

**Inputs:**
```text
deadline do homework /by no idea :-p
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Please use a date as yyyy-MM-dd, e.g. 2019-12-02
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC04: Empty todo and unknown command leave list empty

**Aim:** Negative inputs must not create tasks; a later valid `todo` should be the only item.

**Inputs:**
```text
todo
blah
list
todo after errors
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
A todo needs a description — try: todo borrow book
____________________________________________________________
____________________________________________________________
Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, on, find, free, list, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] after errors
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] after errors
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC05: Bad deadline/event/mark then successful mark

**Aim:** Incomplete commands and a bad `mark` must not corrupt a later valid todo; `mark 1` should still work.

**Inputs:**
```text
deadline
event project /from Mon
mark 1
todo read book
mark abc
list
mark 1
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
A deadline needs details — try: deadline return book /by 2019-12-02
____________________________________________________________
____________________________________________________________
Events need /from and /to as yyyy-MM-dd — e.g. event meeting /from 2019-10-04 /to 2019-10-11
____________________________________________________________
____________________________________________________________
There is no task number 1 in your list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
That task number doesn't look like a number: abc
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Marked this task as done:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC06: Interleave empty todo and unknown command with valid adds

**Aim:** After a failed `todo` and an unknown word, valid adds still accumulate with the correct counts.

**Inputs:**
```text
todo
todo buy milk
list
blah
deadline submit report /by 2019-10-18
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
A todo needs a description — try: todo borrow book
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, on, find, free, list, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: Oct 18 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] buy milk
2.[D][ ] submit report (by: Oct 18 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC07: Malformed deadline/event then valid pair

**Aim:** Missing `/by` pieces and an empty `event` must not add tasks; only the following valid deadline and event appear.

**Inputs:**
```text
deadline return book /by
deadline /by 2019-12-02
deadline return book /by 2019-12-02
event
event meeting /from 2019-10-04 /to 2019-10-04
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Deadlines need both a description and /by yyyy-MM-dd — e.g. deadline return book /by 2019-12-02
____________________________________________________________
____________________________________________________________
Deadlines need both a description and /by yyyy-MM-dd — e.g. deadline return book /by 2019-12-02
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
An event needs details — try: event meeting /from 2019-10-04 /to 2019-10-11
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: Oct 04 2019 to: Oct 04 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Dec 02 2019)
2.[E][ ] meeting (from: Oct 04 2019 to: Oct 04 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC08: Bad mark/unmark must not change done status incorrectly

**Aim:** Out-of-range, non-numeric, and missing mark/unmark args leave existing status alone; valid marks still apply to the right tasks.

**Inputs:**
```text
todo task a
mark 2
mark 1
unmark 9
list
todo task b
mark abc
unmark
mark 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task a
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
There is no task number 2 in your list.
____________________________________________________________
____________________________________________________________
Marked this task as done:
1.[T][X] task a
____________________________________________________________
____________________________________________________________
There is no task number 9 in your list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] task a
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task b
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
That task number doesn't look like a number: abc
____________________________________________________________
____________________________________________________________
Please give a task number, e.g. unmark 1
____________________________________________________________
____________________________________________________________
Marked this task as done:
2.[T][X] task b
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] task a
2.[T][X] task b
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC09: Burst of negatives then one valid add

**Aim:** Several failed commands in a row leave the list empty; the first successful add is task 1.

**Inputs:**
```text
todo
deadline
event
mark
unmark 1
list
todo only real task
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
A todo needs a description — try: todo borrow book
____________________________________________________________
____________________________________________________________
A deadline needs details — try: deadline return book /by 2019-12-02
____________________________________________________________
____________________________________________________________
An event needs details — try: event meeting /from 2019-10-04 /to 2019-10-11
____________________________________________________________
____________________________________________________________
Please give a task number, e.g. mark 1
____________________________________________________________
____________________________________________________________
There is no task number 1 in your list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] only real task
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] only real task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC10: Delete a middle task and renumber the list

**Aim:** `delete 3` removes that task, shifts later items up, and reports the new count.

**Inputs:**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-06
todo join sports club
todo borrow book
list
delete 3
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
4.[T][ ] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[T][ ] join sports club
4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC11: Invalid delete leaves list unchanged; valid delete works

**Aim:** Bad `delete` inputs must not remove tasks; a later valid `delete 1` removes the first item only.

**Inputs:**
```text
todo keep me
delete 2
delete
delete abc
list
todo another
delete 1
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] keep me
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
There is no task number 2 in your list.
____________________________________________________________
____________________________________________________________
Please give a task number, e.g. delete 1
____________________________________________________________
____________________________________________________________
That task number doesn't look like a number: abc
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep me
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] another
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][ ] keep me
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] another
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC12: Saving is silent while the list still updates on screen

**Aim:** Console behaviour is unchanged when auto-save runs after add/mark (no extra save messages on the happy path). After this session, `./data/kiwi.txt` should contain the pipe-separated lines for the remaining tasks.

**Inputs:**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-06
mark 1
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Marked this task as done:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC13: Load saved tasks from disk at startup

**Aim:** On startup, tasks previously written to `./data/kiwi.txt` appear in `list` with the correct types and done status.

**Seed file:**
```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 0 | project meeting | 2019-08-06 | 2019-08-06
```

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC14: Loaded tasks keep numbering when adding more

**Aim:** After loading two tasks from disk, a new todo becomes task 3 and the count is 3.

**Seed file:**
```text
T | 0 | existing one
D | 1 | existing two | 2019-10-18
```

**Inputs:**
```text
todo brand new
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] brand new
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] existing one
2.[D][X] existing two (by: Oct 18 2019)
3.[T][ ] brand new
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC15: Skip corrupted save lines and keep valid ones

**Aim:** Malformed lines (wrong field count, bad done flag, unknown type) produce skip warnings and do not prevent valid tasks from loading.

**Seed file:**
```text
T | 1 | keep me

not a valid line
D | 0 | incomplete
D | 2 | bad done | 2019-12-02
T | 0 |
D | 0 | ok deadline | 2019-10-18
E | 0 | meet | 2019-08-06
X | 0 | mystery
E | 0 | party | 2019-12-01 | 2019-12-02
```

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
Skipping corrupted save line 3 (expected at least 3 fields separated by " | ")
Skipping corrupted save line 4 (deadline lines must look like: D | 0 | description | yyyy-MM-dd)
Skipping corrupted save line 5 (done flag must be 0 or 1, found "2")
Skipping corrupted save line 6 (expected at least 3 fields separated by " | ")
Skipping corrupted save line 8 (event lines must look like: E | 0 | description | yyyy-MM-dd | yyyy-MM-dd)
Skipping corrupted save line 9 (unknown task type "X")
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] keep me
2.[D][ ] ok deadline (by: Oct 18 2019)
3.[E][ ] party (from: Dec 01 2019 to: Dec 02 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC16: Empty save file starts with an empty list

**Aim:** An existing but empty `kiwi.txt` loads as zero tasks (same as a missing file for the user).

**Seed file:**
```text
```

**Inputs:**
```text
list
todo after empty file
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] after empty file
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] after empty file
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC17: List deadlines and events on a specific date

**Aim:** `on yyyy-MM-dd` shows deadlines due that day and events whose inclusive range covers it; to-dos are omitted; unrelated dates show "None found."

**Inputs:**
```text
todo ignore me
deadline return book /by 2019-12-02
event project meeting /from 2019-12-01 /to 2019-12-03
deadline other /by 2019-10-15
on 2019-12-02
on 2019-10-15
on 2020-01-01
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] ignore me
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Dec 01 2019 to: Dec 03 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] other (by: Oct 15 2019)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the deadlines/events on Dec 02 2019:
2.[D][ ] return book (by: Dec 02 2019)
3.[E][ ] project meeting (from: Dec 01 2019 to: Dec 03 2019)
____________________________________________________________
____________________________________________________________
Here are the deadlines/events on Oct 15 2019:
4.[D][ ] other (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Here are the deadlines/events on Jan 01 2020:
None found.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC18: Find tasks by keyword in the description

**Aim:** `find book` lists only tasks whose description contains `book`, numbered from 1 in the result (not the original list index).

**Inputs:**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-06
todo join sports club
todo borrow book
mark 1
mark 2
find book
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Marked this task as done:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Marked this task as done:
2.[D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2019)
3.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC19: Find with no matches and missing keyword

**Aim:** `find` without a keyword is rejected; a keyword that matches nothing prints `None found.`

**Inputs:**
```text
todo read book
find
find xyz
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Please give a keyword to search for, e.g. find book
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
None found.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC20: Find the nearest 4-hour free slot

**Aim:** `free 4 /from yyyy-MM-dd` skips days covered by events, ignores deadlines and to-dos, and reports the first work-day slot `08:00-12:00`.

**Inputs:**
```text
todo ignore me
deadline return book /by 2019-12-01
event conference /from 2019-12-01 /to 2019-12-03
free 4 /from 2019-12-01
free 4 /from 2019-11-30
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] ignore me
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] conference (from: Dec 01 2019 to: Dec 03 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
The nearest 4-hour free slot is on Dec 04 2019, 08:00-12:00.
____________________________________________________________
____________________________________________________________
The nearest 4-hour free slot is on Nov 30 2019, 08:00-12:00.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC21: Reject invalid free commands

**Aim:** Missing hours, non-numeric or non-positive hours, a slot longer than the work day, and a missing `/from` date are rejected without changing the list.

**Inputs:**
```text
free
free abc
free 0
free 11
free 4 /from
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Please give a number of hours, e.g. free 4
____________________________________________________________
____________________________________________________________
That duration doesn't look like a number: abc
____________________________________________________________
____________________________________________________________
Please give a positive number of hours, e.g. free 4
____________________________________________________________
____________________________________________________________
A free slot must fit in one work day (08:00-18:00, 10 hours).
____________________________________________________________
____________________________________________________________
Please give a start date, e.g. free 4 /from 2019-12-01
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC22: No free slot within 365 days

**Aim:** When events occupy every day in the 365-day search window, `free` says no slot was found.

**Inputs:**
```text
event long trip /from 2019-01-01 /to 2021-12-31
free 4 /from 2019-12-01
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] long trip (from: Jan 01 2019 to: Dec 31 2021)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Couldn't find a 4-hour free slot in the next 365 days.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC23: Leading, trailing, and extra spaces are accepted

**Aim:** Extra whitespace around a command and around `/by` is treated as a single space so the tasks are still added.

**Inputs:**
```text
  todo   borrow book
deadline return book  /by  2019-12-02
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC24: Reject extra words, duplicate flags, empty input, and unsafe descriptions

**Aim:** Empty input, extra words on `list`/`bye`/`mark`, a repeated `/by`, a non-positive task number, and a description containing ` | ` are rejected without changing the list.

**Inputs:**
```text

list extra
bye now
deadline return book /by 2019-12-02 /by 2019-12-03
mark 0
todo a | b
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Please type a command, e.g. todo borrow book
____________________________________________________________
____________________________________________________________
list doesn't take extra words. Just type: list
____________________________________________________________
____________________________________________________________
bye doesn't take extra words. Just type: bye
____________________________________________________________
____________________________________________________________
/by appears more than once. Deadlines need both a description and /by yyyy-MM-dd — e.g. deadline return book /by 2019-12-02
____________________________________________________________
____________________________________________________________
Task numbers start from 1.
____________________________________________________________
____________________________________________________________
Task descriptions cannot contain " | ".
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC25: Reject duplicate tasks, impossible dates, and inverted event ranges

**Aim:** A second identical todo is refused; Feb 30 and an event whose end is before its start do not add tasks.

**Inputs:**
```text
todo read book
todo read book
deadline return book /by 2019-02-30
event meeting /from 2019-10-11 /to 2019-10-04
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
That task is already in your list.
____________________________________________________________
____________________________________________________________
2019-02-30 is not a real calendar date. Please use a date as yyyy-MM-dd, e.g. 2019-12-02
____________________________________________________________
____________________________________________________________
Event end date cannot be before the start date.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC26: Reject signed years and display year 0000 correctly

**Aim:** Dates must be unsigned `yyyy-MM-dd`. Negative years and years outside 0000-9999 are rejected with the usual format error; year `0000` is accepted and shown as `0000`.

**Inputs:**
```text
deadline dl1 /by -0005-03-01
deadline dl2 /by +10000-01-01
deadline dl3 /by 0000-01-01
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Please use a date as yyyy-MM-dd, e.g. 2019-12-02
____________________________________________________________
____________________________________________________________
Please use a date as yyyy-MM-dd, e.g. 2019-12-02
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] dl3 (by: Jan 01 0000)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] dl3 (by: Jan 01 0000)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
