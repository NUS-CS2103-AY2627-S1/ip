# UI Test Plan

This file lists the UI test cases used by the `test-ui` skill. Each test case
is a full chatbot session: a sequence of commands fed to the program's
standard input (one per line, always ending with `bye`), and the exact
console output the program is expected to produce for that session.

The program's entry point is `src/main/java/pdd/PDD.java` (class
`pdd.PDD`), which wires together `pdd.ui.Ui` (console I/O),
`pdd.storage.Storage` (loading/saving `./data/pdd.txt`), `pdd.task.TaskList`
(the in-memory task list) and `pdd.parser.Parser` (turns a raw input line
into a `pdd.command.Command` object — `ListCommand`, `MarkCommand`,
`UnmarkCommand`, `DeleteCommand`, `TodoCommand`, `DeadlineCommand`,
`EventCommand`, `OnCommand`, `ExitCommand` — one per command type, all in
package `pdd.command`). `Storage` persists the task list to
`./data/pdd.txt` after every mutating command and reloads it on startup —
see the "Data file cleanup" note below for how this affects test
isolation.

## How a test case is structured

- **Aim**: what behavior this test case is checking.
- **Input**: the exact lines sent to the program's stdin, in order.
- **Expected output**: the exact stdout the program must produce.

## Data file cleanup

Since Level 7, the chatbot saves tasks to `./data/pdd.txt` (relative to the
project root) after every mutating command, and reloads them on startup.
Because every test case is run from the same working directory, the
`./data` directory must be deleted **before each test case** (not just once
before the whole suite) — otherwise a later test case would start with
tasks left over from an earlier one, breaking the "independent full
session" contract each test case relies on.

---

## TC1: Greet and exit

**Aim**: Verify the startup banner/greeting is shown, and that `bye` exits
with the goodbye message.

**Input**:
```
bye
```

**Expected output**:
```
____________________________________________________________
 ____  ____  ____  
|  _ \|  _ \|  _ \ 
| |_) | | | | | | |
|  __/| |_| | |_| |
|_|   |____/|____/ 

Hello! I'm PDD.
What can I do for you?
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC2: Add a todo, deadline, and event; list them

**Aim**: Verify `todo`, `deadline`, and `event` commands add tasks with the
correct type tag and are echoed correctly, and that `list` shows them with
1-based numbering.

**Input**:
```
todo borrow book
deadline return book /by 2019-12-01
event project meeting /from 2019-12-02 /to 4pm
list
bye
```

**Expected output**:
```
____________________________________________________________
 ____  ____  ____  
|  _ \|  _ \|  _ \ 
| |_) | | | | | | |
|  __/| |_| | |_| |
|_|   |____/|____/ 

Hello! I'm PDD.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Dec 02 2019 to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 01 2019)
3.[E][ ] project meeting (from: Dec 02 2019 to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC3: Mark and unmark a task

**Aim**: Verify `mark` sets a task's status icon to `[X]`, and `unmark`
reverts it back to `[ ]`, with the correct confirmation messages.

**Input**:
```
todo borrow book
mark 1
list
unmark 1
list
bye
```

**Expected output**:
```
____________________________________________________________
 ____  ____  ____  
|  _ \|  _ \|  _ \ 
| |_) | | | | | | |
|  __/| |_| | |_| |
|_|   |____/|____/ 

Hello! I'm PDD.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] borrow book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] borrow book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] borrow book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC4: Deadline with an invalid date string

**Aim**: Verify that `/by` text which isn't a valid `yyyy-MM-dd` date is
rejected with a clear error message, and no task is added.

**Input**:
```
deadline do homework /by no idea :-p
list
bye
```

**Expected output**:
```
____________________________________________________________
 ____  ____  ____  
|  _ \|  _ \|  _ \ 
| |_) | | | | | | |
|  __/| |_| | |_| |
|_|   |____/|____/ 

Hello! I'm PDD.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! Please enter the date in yyyy-MM-dd format, e.g. 2019-10-15.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC5: Unknown command

