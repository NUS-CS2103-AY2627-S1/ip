# UI test plan

## Test: Add a to-do and list it

Aim: Confirm that a to-do is stored and displayed using the to-do type marker.

### Commands

```text
todo borrow book
list
```

### Expected outputs

```text
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
```

```text
     Here are the tasks in your list:
     1.[T][ ] borrow book
```

## Test: Add a deadline

Aim: Confirm that an ISO deadline date is parsed and displayed in a friendlier format.

### Commands

```text
deadline return book /by 2019-12-02
```

### Expected outputs

```text
     Got it. I've added this task:
       [D][ ] return book (by: Dec 2 2019)
     Now you have 1 tasks in the list.
```

## Test: Add, list, and delete a note

Aim: Confirm that a note can be saved with the note marker, listed, and removed like other items.

### Commands

```text
note Bring a laptop charger
mark 1
list
delete 1
```

### Expected outputs

```text
     Got it. I've added this task:
       [N] Bring a laptop charger
     Now you have 1 tasks in the list.
```

```text
     Quack? Notes do not have a completion status.
```

```text
     Here are the tasks in your list:
     1.[N] Bring a laptop charger
```

```text
     Noted. I've removed this task:
       [N] Bring a laptop charger
     Now you have 0 tasks in the list.
```

## Test: Reject invalid deadline dates

Aim: Confirm that deadlines reject non-ISO and impossible dates with a helpful error.

### Commands

```text
deadline return book /by Sunday
deadline return book /by 2019-02-30
```

### Expected outputs

```text
     Quack? Use yyyy-MM-dd for the deadline date.
```

```text
     Quack? Use yyyy-MM-dd for the deadline date.
```

## Test: Add an event

Aim: Confirm that event start and end times are displayed with the event marker.

### Commands

```text
event project meeting /from Mon 2pm /to 4pm
```

### Expected outputs

```text
     Got it. I've added this task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 1 tasks in the list.
```

## Test: Reject incorrect commands and recover

Aim: Confirm that invalid input produces a helpful error and the chatbot continues accepting commands.

### Commands

```text
todo
blah
deadline submit report
event project meeting /from Mon 2pm
mark one
unmark 1
```

### Expected outputs

```text
     Quack? Give me a todo description!
```

```text
     Quack? I don't know what that means :-(
```

```text
     Quack? Use /by to give the deadline.
```

```text
     Quack? Use /from START /to END for an event.
```

```text
     Quack? Please enter a valid task number.
```

```text
     Quack? Please enter a task number from the list.
```

## Test: Delete a task and renumber the list

Aim: Confirm that deleting a task removes the selected item and that remaining tasks shift into their new positions.

### Commands

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
delete 3
list
```

### Expected outputs

```text
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
```

```text
     Got it. I've added this task:
       [D][ ] return book (by: Jun 6 2019)
     Now you have 2 tasks in the list.
```

```text
     Got it. I've added this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 3 tasks in the list.
```

```text
     Noted. I've removed this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 2 tasks in the list.
```

```text
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Jun 6 2019)
```

## Test: Reject an invalid delete number

Aim: Confirm that a delete command cannot remove a task outside the current list.

### Commands

```text
delete 1
```

### Expected outputs

```text
     Quack? Please enter a task number from the list.
```

## Test: Find tasks by description

Aim: Confirm that find searches task descriptions without regard to letter case and renumbers matches.

### Commands

```text
todo read book
deadline return Book /by 2019-06-06
event project meeting /from book room /to 4pm
find BOOK
find
```

### Expected outputs

```text
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
```

```text
     Got it. I've added this task:
       [D][ ] return Book (by: Jun 6 2019)
     Now you have 2 tasks in the list.
```

```text
     Got it. I've added this task:
       [E][ ] project meeting (from: book room to: 4pm)
     Now you have 3 tasks in the list.
```

```text
     Here are the matching tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return Book (by: Jun 6 2019)
```

```text
     Quack? Give me a keyword to find!
```
