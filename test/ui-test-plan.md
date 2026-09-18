# UI Test Plan

This file drives the `test-ui` skill. It records how to build and run the
program, and the list of test cases the skill executes against it.

Bot saves its task list to `./data/bot.txt` and reloads it on startup (see
Level 7), so most test cases must start with no leftover data from a
previous test case or a previous manual run, or their expected output
(which assumes an empty task list at startup) won't match. A test case
with a single **Input**/**Expected Output** pair is self-contained: delete
`./data` first, then start the program fresh, feed it the listed input
lines (one per line, in order, as if typed by a user), and compare its
full console output against the expected output. A test case with
multiple numbered **Session** blocks instead (used for testing
persistence itself) is exercised across that many separate runs of the
program *without* deleting `./data` between them, so later sessions see
what earlier sessions saved — see each such test case's **Setup** note for
the exact data-file handling it needs.

## How to run

1. Build (run from the repository root): `find src/main/java -name
   "*.java" | xargs javac -d out`. The source is organized into packages
   under `src/main/java/bot/...` rather than sitting flat in
   `src/main/java`, so it must be discovered recursively; a plain
   `src/main/java/*.java` glob will miss everything in a subpackage.
2. For each test case:
   a. Follow its **Setup** note, if it has one (typically: delete `./data`
      if present; a corrupted-file test case instead writes specific
      content to `./data/bot.txt` before running). A test case with no
      **Setup** note still needs `./data` deleted first, per the note
      above.
   b. For a single **Input**/**Expected Output** pair, run `java -cp out
      bot.Bot` once, sending the **Input** lines to standard input in
      order (one command per line).
   c. For numbered **Session** blocks, run `java -cp out bot.Bot` once
      per session, in order, each time sending that session's **Input**
      lines to standard input — but do NOT delete or reset `./data`
      between sessions within the same test case, since the point is to
      check that state carries over.
3. Compare the program's actual standard output for each run, verbatim,
   against that run's **Expected Output** block. A trailing newline
   difference at the very end of output is not considered a mismatch;
   anything else is.
4. Run test cases in the order listed below. Stop at the first failure.

## Test Case 1: Greet and exit

**Aim:** The program greets the user and exits cleanly when the first
command is `bye`, with no tasks added.

**Input:**
```
bye
```

**Expected Output:**
```
    ____________________________________________________________
 ____   ___  _____ 
| __ ) / _ \|_   _|
|  _ \| | | | | |  
| |_) | |_| | | |  
|____/ \___/  |_|  
     Hello! I'm Bot.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 2: Listing with no tasks

**Aim:** `list` on an empty task collection prints only the header line,
with no numbered entries.

**Input:**
```
list
bye
```

**Expected Output:**
```
    ____________________________________________________________
 ____   ___  _____ 
| __ ) / _ \|_   _|
|  _ \| | | | | |  
| |_) | |_| | | |  
|____/ \___/  |_|  
     Hello! I'm Bot.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 3: Add a todo, a deadline, and an event, then list them

**Aim:** Each of the three task types is added with the correct type icon
and running task count. The deadline/event dates are understood as real
dates (Level 8), not opaque text: entered as `yyyy-MM-dd` (deadline) or
`yyyy-MM-dd HHmm` (event, date + 24-hour time), they're displayed in
`MMM dd yyyy` / `MMM dd yyyy, h:mma` form, and `list` shows them numbered
in insertion order.

**Input:**
```
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600
list
bye
```

**Expected Output:**
```
    ____________________________________________________________
 ____   ___  _____ 
| __ ) / _ \|_   _|
|  _ \| | | | | |  
| |_) | |_| | | |  
|____/ \___/  |_|  
     Hello! I'm Bot.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 1 task in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Oct 15 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)
     Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: Oct 15 2019)
     3.[E][ ] project meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 4: Mark and unmark a task

**Aim:** `mark N` and `unmark N` flip a task's done status, the
confirmation message reflects the change, and `list` shows the updated
status icon afterwards.

**Input:**
```
todo read book
todo join club
mark 1
list
unmark 1
list
bye
```

**Expected Output:**
```
    ____________________________________________________________
 ____   ___  _____ 
| __ ) / _ \|_   _|
|  _ \| | | | | |  
| |_) | |_| | | |  
|____/ \___/  |_|  
     Hello! I'm Bot.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] join club
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[T][ ] join club
    ____________________________________________________________
    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[T][ ] join club
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 5: Error handling for invalid input

**Aim:** An unrecognized command word, a todo/deadline/event with a missing
or malformed argument, a deadline/event/`on` with a date string that isn't
valid `yyyy-MM-dd`(` HHmm`) (Level 8), and a mark/delete with a
missing/non-numeric/out-of-range index each produce a specific "OOPS!!!"
message (via `BotException`) instead of crashing or silently doing
nothing, and no task is added, changed, or removed as a result — the
trailing `list` confirms the task collection is still empty after every
failed command. `mark`, `unmark`, and `delete` all validate their index
through the same `parseTaskIndex` helper, so exercising it via `mark` and
`delete` here is taken as sufficient coverage for `unmark` too, rather
than repeating the same three cases a third time.

