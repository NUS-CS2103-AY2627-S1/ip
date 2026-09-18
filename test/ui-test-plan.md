# Text UI test plan

This file is both the documentation and the input for the `test-ui` skill. The
skill's runner parses the test cases below, runs the program once per test case
with the listed input lines piped to its standard input, and compares the console
output with the expected output recorded here.

Run the whole plan from the repository root with:

```bash
python3 .claude/skills/test-ui/scripts/run-ui-tests.py
```

## How to write a test case

Each test case is a level-3 heading of the form `### <id>: <title>`, followed by
three fields:

* `**Aim:**` — one line saying what the test case is checking and why.
* `**Input:**` — a fenced block holding one input line per line, exactly as the
  user would type it.
* `**Expected output:**` — a fenced block holding the console output the program
  should produce for that input, from the first line to the last.

Two further fields are optional, and are only needed by test cases about saving
and loading:

* `**Saved file:**` — a fenced block holding the contents of `data/tasks.txt` to
  put in place *before* the run. Use it to test loading. Omit it entirely and the
  run starts with no save file at all, which is what a first run on a new machine
  looks like; give it an empty block for an existing but empty file.
* `**Expected saved file:**` — a fenced block holding the contents `data/tasks.txt`
  should have *after* the run. Use it to test saving. Omit it and the file is not
  checked.

Each test case runs in its own empty working directory. Without that, a case
would inherit the tasks saved by the case before it, and the order the cases
happen to run in would change their results.

Comparison ignores differences that are invisible on screen: line-ending style,
trailing spaces at the end of a line, and blank lines at the very end of the
output. Everything else must match character for character.

`{{NAME}}` inside an expected output is replaced by the reusable snippet of the
same name defined below. The greeting banner and the sign-off appear in every
run, so they are written once here rather than repeated in every test case.

Two habits keep these test cases worth running:

* **Write the expected output from the source, not from a run.** If you paste in
  what the program printed, the test case can only ever confirm what the program
  already does, including its bugs. Predict the output, then run the case; a
  mismatch means either the program or the prediction is wrong, and working out
  which is the point of the exercise.
* **End a negative test case with a positive one.** After input that should be
  rejected, add a valid command and a `list`. A rejection that nevertheless
  corrupted the stored tasks — a half-built task, a wrong count, a shifted
  numbering — then shows up as a failure instead of passing unnoticed.

## Reusable output snippets

#### GREETING

The banner and welcome message printed before any command is read.

```text
____________________________________________________________

+----------------------------------------------------------+
|      ____      _    __     __ ___  ____                  |
|     |  _ \    / \   \ \   / /|_ _||  _ \                 |
|     | | | |  / _ \   \ \ / /  | | | | | |                |
|     | |_| | / ___ \   \ V /   | | | |_| |                |
|     |____/ /_/   \_\   \_/   |___||____/                 |
|       ____   ___    ____   ____  ___  _   _  ____        |
|      / ___| / _ \  / ___| / ___||_ _|| \ | |/ ___|       |
|     | |  _ | | | || |  _ | |  _  | | |  \| |\___ \       |
|     | |_| || |_| || |_| || |_| | | | | |\  | ___) |      |
|      \____| \___/  \____| \____||___||_| \_||____/       |
|                                                          |
|       __                                       __        |
|      /  \                                     /  \       |
|     | ## |===================================| ## |      |
|     | ## |===================================| ## |      |
|      \__/                                     \__/       |
|                                                          |
|       "WHO'S GONNA CARRY THE BOATS AND THE LOGS?!"       |
|                 THEY DON'T KNOW ME, SON!                 |
|                                                          |
|                     >> STAY HARD. <<                     |
+----------------------------------------------------------+
I'm David Goggins. Nobody is coming to save you, so let's get to work.
What are you going to conquer today?
Type help to see the commands I understand.
____________________________________________________________

```

#### FAREWELL

The sign-off printed as the program exits.

```text
____________________________________________________________
Rest up. Tomorrow we go again. Stay hard!
____________________________________________________________
```

## Test cases

The table shows which test cases exercise each command, so an untested area is
easy to spot. A command should have at least one case for its normal use and one
for each way its input can be wrong.

