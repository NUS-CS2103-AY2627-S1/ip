# Console UI Test Plan

The `test-ui` skill executes these cases in order and stops at the first failure.

## Run information

- Java version: Java 25 (`25.0.3.fx-zulu` on macOS)
- Compile command: `javac -d /tmp/noms-ui-test-out $(find src/main/java -name "*.java")` (sources now live in `noms` sub-packages, so a recursive find is needed instead of a flat `*.java` glob)
- Run command: `java -cp /tmp/noms-ui-test-out noms.Noms` (the entry point is now the fully qualified `noms.Noms`)
- Working directory: repository root
- Output comparison: exact, except for line-ending differences
- Shared process: no; every test case starts a fresh process with an empty task list, **except TC-017 and TC-018, which explicitly test persistence across two processes**
- Before every test case (including TC-017's first run), delete `./data/noms.txt` if it exists. Noms now loads any saved tasks from that file at startup, so a file left over from a previous case would make that case start with a non-empty list instead of the documented one. **TC-019 is the exception: it pre-seeds `./data/noms.txt` with specific contents before running, as described in that case.**
- User input is not echoed by the application
- Inputs are supplied exactly as shown, including blank lines
- Expected output includes the complete startup banner, responses, separators, and goodbye output

## Test cases

### TC-001: Add and list all task types

**Aim:**

Verify that todos, deadlines, and events are parsed into the correct subclasses and displayed with their type and dates. Deadlines and events now take dates in `yyyy-mm-dd` format and print them as `MMM dd yyyy`.

**Inputs:**

```text
todo borrow book
deadline return book /by 2019-12-01
event project meeting /from 2019-08-06 /to 2019-08-07
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] borrow book
 Your menu now has 1 task.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] return book (by: Dec 01 2019)
 Your menu now has 2 tasks.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
 Your menu now has 3 tasks.
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Dec 01 2019)
 3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-002: Mark and unmark a typed task

**Aim:**

Verify that marking and unmarking preserves the task type and deadline details after the date-parsing change.

**Inputs:**

```text
deadline submit report /by 2019-10-11
mark 1
mark 1
unmark 1
unmark 1
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] submit report (by: Oct 11 2019)
 Your menu now has 1 task.
____________________________________________________________
 Yum! Noms has marked this task as done:
   [D][X] submit report (by: Oct 11 2019)
____________________________________________________________
 Nom nom! This task is already marked as done:
   [D][X] submit report (by: Oct 11 2019)
____________________________________________________________
 No worries! Noms has put this task back on the menu:
   [D][ ] submit report (by: Oct 11 2019)
____________________________________________________________
 Nom nom! This task is already unmarked:
   [D][ ] submit report (by: Oct 11 2019)
____________________________________________________________
 Here's what Noms has on the menu:
 1.[D][ ] submit report (by: Oct 11 2019)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-003: Reject malformed typed commands

**Aim:**

Verify that missing descriptions or date sections are rejected without adding tasks. Error hints now reference the ISO date format.

**Inputs:**

```text
todo
deadline return book
event meeting /from 2019-08-06
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Oops! This todo is missing its main ingredient: a description.
Try: todo <description>
____________________________________________________________
 Oops! This deadline recipe is incomplete.
Try: deadline <description> /by yyyy-mm-dd
____________________________________________________________
 Oops! This event recipe needs more ingredients.
Try: event <description> /from yyyy-mm-dd /to yyyy-mm-dd
____________________________________________________________
 Noms's menu is empty! Feed me a task when you're ready!
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-004: Reject unknown commands

**Aim:**

Verify that an unrecognised command produces a helpful Noms-style error and does not terminate the program. The help text now lists `on` alongside the other commands.

**Inputs:**

```text
blah
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Oops! Grrr... Noms couldn't understand that command.
Try feeding me a todo, deadline, event, list, mark, unmark, delete, snooze, on, or bye.
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-005: Reject malformed deadlines

**Aim:**

Verify that deadlines without a valid `/by` section are rejected with a helpful format explanation showing the ISO date format.

**Inputs:**

