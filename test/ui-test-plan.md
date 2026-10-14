# UI Test Plan

This file records command-line UI test cases for the project. Each test case states its aim, console inputs, and exact expected output. The `ui-tests` JSON block at the end is the machine-readable version used by `.codex/skills/test-ui/scripts/run-ui-tests.py`.

All chatbot responses are wrapped in a horizontal line to make the console output easier to read.

## Test Case 1: Exit

Aim: Verify that the program starts, shows the greeting, accepts bye, and exits with the farewell message.

Command:

```bash
javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby
```

Inputs:

```text
bye
```

Expected output:

```text
____________________________________________________________
 Welcome back, Your Highness!
 What would you like to do today?
____________________________________________________________
____________________________________________________________
 Until next time, Your Highness!
 Stay wonderful!
____________________________________________________________
```

## Test Case 2: Add Task Types And List

Aim: Verify that todo, deadline, and event create the correct task types and display their extra date/time text.

Command:

```bash
javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby
```

Inputs:

```text
todo borrow book
deadline return book /by 2026-08-30 1200
event project meeting /from 2026-08-31 1400 /to 2026-08-31 1600
list
bye
```

Expected output:

```text
____________________________________________________________
 Welcome back, Your Highness!
 What would you like to do today?
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [T][ ] borrow book
 You now have 1 task in your list.
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [D][ ] return book (by: Aug 30 2026, 12:00 PM)
 You now have 2 tasks in your list.
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [E][ ] project meeting (from: Aug 31 2026, 2:00 PM to: Aug 31 2026, 4:00 PM)
 You now have 3 tasks in your list.
____________________________________________________________
____________________________________________________________
 Your Highness, here are the tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Aug 30 2026, 12:00 PM)
 3.[E][ ] project meeting (from: Aug 31 2026, 2:00 PM to: Aug 31 2026, 4:00 PM)
____________________________________________________________
____________________________________________________________
 Until next time, Your Highness!
 Stay wonderful!
____________________________________________________________
```

## Test Case 3: Mark And Unmark Typed Tasks

Aim: Verify that typed tasks can still be marked and unmarked while keeping their type-specific display format.

Command:

```bash
javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby
```

Inputs:

```text
todo read book
deadline do homework /by 2026-09-01 2359
mark 2
unmark 2
bye
```

Expected output:

```text
____________________________________________________________
 Welcome back, Your Highness!
 What would you like to do today?
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [T][ ] read book
 You now have 1 task in your list.
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [D][ ] do homework (by: Sep 01 2026, 11:59 PM)
 You now have 2 tasks in your list.
____________________________________________________________
____________________________________________________________
 Splendid! Task 2 is now marked as done:
  [D][X] do homework (by: Sep 01 2026, 11:59 PM)
____________________________________________________________
____________________________________________________________
 Certainly, Your Highness. Task 2 is marked as not done again:
  [D][ ] do homework (by: Sep 01 2026, 11:59 PM)
____________________________________________________________
____________________________________________________________
 Until next time, Your Highness!
 Stay wonderful!
____________________________________________________________
```

## Test Case 4: Handle Incorrect Inputs

Aim: Verify that incorrect inputs show helpful error messages, do not crash the program, and do not add invalid tasks to the list.

Command:

```bash
javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby
```

Inputs:

```text

todo
todo read book
deadline /by Sunday
deadline return book
deadline return book /by Sunday
deadline return book /by 2026-08-30 1200
event meeting /from Mon
event /from Mon /to Tue
event meeting /from /to Tue
event meeting /from Mon /to
event meeting /from Mon /to Tue
event meeting /from 2026-08-31 1400 /to 2026-08-31 1600
mark
mark two
mark 99
mark 2
blah
list
bye
```

Expected output:

