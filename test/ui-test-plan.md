# UI Test Plan

Add one section per command-line UI scenario. The `test-ui` skill runs these cases in order and stops at the first mismatch.

Copy the following template for each real test case (remove the indentation):

```markdown
    ## Test case: <short name>

    **Aim:** <what behavior is verified>
    **Command:** `<command to run>`
    **Inputs:**
    ```text
    <stdin, exactly as supplied, or leave empty for no input>
    ```
    **Expected output:**
    ```text
    <complete combined stdout and stderr, exactly as it should appear>
    ```
```

## Test case: reject malformed task data

**Aim:** Verify that Fein rejects repeated spaces, invalid dates, malformed markers, and save-file separators.
**Command:** `mkdir -p /tmp/fein-ui-errors/data /tmp/fein-ui-build && rm -f /tmp/fein-ui-errors/data/fein.txt && javac -d /tmp/fein-ui-build $(find src/main/java/fein -name '*.java' ! -name 'Main.java' ! -name 'Launcher.java' ! -name 'MainWindow.java' ! -name 'DialogBox.java') && cd /tmp/fein-ui-errors && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
todo buy    milk
deadline submit report /by 31/6/2026
event workshop /from 2/12/2026 1800 /to 2/12/2026 1700
todo buy | milk
deadline submit report /by Friday /by Saturday
event workshop /to 4pm /from 2pm
bye
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Please use a single space between words, for example: `todo buy milk`.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 The due date is not a valid date and time. Try `2/12/2026 1800`.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 The event end time must be after the start time.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 A task description cannot contain `|`. Please use another character.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Use `/by` only once for a deadline.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Place `/from` before `/to` in an event.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
Bye! Keeping feining and stay LIT!
____________________________________________________________________________________________________

```

## Test case: delete task and renumber remaining tasks

**Aim:** Verify that deleting a task removes it, shifts later tasks forward, and updates the task count.
**Command:** `rm -f data/fein.txt && mkdir -p /tmp/fein-ui-build && javac -d /tmp/fein-ui-build $(find src/main/java/fein -name '*.java' ! -name 'Main.java' ! -name 'Launcher.java' ! -name 'MainWindow.java' ! -name 'DialogBox.java') && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
todo first
deadline second /by tomorrow
event third /from 2pm /to 4pm
delete 2
list
bye
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Got it. I've added this task:
   [T][ ] first
 Now you have 1 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Got it. I've added this task:
   [D][ ] second (by: tomorrow)
 Now you have 2 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Got it. I've added this task:
   [E][ ] third (from: 2pm to: 4pm)
 Now you have 3 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Noted. I've removed this task:
   [D][ ] second (by: tomorrow)
 Now you have 2 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] first
 2.[E][ ] third (from: 2pm to: 4pm)
____________________________________________________________________________________________________
____________________________________________________________________________________________________
Bye! Keeping feining and stay LIT!
____________________________________________________________________________________________________

```

## Test case: load tasks at startup

**Aim:** Verify that Fein loads saved todo, deadline, and event tasks, including completion status, when it starts.
**Command:** `mkdir -p data && printf '%s\n' 'T | 1 | read book' 'D | 0 | return book | June 6th' 'E | 0 | project meeting | Aug 6th 2pm | 4pm' > data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
list
bye
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: June 6th)
 3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________________________________________________
____________________________________________________________________________________________________
Bye! Keeping feining and stay LIT!
____________________________________________________________________________________________________

```

## Test case: unknown command

**Aim:** Verify that Fein rejects commands it does not recognise.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
blah
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 That command is not feining. Try `todo buy milk`, `list`, or `bye`.
____________________________________________________________________________________________________

```

## Test case: todo without description

**Aim:** Verify that a todo requires a description.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
todo
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Tell me what you would like to remember. Try `todo buy milk` or `bye`.
____________________________________________________________________________________________________

```

## Test case: deadline without description

**Aim:** Verify that a deadline requires a description.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
deadline
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Add a task description, for example: `deadline submit report /by Friday`.
____________________________________________________________________________________________________

```

## Test case: deadline without due date marker

**Aim:** Verify that a deadline requires a `/by` marker.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
deadline return book
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Almost there - add a due date, for example: `deadline submit report /by Friday`.
____________________________________________________________________________________________________

```

