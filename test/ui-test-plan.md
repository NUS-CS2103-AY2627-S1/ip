# Console UI Test Plan

This file is the source of truth for `$test-ui`. Run each case in a fresh program process and stop the full test session at the first failure.
Unless a case provides initial data-file contents, ensure `data/henry.txt` does not exist before starting its process.
When a case specifies that `data/henry.txt` is a directory, create an empty directory at that path
instead of a file.

## Test case template

### UI-N: Short descriptive name

**Aim:** Describe the behavior this case verifies.

| Step | Input |
| --- | --- |
| 1 | `command` |
| 2 | `bye` |

#### Expected startup output

```text
Paste the exact output printed before the first input here.
```

#### Expected output after step 1

```text
Paste the exact output caused by step 1 here.
```

#### Expected output after step 2

```text
Paste the exact output caused by step 2 here.
```

Use `<empty input>` when the user presses Enter without typing any characters. Use `<no output>` as the entire expected block when a step should produce no output. Preserve all other whitespace exactly.

### UI-1: Add and list all task types

**Aim:** Verify that todos, deadlines, and events are stored and displayed using their type-specific formats.

| Step | Input |
| --- | --- |
| 1 | `todo borrow book` |
| 2 | `deadline do homework /by 2/12/2019 1800` |
| 3 | `event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600` |
| 4 | `list` |
| 5 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hey, I'm Henry. What are we tackling today?
____________________________________________________________
```

#### Expected output after step 1

```text
Got it. I've added this to our route:
[T][ ] borrow book
You now have 1 task on the list.
____________________________________________________________
```

#### Expected output after step 2

```text
Got it. I've added this to our route:
[D][ ] do homework (by: Dec 2 2019 6:00 PM)
You now have 2 tasks on the list.
____________________________________________________________
```

#### Expected output after step 3

```text
Got it. I've added this to our route:
[E][ ] project meeting (from: Dec 2 2019 2:00 PM to: Dec 2 2019 4:00 PM)
You now have 3 tasks on the list.
____________________________________________________________
```

#### Expected output after step 4

```text
Here's what's ahead:
1. [T][ ] borrow book
2. [D][ ] do homework (by: Dec 2 2019 6:00 PM)
3. [E][ ] project meeting (from: Dec 2 2019 2:00 PM to: Dec 2 2019 4:00 PM)
____________________________________________________________
```

#### Expected output after step 5

```text
That's all for now. Take care out there.
____________________________________________________________
```

### UI-2: Reject malformed task additions

**Aim:** Verify specific errors for todos, deadlines, and events with missing required information without terminating Henry.

| Step | Input |
| --- | --- |
| 1 | `todo` |
| 2 | `deadline homework` |
| 3 | `deadline /by Friday` |
| 4 | `deadline homework /by` |
| 5 | `event meeting` |
| 6 | `event meeting /from 2pm` |
| 7 | `event /from 2pm /to 3pm` |
| 8 | `event meeting /from /to 3pm` |
| 9 | `event meeting /from 2pm /to` |
| 10 | `event meeting /from tomorrow /to later` |
| 11 | `event meeting /from 31/4/2025 1400 /to 1/5/2025 1500` |
| 12 | `event meeting /from 2/12/2019 1500 /to 2/12/2019 1400` |
| 13 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hey, I'm Henry. What are we tackling today?
____________________________________________________________
```

#### Expected output after step 1

```text
I'll need a description for that. For example: todo borrow a book
____________________________________________________________
```

#### Expected output after step 2

```text
A deadline needs '/by'. For example: deadline submit report /by 2019-12-02
____________________________________________________________
```

#### Expected output after step 3

```text
A deadline needs a description before '/by'.
____________________________________________________________
```

#### Expected output after step 4

```text
A deadline needs a date or time after '/by'.
____________________________________________________________
```

#### Expected output after step 5

```text
An event needs '/from' and '/to'. For example: event meeting /from 2/12/2019 1400 /to 2/12/2019 1500
____________________________________________________________
```

#### Expected output after step 6

```text
An event needs an ending time introduced by '/to'.
____________________________________________________________
```

#### Expected output after step 7

```text
An event needs a description before '/from'.
____________________________________________________________
```

#### Expected output after step 8

```text
An event needs a starting time after '/from'.
____________________________________________________________
```

#### Expected output after step 9

```text
An event needs an ending time after '/to'.
____________________________________________________________
```

#### Expected output after step 10

```text
Please use an event date like 2/12/2019 1400 or 2019-12-02.
____________________________________________________________
```

#### Expected output after step 11

```text
That date is incorrect. Please enter a valid calendar date.
____________________________________________________________
```