```text
____________________________________________________________
 Welcome back, Your Highness!
 What would you like to do today?
____________________________________________________________
____________________________________________________________
 Oh snow! Please enter a command.
____________________________________________________________
____________________________________________________________
 Oh snow! A todo needs a description. Try: todo read book
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [T][ ] read book
 You now have 1 task in your list.
____________________________________________________________
____________________________________________________________
 Oh snow! A deadline needs a description before /by.
____________________________________________________________
____________________________________________________________
 Oh snow! A deadline needs a description and /by. Try: deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 Oh snow! Please use the format yyyy-MM-dd HHmm.
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [D][ ] return book (by: Aug 30 2026, 12:00 PM)
 You now have 2 tasks in your list.
____________________________________________________________
____________________________________________________________
 Oh snow! An event needs an end time after /to.
____________________________________________________________
____________________________________________________________
 Oh snow! An event needs a description before /from.
____________________________________________________________
____________________________________________________________
 Oh snow! An event needs a start time after /from.
____________________________________________________________
____________________________________________________________
 Oh snow! An event needs an end time after /to.
____________________________________________________________
____________________________________________________________
 Oh snow! Please use the format yyyy-MM-dd HHmm.
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [E][ ] meeting (from: Aug 31 2026, 2:00 PM to: Aug 31 2026, 4:00 PM)
 You now have 3 tasks in your list.
____________________________________________________________
____________________________________________________________
 Oh snow! Please give me a task number to mark. Try: mark 1
____________________________________________________________
____________________________________________________________
 Oh snow! Task numbers must be whole numbers. Try: mark 1
____________________________________________________________
____________________________________________________________
 Oh snow! Task number 99 is not in your list.
____________________________________________________________
____________________________________________________________
 Splendid! Task 2 is now marked as done:
  [D][X] return book (by: Aug 30 2026, 12:00 PM)
____________________________________________________________
____________________________________________________________
 Oh snow! I don't recognise that command. Try todo, deadline, event, list, mark, unmark, delete, find, or bye.
____________________________________________________________
____________________________________________________________
 Your Highness, here are the tasks in your list:
 1.[T][ ] read book
 2.[D][X] return book (by: Aug 30 2026, 12:00 PM)
 3.[E][ ] meeting (from: Aug 31 2026, 2:00 PM to: Aug 31 2026, 4:00 PM)
____________________________________________________________
____________________________________________________________
 Until next time, Your Highness!
 Stay wonderful!
____________________________________________________________
```

## Test Case 5: Delete Task And Renumber List

Aim: Verify that delete removes the selected task, reports the removed task, and renumbers the remaining tasks.

Command:

```bash
javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby
```

Inputs:

```text
todo read book
deadline return book /by 2026-06-06 0900
event project meeting /from 2026-08-06 1400 /to 2026-08-06 1600
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
delete
delete two
delete 99
list
bye
```

Expected output:

```text
____________________________________________________________
 Welcome back, Your Highness!
 What would you like to do today?
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [T][ ] read book
 You now have 1 task in your list.
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [D][ ] return book (by: Jun 06 2026, 9:00 AM)
 You now have 2 tasks in your list.
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [E][ ] project meeting (from: Aug 06 2026, 2:00 PM to: Aug 06 2026, 4:00 PM)
 You now have 3 tasks in your list.
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [T][ ] join sports club
 You now have 4 tasks in your list.
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [T][ ] borrow book
 You now have 5 tasks in your list.
____________________________________________________________
____________________________________________________________
 Splendid! Task 1 is now marked as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
 Splendid! Task 2 is now marked as done:
  [D][X] return book (by: Jun 06 2026, 9:00 AM)
____________________________________________________________
____________________________________________________________
 Splendid! Task 4 is now marked as done:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
 Your Highness, here are the tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2026, 9:00 AM)
 3.[E][ ] project meeting (from: Aug 06 2026, 2:00 PM to: Aug 06 2026, 4:00 PM)
 4.[T][X] join sports club
 5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 It's been removed, Your Highness.
 Deleted: [E][ ] project meeting (from: Aug 06 2026, 2:00 PM to: Aug 06 2026, 4:00 PM)
 You now have 4 tasks in your list.
____________________________________________________________
____________________________________________________________
 Your Highness, here are the tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2026, 9:00 AM)
 3.[T][X] join sports club
 4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Oh snow! Please give me a task number to delete. Try: delete 1
____________________________________________________________
____________________________________________________________
 Oh snow! Task numbers must be whole numbers. Try: delete 1
____________________________________________________________
____________________________________________________________
 Oh snow! Task number 99 is not in your list.
____________________________________________________________
____________________________________________________________
 Your Highness, here are the tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2026, 9:00 AM)
 3.[T][X] join sports club
 4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Until next time, Your Highness!
 Stay wonderful!
____________________________________________________________
```

