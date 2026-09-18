# Text UI test plan

Manual-style tests for the text UI of `AlfredTheButler`, run automatically by the
`test-ui` skill (`.claude/skills/test-ui/SKILL.md`).

## How a test case is run

1. All sources in `src/main/java` are compiled to a temporary build directory.
1. For **each** test case, the whole `data` folder is deleted, then a **fresh**
   program is started and the case's `Input` lines are fed to it on standard
   input, one per line.
1. The program's whole console output is compared with the case's
   `Expected output`.

The program reads `data/alfred.txt` at startup and rewrites it after every
change, so deleting it before each case is what keeps cases independent: tasks
added in one case never leak into the next. This is why every case that needs a
task list has to add the tasks itself.

The **folder** is deleted, not just the file, which means every case that does
not seed a save file starts from exactly what a fresh copy of this project on
another computer looks like: no save file, and no folder to hold one. The
program has to create both on demand, so that path is exercised by every such
case rather than needing one of its own.

A **two-run case** is the exception, and the only way to test that tasks survive
a restart. It has three fenced blocks instead of two: two inputs, run one after
the other against the _same_ save file, and one expected output holding both
runs' console output end to end. The file is cleared before the case, not
between its two runs.

## Rules for writing a test case

* Every fenced block is introduced by a bold label saying what it is, and the
  label is what the runner goes by, not the order. A one-run case has
  **Input** and **Expected output**; a two-run case has **First input**,
  **Second input**, and **Expected output**. The aim is the prose above them.
* A case may add a **Save file** block, whose contents become `data/alfred.txt`
  before the case runs. That is the only way to test what the program does with
  a file it did not write, since it never saves a line it cannot read back.
* The last input line is normally `bye`, but does not have to be: input that
  simply runs out is treated the same way, which TC16 proves. Extra words after
  the keyword are ignored, so `bye now` also exits; only TC8 relies on that.
* `Expected output` is the **entire** console output of the run, starting with
  the greeting banner and ending with the farewell message.
* Comparison is line-by-line and exact, except that trailing whitespace,
  Windows/Unix line-ending differences, and blank lines at the very end of the
  output are ignored. Those differ between machines without being real bugs.
* Cases run top to bottom. The session stops at the first failing case.

Note that the greeting banner is repeated in every expected output. That is
deliberate: each case is readable on its own. A more compact alternative would
be to store the banner once and have the runner prepend it, at the cost of an
extra rule to remember when reading the file.

---

## TC1: Startup and exit

**Aim:** The greeting (banner, name, prompt) is printed once at startup, and
`bye` exits with the farewell message and nothing else.

**Input:**

```
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC2: Add todos and list them

**Aim:** `todo` stores a task and confirms it with a running count, and `list`
shows the stored tasks numbered from 1 in the order they were added.

**Input:**

```
todo read book
todo buy milk
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] buy milk
     That makes 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] read book
     2.[T][ ] buy milk
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC3: Mark and unmark a task

**Aim:** `mark <n>` and `unmark <n>` change the status box of the n-th task
only, and the change survives until it is reversed (checked with `list` after
each command).

**Input:**

```
todo read book
todo buy milk
mark 2
list
unmark 2
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] buy milk
     That makes 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Splendid, sir. I've marked this task as done:
       [T][X] buy milk
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] read book
     2.[T][X] buy milk
    ____________________________________________________________

    ____________________________________________________________
     Very well, sir. I've marked this task as not done yet:
       [T][ ] buy milk
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] read book
     2.[T][ ] buy milk
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC4: Add a deadline

**Aim:** `deadline <description> /by <date>` splits the line at `/by`, reads the
date as a real date rather than as the words that were typed, stores a deadline,
and displays it as `[D]` with its due date in brackets.

The date goes in as `2019-10-15`, the `yyyy-mm-dd` form the program reads, and
comes out as `Oct 15 2019`, the form a reader gets. That the two differ is the
assertion: text that had merely been carried through would come back exactly as
it was typed, so a display in the input's own form would prove nothing.

**Input:**

```
deadline return book /by 2019-10-15
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [D][ ] return book (by: Oct 15 2019)
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[D][ ] return book (by: Oct 15 2019)
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC5: Add an event

**Aim:** `event <description> /from <start> /to <end>` splits the line at both
keywords, reads both dates, stores an event, and displays it as `[E]` with its
start and end dates.

The start and the end are different days, so a start wrongly stored in place of
the end, or the reverse, would show up rather than being hidden by two equal
values. Both are shown as `MMM dd yyyy`, the same reader's form a deadline
gets.

**Input:**

