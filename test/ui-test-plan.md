# Bott UI test plan

This file records the text-UI test cases for Bott. They are run by the `test-ui` skill
(`.claude/skills/test-ui/SKILL.md`), which feeds each test case's **Input** lines to the compiled
program's standard input and checks the program's standard output against **Expected output**,
character for character (including whitespace, indentation, and blank lines).

Each test case is a single, independent run of the program: its Input lines are sent to one fresh
invocation of `Bott`, in order, ending with `bye`. Expected output is the *entire* console output for
that run, from the first line of the startup banner to the final blank line after the farewell message.
The exception is TC11, which spans two runs to check that tasks saved to `data/bott.txt` are reloaded on
the next run — see its Aim for details.

See `AGENTS.md` for when this plan and the `test-ui` skill should be updated/run.

## Test cases

### TC1 — Startup and immediate exit

**Aim:** The program greets the user on startup and exits cleanly on `bye` when no tasks are added.

**Input:**
```
bye
```

**Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC2 — Add a todo and list it

**Aim:** `todo <description>` stores a todo task and acknowledges it with a `[T]` icon; `list` shows it
numbered.

**Input:**
```
todo borrow book
list
bye
```

**Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [T][ ] borrow book
     You now have 1 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Roll call! Here's your mission roster:
     1.[T][ ] borrow book
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC3 — Add a deadline

**Aim:** `deadline <description> /by <yyyy-MM-dd>` stores a deadline task and acknowledges it with a
`[D]` icon and its `(by: ...)` suffix; the stored date is parsed from `yyyy-MM-dd` input and displayed
in `MMM dd yyyy` format.

**Input:**
```
deadline return book /by 2019-10-15
bye
```

**Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [D][ ] return book (by: Oct 15 2019)
     You now have 1 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC4 — Add an event

**Aim:** `event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>` stores an event task and acknowledges
it with an `[E]` icon and its `(from: ... to: ...)` suffix; both dates are parsed from `yyyy-MM-dd` input
and displayed in `MMM dd yyyy` format.

**Input:**
```
event project meeting /from 2019-08-06 /to 2019-08-07
bye
```

**Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
     You now have 1 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC5 — Mark and unmark a task

**Aim:** `mark <n>` marks the nth task done (`[X]`) and `unmark <n>` reverts it to not done (`[ ]`); both
changes are reflected by a following `list`.

**Input:**
```
todo read book
mark 1
list
unmark 1
list
bye
```

**Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [T][ ] read book
     You now have 1 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Outstanding! Mission accomplished:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Roll call! Here's your mission roster:
     1.[T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     At ease. Mission's back on the roster:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Roll call! Here's your mission roster:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC6 — Mixed task types in one list

**Aim:** Todos, deadlines, and events can be mixed in the same list, keep their own icons and suffixes,
and are numbered by insertion order regardless of type; marking a non-todo task works the same way as
marking a todo.

**Input:**
```
todo read book
todo return book
deadline submit report /by 2019-10-11
event orientation week /from 2019-10-04 /to 2019-10-11
mark 1
mark 3
list
bye
```

**Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [T][ ] read book
     You now have 1 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [T][ ] return book
     You now have 2 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [D][ ] submit report (by: Oct 11 2019)
     You now have 3 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [E][ ] orientation week (from: Oct 04 2019 to: Oct 11 2019)
     You now have 4 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Outstanding! Mission accomplished:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Outstanding! Mission accomplished:
       [D][X] submit report (by: Oct 11 2019)
    ____________________________________________________________

    ____________________________________________________________
     Roll call! Here's your mission roster:
     1.[T][X] read book
     2.[T][ ] return book
     3.[D][X] submit report (by: Oct 11 2019)
     4.[E][ ] orientation week (from: Oct 04 2019 to: Oct 11 2019)
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC7 — Empty todo description and unknown command

**Aim:** The two minimal required errors: `todo` with no description, and a command Bott doesn't
recognize, each produce a specific "NEGATIVE, RECRUIT!" message rather than crashing or being silently ignored.

**Input:**
```
todo
blah
bye
```

**Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! A todo needs a description. Try: todo <description>
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! That's not an order I recognize: "blah". Try: list, todo, deadline, event, duration, find, mark, unmark, delete, or bye.
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC8 — Task-number and deadline/event syntax errors, then recovery

**Aim:** `mark`/`unmark` on a task number that doesn't exist, a `deadline` missing its `/by` marker, and
an `event` missing its `/to` marker each get a specific, actionable error message, and a bad command
doesn't corrupt state or stop later valid commands from working.

**Input:**
```
mark 1
deadline foo
event foo /from Mon
bye
```

**Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! There is no task number 1 in your list. You currently have 0 task(s).
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! A deadline needs a "/by" date. Try: deadline <description> /by <yyyy-MM-dd>
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! An event needs a "/to" end date after its "/from" start date. Try: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC9 — Delete a task

**Aim:** `delete <n>` removes the nth task, acknowledges it by echoing the removed task and the new
count, and every task after it shifts down by one number on the next `list`; deleting continues to work
correctly across repeated deletes.

**Input:**
```
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-06
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
delete 1
list
bye
```

**Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [T][ ] read book
     You now have 1 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [D][ ] return book (by: Jun 06 2019)
     You now have 2 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
     You now have 3 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [T][ ] join sports club
     You now have 4 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [T][ ] borrow book
     You now have 5 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Outstanding! Mission accomplished:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Outstanding! Mission accomplished:
       [D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Outstanding! Mission accomplished:
       [T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Roll call! Here's your mission roster:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
     3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
     4.[T][X] join sports club
     5.[T][ ] borrow book
    ____________________________________________________________

    ____________________________________________________________
     Mission scrubbed, recruit! Fall out:
       [E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
     You now have 4 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Roll call! Here's your mission roster:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
     3.[T][X] join sports club
     4.[T][ ] borrow book
    ____________________________________________________________

    ____________________________________________________________
     Mission scrubbed, recruit! Fall out:
       [T][X] read book
     You now have 3 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Roll call! Here's your mission roster:
     1.[D][X] return book (by: Jun 06 2019)
     2.[T][X] join sports club
     3.[T][ ] borrow book
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC10 — Delete syntax and range errors

**Aim:** `delete` with no number, a non-numeric number, and a number with no matching task (on an empty
list) each get the same specific error phrasing already used by `mark`/`unmark`, since `delete` shares
their task-number parsing.

**Input:**
```
delete
delete abc
delete 1
bye
```

**Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! Please specify a task number. Try: delete <task number>
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! "abc" is not a valid task number. Try: delete <task number>
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! There is no task number 1 in your list. You currently have 0 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC11 — Tasks persist across restarts

**Aim:** Tasks saved to `data/bott.txt` during one run of the program, including their done/not-done
status, are reloaded the next time the program starts. Unlike the other test cases, this one is two
separate runs of the program that share the same `data/bott.txt` (do not delete it between the two runs).

**Run A — Input:**
```
todo read book
deadline return book /by 2019-06-06
mark 1
bye
```

**Run A — Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [T][ ] read book
     You now have 1 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [D][ ] return book (by: Jun 06 2019)
     You now have 2 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Outstanding! Mission accomplished:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

**Run B — Input:**
```
list
bye
```

**Run B — Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     Roll call! Here's your mission roster:
     1.[T][X] read book
     2.[D][ ] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC12 — Invalid deadline/event dates

**Aim:** A `deadline`/`event` date that isn't valid `yyyy-MM-dd` — wrong format, or a calendar date that
doesn't exist — gets a specific error naming the bad value and the expected format, rather than crashing;
the task isn't added, and a later valid command still works.

**Input:**
```
deadline return book /by 15/10/2019
event trip /from 2019-08-04 /to not-a-date
deadline check leap day /by 2021-02-29
todo valid task
list
bye
```

**Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! The "by" date must be in yyyy-MM-dd format (e.g. 2019-10-15). "15/10/2019" is not a valid date.
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! The "to" date must be in yyyy-MM-dd format (e.g. 2019-10-15). "not-a-date" is not a valid date.
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! The "by" date must be in yyyy-MM-dd format (e.g. 2019-10-15). "2021-02-29" is not a valid date.
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [T][ ] valid task
     You now have 1 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Roll call! Here's your mission roster:
     1.[T][ ] valid task
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC13 — Find tasks by keyword

**Aim:** `find <keyword>` lists only the tasks whose description contains the keyword, matching
case-insensitively, renumbered from 1 regardless of the tasks' positions in the full list; a keyword that
matches nothing shows just the header with no tasks listed; `find` with no keyword produces a specific
error; none of this changes the underlying list, as the closing `list` shows.

**Input:**
```
todo read book
deadline return book /by 2019-06-06
todo join sports club
mark 1
mark 2
find book
find BOOK
find zzz
find
list
bye
```

**Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [T][ ] read book
     You now have 1 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [D][ ] return book (by: Jun 06 2019)
     You now have 2 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [T][ ] join sports club
     You now have 3 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Outstanding! Mission accomplished:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Outstanding! Mission accomplished:
       [D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Found these missions matching your intel:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Found these missions matching your intel:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Found these missions matching your intel:
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! Please specify a keyword to search for. Try: find <keyword>
    ____________________________________________________________

    ____________________________________________________________
     Roll call! Here's your mission roster:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
     3.[T][ ] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC14 — Fixed-duration tasks: add, list, and persist across restarts

**Aim:** `duration <description> /for <amount>` stores a task with a `[F]` icon and no
start/end time; the amount accepts `Xh`, `Ym`, and combined `XhYm`, and displays as
`(for: ...)` with a zero component dropped. Like TC11, this is two separate runs sharing the
same `data/bott.txt` (do not delete it between the two runs) — it checks that fixed-duration
tasks, including their done status, reload on the next start.

**Run A — Input:**
```
duration read sales report /for 2h
duration call bank /for 45m
duration deep work block /for 1h30m
mark 2
bye
```

**Run A — Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [F][ ] read sales report (for: 2h)
     You now have 1 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [F][ ] call bank (for: 45m)
     You now have 2 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [F][ ] deep work block (for: 1h 30m)
     You now have 3 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Outstanding! Mission accomplished:
       [F][X] call bank (for: 45m)
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

**Run B — Input:**
```
list
bye
```

**Run B — Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     Roll call! Here's your mission roster:
     1.[F][ ] read sales report (for: 2h)
     2.[F][X] call bank (for: 45m)
     3.[F][ ] deep work block (for: 1h 30m)
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```

### TC15 — Invalid fixed-duration inputs

**Aim:** A `duration` command missing its description, missing `/for`, missing the amount,
giving a number with no unit, or giving a combined amount whose minutes exceed 59 each gets a
specific error and adds nothing; a following valid `duration` command still works.

**Input:**
```
duration
duration read report
duration read report /for
duration read report /for 2
duration read report /for 1h90m
duration read report /for 2h
list
bye
```

**Expected output:**
```
    ____________________________________________________________
 ____        _       _____      ____    _____ 
/ ___|      / \     |  __ \    / ___|  |  ___|
\___ \     / _ \    | |__) |  | |  _   | |__  
 ___) |   / ___ \   |  _  /   | |_| |  |  __| 
|____/   /_/   \_\  |_|  \_\   \____|  |_____|
    ____________________________________________________________
     Ten-hut! Sergeant Bott reporting for duty.
     What's your first order, recruit?
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! A fixed-duration task needs a description. Try: duration <description> /for <2h30m>
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! A fixed-duration task needs a "/for" duration. Try: duration <description> /for <2h30m>
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! The "for" duration of a fixed-duration task cannot be empty. Try: duration <description> /for <2h30m>
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! "2" is not a valid duration. Use a number with a unit, e.g. 2h, 30m, or 1h30m.
    ____________________________________________________________

    ____________________________________________________________
     NEGATIVE, RECRUIT! In a combined duration like 1h30m, the minutes must be 0-59. Try: duration <description> /for <2h30m>
    ____________________________________________________________

    ____________________________________________________________
     Mission logged, recruit! Fall in:
       [F][ ] read report (for: 2h)
     You now have 1 mission(s) on the roster.
    ____________________________________________________________

    ____________________________________________________________
     Roll call! Here's your mission roster:
     1.[F][ ] read report (for: 2h)
    ____________________________________________________________

    ____________________________________________________________
     Dismissed! Fall out, recruit.
    ____________________________________________________________

```
