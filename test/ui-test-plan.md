---
main_class: billy.Billy
source_glob: src/main/java/**/*.java
exclude_glob: src/main/java/billy/gui/**/*.java
data_file: data/billy.txt
---

# Billy text UI test plan

Test cases for Billy's text user interface. Each case lists its aim, the exact
lines typed into the console, and the exact console output expected in return.

Run them with the `test-ui` skill, or directly:

```bash
python3 .claude/skills/test-ui/scripts/run-ui-tests.py
```

## How the cases are run

* Every case starts a **fresh run** of the program in a **working directory of its
  own**, so no case can be affected by tasks left behind by an earlier one —
  neither by what is still in memory, nor by the file the program saved to disk.
  Task numbering always restarts at 1.
* Because the working directory is empty, there is no saved data file unless the
  case asks for one, which is also how the program behaves the first time someone
  runs it on their computer.
* A case that needs a saved list to already exist writes it in an optional
  `**Data file:**` block. The runner plants those lines in `data/billy.txt` inside
  the case's working directory before the program starts. This is how a damaged
  file can be tested without damaging one by hand.
* An input line of exactly `--- restart ---` ends the run and starts another one
  in the same working directory. The output of both runs is compared as one
  session, which is how saving in one run and loading in the next is tested.
* The input lines are fed to the program's standard input, one line at a time.
  Ending with `bye` tests the normal exit. Leaving it out is also valid and tests
  the end-of-input exit, the same path as pressing Ctrl+D at the keyboard.
* The expected output is the **whole** console session, including the startup
  banner and the farewell, so any change to those is caught too. The banner and
  greeting are written once as the `GREETING` snippet, and the sign-off as
  `FAREWELL`; a line reading `{{GREETING}}` is replaced by those lines before
  comparing.
* Trailing spaces at the end of a line are ignored when comparing, since they are
  invisible on screen. Everything else must match exactly.
* Cases run in the order written, and the session stops at the first failure.

## Conventions

* Headings must start with `TC-` followed by a number, or the runner will not
  treat the section as a test case. A `## Snippet: NAME` heading defines reusable
  output instead of a case.
* Avoid trailing spaces on input lines: they are invisible, and many editors strip
  them on save. Leading and repeated inner spaces are safe to test with.
* The settings at the top of this file name the entry point class and which
  source files to compile.

## Snippet: GREETING

Everything Billy prints on startup, before any command is typed.

```text
____________________________________________________________
 ____  _ _ _
| __ )(_) | |_   _
|  _ \| | | | | | |
| |_) | | | | |_| |
|____/|_|_|_|\__, |
             |___/
BILLY HERE! Keeper of lists, guardian of things you would otherwise forget.
Todos, deadlines, events. Say 'list' and I'll spill it all. 'help' if you're lost.
____________________________________________________________
```

## Snippet: FAREWELL

Billy's sign-off, printed when the conversation ends.

```text
Off you go! I'll be right here, guarding the list. Vigilantly.
____________________________________________________________
```

## TC-01 Greeting and immediate exit

**Aim:** Verify that Billy shows the banner and greeting on startup, and exits with a farewell when the user types `bye`.

**Input:**
```text
bye
```

**Expected output:**
```text
{{GREETING}}
{{FAREWELL}}
```

## TC-02 Adding one task of each type

**Aim:** Verify that `todo`, `deadline` and `event` each store a task, show the correct type and status icons, and report a running count that is singular for the first task.

**Input:**
```text
todo borrow book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] borrow book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [D][ ] return book (by: Dec 2 2019, 6:00pm)
That's 2 tasks on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)
That's 3 tasks on the books.
____________________________________________________________
{{FAREWELL}}
```

## TC-03 Listing a mix of task types and statuses

**Aim:** Verify that `list` numbers tasks from 1 and shows each one's type and done status, mixing all three task types. Mirrors the worked example in the Level-4 requirements.

**Input:**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
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
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [D][ ] return book (by: Jun 6 2019)
That's 2 tasks on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [E][ ] project meeting (from: Aug 6 2019, 2:00pm to: Aug 6 2019, 4:00pm)
That's 3 tasks on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] join sports club
That's 4 tasks on the books.
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [T][X] read book
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] borrow book
That's 5 tasks on the books.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2019)
3.[E][ ] project meeting (from: Aug 6 2019, 2:00pm to: Aug 6 2019, 4:00pm)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
{{FAREWELL}}
```

## TC-04 Marking and unmarking keeps the task type

**Aim:** Verify that `mark` and `unmark` change only the status box, leaving the type label and the due date of a deadline intact. Guards the polymorphic formatting introduced by the Task subclasses.

**Input:**
```text
deadline pay fees /by 2019-12-06
mark 1
unmark 1
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [D][ ] pay fees (by: Dec 6 2019)
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [D][X] pay fees (by: Dec 6 2019)
And that's your whole list conquered. Take a bow.
____________________________________________________________
____________________________________________________________
Un-done! We've all been there:
  [D][ ] pay fees (by: Dec 6 2019)
