# UI Test Plan

This file is the source of truth for the scripted command-line UI tests. Run it from the repository root with:

```powershell
& "<python-3>" .codex/skills/test-ui/scripts/run_ui_tests.py --timeout 60
```

## Test Case 17: Reject malformed and duplicate task commands

### Aim

Verify that command whitespace is normalized and that invalid dates, reversed event times,
unexpected arguments, duplicate tasks, and repeated status changes produce clear errors.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai
```

### Inputs

```text
   todo   Read    Book
todo read book
deadline report /by 2026-02-30
event meeting /from 11am /to 10am
list extra
mark 1
mark 1
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] Read Book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! That task already exists.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! Invalid date or time: 2026-02-30.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! An event must end after it starts.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! 'list' does not take any arguments.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] Read Book
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! That task is already marked as done.
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
```

The runner executes test cases from top to bottom. It supplies the `Inputs` block to the command's standard input, compares the complete merged console output with `Expected output`, and stops immediately at the first failure. Line-ending differences and final newline characters are ignored; spaces inside the output are significant. The 60-second per-case timeout allows the repeated Java 25 compilation step to complete reliably on slower runs.

Java commands in this plan require JDK 25. The compile step is included in each command so that the plan can be run from a clean checkout.

## Test Case 1: Exit with `bye`

### Aim

Verify that the application starts successfully and exits with the expected farewell when the user enters `bye`.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai
```

### Inputs

```text
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
```

## Test Case 2: Reject invalid task numbers without changing completion state

### Aim

Verify that missing, non-numeric, zero, and out-of-range task numbers are rejected, while valid mark and unmark commands update the intended task only.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai
```

### Inputs

```text
todo submit assignment
mark
mark abc
mark 0
mark 1
unmark nope
unmark 2
unmark 1
list
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] submit assignment
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! 'mark' needs a task number, such as 'mark 1'.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! Task numbers must be positive whole numbers, such as 'mark 1'.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! Task 0 does not exist. Use 'list' to see your tasks.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] submit assignment
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! Task numbers must be positive whole numbers, such as 'unmark 1'.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! Task 2 does not exist. Use 'list' to see your tasks.
    ____________________________________________________________

    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] submit assignment
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] submit assignment
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
```

## Test Case 3: Handle blank and whitespace-padded input

### Aim

Verify that a blank command is rejected without adding a task and that a valid command with leading and internal extra whitespace is parsed correctly.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai
```

### Inputs

```text

  todo   read book
list
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! I don't recognize that command. Try 'list' to view your tasks.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
```

## Test Case 4: Add and list all task types

### Aim

Verify that todo, deadline, and event commands preserve their descriptions and date/time strings, display the correct type markers, and appear in the task list.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai
```

### Inputs

```text
todo borrow book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
list
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: June 6th)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: June 6th)
     3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
```

## Test Case 5: Explain invalid input

### Aim

Verify that an empty todo description and an unknown command produce helpful error messages and that the application continues accepting commands.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai
```

### Inputs

```text
todo
blah
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! A todo needs a description. Try: todo <description>.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! I don't recognize that command. Try 'list' to view your tasks.
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
```

## Test Case 6: Reject malformed task creation without changing the list

### Aim

Verify that malformed todo, deadline, and event commands are rejected, while valid commands interleaved between them still create exactly the expected tasks.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai
```

### Inputs

```text
todo
todo buy milk
deadline /by tomorrow
deadline submit report /by tomorrow
event team sync /from 10am
event team sync /from 10am /to 11am
list
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! A todo needs a description. Try: todo <description>.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] buy milk
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! A deadline needs both a description and a /by date or time.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] submit report (by: tomorrow)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! An event must look like: event <description> /from <start> /to <end>.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] team sync (from: 10am to: 11am)
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] buy milk
     2.[D][ ] submit report (by: tomorrow)
     3.[E][ ] team sync (from: 10am to: 11am)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
```

## Test Case 7: Delete tasks and validate task numbers

### Aim

Verify that a task can be deleted by its displayed number, that the remaining tasks are renumbered, and that invalid delete commands do not change the list.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai
```

### Inputs

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
delete 2
delete abc
delete 3
delete
list
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: June 6th)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Noted. I've removed this task:
       [D][ ] return book (by: June 6th)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! Task numbers must be positive whole numbers, such as 'delete 1'.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! Task 3 does not exist. Use 'list' to see your tasks.
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! 'delete' needs a task number, such as 'delete 1'.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
```

## Test Case 8: Start with no data folder

### Aim

Verify that the chatbot starts when both the data folder and file are absent, and creates them automatically when the first task is saved.

### Command

```text
(if exist data rmdir /s /q data) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai && type data\niulai.txt
```

### Inputs

```text
todo first run
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] first run
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
T | 0 | first run
```

## Test Case 9: Preserve special characters when saving

### Aim

Verify that flexible whitespace is accepted and that pipes and backslashes in task fields are escaped in the file without changing the displayed task text.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai && type data\niulai.txt
```

### Inputs

```text
  todo   read | review \ draft
deadline submit | report /by June | 6th
event team | sync /from 10 | 11 /to 12 | 13
list
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read | review \ draft
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] submit | report (by: June | 6th)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] team | sync (from: 10 | 11 to: 12 | 13)
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read | review \ draft
     2.[D][ ] submit | report (by: June | 6th)
     3.[E][ ] team | sync (from: 10 | 11 to: 12 | 13)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
T | 0 | read \| review \\ draft
D | 0 | submit \| report | June \| 6th
E | 0 | team \| sync | 10 \| 11 | 12 \| 13
```

