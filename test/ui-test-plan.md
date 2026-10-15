# Zikiai UI Test Plan

The cases below continue to test the console entry point `zikiai.Zikiai`, which
uses the same command-response handler as the GUI. The runner resolves JavaFX
compile dependencies through Gradle, then compiles into its temporary directory.
Console output and all existing expected results remain unchanged for Level 10.
GUI-specific behavior and smoke tests are documented in `test/gui-test-plan.md`.

The UI test runner starts a fresh chatbot process for each case. Commands are supplied in order, one per line. Expected output is matched exactly after expanding these placeholders:

- `{{SEPARATOR}}`: 60 underscore characters
- `{{BANNER}}`: the five-line Zikiai banner

A case can include an optional `Initial data file` block to seed `data/zikiai.txt` before startup. It can also include an optional `Expected data file` block, which the runner compares with the file after the commands finish.

## TC-01 Add and list a todo

**Aim:** Verify that a todo is stored, displayed with the todo type and incomplete status, and included in the task count.

### Input

```text
todo borrow book
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] borrow book
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] borrow book
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

## TC-02 Add and mark a deadline

**Aim:** Verify that an ISO deadline date is parsed, displayed in a friendly format, stored, marked as done, and listed correctly.

### Input

```text
deadline return book /by 2026-08-23
mark 1
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [D][ ] return book (by: Aug 23 2026)
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Shiok! This task is done already:
    [D][X] return book (by: Aug 23 2026)
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[D][X] return book (by: Aug 23 2026)
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

## TC-03 Add and unmark an event

**Aim:** Verify that an event is dissected around `/from` and `/to`, and that its inherited done status can be reversed.

### Input

```text
event project meeting /from Mon 2pm /to 4pm
mark 1
unmark 1
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [E][ ] project meeting (from: Mon 2pm to: 4pm)
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Shiok! This task is done already:
    [E][X] project meeting (from: Mon 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
Okay can, this task is not done yet:
    [E][ ] project meeting (from: Mon 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

## TC-04 Handle invalid commands with exceptions

**Aim:** Verify that a missing todo description and an unknown command are reported without terminating the chatbot.

### Input

```text
todo
blah
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use todo DESCRIPTION, for example todo read book.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Walao, I don't understand that command leh.
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

## TC-05 Handle invalid task numbers with exceptions

**Aim:** Verify that mark and unmark commands report task numbers that do not exist and continue accepting commands.

### Input

```text
todo read book
mark 99
unmark 0
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] read book
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] read book
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

## TC-06 Delete a task and handle invalid deletion

**Aim:** Verify that deleting a task removes the selected list item, displays it, updates the count, and rejects a nonexistent task number.

### Input

```text
todo read book
deadline return book /by 2026-08-23
event project meeting /from Mon 2pm /to 4pm
delete 2
list
delete 0
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] read book
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [D][ ] return book (by: Aug 23 2026)
You have 2 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [E][ ] project meeting (from: Mon 2pm to: 4pm)
You have 3 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Okay, removed this task already:
    [D][ ] return book (by: Aug 23 2026)
You have 2 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] read book
2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

## TC-07 Reject malformed task creation without changing state

**Aim:** Verify that malformed deadline and event commands do not add partial tasks or alter a previously stored todo.

### Input

```text
todo keep me
deadline return book
deadline /by Sunday
event meeting /from Monday
event /from Monday /to Tuesday
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] keep me
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use deadline DESCRIPTION /by yyyy-MM-dd, for example deadline submit report /by 2026-09-30.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use deadline DESCRIPTION /by yyyy-MM-dd, for example deadline submit report /by 2026-09-30.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use event DESCRIPTION /from START /to END, for example event meeting /from 2pm /to 4pm.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use event DESCRIPTION /from START /to END, for example event meeting /from 2pm /to 4pm.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] keep me
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

## TC-08 Interleave malformed and valid status commands

**Aim:** Verify that malformed action commands do not change a task and that later valid mark and unmark commands still work.

### Input

```text
todo alpha
mark homework
mark 1
unmark 1 task
unmark 1
delete two
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] alpha
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use mark TASK_NUMBER, for example mark 1.
{{SEPARATOR}}
{{SEPARATOR}}
Shiok! This task is done already:
    [T][X] alpha
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use unmark TASK_NUMBER, for example unmark 1.
{{SEPARATOR}}
{{SEPARATOR}}
Okay can, this task is not done yet:
    [T][ ] alpha
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use delete TASK_NUMBER, for example delete 1.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] alpha
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