```
event project meeting /from 2019-12-02 /to 2019-12-03
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC6: Mixed task types in one list

**Aim:** The three task types can coexist in one list, keep their own type
boxes, are numbered in the order they were added, and `mark` picks the right one
by number regardless of type.

**Input:**

```
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2019-12-02 /to 2019-12-03
mark 2
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] borrow book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [D][ ] return book (by: Oct 15 2019)
     That makes 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
     That makes 3 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Splendid, sir. I've marked this task as done:
       [D][X] return book (by: Oct 15 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] borrow book
     2.[D][X] return book (by: Oct 15 2019)
     3.[E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC7: Blank input is refused

**Aim:** A line with nothing on it draws an error message instead of being
stored as a task with an empty description, and the loop carries on afterwards
rather than exiting.

The case feeds two blank lines: one truly empty, one holding only spaces. The
second checks that the line is judged by its content rather than its length,
though the two are indistinguishable when reading this file. That is
deliberately harmless: if an editor ever strips the trailing spaces, the line
becomes a second empty line and the expected output is unchanged.

**Input:**

```

   
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     You'll have to give me something to work with, sir.
    ____________________________________________________________

    ____________________________________________________________
     You'll have to give me something to work with, sir.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC8: Spacing and extra words do not stop a command

**Aim:** A command is found by its first word, so spaces around the line are
ignored and words after the keyword are ignored too. `   list   ` and
`list all` both list, and `bye now` still exits.

The list is left empty on purpose, so the case tests only how the line is read
and does not have to be rewritten whenever the way tasks are added changes.

**Input:**

```
   list   
list all
bye now
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC9: Add todos with the `todo` keyword

**Aim:** `todo <description>` stores a todo described by the words after the
keyword, not by the whole line, and it is displayed and numbered exactly like a
todo added any other way.

The keyword is now the only way to add a todo; a bare description is refused,
which TC10 covers. This case overlaps with TC2 on purpose: TC2 is about the
running count and the numbering, this one about the keyword and how much of the
line becomes the description.

**Input:**

```
todo read book
todo return the Batmobile
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] return the Batmobile
     That makes 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] read book
     2.[T][ ] return the Batmobile
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC10: Unrecognized commands are refused

**Aim:** A word that is not a command draws an error quoting that word back,
rather than being stored as a task. Covers both a mistyped command (`lst`) and
a bare description of the kind that used to become a todo (`borrow book`).

The closing `list` is the point of the case: it shows an empty list, proving
the two refused lines stored nothing rather than being quietly accepted.

**Input:**

```
lst
borrow book
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     I'm afraid I don't know 'lst', sir.
    ____________________________________________________________

    ____________________________________________________________
     I'm afraid I don't know 'borrow', sir.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC11: Task commands with missing parts are refused

**Aim:** `todo`, `deadline`, and `event` each check that every part they need is
present, and refuse the command with one message naming all of those parts
rather than crashing or storing a half-built task.

The six inputs cover every way a part can go missing: no arguments at all, a
keyword with no separator, a separator with nothing before it, and a separator
with nothing after it. `deadline /by Sunday` is the case that needs the
separator to be matched without its surrounding spaces; matched with them, it
would look like a missing `/by` rather than a missing description.

`Sunday` is deliberately not a date. The case still expects the message about
the missing description, which is what shows the parts are checked for presence
before the date is read, rather than the other way around.

As in TC10, the closing `list` is the assertion that matters: an empty list
shows none of the six was stored.

**Input:**

```
todo
deadline return book
deadline /by Sunday
deadline return book /by
event project meeting
event project meeting /from Mon 2pm
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     A todo needs a description, sir.
    ____________________________________________________________

    ____________________________________________________________
     A deadline needs a description and a /by date, sir.
    ____________________________________________________________

    ____________________________________________________________
     A deadline needs a description and a /by date, sir.
    ____________________________________________________________

    ____________________________________________________________
     A deadline needs a description and a /by date, sir.
    ____________________________________________________________

    ____________________________________________________________
     An event needs a description, a /from date, and a /to date, sir.
    ____________________________________________________________

    ____________________________________________________________
     An event needs a description, a /from date, and a /to date, sir.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC12: Bad task numbers are refused

**Aim:** `mark`, `unmark`, and `delete` check the number they are given before
using it. Anything that is not a number, and any number that does not name a
task in the list, draws a message rather than a crash.

The inputs cover both messages and every route to them: a word, no number at
all, a negative, zero, a number past the end of the list, and a number given
when the list is empty. The first line runs before anything has been added,
which is the empty-list case; it shares the out-of-range message rather than
having one of its own.

All three commands read their number through the same helper, so the two
`delete` lines are there to show that `delete` uses it too, rather than to
retest the checks themselves.

The closing `list` shows the task still present and still unmarked, proving
that none of the refused commands changed anything on its way to being
refused — in particular that a refused `delete` removed nothing.

**Input:**

```
mark 1
todo read book
mark abc
mark
mark -1
mark 0
mark 2
unmark 9
delete xyz
delete 2
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     There is no such task, sir.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     That is not a task number, sir.
    ____________________________________________________________

    ____________________________________________________________
     That is not a task number, sir.
    ____________________________________________________________

    ____________________________________________________________
     There is no such task, sir.
    ____________________________________________________________

    ____________________________________________________________
     There is no such task, sir.
    ____________________________________________________________

    ____________________________________________________________
     There is no such task, sir.
    ____________________________________________________________

    ____________________________________________________________
     There is no such task, sir.
    ____________________________________________________________

    ____________________________________________________________
     That is not a task number, sir.
    ____________________________________________________________

    ____________________________________________________________
     There is no such task, sir.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC13: Delete a task

**Aim:** `delete <n>` removes the n-th task, confirms it by showing the task
that was removed and the number left, and the tasks after it move up so the
list stays numbered from 1 with no gap.

The task deleted is the middle one of three, which is what makes the
renumbering visible: after `delete 2` the third task has to become number 2.
The two `list` commands bracket the deletion so the before and after can be
compared directly. Deleting the last remaining task afterwards checks the
count reaches zero and the list comes back empty rather than breaking.

**Input:**

```
todo read book
deadline return book /by 2019-10-15
event project meeting /from 2019-12-02 /to 2019-12-03
list
delete 2
list
delete 1
delete 1
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [D][ ] return book (by: Oct 15 2019)
     That makes 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
     That makes 3 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] read book
     2.[D][ ] return book (by: Oct 15 2019)
     3.[E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
    ____________________________________________________________

    ____________________________________________________________
     As you wish, sir. I've removed this task:
       [D][ ] return book (by: Oct 15 2019)
     That leaves 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] read book
     2.[E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
    ____________________________________________________________

    ____________________________________________________________
     As you wish, sir. I've removed this task:
       [T][ ] read book
     That leaves 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     As you wish, sir. I've removed this task:
       [E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
     That leaves 0 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC14: Tasks survive a restart

**Aim:** Tasks saved by one run are read back by the next. All three types come
back, with their descriptions, their dates, their done status, and their
original order intact, and the count of restored tasks is announced once before
the first command.

This is a two-run case: the first run builds and marks a list and exits, the
second starts against the file the first left behind and does nothing but
`list`. The task marked in the first run is the one that proves the status
digit is read as well as written, and the deadline and event prove the fields
after the description survive the round trip.

Those fields are now dates rather than free text, so this case carries the
weight of the date handling: the second run rebuilds each one with
`LocalDate.parse`, and a date written to the file in a form that could not be
read back would be dropped as a damaged line and show up here as a missing
task.

It is also what holds the two date formats apart. The file keeps `yyyy-mm-dd`,
because that is the form that reads back exactly, while the list shows
`MMM dd yyyy`. Saving what the user is shown would round-trip a month name
through `LocalDate.parse`, and this case is where that would surface, as three
tasks that failed to come back.

**First input:**

```
todo read book
deadline return book /by 2019-10-15
event project meeting /from 2019-12-02 /to 2019-12-03
mark 1
bye
```

**Second input:**

```
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [D][ ] return book (by: Oct 15 2019)
     That makes 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
     That makes 3 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Splendid, sir. I've marked this task as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________

    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     I've brought back 3 tasks from last time, sir.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][X] read book
     2.[D][ ] return book (by: Oct 15 2019)
     3.[E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC15: An emptied list brings nothing back

**Aim:** A run that ends with no tasks leaves nothing for the next run to
restore. The next run says nothing about loading and starts with an empty list,
rather than announcing zero tasks or bringing back the deleted one.

The first run adds a task and deletes it again, so the save file is emptied
rather than never written. That is the case that would break if deleting the
last task left the file untouched, or if the greeting announced a count
unconditionally.

**First input:**

```
todo read book
delete 1
bye
```

**Second input:**

```
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     As you wish, sir. I've removed this task:
       [T][ ] read book
     That leaves 0 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________

    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC16: Input that runs out is treated as `bye`

**Aim:** Input that ends without an exit command finishes the session cleanly,
with the same farewell as `bye`, instead of failing to read a line that is not
there.

The input deliberately has no `bye`. Before this was handled, the program threw
`NoSuchElementException` and printed a stack trace after the last command, which
is what a redirected or piped session hits every time it reaches the end of its
file.

**Input:**

```
todo read book
list
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC17: A description containing the field separator survives a restart

**Aim:** A description holding the ` | ` that separates fields in the save file
comes back whole, rather than being cut short at the first one.

This is the case that would silently lose data: before the separator was
escaped, `todo a | b` saved as three fields and came back as just `a`, with no
error to show anything had gone wrong. The deadline covers a separator in the
description of a task that has a field after it, which is where a lost
separator would misalign the remaining fields rather than only truncating the
last one.

The `/by` date can no longer hold a separator, since a `yyyy-mm-dd` date has no
room for one, so the escaping is exercised through the description alone.

**First input:**

```
todo a | b
deadline pipe | desc /by 2019-10-15
bye
```

**Second input:**

```
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] a | b
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [D][ ] pipe | desc (by: Oct 15 2019)
     That makes 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________

    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     I've brought back 2 tasks from last time, sir.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] a | b
     2.[D][ ] pipe | desc (by: Oct 15 2019)
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC18: Damaged lines are skipped, not fatal

**Aim:** A save file with bad lines in it still gives up its good ones. Each
readable task is restored, the unreadable lines are counted and reported, and
the user is told where a copy of the file was kept, since the next save rewrites
the file from the tasks that were read and drops the lines that were not.

The seeded file covers every way a line can be wrong: an unknown type letter,
too few fields, a missing date field, a status that is neither 0 nor 1, and a
date field that is not a date. A good task sits at each end, so a bad line in
the middle cannot be shown to stop the ones after it from loading.

The good event is seeded as `2019-12-02` and listed as `Dec 02 2019`, which is
the file's form and the reader's form seen side by side in one case: the block
below is exactly what is on disk, and the expected output is what a person sees.

The last two bad lines are the ones the dates brought with them, and they are
wrong in the two different ways a date can be. `Sunday` is not shaped like a
date at all; `2019-02-30` is shaped like one and names a day that does not
exist. A hand-edited file is the only way either reaches this code, since the
program never writes one.

Each has to cost its own line and no more. The exception `LocalDate.parse`
throws is unchecked, so passed along as it came it would escape the loading loop
and cost the whole file; `Dates` turns it into the checked one the loop
watches for.

The copy is named in the reply but not checked here, because a case can only
read the console. That the copy exists, and that it holds the damaged lines
after the file has been rewritten without them, is asserted by `StorageTest`.

**Save file:**

```
T | 1 | read book
X | 0 | unknown type
T | 0
D | 0 | no by field
T | 2 | bad status
D | 0 | bad date | Sunday
D | 0 | impossible day | 2019-02-30
E | 0 | good event | 2019-12-02 | 2019-12-03
```

**Input:**

```
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     I've brought back 2 tasks from last time, sir.
    ____________________________________________________________

    ____________________________________________________________
     I could not make sense of 6 lines in your saved tasks, sir.
     I have left them out, but kept a copy of the file beside it as alfred.txt.bak.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][X] read book
     2.[E][ ] good event (from: Dec 02 2019 to: Dec 03 2019)
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC19: A date that cannot be read is refused with a message

**Aim:** A date the program cannot read draws a message saying what to type,
the task is not stored, and the session carries on.

A date can be wrong in two ways, and the two get different messages because the
user can do something different about each:

* **Not shaped like a date.** `Sunday`, `2/12/2019`, and `2019-2-3` are written
  in the wrong format, so the reply names the right one and shows an example.
  `2019-2-3` is the case that would slip through a looser check: it is nearly
  right, and wrong only in that the month and day are not padded to two digits.
* **Shaped like a date but naming no real day.** `2019-02-30` has the format
  exactly right, so repeating the format would not help. The reply points at the
  day and the month instead.

The last input covers a bad `/to`, so the check is shown to apply to an event's
dates and not only to a deadline's.

Earlier this behavior was the happy path only, and the unchecked exception from
`LocalDate.parse` fell through to the safety net meant for bugs in the program,
which replied with the name of a Java class. This case previously asserted that,
so that it would fail and ask to be rewritten once a real message existed. This
is that rewrite.

The closing `list` is the assertion that matters: an empty list shows that no
half-built task was stored on its way to being refused.

**Input:**

```
deadline return book /by Sunday
deadline pay rent /by 2/12/2019
deadline file taxes /by 2019-2-3
deadline leap /by 2019-02-30
event party /from 2019-12-02 /to next week
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     I don't know 'Sunday' as a date, sir. Do use yyyy-mm-dd, as in 2019-10-15.
    ____________________________________________________________

    ____________________________________________________________
     I don't know '2/12/2019' as a date, sir. Do use yyyy-mm-dd, as in 2019-10-15.
    ____________________________________________________________

    ____________________________________________________________
     I don't know '2019-2-3' as a date, sir. Do use yyyy-mm-dd, as in 2019-10-15.
    ____________________________________________________________

    ____________________________________________________________
     There is no such date as '2019-02-30', sir. Do check the day and the month.
    ____________________________________________________________

    ____________________________________________________________
     I don't know 'next week' as a date, sir. Do use yyyy-mm-dd, as in 2019-10-15.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC20: Edge-case dates survive a save and a load

**Aim:** A date comes back from the save file as the same day it went in, for
the dates most likely to expose a mistake in the two formats.

TC14 already restarts the program with a deadline and an event in the list, but
its dates are ordinary ones. This case picks the three that would catch a format
error TC14 would miss:

* `2019-01-05` has a single-digit month and a single-digit day. Written with a
  pattern that does not pad, it would save as `2019-1-5`, which
  `LocalDate.parse` refuses, and the task would come back as a skipped line.
* `2020-02-29` is a leap day. It exists only if the year is carried with it, so
  a format that dropped or altered the year would turn it into an impossible
  date rather than a merely wrong one.
* The event runs from `2019-12-31` to `2020-01-01`, crossing a year boundary, so
  a year taken from the wrong field would show up as a start and end in the same
  year.

Reading them back is the assertion. The dates the second run prints are produced
from the file rather than from anything left in memory, so a date that did not
survive shows up either as a changed day or as a task that is missing entirely.

**First input:**

```
deadline file taxes /by 2019-01-05
deadline leap day /by 2020-02-29
event new year /from 2019-12-31 /to 2020-01-01
bye
```

**Second input:**

```
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [D][ ] file taxes (by: Jan 05 2019)
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [D][ ] leap day (by: Feb 29 2020)
     That makes 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [E][ ] new year (from: Dec 31 2019 to: Jan 01 2020)
     That makes 3 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________

    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     I've brought back 3 tasks from last time, sir.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[D][ ] file taxes (by: Jan 05 2019)
     2.[D][ ] leap day (by: Feb 29 2020)
     3.[E][ ] new year (from: Dec 31 2019 to: Jan 01 2020)
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC21: An event cannot end before it starts

**Aim:** An event whose `/to` date falls before its `/from` date is refused,
while one that starts and ends on the same day is allowed.

Both dates can be perfectly readable and still describe something that cannot
happen, so this check is separate from reading them, and runs only once both
have been read: two dates that cannot be read cannot be compared.

The second input is the boundary. An event lasting a single day has an end that
is not after its start, so a check written with the comparison the wrong way
round would refuse it. Storing it is what shows only a strictly earlier end is
refused.

**Input:**

```
event backwards /from 2020-01-01 /to 2019-01-01
event one day /from 2020-01-01 /to 2020-01-01
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     An event cannot end before it starts, sir.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [E][ ] one day (from: Jan 01 2020 to: Jan 01 2020)
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[E][ ] one day (from: Jan 01 2020 to: Jan 01 2020)
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC22: `on <date>` shows what falls on one day

**Aim:** `on <date>` shows the deadlines due that day and the events running
over it, leaves out everything else, and numbers what it shows by each task's
place in the whole list.

The list is built so that each kind of task answers differently:

* The todo has no date, so it never appears.
* The deadline appears on `2019-12-02` and on no other day.
* The event runs from `2019-12-01` to `2019-12-03`, so it appears on all three,
  its first and last days included. The queries walk the day before it, its
  three days, and the day after, which is what pins both ends: a check written
  with `isAfter` in place of `!isBefore` would drop the first day, and the
  mirror mistake would drop the last.

`2019-12-02` is the day both a deadline and an event fall on, so it also shows
the two kinds listed together.

The numbering is the other half of the case. The event is task 3 of 3, and it
keeps the number 3 when shown on a day where it is the only match. `mark 3`
then marks it, which is the point: a number shown by `on` is the number the
other commands expect. Were the matches renumbered from 1, that `mark 3` would
have found nothing, or worse, the wrong task. The gap between `2.` and `3.` in
the `2019-12-02` reply is the visible sign of this.

The last query repeats an earlier one to show the mark took effect on the task
that was meant.

**Input:**

```
todo no date
deadline return book /by 2019-12-02
event conference /from 2019-12-01 /to 2019-12-03
on 2019-11-30
on 2019-12-01
on 2019-12-02
on 2019-12-03
on 2019-12-04
mark 3
on 2019-12-01
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] no date
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [D][ ] return book (by: Dec 02 2019)
     That makes 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [E][ ] conference (from: Dec 01 2019 to: Dec 03 2019)
     That makes 3 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     You have nothing on Nov 30 2019, sir.
    ____________________________________________________________

    ____________________________________________________________
     Here is what you have on Dec 01 2019:
     3.[E][ ] conference (from: Dec 01 2019 to: Dec 03 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here is what you have on Dec 02 2019:
     2.[D][ ] return book (by: Dec 02 2019)
     3.[E][ ] conference (from: Dec 01 2019 to: Dec 03 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here is what you have on Dec 03 2019:
     3.[E][ ] conference (from: Dec 01 2019 to: Dec 03 2019)
    ____________________________________________________________

    ____________________________________________________________
     You have nothing on Dec 04 2019, sir.
    ____________________________________________________________

    ____________________________________________________________
     Splendid, sir. I've marked this task as done:
       [E][X] conference (from: Dec 01 2019 to: Dec 03 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here is what you have on Dec 01 2019:
     3.[E][X] conference (from: Dec 01 2019 to: Dec 03 2019)
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC23: `on` checks the date it is given

**Aim:** `on` refuses a missing date and an unreadable one, each with its own
message, and changes nothing on the way.

A date left out gets a message about the command rather than one about the
format, since quoting an empty string back as the thing that was not recognized
would say nothing useful. A date that is present but unreadable goes through the
same reader as a deadline's, so it draws the same two messages TC19 covers,
which is the point: the rules for a date do not depend on the command it was
typed into.

The closing `list` shows the one real task untouched, so none of the three
refused commands disturbed the list.

**Input:**

```
todo read book
on
on Sunday
on 2019-02-30
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     The on command needs a date, sir.
    ____________________________________________________________

    ____________________________________________________________
     I don't know 'Sunday' as a date, sir. Do use yyyy-mm-dd, as in 2019-10-15.
    ____________________________________________________________

    ____________________________________________________________
     There is no such date as '2019-02-30', sir. Do check the day and the month.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC24: `find <keyword>` shows the tasks whose description matches

**Aim:** `find <keyword>` shows every task whose description contains the
keyword, whatever its kind or its mark, ignores the difference between uppercase
and lowercase, and numbers what it shows by each task's place in the whole list.

The list holds two tasks with `book` in them and one without, so a reply that
showed everything and a reply that showed nothing are both visibly wrong.

The numbering carries the same weight it does in TC22. The two matches are tasks
1 and 3 of 3, and the gap where `2.` would be is the visible sign that these are
list numbers rather than a fresh count of the matches. The `delete 2` in the
middle then closes the gap, and the `unmark 2` after it acts on the number the
reply above it printed, which is what the whole rule is for: were the matches
renumbered from 1, that same `unmark 2` would have hit the wrong task both
before and after the delete.

`find BOOK` repeats the previous search in capitals to show that case is
ignored, and `find Milk` searches for the one task the other searches leave out,
so neither reply can be right by accident. `find homework` is the answer to a
search that matches nothing, which is a sentence rather than an empty list.

**Input:**

```
todo read book
todo buy milk
deadline return book /by 2019-06-06
mark 1
mark 3
find book
find BOOK
find Milk
find homework
delete 2
find book
unmark 2
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] buy milk
     That makes 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [D][ ] return book (by: Jun 06 2019)
     That makes 3 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Splendid, sir. I've marked this task as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Splendid, sir. I've marked this task as done:
       [D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here are the matching tasks on your list, sir:
     1.[T][X] read book
     3.[D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here are the matching tasks on your list, sir:
     1.[T][X] read book
     3.[D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here are the matching tasks on your list, sir:
     2.[T][ ] buy milk
    ____________________________________________________________

    ____________________________________________________________
     I found no matching tasks, sir.
    ____________________________________________________________

    ____________________________________________________________
     As you wish, sir. I've removed this task:
       [T][ ] buy milk
     That leaves 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the matching tasks on your list, sir:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Very well, sir. I've marked this task as not done yet:
       [D][ ] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC25: `find` checks its keyword and searches for the whole of it

**Aim:** `find` refuses a missing keyword, answers a keyword that matches
nothing, and treats everything after the command as one piece of text rather
than as separate words.

The first search runs before anything has been added, so an empty list is
answered rather than crashed on. `find` on its own is refused, because an empty
keyword is contained in every description and would print the whole list — an
answer that looks like a working search and is not one.

The last three searches are the same words in three arrangements. `find read
book` matches the phrase, `find book read` does not, and `find READ BOOK`
matches again, which together pin the rule: the keyword is one phrase, matched
in the order it was typed, and case is ignored. A search that split the keyword
into words would make `find book read` match too.

The closing `list` shows the one real task untouched, so neither the refusal nor
any of the searches disturbed the list.

**Input:**

```
find book
find
todo read book tonight
find read book
find book read
find READ BOOK
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     I found no matching tasks, sir.
    ____________________________________________________________

    ____________________________________________________________
     The find command needs a keyword, sir.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book tonight
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the matching tasks on your list, sir:
     1.[T][ ] read book tonight
    ____________________________________________________________

    ____________________________________________________________
     I found no matching tasks, sir.
    ____________________________________________________________

    ____________________________________________________________
     Here are the matching tasks on your list, sir:
     1.[T][ ] read book tonight
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] read book tonight
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC26: `priority` sets a level and `list` shows it

**Aim:** `priority <number> <level>` gives one task a level and `list` shows
that box between the status box and the description.

The level goes on the second task, so a command acting on the wrong one would
show up, and the first task keeps no box at all — a task nobody has prioritized
displays exactly as it did before this feature existed.

**Input:**

```
todo read book
todo submit report
priority 2 high
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] submit report
     That makes 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good. I've set this task's priority:
       [T][ ][HIGH] submit report
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] read book
     2.[T][ ][HIGH] submit report
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________

```

---

## TC27: A priority can be changed and taken off again

**Aim:** A second `priority` on the same task replaces the level rather than
adding to it, and `priority <number> none` takes the priority off again.

`list` runs after the change and after the removal, so the level is read back
from the stored task rather than only from the confirmation printed at the time.

**Input:**

```
todo submit report
priority 1 low
priority 1 high
list
priority 1 none
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] submit report
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good. I've set this task's priority:
       [T][ ][LOW] submit report
    ____________________________________________________________

    ____________________________________________________________
     Very good. I've set this task's priority:
       [T][ ][HIGH] submit report
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ][HIGH] submit report
    ____________________________________________________________

    ____________________________________________________________
     Very good. I've taken the priority off this task:
       [T][ ] submit report
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] submit report
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________

