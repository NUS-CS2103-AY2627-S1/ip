# Braun UI Test Plan

This test plan defines the UI/IO test cases for the Braun chatbot application.
Each test case specifies the **Aim**, **Input**, and **Expected Output**.
The test runner executes these test cases sequentially within a broadcast session, validates each output block, and terminates immediately if any discrepancy is found.

---

### Test Case 1: Startup Greeting Banner
**Aim**: Verify that Braun initializes with the CRT television banner and late-night talk show greeting.

**Input**:
```
```

**Expected Output**:
```
    ____________________________________________________________
    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
    ⠀⠀⠀⠀⠀⠀⠀⢠⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⡄⠀⠀⠀⠀⠀⠀⠀
    ⠀⠀⠀⠀⠀⠀⠀⠀⠉⠻⣦⣄⠀⠀⠀⠀⠀⠀⣠⣴⠟⠉⠀⠀⠀⠀⠀⠀⠀⠀
    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⣠⣴⣶⣶⣦⣄⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
    ⠀⠀⠀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣉⣉⣉⣉⣉⣉⣀⣀⣀⣀⣀⣀⣀⣀⣀⠀⠀⠀
    ⠀⠀⢸⣿⠟⠛⣛⣛⣛⣛⣛⣛⣛⣛⣛⣛⣛⣛⠛⠛⠛⠛⢿⡿⠛⠿⣿⡇⠀⠀
    ⠀⠀⢸⡏⢠⣾⣿⣿⣿⣿⣿⠿⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⢸⣇⠀⠀⣽⡇⠀⠀
    ⠀⠀⢸⡇⢸⣿⣿⣿⠟⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⣿⣿⡇⠀⠀
    ⠀⠀⢸⡇⢸⣿⡿⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⣿⣿⡇⠀⠀
    ⠀⠀⢸⡇⢸⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⡿⠿⠿⣿⡇⠀⠀
    ⠀⠀⢸⡇⠸⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⡷⠶⠶⣾⡇⠀⠀
    ⠀⠀⢸⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⡷⠶⠶⢾⡇⠀⠀
    ⠀⠀⢸⣿⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣼⣷⣶⣶⣿⡇⠀⠀
    ⠀⠀⠘⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠃⠀⠀
    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     *kzzzt... bzzzt!*
     Good evening, dear guest! I'm Braun, host of the Late-Night Show.
     What can I do for you?
    ____________________________________________________________
```

---

### Test Case 2: List Empty Tasks
**Aim**: Verify that the `list` command displays an empty task list header when no tasks have been added.

**Input**:
```
list
```

**Expected Output**:
```
    ____________________________________________________________
     Here are the tasks in your list:
    ____________________________________________________________
```

---

### Test Case 3: Add Todo Task
**Aim**: Verify adding a `todo` task containing the lore keyword 'ghost' creates a Todo with [T] tag and triggers anomaly remark.

**Input**:
```
todo ghost exploration
```

**Expected Output**:
```
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] ghost exploration
     Now you have 1 tasks in the list.
     *screen flickers* A new anomaly! Daydream Inc. will want this documented.
    ____________________________________________________________
```

---

### Test Case 4: Add Deadline Task
**Aim**: Verify adding a `deadline` task with `/by` creates a Deadline with formatted date/time.

**Input**:
```
deadline submit monthly report /by 2026-08-30 1700
```

**Expected Output**:
```
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] submit monthly report (by: Aug 30 2026, 5:00PM)
     Now you have 2 tasks in the list.
     *chime* Even trapped in a ghost story, we still gotta work, don't we?
    ____________________________________________________________
```

---

### Test Case 5: Add Event Task
**Aim**: Verify adding an `event` task with `/from` and `/to` creates an Event with formatted date/time intervals.

**Input**:
```
event search pink rabbit doll /from 2026-08-24 1400 /to 2026-08-24 1600
```

**Expected Output**:
```
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] search pink rabbit doll (from: Aug 24 2026, 2:00PM to: Aug 24 2026, 4:00PM)
     Now you have 3 tasks in the list.
     *bzzzt* Reminds me of a certain charming pink rabbit doll, doesn't it?
    ____________________________________________________________
```

---

### Test Case 6: List Added Tasks
**Aim**: Verify that the `list` command enumerates all polymorphic task types with [T], [D], [E] tags and formatted dates.

**Input**:
```
list
```

**Expected Output**:
```
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] ghost exploration
     2.[D][ ] submit monthly report (by: Aug 30 2026, 5:00PM)
     3.[E][ ] search pink rabbit doll (from: Aug 24 2026, 2:00PM to: Aug 24 2026, 4:00PM)
    ____________________________________________________________
```

---

### Test Case 7: Mark Deadline Task As Done
**Aim**: Verify marking task 2 updates its status to completed `[X]` preserving `[D]` tag and due date.

**Input**:
```
mark 2
```

**Expected Output**:
```
    ____________________________________________________________
     Nice! I've marked this task as done:
       [D][X] submit monthly report (by: Aug 30 2026, 5:00PM)
    ____________________________________________________________
```

