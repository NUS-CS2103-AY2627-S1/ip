# Emma UI test plan

Manual-style end-to-end tests for Emma's console interface, run by the `test-ui`
skill. Each case starts a fresh chatbot session, feeds it a list of commands, and
compares what Emma prints against the expected output.

## Conventions

An **Input** block holds one command per line, exactly as a user would type it.
Every case ends with `bye`.

An **Expected output** block holds *only Emma's responses*, in order. The runner
strips the following before comparing, so leave them out:

- the startup banner and the greeting
- the coloured `Emma` and `user` speaker labels
- blank lines between blocks
- trailing whitespace on any line

Indentation *within* a response is significant — the two-space indent before a
task in an "added"/"marked" confirmation is part of the expected output.

Each case runs in its own empty working directory, so it starts with no saved
tasks and never touches the repository's real `data/emma.json`. A case that
needs existing tasks must add them itself.

A line reading exactly `--- restart ---` inside an Input block ends the chatbot
session and starts a new one in the same directory. Use it to check that tasks
saved by one run are loaded by the next.

---

## TC1: Greeting and exit

**Aim:** Emma exits cleanly on `bye` and shows the farewell message.

**Input**

```
bye
```

**Expected output**

```
Bye for now! Hope to see you again soon.
```

---

## TC2: Listing with nothing tracked

**Aim:** `list` on an empty task list explains there is nothing to show rather
than printing an empty response.

**Input**

```
list
bye
```

**Expected output**

```
You haven't given me anything to track yet!
Bye for now! Hope to see you again soon.
```

---

## TC3: Adding each type of task

**Aim:** `todo`, `deadline` and `event` each create a task and confirm it with
the correct type icon, and dates typed as `yyyy-mm-dd` are echoed back with the
month in words.

**Input**

```
todo read book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-15 /to 2019-10-16
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
Got it, I've added this:
  [D][ ] return book (by: Oct 15 2019)
Got it, I've added this:
  [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Bye for now! Hope to see you again soon.
```

---

## TC4: Listing tasks in order

**Aim:** `list` numbers tasks from 1 in the order they were added, showing each
type's own rendering.

**Input**

```
todo read book
deadline return book /by 2019-10-15
list
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
Got it, I've added this:
  [D][ ] return book (by: Oct 15 2019)
Here's your tasks:
1. [T][ ] read book
2. [D][ ] return book (by: Oct 15 2019)
Bye for now! Hope to see you again soon.
```

---

## TC5: Marking and unmarking

**Aim:** `mark` and `unmark` change the status of the numbered task only, and
the change persists into the next `list`.

**Input**

```
todo read book
todo return book
mark 2
list
unmark 2
list
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
Got it, I've added this:
  [T][ ] return book
Nice! I've marked this as done:
  [T][x] return book
Here's your tasks:
1. [T][ ] read book
2. [T][x] return book
Okay, I've marked this as not done yet:
  [T][ ] return book
Here's your tasks:
1. [T][ ] read book
2. [T][ ] return book
Bye for now! Hope to see you again soon.
```

---

## TC6: Task numbers that do not exist

**Aim:** `mark` rejects a non-numeric argument and an out-of-range number
without crashing the session.

**Input**

```
todo read book
mark abc
mark 9
mark 0
list
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
I need a task number, like "mark 1".
You don't have a task numbered 9.
You don't have a task numbered 0.
Here's your tasks:
1. [T][ ] read book
Bye for now! Hope to see you again soon.
```

---

## TC7: Incomplete task commands

**Aim:** each task command explains its own syntax when a required part is
missing, and adds nothing to the list.

**Input**

```
todo
deadline return book
event project meeting /from 2019-10-15
list
bye
```

**Expected output**

```
A todo needs a description, like "todo read book".
A deadline needs a description and a date, like "deadline return book /by 2019-10-15".
An event needs a description, a start date and an end date, like "event project meeting /from 2019-10-15 /to 2019-10-16".
You haven't given me anything to track yet!
Bye for now! Hope to see you again soon.
```

---

## TC8: Deleting a task

**Aim:** `delete` reports the task it removed, the task is gone from the next
`list`, and the tasks after it are renumbered.

**Input**

```
todo read book
todo return book
todo join club
delete 2
list
delete 5
delete abc
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
Got it, I've added this:
  [T][ ] return book
Got it, I've added this:
  [T][ ] join club
Okay, I've removed this:
  [T][ ] return book
Here's your tasks:
1. [T][ ] read book
2. [T][ ] join club
You don't have a task numbered 5.
I need a task number, like "delete 1".
Bye for now! Hope to see you again soon.
```

---

## TC9: Tasks survive a restart