| Area | Works correctly | Rejects bad input |
| --- | --- | --- |
| Start-up and `bye` | TC1, TC7, TC8 | — |
| `list` | TC2, TC3, TC4 | — |
| `todo` | TC3, TC9 | TC11 |
| `deadline` | TC9 | TC12 |
| `event` | TC9 | TC13 |
| `mark` / `unmark` | TC4, TC33 | TC5, TC6, TC18 |
| `delete` | TC15 | TC16 |
| Parsing the command word | TC7, TC17 | TC10, TC17 |
| Stored state after errors | TC14 | TC12, TC13, TC14, TC16 |
| Loading and saving | TC19, TC20, TC21, TC22 | TC23, TC34 |
| `find` | TC32 | TC32 |
| Progress callouts under `list` | TC2, TC33 | — |
| `help` | TC25, TC26 | TC27 |
| Duplicate tasks | TC28 | TC28 |
| Flags (`/by`, `/from`, `/to`) | TC29 | TC29 |
| Details after `list` / `bye`, several task numbers | TC30 | TC30 |
| Dates that do not exist | TC31 | TC31 |


### TC1: Greets the user and says goodbye

**Aim:** Checks that the chatbot shows its banner and greeting on start-up, and its sign-off when the user types `bye`.

**Input:**

```text
bye
```

**Expected output:**

```text
{{GREETING}}
{{FAREWELL}}
```

### TC2: Lists an empty task list

**Aim:** Checks that `list` on a fresh session reports that the list is empty rather than printing an empty block.

**Input:**

```text
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Your list is empty. Comfortable, aren't you? Add something hard.
____________________________________________________________

{{FAREWELL}}
```

### TC3: Adds todos and lists them

**Aim:** Checks that `todo` stores a task, that each addition is confirmed with the new list size, and that `list` numbers the tasks from 1 with a type box and an unticked status box.

**Input:**

```text
todo read book
todo run 10 miles
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [T][ ] run 10 miles
 You have 2 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 2.[T][ ] run 10 miles
 0 of 2 done. Stop planning and start doing.
____________________________________________________________

{{FAREWELL}}
```

### TC4: Marks and unmarks a task

**Aim:** Checks that `mark 2` ticks the second task, that `unmark 2` clears it again, and that `list` reflects each change.

**Input:**

```text
todo read book
todo run 10 miles
mark 2
list
unmark 2
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [T][ ] run 10 miles
 You have 2 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 DONE. That's one less excuse:
   [T][X] run 10 miles
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 2.[T][X] run 10 miles
 1 of 2 done. You're not finished.
____________________________________________________________

____________________________________________________________
 Not done after all? Then it's still waiting for you:
   [T][ ] run 10 miles
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 2.[T][ ] run 10 miles
 0 of 2 done. Stop planning and start doing.
____________________________________________________________

{{FAREWELL}}
```

### TC5: Rejects a task number that does not exist

**Aim:** Checks that marking a task beyond the end of the list is reported to the user instead of crashing with an index error.

**Input:**

```text
todo read book
mark 5
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! There's no task 5 in your list: pick a number from 1 to 1.
____________________________________________________________

{{FAREWELL}}
```

### TC6: Rejects a missing or non-numeric task number

**Aim:** Checks that `mark two` is reported as a non-numeric task number and that a bare `unmark` asks for a number, each naming the command the user actually typed, rather than being stored as new tasks.

**Input:**

```text
todo read book
mark two
unmark
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! "two" is not a task number. Use a whole number, e.g. mark 2.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! Which task? Give me the number, e.g. unmark 2.
____________________________________________________________

{{FAREWELL}}
```

### TC7: Accepts the exit command in any capitalisation

**Aim:** Checks that `BYE` ends the session, confirming the exit command is matched case-insensitively.

**Input:**

```text
todo read book
BYE
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

{{FAREWELL}}
```

### TC8: Exits cleanly when the input ends without `bye`

**Aim:** Checks that reaching the end of piped input still prints the sign-off, rather than throwing NoSuchElementException.

**Input:**

```text
todo read book
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

{{FAREWELL}}
```

### TC9: Adds each kind of task

**Aim:** Checks the three task types end to end: `todo`, `deadline ... /by ...` and `event ... /from ... /to ...` are parsed, confirmed with the running list size, and shown by `list` with the right type box and details.