____________________________________________________________
{{FAREWELL}}
```

## TC-05 Dates are understood rather than echoed back

**Aim:** Verify that a date is read as a date and shown in Billy's own format, not repeated as it was typed: `2019-10-15` must come back as `Oct 15 2019`. A date given with a time keeps it, and one given without a time must not have midnight invented for it. Free text, which earlier versions accepted, is now refused with the format spelt out — a deliberate change, since a stored date that means nothing to Billy would be no use to the `on` command.

**Input:**
```text
deadline do homework /by 2019-10-15
deadline call mum /by 2019-10-15 0930
deadline do nothing /by no idea :-p
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [D][ ] do homework (by: Oct 15 2019)
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [D][ ] call mum (by: Oct 15 2019, 9:30am)
That's 2 tasks on the books.
____________________________________________________________
____________________________________________________________
I couldn't read 'no idea :-p' as a date. Use yyyy-MM-dd or d/M/yyyy, and a 24-hour time if you want one, e.g. 2019-12-02, 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
{{FAREWELL}}
```

## TC-06 Empty list and blank input

**Aim:** Verify that listing with nothing stored, and pressing Enter on an empty line, both give a helpful message rather than an error or an empty task.

**Input:**
```text
list

bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Nothing. Nada. An empty list. Suspicious.
____________________________________________________________
____________________________________________________________
You'll have to give me something to work with!
____________________________________________________________
{{FAREWELL}}
```

## TC-07 Invalid task numbers are rejected

**Aim:** Verify that a missing number, a non-numeric number, a number beyond the end of the list, and zero are all reported clearly instead of crashing the program.

**Input:**
```text
todo read book
mark
mark nine
mark 7
unmark 0
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
I need a task number, like 'mark 2'.
____________________________________________________________
____________________________________________________________
I need a task number, like 'mark 2'.
____________________________________________________________
____________________________________________________________
There's no task 7 on your list. You have 1.
____________________________________________________________
____________________________________________________________
There's no task 0 on your list. You have 1.
____________________________________________________________
{{FAREWELL}}
```

## TC-08 Malformed deadline and event commands

**Aim:** Verify that a deadline with no `/by`, a deadline with no description, and an event missing its `/to` each name the specific part that is missing, rather than storing a broken task.

**Input:**
```text
deadline homework
deadline /by 2019-12-02
event meeting /from 2pm
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
I need '/by' in that command. Try: deadline return book /by 2019-12-02 1800
____________________________________________________________
____________________________________________________________
The description can't be empty. Try: deadline return book /by 2019-12-02 1800
____________________________________________________________
____________________________________________________________
I need '/to' in that command. Try: event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
____________________________________________________________
{{FAREWELL}}
```

## TC-09 Unknown commands are rejected

**Aim:** Verify that a keyword Billy does not recognise is reported, and that nothing is stored as a result, so a typo cannot quietly become a task. The closing `list` confirms the list is still empty.

**Input:**
```text
blah
buy bread
list
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
I don't know what 'blah' means. I understand: todo, deadline, event, list, on, find, mark, unmark, delete, help, bye. Type 'help' for the short forms.
____________________________________________________________
____________________________________________________________
I don't know what 'buy' means. I understand: todo, deadline, event, list, on, find, mark, unmark, delete, help, bye. Type 'help' for the short forms.
____________________________________________________________
____________________________________________________________
Nothing. Nada. An empty list. Suspicious.
____________________________________________________________
{{FAREWELL}}
```

## TC-10 Missing parts are named specifically

**Aim:** Verify that when a command has the right shape but a part of it is blank, the error names which part is missing, so the user knows what to correct.

**Input:**
```text
todo
deadline homework /by
event meeting /from /to 4pm
event meeting /from Mon /to
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
The description of a todo can't be empty. Try: todo borrow book
____________________________________________________________
____________________________________________________________
The due date can't be empty. Try: deadline return book /by 2019-12-02 1800
____________________________________________________________
____________________________________________________________
The start time can't be empty. Try: event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
____________________________________________________________
____________________________________________________________
The end time can't be empty. Try: event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
____________________________________________________________
{{FAREWELL}}
```

## TC-11 Marking when the list is empty

**Aim:** Verify that marking or unmarking with nothing stored explains that the list is empty, rather than reporting a task number that could never have worked.

**Input:**
```text
mark 1
unmark 3
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Your list is empty, so there's nothing to change.
____________________________________________________________
____________________________________________________________
Your list is empty, so there's nothing to change.
____________________________________________________________
{{FAREWELL}}
```

## TC-12 Deleting a task renumbers the rest

**Aim:** Verify that `delete` removes the task at the given number, confirms which task went, reports the new count, and that the tasks after it move up so numbering stays consecutive. Mirrors the worked example in the Level-6 requirements.

**Input:**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [D][ ] return book (by: Jun 6 2019)
That's 2 tasks on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [E][ ] project meeting (from: Aug 6 2019, 2:00pm to: Aug 6 2019, 4:00pm)
That's 3 tasks on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] join sports club
That's 4 tasks on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] borrow book
That's 5 tasks on the books.
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [T][X] read book
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [D][X] return book (by: Jun 6 2019)
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 6 2019)
3.[E][ ] project meeting (from: Aug 6 2019, 2:00pm to: Aug 6 2019, 4:00pm)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Gone. Vanished. No trace:
  [E][ ] project meeting (from: Aug 6 2019, 2:00pm to: Aug 6 2019, 4:00pm)
That's 4 tasks on the books.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 6 2019)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
{{FAREWELL}}
```

