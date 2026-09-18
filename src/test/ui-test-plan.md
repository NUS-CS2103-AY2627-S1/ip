# Console UI Test Plan

## How to run

Use Java 25. Compile the application with Gradle so that its JavaFX dependencies
are available, then run the console `Sandrone` class from the compiled output.
A typical test command is:

```bash
TEST_UI_JAVA_HOME=$(/usr/libexec/java_home -v 25)
PROJECT_ROOT=$(pwd)
./gradlew classes
TEST_DIR=$(mktemp -d)
(cd "$TEST_DIR" && "$TEST_UI_JAVA_HOME/bin/java" -cp "$PROJECT_ROOT/build/classes/java/main" sandrone.Sandrone)
```

Run every test case in a fresh process. Compare its complete output exactly,
including spaces and blank lines. Use a separate empty `TEST_DIR` for each test
case so that saved task data does not affect another test.

## Test cases

### TC-02: Manage and display every task type

**Aim:** Verify that the user can add todo, deadline, and event tasks; mark and
unmark a task; and list all saved-in-memory tasks.

**Inputs:**

```text
todo read book
deadline return book /by 29/8/2026 1430
event project meeting /from 30/8/2026 1400 /to 30/8/2026 1600
mark 1
unmark 1
list
bye
```

**Expected output:**

```text
____________________________________________________________
 SSSS    A   N   N DDDD  RRRR   OOO  N   N EEEEE
S       A A  NN  N D   D R   R O   O NN  N E
 SSS   AAAAA N N N D   D RRRR  O   O N N N EEEE
    S  A   A N  NN D   D R R   O   O N  NN E
SSSS   A   A N   N DDDD  R  RR  OOO  N   N EEEEE

Sandrone, Marionette.
Where, when, how. Go.
Facts are objective. Your records, however, require maintenance. State your task.
____________________________________________________________

____________________________________________________________
 Recorded. Do try not to make me repeat the process.
 todo read book
You now have 1 task in the list
____________________________________________________________

____________________________________________________________
 Recorded. Do try not to make me repeat the process.
 deadline return book /by 29/8/2026 1430
You now have 2 tasks in the list
____________________________________________________________

____________________________________________________________
 Recorded. Do try not to make me repeat the process.
 event project meeting /from 30/8/2026 1400 /to 30/8/2026 1600
You now have 3 tasks in the list
____________________________________________________________

____________________________________________________________
 Status updated. An acceptable result:
   [X] read book
____________________________________________________________

____________________________________________________________
 Status updated. The record has been reopened:
   [ ] read book
____________________________________________________________

____________________________________________________________
 Your records are below. Review them carefully.
 1.[T][ ] read book
 2.[D][ ] return book (by: 29/8/2026 2:30PM)
 3.[E][ ] project meeting (from: 30/8/2026 2:00PM to: 30/8/2026 4:00PM)
____________________________________________________________

____________________________________________________________
Your records have been preserved. Do not lose them.
____________________________________________________________

```

### TC-03: Load tasks saved by an earlier session

**Aim:** Verify that a fresh chatbot process restores todo, deadline, and event
tasks, including their completion status, from `data/tasks.txt`.

**Setup:** Before starting the tested chatbot process, ensure that
`TEST_DIR/data/tasks.txt` already contains the following saved tasks. One way
to create this file is to run the listed preliminary session in the same
`TEST_DIR`:

```text
T | 0 | read book
D | 1 | return book | 29/8/2026 2:30PM
E | 0 | project meeting | 30/8/2026 2:00PM | 30/8/2026 4:00PM
```

Preliminary session to create that file through the application:

```text
todo read book
deadline return book /by 29/8/2026 1430
event project meeting /from 30/8/2026 1400 /to 30/8/2026 1600
mark 2
bye
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
____________________________________________________________
 SSSS    A   N   N DDDD  RRRR   OOO  N   N EEEEE
S       A A  NN  N D   D R   R O   O NN  N E
 SSS   AAAAA N N N D   D RRRR  O   O N N N EEEE
    S  A   A N  NN D   D R R   O   O N  NN E
SSSS   A   A N   N DDDD  R  RR  OOO  N   N EEEEE

Sandrone, Marionette.
Where, when, how. Go.
Facts are objective. Your records, however, require maintenance. State your task.
____________________________________________________________

____________________________________________________________
 Your records are below. Review them carefully.
 1.[T][ ] read book
 2.[D][X] return book (by: 29/8/2026 2:30PM)
 3.[E][ ] project meeting (from: 30/8/2026 2:00PM to: 30/8/2026 4:00PM)
____________________________________________________________

____________________________________________________________
Your records have been preserved. Do not lose them.
____________________________________________________________

```

