# UI Test Plan for GUNNA Chatbot

This file contains test cases for the GUNNA chatbot user interface.

## Manual GUI Checks

These checks cover the JavaFX layout, which is not exercised by the console-output test runner below.

1. Launch the GUI and enter `todo read a long chapter title that should wrap neatly in the chat bubble`.
   Expected: the command is right-aligned in a blue bubble without an avatar; Gunna's reply is left-aligned
   beside a compact avatar. Neither bubble uses more than roughly three quarters of the chat width.
2. Enter `unknown-command`.
   Expected: Gunna's existing explanatory error text is preserved in a left-aligned bubble labelled `Error`
   with a distinct border and background.
3. Resize the window wider and narrower, staying above the minimum size.
   Expected: the chat area and input field resize with the window, the Send button stays visible, and messages
   rewrap without a horizontal scrollbar.

## Test Case 1: Add Todo Task

**Aim:** Test that a todo task can be added successfully

**Input:**
```
todo borrow book
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] borrow book
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 2: Add Deadline Task

**Aim:** Test that a deadline task can be added successfully

**Input:**
```
deadline return book /by 2024-12-15
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [D][ ] return book (by: Dec 15 2024)
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[D][ ] return book (by: Dec 15 2024)
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 3: Add Event Task

**Aim:** Test that an event task can be added successfully

**Input:**
```
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 4: Mark Task as Done

**Aim:** Test that a task can be marked as done

**Input:**
```
todo read book
mark 1
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] read book
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Marked complete:
       [T][X] read book
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[T][X] read book
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 5: Unmark Task

**Aim:** Test that a task can be unmarked (marked as not done)

**Input:**
```
todo read book
mark 1
unmark 1
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] read book
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Marked complete:
       [T][X] read book
____________________________________________________________
____________________________________________________________
     Returned to queue:
       [T][ ] read book
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[T][ ] read book
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 6: Multiple Tasks with Different Types

**Aim:** Test adding multiple tasks of different types and listing them

**Input:**
```
todo buy groceries
deadline submit assignment /by 2024-12-20
event team meeting /from 2pm /to 3pm
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] buy groceries
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [D][ ] submit assignment (by: Dec 20 2024)
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [E][ ] team meeting (from: 2pm to: 3pm)
     Queue now holds 3 task(s).
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[T][ ] buy groceries
     2.[D][ ] submit assignment (by: Dec 20 2024)
     3.[E][ ] team meeting (from: 2pm to: 3pm)
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 7: Deadline with Invalid Format

**Aim:** Test error handling for deadline without /by delimiter

**Input:**
```
deadline return book
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Format check.
     Usage: deadline <description> /by <time>
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 8: Event with Invalid Format

**Aim:** Test error handling for event without proper delimiters

**Input:**
```
event meeting
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Format check.
     Usage: event <description> /from <time> /to <time>
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 9: Empty Todo Description

**Aim:** Test error handling for todo without description

**Input:**
```
todo
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task details are required for a todo.
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 10: Unknown Command

**Aim:** Test error handling for unknown commands

**Input:**
```
blah
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Command not recognized. Check the format and try again.
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 11: Invalid Mark Number

**Aim:** Test error handling for marking non-existent task

**Input:**
```
todo read book
mark 5
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] read book
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task number 5 is not in the queue.
     Queue contains 1 task(s).
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 12: Empty Deadline Description

**Aim:** Test error handling for deadline with empty description

**Input:**
```
deadline  /by tomorrow
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task details are required for a deadline.
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 13: Mark with Non-Numeric Input

**Aim:** Test error handling for mark with non-numeric input

**Input:**
```
todo read book
mark abc
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] read book
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task number must be a whole number.
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 14: Empty Event Description

**Aim:** Test error handling for event with empty description

**Input:**
```
event  /from 2pm /to 4pm
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task details are required for an event.
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 15: Interleaved Error and Success - State Integrity

**Aim:** Test that errors don't corrupt internal state; task count should remain correct

**Input:**
```
todo
todo valid task
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task details are required for a todo.
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] valid task
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[T][ ] valid task
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 16: Mark with Zero

**Aim:** Test error handling for marking task 0

**Input:**
```
todo read book
mark 0
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] read book
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task number 0 is not in the queue.
     Queue contains 1 task(s).
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 17: Mark with Negative Number

**Aim:** Test error handling for marking with negative task number

**Input:**
```
todo read book
mark -1
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] read book
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task number -1 is not in the queue.
     Queue contains 1 task(s).
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 18: Multiple Errors Then Success

**Aim:** Test that multiple errors don't affect subsequent successful operations

**Input:**
```
blah
todo
deadline return book
todo buy groceries
deadline submit report /by 2024-12-25
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Command not recognized. Check the format and try again.
____________________________________________________________
____________________________________________________________
     Task details are required for a todo.
____________________________________________________________
____________________________________________________________
     Format check.
     Usage: deadline <description> /by <time>
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] buy groceries
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [D][ ] submit report (by: Dec 25 2024)
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[T][ ] buy groceries
     2.[D][ ] submit report (by: Dec 25 2024)
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 19: Unmark Non-Existent Task

