# UI Test Plan

This file is the source of truth for console UI test cases run with the `test-ui` skill.

## Test configuration

- Compilation command: `.\gradlew.bat classes`
- Compilation working directory: `C:\Users\joshu\Code\ip`
- Default program start command: `java -ea -cp ..\..\build\classes\java\main lloyd.Lloyd`
- Default UI working directory: `C:\Users\joshu\Code\ip\_temp\ui-case`
- Java version: 25.
- Session isolation: Start each test case in a fresh process.
- Storage setup: Before each default Lloyd UI case, use a fresh working directory
  without a `data` directory. A case can provide seeded data explicitly.
- Comparison: Exact text after normalizing CRLF and LF line endings. Spaces, capitalization, punctuation, divider lines, and blank lines are significant.
- Indentation used to format examples in chat is not part of the expected output.

## Test cases

### STORAGE-001: Save and load all task types

**Aim:** Verify that the standalone storage class initializes missing storage,
writes the specified delimited format safely, restores task data, preserves an
existing file after rejected data, and reports malformed data and write failures.

**Compilation command:**

```powershell
$storageSources = @(Get-ChildItem 'src\main\java\lloyd\task' -Filter '*.java')
$storageSources += Get-Item 'src\main\java\lloyd\storage\Storage.java'
$storageSources += Get-Item 'test\StorageTest.java'
javac -d out $storageSources.FullName
```

**Program start command:** `java -ea -cp "out;build\classes\java\main" lloyd.storage.StorageTest`

**Expected startup output:**

```text
Storage save format: PASSED
Storage load: PASSED
Missing storage initialization: PASSED
Invalid storage data: PASSED
Rejected save preservation: PASSED
Storage write failure reporting: PASSED
Reserved delimiter validation: PASSED
```

This test has no user input. The process must exit successfully immediately after printing the expected output.

### UI-009: Save after every successful task-list change

**Aim:** Verify that the chatbot writes the complete task list after adding, marking,
unmarking, and deleting tasks.

**Working directory:** `C:\Users\joshu\Code\ip\_temp\ui-save-test`

**Program start command:** `java -ea -cp ..\..\build\classes\java\main lloyd.Lloyd`

**Storage setup:** Ensure the working directory does not contain a `data` directory.

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
todo survey land
```

**Expected output:**

```text
____________________________________________________________
Task added:
[T][ ] survey land
Total tasks: 1.
____________________________________________________________

```

**Expected `data/lloyd.txt`:**

```text
T | 0 | survey land
```

#### Step 2

**Input:**

```text
deadline build bridge /by 29/08/2026
```

**Expected output:**

```text
____________________________________________________________
Task added:
[D][ ] build bridge (by: Aug 29 2026)
Total tasks: 2.
____________________________________________________________

```

**Expected `data/lloyd.txt`:**

```text
T | 0 | survey land
D | 0 | build bridge | 2026-08-29
```

#### Step 3

**Input:**

```text
mark 2
```

**Expected output:**

```text
____________________________________________________________
Task completed:
[D][X] build bridge (by: Aug 29 2026)
____________________________________________________________

```

**Expected `data/lloyd.txt`:**

```text
T | 0 | survey land
D | 1 | build bridge | 2026-08-29
```

#### Step 4

**Input:**

```text
event opening ceremony /from 31/08/2026 0900 /to 31/08/2026 1000
```

**Expected output:**

```text
____________________________________________________________
Task added:
[E][ ] opening ceremony (from: Aug 31 2026, 9:00 AM to: Aug 31 2026, 10:00 AM)
Total tasks: 3.
____________________________________________________________

```

**Expected `data/lloyd.txt`:**

```text
T | 0 | survey land
D | 1 | build bridge | 2026-08-29
E | 0 | opening ceremony | 2026-08-31T09:00 | 2026-08-31T10:00
```

#### Step 5

**Input:**

```text
unmark 2
```

**Expected output:**

```text
____________________________________________________________
Task marked as incomplete:
[D][ ] build bridge (by: Aug 29 2026)
____________________________________________________________

