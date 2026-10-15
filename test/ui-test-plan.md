# Chocolate UI test plan

Run each independent test from a clean temporary working directory so saved data from one test
does not affect another test.
Run the packaged application using the `chocolate.Chocolate` main class.

## Test: Find tasks by description keyword

Aim: Verify find displays matching tasks only and matches keywords without case sensitivity.

### Input
```text
todo Read Book
deadline return book /by 2019-10-15
event project meeting /from Mon 2pm /to 4pm
find BOOK
find missing
bye
```

### Expected output
```text
**************************************
Chocolate
Hello! I'm Chocolate, your task chocolatier.
What shall we sweeten up today?
**************************************
**************************************
Sweet! I've added this to your list:
  [T][ ] Read Book
You now have 1 tasks on your tray.
**************************************
**************************************
Sweet! I've added this to your list:
  [D][ ] return book (by: Oct 15 2019)
You now have 2 tasks on your tray.
**************************************
**************************************
Sweet! I've added this to your list:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
You now have 3 tasks on your tray.
**************************************
**************************************
Here are the matching tasks in your list:
1.[T][ ] Read Book
2.[D][ ] return book (by: Oct 15 2019)
**************************************
**************************************
Here are the matching tasks in your list:
**************************************
**************************************
Thanks for visiting Chocolate's Cocoa Corner. See you soon!
**************************************
```

## Test: Recover from flexible spacing, duplicates, and invalid values

Aim: Verify Chocolate normalizes ordinary spacing, rejects invalid additions and date ranges, and preserves the task list after errors.

### Input
```text
  todo   brew cocoa
todo brew cocoa
deadline submit report /by 2019-10-15 /by 2019-10-16
event meeting /from 2019-10-15 /to 2019-10-15
mark 0
list
bye
```

### Expected output
```text
**************************************
Chocolate
Hello! I'm Chocolate, your task chocolatier.
What shall we sweeten up today?
**************************************
**************************************
Sweet! I've added this to your list:
  [T][ ] brew cocoa
You now have 1 tasks on your tray.
**************************************
**************************************
Oops! That crumbled. That task is already on your list!
**************************************
**************************************
Oops! That crumbled. Please use: deadline DESCRIPTION /by yyyy-MM-dd.
**************************************
**************************************
Oops! That crumbled. The event end date must be after its start date.
**************************************
**************************************
Oops! That crumbled. Task number must be at least 1.
**************************************
**************************************
Here are the tasks in your list:
1.[T][ ] brew cocoa
**************************************
**************************************
Thanks for visiting Chocolate's Cocoa Corner. See you soon!
**************************************
```

## Test: Archive all active tasks and list archived tasks

Aim: Verify archive moves every active task out of the list, preserves task details, and rejects unsupported archive arguments.

### Input
```text
archive
todo buy chocolate
deadline submit report /by 2019-10-15
mark 2
archive
list
archive list
archive all
bye
```

### Expected output
```text
**************************************
Chocolate
Hello! I'm Chocolate, your task chocolatier.
What shall we sweeten up today?
**************************************
**************************************
The archive basket is empty.
**************************************
**************************************
Sweet! I've added this to your list:
  [T][ ] buy chocolate
You now have 1 tasks on your tray.
**************************************
**************************************
Sweet! I've added this to your list:
  [D][ ] submit report (by: Oct 15 2019)
You now have 2 tasks on your tray.
**************************************
**************************************
Delicious progress! I've marked this task as done:
  [X] submit report
**************************************
**************************************
Your 2 task(s) are tucked safely into the archive.
**************************************
**************************************
Here are the tasks in your list:
**************************************
**************************************
Here are your archived treats:
1.[T][ ] buy chocolate
2.[D][X] submit report (by: Oct 15 2019)
**************************************
**************************************
Oops! That crumbled. Please use: archive or archive list.
**************************************
**************************************
Thanks for visiting Chocolate's Cocoa Corner. See you soon!
**************************************
```

## Test: Reject malformed commands without terminating

Aim: Verify Parser reports missing command details and Chocolate continues accepting commands.

### Input
```text
mark
delete abc
deadline return book
event meeting /from Monday
bye
```

### Expected output
```text
**************************************
Chocolate
Hello! I'm Chocolate, your task chocolatier.
What shall we sweeten up today?
**************************************
**************************************
Oops! That crumbled. Please provide a task number!
**************************************
**************************************
Oops! That crumbled. Please provide a whole number for the task number!
**************************************
**************************************
Oops! That crumbled. Please use: deadline DESCRIPTION /by yyyy-MM-dd.
**************************************
**************************************
Oops! That crumbled. Please use: event DESCRIPTION /from START /to END.
**************************************
**************************************
Thanks for visiting Chocolate's Cocoa Corner. See you soon!
**************************************
```

