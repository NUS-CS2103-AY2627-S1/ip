# UI Test Plan

Test cases for the `test-ui` skill (`.claude/skills/test-ui/`). Each case is
run by feeding **Input** to the program's stdin, one command per line, and
comparing the program's full stdout against **Expected output** exactly.

Every case's input must end with `bye` so the program exits cleanly instead
of hitting end-of-input while still waiting for a command.

The `data/` folder the program persists its task list under (see
`Storage.java`) is reset before each test case's first run, so every case
still starts from an empty task list regardless of what an earlier case
saved. A case may optionally include a second input/expected-output pair
(run as a separate process invocation, without resetting `data/` in
between) to verify behavior across two runs, and/or a "Data file before
run" block to seed the data file with specific content before the first
run -- see `.claude/skills/test-ui/SKILL.md` for the exact format.

When behavior changes (a new command, a changed message, a new class),
update or add test cases here so this file always reflects the program's
actual current behavior.

## Test 1: Greet and exit

**Aim:** The program prints the banner and greeting, then exits cleanly
when the very first command is `bye`.

**Input:**
```text
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 2: Add tasks and list them

**Aim:** `todo <description>` adds a new task and confirms it with
`Added it, let's gooo: ...`; `list` shows every stored task,
numbered from 1, each with a not-done `[ ]` status icon.

**Input:**
```text
todo read book
todo return book
list
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] read book
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] return book
That's 2 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Here's everything on your list:
1.[T][ ] read book
2.[T][ ] return book
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 3: Mark a task as done

**Aim:** `mark <n>` marks the n-th task (1-based) as done, confirms it, and
the change is reflected the next time `list` is run.

**Input:**
```text
todo read book
todo return book
mark 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] read book
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] return book
That's 2 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
YESSS! Marked as done:
  [T][X] return book
____________________________________________________________
____________________________________________________________
Here's everything on your list:
1.[T][ ] read book
2.[T][X] return book
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 4: Unmark a task

**Aim:** `unmark <n>` reverses a task's done status back to not-done and
confirms it.

**Input:**
```text
todo read book
todo return book
mark 1
mark 2
unmark 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] read book
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] return book
That's 2 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
YESSS! Marked as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
YESSS! Marked as done:
  [T][X] return book
____________________________________________________________
____________________________________________________________
Got it, back on the list:
  [T][ ] return book
____________________________________________________________
____________________________________________________________
Here's everything on your list:
1.[T][X] read book
2.[T][ ] return book
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 5: Add a ToDo

**Aim:** `todo <description>` adds a `ToDo`, confirmed with the `[T]` type
icon, a not-done `[ ]` status icon, and the running task count.

**Input:**
```text
todo borrow book
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] borrow book
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 6: Add a Deadline

**Aim:** `deadline <description> /by <yyyy-mm-dd>` adds a `Deadline`,
confirmed with the `[D]` type icon and a `(by: ...)` suffix; the date is
parsed into a `java.time.LocalDate` and displayed in "MMM d yyyy" format.

**Input:**
```text
deadline return book /by 2019-06-06
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [D][ ] return book (by: Jun 6 2019)
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 7: Add an Event

**Aim:** `event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>` adds an
`Event`, confirmed with the `[E]` type icon and a `(from: ... to: ...)`
suffix; both dates are parsed into `java.time.LocalDate` and displayed in
"MMM d yyyy" format.

**Input:**
```text
event project meeting /from 2019-08-06 /to 2019-08-07
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 8: Mixed task types with mark

