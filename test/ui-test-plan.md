# Gooble UI test plan

Run these cases with the project-local `test-ui` skill. Each case starts a new Gooble session; include `bye` so the session exits normally.

## Test case: Greet the user and exit

Aim: Verify that Gooble greets the user at startup and exits politely on the bye command.

### Inputs

```text
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Add a general task

Aim: Verify that entering ordinary text adds a task to the list and writes the current list to disk.

### Inputs

```text
add read book
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: List tasks

Aim: Verify that list shows an added task with its completion status.

### Inputs

```text
add read book
list
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Mark a task

Aim: Verify that mark changes a task's completion status to done.

### Inputs

```text
add read book
mark 1
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [X] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Find tasks by description keyword

Aim: Verify that find displays only tasks whose descriptions contain the keyword, preserving match order.

### Inputs

```text
todo read book
deadline return book /by 2/12/2019 1800
todo buy tea
find book
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy tea
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list tehee:
1.[T] [ ] read book
2.[D] [ ] return book (by: Dec 02 2019, 6:00 PM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Unmark a task

Aim: Verify that unmark changes a completed task back to incomplete.

### Inputs

```text
add read book
mark 1
unmark 1
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [X] read book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Add a to-do

Aim: Verify that a to-do task is created and displayed with the T type marker.

### Inputs

```text
todo borrow book
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Add a deadline

Aim: Verify that a deadline task keeps its by-date in the task display.

### Inputs

```text
deadline return book /by 2/12/2019 1800
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject an invalid deadline date

Aim: Verify that unsupported deadline date formats show a helpful example.

### Inputs

```text
deadline return book /by Sunday
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Please use a proper date format. Examples: 2019-12-02 or 2/12/2019 1800
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Add a Valentine's Day deadline

Aim: Verify that a deadline on February 14 displays the Valentine's Day message.

### Inputs

```text
deadline buy chocolates /by 2026-02-14 1800
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] buy chocolates (by: Feb 14 2026, 6:00 PM)
Love is in the air~
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Add a Chinese New Year deadline

Aim: Verify that a deadline on Chinese New Year displays the prosperity greeting.

### Inputs

```text
deadline collect red packets /by 2026-02-17 1800
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] collect red packets (by: Feb 17 2026, 6:00 PM)
恭喜发财！！
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Add an event

Aim: Verify that an event includes its start and end dates in the display.

### Inputs

```text
event project meeting /from 2026-03-12 1400 /to 2026-03-12 1600
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: 2026-03-12 1400 to: 2026-03-12 1600)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Filter events by date and time

Aim: Verify that list from/to shows only events fully within the requested period.

### Inputs

```text
event in range /from 2026-03-10 0900 /to 2026-03-10 1000
event outside range /from 2026-03-12 0900 /to 2026-03-12 1000
list from 2026-03-09 0000 to 2026-03-11 2359
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] in range (from: 2026-03-10 0900 to: 2026-03-10 1000)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] outside range (from: 2026-03-12 0900 to: 2026-03-12 1000)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the events in your list for that period:
1.[E][ ] in range (from: 2026-03-10 0900 to: 2026-03-10 1000)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject an invalid event list range

Aim: Verify that a reversed list date-time range is rejected.

### Inputs

```text
list from 2026-03-12 1000 to 2026-03-10 0900
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Please ensure the 'to' date and time is not before the 'from' date and time.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject incomplete task commands

Aim: Verify that todo, deadline, and event commands without descriptions show errors and do not add tasks.

### Inputs

