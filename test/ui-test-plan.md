# UI Test Plan

Scripted console sessions for the `test-ui` skill. Each test case below is
run as its own fresh session: every `input` line is fed to the program in
order, and the text it prints for that command (the content between the two
divider lines) is checked against the matching `expected` block.

The very first thing the program prints -- the startup banner and greeting
-- is shown in the console transcript but is not itself checked, since it
isn't a response to any command.

The program persists tasks in `data/samantha.txt`, relative to its working
directory. Run each test case with an isolated working directory (or remove
that directory's `data/samantha.txt` before starting it) so saved tasks from
one case do not affect another case.

## Format for new test cases

```
## Test <n>: <short name>

**Aim:** <what behaviour this checks, and why it matters>

\`\`\`input
<one command>
\`\`\`
\`\`\`expected
<exactly what should appear between the divider lines for that command>
\`\`\`
```

Repeat the `input`/`expected` pair for each command in the session, in
order. Every test case should end with a `bye` step so the process
terminates cleanly instead of crashing on end-of-input.

## Test 1: Greet and exit

**Aim:** The program greets the user automatically on startup (checked via
the transcript, not asserted here) and prints a farewell message then exits
cleanly when the very first command is `bye`.

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 11: Filter tasks by date

**Aim:** `list` accepts an optional date using either `/` or `-`, showing
deadlines and events occurring on that date while excluding unrelated tasks.

```input
todo buy groceries
```
```expected
I'll remember that:
  [T][ ] buy groceries
Now you have 1 tasks in the list.
```

```input
deadline return book /by 2/12/2019
```
```expected
I'll remember that:
  [D][ ] return book (by: Dec 2 2019)
Now you have 2 tasks in the list.
```

```input
event project meeting /from 2-12-2019 1400 /to 2-12-2019 1600
```
```expected
I'll remember that:
  [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Now you have 3 tasks in the list.
```

```input
list 2/12/2019
```
```expected
Here are the tasks in your list:
2. [D][ ] return book (by: Dec 2 2019)
3. [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
```

```input
list 3-12-2019
```
```expected
Here are the tasks in your list:
```

```input
list 31/02/2019
```
```expected
The date and time must use d/M/yyyy or d-M-yyyy, optionally followed by HHmm.
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 2: Add a todo and list it

**Aim:** Adding a todo confirms the task with its `[T]` marker and an
unchecked box, updates the task count, and the same line then shows up
under `list`.

```input
todo borrow book
```
```expected
I'll remember that:
  [T][ ] borrow book
Now you have 1 tasks in the list.
```

```input
list
```
```expected
Here are the tasks in your list:
1. [T][ ] borrow book
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 12: Find tasks by description keyword

**Aim:** `find <keyword>` searches task descriptions without regard to case,
shows matching todo and deadline tasks in their original order and numbering,
and reports no task lines when there are no matches.

```input
todo read book
```
```expected
I'll remember that:
  [T][ ] read book
Now you have 1 tasks in the list.
```

```input
deadline return book /by 6-6-2019
```
```expected
I'll remember that:
  [D][ ] return book (by: Jun 6 2019)
Now you have 2 tasks in the list.
```

```input
todo join club
```
```expected
I'll remember that:
  [T][ ] join club
Now you have 3 tasks in the list.
```

```input
find BOOK
```
```expected
Here are the matching tasks in your list:
1. [T][ ] read book
2. [D][ ] return book (by: Jun 6 2019)

Here are the matching notes in your list:
```

```input
find holiday
```
```expected
Here are the matching tasks in your list:

Here are the matching notes in your list:
```

```input
find
```
```expected
You forgot to mention the keyword to search for.
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 10: Parse numeric deadline dates with optional times

**Aim:** A numeric deadline is stored as a date rather than raw text, accepts
both supported date separators, and can omit its time for an all-day deadline.
Malformed numeric dates are rejected without adding a task.

```input
deadline return book /by 2/12/2019 1800
```
```expected
I'll remember that:
  [D][ ] return book (by: Dec 2 2019, 6:00 PM)
Now you have 1 tasks in the list.
```

```input
deadline submit report /by 5-01-2020
```
```expected
I'll remember that:
  [D][ ] submit report (by: Jan 5 2020)
Now you have 2 tasks in the list.
```

```input
deadline invalid date /by 31/02/2019
```
```expected
The date and time must use d/M/yyyy or d-M-yyyy, optionally followed by HHmm.
```

```input
deadline invalid time /by 2-12-2019 2560
```
```expected
The date and time must use d/M/yyyy or d-M-yyyy, optionally followed by HHmm.
```

```input
deadline natural language /by Sunday
```
```expected
The date and time must use d/M/yyyy or d-M-yyyy, optionally followed by HHmm.
```

```input
list
```
```expected
Here are the tasks in your list:
1. [D][ ] return book (by: Dec 2 2019, 6:00 PM)
2. [D][ ] submit report (by: Jan 5 2020)
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 3: Add a deadline and an event, then list

**Aim:** A deadline's `/by` date and an event's `/from`/`/to` range are
parsed out of the command and shown correctly in both the add confirmation
and `list`, and the task count accumulates across different task types.

```input
deadline return book /by 2/12/2019
```
```expected
I'll remember that:
  [D][ ] return book (by: Dec 2 2019)
Now you have 1 tasks in the list.
```

```input
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
```
```expected
I'll remember that:
  [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Now you have 2 tasks in the list.
```

```input
list
```
```expected
Here are the tasks in your list:
1. [D][ ] return book (by: Dec 2 2019)
2. [E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 4: Mark and unmark the middle task of several

**Aim:** `mark <n>` and `unmark <n>` flip the checkbox for task `n`
specifically -- not just for the first or last task. The list deliberately
holds three tasks and the test targets the middle one, so an off-by-one in
the index arithmetic, or a bug that always touches `tasks.get(0)`, shows up
as the wrong line changing. The surrounding tasks must stay untouched, which
is why `list` is checked in full after each flip.

```input
todo read book
```
```expected
I'll remember that:
  [T][ ] read book
Now you have 1 tasks in the list.
```

```input
todo return book
```
```expected
I'll remember that:
  [T][ ] return book
Now you have 2 tasks in the list.
```

```input
todo join club
```
```expected
I'll remember that:
  [T][ ] join club
Now you have 3 tasks in the list.
```

```input
mark 2
```
```expected
That's done:
  [T][X] return book
```

```input
list
```
```expected
Here are the tasks in your list:
1. [T][ ] read book
2. [T][X] return book
3. [T][ ] join club
```

```input
unmark 2
```
```expected
I've put that back on the list:
  [T][ ] return book
```

```input
list
```
```expected
Here are the tasks in your list:
1. [T][ ] read book
2. [T][ ] return book
3. [T][ ] join club
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 5: Reject an empty todo and an unrecognized command

**Aim:** The two error cases required by the Level-5 spec -- an empty todo
description, and a command word the program doesn't recognize -- are
reported with an error message instead of crashing or silently doing
nothing, and neither one adds anything to the task list.

```input
todo
```
```expected
The description of a todo cannot be empty.
```

```input
blah
```
```expected
I don't recognize that. Type help if you want the guide.
```

```input
list
```
```expected
Here are the tasks in your list:
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 6: Reject malformed deadlines and events, then confirm a valid one still works

**Aim:** A deadline missing `/by`, a deadline with an empty description,
an event missing `/to`, an event whose end is not after its start, and an
event with a repeated `/from` are all rejected with a specific error message
and don't add anything to the list -- and, importantly, none of them corrupt
internal state: a valid `deadline` right after still gets added as task #1.

```input
deadline return book
```
```expected
You forgot to include /by for this deadline.
```

```input
deadline /by 2/12/2019
```
```expected
The description of a deadline cannot be empty.
```

```input
event meeting /from 2/12/2019 1400
```
```expected
You forgot to include /from and /to for this event.
```

```input
event meeting /from 2/12/2019 1600 /to 2/12/2019 1400
```
```expected
The event must end after it starts.
```

```input
event meeting /from 2/12/2019 1400 /to 2/12/2019 1600 /from 2/12/2019 1500
```
```expected
An event can only have one /from and one /to.
```

```input
deadline return book /by 2/12/2019
```
```expected
I'll remember that:
  [D][ ] return book (by: Dec 2 2019)
Now you have 1 tasks in the list.
```

```input
list
```
```expected
Here are the tasks in your list:
1. [D][ ] return book (by: Dec 2 2019)
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 7: Reject bad mark input, then confirm a valid mark still works

**Aim:** `mark` with no number, a non-numeric number, and a number outside
the task list's range are each rejected with a specific message instead of
crashing, and a valid `mark` afterward still flips the right task.

```input
todo read book
```
```expected
I'll remember that:
  [T][ ] read book
Now you have 1 tasks in the list.
```

```input
mark
```
```expected
You forgot to mention the id of the task
```

```input
mark abc
```
```expected
You need to enter a number for the task id.
```

```input
mark 5
```
```expected
You entered a task number that does not exist.
```

```input
mark  1
```
```expected
That's done:
  [T][X] read book
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 8: Delete a task and confirm the rest renumber

**Aim:** `delete <n>` removes task `n`, reports it with the updated count,
and the tasks after it shift up to fill the gap. The final `mark 2` is the
real point of this test: after deleting task 2 of 3, the old task 3 must now
answer to index 2. A delete that removed the right task but left the list
indexed wrongly would pass every check except that last one.

```input
todo read book
```
```expected
I'll remember that:
  [T][ ] read book
Now you have 1 tasks in the list.
```

```input
deadline return book /by 6-6-2019
```
```expected
I'll remember that:
  [D][ ] return book (by: Jun 6 2019)
Now you have 2 tasks in the list.
```

```input
event project meeting /from 8-6-2019 1400 /to 8-6-2019 1600
```
```expected
I'll remember that:
  [E][ ] project meeting (from: Jun 8 2019, 2:00 PM to: Jun 8 2019, 4:00 PM)
Now you have 3 tasks in the list.
```

```input
delete 2
```
```expected
I've taken that off the list:
  [D][ ] return book (by: Jun 6 2019)
Now you have 2 tasks in the list.
```

```input
list
```
```expected
Here are the tasks in your list:
1. [T][ ] read book
2. [E][ ] project meeting (from: Jun 8 2019, 2:00 PM to: Jun 8 2019, 4:00 PM)
```

```input
mark 2
```
```expected
That's done:
  [E][X] project meeting (from: Jun 8 2019, 2:00 PM to: Jun 8 2019, 4:00 PM)
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 9: Reject bad delete input, then confirm the list is intact

**Aim:** `delete` with a missing, non-numeric, out-of-range, or over-supplied
argument is rejected instead of crashing or deleting the wrong thing. The
`delete 1 2` step covers the "too many parameters" branch, which no other
test reaches. The closing `list` proves none of the four rejected commands
removed anything.

```input
todo read book
```
```expected
I'll remember that:
  [T][ ] read book
Now you have 1 tasks in the list.
```

```input
delete
```
```expected
You forgot to mention the id of the task
```

```input
delete abc
```
```expected
You need to enter a number for the task id.
```

```input
delete 5
```
```expected
You entered a task number that does not exist.
```

```input
delete 1 2
```
```expected
You entered too many parameters for this operation
```

```input
list
```
```expected
Here are the tasks in your list:
1. [T][ ] read book
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 13: Display command help

**Aim:** `help` lists each supported command and its required syntax without
changing the task list, so a new user can discover how to use Samantha.

```input
help
```
```expected
Here's how to use Samantha:
  help
    Show this guide.
  todo DESCRIPTION
    Add a task.
  deadline DESCRIPTION /by DATE [TIME]
    Add a task due on DATE, optionally at TIME.
  event DESCRIPTION /from DATE TIME /to DATE TIME
    Add an event.
  list [DATE]
    List every task, or only tasks on DATE.
  find KEYWORD
    Find tasks and notes whose text contains KEYWORD.
  mark N | unmark N | delete N
    Update or remove task number N.
  note TEXT
    Save a note.
  notes
    List your notes.
  edit-note N TEXT
    Edit note number N.
  delete-note N
    Remove note number N.
  undo
    Undo the most recent state-changing command.
  bye
    Close Samantha.

Use dates as d/M/yyyy or d-M-yyyy. Use times as HHmm.
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 14: Manage notes and search them

**Aim:** Notes can be added, listed, edited, deleted, persisted, and found
through the shared `find` command without sharing task numbering.

```input
note movie title: Inception
```
```expected
I'll keep this note:
  movie title: Inception
Now you have 1 notes in the list.
```

```input
note My waist size is 32 inches
```
```expected
I'll keep this note:
  My waist size is 32 inches
Now you have 2 notes in the list.
```

```input
notes
```
```expected
Here are your notes:
1. movie title: Inception
2. My waist size is 32 inches
```

```input
edit-note 1 movie title: Interstellar
```
```expected
I've updated this note:
  movie title: Interstellar
```

```input
find MOVIE
```
```expected
Here are the matching tasks in your list:

Here are the matching notes in your list:
1. movie title: Interstellar
```

```input
delete-note 2
```
```expected
I've taken that off the list:
  My waist size is 32 inches
Now you have 1 notes in the list.
```

```input
notes
```
```expected
Here are your notes:
1. movie title: Interstellar
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 15: Undo the most recent state-changing command

**Aim:** `undo` reverses the most recent state-changing command, persists the
restored state, rejects unexpected arguments, and reports clearly when there is
no command left to undo.

```input
todo read book
```
```expected
I'll remember that:
  [T][ ] read book
Now you have 1 tasks in the list.
```

```input
undo extra
```
```expected
You entered too many parameters for this operation
```

```input
undo
```
```expected
I've undone the last command.
```

```input
undo
```
```expected
There is nothing to undo.
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 16: Undo note changes

**Aim:** `undo` reverses note additions, edits, and deletions while preserving
the remaining note order.

```input
note first
```
```expected
I'll keep this note:
  first
Now you have 1 notes in the list.
```

```input
note second
```
```expected
I'll keep this note:
  second
Now you have 2 notes in the list.
```

```input
edit-note 1 updated
```
```expected
I've updated this note:
  updated
```

```input
undo
```
```expected
I've undone the last command.
```

```input
delete-note 1
```
```expected
I've taken that off the list:
  first
Now you have 1 notes in the list.
```

```input
undo
```
```expected
I've undone the last command.
```

```input
notes
```
```expected
Here are your notes:
1. first
2. second
```

```input
note third
```
```expected
I'll keep this note:
  third
Now you have 3 notes in the list.
```

```input
undo
```
```expected
I've undone the last command.
```

```input
notes
```
```expected
Here are your notes:
1. first
2. second
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 17: Reject an empty event description and undo timed tasks

**Aim:** An event missing its description is rejected without adding a task,
and undoing a deadline or event restores an empty task list so those
additions are reversible like todos.

```input
event /from 2/12/2019 1400 /to 2/12/2019 1600
```
```expected
The description of a event cannot be empty.
```

```input
deadline return book /by 2/12/2019
```
```expected
I'll remember that:
  [D][ ] return book (by: Dec 2 2019)
Now you have 1 tasks in the list.
```

```input
undo
```
```expected
I've undone the last command.
```

```input
event meeting /from 2/12/2019 1400 /to 2/12/2019 1600
```
```expected
I'll remember that:
  [E][ ] meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
Now you have 1 tasks in the list.
```

```input
undo
```
```expected
I've undone the last command.
```

```input
list
```
```expected
Here are the tasks in your list:
```

```input
bye
```
```expected
Bye. Let's talk next time!
```

## Test 18: Reject the save-file separator in task descriptions

**Aim:** A `|` in a task description would be split into extra fields when
the save file is next loaded, making the whole file look corrupted. Each task
type must reject it up front, and the list must be unchanged afterwards.

```input
todo a | b
```
```expected
The description of a todo cannot contain |.
```

```input
deadline report|draft /by 12/9/2026 1800
```
```expected
The description of a deadline cannot contain |.
```

```input
event sync | review /from 12/9/2026 1400 /to 12/9/2026 1600
```
```expected
The description of a event cannot contain |.
```

```input
list
```
```expected
Here are the tasks in your list:
```

```input
bye
```
```expected
Bye. Let's talk next time!
```
