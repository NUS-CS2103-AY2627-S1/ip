# UI Test Plan

This file defines the test cases used by the `test-ui` skill (see
`.claude/skills/test-ui/SKILL.md`) to verify XiaoZhi's console behavior.

Each test case gives the exact lines fed to the program on stdin, in order,
and the exact console output the program is expected to produce in return
(including the banner/greeting/farewell, so the whole session is checked,
not just the feature under test). Add a new test case here whenever you add
or change a command, and re-run the `test-ui` skill before committing.

## Test 1: Greet and exit

**Aim:** Verify the banner, greeting, and farewell all print correctly, and
the program exits immediately on `bye` without doing anything else.

**Input:**
```
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Go now, and let your tasks find their season.
```

## Test 2: Add a todo

**Aim:** Verify a `todo` command stores the task and confirms with the
`[T]` tag, and that the type keyword is stripped from the description.

**Input:**
```
todo read book
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
Go now, and let your tasks find their season.
```

## Test 3: Add a deadline

**Aim:** Verify a `deadline` command splits off the `/by` portion and shows
it as `(by: ...)` with the `[D]` tag.

**Input:**
```
deadline return book /by 2019-12-02
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [D][ ] return book (by: Dec 02 2019)
Now 1 task(s) rest among your intentions.
Go now, and let your tasks find their season.
```

## Test 4: Add an event

**Aim:** Verify an `event` command splits off both the `/from` and `/to`
portions and shows them as `(from: ... to: ...)` with the `[E]` tag.

**Input:**
```
event project meeting /from 2019-08-06 /to 2019-08-07
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Now 1 task(s) rest among your intentions.
Go now, and let your tasks find their season.
```

## Test 5: List multiple tasks of different types

**Aim:** Verify `list` prints every stored task, numbered from 1, each
rendered with its own type's format, in the order they were added.

**Input:**
```
todo read book
deadline return book /by 2019-12-02
event project meeting /from 2019-08-06 /to 2019-08-07
list
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
It is done.
  [D][ ] return book (by: Dec 02 2019)
Now 2 task(s) rest among your intentions.
It is done.
  [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Now 3 task(s) rest among your intentions.
The tasks that occupy your mind:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Go now, and let your tasks find their season.
```

## Test 6: Mark and unmark a task

**Aim:** Verify `mark <n>` sets the task's status icon to `X` and `unmark
<n>` clears it back, using 1-based indices as shown by `list`.

**Input:**
```
todo read book
mark 1
unmark 1
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
The circle closes.
  [T][X] read book
What was closed is opened again.
  [T][ ] read book
Go now, and let your tasks find their season.
```

## Test 7: Unrecognised command

**Aim:** Verify a command that isn't `list`/`mark`/`unmark`/`todo`/
`deadline`/`event`/`bye` is reported back to the user instead of crashing
or being silently stored.

**Input:**
```
foobar
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. I have not learned the word "foobar".
Go now, and let your tasks find their season.
```

## Test 8: Empty todo description

**Aim:** Verify `todo` with nothing after it is reported as an error
instead of crashing or being silently added, per the `A-Exceptions`
requirement to handle a bare `todo`.

**Input:**
```
todo
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. The description of a todo cannot be empty.
Go now, and let your tasks find their season.
```

## Test 9: Deadline missing /by

**Aim:** Verify a `deadline` command with no `/by` marker is reported as
an error, rather than swallowing the whole line as the description (as it
did before A-Exceptions was implemented).

**Input:**
```
deadline return book
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. A deadline needs a /by date. Try: deadline <description> /by <date>
Go now, and let your tasks find their season.
```

## Test 10: Event missing /from

**Aim:** Verify an `event` command with no `/from` marker is reported as
an error.

**Input:**
```
event project meeting
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. An event needs a /from time. Try: event <description> /from <start> /to <end>
Go now, and let your tasks find their season.
```

## Test 11: Event missing /to

**Aim:** Verify an `event` command with `/from` but no `/to` marker is
reported as an error.

**Input:**
```
event project meeting /from Mon 2pm
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. An event needs a /to time. Try: event <description> /from <start> /to <end>
Go now, and let your tasks find their season.
```

## Test 12: Mark with an out-of-range task number

**Aim:** Verify `mark <n>` with an `n` beyond the current task count is
reported as an error instead of throwing an uncaught
`NullPointerException`.

**Input:**
```
todo read book
mark 5
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
Reflect. There is no task 5 to speak of. Only 1 task(s) exist; look again before you act.
Go now, and let your tasks find their season.
```

## Test 13: Mark with a non-numeric task number

**Aim:** Verify `mark <n>` with a non-numeric `n` is reported as an error
instead of throwing an uncaught `NumberFormatException`.

**Input:**
```
todo read book
mark abc
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
Reflect. "abc" isn't a valid task number.
Go now, and let your tasks find their season.
```

## Test 14: Delete a task

**Aim:** Verify `delete <n>` removes the right task from the list and the
remaining tasks re-number correctly on the next `list`.

**Input:**
```
todo read book
deadline return book /by 2019-12-02
delete 1
list
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
It is done.
  [D][ ] return book (by: Dec 02 2019)
