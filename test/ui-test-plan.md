# Console UI test plan

Run every case in order, even after a failure, using Java 25.
Run `python3 test/run-ui-tests.py` from the project root.
Each case uses an isolated temporary working directory; existing user data is untouched.
The runner compiles console sources into a temporary classes directory (`$MINERVA_CLASSES`).
Create `data/minerva.txt` from the exact storage fixture before each case.
For storage mode `directory`, create a directory at that path instead.
Expected output includes every banner, prompt, space, and newline. Expected stderr is empty and exit status is 0.

## Test case: Add a todo

**Aim:** Checks todo creation.

**Storage mode:** file

**Storage fixture:**

```text
```

**Command:**

```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

**Console input:**

```text
todo borrow book
bye
```

**Expected console output:**

```text
$$\      $$\       $$\                                                                         $$$$$$\  
$$$\    $$$ |      \__|                                                                       $$  __$$\ 
$$$$\  $$$$ |      $$\       $$$$$$$\         $$$$$$\         $$$$$$\        $$\    $$\       $$ /  $$ |
$$\$$\$$ $$ |      $$ |      $$  __$$\       $$  __$$\       $$  __$$\       \$$\  $$  |      $$$$$$$$ |
$$ \$$$  $$ |      $$ |      $$ |  $$ |      $$$$$$$$ |      $$ |  \__|       \$$\$$  /       $$  __$$ |
$$ |\$  /$$ |      $$ |      $$ |  $$ |      $$   ____|      $$ |              \$$$  /        $$ |  $$ |
$$ | \_/ $$ |      $$ |      $$ |  $$ |      \$$$$$$$\       $$ |               \$  /         $$ |  $$ |
\__|     \__|      \__|      \__|  \__|       \_______|      \__|                \_/          \__|  \__|
Hello! I'm minerva.Minerva! Ask me anything. *Type help for list of commands
What can I do for you today :)? 
Awaiting your input: 
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
What can I do for you today :)? 
Awaiting your input: 
Bye. And I hope to see you again soon!
```

## Test case: List tasks

**Aim:** Checks listing all dated task types.

**Storage mode:** file

**Storage fixture:**

```text
```

**Command:**

```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

**Console input:**

```text
todo borrow book
deadline return book /by 2026-09-30
event project meeting /from 2026-09-20 /to 4pm
list
bye
```

**Expected console output:**

```text
$$\      $$\       $$\                                                                         $$$$$$\  
$$$\    $$$ |      \__|                                                                       $$  __$$\ 
$$$$\  $$$$ |      $$\       $$$$$$$\         $$$$$$\         $$$$$$\        $$\    $$\       $$ /  $$ |
$$\$$\$$ $$ |      $$ |      $$  __$$\       $$  __$$\       $$  __$$\       \$$\  $$  |      $$$$$$$$ |
$$ \$$$  $$ |      $$ |      $$ |  $$ |      $$$$$$$$ |      $$ |  \__|       \$$\$$  /       $$  __$$ |
$$ |\$  /$$ |      $$ |      $$ |  $$ |      $$   ____|      $$ |              \$$$  /        $$ |  $$ |
$$ | \_/ $$ |      $$ |      $$ |  $$ |      \$$$$$$$\       $$ |               \$  /         $$ |  $$ |
\__|     \__|      \__|      \__|  \__|       \_______|      \__|                \_/          \__|  \__|
Hello! I'm minerva.Minerva! Ask me anything. *Type help for list of commands
What can I do for you today :)? 
Awaiting your input: 
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
What can I do for you today :)? 
Awaiting your input: 
Got it. I've added this task:
[D][ ] return book (by: Sep 30 2026)
Now you have 2 tasks in the list.
What can I do for you today :)? 
Awaiting your input: 
Got it. I've added this task:
[E][ ] project meeting (from: Sep 20 2026 to: 4pm)
Now you have 3 tasks in the list.
What can I do for you today :)? 
Awaiting your input: 
Here are the tasks in your list:
1. [T][ ] borrow book
2. [D][ ] return book (by: Sep 30 2026)
3. [E][ ] project meeting (from: Sep 20 2026 to: 4pm)
What can I do for you today :)? 
Awaiting your input: 
Bye. And I hope to see you again soon!
```

## Test case: Add a deadline

**Aim:** Checks deadline creation.

**Storage mode:** file

**Storage fixture:**

```text
```

