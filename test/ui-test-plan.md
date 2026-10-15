# UI Test Plan

Manual/automated console test cases for the `Coco` chatbot. Each test case is run
independently against a fresh instance of the program (no state carries over
between test cases). The `test-ui` skill parses this file, runs the program with
each test case's input, and compares the captured console output against the
expected output verbatim.

Since Level 7, the chatbot persists its task list to `./data/coco.txt` (relative
to the working directory) and reloads it on startup. To keep test cases
independent despite this, `run_ui_tests.py` deletes the `data/` folder before
every case, so each one still starts from an empty task list. The
`### Input`/`### Expected Output` format only covers a single process per test
case, so persistence across a restart (write in one run, read back in the
next) is verified manually rather than via a formal test case here — see the
project's git history/PR description for that verification.

## Test 1: Greet and exit

Aim: Verify the banner, greeting, and prompt are printed, and the program exits
cleanly (printing the goodbye message) when the user immediately types `bye`.

### Input

```
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```

## Test 2: Add a todo and list it

Aim: Verify `todo <description>` adds a task with the `[T]` type marker, confirms
with the addition message and running count, and `list` shows it correctly.

### Input

```
todo borrow book
list
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [T][ ] borrow book
That's 1 things cookin' now.
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```

## Test 3: Add a deadline and an event, then list

Aim: Verify `deadline <description> /by <date>` and `event <description> /from
<start> /to <end>` add tasks with the `[D]`/`[E]` markers and their date
fields, that `deadline`'s `/by` date (given as `yyyy-mm-dd`) is stored and
displayed in the `MMM dd yyyy` format, and that `list` renders both task
types correctly.

### Input

```
deadline return book /by 2019-10-15
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [D][ ] return book (by: Oct 15 2019)
That's 1 things cookin' now.
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
That's 2 things cookin' now.
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[D][ ] return book (by: Oct 15 2019)
2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```

## Test 4: Mark and unmark a task

Aim: Verify `mark <n>` sets a task's done status to `[X]` and `unmark <n>` reverts
it to `[ ]`, with `list` reflecting the change after each.

### Input

```
todo read book
mark 1
list
unmark 1
list
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [T][ ] read book
That's 1 things cookin' now.
____________________________________________________________
____________________________________________________________
Nice one, that's outta here:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Ah, bringing it back, huh? Marked as not done:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```

## Test 5: Combined regression scenario

Aim: Regression-test all three task types together in one session, mixing
additions, marks, and a `list`, plus adding a second deadline/event that share a
description with an earlier task (they should be tracked as distinct entries).

### Input

```
todo read book
mark 1
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
mark 4
todo borrow book
list
deadline return book /by 2019-10-15
event project meeting /from Mon 2pm /to 4pm
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [T][ ] read book
That's 1 things cookin' now.
____________________________________________________________
____________________________________________________________
Nice one, that's outta here:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [D][ ] return book (by: Jun 06 2019)
That's 2 things cookin' now.
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
That's 3 things cookin' now.
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [T][ ] join sports club
That's 4 things cookin' now.
____________________________________________________________
____________________________________________________________
Nice one, that's outta here:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [T][ ] borrow book
That's 5 things cookin' now.
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [D][ ] return book (by: Oct 15 2019)
That's 6 things cookin' now.
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
That's 7 things cookin' now.
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```

## Test 6: Todo/deadline/event validation errors

Aim: Verify empty descriptions and malformed/missing `/by`, `/from`, `/to`
markers are rejected with a specific error message instead of crashing or
silently adding a bad task, and that a `/by` date that isn't valid
`yyyy-mm-dd` is rejected the same way.

### Input

```
todo
deadline
deadline return book
deadline return book /by
deadline return book /by tomorrow
event
event project meeting
event project meeting /from Mon 2pm
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Whoa, gotta tell me what the todo actually is!
____________________________________________________________
____________________________________________________________
Easy now, a deadline needs a '/by' date! Try: deadline <description> /by <date> [/every daily|weekly|monthly]
____________________________________________________________
____________________________________________________________
Easy now, a deadline needs a '/by' date! Try: deadline <description> /by <date> [/every daily|weekly|monthly]
____________________________________________________________
____________________________________________________________
Hey now, gotta give me a date for that deadline!
____________________________________________________________
____________________________________________________________
Hmm, 'tomorrow' doesn't look like a date to me. Use yyyy-mm-dd, like 2019-10-15.
____________________________________________________________
____________________________________________________________
Easy now, an event needs a '/from' and '/to'! Try: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Easy now, an event needs a '/from' and '/to'! Try: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Easy now, an event needs a '/from' and '/to'! Try: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```

