# Console UI Regression Test Plan

Run this behavior suite alongside the JUnit tests after code or test-infrastructure changes.
For A-JUnit, expected console output and save-file contents remain unchanged.

- Working directory: repository root
- Java version: Microsoft OpenJDK 25.0.4.1
- Build commands (PowerShell, with `src/main/java` kept as the source root):
  ```powershell
  $javaSources = Get-ChildItem src/main/java -Recurse -Filter *.java | ForEach-Object { $_.FullName }
  & 'C:\Users\moons\.jdks\ms-25.0.4.1\bin\javac.exe' -d out/test-classes $javaSources
  ```
- Launch command: `C:\Users\moons\.jdks\ms-25.0.4.1\bin\java.exe -cp out\test-classes anaconda.Anaconda`
- Process timeout: 10 seconds per test case
- Comparison: normalize CRLF/LF line endings and one final newline only
- Process isolation: launch a fresh program process for every test case
- Storage isolation: run each independent case in a fresh `_temp/oop-ui-tests-<run>/TCxx` folder with an
  absolute classpath; TC06 reuses TC05's folder to verify loading in a fresh process

## Test case: TC01 - Add and list all task types

### Aim

Verify that valid ToDo, Deadline, and Event commands still work after moving task storage to a Java collection.

### Inputs

```text
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-15 /to 2019-10-16
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _   _    _    ____ ___  _   _ ____    _
   / \  | \ | |  / \  / ___/ _ \| \ | |  _ \  / \
  / _ \ |  \| | / _ \| |  | | | |  \| | | | |/ _ \
 / ___ \| |\  |/ ___ \ |__| |_| | |\  | |_| / ___ \
/_/   \_\_| \_/_/   \_\____\___/|_| \_|____/_/   \_\

Yo, it's Anaconda.
What do you need?
____________________________________________________________
Alright, added it:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] borrow book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Alright, off you go. Try to get something done.
____________________________________________________________
```

## Test case: TC04 - Delete tasks and renumber the list

### Aim

Verify that deleting valid tasks reports the removed task, updates the task count, and closes numbering gaps, while invalid delete commands leave the list unchanged.

### Inputs

```text
todo read book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-15 /to 2019-10-16
delete 2
list
delete 3
delete two
delete 0
delete 1
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _   _    _    ____ ___  _   _ ____    _
   / \  | \ | |  / \  / ___/ _ \| \ | |  _ \  / \
  / _ \ |  \| | / _ \| |  | | | |  \| | | | |/ _ \
 / ___ \| |\  |/ ___ \ |__| |_| | |\  | |_| / ___ \
/_/   \_\_| \_/_/   \_\____\___/|_| \_|____/_/   \_\

Yo, it's Anaconda.
What do you need?
____________________________________________________________
Alright, added it:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Gone. Hope you didn't need that:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] read book
2.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Hang on. Task 3 does not exist.
____________________________________________________________
____________________________________________________________
Hang on. I need one task number here.
____________________________________________________________
____________________________________________________________
Hang on. Task 0 does not exist.
____________________________________________________________
____________________________________________________________
Gone. Hope you didn't need that:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Alright, off you go. Try to get something done.
____________________________________________________________
```

## Test case: TC02 - Recover from general and task-number errors

### Aim

Verify that the command enum rejects unknown commands and that invalid task numbers and extra arguments produce friendly errors without changing valid task state.

### Inputs

```text

todo
blah
todo read book
mark two
mark 2
mark 1
unmark 0
unmark 1
list extra
list
bye now
bye
```

### Expected output

