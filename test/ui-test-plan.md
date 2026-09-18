# UI Test Plan

This file drives the `test-ui` skill. Each test case lists the commands to
type at the program's prompt, in order, and the exact console session
transcript expected to result. The last input line of every test case must
be the command that makes the program exit (`bye`), so a full session can be
captured in one run.

## Setup

- **Source directory:** `src/main/java`
- **Main class:** `chud.Chud`

## Test Case: Greet and exit

**Aim:** The program greets the user with its banner and name, then exits cleanly when told to.

**Input:**
```
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Add a todo

**Aim:** A `todo` command adds a task with no date/time and is acknowledged and counted correctly.

**Input:**
```
todo borrow book
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

todo borrow book
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Add a deadline

**Aim:** A `deadline` command adds a task with a `/by` date/time, parsed and stored as a real date (not just free text), and displayed in a friendly format.

**Input:**
```
deadline return book /by 2/12/2019 1800
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

deadline return book /by 2/12/2019 1800
    ____________________________________________________________
     bet, locked in 🔒:
       [D][ ] return book (by: Dec 02 2019, 6:00PM)
     Now you have 1 tasks in the list.
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Add an event

**Aim:** An `event` command adds a task with `/from` and `/to` date/times, parsed and stored as real dates, and displayed in a friendly format.

**Input:**
```
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
    ____________________________________________________________
     bet, locked in 🔒:
       [E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)
     Now you have 1 tasks in the list.
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Mark and unmark tasks

**Aim:** `mark` and `unmark` flip a task's done status, and `list` reflects the change immediately.

**Input:**
```
todo read book
todo return book
mark 1
list
unmark 1
list
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

todo read book
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

todo return book
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] return book
     Now you have 2 tasks in the list.
    ____________________________________________________________

mark 1
    ____________________________________________________________
     W rizz, secured the bag 🏆:
       [T][X] read book
    ____________________________________________________________

list
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[T][ ] return book
    ____________________________________________________________

unmark 1
    ____________________________________________________________
     mid, back to the grind 💀:
       [T][ ] read book
    ____________________________________________________________

list
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[T][ ] return book
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Unrecognized command

**Aim:** A command that isn't `list`, `mark`, `unmark`, `delete`, `todo`, `deadline`, `event`, or `bye` gets a graceful, specific error instead of crashing or being silently added.

**Input:**
```
frobnicate
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

frobnicate
    ____________________________________________________________
     bruh 💀 I don't know what 'frobnicate' means. Try list, todo, deadline, event, on, find, mark, unmark, delete, or bye.
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Empty todo description

**Aim:** A `todo` with no description is rejected with a specific error instead of adding a blank task.

**Input:**
```
todo
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

todo
    ____________________________________________________________
     bruh 💀 The description of a todo cannot be empty. Try: todo borrow book
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Deadline validation errors

**Aim:** A `deadline` missing its `/by` marker, with an empty description or empty `/by` value, or with a `/by` value that isn't a date/time in a recognized format, is rejected with a specific, correctable error instead of crashing.

**Input:**
```
deadline
deadline submit report
deadline /by 2019-10-15
deadline submit report /by
deadline submit report /by not-a-date
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

deadline
    ____________________________________________________________
     bruh 💀 A deadline needs a '/by' date/time. Try: deadline return book /by 2019-10-15 1800
    ____________________________________________________________

deadline submit report
    ____________________________________________________________
     bruh 💀 A deadline needs a '/by' date/time. Try: deadline return book /by 2019-10-15 1800
    ____________________________________________________________

deadline /by 2019-10-15
    ____________________________________________________________
     bruh 💀 The description of a deadline cannot be empty. Try: deadline return book /by 2019-10-15 1800
    ____________________________________________________________

deadline submit report /by
    ____________________________________________________________
     bruh 💀 A deadline needs a '/by' date/time. Try: deadline return book /by 2019-10-15 1800
    ____________________________________________________________

deadline submit report /by not-a-date
    ____________________________________________________________
     bruh 💀 'not-a-date' isn't a date/time I understand. Try a format like 2019-10-15, 2019-10-15 1800, 2/12/2019, or 2/12/2019 1800.
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Event validation errors

**Aim:** An `event` missing its `/from`/`/to` markers, with an empty description or empty `/from`/`/to` value, with a `/from`/`/to` value that isn't a date/time in a recognized format, or with a `/to` date/time chronologically before its `/from` (even though the markers themselves are in the right order), is rejected with a specific, correctable error instead of crashing or being silently accepted.