#### Expected output after step 12

```text
An event's ending time must be after its starting time.
____________________________________________________________
```

#### Expected output after step 13

```text
That's all for now. Take care out there.
____________________________________________________________
```

### UI-3: Validate task numbers and empty-list commands

**Aim:** Verify empty-list output and task-number errors for both mark and unmark commands.

| Step | Input |
| --- | --- |
| 1 | `list` |
| 2 | `mark` |
| 3 | `unmark` |
| 4 | `mark first` |
| 5 | `mark 1` |
| 6 | `unmark 1` |
| 7 | `todo borrow book` |
| 8 | `mark 0` |
| 9 | `mark 2` |
| 10 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hey, I'm Henry. What are we tackling today?
____________________________________________________________
```

#### Expected output after step 1

```text
Here's what's ahead:
____________________________________________________________
```

#### Expected output after step 2

```text
I'll need a task number for that. For example: mark 1
____________________________________________________________
```

#### Expected output after step 3

```text
I'll need a task number for that. For example: unmark 1
____________________________________________________________
```

#### Expected output after step 4

```text
'first' isn't a valid task number.
____________________________________________________________
```

#### Expected output after step 5

```text
There aren't any tasks to mark yet.
____________________________________________________________
```

#### Expected output after step 6

```text
There aren't any tasks to unmark yet.
____________________________________________________________
```

#### Expected output after step 7

```text
Got it. I've added this to our route:
[T][ ] borrow book
You now have 1 task on the list.
____________________________________________________________
```

#### Expected output after step 8

```text
I can't find task 0. Choose a number from 1 to 1.
____________________________________________________________
```

#### Expected output after step 9

```text
I can't find task 2. Choose a number from 1 to 1.
____________________________________________________________
```

#### Expected output after step 10

```text
That's all for now. Take care out there.
____________________________________________________________
```

### UI-4: Preserve state across invalid commands

**Aim:** Verify that a rejected addition and task update do not change existing tasks or completion state.

| Step | Input |
| --- | --- |
| 1 | `todo read book` |
| 2 | `deadline submit report /by 2019-12-02` |
| 3 | `mark 2` |
| 4 | `deadline missing date /by` |
| 5 | `unmark 3` |
| 6 | `list` |
| 7 | `unmark 2` |
| 8 | `list` |
| 9 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hey, I'm Henry. What are we tackling today?
____________________________________________________________
```

#### Expected output after step 1

```text
Got it. I've added this to our route:
[T][ ] read book
You now have 1 task on the list.
____________________________________________________________
```

#### Expected output after step 2

```text
Got it. I've added this to our route:
[D][ ] submit report (by: Dec 2 2019 12:00 AM)
You now have 2 tasks on the list.
____________________________________________________________
```

#### Expected output after step 3

```text
Nice, that one's done.
[D][X] submit report (by: Dec 2 2019 12:00 AM)
____________________________________________________________
```

#### Expected output after step 4

```text
A deadline needs a date or time after '/by'.
____________________________________________________________
```

#### Expected output after step 5

```text
I can't find task 3. Choose a number from 1 to 2.
____________________________________________________________
```

#### Expected output after step 6

```text
Here's what's ahead:
1. [T][ ] read book
2. [D][X] submit report (by: Dec 2 2019 12:00 AM)
____________________________________________________________
```

#### Expected output after step 7

```text
No worries. I've put this back on the trail:
[D][ ] submit report (by: Dec 2 2019 12:00 AM)
____________________________________________________________
```

#### Expected output after step 8

```text
Here's what's ahead:
1. [T][ ] read book
2. [D][ ] submit report (by: Dec 2 2019 12:00 AM)
____________________________________________________________
```

#### Expected output after step 9

```text
That's all for now. Take care out there.
____________________________________________________________
```

### UI-5: Trim input and reject non-command text

**Aim:** Verify surrounding-whitespace trimming, exact command-word matching, blank-input handling,
help output, and preserved task state.

