# UI Test Plan for Jiji Chatbot

This document contains test cases for automated UI and CLI verification of the Jiji chatbot application.

---

## Test Case 1: Startup and Immediate Exit
- **Aim**: Verify that the application displays the startup banner, greeting message, and terminates gracefully when given the `bye` command.
- **Inputs**:
```text
bye
```
- **Expected Output**:
```text
    ____________________________________________________________
         _     _          _     _ 
        | |   (_)        (_)   (_)
        | |    _          _     _ 
     _  | |   | |        | |   | |
    | |_| |   | |     _  | |   | |
     \___/    |_|    | |_| |   |_|
                      \___/       

     Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ
     What shall we gently tackle together today?
    ____________________________________________________________
    ____________________________________________________________
     Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
```

---

## Test Case 2: Add Todo, Deadline, Event, and List
- **Aim**: Verify adding different task types (Todo, Deadline, Event) and viewing them via the `list` command.
- **Inputs**:
```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```
- **Expected Output**:
```text
    ____________________________________________________________
         _     _          _     _ 
        | |   (_)        (_)   (_)
        | |    _          _     _ 
     _  | |   | |        | |   | |
    | |_| |   | |     _  | |   | |
     \___/    |_|    | |_| |   |_|
                      \___/       

     Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ
     What shall we gently tackle together today?
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [T][ ] read book
     That makes 1 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [D][ ] return book (by: Sunday)
     That makes 2 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     That makes 3 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
```

---

## Test Case 3: Mark and Unmark Polymorphic Tasks
- **Aim**: Verify marking a Deadline task as done and unmarking an Event task.
- **Inputs**:
```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
mark 2
list
unmark 2
list
bye
```
- **Expected Output**:
```text
    ____________________________________________________________
         _     _          _     _ 
        | |   (_)        (_)   (_)
        | |    _          _     _ 
     _  | |   | |        | |   | |
    | |_| |   | |     _  | |   | |
     \___/    |_|    | |_| |   |_|
                      \___/       

     Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ
     What shall we gently tackle together today?
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [T][ ] read book
     That makes 1 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [D][ ] return book (by: Sunday)
     That makes 2 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     That makes 3 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Paws up! Marked this task as done:
       [D][X] return book (by: Sunday)
     Wonderful job! Time for a gentle stretch. ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][X] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     No hurry at all! I've marked this task as pending again:
       [D][ ] return book (by: Sunday)
     We'll get back to it when you're ready. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
```

---

## Test Case 4: Delete Tasks and Re-indexing (Level-6 & A-Collections)
- **Aim**: Verify deleting tasks from the middle, beginning, and end of the list, verifying re-indexing and task count decrement.
- **Inputs**:
```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
delete 2
list
delete 1
list
delete 1
list
bye
```
- **Expected Output**:
```text
    ____________________________________________________________
         _     _          _     _ 
        | |   (_)        (_)   (_)
        | |    _          _     _ 
     _  | |   | |        | |   | |
    | |_| |   | |     _  | |   | |
     \___/    |_|    | |_| |   |_|
                      \___/       

     Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ
     What shall we gently tackle together today?
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [T][ ] read book
     That makes 1 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [D][ ] return book (by: Sunday)
     That makes 2 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     That makes 3 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Gently cleared away! I've removed this task:
       [D][ ] return book (by: Sunday)
     That leaves 2 cozy tasks in your list. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Gently cleared away! I've removed this task:
       [T][ ] read book
     That leaves 1 cozy tasks in your list. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Gently cleared away! I've removed this task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     That leaves 0 cozy tasks in your list. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
    ____________________________________________________________
    ____________________________________________________________
     Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
```

---

