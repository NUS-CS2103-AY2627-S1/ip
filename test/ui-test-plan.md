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
____________________________________________________________
Mona > ____________________________________________________________
❌ No such fate is written in the constellations. Please enter a valid task number.
____________________________________________________________
Mona > ____________________________________________________________
❌ Even the stars need a fixed point. Specify the deadline using /by.
____________________________________________________________
Mona > ____________________________________________________________
❌ Fate needs both a dawn and a dusk. Specify the event using /from and /to.
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
❌ That is not written in the stars I can read. Try a todo, deadline, or event.
____________________________________________________________
Mona > ____________________________________________________________
❌ A todo needs a name before its fate can be charted.
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
____________________________________________________________
Mona > ____________________________________________________________
❌ An event needs a name before its fate can be charted.
____________________________________________________________
Mona > ____________________________________________________________
❌ No such fate is written in the constellations. Please enter a valid task number.
____________________________________________________________
Mona > ____________________________________________________________
✨ Here is what the stars reveal:
____________________________________________________________
Mona > ____________________________________________________________
✨ Farewell. May the stars guide you until we meet again.
____________________________________________________________

```
