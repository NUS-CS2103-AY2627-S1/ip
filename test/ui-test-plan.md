# Tally UI test plan

Manual-style tests of Tally's console behaviour, run automatically.

> **Attribution:** this plan was written by Claude (AI). The AI chose the test
> cases and wrote their expected outputs, deriving them from the increment
> specifications on the course website rather than from the program's output.

## How to run

```bash
python3 test/run-ui-tests.py           # every case
python3 test/run-ui-tests.py TC-03     # one case
```

The runner builds the project through Gradle, then replays each case's commands
through the program and compares what was printed against the expected output
below. Building the same way the real build does means the cases run against
the classes and dependencies that actually ship. It prints the whole session as it goes, and stops at the first
failure, reporting the expected output, the actual output, and a diff.

## How to write a case

Each case needs a `## TC-nn - Title` heading, an `**Aim:**` line, an `**Input**`
fenced block, and an `**Expected output**` fenced block. Input is one command per
line, exactly as the user would type it. Expected output is everything the
program prints, including the greeting banner.

Two things are ignored when comparing, so they cannot cause a false failure:
trailing spaces on a line, and blank lines at the very end of the output.

A case whose input does not end with `bye` tests what happens when the input
stream closes instead.

A case may add an optional `**Expected files after the run**` block, in which
each line reads `filename >>> one line that file should hold`. That checks what
was left on disk, not just what was printed. Console output alone would miss a
change that says the right thing and then destroys the data.

The block has to name every file the run leaves behind, and every line of each
one. A file nobody accounted for fails the case, so a half-written file left
uncleaned is caught rather than passed over; and blank lines count, so one that
appears in the middle of a data file is a difference like any other.

A case may add an optional `**Given the data file**` block before the input, to
write that content into the data file before the run. That is how a damaged file
is tested.

Two characters are spelled out rather than typed, in an input block or a data
file block alike, so that the plan holds nothing invisible and no editor can
tidy one away: `<TAB>` for a tab, and `<BOM>` for the byte-order mark some
editors write at the start of a file.

A case may add an optional `**Then restart and type**` block between the input
and the expected output. The runner then runs the program a second time against
the same data file, and the expected output covers both runs one after the
other. That is how the saved tally is checked.

Every case gets a data file of its own, deleted before the case runs, so no case
can inherit tasks another one saved.

The program is started in that file's folder and given the bare file name, which
is the shape a user naturally types for a file beside them. A path written that
way names no folder of its own, and saving to one used to crash, so running every
case this way keeps that from coming back.

## Coverage

These cases cover Level-0 through Level-10, the `A-Classes`, `A-Inheritance`,
`A-Exceptions` and `A-Collections` extensions, the `B-DoWithinPeriodTasks` and
`B-FindFreeTimes` extensions, and `A-MoreErrorHandling`.

## What is tested by hand

The JUnit suite covers 98% of the lines outside the window, measured with JaCoCo.
What it does not reach is either the window itself or code that needs a file
system behaving differently from this one: the fallback for a file system that
cannot rename atomically, the failure to delete a half-written file, and the
branch for a file system without POSIX permissions.

The window is checked by hand, since driving JavaFX from a test would test the
harness more than the program:

| Checked | How |
| --- | --- |
| It starts from the packaged jar and draws its window | every push, on Linux, under a virtual display |
| Replies appear, scroll, and the input keeps focus | by using it |
| Widening the window gives the text the room | by dragging it wider, and at a forced 780px |
| The window can still be made small | its minimum is 420 by 420 |
| Saying `bye` closes it after the goodbye is read | by using it |

Three operating systems run the JUnit suite, this console suite, and the
packaged-jar check on every push. All three are English, so a machine whose own
language folds letters differently is covered by a JUnit test that sets the
default locale to Turkish rather than by CI.

Cases that exercise a rejected command also issue a good command afterwards and
list the tally at the end. Checking only the error message would miss a bad
command that printed the right complaint but still altered the stored tasks.

---

## TC-01 - Greet and exit

**Aim:** Tally greets the user on startup and says goodbye when told `bye`, which is Level-0's requirement.

**Input**
```text
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-02 - Exit when the input stream closes

**Aim:** Closing the input without typing `bye` ends the conversation cleanly rather than throwing, so piping a file into Tally does not crash it.

**Input**
```text
todo read book
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-03 - List an empty tally

**Aim:** Asking to list before adding anything says so in words, rather than printing an empty block that reads as a fault.

**Input**
```text
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Nothing on record. Enjoy it while it lasts.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-04 - Add each task type and list them

**Aim:** `todo`, `deadline` and `event` each build the right task type, and the listing tags them `[T]`, `[D]` and `[E]` with their times, per Level-4.

**Input**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
Recorded:
[D][ ] return book (by: Jun 06 2019)
2 tasks on record.
____________________________________________________________

____________________________________________________________
Recorded:
[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
3 tasks on record.
____________________________________________________________

____________________________________________________________
On record:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-05 - Mark and unmark a task

**Aim:** `mark` fills a task's checkbox and `unmark` clears it again, and the change survives to the next listing, which is Level-3's requirement.

**Input**
```text
todo read book
todo return book
mark 2
list
unmark 2
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] return book
2 tasks on record.
____________________________________________________________

