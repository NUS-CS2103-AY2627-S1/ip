# EV UI Test Plan

Text-UI regression tests for EV. Each test case starts a fresh `EV` process, feeds it the
listed input lines, and compares the console output against the expected output.

## How to run

```powershell
.\test\run-ui-tests.ps1
```

Options:

- `.\test\run-ui-tests.ps1 -Filter TC-13` runs a single case (prefix match, so `-Filter TC-1` runs TC-10 to TC-19).
- `.\test\run-ui-tests.ps1 -Quiet` prints only the pass/fail summary instead of the full session transcript.

The runner compiles every `.java` file under `src\main\java\` into `out\` and runs the `ev.EV`
class, so there is no need to compile by hand first. It stops at the first failing case and prints the expected and actual
output side by side.

## Conventions

Every session starts with the banner and greeting, and ends with the farewell:

```text
 _______     __
|   ____|   /  \
|  |__     |    |
|   __|    |    |
|  |____    \  /
|_______|    \/

____________________________________________________________
Hi Peter.
EV online. What do you need?
____________________________________________________________
...
____________________________________________________________
Signing off, Peter.
____________________________________________________________
```

These three fixed parts are checked by the runner on every case, so the **Expected output**
block of each case below covers only the replies in between. This keeps the test cases short
and means a change to the greeting does not require editing all of them.

Every input block ends with `bye` so the session terminates.

A case may also declare the saved data file. **Data file before** seeds `data/ev.txt` in the
case's own working folder before the session starts; **Data file after** is compared against the
file once the session ends, and the single line `(no file)` means no file should exist. Each case
runs in a fresh working folder, so cases never see each other's saved tasks.

## Test cases

### TC-01 Greet and exit

**Aim:** A session with no commands produces exactly the banner, greeting and farewell, and
nothing else.

**Input**

```text
bye
```

**Expected output**

```text
```

### TC-02 Add a todo

**Aim:** `todo` creates a task shown with the `[T]` type icon and an unticked status box, and
the running count is reported in the singular.

**Input**

```text
todo borrow book
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] borrow book
1 task.
____________________________________________________________
```

### TC-03 Add a deadline

**Aim:** `deadline ... /by ...` creates a `[D]` task and echoes the due time in the
`(by: ...)` suffix.

**Input**

```text
deadline return book /by 2019-06-06
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [D][ ] return book (by: Jun 6 2019)
1 task.
____________________________________________________________
```

### TC-04 Add an event

**Aim:** `event ... /from ... /to ...` creates an `[E]` task and echoes both times in the
`(from: ... to: ...)` suffix.

**Input**

```text
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [E][ ] project meeting (from: Aug 6 2019, 2:00 PM to: Aug 6 2019, 4:00 PM)
1 task.
____________________________________________________________
```

### TC-05 Dates are understood and reprinted in a fixed format

**Aim:** `/by`, `/from` and `/to` are real dates, not free text. Both accepted input formats
(`yyyy-MM-dd` and `d/M/yyyy`, each with an optional `HHmm` time) are printed back the same way:
`MMM d yyyy` when only a date was given, and `MMM d yyyy, h:mm a` when a time was given too.

**Input**

```text
deadline return book /by 2/12/2019 1800
deadline pay rent /by 2019-10-15
event trip /from 1/12/2019 /to 2019-12-03 1030
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [D][ ] return book (by: Dec 2 2019, 6:00 PM)
1 task.
____________________________________________________________
____________________________________________________________
Added.
  [D][ ] pay rent (by: Oct 15 2019)
2 tasks.
____________________________________________________________
____________________________________________________________
Added.
  [E][ ] trip (from: Dec 1 2019 to: Dec 3 2019, 10:30 AM)
