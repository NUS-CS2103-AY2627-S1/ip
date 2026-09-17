# UI Test Plan

This file lists the UI test cases run by the `test-ui` skill
(`.claude/skills/test-ui`). Each test case is fed to one fresh run of the
program, one command per line, and must end with `bye` so the program
terminates cleanly.

The startup banner and greeting are identical for every run, so **expected
output only needs to cover what the program prints from the first command
onward** (the runner strips the banner/greeting before comparing).

Positive and negative cases are interleaved on purpose, so a bug that
corrupts internal state (e.g. an error path that still increments the task
count) shows up in the very next positive case rather than being masked.

Each test case runs in its own fresh working directory, so tasks the
program saves to `data/lebron.txt` in one test case never carry over to the
next. Two optional blocks support the save/load feature:

- **Data file:** — contents written to `data/lebron.txt` *before* the run,
  for testing that previously saved tasks are loaded on startup.
- **Expected data file:** — contents `data/lebron.txt` must have *after*
  the run, for testing that task-list changes are saved. An empty block
  asserts the file is absent or empty.

When present, these blocks come *after* the Commands and Expected output
blocks.

## Test Case: Greet and exit

- **Aim:** The program greets the user and exits cleanly on `bye` with no
  other input.
- **Commands:**
  ```
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Add todos and list them

- **Aim:** `todo <description>` adds a task; `list` shows all added tasks,
  unmarked, in order.
- **Commands:**
  ```
  todo read book
  todo return book
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: read book
  ____________________________________________________________
  ____________________________________________________________
  on the board: return book
  ____________________________________________________________
  ____________________________________________________________
  Here's the playbook:
  1.[ ] read book
  2.[ ] return book
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject a todo with an empty description

- **Aim:** `todo` with no description is rejected with an error instead of
  adding a blank task, and the session continues normally afterward.
- **Commands:**
  ```
  todo
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! A todo needs a description, e.g. todo read book
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject an unrecognised command

- **Aim:** Input that isn't a known command keyword is rejected with an
  error instead of silently being treated as a task (this also confirms the
  rejected line was not added as a task -- verified by the next test case
  starting from an empty list).
- **Commands:**
  ```
  blah
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! I don't understand that command. Try: list, todo, deadline, event, mark, unmark, delete, find, or bye.
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Mark a task as done

- **Aim:** `mark <index>` flags the given task done, confirms it, and `list`
  reflects the change while leaving other tasks untouched.
- **Commands:**
  ```
  todo read book
  todo return book
  mark 1
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: read book
  ____________________________________________________________
  ____________________________________________________________
  on the board: return book
  ____________________________________________________________
  ____________________________________________________________
  Nice bucket! Marked as done:
    [X] read book
  ____________________________________________________________
  ____________________________________________________________
  Here's the playbook:
  1.[X] read book
  2.[ ] return book
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject mark with a missing task number

- **Aim:** `mark` with no argument is rejected with an error, rather than
  crashing.
- **Commands:**
  ```
  todo read book
  mark
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: read book
  ____________________________________________________________
  ____________________________________________________________
  OOPS!!! Tell me which task number to mark, e.g. mark 2
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject mark with a non-numeric task number

- **Aim:** `mark <non-number>` is rejected with an error, rather than
  crashing with a `NumberFormatException`.
- **Commands:**
  ```
  todo read book
  mark two
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: read book
  ____________________________________________________________
  ____________________________________________________________
  OOPS!!! 'two' doesn't look like a task number.
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject mark with an out-of-range task number

- **Aim:** `mark <n>` where `n` is outside the current list is rejected with
  an error, rather than crashing with an `ArrayIndexOutOfBoundsException` or
  a null pointer exception.
- **Commands:**
  ```
  todo read book
  mark 5
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: read book
  ____________________________________________________________
  ____________________________________________________________
  OOPS!!! There is no task 5 in your list. You have 1 task(s).
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Unmark a task