## TC-13 Deleting with a bad or missing task number

**Aim:** Verify that deleting from an empty list, deleting without a number, and deleting a number beyond the end are each reported clearly, and that deleting the only task leaves an empty list rather than a broken one.

**Input:**
```text
delete 1
todo read book
delete
delete 9
delete 1
list
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Your list is empty, so there's nothing to change.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
I need a task number, like 'delete 2'.
____________________________________________________________
____________________________________________________________
There's no task 9 on your list. You have 1.
____________________________________________________________
____________________________________________________________
Gone. Vanished. No trace:
  [T][ ] read book
And that was the last of them. Empty list. Savor it.
____________________________________________________________
____________________________________________________________
Nothing. Nada. An empty list. Suspicious.
____________________________________________________________
{{FAREWELL}}
```

## TC-14 Ending the session by running out of input

**Aim:** Verify that Billy exits cleanly with its farewell when the input ends without `bye`, which is what happens when a user presses Ctrl+D. Guards the `hasNextLine` check that stops the loop from reading past the end of input.

**Input:**
```text
todo read book
list
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][ ] read book
____________________________________________________________
{{FAREWELL}}
```

## TC-15 Keywords ignore case and surrounding spaces

**Aim:** Verify that commands are recognised whatever their capitalisation, and that leading or repeated spaces do not stop a command being understood. The task description must keep the capitalisation the user typed, since only the keyword is lowercased.

**Input:**
```text
TODO Read Book
   LiSt
  MaRk   1
List
BYE
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] Read Book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][ ] Read Book
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [T][X] Read Book
And that's your whole list conquered. Take a bow.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][X] Read Book
____________________________________________________________
{{FAREWELL}}
```

## TC-16 Marking a task that is already done

**Aim:** Verify that marking an already-done task is harmless and simply confirms it again, and that unmarking afterwards still returns the task to not done. Pins down that the status is set outright rather than toggled.

**Input:**
```text
todo read book
mark 1
mark 1
unmark 1
list
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [T][X] read book
And that's your whole list conquered. Take a bow.
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [T][X] read book
And that's your whole list conquered. Take a bow.
____________________________________________________________
____________________________________________________________
Un-done! We've all been there:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][ ] read book
____________________________________________________________
{{FAREWELL}}
```

## TC-17 Anything after `bye` is ignored

**Aim:** Verify that the exit command is recognised from its first word like every other command, so `bye now` ends the session rather than being reported as unknown. The line after it must not run, which proves the session really ended.

**Input:**
```text
todo read book
bye now
todo this must never run
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
{{FAREWELL}}
```

## TC-18 Tasks are saved and reloaded on the next run

**Aim:** Verify the point of Level 7: the list survives Billy being closed. The first run adds one task of each type and marks one of them; the second run must show the same three tasks, in the same order, with the same done statuses — which also proves that Billy created the `data` folder and the file itself, since the working directory started empty. The `Welcome back` note tells the user why their old tasks are there.

**Input:**
```text
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
mark 2
bye
--- restart ---
list
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [D][ ] return book (by: Dec 2 2019, 6:00pm)
That's 2 tasks on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)
That's 3 tasks on the books.
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [D][X] return book (by: Dec 2 2019, 6:00pm)
____________________________________________________________
{{FAREWELL}}
{{GREETING}}
____________________________________________________________
Welcome back! Nothing moved while you were gone: 3 tasks still waiting.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][ ] read book
2.[D][X] return book (by: Dec 2 2019, 6:00pm)
3.[E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)
____________________________________________________________
{{FAREWELL}}
```

## TC-19 Deleting and unmarking are saved too

**Aim:** Verify that saving is triggered by *every* change to the list, not just by adding. A task is deleted and another unmarked in the first run; if either change were left unsaved, the second run would show the old list. Loading exactly one task also checks the singular wording of the `Welcome back` note.