Now 2 task(s) rest among your intentions.
What no longer serves you has been released.
  [T][ ] read book
Now 1 task(s) remain.
The tasks that occupy your mind:
1.[D][ ] return book (by: Dec 02 2019)
Go now, and let your tasks find their season.
```

## Test 15: Delete with a missing task number

**Aim:** Verify `delete` with no number after it is reported as an error.

**Input:**
```
todo read book
delete
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
Reflect. Please specify which task number to delete.
Go now, and let your tasks find their season.
```

## Test 16: Delete with a non-numeric task number

**Aim:** Verify `delete <n>` with a non-numeric `n` is reported as an
error instead of throwing an uncaught `NumberFormatException`.

**Input:**
```
todo read book
delete abc
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
Reflect. "abc" isn't a valid task number.
Go now, and let your tasks find their season.
```

## Test 17: Delete with an out-of-range task number

**Aim:** Verify `delete <n>` with an `n` beyond the current task count
(including `0`) is reported as an error.

**Input:**
```
todo read book
delete 5
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
Reflect. There is no task 5 to speak of. Only 1 task(s) exist; look again before you act.
Go now, and let your tasks find their season.
```

## Test 18: Deadline with an invalid date

**Aim:** Verify a `deadline` command whose `/by` value isn't a valid
`yyyy-mm-dd` date is reported as an error instead of being stored as-is.

**Input:**
```
deadline return book /by tomorrow
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. "tomorrow" isn't a valid date. Please use yyyy-mm-dd, e.g. 2019-10-15.
Go now, and let your tasks find their season.
```

## Test 19: Event with an invalid date

**Aim:** Verify an `event` command whose `/from` value isn't a valid
`yyyy-mm-dd` date is reported as an error instead of being stored as-is.

**Input:**
```
event project meeting /from tomorrow /to 2019-08-07
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. "tomorrow" isn't a valid date. Please use yyyy-mm-dd, e.g. 2019-10-15.
Go now, and let your tasks find their season.
```

## Test 20: Find matches across task types

**Aim:** Verify `find` returns every task (todo, deadline, event alike)
whose description contains the keyword, numbered from 1 in their original
order, and leaves tasks that don't match out.

**Input:**
```
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-06
find book
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
It is done.
  [D][ ] return book (by: Jun 06 2019)
Now 2 task(s) rest among your intentions.
It is done.
  [E][ ] project meeting (from: Aug 06 2019 to: Aug 06 2019)
Now 3 task(s) rest among your intentions.
These are the tasks that echo your search:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
Go now, and let your tasks find their season.
```

## Test 21: Find with no matches

**Aim:** Verify `find` prints just the heading, with no task lines, when no
task's description contains the keyword.

**Input:**
```
todo read book
find homework
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
These are the tasks that echo your search:
Go now, and let your tasks find their season.
```

## Test 22: Find is case-insensitive

**Aim:** Verify `find` matches a keyword regardless of case.

**Input:**
```
todo Read Book
find READ
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] Read Book
Now 1 task(s) rest among your intentions.
These are the tasks that echo your search:
1.[T][ ] Read Book
Go now, and let your tasks find their season.
```

## Test 23: Find with an empty keyword

**Aim:** Verify a bare `find` command with no keyword is reported as an
error instead of matching (or failing to match) every task.

**Input:**
```
find
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. Please specify a keyword to search for.
Go now, and let your tasks find their season.
```

## Test 24: Undo an added task

**Aim:** Verify `undo` right after a `todo`/`deadline`/`event` removes the
task that was just added, as if it had never been entered.

**Input:**
```
todo read book
undo
list
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
What no longer serves you has been released.
  [T][ ] read book
