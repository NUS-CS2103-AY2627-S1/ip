# UI Test Plan

Regression test cases for Rex's console interface, run via the `test-ui` skill
(`.claude/skills/test-ui/`). A test case runs the program and sends it inputs
in order, checking the program's output against the expected output after each
input. A test case is normally one program session, but it may restart the
program part-way through to check that data was saved to disk.

## How to run

Invoke the `test-ui` skill, or run directly from the repository root:

```
python3 .claude/skills/test-ui/scripts/run_ui_tests.py
```

## Format

Each test case is a fenced ` ```session ` block. A line starting with `>>> `
sends the rest of that line as one line of input. All following lines up to
the next `>>> ` (or the end of the block) are the expected output for that
step, compared line for line. Trailing whitespace on each line is ignored;
everything else must match exactly.

Some inputs are directives rather than input sent to the program:

* `(startup)` sends nothing, and just captures the program's output before
  any input is read.
* `(restart)` quits the program and launches it again, then captures the new
  session's startup output. Because both sessions run in the same working
  directory, this is how a test case checks that something written to disk by
  one session is still there for the next one.
* `(write <path>)` writes the lines that follow it to `<path>` (relative to
  the working directory) instead of treating them as expected output, and
  reads nothing back from the program. Use it with `(restart)` to put a file
  on disk and then start a session that reads it — for example, to check how
  a damaged save file is handled.

Each test case runs in its own empty temporary directory, so files a test
case writes are never seen by another test case and never touch real data in
the repository.

## Test cases

### TC1: Greeting and exit

**Aim:** Verify the startup banner/greeting print correctly, and that `bye`
exits with the farewell message.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC2: Add todos, deadlines, and events; list them

**Aim:** Verify all three task types can be added via `todo`/`deadline`/
`event`, each with the correct type icon, confirmation message, and task
count, and that `list` renders each type's details correctly. Also verify that
dates are shown in display format (`MMM dd yyyy`) rather than as typed, both
with and without a time of day, for a deadline's `/by` and for an event's
`/from` and `/to`.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> todo borrow book
Got it! I've fetched this task for you:
  [T][ ] borrow book
You now have 1 tasks in your bowl!
>>> deadline return book /by 2019-10-15
Got it! I've fetched this task for you:
  [D][ ] return book (by: Oct 15 2019)
You now have 2 tasks in your bowl!
>>> deadline submit report /by 2019-10-15 1800
Got it! I've fetched this task for you:
  [D][ ] submit report (by: Oct 15 2019, 6:00PM)
You now have 3 tasks in your bowl!
>>> event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600
Got it! I've fetched this task for you:
  [E][ ] project meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)
You now have 4 tasks in your bowl!
>>> event team retreat /from 2019-10-18 /to 2019-10-20
Got it! I've fetched this task for you:
  [E][ ] team retreat (from: Oct 18 2019 to: Oct 20 2019)
You now have 5 tasks in your bowl!
>>> list
Here's what's in your bowl:
1.[T][ ] borrow book
2.[D][ ] return book (by: Oct 15 2019)
3.[D][ ] submit report (by: Oct 15 2019, 6:00PM)
4.[E][ ] project meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)
5.[E][ ] team retreat (from: Oct 18 2019 to: Oct 20 2019)
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC3: Mark and unmark tasks

**Aim:** Verify `mark`/`unmark` update a task's status icon (alongside its
type icon), and that `list` reflects the change.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> todo borrow book
Got it! I've fetched this task for you:
  [T][ ] borrow book
You now have 1 tasks in your bowl!
>>> deadline return book /by 2019-10-15
Got it! I've fetched this task for you:
  [D][ ] return book (by: Oct 15 2019)
You now have 2 tasks in your bowl!
>>> mark 1
Nice catch! I've marked this task as done:
  [T][X] borrow book
>>> unmark 1
Okay, putting this one back in the yard — not done yet:
  [T][ ] borrow book
>>> list
Here's what's in your bowl:
1.[T][ ] borrow book
2.[D][ ] return book (by: Oct 15 2019)
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC4: Error handling

**Aim:** Verify bad input produces a clear error (via `RexException`) instead
of being silently ignored or crashing the program, and that the program
keeps running afterward. Includes a date the program cannot read, which
reaches it as an `IllegalArgumentException` from `TaskDateTime` and must be
reported the same way as any other mistake in a command.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> todo
Ruff! The description of a todo cannot be empty.
>>> blah
Woof? I don't know what that means :-(
>>> deadline return book
Ruff! A deadline needs a '/by' date, e.g. deadline return book /by 2019-10-15.
>>> deadline return book /by Sunday
Woof! I don't understand the date "Sunday". Write it as yyyy-mm-dd, e.g. 2019-10-15, optionally with a 24-hour time, e.g. 2019-10-15 1800.
>>> deadline return book /by 15/10/2019
Woof! I don't understand the date "15/10/2019". Write it as yyyy-mm-dd, e.g. 2019-10-15, optionally with a 24-hour time, e.g. 2019-10-15 1800.
>>> event project meeting /from 2019-10-15 1400
Ruff! An event needs a '/to' time, e.g. event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600.
>>> event project meeting /from Monday /to 2019-10-15 1600
Woof! I don't understand the date "Monday". Write it as yyyy-mm-dd, e.g. 2019-10-15, optionally with a 24-hour time, e.g. 2019-10-15 1800.
>>> event project meeting /from 2019-10-15 1400 /to whenever
Woof! I don't understand the date "whenever". Write it as yyyy-mm-dd, e.g. 2019-10-15, optionally with a 24-hour time, e.g. 2019-10-15 1800.
>>> mark abc
Woof! "abc" isn't a valid task number.
>>> mark 99
Woof! There's no task numbered 99 in your bowl.
>>> todo borrow book
Got it! I've fetched this task for you:
  [T][ ] borrow book
You now have 1 tasks in your bowl!
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC5: Delete tasks

**Aim:** Verify `delete` removes the correct task, reports it back, updates
the task count, and that `list` reflects the new indices. Also verify
`delete` reuses the same task-number validation as `mark`/`unmark`.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> todo read book
Got it! I've fetched this task for you:
  [T][ ] read book
You now have 1 tasks in your bowl!
>>> deadline return book /by 2019-10-15
Got it! I've fetched this task for you:
  [D][ ] return book (by: Oct 15 2019)
You now have 2 tasks in your bowl!
>>> todo borrow book
Got it! I've fetched this task for you:
  [T][ ] borrow book
You now have 3 tasks in your bowl!
>>> delete 2
Gotcha! I've removed this task from your bowl:
  [D][ ] return book (by: Oct 15 2019)
You now have 2 tasks in your bowl!
>>> list
Here's what's in your bowl:
1.[T][ ] read book
2.[T][ ] borrow book
>>> delete 99
Woof! There's no task numbered 99 in your bowl.
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC6: Tasks are saved and reloaded after a restart

**Aim:** Verify that tasks added in one session are still present, with their
done/not-done state and type-specific details intact, when the program is
started again. Also verify that a deletion is persisted, i.e. the restarted
session does not bring a deleted task back, and that dates survive the round
trip through the file unchanged — including whether a time of day was given,
which is written to the file and must be read back. Covers a deadline's `/by`
and both ends of an event.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> todo read book
Got it! I've fetched this task for you:
  [T][ ] read book
You now have 1 tasks in your bowl!
>>> deadline return book /by 2019-10-15
Got it! I've fetched this task for you:
  [D][ ] return book (by: Oct 15 2019)
You now have 2 tasks in your bowl!
>>> event team retreat /from 2019-10-18 /to 2019-10-20
Got it! I've fetched this task for you:
  [E][ ] team retreat (from: Oct 18 2019 to: Oct 20 2019)
You now have 3 tasks in your bowl!
>>> deadline submit report /by 2019-10-15 1800
Got it! I've fetched this task for you:
  [D][ ] submit report (by: Oct 15 2019, 6:00PM)
You now have 4 tasks in your bowl!
>>> todo throw away
Got it! I've fetched this task for you:
  [T][ ] throw away
You now have 5 tasks in your bowl!
>>> mark 1
Nice catch! I've marked this task as done:
  [T][X] read book
>>> delete 5
Gotcha! I've removed this task from your bowl:
  [T][ ] throw away
You now have 4 tasks in your bowl!
>>> (restart)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> list
Here's what's in your bowl:
1.[T][X] read book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] team retreat (from: Oct 18 2019 to: Oct 20 2019)
4.[D][ ] submit report (by: Oct 15 2019, 6:00PM)
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC7: A fresh start with no save file

**Aim:** Verify that starting with no save file present is treated as a normal
first run: an empty list, and no error message.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> list
Here's what's in your bowl:
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC8: A damaged save file skips only the bad lines

**Aim:** Verify that lines the save file format doesn't allow are skipped with
a single counted warning, that the readable tasks around them still load, and
that the program remains usable afterwards. Covers each way a line can be
rejected: unknown type letter, wrong field count for the type, a done flag
that isn't 0 or 1, an empty description, and a date the program cannot read
(which a file saved before dates were understood would be full of).

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> (write data/rex.txt)
T | 1 | read book
X | 0 | unknown type letter
D | 0 | deadline with no by field
E | 0 | event missing its to field | Mon 2pm
T | 9 | done flag is not 0 or 1
T | 0 | 
D | 0 | deadline whose date cannot be read | Sunday
D | 1 | return book | 2019-10-15
>>> (restart)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
Ruff! I couldn't read 6 line(s) in data/rex.txt, so I've skipped them.
What can I fetch for you today?
>>> list
Here's what's in your bowl:
1.[T][X] read book
2.[D][X] return book (by: Oct 15 2019)
>>> todo walk the dog
Got it! I've fetched this task for you:
  [T][ ] walk the dog
You now have 3 tasks in your bowl!
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC9: A completely unreadable save file

**Aim:** Verify that a file containing nothing the program recognises loads as
an empty list with a warning, rather than crashing on startup.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> (write data/rex.txt)
{"tasks": [{"description": "read book"}]}
not a task line at all
>>> (restart)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
Ruff! I couldn't read 2 line(s) in data/rex.txt, so I've skipped them.
What can I fetch for you today?
>>> list
Here's what's in your bowl:
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC10: Find the tasks falling on a given day

**Aim:** Verify `on` lists only the tasks that fall on the day asked about.
Covers a deadline matching its due day whatever time of day it carries, a
multi-day event matching every day it spans including both its first and last,
a todo never matching because it has no date, and a day with nothing on it
reporting that instead of an empty heading. Also verify that matches keep
their number from the full list, so a number seen here still refers to the
same task for `mark`/`unmark`/`delete`, and that `on` requires a whole day —
a missing, unreadable or time-bearing argument is refused.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> todo borrow book
Got it! I've fetched this task for you:
  [T][ ] borrow book
You now have 1 tasks in your bowl!
>>> deadline return book /by 2019-10-15
Got it! I've fetched this task for you:
  [D][ ] return book (by: Oct 15 2019)
You now have 2 tasks in your bowl!
>>> deadline submit report /by 2019-10-15 1800
Got it! I've fetched this task for you:
  [D][ ] submit report (by: Oct 15 2019, 6:00PM)
You now have 3 tasks in your bowl!
>>> event team retreat /from 2019-10-18 /to 2019-10-20
Got it! I've fetched this task for you:
  [E][ ] team retreat (from: Oct 18 2019 to: Oct 20 2019)
You now have 4 tasks in your bowl!
>>> on 2019-10-15
Here's what's on Oct 15 2019:
2.[D][ ] return book (by: Oct 15 2019)
3.[D][ ] submit report (by: Oct 15 2019, 6:00PM)
>>> on 2019-10-18
Here's what's on Oct 18 2019:
4.[E][ ] team retreat (from: Oct 18 2019 to: Oct 20 2019)
>>> on 2019-10-19
Here's what's on Oct 19 2019:
4.[E][ ] team retreat (from: Oct 18 2019 to: Oct 20 2019)
>>> on 2019-10-20
Here's what's on Oct 20 2019:
4.[E][ ] team retreat (from: Oct 18 2019 to: Oct 20 2019)
>>> on 2019-10-21
Nothing on Oct 21 2019 — your bowl's empty that day!
>>> on 2019-10-16
Nothing on Oct 16 2019 — your bowl's empty that day!
>>> mark 3
Nice catch! I've marked this task as done:
  [D][X] submit report (by: Oct 15 2019, 6:00PM)
>>> on 2019-10-15
Here's what's on Oct 15 2019:
2.[D][ ] return book (by: Oct 15 2019)
3.[D][X] submit report (by: Oct 15 2019, 6:00PM)
>>> on
Woof! Tell me which day to look at, written as yyyy-mm-dd, e.g. on 2019-10-15.
>>> on Tuesday
Woof! Tell me which day to look at, written as yyyy-mm-dd, e.g. on 2019-10-15.
>>> on 2019-10-15 1800
Woof! Tell me which day to look at, written as yyyy-mm-dd, e.g. on 2019-10-15.
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC11: Find the tasks matching a keyword

**Aim:** Verify `find` lists only the tasks whose description contains the
keyword. Covers matching without regard to case, matching part of a word as
well as a whole one, searching for a phrase of more than one word, a keyword
nothing matches reporting that instead of an empty heading, and `find` with no
keyword being refused. Also verify that matches keep their number from the full
list rather than being renumbered from 1, so that a number shown by `find` still
names the same task for `mark` and `delete`, and that a task's done mark is
shown as it currently stands.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> todo read book
Got it! I've fetched this task for you:
  [T][ ] read book
You now have 1 tasks in your bowl!
>>> deadline return book /by 2019-10-15
Got it! I've fetched this task for you:
  [D][ ] return book (by: Oct 15 2019)
You now have 2 tasks in your bowl!
>>> event bookshop trip /from 2019-10-18 /to 2019-10-20
Got it! I've fetched this task for you:
  [E][ ] bookshop trip (from: Oct 18 2019 to: Oct 20 2019)
You now have 3 tasks in your bowl!
>>> todo buy milk
Got it! I've fetched this task for you:
  [T][ ] buy milk
You now have 4 tasks in your bowl!
>>> find book
Here's what matches "book":
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] bookshop trip (from: Oct 18 2019 to: Oct 20 2019)
>>> find BOOK
Here's what matches "BOOK":
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] bookshop trip (from: Oct 18 2019 to: Oct 20 2019)
>>> find return book
Here's what matches "return book":
2.[D][ ] return book (by: Oct 15 2019)
>>> find milk
Here's what matches "milk":
4.[T][ ] buy milk
>>> find zzz
No sign of "zzz" in your bowl!
>>> find
Woof! Tell me what to sniff out, e.g. find book.
>>> mark 2
Nice catch! I've marked this task as done:
  [D][X] return book (by: Oct 15 2019)
>>> find book
Here's what matches "book":
1.[T][ ] read book
2.[D][X] return book (by: Oct 15 2019)
3.[E][ ] bookshop trip (from: Oct 18 2019 to: Oct 20 2019)
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC12: Tasks to be done within a period

**Aim:** Verify `within` adds a task carrying a date range, that the range is
shown and searched by `on` for every day it covers, and that the task survives
a restart.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> within collect certificate /from 2026-01-15 /to 2026-01-25
Got it! I've fetched this task for you:
  [W][ ] collect certificate (within: Jan 15 2026 to Jan 25 2026)
You now have 1 tasks in your bowl!
>>> within vote /from 2026-01-15 1800 /to 2026-01-16 0900
Got it! I've fetched this task for you:
  [W][ ] vote (within: Jan 15 2026, 6:00PM to Jan 16 2026, 9:00AM)
You now have 2 tasks in your bowl!
>>> list
Here's what's in your bowl:
1.[W][ ] collect certificate (within: Jan 15 2026 to Jan 25 2026)
2.[W][ ] vote (within: Jan 15 2026, 6:00PM to Jan 16 2026, 9:00AM)
>>> on 2026-01-15
Here's what's on Jan 15 2026:
1.[W][ ] collect certificate (within: Jan 15 2026 to Jan 25 2026)
2.[W][ ] vote (within: Jan 15 2026, 6:00PM to Jan 16 2026, 9:00AM)
>>> on 2026-01-20
Here's what's on Jan 20 2026:
1.[W][ ] collect certificate (within: Jan 15 2026 to Jan 25 2026)
>>> on 2026-01-25
Here's what's on Jan 25 2026:
1.[W][ ] collect certificate (within: Jan 15 2026 to Jan 25 2026)
>>> on 2026-01-26
Nothing on Jan 26 2026 — your bowl's empty that day!
>>> mark 1
Nice catch! I've marked this task as done:
  [W][X] collect certificate (within: Jan 15 2026 to Jan 25 2026)
>>> (restart)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> list
Here's what's in your bowl:
1.[W][X] collect certificate (within: Jan 15 2026 to Jan 25 2026)
2.[W][ ] vote (within: Jan 15 2026, 6:00PM to Jan 16 2026, 9:00AM)
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC13: Bad within commands

**Aim:** Verify a `within` command missing its description, either marker or a
readable date is refused with a message naming what is wrong.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> within
Ruff! The description of a within-period task cannot be empty.
>>> within collect certificate
Ruff! A within task needs a '/from' date, e.g. within collect certificate /from 2026-01-15 /to 2026-01-25.
>>> within collect certificate /from 2026-01-15
Ruff! A within task needs a '/to' date, e.g. within collect certificate /from 2026-01-15 /to 2026-01-25.
>>> within collect certificate /from someday /to 2026-01-25
Woof! I don't understand the date "someday". Write it as yyyy-mm-dd, e.g. 2019-10-15, optionally with a 24-hour time, e.g. 2019-10-15 1800.
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC14: Descriptions holding the save file's separator

**Aim:** Verify a description containing `|` is refused for every kind of task,
with or without spaces around it, and that nothing is added; then that a
normal task added afterwards still survives a restart.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> todo buy milk | eggs
Ruff! A description can't contain '|' — I use it to keep your tasks apart in the save file.
>>> todo either|or
Ruff! A description can't contain '|' — I use it to keep your tasks apart in the save file.
>>> deadline pay | file taxes /by 2019-10-15
Ruff! A description can't contain '|' — I use it to keep your tasks apart in the save file.
>>> event a | b /from 2019-10-15 /to 2019-10-16
Ruff! A description can't contain '|' — I use it to keep your tasks apart in the save file.
>>> within a | b /from 2026-01-15 /to 2026-01-25
Ruff! A description can't contain '|' — I use it to keep your tasks apart in the save file.
>>> todo buy milk and eggs
Got it! I've fetched this task for you:
  [T][ ] buy milk and eggs
You now have 1 tasks in your bowl!
>>> list
Here's what's in your bowl:
1.[T][ ] buy milk and eggs
>>> (restart)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> list
Here's what's in your bowl:
1.[T][ ] buy milk and eggs
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC15: Periods that end before they start

**Aim:** Verify an `event` or `within` whose end comes before its start is
refused, whether the days or only the times are the wrong way round; and that a
timed start ending on a plain date that same day is still accepted, since a
plain date stands for the whole day.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> event retreat /from 2019-10-18 /to 2019-10-15
Ruff! An event can't end before it starts.
>>> event meeting /from 2019-10-15 1600 /to 2019-10-15 1400
Ruff! An event can't end before it starts.
>>> within collect certificate /from 2026-01-25 /to 2026-01-15
Ruff! A within task's period can't end before it starts.
>>> event workshop /from 2019-10-15 1400 /to 2019-10-15
Got it! I've fetched this task for you:
  [E][ ] workshop (from: Oct 15 2019, 2:00PM to: Oct 15 2019)
You now have 1 tasks in your bowl!
>>> list
Here's what's in your bowl:
1.[E][ ] workshop (from: Oct 15 2019, 2:00PM to: Oct 15 2019)
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```