```text
deadline return book
deadline return book /by
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Oops! This deadline recipe is incomplete.
Try: deadline <description> /by yyyy-mm-dd
____________________________________________________________
 Oops! This deadline recipe is incomplete.
Try: deadline <description> /by yyyy-mm-dd
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-006: Reject malformed events

**Aim:**

Verify that events without valid `/from` and `/to` sections are rejected with a helpful format explanation showing the ISO date format.

**Inputs:**

```text
event meeting
event meeting /from 2019-08-06
event meeting /from /to 2019-08-06
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Oops! This event recipe needs more ingredients.
Try: event <description> /from yyyy-mm-dd /to yyyy-mm-dd
____________________________________________________________
 Oops! This event recipe needs more ingredients.
Try: event <description> /from yyyy-mm-dd /to yyyy-mm-dd
____________________________________________________________
 Oops! This event recipe needs more ingredients.
Try: event <description> /from yyyy-mm-dd /to yyyy-mm-dd
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-007: Reject invalid mark and unmark task numbers

**Aim:**

Verify that missing, non-numeric, and out-of-range task numbers are handled without terminating Noms.

**Inputs:**

```text
todo anchor task
mark
mark abc
mark 0
mark 999999999999
unmark
unmark 1.5
unmark 99
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] anchor task
 Your menu now has 1 task.
____________________________________________________________
 Oops! Noms needs to know which task to mark.
Try: mark <task number>
____________________________________________________________
 Oops! Noms needs a whole task number to find the right menu item.
Try: mark 1
____________________________________________________________
 Oops! Task number 0 is out of range.
Choose a task number from 1 to 1.
____________________________________________________________
 Oops! That task number is too large for Noms.
Choose a task number from 1 to 1.
____________________________________________________________
 Oops! Noms needs to know which task to unmark.
Try: unmark <task number>
____________________________________________________________
 Oops! Noms needs a whole task number to find the right menu item.
Try: unmark 1
____________________________________________________________
 Oops! Task number 99 is out of range.
Choose a task number from 1 to 1.
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] anchor task
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-008: Delete a task and renumber the list

**Aim:**

Verify that deleting a task removes it, displays the deleted task, and renumbers the remaining tasks. Dates print in the display format.

**Inputs:**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
delete 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] read book
 Your menu now has 1 task.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] return book (by: Jun 06 2019)
 Your menu now has 2 tasks.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
 Your menu now has 3 tasks.
____________________________________________________________
 Noted. Noms has taken this task off the menu:
   [D][ ] return book (by: Jun 06 2019)
 Your menu now has 2 tasks.
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] read book
 2.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-009: Reject invalid delete task numbers

**Aim:**

Verify that malformed or out-of-range delete commands do not terminate Noms or change the task list.

**Inputs:**

```text
todo buy milk
delete
delete abc
delete 0
delete 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] buy milk
 Your menu now has 1 task.
____________________________________________________________
 Oops! Noms needs to know which task to delete.
Try: delete <task number>
____________________________________________________________
 Oops! Noms needs a whole task number to find the right menu item.
Try: delete 1
____________________________________________________________
 Oops! Task number 0 is out of range.
Choose a task number from 1 to 1.
____________________________________________________________
 Oops! Task number 2 is out of range.
Choose a task number from 1 to 1.
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] buy milk
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-010: Reject an empty command

**Aim:**

Verify that an empty input is rejected with a specific message and that Noms continues accepting commands afterwards. The help text now includes `on`.

**Inputs:**

```text

list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Oops! Noms needs a command. Try feeding me a todo, deadline, event, list, mark, unmark, delete, snooze, on, or bye.
____________________________________________________________
 Noms's menu is empty! Feed me a task when you're ready!
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```
### TC-011: Preserve task numbering after malformed additions

**Aim:**

Verify that malformed deadline and event commands interleaved with valid additions do not create hidden tasks or disturb task numbering.

**Inputs:**