Now 0 task(s) remain.
The tasks that occupy your mind:
Go now, and let your tasks find their season.
```

## Test 25: Undo a deleted task restores its original position

**Aim:** Verify `undo` right after a `delete` puts the task back at the same
position it was removed from, not at the end of the list.

**Input:**
```
todo read book
deadline return book /by 2019-12-02
delete 1
undo
list
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
It is done.
  [D][ ] return book (by: Dec 02 2019)
Now 2 task(s) rest among your intentions.
What no longer serves you has been released.
  [T][ ] read book
Now 1 task(s) remain.
It is done.
  [T][ ] read book
Now 2 task(s) rest among your intentions.
The tasks that occupy your mind:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2019)
Go now, and let your tasks find their season.
```

## Test 26: Undo a mark

**Aim:** Verify `undo` right after `mark <n>` unmarks the task again.

**Input:**
```
todo read book
mark 1
undo
list
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
The circle closes.
  [T][X] read book
What was closed is opened again.
  [T][ ] read book
The tasks that occupy your mind:
1.[T][ ] read book
Go now, and let your tasks find their season.
```

## Test 27: Undo an unmark

**Aim:** Verify `undo` right after `unmark <n>` marks the task done again.

**Input:**
```
todo read book
mark 1
unmark 1
undo
list
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
The circle closes.
  [T][X] read book
What was closed is opened again.
  [T][ ] read book
The circle closes.
  [T][X] read book
The tasks that occupy your mind:
1.[T][X] read book
Go now, and let your tasks find their season.
```

## Test 28: Undo a repeated mark restores the exact prior state

**Aim:** Verify that undoing a `mark <n>` that was a no-op (the task was
already done) leaves the task done, instead of blindly toggling it off. This
guards against a naive undo that just flips the status bit rather than
restoring what it was immediately before this specific command ran.

**Input:**
```
todo read book
mark 1
mark 1
undo
list
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
The circle closes.
  [T][X] read book
The circle closes.
  [T][X] read book
The circle closes.
  [T][X] read book
The tasks that occupy your mind:
1.[T][X] read book
Go now, and let your tasks find their season.
```

## Test 29: Undo twice walks back two commands

**Aim:** Verify repeated `undo` keeps reversing commands one at a time, in
reverse chronological order (a delete then an add, undone as add-back then
remove-again).

**Input:**
```
todo read book
todo write essay
undo
undo
list
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
It is done.
  [T][ ] write essay
Now 2 task(s) rest among your intentions.
What no longer serves you has been released.
  [T][ ] write essay
Now 1 task(s) remain.
What no longer serves you has been released.
  [T][ ] read book
Now 0 task(s) remain.
The tasks that occupy your mind:
Go now, and let your tasks find their season.
```

## Test 30: Undo with nothing to undo

**Aim:** Verify `undo` with no prior undoable command (including right after
startup, and after every earlier undo has already been used up) is reported
as an error instead of crashing or silently doing nothing.

**Input:**
```
undo
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. There is nothing left to undo; the past cannot be unmade twice.
Go now, and let your tasks find their season.
```

## Test 31: List and find cannot be undone

**Aim:** Verify `undo` skips over non-mutating commands entirely -- an
`undo` right after `list` reverses the last mutating command before it
(the `todo`), not the `list` itself (which has nothing to undo).

**Input:**
```
todo read book
list
undo
list
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
The tasks that occupy your mind:
1.[T][ ] read book
What no longer serves you has been released.
  [T][ ] read book