**Aim:** tasks, their done status and their dates are saved to disk and reloaded
when Emma is started again; changes made after the restart are saved too.

**Input**

```
todo read book
deadline return book /by 2019-10-15
event meeting /from 2019-10-15 /to 2019-10-16
mark 1
--- restart ---
list
delete 2
--- restart ---
list
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
Got it, I've added this:
  [D][ ] return book (by: Oct 15 2019)
Got it, I've added this:
  [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
Nice! I've marked this as done:
  [T][x] read book
Here's your tasks:
1. [T][x] read book
2. [D][ ] return book (by: Oct 15 2019)
3. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
Okay, I've removed this:
  [D][ ] return book (by: Oct 15 2019)
Here's your tasks:
1. [T][x] read book
2. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
Bye for now! Hope to see you again soon.
```

---

## TC10: Unrecognised command

**Aim:** unknown input is reported as unknown rather than silently stored as a
task, and the session carries on afterwards.

**Input**

```
blah blah
deadlin return book /by Sunday
list
bye
```

**Expected output**

```
Sorry, I don't know what that means!
Sorry, I don't know what that means!
You haven't given me anything to track yet!
Bye for now! Hope to see you again soon.
```

---

## TC11: Dates Emma cannot understand

**Aim:** `deadline` and `event` reject anything that is not a real date written
as `yyyy-mm-dd` — including a well-formed date whose day does not exist — naming
the part of the command at fault and adding nothing to the list.

**Input**

```
deadline return book /by Sunday
deadline return book /by 2019-02-30
event meeting /from 2019-13-01 /to 2019-10-16
event meeting /from 2019-10-15 /to next week
list
bye
```

**Expected output**

```
I need a due date as a date like 2019-10-15, but I got "Sunday".
I need a due date as a date like 2019-10-15, but I got "2019-02-30".
I need a start date as a date like 2019-10-15, but I got "2019-13-01".
I need an end date as a date like 2019-10-15, but I got "next week".
You haven't given me anything to track yet!
Bye for now! Hope to see you again soon.
```

---

## TC12: Events that end before they start

**Aim:** `event` rejects an end date earlier than its start date and adds
nothing, but still accepts an event that starts and ends on the same day.

**Input**

```
event meeting /from 2019-10-16 /to 2019-10-15
event meeting /from 2019-10-15 /to 2019-10-15
list
bye
```

**Expected output**

```
An event has to end on or after it starts, but Oct 15 2019 is before Oct 16 2019.
Got it, I've added this:
  [E][ ] meeting (from: Oct 15 2019 to: Oct 15 2019)
Here's your tasks:
1. [E][ ] meeting (from: Oct 15 2019 to: Oct 15 2019)
Bye for now! Hope to see you again soon.
```

---

## TC13: Filtering by task type

**Aim:** `filter /type` shows only the tasks of that type, and keeps each task's
number from the full list rather than renumbering from 1.

**Input**

```
todo read book
deadline return book /by 2019-10-15
event meeting /from 2019-10-15 /to 2019-10-16
filter /type todo
filter /type deadline
filter /type event
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
Got it, I've added this:
  [D][ ] return book (by: Oct 15 2019)
Got it, I've added this:
  [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
Here's what matches:
1. [T][ ] read book
Here's what matches:
2. [D][ ] return book (by: Oct 15 2019)
Here's what matches:
3. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
Bye for now! Hope to see you again soon.
```

---

## TC14: Filtering deadlines by a cutoff date

**Aim:** `filter /type deadline /due-by` keeps deadlines due on or before the
date, includes one falling exactly on it, and says so when none match.

**Input**

```
deadline early /by 2019-10-10
deadline exact /by 2019-10-15
deadline late /by 2019-10-20
todo read book
filter /type deadline /due-by 2019-10-15
filter /type deadline /due-by 2019-01-01
bye
```

**Expected output**

```
Got it, I've added this:
  [D][ ] early (by: Oct 10 2019)
Got it, I've added this:
  [D][ ] exact (by: Oct 15 2019)
Got it, I've added this:
  [D][ ] late (by: Oct 20 2019)
Got it, I've added this:
  [T][ ] read book
Here's what matches:
1. [D][ ] early (by: Oct 10 2019)
2. [D][ ] exact (by: Oct 15 2019)
Nothing matches that filter.
Bye for now! Hope to see you again soon.
```

---

## TC15: Filtering events by a date

**Aim:** `filter /type event /at` keeps events running on that date, counting a
date in the middle of a multi-day event and one on its first or last day.

**Input**

