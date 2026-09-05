# Bibi UI Test Plan

Each expected-output block contains ordered output fragments. The test-ui skill checks that each fragment appears in the console output after the previous fragment.

These cases drive the text-based interface (`./gradlew runCli`). They cover the GUI too, because both interfaces hand the same typed line to the same parser and commands; only the last step, printing versus putting the words in a dialog box, differs. What they do not cover is the window itself, which has to be checked by opening it.

## Test 1: Add and list a ToDo

Aim: Confirm that a ToDo is stored without date or time details.

### Input

```text
todo borrow book
list
bye
```

### Expected output

```text
Bibi: Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
Bibi: Here are the tasks in your list:
1. [T][ ] borrow book
Bibi: Goodbye! Till next time...
```

## Test 2: Add and list a deadline

Aim: Confirm that text after /by is retained and shown with a deadline.

### Input

```text
deadline return book /by 2019-10-15
list
bye
```

### Expected output

```text
[D][ ] return book (by: Oct 15 2019)
Now you have 1 tasks in the list.
1. [D][ ] return book (by: Oct 15 2019)
```

## Test 3: Add and list an event

Aim: Confirm that text after /from and /to is retained and shown with an event.

### Input

```text
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
list
bye
```

### Expected output

```text
[E][ ] project meeting (from: Aug 06 2019 2:00PM to: Aug 06 2019 4:00PM)
Now you have 1 tasks in the list.
1. [E][ ] project meeting (from: Aug 06 2019 2:00PM to: Aug 06 2019 4:00PM)
```

## Test 4: Mark and unmark a task

Aim: Confirm that completion state changes are displayed for a Task subclass.

### Input

```text
todo join sports club
mark 1
list
unmark 1
list
bye
```

### Expected output

```text
Bibi: Marked task 1 as complete.
1. [T][X] join sports club
Bibi: Unmarked task 1, now incomplete.
1. [T][ ] join sports club
```

## Test 5: Reject incomplete deadline and event commands

Aim: Confirm that missing date/time markers produce clear guidance instead of invalid tasks.

### Input

```text
deadline return book
event project meeting /from Mon 2pm
bye
```

### Expected output

```text
Bibi: Use deadline <description> /by <time>.
Bibi: Use event <description> /from <start> /to <end>.
```

## Test 6: Reject empty, unknown, and incomplete commands

Aim: Confirm that invalid input is reported without ending the program.

### Input

```text

remind me
todo
mark two
bye
```

### Expected output

```text
Bibi: Please enter a command.
Bibi: I don't understand that command. Try todo, deadline, event, list, find, on, mark, unmark, or bye.
Bibi: Use todo followed by a description.
Bibi: Use mark followed by a task number, for example: mark 2
```

## Test 7: Keep tasks between sessions

Aim: Confirm that tasks added in one session are saved and restored in the next.

### Input

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
mark 1
bye
<<restart>>
list
bye
```

### Expected output

```text
Bibi: Marked task 1 as complete.
Bibi: Goodbye! Till next time...
Bibi: Loaded 3 saved task(s).
Bibi: Here are the tasks in your list:
1. [T][X] read book
2. [D][ ] return book (by: Jun 06 2019)
3. [E][ ] project meeting (from: Aug 06 2019 2:00PM to: Aug 06 2019 4:00PM)
```

## Test 8: Start with no save file

Aim: Confirm that a first run without a save file starts with an empty list.

### Input

```text
list
bye
```

### Expected output

```text
Bibi: Your task list is empty.
Bibi: Goodbye! Till next time...
```

## Test 9: Restore a task list written earlier

Aim: Confirm that an existing save file is read in the documented format.

### Saved data

```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600
T | 1 | join sports club
```

### Input

```text
list
bye
```

### Expected output

```text
Bibi: Loaded 4 saved task(s).
1. [T][X] read book
2. [D][ ] return book (by: Jun 06 2019)
3. [E][ ] project meeting (from: Aug 06 2019 2:00PM to: Aug 06 2019 4:00PM)
4. [T][X] join sports club
```

## Test 10: Recover from a corrupted save file

Aim: Confirm that damaged lines are reported and skipped while valid tasks still load.

### Saved data

```text
T | 1 | read book
this line is not a task
X | 0 | unknown type
D | 2 | return book | June 6th
E | 0 | missing the end time

T | 0 | join sports club
```

### Input

```text
list
bye
```

### Expected output

```text
Bibi: Loaded 2 saved task(s).
Bibi: I had trouble reading part of your save file:
  Line 2: expected at least type, status, and description separated by '|'.
  Line 3: unknown task type 'X'.
  Line 4: status '2' should be 1 (done) or 0 (not done).
  Line 5: expected 5 fields but found 3, for example: E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600
