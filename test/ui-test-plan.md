# Kenbot text-UI test plan

This file is the single record of the text-UI ("black box") test cases for
Kenbot. Each test case starts a **fresh** run of the program, types the listed
input lines into the console, and compares everything the program printed
against the expected output.

Each case also runs in its own **empty throwaway folder**. Kenbot saves its
tasks to `data/Kenbot.txt` relative to the folder it is started from, so this
keeps one case's saved file from being seen by the next one, and keeps the test
runner from overwriting the real `data/Kenbot.txt` in this repository.

Run the whole plan with:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

## How this file is read

* Every `## ` heading whose section contains an `**Input:**` block is a test
  case. The first word of the heading (for example `TC3`) is its identifier.
* `**Aim:**` says what the test case is checking, so a reader knows why it
  exists.
* `**Input:**` lists the lines typed into the console, one per line. Most test
  cases end with `bye`. A case may deliberately leave it out to check that the
  program copes with the input running out.
* `**Expected output:**` lists what the program should print *after* the
  greeting. The greeting is recorded once in the `Greeting` section below and
  is added automatically, so it does not have to be repeated.
* Trailing spaces at the end of a line and blank lines at the very end of the
  output are ignored when comparing, since a reader cannot see them.

The first test case that fails ends the session immediately; the expected
output, the actual output, and the difference between them are reported, and
the remaining test cases are not run.

## Greeting

The banner and welcome message printed at the start of every run. It is
prepended to each test case's expected output.

```text
____________________________________________________________
 _  __          _           _
| |/ /___ _ __ | |__   ___ | |_
| ' // _ \ '_ \| '_ \ / _ \| __|
| . \  __/ | | | |_) | (_) | |_
|_|\_\___|_| |_|_.__/ \___/ \__|

                 Kenbot
Yo, I'm Kenbot.
What are we getting done today?
____________________________________________________________

```

## TC1 - Greet and exit

**Aim:** Check that the program greets the user on startup and says goodbye
when given the `bye` command, without any task in the list.

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC2 - Add a to-do

**Aim:** Check that `todo` creates a `Todo`, which is displayed with the `[T]`
type icon and an empty status icon, and that the running task count is shown.

**Input:**

```text
todo read book
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC3 - Add a deadline

**Aim:** Check that `deadline ... /by ...` is split correctly and that the
deadline is displayed after the description as `(by: ...)`.

**Input:**

```text
deadline return book /by 2019-06-06
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [D][ ] return book (by: Jun 06 2019)
That makes 1 task.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC4 - Add an event

**Aim:** Check that `event ... /from ... /to ...` is split into three parts and
that the start and end times are displayed in the right order.

**Input:**

```text
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [E][ ] project meeting (from: Aug 06 2019 1400 to: Aug 06 2019 1600)
That makes 1 task.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC5 - List tasks of every type

**Aim:** Check that tasks of all three types can be held at once, that the
task count increases with each one, and that `list` numbers them from 1 in the
order they were added.

**Input:**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
list
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Got it, that's on the list:
  [D][ ] return book (by: Jun 06 2019)
That makes 2 tasks.
____________________________________________________________
____________________________________________________________
Got it, that's on the list:
  [E][ ] project meeting (from: Aug 06 2019 1400 to: Aug 06 2019 1600)
That makes 3 tasks.
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 1400 to: Aug 06 2019 1600)
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC6 - Mark and unmark a task

**Aim:** Check that `mark` sets the status icon to `X` and `unmark` clears it
again, and that the change is still visible in a later `list`.

**Input:**

```text
todo read book
mark 1
list
unmark 1
list
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Nice, that's done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Alright, back on the list:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC7 - Reject a deadline with no /by part

**Aim:** Check that a malformed `deadline` command is rejected with a usage
message instead of creating a task.

**Input:**

```text
deadline return book
bye
```

**Expected output:**