---

### Test Case 8: Verify List After Marking
**Aim**: Verify that the `list` command displays task 2 as `[D][X]` while others remain uncompleted.

**Input**:
```
list
```

**Expected Output**:
```
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] ghost exploration
     2.[D][X] submit monthly report (by: Aug 30 2026, 5:00PM)
     3.[E][ ] search pink rabbit doll (from: Aug 24 2026, 2:00PM to: Aug 24 2026, 4:00PM)
    ____________________________________________________________
```

---

### Test Case 9: Unmark Task
**Aim**: Verify unmarking task 2 reverts its status back to uncompleted `[D][ ]`.

**Input**:
```
unmark 2
```

**Expected Output**:
```
    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [D][ ] submit monthly report (by: Aug 30 2026, 5:00PM)
    ____________________________________________________________
```

---

### Test Case 10: Mark Out-Of-Bounds Index
**Aim**: Verify error handling when attempting to mark an index outside current task range.

**Input**:
```
mark 99
```

**Expected Output**:
```
    ____________________________________________________________
     *bzzzt* Invalid broadcast index! Task not found.
    ____________________________________________________________
```

---

### Test Case 11: Mark Non-Numeric Index
**Aim**: Verify error handling when passing a non-numeric index.

**Input**:
```
mark abc
```

**Expected Output**:
```
    ____________________________________________________________
     *static* Please provide a valid task number to mark.
    ____________________________________________________________
```

---

### Test Case 12: Empty Todo Description Error
**Aim**: Verify error handling when `todo` command is provided without description.

**Input**:
```
todo
```

**Expected Output**:
```
    ____________________________________________________________
     *static* The description of a todo cannot be empty.
    ____________________________________________________________
```

---

### Test Case 13: Deadline Missing /by Parameter Error
**Aim**: Verify error handling when `deadline` command is missing `/by` specifier.

**Input**:
```
deadline return book
```

**Expected Output**:
```
    ____________________________________________________________
     *static* Please specify deadline due time using /by <time>.
    ____________________________________________________________
```

---

### Test Case 14: Event Missing /from or /to Parameter Error
**Aim**: Verify error handling when `event` command is missing `/to` parameter.

**Input**:
```
event team meeting /from 2pm
```

**Expected Output**:
```
    ____________________________________________________________
     *static* Please specify event duration using /from <start> /to <end>.
    ____________________________________________________________
```

---

### Test Case 15: Unknown Broadcast Command Error
**Aim**: Verify error handling when user inputs an unrecognized command such as `blah`.

**Input**:
```
blah
```

**Expected Output**:
```
    ____________________________________________________________
     *static* Unknown broadcast command! Please use todo, deadline, event, list, mark, unmark, delete, find, date, or bye.
    ____________________________________________________________
```

---

### Test Case 16: Unmark Non-Numeric Index Error
**Aim**: Verify error handling when passing non-numeric argument to `unmark`.

**Input**:
```
unmark xyz
```

**Expected Output**:
```
    ____________________________________________________________
     *static* Please provide a valid task number to unmark.
    ____________________________________________________________
```

---

### Test Case 17: Deadline Completely Empty Parameters Error
**Aim**: Verify error handling when `deadline` command is invoked with no arguments.

**Input**:
```
deadline
```

**Expected Output**:
```
    ____________________________________________________________
     *static* Please specify deadline due time using /by <time>.
    ____________________________________________________________
```

---

### Test Case 18: Event Completely Empty Parameters Error
**Aim**: Verify error handling when `event` command is invoked with no arguments.

**Input**:
```
event
```

**Expected Output**:
```
    ____________________________________________________________
     *static* Please specify event duration using /from <start> /to <end>.
    ____________________________________________________________
```

---

### Test Case 19: Delete Task From List
**Aim**: Verify deleting a task from the list removes the task and outputs the updated count.

**Input**:
```
delete 2
```

**Expected Output**:
```
    ____________________________________________________________
     Noted. I've removed this task:
       [D][ ] submit monthly report (by: Aug 30 2026, 5:00PM)
     Now you have 2 tasks in the list.
    ____________________________________________________________
```

---

### Test Case 20: Verify Task List After Deletion
**Aim**: Verify that the remaining tasks in the list are shifted and numbered correctly after deletion.

**Input**:
```
list
```

**Expected Output**:
```
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] ghost exploration
     2.[E][ ] search pink rabbit doll (from: Aug 24 2026, 2:00PM to: Aug 24 2026, 4:00PM)
    ____________________________________________________________
```

---

### Test Case 21: Delete Out-Of-Bounds Index Error
**Aim**: Verify error handling when attempting to delete an index out of bounds.

**Input**:
```
delete 99
```

**Expected Output**:
```
    ____________________________________________________________
     *bzzzt* Invalid broadcast index! Task not found.
    ____________________________________________________________
```

---

### Test Case 22: Delete Non-Numeric Index Error
**Aim**: Verify error handling when passing non-numeric argument to `delete`.