**Input:**
```
event
event meeting /from 2019-10-15
event /from 2019-10-15 /to 2019-10-16
event meeting /from /to 2019-10-16
event meeting /from not-a-date /to 2019-10-16
event reversed /from 2026-09-22 /to 2026-09-21
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

event
    ____________________________________________________________
     bruh 💀 An event needs both '/from' and '/to' date/times, in that order. Try: event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
    ____________________________________________________________

event meeting /from 2019-10-15
    ____________________________________________________________
     bruh 💀 An event needs both '/from' and '/to' date/times, in that order. Try: event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
    ____________________________________________________________

event /from 2019-10-15 /to 2019-10-16
    ____________________________________________________________
     bruh 💀 The description of an event cannot be empty. Try: event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
    ____________________________________________________________

event meeting /from /to 2019-10-16
    ____________________________________________________________
     bruh 💀 The '/from' and '/to' date/times of an event cannot be empty. Try: event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
    ____________________________________________________________

event meeting /from not-a-date /to 2019-10-16
    ____________________________________________________________
     bruh 💀 'not-a-date' isn't a date/time I understand. Try a format like 2019-10-15, 2019-10-15 1800, 2/12/2019, or 2/12/2019 1800.
    ____________________________________________________________

event reversed /from 2026-09-22 /to 2026-09-21
    ____________________________________________________________
     bruh 💀 An event's '/to' date/time can't be before its '/from' date/time. Try: event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Mark and unmark validation errors

**Aim:** `mark`/`unmark` with a missing, non-numeric, or out-of-range task number is rejected with a specific error instead of crashing (this previously threw an uncaught exception for out-of-range and non-numeric input).

**Input:**
```
mark
mark abc
mark 5
todo do laundry
mark 0
unmark 99
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

mark
    ____________________________________________________________
     bruh 💀 Tell me which task number, e.g. mark 2
    ____________________________________________________________

mark abc
    ____________________________________________________________
     bruh 💀 'abc' is not a valid task number.
    ____________________________________________________________

mark 5
    ____________________________________________________________
     bruh 💀 There is no task number 5. Your task list is empty.
    ____________________________________________________________

todo do laundry
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] do laundry
     Now you have 1 tasks in the list.
    ____________________________________________________________

mark 0
    ____________________________________________________________
     bruh 💀 There is no task number 0. You have 1 task(s).
    ____________________________________________________________

unmark 99
    ____________________________________________________________
     bruh 💀 There is no task number 99. You have 1 task(s).
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Delete a task

**Aim:** `delete` removes the given task from the list, acknowledges what was removed, and reports the new task count; `list` reflects the removal.

**Input:**
```
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

todo read book
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

deadline return book /by 2019-06-06
    ____________________________________________________________
     bet, locked in 🔒:
       [D][ ] return book (by: Jun 06 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________

event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
    ____________________________________________________________
     bet, locked in 🔒:
       [E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
     Now you have 3 tasks in the list.
    ____________________________________________________________

todo join sports club
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] join sports club
     Now you have 4 tasks in the list.
    ____________________________________________________________

todo borrow book
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] borrow book
     Now you have 5 tasks in the list.
    ____________________________________________________________

mark 1
    ____________________________________________________________
     W rizz, secured the bag 🏆:
       [T][X] read book
    ____________________________________________________________

mark 2
    ____________________________________________________________
     W rizz, secured the bag 🏆:
       [D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

mark 4
    ____________________________________________________________
     W rizz, secured the bag 🏆:
       [T][X] join sports club
    ____________________________________________________________

list
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
     3.[E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
     4.[T][X] join sports club
     5.[T][ ] borrow book
    ____________________________________________________________

delete 3
    ____________________________________________________________
     yeeted into the shadow realm 🫡:
       [E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
     Now you have 4 tasks in the list.
    ____________________________________________________________

list
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
     3.[T][X] join sports club
     4.[T][ ] borrow book
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Delete validation errors

**Aim:** `delete` with a missing, non-numeric, or out-of-range task number is rejected with a specific error instead of crashing, and does not change the list.

**Input:**
```
delete
delete abc
delete 1
todo read book
delete 5
list
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

delete
    ____________________________________________________________
     bruh 💀 Tell me which task number, e.g. delete 2
    ____________________________________________________________

delete abc
    ____________________________________________________________
     bruh 💀 'abc' is not a valid task number.
    ____________________________________________________________

delete 1
    ____________________________________________________________
     bruh 💀 There is no task number 1. Your task list is empty.
    ____________________________________________________________

todo read book
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

delete 5
    ____________________________________________________________
     bruh 💀 There is no task number 5. You have 1 task(s).
    ____________________________________________________________

list
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Whitespace-padded commands