Now 0 task(s) remain.
The tasks that occupy your mind:
Go now, and let your tasks find their season.
```

## Test 32: Leading whitespace before the command word is ignored

**Aim:** Verify a stray leading space before the command word (e.g. from a
copy-paste) does not make the command look unrecognised.

**Input:**
```
  list
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
The tasks that occupy your mind:
Go now, and let your tasks find their season.
```

## Test 33: Multiple spaces between the command word and its argument

**Aim:** Verify an accidental extra space right after the command word
(e.g. `todo  read book`) is collapsed rather than becoming part of the
stored description as a leading space.

**Input:**
```
todo  read book
list
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
The tasks that occupy your mind:
1.[T][ ] read book
Go now, and let your tasks find their season.
```

## Test 34: Blank input

**Aim:** Verify a line with nothing but whitespace (e.g. an accidental
Enter press) is reported as an error with a dedicated message, instead of
being reported as an unrecognised command with an empty name.

**Input:**
```
   
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. Please enter a command.
Go now, and let your tasks find their season.
```

## Test 35: Mark with more than one argument

**Aim:** Verify `mark <n> <extra>` is reported as an error instead of
silently ignoring everything after the first number.

**Input:**
```
todo read book
mark 1 2
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
Reflect. Please give exactly one task number to mark -- got 2.
Go now, and let your tasks find their season.
```

## Test 36: Deadline with two /by markers

**Aim:** Verify a `deadline` command with more than one `/by` marker is
reported with a dedicated message, instead of feeding the leftover text
into date parsing and producing a confusing "isn't a valid date" error.

**Input:**
```
deadline return book /by 2019-12-02 /by 2019-12-03
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. A deadline can only have one /by date.
Go now, and let your tasks find their season.
```

## Test 37: Event with two /from markers

**Aim:** Verify an `event` command with more than one `/from` marker is
reported with a dedicated message.

**Input:**
```
event trip /from 2019-08-06 /from 2019-08-07 /to 2019-08-08
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. An event can only have one /from time.
Go now, and let your tasks find their season.
```

## Test 38: Event with two /to markers

**Aim:** Verify an `event` command with more than one `/to` marker is
reported with a dedicated message.

**Input:**
```
event trip /from 2019-08-06 /to 2019-08-07 /to 2019-08-08
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. An event can only have one /to time.
Go now, and let your tasks find their season.
```

## Test 39: Event with /from later than /to

**Aim:** Verify an `event` whose `/from` date is later than its `/to` date
is rejected, instead of being stored as a nonsensical backwards span.

**Input:**
```
event trip /from 2019-08-10 /to 2019-08-01
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. An event's /from date cannot be later than its /to date.
Go now, and let your tasks find their season.
```

## Test 40: Event with /from equal to /to is still allowed

**Aim:** Verify a single-day event (`/from` and `/to` the same date) is
still accepted -- only a `/from` strictly *later* than `/to` is an error,
since same-day is a legitimate event at this app's day-level granularity.

**Input:**
```
event trip /from 2019-08-10 /to 2019-08-10
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [E][ ] trip (from: Aug 10 2019 to: Aug 10 2019)
Now 1 task(s) rest among your intentions.
Go now, and let your tasks find their season.
```

## Test 41: Task description containing the save-file delimiter

**Aim:** Verify a description containing the `|` character is rejected,
since the save file uses `|` to separate fields and a literal `|` in a
description would corrupt it and be silently truncated when read back in.

**Input:**
```
todo buy milk | eggs
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
Reflect. A task's description cannot contain the '|' character, since it is used internally to save your tasks to disk.
Go now, and let your tasks find their season.
```

## Test 42: Adding the same task twice

**Aim:** Verify adding a task that is identical (same type, description,
and any dates) to one already in the list is rejected instead of silently
creating a duplicate.

**Input:**
```
todo read book
todo read book
list
bye
```

**Expected output:**
```
__  ___            ______     _
\ \/ (_) __ _  ___|__  / |__ (_)
 \  /| |/ _` |/ _ \ / /| '_ \| |
 /  \| | (_| | (_) / /_| | | | |
/_/\_\_|\__,_|\___/____|_| |_|_|

I am Master Zhi.
Speak your intention, and I shall attend to it.
It is done.
  [T][ ] read book
Now 1 task(s) rest among your intentions.
Reflect. This task already lives among your intentions: [T][ ] read book
The tasks that occupy your mind:
1.[T][ ] read book
Go now, and let your tasks find their season.
```
