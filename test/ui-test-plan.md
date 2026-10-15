# UI Test Plan

Run this plan with `& .\.codex\skills\test-ui\scripts\run-ui-tests.ps1` using Java 25. Each session runs in a fresh program session. Its expected output must include every displayed line, including the banner and separators. See [`.codex/skills/test-ui/SKILL.md`](../.codex/skills/test-ui/SKILL.md) for the multi-session and save-file fixture syntax used by the save/load test cases below.

## Test case: add and display task types

**Aim:** Verify that `todo`, `deadline`, and `event` create the correct task subtypes, that `deadline`/`event` dates entered as `yyyy-mm-dd` are stored and displayed as `MMM dd yyyy`, and that `list` displays their type, completion state, and details.

### Inputs

```text
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-15 /to 2019-10-16
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
You have something to do...
[T][ ] borrow book
You have 1 tasks.
____________________________________________________________
____________________________________________________________
Do not miss the deadline.
[D][ ] return book (by: Oct 15 2019)
You have 2 tasks.
____________________________________________________________
____________________________________________________________
Am I invited?
[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
You have 3 tasks.
____________________________________________________________
____________________________________________________________
Behold, all that awaits you:
1.[T][ ] borrow book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

## Test case: find displays only tasks matching a keyword

**Aim:** Verify that `find` matches the keyword against task descriptions case-insensitively, displays no lines when nothing matches, and reports an error (without ending the session) when no keyword is given.

### Inputs

```text
todo read book
todo go jogging
deadline return book /by 2019-10-15
mark 1
find book
find BOOK
find jog
find xyz
find
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
You have something to do...
[T][ ] read book
You have 1 tasks.
____________________________________________________________
____________________________________________________________
You have something to do...
[T][ ] go jogging
You have 2 tasks.
____________________________________________________________
____________________________________________________________
Do not miss the deadline.
[D][ ] return book (by: Oct 15 2019)
You have 3 tasks.
____________________________________________________________
____________________________________________________________
You have satisfied the great Alzara.
[T][X] read book
____________________________________________________________
____________________________________________________________
The great Alzara finds these:
1.[T][X] read book
2.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
The great Alzara finds these:
1.[T][X] read book
2.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
The great Alzara finds these:
1.[T][ ] go jogging
____________________________________________________________
____________________________________________________________
The great Alzara finds these:
____________________________________________________________
____________________________________________________________
I cannot find nothing.
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

## Test case: view displays tasks scheduled on a given date

**Aim:** Verify that `view` shows every `deadline` due on the queried date and every `event` spanning it (inclusive of both its start and end date), sorted chronologically by each task's own date rather than list order, excludes non-matching tasks (including `todo`, which never has a date), shows a distinct message when nothing matches, and reports the missing-date/invalid-date errors without ending the session.

### Inputs

```text
todo read book
deadline return book /by 2019-10-15
event trip /from 2019-10-14 /to 2019-10-16
event conference /from 2019-11-01 /to 2019-11-03
view 2019-10-15
view 2019-10-14
view 2019-10-16
view 2019-10-20
view
view Sunday
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
You have something to do...
[T][ ] read book
You have 1 tasks.
____________________________________________________________
____________________________________________________________
Do not miss the deadline.
[D][ ] return book (by: Oct 15 2019)
You have 2 tasks.
____________________________________________________________
____________________________________________________________
Am I invited?
[E][ ] trip (from: Oct 14 2019 to: Oct 16 2019)
You have 3 tasks.
____________________________________________________________
____________________________________________________________
Am I invited?
[E][ ] conference (from: Nov 01 2019 to: Nov 03 2019)
You have 4 tasks.
____________________________________________________________
____________________________________________________________
I see all on Oct 15 2019:
1.[E][ ] trip (from: Oct 14 2019 to: Oct 16 2019)
2.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
I see all on Oct 14 2019:
1.[E][ ] trip (from: Oct 14 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
I see all on Oct 16 2019:
1.[E][ ] trip (from: Oct 14 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
I see nothing on Oct 20 2019.
____________________________________________________________
____________________________________________________________
How far in time do you wish to see?
____________________________________________________________
____________________________________________________________
I cannot read that date. Use yyyy-mm-dd.
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

## Test case: unrecognised commands are rejected without being added as tasks

**Aim:** Verify that input matching no known command is reported as an error and never added to the task list, instead of being silently stored as a raw task.

### Inputs

```text
/list
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
Speak sense.
____________________________________________________________
____________________________________________________________
Behold, all that awaits you:
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