**Aim:** ToDo, Deadline, and Event tasks can be added, marked done, and
listed together, each keeping its own type icon and detail suffix (matches
the Level-4 requirement's example transcript).

**Input:**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
todo join sports club
mark 1
mark 4
todo borrow book
list
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] read book
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [D][ ] return book (by: Jun 6 2019)
That's 2 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)
That's 3 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] join sports club
That's 4 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
YESSS! Marked as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
YESSS! Marked as done:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] borrow book
That's 5 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Here's everything on your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2019)
3.[E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 9: Empty description and unknown command

**Aim:** `todo` with no description, and any input that doesn't match a
known command (e.g. `blah`), each produce a specific `Oops, ...` error
instead of crashing or silently doing something wrong. Matches the
Level-5 requirement's own example transcript.

**Input:**
```text
todo
blah
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Oops, your to-do needs a description! What are we adding?
____________________________________________________________
____________________________________________________________
Oops, I don't recognize that command! No worries -- give it another shot?
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 10: Malformed deadline/event and bad task numbers

**Aim:** A `deadline` missing `/by`, an `event` missing `/to`, `mark`/`unmark`
with an out-of-range or non-numeric task number all produce specific
error messages, and the program keeps running afterward instead of
crashing.

**Input:**
```text
deadline return book
event project meeting /from 2019-10-04
mark 5
unmark abc
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Oops, a deadline needs a description and a '/by' date! Try: deadline return book /by 2019-12-02.
____________________________________________________________
____________________________________________________________
Oops, an event needs a description, a '/from' date, and a '/to' date! Try: event project meeting /from 2019-10-04 /to 2019-10-11.
____________________________________________________________
____________________________________________________________
Oops, there's no task number 5 in your list!
____________________________________________________________
____________________________________________________________
Oops, 'abc' isn't a valid task number! Numbers only, please.
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 11: Delete a task

**Aim:** `delete <n>` removes the n-th task, confirms it with the removed
task's own display text and the updated count, and the remaining tasks
shift down and renumber correctly in `list`. Matches the Level-6
requirement's example transcript.

**Input:**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
mark 1
mark 2
delete 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] read book
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [D][ ] return book (by: Jun 6 2019)
That's 2 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)
That's 3 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
YESSS! Marked as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
YESSS! Marked as done:
  [D][X] return book (by: Jun 6 2019)
____________________________________________________________
____________________________________________________________
Poof, gone! Removed:
  [D][X] return book (by: Jun 6 2019)
Down to 2 tasks -- look at you go!
____________________________________________________________
____________________________________________________________
Here's everything on your list:
1.[T][X] read book
2.[E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 12: Delete errors reuse task-number validation

**Aim:** `delete` with a missing, non-numeric, or out-of-range task number
produces the same specific errors as `mark`/`unmark` (they share the
`parseTaskNumber` helper), and the task list is left untouched.

**Input:**
```text
todo x
delete
delete abc
delete 0
delete 99
list
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] x
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Oops, I need a task number for that! Try something like mark 2.
____________________________________________________________
____________________________________________________________
Oops, 'abc' isn't a valid task number! Numbers only, please.
____________________________________________________________
____________________________________________________________
Oops, there's no task number 0 in your list!
____________________________________________________________
____________________________________________________________
Oops, there's no task number 99 in your list!
____________________________________________________________
____________________________________________________________
Here's everything on your list:
1.[T][ ] x
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 13: Tasks persist across separate runs

**Aim:** Tasks added and marked in one run of the program are saved to
disk, and a completely separate run of the program (started fresh, no
in-memory state carried over) loads them back via `list`.

**Input:**
```text
todo read book
deadline return book /by 2019-06-06
mark 1
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] read book
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [D][ ] return book (by: Jun 6 2019)
That's 2 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
YESSS! Marked as done:
  [T][X] read book
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

**Second input:**
```text
list
bye
```

**Second expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Here's everything on your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2019)
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 14: Corrupted data file lines are skipped, not crashed on

