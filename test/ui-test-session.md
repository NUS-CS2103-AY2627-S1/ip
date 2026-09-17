# UI Test Session

## UI-01: Add todo, deadline, and event tasks and list their type-specific details

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: todo read book
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [T][ ] read book
         Now you have 1 task in the list.
____________________________________________________________
You: deadline do homework /by no idea :-p
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [D][ ] do homework (by: no idea :-p)
         Now you have 2 tasks in the list.
____________________________________________________________
You: event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [E][ ] project meeting (from: Mon 1400 to: 1600)
         Now you have 3 tasks in the list.
____________________________________________________________
You: list
____________________________________________________________
Crystal: Ta-da!!! Here are all your tasks:
         1.[T][ ] read book
         2.[D][ ] do homework (by: no idea :-p)
         3.[E][ ] project meeting (from: Mon 1400 to: 1600)
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-02: Mark and unmark a task while guarding against repeated status changes

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: todo read book
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [T][ ] read book
         Now you have 1 task in the list.
____________________________________________________________
You: mark 1
____________________________________________________________
Crystal: Yayyy!!! You finished this task:
         [T][X] read book
____________________________________________________________
You: mark 1
____________________________________________________________
Crystal: This task is already done, good job!
         [T][X] read book
____________________________________________________________
You: unmark 1
____________________________________________________________
Crystal: Okkk!!! I've put this task back on your list:
         [T][ ] read book
____________________________________________________________
You: unmark 1
____________________________________________________________
Crystal: This task is already waiting for you!
         [T][ ] read book
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-03: Reject commands that do not begin with a recognized task type

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: read book
____________________________________________________________
Crystal: Oopsies!!! I don't know what that means :-(
____________________________________________________________
You: list
____________________________________________________________
Crystal: Your task list is all clear, well done!
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-04: Report empty, padded, malformed, unsafe, and out-of-range commands as Crystal exceptions

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You:
____________________________________________________________
Crystal: Oopsies!!! I cant reply to nothing...
____________________________________________________________
You:  list
____________________________________________________________
Crystal: Oopsies!!! Please remove spaces from the start or end of your command!
____________________________________________________________
You: list 
____________________________________________________________
Crystal: Oopsies!!! Please remove spaces from the start or end of your command!
____________________________________________________________
You: todo
____________________________________________________________
Crystal: Oopsies!!! A todo must have a description!
____________________________________________________________
You: deadline
____________________________________________________________
Crystal: Oopsies!!! A deadline must have a description and a /by time!
____________________________________________________________
You: event
____________________________________________________________
Crystal: Oopsies!!! An event must have a description, a /from time and a /to time!
____________________________________________________________
You: deadline report /by Friday /by Saturday
____________________________________________________________
Crystal: Oopsies!!! A deadline must have a description and a /by time!
____________________________________________________________
You: event meeting /from Monday /from Tuesday /to Wednesday
____________________________________________________________
Crystal: Oopsies!!! An event must have a description, a /from time and a /to time!
____________________________________________________________
You: todo unsafe | task
____________________________________________________________
Crystal: Oopsies!!! Task details cannot contain ' | '!
____________________________________________________________
You: mark
____________________________________________________________
Crystal: Oopsies!!! You have to mark a task number!
____________________________________________________________
You: mark abc
____________________________________________________________
Crystal: Oopsies!!! You have to mark a task number!
____________________________________________________________
You: unmark
____________________________________________________________
Crystal: Oopsies!!! You have to unmark a task number!
____________________________________________________________
You: delete abc
____________________________________________________________
Crystal: Oopsies!!! You have to delete a task number!
____________________________________________________________
You: mark 1
____________________________________________________________
Crystal: Oopsies!!! That task number does not exist!
____________________________________________________________
You: list
____________________________________________________________
Crystal: Your task list is all clear, well done!
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-05: Delete a task and shift the remaining tasks forward in the list

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: todo read book
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [T][ ] read book
         Now you have 1 task in the list.
____________________________________________________________
You: event project meeting /from Aug 6th 2pm /to 4pm
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [E][ ] project meeting (from: Aug 6th 1400 to: 1600)
         Now you have 2 tasks in the list.
____________________________________________________________
You: todo borrow book
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [T][ ] borrow book
         Now you have 3 tasks in the list.
____________________________________________________________
You: delete 2
____________________________________________________________
Crystal: All gone!!! I've removed this task:
         [E][ ] project meeting (from: Aug 6th 1400 to: 1600)
         Now you have 2 tasks in the list.
____________________________________________________________
You: list
____________________________________________________________
Crystal: Ta-da!!! Here are all your tasks:
         1.[T][ ] read book
         2.[T][ ] borrow book
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-06: Exercise every task format and mutation that must be reflected in the saved data file

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: todo read book
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [T][ ] read book
         Now you have 1 task in the list.
____________________________________________________________
You: deadline return book /by June 6th
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [D][ ] return book (by: June 6th)
         Now you have 2 tasks in the list.