```

---

## TC28: `priority` checks both of its parts

**Aim:** The command is refused when either part is missing, when the number is
not a number, when the level is not a level, and when the number names no task —
each with the message for that fault, and each leaving the list alone.

The five refusals come in the order the program checks them: a missing part, a
number that is not a number, a level that is not a level, and last a well-formed
command whose number names no task, which only the task list can say. The
closing `list` is the assertion that matters most — after five refusals the one
stored task is untouched and still carries no priority.

**Input:**

```
todo read book
priority
priority 1
priority two high
priority 1 urgent
priority 9 high
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     The priority command needs a task number and a level, sir.
    ____________________________________________________________

    ____________________________________________________________
     The priority command needs a task number and a level, sir.
    ____________________________________________________________

    ____________________________________________________________
     That is not a task number, sir.
    ____________________________________________________________

    ____________________________________________________________
     I know high, medium, low, and none as priorities, sir.
    ____________________________________________________________

    ____________________________________________________________
     There is no such task, sir.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________

```

---

## TC29: Spacing mistakes, repeated keywords, and repeated tasks are refused

**Aim:** The three new ways a line can be wrong each get a message of their
own, and none of them touches the list. Extra spaces anywhere in a line are
forgiven, so the first todo is stored with single spaces. A keyword given twice
is refused by name, and `/to` ahead of `/from` is refused for its order, rather
than either being read as part of a date and refused as an unreadable one. A
task already on the list is refused naming its number, whatever its case.

One line of each is enough here. The finer points — tabs, `a/by` staying in a
description, a deadline on another day being a different task, and the copy
kept of a damaged save file — are properties of one method each, and
`ParserTest`, `DeadlineTest`, `EventTest`, and `StorageTest` state them in a
few lines apiece. The closing `list` is the assertion: one task, spelled with
single spaces.

**Input:**

```
   todo   read    book   