**Command:**

```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

**Console input:**

```text
deadline return book /by 2026-09-30
bye
```

**Expected console output:**

```text
$$\      $$\       $$\                                                                         $$$$$$\  
$$$\    $$$ |      \__|                                                                       $$  __$$\ 
$$$$\  $$$$ |      $$\       $$$$$$$\         $$$$$$\         $$$$$$\        $$\    $$\       $$ /  $$ |
$$\$$\$$ $$ |      $$ |      $$  __$$\       $$  __$$\       $$  __$$\       \$$\  $$  |      $$$$$$$$ |
$$ \$$$  $$ |      $$ |      $$ |  $$ |      $$$$$$$$ |      $$ |  \__|       \$$\$$  /       $$  __$$ |
$$ |\$  /$$ |      $$ |      $$ |  $$ |      $$   ____|      $$ |              \$$$  /        $$ |  $$ |
$$ | \_/ $$ |      $$ |      $$ |  $$ |      \$$$$$$$\       $$ |               \$  /         $$ |  $$ |
\__|     \__|      \__|      \__|  \__|       \_______|      \__|                \_/          \__|  \__|
Hello! I'm minerva.Minerva! Ask me anything. *Type help for list of commands
What can I do for you today :)? 
Awaiting your input: 
Got it. I've added this task:
[D][ ] return book (by: Sep 30 2026)
Now you have 1 tasks in the list.
What can I do for you today :)? 
Awaiting your input: 
Bye. And I hope to see you again soon!
```

## Test case: Add an event

**Aim:** Checks event creation.

**Storage mode:** file

**Storage fixture:**

```text
```

**Command:**

```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

**Console input:**

```text
event project meeting /from 2026-09-20 /to 4pm
bye
```

**Expected console output:**

```text
$$\      $$\       $$\                                                                         $$$$$$\  
$$$\    $$$ |      \__|                                                                       $$  __$$\ 
$$$$\  $$$$ |      $$\       $$$$$$$\         $$$$$$\         $$$$$$\        $$\    $$\       $$ /  $$ |
$$\$$\$$ $$ |      $$ |      $$  __$$\       $$  __$$\       $$  __$$\       \$$\  $$  |      $$$$$$$$ |
$$ \$$$  $$ |      $$ |      $$ |  $$ |      $$$$$$$$ |      $$ |  \__|       \$$\$$  /       $$  __$$ |
$$ |\$  /$$ |      $$ |      $$ |  $$ |      $$   ____|      $$ |              \$$$  /        $$ |  $$ |
$$ | \_/ $$ |      $$ |      $$ |  $$ |      \$$$$$$$\       $$ |               \$  /         $$ |  $$ |
\__|     \__|      \__|      \__|  \__|       \_______|      \__|                \_/          \__|  \__|
Hello! I'm minerva.Minerva! Ask me anything. *Type help for list of commands
What can I do for you today :)? 
Awaiting your input: 
Got it. I've added this task:
[E][ ] project meeting (from: Sep 20 2026 to: 4pm)
Now you have 1 tasks in the list.
What can I do for you today :)? 
Awaiting your input: 
Bye. And I hope to see you again soon!
```

## Test case: Reload task types and completion

**Aim:** Checks loading the corrected storage format.

**Storage mode:** file

**Storage fixture:**

```text
T | 1 | read book
D | 0 | submit | 2026-09-30
E | 1 | meeting | 2026-09-20 | 3pm
1 | plan weekend
```

**Command:**

```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

**Console input:**

```text
list
bye
```

**Expected console output:**

```text
$$\      $$\       $$\                                                                         $$$$$$\  
$$$\    $$$ |      \__|                                                                       $$  __$$\ 
$$$$\  $$$$ |      $$\       $$$$$$$\         $$$$$$\         $$$$$$\        $$\    $$\       $$ /  $$ |
$$\$$\$$ $$ |      $$ |      $$  __$$\       $$  __$$\       $$  __$$\       \$$\  $$  |      $$$$$$$$ |
$$ \$$$  $$ |      $$ |      $$ |  $$ |      $$$$$$$$ |      $$ |  \__|       \$$\$$  /       $$  __$$ |
$$ |\$  /$$ |      $$ |      $$ |  $$ |      $$   ____|      $$ |              \$$$  /        $$ |  $$ |
$$ | \_/ $$ |      $$ |      $$ |  $$ |      \$$$$$$$\       $$ |               \$  /         $$ |  $$ |
\__|     \__|      \__|      \__|  \__|       \_______|      \__|                \_/          \__|  \__|
Hello! I'm minerva.Minerva! Ask me anything. *Type help for list of commands
What can I do for you today :)? 
Awaiting your input: 
Here are the tasks in your list:
1. [T][X] read book
2. [D][ ] submit (by: Sep 30 2026)
3. [E][X] meeting (from: Sep 20 2026 to: 3pm)
4. [X] plan weekend
What can I do for you today :)? 
Awaiting your input: 
Bye. And I hope to see you again soon!
```

## Test case: Reject storage separators

**Aim:** Checks rejection without changing tasks or undo history.

**Storage mode:** file

**Storage fixture:**

```text
```

**Command:**

```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

