# Console UI test plan

## Task types, completion, and listing

Aim: Verify that each task type can be added, marked or unmarked, and displayed with its type-specific details.

### Input
```text
todo borrow book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
mark 2
unmark 2
list
bye
```

### Expected output
```text
    ____________________________________________________________
     _____
    | ____|___
    |  _| / __|
    | |___\__ \
    |_____|___/
    Hello! I'm Es.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
    Got it. I've added this task:
      [T][ ] borrow book
    Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Got it. I've added this task:
      [D][ ] return book (by: Jun 6 2019)
    Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Got it. I've added this task:
      [E][ ] project meeting (from: Aug 6 2019 2:00 pm to: Aug 6 2019 4:00 pm)
    Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Nice! I've marked this task as done:
      [D][X] return book (by: Jun 6 2019)
    ____________________________________________________________
    ____________________________________________________________
    OK, I've marked this task as not done yet:
      [D][ ] return book (by: Jun 6 2019)
    ____________________________________________________________
    ____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Jun 6 2019)
    3.[E][ ] project meeting (from: Aug 6 2019 2:00 pm to: Aug 6 2019 4:00 pm)
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Malformed deadline and event markers

Aim: Verify that /by, /from, and /to are recognised only as standalone command markers.

### Input
```text
deadline submit report /byFriday
event project /fromMon /to4pm
bye
```

### Expected output
```text
    ____________________________________________________________
     _____
    | ____|___
    |  _| / __|
    | |___\__ \
    |_____|___/
    Hello! I'm Es.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! A deadline must include /by followed by a date or time.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! An event must include /from and /to times.
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Deleting a task

Aim: Verify that deleting a task reports the removed task and renumbers the remaining list.

### Input
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
delete 2
list
bye
```

### Expected output
```text
    ____________________________________________________________
     _____
    | ____|___
    |  _| / __|
    | |___\__ \
    |_____|___/
    Hello! I'm Es.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
    Got it. I've added this task:
      [T][ ] read book
    Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Got it. I've added this task:
      [D][ ] return book (by: Jun 6 2019)
    Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Got it. I've added this task:
      [E][ ] project meeting (from: Aug 6 2019 2:00 pm to: Aug 6 2019 4:00 pm)
    Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Noted. I've removed this task:
      [D][ ] return book (by: Jun 6 2019)
    Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[E][ ] project meeting (from: Aug 6 2019 2:00 pm to: Aug 6 2019 4:00 pm)
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________
```

## Invalid commands and missing task details

Aim: Verify that malformed task commands, empty required fields, invalid task numbers, blank input, and unknown commands show clear errors without changing the task list.

### Input
```text
todo
deadline submit report
deadline /by Sunday
deadline submit report /by
event project
event /from Mon /to Tue
event project /from /to Tue
event project /from Mon /to
event project /to Tue /from Mon
mark
mark abc
mark 1
unmark
unmark abc
unmark 1
delete
delete abc
delete 1

blah
bye
```

### Expected output
```text
    ____________________________________________________________
     _____
    | ____|___
    |  _| / __|
    | |___\__ \
    |_____|___/
    Hello! I'm Es.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! The description of a todo cannot be empty.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! A deadline must include /by followed by a date or time.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! The description of a deadline cannot be empty.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! The deadline cannot be empty.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! An event must include /from and /to times.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! The description of an event cannot be empty.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! The start time of an event cannot be empty.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! The end time of an event cannot be empty.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! The /from time must come before the /to time.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! Please provide a task number to mark.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! The task number to mark must be a positive whole number.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! There is no task with that number.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! Please provide a task number to unmark.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! The task number to unmark must be a positive whole number.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! There is no task with that number.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! Please provide a task number to delete.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! The task number to delete must be a positive whole number.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! There is no task with that number.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! Please enter a command.
    ____________________________________________________________
    ____________________________________________________________
    OOPS!!! I'm sorry, but I don't know what that means :-(
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________
```