```text
____________________________________________________________
Deadlines need a description and a /by. Like:
  deadline return book /by 2019-10-15
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC8 - Reject an event with no /to part

**Aim:** Check that an `event` missing one of its three parts is rejected with a
usage message instead of creating a task.

**Input:**

```text
event project meeting /from 2019-08-06 1400
bye
```

**Expected output:**

```text
____________________________________________________________
Events need a description, a /from and a /to. Like:
  event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC9 - Reject a to-do with no description

**Aim:** Check that the command word on its own is rejected. This is the case
that used to create a task named "todo", because the command was recognised only
when followed by a space.

**Input:**

```text
todo
list
bye
```

**Expected output:**

```text
____________________________________________________________
Need a description for that. Like:
  todo read book
____________________________________________________________
____________________________________________________________
Nothing on the list yet.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC10 - Reject an unrecognised command

**Aim:** Check that an unknown command is reported, and that nothing is added to
the list as a side effect.

**Input:**

```text
blah
list
bye
```

**Expected output:**

```text
____________________________________________________________
Don't know that one.
____________________________________________________________
____________________________________________________________
Nothing on the list yet.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC11 - Show an empty list

**Aim:** Check that `list` on an empty list says so, rather than printing a
heading with nothing under it.

**Input:**

```text
list
bye
```

**Expected output:**

```text
____________________________________________________________
Nothing on the list yet.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC12 - Reject mark with no task number

**Aim:** Check that `mark` on its own is reported instead of crashing, and that
the list is unchanged afterwards.

**Input:**

```text
todo read book
mark
list
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Which task to mark? Like: mark 2
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC13 - Reject a task number that is not a number

**Aim:** Check that text where a number is expected is reported instead of
crashing.

**Input:**

```text
todo read book
mark abc
list
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
'abc' isn't a task number.
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC14 - Reject a task number that is too large

**Aim:** Check that a number past the end of the list is reported instead of
crashing, and that the task that does exist is left untouched.

**Input:**

```text
todo read book
mark 5
list
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
There's no task 5. You've got 1.
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC15 - Reject task number zero

**Aim:** Check the lower boundary. Task numbers shown to the user start at 1, so
`mark 0` has no task to refer to.

**Input:**

```text
todo read book
mark 0
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
There's no task 0. You've got 1.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC16 - Reject mark when the list is empty

**Aim:** Check that marking a task before adding any gives a message about the
empty list, rather than one about a range.

**Input:**

```text
mark 1
bye
```

**Expected output:**

```text
____________________________________________________________
There's no task 1, the list is empty.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC17 - Reject unmark with no task number

**Aim:** Check that the message names the command the user actually typed, so
`unmark` does not suggest typing `mark`.

**Input:**

```text
todo read book
unmark
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Which task to unmark? Like: unmark 2
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC18 - Keep working correctly after errors

**Aim:** Interleave working and failing commands to check that a rejected
command leaves the list exactly as it was, and that later commands still work.
A test case that only feeds bad input would not notice an error handler that
damages the list on its way out.

**Input:**

```text
todo read book
deadline return book /by 2019-06-06
mark 99
list
mark 2
blah
list
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Got it, that's on the list:
  [D][ ] return book (by: Jun 06 2019)
That makes 2 tasks.
____________________________________________________________
____________________________________________________________
There's no task 99. You've got 2.
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Nice, that's done:
  [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Don't know that one.
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] read book
2.[D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC19 - Cope with the input running out

**Aim:** Check that reaching the end of the input without typing `bye` ends the
program quietly. This used to crash. There is deliberately no `bye` here.

**Input:**

```text
todo read book
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
```

## TC20 - Delete a task from the middle of the list

**Aim:** Check that `delete` removes the task the user named, and that the tasks
after it are renumbered. Delete is the first command where the numbers shown by
`list` change, so an off-by-one would show up here and nowhere else.

**Input:**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
delete 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Got it, that's on the list:
  [D][ ] return book (by: Jun 06 2019)
That makes 2 tasks.
____________________________________________________________
____________________________________________________________
Got it, that's on the list:
  [E][ ] project meeting (from: Aug 06 2019 1400 to: Aug 06 2019 1600)
That makes 3 tasks.
____________________________________________________________
____________________________________________________________
Gone:
  [D][ ] return book (by: Jun 06 2019)
That makes 2 tasks.
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] read book
2.[E][ ] project meeting (from: Aug 06 2019 1400 to: Aug 06 2019 1600)
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC21 - Delete the first task and check the numbering shifts

