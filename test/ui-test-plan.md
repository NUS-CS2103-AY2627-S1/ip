# Console UI Test Plan

Run these tests from the project root with:

```sh
python3 skills/test-ui/scripts/run_ui_tests.py
```
 
Each test case records its aim, command, console input, and complete expected console output. Test cases run in the order listed. The runner stops at the first failure.

## Test Case: Save tasks for the next startup

### Aim
Verify that a task created in one session is stored for a later session.

### Command
```sh
rm -f /private/tmp/pebby-ui-restart.txt && java -Dpebby.storage.path=/private/tmp/pebby-ui-restart.txt -cp build/classes/java/main pebby.Pebby
```

### Input
```text
todo persist this task
bye
```

### Expected Output
```text
____________________________________________________________
 ____       _     _          
|  _ \  ___| |__ | |__  _   _
| |_) |/ _ \ '_ \| '_ \| | | |
|  __/|  __/ |_) | |_) | |_| |
|_|    \___|_.__/|_.__/ \__, |
                         |___/
Hello! I'm Pebby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task: 
  [T] [ ] persist this task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye Bye!
____________________________________________________________
```

## Test Case: Load tasks after restart

### Aim
Verify that the task from the preceding session is available immediately after Pebby starts again.

### Command
```sh
java -Dpebby.storage.path=/private/tmp/pebby-ui-restart.txt -cp build/classes/java/main pebby.Pebby
```

### Input
```text
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____       _     _          
|  _ \  ___| |__ | |__  _   _
| |_) |/ _ \ '_ \| '_ \| | | |
|  __/|  __/ |_) | |_) | |_| |
|_|    \___|_.__/|_.__/ \__, |
                         |___/
Hello! I'm Pebby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T] [ ] persist this task
____________________________________________________________
____________________________________________________________
Bye Bye!
____________________________________________________________
```

## Test Case: Reject an invalid task number

### Aim
Verify that an invalid mark command reports an error instead of crashing Pebby.

### Command
```sh
rm -f /private/tmp/pebby-ui-invalid-mark.txt && java -Dpebby.storage.path=/private/tmp/pebby-ui-invalid-mark.txt -cp build/classes/java/main pebby.Pebby
```

### Input
```text
mark zero
bye
```

### Expected Output
```text
____________________________________________________________
 ____       _     _          
|  _ \  ___| |__ | |__  _   _
| |_) |/ _ \ '_ \| '_ \| | | |
|  __/|  __/ |_) | |_) | |_| |
|_|    \___|_.__/|_.__/ \__, |
                         |___/
Hello! I'm Pebby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Invalid mark: Please provide a whole task number.
____________________________________________________________
____________________________________________________________
Bye Bye!
____________________________________________________________
```

## Test Case: Add a deadline

### Aim
Verify that a deadline command accepts an ISO date, stores it as a date, and displays it readably.

### Command
```sh
rm -f /private/tmp/pebby-ui-deadline.txt && java -Dpebby.storage.path=/private/tmp/pebby-ui-deadline.txt -cp build/classes/java/main pebby.Pebby
```

### Input
```text
deadline return book /by 15 June 2019
bye
```

### Expected Output
```text
____________________________________________________________
 ____       _     _          
|  _ \  ___| |__ | |__  _   _
| |_) |/ _ \ '_ \| '_ \| | | |
|  __/|  __/ |_) | |_) | |_| |
|_|    \___|_.__/|_.__/ \__, |
                         |___/
Hello! I'm Pebby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task: 
  [D] [ ] return book (by: Jun 15 2019)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye Bye!
____________________________________________________________
```

## Test Case: Add a todo

### Aim
Verify that a todo command stores its description and reports the updated task count.

### Command
```sh
rm -f /private/tmp/pebby-ui-todo.txt && java -Dpebby.storage.path=/private/tmp/pebby-ui-todo.txt -cp build/classes/java/main pebby.Pebby
```

### Input
```text
todo read book
bye
```