**Aim**: Verify that an unrecognized command produces an error message
instead of being silently added as a task, and that the program keeps
running afterwards.

**Input**:
```
blah
todo borrow book
list
bye
```

**Expected output**:
```
____________________________________________________________
 ____  ____  ____  
|  _ \|  _ \|  _ \ 
| |_) | | | | | | |
|  __/| |_| | |_| |
|_|   |____/|____/ 

Hello! I'm PDD.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC6: Empty todo/deadline/event descriptions

**Aim**: Verify that `todo`, `deadline`, and `event` commands with no
description produce a specific error message for each, and add no task.

**Input**:
```
todo
deadline
event
list
bye
```

**Expected output**:
```
____________________________________________________________
 ____  ____  ____  
|  _ \|  _ \|  _ \ 
| |_) | | | | | | |
|  __/| |_| | |_| |
|_|   |____/|____/ 

Hello! I'm PDD.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a deadline cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! The description of an event cannot be empty.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC7: Deadline/event missing required markers

**Aim**: Verify that `deadline` without `/by`, and `event` without `/from`
or `/to`, produce a specific, corrective error message, and add no task.

**Input**:
```
deadline return book
event meeting /from Mon 2pm
list
bye
```

**Expected output**:
```
____________________________________________________________
 ____  ____  ____  
|  _ \|  _ \|  _ \ 
| |_) | | | | | | |
|  __/| |_| | |_| |
|_|   |____/|____/ 

Hello! I'm PDD.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a description and a '/by' date, e.g. deadline return book /by 2019-10-15
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a description, a '/from' date and a '/to' time, e.g. event meeting /from 2019-10-15 /to 4pm
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC8: Invalid mark/unmark task numbers

**Aim**: Verify that `mark`/`unmark` with a missing, non-numeric, or
out-of-range task number produce a specific error message, and do not
crash the program.

**Input**:
```
todo borrow book
mark
mark abc
mark 5
unmark 0
list
bye
```

**Expected output**:
```
____________________________________________________________
 ____  ____  ____  
|  _ \|  _ \|  _ \ 
| |_) | | | | | | |
|  __/| |_| | |_| |
|_|   |____/|____/ 

Hello! I'm PDD.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a valid task number, e.g. mark 2
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a valid task number, e.g. mark 2
____________________________________________________________
____________________________________________________________
OOPS!!! Task number 5 does not exist. You have 1 task(s) in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! Task number 0 does not exist. You have 1 task(s) in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC9: Delete a task

**Aim**: Verify `delete` removes the specified task, confirms with the
task's own display line and updated count, and that remaining tasks are
renumbered in `list`. Also verify `delete` reuses the same task-number
validation as `mark`/`unmark` (missing, non-numeric, or out-of-range index).

**Input**:
```
todo read book
deadline return book /by 2019-12-01
event project meeting /from 2019-12-02 /to 4pm
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
delete
delete abc
delete 9
bye
```

**Expected output**:
```
____________________________________________________________
 ____  ____  ____  
|  _ \|  _ \|  _ \ 
| |_) | | | | | | |
|  __/| |_| | |_| |
|_|   |____/|____/ 

Hello! I'm PDD.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Dec 02 2019 to: 4pm)
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
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Dec 01 2019)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 01 2019)
3.[E][ ] project meeting (from: Dec 02 2019 to: 4pm)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Dec 02 2019 to: 4pm)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 01 2019)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a valid task number, e.g. mark 2
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a valid task number, e.g. mark 2
____________________________________________________________
____________________________________________________________
OOPS!!! Task number 9 does not exist. You have 4 task(s) in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC10: Session behavior is unchanged with the save-to-disk layer active

**Aim**: Verify that adding a todo/deadline/event, marking a task, and
deleting a task all still produce their normal confirmation output and
console session shape now that every one of those commands also writes to
`./data/pdd.txt` in the background. (Reloading a save file across a
restart isn't exercised by this single-process harness — see the "Data
file cleanup" note above; that behavior is verified manually instead.)

**Input**:
```
todo read book
deadline return book /by 2019-12-01
mark 1
delete 2
list
bye
```

**Expected output**:
```
____________________________________________________________
 ____  ____  ____  
