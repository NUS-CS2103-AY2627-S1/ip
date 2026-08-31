# Bibi UI Test Plan

Each expected-output block contains ordered output fragments. The test-ui skill checks that each fragment appears in the console output after the previous fragment.

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
deadline return book /by Sunday
list
bye
```

### Expected output

```text
[D][ ] return book (by: Sunday)
Now you have 1 tasks in the list.
1. [D][ ] return book (by: Sunday)
```

## Test 3: Add and list an event

Aim: Confirm that text after /from and /to is retained and shown with an event.

### Input

```text
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
[E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 tasks in the list.
1. [E][ ] project meeting (from: Mon 2pm to: 4pm)
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
Bibi: I don't understand that command. Try todo, deadline, event, list, mark, unmark, or bye.
Bibi: Use todo followed by a description.
Bibi: Use mark followed by a task number, for example: mark 2
```

## Test 7: Keep tasks between sessions

Aim: Confirm that tasks added in one session are saved and restored in the next.

### Input

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
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
2. [D][ ] return book (by: June 6th)
3. [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
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
D | 0 | return book | June 6th
E | 0 | project meeting | Aug 6th 2pm | 4pm
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
2. [D][ ] return book (by: June 6th)
3. [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
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
  Line 5: expected 5 fields but found 3, for example: E | 0 | project meeting | Aug 6th 2pm | 4pm
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