Bibi: Those entries are skipped, and will be dropped from the file the next time your task list changes.
Bibi: Here are the tasks in your list:
1. [T][X] read book
2. [T][ ] join sports club
```

## Test 11: Reject task text containing the save-file separator

Aim: Confirm that a description that would corrupt the save file is refused.

### Input

```text
todo read | book
list
bye
```

### Expected output

```text
Bibi: Task text cannot contain '|' because that character separates the fields in the save file.
Bibi: Your task list is empty.
```

## Test 12: Understand dates and times in several formats

Aim: Confirm that dates are parsed and shown in the display format, with the
time kept only when one was given.

### Input

```text
deadline return book /by 2/12/2019 1800
deadline submit essay /by 2019-10-15
event camp /from 2019-08-10 /to 2019-08-12
list
bye
```

### Expected output

```text
[D][ ] return book (by: Dec 02 2019 6:00PM)
[D][ ] submit essay (by: Oct 15 2019)
[E][ ] camp (from: Aug 10 2019 to: Aug 12 2019)
Bibi: Here are the tasks in your list:
1. [D][ ] return book (by: Dec 02 2019 6:00PM)
2. [D][ ] submit essay (by: Oct 15 2019)
3. [E][ ] camp (from: Aug 10 2019 to: Aug 12 2019)
```

## Test 13: Reject dates that cannot be understood

Aim: Confirm that unreadable dates and impossible event ranges are refused with
guidance instead of being stored as text.

### Input

```text
deadline return book /by next Tuesday
deadline return book /by 2019-13-45
event camp /from 2019-08-12 /to 2019-08-10
list
bye
```

### Expected output

```text
Bibi: I could not read the date 'next Tuesday'. Use yyyy-MM-dd or d/M/yyyy, optionally followed by a 24-hour time, for example 2019-10-15 or 2/12/2019 1800.
Bibi: I could not read the date '2019-13-45'.
Bibi: An event cannot end before it starts.
Bibi: Your task list is empty.
```

## Test 14: Report saved tasks whose dates predate this format

Aim: Confirm that a save file written before dates were understood is reported
rather than silently accepted.

### Saved data

```text
T | 0 | read book
D | 0 | return book | Sunday
```

### Input

```text
list
bye
```

### Expected output

```text
Bibi: Loaded 1 saved task(s).
Bibi: I had trouble reading part of your save file:
  Line 2: I could not read the date 'Sunday'.
Bibi: Here are the tasks in your list:
1. [T][ ] read book
```

## Test 15: List what is happening on one date

Aim: Confirm that the on command finds deadlines due that day and events running
that day, keeps each task's number from the full list, and ignores ToDos.

### Input

```text
todo read book
deadline return book /by 2019-08-11
event camp /from 2019-08-10 /to 2019-08-12
event party /from 2019-08-11 1900 /to 2019-08-11 2300
on 2019-08-11
on 2019-12-25
on someday
bye
```

### Expected output

```text
Bibi: Here is what you have on Aug 11 2019:
2. [D][ ] return book (by: Aug 11 2019)
3. [E][ ] camp (from: Aug 10 2019 to: Aug 12 2019)
4. [E][ ] party (from: Aug 11 2019 7:00PM to: Aug 11 2019 11:00PM)
Bibi: You have nothing on Dec 25 2019.
Bibi: I could not read the date 'someday'.
```

## Test 16: Show the list of commands

Aim: Confirm that help is recognised on its own, and lists every command.

### Input

```text
help
bye
```

### Expected output

```text
Bibi: Here are the commands I understand:
  todo <description>
  deadline <description> /by <time>
  event <description> /from <start> /to <end>
  list
  find <keyword>
  on <date>
  mark <number>
  unmark <number>
  remove <number>
  help
  bye
Bibi: Goodbye! Till next time...
```

## Test 17: Find tasks by keyword

Aim: Confirm that find matches descriptions case-insensitively, keeps each
task's number from the full list, and reports when nothing matches.

### Input

```text
todo read book
deadline return Book /by 2019-06-06
todo join sports club
mark 1
find book
find sports
find zzz
find
bye
```

### Expected output

```text
Bibi: Here are the matching tasks in your list:
1. [T][X] read book
2. [D][ ] return Book (by: Jun 06 2019)
Bibi: Here are the matching tasks in your list:
3. [T][ ] join sports club
Bibi: No tasks match 'zzz'.
Bibi: Use find followed by a keyword, for example: find book
```
