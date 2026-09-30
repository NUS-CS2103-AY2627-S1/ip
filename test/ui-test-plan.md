# Console UI Test Plan

## Test Case: Exit greeting

**Aim:** Verify that the packaged `marcus.Marcus` entry point starts successfully, displays its initial prompt, and exits politely when the user enters `bye`.

### Input
```text
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Enforce command boundaries and task-number ranges

**Aim:** Verify that commands are case-sensitive, command-name prefixes are not accepted, blank input is handled, and zero, negative, and out-of-range task numbers cannot change a valid task.

### Input
```text

LIST
listing
todo alpha
mark 0
mark -1
mark 2
unmark 0
delete -1
delete 2
list
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Please enter a command.
____________________________________________________________
____________________________________________________________
     What do you mean by "LIST", please enter a valid command
____________________________________________________________
____________________________________________________________
     What do you mean by "listing", please enter a valid command
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [T][ ] alpha
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     That task number does not exist.
____________________________________________________________
____________________________________________________________
     That task number does not exist.
____________________________________________________________
____________________________________________________________
     That task number does not exist.
____________________________________________________________
____________________________________________________________
     That task number does not exist.
____________________________________________________________
____________________________________________________________
     That task number does not exist.
____________________________________________________________
____________________________________________________________
     That task number does not exist.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] alpha
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected Saved Tasks
```text
T | 0 | alpha
```

## Test Case: Find a multi-day event at its boundaries

**Aim:** Verify that `find` includes a dated event on its start date, intermediate dates, and end date, but excludes dates immediately outside that range.

### Input
```text
event conference /from 2019-10-15 23:00 /to 2019-10-17 01:00
find 2019-10-14
find 2019-10-15
find 2019-10-16
find 2019-10-17
find 2019-10-18
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [E][ ] conference (from: 2019-10-15 23:00 to: 2019-10-17 01:00)
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks occurring on Oct 14 2019:
     There are no tasks occurring on Oct 14 2019.
____________________________________________________________
____________________________________________________________
     Here are the tasks occurring on Oct 15 2019:
     1.[E][ ] conference (from: 2019-10-15 23:00 to: 2019-10-17 01:00)
____________________________________________________________
____________________________________________________________
     Here are the tasks occurring on Oct 16 2019:
     1.[E][ ] conference (from: 2019-10-15 23:00 to: 2019-10-17 01:00)
____________________________________________________________
____________________________________________________________
     Here are the tasks occurring on Oct 17 2019:
     1.[E][ ] conference (from: 2019-10-15 23:00 to: 2019-10-17 01:00)
____________________________________________________________
____________________________________________________________
     Here are the tasks occurring on Oct 18 2019:
     There are no tasks occurring on Oct 18 2019.
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected Saved Tasks
```text
E | 0 | conference | 2019-10-15 23:00 | 2019-10-17 01:00
```

## Test Case: Packaged end-to-end command, task-list, and storage workflow

**Aim:** Verify that the packaged parser, task, UI, and storage classes work together to load, interpret, add, mark, delete, search, list, and save tasks in one session.

### Initial Saved Tasks
```text
T | 0 | prepare slides
D | 0 | submit report | 2019-10-15
E | 0 | team meeting | 2019-10-15 14:00 | 2019-10-15 16:00
```

### Input
```text
todo buy bread
mark 1
delete 2
event review /from 2019-10-16 09:00 /to 2019-10-16 10:00
find 2019-10-15
list
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [T][ ] buy bread
     Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] prepare slides
____________________________________________________________
____________________________________________________________
     Noted. I've removed this task:
       [D][ ] submit report (by: Oct 15 2019)
     Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [E][ ] review (from: 2019-10-16 09:00 to: 2019-10-16 10:00)
     Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks occurring on Oct 15 2019:
     2.[E][ ] team meeting (from: 2019-10-15 14:00 to: 2019-10-15 16:00)
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] prepare slides
     2.[E][ ] team meeting (from: 2019-10-15 14:00 to: 2019-10-15 16:00)
     3.[T][ ] buy bread
     4.[E][ ] review (from: 2019-10-16 09:00 to: 2019-10-16 10:00)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected Saved Tasks
```text
T | 1 | prepare slides
E | 0 | team meeting | 2019-10-15 14:00 | 2019-10-15 16:00
T | 0 | buy bread
E | 0 | review | 2019-10-16 09:00 | 2019-10-16 10:00
```

## Test Case: Add, mark, unmark, and list typed tasks

**Aim:** Verify that each complete command is recognised by its command type, its remaining text is parsed as task details, and the resulting typed tasks can be marked, unmarked, and listed.