**Input:**
```text
todo read book
todo return book
mark 1
mark 2
delete 2
unmark 1
bye
--- restart ---
list
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] return book
That's 2 tasks on the books.
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [T][X] read book
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [T][X] return book
And that's your whole list conquered. Take a bow.
____________________________________________________________
____________________________________________________________
Gone. Vanished. No trace:
  [T][X] return book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Un-done! We've all been there:
  [T][ ] read book
____________________________________________________________
{{FAREWELL}}
{{GREETING}}
____________________________________________________________
Welcome back! Nothing moved while you were gone: 1 task still waiting.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][ ] read book
____________________________________________________________
{{FAREWELL}}
```

## TC-20 Emptying the list is saved as an empty list

**Aim:** Verify that deleting the last task really empties the saved file rather than leaving the old contents behind. Billy should say nothing about a previous session on the next run, since there is nothing to report — the same as a first run.

**Input:**
```text
todo read book
delete 1
bye
--- restart ---
list
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Gone. Vanished. No trace:
  [T][ ] read book
And that was the last of them. Empty list. Savor it.
____________________________________________________________
{{FAREWELL}}
{{GREETING}}
____________________________________________________________
Nothing. Nada. An empty list. Suspicious.
____________________________________________________________
{{FAREWELL}}
```

## TC-21 A saved list written by hand is loaded

**Aim:** Verify that Billy reads the documented file format, not merely whatever it happens to write itself. The file here is typed by hand, with the type letter, the done flag and the details of each kind of task, and every one of the three types must come back correctly.

**Data file:**
```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 1 | project meeting | 2019-08-06 1400 | 2019-08-06 1600
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
Welcome back! Nothing moved while you were gone: 3 tasks still waiting.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2019)
3.[E][X] project meeting (from: Aug 6 2019, 2:00pm to: Aug 6 2019, 4:00pm)
____________________________________________________________
{{FAREWELL}}
```

## TC-22 A corrupted file loses only the damaged lines

**Aim:** Verify the stretch goal: a file that has been edited badly must not crash Billy or cost the user the tasks that are still readable. The damaged lines here cover the ways a line can go wrong — an unknown type letter, a done flag that is not 0 or 1, a missing field, and text that is not in the format at all. Billy should load the two good tasks, say how many lines it gave up on, and carry on working normally.

**Data file:**
```text
T | 1 | read book
X | 0 | who knows what this is
D | 2 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-02 1400
this line is not in the format at all
T | 0 | join sports club
```

**Input:**
```text
list
todo buy milk
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Welcome back! Nothing moved while you were gone: 2 tasks still waiting.
Heads up: 4 lines in data/billy.txt were gibberish to me, so I skipped them.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][X] read book
2.[T][ ] join sports club
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] buy milk
That's 3 tasks on the books.
____________________________________________________________
{{FAREWELL}}
```

## TC-23 An empty saved file is not worth mentioning

**Aim:** Verify that a file left behind by a session that ended with no tasks is treated as an empty list rather than as damage. Blank lines in it should be passed over quietly, with no warning about skipped lines.

**Data file:**
```text

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
Nothing. Nada. An empty list. Suspicious.
____________________________________________________________
{{FAREWELL}}
```

## TC-24 Finding the tasks on one day

**Aim:** Verify the `on` command. A deadline matches only the day it is due, while an event matches every day it runs across, both ends included — so the multi-day event here must be found on all three of its days, and the deadline on only one. The todo has no date and must never appear. Each match is shown with the number it carries in the full list, not a fresh count, so a task found this way can be marked or deleted straight away.

**Input:**
```text
deadline return book /by 2019-12-05 1800
event conference /from 2019-12-02 0900 /to 2019-12-04 1700
todo buy milk
on 2019-12-02
on 2019-12-03
on 2019-12-05
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [D][ ] return book (by: Dec 5 2019, 6:00pm)
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [E][ ] conference (from: Dec 2 2019, 9:00am to: Dec 4 2019, 5:00pm)
That's 2 tasks on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] buy milk
That's 3 tasks on the books.
____________________________________________________________
____________________________________________________________
Here's what Dec 2 2019 has in store:
2.[E][ ] conference (from: Dec 2 2019, 9:00am to: Dec 4 2019, 5:00pm)
____________________________________________________________
____________________________________________________________
Here's what Dec 3 2019 has in store:
2.[E][ ] conference (from: Dec 2 2019, 9:00am to: Dec 4 2019, 5:00pm)
____________________________________________________________
____________________________________________________________
Here's what Dec 5 2019 has in store:
1.[D][ ] return book (by: Dec 5 2019, 6:00pm)
____________________________________________________________
{{FAREWELL}}
```

## TC-25 Asking about a day with nothing on it, or asking badly

**Aim:** Verify that a day with no tasks is answered plainly rather than with an empty list, that a day just outside an event's span is correctly found to be empty, and that `on` with no date or an unreadable one says how to type it. A time given to `on` is ignored, since a whole day is being asked about.