**Aim:** Test error handling for unmarking non-existent task

**Input:**
```
todo task1
unmark 2
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task1
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task number 2 is not in the queue.
     Queue contains 1 task(s).
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 20: Empty Deadline Time

**Aim:** Test error handling for deadline with empty time after /by

**Input:**
```
deadline submit assignment /by
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Deadline date is required.
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 21: Interleaved Success, Error, Success - Mark Operations

**Aim:** Test that marking errors don't affect valid mark operations

**Input:**
```
todo task1
todo task2
mark 1
mark 5
mark 2
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task1
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task2
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Marked complete:
       [T][X] task1
____________________________________________________________
____________________________________________________________
     Task number 5 is not in the queue.
     Queue contains 2 task(s).
____________________________________________________________
____________________________________________________________
     Marked complete:
       [T][X] task2
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[T][X] task1
     2.[T][X] task2
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 22: Mark Without Number

**Aim:** Test error handling for mark command without task number

**Input:**
```
todo task1
mark
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task1
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task number required.
     Usage: mark <task number>
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 23: Unmark Without Number

**Aim:** Test error handling for unmark command without task number

**Input:**
```
todo task1
mark 1
unmark
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task1
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Marked complete:
       [T][X] task1
____________________________________________________________
____________________________________________________________
     Task number required.
     Usage: unmark <task number>
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 24: Empty Event Time

**Aim:** Test error handling for event with empty time fields

**Input:**
```
event meeting /from 2pm /to
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Event start and end times are required.
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 25: Complex Interleaved Operations

**Aim:** Test complex interleaving of successful and failed operations to verify complete state integrity

**Input:**
```
todo task1
deadline
event meeting /from 2pm /to 3pm
mark 1
mark 10
todo task2
unmark abc
unmark 1
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task1
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Format check.
     Usage: deadline <description> /by <time>
____________________________________________________________
____________________________________________________________
     Task secured:
       [E][ ] meeting (from: 2pm to: 3pm)
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Marked complete:
       [T][X] task1
____________________________________________________________
____________________________________________________________
     Task number 10 is not in the queue.
     Queue contains 2 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task2
     Queue now holds 3 task(s).
____________________________________________________________
____________________________________________________________
     Task number must be a whole number.
____________________________________________________________
____________________________________________________________
     Returned to queue:
       [T][ ] task1
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[T][ ] task1
     2.[E][ ] meeting (from: 2pm to: 3pm)
     3.[T][ ] task2
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 26: Delete Task

**Aim:** Test that a task can be deleted successfully

**Input:**
```
todo task1
todo task2
todo task3
delete 2
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task1
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task2
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task3
     Queue now holds 3 task(s).
____________________________________________________________
____________________________________________________________
     Removed from the queue:
       [T][ ] task2
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[T][ ] task1
     2.[T][ ] task3
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 27: Delete Non-Existent Task

**Aim:** Test error handling for deleting non-existent task

**Input:**
```
todo task1
delete 5
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task1
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task number 5 is not in the queue.
     Queue contains 1 task(s).
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 28: Delete Without Number

**Aim:** Test error handling for delete command without task number

**Input:**
```
todo task1
delete
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task1
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task number required.
     Usage: delete <task number>
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 29: Delete with Invalid Number

**Aim:** Test error handling for delete with non-numeric input

**Input:**
```
todo task1
delete abc
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task1
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task number must be a whole number.
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 30: Delete Different Task Types

**Aim:** Test deleting different task types (Todo, Deadline, Event)

**Input:**
```
todo buy groceries
deadline submit report /by 2024-12-31
event meeting /from 2pm /to 3pm
delete 2
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] buy groceries
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [D][ ] submit report (by: Dec 31 2024)
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [E][ ] meeting (from: 2pm to: 3pm)
     Queue now holds 3 task(s).
____________________________________________________________
____________________________________________________________
     Removed from the queue:
       [D][ ] submit report (by: Dec 31 2024)
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[T][ ] buy groceries
     2.[E][ ] meeting (from: 2pm to: 3pm)
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 31: Invalid Date Format in Deadline

**Aim:** Test error handling for invalid date format in deadline

**Input:**
```
deadline return book /by 12/31/2024
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Date format invalid. Use yyyy-MM-dd (e.g., 2019-12-31).
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 32: Find Tasks on Specific Date

**Aim:** Test the on command to find tasks on a specific date

**Input:**
```
deadline return book /by 2024-12-15
deadline submit assignment /by 2024-12-15
deadline project meeting /by 2024-12-20
on 2024-12-15
on 2024-12-20
on 2024-12-10
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [D][ ] return book (by: Dec 15 2024)
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [D][ ] submit assignment (by: Dec 15 2024)
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [D][ ] project meeting (by: Dec 20 2024)
     Queue now holds 3 task(s).
____________________________________________________________
____________________________________________________________
     Schedule for Dec 15 2024:
     1.[D][ ] return book (by: Dec 15 2024)
     2.[D][ ] submit assignment (by: Dec 15 2024)
