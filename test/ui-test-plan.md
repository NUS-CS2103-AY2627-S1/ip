# UI Test Plan

## Test configuration

- Application: `Yapper`
- Java version: 25
- Source directory: `src/main/java`
- Generated class directory: `build/ui-test-classes`
- Launch command: `java -cp build/ui-test-classes yapper.Yapper`
- Response timeout: 5 seconds per command
- Comparison: Exact text after normalizing line endings to `LF`; whitespace, blank lines, punctuation, and separators remain significant.
- Isolation: Start a fresh application process for each test case.

## Test cases

### UI-1: Invalid task and index inputs preserve state

**Aim:** Verify empty and unknown task inputs are rejected, invalid mark/unmark inputs do not mutate tasks, and valid operations still work afterward.

#### Command 1

**Input**

```text
todo borrow book
```

**Expected output**

```text
 OHOHO, MAGNIFICENT! I have triumphantly added this dazzling new task:
   [T][ ] borrow book
 Your legendary list now contains 1 task(s). Yes, I counted them personally!
____________________________________________________________
```

#### Command 2

**Input**

```text
todo
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: The todo description cannot be empty. Try: todo <task description>
____________________________________________________________
```

#### Command 3

**Input**

```text
buy groceries
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: Please precede a task with a command to add it. Otherwise, type 'help' for a list of commands.
____________________________________________________________
```

#### Command 4

**Input**

```text
list
```

**Expected output**

```text
 ATTENTION, EVERYONE! Yapper proudly presents your complete and glorious task list:
 1.[T][ ] borrow book
____________________________________________________________
```

#### Command 5

**Input**

```text
mark
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: Please specify a task number. Try: mark <task number>
____________________________________________________________
```

#### Command 6

**Input**

```text
mark book
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: 'book' is not a valid task number. Try: mark <task number>
____________________________________________________________
```

#### Command 7

**Input**

```text
mark 2
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: Task number 2 is not in the list. Type 'list' to see the available task numbers.
____________________________________________________________
```

#### Command 8

**Input**

```text
mark 1
```

**Expected output**

```text
 CONFETTI CANNONS! This task is now officially, unmistakably, spectacularly DONE:
  [T][X] borrow book
____________________________________________________________
```

#### Command 9

**Input**

```text
unmark
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: Please specify a task number. Try: unmark <task number>
____________________________________________________________
```

#### Command 10

**Input**

```text
list
```

**Expected output**

```text
 ATTENTION, EVERYONE! Yapper proudly presents your complete and glorious task list:
 1.[T][X] borrow book
____________________________________________________________
```

#### Command 11

**Input**

```text
unmark 1
```

**Expected output**

```text
 PLOT TWIST! This task has returned to the thrilling realm of NOT DONE YET:
  [T][ ] borrow book
____________________________________________________________
```

#### Command 12

**Input**

```text
bye
```

**Expected output**

```text
 FAREWELL, magnificent human! Yapper shall now stop talking--an historic occasion!
 Return soon, because your tasks and I will have an absolutely enormous amount to discuss.
____________________________________________________________
```

### UI-2: Malformed dated tasks preserve state

**Aim:** Verify malformed deadlines and events are rejected without being added, while valid dated tasks remain usable and unsupported date strings are rejected.

#### Command 1

**Input**

```text
deadline
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: The deadline description cannot be empty. Try: deadline <task description>
____________________________________________________________
```

#### Command 2

**Input**

```text
deadline submit report
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: A deadline needs a description and a due date. Try: deadline <task description> /by <date or time>
____________________________________________________________
```

#### Command 3

**Input**

```text
deadline submit report /by 2019-12-02 1800
```

**Expected output**

```text
 OHOHO, MAGNIFICENT! I have triumphantly added this dazzling new task:
   [D][ ] submit report (by: Dec 2 2019, 6:00PM)
 Your legendary list now contains 1 task(s). Yes, I counted them personally!
____________________________________________________________
```

#### Command 3a

**Input**

```text
deadline invalid date /by no idea :-p
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: Please use yyyy-MM-dd HHmm or d/M/yyyy HHmm for the deadline date. For example: deadline return book /by 2019-12-02 1800
____________________________________________________________
```
#### Command 4

**Input**