**Input:**
```text
event conference /from 2019-12-02 0900 /to 2019-12-04 1700
on 2019-12-05
on 2019-12-04 2359
on
on next tuesday
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [E][ ] conference (from: Dec 2 2019, 9:00am to: Dec 4 2019, 5:00pm)
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Nothing on Dec 5 2019. Enjoy the day off!
____________________________________________________________
____________________________________________________________
Here's what Dec 4 2019 has in store:
1.[E][ ] conference (from: Dec 2 2019, 9:00am to: Dec 4 2019, 5:00pm)
____________________________________________________________
____________________________________________________________
Which day should I look at? Try: on 2019-12-02
____________________________________________________________
____________________________________________________________
I couldn't read 'next tuesday' as a date. Use yyyy-MM-dd or d/M/yyyy, and a 24-hour time if you want one, e.g. 2019-12-02, 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
{{FAREWELL}}
```

## TC-26 Dates survive being saved and reloaded

**Aim:** Verify that a date is written to disk in a form that reads back as the same date, and that the difference between a date with a time and one without survives the round trip. If a time were invented on loading, the reloaded deadline would show midnight; if one were dropped, the event would lose its start time. The `on` command working after the restart proves the reloaded dates are real dates and not just text that looks right.

**Input:**
```text
deadline return book /by 2019-12-05
event conference /from 2019-12-02 0900 /to 2019-12-04 1700
bye
--- restart ---
list
on 2019-12-03
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [D][ ] return book (by: Dec 5 2019)
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [E][ ] conference (from: Dec 2 2019, 9:00am to: Dec 4 2019, 5:00pm)
That's 2 tasks on the books.
____________________________________________________________
{{FAREWELL}}
{{GREETING}}
____________________________________________________________
Welcome back! Nothing moved while you were gone: 2 tasks still waiting.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[D][ ] return book (by: Dec 5 2019)
2.[E][ ] conference (from: Dec 2 2019, 9:00am to: Dec 4 2019, 5:00pm)
____________________________________________________________
____________________________________________________________
Here's what Dec 3 2019 has in store:
2.[E][ ] conference (from: Dec 2 2019, 9:00am to: Dec 4 2019, 5:00pm)
____________________________________________________________
{{FAREWELL}}
```

## TC-27 A saved date that is not a date is treated as damage

**Aim:** Verify that the corrupted-file handling now covers dates too. A file written by an older version of Billy holds free text where a date should be, and a hand-edited one may hold an impossible date such as the 31st of February. Neither can become a task, so both lines must be skipped and counted, leaving the readable tasks untouched.

**Data file:**
```text
T | 0 | read book
D | 0 | return book | Sunday
D | 0 | pay fees | 2019-02-31
E | 0 | conference | 2019-12-02 0900 | 2019-12-04 1700
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
Welcome back! Nothing moved while you were gone: 2 tasks still waiting.
Heads up: 2 lines in data/billy.txt were gibberish to me, so I skipped them.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][ ] read book
2.[E][ ] conference (from: Dec 2 2019, 9:00am to: Dec 4 2019, 5:00pm)
____________________________________________________________
{{FAREWELL}}
```

## TC-28 Dates may be written day-first with slashes

**Aim:** Verify the second accepted date format, `d/M/yyyy`, so that `2/12/2019 1800` means the 2nd of December 2019 at 6pm rather than being refused. The day comes first, so `12/2/2019` must be read as the 12th of February and not the American way round. A single digit is allowed for the day or the month, and an impossible date in this format must still be caught. Both formats end up displayed identically, since the format a date was typed in says nothing about the date itself.

**Input:**
```text
deadline return book /by 2/12/2019 1800
deadline pay fees /by 02/12/2019
deadline call mum /by 12/2/2019
deadline never /by 31/2/2019
list
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [D][ ] return book (by: Dec 2 2019, 6:00pm)
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [D][ ] pay fees (by: Dec 2 2019)
That's 2 tasks on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [D][ ] call mum (by: Feb 12 2019)
That's 3 tasks on the books.
____________________________________________________________
____________________________________________________________
I couldn't read '31/2/2019' as a date. Use yyyy-MM-dd or d/M/yyyy, and a 24-hour time if you want one, e.g. 2019-12-02, 2/12/2019 or 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[D][ ] return book (by: Dec 2 2019, 6:00pm)
2.[D][ ] pay fees (by: Dec 2 2019)
3.[D][ ] call mum (by: Feb 12 2019)
____________________________________________________________
{{FAREWELL}}
```

## TC-29 Both date formats are saved the same way

**Aim:** Verify that the format a date was typed in is not carried into the save file. A date read from `2/12/2019` and one read from `2019-12-02` are the same date, so after a restart they must be indistinguishable. This also proves the day-first form is converted on the way in rather than stored as text and reinterpreted on the way out, which would risk `12/2/2019` coming back as the 2nd of December.