## Test: Reject invalid deadline dates

Aim: Verify malformed and impossible dates show an error and do not add tasks.

### Input
```text
deadline return book /by 15-10-2019
deadline submit report /by 2019-02-30
list
bye
```

### Expected output
```text
**************************************
Chocolate
Hello! I'm Chocolate, your task chocolatier.
What shall we sweeten up today?
**************************************
**************************************
Oops! That crumbled. Please use the date format yyyy-MM-dd.
**************************************
**************************************
Oops! That crumbled. Please use the date format yyyy-MM-dd.
**************************************
**************************************
Here are the tasks in your list:
**************************************
**************************************
Thanks for visiting Chocolate's Cocoa Corner. See you soon!
**************************************
```

## Test: Save tasks and restore them in a later session

Aim: Verify task types and completion status are saved automatically and loaded at startup.

### First-session input
```text
todo read book
deadline return book /by 2019-10-15
mark 2
bye
```

### Second-session input
```text
list
bye
```

### Expected second-session output
```text
**************************************
Chocolate
Hello! I'm Chocolate, your task chocolatier.
What shall we sweeten up today?
**************************************
**************************************
Here are the tasks in your list:
1.[T][ ] read book
2.[D][X] return book (by: Oct 15 2019)
**************************************
**************************************
Thanks for visiting Chocolate's Cocoa Corner. See you soon!
**************************************
```

## Test: Add, list, mark, and unmark each task type

Aim: Verify Todo, Deadline, and Event commands preserve their type and done status.

### Input
```text
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from Mon 2pm /to 4pm
list
mark 2
unmark 2
bye
```

### Expected output
```text
**************************************
Chocolate
Hello! I'm Chocolate, your task chocolatier.
What shall we sweeten up today?
**************************************
**************************************
Sweet! I've added this to your list:
  [T][ ] borrow book
You now have 1 tasks on your tray.
**************************************
**************************************
Sweet! I've added this to your list:
  [D][ ] return book (by: Oct 15 2019)
You now have 2 tasks on your tray.
**************************************
**************************************
Sweet! I've added this to your list:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
You now have 3 tasks on your tray.
**************************************
**************************************
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
**************************************
**************************************
Delicious progress! I've marked this task as done:
  [X] return book
**************************************
**************************************
No worries! I've marked this task as not done yet:
  [ ] return book
**************************************
**************************************
Thanks for visiting Chocolate's Cocoa Corner. See you soon!
**************************************
```

## Test: Reject an empty todo without changing the task list

Aim: Verify empty todo commands show an error and do not add a task.

### Input
```text
todo
list
todo read book
list
bye
```

### Expected output
```text
**************************************
Chocolate
Hello! I'm Chocolate, your task chocolatier.
What shall we sweeten up today?
**************************************
**************************************
Oops! That crumbled. Please provide a valid description after the command you used!
**************************************
**************************************
Here are the tasks in your list:
**************************************
**************************************
Sweet! I've added this to your list:
  [T][ ] read book
You now have 1 tasks on your tray.
**************************************
**************************************
Here are the tasks in your list:
1.[T][ ] read book
**************************************
**************************************
Thanks for visiting Chocolate's Cocoa Corner. See you soon!
**************************************
```

## Test: Delete a task without changing the list after an invalid deletion

Aim: Verify deletion removes the chosen task and invalid deletion leaves the list unchanged.

### Input
```text
todo read book
deadline return book /by 2019-10-15
event project meeting /from Mon 2pm /to 4pm
delete 2
list
delete 3
list
bye
```

### Expected output
```text
**************************************
Chocolate
Hello! I'm Chocolate, your task chocolatier.
What shall we sweeten up today?
**************************************
**************************************
Sweet! I've added this to your list:
  [T][ ] read book
You now have 1 tasks on your tray.
**************************************
**************************************
Sweet! I've added this to your list:
  [D][ ] return book (by: Oct 15 2019)
You now have 2 tasks on your tray.
**************************************
**************************************
Sweet! I've added this to your list:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
You now have 3 tasks on your tray.
**************************************
**************************************
Poof! This task has melted away:
  [D][ ] return book (by: Oct 15 2019)
You now have 2 tasks left on your tray.
**************************************
**************************************
Here are the tasks in your list:
1.[T][ ] read book
2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
**************************************
**************************************
Oops! That crumbled. That task number does not exist in your list!
**************************************
**************************************
Here are the tasks in your list:
1.[T][ ] read book
2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
**************************************
**************************************
Thanks for visiting Chocolate's Cocoa Corner. See you soon!
**************************************
```
