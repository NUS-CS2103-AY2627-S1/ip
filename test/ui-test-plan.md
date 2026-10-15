# Alexa console UI test plan

## How to run this plan

Compile the Java sources with Java 25, then run `alexa.Alexa`. Each Input block below is sent as standard input to a fresh program session. Compare the captured standard output exactly with the corresponding Expected output block, ignoring only line-ending style.

## Test case: Greeting and farewell

**Aim:** Verify that Alexa starts with its greeting and ends cleanly after `bye`.

**Input:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
                 A L E X A
Hello! I'm Alexa.
What can I do for you?
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Add and list all task types

**Aim:** Verify that `Todo`, `Deadline`, and `Event` are stored as `Task` objects and displayed with their type-specific details.

**Input:**

```text
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-16 /to 2019-10-17
list
bye
```

**Expected output:**

```text
____________________________________________________________
                 A L E X A
Hello! I'm Alexa.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Oct 16 2019 to: Oct 17 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 16 2019 to: Oct 17 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Delete a task and renumber the list

**Aim:** Verify that `delete 3` removes the third task and that later tasks shift up in the list.

**Input:**

```text
todo first
todo second
todo third
todo fourth
delete 3
list
bye
```

**Expected output:**

```text
____________________________________________________________
                 A L E X A
Hello! I'm Alexa.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] first
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] second
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] third
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] fourth
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][ ] third
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first
2.[T][ ] second
3.[T][ ] fourth
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject an invalid deletion number

**Aim:** Verify that Alexa reports a clear error for a deletion outside the current list.

**Input:**

```text
todo read book
delete 2
bye
```

**Expected output:**

```text
____________________________________________________________
                 A L E X A
Hello! I'm Alexa.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task 2. Use list to see the task numbers.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Save task changes

**Aim:** Verify that adding and marking a task retains the normal user interface while exercising automatic saving.

**Input:**

```text
todo read book
mark 1
unmark 1
bye
```

**Expected output:**

```text
____________________________________________________________
                 A L E X A
Hello! I'm Alexa.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Ok, I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject an invalid date

**Aim:** Verify that Alexa explains the required date format when a deadline date cannot be parsed.

**Input:**

```text
deadline return book /by tomorrow
bye
```

**Expected output:**

```text
____________________________________________________________
                 A L E X A
Hello! I'm Alexa.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! The deadline date must use yyyy-MM-dd, for example 2019-10-15.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
## Test case: Find matching tasks

**Aim:** Verify that `find` returns every task whose description contains the keyword, ignoring case.

**Input:**

```text
todo read book
deadline return book /by 2019-10-15
todo buy groceries
find BOOK
bye
```

**Expected output:**

```text
____________________________________________________________
                 A L E X A
Hello! I'm Alexa.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy groceries
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject a duplicate task

**Aim:** Verify that Alexa rejects an identical new task, identifies the existing task number, and leaves the list unchanged.

**Input:**

```text
todo read book
todo read book
list
bye
```

**Expected output:**

```text
____________________________________________________________
                 A L E X A
Hello! I'm Alexa.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! This task already exists in task 1.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject malformed task details

**Aim:** Verify that Alexa accepts harmless command whitespace while rejecting repeated markers, invalid event ranges, and unsafe task descriptions.

**Input:**

```text
  todo    read book
deadline return book /by 2026-10-15 /by 2026-10-16
event meeting /from 2026-10-16 /to 2026-10-15
todo read | book
bye
```

**Expected output:**

```text
____________________________________________________________
                 A L E X A
Hello! I'm Alexa.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline can contain /by only once.
____________________________________________________________
____________________________________________________________
OOPS!!! The event end date must be later than the start date.
____________________________________________________________
____________________________________________________________
OOPS!!! A task description cannot contain the | character.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