deadline return book /by 2019-10-15 /by 2019-10-16
event meeting /to 2019-12-03 /from 2019-12-02
todo Read Book
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     You've given /by more than once, sir. Once will do.
    ____________________________________________________________

    ____________________________________________________________
     The /from date has to come before the /to date, sir.
    ____________________________________________________________

    ____________________________________________________________
     You already have that task, sir, as number 1.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## TC30: Tasks of other kinds can be added after an event

**Aim:** A deadline and a todo are both accepted once an event is on the list,
and `list` shows all three in the order they were added.

Every addition is checked against each task already on the list, to refuse a
repeat. The check compares kinds before anything else, so an event held up
against a deadline has to answer "different" and move on rather than fail. A
version that looked at the event's dates before settling the kind crashed on
exactly this input, and no other case caught it: the event is the last task
added in every case that has one, so nothing was ever compared against it.

**Input:**

```
event project meeting /from 2019-12-02 /to 2019-12-03
deadline return book /by 2019-10-15
todo read book
list
bye
```

**Expected output:**

```
    ____________________________________________________________
            _     _      _____  ____   _____  ____
           / \   | |    |  ___||  _ \ | ____||  _ \
          / _ \  | |    | |_   | |_) ||  _|  | | | |
         / ___ \ | |___ |  _|  |  _ < | |___ | |_| |
        /_/   \_\|_____||_|    |_| \_\|_____||____/
                    P E N N Y W O R T H

      Butler to the Wayne family  --  At your service
     Good day, sir. Alfred Pennyworth, at your disposal.
     What may I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
     That makes 1 task on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [D][ ] return book (by: Oct 15 2019)
     That makes 2 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I've added this task:
       [T][ ] read book
     That makes 3 tasks on your list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks on your list, sir:
     1.[E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
     2.[D][ ] return book (by: Oct 15 2019)
     3.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Very good, sir. I shall be here when you need me.
    ____________________________________________________________
```