**Aim:** Check the boundary at the start of the list. Removing task 1 should move
every other task up by one position.

**Input:**

```text
todo read book
todo join sports club
delete 1
list
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Got it, that's on the list:
  [T][ ] join sports club
That makes 2 tasks.
____________________________________________________________
____________________________________________________________
Gone:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] join sports club
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC22 - Delete the only task

**Aim:** Check that emptying the list leaves it in a usable state, reported the
same way as a list that was never filled.

**Input:**

```text
todo read book
delete 1
list
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Gone:
  [T][ ] read book
That makes 0 tasks.
____________________________________________________________
____________________________________________________________
Nothing on the list yet.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC23 - Reject delete with no task number

**Aim:** Check that `delete` on its own is reported, that the message names the
command the user typed, and that the list is unchanged.

**Input:**

```text
todo read book
delete
list
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Which task to delete? Like: delete 2
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC24 - Reject a delete number that is out of range

**Aim:** Check that a number past the end of the list is reported in the same
wording as `mark`, and that nothing is removed.

**Input:**

```text
todo read book
delete 99
delete abc
list
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
There's no task 99. You've got 1.
____________________________________________________________
____________________________________________________________
'abc' isn't a task number.
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC25 - Reject delete when the list is empty

**Aim:** Check that deleting before adding anything explains that the list is
empty, rather than quoting a range that makes no sense.

**Input:**

```text
delete 1
bye
```

**Expected output:**

```text
____________________________________________________________
There's no task 1, the list is empty.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC26 - Keep counting correctly across adds and deletes

**Aim:** Interleave adding, deleting and marking to check that the task count
follows the list rather than counting how many tasks were ever created, and that
marking still reaches the right task after a removal.

**Input:**

```text
todo read book
todo join sports club
delete 1
todo borrow book
mark 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Got it, that's on the list:
  [T][ ] join sports club
That makes 2 tasks.
____________________________________________________________
____________________________________________________________
Gone:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Got it, that's on the list:
  [T][ ] borrow book
That makes 2 tasks.
____________________________________________________________
____________________________________________________________
Nice, that's done:
  [T][X] borrow book
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] join sports club
2.[T][X] borrow book
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC27 - Reject a task containing the save-file separator

**Aim:** Kenbot separates fields in its save file with `|`, so a description
containing that character could not be read back correctly. Check that such a
task is refused when it is typed, rather than being accepted and then coming
back incomplete after a restart.

**Input:**

```text
todo read | book
list
bye
```

**Expected output:**

```text
____________________________________________________________
Can't use '|' in a task, that's what splits the fields in my save file.
____________________________________________________________
____________________________________________________________
Nothing on the list yet.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC28 - Reject a deadline whose date is not a real date

**Aim:** Dates are now stored as real dates rather than as text, so anything
that is not a date has to be refused when it is typed. Check that the refusal
names the offending text, shows the accepted format, and does not create a task.

**Input:**

```text
deadline return book /by Sunday
list
bye
```

**Expected output:**

```text
____________________________________________________________
'Sunday' isn't a date I can read. Write it as yyyy-mm-dd, like: 2019-10-15 1800
____________________________________________________________
____________________________________________________________
Nothing on the list yet.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC29 - Find tasks by a keyword in the description

**Aim:** Check that `find` shows every task whose description contains the
keyword, whatever its type, and numbers the matches from 1.

**Input:**