**Input:**

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 2026-08-06 /to 2026-08-07
todo join sports club
mark 1
mark 4
todo borrow book
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [D][ ] return book (by: 2026-06-06)
 You have 2 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [E][ ] project meeting (from: 2026-08-06 to: 2026-08-07)
 You have 3 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [T][ ] join sports club
 You have 4 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 DONE. That's one less excuse:
   [T][X] read book
____________________________________________________________

____________________________________________________________
 DONE. That's one less excuse:
   [T][X] join sports club
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [T][ ] borrow book
 You have 5 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][X] read book
 2.[D][ ] return book (by: 2026-06-06)
 3.[E][ ] project meeting (from: 2026-08-06 to: 2026-08-07)
 4.[T][X] join sports club
 5.[T][ ] borrow book
 2 of 5 done. You're not finished.
____________________________________________________________

{{FAREWELL}}
```

### TC10: Rejects an unrecognised command

**Aim:** Checks that plain text with no command word is reported as unknown instead of being silently stored, now that every task must be added with `todo`, `deadline` or `event`.

**Input:**

```text
read book
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 NO EXCUSES! I don't know the command "read". Stop guessing. Type help to see the commands I understand.
____________________________________________________________

____________________________________________________________
 Your list is empty. Comfortable, aren't you? Add something hard.
____________________________________________________________

{{FAREWELL}}
```

### TC11: Rejects a todo with no description

**Aim:** Checks the second error required by Level 5: `todo` with nothing after it is refused with a message showing the correct form, and the following `list` confirms no blank task was stored.

**Input:**

```text
todo
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 NO EXCUSES! The description of a todo cannot be empty. Name the work. Try: todo read book
____________________________________________________________

____________________________________________________________
 Your list is empty. Comfortable, aren't you? Add something hard.
____________________________________________________________

{{FAREWELL}}
```

### TC12: Rejects malformed deadlines

**Aim:** Checks each way a `deadline` can be incomplete — no `/by`, no description before `/by`, nothing after `/by` — and that a correct deadline typed straight afterwards still works, so the failed attempts left no half-built task behind.

**Input:**

```text
deadline return book
deadline /by 2026-09-13
deadline return book /by
deadline return book /by 2026-09-13
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 NO EXCUSES! A deadline needs a /by part. Without a date it's just a wish. Try: deadline return book /by 2026-09-10
____________________________________________________________

____________________________________________________________
 NO EXCUSES! The description of a deadline cannot be empty. Name the work. Try: deadline return book /by 2026-09-10
____________________________________________________________

____________________________________________________________
 NO EXCUSES! Tell me when it is due after /by. No date, no deadline. Try: deadline return book /by 2026-09-10
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [D][ ] return book (by: 2026-09-13)
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[D][ ] return book (by: 2026-09-13)
 0 of 1 done. Stop planning and start doing.
____________________________________________________________

{{FAREWELL}}
```

### TC13: Rejects malformed events

**Aim:** Checks each missing piece of an `event` — no `/from`, no `/to`, and an empty description, start or end — and that a correct event afterwards is still added as task 1, proving the rejected lines did not reach the list.

**Input:**

```text
event project meeting
event project meeting /from 2026-09-14
event /from 2026-09-14 /to 2026-09-15
event project meeting /from /to 4pm
event project meeting /from 2026-09-14 /to
event project meeting /from 2026-09-14 /to 2026-09-15
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 NO EXCUSES! An event needs a /from part. When does the work start? Try: event project meeting /from 2026-09-10 /to 2026-09-11
____________________________________________________________

____________________________________________________________
 NO EXCUSES! An event needs a /to part after /from. When does it end? Try: event project meeting /from 2026-09-10 /to 2026-09-11
____________________________________________________________

____________________________________________________________
 NO EXCUSES! The description of an event cannot be empty. Name the work. Try: event project meeting /from 2026-09-10 /to 2026-09-11
____________________________________________________________

____________________________________________________________
 NO EXCUSES! Tell me when the event starts after /from. Try: event project meeting /from 2026-09-10 /to 2026-09-11
____________________________________________________________