**Aim:** A pre-existing data file containing a line that doesn't parse at
all, a line with an invalid status field, and a line with a date that
isn't valid `yyyy-mm-dd` (e.g. saved under the pre-Level-8 free-text
format) are each skipped with a warning printed to the console;
well-formed lines -- including ones with real dates -- in the same file
still load correctly (stretch goal from the Level 7 requirement, and
proof that Level 8's date parsing degrades the same way on bad data).

**Data file before run:**
```text
T | 1 | read book
NOT A VALID LINE
T | X | bad status
D | 0 | return book | June 6th
D | 0 | good deadline | 2019-12-02
E | 0 | trip | 2019-10-04 | 2019-10-11
```

**Input:**
```text
list
bye
```

**Expected output:**
```text
Warning: skipping corrupted line in data file: NOT A VALID LINE
Warning: skipping corrupted line in data file: T | X | bad status
Warning: skipping corrupted line in data file: D | 0 | return book | June 6th
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Here's everything on your list:
1.[T][X] read book
2.[D][ ] good deadline (by: Dec 2 2019)
3.[E][ ] trip (from: Oct 4 2019 to: Oct 11 2019)
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 15: Invalid dates are rejected with a field-specific error

**Aim:** A deadline's `/by` date, or an event's `/from`/`/to` date, that
isn't valid `yyyy-mm-dd` produces a field-specific error message (not the
generic "don't know what that means" error) and the task is not added to
the list.

**Input:**
```text
deadline return book /by Sunday
event trip /from 2019-10-04 /to notadate
list
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Oops, give the '/by' date as yyyy-mm-dd! Like 2019-12-02.
____________________________________________________________
____________________________________________________________
Oops, give the '/to' date as yyyy-mm-dd! Like 2019-10-11.
____________________________________________________________
____________________________________________________________
Here's everything on your list:
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 16: `on` shows tasks occurring on a date

**Aim:** `on <yyyy-mm-dd>` lists only the deadlines/events that occur on
that date -- a `Deadline` matches its exact `/by` date, an `Event` matches
any date within its `/from`-`/to` range inclusive (both endpoints and the
middle), and a `ToDo` never matches since it has no date. A date with no
matches gets a friendly "no tasks" message instead of an empty list
(stretch goal from the Level 8 requirement).

**Input:**
```text
todo just a todo
deadline return book /by 2019-12-02
event trip /from 2019-10-04 /to 2019-10-11
on 2019-10-07
on 2019-10-04
on 2019-01-01
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] just a todo
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [D][ ] return book (by: Dec 2 2019)
That's 2 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [E][ ] trip (from: Oct 4 2019 to: Oct 11 2019)
That's 3 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Here's what's happening on Oct 7 2019:
1.[E][ ] trip (from: Oct 4 2019 to: Oct 11 2019)
____________________________________________________________
____________________________________________________________
Here's what's happening on Oct 4 2019:
1.[E][ ] trip (from: Oct 4 2019 to: Oct 11 2019)
____________________________________________________________
____________________________________________________________
Nothing going on Jan 1 2019 -- nice and clear!
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 17: `on` errors on a missing or invalid date

**Aim:** `on` with no date, or a date that isn't valid `yyyy-mm-dd`,
produces a specific error message instead of crashing or silently doing
nothing.

**Input:**
```text
on
on notadate
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Oops, which date? Try: on 2019-12-02.
____________________________________________________________
____________________________________________________________
Oops, give the date as yyyy-mm-dd! Like on 2019-12-02.
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 18: `find` shows tasks whose description contains a keyword

**Aim:** `find <keyword>` lists only the tasks whose description contains
that keyword (case-insensitive), across all task types, keeping each
task's own status icon, type icon, and detail suffix. A keyword with no
matches gets a friendly "no matching tasks" message instead of an empty
list. Matches the Level-9 requirement's own example transcript.

**Input:**
```text
todo read book
deadline return book /by 2019-06-06
todo join sports club
mark 1
mark 2
find book
find sports
find nonexistent
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] read book
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [D][ ] return book (by: Jun 6 2019)
That's 2 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] join sports club
That's 3 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
YESSS! Marked as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
YESSS! Marked as done:
  [D][X] return book (by: Jun 6 2019)
____________________________________________________________
____________________________________________________________
Found these matches for you:
1.[T][X] read book
2.[D][X] return book (by: Jun 6 2019)
____________________________________________________________
____________________________________________________________
Found these matches for you:
1.[T][ ] join sports club
____________________________________________________________
____________________________________________________________
Hmm, no matches in your list -- but don't stop now!
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 19: `find` errors on a missing keyword

**Aim:** `find` with no keyword produces a specific error message instead
of crashing or silently doing nothing.

**Input:**
```text
find
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Oops, what should I search for? Try: find book.
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 20: `schedule` shows deadlines and events in chronological order

**Aim:** `schedule` lists every deadline and event across the whole task
list -- not just one date, unlike `on` -- sorted earliest first regardless
of the order they were added in. A `ToDo` never appears, since it has no
date. (B-ViewSchedules extension.)

**Input:**
```text
todo just a todo
deadline return book /by 2019-12-02
event trip /from 2019-10-04 /to 2019-10-11
deadline earlier task /by 2019-09-01
schedule
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] just a todo
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [D][ ] return book (by: Dec 2 2019)
That's 2 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [E][ ] trip (from: Oct 4 2019 to: Oct 11 2019)
That's 3 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [D][ ] earlier task (by: Sep 1 2019)
That's 4 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Here's your schedule, all lined up:
1.[D][ ] earlier task (by: Sep 1 2019)
2.[E][ ] trip (from: Oct 4 2019 to: Oct 11 2019)
3.[D][ ] return book (by: Dec 2 2019)
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 21: `schedule` with no deadlines or events

**Aim:** `schedule` shows a friendly message instead of an empty list
when there are no dated tasks, matching a plain `ToDo` never appearing.

**Input:**
```text
todo just a todo
schedule
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] just a todo
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Your schedule's wide open -- blank canvas energy!
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 22: `schedule <date>` reuses `on`'s display for a single date