```text
event meeting /from Monday
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: An event needs a description, start, and end. Try: event <task description> /from <start> /to <end>
____________________________________________________________
```

#### Command 5

**Input**

```text
list
```

**Expected output**

```text
 ATTENTION, EVERYONE! Yapper proudly presents your complete and glorious task list:
 1.[D][ ] submit report (by: Dec 2 2019, 6:00PM)
____________________________________________________________
```

#### Command 6

**Input**

```text
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
```

**Expected output**

```text
 OHOHO, MAGNIFICENT! I have triumphantly added this dazzling new task:
   [E][ ] project meeting (from: Dec 2 2019, 2:00PM to: Dec 2 2019, 4:00PM)
 Your legendary list now contains 2 task(s). Yes, I counted them personally!
____________________________________________________________
```

#### Command 7

**Input**

```text
help
```

**Expected output**

```text
 PREPARE YOURSELF! Here is Yapper's impressively comprehensive command repertoire:
  todo <description>
  deadline <description> /by <yyyy-MM-dd HHmm>
  event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>
  on <yyyy-MM-dd>
  find <keyword>
  list
  stats
  mark <task number>
  unmark <task number>
  delete <task number>
  help
  bye
____________________________________________________________
```

#### Command 8

**Input**

```text
list
```

**Expected output**

```text
 ATTENTION, EVERYONE! Yapper proudly presents your complete and glorious task list:
 1.[D][ ] submit report (by: Dec 2 2019, 6:00PM)
 2.[E][ ] project meeting (from: Dec 2 2019, 2:00PM to: Dec 2 2019, 4:00PM)
____________________________________________________________
```

#### Command 9

**Input**

```text
bye
```

**Expected output**

```text
 FAREWELL, magnificent human! Yapper shall now stop talking--an historic occasion!
 Return soon, because your tasks and I will have an absolutely enormous amount to discuss.
____________________________________________________________
```

### UI-3: Delete tasks and reject invalid delete indices

**Aim:** Verify deletion removes the selected task, renumbers the remaining tasks, and missing, non-numeric, or out-of-range task numbers do not mutate the list.

#### Command 1

**Input**

```text
todo read book
```

**Expected output**

```text
 OHOHO, MAGNIFICENT! I have triumphantly added this dazzling new task:
   [T][ ] read book
 Your legendary list now contains 1 task(s). Yes, I counted them personally!
____________________________________________________________
```

#### Command 2

**Input**

```text
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
```

**Expected output**

```text
 OHOHO, MAGNIFICENT! I have triumphantly added this dazzling new task:
   [E][ ] project meeting (from: Aug 6 2019, 2:00PM to: Aug 6 2019, 4:00PM)
 Your legendary list now contains 2 task(s). Yes, I counted them personally!
____________________________________________________________
```

#### Command 3

**Input**

```text
todo borrow book
```

**Expected output**

```text
 OHOHO, MAGNIFICENT! I have triumphantly added this dazzling new task:
   [T][ ] borrow book
 Your legendary list now contains 3 task(s). Yes, I counted them personally!
____________________________________________________________
```

#### Command 4

**Input**

```text
delete
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: Please specify a task number. Try: delete <task number>
____________________________________________________________
```

#### Command 5

**Input**

```text
delete meeting
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: 'meeting' is not a valid task number. Try: delete <task number>
____________________________________________________________
```

#### Command 6

**Input**

```text
delete 4
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: Task number 4 is not in the list. Type 'list' to see the available task numbers.
____________________________________________________________
```

#### Command 7

**Input**

```text
delete 2
```

**Expected output**

```text
 BEHOLD! With one decisive flourish, I have removed this task from existence:
   [E][ ] project meeting (from: Aug 6 2019, 2:00PM to: Aug 6 2019, 4:00PM)
 Your freshly streamlined list now contains 2 task(s). Delightfully tidy!
____________________________________________________________
```

#### Command 8

**Input**

```text
list
```

**Expected output**

```text
 ATTENTION, EVERYONE! Yapper proudly presents your complete and glorious task list:
 1.[T][ ] read book
 2.[T][ ] borrow book
____________________________________________________________
```

#### Command 9

**Input**

```text
bye
```

**Expected output**

```text
 FAREWELL, magnificent human! Yapper shall now stop talking--an historic occasion!
 Return soon, because your tasks and I will have an absolutely enormous amount to discuss.
____________________________________________________________
```

