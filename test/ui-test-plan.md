# UI Test Plan

Test cases for the `test-ui` skill (`.claude/skills/test-ui/`). Each test case
is run as one continuous Steph session: its commands are sent to the program
in order, and the output that follows each command is checked against the
recorded expected output. State (e.g. tasks added earlier in the same test
case) carries across commands within a test case, the same way it would in a
real session -- but each test case starts a fresh instance of the program, so
nothing carries over *between* test cases.

Run the whole plan with:

```
python3 .claude/skills/test-ui/scripts/run_ui_tests.py
```

## Format

Each test case starts with a `## Test case: <short name>` heading, followed
by an `**Aim:**` line describing what it's checking and why. After that comes
one or more pairs of sections: a `### Command` heading with the single
command in a fenced code block, then an `### Expected output` heading with
the response Steph should print in a fenced code block -- without the
surrounding `____...` delimiter lines, since those are added automatically.
The test cases below are the canonical example of the format; copy the
structure of one of those rather than improvising.

A test case can have as many Command/Expected output pairs as needed. Don't
put more than one command in a single Command block.

### Restarting the program mid-test-case

To test that tasks are saved to disk and reloaded on startup, put a bare
`### Restart` heading (no fenced block) between an `### Expected output` and
the next `### Command`. At that point the runner ends the current Steph
process and starts a fresh one **in the same working directory**: in-memory
state is gone, but `./data/steph.txt` is still there, so the new process
loads whatever the previous commands saved. A test case can restart more
than once.

Don't hand-type the expected output -- it's easy to get a space or a blank
line wrong and end up "fixing" a false failure instead of a real one. Instead
run the commands for real and let the script generate the block:

```
python3 .claude/skills/test-ui/scripts/run_ui_tests.py record "todo buy milk" "list"
```

Pass a bare `RESTART` argument where you want a restart -- the recorder runs
the commands before it and after it as separate processes sharing one data
file, and prints a `### Restart` line between the blocks:

```
python3 .claude/skills/test-ui/scripts/run_ui_tests.py record "todo buy milk" RESTART "list"
```

then read over the printed output to confirm it's actually correct (the
script only captures what the program did, not what it *should* do) before
pasting it in here.

Don't include `bye` as a command in a test case: the program doesn't print a
response block for it (it just ends the session), and the session ends on its
own once its commands run out anyway.

---

## Test case: Add a todo and list it

**Aim:** A `todo` command with a valid name adds a task, and `list` displays
it with the `[T]` type marker and an unchecked `[ ]` box.

### Command
```
todo borrow book
```

### Expected output
```
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
```

### Command
```
list
```

### Expected output
```
Here are the tasks in your list:
1.[T][ ] borrow book
```

## Test case: Add a deadline

**Aim:** A `deadline` command splits its argument on `/by` into the task name
and the due date, parses the date from `yyyy-mm-dd`, and displays it in
`MMM dd yyyy` form with the `[D]` marker.

### Command
```
deadline return book /by 2019-10-15
```

### Expected output
```
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 1 tasks in the list.
```

## Test case: Add an event

**Aim:** An `event` command splits its argument on `/from` and `/to` into the
task name, start date, and end date, parses each date from `yyyy-mm-dd`, and
displays them in `MMM dd yyyy` form with the `[E]` marker.

### Command
```
event project meeting /from 2019-10-15 /to 2019-10-16
```

### Expected output
```
Got it. I've added this task:
  [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Now you have 1 tasks in the list.
```

## Test case: Add a deadline with a time

**Aim:** A `deadline` whose `/by` value is `yyyy-mm-dd HHmm` is stored as a
`LocalDateTime`; the `HHmm` time is parsed as 24-hour and shown after the date
as `h:mma` (e.g. `6:00pm`). A morning time confirms `9:00am` too.

### Command
```
deadline return book /by 2019-10-15 1800
```

### Expected output
```
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019, 6:00pm)
Now you have 1 tasks in the list.
```

### Command
```
deadline early bird /by 2019-10-15 0900
```

### Expected output
```
Got it. I've added this task:
  [D][ ] early bird (by: Oct 15 2019, 9:00am)
Now you have 2 tasks in the list.
```

## Test case: Add an event with start and end times

**Aim:** An `event` accepts a `yyyy-mm-dd HHmm` value on both `/from` and `/to`,
storing each as a `LocalDateTime` and showing the time alongside the date.

### Command
```
event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600
```

### Expected output
```
Got it. I've added this task:
  [E][ ] project meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)
Now you have 1 tasks in the list.
```

## Test case: Mark and unmark a task

**Aim:** `mark` sets a task's checkbox to `[X]` and `unmark` sets it back to
`[ ]`, referencing the task by its 1-based position in the list.

### Command
```
todo borrow book
```

### Expected output
```
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
```