|  _ \|  _ \|  _ \ 
| |_) | | | | | | |
|  __/| |_| | |_| |
|_|   |____/|____/ 

Hello! I'm PDD.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC11: List tasks occurring on a specific date

**Aim**: Verify the `on <date>` command lists every deadline/event whose
date matches, in list order (a todo, having no date, is never matched);
prints just the header with no items when nothing matches; and produces
the expected errors for a missing or invalid date argument.

**Input**:
```
todo read book
deadline return book /by 2019-12-01
event project meeting /from 2019-12-01 /to 4pm
deadline pay bills /by 2019-12-02
on 2019-12-01
on 2019-12-03
on
on nonsense
bye
```

**Expected output**:
```
____________________________________________________________
 ____  ____  ____  
|  _ \|  _ \|  _ \ 
| |_) | | | | | | |
|  __/| |_| | |_| |
|_|   |____/|____/ 

Hello! I'm PDD.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Dec 01 2019 to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] pay bills (by: Dec 02 2019)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks on Dec 01 2019:
1.[D][ ] return book (by: Dec 01 2019)
2.[E][ ] project meeting (from: Dec 01 2019 to: 4pm)
____________________________________________________________
____________________________________________________________
Here are the tasks on Dec 03 2019:
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a date, e.g. on 2019-10-15
____________________________________________________________
____________________________________________________________
OOPS!!! Please enter the date in yyyy-MM-dd format, e.g. 2019-10-15.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC12: Find tasks by keyword

**Aim**: Verify the `find <keyword>` command lists only the tasks whose
description contains the keyword (case-insensitive), numbered from 1,
with an empty list when nothing matches, and the expected error when no
keyword is given.

**Input**:
```
todo read book
deadline return book /by 2019-12-01
todo join sports club
mark 1
mark 2
find book
find BOOK
find sports
find xyz
find
list
bye
```

**Expected output**:
```
____________________________________________________________
 ____  ____  ____  
|  _ \|  _ \|  _ \ 
| |_) | | | | | | |
|  __/| |_| | |_| |
|_|   |____/|____/ 

Hello! I'm PDD.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 01 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] join sports club
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Dec 01 2019)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 01 2019)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 01 2019)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] join sports club
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a keyword to search for, e.g. find book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 01 2019)
3.[T][ ] join sports club
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

---

## TC13: Sort the task list

**Aim**: Verify `sort` reorders the task list with dated tasks
(deadlines/events) first in chronological order, followed by undated tasks
(todos) in alphabetical order of description, and that `list` afterwards
reflects the new order.

**Input**:
```
todo z todo
deadline b deadline /by 2019-12-15
todo a todo
event c event /from 2019-12-01 /to 4pm
sort
list
bye
```

**Expected output**:
```
____________________________________________________________
 ____  ____  ____  
|  _ \|  _ \|  _ \ 
| |_) | | | | | | |
|  __/| |_| | |_| |
|_|   |____/|____/ 

Hello! I'm PDD.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] z todo
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] b deadline (by: Dec 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] a todo
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] c event (from: Dec 01 2019 to: 4pm)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Sorted! Here are your tasks, in order:
1.[E][ ] c event (from: Dec 01 2019 to: 4pm)
2.[D][ ] b deadline (by: Dec 15 2019)
3.[T][ ] a todo
4.[T][ ] z todo
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] c event (from: Dec 01 2019 to: 4pm)
2.[D][ ] b deadline (by: Dec 15 2019)
3.[T][ ] a todo
4.[T][ ] z todo
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

---

## Adding new test cases

Append a new `## TCn: <title>` section following the same structure (Aim,
Input, Expected output). The `test-ui` skill reads this file top to bottom
and treats each `## TC` heading as a new, independent session, run in a
fresh instance of the program.
