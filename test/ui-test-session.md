# Complete console UI test session

```text
openjdk version "25.0.3" 2026-04-21 LTS
OpenJDK Runtime Environment Zulu25.34+17-CA (build 25.0.3+9-LTS)
OpenJDK 64-Bit Server VM Zulu25.34+17-CA (build 25.0.3+9-LTS, mixed mode, sharing)
```

## 1. Add a todo: PASS

Aim: Checks todo creation.

Command:
```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

Console input:
```text
todo borrow book
bye
```

Captured stdout:
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

Captured stderr:
```text
```

Exit status: 0

## 2. List tasks: PASS

Aim: Checks listing all dated task types.

Command:
```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

Console input:
```text
todo borrow book
deadline return book /by 2026-09-30
event project meeting /from 2026-09-20 /to 4pm
list
bye
```

Captured stdout:
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

Captured stderr:
```text
```

Exit status: 0

## 3. Add a deadline: PASS

Aim: Checks deadline creation.

Command:
```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

Console input:
```text
deadline return book /by 2026-09-30
bye
```

Captured stdout:
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

Captured stderr:
```text
```

Exit status: 0

## 4. Add an event: PASS

Aim: Checks event creation.

Command:
```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

Console input:
```text
event project meeting /from 2026-09-20 /to 4pm
bye
```

Captured stdout:
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

Captured stderr:
```text
```

Exit status: 0

## 5. Reload task types and completion: PASS

Aim: Checks loading the corrected storage format.

Command:
```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

Console input:
```text
list
bye
```

Captured stdout:
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

Captured stderr:
```text
```

Exit status: 0

## 6. Reject storage separators: PASS

Aim: Checks rejection without changing tasks or undo history.

Command:
```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

Console input:
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

Captured stdout:
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

Captured stderr:
```text
```

Exit status: 0

## 7. Invalid mark preserves undo: PASS

Aim: Checks invalid indices with assertions enabled.

Command:
```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

Console input:
```text
todo one
mark 99
mark 0
undo
list
bye
```

Captured stdout:
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

Captured stderr:
```text
```

Exit status: 0

## 8. Invalid unmark preserves undo: PASS

Aim: Checks invalid indices with assertions enabled.

Command:
```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

Console input:
```text
todo one
unmark 99
unmark 0
undo
list
bye
```

Captured stdout:
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

Captured stderr:
```text
```

Exit status: 0

## 9. Invalid delete preserves undo: PASS

Aim: Checks invalid indices with assertions enabled.

Command:
```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

Console input:
```text
todo one
delete 99
delete 0
undo
list
bye
```

Captured stdout:
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

Captured stderr:
```text
```

Exit status: 0

## 10. Reject incorrect event separators: PASS

Aim: Checks reversed and duplicate separators.

Command:
```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

Console input:
```text
event meeting /to 2026-09-20 /from 3pm
event meeting /from 2026-09-20 /from 3pm
event meeting /to 2026-09-20 /to 3pm
list
bye
```

Captured stdout:
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

Captured stderr:
```text
```

Exit status: 0

## 11. Report save failure: PASS

Aim: Checks a blocked save path produces a visible warning.

Command:
```bash
java -ea -cp "$MINERVA_CLASSES" minerva.Minerva
```

Console input:
```text
todo important
list
bye
```

Captured stdout:
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

Captured stderr:
```text
```

Exit status: 0

## Failures

None.

11/11 cases passed.