## Test case: invalid command formats

**Aim:** Verify that invalid task numbers and missing deadline or event markers display the editable placeholder error messages without ending the session.

### Inputs

```text
mark
unmark abc
mark 1
deadline return book
event project meeting /from Mon 2pm
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
Which task do you refer to...
____________________________________________________________
____________________________________________________________
Nonsense.
____________________________________________________________
____________________________________________________________
Do you know how to count?
____________________________________________________________
____________________________________________________________
All things must come to pass. Add a deadline.
____________________________________________________________
____________________________________________________________
All things must have a start and an end...
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

## Test case: invalid deadline and event dates are rejected without adding tasks

**Aim:** Verify that a `deadline` or `event` date that is not `yyyy-mm-dd` (for either the start or the end of an event) is reported with the shared date error and never adds a task.

### Inputs

```text
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
event trip /from 2019-10-15 /to Tuesday
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
I cannot read that date. Use yyyy-mm-dd.
____________________________________________________________
____________________________________________________________
I cannot read that date. Use yyyy-mm-dd.
____________________________________________________________
____________________________________________________________
I cannot read that date. Use yyyy-mm-dd.
____________________________________________________________
____________________________________________________________
Behold, all that awaits you:
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

## Test case: task commands without descriptions preserve an empty list

**Aim:** Verify that bare `todo`, `deadline`, and `event` commands use the missing-task error, while commands with a task but missing details use their specific errors and never add incomplete tasks.

### Inputs

```text
todo
deadline
event
deadline submit report
event project meeting
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
You want to do nothing?
____________________________________________________________
____________________________________________________________
You want to do nothing?
____________________________________________________________
____________________________________________________________
You want to do nothing?
____________________________________________________________
____________________________________________________________
All things must come to pass. Add a deadline.
____________________________________________________________
____________________________________________________________
All things must have a start and an end...
____________________________________________________________
____________________________________________________________
Behold, all that awaits you:
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

## Test case: invalid commands preserve existing task state

**Aim:** Verify that errors between valid commands do not add tasks or alter the completion state of existing tasks.

### Inputs

```text
todo read book
deadline return book
deadline return book /by 2019-10-15
mark 2
unmark abc
mark 3
event project meeting /from Mon 2pm
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
You have something to do...
[T][ ] read book
You have 1 tasks.
____________________________________________________________
____________________________________________________________
All things must come to pass. Add a deadline.
____________________________________________________________
____________________________________________________________
Do not miss the deadline.
[D][ ] return book (by: Oct 15 2019)
You have 2 tasks.
____________________________________________________________
____________________________________________________________
You have satisfied the great Alzara.
[D][X] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Nonsense.
____________________________________________________________
____________________________________________________________
Do you know how to count?
____________________________________________________________
____________________________________________________________
All things must have a start and an end...
____________________________________________________________
____________________________________________________________
Behold, all that awaits you:
1.[T][ ] read book
2.[D][X] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

## Test case: invalid deletes preserve tasks before a valid deletion

**Aim:** Verify that malformed and out-of-range `delete` commands do not change tasks, while a valid deletion removes the selected marked task and reports the remaining count.

### Inputs

