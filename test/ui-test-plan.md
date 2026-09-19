# Nori UI Test Plan

This file is the source of truth for Nori console UI tests. Each test case is one
complete program session and must end with `bye`.

## How to run

From the repository root, with Java 25 active:

```powershell
python .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

## Test 10: Save every successful task-list change

**Aim:** Verify that successful task-list changes create the initially missing data directory, save tasks, and preserve normal command responses.

### Input
```text
todo pack bag
deadline submit report /by 2019-12-02
event team meeting /from Mon 2pm /to 4pm
mark 2
delete 1
unmark 1
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] pack bag
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [D][ ] submit report (by: Dec 02 2019)
     The iceberg now holds 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] team meeting (from: Mon 2pm to: 4pm)
     The iceberg now holds 3 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! This task is now ice-cold complete:
       [D][X] submit report (by: Dec 02 2019)
    ____________________________________________________________

    ____________________________________________________________
     Splash! I kicked this task off the iceberg:
       [T][ ] pack bag
     The iceberg now holds 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Brrr... thawing this task back out:
       [D][ ] submit report (by: Dec 02 2019)
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 16: Handle multi-day and reversed dated events

**Aim:** Verify that an event appears on every date it spans and that an event ending before it starts is rejected without changing the saved list.

**Covers:** Inclusive event-date matching, chronological validation, and state preservation after rejected input.

### Input
```text
event conference /from 2026-09-01 /to 2026-09-03
on 2026-09-01
on 2026-09-02
on 2026-09-03
on 2026-09-04
event backwards /from 2026-09-03 /to 2026-09-01
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] conference (from: 2026-09-01 to: 2026-09-03)
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Things hatching on 2026-09-01:
     1.[E][ ] conference (from: 2026-09-01 to: 2026-09-03)
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Things hatching on 2026-09-02:
     1.[E][ ] conference (from: 2026-09-01 to: 2026-09-03)
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Things hatching on 2026-09-03:
     1.[E][ ] conference (from: 2026-09-01 to: 2026-09-03)
    ____________________________________________________________

    ____________________________________________________________
     Nothing is hatching on 2026-09-04. The ice is quiet.
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! An event cannot end before it starts. Time only waddles forward.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[E][ ] conference (from: 2026-09-01 to: 2026-09-03)
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 17: Preserve hostile-looking task text literally

**Aim:** Verify that markup, path traversal text, shell operators, and the storage delimiter remain inert user data rather than being interpreted or corrupting storage.

**Covers:** Literal output handling and delimiter-safe storage encoding.

### Input
```text
todo <script>alert('x')</script> | ../data & command
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] <script>alert('x')</script> | ../data & command
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[T][ ] <script>alert('x')</script> | ../data & command
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 11: Parse valid deadline dates and reject impossible dates

**Aim:** Verify that a deadline due date is parsed and formatted, while an impossible calendar date is rejected without being stored.

### Input
```text
deadline impossible date /by 2019-02-31
deadline return book /by 2019-12-02
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     NOOT?! I cannot understand "2019-02-31" as a deadline. Use a date like "2019-10-15".
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [D][ ] return book (by: Dec 02 2019)
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[D][ ] return book (by: Dec 02 2019)
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 12: Find deadlines and dated events on a specific date

**Aim:** Verify that `on` lists matching deadlines and events using their original task numbers, and handles dates with no matches, missing dates, and invalid dates.

### Input
```text
deadline submit report /by 2019-10-15
event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600
event impossible date /from 2019-02-31 1400 /to 2019-02-31 1500
on 2019-10-15
on 2019-10-17
on
on 2019-02-31
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [D][ ] submit report (by: Oct 15 2019)
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] project meeting (from: 2019-10-15 1400 to: 2019-10-15 1600)
     The iceberg now holds 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! I cannot understand "2019-02-31" as an event date. Use a date like "2019-10-15".
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Things hatching on 2019-10-15:
     1.[D][ ] submit report (by: Oct 15 2019)
     2.[E][ ] project meeting (from: 2019-10-15 1400 to: 2019-10-15 1600)
    ____________________________________________________________

    ____________________________________________________________
     Nothing is hatching on 2019-10-17. The ice is quiet.
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! "on" needs a date. Try "on 2019-10-15".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! I cannot understand "2019-02-31" as a date. Use a date like "2019-10-15".
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 13: Display command help