**Aim:** Leading/trailing whitespace around a typed command (including `bye`) is trimmed before matching, so padded input behaves the same as the unpadded command instead of being rejected as unrecognized.

**Input:**
```
  todo   pad book  
list  
bye  
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

  todo   pad book  
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] pad book
     Now you have 1 tasks in the list.
    ____________________________________________________________

list  
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] pad book
    ____________________________________________________________

bye  
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: EOF probe

**Aim:** If input ends without an explicit `bye` (e.g. piped input with no trailing command, or the user pressing Ctrl+D), the program exits gracefully via EOF detection instead of crashing with a `NoSuchElementException` or hanging forever. (The runner enforces this indirectly: if the program hung, this run would time out and fail the whole test session rather than merely mismatching one test case.)

**Input:**
```
todo probe task
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

todo probe task
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] probe task
     Now you have 1 tasks in the list.
    ____________________________________________________________
```

## Test Case: List tasks on a date

**Aim:** `on <date>` lists only the deadlines and events occurring on the given date (a deadline matches if its `/by` date matches; an event matches if the date falls within its `/from`-`/to` range), leaving out todos and tasks on other dates. An `on` with a missing or unparsable date is rejected with a specific error instead of crashing.

**Input:**
```
todo pack bags
deadline return book /by 2/12/2019 1800
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
deadline submit report /by 2019-10-15
on 2019-12-02
on
on not-a-date
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

todo pack bags
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] pack bags
     Now you have 1 tasks in the list.
    ____________________________________________________________

deadline return book /by 2/12/2019 1800
    ____________________________________________________________
     bet, locked in 🔒:
       [D][ ] return book (by: Dec 02 2019, 6:00PM)
     Now you have 2 tasks in the list.
    ____________________________________________________________

event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
    ____________________________________________________________
     bet, locked in 🔒:
       [E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)
     Now you have 3 tasks in the list.
    ____________________________________________________________

deadline submit report /by 2019-10-15
    ____________________________________________________________
     bet, locked in 🔒:
       [D][ ] submit report (by: Oct 15 2019)
     Now you have 4 tasks in the list.
    ____________________________________________________________

on 2019-12-02
    ____________________________________________________________
     Here are the tasks occurring on Dec 02 2019:
     1.[D][ ] return book (by: Dec 02 2019, 6:00PM)
     2.[E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)
    ____________________________________________________________

on
    ____________________________________________________________
     bruh 💀 Tell me which date, e.g. on 2019-10-15
    ____________________________________________________________

on not-a-date
    ____________________________________________________________
     bruh 💀 'not-a-date' isn't a date/time I understand. Try a format like 2019-10-15, 2019-10-15 1800, 2/12/2019, or 2/12/2019 1800.
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Bye with trailing arguments

**Aim:** `bye` exits regardless of trailing text after it, since command dispatch is by command word alone (like every other command) rather than an exact match on the whole line.

**Input:**
```
todo test task
bye now please
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

todo test task
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] test task
     Now you have 1 tasks in the list.
    ____________________________________________________________

bye now please
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Find tasks by keyword

**Aim:** `find <keyword>` lists only the tasks whose description contains the keyword (case-insensitively), across all task types; a keyword with no matches lists nothing, and an empty keyword is rejected with a specific error.

**Input:**
```
todo read book
deadline return book /by 2019-06-06
todo do laundry
mark 1
mark 2
find book
find BOOK
find laundry
find nope
find
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

todo read book
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

deadline return book /by 2019-06-06
    ____________________________________________________________
     bet, locked in 🔒:
       [D][ ] return book (by: Jun 06 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________

todo do laundry
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] do laundry
     Now you have 3 tasks in the list.
    ____________________________________________________________

mark 1
    ____________________________________________________________
     W rizz, secured the bag 🏆:
       [T][X] read book
    ____________________________________________________________

mark 2
    ____________________________________________________________
     W rizz, secured the bag 🏆:
       [D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

find book
    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

find BOOK
    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

find laundry
    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][ ] do laundry
    ____________________________________________________________

find nope
    ____________________________________________________________
     Here are the matching tasks in your list:
    ____________________________________________________________