```

**Expected `data/lloyd.txt`:**

```text
T | 0 | survey land
D | 0 | build bridge | 2026-08-29
E | 0 | opening ceremony | 2026-08-31T09:00 | 2026-08-31T10:00
```

#### Step 6

**Input:**

```text
delete 1
```

**Expected output:**

```text
____________________________________________________________
Task deleted:
[T][ ] survey land
Total tasks: 2.
____________________________________________________________

```

**Expected `data/lloyd.txt`:**

```text
D | 0 | build bridge | 2026-08-29
E | 0 | opening ceremony | 2026-08-31T09:00 | 2026-08-31T10:00
```

#### Step 7

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-018: Display help without changing stored tasks

**Aim:** Verify that `help` displays every supported command and its format
without changing existing tasks or ending the session.

**Working directory:** `C:\Users\joshu\Code\ip\_temp\ui-help-test`

**Program start command:** `java -ea -cp ..\..\build\classes\java\main lloyd.Lloyd`

**Initial `data/lloyd.txt`:**

```text
T | 0 | inspect foundations
```

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
help
```

**Expected output:**

```text
____________________________________________________________
Here's what I can do:

Add tasks:
  todo DESCRIPTION
  deadline DESCRIPTION /by DD/MM/YYYY
  event DESCRIPTION /from DD/MM/YYYY HHMM /to DD/MM/YYYY HHMM

View tasks:
  list
  find KEYWORD
  check DD/MM/YYYY
  reminder

Manage tasks:
  mark TASK_NUMBER
  unmark TASK_NUMBER
  delete TASK_NUMBER

Other commands:
  help [COMMAND]
  bye

Dates use DD/MM/YYYY.
Times use the 24-hour HHMM format; for example, 1430 means 2:30 PM.
____________________________________________________________

```

**Expected unchanged `data/lloyd.txt`:**

```text
T | 0 | inspect foundations
```

#### Step 2

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Tasks:
1. [T][ ] inspect foundations
____________________________________________________________

```

#### Step 3

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-019: Display command-specific help

**Aim:** Verify that `help COMMAND` displays detailed guidance for supported
commands, rejects unknown topics, and does not change the task list or end the session.

**Working directory:** `C:\Users\joshu\Code\ip\_temp\ui-help-arguments-test`

**Program start command:** `java -ea -cp ..\..\build\classes\java\main lloyd.Lloyd`

**Storage setup:** Ensure the working directory does not contain a `data` directory.

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
help todo
```

**Expected output:**

```text
____________________________________________________________
todo - Add a task without a date or time.

Format:
  todo DESCRIPTION

Example:
  todo read book
____________________________________________________________

```

#### Step 2

**Input:**

```text
help event
```

**Expected output:**

```text
____________________________________________________________
event - Add an event with a start and end date and time.

Format:
  event DESCRIPTION /from DD/MM/YYYY HHMM /to DD/MM/YYYY HHMM

Example:
  event project meeting /from 06/08/2026 1400 /to 06/08/2026 1600
____________________________________________________________

```

#### Step 3

**Input:**

```text
help tasks
```

**Expected output:**

```text
____________________________________________________________
No help is available for: tasks
Available help topics: todo, deadline, event, list, find, check, reminder, mark, unmark, delete, help, bye
____________________________________________________________

```

#### Step 4

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Your task list is empty.
____________________________________________________________

```

#### Step 5

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-015: Show overdue reminders without changing stored tasks

**Aim:** Verify that `reminder` displays incomplete dated tasks in date order,
uses their original task numbers, and excludes todos, completed tasks, and
distant future tasks without changing storage.

**Working directory:** `C:\Users\joshu\Code\ip\_temp\ui-reminder-overdue-test`

**Program start command:** `java -ea -cp ..\..\build\classes\java\main lloyd.Lloyd`

**Initial `data/lloyd.txt`:**

```text
T | 0 | buy supplies
D | 0 | pay invoice | 2000-01-01
D | 1 | completed permit | 2000-01-02
E | 0 | old site visit | 2000-01-03T09:00 | 2000-01-03T10:00
D | 0 | distant project | 9999-12-31
```

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
reminder
```