3 tasks.
____________________________________________________________
____________________________________________________________
Your list:
1.[D][ ] return book (by: Dec 2 2019, 6:00 PM)
2.[D][ ] pay rent (by: Oct 15 2019)
3.[E][ ] trip (from: Dec 1 2019 to: Dec 3 2019, 10:30 AM)
____________________________________________________________
```

### TC-06 List an empty list

**Aim:** `list` on a fresh session reports that the list is empty rather than printing an empty
frame.

**Input**

```text
list
bye
```

**Expected output**

```text
____________________________________________________________
Nothing on your list.
____________________________________________________________
```

### TC-07 List all three task types

**Aim:** `list` numbers tasks from 1 in insertion order and renders each subclass with its own
type icon and suffix.

**Input**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Added.
  [D][ ] return book (by: Jun 6 2019)
2 tasks.
____________________________________________________________
____________________________________________________________
Added.
  [E][ ] project meeting (from: Aug 6 2019, 2:00 PM to: Aug 6 2019, 4:00 PM)
3 tasks.
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 6 2019)
3.[E][ ] project meeting (from: Aug 6 2019, 2:00 PM to: Aug 6 2019, 4:00 PM)
____________________________________________________________
```

### TC-08 Mark a task as done

**Aim:** `mark 2` ticks the second task only, and the change is visible in a later `list`.
Guards against off-by-one errors in the 1-based to 0-based index conversion.

**Input**

```text
todo read book
todo return book
mark 2
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Added.
  [T][ ] return book
2 tasks.
____________________________________________________________
____________________________________________________________
Done.
  [T][X] return book
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] read book
2.[T][X] return book
____________________________________________________________
```

### TC-09 Unmark a task

**Aim:** `unmark` reverses `mark` and restores the empty status box.

**Input**

```text
todo read book
mark 1
unmark 1
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Done.
  [T][X] read book
____________________________________________________________
____________________________________________________________
Back to not done.
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] read book
____________________________________________________________
```

### TC-10 Marking twice is harmless

**Aim:** Marking an already-done task leaves it done instead of toggling it back off.

**Input**

```text
todo read book
mark 1
mark 1
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Done.
  [T][X] read book
____________________________________________________________
____________________________________________________________
Done.
  [T][X] read book
____________________________________________________________
____________________________________________________________
Your list:
1.[T][X] read book
____________________________________________________________
```

### TC-11 Unknown command

**Aim:** An unrecognised keyword is rejected with the offending word quoted and the list of
supported commands.

**Input**

```text
blah
bye
```

**Expected output**

```text
____________________________________________________________
No such command: "blah"
I take: todo, deadline, event, list, on, find, mark, unmark, delete, update, bye.
____________________________________________________________
```

### TC-12 Todo without a description

**Aim:** `todo` with no argument is rejected with an example of correct usage.

**Input**

```text
todo
bye
```

**Expected output**

```text
____________________________________________________________
A todo needs a description.
e.g. todo borrow book
____________________________________________________________
```

### TC-13 Malformed deadline

**Aim:** The three ways a `deadline` can be incomplete each produce their own specific message
rather than one generic error: no `/by` at all, nothing before `/by`, nothing after `/by`.

**Input**

```text
deadline
deadline /by 2019-12-02
deadline return book /by
bye
```

**Expected output**

```text
____________________________________________________________
A deadline needs /by.
e.g. deadline return book /by 2019-12-02 1800
____________________________________________________________
____________________________________________________________
Missing description before /by.
e.g. deadline return book /by 2019-12-02 1800
____________________________________________________________
____________________________________________________________
Missing date after /by.
e.g. deadline return book /by 2019-12-02 1800
____________________________________________________________
```

### TC-14 Malformed event

**Aim:** Each way an `event` can be malformed produces its own message: no `/from`, no `/to`,
`/to` written before `/from`, and no description.

**Input**

```text
event
event meeting /from 2019-12-02 1400
event meeting /to 2019-12-02 1600 /from 2019-12-02 1400
event /from 2019-12-02 1400 /to 2019-12-02 1600
bye
```

**Expected output**

```text
____________________________________________________________
An event needs /from.
e.g. event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
____________________________________________________________
____________________________________________________________
An event needs /to.
e.g. event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
____________________________________________________________
____________________________________________________________
/from must come before /to.
e.g. event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
____________________________________________________________
____________________________________________________________
Missing description before /from.
e.g. event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
____________________________________________________________
```