- **Aim:** `unmark <index>` reverses a task's done status and confirms it,
  independently of other marked tasks.
- **Commands:**
  ```
  todo read book
  todo return book
  mark 1
  mark 2
  unmark 2
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: read book
  ____________________________________________________________
  ____________________________________________________________
  on the board: return book
  ____________________________________________________________
  ____________________________________________________________
  Nice bucket! Marked as done:
    [X] read book
  ____________________________________________________________
  ____________________________________________________________
  Nice bucket! Marked as done:
    [X] return book
  ____________________________________________________________
  ____________________________________________________________
  Timeout -- back on the clock, not done yet:
    [ ] return book
  ____________________________________________________________
  ____________________________________________________________
  Here's the playbook:
  1.[X] read book
  2.[ ] return book
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject unmark with an out-of-range task number

- **Aim:** `unmark <n>` where `n` is outside the current list is rejected
  with an error, mirroring the same check for `mark`.
- **Commands:**
  ```
  todo read book
  unmark 5
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: read book
  ____________________________________________________________
  ____________________________________________________________
  OOPS!!! There is no task 5 in your list. You have 1 task(s).
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Delete a task from the middle of the list

- **Aim:** `delete <index>` removes the given task, confirms it with the
  removed task's text and the new task count, and `list` shows the
  remaining tasks correctly re-numbered (not just nulled out).
- **Commands:**
  ```
  todo read book
  todo return book
  todo buy bread
  delete 2
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: read book
  ____________________________________________________________
  ____________________________________________________________
  on the board: return book
  ____________________________________________________________
  ____________________________________________________________
  on the board: buy bread
  ____________________________________________________________
  ____________________________________________________________
  Cut from the roster:
    [ ] return book
  Now you've got 2 plays left in the game plan.
  ____________________________________________________________
  ____________________________________________________________
  Here's the playbook:
  1.[ ] read book
  2.[ ] buy bread
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject delete with a missing task number

- **Aim:** `delete` with no argument is rejected with an error, rather than
  crashing.
- **Commands:**
  ```
  todo read book
  delete
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: read book
  ____________________________________________________________
  ____________________________________________________________
  OOPS!!! Tell me which task number to delete, e.g. delete 2
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject delete with an out-of-range task number

- **Aim:** `delete <n>` where `n` is outside the current list is rejected
  with an error, and the task list is left unchanged (verified by `list`
  still showing the one task afterward).
- **Commands:**
  ```
  todo read book
  delete 5
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: read book
  ____________________________________________________________
  ____________________________________________________________
  OOPS!!! There is no task 5 in your list. You have 1 task(s).
  ____________________________________________________________
  ____________________________________________________________
  Here's the playbook:
  1.[ ] read book
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Start with no saved data file

- **Aim:** On the very first run (no `data/` folder or data file exists),
  the program starts with an empty task list instead of crashing.
- **Commands:**
  ```
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  Here's the playbook:
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Load tasks saved from a previous session

- **Aim:** Tasks stored in `data/lebron.txt` are read back on startup,
  preserving their order and done status, so `list` shows them immediately.
- **Commands:**
  ```
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  Here's the playbook:
  1.[ ] read book
  2.[X] return book
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```
- **Data file:**
  ```
  T | 0 | read book
  T | 1 | return book
  ```

## Test Case: Recover from a corrupted data file

- **Aim:** A data file containing a line that is not in the expected format
  loads the valid lines anyway and prints a single warning, rather than
  crashing or discarding everything (stretch goal).
- **Commands:**
  ```
  list
  bye
  ```
- **Expected output:**
  ```
  OOPS!!! Skipped 1 unreadable line(s) in your data file.
  ____________________________________________________________
  Here's the playbook:
  1.[ ] read book
  2.[X] return book
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```
- **Data file:**
  ```
  T | 0 | read book
  this is not a valid line
  T | 1 | return book
  ```

## Test Case: Save the task list after a change

