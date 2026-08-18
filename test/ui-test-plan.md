# UI test plan

Run `javac --release 25 -d build/classes src/main/java/*.java` before this plan.
Each test starts a new copy of the program.

## Test 1: Exit the application

**Aim:** Verify that the program accepts the `bye` command and prints a farewell.

**Run:** `java -cp build/classes Mona`

**Input:**
```text
bye
```

**Expected output:**
```text
____________________________________________________________
 __  __  ___  _   _    _ 
|  \/  |/ _ \| \ | |  / \
| |\/| | | | |  \| | / _ \
| |  | | |_| | |\  |/ ___ \
|_|  |_|\___/|_| \_/_/   \_\

Hello! I'm Mona.
What can I do for you?
____________________________________________________________
Mona > ____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```

## Test 2: Track all task types

**Aim:** Verify that todos, deadlines, and events are added, marked, and listed with their type details.

**Run:** `java -cp build/classes Mona`

**Input:**
```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
mark 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
 __  __  ___  _   _    _ 
|  \/  |/ _ \| \ | |  / \
| |\/| | | | |  \| | / _ \
| |  | | |_| | |\  |/ ___ \
|_|  |_|\___/|_| \_/_/   \_\

Hello! I'm Mona.
What can I do for you?
____________________________________________________________
Mona > ____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Sunday)
____________________________________________________________
Mona > ____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][X] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
Mona > ____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```

## Test 3: Preserve informal date text

**Aim:** Verify that a deadline date is stored and displayed as entered instead of being parsed.

**Run:** `java -cp build/classes Mona`

**Input:**
```text
deadline do homework /by no idea :-p
list
bye
```

**Expected output:**
```text
____________________________________________________________
 __  __  ___  _   _    _ 
|  \/  |/ _ \| \ | |  / \
| |\/| | | | |  \| | / _ \
| |  | | |_| | |\  |/ ___ \
|_|  |_|\___/|_| \_/_/   \_\

Hello! I'm Mona.
What can I do for you?
____________________________________________________________
Mona > ____________________________________________________________
Got it. I've added this task:
  [D][ ] do homework (by: no idea :-p)
Now you have 1 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
Here are the tasks in your list:
1.[D][ ] do homework (by: no idea :-p)
____________________________________________________________
Mona > ____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```

## Test 4: Reject invalid task input

**Aim:** Verify that invalid task numbers and missing task separators are rejected without adding tasks.

**Run:** `java -cp build/classes Mona`

**Input:**
```text
mark one
mark 1
deadline return book
event meeting /from Monday
list
bye
```

**Expected output:**
```text
____________________________________________________________
 __  __  ___  _   _    _ 
|  \/  |/ _ \| \ | |  / \
| |\/| | | | |  \| | / _ \
| |  | | |_| | |\  |/ ___ \
|_|  |_|\___/|_| \_/_/   \_\

Hello! I'm Mona.
What can I do for you?
____________________________________________________________
Mona > ____________________________________________________________
Please enter a valid task number.
____________________________________________________________
Mona > ____________________________________________________________
Please enter a valid task number.
____________________________________________________________
Mona > ____________________________________________________________
Please specify the deadline using /by.
____________________________________________________________
Mona > ____________________________________________________________
Please specify the event times using /from and /to.
____________________________________________________________
Mona > ____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
Mona > ____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```