### TC-15 Invalid task numbers for mark

**Aim:** Marking is rejected when the list is empty, when no number is given, when the argument
is not a number, and when the number is out of range. The out-of-range message states the valid
range.

**Input**

```text
mark 1
todo read book
mark
mark abc
mark 5
bye
```

**Expected output**

```text
____________________________________________________________
Your list is empty.
____________________________________________________________
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Which task?
e.g. mark 2
____________________________________________________________
____________________________________________________________
"abc" is not a number.
e.g. mark 2
____________________________________________________________
____________________________________________________________
No task 5.
Pick between 1 and 1.
____________________________________________________________
```

### TC-16 Invalid task numbers for unmark

**Aim:** `unmark` validates its argument the same way `mark` does, including rejecting task
number 0 (the list is 1-based).

**Input**

```text
todo read book
unmark abc
unmark 0
unmark 1
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
"abc" is not a number.
e.g. mark 2
____________________________________________________________
____________________________________________________________
No task 0.
Pick between 1 and 1.
____________________________________________________________
____________________________________________________________
Back to not done.
  [T][ ] read book
____________________________________________________________
```

### TC-17 Failed commands do not corrupt the list

**Aim:** Interleave rejected commands with successful ones. The closing `list` must contain
exactly the two tasks that were added successfully, numbered 1 and 2, proving that a rejected
command left no half-built task behind and did not advance the task counter.

**Input**

```text
todo read book
blah
deadline oops
mark 9
deadline return book /by 2019-06-06
todo
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
No such command: "blah"
I take: todo, deadline, event, list, on, find, mark, unmark, delete, update, bye.
____________________________________________________________
____________________________________________________________
A deadline needs /by.
e.g. deadline return book /by 2019-12-02 1800
____________________________________________________________
____________________________________________________________
No task 9.
Pick between 1 and 1.
____________________________________________________________
____________________________________________________________
Added.
  [D][ ] return book (by: Jun 6 2019)
2 tasks.
____________________________________________________________
____________________________________________________________
A todo needs a description.
e.g. todo borrow book
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 6 2019)
____________________________________________________________
```

### TC-18 Blank input lines are ignored

**Aim:** Pressing Enter on an empty line produces no reply and does not create an empty task.

**Input**

```text

todo read book


list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] read book
____________________________________________________________
```

### TC-19 Descriptions that look like commands

**Aim:** Only the first word of a line is treated as the command keyword, so a description that
happens to be or start with a command word is stored as ordinary text.

**Input**

```text
todo list
todo unmark the thing
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] list
1 task.
____________________________________________________________
____________________________________________________________
Added.
  [T][ ] unmark the thing
2 tasks.
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] list
2.[T][ ] unmark the thing
____________________________________________________________
```

### TC-20 Surrounding and repeated whitespace is trimmed

**Aim:** Leading, trailing and repeated spaces around the command and its argument are removed
before the task is stored.

**Input**

```text
   todo    borrow book   
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] borrow book
1 task.
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] borrow book
____________________________________________________________
```

### TC-21 Delete a task from the middle

**Aim:** `delete 3` removes the third task, reports it, and the remaining tasks close the gap so
that `list` numbers them 1 to 3 with no hole.

**Input**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo join sports club
delete 3
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Added.
  [D][ ] return book (by: Jun 6 2019)
2 tasks.
____________________________________________________________
____________________________________________________________
Added.
  [E][ ] project meeting (from: Aug 6 2019, 2:00 PM to: Aug 6 2019, 4:00 PM)
3 tasks.
____________________________________________________________
____________________________________________________________
Added.
  [T][ ] join sports club
4 tasks.
____________________________________________________________
____________________________________________________________
Removed.
  [E][ ] project meeting (from: Aug 6 2019, 2:00 PM to: Aug 6 2019, 4:00 PM)