**Input**:
```
delete abc
```

**Expected Output**:
```
    ____________________________________________________________
     *static* Please provide a valid task number to delete.
    ____________________________________________________________
```

---

### Test Case 23: Delete Missing Index Argument Error
**Aim**: Verify error handling when invoking `delete` without specifying an index.

**Input**:
```
delete
```

**Expected Output**:
```
    ____________________________________________________________
     *static* Please provide a valid task number to delete.
    ____________________________________________________________
```

---

### Test Case 24: Add Task Dynamically To Collections List
**Aim**: Verify adding a task to the dynamically resized collection works smoothly.

**Input**:
```
todo submit salary claim
```

**Expected Output**:
```
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] submit salary claim
     Now you have 3 tasks in the list.
     *chime* Even trapped in a ghost story, we still gotta work, don't we?
    ____________________________________________________________
```

---

### Test Case 25: Query Tasks By Specific Date
**Aim**: Verify querying tasks occurring on a specific date filters and lists the event.

**Input**:
```
date 2026-08-24
```

**Expected Output**:
```
    ____________________________________________________________
     Here are the tasks scheduled for Aug 24 2026:
     1.[E][ ] search pink rabbit doll (from: Aug 24 2026, 2:00PM to: Aug 24 2026, 4:00PM)
    ____________________________________________________________
```

---

### Test Case 26: Query Date With No Tasks
**Aim**: Verify querying a date with no scheduled tasks returns appropriate feedback.

**Input**:
```
date 2026-12-31
```

**Expected Output**:
```
    ____________________________________________________________
     *static* No broadcast tasks scheduled for Dec 31 2026.
    ____________________________________________________________
```

---

### Test Case 27: Invalid Date Format Error
**Aim**: Verify error handling when deadline provides an unparseable date/time string.

**Input**:
```
deadline return equipment /by invalid-date
```

**Expected Output**:
```
    ____________________________________________________________
     *static* Invalid date format! Please use yyyy-MM-dd (e.g. 2026-08-30) or yyyy-MM-dd HHmm / d/M/yyyy HHmm (e.g. 2026-08-30 1700 or 2/12/2019 1800).
    ____________________________________________________________
```

---

### Test Case 28: Find Matching Task
**Aim**: Verify that `find` locates tasks whose description contains the specified keyword.

**Input**:
```
find rabbit
```

**Expected Output**:
```
    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[E][ ] search pink rabbit doll (from: Aug 24 2026, 2:00PM to: Aug 24 2026, 4:00PM)
    ____________________________________________________________
```

---

### Test Case 29: Find Case-Insensitive Keyword
**Aim**: Verify that `find` matches keywords case-insensitively.

**Input**:
```
find GHOST
```

**Expected Output**:
```
    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][ ] ghost exploration
    ____________________________________________________________
```

---

### Test Case 30: Find Nonexistent Task
**Aim**: Verify appropriate notification when no tasks match the searched keyword.

**Input**:
```
find nonexistent
```

**Expected Output**:
```
    ____________________________________________________________
     *static* No matching broadcast tasks found for: nonexistent
    ____________________________________________________________
```

---

### Test Case 31: Find Missing Keyword Error
**Aim**: Verify error handling when the `find` command is issued without a search keyword.

**Input**:
```
find
```

**Expected Output**:
```
    ____________________________________________________________
     *static* Please specify a keyword to search for (e.g. find book).
    ____________________________________________________________
```

---

### Test Case 32: Find Multiple Keywords (OR Search)
**Aim**: Verify that `find` matches tasks containing any of multiple whitespace-separated keywords.

**Input**:
```
find rabbit ghost
```

**Expected Output**:
```
    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][ ] ghost exploration
     2.[E][ ] search pink rabbit doll (from: Aug 24 2026, 2:00PM to: Aug 24 2026, 4:00PM)
    ____________________________________________________________
```

---

### Test Case 33: Find Quoted Exact Phrase
**Aim**: Verify that `find` matches exact phrases enclosed in double quotes.

**Input**:
```
find "pink rabbit"
```

**Expected Output**:
```
    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[E][ ] search pink rabbit doll (from: Aug 24 2026, 2:00PM to: Aug 24 2026, 4:00PM)
    ____________________________________________________________
```

---

### Test Case 34: Find By Date Keyword
**Aim**: Verify that `find` matches tasks by date strings appearing in their schedule.

**Input**:
```
find Aug 24
```

**Expected Output**:
```
    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[E][ ] search pink rabbit doll (from: Aug 24 2026, 2:00PM to: Aug 24 2026, 4:00PM)
    ____________________________________________________________
```

---

### Test Case 35: Exit Application
**Aim**: Verify that the `bye` command prints the farewell broadcast message and exits cleanly.

**Input**:
```
bye
```

**Expected Output**:
```
    ____________________________________________________________
     *bzzzt* That's a wrap for today's broadcast!
     Bye. Hope to see you again soon!
    ____________________________________________________________
```
