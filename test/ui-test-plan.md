# UI Test Plan

This file is the source of truth for automated console UI test cases. Each
test case starts a new Yuno process, and its commands are entered in order.

## Test Case: Display help

### Aim

Verify that `help` displays every supported command with its expected format
and that additional arguments are rejected.

### Inputs

```text
help
help commands
bye
```

### Expected Output

```text
__________________________________________________
__   __ _   _ _   _  ___
\ \ / /| | | | \ | |/ _ \
 \ V / | | | |  \| | | | |
  | |  | |_| | |\  | |_| |
  |_|   \___/|_| \_|\___/

I'm Yuno.
Can we just get this over quickly?
__________________________________________________
You forgot the commands already? Fine. Pay attention this time.

Dates accept yyyy-MM-dd or yyyy-MM-dd HHmm.

TASKS
  todo <description>
    Adds a task without a date.

  deadline <description> /by <yyyy-MM-dd HHmm>
    Adds a task that must be completed by a particular time.

  event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>
    Adds an event with a start and end time.

MANAGE
  list
    Shows all your stored responsibilities.

  mark <task number>
    Marks a task as complete. Miracles do happen.

  unmark <task number>
    Marks a task as incomplete again. Of course.

  delete <task number>
    Removes a task.

  clear
    Removes every task.

SEARCH AND SORT
  find <text>
    Finds tasks containing the given text.

  find /date <yyyy-MM-dd>
    Finds tasks relevant to the given date.

  sort
    Sorts dated tasks from earliest to latest.

  sort /order <asc|desc>
    Sorts dated tasks in the chosen order.

OTHER
  help
    Shows this list, since apparently you need it.

  bye
    Exits Yuno. Finally.
__________________________________________________
It is just 'help'. How did you manage to complicate that?
__________________________________________________
Finally! Bye. I'm leaving!
__________________________________________________
```

## Test Case: Exit immediately

### Aim

Verify that Yuno displays its greeting and exits cleanly when the first command
is `bye`.

### Inputs

```text
bye
```

### Expected Output

```text
__________________________________________________
__   __ _   _ _   _  ___
\ \ / /| | | | \ | |/ _ \
 \ V / | | | |  \| | | | |
  | |  | |_| | |\  | |_| |
  |_|   \___/|_| \_|\___/

I'm Yuno.
Can we just get this over quickly?
__________________________________________________
Finally! Bye. I'm leaving!
__________________________________________________
```

## Test Case: Reject malformed commands

### Aim

Verify that Yuno reports common command errors, continues accepting commands,
and exits normally afterward.

### Inputs

```text
nonsense
todo
deadline submit report
event meeting /from Monday
event meeting /to Tuesday /from Monday
deadline submit report /by 2026-02-30 1200
event meeting /from 2026-08-31 1100 /to 2026-08-31 1000
event meeting /from 2026-08-31 1000 /to 2026-08-31 1000
mark abc
mark 0
list extra
bye extra
bye
```

### Expected Output

```text
__________________________________________________
__   __ _   _ _   _  ___
\ \ / /| | | | \ | |/ _ \
 \ V / | | | |  \| | | | |
  | |  | |_| | |\  | |_| |
  |_|   \___/|_| \_|\___/

I'm Yuno.
Can we just get this over quickly?
__________________________________________________
Did you look at what you typed? That's just a random command.
__________________________________________________
If you have no task, please don't bother me.
__________________________________________________
If you are not constrained by a date, use another task type.
__________________________________________________
If your task does not have a start and end time, save me some time and use another task type.
__________________________________________________
Any normal human would remember it as '/from' then '/to'. Check it before wasting my time.
__________________________________________________
Memorize the date format before you even type. It's either yyyy-MM-dd HHmm or yyyy-MM-dd.
__________________________________________________
An event must end after it starts. Check the dates first before even submitting.
__________________________________________________
An event must end after it starts. Check the dates first before even submitting.
__________________________________________________
Did you even give me an integer? Please don't waste my time!
__________________________________________________
Are you wasting my time? The integer you gave is out of bounds.
__________________________________________________
I only need 'list' to show your tasks. What am I supposed to do with the rest?
__________________________________________________
If you're leaving, just say 'bye'. I don't need the unnecessary details.
__________________________________________________
Finally! Bye. I'm leaving!
__________________________________________________
```

## Test Case: Manage tasks successfully

### Aim

Verify that valid task commands accept full date-times and date-only values,
with date-only values interpreted as midnight.

### Inputs