## Test Case 5: Error Handling and Interleaved Operations
- **Aim**: Verify that invalid commands, malformed parameters, and out-of-bounds operations (including delete) do not corrupt the task list or alter state.
- **Inputs**:
```text
list
delete 1
mark 1
unmark 1
todo read book
todo   
deadline
deadline /by Sunday
deadline return book /by 
event
event meeting /from 2pm
event meeting /to 4pm
event /from 2pm /to 4pm
event meeting /from  /to 4pm
list
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
delete 0
delete -1
delete 4
delete abc
delete
mark 0
mark -1
mark 4
mark abc
mark
unmark 0
unmark 99
unmark xyz
unmark
list
mark 2
unknown_cmd
list
delete 2
list
bye
```
- **Expected Output**:
```text
    ____________________________________________________________
         _     _          _     _ 
        | |   (_)        (_)   (_)
        | |    _          _     _ 
     _  | |   | |        | |   | |
    | |_| |   | |     _  | |   | |
     \___/    |_|    | |_| |   |_|
                      \___/       

     Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ
     What shall we gently tackle together today?
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [T][ ] read book
     That makes 1 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^._.^₎ 𐒡 The description of a todo cannot be empty.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ^๑_๑^ ੭ A deadline task requires a description and a '/by' time.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ^๑_๑^ ੭ A deadline task requires a description and a '/by' time.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ^๑_๑^ ੭ A deadline task requires a description and a '/by' time.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ^๑_๑^ ੭ An event task requires a description, '/from', and '/to' times.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ^๑_๑^ ੭ An event task requires a description, '/from', and '/to' times.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ^๑_๑^ ੭ An event task requires a description, '/from', and '/to' times.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ^๑_๑^ ੭ An event task requires a description, '/from', and '/to' times.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ^๑_๑^ ੭ An event task requires a description, '/from', and '/to' times.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [D][ ] return book (by: Sunday)
     That makes 2 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     That makes 3 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please specify a task number to delete.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please specify a task number to mark.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please specify a task number to unmark.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Paws up! Marked this task as done:
       [D][X] return book (by: Sunday)
     Wonderful job! Time for a gentle stretch. ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ I'm sorry, but I don't know what that means.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][X] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Gently cleared away! I've removed this task:
       [D][X] return book (by: Sunday)
     That leaves 2 cozy tasks in your list. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
```

---

## Test Case 6: Data Persistence - Save Operations (Level-7)
- **Aim**: Verify adding, marking, and deleting tasks in an initial session, persisting them to data storage upon modification.
- **Inputs**:
```text
todo read textbook
deadline submit homework /by tonight
event team sync /from 3pm /to 4pm
mark 1
delete 2
list
bye
```
- **Expected Output**:
```text
    ____________________________________________________________
         _     _          _     _ 
        | |   (_)        (_)   (_)
        | |    _          _     _ 
     _  | |   | |        | |   | |
    | |_| |   | |     _  | |   | |
     \___/    |_|    | |_| |   |_|
                      \___/       

     Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ
     What shall we gently tackle together today?
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [T][ ] read textbook
     That makes 1 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [D][ ] submit homework (by: tonight)
     That makes 2 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [E][ ] team sync (from: 3pm to: 4pm)
     That makes 3 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Paws up! Marked this task as done:
       [T][X] read textbook
     Wonderful job! Time for a gentle stretch. ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
    ____________________________________________________________
     Gently cleared away! I've removed this task:
       [D][ ] submit homework (by: tonight)
     That leaves 2 cozy tasks in your list. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read textbook
     2.[E][ ] team sync (from: 3pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
```

---

## Test Case 7: Data Persistence - Load Saved Tasks in New Session (Level-7)
- **Aim**: Verify that launching a new session loads previously saved tasks and preserves their completion state.
- **Preserve Data**: true
- **Inputs**:
```text
list
todo sleep early
list
bye
```
- **Expected Output**:
```text
    ____________________________________________________________
         _     _          _     _ 
        | |   (_)        (_)   (_)
        | |    _          _     _ 
     _  | |   | |        | |   | |
    | |_| |   | |     _  | |   | |
     \___/    |_|    | |_| |   |_|
                      \___/       

     Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ
     What shall we gently tackle together today?
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read textbook
     2.[E][ ] team sync (from: 3pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [T][ ] sleep early
     That makes 3 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read textbook
     2.[E][ ] team sync (from: 3pm to: 4pm)
     3.[T][ ] sleep early
    ____________________________________________________________
    ____________________________________________________________
     Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
```

---