**Aim:** Verify that `help` lists every command, including both forms of `schedule`, and documents the date format used by deadline and date-query commands as well as the times a schedule can order events by.

### Input
```text
help
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot noot! Here is what my flippers understand:
     todo <description>
     deadline <description> /by yyyy-MM-dd
     event <description> /from <start> /to <end>
     on yyyy-MM-dd
     schedule
     schedule yyyy-MM-dd
     list
     list /from yyyy-MM-dd /to yyyy-MM-dd
     find <keyword>
     mark <task number>
     unmark <task number>
     delete <task number>
     help
     bye
     Penguin tip: use yyyy-MM-dd in an event's /from or /to so "on" can find it.
     Penguin tip: add a time like 0900, 9:30 or 2pm to an event's /from to place it on the schedule.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 14: List deadlines and events in an inclusive date range

**Aim:** Verify that `list /from ... /to ...` includes deadline boundaries and overlapping dated events,
while excluding todos and non-matching tasks. It must also reject empty, reversed, and malformed ranges.

### Input
```text
todo excluded task
deadline first day /by 2019-01-01
deadline after range /by 2021-01-02
event long project /from 2018-12-01 0900 /to 2021-02-01 1700
event old meeting /from 2018-12-31 0900 /to 2018-12-31 1200
list /from 2019-01-01 /to 2021-01-01
list /from 2022-01-01 /to 2022-01-02
list /from 2021-01-02 /to 2021-01-01
list /from 2019-01-01
list unexpected details
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] excluded task
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [D][ ] first day (by: Jan 01 2019)
     The iceberg now holds 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [D][ ] after range (by: Jan 02 2021)
     The iceberg now holds 3 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] long project (from: 2018-12-01 0900 to: 2021-02-01 1700)
     The iceberg now holds 4 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] old meeting (from: 2018-12-31 0900 to: 2018-12-31 1200)
     The iceberg now holds 5 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Things hatching from 2019-01-01 to 2021-01-01:
     2.[D][ ] first day (by: Jan 01 2019)
     4.[E][ ] long project (from: 2018-12-01 0900 to: 2021-02-01 1700)
    ____________________________________________________________

    ____________________________________________________________
     Nothing is hatching from 2022-01-01 to 2022-01-02. The ice is quiet.
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! The "/to" date cannot be before the "/from" date. Time only waddles forward.
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! A date-range list needs "/to" and an end date. Try "list /from 2019-01-01 /to 2021-01-01".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! Use either "list" or "list /from 2019-01-01 /to 2021-01-01".
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

The runner compares output exactly after normalising line endings, so trailing spaces
matter: the first banner line genuinely ends in a space. Every case needs both an
`### Input` and an `### Expected output` block; a case missing either one aborts the
whole run before any test executes.

## Known gaps

* **JavaFX interactions.** The command-and-output runner exercises the retained console UI. For the graphical UI,
  manually verify that the window opens, both Enter and the Send button submit commands, long conversations scroll to
  the latest message, task-changing commands persist normally, and `bye` shows the farewell before closing the window.
* **Startup loading.** The runner isolates every test session's data directory, so it
  cannot cover a task list surviving from one application launch to another. Check this
  manually by add tasks in one session, then run `list` in a new session; the stored
  task types and completion states should be restored.
* **Unavailable storage.** Check manually that an unreadable storage location reports
  a storage error and leaves the in-memory task list unchanged; this needs filesystem
  setup that the command-only runner cannot express.
* **Corrupt-file recovery.** Automated coverage is in `StorageTest`; it verifies that
  malformed data is preserved as `data/nori.txt.corrupt` and restored from the backup.
* **Input ending without `bye`.** This path is covered in `StorageTest`, because the UI
  runner always supplies a final `bye` and therefore cannot express end-of-input.
* **A capacity limit.** The task list is an unbounded `ArrayList`, so there is no
  full-list message left to test.

## Test 1: Display task subclasses and prevent repeated status changes

**Aim:** Verify that Todo, Deadline, and Event tasks display correctly in one task list and that repeated marking or unmarking reports the existing state.

