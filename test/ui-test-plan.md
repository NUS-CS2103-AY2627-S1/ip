# Vector Text UI Test Plan

- Setup command: `javac -d _temp/ui-test-classes src/main/java/vector/Vector.java src/main/java/vector/VectorException.java src/main/java/vector/ui/*.java src/main/java/vector/storage/*.java src/main/java/vector/task/*.java src/main/java/vector/parser/*.java src/main/java/vector/command/*.java`
- Program command: `java -cp _temp/ui-test-classes vector.Vector --clear-data`
- Timeout seconds: `10`

The runner starts a fresh Vector process for each case. Output is compared
exactly after normalising line endings and ignoring one final line ending.
Positive and negative/edge cases are interleaved so that each state-changing
operation is followed by a check of the resulting task state.

## Test case 1: Start and exit
- Aim: Verify that Vector displays its greeting and exits cleanly when the user enters `bye`.

### Inputs
```text
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 2: Reject an invalid task index
- Aim: Verify that an out-of-range task index is rejected without creating or corrupting task state.

### Inputs
```text
mark 0
list
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     SYSTEM FAULT: That task number does not exist in your list.
    ____________________________________________________________
    ____________________________________________________________
     Your task list is empty.
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 3: Reject an unknown command without changing state
- Aim: Verify that an unknown command is rejected and does not add an extra task to the list.