**Input:**
```text
deadline slashes /by 12/2/2019 0930
deadline dashes /by 2019-02-12 0930
bye
--- restart ---
list
on 12/2/2019
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [D][ ] slashes (by: Feb 12 2019, 9:30am)
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [D][ ] dashes (by: Feb 12 2019, 9:30am)
That's 2 tasks on the books.
____________________________________________________________
{{FAREWELL}}
{{GREETING}}
____________________________________________________________
Welcome back! Nothing moved while you were gone: 2 tasks still waiting.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[D][ ] slashes (by: Feb 12 2019, 9:30am)
2.[D][ ] dashes (by: Feb 12 2019, 9:30am)
____________________________________________________________
____________________________________________________________
Here's what Feb 12 2019 has in store:
1.[D][ ] slashes (by: Feb 12 2019, 9:30am)
2.[D][ ] dashes (by: Feb 12 2019, 9:30am)
____________________________________________________________
{{FAREWELL}}
```

## TC-30 An event cannot end before it starts

**Aim:** Verify that a backwards event is refused instead of stored. Such an event covers no days at all, so it would sit in the list and yet never be found by `on`, even on the days it names — which reads as a fault in the search rather than in the event. Both a backwards day and a backwards time on the same day must be caught, while an end with no time given must not be mistaken for midnight and wrongly refused.

**Input:**
```text
event backwards /from 2019-12-10 1000 /to 2019-12-02 1000
event too early /from 2019-12-02 1400 /to 2019-12-02 1000
event same day /from 2019-12-02 1000 /to 2019-12-02 1400
event open end /from 2019-12-02 1000 /to 2019-12-02
list
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
An event can't end before it starts, and you gave from: Dec 10 2019, 10:00am to: Dec 2 2019, 10:00am.
____________________________________________________________
____________________________________________________________
An event can't end before it starts, and you gave from: Dec 2 2019, 2:00pm to: Dec 2 2019, 10:00am.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [E][ ] same day (from: Dec 2 2019, 10:00am to: Dec 2 2019, 2:00pm)
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [E][ ] open end (from: Dec 2 2019, 10:00am to: Dec 2 2019)
That's 2 tasks on the books.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[E][ ] same day (from: Dec 2 2019, 10:00am to: Dec 2 2019, 2:00pm)
2.[E][ ] open end (from: Dec 2 2019, 10:00am to: Dec 2 2019)
____________________________________________________________
{{FAREWELL}}
```

## TC-31 A backwards event in the save file is treated as damage

**Aim:** Verify that the rule holds for files as well as commands. A hand-edited file could hold an event that ends before it starts; since such an event cannot exist, the line must be skipped and counted like any other damage rather than loaded through a back door that the typed command refuses.

**Data file:**
```text
T | 0 | read book
E | 0 | backwards | 2019-12-10 1000 | 2019-12-02 1000
E | 0 | conference | 2019-12-02 0900 | 2019-12-04 1700
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
Welcome back! Nothing moved while you were gone: 2 tasks still waiting.
Heads up: 1 line in data/billy.txt was gibberish to me, so I skipped it.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][ ] read book
2.[E][ ] conference (from: Dec 2 2019, 9:00am to: Dec 4 2019, 5:00pm)
____________________________________________________________
{{FAREWELL}}
```

## TC-32 Find shows the tasks whose description matches

**Aim:** Verify the main use of `find`: a word typed after the keyword lists
every task whose description contains it, and nothing else. The matches keep
the numbers they have in the full list, so a task found this way can be acted
on straight away — the closing `mark` proves that number is the real one.

**Input:**
```text
todo read book
deadline return book /by 2019-06-06
todo buy milk
find book
mark 2
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [D][ ] return book (by: Jun 6 2019)
That's 2 tasks on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] buy milk
That's 3 tasks on the books.
____________________________________________________________
____________________________________________________________
Found these lurking in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 6 2019)
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [D][X] return book (by: Jun 6 2019)
____________________________________________________________
{{FAREWELL}}
```

## TC-33 Find ignores capitals and matches part of a word

**Aim:** Verify that a search is not defeated by how the task was capitalised,
and that part of a word counts. Someone who wrote "Textbook" should still find
it by typing `book`, since otherwise they must remember their own wording to
search for it. The third task is present to show the search does not simply
return everything.

**Input:**
```text
todo Read Textbook
todo buy milk
find BOOK
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] Read Textbook
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] buy milk
That's 2 tasks on the books.
____________________________________________________________
____________________________________________________________
Found these lurking in your list:
1.[T][ ] Read Textbook
____________________________________________________________
{{FAREWELL}}
```

## TC-34 Find says so when nothing matches