____________________________________________________________
Marked done:
[T][X] return book
____________________________________________________________

____________________________________________________________
On record:
1.[T][ ] read book
2.[T][X] return book
____________________________________________________________

____________________________________________________________
Marked not done. As you were:
[T][ ] return book
____________________________________________________________

____________________________________________________________
On record:
1.[T][ ] read book
2.[T][ ] return book
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-06 - Task count wording

**Aim:** The count after adding reads "1 task" for one task and "2 tasks" for more, so the confirmation is grammatical at every size.

**Input**
```text
todo first
todo second
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] first
1 task on record.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] second
2 tasks on record.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-07 - A date that cannot be read is refused

**Aim:** A deadline whose `/by` is not a yyyy-mm-dd date is rejected with a message naming what was typed and the form to use, rather than being stored as loose text. Level-8 replaced the old behaviour of keeping whatever was typed. A good deadline afterwards shows the rejected one left nothing behind.

**Input**
```text
deadline do homework /by no idea :-p
deadline do homework /by 2019-10-15
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Unreadable date: "no idea :-p". The form is yyyy-mm-dd, and has not changed. Example: 2019-10-15.
____________________________________________________________

____________________________________________________________
Recorded:
[D][ ] do homework (by: Oct 15 2019)
1 task on record.
____________________________________________________________

____________________________________________________________
On record:
1.[D][ ] do homework (by: Oct 15 2019)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-08 - An unrecognized command is rejected

**Aim:** Text that is not a known command is refused with an explanation rather than being stored, which is the first of the two errors Level-5 requires. Interleaving a good command afterwards shows the rejected line left no trace on the tally.

**Input**
```text
blah
todo read book
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Unknown command. The ones I answer to: todo, deadline, event, window, list, mark, unmark, delete, find, free, bye.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
On record:
1.[T][ ] read book
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-09 - A todo with no description is rejected

**Aim:** `todo` with nothing after it is refused with a message naming the fix, which is the second of the two errors Level-5 requires. The tally stays empty afterwards, proving the bad command added nothing.

**Input**
```text
todo
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
A todo needs a description. Example: todo read book
____________________________________________________________

____________________________________________________________
Nothing on record. Enjoy it while it lasts.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-10 - A deadline or event missing its times is rejected

**Aim:** `deadline` without `/by`, and `event` without `/to`, are refused rather than throwing. A good deadline afterwards confirms the rejected lines did not disturb the tally.

**Input**
```text
deadline return book
event project meeting /from 2019-08-06
deadline return book /by 2019-10-15
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
A deadline needs a description and a /by date. Example: deadline return book /by 2019-10-15
____________________________________________________________

____________________________________________________________
An event needs a description, a /from date and a /to date, in that order, each with an optional time after it. Example: event lecture /from 2026-08-06 16:00 /to 2026-08-06 18:00
____________________________________________________________

____________________________________________________________
Recorded:
[D][ ] return book (by: Oct 15 2019)
1 task on record.
____________________________________________________________

____________________________________________________________
On record:
1.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-11 - A bad task number is rejected

**Aim:** `mark` with no number, with text instead of a number, and with a number past the end of the list are each refused with their own explanation. The task marked in between shows the tally still works, and the final listing shows the rejected commands changed nothing.

**Input**
```text
todo read book
mark
mark abc
mark 4
mark 1
mark 0
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
mark needs the number of a task. Example: mark 2
____________________________________________________________

____________________________________________________________
mark needs the number of a task. Example: mark 2
____________________________________________________________

____________________________________________________________
There is no task 4 on record. Type list before guessing.
____________________________________________________________

____________________________________________________________
Marked done:
[T][X] read book
____________________________________________________________

____________________________________________________________
There is no task 0 on record. Type list before guessing.
____________________________________________________________

____________________________________________________________
On record:
1.[T][X] read book
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-12 - Delete a task

**Aim:** `delete` removes the named task, reports which one went, and the remaining tasks are renumbered so the positions stay contiguous. Deleting the middle of three proves later tasks shift up rather than leaving a gap.

**Input**
```text
todo read book
todo return book
todo borrow book
delete 2
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] return book
2 tasks on record.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] borrow book
3 tasks on record.
____________________________________________________________

____________________________________________________________
Struck from the record:
[T][ ] return book
2 tasks on record.
____________________________________________________________

____________________________________________________________
On record:
1.[T][ ] read book
2.[T][ ] borrow book
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-13 - Deleting the last task empties the tally

**Aim:** Removing the only task leaves the tally empty rather than in a broken state, and the count reads "0 tasks". Listing afterwards falls back to the empty-tally message, and marking into the empty tally is refused.

**Input**
```text
todo read book
delete 1
list
mark 1
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
Struck from the record:
[T][ ] read book
0 tasks on record.
____________________________________________________________

____________________________________________________________
Nothing on record. Enjoy it while it lasts.
____________________________________________________________

____________________________________________________________
There is no task 1 on record. Type list before guessing.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-14 - A bad delete number is rejected

