# UI Test Plan

## Test Case: Exit On Bye

Aim: Verify that the chatbot greets the user and exits when the user enters `bye`.

Inputs:
```text
bye
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
What can I do for you?
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Add And List ToDos

Aim: Verify that `todo` commands are stored as to-do tasks and displayed when `list` is entered.

Inputs:
```text
todo read book
todo return book
list
bye
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Add Typed Tasks

Aim: Verify that `todo`, `deadline`, and `event` commands create tasks with the correct type icons and details.

Inputs:
```text
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
list
bye
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Dec 2 2019 2:00 PM to: Dec 2 2019 4:00 PM)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Dec 2 2019 2:00 PM to: Dec 2 2019 4:00 PM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Mark And Unmark Tasks

Aim: Verify that `mark` and `unmark` update task done status in subsequent `list` output.

Inputs:
```text
todo read book
todo return book
todo buy bread
mark 1
mark 2
list
unmark 2
list
bye
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy bread
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[T][X] return book
3.[T][ ] buy bread
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[T][ ] return book
3.[T][ ] buy bread
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Find Tasks By Description

Aim: Verify that `find` displays tasks whose descriptions contain the keyword, without matching date/time fields.

Inputs:
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
find book
find 2019
bye
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
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
  [E][ ] project meeting (from: Aug 6 2019 2:00 PM to: Aug 6 2019 4:00 PM)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 6 2019)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Delete Task

Aim: Verify that `delete` removes the specified task and the remaining tasks are renumbered.

Inputs:
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
bye
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
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
  [E][ ] project meeting (from: Aug 6 2019 2:00 PM to: Aug 6 2019 4:00 PM)
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
  [D][X] return book (by: Jun 6 2019)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 6 2019)
3.[E][ ] project meeting (from: Aug 6 2019 2:00 PM to: Aug 6 2019 4:00 PM)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 6 2019 2:00 PM to: Aug 6 2019 4:00 PM)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 6 2019)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Save Tasks To File

Aim: Verify that the chatbot writes the current task list to `data/duke.txt` after task list changes.

Inputs:
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo join sports club
mark 1
delete 2
bye
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
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
  [E][ ] project meeting (from: Aug 6 2019 2:00 PM to: Aug 6 2019 4:00 PM)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [D][ ] return book (by: Jun 6 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Expected file `data/duke.txt`:
```text
T | 1 | read book
E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600
T | 0 | join sports club
```

## Test Case: Load Tasks From File

Aim: Verify that the chatbot loads tasks from `data/duke.txt` when it starts.

Inputs:
```text
list
bye
```

Initial file `data/duke.txt`:
```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600
T | 1 | join sports club
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2019)
3.[E][ ] project meeting (from: Aug 6 2019 2:00 PM to: Aug 6 2019 4:00 PM)
4.[T][X] join sports club
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Expected file `data/duke.txt`:
```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600
T | 1 | join sports club
```

## Test Case: Handle Malformed Save File

Aim: Verify that malformed saved data is reported without crashing the chatbot.

Inputs:
```text
list
bye
```

Initial file `data/duke.txt`:
```text
D | 1 | return book | Sunday
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
What can I do for you?
____________________________________________________________
OOPS!!! I could not load the task list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Handle Invalid Commands

Aim: Verify that invalid user inputs are handled with chatbot-specific error messages without ending the session.

Inputs:
```text
todo
blah
deadline return book
deadline invalid date /by Sunday
event meeting /from Mon 2pm
find
delete 1
mark 1
bye
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
OOPS!!! The by date/time of a deadline cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! Date/time must be in yyyy-MM-dd, yyyy-MM-dd HHmm, d/M/yyyy, or d/M/yyyy HHmm format.
____________________________________________________________
____________________________________________________________
OOPS!!! The end date/time of an event cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! The keyword of a find command cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number is not in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number is not in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Handle Edge Case Inputs

Aim: Verify that blank commands, bad task numbers, and unsupported save-file delimiters are handled without crashing.

Inputs:
```text
   
todo visit | park
mark
mark abc
todo valid task
delete 99
unmark 0
   list   
   bye   
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! Please enter a command.
____________________________________________________________
____________________________________________________________
OOPS!!! Task details cannot contain " | ".
____________________________________________________________
____________________________________________________________
OOPS!!! Please specify a task number after mark.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number must be a valid number.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] valid task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number is not in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number is not in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] valid task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Expected file `data/duke.txt`:
```text
T | 0 | valid task
```

## Test Case: Manage Clients

Aim: Verify that clients can be added, listed, edited, cleared, and deleted.