```
event conference /from 2019-10-15 /to 2019-10-18
event standup /from 2019-10-20 /to 2019-10-20
filter /type event /at 2019-10-17
filter /type event /at 2019-10-18
filter /type event /at 2019-10-20
filter /type event /at 2019-10-19
bye
```

**Expected output**

```
Got it, I've added this:
  [E][ ] conference (from: Oct 15 2019 to: Oct 18 2019)
Got it, I've added this:
  [E][ ] standup (from: Oct 20 2019 to: Oct 20 2019)
Here's what matches:
1. [E][ ] conference (from: Oct 15 2019 to: Oct 18 2019)
Here's what matches:
1. [E][ ] conference (from: Oct 15 2019 to: Oct 18 2019)
Here's what matches:
2. [E][ ] standup (from: Oct 20 2019 to: Oct 20 2019)
Nothing matches that filter.
Bye for now! Hope to see you again soon.
```

---

## TC16: Filters Emma cannot carry out

**Aim:** `filter` rejects a missing or unknown type, an option used with the
wrong type, an unknown option, and an option not followed by a real date.

**Input**

```
filter
filter /type task
filter /type todo /due-by 2019-10-15
filter /type deadline /at 2019-10-15
filter /type event /on 2019-10-15
filter /type deadline /due-by Sunday
bye
```

**Expected output**

```
A filter needs a type, like "filter /type deadline".
I can only filter by todo, deadline or event.
Only a deadline filter takes "/due-by".
Only an event filter takes "/at".
I don't know what "/on" means in a filter.
I need a cutoff date as a date like 2019-10-15, but I got "Sunday".
Bye for now! Hope to see you again soon.
```

---

## TC17: Filters typed with extra spaces

**Aim:** `filter` tolerates extra spaces around the type and the optional flag,
the way the other commands already do.

**Input**

```
todo read book
deadline return book /by 2019-10-15
filter  /type todo
filter /type deadline  /due-by  2019-10-15
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
Got it, I've added this:
  [D][ ] return book (by: Oct 15 2019)
Here's what matches:
1. [T][ ] read book
Here's what matches:
2. [D][ ] return book (by: Oct 15 2019)
Bye for now! Hope to see you again soon.
```

---

## TC18: Finding tasks by their description

**Aim:** `find` shows every task whose description contains the text, of any type,
keeping their numbers from the full list; it ignores the dates and type icons, and
says so when nothing matches or when no text was given.

**Input**

```
todo read book
deadline return book /by 2019-10-15
event book club /from 2019-10-15 /to 2019-10-16
todo buy milk
find book
find milk
find Oct
find homework
find
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
Got it, I've added this:
  [D][ ] return book (by: Oct 15 2019)
Got it, I've added this:
  [E][ ] book club (from: Oct 15 2019 to: Oct 16 2019)
Got it, I've added this:
  [T][ ] buy milk
Here's what I found:
1. [T][ ] read book
2. [D][ ] return book (by: Oct 15 2019)
3. [E][ ] book club (from: Oct 15 2019 to: Oct 16 2019)
Here's what I found:
4. [T][ ] buy milk
Nothing has "Oct" in its description.
Nothing has "homework" in its description.
A find needs something to look for, like "find book".
Bye for now! Hope to see you again soon.
```

---

## TC19: Adding a task that is already tracked

**Aim:** Emma refuses a task the list already holds, whatever its type and whether
or not it is done, but still accepts one that differs in type or in any of its
dates. A refused task is never added, so the numbering stays unbroken.

**Input**

```
todo read book
todo read book
mark 1
todo read book
deadline read book /by 2019-10-15
deadline read book /by 2019-10-15
deadline read book /by 2019-10-16
event meeting /from 2019-10-15 /to 2019-10-16
event meeting /from 2019-10-15 /to 2019-10-17
event meeting /from 2019-10-15 /to 2019-10-16
list
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
You're already tracking this:
  [T][ ] read book
Nice! I've marked this as done:
  [T][x] read book
You're already tracking this:
  [T][ ] read book
Got it, I've added this:
  [D][ ] read book (by: Oct 15 2019)
You're already tracking this:
  [D][ ] read book (by: Oct 15 2019)
Got it, I've added this:
  [D][ ] read book (by: Oct 16 2019)
Got it, I've added this:
  [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
Got it, I've added this:
  [E][ ] meeting (from: Oct 15 2019 to: Oct 17 2019)
You're already tracking this:
  [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
Here's your tasks:
1. [T][x] read book
2. [D][ ] read book (by: Oct 15 2019)
3. [D][ ] read book (by: Oct 16 2019)
4. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
5. [E][ ] meeting (from: Oct 15 2019 to: Oct 17 2019)
Bye for now! Hope to see you again soon.
```