## Test Case 6: Delete Boundary Cases

Aim: Verify that delete works for the first and last task, and rejects empty-list and lower-bound task numbers.

Command:

```bash
javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby
```

Inputs:

```text
todo first
todo second
delete 1
list
delete 1
list
delete 1
delete 0
delete -1
bye
```

Expected output:

```text
____________________________________________________________
 Welcome back, Your Highness!
 What would you like to do today?
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [T][ ] first
 You now have 1 task in your list.
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [T][ ] second
 You now have 2 tasks in your list.
____________________________________________________________
____________________________________________________________
 It's been removed, Your Highness.
 Deleted: [T][ ] first
 You now have 1 task in your list.
____________________________________________________________
____________________________________________________________
 Your Highness, here are the tasks in your list:
 1.[T][ ] second
____________________________________________________________
____________________________________________________________
 It's been removed, Your Highness.
 Deleted: [T][ ] second
 You now have 0 tasks in your list.
____________________________________________________________
____________________________________________________________
 Your Highness, here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Oh snow! Task number 1 is not in your list.
____________________________________________________________
____________________________________________________________
 Oh snow! Task number 0 is not in your list.
____________________________________________________________
____________________________________________________________
 Oh snow! Task number -1 is not in your list.
____________________________________________________________
____________________________________________________________
 Until next time, Your Highness!
 Stay wonderful!
____________________________________________________________
```

## Test Case 7: Find Matching Tasks

Aim: Verify that find lists tasks using case-insensitive partial matching, reports no matches, and rejects an empty keyword.

Command:

```bash
javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby
```

Inputs:

```text
todo read book
deadline return book /by 2026-08-30 1200
event project meeting /from 2026-08-31 1400 /to 2026-08-31 1600
mark 1
find book
find BOOK
find boo
find pen
find
bye
```

Expected output:

```text
____________________________________________________________
 Welcome back, Your Highness!
 What would you like to do today?
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [T][ ] read book
 You now have 1 task in your list.
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [D][ ] return book (by: Aug 30 2026, 12:00 PM)
 You now have 2 tasks in your list.
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [E][ ] project meeting (from: Aug 31 2026, 2:00 PM to: Aug 31 2026, 4:00 PM)
 You now have 3 tasks in your list.
____________________________________________________________
____________________________________________________________
 Splendid! Task 1 is now marked as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
 Here are the matching tasks, Your Highness:
 1.[T][X] read book
 2.[D][ ] return book (by: Aug 30 2026, 12:00 PM)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks, Your Highness:
 1.[T][X] read book
 2.[D][ ] return book (by: Aug 30 2026, 12:00 PM)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks, Your Highness:
 1.[T][X] read book
 2.[D][ ] return book (by: Aug 30 2026, 12:00 PM)
____________________________________________________________
____________________________________________________________
 No matching tasks were found, Your Highness.
____________________________________________________________
____________________________________________________________
 Oh snow! Please give me a keyword to find.
____________________________________________________________
____________________________________________________________
 Until next time, Your Highness!
 Stay wonderful!
____________________________________________________________
```

## Test Case 8: Handle Additional Malformed Inputs

Aim: Verify whitespace normalization, duplicate parameters, strict dates, event ordering, and invalid indices.

Command:

```bash
javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby
```

Inputs:

```text
   todo    spaced    task
deadline test /by 2026-02-30 1200
deadline test /by 2026-08-30 1200 /by 2026-09-01 1200
event meeting /from 2026-08-30 1000 /from 2026-08-30 1100 /to 2026-08-30 1200
event meeting /from 2026-08-30 1400 /to 2026-08-30 1200
event meeting /to 2026-08-30 1400 /from 2026-08-30 1200
mark 0
unmark 2
delete 1 2
list
bye
```