```text
todo read book
todo borrow book
mark 2
delete
delete abc
delete 3
delete 2
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
You have something to do...
[T][ ] read book
You have 1 tasks.
____________________________________________________________
____________________________________________________________
You have something to do...
[T][ ] borrow book
You have 2 tasks.
____________________________________________________________
____________________________________________________________
You have satisfied the great Alzara.
[T][X] borrow book
____________________________________________________________
____________________________________________________________
Which task do you refer to...
____________________________________________________________
____________________________________________________________
Nonsense.
____________________________________________________________
____________________________________________________________
Do you know how to count?
____________________________________________________________
____________________________________________________________
I have removed the task [T][X] borrow book
1 tasks remain.
____________________________________________________________
____________________________________________________________
Behold, all that awaits you:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

## Test case: starting with no save file begins with an empty task list

**Aim:** Verify that a first run with no `data/alzara.txt` starts silently with an empty list rather than printing an error.

### Inputs

```text
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
Behold, all that awaits you:
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

## Test case: save file persists tasks and their completion state across a restart

**Aim:** Verify that tasks added and marked in one session are written to `data/alzara.txt` and are reloaded correctly, in order, with their completion state intact, when the program is restarted.

### Session 1 inputs

```text
todo borrow book
deadline return book /by 2019-10-15
mark 1
bye
```

### Session 1 expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
You have something to do...
[T][ ] borrow book
You have 1 tasks.
____________________________________________________________
____________________________________________________________
Do not miss the deadline.
[D][ ] return book (by: Oct 15 2019)
You have 2 tasks.
____________________________________________________________
____________________________________________________________
You have satisfied the great Alzara.
[T][X] borrow book
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

### Session 2 inputs

```text
list
bye
```

### Session 2 expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
Behold, all that awaits you:
1.[T][X] borrow book
2.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

### Save file after

```text
T | Y | borrow book
D | N | return book | 2019-10-15
```

## Test case: marking and deleting tasks update the save file immediately

**Aim:** Verify that `mark` and `delete` rewrite `data/alzara.txt` right away, so the file on disk always matches the in-memory list.

### Inputs