```text
____________________________________________________________
    _    _   _    _    ____ ___  _   _ ____    _
   / \  | \ | |  / \  / ___/ _ \| \ | |  _ \  / \
  / _ \ |  \| | / _ \| |  | | | |  \| | | | |/ _ \
 / ___ \| |\  |/ ___ \ |__| |_| | |\  | |_| / ___ \
/_/   \_\_| \_/_/   \_\____\___/|_| \_|____/_/   \_\

Yo, it's Anaconda.
What do you need?
____________________________________________________________
Hang on. Give me a command to work with.
____________________________________________________________
____________________________________________________________
Hang on. The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
Hang on. Yeah... I don't recognize that command.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Hang on. I need one task number here.
____________________________________________________________
____________________________________________________________
Hang on. Task 2 does not exist.
____________________________________________________________
____________________________________________________________
Fine, that's done now:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Hang on. Task 0 does not exist.
____________________________________________________________
____________________________________________________________
Really? Unmarked? Alright . . .
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Hang on. The list command does not take extra text.
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Hang on. The bye command cannot have extra text.
____________________________________________________________
____________________________________________________________
Alright, off you go. Try to get something done.
____________________________________________________________
```

## Test case: TC03 - Recover from malformed deadlines and events

### Aim

Verify that missing deadline and event fields are rejected and that later valid typed tasks are stored with the correct numbering and values.

### Inputs

```text
deadline return book
deadline return book /by
deadline return book /by 2019-10-15
deadline impossible date /by 2019-02-30
event project meeting /from 2019-10-15
event project meeting /from /to 2019-10-16
event project meeting /from 2019-10-15 /to 2019-10-16
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _   _    _    ____ ___  _   _ ____    _
   / \  | \ | |  / \  / ___/ _ \| \ | |  _ \  / \
  / _ \ |  \| | / _ \| |  | | | |  \| | | | |/ _ \
 / ___ \| |\  |/ ___ \ |__| |_| | |\  | |_| / ___ \
/_/   \_\_| \_/_/   \_\____\___/|_| \_|____/_/   \_\

Yo, it's Anaconda.
What do you need?
____________________________________________________________
Hang on. A deadline needs '/by' followed by a date or time.
____________________________________________________________
____________________________________________________________
Hang on. A deadline needs a date or time after '/by'.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [D][ ] return book (by: Oct 15 2019)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Hang on. Please enter a valid calendar date in yyyy-MM-dd or dd-MM-yyyy format.
____________________________________________________________
____________________________________________________________
Hang on. An event needs both '/from' and '/to' times.
____________________________________________________________
____________________________________________________________
Hang on. An event needs times after both '/from' and '/to'.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[D][ ] return book (by: Oct 15 2019)
2.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Alright, off you go. Try to get something done.
____________________________________________________________
```

## Test case: TC05 - Save tasks and completion status

### Aim

Verify that adding all three task types and marking a task creates a new data folder and saves the latest task state.

### Inputs

```text
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-15 /to 2019-10-16
mark 2
bye
```

### Expected output

```text
____________________________________________________________
    _    _   _    _    ____ ___  _   _ ____    _
   / \  | \ | |  / \  / ___/ _ \| \ | |  _ \  / \
  / _ \ |  \| | / _ \| |  | | | |  \| | | | |/ _ \
 / ___ \| |\  |/ ___ \ |__| |_| | |\  | |_| / ___ \
/_/   \_\_| \_/_/   \_\____\___/|_| \_|____/_/   \_\

Yo, it's Anaconda.
What do you need?
____________________________________________________________
Alright, added it:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Fine, that's done now:
  [D][X] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Alright, off you go. Try to get something done.
____________________________________________________________
```

### Expected data file

```text
T | 0 | borrow book
D | 1 | return book | 2019-10-15
E | 0 | project meeting | 2019-10-15 | 2019-10-16
```

## Test case: TC09 - Filter tasks by ending date

### Aim

Verify inclusive by/from filtering, exact sharp filtering, day-first query dates, and exclusion of undated ToDos.

### Inputs

```text
deadline old deadline /by 2026-08-18
deadline exact deadline /by 2026-08-19
deadline future deadline /by 2026-08-20
event exact-ending event /from 2026-08-17 /to 2026-08-19
event future-ending event /from 2026-08-18 /to 2026-08-21
todo undated task
/by 19-08-2026
/by 19-08-2026 sharp
/from 19-08-2026
/from 19-08-2026 sharp
bye
```

### Expected output

