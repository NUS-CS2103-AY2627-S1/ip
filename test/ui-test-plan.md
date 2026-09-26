# Dulio UI Test Plan

The `test-ui` skill runs these cases from the repository root. Inputs are supplied to `Dulio` through standard input. Expected output is compared exactly after converting CRLF to LF and trimming trailing whitespace from each line.

## Case: manage-recurring-task

### Aim
Verify that a recurring task accepts a repetition interval, appears in task listings, can be marked, and is persisted.

### Inputs
1. `recurring project meeting /every week`
2. `list`
3. `mark 1`
4. `bye`

### Expected output
```text
____________________________________________________________
 ____  _   _ _     ___ ___
|  _ \| | | | |   |_ _/ _ \
| | | | | | | |    | | | | |
| |_| | |_| | |___ | | |_| |
|____/ \___/|_____|___\___/

Hello! I'm Dulio.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [R][ ] project meeting (every: week)
   Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[R][ ] project meeting (every: week)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [R][X] project meeting (every: week)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected data file
Path: `data/dulio.txt`

```text
R | 1 | project meeting | week
```

## Case: add-and-list-task-types

### Aim
Verify that todo, deadline, and event commands create the correct task types, preserve their date/time strings, report the task count, and display correctly in the list.

### Inputs
1. `todo borrow book`
2. `deadline return book /by 2019-06-06`
3. `event project meeting /from Mon 2pm /to 4pm`
4. `list`
5. `bye`

### Expected output
```text
____________________________________________________________
 ____  _   _ _     ___ ___
|  _ \| | | | |   |_ _/ _ \
| | | | | | | |    | | | | |
| |_| | |_| | |___ | | |_| |
|____/ \___/|_____|___\___/

Hello! I'm Dulio.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
   Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Jun 06 2019)
   Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
   Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Jun 06 2019)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Case: find-tasks-by-keyword

### Aim
Verify that `find` performs a case-insensitive search in task descriptions, preserves original task numbers, and reports when there are no matches.

### Inputs
1. `todo read book`
2. `deadline return book /by 2019-06-06`
3. `event project meeting /from Aug 6th 2pm /to 4pm`
4. `todo join sports club`
5. `find BOOK`
6. `find airplane`
7. `bye`

### Expected output
```text
____________________________________________________________
 ____  _   _ _     ___ ___
|  _ \| | | | |   |_ _/ _ \
| | | | | | | |    | | | | |
| |_| | |_| | |___ | | |_| |
|____/ \___/|_____|___\___/

Hello! I'm Dulio.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
   Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Jun 06 2019)
   Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
   Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] join sports club
   Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 (no matching tasks)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Case: parse-and-format-deadline-date

### Aim
Verify that an ISO deadline date is parsed as a date, displayed in a readable format, saved in ISO format, and that invalid date input is rejected.

### Inputs
1. `deadline return book /by 2019-12-02`
2. `list`
3. `deadline late task /by 02/12/2019`
4. `bye`

### Expected output
```text
____________________________________________________________
 ____  _   _ _     ___ ___
|  _ \| | | | |   |_ _/ _ \
| | | | | | | |    | | | | |
| |_| | |_| | |___ | | |_| |
|____/ \___/|_____|___\___/

Hello! I'm Dulio.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2019)
   Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
 OOPS!!! Please enter dates in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected data file
Path: `data/dulio.txt`

```text
D | 0 | return book | 2019-12-02
```

## Case: save-tasks-after-changes

### Aim
Verify that adding a todo, deadline, and event automatically creates or updates the relative data file with their types, statuses, descriptions, and date/time values.

### Inputs
1. `todo read book`
2. `deadline return book /by 2019-06-06`
3. `event project meeting /from Aug 6th 2pm /to 4pm`
4. `mark 1`
5. `bye`

### Expected output
```text
____________________________________________________________
 ____  _   _ _     ___ ___
|  _ \| | | | |   |_ _/ _ \
| | | | | | | |    | | | | |
| |_| | |_| | |___ | | |_| |
|____/ \___/|_____|___\___/

Hello! I'm Dulio.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
   Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Jun 06 2019)
   Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
   Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected data file
Path: `data/dulio.txt`

```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 0 | project meeting | Aug 6th 2pm | 4pm
```

## Case: load-tasks-at-startup

### Aim
Verify that Dulio loads saved todo, deadline, and event tasks with their statuses and date/time values when it starts.

### Initial data file
Path: `data/dulio.txt`

```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 0 | project meeting | Aug 6th 2pm | 4pm
```

### Inputs
1. `list`
2. `bye`

### Expected output
```text
____________________________________________________________
 ____  _   _ _     ___ ___
|  _ \| | | | |   |_ _/ _ \
| | | | | | | |    | | | | |
| |_| | |_| | |___ | | |_| |
|____/ \___/|_____|___\___/

Hello! I'm Dulio.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: Jun 06 2019)
 3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Case: reject-empty-and-unknown-commands

### Aim
Verify that an empty todo description and an unknown command produce clear error messages while the program continues accepting commands.

### Inputs
1. `todo`
2. `blah`
3. `bye`

### Expected output
```text
____________________________________________________________
 ____  _   _ _     ___ ___
|  _ \| | | | |   |_ _/ _ \
| | | | | | | |    | | | | |
| |_| | |_| | |___ | | |_| |
|____/ \___/|_____|___\___/

Hello! I'm Dulio.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
 OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Case: delete-task

### Aim
Verify that deleting a task by its list number removes the correct typed task, shifts later tasks up, and reports the updated task count.

### Inputs
1. `todo read book`
2. `deadline return book /by 2019-06-06`
3. `event project meeting /from Aug 6th 2pm /to 4pm`
4. `todo join sports club`
5. `todo borrow book`
6. `mark 1`
7. `mark 2`
8. `mark 4`
9. `list`
10. `delete 3`
11. `list`
12. `bye`

### Expected output
```text
____________________________________________________________
 ____  _   _ _     ___ ___
|  _ \| | | | |   |_ _/ _ \
| | | | | | | |    | | | | |
| |_| | |_| | |___ | | |_| |
|____/ \___/|_____|___\___/

Hello! I'm Dulio.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
   Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Jun 06 2019)
   Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
   Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] join sports club
   Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
   Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] join sports club
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2019)
 3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 4.[T][X] join sports club
 5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
   Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2019)
 3.[T][X] join sports club
 4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