3 tasks.
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 6 2019)
3.[T][ ] join sports club
____________________________________________________________
```

### TC-22 Invalid task numbers for delete

**Aim:** `delete` validates its argument the same way `mark` and `unmark` do: empty list, no
number, non-numeric argument, and out of range.

**Input**

```text
delete 1
todo read book
delete
delete abc
delete 2
bye
```

**Expected output**

```text
____________________________________________________________
Your list is empty.
____________________________________________________________
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Which task?
e.g. mark 2
____________________________________________________________
____________________________________________________________
"abc" is not a number.
e.g. mark 2
____________________________________________________________
____________________________________________________________
No task 2.
Pick between 1 and 1.
____________________________________________________________
```

### TC-23 Deleting keeps the remaining tasks intact

**Aim:** The most dangerous part of delete is shifting the surviving tasks. Mark the last task,
delete the first, then check that (a) the marked task is still marked after moving position,
(b) `mark 2` now targets the shifted task rather than the old occupant of that slot, and (c) a
task added afterwards lands at the end instead of overwriting a survivor.

**Input**

```text
todo a
todo b
todo c
mark 3
delete 1
list
mark 2
list
todo d
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] a
1 task.
____________________________________________________________
____________________________________________________________
Added.
  [T][ ] b
2 tasks.
____________________________________________________________
____________________________________________________________
Added.
  [T][ ] c
3 tasks.
____________________________________________________________
____________________________________________________________
Done.
  [T][X] c
____________________________________________________________
____________________________________________________________
Removed.
  [T][ ] a
2 tasks.
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] b
2.[T][X] c
____________________________________________________________
____________________________________________________________
Done.
  [T][X] c
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] b
2.[T][X] c
____________________________________________________________
____________________________________________________________
Added.
  [T][ ] d
3 tasks.
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] b
2.[T][X] c
3.[T][ ] d
____________________________________________________________
```

### TC-24 Delete the only task

**Aim:** Deleting the last remaining task reports a count of zero and leaves the list genuinely
empty, not holding a stale reference to the removed task.

**Input**

```text
todo only task
delete 1
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] only task
1 task.
____________________________________________________________
____________________________________________________________
Removed.
  [T][ ] only task
0 tasks.
____________________________________________________________
____________________________________________________________
Nothing on your list.
____________________________________________________________
```

## Not covered

- Large lists. There is no longer a fixed capacity now that tasks are held in an `ArrayList`, so
  there is no "list is full" branch to cover. A case with hundreds of setup commands would
  dominate the plan without testing any logic the smaller cases miss.
- Interactive behaviour such as Ctrl+C, and terminal-specific rendering of the banner.

### TC-25 Tasks are saved as they are added and marked

**Aim:** Every change to the list is written to `data/ev.txt` immediately, one line per task,
in the order the tasks appear in the list.

**Input**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
mark 1
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Added.
  [D][ ] return book (by: Jun 6 2019)
2 tasks.
____________________________________________________________
____________________________________________________________
Added.
  [E][ ] project meeting (from: Aug 6 2019, 2:00 PM to: Aug 6 2019, 4:00 PM)
3 tasks.
____________________________________________________________
____________________________________________________________
Done.
  [T][X] read book
____________________________________________________________
```

**Data file after**

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00
E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

### TC-26 Saved tasks are loaded at startup

**Aim:** A session that starts with an existing data file begins with those tasks already in the
list, with their done status, type and times restored. A session that only reads the list leaves
the file untouched.

**Data file before**

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00
E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

**Input**

```text
list
bye
```

**Expected output**

```text
____________________________________________________________
Your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2019)
3.[E][ ] project meeting (from: Aug 6 2019, 2:00 PM to: Aug 6 2019, 4:00 PM)
____________________________________________________________
```

**Data file after**

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00
E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

### TC-27 Deleting and unmarking are saved too

**Aim:** `delete` and `unmark` rewrite the file as well, so the saved list always matches the list
in memory.

**Data file before**

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00
E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

**Input**

```text
delete 2
unmark 1
bye
```

**Expected output**

```text
____________________________________________________________
Removed.
  [D][ ] return book (by: Jun 6 2019)
2 tasks.
____________________________________________________________
____________________________________________________________
Back to not done.
  [T][ ] read book
____________________________________________________________
```

**Data file after**