```text
todo buy groceries
deadline submit report
 deadline ignored
deadline submit report /by 2019-08-30
event study session /from 2019-09-01
event study session /from 2019-09-01 /to 2019-09-02
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] buy groceries
 Your menu now has 1 task.
____________________________________________________________
 Oops! This deadline recipe is incomplete.
Try: deadline <description> /by yyyy-mm-dd
____________________________________________________________
 Oops! This deadline recipe is incomplete.
Try: deadline <description> /by yyyy-mm-dd
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] submit report (by: Aug 30 2019)
 Your menu now has 2 tasks.
____________________________________________________________
 Oops! This event recipe needs more ingredients.
Try: event <description> /from yyyy-mm-dd /to yyyy-mm-dd
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [E][ ] study session (from: Sep 01 2019 to: Sep 02 2019)
 Your menu now has 3 tasks.
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] buy groceries
 2.[D][ ] submit report (by: Aug 30 2019)
 3.[E][ ] study session (from: Sep 01 2019 to: Sep 02 2019)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-012: Preserve completion state after invalid mark operations

**Aim:**

Verify that invalid mark and unmark commands interleaved with valid state changes do not modify another task or corrupt completion state.

**Inputs:**

```text
todo wash dishes
deadline pay bills /by 2019-09-30
mark 1
mark 3
unmark abc
unmark 1
mark 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] wash dishes
 Your menu now has 1 task.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] pay bills (by: Sep 30 2019)
 Your menu now has 2 tasks.
____________________________________________________________
 Yum! Noms has marked this task as done:
   [T][X] wash dishes
____________________________________________________________
 Oops! Task number 3 is out of range.
Choose a task number from 1 to 2.
____________________________________________________________
 Oops! Noms needs a whole task number to find the right menu item.
Try: unmark 1
____________________________________________________________
 No worries! Noms has put this task back on the menu:
   [T][ ] wash dishes
____________________________________________________________
 Yum! Noms has marked this task as done:
   [D][X] pay bills (by: Sep 30 2019)
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] wash dishes
 2.[D][X] pay bills (by: Sep 30 2019)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-013: Continue correctly after empty and unknown commands

**Aim:**

Verify that empty and unknown commands interleaved with valid additions do not terminate Noms or change the task list.

**Inputs:**

```text
todo first

blah
deadline second /by 2019-12-31
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] first
 Your menu now has 1 task.
____________________________________________________________
 Oops! Noms needs a command. Try feeding me a todo, deadline, event, list, mark, unmark, delete, snooze, on, or bye.
____________________________________________________________
 Oops! Grrr... Noms couldn't understand that command.
Try feeding me a todo, deadline, event, list, mark, unmark, delete, snooze, on, or bye.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] second (by: Dec 31 2019)
 Your menu now has 2 tasks.
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] first
 2.[D][ ] second (by: Dec 31 2019)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-014: Reject deadlines and events with invalid dates

**Aim:**

Verify that dates that don't match `yyyy-mm-dd` are rejected with a helpful message and that no task is added.

**Inputs:**

```text
deadline return book /by Sunday
deadline return book /by 2/12/2019
event trip /from 2019-08-06 /to nextweek
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Oops! Noms couldn't read the date "Sunday".
Try the format yyyy-mm-dd (e.g. 2019-10-15).
____________________________________________________________
 Oops! Noms couldn't read the date "2/12/2019".
Try the format yyyy-mm-dd (e.g. 2019-10-15).
____________________________________________________________
 Oops! Noms couldn't read the date "nextweek".
Try the format yyyy-mm-dd (e.g. 2019-10-15).
____________________________________________________________
 Noms's menu is empty! Feed me a task when you're ready!
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-015: List tasks occurring on a given date

**Aim:**

Verify that the `on <yyyy-mm-dd>` command lists deadlines that fall on the date and events that span (inclusively) the date, and reports nothing for a date with no matches. Todos are never matched because they have no date.

**Inputs:**

```text
todo standalone chore
deadline return book /by 2019-12-01
deadline pay rent /by 2019-12-15
event conference /from 2019-11-30 /to 2019-12-02
event holiday /from 2019-12-20 /to 2019-12-30
on 2019-12-01
on 2019-12-25
on 2019-06-15
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] standalone chore
 Your menu now has 1 task.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] return book (by: Dec 01 2019)
 Your menu now has 2 tasks.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] pay rent (by: Dec 15 2019)
 Your menu now has 3 tasks.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [E][ ] conference (from: Nov 30 2019 to: Dec 02 2019)
 Your menu now has 4 tasks.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [E][ ] holiday (from: Dec 20 2019 to: Dec 30 2019)
 Your menu now has 5 tasks.
____________________________________________________________
 Tasks on Dec 01 2019:
   1. [D][ ] return book (by: Dec 01 2019)
   2. [E][ ] conference (from: Nov 30 2019 to: Dec 02 2019)
____________________________________________________________
 Tasks on Dec 25 2019:
   1. [E][ ] holiday (from: Dec 20 2019 to: Dec 30 2019)
____________________________________________________________
 Tasks on Jun 15 2019:
 (nothing on the menu that day)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-016: Reject invalid `on` commands

**Aim:**

Verify that `on` with a missing date or an unparseable date is rejected with the same friendly Noms message used for deadline/event dates, and that Noms keeps running.

**Inputs:**

```text
on
on Sunday
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Oops! Noms couldn't read the date "".
Try the format yyyy-mm-dd (e.g. 2019-10-15).
____________________________________________________________
 Oops! Noms couldn't read the date "Sunday".