**Aim:** `delete` with no number and with a number past the end are refused, and the error names `delete` rather than another command. A good delete afterwards shows the tally was untouched by the rejected attempts.

**Input**
```text
todo read book
delete
delete 5
delete 1
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
delete needs the number of a task. Example: delete 2
____________________________________________________________

____________________________________________________________
There is no task 5 on record. Type list before guessing.
____________________________________________________________

____________________________________________________________
Struck from the record:
[T][ ] read book
0 tasks on record.
____________________________________________________________

____________________________________________________________
Nothing on record. Enjoy it while it lasts.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-15 - An event with /to before /from is rejected

**Aim:** Writing `/to` before `/from` is refused rather than recorded with the start and end swapped. Regression test: this input once produced `(from: 4pm to: 2pm)` silently. The correctly ordered event afterwards shows the markers still work when written the right way round.

**Input**
```text
event meeting /to 2026-09-08 /from 2026-09-07
event meeting /from 2026-09-07 /to 2026-09-08
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
An event needs a description, a /from date and a /to date, in that order, each with an optional time after it. Example: event lecture /from 2026-08-06 16:00 /to 2026-08-06 18:00
____________________________________________________________

____________________________________________________________
Recorded:
[E][ ] meeting (from: Sep 07 2026 to: Sep 08 2026)
1 task on record.
____________________________________________________________

____________________________________________________________
On record:
1.[E][ ] meeting (from: Sep 07 2026 to: Sep 08 2026)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-16 - The tally survives a restart

**Aim:** Tasks and their done state are written to disk as they change and read back when Tally next starts, which is Level-7's requirement. Marking before the restart shows the checkbox state is saved too, not just the descriptions.

**Input**
```text
todo read book
deadline return book /by 2019-06-06
mark 1
bye
```

**Then restart and type**
```text
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
Recorded:
[D][ ] return book (by: Jun 06 2019)
2 tasks on record.
____________________________________________________________

____________________________________________________________
Marked done:
[T][X] read book
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________

____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
On record:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-17 - A deletion survives a restart

**Aim:** The file is rewritten when a task is removed, not only when one is added, so a deleted task does not come back on the next start.

**Input**
```text
todo read book
todo return book
delete 1
bye
```

**Then restart and type**
```text
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] return book
2 tasks on record.
____________________________________________________________

____________________________________________________________
Struck from the record:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________

____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
On record:
1.[T][ ] return book
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-18 - A damaged data file is reported, not obeyed

**Aim:** A line that is not in the saved format is skipped and named, while every line that reads is kept. Level-7's stretch goal. One unreadable line costing the user every other task is a worse answer than losing the one line, so long as the reply says which line went and where the original was kept.

**Given the data file**
```text
T | 1 | read book
this line is nonsense
D | 0 | return book | 2019-06-06
```

**Input**
```text
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Line 2 of tally.txt could not be read, so that task is not on record. Copied to tally.txt.broken for repair.
____________________________________________________________

____________________________________________________________
On record:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-19 - A saved tally is read back and can be worked on

**Aim:** A well-formed data file written by an earlier run is loaded in full, with each type and its done state restored, and the loaded tasks can then be numbered and marked like any other. This is the read half of Level-7, tested without relying on Tally having written the file itself.

**Given the data file**
```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 0 | project meeting | 2019-08-06 | 2019-08-07
```

**Input**
```text
list
mark 2
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
On record:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
____________________________________________________________

____________________________________________________________
Marked done:
[D][X] return book (by: Jun 06 2019)
____________________________________________________________

____________________________________________________________
On record:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-20 - A damaged file is moved aside, not destroyed

**Aim:** A file holding two unreadable lines is copied before anything can write over it, so the original survives for the user to repair while the tasks that did load stay on the tally. Regression test: Tally used to discard every task in the file, and before that destroyed the file outright when the first command saved over it. Typing a command here is the point, since that is what used to do the damage.

**Given the data file**
```text
T | 0 | precious task
GARBAGE LINE
D | 0 | another | Friday
```

**Input**
```text
todo start again
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Lines 2 and 3 of tally.txt could not be read, so those tasks are not on record. Copied to tally.txt.broken for repair.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] start again
2 tasks on record.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> T | 0 | precious task
tally.txt >>> T | 0 | start again
tally.txt.broken >>> T | 0 | precious task
tally.txt.broken >>> GARBAGE LINE
tally.txt.broken >>> D | 0 | another | Friday
```

---

## TC-21 - A date is read, shown and stored in the right forms

**Aim:** Level-8 asks that a date be accepted in one format and printed in another. The command supplies `2019-06-06`, the listing shows `Jun 06 2019`, and the data file keeps `2019-06-06`. Storing the form LocalDate reads back is what lets the deadline survive a restart, so all three are checked together.

**Input**
```text
deadline return book /by 2019-06-06
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[D][ ] return book (by: Jun 06 2019)
1 task on record.
____________________________________________________________

____________________________________________________________
On record:
1.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> D | 0 | return book | 2019-06-06
```

---

## TC-22 - A data file holding an unreadable date is damage

**Aim:** A deadline in the data file whose date is not yyyy-mm-dd cannot be turned into a task, so that line is skipped and named and the file is copied aside, rather than the user being asked about a file they did not type. Here it is the only line, so the tally is empty afterwards. This is the file-side counterpart of TC-07.