```text
todo
deadline
event
add
list
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Missing a description. Gooble needs something to put on the task list.
____________________________________________________________
____________________________________________________________
Missing a description. Gooble needs something to put on the task list.
____________________________________________________________
____________________________________________________________
Missing a description. Gooble needs something to put on the task list.
____________________________________________________________
____________________________________________________________
Missing a description. Gooble needs something to put on the task list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
## Test case: Reject invalid task commands

Aim: Verify that input without command show errors and do not add tasks.

### Inputs

```text
book
tea
hi
list
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Invalid command. Gooble is confused, but not offended. Type help to see what I understand.
____________________________________________________________
____________________________________________________________
Invalid command. Gooble is confused, but not offended. Type help to see what I understand.
____________________________________________________________
____________________________________________________________
Invalid command. Gooble is confused, but not offended. Type help to see what I understand.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject commands without task numbers

Aim: Verify that mark, unmark, and delete without a task number report an error instead of crashing.

### Inputs

```text
mark
unmark
delete

bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Please provide a valid task number. Gooble cannot mark task-shaped air.
____________________________________________________________
____________________________________________________________
Please provide a valid task number. Gooble cannot mark task-shaped air.
____________________________________________________________
____________________________________________________________
Please provide a valid task number. Gooble cannot delete task-shaped air.
____________________________________________________________
____________________________________________________________
Invalid command. Gooble is confused, but not offended. Type help to see what I understand.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Delete task commands

Aim: Verify that the delete command removes the task from the list

### Inputs

```text
add book
add tea
list
delete 2
list
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _      
 / ___| ___   ___ | |__ | | ___ 
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: book
____________________________________________________________
____________________________________________________________
added: tea
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[ ] book
2.[ ] tea
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [ ] tea
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[ ] book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Tag and untag a task

Aim: Verify that a task can receive a normalized tag, display it, and have all tags removed.

### Inputs

```text
add read book
tag 1 #Fun
list
untag 1
list
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: read book
____________________________________________________________
____________________________________________________________
I've tagged this task:
  [ ] read book [#fun]
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[ ] read book [#fun]
____________________________________________________________
____________________________________________________________
I've removed the tags from this task:
  [ ] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Find tasks by tag

Aim: Verify that find searches normalized tags as well as task descriptions.

### Inputs

```text
todo read book
tag 1 #School
find #school
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
I've tagged this task:
  [T][ ] read book [#school]
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list tehee:
1.[T] [ ] read book [#school]
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Tag validation and oldest-tag rollover

Aim: Verify invalid tag input, duplicate rejection, and removal of the oldest tag when a fourth tag is added.

### Inputs

```text
todo read book
tag 1 #one
tag 1 #two
tag 1 #three
tag 1 #four
tag 1 #four
tag 1 #bad!
untag 1
untag 1
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
I've tagged this task:
  [T][ ] read book [#one]
____________________________________________________________
____________________________________________________________
I've tagged this task:
  [T][ ] read book [#one, #two]
____________________________________________________________
____________________________________________________________
I've tagged this task:
  [T][ ] read book [#one, #two, #three]
____________________________________________________________
____________________________________________________________
I've tagged this task:
  [T][ ] read book [#two, #three, #four]
____________________________________________________________
____________________________________________________________
This task already has that tag.
____________________________________________________________
____________________________________________________________
Tags may contain only letters, numbers, hyphens, and underscores. Gooble rejects tag confetti.
____________________________________________________________
____________________________________________________________
I've removed the tags from this task:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
This task has no tags to remove. Gooble found only tag-shaped air.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject duplicate task details

Aim: Verify that duplicate tasks are rejected without changing the task list.

### Inputs

```text
todo read book
todo read book
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
A task with the same details already exists.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject reversed event date-times

Aim: Verify that an event ending at or before its start is rejected.

### Inputs

```text
event project meeting /from 2026-03-12 1000 /to 2026-03-12 0900
bye
```

### Expected output

```text
____________________________________________________________
  ____            _     _
 / ___| ___   ___ | |__ | | ___
| |  _ / _ \ / _ \| '_ \| |/ _ \
| |_| | (_) | (_) | |_) | |  __/
 \____|\___/ \___/|_.__/|_|\___|
Hello! I'm Gooble.
What can I do for you?
____________________________________________________________
____________________________________________________________
Please ensure the event ends after it starts.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