Try the format yyyy-mm-dd (e.g. 2019-10-15).
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-017: Tasks persist across a restart

**Aim:**

Verify that tasks (now with `LocalDate` fields) saved by one run of Noms
are loaded back in and shown correctly when Noms is started again against
the same save file.

**Note:** unlike every other case in this plan, this test case runs the
program **twice** in sequence against the same working directory, so that
the second run's `list` reflects what the first run saved. Delete
`./data/noms.txt` before the *first* run only; leave it in place between the
first and second run.

**First run — inputs:**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
mark 2
bye
```

**First run — expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] read book
 Your menu now has 1 task.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] return book (by: Jun 06 2019)
 Your menu now has 2 tasks.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
 Your menu now has 3 tasks.
____________________________________________________________
 Yum! Noms has marked this task as done:
   [D][X] return book (by: Jun 06 2019)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

**Second run — inputs:**

```text
list
bye
```

**Second run — expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] read book
 2.[D][X] return book (by: Jun 06 2019)
 3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-018: Descriptions with `|` and `\` survive a restart

**Aim:**

Verify that a `|` or `\` typed as part of a task's own text does not get
misread as a save-file field separator, and round-trips correctly across a
restart instead of being corrupted or truncated.

**Note:** like TC-017, this runs the program twice against the same working
directory. Delete `./data/noms.txt` before the *first* run only.

**First run — inputs:**

```text
todo buy milk | bread
deadline audit C:\logs\a\|b /by 2019-11-08
bye
```

**First run — expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] buy milk | bread
 Your menu now has 1 task.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] audit C:\logs\a\|b (by: Nov 08 2019)
 Your menu now has 2 tasks.
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

**Second run — inputs:**

```text
list
bye
```

**Second run — expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] buy milk | bread
 2.[D][ ] audit C:\logs\a\|b (by: Nov 08 2019)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-019: Skip a spoiled line in the save file

**Aim:**

Verify that a save file containing an unparseable line is loaded without
crashing: the good tasks still load, the bad line is skipped, and Noms
reports it with the standard error message. This exercises the path where
`Storage.load()` records skipped lines and Noms reports them through the
`Ui`, so that no console output originates outside `Ui`.

**Note:** unlike the other cases, this case **pre-seeds** `./data/noms.txt`
before the run instead of deleting it. Create `./data/noms.txt` with
exactly these five lines, then start Noms:

```text
T | 0 | good task
T | 2 | invalid completion flag
T | 0 | unexpected field | extra
GARBAGE LINE
D | 1 | pay rent | 2019-12-15
```

The skipped-entry warning is printed while the file is loaded (during
construction), so it appears **before** the welcome banner.

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
 Oops! Noms found a spoiled entry in the save file and skipped it: T | 2 | invalid completion flag
____________________________________________________________
 Oops! Noms found a spoiled entry in the save file and skipped it: T | 0 | unexpected field | extra
____________________________________________________________
 Oops! Noms found a spoiled entry in the save file and skipped it: GARBAGE LINE
____________________________________________________________
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] good task
 2.[D][X] pay rent (by: Dec 15 2019)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-020: Find tasks by keyword

**Aim:**

Verify that `find <keyword>` lists every task whose description contains the keyword (case-insensitively, in list order), and reports a friendly "nothing found" message when no task matches. Matching is on the description text only, so a keyword that appears in a date is not matched.

**Inputs:**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
find book
find BOOK
find holiday
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] read book
 Your menu now has 1 task.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] return book (by: Jun 06 2019)
 Your menu now has 2 tasks.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
 Your menu now has 3 tasks.
____________________________________________________________
 Yum! Noms dug up these matching tasks for "book":
   1. [T][ ] read book
   2. [D][ ] return book (by: Jun 06 2019)
____________________________________________________________
 Yum! Noms dug up these matching tasks for "BOOK":
   1. [T][ ] read book
   2. [D][ ] return book (by: Jun 06 2019)
____________________________________________________________
 Hmm, Noms sniffed around but found no tasks matching "holiday".
 Nothing on the menu to nibble on!
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-021: Reject `find` with no keyword

**Aim:**

Verify that a bare `find` (or `find` followed only by whitespace) is rejected with a friendly Noms message showing the correct format, and that Noms keeps running.

**Inputs:**

```text
find
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Oops! Noms can't sniff out a task without a scent!
Tell Noms a keyword to hunt for.
Try: find <keyword>
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-022: Snooze deadlines and events