## Test Case 8: Semantic Date and Time Handling (Level-8)
- **Aim**: Verify parsing of standard date formats (yyyy-MM-dd, d/M/yyyy HHmm) and formatted display output (MMM dd yyyy, h:mma).
- **Inputs**:
```text
deadline return book /by 2026-08-30
deadline submit project /by 2/12/2026 1800
event orientation camp /from 2026-09-01 0900 /to 2026-09-03 1700
list
bye
```
- **Expected Output**:
```text
    ____________________________________________________________
         _     _          _     _ 
        | |   (_)        (_)   (_)
        | |    _          _     _ 
     _  | |   | |        | |   | |
    | |_| |   | |     _  | |   | |
     \___/    |_|    | |_| |   |_|
                      \___/       

     Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ
     What shall we gently tackle together today?
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [D][ ] return book (by: Aug 30 2026)
     That makes 1 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [D][ ] submit project (by: Dec 02 2026, 6:00PM)
     That makes 2 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [E][ ] orientation camp (from: Sep 01 2026, 9:00AM to: Sep 03 2026, 5:00PM)
     That makes 3 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] return book (by: Aug 30 2026)
     2.[D][ ] submit project (by: Dec 02 2026, 6:00PM)
     3.[E][ ] orientation camp (from: Sep 01 2026, 9:00AM to: Sep 03 2026, 5:00PM)
    ____________________________________________________________
    ____________________________________________________________
     Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
```

---

## Test Case 9: Persistence of Date/Time Objects (Level-8)
- **Aim**: Verify that dates/times saved in storage are reloaded across sessions and properly formatted upon restart.
- **Preserve Data**: true
- **Inputs**:
```text
list
bye
```
- **Expected Output**:
```text
    ____________________________________________________________
         _     _          _     _ 
        | |   (_)        (_)   (_)
        | |    _          _     _ 
     _  | |   | |        | |   | |
    | |_| |   | |     _  | |   | |
     \___/    |_|    | |_| |   |_|
                      \___/       

     Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ
     What shall we gently tackle together today?
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] return book (by: Aug 30 2026)
     2.[D][ ] submit project (by: Dec 02 2026, 6:00PM)
     3.[E][ ] orientation camp (from: Sep 01 2026, 9:00AM to: Sep 03 2026, 5:00PM)
    ____________________________________________________________
    ____________________________________________________________
     Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
```

---

## Test Case 10: Find Tasks by Keyword (Level-9)
- **Aim**: Verify searching for matching tasks by keyword, empty result handling, and error handling for missing search keyword.
- **Inputs**:
```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
todo write book review
find book
find meeting
find swimming
find
bye
```
- **Expected Output**:
```text
    ____________________________________________________________
         _     _          _     _ 
        | |   (_)        (_)   (_)
        | |    _          _     _ 
     _  | |   | |        | |   | |
    | |_| |   | |     _  | |   | |
     \___/    |_|    | |_| |   |_|
                      \___/       

     Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ
     What shall we gently tackle together today?
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [T][ ] read book
     That makes 1 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [D][ ] return book (by: Sunday)
     That makes 2 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     That makes 3 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [T][ ] write book review
     That makes 4 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Sunday)
     3.[T][ ] write book review
    ____________________________________________________________
    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Here are the matching tasks in your list:
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please specify a keyword to search for.
    ____________________________________________________________
    ____________________________________________________________
     Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
```

---

## Test Case 11: Help Command (C-Help)
- **Aim**: Verify the in-app guidance system for general help overview, specific command details, and unrecognized help topics.
- **Inputs**:
```text
help
help deadline
help unknown
bye
```
- **Expected Output**:
```text
    ____________________________________________________________
         _     _          _     _ 
        | |   (_)        (_)   (_)
        | |    _          _     _ 
     _  | |   | |        | |   | |
    | |_| |   | |     _  | |   | |
     \___/    |_|    | |_| |   |_|
                      \___/       

     Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ
     What shall we gently tackle together today?
    ____________________________________________________________
    ____________________________________________________________
     Available commands in Jiji:

     [Add Tasks]
     • todo <description>
     • deadline <description> /by <time>
     • event <desc> /from <start> /to <end>

     [Manage Tasks]
     • list [filter] - View tasks (pending/done)
     • schedule <date> - View schedule for date
     • mark <index> - Mark as done
     • unmark <index> - Mark as not done
     • delete <index> - Delete a task
     • find <keyword> - Search by keyword

     [General]
     • stats - View task statistics
     • help [command] - View command guide
     • bye - Exit Jiji

     Tip: Type 'help <command>' (e.g. 'help deadline') for details!
    ____________________________________________________________
    ____________________________________________________________
     Command: deadline
     Syntax: deadline <desc> /by <time>
     Description: Adds a task due by a specific date/time.
     Formats: yyyy-MM-dd, d/M/yyyy, HHmm
     Example: deadline submit report /by 2026-08-30 1800
    ____________________________________________________________
    ____________________________________________________________
     Unknown command: unknown.
     Type 'help' to see the list of all available commands.
    ____________________________________________________________
    ____________________________________________________________
     Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
```