- **Aim:** Adding and then marking a task writes the current list to
  `data/lebron.txt` in the expected format, so the change survives a
  restart.
- **Commands:**
  ```
  todo read book
  mark 1
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: read book
  ____________________________________________________________
  ____________________________________________________________
  Nice bucket! Marked as done:
    [X] read book
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```
- **Expected data file:**
  ```
  T | 1 | read book
  ```

## Test Case: Save the task list after a deletion

- **Aim:** Deleting a task rewrites `data/lebron.txt` without the removed
  task, so the deletion is persisted rather than only applied in memory.
- **Commands:**
  ```
  delete 1
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  Cut from the roster:
    [ ] read book
  Now you've got 1 plays left in the game plan.
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```
- **Data file:**
  ```
  T | 0 | read book
  T | 1 | return book
  ```
- **Expected data file:**
  ```
  T | 1 | return book
  ```

## Test Case: Add a deadline with a date only

- **Aim:** `deadline <desc> /by <yyyy-mm-dd>` parses the date and prints it
  back in `MMM dd yyyy` form (not as the raw input string).
- **Commands:**
  ```
  deadline return book /by 2019-12-02
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: [D][ ] return book (by: Dec 02 2019)
  ____________________________________________________________
  ____________________________________________________________
  Here's the playbook:
  1.[D][ ] return book (by: Dec 02 2019)
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```
- **Expected data file:**
  ```
  D | 0 | return book | 2019-12-02
  ```

## Test Case: Add a deadline with a date and time

- **Aim:** A `HHmm` time after the date is parsed and shown as a 12-hour
  clock time, e.g. `1800` becomes `6:00pm`.
- **Commands:**
  ```
  deadline submit report /by 2019-12-02 1800
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: [D][ ] submit report (by: Dec 02 2019 6:00pm)
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```
- **Expected data file:**
  ```
  D | 0 | submit report | 2019-12-02 1800
  ```

## Test Case: Accept the day-first date format from the requirement

- **Aim:** The `d/M/yyyy HHmm` form (`2/12/2019 1800` = 2 Dec 2019, 6pm)
  from the requirement's example is understood and normalised.
- **Commands:**
  ```
  deadline pay bill /by 2/12/2019 1800
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: [D][ ] pay bill (by: Dec 02 2019 6:00pm)
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```
- **Expected data file:**
  ```
  D | 0 | pay bill | 2019-12-02 1800
  ```

## Test Case: Add an event with start and end date-times

- **Aim:** `event <desc> /from <date> /to <date>` parses both endpoints.
- **Commands:**
  ```
  event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: [E][ ] project meeting (from: Dec 02 2019 2:00pm to: Dec 02 2019 4:00pm)
  ____________________________________________________________
  ____________________________________________________________
  Here's the playbook:
  1.[E][ ] project meeting (from: Dec 02 2019 2:00pm to: Dec 02 2019 4:00pm)
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```
- **Expected data file:**
  ```
  E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600
  ```

## Test Case: Reject a deadline with a missing /by

- **Aim:** `deadline` without a `/by` section is rejected with a usage hint
  rather than being stored with no date.
- **Commands:**
  ```
  deadline return book
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! A deadline needs a description and a /by date, e.g. deadline return book /by 2019-12-02 1800 [/every day|week|month]
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject a deadline with an unparseable date

- **Aim:** A `/by` value that matches none of the accepted date formats is
  rejected with an error instead of crashing or storing a bad task.
- **Commands:**
  ```
  deadline return book /by someday
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! I don't understand the date 'someday'. Try e.g. 2019-12-02 or 2019-12-02 1800.
  ____________________________________________________________
  ____________________________________________________________
  Here's the playbook:
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Load deadlines and events saved from a previous session

- **Aim:** `D` and `E` lines in the data file are read back into the right
  task types, with their dates re-formatted for display and their done
  status preserved.
