# Moon UI Test Plan

## Test environment

- Run the `Moon` main class with Java 25.
- Compare only text printed by the program. The terminal's echo of typed commands is not part of expected output.
- The test runner stops after the first failing test case.

## Test case: Create and list every task type

**Aim:** Verify that to-dos, deadlines, and events are stored and displayed with their type, details, and incomplete status.

### Input
```text
todo borrow book
deadline return book /by 2019-12-02
event project meeting /from 2019-12-03 /to 2019-12-04
list
bye
```

### Expected output
```text
____________________________________________________________
Hello! I'm Moon, your personal chatbot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 03 2019 to: Dec 04 2019)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Dec 02 2019)
 3.[E][ ] project meeting (from: Dec 03 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Find tasks by keyword

**Aim:** Verify that find displays only tasks whose descriptions contain the keyword, ignoring letter case.

### Input
```text
todo read book
deadline return book /by 2019-12-02
todo wash dishes
find BOOK
bye
```

### Expected output
```text
____________________________________________________________
Hello! I'm Moon, your personal chatbot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] wash dishes
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Mark and unmark a task

**Aim:** Verify that marking a task done and then unmarking it changes the displayed status correctly.

### Input
```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected output
```text
____________________________________________________________
Hello! I'm Moon, your personal chatbot.
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
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Save after every task-list change

**Aim:** Verify that successful add, mark, unmark, and delete commands complete normally; each command triggers saving of the changed list.

### Input
```text
todo read book
mark 1
unmark 1
delete 1
bye
```

### Expected output
```text
____________________________________________________________
Hello! I'm Moon, your personal chatbot.
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
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] read book
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject invalid commands without ending the session

**Aim:** Verify that Moon reports missing todo descriptions and unknown commands, then continues to accept valid commands.

### Input
```text
todo
blah
todo borrow book
list
bye
```

### Expected output
```text
____________________________________________________________
Hello! I'm Moon, your personal chatbot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Oof! your todo needs a description.
____________________________________________________________
____________________________________________________________
 Oof! I don't recognise that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Delete a task and renumber the list

**Aim:** Verify that deleting a task removes the correct task and shifts later tasks to their new list numbers.

### Input
```text
todo read book
deadline return book /by 2019-12-02
event project meeting /from 2019-12-03 /to 2019-12-04
delete 2
list
bye
```

### Expected output
```text
____________________________________________________________
Hello! I'm Moon, your personal chatbot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 03 2019 to: Dec 04 2019)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: Dec 02 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[E][ ] project meeting (from: Dec 03 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject malformed scheduled tasks without adding them

**Aim:** Verify that invalid deadline and event commands do not change the list before a later valid deadline is added.

### Input
```text
todo read book
deadline submit report
event team meeting /from Mon 2pm
deadline submit report /by 2019-12-06
list
bye
```

### Expected output
```text
____________________________________________________________
Hello! I'm Moon, your personal chatbot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Oof! a deadline needs /by followed by its due date or time.
____________________________________________________________
____________________________________________________________
 Oof! an event needs /from and /to times.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit report (by: Dec 06 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] submit report (by: Dec 06 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject invalid task numbers and keep the right tasks

**Aim:** Verify that failed mark and delete commands leave task state unchanged before later valid commands use the correct task numbers.

### Input
```text
todo read book
todo return book
mark 3
delete 0
mark 2
delete 1
list
bye
```

### Expected output
```text
____________________________________________________________
Hello! I'm Moon, your personal chatbot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] return book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Oof! task 3 is not in your list yet.
____________________________________________________________
____________________________________________________________
 Oof! task 0 is not in your list yet.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] return book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] return book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
