# UI test plan

This file is the source of truth for Mouse chatbot UI tests. The `test-ui` skill runs each test case in order.

## Program

- Main class: `mouse.Mouse`
- Source files: `src/main/java/mouse/Mouse.java`, `mouse/MouseException.java`, `mouse/ui/Ui.java`, `mouse/parser/Parser.java`, `mouse/parser/CommandType.java`, `mouse/task/TaskList.java`, `mouse/task/Task.java`, `mouse/task/ToDo.java`, `mouse/task/Deadline.java`, `mouse/task/Event.java`, `mouse/task/TaskType.java`
- Java: Azul JDK 25 (`25.0.3.fx-zulu`)
- Each test case starts a **new** program process, so task lists do not carry over.

## How to run

From the repository root:

```bash
python3 .cursor/skills/test-ui/scripts/run-ui-tests.py
```

## Test case format

Each test case has an aim, a list of console inputs (one command per line), and the exact expected console output.

## Test case: greet-and-exit

**Aim:** Greet the user and exit when they type bye.

**Inputs:**

```text
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```

## Test case: add-task

**Aim:** Add a task and confirm it was added before exiting.

**Inputs:**

```text
todo read book
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] read book
     The stash now holds 1 crumb.
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```

## Test case: add-todo-deadline-event

**Aim:** Add a todo, deadline, and event, then list them to confirm polymorphic display.

**Inputs:**

```text
todo borrow book
deadline submit report /by Friday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] borrow book
     The stash now holds 1 crumb.
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [D][ ] submit report (by: Friday)
     The stash now holds 2 crumbs.
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     The stash now holds 3 crumbs.
    ____________________________________________________________
    ____________________________________________________________
     Crumbs in the stash:
     1.[T][ ] borrow book
     2.[D][ ] submit report (by: Friday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```

## Test case: list-mark-unmark

**Aim:** Add two tasks, list them, mark the second as done, unmark it, then list again.

**Inputs:**

```text
todo read book
todo return book
list
mark 2
unmark 2
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] read book
     The stash now holds 1 crumb.
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] return book
     The stash now holds 2 crumbs.
    ____________________________________________________________
    ____________________________________________________________
     Crumbs in the stash:
     1.[T][ ] read book
     2.[T][ ] return book
    ____________________________________________________________
    ____________________________________________________________
     Nibble done. Marked this crumb:
       [T][X] return book
    ____________________________________________________________
    ____________________________________________________________
     Un-nibbled. This crumb is open again:
       [T][ ] return book
    ____________________________________________________________
    ____________________________________________________________
     Crumbs in the stash:
     1.[T][ ] read book
     2.[T][ ] return book
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```

## Test case: invalid-commands

**Aim:** Test unknown commands and typos.

**Inputs:**

```text
blah
todo
todo   
list
mark
unmark
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! Mouse no understand. Give cheese (or a real command).
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! That crumb has no name GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! That crumb has no name GRR
    ____________________________________________________________
    ____________________________________________________________
     Crumbs in the stash:
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! Mouse needs a crumb number after mark GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! Mouse needs a crumb number after unmark GRR
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```

## Test case: missing-delimiters

**Aim:** Test tasks missing required date/time delimiters.

**Inputs:**

```text
deadline return book
event meeting
event meeting /to 4pm
event meeting /from Mon
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! A deadline crumb needs a '/by' time GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! An event crumb needs both '/from' and '/to' times GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! An event crumb needs both '/from' and '/to' times GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! An event crumb needs both '/from' and '/to' times GRR
    ____________________________________________________________
    ____________________________________________________________
     Crumbs in the stash:
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```

## Test case: invalid-task-numbers

**Aim:** Test marking and unmarking invalid task numbers.

**Inputs:**

```text
todo read book
mark 2
unmark 0
mark abc
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] read book
     The stash now holds 1 crumb.
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! That crumb is not in the stash GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! Crumb numbers start at 1 GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! That's not a valid crumb number GRR
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```

## Test case: mixed-valid-invalid

**Aim:** Interleave positive and negative test cases to detect incorrect inputs affecting the correctness of the internal states.

**Inputs:**

```text
todo borrow book
blah
deadline return book /by Sunday
todo 
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] borrow book
     The stash now holds 1 crumb.
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! Mouse no understand. Give cheese (or a real command).
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [D][ ] return book (by: Sunday)
     The stash now holds 2 crumbs.
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! That crumb has no name GRR
    ____________________________________________________________
    ____________________________________________________________
     Crumbs in the stash:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: Sunday)
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```