____________________________________________________________
 NO EXCUSES! Tell me when the event ends after /to. Try: event project meeting /from 2026-09-10 /to 2026-09-11
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [E][ ] project meeting (from: 2026-09-14 to: 2026-09-15)
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[E][ ] project meeting (from: 2026-09-14 to: 2026-09-15)
 0 of 1 done. Stop planning and start doing.
____________________________________________________________

{{FAREWELL}}
```

### TC14: Keeps the list intact across interleaved good and bad input

**Aim:** Interleaves valid and invalid commands — a `mark` past the end, a blank line, an uppercase `TODO`, a valid `mark`, an empty `todo` — and checks with `list` after each stage that only the valid commands changed the stored tasks and their done status.

**Input:**

```text
todo read book
mark 2
list

TODO run 10 miles
mark 2
todo
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! There's no task 2 in your list: pick a number from 1 to 1.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 0 of 1 done. Stop planning and start doing.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! You typed nothing. Silence won't get it done. Give me a command, e.g. list.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [T][ ] run 10 miles
 You have 2 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 DONE. That's one less excuse:
   [T][X] run 10 miles
____________________________________________________________

____________________________________________________________
 NO EXCUSES! The description of a todo cannot be empty. Name the work. Try: todo read book
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 2.[T][X] run 10 miles
 1 of 2 done. You're not finished.
____________________________________________________________

{{FAREWELL}}
```

### TC15: Deletes a task and renumbers the ones left

**Aim:** Checks that `delete` removes the right task, reports the new list size, and that the remaining tasks are renumbered, so a later `mark 1` acts on what is now task 1 rather than on the deleted position.

**Input:**

```text
todo read book
deadline return book /by 2026-09-13
event project meeting /from 2026-09-14 /to 2026-09-15
mark 3
delete 1
list
mark 1
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [D][ ] return book (by: 2026-09-13)
 You have 2 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [E][ ] project meeting (from: 2026-09-14 to: 2026-09-15)
 You have 3 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 DONE. That's one less excuse:
   [E][X] project meeting (from: 2026-09-14 to: 2026-09-15)
____________________________________________________________

____________________________________________________________
 Gone. I've taken this off your list:
   [T][ ] read book
 You have 2 tasks in the list.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[D][ ] return book (by: 2026-09-13)
 2.[E][X] project meeting (from: 2026-09-14 to: 2026-09-15)
 1 of 2 done. You're not finished.
____________________________________________________________

____________________________________________________________
 DONE. That's one less excuse:
   [D][X] return book (by: 2026-09-13)
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[D][X] return book (by: 2026-09-13)
 2.[E][X] project meeting (from: 2026-09-14 to: 2026-09-15)
 All 2 done. Now go find something harder.
____________________________________________________________

{{FAREWELL}}
```

### TC16: Rejects a delete that cannot be carried out

**Aim:** Checks every way `delete` can go wrong — on an empty list, with no number, with a non-number, with 0, and past the end — and that the one real task survives all of them and can still be deleted afterwards.

**Input:**

```text
delete 1
todo read book
delete
delete two
delete 0
delete 2
list
delete 1
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 NO EXCUSES! There's no task 1 in your list: your list is empty, so add a task first.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! Which task? Give me the number, e.g. delete 2.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! "two" is not a task number. Use a whole number, e.g. delete 2.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! There's no task 0 in your list: pick a number from 1 to 1.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! There's no task 2 in your list: pick a number from 1 to 1.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 0 of 1 done. Stop planning and start doing.
____________________________________________________________

____________________________________________________________
 Gone. I've taken this off your list:
   [T][ ] read book
 You have 0 tasks in the list.
____________________________________________________________

____________________________________________________________
 Your list is empty. Comfortable, aren't you? Add something hard.
____________________________________________________________

{{FAREWELL}}
```

### TC17: Accepts any capitalisation and untidy spacing

**Aim:** Checks the parser's tidying-up: the command word is matched case-insensitively, leading spaces and runs of spaces between the command and its argument are ignored, and a line of only spaces counts as empty input rather than as an unknown command.

**Input:**

```text
   ToDo    read book
LIST
   
DEADLINE return book /by   2026-09-13
MARK 1
list
BYE
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 0 of 1 done. Stop planning and start doing.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! You typed nothing. Silence won't get it done. Give me a command, e.g. list.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [D][ ] return book (by: 2026-09-13)
 You have 2 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 DONE. That's one less excuse:
   [T][X] read book
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][X] read book
 2.[D][ ] return book (by: 2026-09-13)
 1 of 2 done. You're not finished.