- **Commands:**
  ```
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  Here's the playbook:
  1.[D][X] return book (by: Dec 02 2019 6:00pm)
  2.[E][ ] camp (from: Jun 01 2019 to: Jun 03 2019)
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```
- **Data file:**
  ```
  D | 1 | return book | 2019-12-02 1800
  E | 0 | camp | 2019-06-01 | 2019-06-03
  ```

## Test Case: Skip a data-file line with a corrupted date

- **Aim:** A `D` line whose date field is not a valid date is treated as
  corruption -- skipped with a warning -- while the other tasks still load
  (stretch goal).
- **Commands:**
  ```
  list
  bye
  ```
- **Expected output:**
  ```
  OOPS!!! Skipped 1 unreadable line(s) in your data file.
  ____________________________________________________________
  Here's the playbook:
  1.[D][ ] return book (by: Dec 02 2019)
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```
- **Data file:**
  ```
  D | 0 | return book | 2019-12-02
  D | 0 | pay rent | last tuesday
  ```

## Test Case: Find tasks matching a keyword

- **Aim:** `find <keyword>` lists the tasks whose description contains the
  keyword, keeping their done status and displaying them like `list` (but
  under a "matching tasks" heading), while leaving out non-matching tasks.
- **Commands:**
  ```
  todo read book
  todo return book
  todo buy bread
  mark 1
  find book
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: read book
  ____________________________________________________________
  ____________________________________________________________
  on the board: return book
  ____________________________________________________________
  ____________________________________________________________
  on the board: buy bread
  ____________________________________________________________
  ____________________________________________________________
  Nice bucket! Marked as done:
    [X] read book
  ____________________________________________________________
  ____________________________________________________________
  Here's what matches from the playbook:
  1.[X] read book
  2.[ ] return book
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Find with no matching tasks

- **Aim:** `find` with a keyword that matches nothing prints the heading and
  no task lines, rather than erroring.
- **Commands:**
  ```
  todo read book
  find plane
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: read book
  ____________________________________________________________
  ____________________________________________________________
  Here's what matches from the playbook:
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject find with no keyword

- **Aim:** `find` with no argument is rejected with an error instead of
  matching every task.
- **Commands:**
  ```
  find
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! Tell me what to search for, e.g. find book
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Add a recurring deadline and see it roll forward on mark

- **Aim:** `deadline ... /every <period>` creates a recurring deadline shown
  with an `(every: ...)` suffix; `mark` on it advances the due date by one
  period and leaves it not done, with its own confirmation message, instead
  of staying marked done.
- **Commands:**
  ```
  deadline pay rent /by 2025-01-01 /every month
  mark 1
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: [D][ ] pay rent (by: Jan 01 2025) (every: month)
  ____________________________________________________________
  ____________________________________________________________
  Nice bucket! Since this one's a repeat play, I've already queued up the next rep:
    [D][ ] pay rent (by: Feb 01 2025) (every: month)
  ____________________________________________________________
  ____________________________________________________________
  Here's the playbook:
  1.[D][ ] pay rent (by: Feb 01 2025) (every: month)
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Add a recurring event and see both endpoints roll forward

- **Aim:** `event ... /every <period>` creates a recurring event; marking it
  done advances both `/from` and `/to` by one period, keeping the event's
  duration.
- **Commands:**
  ```
  event standup /from 2025-01-06 0900 /to 2025-01-06 0930 /every week
  mark 1
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: [E][ ] standup (from: Jan 06 2025 9:00am to: Jan 06 2025 9:30am) (every: week)
  ____________________________________________________________
  ____________________________________________________________
  Nice bucket! Since this one's a repeat play, I've already queued up the next rep:
    [E][ ] standup (from: Jan 13 2025 9:00am to: Jan 13 2025 9:30am) (every: week)
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject an unrecognised recurrence period

- **Aim:** `/every` with anything other than day/week/month is rejected with
  a clear message, and the task is not created.
- **Commands:**
  ```
  deadline pay rent /by 2025-01-01 /every fortnight
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! 'fortnight' isn't a recurrence I understand. Try day, week, or month.
  ____________________________________________________________
  ____________________________________________________________
  Here's the playbook:
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject /every with no value