**Given the data file**
```text
D | 0 | return book | last Tuesday
```

**Input**
```text
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Line 1 of tally.txt could not be read, so that task is not on record. Copied to tally.txt.broken for repair.
____________________________________________________________

____________________________________________________________
Nothing on record. Enjoy it while it lasts.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> D | 0 | return book | last Tuesday
tally.txt.broken >>> D | 0 | return book | last Tuesday
```

---

## TC-23 - Find tasks by a word in their description

**Aim:** `find` shows only the tasks whose description contains the word, keeping each task's number from the whole tally so the user can act on what they see. Matching ignores case. Level-9's requirement.

**Input**
```text
todo read book
deadline return book /by 2019-06-06
todo buy bread
mark 1
find book
find BOOK
mark 2
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
Recorded:
[D][ ] return book (by: Jun 06 2019)
2 tasks on record.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] buy bread
3 tasks on record.
____________________________________________________________

____________________________________________________________
Marked done:
[T][X] read book
____________________________________________________________

____________________________________________________________
Matching:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________

____________________________________________________________
Matching:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________

____________________________________________________________
Marked done:
[D][X] return book (by: Jun 06 2019)
____________________________________________________________

____________________________________________________________
On record:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2019)
3.[T][ ] buy bread
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

---

## TC-24 - Find with nothing to show or nothing to look for

**Aim:** A search matching no task says so rather than printing an empty block, and `find` with no word is refused like any other command missing its argument. A good search afterwards shows neither disturbed the tally.

**Input**
```text
todo read book
find umbrella
find
find book
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
No match. Nothing you wrote down, at least.
____________________________________________________________

____________________________________________________________
find needs something to look for. Example: find book
____________________________________________________________

____________________________________________________________
Matching:
1.[T][ ] read book
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

## TC-25 - A window task is added, saved and read back

**Aim:** `window` records a task that may be done on any day of a period, shows both ends in the display format Level-8 asks for, and writes them in the yyyy-mm-dd form it reads back, so the task survives a restart.

**Input**
```text
window submit form /between 2026-09-08 /and 2026-09-12
list
bye
```

**Then restart and type**
```text
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[W][ ] submit form (window: Sep 08 2026 to Sep 12 2026)
1 task on record.
____________________________________________________________

____________________________________________________________
On record:
1.[W][ ] submit form (window: Sep 08 2026 to Sep 12 2026)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________

____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
On record:
1.[W][ ] submit form (window: Sep 08 2026 to Sep 12 2026)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> W | 0 | submit form | 2026-09-08 | 2026-09-12
```

## TC-26 - A window that ends before it starts is refused

**Aim:** the two ends are read as dates rather than kept as text so that a period running backwards can be refused, naming both ends. Nothing reaches the tally.

**Input**
```text
window submit form /between 2026-09-12 /and 2026-09-08
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
A window cannot end before it starts. You have it backwards: ends 2026-09-08, starts 2026-09-12.
____________________________________________________________

____________________________________________________________
Nothing on record. Enjoy it while it lasts.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

## TC-27 - A malformed window command is refused

**Aim:** a missing `/and`, a missing description and an unreadable date are each refused with their own explanation. The date complaint names the format rather than a command, since deadlines and windows both use it.

**Input**
```text
window submit form /between 2026-09-08
window /between 2026-09-08 /and 2026-09-12
window submit form /between soon /and 2026-09-12
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
A window needs a description, a /between date and an /and date, in that order. Example: window submit form /between 2026-09-08 /and 2026-09-12
____________________________________________________________

____________________________________________________________
A window needs a description, a /between date and an /and date, in that order. Example: window submit form /between 2026-09-08 /and 2026-09-12
____________________________________________________________

____________________________________________________________
Unreadable date: "soon". The form is yyyy-mm-dd, and has not changed. Example: 2019-10-15.
____________________________________________________________

____________________________________________________________
Nothing on record. Enjoy it while it lasts.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

## TC-28 - The next free day, and the next run of free days

**Aim:** `free` reports the earliest day nothing takes up, and `/for` the earliest run of that many in a row. A deadline takes up its own day and a window every day of its period, so the run has to clear both. `/from` fixes the day the search starts, which is what makes the answer checkable.

**Input**
```text
deadline report /by 2026-09-10
window certificate /between 2026-09-12 /and 2026-09-14
free /from 2026-09-09
free /for 3 /from 2026-09-09
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[D][ ] report (by: Sep 10 2026)
1 task on record.
____________________________________________________________

____________________________________________________________
Recorded:
[W][ ] certificate (window: Sep 12 2026 to Sep 14 2026)
2 tasks on record.
____________________________________________________________

____________________________________________________________
Next free day: Sep 09 2026.
____________________________________________________________

____________________________________________________________
Next 3 free days in a row begin Sep 15 2026.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

## TC-29 - A free search counts every event, and refuses what it cannot read

**Aim:** an event runs across the days between its ends, so the free-day search steps past them and answers with the first day no task claims. An event's ends were once free text, which named no day the search could see, and the reply had to say it was drawn from less than the whole tally; there is nothing left to disclaim. A run shorter than a day, a count that is not a number and an unreadable date are each refused.

**Input**
```text
event standup /from 2026-09-09 /to 2026-09-09
free /from 2026-09-09
free /for 0
free /for abc
free /from nope
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[E][ ] standup (from: Sep 09 2026 to: Sep 09 2026)
1 task on record.
____________________________________________________________