---

## Known gaps (not yet covered)

No invalid command crashes the program any more. Blank input, an unknown
keyword, a missing description or separator, and a bad task number are all
refused with a message, covered by TC7, TC10, TC11, and TC12.

A damaged save file no longer costs the whole list, a description containing the
field separator survives being saved, input that runs out ends the session
cleanly, and counts read as "1 task" rather than "1 tasks". Those are covered by
TC18, TC17, TC16, and the counts throughout.

A date that cannot be read now has a message of its own, saying either what
format to use or that the day does not exist, covered by TC19. A date missing
altogether is covered by TC11, an event that ends before it starts by TC21, and
an unreadable date in the save file by TC18. Asking what falls on one day is
covered by TC22, and the ways that question can be malformed by TC23.

Searching descriptions for a keyword is covered by TC24, and a missing keyword,
a keyword that matches nothing, and a keyword of several words by TC25.

Attaching a priority to a task is covered by TC26, changing one and taking it
off again by TC27, and the ways the command can be malformed by TC28. That a
task without a priority displays as it did before priorities existed is covered
by every case before TC26, none of which needed changing.

Spacing mistakes, a keyword given twice or in the wrong order, and a task added
twice are covered by TC29, one line each. Tabs are forgiven the same way as
spaces, but that is checked by `ParserTest` rather than here, because a tab in
a fenced block is invisible on the page and easily replaced by an editor.