```text
T | 0 | read book
E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

### TC-28 A missing data file is not an error

**Aim:** The first run on a new computer has neither the `data` folder nor the file. EV must start
with an empty list, say nothing about it, and not create the file until there is something to save.

**Input**

```text
list
bye
```

**Expected output**

```text
____________________________________________________________
Nothing on your list.
____________________________________________________________
```

**Data file after**

```text
(no file)
```

### TC-29 Corrupted lines are reported and dropped

**Aim:** Lines that are not in the expected format (unknown type letter, status that is neither 0
nor 1, wrong number of fields, a due date that is not a saved date) are counted and skipped instead of crashing EV. The readable tasks
still load, and the next change rewrites the file without the bad lines.

**Data file before**

```text
T | 1 | read book
X | 0 | mystery task
D | 2 | return book | 2019-06-06T00:00
T | 0 |
D | 0 | return book | June 6th
E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

**Input**

```text
todo water plants
bye
```

**Expected output**

```text
____________________________________________________________
Skipped 4 unreadable line(s) in data\ev.txt.
The rest loaded. Next change rewrites the file.
____________________________________________________________
____________________________________________________________
Added.
  [T][ ] water plants
3 tasks.
____________________________________________________________
```

**Data file after**

```text
T | 1 | read book
E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
T | 0 | water plants
```

### TC-30 Dates that cannot be understood are rejected

**Aim:** Free text, an impossible date and a date-shaped phrase are all refused with the list of
accepted formats, and the task is not added. Guards against a bad date silently becoming a task
with a wrong or missing time.

**Input**

```text
deadline do homework /by no idea :-p
deadline do homework /by 2019-13-45
event trip /from 2019-12-01 /to next week
list
bye
```

**Expected output**

```text
____________________________________________________________
Not a date: "no idea :-p"
Use 2019-12-02, 2019-12-02 1800, 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
Not a date: "2019-13-45"
Use 2019-12-02, 2019-12-02 1800, 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
Not a date: "next week"
Use 2019-12-02, 2019-12-02 1800, 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
Nothing on your list.
____________________________________________________________
```

**Data file after**

```text
(no file)
```

### TC-31 List what happens on a given date

**Aim:** `on` shows the deadlines due on that date and the events that span it, keeping each
task's number from the full list so it can be marked or deleted straight away. Todos have no date
and never appear. The date argument accepts the same formats as the other commands.

**Input**

```text
deadline return book /by 2019-12-02 1800
todo read book
event project meeting /from 2019-12-01 1400 /to 2019-12-03 1600
on 2/12/2019
on 2020-01-01
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [D][ ] return book (by: Dec 2 2019, 6:00 PM)
1 task.
____________________________________________________________
____________________________________________________________
Added.
  [T][ ] read book
2 tasks.
____________________________________________________________
____________________________________________________________
Added.
  [E][ ] project meeting (from: Dec 1 2019, 2:00 PM to: Dec 3 2019, 4:00 PM)
3 tasks.
____________________________________________________________
____________________________________________________________
On Dec 2 2019:
1.[D][ ] return book (by: Dec 2 2019, 6:00 PM)
3.[E][ ] project meeting (from: Dec 1 2019, 2:00 PM to: Dec 3 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
Nothing on Jan 1 2020.
____________________________________________________________
```

### TC-32 The date of an event's first and last day counts

**Aim:** An event that runs over several days is reported on its first day, its last day and the
days in between, but not on the day before or the day after. Guards against an off-by-one in the
range check.

**Input**

```text
event camp /from 2019-12-01 0900 /to 2019-12-03 1700
on 2019-11-30
on 2019-12-01
on 2019-12-02
on 2019-12-03
on 2019-12-04
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)
1 task.
____________________________________________________________
____________________________________________________________
Nothing on Nov 30 2019.
____________________________________________________________
____________________________________________________________
On Dec 1 2019:
1.[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)
____________________________________________________________
____________________________________________________________
On Dec 2 2019:
1.[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)
____________________________________________________________
____________________________________________________________
On Dec 3 2019:
1.[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)
____________________________________________________________
____________________________________________________________
Nothing on Dec 4 2019.
____________________________________________________________
```