| Step | Input |
| --- | --- |
| 1 | `   todo padded task   ` |
| 2 | `todolist` |
| 3 | `<empty input>` |
| 4 | `help` |
| 5 | `list` |
| 6 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hey, I'm Henry. What are we tackling today?
____________________________________________________________
```

#### Expected output after step 1

```text
Got it. I've added this to our route:
[T][ ] padded task
You now have 1 task on the list.
____________________________________________________________
```

#### Expected output after step 2

```text
I'm not quite sure what you mean. Try todo, deadline, event, list, find, mark, unmark, delete, help, or bye.
____________________________________________________________
```

#### Expected output after step 3

```text
I'm not quite sure what you mean. Try todo, deadline, event, list, find, mark, unmark, delete, help, or bye.
____________________________________________________________
```

#### Expected output after step 4

```text
Here are the commands I can help with:
todo DESCRIPTION - Add a todo.
deadline DESCRIPTION /by DATE - Add a deadline.
event DESCRIPTION /from START_DATE /to END_DATE - Add an event.
list - Show all tasks.
find KEYWORD - Find tasks by description.
mark TASK_NUMBER - Mark a task as completed.
unmark TASK_NUMBER - Mark a task as not completed.
delete TASK_NUMBER - Delete a task.
help - Show this command list.
bye - Exit Henry.
____________________________________________________________
```

#### Expected output after step 5

```text
Here's what's ahead:
1. [T][ ] padded task
____________________________________________________________
```

#### Expected output after step 6

```text
That's all for now. Take care out there.
____________________________________________________________
```

### UI-6: Delete and renumber tasks

**Aim:** Verify delete validation, removal confirmation, task-count updates, list renumbering after deletion,
and the final file representation after add, mark, and delete operations.

| Step | Input |
| --- | --- |
| 1 | `delete` |
| 2 | `delete first` |
| 3 | `delete 1` |
| 4 | `todo read book` |
| 5 | `deadline return book /by 2019-06-06` |
| 6 | `event project meeting /from 6/8/2019 1400 /to 6/8/2019 1600` |
| 7 | `todo join sports club` |
| 8 | `todo borrow book` |
| 9 | `mark 1` |
| 10 | `mark 2` |
| 11 | `mark 4` |
| 12 | `delete 3` |
| 13 | `list` |
| 14 | `delete 0` |
| 15 | `delete 5` |
| 16 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hey, I'm Henry. What are we tackling today?
____________________________________________________________
```

#### Expected output after step 1

```text
I'll need a task number for that. For example: delete 1
____________________________________________________________
```

#### Expected output after step 2

```text
'first' isn't a valid task number.
____________________________________________________________
```

#### Expected output after step 3

```text
There aren't any tasks to delete yet.
____________________________________________________________
```

#### Expected output after step 4

```text
Got it. I've added this to our route:
[T][ ] read book
You now have 1 task on the list.
____________________________________________________________
```

#### Expected output after step 5

```text
Got it. I've added this to our route:
[D][ ] return book (by: Jun 6 2019 12:00 AM)
You now have 2 tasks on the list.
____________________________________________________________
```

#### Expected output after step 6

```text
Got it. I've added this to our route:
[E][ ] project meeting (from: Aug 6 2019 2:00 PM to: Aug 6 2019 4:00 PM)
You now have 3 tasks on the list.
____________________________________________________________
```

#### Expected output after step 7

```text
Got it. I've added this to our route:
[T][ ] join sports club
You now have 4 tasks on the list.
____________________________________________________________
```

#### Expected output after step 8

```text
Got it. I've added this to our route:
[T][ ] borrow book
You now have 5 tasks on the list.
____________________________________________________________
```

#### Expected output after step 9

```text
Nice, that one's done.
[T][X] read book
____________________________________________________________
```

#### Expected output after step 10

```text
Nice, that one's done.
[D][X] return book (by: Jun 6 2019 12:00 AM)
____________________________________________________________
```

#### Expected output after step 11

```text
Nice, that one's done.
[T][X] join sports club
____________________________________________________________
```

#### Expected output after step 12

```text
All right, I've cleared this from the list:
[E][ ] project meeting (from: Aug 6 2019 2:00 PM to: Aug 6 2019 4:00 PM)
You now have 4 tasks on the list.
____________________________________________________________
```

#### Expected output after step 13

```text
Here's what's ahead:
1. [T][X] read book
2. [D][X] return book (by: Jun 6 2019 12:00 AM)
3. [T][X] join sports club
4. [T][ ] borrow book
____________________________________________________________
```

#### Expected output after step 14

```text
I can't find task 0. Choose a number from 1 to 4.
____________________________________________________________
```

#### Expected output after step 15

```text
I can't find task 5. Choose a number from 1 to 4.
____________________________________________________________
```

#### Expected output after step 16

```text
That's all for now. Take care out there.
____________________________________________________________
```

#### Expected `data/henry.txt` after step 16

```text
T | 1 | read book
D | 1 | return book | 2019-06-06T00:00
T | 1 | join sports club
T | 0 | borrow book
```

### UI-7: Load saved tasks on startup

**Aim:** Verify that todos, deadlines, and events, including their completion states, are restored from disk.

#### Initial `data/henry.txt`

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00
E | 1 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