____________________________________________________________
You: event project meeting /from Aug 6th 2pm /to 4pm
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [E][ ] project meeting (from: Aug 6th 1400 to: 1600)
         Now you have 3 tasks in the list.
____________________________________________________________
You: mark 2
____________________________________________________________
Crystal: Yayyy!!! You finished this task:
         [D][X] return book (by: June 6th)
____________________________________________________________
You: todo join sports club
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [T][ ] join sports club
         Now you have 4 tasks in the list.
____________________________________________________________
You: delete 4
____________________________________________________________
Crystal: All gone!!! I've removed this task:
         [T][ ] join sports club
         Now you have 3 tasks in the list.
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-07: Load saved tasks on restart and persist every mutation against that existing list

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: list
____________________________________________________________
Crystal: Ta-da!!! Here are all your tasks:
         1.[T][ ] read book
         2.[D][X] return book (by: 02 Dec 2019 1800)
         3.[E][ ] project meeting (from: Monday 0600 to: 1830)
____________________________________________________________
You: mark 1
____________________________________________________________
Crystal: Yayyy!!! You finished this task:
         [T][X] read book
____________________________________________________________
You: unmark 2
____________________________________________________________
Crystal: Okkk!!! I've put this task back on your list:
         [D][ ] return book (by: 02 Dec 2019 1800)
____________________________________________________________
You: todo join sports club
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [T][ ] join sports club
         Now you have 4 tasks in the list.
____________________________________________________________
You: delete 3
____________________________________________________________
Crystal: All gone!!! I've removed this task:
         [E][ ] project meeting (from: Monday 0600 to: 1830)
         Now you have 3 tasks in the list.
____________________________________________________________
You: list
____________________________________________________________
Crystal: Ta-da!!! Here are all your tasks:
         1.[T][X] read book
         2.[D][ ] return book (by: 02 Dec 2019 1800)
         3.[T][ ] join sports club
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-08: Report corrupted saved data and continue with a safe empty task list

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
Crystal: Oopsies!!! Your saved task data is invalid.
You: list
____________________________________________________________
Crystal: Your task list is all clear, well done!
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-09: Parse and normalize supported calendar dates and 12-hour and 24-hour times

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: deadline return book /by 2/12/2019 1800
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [D][ ] return book (by: 02 Dec 2019 1800)
         Now you have 1 task in the list.
____________________________________________________________
You: deadline submit report /by 2019-10-15
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [D][ ] submit report (by: 15 Oct 2019)
         Now you have 2 tasks in the list.
____________________________________________________________
You: event breakfast /from 6am /to 6.30pm
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [E][ ] breakfast (from: 0600 to: 1830)
         Now you have 3 tasks in the list.
____________________________________________________________
You: event workshop /from 630pm /to Monday
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [E][ ] workshop (from: 1830 to: Monday)
         Now you have 4 tasks in the list.
____________________________________________________________
You: deadline call client /by 18:45
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [D][ ] call client (by: 1845)
         Now you have 5 tasks in the list.
____________________________________________________________
You: list
____________________________________________________________
Crystal: Ta-da!!! Here are all your tasks:
         1.[D][ ] return book (by: 02 Dec 2019 1800)
         2.[D][ ] submit report (by: 15 Oct 2019)
         3.[E][ ] breakfast (from: 0600 to: 1830)
         4.[E][ ] workshop (from: 1830 to: Monday)
         5.[D][ ] call client (by: 1845)
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-10: Parse month-name dates despite missing spaces and common separator variations

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: deadline first release /by 2Oct2026
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [D][ ] first release (by: 02 Oct 2026)
         Now you have 1 task in the list.
____________________________________________________________
You: deadline second release /by 2 Dec2026
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [D][ ] second release (by: 02 Dec 2026)
         Now you have 2 tasks in the list.
____________________________________________________________
You: deadline third release /by 2Nov 2026
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [D][ ] third release (by: 02 Nov 2026)
         Now you have 3 tasks in the list.
____________________________________________________________
You: event launch /from 2nd-Oct-2026 6am /to 3 October 2026 18:30
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [E][ ] launch (from: 02 Oct 2026 0600 to: 03 Oct 2026 1830)
         Now you have 4 tasks in the list.
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-11: Expand two-digit years to the 2000s in numeric and month-name dates

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: deadline numeric date /by 2/12/26
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [D][ ] numeric date (by: 02 Dec 2026)
         Now you have 1 task in the list.
____________________________________________________________
You: deadline compact date /by 2Oct26
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [D][ ] compact date (by: 02 Oct 2026)
         Now you have 2 tasks in the list.
____________________________________________________________
You: deadline partial spacing /by 2 Dec26 630pm
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [D][ ] partial spacing (by: 02 Dec 2026 1830)
         Now you have 3 tasks in the list.