---

## Test Case 12: Statistics and Filtered List Views (C-Statistics)
- **Aim**: Verify task statistics calculation and filtered list views (list pending, list done) with master index preservation and completion rates.
- **Inputs**:
```text
stats
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
mark 1
list pending
list done
stats
bye
```
- **Expected Output**:
```text
    ____________________________________________________________
         _     _          _     _ 
        | |   (_)        (_)   (_)
        | |    _          _     _ 
     _  | |   | |        | |   | |
    | |_| |   | |     _  | |   | |
     \___/    |_|    | |_| |   |_|
                      \___/       

     Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ
     What shall we gently tackle together today?
    ____________________________________________________________
    ____________________________________________________________
     Task Statistics & Insights:

     • No tasks in your list yet!
     • Use 'todo', 'deadline', or 'event' to add tasks.
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [T][ ] read book
     That makes 1 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [D][ ] return book (by: Sunday)
     That makes 2 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     That makes 3 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Paws up! Marked this task as done:
       [T][X] read book
     Wonderful job! Time for a gentle stretch. ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
    ____________________________________________________________
     Here are the pending tasks in your list:
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Here are the completed tasks in your list:
     1.[T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Task Statistics & Insights:

     [Overall Progress]
     • Total tasks: 3
     • Completed: 1 (33.3%)
     • Pending: 2

     [Breakdown by Type]
     • ToDos: 1 (1 completed)
     • Deadlines: 1 (0 completed, 0 overdue)
     • Events: 1 (0 completed)

     Tip: Use 'list pending' to view only incomplete tasks!
    ____________________________________________________________
    ____________________________________________________________
     Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
```

---

## Test Case 13: View Schedules by Date (B-ViewSchedules)
- **Aim**: Verify viewing scheduled tasks (deadlines and events) for a specific date, handling ongoing multi-day events, empty schedules, and input validation.
- **Inputs**:
```text
todo read book
deadline return book /by 2026-08-30
event orientation camp /from 2026-08-29 0900 /to 2026-08-31 1700
deadline submit project /by 2026-09-05
schedule 2026-08-30
schedule 2026-08-31
schedule 2026-12-25
schedule
schedule invalid-date
bye
```
- **Expected Output**:
```text
    ____________________________________________________________
         _     _          _     _ 
        | |   (_)        (_)   (_)
        | |    _          _     _ 
     _  | |   | |        | |   | |
    | |_| |   | |     _  | |   | |
     \___/    |_|    | |_| |   |_|
                      \___/       

     Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ
     What shall we gently tackle together today?
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [T][ ] read book
     That makes 1 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [D][ ] return book (by: Aug 30 2026)
     That makes 2 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [E][ ] orientation camp (from: Aug 29 2026, 9:00AM to: Aug 31 2026, 5:00PM)
     That makes 3 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Tucked away safely! I've nestled this task into your list:
       [D][ ] submit project (by: Sep 05 2026)
     That makes 4 tasks in our cozy bundle. ₍^. .^₎
    ____________________________________________________________
    ____________________________________________________________
     Schedule for Aug 30 2026:
     2.[D][ ] return book (by: Aug 30 2026)
     3.[E][ ] orientation camp (from: Aug 29 2026, 9:00AM to: Aug 31 2026, 5:00PM)
    ____________________________________________________________
    ____________________________________________________________
     Schedule for Aug 31 2026:
     3.[E][ ] orientation camp (from: Aug 29 2026, 9:00AM to: Aug 31 2026, 5:00PM)
    ____________________________________________________________
    ____________________________________________________________
     No tasks scheduled for Dec 25 2026. A purr-fectly peaceful day to rest! ₍^ ᵕ ᵕ ^₎
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Please specify a date for the schedule (e.g. 'schedule 2026-08-30' or 'schedule today').
    ____________________________________________________________
    ____________________________________________________________
     OOPS! ₍^› ꘍ ‹ ^₎⟆ Invalid date format. Please use yyyy-MM-dd (e.g. 2026-08-30) or 'today'.
    ____________________________________________________________
    ____________________________________________________________
     Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
    ____________________________________________________________
```


