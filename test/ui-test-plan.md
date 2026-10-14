# bingus.Bingus UI Test Plan

All divider lines begin at column one. Ordinary response lines have four leading spaces, and task lines in
add, mark, unmark, update, and delete responses have eight. Every test includes the greeting shown at startup.

Run each test independently and reset `data/bingus.txt` before starting it. For Tests 1–4 and 7, ensure that
the file does not exist. For Tests 5, 6, and 8, use only the data described in that test's setup.

## Test 1: Add a todo task

### Input

```text
todo borrow book
bye
```

### Expected output

```text
____________________________________________________________
    Hi! I'm Bingus, your task-watching cat.
    Tell me what you need to remember, and I'll keep an eye on it.
____________________________________________________________
____________________________________________________________
    Got it! I added this task:
        [T][○] borrow book
    You now have 1 task in your list.
____________________________________________________________
____________________________________________________________
    Bingus is curling up for a nap. See you soon!
____________________________________________________________
```

## Test 2: Add deadline and event tasks

### Input

```text
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-03 1400 /to 2019-12-03 1600
bye
```

### Expected output

```text
____________________________________________________________
    Hi! I'm Bingus, your task-watching cat.
    Tell me what you need to remember, and I'll keep an eye on it.
____________________________________________________________
____________________________________________________________
    Got it! I added this task:
        [D][○] return book (by: Dec 2 2019, 6:00 PM)
    You now have 1 task in your list.
____________________________________________________________
____________________________________________________________
    Got it! I added this task:
        [E][○] project meeting (from: Dec 3 2019, 2:00 PM to: Dec 3 2019, 4:00 PM)
    You now have 2 tasks in your list.
____________________________________________________________
____________________________________________________________
    Bingus is curling up for a nap. See you soon!
____________________________________________________________
```

## Test 3: List and mark mixed task types

### Input

```text
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-03 1400 /to 2019-12-03 1600
list
mark 2
list
bye
```

### Expected output

```text
____________________________________________________________
    Hi! I'm Bingus, your task-watching cat.
    Tell me what you need to remember, and I'll keep an eye on it.
____________________________________________________________
____________________________________________________________
    Got it! I added this task:
        [T][○] read book
    You now have 1 task in your list.
____________________________________________________________
____________________________________________________________
    Got it! I added this task:
        [D][○] return book (by: Dec 2 2019, 6:00 PM)
    You now have 2 tasks in your list.
____________________________________________________________
____________________________________________________________
    Got it! I added this task:
        [E][○] project meeting (from: Dec 3 2019, 2:00 PM to: Dec 3 2019, 4:00 PM)
    You now have 3 tasks in your list.
____________________________________________________________
____________________________________________________________
    Here are the tasks I am watching:
    1. [T][○] read book
    2. [D][○] return book (by: Dec 2 2019, 6:00 PM)
    3. [E][○] project meeting (from: Dec 3 2019, 2:00 PM to: Dec 3 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
    Purrfect! This task is complete:
        [D][✓] return book (by: Dec 2 2019, 6:00 PM)
____________________________________________________________
____________________________________________________________
    Here are the tasks I am watching:
    1. [T][○] read book
    2. [D][✓] return book (by: Dec 2 2019, 6:00 PM)
    3. [E][○] project meeting (from: Dec 3 2019, 2:00 PM to: Dec 3 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
    Bingus is curling up for a nap. See you soon!
____________________________________________________________
```

## Test 4: Persist task text containing the record delimiter

### Input

```text
todo plan | revise
bye
```

### Expected output

```text
____________________________________________________________
    Hi! I'm Bingus, your task-watching cat.
    Tell me what you need to remember, and I'll keep an eye on it.
____________________________________________________________
____________________________________________________________
    Got it! I added this task:
        [T][○] plan | revise
    You now have 1 task in your list.
____________________________________________________________
____________________________________________________________
    Bingus is curling up for a nap. See you soon!
____________________________________________________________
```

After the first command, verify that `data/bingus.txt` contains:

```text
T|0|cGxhbiB8IHJldmlzZQ==
```

## Test 5: Load saved task records at startup

### Setup

Create `data/bingus.txt` with:

```text
T|1|cGxhbiB8IHJldmlzZQ==
```

### Input

```text
list
bye
```

### Expected output

```text
____________________________________________________________
    Hi! I'm Bingus, your task-watching cat.
    Tell me what you need to remember, and I'll keep an eye on it.
____________________________________________________________
____________________________________________________________
    Here are the tasks I am watching:
    1. [T][✓] plan | revise
____________________________________________________________
____________________________________________________________
    Bingus is curling up for a nap. See you soon!
____________________________________________________________
```

## Test 6: Start safely with a corrupted save file

### Setup

Create `data/bingus.txt` with this invalid content:

```text
T|1|not-valid-base64!
```

### Input

```text
list
bye
```

### Expected output

```text
____________________________________________________________
    I could not load your saved tasks. I have started with an empty list.
____________________________________________________________
____________________________________________________________
    Hi! I'm Bingus, your task-watching cat.
    Tell me what you need to remember, and I'll keep an eye on it.
____________________________________________________________
____________________________________________________________
    Your task list is empty. Time for a cat nap!
____________________________________________________________
____________________________________________________________
    Bingus is curling up for a nap. See you soon!
____________________________________________________________
```

## Test 7: Validate date and time input

### Input

```text
deadline return book /by 2019-02-29 1800
event meeting /from 2019-12-03 1600 /to 2019-12-03 1400
bye
```

### Expected output

```text
____________________________________________________________
    Hi! I'm Bingus, your task-watching cat.
    Tell me what you need to remember, and I'll keep an eye on it.
____________________________________________________________
____________________________________________________________
    That deadline date and time are not valid.
    Use yyyy-MM-dd HHmm, for example 2019-12-02 1800.
____________________________________________________________
____________________________________________________________
    The event must end after it starts.
____________________________________________________________
____________________________________________________________
    Bingus is curling up for a nap. See you soon!
____________________________________________________________
```

## Test 8: List tasks occurring on a date

### Setup

Create these tasks before running the command:

```text
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-03 1600
```

### Input

```text
list 2019-12-02
bye
```

### Expected output

```text
____________________________________________________________
    Hi! I'm Bingus, your task-watching cat.
    Tell me what you need to remember, and I'll keep an eye on it.
____________________________________________________________
____________________________________________________________
    Here are the tasks I am watching on Dec 2 2019:
    2. [D][○] return book (by: Dec 2 2019, 6:00 PM)
    3. [E][○] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 3 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
    Bingus is curling up for a nap. See you soon!
____________________________________________________________
```