____________________________________________________________
Next free day: Sep 10 2026.
____________________________________________________________

____________________________________________________________
A free stretch must be at least one day. Fewer is not a stretch.
____________________________________________________________

____________________________________________________________
free takes an optional /for count and an optional /from date. Example: free /for 3 /from 2026-09-08
____________________________________________________________

____________________________________________________________
Unreadable date: "nope". The form is yyyy-mm-dd, and has not changed. Example: 2019-10-15.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

## TC-30 - Text the data file could not carry back is refused

**Aim:** the data file separates a task's parts with ` | ` and has no way to escape it, so a description holding a bar would be written out and read back as a different number of parts. The whole character is refused, not just the separator as written: the file joins parts with a space either side, so a description merely ending in a bar builds the separator on being saved. Refusing it where it is typed is what keeps every task readable back. A date outside the yyyy-mm-dd form is refused for the same reason: the wider forms parse, then overflow the date arithmetic done later.

**Input**
```text
todo laundry | dry cleaning
event meet /from Mon 2pm | room 3 /to 4pm
deadline pay /by +999999999-12-31
todo laundry and dry cleaning
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
A task cannot contain "|": the record file separates one part from the next with it. Choose another character.
____________________________________________________________

____________________________________________________________
A task cannot contain "|": the record file separates one part from the next with it. Choose another character.
____________________________________________________________

____________________________________________________________
Unreadable date: "+999999999-12-31". The form is yyyy-mm-dd, and has not changed. Example: 2019-10-15.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] laundry and dry cleaning
1 task on record.
____________________________________________________________

____________________________________________________________
On record:
1.[T][ ] laundry and dry cleaning
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> T | 0 | laundry and dry cleaning
```

## TC-31 - free reads its markers as whole words, in either order

**Aim:** the markers are matched as complete words rather than as the start of one, so "/forgotten" is not read as "/for" and the date after "/from" no longer swallows the rest of the line and gets complained about as a date. Both markers are optional and carry their own meaning, so either order is accepted, while giving one twice is refused.

**Input**
```text
free /from 2026-09-08 /for 3
free /for 3 /from 2026-09-08
free /forgotten
free /forbidden 3
free /for 3 /for 4
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Next 3 free days in a row begin Sep 08 2026.
____________________________________________________________

____________________________________________________________
Next 3 free days in a row begin Sep 08 2026.
____________________________________________________________

____________________________________________________________
free takes an optional /for count and an optional /from date. Example: free /for 3 /from 2026-09-08
____________________________________________________________

____________________________________________________________
free takes an optional /for count and an optional /from date. Example: free /for 3 /from 2026-09-08
____________________________________________________________

____________________________________________________________
free takes an optional /for count and an optional /from date. Example: free /for 3 /from 2026-09-08
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

## TC-32 - A window with two faults is told about the one it can be sure of

**Aim:** a description holding a bar cannot be written back whatever else is wrong with the command, so it is reported before the dates are read. This matches `deadline`, which already refused the bar first, and means the reply does not depend on which fault the parser happened to reach first.

**Input**
```text
window notes|draft /between soon /and 2026-09-12
window notes|draft /between 2026-09-12 /and 2026-09-08
window notes /between 2026-09-12 /and 2026-09-08
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
A task cannot contain "|": the record file separates one part from the next with it. Choose another character.
____________________________________________________________

____________________________________________________________
A task cannot contain "|": the record file separates one part from the next with it. Choose another character.
____________________________________________________________

____________________________________________________________
A window cannot end before it starts. You have it backwards: ends 2026-09-08, starts 2026-09-12.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

## TC-33 - No free day within the days searched

**Aim:** A window covering every day the search reaches leaves no free day to offer, and the reply says which days were looked at rather than claiming a year. The search stops a year ahead or at the last day a date can be written as, whichever comes first, so naming the span is the only wording that stays true at both ends.

**Input**
```text
window busy /between 2026-09-09 /and 2027-09-09
free /from 2026-09-09
free /for 3 /from 2026-09-09
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[W][ ] busy (window: Sep 09 2026 to Sep 09 2027)
1 task on record.
____________________________________________________________

____________________________________________________________
No free day from Sep 09 2026 to Sep 09 2027. You did this to yourself.
____________________________________________________________

____________________________________________________________
No run of 3 free days from Sep 09 2026 to Sep 09 2027. Ambitious.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

## TC-34 - Mistakes Tally names rather than guesses at

**Aim:** Four kinds of user error that Tally used to act on silently. `A-MoreErrorHandling` asks that anticipated errors be handled: a task already on the tally is not added twice; text after a command that takes none is questioned rather than dropped, which matters most for `bye`, since acting on it ended the session and discarded everything typed afterwards; and a marker given twice is named, rather than being swallowed into the value of the first one. Runs of spaces inside a description are recorded as one, which is also what lets the second `todo` here be recognised as the first.

**Input**
```text
todo read book
todo read    book
bye now
list extra
deadline essay /by 2026-09-10 /by 2026-09-11
event party /from 2026-09-08 /to 2026-09-09 /to 2026-09-10
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read book
1 task on record.
____________________________________________________________