**Input:**
```
blah
todo
deadline
deadline return book
deadline return book /by notadate
event
event meeting /from 2019-10-15 1400
event meeting /from 2019-10-15 1400 /to notadate
mark
mark abc
mark 1
delete
delete abc
delete 1
on
on notadate
list
bye
```

**Expected Output:**
```
    ____________________________________________________________
 ____   ___  _____ 
| __ ) / _ \|_   _|
|  _ \| | | | | |  
| |_) | |_| | | |  
|____/ \___/  |_|  
     Hello! I'm Bot.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! I don't understand "blah" - try list, todo, deadline, event, mark, unmark, delete, on, find, sort, or bye.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A todo needs a description, e.g. "todo borrow book".
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A deadline needs a "/by" date or time, e.g. "deadline return book /by 2019-10-15" or "deadline return book /by 2019-10-15 1800".
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A deadline needs a "/by" date or time, e.g. "deadline return book /by 2019-10-15" or "deadline return book /by 2019-10-15 1800".
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! "notadate" isn't a date I understand - use yyyy-MM-dd (e.g. 2019-10-15) or yyyy-MM-dd HHmm (e.g. 2019-10-15 1800).
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event needs both "/from" and "/to", e.g. "event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600".
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event needs both "/from" and "/to", e.g. "event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600".
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! "notadate" isn't a date I understand - use yyyy-MM-dd (e.g. 2019-10-15) or yyyy-MM-dd HHmm (e.g. 2019-10-15 1800).
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Tell me which task number to mark, e.g. "mark 2".
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! "abc" isn't a task number - try something like "mark 2".
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! There's no task number 1 in your list.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Tell me which task number to delete, e.g. "delete 2".
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! "abc" isn't a task number - try something like "delete 2".
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! There's no task number 1 in your list.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Tell me which date, e.g. "on 2019-10-15".
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! "notadate" isn't a date I understand - use yyyy-MM-dd (e.g. 2019-10-15).
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 6: Delete a task

**Aim:** `delete N` removes the Nth task, prints its final state and the
new running task count, and a subsequent `list` shows the remaining
tasks renumbered with no gap left by the deleted task.

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
 ____   ___  _____ 
| __ ) / _ \|_   _|
|  _ \| | | | | |  
| |_) | |_| | | |  
|____/ \___/  |_|  
     Hello! I'm Bot.
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
       [E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
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
       [D][X] return book (by: Jun 06 2019)
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] join sports club
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
     3.[E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
     4.[T][X] join sports club
     5.[T][ ] borrow book
    ____________________________________________________________
    ____________________________________________________________
     Noted. I've removed this task:
       [E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
     Now you have 4 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
     3.[T][X] join sports club
     4.[T][ ] borrow book
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 7: Tasks persist across a restart

**Aim:** Tasks added and marked done in one run of the program are saved
to `./data/bot.txt`, and a later run of the program (started fresh,
without deleting that file) loads them back with the same description,
type, and done status, per Level 7's save-on-change / load-on-startup
behavior. This also covers Level 8: the deadline's date round-trips
through the data file and displays the same way (`Jun 06 2019`) in both
sessions, confirming it's saved/reloaded as a real date rather than as
whatever text was typed.

**Setup:** Delete `./data` if present before Session 1, so Session 1
starts with no saved tasks. Do NOT delete or reset `./data` between
Session 1 and Session 2 — Session 2 must see whatever Session 1 saved.

**Session 1 Input:**
```
todo read book
deadline return book /by 2019-06-06
mark 1
bye
```

**Session 1 Expected Output:**
```
    ____________________________________________________________
 ____   ___  _____ 
| __ ) / _ \|_   _|
|  _ \| | | | | |  
| |_) | |_| | | |  
|____/ \___/  |_|  
     Hello! I'm Bot.
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
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

**Session 2 Input:**
```
list
bye
```

**Session 2 Expected Output:**
```
    ____________________________________________________________
 ____   ___  _____ 
| __ ) / _ \|_   _|
|  _ \| | | | | |  
| |_) | |_| | | |  
|____/ \___/  |_|  
     Hello! I'm Bot.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: Jun 06 2019)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 8: Corrupted data file is reported and partially recovered

**Aim:** If `./data/bot.txt` contains lines that don't match the expected
format (bad task-type letter, bad done flag, missing fields), the program
still starts up, reports each bad line (with its line number and what was
wrong with it) instead of crashing, skips only those lines, and keeps
whatever tasks on other lines parsed correctly — the stretch goal for
Level 7.

**Setup:** Delete `./data` if present, then create the `./data` directory
and write exactly the following content to `./data/bot.txt` (six lines,
mixing two well-formed tasks with four malformed ones):
```
T | 1 | read book
X | 0 | bad type
D | 0 | return book
T | 2 | bad done flag
notenoughfields
T | 0 | join club
```

**Input:**
```
list
bye
```

**Expected Output:**
```
    ____________________________________________________________
 ____   ___  _____ 