**Aim:** `schedule <yyyy-mm-dd>` filters to just that date, showing the
exact same message as `on <yyyy-mm-dd>` -- both a match and a no-match
case -- since viewing the schedule for one date and asking what's on
that date are the same question. (B-ViewSchedules extension.)

**Input:**
```text
event trip /from 2019-10-04 /to 2019-10-11
schedule 2019-10-07
schedule 2019-01-01
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [E][ ] trip (from: Oct 4 2019 to: Oct 11 2019)
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Here's what's happening on Oct 7 2019:
1.[E][ ] trip (from: Oct 4 2019 to: Oct 11 2019)
____________________________________________________________
____________________________________________________________
Nothing going on Jan 1 2019 -- nice and clear!
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 23: `schedule` errors on an invalid date

**Aim:** `schedule <text>` with an unparseable date produces a
schedule-specific error message (mentioning `schedule`, not `on`, in its
example) instead of crashing or silently doing nothing.

**Input:**
```text
schedule notadate
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Oops, give the date as yyyy-mm-dd! Like schedule 2019-12-02.
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 24: `help` shows the full command list

**Aim:** `help` prints the full list of available commands with their
usage and description, matching what used to be dumped automatically
at startup -- the greeting itself now only points the user at `help`
instead of listing every command.

**Input:**
```text
help
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Here's everything I can do:

• help
   Show this list of commands again!

• todo <description>
   Add a to-do task!

• deadline <description> /by <yyyy-mm-dd>
   Add a task with a deadline!

• event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
   Add an event!

• on <yyyy-mm-dd>
   Show tasks happening on a date!

• find <keyword>
   Find tasks matching a keyword!

• schedule [yyyy-mm-dd]
   Show your whole schedule, or just one date's!

• list
   Show off your whole task list!

• mark <task number>
   Mark a task as done!

• unmark <task number>
   Mark a task as not done yet!

• delete <task number>
   Clear a task off your list!

• bye
   Wrap up for now!
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 25: Leading and trailing whitespace are tolerated

**Aim:** Leading whitespace before the command word (which used to make
`getCommandWord` see an empty word and report "unrecognized command"),
and trailing whitespace after a command, are trimmed away instead of
breaking a perfectly valid command.

**Input:**
```text
   todo read book
list   
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [T][ ] read book
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Here's everything on your list:
1.[T][ ] read book
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 26: Descriptions can't contain reserved characters

**Aim:** A description containing '|' would silently corrupt that task
(and mis-align every task saved after it) the next time the list is
saved to disk, since `Storage` uses " | " to separate a saved task's
fields. `todo`, `deadline`, and `event` each reject it with a clear
error instead of adding a task that will break on reload; none of the
three end up on the list.