### Inputs
```text
todo read book
what is this
list
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [T][ ] read book
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     SYSTEM FAULT: I don't recognize that command. Valid commands are: todo, deadline, event, list, mark, unmark, delete, schedule, find, help, bye.
    ____________________________________________________________
    ____________________________________________________________
     Accessing database... Current task matrix:
     1.[T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 4: Unmark an incomplete task
- Aim: Verify that an invalid state transition does not mark an incomplete todo as done or change the task count.

### Inputs
```text
todo read book
unmark 1
list
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [T][ ] read book
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Task execution reverted. Status updated to: PENDING:
       [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Accessing database... Current task matrix:
     1.[T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 5: Mark and unmark a task
- Aim: Verify that `mark` changes the task status to done and `unmark` changes it back to not done.

### Inputs
```text
todo submit report
mark 1
unmark 1
list
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [T][ ] submit report
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Task execution verified. Status updated to: COMPLETE:
       [T][X] submit report
    ____________________________________________________________
    ____________________________________________________________
     Task execution reverted. Status updated to: PENDING:
       [T][ ] submit report
    ____________________________________________________________
    ____________________________________________________________
     Accessing database... Current task matrix:
     1.[T][ ] submit report
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 6: Mark an already completed task
- Aim: Verify that repeating `mark` keeps the task done and does not duplicate the task.

### Inputs
```text
todo read book
mark 1
mark 1
list
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [T][ ] read book
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Task execution verified. Status updated to: COMPLETE:
       [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Task execution verified. Status updated to: COMPLETE:
       [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Accessing database... Current task matrix:
     1.[T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 7: Add and list a todo
- Aim: Verify that the `todo` command creates a todo task and displays its type and incomplete status.

### Inputs
```text
todo read book
list
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [T][ ] read book
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Accessing database... Current task matrix:
     1.[T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 8: Reject a todo without a description
- Aim: Verify that a malformed todo command is rejected and leaves the existing task list unchanged.

### Inputs
```text
todo read book
todo
list
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [T][ ] read book
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     SYSTEM FAULT: A todo task must have a description. Please try again.
    ____________________________________________________________
    ____________________________________________________________
     Accessing database... Current task matrix:
     1.[T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 9: Add and list a deadline
- Aim: Verify that the `deadline` command records and displays the deadline time.

### Inputs
```text
deadline submit report /by 2019-10-15 1800
list
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [D][ ] submit report (by: Oct 15 2019, 6:00 PM)
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Accessing database... Current task matrix:
     1.[D][ ] submit report (by: Oct 15 2019, 6:00 PM)
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 10: Reject a malformed deadline
- Aim: Verify that a deadline without a `/by` separator is rejected and does not alter an existing deadline.

### Inputs
```text
deadline submit report /by 2019-10-15 1800
deadline invalid
list
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [D][ ] submit report (by: Oct 15 2019, 6:00 PM)
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     SYSTEM FAULT: The deadline format is incorrect. Use: deadline <task> /by <date/time>
    ____________________________________________________________
    ____________________________________________________________
     Accessing database... Current task matrix:
     1.[D][ ] submit report (by: Oct 15 2019, 6:00 PM)
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 11: Add and list an event
- Aim: Verify that the `event` command records and displays the event time range.

### Inputs
```text
event team meeting /from 2019-10-14 /to 2019-10-15 1800
list
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [E][ ] team meeting (from: Oct 14 2019, 12:00 am to: Oct 15 2019, 6:00 PM)
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Accessing database... Current task matrix:
     1.[E][ ] team meeting (from: Oct 14 2019, 12:00 am to: Oct 15 2019, 6:00 PM)
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 12: Reject an incomplete event
- Aim: Verify that an event without a `/to` time is rejected and does not alter an existing event.

### Inputs
```text
event team meeting /from 2019-10-14 /to 2019-10-15 1800
event planning /from 2019-10-14
list
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [E][ ] team meeting (from: Oct 14 2019, 12:00 am to: Oct 15 2019, 6:00 PM)
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     SYSTEM FAULT: The event format is incorrect. Use: event <task> /from <start> /to <end>
    ____________________________________________________________
    ____________________________________________________________
     Accessing database... Current task matrix:
     1.[E][ ] team meeting (from: Oct 14 2019, 12:00 am to: Oct 15 2019, 6:00 PM)
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 13: Delete a task
- Aim: Verify that a task can be deleted successfully and the count updates correctly.

### Inputs
```text
todo read book
todo return book
delete 2
list
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [T][ ] read book
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [T][ ] return book
     Now you have 2 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Task erased from memory banks:
       [T][ ] return book
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Accessing database... Current task matrix:
     1.[T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 14: Schedule command
- Aim: Verify that schedule finds tasks occurring on a specific date.

### Inputs
```text
deadline assignment /by 2019-12-02 2359
event conference /from 2019-12-01 /to 2019-12-03
schedule 2019-12-02
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [D][ ] assignment (by: Dec 2 2019, 11:59 PM)
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [E][ ] conference (from: Dec 1 2019, 12:00 am to: Dec 3 2019, 12:00 AM)
     Now you have 2 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Scanning schedule for 2019-12-02. Results:
     1.[D][ ] assignment (by: Dec 2 2019, 11:59 PM)
     2.[E][ ] conference (from: Dec 1 2019, 12:00 am to: Dec 3 2019, 12:00 AM)
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 15: Find a task
- Aim: Verify that find matches tasks by keyword in their description.

### Inputs
```text
todo read book
deadline return book /by 2023-06-06 2359
todo buy groceries
mark 1
mark 2
find book
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [T][ ] read book
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [D][ ] return book (by: Jun 6 2023, 11:59 PM)
     Now you have 2 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [T][ ] buy groceries
     Now you have 3 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Task execution verified. Status updated to: COMPLETE:
       [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Task execution verified. Status updated to: COMPLETE:
       [D][X] return book (by: Jun 6 2023, 11:59 PM)
    ____________________________________________________________
    ____________________________________________________________
     Search protocol complete. Query results:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 6 2023, 11:59 PM)
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 16: Help command
- Aim: Verify that the help command lists all available commands.

### Inputs
```text
help
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Displaying available system commands:
     1. todo <description> - Adds a todo task
     2. deadline <description> /by <date/time> - Adds a deadline task
     3. event <description> /from <start> /to <end> - Adds an event
     4. list - Lists all tasks
     5. mark <task_number> - Marks a task as done
     6. unmark <task_number> - Marks a task as not done
     7. delete <task_number> - Deletes a task
     8. find <keyword> - Finds tasks by keyword
     9. schedule <date> - Finds tasks occurring on a date
     10. help - Shows this help message
     11. bye - Exits the application
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 17: Reject duplicate task
- Aim: Verify that adding an identical task is rejected.

### Inputs
```text
todo read book
todo read book
list
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     Data logged. New task added to the matrix:
       [T][ ] read book
     Now you have 1 tasks in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     SYSTEM FAULT: An identical task already exists in the matrix.
    ____________________________________________________________
    ____________________________________________________________
     Accessing database... Current task matrix:
     1.[T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```

## Test case 18: Reject pipe character
- Aim: Verify that the pipe character is rejected to prevent file corruption.

### Inputs
```text
todo read | book
bye
```

### Expected output
```text
    ____________________________________________________________
 __     _______ ____ _____ ___  ____
 \ \   / / ____/ ___|_   _/  _ \|  _ \
  \ \ / /|  _| | |     | || | | | |_) |
   \ V / | |___| |___  | || |_| |  _ <
    \_/  |______\____| |_| \___/|_| \_\
     Initialization complete. I am Vector, your personal cybernetic assistant.
     Awaiting input protocols.
    ____________________________________________________________
    ____________________________________________________________
     SYSTEM FAULT: The character '|' is reserved and cannot be used in tasks.
    ____________________________________________________________
    ____________________________________________________________
     System shutting down. End of line.
    ____________________________________________________________
```