**Console input:**

```text
todo read | revise
task read|revise
deadline read | revise /by 2026-09-30
event read | revise /from 2026-09-20 /to 3pm
event meeting /from 2026-09-20 /to 3pm | 4pm
list
undo
bye
```

**Expected console output:**

```text
$$\      $$\       $$\                                                                         $$$$$$\  
$$$\    $$$ |      \__|                                                                       $$  __$$\ 
$$$$\  $$$$ |      $$\       $$$$$$$\         $$$$$$\         $$$$$$\        $$\    $$\       $$ /  $$ |
$$\$$\$$ $$ |      $$ |      $$  __$$\       $$  __$$\       $$  __$$\       \$$\  $$  |      $$$$$$$$ |
$$ \$$$  $$ |      $$ |      $$ |  $$ |      $$$$$$$$ |      $$ |  \__|       \$$\$$  /       $$  __$$ |
$$ |\$  /$$ |      $$ |      $$ |  $$ |      $$   ____|      $$ |              \$$$  /        $$ |  $$ |
$$ | \_/ $$ |      $$ |      $$ |  $$ |      \$$$$$$$\       $$ |               \$  /         $$ |  $$ |
\__|     \__|      \__|      \__|  \__|       \_______|      \__|                \_/          \__|  \__|
Hello! I'm minerva.Minerva! Ask me anything. *Type help for list of commands
What can I do for you today :)? 
Awaiting your input: 
Please avoid pipes (|) and line breaks in task details.
What can I do for you today :)? 
Awaiting your input: 
Please avoid pipes (|) and line breaks in task details.
What can I do for you today :)? 
Awaiting your input: 
Please avoid pipes (|) and line breaks in task details.
What can I do for you today :)? 
Awaiting your input: 
Please avoid pipes (|) and line breaks in task details.
What can I do for you today :)? 
Awaiting your input: 
Please avoid pipes (|) and line breaks in task details.
What can I do for you today :)? 
Awaiting your input: 
Here are the tasks in your list:
What can I do for you today :)? 
Awaiting your input: 
There’s nothing to undo yet, but I’m ready when you are.
What can I do for you today :)? 
Awaiting your input: 
Bye. And I hope to see you again soon!
```

## Test case: Invalid mark preserves undo

**Aim:** Checks invalid indices with assertions enabled.

**Storage mode:** file

**Storage fixture:**

```text
```

**Command:**

```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

**Console input:**

```text
todo one
mark 99
mark 0
undo
list
bye
```

**Expected console output:**

```text
$$\      $$\       $$\                                                                         $$$$$$\  
$$$\    $$$ |      \__|                                                                       $$  __$$\ 
$$$$\  $$$$ |      $$\       $$$$$$$\         $$$$$$\         $$$$$$\        $$\    $$\       $$ /  $$ |
$$\$$\$$ $$ |      $$ |      $$  __$$\       $$  __$$\       $$  __$$\       \$$\  $$  |      $$$$$$$$ |
$$ \$$$  $$ |      $$ |      $$ |  $$ |      $$$$$$$$ |      $$ |  \__|       \$$\$$  /       $$  __$$ |
$$ |\$  /$$ |      $$ |      $$ |  $$ |      $$   ____|      $$ |              \$$$  /        $$ |  $$ |
$$ | \_/ $$ |      $$ |      $$ |  $$ |      \$$$$$$$\       $$ |               \$  /         $$ |  $$ |
\__|     \__|      \__|      \__|  \__|       \_______|      \__|                \_/          \__|  \__|
Hello! I'm minerva.Minerva! Ask me anything. *Type help for list of commands
What can I do for you today :)? 
Awaiting your input: 
Got it. I've added this task:
[T][ ] one
Now you have 1 tasks in the list.
What can I do for you today :)? 
Awaiting your input: 
I couldn’t find that task. Please choose a number from 1 to 1.
What can I do for you today :)? 
Awaiting your input: 
I couldn’t find that task. Please choose a number from 1 to 1.
What can I do for you today :)? 
Awaiting your input: 
Alright, I've undone the previous valid operation. Current state: <3 
Here are the tasks in your list:
What can I do for you today :)? 
Awaiting your input: 
Here are the tasks in your list:
What can I do for you today :)? 
Awaiting your input: 
Bye. And I hope to see you again soon!
```

## Test case: Invalid unmark preserves undo

**Aim:** Checks invalid indices with assertions enabled.

**Storage mode:** file

**Storage fixture:**

```text
```

**Command:**

```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