____________________________________________________________

{{FAREWELL}}
```

### TC18: Handles task numbers at and beyond the boundaries

**Aim:** Checks the edges of the task-number range — a negative number, 0, and a number too large to fit in an `int` — and that marking an already-done task or unmarking an already-not-done task only gives a warning, so the final `list` shows the single task untouched.

**Input:**

```text
todo read book
mark -1
mark 0
mark 99999999999999999999
mark 1
mark 1
unmark 2
unmark 1
unmark 1
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! There's no task -1 in your list: pick a number from 1 to 1.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! There's no task 0 in your list: pick a number from 1 to 1.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! There's no task 99999999999999999999 in your list. That number is way past the end of it.
____________________________________________________________

____________________________________________________________
 DONE. That's one less excuse:
   [T][X] read book
____________________________________________________________

____________________________________________________________
 Warning: Task 1 is already done: [T][X] read book. Nothing changed.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! There's no task 2 in your list: pick a number from 1 to 1.
____________________________________________________________

____________________________________________________________
 Not done after all? Then it's still waiting for you:
   [T][ ] read book
____________________________________________________________

____________________________________________________________
 Warning: Task 1 is already not done: [T][ ] read book. Nothing changed.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 0 of 1 done. Stop planning and start doing.
____________________________________________________________

{{FAREWELL}}
```

### TC19: Saves every change to the data file

**Aim:** Checks that adding, marking and deleting are each written through to `data/tasks.txt` in the storage format, so nothing is lost if the program stops.

**Input:**

```text
todo read book
deadline return book /by 2026-09-13
event project meeting /from 2026-09-14 /to 2026-09-15
mark 1
delete 2
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [D][ ] return book (by: 2026-09-13)
 You have 2 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [E][ ] project meeting (from: 2026-09-14 to: 2026-09-15)
 You have 3 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 DONE. That's one less excuse:
   [T][X] read book
____________________________________________________________

____________________________________________________________
 Gone. I've taken this off your list:
   [D][ ] return book (by: 2026-09-13)
 You have 2 tasks in the list.
____________________________________________________________

{{FAREWELL}}
```

**Expected saved file:**

```text
T | 1 | read book
E | 0 | project meeting | 2026-09-14 | 2026-09-15
```

### TC20: Loads saved tasks when the program starts

**Aim:** Checks that a task list saved by an earlier run comes back, with each kind of task rebuilt and its done status preserved, and that new tasks are numbered after the loaded ones.

**Saved file:**

```text
T | 1 | read book
D | 0 | return book | 2026-09-13
E | 1 | project meeting | 2026-09-14 | 2026-09-15
```

**Input:**

```text
list
todo run 10 miles
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Here's what you signed up for:
 1.[T][X] read book
 2.[D][ ] return book (by: 2026-09-13)
 3.[E][X] project meeting (from: 2026-09-14 to: 2026-09-15)
 2 of 3 done. You're not finished.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][X] read book
 2.[D][ ] return book (by: 2026-09-13)
 3.[E][X] project meeting (from: 2026-09-14 to: 2026-09-15)
 2 of 3 done. You're not finished.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [T][ ] run 10 miles
 You have 4 tasks in the list. Get after it.
____________________________________________________________

{{FAREWELL}}
```

**Expected saved file:**

```text
T | 1 | read book
D | 0 | return book | 2026-09-13
E | 1 | project meeting | 2026-09-14 | 2026-09-15
T | 0 | run 10 miles
```

### TC21: Starts with an empty list when there is no data file

**Aim:** Checks the first run on a new machine: with no `data/` folder and no save file, the program starts normally and says nothing about the missing file, then creates the file when the list first changes.

**Input:**

```text
list
todo read book
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Your list is empty. Comfortable, aren't you? Add something hard.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

{{FAREWELL}}
```

**Expected saved file:**

```text
T | 0 | read book
```

### TC22: Ignores a save file that is empty or blank

**Aim:** Checks that an existing but empty save file, and stray blank lines in one, are treated as "no tasks" rather than as corruption.

**Saved file:**

```text

T | 0 | read book