### TC-33 `on` validates its argument

**Aim:** `on` without a date, and `on` with something that is not a date, are rejected the same
way as the other commands rather than listing everything or nothing.

**Input**

```text
on
on someday
bye
```

**Expected output**

```text
____________________________________________________________
Which date?
e.g. on 2019-12-02
____________________________________________________________
____________________________________________________________
Not a date: "someday"
Use 2019-12-02, 2019-12-02 1800, 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
```

### TC-34 Find tasks by a word in the description

**Aim:** `find` shows every task whose description contains the word, whatever its type or
status, and keeps each task's number from the full list so it can be marked or deleted straight
away. Tasks that do not match are left out.

**Input**

```text
todo read book
deadline return book /by 2019-06-06
todo water plants
mark 2
find book
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Added.
  [D][ ] return book (by: Jun 6 2019)
2 tasks.
____________________________________________________________
____________________________________________________________
Added.
  [T][ ] water plants
3 tasks.
____________________________________________________________
____________________________________________________________
Done.
  [D][X] return book (by: Jun 6 2019)
____________________________________________________________
____________________________________________________________
Matches:
1.[T][ ] read book
2.[D][X] return book (by: Jun 6 2019)
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] read book
2.[D][X] return book (by: Jun 6 2019)
3.[T][ ] water plants
____________________________________________________________
```

### TC-35 Finding is case insensitive and matches part of a word

**Aim:** The search is on the description only, ignores capitalisation, and matches a word part
so that `boo` finds `book`. A word that appears only in a date is not a match, since dates are no
longer stored as text.

**Input**

```text
todo Read Book
event December trip /from 2019-12-01 /to 2019-12-03
find boo
find december
find Dec
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] Read Book
1 task.
____________________________________________________________
____________________________________________________________
Added.
  [E][ ] December trip (from: Dec 1 2019 to: Dec 3 2019)
2 tasks.
____________________________________________________________
____________________________________________________________
Matches:
1.[T][ ] Read Book
____________________________________________________________
____________________________________________________________
Matches:
2.[E][ ] December trip (from: Dec 1 2019 to: Dec 3 2019)
____________________________________________________________
____________________________________________________________
Matches:
2.[E][ ] December trip (from: Dec 1 2019 to: Dec 3 2019)
____________________________________________________________
```

### TC-36 Finding nothing, and finding without a keyword

**Aim:** A search that matches nothing says so instead of printing an empty list, and `find`
with no keyword is rejected with an example rather than listing everything.

**Input**

```text
todo read book
find plants
find
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
No match for "plants".
____________________________________________________________
____________________________________________________________
Search for what?
e.g. find book
____________________________________________________________
```

### TC-37 Update a task without deleting it

**Aim:** `update` changes exactly one detail of an existing task. The done status, the other
fields and the other tasks are all left alone, and the task keeps its number in the list.

**Input**

```text
todo read book
deadline return book /by 2019-06-06
mark 1
update 2 /by 2019-12-05 1800
update 1 /desc read the whole book
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Added.
  [D][ ] return book (by: Jun 6 2019)
2 tasks.
____________________________________________________________
____________________________________________________________
Done.
  [T][X] read book
____________________________________________________________
____________________________________________________________
Updated.
  [D][ ] return book (by: Dec 5 2019, 6:00 PM)
____________________________________________________________
____________________________________________________________
Updated.
  [T][X] read the whole book
____________________________________________________________
____________________________________________________________
Your list:
1.[T][X] read the whole book
2.[D][ ] return book (by: Dec 5 2019, 6:00 PM)
____________________________________________________________
```

### TC-38 Update the two ends of an event separately

**Aim:** `/from` and `/to` can be changed one at a time without disturbing each other, and the
new times are shown in the usual display format.

**Input**

```text
event camp /from 2019-12-01 0900 /to 2019-12-03 1700
update 1 /to 2019-12-04 1700
update 1 /from 2019-11-30 0800
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)
1 task.
____________________________________________________________
____________________________________________________________
Updated.
  [E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 4 2019, 5:00 PM)
____________________________________________________________
____________________________________________________________
Updated.
  [E][ ] camp (from: Nov 30 2019, 8:00 AM to: Dec 4 2019, 5:00 PM)
____________________________________________________________
```