### Command
```
mark 1
```

### Expected output
```
Awesome! I've marked this task as done:
  [T][X] borrow book
```

### Command
```
unmark 1
```

### Expected output
```
OK, I've marked this task as not done yet:
  [T][ ] borrow book
```

## Test case: Delete a task

**Aim:** `delete` removes the task at the given 1-based position, shifts the
remaining tasks up, and `list` reflects the updated numbering.

### Command
```
todo borrow book
```

### Expected output
```
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
```

### Command
```
todo return book
```

### Expected output
```
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
```

### Command
```
delete 1
```

### Expected output
```
Okay! I've removed this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
```

### Command
```
list
```

### Expected output
```
Here are the tasks in your list:
1.[T][ ] return book
```

## Test case: Delete with an out-of-range task number is rejected

**Aim:** `delete` reuses the same task-number validation as `mark`/`unmark`,
so a number beyond the end of the list should report a usage error instead
of throwing an `IndexOutOfBoundsException`.

### Command
```
todo borrow book
```

### Expected output
```
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
```

### Command
```
delete 5
```

### Expected output
```
Hmm.. I don't understand that.
Please type "delete <task-number>" with a valid task number.
```

## Test case: Deadline missing /by is rejected

**Aim:** A `deadline` command with no `/by` marker should not silently create
a task with a blank due date -- it should report a usage error instead.

### Command
```
deadline missing name
```

### Expected output
```
Hmm.. I don't understand that.
Please type "deadline <task-name> /by <yyyy-mm-dd>".
```

## Test case: Deadline with an unparseable date is rejected

**Aim:** A `deadline` command whose `/by` value isn't a valid `yyyy-mm-dd`
date reports a readable hint instead of crashing on `DateTimeParseException`.

### Command
```
deadline submit report /by next friday
```

### Expected output
```
Hmm.. I couldn't read "next friday" as a date.
Please use the format yyyy-mm-dd or yyyy-mm-dd HHmm, e.g. 2019-10-15 1800.
```

## Test case: Unknown command is rejected

**Aim:** A command that isn't `list`/`mark`/`unmark`/`todo`/`deadline`/`event`
should report an error instead of being silently added to the task list as
plain text.

### Command
```
foo bar
```

### Expected output
```
Hmm.. I don't understand that command: "foo".
```

## Test case: Tasks are saved and reloaded across restarts

**Aim:** The task list is written to `./data/steph.txt` after every change and
read back when Steph starts, so a task added in one session is still there in
the next. Checks all three task types round-trip, that a task's done status
(`mark`) is preserved, and that a change made after a restart (`delete`) is
itself saved for the following restart.

### Command
```
todo read book
```

### Expected output
```
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

### Command
```
deadline return book /by 2019-10-15
```

### Expected output
```
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
```

### Command
```
event project meeting /from 2019-10-15 /to 2019-10-16
```

### Expected output
```
Got it. I've added this task:
  [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Now you have 3 tasks in the list.
```

### Command
```
mark 2
```

### Expected output
```
Awesome! I've marked this task as done:
  [D][X] return book (by: Oct 15 2019)
```

### Restart

### Command
```
list
```

### Expected output
```
Here are the tasks in your list:
1.[T][ ] read book
2.[D][X] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
```

### Command
```
delete 1
```

### Expected output
```
Okay! I've removed this task:
  [T][ ] read book
Now you have 2 tasks in the list.
```

### Restart

### Command
```
list
```

### Expected output
```
Here are the tasks in your list:
1.[D][X] return book (by: Oct 15 2019)
2.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
```

## Test case: A deadline's time survives a restart

**Aim:** The `HHmm` time on a deadline is written to `./data/steph.txt` as part
of an ISO `LocalDateTime` (`2019-10-15T18:00`) and read back unchanged on
startup, so the displayed time is the same before and after a restart.

### Command
```
deadline return book /by 2019-10-15 1800
```

### Expected output
```
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019, 6:00pm)
Now you have 1 tasks in the list.
```

### Command
```
list
```

### Expected output
```
Here are the tasks in your list:
1.[D][ ] return book (by: Oct 15 2019, 6:00pm)
```

### Restart

### Command
```
list
```

### Expected output
```
Here are the tasks in your list:
1.[D][ ] return book (by: Oct 15 2019, 6:00pm)
```

## Test case: Find tasks by keyword

**Aim:** `find <keyword>` lists every task whose name contains the keyword,
re-numbered from 1, and matches regardless of letter case. A keyword that
matches nothing reports a "couldn't find" message rather than an empty list.

### Command
```
todo read book
```

### Expected output
```
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

### Command
```
deadline return book /by 2019-10-15
```

### Expected output
```
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
```

### Command
```
todo buy milk
```