```

**Input:**

```text
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 0 of 1 done. Stop planning and start doing.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 0 of 1 done. Stop planning and start doing.
____________________________________________________________

{{FAREWELL}}
```

### TC23: Skips corrupted lines and keeps the readable ones

**Aim:** Checks the stretch goal. Each line of the save file can be malformed in a different way — an unknown type, a done flag that is not 0 or 1, too few fields, too many, an empty field, no separators at all — and none of them should stop the program starting. The readable lines must still load, and the user must be told how many were dropped and where the untouched original was copied.

**Saved file:**

```text
T | 1 | read book
X | 0 | unknown type marker
D | 2 | done flag is not 0 or 1 | 2026-09-13
T | 0
E | 0 | too few fields for an event | 2026-09-14
E | 0 | too many fields | Mon | 4pm | extra
D | 0 | empty due date |
T | 0 |
no separators at all
D | 0 | return book | 2026-09-13
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
 Warning: skipped 8 unreadable lines in data/tasks.txt. Your original file is backed up to data/tasks.txt.bak.
{{GREETING}}
____________________________________________________________
 Here's what you signed up for:
 1.[T][X] read book
 2.[D][ ] return book (by: 2026-09-13)
 1 of 2 done. You're not finished.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][X] read book
 2.[D][ ] return book (by: 2026-09-13)
 1 of 2 done. You're not finished.
____________________________________________________________

{{FAREWELL}}
```

### TC24: Refuses a task containing the field separator

**Aim:** Checks the round-trip hazard: a description containing the `|` used to separate fields in the save file would be written as an extra field and could not be read back, so it is refused up front. Ends with a valid task to show the list is unharmed.

**Input:**

```text
todo read book | now
deadline return book /by 2026-09-13 | Monday
todo read book
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 NO EXCUSES! A task cannot contain the "|" character, since that is what I use to separate fields when saving. Drop it and try again.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! A task cannot contain the "|" character, since that is what I use to separate fields when saving. Drop it and try again.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 0 of 1 done. Stop planning and start doing.
____________________________________________________________

{{FAREWELL}}
```

**Expected saved file:**

```text
T | 0 | read book
```

### TC25: Shows the help page

**Aim:** Checks that `help` lists every command with its syntax and an example for each command that adds a task, and that asking for help leaves the list and the save file untouched.

**Saved file:**

```text
T | 0 | read book
```

**Input:**

```text
help
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 0 of 1 done. Stop planning and start doing.
____________________________________________________________

____________________________________________________________
 Here are the commands I understand. Learn them, then use them:
 todo <description>
 Example: todo read book
 deadline <description> /by <yyyy-mm-dd>
 Example: deadline return book /by 2026-09-10
 event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
 Example: event project meeting /from 2026-09-10 /to 2026-09-11
 list
 find <keyword>
 mark <task number>
 unmark <task number>
 delete <task number>
 help
 bye
____________________________________________________________

{{FAREWELL}}
```

**Expected saved file:**

```text
T | 0 | read book
```

### TC26: Accepts help in any capitalisation and spacing

**Aim:** Checks that `help` follows the same rules as every other command word: case does not matter and surrounding spaces are ignored.

**Input:**

```text
  HELP  
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Here are the commands I understand. Learn them, then use them:
 todo <description>
 Example: todo read book
 deadline <description> /by <yyyy-mm-dd>
 Example: deadline return book /by 2026-09-10
 event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
 Example: event project meeting /from 2026-09-10 /to 2026-09-11
 list
 find <keyword>
 mark <task number>
 unmark <task number>
 delete <task number>
 help
 bye
____________________________________________________________

{{FAREWELL}}
```

### TC27: Rejects help followed by details

**Aim:** Checks that `help` takes nothing after it, so a guess such as `help deadline` is explained rather than silently treated as plain `help`.

**Input:**

```text
help deadline
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 NO EXCUSES! The help command takes no details. Try: help
____________________________________________________________

