# Clarry UI test plan

Run each case as a new Clarry session. Before each case, remove `data/clarry.txt` so saved tasks from a previous session do not affect the result. Expected output includes the startup banner and all responses in the session.

## Help command

Aim: Verify that help lists every supported command, does not add tasks, explains empty lists/searches/dates, and rejects extra arguments.

### Inputs

```text
help
list
find shells
on 2019-10-16
help extra
bye
```

### Expected output

```text
  _____ _
 / ____| |
| |    | | __ _ _ __ _ __ _   _
| |    | |/ _` | '__| '__| | | |
| |____| | (_| | |  | |  | |_| |
 \_____|_|\__,_|_|  |_|  \__,  |
                          __/  |
                         |____/

____________________________________________________________
 Hi, I'm Clarry, your little task shark!
 What are we tackling today?
____________________________________________________________
____________________________________________________________
 Need a course to follow? Here are my commands:
 help - show this help message
 list - show all tasks
 todo DESCRIPTION - add a todo
 deadline DESCRIPTION /by YYYY-MM-DD - add a deadline
 event DESCRIPTION /from YYYY-MM-DD HH:mm /to YYYY-MM-DD HH:mm - add an event
 on YYYY-MM-DD - show tasks occurring on a date
 find KEYWORD - find tasks by description
 mark NUMBER - mark a task as done
 unmark NUMBER - mark a task as not done
 delete NUMBER - delete a task
 bye - exit Clarry
____________________________________________________________
____________________________________________________________
 Your radar is clear! Add a task with 'todo DESCRIPTION'.
____________________________________________________________
____________________________________________________________
 Nothing spotted! Try another keyword.
____________________________________________________________
____________________________________________________________
 Clear waters! No tasks on 2019-10-16.
____________________________________________________________
____________________________________________________________
 I'm a little lost! Type 'help' to see what I understand.
____________________________________________________________
____________________________________________________________
 See you next tide! You've got this.
____________________________________________________________
```

## Task types, completion status, and listing

Aim: Verify task-type display, completion updates, deletion, and renumbering of the remaining tasks.

### Inputs

```text
todo read book
deadline submit assignment /by 2019-10-15
event project meeting /from 2019-10-15 14:00 /to 2019-10-16 16:00
mark 2
unmark 2
list
find BOOK
on 2019-10-16
delete 2
list
bye
```

### Expected output

```text
  _____ _
 / ____| |
| |    | | __ _ _ __ _ __ _   _
| |    | |/ _` | '__| '__| | | |
| |____| | (_| | |  | |  | |_| |
 \_____|_|\__,_|_|  |_|  \__,  |
                          __/  |
                         |____/

____________________________________________________________
 Hi, I'm Clarry, your little task shark!
 What are we tackling today?
____________________________________________________________
____________________________________________________________
 Got it! Safely aboard your task list:
   [T][ ] read book
 You have 1 task aboard.
____________________________________________________________
____________________________________________________________
 Got it! Safely aboard your task list:
   [D][ ] submit assignment (by: Oct 15 2019)
 You have 2 tasks aboard.
____________________________________________________________
____________________________________________________________
 Got it! Safely aboard your task list:
   [E][ ] project meeting (from: Oct 15 2019 14:00 to: Oct 16 2019 16:00)
 You have 3 tasks aboard.
____________________________________________________________
____________________________________________________________
 Fin-tastic! One less thing to tackle:
   [D][X] submit assignment (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
 Back on your radar:
   [D][ ] submit assignment (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
 Here's what's on your radar:
 1.[T][ ] read book
 2.[D][ ] submit assignment (by: Oct 15 2019)
 3.[E][ ] project meeting (from: Oct 15 2019 14:00 to: Oct 16 2019 16:00)
____________________________________________________________
____________________________________________________________
 Here's what I spotted:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Here's what's on your radar for 2019-10-16:
 1.[E][ ] project meeting (from: Oct 15 2019 14:00 to: Oct 16 2019 16:00)
____________________________________________________________
____________________________________________________________
 All clear! I've removed this task:
   [D][ ] submit assignment (by: Oct 15 2019)
 You have 2 tasks aboard.
____________________________________________________________
____________________________________________________________
 Here's what's on your radar:
 1.[T][ ] read book
 2.[E][ ] project meeting (from: Oct 15 2019 14:00 to: Oct 16 2019 16:00)
____________________________________________________________
____________________________________________________________
 See you next tide! You've got this.
____________________________________________________________
```

## Invalid commands and task details

Aim: Verify that invalid commands and incomplete task details show an error message and that Clarry continues accepting commands.

### Inputs

```text
todo
deadline return book
deadline return book /by Friday
event meeting /from 2pm
event meeting /from 2019-02-29 14:00 /to 2019-02-29 16:00
event meeting /from 2019-10-15 14:00 /to 2019-10-15 13:00
on Friday
mark
mark abc
mark 1
delete
delete 1
find
nonsense
bye
```

### Expected output

```text
  _____ _
 / ____| |
| |    | | __ _ _ __ _ __ _   _
| |    | |/ _` | '__| '__| | | |
| |____| | (_| | |  | |  | |_| |
 \_____|_|\__,_|_|  |_|  \__,  |
                          __/  |
                         |____/

____________________________________________________________
 Hi, I'm Clarry, your little task shark!
 What are we tackling today?
____________________________________________________________
____________________________________________________________
 The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
 A deadline needs a '/by' date, e.g. deadline return book /by 2019-10-15
____________________________________________________________
____________________________________________________________
 Please use yyyy-mm-dd for the deadline date, e.g. 2019-10-15.
____________________________________________________________
____________________________________________________________
 An event needs a description, '/from', and '/to' date and time.
____________________________________________________________
____________________________________________________________
 Please use yyyy-mm-dd HH:mm for event dates, e.g. 2019-10-15 14:00.
____________________________________________________________
____________________________________________________________
 An event must end after it starts.
____________________________________________________________
____________________________________________________________
 Please use yyyy-mm-dd for the date, e.g. on 2019-10-15.
____________________________________________________________
____________________________________________________________
 Please specify which task number to mark.
____________________________________________________________
____________________________________________________________
 Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 That task number doesn't exist. Type 'list' to check your tasks.
____________________________________________________________
____________________________________________________________
 Please specify which task number to delete.
____________________________________________________________
____________________________________________________________
 That task number doesn't exist. Type 'list' to check your tasks.
____________________________________________________________
____________________________________________________________
 Please specify a keyword to find.
____________________________________________________________
____________________________________________________________
 I'm a little lost! Type 'help' to see what I understand.
____________________________________________________________
____________________________________________________________
 See you next tide! You've got this.
____________________________________________________________
```

## Whitespace and additional validation

Aim: Accept leading and repeated spaces and reject duplicates, repeated parameters, unsafe descriptions, invalid indices, and zero-length events without altering tasks.

### Inputs

```text
  todo   pack sunscreen
todo pack sunscreen
deadline book /by 2026-09-15 /by 2026-09-16
event swim /from 2026-09-15 10:00 /to 2026-09-15 10:00
todo a | b
mark 999999999999999999999
delete +1
  mark   1
  list
  bye
```

### Expected output

```text
  _____ _
 / ____| |
| |    | | __ _ _ __ _ __ _   _
| |    | |/ _` | '__| '__| | | |
| |____| | (_| | |  | |  | |_| |
 \_____|_|\__,_|_|  |_|  \__,  |
                          __/  |
                         |____/

____________________________________________________________
 Hi, I'm Clarry, your little task shark!
 What are we tackling today?
____________________________________________________________
____________________________________________________________
 Got it! Safely aboard your task list:
   [T][ ] pack sunscreen
 You have 1 task aboard.
____________________________________________________________
____________________________________________________________
 That task is already aboard! Type 'list' to find it.
____________________________________________________________
____________________________________________________________
 Please specify '/by' only once.
____________________________________________________________
____________________________________________________________
 An event must end after it starts.
____________________________________________________________
____________________________________________________________
 Task descriptions cannot contain '|'; please use another character.
____________________________________________________________
____________________________________________________________
 Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 Fin-tastic! One less thing to tackle:
   [T][X] pack sunscreen
____________________________________________________________
____________________________________________________________
 Here's what's on your radar:
 1.[T][X] pack sunscreen
____________________________________________________________
____________________________________________________________
 See you next tide! You've got this.
____________________________________________________________
```

## End of console input

Aim: Exit cleanly when console input ends without a bye command.

### Inputs

```text
list
```

### Expected output

```text
  _____ _
 / ____| |
| |    | | __ _ _ __ _ __ _   _
| |    | |/ _` | '__| '__| | | |
| |____| | (_| | |  | |  | |_| |
 \_____|_|\__,_|_|  |_|  \__,  |
                          __/  |
                         |____/

____________________________________________________________
 Hi, I'm Clarry, your little task shark!
 What are we tackling today?
____________________________________________________________
____________________________________________________________
 Your radar is clear! Add a task with 'todo DESCRIPTION'.
____________________________________________________________
____________________________________________________________
 See you next tide! You've got this.
____________________________________________________________
```