**Aim:** Verify that a search matching nothing is answered plainly rather than
with an empty heading, which would read as though something had gone wrong. The
word is quoted back so a typo in the search itself is easy to see. Searching an
empty list is the same case and must not fail differently.

**Input:**
```text
find milk
todo read book
find zzz
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Searched high and low. Nothing mentions 'milk'.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Searched high and low. Nothing mentions 'zzz'.
____________________________________________________________
{{FAREWELL}}
```

## TC-35 Find needs something to look for

**Aim:** Verify that `find` with no word is refused with an example rather than
listing every task or silently matching everything, either of which would look
like Billy had misunderstood. A search made only of spaces is the same case,
since the line is trimmed before it is read.

**Input:**
```text
todo read book
find
find   
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
What should I look for? Try: find book
____________________________________________________________
____________________________________________________________
What should I look for? Try: find book
____________________________________________________________
{{FAREWELL}}
```

## TC-36 Short words invoke the same commands

**Aim:** Verify that the shorter word declared beside each keyword reaches the
same command, so that a whole session can be held in the short forms. Adding,
listing, marking and deleting are all exercised, since a short word that reached
the wrong command would still look plausible on its own.

**Input:**
```text
t read book
dl return book /by 2019-12-02
l
m 1
rm 2
q
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [D][ ] return book (by: Dec 2 2019)
That's 2 tasks on the books.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 2 2019)
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Gone. Vanished. No trace:
  [D][ ] return book (by: Dec 2 2019)
That's 1 task on the books.
____________________________________________________________
{{FAREWELL}}
```

## TC-37 A short word is read the same whatever its case

**Aim:** Verify that capitalization is the user's business for a short word as
much as for a full keyword, and that a near miss is still refused rather than
guessed at. The refusal names the full keywords only, since the short words are
for someone who already knows the command.

**Input:**
```text
T borrow book
LS
tt read book
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] borrow book
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
I don't know what 'tt' means. I understand: todo, deadline, event, list, on, find, mark, unmark, delete, help, bye. Type 'help' for the short forms.
____________________________________________________________
{{FAREWELL}}
```

## TC-38 Help names every command and its shorter words

**Aim:** Verify that `help` lists every command Billy accepts, grouped by the kind of work it does, and names the shorter words alongside each. This is the only place inside Billy where the abbreviations are advertised, since the message shown for an unknown command deliberately leaves them out.

**Input:**
```text
help
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Everything I know how to do. Short forms in brackets, for the impatient.
Adding: todo (t), deadline (d, dl), event (e, ev)
Seeing: list (l, ls), on, find (f)
Changing: mark (m), unmark (um), delete (del, rm)
Other: help (h, ?), bye (exit, quit, q)
____________________________________________________________
{{FAREWELL}}
```

## TC-39 Help answers to its own shorter words

**Aim:** Verify that `h` and `?` show the same listing as `help`. A command that advertises abbreviations but does not answer to its own would be a poor advertisement for them.

**Input:**
```text
h
?
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Everything I know how to do. Short forms in brackets, for the impatient.
Adding: todo (t), deadline (d, dl), event (e, ev)
Seeing: list (l, ls), on, find (f)
Changing: mark (m), unmark (um), delete (del, rm)
Other: help (h, ?), bye (exit, quit, q)
____________________________________________________________
____________________________________________________________
Everything I know how to do. Short forms in brackets, for the impatient.
Adding: todo (t), deadline (d, dl), event (e, ev)
Seeing: list (l, ls), on, find (f)
Changing: mark (m), unmark (um), delete (del, rm)
Other: help (h, ?), bye (exit, quit, q)
____________________________________________________________
{{FAREWELL}}
```

## TC-40 Help leaves the task list alone

**Aim:** Verify that asking for help neither changes the list nor disturbs what is saved. `help` is the one command that touches neither, so the list must read back exactly as it did before, including across a restart.

**Data file:**
```text
T | 0 | read book
D | 1 | return book | 2019-12-02 1800
```

**Input:**
```text
help
list
--- restart ---
list
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Welcome back! Nothing moved while you were gone: 2 tasks still waiting.
____________________________________________________________
____________________________________________________________
Everything I know how to do. Short forms in brackets, for the impatient.
Adding: todo (t), deadline (d, dl), event (e, ev)
Seeing: list (l, ls), on, find (f)
Changing: mark (m), unmark (um), delete (del, rm)
Other: help (h, ?), bye (exit, quit, q)
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][ ] read book
2.[D][X] return book (by: Dec 2 2019, 6:00pm)
____________________________________________________________
{{FAREWELL}}
{{GREETING}}
____________________________________________________________
Welcome back! Nothing moved while you were gone: 2 tasks still waiting.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][ ] read book
2.[D][X] return book (by: Dec 2 2019, 6:00pm)
____________________________________________________________
{{FAREWELL}}
```