{{FAREWELL}}
```

### TC28: Rejects a task that is already in the list

**Aim:** Checks that adding the same task twice is refused, ignoring case, extra spaces and whether the first copy is done, while a deadline with a different date and a one-day event are still accepted.

**Input:**

```text
todo run 10 miles
TODO   Run  10 Miles
deadline return book /by 2026-09-13
deadline Return Book /by 2026-09-14
deadline return book /by 2026-09-13
mark 1
todo run 10 miles
event camp /from 2026-10-01 /to 2026-10-01
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] run 10 miles
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! You already logged that as task 1: [T][ ] run 10 miles. Writing it down twice won't get it done twice.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [D][ ] return book (by: 2026-09-13)
 You have 2 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [D][ ] Return Book (by: 2026-09-14)
 You have 3 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! You already logged that as task 2: [D][ ] return book (by: 2026-09-13). Writing it down twice won't get it done twice.
____________________________________________________________

____________________________________________________________
 DONE. That's one less excuse:
   [T][X] run 10 miles
____________________________________________________________

____________________________________________________________
 NO EXCUSES! You already logged that as task 1: [T][X] run 10 miles. Writing it down twice won't get it done twice.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [E][ ] camp (from: 2026-10-01 to: 2026-10-01)
 You have 4 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][X] run 10 miles
 2.[D][ ] return book (by: 2026-09-13)
 3.[D][ ] Return Book (by: 2026-09-14)
 4.[E][ ] camp (from: 2026-10-01 to: 2026-10-01)
 1 of 4 done. You're not finished.
____________________________________________________________

{{FAREWELL}}
```

### TC29: Rejects repeated, misplaced and unknown flags

**Aim:** Checks that a flag given twice, a flag that belongs to another task type, an unknown flag, and `/to` before `/from` are each named as the problem instead of surfacing as a confusing date error, while a slash inside a path is still fine in a todo.

**Input:**

```text
deadline return book /by 2026-09-13 /by 2026-09-14
deadline return book /from 2026-09-13
deadline return book /at 2026-09-13
todo read book /by 2026-09-13
event camp /from 2026-10-01 /from 2026-10-02 /to 2026-10-03
event camp /to 2026-10-03 /from 2026-10-01
event camp /from 2026-10-01 /to 2026-10-03 /by 2026-10-02
todo fix /etc/hosts
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 NO EXCUSES! You gave /by more than once. A deadline has one due date. Try: deadline return book /by 2026-09-10
____________________________________________________________

____________________________________________________________
 NO EXCUSES! A deadline does not take a /from part, only /by. Try: deadline return book /by 2026-09-10
____________________________________________________________

____________________________________________________________
 NO EXCUSES! A deadline does not take a /at part, only /by. Try: deadline return book /by 2026-09-10
____________________________________________________________

____________________________________________________________
 NO EXCUSES! A todo has no dates, so it takes no /by part. Use deadline or event for a dated task. Try: todo read book
____________________________________________________________

____________________________________________________________
 NO EXCUSES! You gave /from more than once. An event starts once. Try: event project meeting /from 2026-09-10 /to 2026-09-11
____________________________________________________________

____________________________________________________________
 NO EXCUSES! Put /from before /to: start first, then finish. Try: event project meeting /from 2026-09-10 /to 2026-09-11
____________________________________________________________

____________________________________________________________
 NO EXCUSES! An event does not take a /by part, only /from and /to. Try: event project meeting /from 2026-09-10 /to 2026-09-11
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [T][ ] fix /etc/hosts
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] fix /etc/hosts
 0 of 1 done. Stop planning and start doing.
____________________________________________________________

{{FAREWELL}}
```

### TC30: Rejects details after bare commands and several task numbers

**Aim:** Checks that `list` and `bye` refuse anything typed after them instead of ignoring it or calling `bye` unknown, and that `mark` and `delete` ask for one task at a time, leaving the list untouched.

**Input:**

```text
todo read book
list all
bye now
mark 1 2
delete 1 1
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! The list command takes no details. Try: list
____________________________________________________________

____________________________________________________________
 NO EXCUSES! The bye command takes no details. Try: bye
____________________________________________________________

____________________________________________________________
 NO EXCUSES! One task at a time. Give me a single number, e.g. mark 2.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! One task at a time. Give me a single number, e.g. delete 2.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 0 of 1 done. Stop planning and start doing.
____________________________________________________________