```text
todo read book
deadline return book /by 2019-06-06
todo join sports club
find book
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Got it, that's on the list:
  [D][ ] return book (by: Jun 06 2019)
That makes 2 tasks.
____________________________________________________________
____________________________________________________________
Got it, that's on the list:
  [T][ ] join sports club
That makes 3 tasks.
____________________________________________________________
____________________________________________________________
Found these:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC30 - Find with a keyword nothing matches

**Aim:** A search with no matches should say so plainly, rather than printing
the "matching tasks" heading with nothing under it.

**Input:**

```text
todo read book
find xyz
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Nothing matches 'xyz'.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC31 - Reject find with no keyword

**Aim:** `find` on its own has nothing to search for, so it should ask for a
keyword and show the expected form.

**Input:**

```text
find
bye
```

**Expected output:**

```text
____________________________________________________________
What am I looking for? Like: find book
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC32 - Add a task with tags

**Aim:** Check that trailing `#` words become tags rather than part of the
description, that several can be given at once, and that they are shown after
the description in the order they were typed.

**Input:**

```text
todo read book #fun #cs2103
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book #fun #cs2103
That makes 1 task.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC33 - Keep a hash in the middle of a description

**Aim:** Only a run of `#` words at the *end* of the line is tags. A `#` word
with ordinary words after it is part of what the user is describing, so it must
survive into the description untouched.

**Input:**

```text
todo read #1 book
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read #1 book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC34 - Tag a deadline after its date

**Aim:** Tags are taken off before `/by` is looked for, so a tag written after
the date must not be swallowed into the date.

**Input:**

```text
deadline return book /by 2019-10-15 #urgent
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [D][ ] return book (by: Oct 15 2019) #urgent
That makes 1 task.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC35 - Tag and untag an existing task

**Aim:** Check that `tag` adds to a task already in the list, that a leading
`#` is optional, and that `untag` takes one back off again.

**Input:**

```text
todo read book
tag 1 #fun
untag 1 fun
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Tagged:
  [T][ ] read book #fun
____________________________________________________________
____________________________________________________________
Untagged:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC36 - Find by tag

**Aim:** A keyword starting with `#` searches tags instead of descriptions, and
part of a tag counts, the same way part of a description does.

**Input:**

```text
todo read book #cs2103
todo buy milk
find #cs
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book #cs2103
That makes 1 task.
____________________________________________________________
____________________________________________________________
Got it, that's on the list:
  [T][ ] buy milk
That makes 2 tasks.
____________________________________________________________
____________________________________________________________
Found these:
1.[T][ ] read book #cs2103
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC37 - A plain keyword does not match a tag

**Aim:** The `#` is what chooses which field is searched. Without it, a word
that happens to be a tag must not match, or the two kinds of search would blur
into one.

**Input:**

```text
todo read book #fun
find fun
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book #fun
That makes 1 task.
____________________________________________________________
____________________________________________________________
Nothing matches 'fun'.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC38 - Reject a tag with no name

**Aim:** A bare `#` names nothing, so it should be reported rather than stored
as a tag with an empty name.

**Input:**

```text
todo read book
tag 1 #
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Need a name after the '#', like: #fun
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC39 - Reject tag with no tag given

**Aim:** `tag 1` says which task but not what to tag it with, so it should ask
and show the expected form.

**Input:**

```text
todo read book
tag 1
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Which task to tag, and with what? Like: tag 2 fun
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC40 - Reject untag for a tag the task does not have

**Aim:** Removing a tag that is not there changes nothing, and a command that
appears to work while doing nothing is worse than one that explains itself.

**Input:**

