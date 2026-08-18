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

✨ Hello, I'm Mona.
The constellations lie reflected in the water tonight. What fate shall we divine?
____________________________________________________________
Mona > ____________________________________________________________
✨ Farewell. May the stars guide you until we meet again.
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

✨ Hello, I'm Mona.
The constellations lie reflected in the water tonight. What fate shall we divine?
____________________________________________________________
Mona > ____________________________________________________________
✅ Your fate is rewritten. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
✅ Your fate is rewritten. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
✅ Your fate is rewritten. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
✅ The stars align. I've marked this task as done:
  [D][X] return book (by: Sunday)
____________________________________________________________
Mona > ____________________________________________________________
✨ Here is what the stars reveal:
1.[T][ ] borrow book
2.[D][X] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
Mona > ____________________________________________________________
✨ Farewell. May the stars guide you until we meet again.
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

✨ Hello, I'm Mona.
The constellations lie reflected in the water tonight. What fate shall we divine?
____________________________________________________________
Mona > ____________________________________________________________
✅ Your fate is rewritten. I've added this task:
  [D][ ] do homework (by: no idea :-p)
Now you have 1 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
✨ Here is what the stars reveal:
1.[D][ ] do homework (by: no idea :-p)
____________________________________________________________
Mona > ____________________________________________________________
✨ Farewell. May the stars guide you until we meet again.
____________________________________________________________

```

## Test 4: Reject invalid task numbers and missing separators

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

✨ Hello, I'm Mona.
The constellations lie reflected in the water tonight. What fate shall we divine?
____________________________________________________________
Mona > ____________________________________________________________
❌ No such fate is written in the constellations. Please enter a valid task number.
Hint: mark 2
____________________________________________________________
Mona > ____________________________________________________________
❌ There are no tasks yet whose fate can be altered.
Hint: todo read book
____________________________________________________________
Mona > ____________________________________________________________
❌ Even the stars need a fixed point. Specify the deadline using /by.
Hint: deadline return book /by Sunday
____________________________________________________________
Mona > ____________________________________________________________
❌ Fate needs both a dawn and a dusk. Specify the event using /from and /to.
Hint: event project meeting /from Mon 2pm /to Mon 4pm
____________________________________________________________
Mona > ____________________________________________________________
✨ Here is what the stars reveal:
____________________________________________________________
Mona > ____________________________________________________________
✨ Farewell. May the stars guide you until we meet again.
____________________________________________________________

```

## Test 5: Reject unknown commands and empty descriptions

**Aim:** Verify that an unrecognized command and an empty todo description are both rejected
with a specific error, and that no task is added in either case.

**Run:** `java -cp build/classes Mona`

**Input:**
```text
blah
todo
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

✨ Hello, I'm Mona.
The constellations lie reflected in the water tonight. What fate shall we divine?
____________________________________________________________
Mona > ____________________________________________________________
❌ That command is not written in the stars I can read. Try a todo, deadline, or event.
Hint: list | todo <description> | deadline <description> /by <when> | event <description> /from <start> /to <end> | mark <number> | unmark <number> | delete <number> | bye
____________________________________________________________
Mona > ____________________________________________________________
❌ A todo needs a name before its fate can be charted.
Hint: todo read book
____________________________________________________________
Mona > ____________________________________________________________
✨ Here is what the stars reveal:
____________________________________________________________
Mona > ____________________________________________________________
✨ Farewell. May the stars guide you until we meet again.
____________________________________________________________

```

## Test 6: Reject empty deadline and event descriptions

**Aim:** Verify that a deadline or event with a blank description (but a valid /by, /from,
and /to) is rejected, and that a missing task number for mark/unmark is rejected too.

**Run:** `java -cp build/classes Mona`

**Input:**
```text
deadline  /by Sunday
event  /from Mon /to 4pm
mark
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

✨ Hello, I'm Mona.
The constellations lie reflected in the water tonight. What fate shall we divine?
____________________________________________________________
Mona > ____________________________________________________________
❌ A deadline needs a name before its fate can be charted.
Hint: deadline return book /by Sunday
____________________________________________________________
Mona > ____________________________________________________________
❌ An event needs a name before its fate can be charted.
Hint: event project meeting /from Mon 2pm /to Mon 4pm
____________________________________________________________
Mona > ____________________________________________________________
❌ Tell me which task's fate to alter.
Hint: mark 2
____________________________________________________________
Mona > ____________________________________________________________
✨ Here is what the stars reveal:
____________________________________________________________
Mona > ____________________________________________________________
✨ Farewell. May the stars guide you until we meet again.
____________________________________________________________

```

## Test 7: Delete a task and renumber the list

**Aim:** Verify that deleting a task removes the selected task, reports the new task count,
and closes the numbering gap in the list.

**Run:** `java -cp build/classes Mona`

**Input:**
```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
delete 3
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

✨ Hello, I'm Mona.
The constellations lie reflected in the water tonight. What fate shall we divine?
____________________________________________________________
Mona > ____________________________________________________________
✅ Your fate is rewritten. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
✅ Your fate is rewritten. I've added this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
✅ Your fate is rewritten. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
✅ Your fate is rewritten. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
✅ Your fate is rewritten. I've added this task:
  [T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
✅ The stars align. I've marked this task as done:
  [T][X] read book
____________________________________________________________
Mona > ____________________________________________________________
✅ The stars align. I've marked this task as done:
  [D][X] return book (by: June 6th)
____________________________________________________________
Mona > ____________________________________________________________
✅ The stars align. I've marked this task as done:
  [T][X] join sports club
____________________________________________________________
Mona > ____________________________________________________________
✅ A fate fades from the constellations. I've removed this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 4 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
✨ Here is what the stars reveal:
1.[T][X] read book
2.[D][X] return book (by: June 6th)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
Mona > ____________________________________________________________
✨ Farewell. May the stars guide you until we meet again.
____________________________________________________________

```

## Test 8: Reject invalid delete commands

**Aim:** Verify that delete reports the themed, actionable error for a missing number,
a nonnumeric number, an empty list, and an out-of-range number without removing a task.

**Run:** `java -cp build/classes Mona`

**Input:**
```text
delete
delete one
delete 1
todo read book
delete 0
delete 2
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

✨ Hello, I'm Mona.
The constellations lie reflected in the water tonight. What fate shall we divine?
____________________________________________________________
Mona > ____________________________________________________________
❌ Tell me which task should be deleted.
Hint: delete 2
____________________________________________________________
Mona > ____________________________________________________________
❌ No such fate is written in the constellations. Please enter a valid task number.
Hint: delete 2
____________________________________________________________
Mona > ____________________________________________________________
❌ The constellations remain still. There are no tasks to be deleted.
Hint: todo read book
____________________________________________________________
Mona > ____________________________________________________________
✅ Your fate is rewritten. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
Mona > ____________________________________________________________
❌ No such fate is written in the constellations. Please enter a valid task number.
Hint: list
____________________________________________________________
Mona > ____________________________________________________________
❌ No such fate is written in the constellations. Please enter a valid task number.
Hint: list
____________________________________________________________
Mona > ____________________________________________________________
✨ Here is what the stars reveal:
1.[T][ ] read book
____________________________________________________________
Mona > ____________________________________________________________
✨ Farewell. May the stars guide you until we meet again.
____________________________________________________________

```