### Expected Output
```text
____________________________________________________________
 ____       _     _          
|  _ \  ___| |__ | |__  _   _
| |_) |/ _ \ '_ \| '_ \| | | |
|  __/|  __/ |_) | |_) | |_| |
|_|    \___|_.__/|_.__/ \__, |
                         |___/
Hello! I'm Pebby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task: 
  [T] [ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye Bye!
____________________________________________________________
```

## Test Case: Add an event

### Aim
Verify that an event command stores its description, start time, end time, and updated task count.

### Command
```sh
rm -f /private/tmp/pebby-ui-event.txt && java -Dpebby.storage.path=/private/tmp/pebby-ui-event.txt -cp build/classes/java/main pebby.Pebby
```

### Input
```text
event project meeting /from 2019-12-02 /to 2019-12-03
bye
```

### Expected Output
```text
____________________________________________________________
 ____       _     _          
|  _ \  ___| |__ | |__  _   _
| |_) |/ _ \ '_ \| '_ \| | | |
|  __/|  __/ |_) | |_) | |_| |
|_|    \___|_.__/|_.__/ \__, |
                         |___/
Hello! I'm Pebby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task: 
  [E] [ ] project meeting (from: 2019-12-02 to: 2019-12-03)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye Bye!
____________________________________________________________
```

## Test Case: Reject an invalid deadline date

### Aim
Verify that an invalid deadline date produces a helpful error without stopping Pebby.

### Command
```sh
rm -f /private/tmp/pebby-ui-invalid-date.txt && java -Dpebby.storage.path=/private/tmp/pebby-ui-invalid-date.txt -cp build/classes/java/main pebby.Pebby
```

### Input
```text
deadline return book /by 2019-02-30
bye
```

### Expected Output
```text
____________________________________________________________
 ____       _     _          
|  _ \  ___| |__ | |__  _   _
| |_) |/ _ \ '_ \| '_ \| | | |
|  __/|  __/ |_) | |_) | |_| |
|_|    \___|_.__/|_.__/ \__, |
                         |___/
Hello! I'm Pebby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Invalid deadline: Please use a valid date as yyyy-MM-dd or d MMMM yyyy, for example 2019-12-02 or 15 June 2026.
____________________________________________________________
____________________________________________________________
Bye Bye!
____________________________________________________________
```

## Test Case: Reject malformed commands and invalid event ranges

### Aim
Verify that malformed whitespace and an event whose start date is not before its end date report errors and Pebby continues running.

### Command
```sh
rm -f /private/tmp/pebby-ui-invalid-command.txt && java -Dpebby.storage.path=/private/tmp/pebby-ui-invalid-command.txt -cp build/classes/java/main pebby.Pebby
```

### Input
```text
todo  read book
event meeting /from 2019-12-03 /to 2019-12-03
bye
```

### Expected Output
```text
____________________________________________________________
 ____       _     _          
|  _ \  ___| |__ | |__  _   _
| |_) |/ _ \ '_ \| '_ \| | | |
|  __/|  __/ |_) | |_) | |_| |
|_|    \___|_.__/|_.__/ \__, |
                         |___/
Hello! I'm Pebby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Invalid command: Use single spaces between command parts.
____________________________________________________________
____________________________________________________________
Invalid event: The start date must be before the end date.
____________________________________________________________
____________________________________________________________
Bye Bye!
____________________________________________________________
```

## Test Case: Find tasks by keyword

### Aim
Verify that find lists only tasks whose descriptions contain the requested keyword.

### Command
```sh
rm -f /private/tmp/pebby-ui-find.txt && java -Dpebby.storage.path=/private/tmp/pebby-ui-find.txt -cp build/classes/java/main pebby.Pebby
```

### Input
```text
deadline return book /by 2019-12-02
todo buy groceries
find book
bye
```