```text
todo read book
deadline submit report /by 2026-08-30 1800
event meeting /from 2026-08-31 0900 /to 2026-08-31 1030
deadline midnight submission /by 2026-09-01
event overnight event /from 2026-09-02 /to 2026-09-03
mark 1
unmark 1
list
bye
```

### Expected Output

```text
__________________________________________________
__   __ _   _ _   _  ___
\ \ / /| | | | \ | |/ _ \
 \ V / | | | |  \| | | | |
  | |  | |_| | |\  | |_| |
  |_|   \___/|_| \_|\___/

I'm Yuno.
Can we just get this over quickly?
__________________________________________________
Added:
[T][ ] read book
Just another task you won't finish.
__________________________________________________
Added:
[D][ ] submit report (by: Aug 30 2026, 06:00 PM)
Just another task you won't finish.
__________________________________________________
Added:
[E][ ] meeting (from: Aug 31 2026, 09:00 AM to: Aug 31 2026, 10:30 AM)
Just another task you won't finish.
__________________________________________________
Added:
[D][ ] midnight submission (by: Sep 01 2026, 12:00 AM)
Just another task you won't finish.
__________________________________________________
Added:
[E][ ] overnight event (from: Sep 02 2026, 12:00 AM to: Sep 03 2026, 12:00 AM)
Just another task you won't finish.
__________________________________________________
You actually completed a task? Bet it's the only task you'll ever complete.
[T][X] read book
__________________________________________________
Wow! So you lied about completing it? Typical behavior from you.
[T][ ] read book
__________________________________________________
Wow. Look at how slow you are at completing these tasks.
1. [T][ ] read book
2. [D][ ] submit report (by: Aug 30 2026, 06:00 PM)
3. [E][ ] meeting (from: Aug 31 2026, 09:00 AM to: Aug 31 2026, 10:30 AM)
4. [D][ ] midnight submission (by: Sep 01 2026, 12:00 AM)
5. [E][ ] overnight event (from: Sep 02 2026, 12:00 AM to: Sep 03 2026, 12:00 AM)
__________________________________________________
Finally! Bye. I'm leaving!
__________________________________________________
```

## Test Case: Delete tasks

### Aim

Verify that deleting tasks displays the removed task, updates the list, handles
the last task, and rejects invalid task numbers.

### Inputs

```text
todo first task
todo second task
delete 1
list
delete 1
list
delete 1
delete abc
bye
```

### Expected Output

```text
__________________________________________________
__   __ _   _ _   _  ___
\ \ / /| | | | \ | |/ _ \
 \ V / | | | |  \| | | | |
  | |  | |_| | |\  | |_| |
  |_|   \___/|_| \_|\___/

I'm Yuno.
Can we just get this over quickly?
__________________________________________________
Added:
[T][ ] first task
Just another task you won't finish.
__________________________________________________
Added:
[T][ ] second task
Just another task you won't finish.
__________________________________________________
Wow! Did you give up, or did you actually finish it?
[T][ ] first task
__________________________________________________
Wow. Look at how slow you are at completing these tasks.
1. [T][ ] second task
__________________________________________________
Wow! Did you give up, or did you actually finish it?
[T][ ] second task
__________________________________________________
Wow, not even a single task? You are so lazy.
__________________________________________________
What do you want me to delete, your brain? The integer you gave is out of bounds.
__________________________________________________
Did you even give me an integer? Please don't waste my time!
__________________________________________________
Finally! Bye. I'm leaving!
__________________________________________________
```

## Test Case: Load stored tasks containing delimiters

### Aim

Verify that Yuno loads every task type with its status intact and reconstructs
descriptions that contain the storage delimiter.

### Inputs

```text
list
bye
```

### Initial Data

```text
T | X | read | difficult book
D |   | submit | final report | Aug 30 2026, 06:00 PM
E |   | project | meeting | Aug 31 2026, 09:00 AM | Aug 31 2026, 10:30 AM
```

### Expected Output

```text
__________________________________________________
__   __ _   _ _   _  ___
\ \ / /| | | | \ | |/ _ \
 \ V / | | | |  \| | | | |
  | |  | |_| | |\  | |_| |
  |_|   \___/|_| \_|\___/

I'm Yuno.
Can we just get this over quickly?
__________________________________________________
Wow. Look at how slow you are at completing these tasks.
1. [T][X] read | difficult book
2. [D][ ] submit | final report (by: Aug 30 2026, 06:00 PM)
3. [E][ ] project | meeting (from: Aug 31 2026, 09:00 AM to: Aug 31 2026, 10:30 AM)
__________________________________________________
Finally! Bye. I'm leaving!
__________________________________________________
```