**Console input:**

```text
todo one
unmark 99
unmark 0
undo
list
bye
```

**Expected console output:**

```text
$$\      $$\       $$\                                                                         $$$$$$\  
$$$\    $$$ |      \__|                                                                       $$  __$$\ 
$$$$\  $$$$ |      $$\       $$$$$$$\         $$$$$$\         $$$$$$\        $$\    $$\       $$ /  $$ |
$$\$$\$$ $$ |      $$ |      $$  __$$\       $$  __$$\       $$  __$$\       \$$\  $$  |      $$$$$$$$ |
$$ \$$$  $$ |      $$ |      $$ |  $$ |      $$$$$$$$ |      $$ |  \__|       \$$\$$  /       $$  __$$ |
$$ |\$  /$$ |      $$ |      $$ |  $$ |      $$   ____|      $$ |              \$$$  /        $$ |  $$ |
$$ | \_/ $$ |      $$ |      $$ |  $$ |      \$$$$$$$\       $$ |               \$  /         $$ |  $$ |
\__|     \__|      \__|      \__|  \__|       \_______|      \__|                \_/          \__|  \__|
Hello! I'm minerva.Minerva! Ask me anything. *Type help for list of commands
What can I do for you today :)? 
Awaiting your input: 
Got it. I've added this task:
[T][ ] one
Now you have 1 tasks in the list.
What can I do for you today :)? 
Awaiting your input: 
I couldn’t find that task. Please choose a number from 1 to 1.
What can I do for you today :)? 
Awaiting your input: 
I couldn’t find that task. Please choose a number from 1 to 1.
What can I do for you today :)? 
Awaiting your input: 
Alright, I've undone the previous valid operation. Current state: <3 
Here are the tasks in your list:
What can I do for you today :)? 
Awaiting your input: 
Here are the tasks in your list:
What can I do for you today :)? 
Awaiting your input: 
Bye. And I hope to see you again soon!
```

## Test case: Invalid delete preserves undo

**Aim:** Checks invalid indices with assertions enabled.

**Storage mode:** file

**Storage fixture:**

```text
```

**Command:**

```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

**Console input:**

```text
todo one
delete 99
delete 0
undo
list
bye
```

**Expected console output:**

```text
$$\      $$\       $$\                                                                         $$$$$$\  
$$$\    $$$ |      \__|                                                                       $$  __$$\ 
$$$$\  $$$$ |      $$\       $$$$$$$\         $$$$$$\         $$$$$$\        $$\    $$\       $$ /  $$ |
$$\$$\$$ $$ |      $$ |      $$  __$$\       $$  __$$\       $$  __$$\       \$$\  $$  |      $$$$$$$$ |
$$ \$$$  $$ |      $$ |      $$ |  $$ |      $$$$$$$$ |      $$ |  \__|       \$$\$$  /       $$  __$$ |
$$ |\$  /$$ |      $$ |      $$ |  $$ |      $$   ____|      $$ |              \$$$  /        $$ |  $$ |
$$ | \_/ $$ |      $$ |      $$ |  $$ |      \$$$$$$$\       $$ |               \$  /         $$ |  $$ |
\__|     \__|      \__|      \__|  \__|       \_______|      \__|                \_/          \__|  \__|
Hello! I'm minerva.Minerva! Ask me anything. *Type help for list of commands
What can I do for you today :)? 
Awaiting your input: 
Got it. I've added this task:
[T][ ] one
Now you have 1 tasks in the list.
What can I do for you today :)? 
Awaiting your input: 
I couldn’t find that task. Please choose a number from 1 to 1.
What can I do for you today :)? 
Awaiting your input: 
I couldn’t find that task. Please choose a number from 1 to 1.
What can I do for you today :)? 
Awaiting your input: 
Alright, I've undone the previous valid operation. Current state: <3 
Here are the tasks in your list:
What can I do for you today :)? 
Awaiting your input: 
Here are the tasks in your list:
What can I do for you today :)? 
Awaiting your input: 
Bye. And I hope to see you again soon!
```

## Test case: Reject incorrect event separators

**Aim:** Checks reversed and duplicate separators.

**Storage mode:** file

**Storage fixture:**

```text
```

**Command:**

```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