### Input
```text
todo read book
deadline return book /by 2019-10-15
event project meeting /from Mon 2pm /to 4pm
mark 1
list
unmark 1
list
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Oct 15 2019)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: Oct 15 2019)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] read book
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Oct 15 2019)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected Saved Tasks
```text
T | 0 | read book
D | 0 | return book | 2019-10-15
E | 0 | project meeting | Mon 2pm | 4pm
```

## Test Case: Reject every invalid command form

**Aim:** Verify every current error-response path: incomplete task commands, unknown commands, missing status-command arguments, non-numeric task numbers, and task numbers outside the list.

### Input
```text
todo
deadline whats up
deadline return book /by 2019-02-30
event run
banana
mark
mark two
mark 1
unmark
unmark two
unmark 1
todo invalid | task
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Please enter task with todo, eg. todo go for a run
____________________________________________________________
____________________________________________________________
     Please enter a deadline date in yyyy-mm-dd format, eg. deadline return book /by 2019-10-15
____________________________________________________________
____________________________________________________________
     Please enter a deadline date in yyyy-mm-dd format, eg. deadline return book /by 2019-10-15
____________________________________________________________
____________________________________________________________
     Please enter task with event, eg. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
     What do you mean by "banana", please enter a valid command
____________________________________________________________
____________________________________________________________
     Please provide a task number to mark.
____________________________________________________________
____________________________________________________________
     Please provide a task number to mark.
____________________________________________________________
____________________________________________________________
     That task number does not exist.
____________________________________________________________
____________________________________________________________
     Please provide a task number to unmark.
____________________________________________________________
____________________________________________________________
     Please provide a task number to unmark.
____________________________________________________________
____________________________________________________________
     That task number does not exist.
____________________________________________________________
____________________________________________________________
     Task details cannot contain the | character.
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Delete a task

**Aim:** Verify that `delete <number>` removes the selected task, reports it correctly, and keeps the remaining task numbers contiguous.