## Test 7: Unknown command

Aim: Verify input that doesn't match any known command produces an error
message instead of being silently added as a task.

### Input

```
blah
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Whoa, lost me there, chief. Try somethin' else?
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```

## Test 8: Invalid task numbers for mark/unmark

Aim: Verify `mark`/`unmark` reject an out-of-range index, a non-numeric
argument, and a missing argument, each with a specific error message, without
crashing the program.

### Input

```
todo read book
mark 5
mark abc
mark
unmark 0
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [T][ ] read book
That's 1 things cookin' now.
____________________________________________________________
____________________________________________________________
No task number 5 around here!
____________________________________________________________
____________________________________________________________
'abc' ain't a number I recognize!
____________________________________________________________
____________________________________________________________
Which task number, chief? Gotta tell me that to mark it!
____________________________________________________________
____________________________________________________________
No task number 0 around here!
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```

## Test 9: Interleaved valid/invalid commands preserve correct state

Aim: Verify that failed operations (empty descriptions, malformed
deadline/event, an unknown command, and mark/unmark at exactly
`taskCount + 1`) never corrupt internal state — no phantom tasks are added,
the task count and done-status of existing tasks stay correct, and a valid
command immediately after an invalid one still targets the right task.
This specifically checks the boundary case of marking/unmarking the task
number one past the end of the list, which a generic "large out-of-range
number" test case would not catch.

### Input

```
todo read book
todo
list
mark 2
list
mark 1
unmark 2
list
deadline return book /by 2019-06-06
deadline return book /by
event meeting /from Mon 2pm /to 4pm
event meeting /from Mon 2pm
blah
list
mark 4
unmark 1
list
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [T][ ] read book
That's 1 things cookin' now.
____________________________________________________________
____________________________________________________________
Whoa, gotta tell me what the todo actually is!
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
No task number 2 around here!
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Nice one, that's outta here:
  [T][X] read book
____________________________________________________________
____________________________________________________________
No task number 2 around here!
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [D][ ] return book (by: Jun 06 2019)
That's 2 things cookin' now.
____________________________________________________________
____________________________________________________________
Hey now, gotta give me a date for that deadline!
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [E][ ] meeting (from: Mon 2pm to: 4pm)
That's 3 things cookin' now.
____________________________________________________________
____________________________________________________________
Easy now, an event needs a '/from' and '/to'! Try: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Whoa, lost me there, chief. Try somethin' else?
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
No task number 4 around here!
____________________________________________________________
____________________________________________________________
Ah, bringing it back, huh? Marked as not done:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```

## Test 10: Delete a task

Aim: Verify `delete <n>` removes the specified task, prints the "Noted"
confirmation with the removed task and updated count, and that `list`
reflects the removal with the remaining tasks renumbered.

### Input

```
todo read book
mark 1
deadline return book /by 2019-06-06
mark 2
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
mark 4
todo borrow book
list
delete 3
list
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [T][ ] read book
That's 1 things cookin' now.
____________________________________________________________
____________________________________________________________
Nice one, that's outta here:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [D][ ] return book (by: Jun 06 2019)
That's 2 things cookin' now.
____________________________________________________________
____________________________________________________________
Nice one, that's outta here:
  [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
That's 3 things cookin' now.
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [T][ ] join sports club
That's 4 things cookin' now.
____________________________________________________________
____________________________________________________________
Nice one, that's outta here:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [T][ ] borrow book
That's 5 things cookin' now.
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Alright, cleared that one out:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
That's 4 things left on the pile.
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2019)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```

## Test 11: Delete validation errors, including emptying the list

Aim: Verify `delete` rejects an out-of-range index, a non-numeric index,
and a missing index the same way `mark`/`unmark` do, and that deleting the
last remaining task correctly leaves an empty list (not a crash or a stale
entry).

### Input