____________________________________________________________
Already on record as task 1. Once is enough.
____________________________________________________________

____________________________________________________________
bye takes nothing after it. "now" is yours to explain.
____________________________________________________________

____________________________________________________________
list takes nothing after it. "extra" is yours to explain.
____________________________________________________________

____________________________________________________________
/by is given more than once. Choose.
____________________________________________________________

____________________________________________________________
/to is given more than once. Choose.
____________________________________________________________

____________________________________________________________
On record:
1.[T][ ] read book
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> T | 0 | read book
```

## TC-35 - An event that ends before it starts is refused

**Aim:** An event's two ends are dates, so the pair can always be compared. One running backwards is refused for the same reason a backwards window is, rather than being shown back to front and quietly counted as the days between them. An event that begins and ends on one day is not backwards, and the second command here shows it is taken.

**Input**
```text
event trip /from 2026-09-12 /to 2026-09-08
event standup /from 2026-09-09 /to 2026-09-09
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
An event cannot end before it starts. You have it backwards: ends 2026-09-08, starts 2026-09-12.
____________________________________________________________

____________________________________________________________
Recorded:
[E][ ] standup (from: Sep 09 2026 to: Sep 09 2026)
1 task on record.
____________________________________________________________

____________________________________________________________
On record:
1.[E][ ] standup (from: Sep 09 2026 to: Sep 09 2026)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> E | 0 | standup | 2026-09-09 | 2026-09-09
```

## TC-36 - A task the data file names twice is kept once

**Aim:** Tally refuses to add a task it already holds, so a saved file naming one twice could otherwise put the tally in a state no command can reach. The repeat is dropped and the line named. The two copies here disagree about being done, and the one kept takes the done flag, since a task recorded as done anywhere in the file has been done. A repeat is a readable line rather than damage, so no rescue copy is made: the only file left is the tally itself, rewritten without the repeat once a command changes it.

**Given the data file**
```text
T | 0 | read book
T | 1 | read book
T | 0 | buy bread
```

**Input**
```text
list
todo write essay
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Line 2 of tally.txt repeats a task already on record, so it is kept once.
____________________________________________________________

____________________________________________________________
On record:
1.[T][X] read book
2.[T][ ] buy bread
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] write essay
3 tasks on record.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> T | 1 | read book
tally.txt >>> T | 0 | buy bread
tally.txt >>> T | 0 | write essay
```

## TC-37 - A command typed in any case is still that command

**Aim:** A capital letter is the shift key rather than a different intention, so `TODO`, `List` and `BYE` name the same commands as their lowercase forms. `A-MoreErrorHandling` asks that common slips be handled rather than refused, and this is the commonest of them. A word that is no command in any case is still refused, which the last one shows.

**Input**
```text
TODO read the tP user stories
List
BLAH
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read the tP user stories
1 task on record.
____________________________________________________________

____________________________________________________________
On record:
1.[T][ ] read the tP user stories
____________________________________________________________

____________________________________________________________
Unknown command. The ones I answer to: todo, deadline, event, window, list, mark, unmark, delete, find, free, bye.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> T | 0 | read the tP user stories
```

## TC-38 - A data file an editor marked as UTF-8 still reads

**Aim:** Editors on Windows write a byte-order mark at the start of a UTF-8 file and do not show it, so a user who opened the data file to correct one line saves it back with an invisible character stuck to the first task. That task used to be the single line Tally could not read, and was quarantined as damage. The mark is taken off before the lines are read, so all three tasks load and nothing is copied aside. Adding a task then rewrites the file, which is what shows the mark is gone rather than merely stepped over: the expected file below holds no invisible character.

**Given the data file**
```text
<BOM>T | 0 | read book
D | 1 | return book | 2019-10-15
T | 0 | buy bread
```

**Input**
```text
list
todo write essay
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
On record:
1.[T][ ] read book
2.[D][X] return book (by: Oct 15 2019)
3.[T][ ] buy bread
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] write essay
4 tasks on record.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> T | 0 | read book
tally.txt >>> D | 1 | return book | 2019-10-15
tally.txt >>> T | 0 | buy bread
tally.txt >>> T | 0 | write essay
```

## TC-39 - Dates a hand-edited file padded are still dates

**Aim:** The data file is edited by hand, so a date in it can arrive with a space either side. Reading a date tidies the text first, so a padded date is the day it names and the event loads as any other would; reading the text as it came left the same date unreadable, which is how a damaged line is reported. The first two commands here show the event both listed and counted. The second line of the file pads a pair that runs backwards, which the reader refuses as it would refuse an unpadded one, rather than loading it and rejecting it on the next start.

**Given the data file**
```text
E | 0 | trip |  2026-09-08 | 2026-09-10
E | 0 | backwards |  2026-09-12 | 2026-09-08
T | 0 | keep me
```

**Input**
```text
list
free /from 2026-09-09
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Line 2 of tally.txt could not be read, so that task is not on record. Copied to tally.txt.broken for repair.
____________________________________________________________