### Input
```text
todo borrow book
deadline return book /by 2019-12-02
event project meeting /from Mon 2pm /to 4pm
mark 2
mark 2
unmark 2
unmark 2
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] borrow book
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [D][ ] return book (by: Dec 02 2019)
     The iceberg now holds 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     The iceberg now holds 3 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! This task is now ice-cold complete:
       [D][X] return book (by: Dec 02 2019)
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! That task is already frozen solid (done).
    ____________________________________________________________

    ____________________________________________________________
     Brrr... thawing this task back out:
       [D][ ] return book (by: Dec 02 2019)
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! That task is already thawed (not done).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: Dec 02 2019)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 2: Delete a task and renumber the list

**Aim:** Verify that delete removes the selected task, reports it, and shifts the remaining tasks down, so that later commands addressing the new task numbers reach the intended tasks.

**Covers:** removal and renumbering, including the old last position becoming out of range.

### Input
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
unmark 3
mark 4
mark 5
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] read book
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [D][ ] return book (by: Jun 06 2019)
     The iceberg now holds 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     The iceberg now holds 3 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] join sports club
     The iceberg now holds 4 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] borrow book
     The iceberg now holds 5 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! This task is now ice-cold complete:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! This task is now ice-cold complete:
       [D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! This task is now ice-cold complete:
       [T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
     3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     4.[T][X] join sports club
     5.[T][ ] borrow book
    ____________________________________________________________

    ____________________________________________________________
     Splash! I kicked this task off the iceberg:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     The iceberg now holds 4 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
     3.[T][X] join sports club
     4.[T][ ] borrow book
    ____________________________________________________________

    ____________________________________________________________
     Brrr... thawing this task back out:
       [T][ ] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! This task is now ice-cold complete:
       [T][X] borrow book
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! The iceberg has only 4 task(s), so "mark 5" points straight into the sea.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
     3.[T][ ] join sports club
     4.[T][X] borrow book
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 3: Reject task-number overflow without changing tasks

**Aim:** Verify that an integer-overflow task number receives the overflow message and does not alter the existing list.

### Input
```text
todo read book
mark 999999999999999999999999
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] read book
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     GIANT NOOT! "999999999999999999999999" is far too large for a task number. Use a whole number from 1 to 1.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 4: Reject invalid commands without crashing

**Aim:** Verify that incomplete commands, invalid task numbers, and unknown commands produce helpful error messages instead of ending the program.

### Input
```text
todo
deadline do homework
event project meeting /from Monday
mark eat food
unmark 1
blah
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     NOOT?! A todo needs a description. Try "todo borrow book"; my flippers cannot read minds.
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! I cannot find the "/by" part of that deadline. Use "deadline submit report /by 2019-10-15".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! An event is missing "/to" and its end time. Even penguin meetings eventually end.
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! "eat food" is not a task number. Use "mark 1"; penguins count with digits.
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! The iceberg is empty, so there is nothing to unmark.
    ____________________________________________________________

    ____________________________________________________________
     CONFUSED NOOT! My flippers do not understand that command. Try todo, deadline, event, on, list, find, mark, unmark, delete, help, or bye.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 5: Preserve task state after interleaved invalid commands

**Aim:** Verify that rejected commands neither add tasks nor change task statuses when mixed between valid task operations.

### Input
```text
todo
todo finish lab
mark 2
mark 1
deadline /by Friday
deadline submit report /by 2019-12-02
event project meeting /to 4pm /from Mon 2pm
event project meeting /from Mon 2pm /to 4pm
unmark 4
unmark 1
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     NOOT?! A todo needs a description. Try "todo borrow book"; my flippers cannot read minds.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] finish lab
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! The iceberg has only 1 task(s), so "mark 2" points straight into the sea.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! This task is now ice-cold complete:
       [T][X] finish lab
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! A deadline needs a description before "/by". Try "deadline submit report /by 2019-10-15".
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [D][ ] submit report (by: Dec 02 2019)
     The iceberg now holds 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! Put "/from" before "/to". Time waddles forward, not backward.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     The iceberg now holds 3 task(s).
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! The iceberg has only 3 task(s), so "unmark 4" points straight into the sea.
    ____________________________________________________________

    ____________________________________________________________
     Brrr... thawing this task back out:
       [T][ ] finish lab
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[T][ ] finish lab
     2.[D][ ] submit report (by: Dec 02 2019)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 6: Delete at both ends of the list and empty it

**Aim:** Verify that delete works at the first and last positions and that a fully emptied list says so instead of printing a heading with nothing under it.

**Covers:** delete at the first, last, and last-remaining positions, and the empty-list branch of `formatTaskList`.