**Input:**
```text
todo buy milk | eggs
deadline pay | rent /by 2020-01-01
event trip | home /from 2020-01-01 /to 2020-01-02
list
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Oops, descriptions can't contain '|' or line breaks -- I use those behind the scenes to save your tasks!
____________________________________________________________
____________________________________________________________
Oops, descriptions can't contain '|' or line breaks -- I use those behind the scenes to save your tasks!
____________________________________________________________
____________________________________________________________
Oops, descriptions can't contain '|' or line breaks -- I use those behind the scenes to save your tasks!
____________________________________________________________
____________________________________________________________
Here's everything on your list:
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 27: An event can't end before it starts

**Aim:** `event ... /from X /to Y` is rejected when Y is before X, but
a single-day event (X equal to Y) is accepted -- Eve only tracks whole
dates, not times, so a same-day event is completely normal, not a
zero-duration error.

**Input:**
```text
event trip /from 2020-05-10 /to 2020-05-01
event day trip /from 2020-05-10 /to 2020-05-10
list
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Oops, that event ends before it starts! Double check your '/from' and '/to'.
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [E][ ] day trip (from: May 10 2020 to: May 10 2020)
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Here's everything on your list:
1.[E][ ] day trip (from: May 10 2020 to: May 10 2020)
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 28: Adding an exact duplicate task is rejected

**Aim:** Adding a task identical in type, description, and date(s) to
one already on the list is rejected instead of silently creating a
second copy. A task that merely shares a description with an existing
one, but differs in date, is not treated as a duplicate.

**Input:**
```text
deadline pay rent /by 2020-01-01
deadline pay rent /by 2020-01-01
deadline pay rent /by 2020-02-01
list
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [D][ ] pay rent (by: Jan 1 2020)
That's 1 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Oops, that's already on your list! No need to add it twice.
____________________________________________________________
____________________________________________________________
Added it, let's gooo:
  [D][ ] pay rent (by: Feb 1 2020)
That's 2 tasks -- you're basically unstoppable!
____________________________________________________________
____________________________________________________________
Here's everything on your list:
1.[D][ ] pay rent (by: Jan 1 2020)
2.[D][ ] pay rent (by: Feb 1 2020)
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 29: A repeated date marker is rejected

**Aim:** `deadline ... /by X /by Y` and `event ... /from X /from Y ...`
(or a repeated `/to`) are each rejected with a message naming the
repeated marker, instead of trying to parse the leftover text as part
of the date and failing with a generic "invalid date" error.

**Input:**
```text
deadline task /by 2020-01-01 /by 2020-02-02
event trip /from 2020-01-01 /from 2020-01-02 /to 2020-01-03
event trip /from 2020-01-01 /to 2020-01-02 /to 2020-01-03
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Oops, I only need one '/by' date -- you've given me two!
____________________________________________________________
____________________________________________________________
Oops, I only need one '/from' date -- you've given me two!
____________________________________________________________
____________________________________________________________
Oops, I only need one '/to' date -- you've given me two!
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```

## Test 30: Adjacent `/from`/`/to` with no date between them doesn't crash

**Aim:** `event ... /from /to X` (no from-date text between the two
markers) is rejected with the normal "fill in ... all of them" error
instead of crashing with a raw `StringIndexOutOfBoundsException` --
the two markers end up adjacent, which earlier made the from-date
extraction compute a start index past its end index.

**Input:**
```text
event trip /from /to 2019-10-11
bye
```

**Expected output:**
```text
____________________________________________________________
*********************
***     E V E     ***
*********************

HEYYY! I'm Eve!
I'm SO ready to help you crush your to-do list today! What's first?

(Type help anytime to see everything I can do!)
____________________________________________________________
____________________________________________________________
Oops, fill in the event's description, '/from' date, and '/to' date -- all of them!
____________________________________________________________
Byeee! Go crush it out there -- see you again soon!
____________________________________________________________
```