____________________________________________________________
____________________________________________________________
     Schedule for Dec 20 2024:
     1.[D][ ] project meeting (by: Dec 20 2024)
____________________________________________________________
____________________________________________________________
     No tasks scheduled for Dec 10 2024
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 33: Find Tasks by Keyword - Multiple Matches

**Aim:** Test finding tasks by searching for a keyword with multiple matches

**Input:**
```
todo read book
deadline return book /by 2024-06-15
todo buy groceries
find book
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] read book
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [D][ ] return book (by: Jun 15 2024)
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] buy groceries
     Queue now holds 3 task(s).
____________________________________________________________
____________________________________________________________
     Matches in queue:
     1.[T][ ] read book
     2.[D][ ] return book (by: Jun 15 2024)
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 34: Find Tasks by Keyword - No Matches

**Aim:** Test finding tasks when no matches exist

**Input:**
```
todo read book
todo buy groceries
find meeting
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] read book
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] buy groceries
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     No matching tasks in the queue for: meeting
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 35: Find Tasks by Keyword - Case Insensitive

**Aim:** Test that keyword search is case-insensitive

**Input:**
```
todo Read Book
todo BUY GROCERIES
find book
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] Read Book
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] BUY GROCERIES
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Matches in queue:
     1.[T][ ] Read Book
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 36: Find Tasks with Empty Keyword

**Aim:** Test error handling when find command has no keyword

**Input:**
```
todo read book
find
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] read book
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Search keyword required.
     Usage: find <keyword>
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 37: Sort by Status - Not Done First

**Aim:** Verify sort by status puts not-done tasks before done tasks

**Input:**
```
todo task A
todo task B
mark 1
sort status
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task A
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task B
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Marked complete:
       [T][X] task A
____________________________________________________________
____________________________________________________________
     Here are your tasks sorted by status:
     1.[T][ ] task B
     2.[T][X] task A
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[T][X] task A
     2.[T][ ] task B
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 38: Sort by Description - Alphabetical

**Aim:** Verify sort by description is alphabetical A-Z (case-insensitive)

**Input:**
```
todo zebra
todo apple
todo Mango
sort description
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] zebra
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] apple
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] Mango
     Queue now holds 3 task(s).
____________________________________________________________
____________________________________________________________
     Here are your tasks sorted by description:
     1.[T][ ] apple
     2.[T][ ] Mango
     3.[T][ ] zebra
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 39: Sort by Date - Deadlines First

**Aim:** Verify sort by date puts deadlines (oldest first) before todos/events

**Input:**
```
todo no date task
deadline late /by 2024-12-31
event meeting /from 2pm /to 4pm
deadline early /by 2024-01-15
sort date
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] no date task
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [D][ ] late (by: Dec 31 2024)
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [E][ ] meeting (from: 2pm to: 4pm)
     Queue now holds 3 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [D][ ] early (by: Jan 15 2024)
     Queue now holds 4 task(s).
____________________________________________________________
____________________________________________________________
     Here are your tasks sorted by date:
     1.[D][ ] early (by: Jan 15 2024)
     2.[D][ ] late (by: Dec 31 2024)
     3.[T][ ] no date task
     4.[E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 40: Sort with Invalid Criterion

**Aim:** Test error handling for invalid sort criterion

**Input:**
```
todo task1
sort invalid
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task1
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Invalid sort criterion.
     Usage: sort status / sort description / sort date
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 41: Sort without Criterion

**Aim:** Test error handling when criterion is missing

**Input:**
```
todo task1
sort
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task1
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Sort criterion required.
     Usage: sort status / sort description / sort date
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 42: Sort Case-Insensitive Criteria

**Aim:** Verify criterion matching is case-insensitive

**Input:**
```
todo task1
sort STATUS
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] task1
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Here are your tasks sorted by status:
     1.[T][ ] task1
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 43: Sort Empty List

**Aim:** Test sorting when no tasks exist

**Input:**
```
sort status
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Here are your tasks sorted by status:
     You have no tasks in your list.
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```

## Test Case 44: Sort Immutability

**Aim:** CRITICAL - Verify sort doesn't modify original task order

**Input:**
```
todo first
todo second
todo third
sort description
list
bye
```

**Expected Output:**
```
____________________________________________________________
    ____
 / ___|_   _ _ __  _ __   __ _
| |  _| | | | '_ \| '_ \ / _` |
| |_| | |_| | | | | | | | (_| |
 \____|\__,_|_| |_|_| |_|\__,_|

     Gunna online.
     What are we tackling today?
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] first
     Queue now holds 1 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] second
     Queue now holds 2 task(s).
____________________________________________________________
____________________________________________________________
     Task secured:
       [T][ ] third
     Queue now holds 3 task(s).
____________________________________________________________
____________________________________________________________
     Here are your tasks sorted by description:
     1.[T][ ] first
     2.[T][ ] second
     3.[T][ ] third
____________________________________________________________
____________________________________________________________
     Current queue:
     1.[T][ ] first
     2.[T][ ] second
     3.[T][ ] third
____________________________________________________________
____________________________________________________________
     Gunna signing off. See you next mission.
____________________________________________________________
```