### Input
```text
todo alpha
todo beta
todo charlie
delete 1
list
delete 2
list
delete 1
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] alpha
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] beta
     The iceberg now holds 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] charlie
     The iceberg now holds 3 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Splash! I kicked this task off the iceberg:
       [T][ ] alpha
     The iceberg now holds 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[T][ ] beta
     2.[T][ ] charlie
    ____________________________________________________________

    ____________________________________________________________
     Splash! I kicked this task off the iceberg:
       [T][ ] charlie
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[T][ ] beta
    ____________________________________________________________

    ____________________________________________________________
     Splash! I kicked this task off the iceberg:
       [T][ ] beta
     The iceberg now holds 0 task(s).
    ____________________________________________________________

    ____________________________________________________________
     The iceberg is empty. Try "todo borrow book". Noot noot!
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 7: Reject task numbers that are missing, zero, negative, or out of range

**Aim:** Verify that mark, unmark, and delete each explain exactly why a task number is unusable, and that no such command changes the list.

**Covers:** Every branch of `getTaskNumberError` except the overflow branch, which Test 3 covers.

### Input
```text
mark 1
delete 1
todo alpha
mark
mark 0
mark -3
unmark 2
delete 7
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     NOOT?! The iceberg is empty, so there is nothing to mark.
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! The iceberg is empty, so there is nothing to delete.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] alpha
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! "mark" needs a task number. Try "mark 1".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! Task numbers start from 1, not 0. Penguins can count!
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! Task numbers start from 1, not -3. Penguins can count!
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! The iceberg has only 1 task(s), so "unmark 2" points straight into the sea.
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! The iceberg has only 1 task(s), so "delete 7" points straight into the sea.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[T][ ] alpha
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 8: Report each missing part of a deadline or event

**Aim:** Verify that a deadline or event missing a description, a date, or a separator names the part that is missing, and that none of them is stored.

**Covers:** The `/by`-without-a-date branch, and the both-separators-missing, description-before-`/from`, and missing-`/from` event branches.

### Input
```text
deadline submit report /by
event team meeting
event /from Mon 2pm /to 4pm
event team meeting /to 4pm
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     NOOT?! A deadline needs a due date after "/by". Try "deadline submit report /by 2019-10-15".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! An event needs both "/from" and "/to". Use "event team meeting /from Mon 2pm /to 4pm".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! An event needs a description before "/from". Try "event team meeting /from Mon 2pm /to 4pm".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! An event is missing "/from" and its start time. Tell me when to start waddling.
    ____________________________________________________________

    ____________________________________________________________
     The iceberg is empty. Try "todo borrow book". Noot noot!
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 9: Tolerate stray whitespace but reject near-miss commands

**Aim:** Verify that surrounding whitespace never turns a valid command into an unknown one, while blank input, wrong capitalisation, and words that merely start with a command keyword are still rejected -- each of them now naming the command the user most likely meant rather than listing every command Nori knows.

**Covers:** Input trimming in the main loop, the keyword-boundary check in `isCommandTypeOf`, and the blank-input, wrong-case and nearest-keyword branches of `CommandSuggestions`.

### Input
```text

TODO read book
todos read book
listen
marker 1
todo    read book   
   list   
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot? You didn't say anything. Type "help" and I'll show you what my flippers can do.
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! My commands are all lowercase. Did you mean "todo"?
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! I do not know "todos". Did you mean "todo"?
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! I do not know "listen". Did you mean "list"?
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! I do not know "marker". Did you mean "mark"?
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] read book
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 15: Find tasks by description keyword

**Aim:** Verify that find matches a keyword anywhere in a task description, ignores case, keeps each task's number from the full list, reports when nothing matches, and rejects a search with no keyword.

