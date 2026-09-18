# UI Test Plan

Record chatbot console UI tests here for the `test-ui` skill.

## Test Case: greet-and-exit
Aim:
Verify that the chatbot starts, shows the greeting, and exits cleanly when the user enters `bye`.

Inputs:
```text
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: archive-and-restore-all-tasks
Aim:
Verify that archiving all tasks moves them to the archive and restoring all
tasks returns them to the active list in archive order.

Inputs:
```text
todo read book
deadline return book /by 2026-12-31
archive all
list-archive
unarchive all
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [D][ ] return book (by: Dec 31 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Archived 2 tasks.
 Your task list is now empty.
____________________________________________________________
____________________________________________________________
 Here are your archived tasks:
 1. [T][ ] read book
 2. [D][ ] return book (by: Dec 31 2026)
____________________________________________________________
____________________________________________________________
 Restored 2 tasks.
 Your archive is now empty.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] read book
 2. [D][ ] return book (by: Dec 31 2026)
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: archive-all-with-no-tasks
Aim:
Verify that archiving an empty active list reports a friendly message.

Inputs:
```text
archive all
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 There are no tasks to archive.
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: find-matching-tasks
Aim:
Verify that find displays tasks whose descriptions contain the requested keyword.

Inputs:
```text
find book
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1. [T][X] read book
 2. [D][ ] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

Initial Data:
```text
T | 1 | read book
D | 0 | return book | 2019-06-06
T | 0 | write notes
```

## Test Case: reject-empty-todo
Aim:
Verify that the chatbot shows a friendly error when the user enters a todo without a description.

Inputs:
```text
todo
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 That presents a complication: The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: reject-unknown-command
Aim:
Verify that the chatbot rejects unsupported commands without crashing.

Inputs:
```text
blah
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 That presents a complication: I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: reject-invalid-deadline-format
Aim:
Verify that the chatbot groups malformed deadline commands into one format error.

Inputs:
```text
deadline return book
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 That presents a complication: Use this format: deadline <description> /by <yyyy-MM-dd date>
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: add-mark-unmark-task
Aim:
Verify that the chatbot can add a task and update its completion status.

Inputs:
```text
todo read book
mark 1
unmark 1
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Confirmed. I've marked this task as done:
 [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've marked this task as not done yet:
 [T][ ] read book
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: invalid-mark-does-not-change-state
Aim:
Verify that an invalid mark command does not corrupt the existing task list and that later valid commands still work.

Inputs:
```text
todo read book
mark two
list
mark 1
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 That presents a complication: Please enter a valid task number.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] read book
____________________________________________________________
____________________________________________________________
 Confirmed. I've marked this task as done:
 [T][X] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][X] read book
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: invalid-add-does-not-change-state
Aim:
Verify that malformed task-creation commands do not add tasks and do not affect later valid additions.

Inputs:
```text
todo revise notes
event /from Monday 2pm /to Monday 3pm
list
deadline submit report /by 2019-10-15
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [T][ ] revise notes
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 That presents a complication: Use this format: event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm>
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] revise notes
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [D][ ] submit report (by: Oct 15 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] revise notes
 2. [D][ ] submit report (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: reject-command-prefix-collision
Aim:
Verify that inputs which only share a command prefix are rejected instead of being parsed as valid commands.

Inputs:
```text
todo read chapter
todoagain
deadlinereport /by Sunday
eventmeeting /from Mon 1pm /to 2pm
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [T][ ] read chapter
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 That presents a complication: I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 That presents a complication: I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 That presents a complication: I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] read chapter
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: delete-task-and-check-state
Aim:
Verify that deleting a valid task removes only that task and that the remaining tasks keep the correct order.

Inputs:
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 14:00 /to 2019-08-06 16:00
list
delete 2
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [D][ ] return book (by: Jun 06 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 06 2019 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] read book
 2. [D][ ] return book (by: Jun 06 2019)
 3. [E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 06 2019 16:00)
____________________________________________________________
____________________________________________________________
 Confirmed. I've removed this task:
   [D][ ] return book (by: Jun 06 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] read book
 2. [E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 06 2019 16:00)
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: invalid-delete-does-not-change-state
Aim:
Verify that an invalid delete command leaves the task list unchanged and that a later valid delete still works.

Inputs:
```text
todo borrow book
todo return notes
delete two
list
delete 1
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [T][ ] return notes
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 That presents a complication: Please enter a valid task number.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] borrow book
 2. [T][ ] return notes