### TC-39 Updates that are refused leave the task alone

**Aim:** An option the task type does not have, a missing value, two options at once and an
unreadable date are each refused with their own message. The closing `list` shows that none of
them changed anything.

**Input**

```text
todo read book
deadline pay rent /by 2019-06-06
update 1 /by 2019-12-05
update 2 /from 2019-12-05
update 2 /by
update 2 /desc a /by 2019-12-05
update 2 /by tomorrow
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Added.
  [D][ ] pay rent (by: Jun 6 2019)
2 tasks.
____________________________________________________________
____________________________________________________________
No /by on a todo.
It takes: /desc
____________________________________________________________
____________________________________________________________
No /from on a deadline.
It takes: /desc, /by
____________________________________________________________
____________________________________________________________
Missing value after /by.
e.g. update 2 /by 2019-12-05 1800
____________________________________________________________
____________________________________________________________
One field at a time.
e.g. update 2 /by 2019-12-05 1800
____________________________________________________________
____________________________________________________________
Not a date: "tomorrow"
Use 2019-12-02, 2019-12-02 1800, 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] read book
2.[D][ ] pay rent (by: Jun 6 2019)
____________________________________________________________
```

### TC-40 An update is saved straight away

**Aim:** A successful update rewrites the save file, so the new value survives a restart. A
refused update leaves the file as it was.

**Data file before**

```text
T | 0 | read book
D | 0 | return book | 2019-06-06T00:00
```

**Input**

```text
update 2 /by 2019-12-05 1800
update 1 /by 2019-12-05
bye
```

**Expected output**

```text
____________________________________________________________
Updated.
  [D][ ] return book (by: Dec 5 2019, 6:00 PM)
____________________________________________________________
____________________________________________________________
No /by on a todo.
It takes: /desc
____________________________________________________________
```

**Data file after**

```text
T | 0 | read book
D | 0 | return book | 2019-12-05T18:00
```

### TC-41 Update needs a task and something to change

**Aim:** `update` on its own, with only a number, or with a number that is not a number, is
rejected with a usable example rather than a stack trace.

**Input**

```text
todo read book
update
update 1
update two /desc x
update 9 /desc x
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Update what?
e.g. update 2 /by 2019-12-05 1800
____________________________________________________________
____________________________________________________________
Name a field: /desc, /by, /from or /to.
e.g. update 2 /by 2019-12-05 1800
____________________________________________________________
____________________________________________________________
"two" is not a number.
e.g. mark 2
____________________________________________________________
____________________________________________________________
No task 9.
Pick between 1 and 1.
____________________________________________________________
```

### TC-42 Dates the calendar does not have are refused

**Aim:** A day a month does not have used to be moved quietly to the nearest real one, so
`2019-02-30` became a task due on the 28th without a word. Each of these must be refused, and
the leap day must still be accepted in a year that has one.

**Input**

```text
deadline pay rent /by 2019-02-30
deadline pay rent /by 2019-04-31
deadline pay rent /by 2019-02-29
deadline pay rent /by 2020-02-29
list
bye
```

**Expected output**

```text
____________________________________________________________
Not a date: "2019-02-30"
Use 2019-12-02, 2019-12-02 1800, 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
Not a date: "2019-04-31"
Use 2019-12-02, 2019-12-02 1800, 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
Not a date: "2019-02-29"
Use 2019-12-02, 2019-12-02 1800, 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
Added.
  [D][ ] pay rent (by: Feb 29 2020)
1 task.
____________________________________________________________
____________________________________________________________
Your list:
1.[D][ ] pay rent (by: Feb 29 2020)
____________________________________________________________
```

### TC-43 A description cannot hold the character the save file reserves

**Aim:** The save file separates fields with a pipe, so a description holding one used to be
accepted, saved, and then dropped as unreadable on the next start. The task must be refused
while the user is still there to fix it, and nothing may reach the save file.