**Expected output:**

```text
____________________________________________________________
Reminders:
Overdue:
2. [D][ ] pay invoice (by: Jan 1 2000)
4. [E][ ] old site visit (from: Jan 3 2000, 9:00 AM to: Jan 3 2000, 10:00 AM)
____________________________________________________________

```

**Expected unchanged `data/lloyd.txt`:**

```text
T | 0 | buy supplies
D | 0 | pay invoice | 2000-01-01
D | 1 | completed permit | 2000-01-02
E | 0 | old site visit | 2000-01-03T09:00 | 2000-01-03T10:00
D | 0 | distant project | 9999-12-31
```

#### Step 2

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-016: Report when no tasks need reminders

**Aim:** Verify that `reminder` reports an empty result when the task list has
no incomplete dated tasks that are overdue or due soon.

**Working directory:** `C:\Users\joshu\Code\ip\_temp\ui-reminder-empty-test`

**Program start command:** `java -ea -cp ..\..\build\classes\java\main lloyd.Lloyd`

**Initial `data/lloyd.txt`:**

```text
T | 0 | buy supplies
D | 1 | completed permit | 2000-01-02
D | 0 | distant project | 9999-12-31
```

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
reminder
```

**Expected output:**

```text
____________________________________________________________
No urgent tasks! You have no overdue tasks or tasks due within the next 3 days.
____________________________________________________________

```

#### Step 2

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-017: Reject reminder arguments

**Aim:** Verify that reminder-window arguments are rejected without changing
the task list.

**Working directory:** `C:\Users\joshu\Code\ip\_temp\ui-reminder-arguments-test`

**Program start command:** `java -ea -cp ..\..\build\classes\java\main lloyd.Lloyd`

**Storage setup:** Ensure the working directory does not contain a `data` directory.

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
reminder 5
```

**Expected output:**

```text
____________________________________________________________
Enter reminder without additional information.
____________________________________________________________

```

#### Step 2

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Your task list is empty.
____________________________________________________________

```

#### Step 3

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-010: Load saved tasks when the chatbot starts

**Aim:** Verify that the chatbot loads saved todos, deadlines, and events in order,
including their completion states and task-specific information.

**Working directory:** `C:\Users\joshu\Code\ip\_temp\ui-load-test`

**Program start command:** `java -ea -cp ..\..\build\classes\java\main lloyd.Lloyd`

**Initial `data/lloyd.txt`:**

```text
T | 1 | read book
D | 0 | return book | 2026-06-06
E | 0 | project meeting | 2026-08-06T14:00 | 2026-08-06T16:00
```

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Tasks:
1. [T][X] read book
2. [D][ ] return book (by: Jun 6 2026)
3. [E][ ] project meeting (from: Aug 6 2026, 2:00 PM to: Aug 6 2026, 4:00 PM)
____________________________________________________________

```

#### Step 2

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-011: Recover from missing storage and rejected task data

**Aim:** Verify that startup creates missing storage, a todo without a description
is rejected, and unsavable task data is rolled back without terminating the chatbot.

**Working directory:** `C:\Users\joshu\Code\ip\_temp\ui-storage-errors`

**Program start command:** `java -ea -cp ..\..\build\classes\java\main lloyd.Lloyd`

**Storage setup:** Ensure the working directory does not contain a `data` directory.

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
todo
```

**Expected output:**

```text
____________________________________________________________
Enter a task description.
Example: todo read book
____________________________________________________________

```

#### Step 2

**Input:**

```text
todo compare A | B
```

**Expected output:**

```text
____________________________________________________________
Unable to save the change; your task list was not changed.
Check that data/lloyd.txt is writable and task details do not contain |.
____________________________________________________________

```

#### Step 3

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Your task list is empty.
____________________________________________________________

```

**Expected `data/lloyd.txt`:** An empty file.

#### Step 4

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-012: Report malformed saved data cleanly

**Aim:** Verify that malformed task data produces a clear error without a Java
stack trace or an accidental overwrite of the storage file.

**Working directory:** `C:\Users\joshu\Code\ip\_temp\ui-corrupt-storage`