## TC-41 Billy remarks on a list that has got long

**Aim:** Verify that passing ten tasks earns a remark, and that dropping back below ten loses it again. The threshold is worth pinning end to end because it is the one piece of Billy's character that depends on arithmetic rather than on which command was typed. Nine tasks are loaded from the save file so the case can reach the boundary in one command.

**Data file:**
```text
T | 0 | task one
T | 0 | task two
T | 0 | task three
T | 0 | task four
T | 0 | task five
T | 0 | task six
T | 0 | task seven
T | 0 | task eight
T | 0 | task nine
```

**Input:**
```text
todo task ten
delete 10
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Welcome back! Nothing moved while you were gone: 9 tasks still waiting.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] task ten
That's 10 tasks on the books. Ambitious.
____________________________________________________________
____________________________________________________________
Gone. Vanished. No trace:
  [T][ ] task ten
That's 9 tasks on the books.
____________________________________________________________
{{FAREWELL}}
```

## TC-42 A description may not contain the field separator

**Aim:** Verify that a description containing `|` is refused when it is typed, rather than accepted and then lost. Billy separates the fields of a saved task with `|`, so such a description splits into the wrong fields when the file is read back: a deadline loses its date and the whole task is dropped as damaged on the next run. The todo case is included because its description is the last field on the line and so happens to survive a round trip; it is refused all the same, so the rule does not depend on the task type.

**Input:**
```text
deadline pay rent | utilities /by 2019-12-02
todo buy milk | eggs
todo 50% off / half price (maybe)
list
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
A description can't contain '|', since that's what I use to separate the parts of a saved task. Try wording it without one.
____________________________________________________________
____________________________________________________________
A description can't contain '|', since that's what I use to separate the parts of a saved task. Try wording it without one.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] 50% off / half price (maybe)
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[T][ ] 50% off / half price (maybe)
____________________________________________________________
{{FAREWELL}}
```

## TC-43 A marker given twice is named as the problem

**Aim:** Verify that repeating `/by` or `/from` is reported as the repetition it is. Splitting at the first marker and leaving the rest in place would push the second copy into the date, where it would surface as an unreadable date and say nothing about the actual mistake.

**Input:**
```text
deadline essay /by 2019-12-02 /by 2019-12-03
event meeting /from 2019-12-02 /from 2019-12-03 /to 2019-12-04
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
I found more than one '/by' in that command, so I don't know which one you meant. Try: deadline return book /by 2019-12-02 1800
____________________________________________________________
____________________________________________________________
I found more than one '/from' in that command, so I don't know which one you meant. Try: event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
____________________________________________________________
{{FAREWELL}}
```

## TC-44 An event has to cover some time

**Aim:** Verify that an event starting and ending at the same stated moment is refused, while an all-day event is not. These are told apart by whether a time of day was given: `/from 2019-12-02 /to 2019-12-02` is an ordinary event lasting the whole day, and refusing it alongside the zero-length case would break something that has always worked.

**Input:**
```text
event standup /from 2019-12-02 1400 /to 2019-12-02 1400
event conference /from 2019-12-02 /to 2019-12-02
list
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
An event has to last some time, and you gave from: Dec 2 2019, 2:00pm to: Dec 2 2019, 2:00pm. Leave the times off if you mean the whole day.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [E][ ] conference (from: Dec 2 2019 to: Dec 2 2019)
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Behold, your list:
1.[E][ ] conference (from: Dec 2 2019 to: Dec 2 2019)
____________________________________________________________
{{FAREWELL}}
```

## TC-45 Adding a task that is already on the list

**Aim:** Verify that a duplicate is added but remarked on, that the remark names every outstanding match and the number to delete to undo it, and that a finished task is not treated as a duplicate. The last part is the point: ticking a chore off and adding it again is how a repeat is done, so warning about it would be wrong.

**Input:**
```text
todo water the plants
todo WATER THE PLANTS
todo water the plants
mark 1
mark 2
mark 3
todo water the plants
bye
```

**Expected output:**
```text
{{GREETING}}
____________________________________________________________
Consider it written down:
  [T][ ] water the plants
That's 1 task on the books.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] WATER THE PLANTS
That's 2 tasks on the books.
Heads up: that's the same as task 1. Type 'delete 2' if you didn't mean it.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] water the plants
That's 3 tasks on the books.
Heads up: that's the same as tasks 1 and 2. Type 'delete 3' if you didn't mean it.
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [T][X] water the plants
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [T][X] WATER THE PLANTS
____________________________________________________________
____________________________________________________________
BOOM. Done and dusted:
  [T][X] water the plants
And that's your whole list conquered. Take a bow.
____________________________________________________________
____________________________________________________________
Consider it written down:
  [T][ ] water the plants
That's 4 tasks on the books.
____________________________________________________________
{{FAREWELL}}
```