```text
todo read book
untag 1 fun
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
It doesn't have those.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## Not yet covered

Behaviour that this plan cannot check, or does not check yet:

* **Tasks surviving a restart.** Saving and loading are checked by hand, by
  running the program twice in the same folder and reading `data/Kenbot.txt`
  between the runs. A test case here is a single run of the program, and
  persistence is about what a *second* run sees, so this plan cannot cover it.
* **A corrupted save file.** Lines that cannot be understood are now skipped
  individually, the rest of the file is still loaded, and the count is
  reported. This cannot be tested here, because a test case starts from an
  empty folder and so has no save file to corrupt; it is checked by hand
  instead, by writing a broken file and running the program.

Loading is otherwise in place: a run that finds no save file starts with an
empty list, which is what every test case in this plan relies on.

## TC41 - Reject an event that ends before it starts

**Aim:** Two dates can be given the wrong way round. Stored that way the task
would read back as ending before it began, and no later command could make
sense of it, so it is refused at the point it is typed.

**Input:**

```text
event backwards /from 2019-10-20 /to 2019-10-15
bye
```

**Expected output:**

```text
____________________________________________________________
An event can't end before it starts. You gave Oct 20 2019 to Oct 15 2019.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC42 - Reject an event whose end time is earlier on the same day

**Aim:** When both ends fall on the same day the date cannot decide the order,
so the times are compared instead.

**Input:**

```text
event backwards /from 2019-10-15 1600 /to 2019-10-15 1400
bye
```

**Expected output:**

```text
____________________________________________________________
An event can't end before it starts. You gave Oct 15 2019 1600 to Oct 15 2019 1400.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC43 - Allow a whole-day event

**Aim:** The other side of TC42. Same day and no times given is not an error: it
is an event that lasts the day, and refusing it would be worse than allowing it.

**Input:**

```text
event conference /from 2019-10-15 /to 2019-10-15
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [E][ ] conference (from: Oct 15 2019 to: Oct 15 2019)
That makes 1 task.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC44 - Reject a repeated /by

**Aim:** A second `/by` used to be absorbed into the time of day, producing a
deadline that read back as nonsense. Giving a marker twice is now refused.

**Input:**

```text
deadline submit /by 2019-10-15 /by 2019-10-20
bye
```

**Expected output:**

```text
____________________________________________________________
You've given /by more than once. I only know what to do with one.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC45 - Reject an exact duplicate task

**Aim:** Adding the same thing twice is almost always a double keypress, and two
identical lines cannot be told apart afterwards when marking or deleting one.

**Input:**

```text
todo read book
todo read book
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
That's already on the list:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## TC46 - Allow a task that differs only in case

**Aim:** The other side of TC45. Case is treated as meaningful so that a second
task the user actually wanted is never silently refused.

**Input:**

```text
todo read book
todo Read book
bye
```

**Expected output:**

```text
____________________________________________________________
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
____________________________________________________________
____________________________________________________________
Got it, that's on the list:
  [T][ ] Read book
That makes 2 tasks.
____________________________________________________________
____________________________________________________________
Peace! See you soon!
____________________________________________________________
```

## Deliberately not treated as errors

These were considered and left alone, so that a future reader does not add a
test case for behaviour that was chosen on purpose:

* **Extra words after a command that takes none**, such as `list now`. The extra
  words are ignored and the list is shown. Rejecting them would be stricter
  without helping the user.
* **Capitalisation**, such as `MARK 2` or `Todo read book`. Commands are
  lower-case only, so these are reported as unknown commands.
* **A blank line.** It is ignored and the program carries on waiting.
* **Marking a task that is already done.** Harmless, so it is allowed rather
  than reported.
* **A whole-day event**, such as `event x /from 2019-10-15 /to 2019-10-15`.
  Same day with no times given cannot be ordered, and a day-long event is a
  real thing, so it is allowed. See TC43.
* **A second task differing only in case**, such as `todo read book` followed by
  `todo Read book`. Exact duplicates are refused, but case is treated as
  meaningful so a deliberate second task is never lost. See TC46.
* **Times that cannot be compared**, such as `/from 2019-10-15 morning /to
  2019-10-15 evening`. The time of day is kept as the user typed it, so these
  cannot be ordered; the event is allowed rather than refused on a guess.
* **Searching for a tag in a different case**, such as `find #FUN` matching
  `#fun`. Two tags differing only in case are genuinely different tags, but
  searching ignores case so that `find` behaves the same way whether the
  keyword is a tag or a description.