## Test case: deadline with blank due date

**Aim:** Verify that a deadline cannot have an empty due date.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
deadline return book /by
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Add a date after `/by`, for example: `deadline submit report /by Friday`.
____________________________________________________________________________________________________

```

## Test case: event without description

**Aim:** Verify that an event requires a description.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
event
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Add an event description, for example: `event team meeting /from Mon 2pm /to 4pm`.
____________________________________________________________________________________________________

```

## Test case: event without from and to

**Aim:** Verify that an event requires both `/from` and `/to` markers.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
event meeting
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Add a start and end time, for example: `event team meeting /from Mon 2pm /to 4pm`.
____________________________________________________________________________________________________

```

## Test case: event without to

**Aim:** Verify that an event requires an ending time.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
event meeting /from Mon 2pm
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Add an end time after `/to`, for example: `event team meeting /from Mon 2pm /to 4pm`.
____________________________________________________________________________________________________

```

## Test case: event with blank from

**Aim:** Verify that an event cannot have an empty starting time.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
event meeting /from /to 4pm
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Add a start time after `/from`, for example: `event team meeting /from Mon 2pm /to 4pm`.
____________________________________________________________________________________________________

```

## Test case: mark without task number

**Aim:** Verify that marking requires a task number.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
mark
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Provide a task number, for example: `mark 1`.
____________________________________________________________________________________________________

```

## Test case: mark with non-number

**Aim:** Verify that marking rejects non-numeric task identifiers.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
mark abc
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Use a task number, for example: `mark 1`.
____________________________________________________________________________________________________

```

## Test case: mark nonexistent task

**Aim:** Verify that marking rejects a task number that is not in the list.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
mark 999
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 I couldn't find task 999. Try `list` to check the task number.
____________________________________________________________________________________________________

```

## Test case: mark task zero

**Aim:** Verify that task numbering starts at one.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
mark 0
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Task numbers start at 1. Try `list` to check the task number.
____________________________________________________________________________________________________

```

## Test case: list on empty list

**Aim:** Verify the response when the task list is empty.
**Command:** `rm -f data/fein.txt && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
list
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Your list is empty! Try adding a task with `todo <description>`.
Example: todo buy milk
____________________________________________________________________________________________________

```

## Test case: create and list all task types

**Aim:** Verify that todos, deadlines, and events are parsed and displayed with their type and date/time text.
**Command:** `rm -f data/fein.txt && mkdir -p /tmp/fein-ui-build && javac -d /tmp/fein-ui-build $(find src/main/java/fein -name '*.java' ! -name 'Main.java' ! -name 'Launcher.java' ! -name 'MainWindow.java' ! -name 'DialogBox.java') && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Sunday)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________________________________________________
____________________________________________________________________________________________________
Bye! Keeping feining and stay LIT!
____________________________________________________________________________________________________

```

## Test case: save tasks after list changes

**Aim:** Verify that adding, marking, unmarking, and deleting tasks automatically writes the current task list to `data/fein.txt`.
**Command:** `rm -f data/fein.txt && mkdir -p /tmp/fein-ui-build && javac -d /tmp/fein-ui-build $(find src/main/java/fein -name '*.java' ! -name 'Main.java' ! -name 'Launcher.java' ! -name 'MainWindow.java' ! -name 'DialogBox.java') && java -cp /tmp/fein-ui-build fein.Fein && test "$(wc -l < data/fein.txt)" -eq 2 && grep -Fq "T | 0 | read book" data/fein.txt && grep -Fq "E | 0 | project meeting | Aug 6th 2pm | 4pm" data/fein.txt`
**Inputs:**
```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
mark 1
unmark 1
delete 2
bye
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: June 6th)
 Now you have 2 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 3 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: June 6th)
 Now you have 2 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
Bye! Keeping feining and stay LIT!
____________________________________________________________________________________________________

```

## Test case: parse numeric deadline date and time

**Aim:** Verify that Fein interprets a numeric deadline as a date and time and displays it in a readable format.
**Command:** `rm -f data/fein.txt && mkdir -p /tmp/fein-ui-build && javac -d /tmp/fein-ui-build $(find src/main/java/fein -name '*.java' ! -name 'Main.java' ! -name 'Launcher.java' ! -name 'MainWindow.java' ! -name 'DialogBox.java') && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
deadline return book /by 2/12/2019 1800
list
bye
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 2 2019, 6:00 PM)
 Now you have 1 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 2 2019, 6:00 PM)
