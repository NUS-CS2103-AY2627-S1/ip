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