- **Aim:** `/every` given with nothing after it is rejected with a usage
  hint rather than crashing or silently ignoring it.
- **Commands:**
  ```
  deadline pay rent /by 2025-01-01 /every
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! Tell me how often, e.g. /every week (day, week, or month).
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Load an old-format data file with no recurrence field

- **Aim:** Backward compatibility: a data file saved before recurring tasks
  existed (4-field `D` lines, 5-field `E` lines, no trailing period) still
  loads correctly, as non-recurring tasks.
- **Commands:**
  ```
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  Here's the playbook:
  1.[D][ ] old deadline (by: Dec 02 2019)
  2.[E][ ] old event (from: Jun 01 2019 to: Jun 03 2019)
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```
- **Data file:**
  ```
  D | 0 | old deadline | 2019-12-02
  E | 0 | old event | 2019-06-01 | 2019-06-03
  ```

## Test Case: Skip a data-file line with a corrupted recurrence period

- **Aim:** A `D` line whose trailing period field isn't day/week/month is
  treated as corruption -- skipped with a warning -- while the other tasks
  still load (same handling as any other corrupted line).
- **Commands:**
  ```
  list
  bye
  ```
- **Expected output:**
  ```
  OOPS!!! Skipped 1 unreadable line(s) in your data file.
  ____________________________________________________________
  Here's the playbook:
  1.[D][ ] good deadline (by: Jan 01 2025) (every: month)
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```
- **Data file:**
  ```
  D | 0 | good deadline | 2025-01-01 | month
  D | 0 | bad recurrence | 2025-01-01 | fortnight
  ```

## Test Case: Reject a non-existent calendar date

- **Aim:** A date that doesn't exist (e.g. Feb 30) is rejected outright,
  instead of silently rolling over to the nearest valid date (Feb 28).
- **Commands:**
  ```
  deadline bad date /by 2025-02-30
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! I don't understand the date '2025-02-30'. Try e.g. 2019-12-02 or 2019-12-02 1800.
  ____________________________________________________________
  ____________________________________________________________
  Here's the playbook:
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject an event whose /to is not after its /from

- **Aim:** An event ending at or before its start is rejected, rather than
  being stored with a nonsensical negative or zero duration.
- **Commands:**
  ```
  event backwards /from 2025-01-10 /to 2025-01-01
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! An event's /to must be after its /from.
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject a task description containing the storage delimiter

- **Aim:** A description containing `|` is rejected up front, since it
  would otherwise silently corrupt that task's line in the data file (the
  task would be lost entirely on the next load).
- **Commands:**
  ```
  todo buy milk | eggs
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! A task description can't contain '|' (it's used internally to store tasks) -- please remove it.
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Reject a flag given more than once

- **Aim:** Giving `/by` twice is rejected with a clear message instead of
  the second occurrence silently getting swallowed into the date text.
- **Commands:**
  ```
  deadline pay rent /by 2025-01-01 /by 2025-02-01
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! Give /by only once.
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```

## Test Case: Warn (but still add) a likely-duplicate task

- **Aim:** Adding a task with the same type, description and date(s) as an
  existing one is still added (a deliberate repeat, e.g. "call mom" on two
  different occasions, is legitimate) but flagged with a note so an
  accidental duplicate doesn't go unnoticed.
- **Commands:**
  ```
  todo buy milk
  todo buy milk
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  on the board: buy milk
  ____________________________________________________________
  ____________________________________________________________
  on the board: buy milk (note: you already have a task just like this)
  ____________________________________________________________
  ____________________________________________________________
  Here's the playbook:
  1.[ ] buy milk
  2.[ ] buy milk
  ____________________________________________________________
  ____________________________________________________________
  Good game -- I'll see you back on the court soon!
  ____________________________________________________________
  ```