____________________________________________________________
____________________________________________________________
 Confirmed. I've removed this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] return notes
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: out-of-range-delete-on-empty-and-nonempty-list
Aim:
Verify that out-of-range delete commands fail safely both before and after valid additions.

Inputs:
```text
delete 1
todo plan trip
delete 2
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 That presents a complication: Please enter a valid task number.
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [T][ ] plan trip
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 That presents a complication: Please enter a valid task number.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] plan trip
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: invalid-unmark-does-not-change-state
Aim:
Verify that an invalid unmark command does not affect an already completed task.

Inputs:
```text
todo submit quiz
mark 1
unmark 5
list
unmark 1
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [T][ ] submit quiz
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Confirmed. I've marked this task as done:
 [T][X] submit quiz
____________________________________________________________
____________________________________________________________
 That presents a complication: Please enter a valid task number.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][X] submit quiz
____________________________________________________________
____________________________________________________________
 Noted. I've marked this task as not done yet:
 [T][ ] submit quiz
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] submit quiz
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: save-task-list-after-changes
Aim:
Exercise successful add and mark changes so the resulting data file can be checked for the latest task state.

Inputs:
```text
todo read book
mark 1
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Understood. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Confirmed. I've marked this task as done:
 [T][X] read book
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: load-saved-task-list
Aim:
Verify that todos, deadlines, and events are loaded with their saved completion statuses when Friday starts.

Inputs:
```text
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][X] read book
 2. [D][ ] return book (by: Jun 06 2019)
 3. [E][X] project meeting (from: Aug 06 2019 14:00 to: Aug 06 2019 16:00)
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

Initial Data:
```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 1 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

## Test Case: start-without-data-file
Aim:
Verify that Friday starts with an empty task list when the data file and its folder do not exist yet.

Inputs:
```text
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

## Test Case: skip-corrupted-task-data
Aim:
Verify that Friday skips malformed saved records, explains each problem, and still loads valid records.

Inputs:
```text
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 That presents a complication: I skipped corrupted task data on line 2: completion status must be 0 or 1.
____________________________________________________________
____________________________________________________________
 That presents a complication: I skipped corrupted task data on line 3: unknown task type 'X'.
____________________________________________________________
____________________________________________________________
 That presents a complication: I skipped corrupted task data on line 4: wrong number of fields for task type 'E'.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][X] valid todo
 2. [D][ ] valid deadline (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

Initial Data:
```text
T | 1 | valid todo
D | 2 | bad status | Friday
X | 0 | unknown task
E | 0 | missing end | 2pm
D | 0 | valid deadline | 2019-06-06
```

## Test Case: load-escaped-task-fields
Aim:
Verify that pipe and backslash characters inside saved task fields are not mistaken for field separators.

Inputs:
```text
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] compare A | B in C:\temp
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```

Initial Data:
```text
T | 0 | compare A \| B in C:\\temp
```

## Test Case: reject-invalid-date-values
Aim:
Verify that impossible calendar dates and times are rejected instead of being silently normalized.

Inputs:
```text
deadline return book /by 2019-02-29
event project meeting /from 2019-02-30 14:00 /to 2019-03-01 16:00
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Good day, Mr Stark. I am FRIDAY.
What can I assist you with?
____________________________________________________________
____________________________________________________________
 That presents a complication: Use this format: deadline <description> /by <yyyy-MM-dd date>
____________________________________________________________
____________________________________________________________
 That presents a complication: Use this format: event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm>
____________________________________________________________
____________________________________________________________
 Standing by.
____________________________________________________________
```