## TC-09 Preserve numbering after deletion

**Aim:** Verify that deleting a middle task renumbers the remaining tasks and that subsequent commands use the new numbering.

### Input

```text
todo first
todo second
todo third
delete 2
mark 2
delete 3
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] first
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] second
You have 2 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] third
You have 3 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Okay, removed this task already:
    [T][ ] second
You have 2 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Shiok! This task is done already:
    [T][X] third
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] first
2.[T][X] third
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

## TC-10 Handle empty lists and oversized task numbers

**Aim:** Verify that operations on an empty list and task numbers larger than an integer are rejected without corrupting subsequently stored tasks.

### Input

```text
list
mark 1
unmark 1
delete 1
todo saved task
mark 999999999999999999999
delete 999999999999999999999
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] saved task
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! That task number is too large.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! That task number is too large.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] saved task
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

## TC-11 Save the latest task state to disk

**Aim:** Verify that adding, marking, and deleting tasks rewrites the data file with the latest task types, statuses, and details.

### Input

```text
todo read book
deadline return book /by 2026-08-23
event project meeting /from Mon 2pm /to 4pm
mark 1
delete 2
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] read book
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [D][ ] return book (by: Aug 23 2026)
You have 2 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [E][ ] project meeting (from: Mon 2pm to: 4pm)
You have 3 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Shiok! This task is done already:
    [T][X] read book
{{SEPARATOR}}
{{SEPARATOR}}
Okay, removed this task already:
    [D][ ] return book (by: Aug 23 2026)
You have 2 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

### Expected data file

```text
[T][X] | read book
[E][ ] | project meeting | Mon 2pm | 4pm
```

## TC-12 Save an empty list after deleting the last task

**Aim:** Verify that deleting the only task truncates the data file instead of leaving stale task data behind.

### Input

```text
todo temporary task
delete 1
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] temporary task
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Okay, removed this task already:
    [T][ ] temporary task
You have 0 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

### Expected data file

```text

```

## TC-13 Load tasks and preserve their state

**Aim:** Verify that startup reconstructs all task types and done statuses, and that loaded tasks can still be unmarked and deleted correctly.

### Initial data file

```text
[T][X] | read book
[D][ ] | return book | 2026-08-23
[E][X] | project meeting | Mon 2pm | 4pm
```

### Input

```text
list
unmark 1
delete 2
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][X] read book
2.[D][ ] return book (by: Aug 23 2026)
3.[E][X] project meeting (from: Mon 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
Okay can, this task is not done yet:
    [T][ ] read book
{{SEPARATOR}}
{{SEPARATOR}}
Okay, removed this task already:
    [D][ ] return book (by: Aug 23 2026)
You have 2 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] read book
2.[E][X] project meeting (from: Mon 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

### Expected data file

```text
[T][ ] | read book
[E][X] | project meeting | Mon 2pm | 4pm
```

## TC-14 Reject an unknown saved task type

**Aim:** Verify that an unknown task type is reported safely and the chatbot exits before overwriting the corrupted file.

### Initial data file

```text
[Z][ ] | mysterious task
```

### Input

```text
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! I couldn't load the saved tasks because line 1 is invalid.
{{SEPARATOR}}
```

### Expected data file

```text
[Z][ ] | mysterious task
```

## TC-15 Reject malformed saved fields

**Aim:** Verify that a missing deadline field is detected instead of constructing a partial task.

### Initial data file

```text
[T][ ] | valid task
[D][ ] | return book
```

### Input

```text
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! I couldn't load the saved tasks because line 2 is invalid.
{{SEPARATOR}}
```

### Expected data file

```text
[T][ ] | valid task
[D][ ] | return book
```

## TC-16 Reject an invalid saved status

**Aim:** Verify that only `[X]` and `[ ]` completion statuses are accepted from storage.

### Initial data file

```text
[T][?] | uncertain task
```

### Input

```text
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! I couldn't load the saved tasks because line 1 is invalid.
{{SEPARATOR}}
```

### Expected data file

```text
[T][?] | uncertain task
```

## TC-17 Reject reserved separators without changing state

**Aim:** Verify that pipe characters in new task fields are rejected before they can create ambiguous saved data, while valid stored tasks remain unchanged.

### Input

```text
todo keep me
todo bad | task
deadline return | book /by Sunday
event meeting /from Mon | noon /to 4pm
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] keep me
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Task details cannot contain the | character.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Task details cannot contain the | character.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Task details cannot contain the | character.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] keep me
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

