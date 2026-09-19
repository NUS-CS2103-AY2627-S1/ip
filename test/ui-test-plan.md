# UI Test Plan

These tests run the chatbot as a console program.

Deadline behavior is split across these cases:

- `deadline` with no description and no `/by` is rejected with a message naming both missing parts.
- `deadline /by <yyyy-mm-dd>` with no description is rejected with a description-specific message.
- `deadline <description>` without `/by` is rejected with a `/by`-specific message.
- `deadline <description> /by` with no deadline is rejected with a `/by`-specific message.
- `deadline <description> /by <yyyy-mm-dd>` creates a deadline task.

Event behavior is split across these cases:

- `event` with no description, `/from`, and `/to` is rejected with a message naming all three missing parts.
- `event /from <yyyy-mm-dd> /to <yyyy-mm-dd>` with no description is rejected with a description-specific message.
- `event <description> /to <yyyy-mm-dd>` without `/from` is rejected with a `/from`-specific message.
- `event <description> /from <yyyy-mm-dd>` without `/to` is rejected with a `/to`-specific message.
- `event <description>` without `/from` and `/to` is rejected with a message naming both missing parts.
- `event /to <yyyy-mm-dd>` without description and `/from` is rejected with a message naming both missing parts.
- `event /from <yyyy-mm-dd>` without description and `/to` is rejected with a message naming both missing parts.
- `event /from /to <yyyy-mm-dd>` without description and a `/from` value is rejected with a message naming both missing parts.
- `event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>` creates an event task.
- `event <description> /to <yyyy-mm-dd> /from <yyyy-mm-dd>` creates an event task even when `/to` comes before `/from`.

General command behavior:

- `todo` with no description is rejected with a description-specific error.
- `delete <task number>` removes the matching task from the list.
- `delete` without a task number is rejected.
- `find <keyword>` lists tasks whose descriptions contain the keyword.
- Unknown commands ask the user to type `help`.
- `help` lists all accepted inputs and their formats.

Some cases also include `Initial storage:` to write `data/duke.txt` before the
chatbot starts, and `Expected storage:` to verify its final contents after the
console session.

### TC-001 Todo Command Without Description

Aim: Verify that `todo` without a description is rejected with a description-specific error.

Inputs:

```text
todo
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Todo description cannot be empty. Please use the format: todo <description>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-002 Bare Deadline Command Without Description

Aim: Verify that bare `deadline` without a task description and `/by` is rejected with a message naming both missing parts.

Inputs:

```text
deadline
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Deadline description and /by cannot be empty. Please use the format: deadline <description> /by <yyyy-mm-dd>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-003 Bare Event Command Without Description Or Times

Aim: Verify that bare `event` without a description, `/from`, and `/to` values is rejected with a message naming all three missing parts.

Inputs:

```text
event
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event description, /from, and /to cannot be empty. Please use the format: event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-004 Deadline Command With By But Without Description

Aim: Verify that `deadline /by <yyyy-mm-dd>` without a task description is rejected with a description-specific message.

Inputs:

```text
deadline /by sunday
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Deadline description cannot be empty. Please use the format: deadline <description> /by <yyyy-mm-dd>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-005 Deadline Command With Description But Without By Argument

Aim: Verify that `deadline <description>` without `/by` is rejected with a `/by`-specific message.

Inputs:

```text
deadline return book
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Deadline /by cannot be empty. Please use the format: deadline <description> /by <yyyy-mm-dd>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-006 Deadline Command With Description And Blank By Argument

Aim: Verify that `deadline <description> /by` without a deadline is rejected with a `/by`-specific message.

Inputs:

```text
deadline return book /by 
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Deadline /by cannot be empty. Please use the format: deadline <description> /by <yyyy-mm-dd>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-007 Deadline Command With Description And By Argument

Aim: Verify that `deadline <description> /by <yyyy-mm-dd>` creates a deadline task.

Inputs:

```text
deadline return book /by 2019-12-01
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 1 2019)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-008 Event Command With From And To But Without Description

Aim: Verify that `event /from <yyyy-mm-dd> /to <yyyy-mm-dd>` without a description is rejected with a description-specific message.

Inputs:

```text
event /from 2019-12-01 /to 2019-12-02
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event description cannot be empty. Please use the format: event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-008A Deadline Command With Invalid Date

Aim: Verify that `deadline <description> /by <yyyy-mm-dd>` rejects dates that are not in yyyy-mm-dd format.

Inputs:

```text
deadline return book /by Sunday
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Deadline date must be in yyyy-mm-dd format. Please use the format: deadline <description> /by <yyyy-mm-dd>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-009 Event Command Without From Argument

Aim: Verify that `event <description> /to <yyyy-mm-dd>` without `/from` is rejected with a `/from`-specific message.

Inputs:

```text
event meeting /to 2019-12-02
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event /from cannot be empty. Please use the format: event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-010 Event Command Without To Argument