**Console input:**

```text
event meeting /to 2026-09-20 /from 3pm
event meeting /from 2026-09-20 /from 3pm
event meeting /to 2026-09-20 /to 3pm
list
bye
```

**Expected console output:**

```text
$$\      $$\       $$\                                                                         $$$$$$\  
$$$\    $$$ |      \__|                                                                       $$  __$$\ 
$$$$\  $$$$ |      $$\       $$$$$$$\         $$$$$$\         $$$$$$\        $$\    $$\       $$ /  $$ |
$$\$$\$$ $$ |      $$ |      $$  __$$\       $$  __$$\       $$  __$$\       \$$\  $$  |      $$$$$$$$ |
$$ \$$$  $$ |      $$ |      $$ |  $$ |      $$$$$$$$ |      $$ |  \__|       \$$\$$  /       $$  __$$ |
$$ |\$  /$$ |      $$ |      $$ |  $$ |      $$   ____|      $$ |              \$$$  /        $$ |  $$ |
$$ | \_/ $$ |      $$ |      $$ |  $$ |      \$$$$$$$\       $$ |               \$  /         $$ |  $$ |
\__|     \__|      \__|      \__|  \__|       \_______|      \__|                \_/          \__|  \__|
Hello! I'm minerva.Minerva! Ask me anything. *Type help for list of commands
What can I do for you today :)? 
Awaiting your input: 
An event needs a description, a start time, and an end time. Please try again.
What can I do for you today :)? 
Awaiting your input: 
An event needs a description, a start time, and an end time. Please try again.
What can I do for you today :)? 
Awaiting your input: 
An event needs a description, a start time, and an end time. Please try again.
What can I do for you today :)? 
Awaiting your input: 
Here are the tasks in your list:
What can I do for you today :)? 
Awaiting your input: 
Bye. And I hope to see you again soon!
```

## Test case: Report save failure

**Aim:** Checks a blocked save path produces a visible warning.

**Storage mode:** directory

**Storage fixture:**

```text
```

**Command:**

```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

**Console input:**

```text
todo important
list
bye
```

**Expected console output:**

```text
I couldn’t find your saved tasks, so I’ll start with an empty list.
$$\      $$\       $$\                                                                         $$$$$$\  
$$$\    $$$ |      \__|                                                                       $$  __$$\ 
$$$$\  $$$$ |      $$\       $$$$$$$\         $$$$$$\         $$$$$$\        $$\    $$\       $$ /  $$ |
$$\$$\$$ $$ |      $$ |      $$  __$$\       $$  __$$\       $$  __$$\       \$$\  $$  |      $$$$$$$$ |
$$ \$$$  $$ |      $$ |      $$ |  $$ |      $$$$$$$$ |      $$ |  \__|       \$$\$$  /       $$  __$$ |
$$ |\$  /$$ |      $$ |      $$ |  $$ |      $$   ____|      $$ |              \$$$  /        $$ |  $$ |
$$ | \_/ $$ |      $$ |      $$ |  $$ |      \$$$$$$$\       $$ |               \$  /         $$ |  $$ |
\__|     \__|      \__|      \__|  \__|       \_______|      \__|                \_/          \__|  \__|
Hello! I'm minerva.Minerva! Ask me anything. *Type help for list of commands
What can I do for you today :)? 
Awaiting your input: 
Got it. I've added this task:
[T][ ] important
Now you have 1 tasks in the list.
Changes could not be saved. Please check the storage location.
What can I do for you today :)? 
Awaiting your input: 
Here are the tasks in your list:
1. [T][ ] important
What can I do for you today :)? 
Awaiting your input: 
Bye. And I hope to see you again soon!
```