```text
todo read book
todo borrow book
todo walk dog
mark 2
delete 1
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
You have something to do...
[T][ ] read book
You have 1 tasks.
____________________________________________________________
____________________________________________________________
You have something to do...
[T][ ] borrow book
You have 2 tasks.
____________________________________________________________
____________________________________________________________
You have something to do...
[T][ ] walk dog
You have 3 tasks.
____________________________________________________________
____________________________________________________________
You have satisfied the great Alzara.
[T][X] borrow book
____________________________________________________________
____________________________________________________________
I have removed the task [T][ ] read book
2 tasks remain.
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

### Save file after

```text
T | Y | borrow book
T | N | walk dog
```

## Test case: event commands reject a start date after the end date

**Aim:** Verify that an `event` whose `/from` date is chronologically after its `/to` date is rejected without adding a task, while an event where both dates are the same (a single-day event) is still accepted.

### Inputs

```text
event trip /from 2019-10-20 /to 2019-10-15
event conference /from 2019-10-15 /to 2019-10-15
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
You cannot time travel.
____________________________________________________________
____________________________________________________________
Am I invited?
[E][ ] conference (from: Oct 15 2019 to: Oct 15 2019)
You have 1 tasks.
____________________________________________________________
____________________________________________________________
Behold, all that awaits you:
1.[E][ ] conference (from: Oct 15 2019 to: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

## Test case: stricter command parsing rejects case variants, extra arguments, and repeated markers

**Aim:** Verify that command words are recognised case-insensitively, that `mark`/`unmark`/`delete` reject a trailing extra argument instead of silently ignoring it, and that a repeated `/by`, `/from`, or `/to` marker is rejected instead of being misparsed as part of a date.

### Inputs

```text
TODO read book
mark 1 2
deadline return book /by 2019-10-15 /by 2019-10-16
event trip /from 2019-10-15 /from 2019-10-16 /to 2019-10-17
event trip /from 2019-10-15 /to 2019-10-16 /to 2019-10-17
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
You have something to do...
[T][ ] read book
You have 1 tasks.
____________________________________________________________
____________________________________________________________
The great Alzara attends to one matter at a time.
____________________________________________________________
____________________________________________________________
Do not repeat yourself to the great Alzara.
____________________________________________________________
____________________________________________________________
Do not repeat yourself to the great Alzara.
____________________________________________________________
____________________________________________________________
Do not repeat yourself to the great Alzara.
____________________________________________________________
____________________________________________________________
Behold, all that awaits you:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

## Test case: duplicate tasks and forbidden characters are rejected without being added

**Aim:** Verify that adding a task with the same details (description, and dates for `deadline`/`event`) as one already in the list is rejected without adding a second copy - matching case-insensitively for the description - and that a description containing the `|` character (the save file's field separator) is rejected instead of silently risking save-file corruption.

### Inputs

```text
todo read book
todo read book
todo READ BOOK
deadline return book /by 2019-10-15
deadline return book /by 2019-10-15
deadline return book /by 2019-10-16
todo submit | report
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
____________________________________________________________
You have something to do...
[T][ ] read book
You have 1 tasks.
____________________________________________________________
____________________________________________________________
How forgetful... This task has already been recorded.
____________________________________________________________
____________________________________________________________
How forgetful... This task has already been recorded.
____________________________________________________________
____________________________________________________________
Do not miss the deadline.
[D][ ] return book (by: Oct 15 2019)
You have 2 tasks.
____________________________________________________________
____________________________________________________________
How forgetful... This task has already been recorded.
____________________________________________________________
____________________________________________________________
Do not miss the deadline.
[D][ ] return book (by: Oct 16 2019)
You have 3 tasks.
____________________________________________________________
____________________________________________________________
There is no place for '|'.
____________________________________________________________
____________________________________________________________
Behold, all that awaits you:
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2019)
3.[D][ ] return book (by: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

## Test case: corrupted save file entries are skipped without discarding valid tasks

**Aim:** Verify that each unreadable line in `data/alzara.txt` is reported with its line number and reason — including a deadline or event date that has the right number of fields but does not parse as `yyyy-mm-dd`, and an event whose start date parses fine but is after its end date — skipped, and left untouched on disk, while valid lines around it still load.

### Save file before

```text
T | N | read book
X | N | mystery task
T | Z | broken flag
D | N | return book
E | N | meeting | Mon 2pm
T | N
D | N | old deadline | Sunday
E | N | old event | Mon 2pm | 4pm
E | N | backwards trip | 2019-10-20 | 2019-10-15

T | Y | walk dog
```

### Inputs

```text
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _     ______    _    ____       _    
   / \  | |   |__  /   / \  |  _ \     / \   
  / _ \ | |     / /   / _ \ | |_) |   / _ \  
 / ___ \| |___ / /_  / ___ \|  _ <   / ___ \ 
/_/   \_\_____/____|/_/   \_\_| \_\ /_/   \_\
And as it was foretold,
You find yourself face to face with the great Alzara.
What can I do for you?
____________________________________________________________
A flawed memory on line 2 was discarded: unrecognised task type 'X'
A flawed memory on line 3 was discarded: invalid done flag
A flawed memory on line 4 was discarded: missing deadline field
A flawed memory on line 5 was discarded: missing event start/end field
A flawed memory on line 6 was discarded: not enough fields
A flawed memory on line 7 was discarded: invalid deadline date
A flawed memory on line 8 was discarded: invalid event date
A flawed memory on line 9 was discarded: event start date after end date
____________________________________________________________
____________________________________________________________
Behold, all that awaits you:
1.[T][ ] read book
2.[T][X] walk dog
____________________________________________________________
____________________________________________________________
Our audience has ended. Until we meet again.
____________________________________________________________
```

### Save file after

```text
T | N | read book
X | N | mystery task
T | Z | broken flag
D | N | return book
E | N | meeting | Mon 2pm
T | N
D | N | old deadline | Sunday
E | N | old event | Mon 2pm | 4pm
E | N | backwards trip | 2019-10-20 | 2019-10-15

T | Y | walk dog
```