## Test case: delete-task

**Aim:** Delete a task from the middle of the list.

**Inputs:**

```text
todo read book
todo return book
todo buy bread
list
delete 2
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] read book
     The stash now holds 1 crumb.
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] return book
     The stash now holds 2 crumbs.
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] buy bread
     The stash now holds 3 crumbs.
    ____________________________________________________________
    ____________________________________________________________
     Crumbs in the stash:
     1.[T][ ] read book
     2.[T][ ] return book
     3.[T][ ] buy bread
    ____________________________________________________________
    ____________________________________________________________
     Tossed this crumb:
       [T][ ] return book
     The stash now holds 2 crumbs.
    ____________________________________________________________
    ____________________________________________________________
     Crumbs in the stash:
     1.[T][ ] read book
     2.[T][ ] buy bread
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```

## Test case: invalid-delete

**Aim:** Test deleting invalid task numbers.

**Inputs:**

```text
todo read book
delete 2
delete 0
delete abc
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] read book
     The stash now holds 1 crumb.
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! That crumb is not in the stash GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! Crumb numbers start at 1 GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! That's not a valid crumb number GRR
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```

## Test case: delete-preserves-done-status

**Aim:** Delete a completed task and confirm remaining tasks keep their done status.

**Inputs:**

```text
todo first
todo second
mark 2
delete 1
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] first
     The stash now holds 1 crumb.
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] second
     The stash now holds 2 crumbs.
    ____________________________________________________________
    ____________________________________________________________
     Nibble done. Marked this crumb:
       [T][X] second
    ____________________________________________________________
    ____________________________________________________________
     Tossed this crumb:
       [T][ ] first
     The stash now holds 1 crumb.
    ____________________________________________________________
    ____________________________________________________________
     Crumbs in the stash:
     1.[T][X] second
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```

## Test case: delete-then-mark-reindexed

**Aim:** After deleting item 1, mark 2 should affect the task that shifted into that index.

**Inputs:**

```text
todo a
todo b
todo c
delete 1
mark 2
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] a
     The stash now holds 1 crumb.
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] b
     The stash now holds 2 crumbs.
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] c
     The stash now holds 3 crumbs.
    ____________________________________________________________
    ____________________________________________________________
     Tossed this crumb:
       [T][ ] a
     The stash now holds 2 crumbs.
    ____________________________________________________________
    ____________________________________________________________
     Nibble done. Marked this crumb:
       [T][X] c
    ____________________________________________________________
    ____________________________________________________________
     Crumbs in the stash:
     1.[T][ ] b
     2.[T][X] c
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```

## Test case: empty-list

**Aim:** List an empty task list at startup, then again after deleting the only task.

**Inputs:**

```text
list
todo only
delete 1
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     Crumbs in the stash:
    ____________________________________________________________
    ____________________________________________________________
     Stashed this crumb:
       [T][ ] only
     The stash now holds 1 crumb.
    ____________________________________________________________
    ____________________________________________________________
     Tossed this crumb:
       [T][ ] only
     The stash now holds 0 crumbs.
    ____________________________________________________________
    ____________________________________________________________
     Crumbs in the stash:
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```

## Test case: malformed-commands

**Aim:** Cover malformed delete indexes and incomplete deadline/event delimiters without crashing.

**Inputs:**

```text
delete
delete -1
delete 1 extra
deadline task /by
deadline /by Sunday
event task /from Monday /to
event task /to 4pm /from Mon
deadline foo /by a /by b
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
      __  __                      
     |  \/  | ___  _   _ ___  ___ 
     | |\/| |/ _ \| | | / __|/ _ \
     | |  | | (_) | |_| \__ \  __/
     |_|  |_|\___/ \__,_|___/\___|
     Squeak! I'm Mouse.
     Got any crumbs to stash?
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! Mouse needs a crumb number after delete GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! Crumb numbers start at 1 GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! Extra crumbs after the number GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! The '/by' time cannot be empty GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! That deadline crumb has no name GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! The '/to' time cannot be empty GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! The '/from' time must come before the '/to' time GRR
    ____________________________________________________________
    ____________________________________________________________
     SQUEAK!!! Use /by only once GRR
    ____________________________________________________________
    ____________________________________________________________
     Crumbs in the stash:
    ____________________________________________________________
    ____________________________________________________________
     Squeak. Mouse is off to nibble. See you.
    ____________________________________________________________
```