### Expected Output
```text
____________________________________________________________
 ____       _     _          
|  _ \  ___| |__ | |__  _   _
| |_) |/ _ \ '_ \| '_ \| | | |
|  __/|  __/ |_) | |_) | |_| |
|_|    \___|_.__/|_.__/ \__, |
                         |___/
Hello! I'm Pebby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task: 
  [D] [ ] return book (by: Dec 02 2019)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task: 
  [T] [ ] buy groceries
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1. [D] [ ] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
Bye Bye!
____________________________________________________________
```

## Test Case: Delete a task

### Aim
Verify that deleting a task by its 1-based list position removes the correct task and updates the task count.

### Command
```sh
rm -f /private/tmp/pebby-ui-delete.txt && java -Dpebby.storage.path=/private/tmp/pebby-ui-delete.txt -cp build/classes/java/main pebby.Pebby
```

### Input
```text
todo first task
todo second task
delete 1
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____       _     _          
|  _ \  ___| |__ | |__  _   _
| |_) |/ _ \ '_ \| '_ \| | | |
|  __/|  __/ |_) | |_) | |_| |
|_|    \___|_.__/|_.__/ \__, |
                         |___/
Hello! I'm Pebby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task: 
  [T] [ ] first task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task: 
  [T] [ ] second task
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task: 
[T] [ ] first task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T] [ ] second task
____________________________________________________________
____________________________________________________________
Bye Bye!
____________________________________________________________
```

## Test Case: View a schedule for a date

### Aim
Verify that a schedule includes events spanning the requested date and deadlines due that day, with incomplete tasks before completed tasks.

### Command
```sh
rm -f /private/tmp/pebby-ui-schedule.txt && java -Dpebby.storage.path=/private/tmp/pebby-ui-schedule.txt -cp build/classes/java/main pebby.Pebby
```

### Input
```text
event project meeting /from 2019-12-01 /to 2019-12-03
deadline return book /by 2019-12-02
deadline submit draft /by 2019-12-02
mark 3
schedule 2019-12-02
bye
```

### Expected Output
```text
____________________________________________________________
 ____       _     _          
|  _ \  ___| |__ | |__  _   _
| |_) |/ _ \ '_ \| '_ \| | | |
|  __/|  __/ |_) | |_) | |_| |
|_|    \___|_.__/|_.__/ \__, |
                         |___/
Hello! I'm Pebby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task: 
  [E] [ ] project meeting (from: 2019-12-01 to: 2019-12-03)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task: 
  [D] [ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task: 
  [D] [ ] submit draft (by: Dec 02 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done: 
  [D] [X] submit draft (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
Schedule for 2019-12-02:
1. [E] [ ] project meeting (from: 2019-12-01 to: 2019-12-03)
2. [D] [ ] return book (by: Dec 02 2019)
3. [D] [X] submit draft (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
Bye Bye!
____________________________________________________________
```

## Test Case: Show the command reference

### Aim
Verify that help displays every supported command in grouped, readable sections.

### Command
```sh
rm -f /private/tmp/pebby-ui-help.txt && java -Dpebby.storage.path=/private/tmp/pebby-ui-help.txt -cp build/classes/java/main pebby.Pebby
```

### Input
```text
help
bye
```

### Expected Output
```text
____________________________________________________________
 ____       _     _          
|  _ \  ___| |__ | |__  _   _
| |_) |/ _ \ '_ \| '_ \| | | |
|  __/|  __/ |_) | |_) | |_| |
|_|    \___|_.__/|_.__/ \__, |
                         |___/
Hello! I'm Pebby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here is what I can do:

ADD TASKS
• todo <task description>
• deadline <task description> /by <yyyy-MM-dd or d MMMM yyyy>
• event <task description> /from <yyyy-MM-dd or d MMMM yyyy> /to <yyyy-MM-dd or d MMMM yyyy>

MANAGE TASKS
• list
• mark <task number>
• unmark <task number>
• delete <task number>

FIND AND PLAN
• find <keyword>
• schedule <yyyy-MM-dd or d MMMM yyyy>

OTHER
• help
• bye
____________________________________________________________
____________________________________________________________
Bye Bye!
____________________________________________________________
```