Expected output:

```text
____________________________________________________________
 Welcome back, Your Highness!
 What would you like to do today?
____________________________________________________________
____________________________________________________________
 Consider it done, Your Highness!
 I've added: [T][ ] spaced task
 You now have 1 task in your list.
____________________________________________________________
____________________________________________________________
 Oh snow! Please use the format yyyy-MM-dd HHmm.
____________________________________________________________
____________________________________________________________
 Oh snow! A deadline can only have one /by parameter.
____________________________________________________________
____________________________________________________________
 Oh snow! An event can only have one /from parameter.
____________________________________________________________
____________________________________________________________
 Oh snow! An event's end time must be after its start time.
____________________________________________________________
____________________________________________________________
 Oh snow! Please place /from before /to.
____________________________________________________________
____________________________________________________________
 Oh snow! Task number 0 is not in your list.
____________________________________________________________
____________________________________________________________
 Oh snow! Task number 2 is not in your list.
____________________________________________________________
____________________________________________________________
 Oh snow! Task numbers must be whole numbers. Try: delete 1
____________________________________________________________
____________________________________________________________
 Your Highness, here are the tasks in your list:
 1.[T][ ] spaced task
____________________________________________________________
____________________________________________________________
 Until next time, Your Highness!
 Stay wonderful!
____________________________________________________________
```

## Machine-Readable Test Cases