| Step | Input |
| --- | --- |
| 1 | `list` |
| 2 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hey, I'm Henry. What are we tackling today?
____________________________________________________________
```

#### Expected output after step 1

```text
Here's what's ahead:
1. [T][X] read book
2. [D][ ] return book (by: Jun 6 2019 12:00 AM)
3. [E][X] project meeting (from: Aug 6 2019 2:00 PM to: Aug 6 2019 4:00 PM)
____________________________________________________________
```

#### Expected output after step 2

```text
That's all for now. Take care out there.
____________________________________________________________
```

### UI-8: Recover valid tasks from malformed data

**Aim:** Verify that blank lines are ignored, malformed records are reported and skipped, and valid
records, including escaped pipes and backslashes, are still loaded.

#### Initial `data/henry.txt`

```text
T | 1 | compare A \| B

X | 0 | unknown task
D | 2 | invalid status | Friday
E | 0 | missing end time | 2019-12-02T14:00
T | 0 | unexpected | extra field
D | 0 | use C:\\temp | 2019-12-02T18:00
```

| Step | Input |
| --- | --- |
| 1 | `list` |
| 2 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hey, I'm Henry. What are we tackling today?
____________________________________________________________
I skipped 4 malformed task records while loading data/henry.txt.
____________________________________________________________
```

#### Expected output after step 1

```text
Here's what's ahead:
1. [T][X] compare A | B
2. [D][ ] use C:\temp (by: Dec 2 2019 6:00 PM)
____________________________________________________________
```

#### Expected output after step 2

```text
That's all for now. Take care out there.
____________________________________________________________
```

### UI-9: Continue safely when storage is unusable

**Aim:** Verify that a read failure does not terminate Henry and a write failure rolls back the
in-memory task addition. Before startup, create an empty directory named `data/henry.txt`.

| Step | Input |
| --- | --- |
| 1 | `todo task that cannot be saved` |
| 2 | `list` |
| 3 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hey, I'm Henry. What are we tackling today?
____________________________________________________________
I couldn't load your saved tasks, so we're starting with an empty list.
____________________________________________________________
```

#### Expected output after step 1

```text
I couldn't save that change. Your task list is unchanged.
____________________________________________________________
```

#### Expected output after step 2

```text
Here's what's ahead:
____________________________________________________________
```

#### Expected output after step 3

```text
That's all for now. Take care out there.
____________________________________________________________
```

### UI-10: Escape storage separators in user text

**Aim:** Verify that pipes and backslashes in task text are saved without being confused with file
format separators.

| Step | Input |
| --- | --- |
| 1 | `todo compare A | B \ C` |
| 2 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hey, I'm Henry. What are we tackling today?
____________________________________________________________
```

#### Expected output after step 1

```text
Got it. I've added this to our route:
[T][ ] compare A | B \ C
You now have 1 task on the list.
____________________________________________________________
```

#### Expected output after step 2

```text
That's all for now. Take care out there.
____________________________________________________________
```

#### Expected `data/henry.txt` after step 2

```text
T | 0 | compare A \| B \\ C
```

### UI-11: Parse and validate deadline dates

**Aim:** Verify that deadlines are stored as dates and times, displayed in a friendly format, and
distinguish invalid calendar dates from unsupported formats.

| Step | Input |
| --- | --- |
| 1 | `deadline return book /by 2/12/2019 1800` |
| 2 | `deadline impossible date /by 2019-02-29` |
| 3 | `deadline date outside month /by 31/4/2025 1800` |
| 4 | `deadline unsupported date /by tomorrow` |
| 5 | `list` |
| 6 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hey, I'm Henry. What are we tackling today?
____________________________________________________________
```

#### Expected output after step 1

```text
Got it. I've added this to our route:
[D][ ] return book (by: Dec 2 2019 6:00 PM)
You now have 1 task on the list.
____________________________________________________________
```

#### Expected output after step 2

```text
That date is incorrect. Please enter a valid calendar date.
____________________________________________________________
```

#### Expected output after step 3

```text
That date is incorrect. Please enter a valid calendar date.
____________________________________________________________
```

#### Expected output after step 4

```text
Please use a deadline date like 2/12/2019 1800 or 2019-12-02.
____________________________________________________________
```

#### Expected output after step 5

```text
Here's what's ahead:
1. [D][ ] return book (by: Dec 2 2019 6:00 PM)
____________________________________________________________
```

#### Expected output after step 6

```text
That's all for now. Take care out there.
____________________________________________________________
```

#### Expected `data/henry.txt` after step 6

```text
D | 0 | return book | 2019-12-02T18:00
```

### UI-12: Find tasks by description keyword

**Aim:** Verify that find displays only tasks whose descriptions contain the keyword, preserves
their original order, handles no matches, and rejects a missing keyword.

| Step | Input |
| --- | --- |
| 1 | `todo read book` |
| 2 | `deadline return book /by 2019-06-06` |
| 3 | `todo buy groceries` |
| 4 | `mark 1` |
| 5 | `mark 2` |
| 6 | `find book` |
| 7 | `find movie` |
| 8 | `find` |
| 9 | `bye` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hey, I'm Henry. What are we tackling today?
____________________________________________________________
```

