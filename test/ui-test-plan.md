# UI Test Plan

## Manual GUI checks

- Resize the Aether window to its minimum size and then make it wider. Confirm that long replies wrap within the
  available chat width and remain readable.
- Enter `todo` without a description. Confirm that Aether's corrective reply uses the red error bubble and `!` badge,
  while a subsequent valid `todo read book` reply uses the normal Aether bubble.
- Confirm that the header says "A calm guide for busy days", normal Aether replies use the star badge, and the calm,
  focus-oriented wording is consistent across adding, completing, reopening, deleting, sorting, and exiting.

Console UI tests for Aether. After each code update that changes chatbot behaviour, update this file if needed and run `python test/run_ui_tests.py` from the repository root.

Dates are parsed as `LocalDate` values in the `yyyy-MM-dd` format and displayed as `MMM dd yyyy`.

The test runner deletes `data/aether.txt` before each independent UI case, so saved
tasks cannot affect another case. Persistence is checked separately by running one
session that adds or changes tasks, then a second session whose `list` command must
show the same tasks and done statuses.

## Test case: add a todo

**Aim:** A `todo` command stores a task with type `T`, no date/time, and not-done status.

**Inputs:**
```
todo borrow book
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```

## Test case: reject ambiguous dates and duplicate tasks

**Aim:** Date markers must be standalone and unique, event end dates must be after start dates, and an equivalent task
is rejected without changing the task list.

**Inputs:**
```
todo read book
todo READ BOOK
deadline read /bypass notes
deadline submit /by 2026-09-15 /by 2026-09-16
event camp /from 2026-09-16 /to 2026-09-16
event camp /from 2026-09-17 /to 2026-09-16
event camp /from 2026-09-15 /from 2026-09-16 /to 2026-09-17
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
That task is already in your list. Use a different description or date.
____________________________________________________________
____________________________________________________________
A deadline needs a /by date. Try: deadline return book /by 2019-10-15
____________________________________________________________
____________________________________________________________
Use /by only once in a command.
____________________________________________________________
____________________________________________________________
The /to date must be after the /from date. Try: event project meeting /from 2019-10-15 /to 2019-10-16
____________________________________________________________
____________________________________________________________
The /to date must be after the /from date. Try: event project meeting /from 2019-10-15 /to 2019-10-16
____________________________________________________________
____________________________________________________________
Use /from only once in a command.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```

## Test case: add a deadline

**Aim:** A `deadline` command stores a task with type `D`, parses its `/by` date, and displays it readably.

**Inputs:**
```
deadline return book /by 2019-10-15
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [D][ ] return book (by: Oct 15 2019)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```

## Test case: add an event

**Aim:** An `event` command stores a task with type `E`, parses its `/from` and `/to` dates, and displays them readably.

**Inputs:**
```
event project meeting /from 2019-10-15 /to 2019-10-16
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```

## Test case: mixed list with mark

**Aim:** Todos, deadlines, and events share one list; `list` shows type icons and dates; `mark` keeps those details.

**Inputs:**
```
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
todo join sports club
todo borrow book
mark 1
mark 4
list
deadline return book /by 2019-10-15
event project meeting /from 2019-10-15 /to 2019-10-16
deadline do homework /by 2020-01-20
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Excellent - this task is complete:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Excellent - this task is complete:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [D][ ] return book (by: Oct 15 2019)
Now you have 6 tasks in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Now you have 7 tasks in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [D][ ] do homework (by: Jan 20 2020)
Now you have 8 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
4.[T][X] join sports club
5.[T][ ] borrow book
6.[D][ ] return book (by: Oct 15 2019)
7.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
8.[D][ ] do homework (by: Jan 20 2020)
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```

## Test case: mark and unmark keep type details

**Aim:** `mark` and `unmark` change only the done checkbox; type icon and date text stay the same.

**Inputs:**
```
deadline return book /by 2019-10-15
mark 1
unmark 1
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [D][ ] return book (by: Oct 15 2019)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Excellent - this task is complete:
  [D][X] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
No problem - this task is back in motion:
  [D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```

## Test case: empty todo and unknown command

**Aim:** A `todo` with no description, and a command that is not recognised, each print an error and do not change the list.

**Inputs:**
```
todo
blah
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
The description of a todo cannot be empty. Try: todo borrow book
____________________________________________________________
____________________________________________________________
I don't recognise that command. Try: list, find, sort, todo, deadline, event, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```

## Test case: interleaved valid and invalid commands

**Aim:** Rejected commands leave the list unchanged, so later `list` and `mark` still match the tasks that were actually added.

**Inputs:**
```
todo read book
todo
deadline return book
event project meeting /from 2019-10-15
mark abc
list
deadline return book /by 2019-10-15
mark 2
mark 1
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
The description of a todo cannot be empty. Try: todo borrow book
____________________________________________________________
____________________________________________________________
A deadline needs a /by date. Try: deadline return book /by 2019-10-15
____________________________________________________________
____________________________________________________________
An event needs /from and /to dates. Try: event project meeting /from 2019-10-15 /to 2019-10-16
____________________________________________________________
____________________________________________________________
The task number must be a whole number. Try: mark 1
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Excellent - this task is complete:
  [D][X] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Excellent - this task is complete:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```

## Test case: deadline and event field errors

**Aim:** Empty descriptions and empty `/by`, `/from`, or `/to` values are rejected without adding a task.