____________________________________________________________
You: deadline spaced date /by 2Nov 26
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [D][ ] spaced date (by: 02 Nov 2026)
         Now you have 4 tasks in the list.
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-12: List unnumbered dated tasks on a date without mutating their stored indexes

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: list /on 2Dec26
____________________________________________________________
Crystal: Yay, here are your dated tasks on 02 Dec 2026:
         - [D][ ] submit report (by: 02 Dec 2026 0900)
         - [E][ ] workshop (from: 02 Dec 2026 0800 to: 02 Dec 2026 1000)
         - [E][ ] conference (from: 01 Dec 2026 to: 03 Dec 2026)
         - [E][ ] overnight trip (from: 01 Dec 2026 to: 02 Dec 2026)
         - [W][ ] collect certificate (from: 01 Dec 2026 to: 03 Dec 2026)
____________________________________________________________
You: list /on 4/12/26
____________________________________________________________
Crystal: No dated tasks on 04 Dec 2026, yay!
____________________________________________________________
You: list /on
____________________________________________________________
Crystal: Oopsies!!! To list tasks on a date, enter 'list /on [date]'!
____________________________________________________________
You: list /on nonsense
____________________________________________________________
Crystal: Oopsies!!! I couldn't understand that date!
____________________________________________________________
You: list /on 31/02/26
____________________________________________________________
Crystal: Oopsies!!! I couldn't understand that date!
____________________________________________________________
You: list
____________________________________________________________
Crystal: Ta-da!!! Here are all your tasks:
         1.[T][ ] mention 02 Dec 2026
         2.[D][ ] submit report (by: 02 Dec 2026 0900)
         3.[D][ ] later deadline (by: 03 Dec 2026)
         4.[E][ ] workshop (from: 02 Dec 2026 0800 to: 02 Dec 2026 1000)
         5.[E][ ] conference (from: 01 Dec 2026 to: 03 Dec 2026)
         6.[E][ ] overnight trip (from: 01 Dec 2026 to: 02 Dec 2026)
         7.[W][ ] collect certificate (from: 01 Dec 2026 to: 03 Dec 2026)
         8.[E][ ] weekly call (from: Monday 0600 to: Tuesday 0700)
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-13: Find tasks by a case-insensitive keyword in their descriptions without changing stored tasks

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: find   BOOK
____________________________________________________________
Crystal: Yay, I found these matching tasks:
         1.[T][X] read book
         2.[D][X] return book (by: June 6th)
____________________________________________________________
You: find homework
____________________________________________________________
Crystal: Aww, I couldn't find any matching tasks!
____________________________________________________________
You: find
____________________________________________________________
Crystal: Oopsies!!! To find tasks, enter 'find [keyword]'!
____________________________________________________________
You: list
____________________________________________________________
Crystal: Ta-da!!! Here are all your tasks:
         1.[T][X] read book
         2.[D][X] return book (by: June 6th)
         3.[E][ ] project meeting (from: book to: Friday)
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-14: Display the complete command guide when the user enters help

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: help
____________________________________________________________
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

## UI-15: Add, normalize, persist, and list tasks with flexible completion periods

```text
____________________________________________________________
  ____ ______   ______ _____  _    _
 / ___|  _ \ \ / / ___|_   _|/ \  | |
| |   | |_) \ V /\___ \ | | / _ \ | |
| |___|  _ < | |  ___) || |/ ___ \| |___
 \____|_| \_\|_| |____/ |_/_/   \_\_____|

Hiii!!! I'm Crystal, your sparkly task bestie!
[Crystal's command guide:
- To add a todo, enter 'todo [description]'
- To add a deadline, enter 'deadline [description] /by [deadline]'
- To add an event, enter 'event [description] /from [start] /to [end]'
- To add a within-period task, enter 'within [description] /from [start] /to [end]'
- To view your task list, enter 'list'
- To view dated tasks on a date, enter 'list /on [date]'
- To find tasks by description, enter 'find [keyword]'
- To mark a task as done, enter 'mark [task number]'
- To mark a task as not done, enter 'unmark [task number]'
- To delete a task, enter 'delete [task number]'
- To view this command guide, enter 'help'
- To exit, enter 'bye']
____________________________________________________________
You: within collect certificate /from 15Jan27 /to 25 January 2027
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [W][ ] collect certificate (from: 15 Jan 2027 to: 25 Jan 2027)
         Now you have 1 task in the list.
____________________________________________________________
You: list /on 20Jan27
____________________________________________________________
Crystal: Yay, here are your dated tasks on 20 Jan 2027:
         - [W][ ] collect certificate (from: 15 Jan 2027 to: 25 Jan 2027)
____________________________________________________________
You: within reversed period /from 25Jan27 /to 15Jan27
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [W][ ] reversed period (from: 25 Jan 2027 to: 15 Jan 2027)
         Now you have 2 tasks in the list.
____________________________________________________________
You: within flexible period /from 31Feb27 /to someday
____________________________________________________________
Crystal: Okkk!!! Here's your new task!!
         [W][ ] flexible period (from: 31Feb27 to: someday)
         Now you have 3 tasks in the list.
____________________________________________________________
You: bye
____________________________________________________________
Crystal: Byeee!!! You did amazing today. See you soon!
____________________________________________________________
```

Result: PASS (15 test cases)