#### Expected output after step 1

```text
Got it. I've added this to our route:
[T][ ] read book
You now have 1 task on the list.
____________________________________________________________
```

#### Expected output after step 2

```text
Got it. I've added this to our route:
[D][ ] return book (by: Jun 6 2019 12:00 AM)
You now have 2 tasks on the list.
____________________________________________________________
```

#### Expected output after step 3

```text
Got it. I've added this to our route:
[T][ ] buy groceries
You now have 3 tasks on the list.
____________________________________________________________
```

#### Expected output after step 4

```text
Nice, that one's done.
[T][X] read book
____________________________________________________________
```

#### Expected output after step 5

```text
Nice, that one's done.
[D][X] return book (by: Jun 6 2019 12:00 AM)
____________________________________________________________
```

#### Expected output after step 6

```text
I found these matching tasks:
1. [T][X] read book
2. [D][X] return book (by: Jun 6 2019 12:00 AM)
____________________________________________________________
```

#### Expected output after step 7

```text
I couldn't find a task for that keyword
____________________________________________________________
```

#### Expected output after step 8

```text
I'll need a keyword for that. For example: find book
____________________________________________________________
```

#### Expected output after step 9

```text
That's all for now. Take care out there.
____________________________________________________________
```

### UI-13: Use command aliases

**Aim:** Verify that every fixed alias performs the same operation as its full command.

| Step | Input |
| --- | --- |
| 1 | `t borrow book` |
| 2 | `d return book /by 2019-06-06` |
| 3 | `e project meeting /from 2/12/2019 1400 /to 2/12/2019 1500` |
| 4 | `l` |
| 5 | `m 1` |
| 6 | `u 1` |
| 7 | `f book` |
| 8 | `del 3` |
| 9 | `b` |

#### Expected startup output

```text
____________________________________________________________
 _   _                      
| | | | ___ _ __  _ __ _   _
| |_| |/ _ \ '_ \| '__| | | |
|  _  |  __/ | | | |  | |_| |
|_| |_|\___|_| |_|_|   \__, |
                       |___/ 
Hey, I'm Henry. What are we tackling today?
____________________________________________________________
```

#### Expected output after step 1

```text
Got it. I've added this to our route:
[T][ ] borrow book
You now have 1 task on the list.
____________________________________________________________
```

#### Expected output after step 2

```text
Got it. I've added this to our route:
[D][ ] return book (by: Jun 6 2019 12:00 AM)
You now have 2 tasks on the list.
____________________________________________________________
```

#### Expected output after step 3

```text
Got it. I've added this to our route:
[E][ ] project meeting (from: Dec 2 2019 2:00 PM to: Dec 2 2019 3:00 PM)
You now have 3 tasks on the list.
____________________________________________________________
```

#### Expected output after step 4

```text
Here's what's ahead:
1. [T][ ] borrow book
2. [D][ ] return book (by: Jun 6 2019 12:00 AM)
3. [E][ ] project meeting (from: Dec 2 2019 2:00 PM to: Dec 2 2019 3:00 PM)
____________________________________________________________
```

#### Expected output after step 5

```text
Nice, that one's done.
[T][X] borrow book
____________________________________________________________
```

#### Expected output after step 6

```text
No worries. I've put this back on the trail:
[T][ ] borrow book
____________________________________________________________
```

#### Expected output after step 7

```text
I found these matching tasks:
1. [T][ ] borrow book
2. [D][ ] return book (by: Jun 6 2019 12:00 AM)
____________________________________________________________
```

#### Expected output after step 8

```text
All right, I've cleared this from the list:
[E][ ] project meeting (from: Dec 2 2019 2:00 PM to: Dec 2 2019 3:00 PM)
You now have 2 tasks on the list.
____________________________________________________________
```

#### Expected output after step 9

```text
That's all for now. Take care out there.
____________________________________________________________
```

#### Expected `data/henry.txt` after step 9

```text
T | 0 | borrow book
D | 0 | return book | 2019-06-06T00:00
```