```json ui-tests
[
  {
    "name": "Exit",
    "aim": "Verify that the program starts, shows the greeting, accepts bye, and exits with the farewell message.",
    "command": "javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby",
    "input": [
      "bye"
    ],
    "expected_output": "____________________________________________________________\n Welcome back, Your Highness!\n What would you like to do today?\n____________________________________________________________\n____________________________________________________________\n Until next time, Your Highness!\n Stay wonderful!\n____________________________________________________________\n"
  },
  {
    "name": "Add Task Types And List",
    "aim": "Verify that todo, deadline, and event create the correct task types and display their extra date/time text.",
    "command": "javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby",
    "input": [
      "todo borrow book",
      "deadline return book /by 2026-08-30 1200",
      "event project meeting /from 2026-08-31 1400 /to 2026-08-31 1600",
      "list",
      "bye"
    ],
    "expected_output": "____________________________________________________________\n Welcome back, Your Highness!\n What would you like to do today?\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [T][ ] borrow book\n You now have 1 task in your list.\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [D][ ] return book (by: Aug 30 2026, 12:00 PM)\n You now have 2 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [E][ ] project meeting (from: Aug 31 2026, 2:00 PM to: Aug 31 2026, 4:00 PM)\n You now have 3 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n Your Highness, here are the tasks in your list:\n 1.[T][ ] borrow book\n 2.[D][ ] return book (by: Aug 30 2026, 12:00 PM)\n 3.[E][ ] project meeting (from: Aug 31 2026, 2:00 PM to: Aug 31 2026, 4:00 PM)\n____________________________________________________________\n____________________________________________________________\n Until next time, Your Highness!\n Stay wonderful!\n____________________________________________________________\n"
  },
  {
    "name": "Mark And Unmark Typed Tasks",
    "aim": "Verify that typed tasks can still be marked and unmarked while keeping their type-specific display format.",
    "command": "javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby",
    "input": [
      "todo read book",
      "deadline do homework /by 2026-09-01 2359",
      "mark 2",
      "unmark 2",
      "bye"
    ],
    "expected_output": "____________________________________________________________\n Welcome back, Your Highness!\n What would you like to do today?\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [T][ ] read book\n You now have 1 task in your list.\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [D][ ] do homework (by: Sep 01 2026, 11:59 PM)\n You now have 2 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n Splendid! Task 2 is now marked as done:\n  [D][X] do homework (by: Sep 01 2026, 11:59 PM)\n____________________________________________________________\n____________________________________________________________\n Certainly, Your Highness. Task 2 is marked as not done again:\n  [D][ ] do homework (by: Sep 01 2026, 11:59 PM)\n____________________________________________________________\n____________________________________________________________\n Until next time, Your Highness!\n Stay wonderful!\n____________________________________________________________\n"
  },
  {
    "name": "Handle Incorrect Inputs",
    "aim": "Verify that incorrect inputs show helpful error messages, do not crash the program, and do not add invalid tasks to the list.",
    "command": "javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby",
    "input": [
      "",
      "todo",
      "todo read book",
      "deadline /by Sunday",
      "deadline return book",
      "deadline return book /by Sunday",
      "deadline return book /by 2026-08-30 1200",
      "event meeting /from Mon",
      "event /from Mon /to Tue",
      "event meeting /from /to Tue",
      "event meeting /from Mon /to",
      "event meeting /from Mon /to Tue",
      "event meeting /from 2026-08-31 1400 /to 2026-08-31 1600",
      "mark",
      "mark two",
      "mark 99",
      "mark 2",
      "blah",
      "list",
      "bye"
    ],
    "expected_output": "____________________________________________________________\n Welcome back, Your Highness!\n What would you like to do today?\n____________________________________________________________\n____________________________________________________________\n Oh snow! Please enter a command.\n____________________________________________________________\n____________________________________________________________\n Oh snow! A todo needs a description. Try: todo read book\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [T][ ] read book\n You now have 1 task in your list.\n____________________________________________________________\n____________________________________________________________\n Oh snow! A deadline needs a description before /by.\n____________________________________________________________\n____________________________________________________________\n Oh snow! A deadline needs a description and /by. Try: deadline return book /by Sunday\n____________________________________________________________\n____________________________________________________________\n Oh snow! Please use the format yyyy-MM-dd HHmm.\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [D][ ] return book (by: Aug 30 2026, 12:00 PM)\n You now have 2 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n Oh snow! An event needs an end time after /to.\n____________________________________________________________\n____________________________________________________________\n Oh snow! An event needs a description before /from.\n____________________________________________________________\n____________________________________________________________\n Oh snow! An event needs a start time after /from.\n____________________________________________________________\n____________________________________________________________\n Oh snow! An event needs an end time after /to.\n____________________________________________________________\n____________________________________________________________\n Oh snow! Please use the format yyyy-MM-dd HHmm.\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [E][ ] meeting (from: Aug 31 2026, 2:00 PM to: Aug 31 2026, 4:00 PM)\n You now have 3 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n Oh snow! Please give me a task number to mark. Try: mark 1\n____________________________________________________________\n____________________________________________________________\n Oh snow! Task numbers must be whole numbers. Try: mark 1\n____________________________________________________________\n____________________________________________________________\n Oh snow! Task number 99 is not in your list.\n____________________________________________________________\n____________________________________________________________\n Splendid! Task 2 is now marked as done:\n  [D][X] return book (by: Aug 30 2026, 12:00 PM)\n____________________________________________________________\n____________________________________________________________\n Oh snow! I don't recognise that command. Try todo, deadline, event, list, mark, unmark, delete, find, or bye.\n____________________________________________________________\n____________________________________________________________\n Your Highness, here are the tasks in your list:\n 1.[T][ ] read book\n 2.[D][X] return book (by: Aug 30 2026, 12:00 PM)\n 3.[E][ ] meeting (from: Aug 31 2026, 2:00 PM to: Aug 31 2026, 4:00 PM)\n____________________________________________________________\n____________________________________________________________\n Until next time, Your Highness!\n Stay wonderful!\n____________________________________________________________\n"
  },
  {
    "name": "Delete Task And Renumber List",
    "aim": "Verify that delete removes the selected task, reports the removed task, and renumbers the remaining tasks.",
    "command": "javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby",
    "input": [
      "todo read book",
      "deadline return book /by 2026-06-06 0900",
      "event project meeting /from 2026-08-06 1400 /to 2026-08-06 1600",
      "todo join sports club",
      "todo borrow book",
      "mark 1",
      "mark 2",
      "mark 4",
      "list",
      "delete 3",
      "list",
      "delete",
      "delete two",
      "delete 99",
      "list",
      "bye"
    ],
    "expected_output": "____________________________________________________________\n Welcome back, Your Highness!\n What would you like to do today?\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [T][ ] read book\n You now have 1 task in your list.\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [D][ ] return book (by: Jun 06 2026, 9:00 AM)\n You now have 2 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [E][ ] project meeting (from: Aug 06 2026, 2:00 PM to: Aug 06 2026, 4:00 PM)\n You now have 3 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [T][ ] join sports club\n You now have 4 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [T][ ] borrow book\n You now have 5 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n Splendid! Task 1 is now marked as done:\n  [T][X] read book\n____________________________________________________________\n____________________________________________________________\n Splendid! Task 2 is now marked as done:\n  [D][X] return book (by: Jun 06 2026, 9:00 AM)\n____________________________________________________________\n____________________________________________________________\n Splendid! Task 4 is now marked as done:\n  [T][X] join sports club\n____________________________________________________________\n____________________________________________________________\n Your Highness, here are the tasks in your list:\n 1.[T][X] read book\n 2.[D][X] return book (by: Jun 06 2026, 9:00 AM)\n 3.[E][ ] project meeting (from: Aug 06 2026, 2:00 PM to: Aug 06 2026, 4:00 PM)\n 4.[T][X] join sports club\n 5.[T][ ] borrow book\n____________________________________________________________\n____________________________________________________________\n It's been removed, Your Highness.\n Deleted: [E][ ] project meeting (from: Aug 06 2026, 2:00 PM to: Aug 06 2026, 4:00 PM)\n You now have 4 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n Your Highness, here are the tasks in your list:\n 1.[T][X] read book\n 2.[D][X] return book (by: Jun 06 2026, 9:00 AM)\n 3.[T][X] join sports club\n 4.[T][ ] borrow book\n____________________________________________________________\n____________________________________________________________\n Oh snow! Please give me a task number to delete. Try: delete 1\n____________________________________________________________\n____________________________________________________________\n Oh snow! Task numbers must be whole numbers. Try: delete 1\n____________________________________________________________\n____________________________________________________________\n Oh snow! Task number 99 is not in your list.\n____________________________________________________________\n____________________________________________________________\n Your Highness, here are the tasks in your list:\n 1.[T][X] read book\n 2.[D][X] return book (by: Jun 06 2026, 9:00 AM)\n 3.[T][X] join sports club\n 4.[T][ ] borrow book\n____________________________________________________________\n____________________________________________________________\n Until next time, Your Highness!\n Stay wonderful!\n____________________________________________________________\n"
  },
  {
    "name": "Delete Boundary Cases",
    "aim": "Verify that delete works for the first and last task, and rejects empty-list and lower-bound task numbers.",
    "command": "javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby",
    "input": [
      "todo first",
      "todo second",
      "delete 1",
      "list",
      "delete 1",
      "list",
      "delete 1",
      "delete 0",
      "delete -1",
      "bye"
    ],
    "expected_output": "____________________________________________________________\n Welcome back, Your Highness!\n What would you like to do today?\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [T][ ] first\n You now have 1 task in your list.\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [T][ ] second\n You now have 2 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n It's been removed, Your Highness.\n Deleted: [T][ ] first\n You now have 1 task in your list.\n____________________________________________________________\n____________________________________________________________\n Your Highness, here are the tasks in your list:\n 1.[T][ ] second\n____________________________________________________________\n____________________________________________________________\n It's been removed, Your Highness.\n Deleted: [T][ ] second\n You now have 0 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n Your Highness, here are the tasks in your list:\n____________________________________________________________\n____________________________________________________________\n Oh snow! Task number 1 is not in your list.\n____________________________________________________________\n____________________________________________________________\n Oh snow! Task number 0 is not in your list.\n____________________________________________________________\n____________________________________________________________\n Oh snow! Task number -1 is not in your list.\n____________________________________________________________\n____________________________________________________________\n Until next time, Your Highness!\n Stay wonderful!\n____________________________________________________________\n"
  },
  {
    "name": "Find Matching Tasks",
    "aim": "Verify that find lists tasks using case-insensitive partial matching, reports no matches, and rejects an empty keyword.",
    "command": "javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby",
    "input": [
      "todo read book",
      "deadline return book /by 2026-08-30 1200",
      "event project meeting /from 2026-08-31 1400 /to 2026-08-31 1600",
      "mark 1",
      "find book",
      "find BOOK",
      "find boo",
      "find pen",
      "find",
      "bye"
    ],
    "expected_output": "____________________________________________________________\n Welcome back, Your Highness!\n What would you like to do today?\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [T][ ] read book\n You now have 1 task in your list.\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [D][ ] return book (by: Aug 30 2026, 12:00 PM)\n You now have 2 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [E][ ] project meeting (from: Aug 31 2026, 2:00 PM to: Aug 31 2026, 4:00 PM)\n You now have 3 tasks in your list.\n____________________________________________________________\n____________________________________________________________\n Splendid! Task 1 is now marked as done:\n  [T][X] read book\n____________________________________________________________\n____________________________________________________________\n Here are the matching tasks, Your Highness:\n 1.[T][X] read book\n 2.[D][ ] return book (by: Aug 30 2026, 12:00 PM)\n____________________________________________________________\n____________________________________________________________\n Here are the matching tasks, Your Highness:\n 1.[T][X] read book\n 2.[D][ ] return book (by: Aug 30 2026, 12:00 PM)\n____________________________________________________________\n____________________________________________________________\n Here are the matching tasks, Your Highness:\n 1.[T][X] read book\n 2.[D][ ] return book (by: Aug 30 2026, 12:00 PM)\n____________________________________________________________\n____________________________________________________________\n No matching tasks were found, Your Highness.\n____________________________________________________________\n____________________________________________________________\n Oh snow! Please give me a keyword to find.\n____________________________________________________________\n____________________________________________________________\n Until next time, Your Highness!\n Stay wonderful!\n____________________________________________________________\n"
  },
  {
    "name": "Handle Additional Malformed Inputs",
    "aim": "Verify whitespace normalization, duplicate parameters, strict dates, event ordering, and invalid indices.",
    "command": "javac -d out/test-ui -sourcepath src/main/java src/main/java/baby/Baby.java && java -Dbaby.filePath=$(mktemp) -cp out/test-ui baby.Baby",
    "input": [
      "   todo    spaced    task   ",
      "deadline test /by 2026-02-30 1200",
      "deadline test /by 2026-08-30 1200 /by 2026-09-01 1200",
      "event meeting /from 2026-08-30 1000 /from 2026-08-30 1100 /to 2026-08-30 1200",
      "event meeting /from 2026-08-30 1400 /to 2026-08-30 1200",
      "event meeting /to 2026-08-30 1400 /from 2026-08-30 1200",
      "mark 0",
      "unmark 2",
      "delete 1 2",
      "list",
      "bye"
    ],
    "expected_output": "____________________________________________________________\n Welcome back, Your Highness!\n What would you like to do today?\n____________________________________________________________\n____________________________________________________________\n Consider it done, Your Highness!\n I've added: [T][ ] spaced task\n You now have 1 task in your list.\n____________________________________________________________\n____________________________________________________________\n Oh snow! Please use the format yyyy-MM-dd HHmm.\n____________________________________________________________\n____________________________________________________________\n Oh snow! A deadline can only have one /by parameter.\n____________________________________________________________\n____________________________________________________________\n Oh snow! An event can only have one /from parameter.\n____________________________________________________________\n____________________________________________________________\n Oh snow! An event's end time must be after its start time.\n____________________________________________________________\n____________________________________________________________\n Oh snow! Please place /from before /to.\n____________________________________________________________\n____________________________________________________________\n Oh snow! Task number 0 is not in your list.\n____________________________________________________________\n____________________________________________________________\n Oh snow! Task number 2 is not in your list.\n____________________________________________________________\n____________________________________________________________\n Oh snow! Task numbers must be whole numbers. Try: delete 1\n____________________________________________________________\n____________________________________________________________\n Your Highness, here are the tasks in your list:\n 1.[T][ ] spaced task\n____________________________________________________________\n____________________________________________________________\n Until next time, Your Highness!\n Stay wonderful!\n____________________________________________________________\n"
  }
]
```