____________________________________________________________
On record:
1.[E][ ] trip (from: Sep 08 2026 to: Sep 10 2026)
2.[T][ ] keep me
____________________________________________________________

____________________________________________________________
Next free day: Sep 11 2026.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> E | 0 | trip |  2026-09-08 | 2026-09-10
tally.txt >>> E | 0 | backwards |  2026-09-12 | 2026-09-08
tally.txt >>> T | 0 | keep me
tally.txt.broken >>> E | 0 | trip |  2026-09-08 | 2026-09-10
tally.txt.broken >>> E | 0 | backwards |  2026-09-12 | 2026-09-08
tally.txt.broken >>> T | 0 | keep me
```

## TC-40 - A tab between a command and what follows it

**Aim:** A task's own text has its spacing tidied wherever it comes from, so a tab inside a description is recorded as a space. The command word was split off on a literal space alone, so the same line typed with a tab after the command was refused as an unknown command. Any run of spaces or tabs separates the two now, and the description that follows is tidied as it always was.

**Input**
```text
todo<TAB>read<TAB>the tP user stories
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[T][ ] read the tP user stories
1 task on record.
____________________________________________________________

____________________________________________________________
On record:
1.[T][ ] read the tP user stories
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> T | 0 | read the tP user stories
```

## TC-41 - An event needs two dates, as a deadline needs one

**Aim:** An event's ends were once kept as whatever the user typed, so `Mon 2pm` was taken, and so was `y`, and so was `2026-02-30`, which was shown back as though February had thirty days. Both ends are read as a date now, and refused when they are not. A time may follow a date, but only written as `HH:mm`, so `2026-09-12 4pm` is refused along with the rest. The `list` at the end shows that nothing the first four commands named reached the tally.

**Input**
```text
event trip /from 2026-02-30 /to 2026-03-05
event standup /from Mon 2pm /to 4pm
event x /from y /to z
event lecture /from 2026-09-12 4pm /to 6pm
event conference /from 2026-09-12 /to 2026-09-14
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Unreadable date: "2026-02-30". The form is yyyy-mm-dd, and a time after it is written HH:mm. Example: 2019-10-15 16:00.
____________________________________________________________

____________________________________________________________
Unreadable date: "Mon 2pm". The form is yyyy-mm-dd, and a time after it is written HH:mm. Example: 2019-10-15 16:00.
____________________________________________________________

____________________________________________________________
Unreadable date: "y". The form is yyyy-mm-dd, and a time after it is written HH:mm. Example: 2019-10-15 16:00.
____________________________________________________________

____________________________________________________________
Unreadable date: "2026-09-12 4pm". The form is yyyy-mm-dd, and a time after it is written HH:mm. Example: 2019-10-15 16:00.
____________________________________________________________

____________________________________________________________
Recorded:
[E][ ] conference (from: Sep 12 2026 to: Sep 14 2026)
1 task on record.
____________________________________________________________

____________________________________________________________
On record:
1.[E][ ] conference (from: Sep 12 2026 to: Sep 14 2026)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> E | 0 | conference | 2026-09-12 | 2026-09-14
```

## TC-42 - An event an old data file wrote as free text is set aside

**Aim:** A version of Tally before this one wrote an event's ends as the user typed them, so a data file carried over from it can hold `Mon 2pm` where a date now belongs. Such a line is damage the same way a hand edit is: it is named, copied aside for repair, and left out of the tally, rather than loading as an event Tally can no longer make sense of. The todo on the third line shows the rest of the file still loads.

**Given the data file**
```text
E | 0 | project meeting | Mon 2pm | 4pm
E | 0 | trip | 2026-02-30 | 2026-03-05
T | 0 | keep me
```

**Input**
```text
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Lines 1 and 2 of tally.txt could not be read, so those tasks are not on record. Copied to tally.txt.broken for repair.
____________________________________________________________

____________________________________________________________
On record:
1.[T][ ] keep me
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> E | 0 | project meeting | Mon 2pm | 4pm
tally.txt >>> E | 0 | trip | 2026-02-30 | 2026-03-05
tally.txt >>> T | 0 | keep me
tally.txt.broken >>> E | 0 | project meeting | Mon 2pm | 4pm
tally.txt.broken >>> E | 0 | trip | 2026-02-30 | 2026-03-05
tally.txt.broken >>> T | 0 | keep me
```

## TC-43 - An event can carry the hours it runs between

**Aim:** Most events are remembered by the day they fall on, but a lecture that runs from four to six needs the hours or its two ends say nothing a bare date does not. A time may follow either end's date, written as `HH:mm` and shown back on a clock face, as Level-8 asks of the date. An end given without a time is the whole of its day, so the third command here is a half-day event rather than a pair of ends the wrong way round, and the fourth is refused because within one day the hours decide. The file keeps the 24-hour form that was typed.

**Input**
```text
event lecture /from 2026-09-12 16:00 /to 2026-09-12 18:00
event trip /from 2026-09-12 09:30 /to 2026-09-14
event packing /from 2026-09-11 20:00 /to 2026-09-11
event backwards /from 2026-09-12 18:00 /to 2026-09-12 16:00
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[E][ ] lecture (from: Sep 12 2026 4:00 pm to: Sep 12 2026 6:00 pm)
1 task on record.
____________________________________________________________