### Expected data file

```text
[T][ ] | keep me
```

## TC-26 Handle command spacing and malformed parameters

**Aim:** Interleave harmless whitespace, malformed numbered commands, duplicate
or reversed markers, and valid commands to verify that errors are actionable and
do not corrupt task state.

### Input

```text
   todo     spaced task
mark
mark     1
deadline report /by 2026-09-30 /by 2026-10-01
deadline submit report /by 2026-09-30
event meeting /to 4pm /from 2pm
event meeting /from 2pm /to 4pm /to 5pm
   list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] spaced task
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use mark TASK_NUMBER, for example mark 1.
{{SEPARATOR}}
{{SEPARATOR}}
Shiok! This task is done already:
    [T][X] spaced task
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use deadline DESCRIPTION /by yyyy-MM-dd, for example deadline submit report /by 2026-09-30.
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [D][ ] submit report (by: Sep 30 2026)
You have 2 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use event DESCRIPTION /from START /to END, for example event meeting /from 2pm /to 4pm.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use event DESCRIPTION /from START /to END, for example event meeting /from 2pm /to 4pm.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][X] spaced task
2.[D][ ] submit report (by: Sep 30 2026)
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

### Expected data file

```text
[T][X] | spaced task
[D][ ] | submit report | 2026-09-30
```

## TC-23 Assign, display, persist, and clear priorities

**Aim:** Verify that priorities can be assigned to different task types, displayed in the list, saved, and cleared using `none`.

### Input

```text
todo read book
deadline submit report /by 2026-09-20
priority 1 high
priority 2 medium
list
priority 1 none
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] read book
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [D][ ] submit report (by: Sep 20 2026)
You have 2 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Steady! I've updated this task's priority:
    [T][HIGH][ ] read book
{{SEPARATOR}}
{{SEPARATOR}}
Steady! I've updated this task's priority:
    [D][MEDIUM][ ] submit report (by: Sep 20 2026)
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][HIGH][ ] read book
2.[D][MEDIUM][ ] submit report (by: Sep 20 2026)
{{SEPARATOR}}
{{SEPARATOR}}
Steady! I've updated this task's priority:
    [T][ ] read book
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] read book
2.[D][MEDIUM][ ] submit report (by: Sep 20 2026)
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

### Expected data file

```text
[T][ ] | read book
[D][ ][M] | submit report | 2026-09-20
```

## TC-24 Reject invalid priorities without changing state

**Aim:** Verify that malformed levels, nonnumeric task numbers, and missing tasks are rejected without affecting later valid priority changes.

### Input