Aim: Verify that `event <description> /from <yyyy-mm-dd>` without `/to` is rejected with a `/to`-specific message.

Inputs:

```text
event meeting /from 2019-12-01
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event /to cannot be empty. Please use the format: event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-011 Event Command With Description But Without From And To Arguments

Aim: Verify that `event <description>` without `/from` and `/to` is rejected with a message naming both missing parts.

Inputs:

```text
event meeting
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event /from and /to cannot be empty. Please use the format: event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-012 Event Command With To But Without Description And From

Aim: Verify that `event /to <yyyy-mm-dd>` without description and `/from` is rejected with a message naming both missing parts.

Inputs:

```text
event /to 2019-12-02
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event description and /from cannot be empty. Please use the format: event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-013 Event Command With From But Without Description And To

Aim: Verify that `event /from <yyyy-mm-dd>` without description and `/to` is rejected with a message naming both missing parts.

Inputs:

```text
event /from 2019-12-01
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event description and /to cannot be empty. Please use the format: event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-014 Event Command With Blank From And To Argument

Aim: Verify that `event /from /to <yyyy-mm-dd>` without description and a `/from` value is rejected with a message naming both missing parts.

Inputs:

```text
event /from /to 3
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event description and /from cannot be empty. Please use the format: event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-015 Event Command With Description, From, And To Arguments

Aim: Verify that `event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>` creates an event task and saves it to disk.

Inputs:

```text
event meeting /from 2019-12-01 /to 2019-12-02
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: Dec 1 2019 to: Dec 2 2019)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

Expected storage:

```text
E | 0 | meeting | 2019-12-01 | 2019-12-02
```

### TC-016 Event Command With Description, To, And From Arguments

Aim: Verify that `event <description> /to <yyyy-mm-dd> /from <yyyy-mm-dd>` creates an event task when `/to` comes before `/from`.

Inputs:

```text
event meeting /to 2019-12-02 /from 2019-12-01
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: Dec 1 2019 to: Dec 2 2019)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-017 Delete Command

Aim: Verify that `delete <task number>` removes the matching task, shifts the remaining tasks, and saves the updated list to disk.

Inputs:

```text
todo read book
todo return book
delete 1
list
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] return book
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

Expected storage:

```text
T | 0 | return book
```

### TC-018 Unknown Command

Aim: Verify that a command that does not match any accepted input asks the user to type `help`.

Inputs:

```text
blah
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Sorry, I don't understand that command. Please type "help".
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-019 Help Command

Aim: Verify that `help` lists all accepted inputs and their formats.

Inputs:

```text
help
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Accepted inputs:
todo <description>
deadline <description> /by <yyyy-mm-dd>
event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
list
mark <task number>
unmark <task number>
delete <task number>
find <keyword>
bye
help
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-020 Delete Command Without Task Number

Aim: Verify that `delete` without a task number is rejected.

Inputs:

```text
delete
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Sorry, I don't understand that task format.
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-021 Mark Command Without Task Number

Aim: Verify that `mark` without a task number is rejected.

Inputs:

```text
mark
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Sorry, I don't understand that task format.
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-022 Unmark Command Without Task Number

Aim: Verify that `unmark` without a task number is rejected.

Inputs:

```text
unmark
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Sorry, I don't understand that task format.
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-023 Load Saved Tasks

Aim: Verify that saved tasks are loaded from disk when the chatbot starts.

Initial storage:

```text
T | 1 | read book
D | 0 | return book | 2019-12-01
E | 0 | meeting | 2019-12-01 | 2019-12-02
```

Inputs:

```text
list
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Dec 1 2019)
3.[E][ ] meeting (from: Dec 1 2019 to: Dec 2 2019)
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-024 Save Task With Storage Delimiters

Aim: Verify that task text containing pipe and backslash characters is escaped when saved to disk.

Inputs:

```text
todo read | book \ notes
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read | book \ notes
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

Expected storage:

```text
T | 0 | read \| book \\ notes
```

### TC-025 Load Task With Escaped Storage Delimiters

Aim: Verify that escaped pipe and backslash characters are restored when saved tasks are loaded.

Initial storage:

```text
T | 1 | read \| book \\ notes
```

Inputs:

```text
list
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read | book \ notes
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-026 Malformed Storage File

Aim: Verify that malformed saved data shows a storage error and starts with an empty task list.

Initial storage:

```text
X | 0 | invalid task
```

Inputs:

```text
list
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
Sorry, I could not load your tasks.
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-027 Find Tasks By Keyword

Aim: Verify that `find <keyword>` lists tasks whose descriptions contain the keyword.

Initial storage:

```text
T | 1 | read book
D | 1 | return book | 2019-12-01
E | 0 | project meeting | 2019-12-01 | 2019-12-02
```

Inputs:

```text
find book
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 1 2019)
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-028 Find Command Without Keyword

Aim: Verify that `find` without a keyword is rejected with a keyword-specific message.

Inputs:

```text
find
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Find keyword cannot be empty. Please use the format: find <keyword>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```