____________________________________________________________________________________________________
____________________________________________________________________________________________________
Bye! Keeping feining and stay LIT!
____________________________________________________________________________________________________

```

## Test case: find tasks by keyword

**Aim:** Verify that find displays tasks whose descriptions contain the keyword, ignoring case.
**Command:** `mkdir -p /tmp/fein-ui-work/data && rm -f /tmp/fein-ui-work/data/fein.txt && javac -d /tmp/fein-ui-build $(find src/main/java/fein -name '*.java' ! -name 'Main.java' ! -name 'Launcher.java' ! -name 'MainWindow.java' ! -name 'DialogBox.java') && cd /tmp/fein-ui-work && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
todo read book
todo attend class
find BOOK
bye
```

**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Got it. I've added this task:
   [T][ ] attend class
 Now you have 2 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Here are the matching tasks in your list:
 1.[T][ ] read book
____________________________________________________________________________________________________
____________________________________________________________________________________________________
Bye! Keeping feining and stay LIT!
____________________________________________________________________________________________________

```

## Test case: display the help guide

**Aim:** Verify that help lists every core command, its purpose, and examples where needed.
**Command:** `mkdir -p /tmp/fein-ui-help/data /tmp/fein-ui-build && rm -f /tmp/fein-ui-help/data/fein.txt && javac -d /tmp/fein-ui-build $(find src/main/java/fein -name '*.java' ! -name 'Main.java' ! -name 'Launcher.java' ! -name 'MainWindow.java' ! -name 'DialogBox.java') && cd /tmp/fein-ui-help && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
help
bye
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 FEIN command list:

todo <description>
Purpose: Add a task without a date or time.
Example: todo buy milk

deadline <description> /by <due date>
Purpose: Add a task with a deadline.
Example: deadline submit report /by Friday

event <description> /from <start> /to <end>
Purpose: Add a task with a start and end time.
Example: event team meeting /from 2pm /to 4pm

find <keyword>
Purpose: Find tasks containing a word or phrase.
Example: find report

mark <task number>
Purpose: Mark a task as completed.
Example: mark 1

unmark <task number>
Purpose: Mark a completed task as not completed.
Example: unmark 1

delete <task number>
Purpose: Remove a task from your list.
Example: delete 1

Quick commands:
list
Purpose: Show all tasks in your list.

help
Purpose: Show this command guide.

bye
Purpose: Exit Fein.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
Bye! Keeping feining and stay LIT!
____________________________________________________________________________________________________

```

## Test case: reject a duplicate task

**Aim:** Verify that Fein rejects a case-insensitive duplicate instead of saving it twice.
**Command:** `mkdir -p /tmp/fein-ui-duplicates/data /tmp/fein-ui-build && rm -f /tmp/fein-ui-duplicates/data/fein.txt && javac -d /tmp/fein-ui-build $(find src/main/java/fein -name '*.java' ! -name 'Main.java' ! -name 'Launcher.java' ! -name 'MainWindow.java' ! -name 'DialogBox.java') && cd /tmp/fein-ui-duplicates && java -cp /tmp/fein-ui-build fein.Fein`
**Inputs:**
```text
todo buy milk
todo Buy Milk
list
bye
```
**Expected output:**
```text
____________________________________________________________________________________________________
oooooooooooo           o8o                     
`888'     `8           `"'                     
 888          .ooooo.  oooo  ooo. .oo.         
 888oooo8    d88' `88b `888  `888P"Y88b        
 888    "    888ooo888  888   888   888        
 888         888    .o  888   888   888        
o888o        `Y8bod8P' o888o o888o o888o       

Hello! I'm Fein.
What can I do for you?
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Got it. I've added this task:
   [T][ ] buy milk
 Now you have 1 tasks in your list.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 That task is already on your list. Try `list` to check it.
____________________________________________________________________________________________________
____________________________________________________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy milk
____________________________________________________________________________________________________
____________________________________________________________________________________________________
Bye! Keeping feining and stay LIT!
____________________________________________________________________________________________________

```