**Aim:**

Verify that a deadline accepts `/by`, an event accepts either `/from` alone
or `/from` with `/to`, an event keeps its duration when only `/from` is given,
and snoozing preserves completion state and list order.

**Inputs:**

```text
deadline submit report /by 2026-09-15
event conference /from 2026-09-10 /to 2026-09-12
mark 1
snooze 1 /by 2026-09-20
snooze 2 /from 2026-09-20
snooze 2 /from 2026-10-01 /to 2026-10-05
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] submit report (by: Sep 15 2026)
 Your menu now has 1 task.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [E][ ] conference (from: Sep 10 2026 to: Sep 12 2026)
 Your menu now has 2 tasks.
____________________________________________________________
 Yum! Noms has marked this task as done:
   [D][X] submit report (by: Sep 15 2026)
____________________________________________________________
 Nom nom! Noms has snoozed this task:
   [D][X] submit report (by: Sep 20 2026)
____________________________________________________________
 Nom nom! Noms has snoozed this task:
   [E][ ] conference (from: Sep 20 2026 to: Sep 22 2026)
____________________________________________________________
 Nom nom! Noms has snoozed this task:
   [E][ ] conference (from: Oct 01 2026 to: Oct 05 2026)
____________________________________________________________
 Here's what Noms has on the menu:
 1.[D][X] submit report (by: Sep 20 2026)
 2.[E][ ] conference (from: Oct 01 2026 to: Oct 05 2026)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-023: Reject unsupported and malformed snooze commands

**Aim:**

Verify that todos cannot be snoozed, each dated task requires its own command
markers, invalid dates reuse the normal date error, and rejected commands do
not change any task.

**Inputs:**

```text
todo read book
deadline submit report /by 2026-09-15
event conference /from 2026-09-10 /to 2026-09-12
snooze 1 /by 2026-09-20
snooze 2 /from 2026-09-20
snooze 3 /by 2026-09-20
snooze 2 /by tomorrow
snooze 3 /from 2026-09-20 /to
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] read book
 Your menu now has 1 task.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] submit report (by: Sep 15 2026)
 Your menu now has 2 tasks.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [E][ ] conference (from: Sep 10 2026 to: Sep 12 2026)
 Your menu now has 3 tasks.
____________________________________________________________
 Oops! Noms can only snooze deadlines and events.
Try choosing a task that has a date.
____________________________________________________________
 Oops! This deadline snooze recipe is incomplete.
Try: snooze <task number> /by yyyy-mm-dd
____________________________________________________________
 Oops! This event snooze recipe is incomplete.
Try: snooze <task number> /from yyyy-mm-dd [/to yyyy-mm-dd]
____________________________________________________________
 Oops! Noms couldn't read the date "tomorrow".
Try the format yyyy-mm-dd (e.g. 2019-10-15).
____________________________________________________________
 Oops! This event snooze recipe is incomplete.
Try: snooze <task number> /from yyyy-mm-dd [/to yyyy-mm-dd]
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] read book
 2.[D][ ] submit report (by: Sep 15 2026)
 3.[E][ ] conference (from: Sep 10 2026 to: Sep 12 2026)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-024: Reject invalid snooze task numbers

**Aim:**

Verify that missing, non-numeric, and out-of-range task numbers produce the
existing task-number errors without changing the selected task.

**Inputs:**