**Inputs:**
```
deadline /by 2019-10-15
deadline return book /by
event /from 2019-10-15 /to 2019-10-16
event project meeting /from /to 2019-10-16
event project meeting /from 2019-10-15 /to
mark
unmark 1
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
The description of a deadline cannot be empty. Try: deadline return book /by 2019-10-15
____________________________________________________________
____________________________________________________________
The /by date of a deadline cannot be empty. Try: deadline return book /by 2019-10-15
____________________________________________________________
____________________________________________________________
The description of an event cannot be empty. Try: event project meeting /from 2019-10-15 /to 2019-10-16
____________________________________________________________
____________________________________________________________
The /from date of an event cannot be empty. Try: event project meeting /from 2019-10-15 /to 2019-10-16
____________________________________________________________
____________________________________________________________
The /to date of an event cannot be empty. Try: event project meeting /from 2019-10-15 /to 2019-10-16
____________________________________________________________
____________________________________________________________
Please give a task number after mark. Try: mark 1
____________________________________________________________
____________________________________________________________
That task number does not exist. Use list to see the current numbers.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```

## Test case: invalid dates do not change the list

**Aim:** Dates must be real ISO-8601 dates, and an invalid `/by`, `/from`, or `/to` value must not add a task.

**Inputs:**
```
deadline return book /by 2019-02-30
event project meeting /from 2019-13-01 /to 2019-10-16
event project meeting /from 2019-10-15 /to not-a-date
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
The /by date must be a valid date in yyyy-MM-dd format. Try: deadline return book /by 2019-10-15
____________________________________________________________
____________________________________________________________
The /from date must be a valid date in yyyy-MM-dd format. Try: event project meeting /from 2019-10-15 /to 2019-10-16
____________________________________________________________
____________________________________________________________
The /to date must be a valid date in yyyy-MM-dd format. Try: event project meeting /from 2019-10-15 /to 2019-10-16
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```

## Test case: delete a task

**Aim:** A `delete` command removes the given task, shifts later tasks down, and reports the new count.

**Inputs:**
```
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
todo join sports club
todo borrow book
mark 1
mark 4
list
delete 3
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Excellent - this task is complete:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Excellent - this task is complete:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Consider it cleared from your orbit:
  [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```

## Test case: invalid delete does not change the list

**Aim:** Empty, non-numeric, and out-of-range `delete` commands print an error and leave the list unchanged, including after a valid delete.

**Inputs:**
```
todo read book
todo borrow book
delete
delete abc
delete 0
delete 3
list
delete 1
delete 1
delete 1
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] borrow book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Please give a task number after delete. Try: delete 1
____________________________________________________________
____________________________________________________________
The task number must be a whole number. Try: delete 1
____________________________________________________________
____________________________________________________________
That task number does not exist. Use list to see the current numbers.
____________________________________________________________
____________________________________________________________
That task number does not exist. Use list to see the current numbers.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Consider it cleared from your orbit:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Consider it cleared from your orbit:
  [T][ ] borrow book
Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
That task number does not exist. Use list to see the current numbers.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```

## Test case: find tasks using full-list numbers

**Aim:** `find` searches descriptions without regard to letter case, preserves the full list's task numbers, and an empty
keyword is rejected without changing the list. A displayed search-result number deletes the same task from the full list.

**Inputs:**
```
todo buy milk
todo read book
deadline return book /by 2019-06-06
todo write report
mark 3
find BOOK
delete 2
find
find report
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] buy milk
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] read book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [D][ ] return book (by: Jun 06 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] write report
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Excellent - this task is complete:
  [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
2.[T][ ] read book
3.[D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Consider it cleared from your orbit:
  [T][ ] read book
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
The search keyword cannot be empty. Try: find book
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
3.[T][ ] write report
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] buy milk
2.[D][X] return book (by: Jun 06 2019)
3.[T][ ] write report
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```

## Test case: end of input exits cleanly

**Aim:** Ending console input without a `bye` command exits normally and still shows the farewell message.

**Inputs:**
```
list
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```
## Test case: sort dated tasks and reject sort arguments

**Aim:** `sort` orders events by start date and deadlines by due date, keeps equal-date tasks in their
original order, places todos last, saves the order, and rejects unexpected arguments without changing the list.

**Inputs:**
```
todo buy milk
deadline submit report /by 2026-09-12
event workshop /from 2026-09-05 /to 2026-09-06
deadline renew pass /by 2026-09-05
sort tomorrow
sort
list
bye
```

**Expected output:**
```
____________________________________________________________
    _         _   _
   / \   ___ | |_| |__   ___ _ __
  / _ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \  __/| |_| | | |  __/ |
/_/   \_\___|\__|_| |_|\___|_|
Hello! I'm Aether, your calm guide through a busy day.
What shall we bring into focus?
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [T][ ] buy milk
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [D][ ] submit report (by: Sep 12 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [E][ ] workshop (from: Sep 05 2026 to: Sep 06 2026)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Task captured and brought into focus:
  [D][ ] renew pass (by: Sep 05 2026)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
I don't recognise that command. Try: list, find, sort, todo, deadline, event, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
Your schedule is aligned by date.
Here are the tasks in your list:
1.[E][ ] workshop (from: Sep 05 2026 to: Sep 06 2026)
2.[D][ ] renew pass (by: Sep 05 2026)
3.[D][ ] submit report (by: Sep 12 2026)
4.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] workshop (from: Sep 05 2026 to: Sep 06 2026)
2.[D][ ] renew pass (by: Sep 05 2026)
3.[D][ ] submit report (by: Sep 12 2026)
4.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
Until next time. May your day stay clear and focused.
____________________________________________________________
```