## Test Case 10: Load escaped task fields

### Aim

Verify that escaped pipes and backslashes are decoded when the chatbot starts with the file produced by Test Case 9.

### Setup

This case runs after Test Case 9, which leaves the escaped task data in `data\niulai.txt`.

### Command

```text
javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai
```

### Inputs

```text
list
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read | review \ draft
     2.[D][ ] submit | report (by: June | 6th)
     3.[E][ ] team | sync (from: 10 | 11 to: 12 | 13)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
```

## Test Case 11: Recover from malformed saved data

### Aim

Verify that valid records survive a malformed saved line, the skipped line is reported, and the
original file is backed up before a recovered list is saved.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & (if exist data\niulai.txt.bak del data\niulai.txt.bak) & (if exist data\niulai.txt.bak.1 del data\niulai.txt.bak.1) & (if not exist data mkdir data) & (echo T ^| 0 ^| saved task>data\niulai.txt) & (echo malformed>>data\niulai.txt) & (echo D ^| 0 ^| submit report ^| tomorrow>>data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai && type data\niulai.txt.bak && type data\niulai.txt
```

### Inputs

```text
list
todo recovered task
list
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Warning: I skipped invalid task data on line 2.
     The original file will be backed up before your next saved change.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] saved task
     2.[D][ ] submit report (by: tomorrow)
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] recovered task
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] saved task
     2.[D][ ] submit report (by: tomorrow)
     3.[T][ ] recovered task
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
T | 0 | saved task
malformed
D | 0 | submit report | tomorrow
T | 0 | saved task
D | 0 | submit report | tomorrow
T | 0 | recovered task
```

## Test Case 12: Save tasks after list changes

### Aim

Verify that adding, marking, and deleting tasks automatically writes the current task list to the data file.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai && type data\niulai.txt
```

### Inputs

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
mark 1
delete 2
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: June 6th)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Noted. I've removed this task:
       [D][ ] return book (by: June 6th)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
T | 1 | read book
E | 0 | project meeting | Aug 6th 2pm | 4pm
```

## Test Case 13: Load saved tasks at startup

### Aim

Verify that a new chatbot session loads the tasks saved by the previous session, including each task type and completion state.

### Setup

This case runs after Test Case 12, which leaves `data\niulai.txt` containing the saved tasks shown below.

### Command

```text
javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai
```

### Inputs

```text
list
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
```

## Test Case 14: Parse and format deadline dates

### Aim

Verify that ISO dates and day/month/year dates with times are stored as date values, displayed in a readable format, and preserved when saved.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai && type data\niulai.txt
```

### Inputs

```text
deadline submit report /by 2019-10-15
deadline return book /by 2/12/2019 1800
list
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] submit report (by: Oct 15 2019)
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Dec 02 2019 6:00 PM)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] submit report (by: Oct 15 2019)
     2.[D][ ] return book (by: Dec 02 2019 6:00 PM)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
D | 0 | submit report | 2019-10-15
D | 0 | return book | 2019-12-02 1800
```

## Test Case 15: Find tasks by description keyword

### Aim

Verify that `find <keyword>` displays matching tasks case-insensitively and preserves their original task numbers.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai
```

### Inputs

```text
todo read book
todo buy groceries
deadline return book /by June 6th
mark 1
find BOOK
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] buy groceries
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: June 6th)
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][X] read book
     3.[D][ ] return book (by: June 6th)
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
```

## Test Case 16: Undo the most recent command

### Aim

Verify that `undo` removes the task created by the most recent successful state-changing command and that the command can only be undone once.

### Command

```text
(if exist data\niulai.txt del data\niulai.txt) & javac -d out src/main/java/niulai/NiuLai.java src/main/java/niulai/NiuLaiException.java src/main/java/niulai/model/Task.java src/main/java/niulai/model/TaskStatus.java src/main/java/niulai/model/TemporalValue.java src/main/java/niulai/model/DuplicateTaskException.java src/main/java/niulai/command/Command.java src/main/java/niulai/command/StatusCommand.java src/main/java/niulai/command/ExitCommand.java src/main/java/niulai/command/UndoCommand.java src/main/java/niulai/command/ListCommand.java src/main/java/niulai/command/DeleteCommand.java src/main/java/niulai/command/MarkCommand.java src/main/java/niulai/command/UnmarkCommand.java src/main/java/niulai/command/AddCommand.java src/main/java/niulai/command/FindCommand.java src/main/java/niulai/model/Todo.java src/main/java/niulai/model/Deadline.java src/main/java/niulai/model/Event.java src/main/java/niulai/model/TaskList.java src/main/java/niulai/service/Parser.java src/main/java/niulai/service/Ui.java src/main/java/niulai/service/StorageException.java src/main/java/niulai/service/Storage.java && java -cp out niulai.NiuLai
```

### Inputs

```text
todo read book
undo
list
undo
bye
```

### Expected output

```text
|\ | | |  | |     /\  |
| \| | \__/ |___ /~~\ |

    ____________________________________________________________
     Hello! I'm NiuLai!
     What can I do for you?
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     OK, I've undone the last command.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
    ____________________________________________________________

    ____________________________________________________________
     NOOO!!! There is nothing to undo.
    ____________________________________________________________

    ____________________________________________________________
     Bye. Hope not to see you again.
    ____________________________________________________________
```