```text
deadline submit report /by 2026-09-15
snooze
snooze abc /by 2026-09-20
snooze 2 /by 2026-09-20
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] submit report (by: Sep 15 2026)
 Your menu now has 1 task.
____________________________________________________________
 Oops! Noms needs to know which task to snooze.
Try: snooze <task number>
____________________________________________________________
 Oops! Noms needs a whole task number to find the right menu item.
Try: snooze 1
____________________________________________________________
 Oops! Task number 2 is out of range.
Choose a task number from 1 to 1.
____________________________________________________________
 Here's what Noms has on the menu:
 1.[D][ ] submit report (by: Sep 15 2026)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-025: Reject invalid event date ranges

**Aim:**

Verify that an event and an event snooze require an end date strictly after
the start date, and that rejected ranges do not add or change a task.

**Inputs:**

```text
event trip /from 2026-10-05 /to 2026-10-01
event conference /from 2026-10-01 /to 2026-10-03
snooze 1 /from 2026-10-05 /to 2026-10-05
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Oops! This event's end date must be after its start date.
Try an end date later than the start date.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [E][ ] conference (from: Oct 01 2026 to: Oct 03 2026)
 Your menu now has 1 task.
____________________________________________________________
 Oops! This event's end date must be after its start date.
Try an end date later than the start date.
____________________________________________________________
 Here's what Noms has on the menu:
 1.[E][ ] conference (from: Oct 01 2026 to: Oct 03 2026)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-026: Normalize surrounding command whitespace

**Aim:**

Verify that leading whitespace and repeated whitespace around structural
markers do not corrupt task descriptions or dates. JUnit also covers tabs and
trailing whitespace.

**Inputs:**

```text
  todo read book
  deadline   return book   /by   2019-06-06
  event   conference   /from   2019-08-06   /to   2019-08-07
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] read book
 Your menu now has 1 task.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] return book (by: Jun 06 2019)
 Your menu now has 2 tasks.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [E][ ] conference (from: Aug 06 2019 to: Aug 07 2019)
 Your menu now has 3 tasks.
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] read book
 2.[D][ ] return book (by: Jun 06 2019)
 3.[E][ ] conference (from: Aug 06 2019 to: Aug 07 2019)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-027: Reject arguments for parameterless commands

**Aim:**

Verify that `list` and `bye` reject extra arguments, that malformed `bye`
does not exit, and that valid forms continue to work afterwards.

**Inputs:**

```text
todo keep working
list extra
bye now
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] keep working
 Your menu now has 1 task.
____________________________________________________________
 Oops! Noms doesn't need extra ingredients for the list command.
Try: list
____________________________________________________________
 Oops! Noms doesn't need extra ingredients for the bye command.
Try: bye
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] keep working
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```

### TC-028: Reject tasks with duplicate details

**Aim:**

Verify that Noms rejects repeated task details, keeps the original task, and
still permits the same description when the task type or date differs.

**Inputs:**

```text
todo read book
todo read book
deadline read book /by 2019-06-06
deadline read book /by 2019-06-06
deadline read book /by 2019-06-07
list
bye
```

**Expected output:**

```text
____________________________________________________________
 _   _  ___  __  __  ____
| \ | |/ _ \|  \/  |/ ___|
|  \| | | | | |\/| | \___ \
| |\  | |_| | |  | |  ___) |
|_| \_|\___/|_|  |_| |____/
____________________________________________________________
Hi! I'm Noms, your hungry little task monster. What's on the menu today?
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [T][ ] read book
 Your menu now has 1 task.
____________________________________________________________
 Oops! Noms already has that task on the menu.
Try adding a task with different details.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] read book (by: Jun 06 2019)
 Your menu now has 2 tasks.
____________________________________________________________
 Oops! Noms already has that task on the menu.
Try adding a task with different details.
____________________________________________________________
 Yum! Noms has gobbled up your new task:
   [D][ ] read book (by: Jun 07 2019)
 Your menu now has 3 tasks.
____________________________________________________________
 Here's what Noms has on the menu:
 1.[T][ ] read book
 2.[D][ ] read book (by: Jun 06 2019)
 3.[D][ ] read book (by: Jun 07 2019)
____________________________________________________________
All done! Noms is full for now. See you next time!
____________________________________________________________
```