```text
todo keep me
priority 1 urgent
list
priority one high
list
priority 99 low
list
priority 1 low
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] keep me
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use priority TASK_NUMBER with high, medium, low, or none.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] keep me
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use priority TASK_NUMBER with high, medium, low, or none.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] keep me
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] keep me
{{SEPARATOR}}
{{SEPARATOR}}
Steady! I've updated this task's priority:
    [T][LOW][ ] keep me
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][LOW][ ] keep me
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

### Expected data file

```text
[T][ ][L] | keep me
```

## TC-25 Load and update prioritized task types

**Aim:** Verify that saved priorities and completion states load for every task type and remain writable.

### Initial data file

```text
[T][X][H] | read book
[D][ ][M] | submit report | 2026-09-20
[E][ ][L] | project meeting | 2pm | 4pm
```

### Input

```text
list
priority 3 high
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][HIGH][X] read book
2.[D][MEDIUM][ ] submit report (by: Sep 20 2026)
3.[E][LOW][ ] project meeting (from: 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
Steady! I've updated this task's priority:
    [E][HIGH][ ] project meeting (from: 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][HIGH][X] read book
2.[D][MEDIUM][ ] submit report (by: Sep 20 2026)
3.[E][HIGH][ ] project meeting (from: 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

### Expected data file

```text
[T][X][H] | read book
[D][ ][M] | submit report | 2026-09-20
[E][ ][H] | project meeting | 2pm | 4pm
```

## TC-18 Load an empty data file

**Aim:** Verify that an existing empty data file behaves like an empty task list and does not cause a startup error.

### Initial data file

```text

```

### Input

```text
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

### Expected data file

```text

```

## TC-19 Validate deadline dates without changing existing tasks

**Aim:** Interleave valid and invalid deadline commands to verify that non-ISO and impossible dates are rejected without changing the list, while a valid leap-day deadline is accepted.

### Input

```text
todo keep me
deadline words /by Sunday
deadline impossible /by 2025-02-29
list
deadline leap day /by 2024-02-29
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] keep me
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Please enter the deadline as yyyy-MM-dd, for example 2026-08-23.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Please enter the deadline as yyyy-MM-dd, for example 2026-08-23.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] keep me
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [D][ ] leap day (by: Feb 29 2024)
You have 2 tasks in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] keep me
2.[D][ ] leap day (by: Feb 29 2024)
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

### Expected data file

```text
[T][ ] | keep me
[D][ ] | leap day | 2024-02-29
```

## TC-20 Reject an impossible date in saved data

**Aim:** Verify that an impossible saved deadline date is reported as corrupted data and is not overwritten.

### Initial data file

```text
[D][ ] | impossible task | 2025-02-29
```

### Input

```text
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! I couldn't load the saved tasks because line 1 is invalid.
{{SEPARATOR}}
```

### Expected data file

```text
[D][ ] | impossible task | 2025-02-29
```

## TC-21 Find matching tasks across task types

**Aim:** Verify that find searches task descriptions, preserves the original order and status, and excludes non-matching tasks.

### Initial data file

```text
[T][X] | read book
[D][ ] | return book | 2026-08-23
[E][ ] | project meeting | Mon 2pm | 4pm
```

### Input

```text
find book
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Found these matching tasks for you:
1.[T][X] read book
2.[D][ ] return book (by: Aug 23 2026)
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

### Expected data file

```text
[T][X] | read book
[D][ ] | return book | 2026-08-23
[E][ ] | project meeting | Mon 2pm | 4pm
```

## TC-22 Interleave invalid, empty, and successful searches

**Aim:** Verify that empty and unsuccessful searches do not alter task state, while a later valid search still returns the expected task.

### Input

```text
todo keep me
find
find missing
find keep
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai, your task buddy.
What do you need help with today ah?
{{SEPARATOR}}
{{SEPARATOR}}
Can! I've added this task for you:
    [T][ ] keep me
You have 1 task in your list now.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo! Use find KEYWORD, for example find book.
{{SEPARATOR}}
{{SEPARATOR}}
Aiyo, no matching tasks leh.
{{SEPARATOR}}
{{SEPARATOR}}
Found these matching tasks for you:
1.[T][ ] keep me
{{SEPARATOR}}
{{SEPARATOR}}
Here are your tasks:
1.[T][ ] keep me
{{SEPARATOR}}
{{SEPARATOR}}
Okay, bye bye! See you again lah.
{{SEPARATOR}}
```

### Expected data file

```text
[T][ ] | keep me
```