### UI-4: Keyword search preserves original task numbers and state

**Aim:** Verify empty-list, case-insensitive, phrase, no-match and missing-keyword searches without changing task order or completion state.

#### Command 1

**Input**

```text
find book
```

**Expected output**

```text
 HERE ARE YOUR MATCHING TASKS:
 No matching tasks found. Try another keyword!
____________________________________________________________
```

#### Command 2

**Input**

```text
todo laundry
```

**Expected output**

```text
 OHOHO, MAGNIFICENT! I have triumphantly added this dazzling new task:
   [T][ ] laundry
 Your legendary list now contains 1 task(s). Yes, I counted them personally!
____________________________________________________________
```

#### Command 3

**Input**

```text
todo read book
```

**Expected output**

```text
 OHOHO, MAGNIFICENT! I have triumphantly added this dazzling new task:
   [T][ ] read book
 Your legendary list now contains 2 task(s). Yes, I counted them personally!
____________________________________________________________
```

#### Command 4

**Input**

```text
deadline return book /by 2024-02-29 1800
```

**Expected output**

```text
 OHOHO, MAGNIFICENT! I have triumphantly added this dazzling new task:
   [D][ ] return book (by: Feb 29 2024, 6:00PM)
 Your legendary list now contains 3 task(s). Yes, I counted them personally!
____________________________________________________________
```

#### Command 5

**Input**

```text
mark 3
```

**Expected output**

```text
 CONFETTI CANNONS! This task is now officially, unmistakably, spectacularly DONE:
  [D][X] return book (by: Feb 29 2024, 6:00PM)
____________________________________________________________
```

#### Command 6

**Input**

```text
find BOOK
```

**Expected output**

```text
 HERE ARE YOUR MATCHING TASKS:
 2.[T][ ] read book
 3.[D][X] return book (by: Feb 29 2024, 6:00PM)
____________________________________________________________
```

#### Command 7

**Input**

```text
find missing
```

**Expected output**

```text
 HERE ARE YOUR MATCHING TASKS:
 No matching tasks found. Try another keyword!
____________________________________________________________
```

#### Command 8

**Input**

```text
find
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: Please specify a keyword. Try: find <keyword>
____________________________________________________________
```

#### Command 9

**Input**

```text
find return book
```

**Expected output**

```text
 HERE ARE YOUR MATCHING TASKS:
 3.[D][X] return book (by: Feb 29 2024, 6:00PM)
____________________________________________________________
```

#### Command 10

**Input**

```text
list
```

**Expected output**

```text
 ATTENTION, EVERYONE! Yapper proudly presents your complete and glorious task list:
 1.[T][ ] laundry
 2.[T][ ] read book
 3.[D][X] return book (by: Feb 29 2024, 6:00PM)
____________________________________________________________
```

#### Command 11

**Input**

```text
bye
```

**Expected output**

```text
 FAREWELL, magnificent human! Yapper shall now stop talking--an historic occasion!
 Return soon, because your tasks and I will have an absolutely enormous amount to discuss.
____________________________________________________________
```

### UI-5: Statistics and unsafe descriptions

**Aim:** Verify empty statistics and rejection of reserved storage separators without changing the list.

#### Command 1

**Input**

```text
stats
```

**Expected output**

```text
 YOUR TASK STATISTICS:
 Total: 0
 Completed: 0
 Pending: 0
____________________________________________________________
```

#### Command 2

**Input**

```text
todo read | book
```

**Expected output**

```text
 WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: Task descriptions cannot contain the pipe character (|).
____________________________________________________________
```

#### Command 3

**Input**

```text
stats
```

**Expected output**

```text
 YOUR TASK STATISTICS:
 Total: 0
 Completed: 0
 Pending: 0
____________________________________________________________
```

#### Command 4

**Input**

```text
bye
```

**Expected output**

```text
 FAREWELL, magnificent human! Yapper shall now stop talking--an historic occasion!
 Return soon, because your tasks and I will have an absolutely enormous amount to discuss.
____________________________________________________________
```

## Session notes

- Startup output is captured in the transcript but is not attributed to the first command.
- A failing command ends the entire test run immediately. Later commands and test cases are not run.
- The final transcript shows startup output, console input, console output, and the pass/fail result for every command that ran.