### Input
```text
todo read book
deadline return book /by 2019-06-06
event book fair /from 2019-06-06 1000 /to 2019-06-06 1800
todo Buy Milk
mark 1
find bicycle
find milk
find BOOK
find
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] read book
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [D][ ] return book (by: Jun 06 2019)
     The iceberg now holds 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] book fair (from: 2019-06-06 1000 to: 2019-06-06 1800)
     The iceberg now holds 3 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] Buy Milk
     The iceberg now holds 4 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! This task is now ice-cold complete:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     No matching fish in this sea. Try another keyword!
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! I found these fish:
     4.[T][ ] Buy Milk
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! I found these fish:
     1.[T][X] read book
     2.[D][ ] return book (by: Jun 06 2019)
     3.[E][ ] book fair (from: 2019-06-06 1000 to: 2019-06-06 1800)
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! "find" needs a keyword. Try "find book"; even penguins need a clue.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 18: Lay a day out as a schedule

**Aim:** Verify that `schedule` orders a date's timed events by their start time, groups the all-day events, deadlines and to-dos beneath them under their own headings, drops the sections with nothing in them, and rejects a date it cannot read.

### Input
```text
todo read book
deadline return book /by 2019-06-06
event exam /from 2019-06-06 0900 /to 2019-06-06 1200
event conference /from 2019-06-06 /to 2019-06-08
event book fair /from 2019-06-06 1000 /to 1800
schedule 2019-06-06
schedule 2019-07-01
schedule tomorrow
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] read book
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [D][ ] return book (by: Jun 06 2019)
     The iceberg now holds 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] exam (from: 2019-06-06 0900 to: 2019-06-06 1200)
     The iceberg now holds 3 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] conference (from: 2019-06-06 to: 2019-06-08)
     The iceberg now holds 4 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] book fair (from: 2019-06-06 1000 to: 1800)
     The iceberg now holds 5 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Schedule for 2019-06-06:
     09:00 3.[E][ ] exam (from: 2019-06-06 0900 to: 2019-06-06 1200)
     10:00 5.[E][ ] book fair (from: 2019-06-06 1000 to: 1800)
     All day:
           4.[E][ ] conference (from: 2019-06-06 to: 2019-06-08)
     Due:
           2.[D][ ] return book (by: Jun 06 2019)
     Anytime:
           1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Schedule for 2019-07-01:
     Anytime:
           1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! I cannot understand "tomorrow" as a date. Use a date like "2019-10-15".
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 19: Reject a command that repeats or misuses an option

**Aim:** Verify that repeating an option, or using one a command does not accept, is named as the fault rather than folded into a value, and that no task is stored by any of them.

**Covers:** The repeated-option and unexpected-option branches of the deadline, event and date-range list commands, and the adjoining-separator case that once threw out of `EventCommand`.

### Input
```text
deadline report /by 2019-01-01 /by 2019-02-02
event trip /from 2019-01-01 /to 2019-01-02 /to 2019-01-05
deadline report /from 2019-01-01 /by 2019-02-02
event trip /from 2019-01-01 /to 2019-01-02 /by 2019-01-05
list /from 2019-01-01 /to 2019-01-01 /to 2019-05-05
list /from 2019-01-01 /to 2019-05-05 /by 2020-01-01
event x /from /to y
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     NOOT?! A deadline takes only one "/by". Try "deadline submit report /by 2019-10-15".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! An event takes only one "/to". Use "event team meeting /from Mon 2pm /to 4pm".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! A deadline does not use "/from". Try "deadline submit report /by 2019-10-15".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! An event does not use "/by". Use "event team meeting /from Mon 2pm /to 4pm".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! A date-range list takes only one "/to". Try "list /from 2019-01-01 /to 2021-01-01".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! A date-range list does not use "/by". Try "list /from 2019-01-01 /to 2021-01-01".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! "/from" needs a start time. I cannot waddle in from the void.
    ____________________________________________________________

    ____________________________________________________________
     The iceberg is empty. Try "todo borrow book". Noot noot!
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 20: Reject an event that ends before it starts

**Aim:** Verify that an event whose end falls at or before its start is rejected whenever Nori can be sure of the order, and that details naming a day in words Nori cannot read are still accepted rather than guessed at.

**Covers:** `Event.findOrderingError`, for reversed dates, reversed bare clock times, an undated end time on a dated start, and identical times; and its deliberate silence on "Mon 2pm" to "Tue 1pm".

### Input
```text
event talk /from 2pm /to 1pm
event workshop /from 2019-06-06 1000 /to 0800
event standup /from 0900 /to 0900
event backwards /from 2026-09-03 /to 2026-09-01
event overnight /from Mon 2pm /to Tue 1pm
event fair /from 2019-06-06 1000 /to 1800
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     NOOT?! That event ends at or before it starts. Time only waddles forward.
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! That event ends at or before it starts. Time only waddles forward.
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! That event ends at or before it starts. Time only waddles forward.
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! An event cannot end before it starts. Time only waddles forward.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] overnight (from: Mon 2pm to: Tue 1pm)
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] fair (from: 2019-06-06 1000 to: 1800)
     The iceberg now holds 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[E][ ] overnight (from: Mon 2pm to: Tue 1pm)
     2.[E][ ] fair (from: 2019-06-06 1000 to: 1800)
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 21: Refuse to add a task the iceberg already holds