| __ ) / _ \|_   _|
|  _ \| | | | | |  
| |_) | |_| | | |  
|____/ \___/  |_|  
     Hello! I'm Bot.
     What can I do for you?
    ____________________________________________________________
     Note: some saved tasks were skipped because the data file looks corrupted:
       - line 2: unknown task type "X" (expected "T", "D", or "E")
       - line 3: a deadline ("D") line needs a 4th field for its "by" date/time
       - line 4: done flag must be "0" or "1", found "2"
       - line 5: expected at least 3 fields separated by " | " (type, done flag, description), found 1
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[T][ ] join club
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 9: `on` finds tasks by calendar date

**Aim:** The `on <yyyy-MM-dd>` command (Level 8 stretch goal) lists tasks
occurring on a given date: a deadline matches only its exact `by` date, an
event matches any date within its `from`..`to` range (inclusive, even a
date that's neither endpoint), a date with nothing scheduled prints the
header with no entries, and a Todo (which has no date) never matches.

**Input:**
```
todo pack bags
deadline return book /by 2019-10-15
event conference /from 2019-10-16 0900 /to 2019-10-18 1700
on 2019-10-15
on 2019-10-17
on 2019-10-20
bye
```

**Expected Output:**
```
    ____________________________________________________________
 ____   ___  _____ 
| __ ) / _ \|_   _|
|  _ \| | | | | |  
| |_) | |_| | | |  
|____/ \___/  |_|  
     Hello! I'm Bot.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] pack bags
     Now you have 1 task in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Oct 15 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] conference (from: Oct 16 2019, 9:00AM to: Oct 18 2019, 5:00PM)
     Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks on Oct 15 2019:
     1.[D][ ] return book (by: Oct 15 2019)
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks on Oct 17 2019:
     1.[E][ ] conference (from: Oct 16 2019, 9:00AM to: Oct 18 2019, 5:00PM)
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks on Oct 20 2019:
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 10: `find` searches task descriptions by keyword

**Aim:** The `find <keyword>` command (Level 9) lists tasks whose
description contains the keyword, matched case-insensitively, keeping
each match's original type, status, and formatting. A keyword matching
nothing prints the header with no entries, and a `find` with no keyword
is rejected with a clear error instead of searching for an empty string.

**Input:**
```
todo read book
deadline return book /by 2019-06-06
mark 1
mark 2
todo join club
find book
find xyz
find
bye
```

**Expected Output:**
```
    ____________________________________________________________
 ____   ___  _____ 
| __ ) / _ \|_   _|
|  _ \| | | | | |  
| |_) | |_| | | |  
|____/ \___/  |_|  
     Hello! I'm Bot.
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
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [D][X] return book (by: Jun 06 2019)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] join club
     Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
    ____________________________________________________________
    ____________________________________________________________
     Here are the matching tasks in your list:
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Tell me what keyword to search for, e.g. "find book".
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Test Case 11: `sort` reorders tasks by date and persists the new order

**Aim:** The `sort` command (C-Sort) reorders the task list: every Todo
first (keeping their original relative order), then every Deadline and
Event together in ascending date order (a Deadline by its due date, an
Event by its start date). The reordering is permanent — it's saved to
`./data/bot.txt`, so a later run of the program (started fresh, without
deleting that file) sees the tasks in the new order via `list`, not just
in `sort`'s own output.

**Setup:** Delete `./data` if present before Session 1, so Session 1
starts with no saved tasks. Do NOT delete or reset `./data` between
Session 1 and Session 2 — Session 2 must see whatever Session 1 saved.

**Session 1 Input:**
```
todo read book
deadline pay bills /by 2019-10-20
event project meeting /from 2019-10-15 /to 2019-10-16
todo join club
sort
bye
```

**Session 1 Expected Output:**
```
    ____________________________________________________________
 ____   ___  _____ 
| __ ) / _ \|_   _|
|  _ \| | | | | |  
| |_) | |_| | | |  
|____/ \___/  |_|  
     Hello! I'm Bot.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] pay bills (by: Oct 20 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
     Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] join club
     Now you have 4 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are your tasks, sorted by date:
     1.[T][ ] read book
     2.[T][ ] join club
     3.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
     4.[D][ ] pay bills (by: Oct 20 2019)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```

**Session 2 Input:**
```
list
bye
```

**Session 2 Expected Output:**
```
    ____________________________________________________________
 ____   ___  _____ 
| __ ) / _ \|_   _|
|  _ \| | | | | |  
| |_) | |_| | | |  
|____/ \___/  |_|  
     Hello! I'm Bot.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[T][ ] join club
     3.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
     4.[D][ ] pay bills (by: Oct 20 2019)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon!
    ____________________________________________________________
```