**Input**

```text
todo a | b
deadline pay | rent /by 2019-12-02
todo read book
update 1 /desc a | b
list
bye
```

**Expected output**

```text
____________________________________________________________
A description cannot contain "|".
EV uses it to separate fields when it saves your tasks.
____________________________________________________________
____________________________________________________________
A description cannot contain "|".
EV uses it to separate fields when it saves your tasks.
____________________________________________________________
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
A description cannot contain "|".
EV uses it to separate fields when it saves your tasks.
____________________________________________________________
____________________________________________________________
Your list:
1.[T][ ] read book
____________________________________________________________
```

**Data file after**

```text
T | 0 | read book
```

### TC-44 An option may only be given once

**Aim:** A second `/by` used to be swallowed into the date, so the user was told their date was
unreadable rather than that they had written the option twice.

**Input**

```text
deadline return book /by 2019-12-01 /by 2019-12-02
event camp /from 2019-12-01 /from 2019-12-02 /to 2019-12-03
event camp /from 2019-12-01 /to 2019-12-02 /to 2019-12-03
bye
```

**Expected output**

```text
____________________________________________________________
/by may only appear once.
e.g. deadline return book /by 2019-12-02 1800
____________________________________________________________
____________________________________________________________
/from may only appear once.
e.g. event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
____________________________________________________________
____________________________________________________________
/to may only appear once.
e.g. event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
____________________________________________________________
```

### TC-45 An event cannot end before it starts

**Aim:** The two ends of an event must stay in order however the event is made: when it is
first added, and when either end is changed afterwards. A refused change leaves the event as
it was, and an event that starts and ends at the same moment is still allowed.

**Input**

```text
event camp /from 2019-12-05 /to 2019-12-01
event camp /from 2019-12-01 0900 /to 2019-12-03 1700
update 1 /to 2019-11-01 1700
update 1 /from 2019-12-31 0900
event standup /from 2019-12-02 0900 /to 2019-12-02 0900
list
bye
```

**Expected output**

```text
____________________________________________________________
An event cannot end before it starts.
It would run from Dec 5 2019 to Dec 1 2019.
____________________________________________________________
____________________________________________________________
Added.
  [E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)
1 task.
____________________________________________________________
____________________________________________________________
An event cannot end before it starts.
It would run from Dec 1 2019, 9:00 AM to Nov 1 2019, 5:00 PM.
____________________________________________________________
____________________________________________________________
An event cannot end before it starts.
It would run from Dec 31 2019, 9:00 AM to Dec 3 2019, 5:00 PM.
____________________________________________________________
____________________________________________________________
Added.
  [E][ ] standup (from: Dec 2 2019, 9:00 AM to: Dec 2 2019, 9:00 AM)
2 tasks.
____________________________________________________________
____________________________________________________________
Your list:
1.[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)
2.[E][ ] standup (from: Dec 2 2019, 9:00 AM to: Dec 2 2019, 9:00 AM)
____________________________________________________________
```

### TC-46 The same task cannot be added twice

**Aim:** Adding a task that is already on the list is refused, and the reply says which task it
already is. Finishing a task does not make it a different task. A task that differs in type or
in date is not a duplicate.

**Input**

```text
todo read book
todo read book
mark 1
todo read book
deadline read book /by 2019-12-02
todo read the book
list
bye
```

**Expected output**

```text
____________________________________________________________
Added.
  [T][ ] read book
1 task.
____________________________________________________________
____________________________________________________________
Already on your list as task 1.
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Done.
  [T][X] read book
____________________________________________________________
____________________________________________________________
Already on your list as task 1.
  [T][X] read book
____________________________________________________________
____________________________________________________________
Added.
  [D][ ] read book (by: Dec 2 2019)
2 tasks.
____________________________________________________________
____________________________________________________________
Added.
  [T][ ] read the book
3 tasks.
____________________________________________________________
____________________________________________________________
Your list:
1.[T][X] read book
2.[D][ ] read book (by: Dec 2 2019)
3.[T][ ] read the book
____________________________________________________________
```