**Aim:** Verify that adding a task the list already records is refused and names the task already stored, while a task differing in its date, its span, or the case of its description is still added, and neither completion nor extra spacing makes a repeat look new.

**Covers:** `Task.isSameTask` and its deadline and event overrides, `TaskList.findSameTask`, and the duplicate branch of `AddTaskCommand`.

### Input
```text
todo read book
todo read book
mark 1
todo read book
todo Read Book
todo read   book
deadline report /by 2019-01-01
deadline report /by 2019-01-02
deadline report /by 2019-01-01
event fair /from 1pm /to 2pm
event fair /from 1pm /to 3pm
event fair /from 1pm /to 2pm
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] read book
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! The iceberg already holds that task:
       [T][ ] read book
     I won't carry the same fish twice.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! This task is now ice-cold complete:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! The iceberg already holds that task:
       [T][X] read book
     I won't carry the same fish twice.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [T][ ] Read Book
     The iceberg now holds 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! The iceberg already holds that task:
       [T][X] read book
     I won't carry the same fish twice.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [D][ ] report (by: Jan 01 2019)
     The iceberg now holds 3 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [D][ ] report (by: Jan 02 2019)
     The iceberg now holds 4 task(s).
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! The iceberg already holds that task:
       [D][ ] report (by: Jan 01 2019)
     I won't carry the same fish twice.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] fair (from: 1pm to: 2pm)
     The iceberg now holds 5 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] fair (from: 1pm to: 3pm)
     The iceberg now holds 6 task(s).
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! The iceberg already holds that task:
       [E][ ] fair (from: 1pm to: 2pm)
     I won't carry the same fish twice.
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[T][X] read book
     2.[T][ ] Read Book
     3.[D][ ] report (by: Jan 01 2019)
     4.[D][ ] report (by: Jan 02 2019)
     5.[E][ ] fair (from: 1pm to: 2pm)
     6.[E][ ] fair (from: 1pm to: 3pm)
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```

## Test 22: Reject an event time no clock can show

**Aim:** Verify that an event whose start or end names a clock time that does not exist is rejected and names that time, while real times in the same forms are still accepted.

**Covers:** `Event.findTimeError` and `ClockTimes.findImpossibleTime`, for an hour past 23, a minute past 59, a compact time past 2359, and an hour past 12 before a meridiem.

### Input
```text
event talk /from 2026-10-01 25:00 /to 2026-10-02
event talk /from 2026-10-01 9:75 /to 2026-10-01 1000
event talk /from 2026-10-01 1000 /to 2026-10-01 2400
event talk /from 13pm /to 2pm
event talk /from 2026-10-01 0930 /to 2026-10-01 11:45
list
bye
```

### Expected output
```text
  _   _  ____  _____  _____ 
 | \ | |/ __ \|  __ \|_   _|
 |  \| | |  | | |__) | | |  
 | . ` | |  | |  _  /  | |  
 | |\  | |__| | | \ \ _| |_ 
 |_| \_|\____/|_|  \_\_____|


____________________________________________________________
Noot noot! I'm Nori, your tiny task penguin.
Waddle in a command and I'll get flapping.
____________________________________________________________

    ____________________________________________________________
     NOOT?! I cannot understand "25:00" as an event time. Use a time like "1400" or "2:30pm".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! I cannot understand "9:75" as an event time. Use a time like "1400" or "2:30pm".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! I cannot understand "2400" as an event time. Use a time like "1400" or "2:30pm".
    ____________________________________________________________

    ____________________________________________________________
     NOOT?! I cannot understand "13pm" as an event time. Use a time like "1400" or "2:30pm".
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Task tucked safely under my wing:
       [E][ ] talk (from: 2026-10-01 0930 to: 2026-10-01 11:45)
     The iceberg now holds 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Tasks currently chilling on the iceberg:
     1.[E][ ] talk (from: 2026-10-01 0930 to: 2026-10-01 11:45)
    ____________________________________________________________

    ____________________________________________________________
     Noot noot! Time to waddle off. Stay frosty!
    ____________________________________________________________
```