## Test Case: Reject malformed stored tasks

### Aim

Verify that Yuno reports a controlled storage error and exits when saved task
data contains an invalid completion status.

### Inputs

```text
bye
```

### Initial Data

```text
T | ? | corrupted task
```

### Expected Output

```text
__________________________________________________
__   __ _   _ _   _  ___
\ \ / /| | | | \ | |/ _ \
 \ V / | | | |  \| | | | |
  | |  | |_| | |\  | |_| |
  |_|   \___/|_| \_|\___/

I'm Yuno.
Can we just get this over quickly?
__________________________________________________
Why did you change the task file? I can't load line 1.
__________________________________________________
```

## Test Case: Reject truncated stored task

### Aim

Verify that Yuno reports a controlled storage error instead of crashing when a
saved task line is missing its status and description.

### Inputs

```text
bye
```

### Initial Data

```text
T
```

### Expected Output

```text
__________________________________________________
__   __ _   _ _   _  ___
\ \ / /| | | | \ | |/ _ \
 \ V / | | | |  \| | | | |
  | |  | |_| | |\  | |_| |
  |_|   \___/|_| \_|\___/

I'm Yuno.
Can we just get this over quickly?
__________________________________________________
Why did you change the task file? I can't load line 1.
__________________________________________________
```

## Test Case: Find tasks by date

### Aim

Verify that date searches include todos, deadlines due on or before the date,
and events spanning the date, while rejecting malformed dates.

### Inputs

```text
find /date 2026-08-30
find /date 2026-08-29
find /date 2026-08-31
find /date 2026-09-01
find /date 2026-02-30
find /date
bye
```

### Initial Data

```text
T |   | read book
D |   | submit report | Aug 30 2026, 06:00 PM
E |   | conference | Aug 29 2026, 09:00 AM | Aug 31 2026, 05:00 PM
```

### Expected Output

```text
__________________________________________________
__   __ _   _ _   _  ___
\ \ / /| | | | \ | |/ _ \
 \ V / | | | |  \| | | | |
  | |  | |_| | |\  | |_| |
  |_|   \___/|_| \_|\___/

I'm Yuno.
Can we just get this over quickly?
__________________________________________________
You are not even capable to finish all these in one go.
- [T][ ] read book
- [D][ ] submit report (by: Aug 30 2026, 06:00 PM)
- [E][ ] conference (from: Aug 29 2026, 09:00 AM to: Aug 31 2026, 05:00 PM)
__________________________________________________
You are not even capable to finish all these in one go.
- [T][ ] read book
- [E][ ] conference (from: Aug 29 2026, 09:00 AM to: Aug 31 2026, 05:00 PM)
__________________________________________________
You are not even capable to finish all these in one go.
- [T][ ] read book
- [D][ ] submit report (by: Aug 30 2026, 06:00 PM)
- [E][ ] conference (from: Aug 29 2026, 09:00 AM to: Aug 31 2026, 05:00 PM)
__________________________________________________
You are not even capable to finish all these in one go.
- [T][ ] read book
- [D][ ] submit report (by: Aug 30 2026, 06:00 PM)
__________________________________________________
Memorize the date format before you even type. It's supposed to be yyyy-MM-dd.
__________________________________________________
Memorize the date format before you even type. It's supposed to be yyyy-MM-dd.
__________________________________________________
Finally! Bye. I'm leaving!
__________________________________________________
```

## Test Case: Find tasks by name

### Aim

Verify that name searches return tasks whose descriptions contain the requested
substring in their original order and display a message when no tasks match.

### Inputs

```text
find project
find missing
find
bye
```

### Initial Data

```text
T |   | read project requirements
D |   | submit project report | Aug 30 2026, 06:00 PM
E |   | team meeting | Aug 31 2026, 09:00 AM | Aug 31 2026, 10:30 AM
```

### Expected Output

```text
__________________________________________________
__   __ _   _ _   _  ___
\ \ / /| | | | \ | |/ _ \
 \ V / | | | |  \| | | | |
  | |  | |_| | |\  | |_| |
  |_|   \___/|_| \_|\___/

I'm Yuno.
Can we just get this over quickly?
__________________________________________________
So many task sharing a word. Could you be repeating task to feel accomplished?
- [T][ ] read project requirements
- [D][ ] submit project report (by: Aug 30 2026, 06:00 PM)
__________________________________________________
You have nothing. You must be really happy since you are so lazy.
__________________________________________________
Give me something to find instead of wasting my time.
__________________________________________________
Finally! Bye. I'm leaving!
__________________________________________________
```