One gap is a limit of the program rather than of the tests. Only `yyyy-mm-dd` is
accepted, so `2/12/2019` is refused rather than understood, and a time of day
such as `1800` has nowhere to go, since a task holds a `LocalDate` and not a
`LocalDateTime`. Both are refusals with a clear reason rather than failures, but
accepting more of what a user might reasonably type is the next thing to add.

The rest are limits of the tests rather than of the program.

* The contents of `data/alfred.txt` are still not read by any case directly.
  TC14, TC15, and TC17 check the file indirectly, by restarting the program and
  looking at what comes back, which catches a wrong or missing line. Asserting
  the exact bytes of the file is not something this plan can express.
* A save file that cannot be opened at all — one whose permissions deny reading,
  or whose name has been taken by a folder — is handled and reported, but no
  case covers it. A **Save file** block can only put text in the file; it cannot
  make the file unreadable. A failing save is untestable for the same reason.
  Both were checked by hand.
* Damaged lines are reported and skipped, and the file is copied aside before
  the next save can drop them. TC18 asserts the reply that names the copy, but
  not the copy itself, and no case reaches the reply for a copy that could not
  be made: a **Save file** block cannot make the folder unwritable. That the
  copy exists and holds the damaged lines is asserted by `StorageTest`.
* A save file that holds the same task twice is loaded whole, since only
  additions are checked for a repeat. No case covers it, because the only
  visible effect is a list with two identical lines, which every other case
  already shows is how `list` prints what it holds.
* The safety net that catches an unexpected fault inside a command is not
  covered, since reaching it needs a bug to exist.
* A priority surviving a restart, and a save file written before priorities
  existed still loading, are covered by `StorageTest` rather than here. Both are
  properties of one round trip through the file, which a unit test states in
  five lines and a case here would state in two full console transcripts.