### TC-01: Exit after the welcome message

**Aim:** Verify that the application welcomes the user and exits politely when
the user enters `bye`.

**Inputs:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
 SSSS    A   N   N DDDD  RRRR   OOO  N   N EEEEE
S       A A  NN  N D   D R   R O   O NN  N E
 SSS   AAAAA N N N D   D RRRR  O   O N N N EEEE
    S  A   A N  NN D   D R R   O   O N  NN E
SSSS   A   A N   N DDDD  R  RR  OOO  N   N EEEEE

Sandrone, Marionette.
Where, when, how. Go.
Facts are objective. Your records, however, require maintenance. State your task.
____________________________________________________________

____________________________________________________________
Your records have been preserved. Do not lose them.
____________________________________________________________

```

### TC-04: Reject malformed commands safely

**Aim:** Verify that incomplete task details, invalid task numbers, unsupported
save-format characters, and unknown commands show an error without terminating
the chatbot.

**Inputs:**

```text
todo
deadline submit report
event discussion /from tomorrow
mark banana
unmark 0
remove 2
todo wrong | data
nonsense
bye
```

**Expected output:**

```text
____________________________________________________________
 SSSS    A   N   N DDDD  RRRR   OOO  N   N EEEEE
S       A A  NN  N D   D R   R O   O NN  N E
 SSS   AAAAA N N N D   D RRRR  O   O N N N EEEE
    S  A   A N  NN D   D R R   O   O N  NN E
SSSS   A   A N   N DDDD  R  RR  OOO  N   N EEEEE

Sandrone, Marionette.
Where, when, how. Go.
Facts are objective. Your records, however, require maintenance. State your task.
____________________________________________________________

____________________________________________________________
That input does not form a valid instruction. Correct it: Description cannot be empty
____________________________________________________________

____________________________________________________________
That input does not form a valid instruction. Correct it: Deadline must include /by followed by a time
____________________________________________________________

____________________________________________________________
That input does not form a valid instruction. Correct it: Event must include /from and /to times
____________________________________________________________

____________________________________________________________
That input does not form a valid instruction. Correct it: Task number must be a positive whole number
____________________________________________________________

____________________________________________________________
That input does not form a valid instruction. Correct it: Invalid task index
____________________________________________________________

____________________________________________________________
That input does not form a valid instruction. Correct it: Invalid task index
____________________________________________________________

____________________________________________________________
That input does not form a valid instruction. Correct it: Description cannot contain |
____________________________________________________________

____________________________________________________________
That input does not form a valid instruction. Correct it: Invalid command
____________________________________________________________

____________________________________________________________
Your records have been preserved. Do not lose them.
____________________________________________________________

```

### TC-05: Find tasks by description keyword

**Aim:** Verify that a case-insensitive keyword search displays only matching
tasks.

**Inputs:**

```text
todo read book
deadline return book /by 29/8/2026 1430
todo write report
mark 1
find BOOK
bye
```

**Expected output:**

```text
____________________________________________________________
 SSSS    A   N   N DDDD  RRRR   OOO  N   N EEEEE
S       A A  NN  N D   D R   R O   O NN  N E
 SSS   AAAAA N N N D   D RRRR  O   O N N N EEEE
    S  A   A N  NN D   D R R   O   O N  NN E
SSSS   A   A N   N DDDD  R  RR  OOO  N   N EEEEE

Sandrone, Marionette.
Where, when, how. Go.
Facts are objective. Your records, however, require maintenance. State your task.
____________________________________________________________

____________________________________________________________
 Recorded. Do try not to make me repeat the process.
 todo read book
You now have 1 task in the list
____________________________________________________________

____________________________________________________________
 Recorded. Do try not to make me repeat the process.
 deadline return book /by 29/8/2026 1430
You now have 2 tasks in the list
____________________________________________________________

____________________________________________________________
 Recorded. Do try not to make me repeat the process.
 todo write report
You now have 3 tasks in the list
____________________________________________________________

____________________________________________________________
 Status updated. An acceptable result:
   [X] read book
____________________________________________________________

____________________________________________________________
 Matching records are below. Review them carefully.
 1.[T][X] read book
 2.[D][ ] return book (by: 29/8/2026 2:30PM)
____________________________________________________________

____________________________________________________________
Your records have been preserved. Do not lose them.
____________________________________________________________

```