### Expected output
```
Got it. I've added this task:
  [T][ ] buy milk
Now you have 3 tasks in the list.
```

### Command
```
find book
```

### Expected output
```
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2019)
```

### Command
```
find BOOK
```

### Expected output
```
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2019)
```

### Command
```
find umbrella
```

### Expected output
```
I couldn't find any tasks matching "umbrella".
```

## Test case: Find with no keyword is rejected

**Aim:** `find` on its own has no keyword to search for -- without a check it
would "match" every task -- so it reports a usage error instead.

### Command
```
find
```

### Expected output
```
Hmm.. I don't understand that.
Please type "find <keyword>".
```

## Test case: Adding a clashing event is rejected

**Aim:** An `event` that overlaps an existing pending event is rejected
instead of silently added, and the rejection message lists which event it
clashes with and how to add it anyway.

### Command
```
event camp /from 2019-10-15 1400 /to 2019-10-15 1800
```

### Expected output
```
Got it. I've added this task:
  [E][ ] camp (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 6:00pm)
Now you have 1 tasks in the list.
```

### Command
```
event trip /from 2019-10-15 1600 /to 2019-10-15 2000
```

### Expected output
```
This clashes with an existing event:
  [E][ ] camp (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 6:00pm)
Add '/force' to the command if you want to schedule it anyway.
```

## Test case: An event whose start isn't before its end is rejected

**Aim:** An `event` command is rejected -- rather than silently accepted with
a backwards or zero-duration range -- when its `/from` is not strictly
before its `/to`. This is checked before the clash check, so it applies even
to the very first event, and `/force` does not bypass it.

### Command
```
event exam /from 2019-10-20 /to 2019-10-15
```

### Expected output
```
Hmm.. I don't understand that.
An event's start (Oct 20 2019) must be before its end (Oct 15 2019).
```

### Command
```
event exam /from 2019-10-15 1400 /to 2019-10-15 1400
```

### Expected output
```
Hmm.. I don't understand that.
An event's start (Oct 15 2019, 2:00pm) must be before its end (Oct 15 2019, 2:00pm).
```

### Command
```
event exam /from 2019-10-20 /to 2019-10-15 /force
```

### Expected output
```
Hmm.. I don't understand that.
An event's start (Oct 20 2019) must be before its end (Oct 15 2019).
```

## Test case: /force overrides a clash

**Aim:** Appending a trailing `/force` to an `event` command skips the clash
check entirely, adding the event despite the overlap.

### Command
```
event camp /from 2019-10-15 1400 /to 2019-10-15 1800
```

### Expected output
```
Got it. I've added this task:
  [E][ ] camp (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 6:00pm)
Now you have 1 tasks in the list.
```

### Command
```
event trip /from 2019-10-15 1600 /to 2019-10-15 2000 /force
```

### Expected output
```
Got it. I've added this task:
  [E][ ] trip (from: Oct 15 2019, 4:00pm to: Oct 15 2019, 8:00pm)
Now you have 2 tasks in the list.
```

### Command
```
list
```

### Expected output
```
Here are the tasks in your list:
1.[E][ ] camp (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 6:00pm)
2.[E][ ] trip (from: Oct 15 2019, 4:00pm to: Oct 15 2019, 8:00pm)
```

## Test case: Completed events are excluded from the clash check

**Aim:** Once an event is marked done, it no longer represents a live
scheduling commitment, so a new event overlapping it is added normally
instead of being rejected.

### Command
```
event camp /from 2019-10-15 1400 /to 2019-10-15 1800
```

### Expected output
```
Got it. I've added this task:
  [E][ ] camp (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 6:00pm)
Now you have 1 tasks in the list.
```

### Command
```
mark 1
```

### Expected output
```
Awesome! I've marked this task as done:
  [E][X] camp (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 6:00pm)
```

### Command
```
event trip /from 2019-10-15 1600 /to 2019-10-15 2000
```

### Expected output
```
Got it. I've added this task:
  [E][ ] trip (from: Oct 15 2019, 4:00pm to: Oct 15 2019, 8:00pm)
Now you have 2 tasks in the list.
```

## Test case: A date-only event clashes with a timed event the same day

**Aim:** An event given without a time (defaulting to midnight) is treated as
spanning the whole day for the clash check, so it clashes with a timed event
later that same day.

### Command
```
event campout /from 2019-11-01 /to 2019-11-01
```

### Expected output
```
Got it. I've added this task:
  [E][ ] campout (from: Nov 01 2019 to: Nov 01 2019)
Now you have 1 tasks in the list.
```

### Command
```
event lunch /from 2019-11-01 1200 /to 2019-11-01 1300
```

### Expected output
```
This clashes with an existing event:
  [E][ ] campout (from: Nov 01 2019 to: Nov 01 2019)
Add '/force' to the command if you want to schedule it anyway.
```