____________________________________________________________
Recorded:
[E][ ] trip (from: Sep 12 2026 9:30 am to: Sep 14 2026)
2 tasks on record.
____________________________________________________________

____________________________________________________________
Recorded:
[E][ ] packing (from: Sep 11 2026 8:00 pm to: Sep 11 2026)
3 tasks on record.
____________________________________________________________

____________________________________________________________
An event cannot end before it starts. You have it backwards: ends 2026-09-12 16:00, starts 2026-09-12 18:00.
____________________________________________________________

____________________________________________________________
On record:
1.[E][ ] lecture (from: Sep 12 2026 4:00 pm to: Sep 12 2026 6:00 pm)
2.[E][ ] trip (from: Sep 12 2026 9:30 am to: Sep 14 2026)
3.[E][ ] packing (from: Sep 11 2026 8:00 pm to: Sep 11 2026)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> E | 0 | lecture | 2026-09-12 16:00 | 2026-09-12 18:00
tally.txt >>> E | 0 | trip | 2026-09-12 09:30 | 2026-09-14
tally.txt >>> E | 0 | packing | 2026-09-11 20:00 | 2026-09-11
```

## TC-44 - An hour of a day is enough to take the day

**Aim:** The free-day search asks each task which days it takes up, and an event answers with every day it touches: an hour of a day is enough to make that day something other than free. The event here runs from the evening of the 9th to the morning of the 10th, so the first free day is the 11th, not the 10th.

**Given the data file**
```text
E | 0 | conference | 2026-09-09 19:00 | 2026-09-10 08:00
```

**Input**
```text
free /from 2026-09-09
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Next free day: Sep 11 2026.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> E | 0 | conference | 2026-09-09 19:00 | 2026-09-10 08:00
```

## TC-45 - An event ending at midnight leaves that day free

**Aim:** There is no `24:00` to write, so `00:00` on the following day is the only way to say an event runs until midnight. Midnight is the close of one day rather than a moment of the next, so the day the end names is still free, and the free-day search says so. The second event here ends a minute later, which is a moment of that day and takes it.

**Input**
```text
event red-eye /from 2026-09-09 22:00 /to 2026-09-10 00:00
free /from 2026-09-10
event just over /from 2026-09-10 23:00 /to 2026-09-11 00:01
free /from 2026-09-11
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[E][ ] red-eye (from: Sep 09 2026 10:00 pm to: Sep 10 2026 12:00 am)
1 task on record.
____________________________________________________________

____________________________________________________________
Next free day: Sep 10 2026.
____________________________________________________________

____________________________________________________________
Recorded:
[E][ ] just over (from: Sep 10 2026 11:00 pm to: Sep 11 2026 12:01 am)
2 tasks on record.
____________________________________________________________

____________________________________________________________
Next free day: Sep 12 2026.
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> E | 0 | red-eye | 2026-09-09 22:00 | 2026-09-10 00:00
tally.txt >>> E | 0 | just over | 2026-09-10 23:00 | 2026-09-11 00:01
```

## TC-46 - One moment written two ways is one event

**Aim:** Tally refuses to add a task it already holds. A start written as a bare day and one written as that day at midnight are two spellings of one moment, so the second is the same event and is refused rather than recorded beside the first, which is what happened while events were told apart by their saved line alone. An end is a different matter: without a time it is the close of its day, where `00:00` is the open of it, so the third command here is a different event and is taken.

**Input**
```text
event party /from 2026-09-08 /to 2026-09-09
event party /from 2026-09-08 00:00 /to 2026-09-09
event party /from 2026-09-08 /to 2026-09-09 00:00
list
bye
```

**Expected output**
```text
____________________________________________________________
 _____     _ _
|_   _|_ _| | |_   _
  | |/ _` | | | | | |
  | | (_| | | | |_| |
  |_|\__,_|_|_|\__, |
               |___/
Tally.
I keep the count. You keep the promises.
____________________________________________________________

____________________________________________________________
Recorded:
[E][ ] party (from: Sep 08 2026 to: Sep 09 2026)
1 task on record.
____________________________________________________________

____________________________________________________________
Already on record as task 1. Once is enough.
____________________________________________________________

____________________________________________________________
Recorded:
[E][ ] party (from: Sep 08 2026 to: Sep 09 2026 12:00 am)
2 tasks on record.
____________________________________________________________

____________________________________________________________
On record:
1.[E][ ] party (from: Sep 08 2026 to: Sep 09 2026)
2.[E][ ] party (from: Sep 08 2026 to: Sep 09 2026 12:00 am)
____________________________________________________________

____________________________________________________________
Session ended. Your tasks did not.
____________________________________________________________
```

**Expected files after the run**
```text
tally.txt >>> E | 0 | party | 2026-09-08 | 2026-09-09
tally.txt >>> E | 0 | party | 2026-09-08 | 2026-09-09 00:00
```