```text
____________________________________________________________
    _    _   _    _    ____ ___  _   _ ____    _
   / \  | \ | |  / \  / ___/ _ \| \ | |  _ \  / \
  / _ \ |  \| | / _ \| |  | | | |  \| | | | |/ _ \
 / ___ \| |\  |/ ___ \ |__| |_| | |\  | |_| / ___ \
/_/   \_\_| \_/_/   \_\____\___/|_| \_|____/_/   \_\

Yo, it's Anaconda.
What do you need?
____________________________________________________________
Alright, added it:
  [D][ ] old deadline (by: Aug 18 2026)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [D][ ] exact deadline (by: Aug 19 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [D][ ] future deadline (by: Aug 20 2026)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [E][ ] exact-ending event (from: Aug 17 2026 to: Aug 19 2026)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [E][ ] future-ending event (from: Aug 18 2026 to: Aug 21 2026)
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [T][ ] undated task
Now you have 6 tasks in the list.
____________________________________________________________
____________________________________________________________
Found these:
1.[D][ ] old deadline (by: Aug 18 2026)
2.[D][ ] exact deadline (by: Aug 19 2026)
3.[E][ ] exact-ending event (from: Aug 17 2026 to: Aug 19 2026)
____________________________________________________________
____________________________________________________________
Found these:
1.[D][ ] exact deadline (by: Aug 19 2026)
2.[E][ ] exact-ending event (from: Aug 17 2026 to: Aug 19 2026)
____________________________________________________________
____________________________________________________________
Found these:
1.[D][ ] exact deadline (by: Aug 19 2026)
2.[D][ ] future deadline (by: Aug 20 2026)
3.[E][ ] exact-ending event (from: Aug 17 2026 to: Aug 19 2026)
4.[E][ ] future-ending event (from: Aug 18 2026 to: Aug 21 2026)
____________________________________________________________
____________________________________________________________
Found these:
1.[D][ ] exact deadline (by: Aug 19 2026)
2.[E][ ] exact-ending event (from: Aug 17 2026 to: Aug 19 2026)
____________________________________________________________
____________________________________________________________
Alright, off you go. Try to get something done.
____________________________________________________________
```

## Test case: TC10 - Reject malformed date filters

### Aim

Verify that missing dates, unsupported modifiers, and invalid dates produce friendly errors without ending the session.

### Inputs

```text
/by
/by 19-08-2026 now
/from not-a-date
bye
```

### Expected output

```text
____________________________________________________________
    _    _   _    _    ____ ___  _   _ ____    _
   / \  | \ | |  / \  / ___/ _ \| \ | |  _ \  / \
  / _ \ |  \| | / _ \| |  | | | |  \| | | | |/ _ \
 / ___ \| |\  |/ ___ \ |__| |_| | |\  | |_| / ___ \
/_/   \_\_| \_/_/   \_\____\___/|_| \_|____/_/   \_\

Yo, it's Anaconda.
What do you need?
____________________________________________________________
Hang on. Use '/by DATE' or '/by DATE sharp'.
____________________________________________________________
____________________________________________________________
Hang on. Use '/by DATE' or '/by DATE sharp'.
____________________________________________________________
____________________________________________________________
Hang on. Please enter a valid calendar date in yyyy-MM-dd or dd-MM-yyyy format.
____________________________________________________________
____________________________________________________________
Alright, off you go. Try to get something done.
____________________________________________________________
```

## Test case: TC07 - Immediately clear the task list

### Aim

Verify that clear immediately removes every task and saves the empty list, then list runs normally.

### Inputs

```text
todo borrow book
deadline return book /by 2019-10-15
clear
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _   _    _    ____ ___  _   _ ____    _
   / \  | \ | |  / \  / ___/ _ \| \ | |  _ \  / \
  / _ \ |  \| | / _ \| |  | | | |  \| | | | |/ _ \
 / ___ \| |\  |/ ___ \ |__| |_| | |\  | |_| / ___ \
/_/   \_\_| \_/_/   \_\____\___/|_| \_|____/_/   \_\

Yo, it's Anaconda.
What do you need?
____________________________________________________________
Alright, added it:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Fine. Everything's gone.
____________________________________________________________
____________________________________________________________
Here's what you've got:
____________________________________________________________
____________________________________________________________
Alright, off you go. Try to get something done.
____________________________________________________________
```