{{FAREWELL}}
```

### TC31: Rejects dates that do not exist

**Aim:** Checks that a correctly written date naming a day that does not exist, such as 30 February or 31 April, is refused instead of being quietly moved to the end of the month, that 29 February is accepted only in a leap year, and that an event ending before it starts is still refused.

**Input:**

```text
deadline pay rent /by 2026-02-30
deadline pay rent /by 2027-02-29
deadline pay rent /by 2028-02-29
event trip /from 2026-04-31 /to 2026-05-02
event trip /from 2026-05-03 /to 2026-05-02
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 NO EXCUSES! "2026-02-30" is not a real date. Check the month and the day: that day does not exist.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! "2027-02-29" is not a real date. Check the month and the day: that day does not exist.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [D][ ] pay rent (by: 2028-02-29)
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! "2026-04-31" is not a real date. Check the month and the day: that day does not exist.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! An event cannot end before it starts: you gave a start of "2026-05-03" and an end of "2026-05-02".
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[D][ ] pay rent (by: 2028-02-29)
 0 of 1 done. Stop planning and start doing.
____________________________________________________________

{{FAREWELL}}
```

### TC32: Finds tasks by keyword

**Aim:** Checks that `find` matches any part of a description regardless of case, numbers the matches from 1 rather than by their place in the whole list, reports when nothing matches, and asks for a keyword when none is given.

**Input:**

```text
todo read book
todo run 10 miles
deadline return book /by 2026-09-13
find BOOK
find mile
find swim
find
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [T][ ] run 10 miles
 You have 2 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [D][ ] return book (by: 2026-09-13)
 You have 3 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Here are the matching tasks. Pick one and get it done:
 1.[T][ ] read book
 2.[D][ ] return book (by: 2026-09-13)
____________________________________________________________

____________________________________________________________
 Here are the matching tasks. Pick one and get it done:
 1.[T][ ] run 10 miles
____________________________________________________________

____________________________________________________________
 No tasks match "swim". Nothing to hide behind.
____________________________________________________________

____________________________________________________________
 NO EXCUSES! Tell me what to search for. You can't chase what you can't name. Try: find book
____________________________________________________________

{{FAREWELL}}
```

### TC33: Calls out progress at every stage

**Aim:** Checks the progress line under `list` as tasks are ticked off and back: none done, some done, all done, and back to some done after an `unmark`.

**Input:**

```text
todo read book
todo run 10 miles
list
mark 1
list
mark 2
list
unmark 1
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Logged. This one's on you now:
   [T][ ] read book
 You have 1 task in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [T][ ] run 10 miles
 You have 2 tasks in the list. Get after it.
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 2.[T][ ] run 10 miles
 0 of 2 done. Stop planning and start doing.
____________________________________________________________

____________________________________________________________
 DONE. That's one less excuse:
   [T][X] read book
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][X] read book
 2.[T][ ] run 10 miles
 1 of 2 done. You're not finished.
____________________________________________________________

____________________________________________________________
 DONE. That's one less excuse:
   [T][X] run 10 miles
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][X] read book
 2.[T][X] run 10 miles
 All 2 done. Now go find something harder.
____________________________________________________________

____________________________________________________________
 Not done after all? Then it's still waiting for you:
   [T][ ] read book
____________________________________________________________

____________________________________________________________
 Here's what you signed up for:
 1.[T][ ] read book
 2.[T][X] run 10 miles
 1 of 2 done. You're not finished.
____________________________________________________________

{{FAREWELL}}
```

### TC34: Rewrites a damaged save file with only its readable tasks

**Aim:** Checks what happens after the warning in TC23: the next change saves only the tasks that could be read, in the standard format, while the warning tells the user the original was backed up first.

**Saved file:**

```text
T|1|read book
this line is damaged
```

**Input:**

```text
todo run 10 miles
bye
```

**Expected output:**

```text
 Warning: skipped 1 unreadable line in data/tasks.txt. Your original file is backed up to data/tasks.txt.bak.
{{GREETING}}
____________________________________________________________
 Here's what you signed up for:
 1.[T][X] read book
 All 1 done. Now go find something harder.
____________________________________________________________

____________________________________________________________
 Logged. This one's on you now:
   [T][ ] run 10 miles
 You have 2 tasks in the list. Get after it.
____________________________________________________________

{{FAREWELL}}
```

**Expected saved file:**

```text
T | 1 | read book
T | 0 | run 10 miles
```