## Test Case: Clear all tasks

### Aim

Verify that clear removes every task, displays a confirmation, saves the empty
list, and rejects additional command arguments.

### Inputs

```text
clear
list
clear extra
bye
```

### Initial Data

```text
T |   | read book
D |   | submit report | Aug 30 2026, 06:00 PM
```

### Expected Output

```text
__________________________________________________
__   __ _   _ _   _  ___
\ \ / /| | | | \ | |/ _ \
 \ V / | | | |  \| | | | |
  | |  | |_| | |\  | |_| |
  |_|   \___/|_| \_|\___/

I'm Yuno.
Can we just get this over quickly?
__________________________________________________
Finally. Now that everything is gone, can I go now?
__________________________________________________
Wow, not even a single task? You are so lazy.
__________________________________________________
If you want everything gone, type only 'clear'. Don't make me guess what those extra words mean.
__________________________________________________
Finally! Bye. I'm leaving!
__________________________________________________
```

## Test Case: Sort tasks chronologically

### Aim

Verify that sorting defaults to ascending order, supports descending order,
uses event start times, persists the new numbering, and leaves todos last.

### Inputs

```text
sort
list
sort /order desc
list
bye
```

### Initial Data

```text
T |   | undated todo
D |   | late deadline | Sep 10 2026, 12:00 PM
E |   | early event | Sep 08 2026, 09:00 AM | Sep 08 2026, 10:00 AM
D |   | middle deadline | Sep 09 2026, 12:00 PM
```

### Expected Output

```text
__________________________________________________
__   __ _   _ _   _  ___
\ \ / /| | | | \ | |/ _ \
 \ V / | | | |  \| | | | |
  | |  | |_| | |\  | |_| |
  |_|   \___/|_| \_|\___/

I'm Yuno.
Can we just get this over quickly?
__________________________________________________
Wow. Look at how slow you are at completing these tasks.
1. [E][ ] early event (from: Sep 08 2026, 09:00 AM to: Sep 08 2026, 10:00 AM)
2. [D][ ] middle deadline (by: Sep 09 2026, 12:00 PM)
3. [D][ ] late deadline (by: Sep 10 2026, 12:00 PM)
4. [T][ ] undated todo
__________________________________________________
Wow. Look at how slow you are at completing these tasks.
1. [E][ ] early event (from: Sep 08 2026, 09:00 AM to: Sep 08 2026, 10:00 AM)
2. [D][ ] middle deadline (by: Sep 09 2026, 12:00 PM)
3. [D][ ] late deadline (by: Sep 10 2026, 12:00 PM)
4. [T][ ] undated todo
__________________________________________________
Wow. Look at how slow you are at completing these tasks.
1. [D][ ] late deadline (by: Sep 10 2026, 12:00 PM)
2. [D][ ] middle deadline (by: Sep 09 2026, 12:00 PM)
3. [E][ ] early event (from: Sep 08 2026, 09:00 AM to: Sep 08 2026, 10:00 AM)
4. [T][ ] undated todo
__________________________________________________
Wow. Look at how slow you are at completing these tasks.
1. [D][ ] late deadline (by: Sep 10 2026, 12:00 PM)
2. [D][ ] middle deadline (by: Sep 09 2026, 12:00 PM)
3. [E][ ] early event (from: Sep 08 2026, 09:00 AM to: Sep 08 2026, 10:00 AM)
4. [T][ ] undated todo
__________________________________________________
Finally! Bye. I'm leaving!
__________________________________________________
```

## Test Case: Reject malformed sort commands

### Aim

Verify that malformed sort arguments are rejected without ending the session.

### Inputs

```text
sort asc
sort /order
sort /order sideways
sort /order asc extra
bye
```

### Expected Output

```text
__________________________________________________
__   __ _   _ _   _  ___
\ \ / /| | | | \ | |/ _ \
 \ V / | | | |  \| | | | |
  | |  | |_| | |\  | |_| |
  |_|   \___/|_| \_|\___/

I'm Yuno.
Can we just get this over quickly?
__________________________________________________
'asc' or 'desc'? If you are unsure, go back and think before even calling me.
__________________________________________________
'asc' or 'desc'? If you are unsure, go back and think before even calling me.
__________________________________________________
'asc' or 'desc'? If you are unsure, go back and think before even calling me.
__________________________________________________
'asc' or 'desc'? If you are unsure, go back and think before even calling me.
__________________________________________________
Finally! Bye. I'm leaving!
__________________________________________________
```