### Input
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
mark 1
mark 2
list
delete 3
list
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
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
     Nice! I've marked this task as done:
       [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
     3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
     Noted. I've removed this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected Saved Tasks
```text
T | 1 | read book
D | 1 | return book | 2019-06-06
```

## Test Case: Delete from the middle, first, and final positions

**Aim:** Verify that deletion shifts later tasks into the correct position, retains their status, and supports deleting until the list is empty.

### Input
```text
todo first
deadline second /by 2019-10-18
event third /from 2pm /to 3pm
delete 2
list
mark 2
delete 1
list
delete 1
list
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [T][ ] first
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [D][ ] second (by: Oct 18 2019)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [E][ ] third (from: 2pm to: 3pm)
     Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Noted. I've removed this task:
       [D][ ] second (by: Oct 18 2019)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] first
     2.[E][ ] third (from: 2pm to: 3pm)
____________________________________________________________
____________________________________________________________
     Nice! I've marked this task as done:
       [E][X] third (from: 2pm to: 3pm)
____________________________________________________________
____________________________________________________________
     Noted. I've removed this task:
       [T][ ] first
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[E][X] third (from: 2pm to: 3pm)
____________________________________________________________
____________________________________________________________
     Noted. I've removed this task:
       [E][X] third (from: 2pm to: 3pm)
     Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject invalid delete commands

**Aim:** Verify that delete commands with a missing, non-numeric, or out-of-range task number are rejected without changing the task list.

### Input
```text
delete
delete two
delete 1
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Please provide a task number to delete.
____________________________________________________________
____________________________________________________________
     Please provide a task number to delete.
____________________________________________________________
____________________________________________________________
     That task number does not exist.
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Load saved tasks at startup

**Aim:** Verify that `marcus.storage.Storage` restores a saved to-do, deadline, and event with their completion status and timing details when Marcus starts.

### Initial Saved Tasks
```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 1 | project meeting | Aug 6th 2pm | 4pm
```

### Input
```text
list
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: Jun 06 2019)
     3.[E][X] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected Saved Tasks
```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 1 | project meeting | Aug 6th 2pm | 4pm
```

## Test Case: Skip malformed saved task records

**Aim:** Verify that invalid saved lines are reported and skipped while valid records continue to load.

### Initial Saved Tasks
```text
T | 1 | read book
D | 2 | invalid status
E | 0 | missing fields
X | 0 | unknown type
D | 0 | return book | 2019-10-15
```

### Input
```text
list
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
     Skipped invalid saved task: D | 2 | invalid status
     Skipped invalid saved task: E | 0 | missing fields
     Skipped invalid saved task: X | 0 | unknown type
____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected Saved Tasks
```text
T | 1 | read book
D | 2 | invalid status
E | 0 | missing fields
X | 0 | unknown type
D | 0 | return book | 2019-10-15
```

## Test Case: Find tasks by date

**Aim:** Verify that `find yyyy-mm-dd` lists matching deadlines and events with ISO date-times, keeps their original list numbers, and handles no matches and invalid dates.

### Initial Saved Tasks
```text
T | 0 | buy bread
D | 1 | return book | 2019-10-15
D | 0 | submit report | 2019-10-16
E | 0 | team meeting | 2019-10-15 14:00 | 2019-10-15 16:00
D | 0 | return book | 2019-02-12
```

### Input
```text
find 2019-10-15
find 2019-02-12
find 2019-02-19
find 2019-19-02
find tomorrow
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Here are the tasks occurring on Oct 15 2019:
     2.[D][X] return book (by: Oct 15 2019)
     4.[E][ ] team meeting (from: 2019-10-15 14:00 to: 2019-10-15 16:00)
____________________________________________________________
____________________________________________________________
     Here are the tasks occurring on Feb 12 2019:
     5.[D][ ] return book (by: Feb 12 2019)
____________________________________________________________
____________________________________________________________
     Here are the tasks occurring on Feb 19 2019:
     There are no tasks occurring on Feb 19 2019.
____________________________________________________________
____________________________________________________________
     Please provide a keyword or a date in yyyy-mm-dd format, eg. find book or find 2019-10-15
____________________________________________________________
____________________________________________________________
     Here are the matching tasks in your list:
     There are no matching tasks in your list.
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Find tasks by description keyword

**Aim:** Verify that `find <keyword>` searches task descriptions across all task types, matches partial text without case sensitivity, retains original list numbers, and reports when no tasks match.

### Initial Saved Tasks
```text
T | 1 | read book
D | 1 | return book | 2019-06-06
E | 0 | book club meeting | Monday | Tuesday
T | 0 | buy bread
```

### Input
```text
find BoOk
find report
find
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
     3.[E][ ] book club meeting (from: Monday to: Tuesday)
____________________________________________________________
____________________________________________________________
     Here are the matching tasks in your list:
     There are no matching tasks in your list.
____________________________________________________________
____________________________________________________________
     Please provide a keyword or a date in yyyy-mm-dd format, eg. find book or find 2019-10-15
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected Saved Tasks
```text
T | 1 | read book
D | 1 | return book | 2019-06-06
E | 0 | book club meeting | Monday | Tuesday
T | 0 | buy bread
```

## Test Case: Add, search, and remove task tags

**Aim:** Verify that tags can be added and removed, are displayed separately
from descriptions, are stored in lowercase without duplicates, and can be
searched with `find #tag` and `list #tag`.

### Initial Saved Tasks
```text
T | 0 | watch a movie
```

### Input
```text
tag 1 #Fun #weekend #fun
find #fun
find movie
list #weekend
untag 1 #fun
find #fun
untag 1 all
list #weekend
tag 1 invalid tag
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Added tags #fun #weekend to:
       [T][ ] watch a movie #fun #weekend
____________________________________________________________
____________________________________________________________
     Here are the tasks tagged #fun:
     1.[T][ ] watch a movie #fun #weekend
____________________________________________________________
____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][ ] watch a movie
____________________________________________________________
____________________________________________________________
     Here are the tasks tagged #weekend:
     1.[T][ ] watch a movie #fun #weekend
____________________________________________________________
____________________________________________________________
     Removed tag #fun from:
       [T][ ] watch a movie #weekend
____________________________________________________________
____________________________________________________________
     Here are the tasks tagged #fun:
     There are no tasks tagged #fun.
____________________________________________________________
____________________________________________________________
     Removed all tags from:
       [T][ ] watch a movie
____________________________________________________________
____________________________________________________________
     Here are the tasks tagged #weekend:
     There are no tasks tagged #weekend.
____________________________________________________________
____________________________________________________________
     Tag invalid, please try another tag
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected Saved Tasks
```text
T | 0 | watch a movie
```

## Test Case: Load tagged tasks across task types

**Aim:** Verify that tagged to-dos, deadlines, and events load from storage,
retain their tags in normal lists, and can be found with tag searches.

### Initial Saved Tasks
```text
T | 0 | watch a movie | #fun #weekend
D | 1 | submit report | 2026-09-15 | #school
E | 0 | team meeting | Monday | Tuesday | #work
```

### Input
```text
list
find #school
list #work
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] watch a movie #fun #weekend
     2.[D][X] submit report #school (by: Sep 15 2026)
     3.[E][ ] team meeting #work (from: Monday to: Tuesday)
____________________________________________________________
____________________________________________________________
     Here are the tasks tagged #school:
     2.[D][X] submit report #school (by: Sep 15 2026)
____________________________________________________________
____________________________________________________________
     Here are the tasks tagged #work:
     3.[E][ ] team meeting #work (from: Monday to: Tuesday)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected Saved Tasks
```text
T | 0 | watch a movie | #fun #weekend
D | 1 | submit report | 2026-09-15 | #school
E | 0 | team meeting | Monday | Tuesday | #work
```