**Program start command:** `java -ea -cp ..\..\build\classes\java\main lloyd.Lloyd`

**Initial `data/lloyd.txt`:**

```text
T | 2 | invalid status
```

**Expected startup output:** Same as UI-001.

**Expected output immediately after startup:**

```text
____________________________________________________________
Unable to load tasks from data/lloyd.txt.
Check that the file is readable and contains valid task data.
____________________________________________________________

```

The process must exit successfully without accepting input, and the seeded file
must remain unchanged.

### UI-001: Add and list all task types

**Aim:** Verify that todo, deadline, and event commands create the correct task subtype, preserve their details, update the count, and appear correctly in the list.

**Expected startup output:**

```text
____________________________________________________________
 _      _                 _
| |    | |               | |
| |    | | ___  _   _  __| |
| |    | |/ _ \| | | |/ _` |
| |____| | (_) | |_| | (_| |
|______|_|\___/ \__, |\__,_|
                 __/ |       
                |___/        
Lloyd Frontera, the greatest estate developer, at your service!
Got a problem? Excellent. Problems are profits waiting for an engineer.
Now, what needs doing?
____________________________________________________________

```

#### Step 1

**Input:**

```text
todo read book
```

**Expected output:**

```text
____________________________________________________________
Task added:
[T][ ] read book
Total tasks: 1.
____________________________________________________________

```

#### Step 2

**Input:**

```text
mark 1
```

**Expected output:**

```text
____________________________________________________________
Task completed:
[T][X] read book
____________________________________________________________

```

#### Step 3

**Input:**

```text
deadline return book /by 06/06/2026
```

**Expected output:**

```text
____________________________________________________________
Task added:
[D][ ] return book (by: Jun 6 2026)
Total tasks: 2.
____________________________________________________________

```

#### Step 4

**Input:**

```text
event project meeting /from 06/08/2026 1400 /to 06/08/2026 1600
```

**Expected output:**

```text
____________________________________________________________
Task added:
[E][ ] project meeting (from: Aug 6 2026, 2:00 PM to: Aug 6 2026, 4:00 PM)
Total tasks: 3.
____________________________________________________________

```

#### Step 5

**Input:**

```text
todo join sports club
```

**Expected output:**

```text
____________________________________________________________
Task added:
[T][ ] join sports club
Total tasks: 4.
____________________________________________________________

```

#### Step 6

**Input:**

```text
mark 4
```

**Expected output:**

```text
____________________________________________________________
Task completed:
[T][X] join sports club
____________________________________________________________

```

#### Step 7

**Input:**

```text
todo borrow book
```

**Expected output:**

```text
____________________________________________________________
Task added:
[T][ ] borrow book
Total tasks: 5.
____________________________________________________________

```

#### Step 8

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Tasks:
1. [T][X] read book
2. [D][ ] return book (by: Jun 6 2026)
3. [E][ ] project meeting (from: Aug 6 2026, 2:00 PM to: Aug 6 2026, 4:00 PM)
4. [T][X] join sports club
5. [T][ ] borrow book
____________________________________________________________

```

#### Step 9

**Input:**

```text
deadline return book /by 30/08/2026
```

**Expected output:**

```text
____________________________________________________________
Task added:
[D][ ] return book (by: Aug 30 2026)
Total tasks: 6.
____________________________________________________________

```

#### Step 10

**Input:**

```text
event project meeting /from 07/08/2026 1400 /to 07/08/2026 1600
```

**Expected output:**

```text
____________________________________________________________
Task added:
[E][ ] project meeting (from: Aug 7 2026, 2:00 PM to: Aug 7 2026, 4:00 PM)
Total tasks: 7.
____________________________________________________________

```

#### Step 11

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-014: Find tasks containing a keyword

**Aim:** Verify that `find` returns every task whose description contains the
keyword, preserves their order, and rejects a missing keyword without changing
the task list.

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
todo read book
```

**Expected output:**

```text
____________________________________________________________
Task added:
[T][ ] read book
Total tasks: 1.
____________________________________________________________

```

#### Step 2

**Input:**

```text
deadline return book /by 02/09/2026
```

**Expected output:**

```text
____________________________________________________________
Task added:
[D][ ] return book (by: Sep 2 2026)
Total tasks: 2.
____________________________________________________________

```

#### Step 3

**Input:**

```text
todo write essay
```

**Expected output:**

```text
____________________________________________________________
Task added:
[T][ ] write essay
Total tasks: 3.
____________________________________________________________

```

#### Step 4

**Input:**

```text
find book
```

**Expected output:**

```text
____________________________________________________________
Matching tasks:
1. [T][ ] read book
2. [D][ ] return book (by: Sep 2 2026)
____________________________________________________________

```

#### Step 5

**Input:**

```text
find bridge
```

**Expected output:**

```text
____________________________________________________________
No tasks contain the keyword: bridge
____________________________________________________________

```

#### Step 6

**Input:**

```text
find
```

**Expected output:**

```text
____________________________________________________________
Enter a keyword.
Example: find book
____________________________________________________________

```

#### Step 7

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-013: Check deadlines and event endpoints by date

**Aim:** Verify that `check` lists deadlines due on the requested date and events
that start or end on that date, while excluding todos and dates strictly between
the endpoints of a multi-day event. Also verify missing, invalid, and empty-result
responses.

**Working directory:** `C:\Users\joshu\Code\ip\_temp\ui-check-test`

**Program start command:** `java -ea -cp ..\..\build\classes\java\main lloyd.Lloyd`

**Initial `data/lloyd.txt`:**

```text
T | 0 | inspect foundations
D | 0 | submit permit | 2026-08-29
D | 0 | pay supplier | 2026-08-30
E | 0 | council meeting | 2026-08-29T09:00 | 2026-08-29T10:00
E | 0 | building works | 2026-08-29T08:00 | 2026-09-02T18:00
E | 0 | site survey | 2026-08-27T08:00 | 2026-08-29T18:00
```

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
check 29/08/2026
```

**Expected output:**

```text
____________________________________________________________
Schedule for Aug 29 2026:
2. [D][ ] submit permit (by: Aug 29 2026)
4. [E][ ] council meeting (from: Aug 29 2026, 9:00 AM to: Aug 29 2026, 10:00 AM)
5. [E][ ] building works (from: Aug 29 2026, 8:00 AM to: Sep 2 2026, 6:00 PM)
6. [E][ ] site survey (from: Aug 27 2026, 8:00 AM to: Aug 29 2026, 6:00 PM)
____________________________________________________________

```

#### Step 2

**Input:**

```text
check 30/08/2026
```

**Expected output:**

```text
____________________________________________________________
Schedule for Aug 30 2026:
3. [D][ ] pay supplier (by: Aug 30 2026)
____________________________________________________________

```

#### Step 3

**Input:**

```text
check 31/08/2026
```

**Expected output:**

```text
____________________________________________________________
No deadlines or event endpoints fall on Aug 31 2026.
____________________________________________________________

```

#### Step 4

**Input:**

```text
check
```

**Expected output:**

```text
____________________________________________________________
Enter a date using DD/MM/YYYY (day/month/year).
Example: check 06/08/2026
____________________________________________________________

```

#### Step 5

**Input:**

```text
check 31/02/2026
```

**Expected output:**

```text
____________________________________________________________
Enter a date using DD/MM/YYYY (day/month/year).
Example: check 06/08/2026
____________________________________________________________

```

#### Step 6

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-002: Reject an invalid deadline date

**Aim:** Verify that a deadline not written in `dd/MM/yyyy` format is rejected
without changing the task list.

**Expected startup output:** Same as UI-001.

The first seven setup interactions and their expected outputs are Steps 1–7 of UI-001. They establish five tasks in a fresh process.

#### Step 8

**Input:**

```text
deadline do homework /by no idea :-p
```

**Expected output:**

```text
____________________________________________________________
Enter a deadline in this format:
deadline DESCRIPTION /by DD/MM/YYYY
Date format: DD/MM/YYYY (day/month/year).
Example: deadline submit report /by 06/08/2026
____________________________________________________________

```

#### Step 9

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-003: Show themed command and validation responses

**Aim:** Verify that unmarking and invalid command details produce informative responses without changing task state.

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
todo inspect foundations
```

**Expected output:**

```text
____________________________________________________________
Task added:
[T][ ] inspect foundations
Total tasks: 1.
____________________________________________________________

```

#### Step 2

**Input:**

```text
unmark 1
```

**Expected output:**

```text
____________________________________________________________
Task marked as incomplete:
[T][ ] inspect foundations
____________________________________________________________

```

#### Step 3

**Input:**

```text
mark
```

**Expected output:**

```text
____________________________________________________________
Enter a task number.
Example: mark 1
____________________________________________________________

```

#### Step 4

**Input:**

```text
mark gold
```

**Expected output:**

```text
____________________________________________________________
Enter a numeric task number.
____________________________________________________________

```

#### Step 5

**Input:**

```text
mark 99
```

**Expected output:**

```text
____________________________________________________________
Enter the number of an existing task.
____________________________________________________________

```

#### Step 6

**Input:**

```text
unmark
```

**Expected output:**

```text
____________________________________________________________
Enter a task number.
Example: unmark 1
____________________________________________________________

```

#### Step 7

**Input:**

```text
deadline
```

**Expected output:**

```text
____________________________________________________________
Enter a deadline in this format:
deadline DESCRIPTION /by DD/MM/YYYY
Date format: DD/MM/YYYY (day/month/year).
Example: deadline submit report /by 06/08/2026
____________________________________________________________

```

#### Step 8

**Input:**

```text
deadline build bridge tomorrow
```

**Expected output:**

```text
____________________________________________________________
Enter a deadline in this format:
deadline DESCRIPTION /by DD/MM/YYYY
Date format: DD/MM/YYYY (day/month/year).
Example: deadline submit report /by 06/08/2026
____________________________________________________________

```

#### Step 9

**Input:**

```text
event
```

**Expected output:**

```text
____________________________________________________________
Enter an event in this format:
event DESCRIPTION /from DD/MM/YYYY HHMM /to DD/MM/YYYY HHMM
Date and time format: DD/MM/YYYY HHMM (day/month/year, 24-hour time).
Example: event project meeting /from 06/08/2026 1400 /to 06/08/2026 1600
____________________________________________________________

```

#### Step 10

**Input:**

```text
event grand opening tomorrow
```

**Expected output:**

```text
____________________________________________________________
Enter an event in this format:
event DESCRIPTION /from DD/MM/YYYY HHMM /to DD/MM/YYYY HHMM
Date and time format: DD/MM/YYYY HHMM (day/month/year, 24-hour time).
Example: event project meeting /from 06/08/2026 1400 /to 06/08/2026 1600
____________________________________________________________

```

#### Step 11

**Input:**

```text
event project meeting /from tomorrow /to later
```

**Expected output:**

```text
____________________________________________________________
Enter an event in this format:
event DESCRIPTION /from DD/MM/YYYY HHMM /to DD/MM/YYYY HHMM
Date and time format: DD/MM/YYYY HHMM (day/month/year, 24-hour time).
Example: event project meeting /from 06/08/2026 1400 /to 06/08/2026 1600
____________________________________________________________

```

#### Step 12

**Input:**

```text
event project meeting /from 06/08/2026 1600 /to 06/08/2026 1400
```

**Expected output:**

```text
____________________________________________________________
Enter an event end time that is not before its start.
Example: event project meeting /from 06/08/2026 1400 /to 06/08/2026 1600
____________________________________________________________

```

#### Step 13

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-004: Reject tasks without a type prefix

**Aim:** Verify that input without a `todo`, `deadline`, or `event` prefix is rejected and is not added to the task list.

**Working directory:** `C:\Users\joshu\Code\ip\_temp\ui-invalid-prefix-test`

**Storage setup:** Use a fresh working directory without a `data` directory.

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
inspect foundations
```

**Expected output:**

```text
____________________________________________________________
Enter a valid command.
Commands: todo, deadline, event, list, find, check, reminder, mark, unmark, delete, help, bye
____________________________________________________________

```

#### Step 2

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Your task list is empty.
____________________________________________________________

```

#### Step 3

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-005: Preserve the task list after invalid creation commands

**Aim:** Verify that malformed deadline and event commands, plus unprefixed input, are rejected without changing the number, order, or contents of valid tasks.

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
todo survey land
```

**Expected output:**

```text
____________________________________________________________
Task added:
[T][ ] survey land
Total tasks: 1.
____________________________________________________________

```

#### Step 2

**Input:**

```text
deadline build bridge tomorrow
```

**Expected output:**

```text
____________________________________________________________
Enter a deadline in this format:
deadline DESCRIPTION /by DD/MM/YYYY
Date format: DD/MM/YYYY (day/month/year).
Example: deadline submit report /by 06/08/2026
____________________________________________________________

```

#### Step 3

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Tasks:
1. [T][ ] survey land
____________________________________________________________

```

#### Step 4

**Input:**

```text
deadline build bridge /by 29/08/2026
```

**Expected output:**

```text
____________________________________________________________
Task added:
[D][ ] build bridge (by: Aug 29 2026)
Total tasks: 2.
____________________________________________________________

```

#### Step 5

**Input:**

```text
event opening ceremony /from Monday
```

**Expected output:**

```text
____________________________________________________________
Enter an event in this format:
event DESCRIPTION /from DD/MM/YYYY HHMM /to DD/MM/YYYY HHMM
Date and time format: DD/MM/YYYY HHMM (day/month/year, 24-hour time).
Example: event project meeting /from 06/08/2026 1400 /to 06/08/2026 1600
____________________________________________________________

```

#### Step 6

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Tasks:
1. [T][ ] survey land
2. [D][ ] build bridge (by: Aug 29 2026)
____________________________________________________________

```

#### Step 7

**Input:**

```text
event opening ceremony /from  /to Tuesday
```

**Expected output:**

```text
____________________________________________________________
Enter an event in this format:
event DESCRIPTION /from DD/MM/YYYY HHMM /to DD/MM/YYYY HHMM
Date and time format: DD/MM/YYYY HHMM (day/month/year, 24-hour time).
Example: event project meeting /from 06/08/2026 1400 /to 06/08/2026 1600
____________________________________________________________

```

#### Step 8

**Input:**

```text
event opening ceremony /from 31/08/2026 0900 /to 31/08/2026 1000
```

**Expected output:**

```text
____________________________________________________________
Task added:
[E][ ] opening ceremony (from: Aug 31 2026, 9:00 AM to: Aug 31 2026, 10:00 AM)
Total tasks: 3.
____________________________________________________________

```

#### Step 9

**Input:**

```text
build tunnel
```

**Expected output:**

```text
____________________________________________________________
Enter a valid command.
Commands: todo, deadline, event, list, find, check, reminder, mark, unmark, delete, help, bye
____________________________________________________________

```

#### Step 10

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Tasks:
1. [T][ ] survey land
2. [D][ ] build bridge (by: Aug 29 2026)
3. [E][ ] opening ceremony (from: Aug 31 2026, 9:00 AM to: Aug 31 2026, 10:00 AM)
____________________________________________________________

```

#### Step 11

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-006: Preserve completion state after invalid updates

**Aim:** Verify that invalid task numbers and nonnumeric values do not mark or unmark a valid task, while valid updates still work between the rejected commands.

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
todo inspect walls
```

**Expected output:**

```text
____________________________________________________________
Task added:
[T][ ] inspect walls
Total tasks: 1.
____________________________________________________________

```

#### Step 2

**Input:**

```text
mark 1
```

**Expected output:**

```text
____________________________________________________________
Task completed:
[T][X] inspect walls
____________________________________________________________

```

#### Step 3

**Input:**

```text
mark 0
```

**Expected output:**

```text
____________________________________________________________
Enter the number of an existing task.
____________________________________________________________

```

#### Step 4

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Tasks:
1. [T][X] inspect walls
____________________________________________________________

```

#### Step 5

**Input:**

```text
unmark gold
```

**Expected output:**

```text
____________________________________________________________
Enter a numeric task number.
____________________________________________________________

```

#### Step 6

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Tasks:
1. [T][X] inspect walls
____________________________________________________________

```

#### Step 7

**Input:**

```text
unmark 1
```

**Expected output:**

```text
____________________________________________________________
Task marked as incomplete:
[T][ ] inspect walls
____________________________________________________________

```

#### Step 8

**Input:**

```text
unmark 2
```

**Expected output:**

```text
____________________________________________________________
Enter the number of an existing task.
____________________________________________________________

```

#### Step 9

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Tasks:
1. [T][ ] inspect walls
____________________________________________________________

```

#### Step 10

**Input:**

```text
mark -1
```

**Expected output:**

```text
____________________________________________________________
Enter the number of an existing task.
____________________________________________________________

```

#### Step 11

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Tasks:
1. [T][ ] inspect walls
____________________________________________________________

```

#### Step 12

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-007: Delete a task and renumber the remaining list

**Aim:** Verify that deleting a task removes the selected object, reports the new task count, and shifts later tasks into consecutive list positions without changing their contents.

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
todo survey land
```

**Expected output:**

```text
____________________________________________________________
Task added:
[T][ ] survey land
Total tasks: 1.
____________________________________________________________

```

#### Step 2

**Input:**

```text
deadline build bridge /by 29/08/2026
```

**Expected output:**

```text
____________________________________________________________
Task added:
[D][ ] build bridge (by: Aug 29 2026)
Total tasks: 2.
____________________________________________________________

```

#### Step 3

**Input:**

```text
event opening ceremony /from 31/08/2026 0900 /to 31/08/2026 1000
```

**Expected output:**

```text
____________________________________________________________
Task added:
[E][ ] opening ceremony (from: Aug 31 2026, 9:00 AM to: Aug 31 2026, 10:00 AM)
Total tasks: 3.
____________________________________________________________

```

#### Step 4

**Input:**

```text
mark 2
```

**Expected output:**

```text
____________________________________________________________
Task completed:
[D][X] build bridge (by: Aug 29 2026)
____________________________________________________________

```

#### Step 5

**Input:**

```text
delete 2
```

**Expected output:**

```text
____________________________________________________________
Task deleted:
[D][X] build bridge (by: Aug 29 2026)
Total tasks: 2.
____________________________________________________________

```

#### Step 6

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Tasks:
1. [T][ ] survey land
2. [E][ ] opening ceremony (from: Aug 31 2026, 9:00 AM to: Aug 31 2026, 10:00 AM)
____________________________________________________________

```

#### Step 7

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```

### UI-008: Reject invalid delete commands without changing the list

**Aim:** Verify that a missing, nonnumeric, zero, negative, or out-of-range task number is rejected and does not remove an existing task.

**Expected startup output:** Same as UI-001.

#### Step 1

**Input:**

```text
todo inspect foundations
```

**Expected output:**

```text
____________________________________________________________
Task added:
[T][ ] inspect foundations
Total tasks: 1.
____________________________________________________________

```

#### Step 2

**Input:**

```text
delete
```

**Expected output:**

```text
____________________________________________________________
Enter a task number.
Example: delete 1
____________________________________________________________

```

#### Step 3

**Input:**

```text
delete gold
```

**Expected output:**

```text
____________________________________________________________
Enter a numeric task number.
____________________________________________________________

```

#### Step 4

**Input:**

```text
delete 0
```

**Expected output:**

```text
____________________________________________________________
Enter the number of an existing task.
____________________________________________________________

```

#### Step 5

**Input:**

```text
delete -1
```

**Expected output:**

```text
____________________________________________________________
Enter the number of an existing task.
____________________________________________________________

```

#### Step 6

**Input:**

```text
delete 2
```

**Expected output:**

```text
____________________________________________________________
Enter the number of an existing task.
____________________________________________________________

```

#### Step 7

**Input:**

```text
list
```

**Expected output:**

```text
____________________________________________________________
Tasks:
1. [T][ ] inspect foundations
____________________________________________________________

```

#### Step 8

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
Goodbye! Your tasks will be waiting.
____________________________________________________________

```