```
todo read book
delete 5
delete abc
delete
list
delete 1
list
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [T][ ] read book
That's 1 things cookin' now.
____________________________________________________________
____________________________________________________________
No task number 5 around here!
____________________________________________________________
____________________________________________________________
'abc' ain't a number I recognize!
____________________________________________________________
____________________________________________________________
Which task number, chief? Gotta tell me that to delete it!
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Alright, cleared that one out:
  [T][ ] read book
That's 0 things left on the pile.
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```

## Test 12: Find tasks by keyword

Aim: Verify `find <keyword>` matches tasks whose description contains the
keyword (case-insensitively), across task types, renumbering matches from
1 rather than using their original list position; that no matches prints
an empty (but still headed) list instead of an error; and that a missing
keyword is rejected like other commands' missing arguments.

### Input

```
todo read book
deadline return book /by 2019-06-06
mark 1
mark 2
todo join sports club
find book
find nope
find
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [T][ ] read book
That's 1 things cookin' now.
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [D][ ] return book (by: Jun 06 2019)
That's 2 things cookin' now.
____________________________________________________________
____________________________________________________________
Nice one, that's outta here:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice one, that's outta here:
  [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [T][ ] join sports club
That's 3 things cookin' now.
____________________________________________________________
____________________________________________________________
Here's what I dug up:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Here's what I dug up:
____________________________________________________________
____________________________________________________________
What am I even looking for? Give me a keyword!
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```

## Test 13: Recurring deadlines

Aim: Verify `deadline <description> /by <date> /every <interval>` creates a
recurring deadline whose display includes "(every: ...)"; that `mark`-ing a
recurring deadline advances it to its next occurrence and reports that
instead of marking it done (staying `[ ]`, date advanced by the interval),
repeatedly across multiple marks; that a non-recurring deadline in the same
list still marks done normally; and that an invalid recurrence interval is
rejected the same way other invalid arguments are.

### Input

```
deadline standup /by 2026-01-06 /every daily
deadline submit report /by 2026-01-10
list
mark 1
list
mark 2
mark 1
deadline x /by 2026-01-06 /every fortnightly
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [D][ ] standup (by: Jan 06 2026) (every: daily)
That's 1 things cookin' now.
____________________________________________________________
____________________________________________________________
Tossed that onto the pile, easy breezy:
  [D][ ] submit report (by: Jan 10 2026)
That's 2 things cookin' now.
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[D][ ] standup (by: Jan 06 2026) (every: daily)
2.[D][ ] submit report (by: Jan 10 2026)
____________________________________________________________
____________________________________________________________
That one's on repeat, so I bumped it to the next round instead of calling it done:
  [D][ ] standup (by: Jan 07 2026) (every: daily)
____________________________________________________________
____________________________________________________________
Here's what's on your plate:
1.[D][ ] standup (by: Jan 07 2026) (every: daily)
2.[D][ ] submit report (by: Jan 10 2026)
____________________________________________________________
____________________________________________________________
Nice one, that's outta here:
  [D][X] submit report (by: Jan 10 2026)
____________________________________________________________
____________________________________________________________
That one's on repeat, so I bumped it to the next round instead of calling it done:
  [D][ ] standup (by: Jan 08 2026) (every: daily)
____________________________________________________________
____________________________________________________________
'fortnightly' isn't a recurrence I know. Try daily, weekly, or monthly.
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```

## Test 14: List all commands

Aim: Verify `commands` prints every command and how to format it, so a
first-time user can discover the full command set without external docs.

### Input

```
commands
bye
```

### Expected Output

```
____________________________________________________________
  ____ ___   ____ ___  
 / ___/ _ \ / ___/ _ \ 
| |  | | | | |  | | | |
| |__| |_| | |__| |_| |
 \____\___/ \____\___/ 

Hey hey, Coco here! 🌴
What're we getting done today?
____________________________________________________________
____________________________________________________________
Here's the full lineup, no worries:
  todo <description> - add a todo
  deadline <description> /by <yyyy-mm-dd> [/every daily|weekly|monthly] - add a deadline, optionally recurring
  event <description> /from <start> /to <end> - add an event
  list - show every task
  find <keyword> - search by keyword
  mark <task number> - mark done (or bump a recurring one along)
  unmark <task number> - mark not done
  delete <task number> - remove a task
  commands - show this list again
  bye - head out
____________________________________________________________
____________________________________________________________
Catch you on the flip side! 🌊
____________________________________________________________
```