### Expected data file

```text

```

## Test case: TC08 - Undo clearing the task list

### Aim

Verify that undo immediately restores and saves the tasks removed by clear.

### Inputs

```text
todo borrow book
clear
undo
list
bye
```

### Expected output

```text
____________________________________________________________
    _    _   _    _    ____ ___  _   _ ____    _
   / \  | \ | |  / \  / ___/ _ \| \ | |  _ \  / \
  / _ \ |  \| | / _ \| |  | | | |  \| | | | |/ _ \
 / ___ \| |\  |/ ___ \ |__| |_| | |\  | |_| / ___ \
/_/   \_\_| \_/_/   \_\____\___/|_| \_|____/_/   \_\

Yo, it's Anaconda.
What do you need?
____________________________________________________________
Alright, added it:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Fine. Everything's gone.
____________________________________________________________
____________________________________________________________
Undid the previous command.
Here's what you've got:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Here's what you've got:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Alright, off you go. Try to get something done.
____________________________________________________________
```

### Expected data file

```text
T | 0 | borrow book
```

## Test case: TC06 - Load and update saved tasks

### Aim

Verify that a fresh chatbot process restores task types and completion status, then persists an unmark and deletion.

### Inputs

```text
list
unmark 2
delete 1
bye
```

### Expected output

```text
____________________________________________________________
    _    _   _    _    ____ ___  _   _ ____    _
   / \  | \ | |  / \  / ___/ _ \| \ | |  _ \  / \
  / _ \ |  \| | / _ \| |  | | | |  \| | | | |/ _ \
 / ___ \| |\  |/ ___ \ |__| |_| | |\  | |_| / ___ \
/_/   \_\_| \_/_/   \_\____\___/|_| \_|____/_/   \_\

Yo, it's Anaconda.
What do you need?
____________________________________________________________
Here's what you've got:
1.[T][ ] borrow book
2.[D][X] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Really? Unmarked? Alright . . .
  [D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Gone. Hope you didn't need that:
  [T][ ] borrow book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, off you go. Try to get something done.
____________________________________________________________
```

### Expected data file

```text
D | 0 | return book | 2019-10-15
E | 0 | project meeting | 2019-10-15 | 2019-10-16
```

## Test case: TC11 - Find tasks by description

### Aim

Verify that find performs a case-insensitive substring search on task descriptions, preserves task state and
order, reports empty results, and rejects a missing keyword without changing saved tasks.

### Inputs

```text
todo read book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-15 /to 2019-10-16
todo buy groceries
mark 1
find book
find PROJECT
find missing
find
bye
```

### Expected output

```text
____________________________________________________________
    _    _   _    _    ____ ___  _   _ ____    _
   / \  | \ | |  / \  / ___/ _ \| \ | |  _ \  / \
  / _ \ |  \| | / _ \| |  | | | |  \| | | | |/ _ \
 / ___ \| |\  |/ ___ \ |__| |_| | |\  | |_| / ___ \
/_/   \_\_| \_/_/   \_\____\___/|_| \_|____/_/   \_\

Yo, it's Anaconda.
What do you need?
____________________________________________________________
Alright, added it:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Alright, added it:
  [T][ ] buy groceries
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Fine, that's done now:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Found these:
1.[T][X] read book
2.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Found these:
1.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Found these:
____________________________________________________________
____________________________________________________________
Hang on. Give me a keyword to look for.
____________________________________________________________
____________________________________________________________
Alright, off you go. Try to get something done.
____________________________________________________________
```

### Expected data file

```text
T | 1 | read book
D | 0 | return book | 2019-10-15
E | 0 | project meeting | 2019-10-15 | 2019-10-16
T | 0 | buy groceries
```