Inputs:
```text
client add Alice Tan /phone +65 9123 4567 /email alice@example.com
client add Bob Lee /email bob@example.com
client list
client edit 1 /name Alice Lim /phone -
client list
client delete 2
client list
bye
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this client:
  Alice Tan [phone: +65 9123 4567] [email: alice@example.com]
Now you have 1 client in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this client:
  Bob Lee [phone: -] [email: bob@example.com]
Now you have 2 clients in the list.
____________________________________________________________
____________________________________________________________
Here are the clients in your list:
1.Alice Tan [phone: +65 9123 4567] [email: alice@example.com]
2.Bob Lee [phone: -] [email: bob@example.com]
____________________________________________________________
____________________________________________________________
Got it. I've updated this client:
  Alice Lim [phone: -] [email: alice@example.com]
____________________________________________________________
____________________________________________________________
Here are the clients in your list:
1.Alice Lim [phone: -] [email: alice@example.com]
2.Bob Lee [phone: -] [email: bob@example.com]
____________________________________________________________
____________________________________________________________
Noted. I've removed this client:
  Bob Lee [phone: -] [email: bob@example.com]
Now you have 1 client in the list.
____________________________________________________________
____________________________________________________________
Here are the clients in your list:
1.Alice Lim [phone: -] [email: alice@example.com]
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Expected file `data/clients.txt`:
```text
C | Alice Lim |  | alice@example.com
```

## Test Case: Load Clients From File

Aim: Verify that saved clients load independently from the task file.

Initial file `data/clients.txt`:
```text
C | Alice Tan | +65 9123 4567 | alice@example.com
C | Bob Lee |  | bob@example.com
```

Inputs:
```text
client list
bye
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the clients in your list:
1.Alice Tan [phone: +65 9123 4567] [email: alice@example.com]
2.Bob Lee [phone: -] [email: bob@example.com]
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Expected file `data/clients.txt`:
```text
C | Alice Tan | +65 9123 4567 | alice@example.com
C | Bob Lee |  | bob@example.com
```

## Test Case: Handle Invalid Client Commands

Aim: Verify that malformed client commands report specific errors without ending the session.

Inputs:
```text
client
client add
client add Alice /phone
client add Alice | Tan
client list extra
client add Alice
client edit 1
client edit 1 /fax 123
client edit 1 /phone 123 /phone 456
client edit 1 /name -
client delete abc
client delete 2
bye
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! Please specify a client command.
____________________________________________________________
____________________________________________________________
OOPS!!! The name of a client cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! The phone of a client cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! Client details cannot contain " | ".
____________________________________________________________
____________________________________________________________
OOPS!!! The client list command does not accept additional arguments.
____________________________________________________________
____________________________________________________________
Got it. I've added this client:
  Alice [phone: -] [email: -]
Now you have 1 client in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! Please specify at least one client field to edit.
____________________________________________________________
____________________________________________________________
OOPS!!! Unknown client field: /fax.
____________________________________________________________
____________________________________________________________
OOPS!!! Client field /phone cannot be specified more than once.
____________________________________________________________
____________________________________________________________
OOPS!!! The name of a client cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! The client number must be a valid number.
____________________________________________________________
____________________________________________________________
OOPS!!! The client number is not in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Expected file `data/clients.txt`:
```text
C | Alice |  |
```

## Test Case: Handle Ambiguous And Malformed Parameters

Aim: Verify that flexible whitespace is accepted while ambiguous parameters and malformed values are rejected.

Inputs:
```text
   todo    spaced   task
deadline report /by 2026-10-01 /by 2026-10-02
event meeting /from 2026-10-02 /to 2026-10-01
list extra
bye now
mark 1 2
client add Alice /phone call-me
client add Bob /email not-an-email
client delete 1 2
list
bye
```

Expected output:
```text
____________________________________________________________
  ____                            _        _
 / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__
| |   / _ \| '_ ` _ \| '_ \| | | | __/ _` | '_ \
| |__| (_) | | | | | | |_) | |_| | || (_| | | | |
 \____\___/|_| |_| |_| .__/ \__,_|\__\__,_|_| |_|
                     |_|

Hello! I'm Computah.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] spaced task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! The /by parameter can only be specified once.
____________________________________________________________
____________________________________________________________
OOPS!!! The end date/time of an event cannot be before its start date/time.
____________________________________________________________
____________________________________________________________
OOPS!!! The list command does not accept additional arguments.
____________________________________________________________
____________________________________________________________
OOPS!!! The bye command does not accept additional arguments.
____________________________________________________________
____________________________________________________________
OOPS!!! Please specify exactly one task number after mark.
____________________________________________________________
____________________________________________________________
OOPS!!! The phone of a client may contain only digits, spaces, +, -, and parentheses.
____________________________________________________________
____________________________________________________________
OOPS!!! The email of a client must be in the format name@example.com.
____________________________________________________________
____________________________________________________________
OOPS!!! Please specify exactly one client number.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] spaced task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Expected file `data/duke.txt`:
```text
T | 0 | spaced task
```