find
    ____________________________________________________________
     bruh 💀 Tell me what keyword to search for, e.g. find book
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```

## Test Case: Sort the task list

**Aim:** `list /sort <key> [asc|desc]` reorders the task list by date, description, type, or done
status, in ascending (default) or descending order; a Todo (no date) always sorts last under the
`date` key regardless of direction. The reorder is permanent, so a later plain `list` shows the
new order. A missing `/sort` marker, an unrecognized key, an unrecognized direction, or extra
trailing arguments are each rejected with a specific error instead of crashing or silently doing
nothing.

**Input:**
```
todo pack bags
deadline return book /by 2019-12-10
event project meeting /from 2019-12-05 1400 /to 2019-12-05 1600
deadline submit report /by 2019-12-01
mark 1
list /sort date
list /sort date desc
list /sort description
list /sort type
list /sort done
list /sort
list /sort bogus
list /sort date bogus
list /sort date asc extra
bye
```

**Expected Output:**
```
    ____________________________________________________________
  ____ _               _ 
 / ___| |__  _   _  __| |
| |   | '_ \| | | |/ _` |
| |___| | | | |_| | (_| |
 \____|_| |_|\__,_|\__,_|
     yo it's Chud 🗿
     what we lockin in today?
    ____________________________________________________________

todo pack bags
    ____________________________________________________________
     bet, locked in 🔒:
       [T][ ] pack bags
     Now you have 1 tasks in the list.
    ____________________________________________________________

deadline return book /by 2019-12-10
    ____________________________________________________________
     bet, locked in 🔒:
       [D][ ] return book (by: Dec 10 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________

event project meeting /from 2019-12-05 1400 /to 2019-12-05 1600
    ____________________________________________________________
     bet, locked in 🔒:
       [E][ ] project meeting (from: Dec 05 2019, 2:00PM to: Dec 05 2019, 4:00PM)
     Now you have 3 tasks in the list.
    ____________________________________________________________

deadline submit report /by 2019-12-01
    ____________________________________________________________
     bet, locked in 🔒:
       [D][ ] submit report (by: Dec 01 2019)
     Now you have 4 tasks in the list.
    ____________________________________________________________

mark 1
    ____________________________________________________________
     W rizz, secured the bag 🏆:
       [T][X] pack bags
    ____________________________________________________________

list /sort date
    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] submit report (by: Dec 01 2019)
     2.[E][ ] project meeting (from: Dec 05 2019, 2:00PM to: Dec 05 2019, 4:00PM)
     3.[D][ ] return book (by: Dec 10 2019)
     4.[T][X] pack bags
    ____________________________________________________________

list /sort date desc
    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] return book (by: Dec 10 2019)
     2.[E][ ] project meeting (from: Dec 05 2019, 2:00PM to: Dec 05 2019, 4:00PM)
     3.[D][ ] submit report (by: Dec 01 2019)
     4.[T][X] pack bags
    ____________________________________________________________

list /sort description
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] pack bags
     2.[E][ ] project meeting (from: Dec 05 2019, 2:00PM to: Dec 05 2019, 4:00PM)
     3.[D][ ] return book (by: Dec 10 2019)
     4.[D][ ] submit report (by: Dec 01 2019)
    ____________________________________________________________

list /sort type
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] pack bags
     2.[D][ ] return book (by: Dec 10 2019)
     3.[D][ ] submit report (by: Dec 01 2019)
     4.[E][ ] project meeting (from: Dec 05 2019, 2:00PM to: Dec 05 2019, 4:00PM)
    ____________________________________________________________

list /sort done
    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] return book (by: Dec 10 2019)
     2.[D][ ] submit report (by: Dec 01 2019)
     3.[E][ ] project meeting (from: Dec 05 2019, 2:00PM to: Dec 05 2019, 4:00PM)
     4.[T][X] pack bags
    ____________________________________________________________

list /sort
    ____________________________________________________________
     bruh 💀 Unknown list option '/sort'. Try: list /sort date, list /sort description, list /sort type, or list /sort done (add 'desc' at the end to reverse, e.g. list /sort date desc).
    ____________________________________________________________

list /sort bogus
    ____________________________________________________________
     bruh 💀 'bogus' isn't a sort key I understand. Try: list /sort date, list /sort description, list /sort type, or list /sort done (add 'desc' at the end to reverse, e.g. list /sort date desc).
    ____________________________________________________________

list /sort date bogus
    ____________________________________________________________
     bruh 💀 'bogus' isn't 'asc' or 'desc'. Try: list /sort date, list /sort description, list /sort type, or list /sort done (add 'desc' at the end to reverse, e.g. list /sort date desc).
    ____________________________________________________________

list /sort date asc extra
    ____________________________________________________________
     bruh 💀 Too many arguments after '/sort date'. Try: list /sort date, list /sort description, list /sort type, or list /sort done (add 'desc' at the end to reverse, e.g. list /sort date desc).
    ____________________________________________________________

bye
    ____________________________________________________________
     aight bye fr, stay locked in 🗿✌️
    ____________________________________________________________
```
