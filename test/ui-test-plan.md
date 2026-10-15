# TBlade UI test plan

The `test-ui` skill reads the test cases below. Keep the expected output complete: it is compared character-for-character with the program output.

## Test Case: Add and list all task types
### Aim
Verify that todo, deadline, and event commands create the correct task subtype and that list displays each type and its date/time text.
### Inputs
```text
todo borrow book
deadline return book /by 2026-03-15
event project meeting /from Mon 2pm /to 4pm
list
bye
```
### Expected output
```text
____________________________________________________________ 🐷
#####  ####   #       ###   ####   ##### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #      #####  #   #  #### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #####  #   #  ####   ##### 🐷
Hello! I'm TBlade. 🐷
What can I do for you? 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [T][ ] borrow book 🐷
Now you have 1 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [D][ ] return book (by: Mar 15 2026) 🐷
Now you have 2 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [E][ ] project meeting (from: Mon 2pm to: 4pm) 🐷
Now you have 3 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Here are the tasks in your list: 🐷
1.[T][ ] borrow book 🐷
2.[D][ ] return book (by: Mar 15 2026) 🐷
3.[E][ ] project meeting (from: Mon 2pm to: 4pm) 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Bye. Hope to see you again soon! 🐷
____________________________________________________________ 🐷
```

## Test Case: Preserve task list through rejected mixed commands
### Aim
Verify that rejected unknown, incomplete deadline, incomplete event, and invalid task-number commands do not add tasks or alter the status of valid tasks entered between them.
### Inputs
```text
todo first task
blah
deadline /by Friday
deadline submit report /by 2026-03-20
event workshop /from Monday
event workshop /from Monday /to Tuesday
mark 99
mark 2
unmark 5
list
bye
```
### Expected output
```text
____________________________________________________________ 🐷
#####  ####   #       ###   ####   ##### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #      #####  #   #  #### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #####  #   #  ####   ##### 🐷
Hello! I'm TBlade. 🐷
What can I do for you? 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [T][ ] first task 🐷
Now you have 1 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! I don't know that command. Use: todo, deadline, event, list, find, mark, unmark, delete, or bye. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! The description of a deadline cannot be empty. Use: deadline DESCRIPTION /by DATE 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [D][ ] submit report (by: Mar 20 2026) 🐷
Now you have 2 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! An event needs a /to end time. Use: event DESCRIPTION /from START /to END 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [E][ ] workshop (from: Monday to: Tuesday) 🐷
Now you have 3 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! Task number must be between 1 and 3. Use: mark TASK_NUMBER 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Nice! I've marked this task as done: 🐷
  [X] submit report 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! Task number must be between 1 and 3. Use: unmark TASK_NUMBER 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Here are the tasks in your list: 🐷
1.[T][ ] first task 🐷
2.[D][X] submit report (by: Mar 20 2026) 🐷
3.[E][ ] workshop (from: Monday to: Tuesday) 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Bye. Hope to see you again soon! 🐷
____________________________________________________________ 🐷
```

## Test Case: Preserve completion status after invalid unmark
### Aim
Verify that an invalid unmark command does not reverse a completed task, while a later valid unmark does.
### Inputs
```text
todo keep done
mark 1
unmark one
list
unmark 1
list
bye
```
### Expected output
```text
____________________________________________________________ 🐷
#####  ####   #       ###   ####   ##### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #      #####  #   #  #### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #####  #   #  ####   ##### 🐷
Hello! I'm TBlade. 🐷
What can I do for you? 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [T][ ] keep done 🐷
Now you have 1 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Nice! I've marked this task as done: 🐷
  [X] keep done 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! Task number must be a whole number. Use: unmark TASK_NUMBER 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Here are the tasks in your list: 🐷
1.[T][X] keep done 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OK, I've marked this task as not done yet: 🐷
  [ ] keep done 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Here are the tasks in your list: 🐷
1.[T][ ] keep done 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Bye. Hope to see you again soon! 🐷
____________________________________________________________ 🐷
```

## Test Case: Mark and unmark a task
### Aim
Verify that marking a task done and reversing that status changes the list status icon.
### Inputs
```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected output
```text
____________________________________________________________ 🐷
#####  ####   #       ###   ####   ##### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #      #####  #   #  #### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #####  #   #  ####   ##### 🐷
Hello! I'm TBlade. 🐷
What can I do for you? 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [T][ ] read book 🐷
Now you have 1 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Nice! I've marked this task as done: 🐷
  [X] read book 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OK, I've marked this task as not done yet: 🐷
  [ ] read book 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Here are the tasks in your list: 🐷
1.[T][ ] read book 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Bye. Hope to see you again soon! 🐷
____________________________________________________________ 🐷
```

## Test Case: Delete a task and renumber the list
### Aim
Verify that deleting a completed task removes it, preserves the other tasks, and renumbers the remaining list entries.
### Inputs
```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from Aug 6th 2pm /to 4pm
mark 1
mark 2
delete 2
list
bye
```
### Expected output
```text
____________________________________________________________ 🐷
#####  ####   #       ###   ####   ##### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #      #####  #   #  #### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #####  #   #  ####   ##### 🐷
Hello! I'm TBlade. 🐷
What can I do for you? 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [T][ ] read book 🐷
Now you have 1 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [D][ ] return book (by: Jun 06 2026) 🐷
Now you have 2 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm) 🐷
Now you have 3 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Nice! I've marked this task as done: 🐷
  [X] read book 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Nice! I've marked this task as done: 🐷
  [X] return book 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Noted. I've removed this task: 🐷
  [D][X] return book (by: Jun 06 2026) 🐷
Now you have 2 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Here are the tasks in your list: 🐷
1.[T][X] read book 🐷
2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm) 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Bye. Hope to see you again soon! 🐷
____________________________________________________________ 🐷
```

## Test Case: Explain all current command-format errors
### Aim
Verify that every current command reports an actionable error for missing or extra input, and that all rejected commands leave the list unchanged before a later valid task is added.
### Inputs
```text

list extra
bye now
mark
unmark 1
delete
deadline report
deadline report /by
event
event planning
event planning /from
event planning /from /to Friday
event planning /from Monday /to
todo valid task
list
bye
```
### Expected output
```text
____________________________________________________________ 🐷
#####  ####   #       ###   ####   ##### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #      #####  #   #  #### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #####  #   #  ####   ##### 🐷
Hello! I'm TBlade. 🐷
What can I do for you? 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! Please enter a command. Use: todo, deadline, event, list, find, mark, unmark, delete, or bye. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! The list command does not take extra text. Use: list 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! The bye command does not take extra text. Use: bye 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! Please provide a task number. Use: mark TASK_NUMBER 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! There are no tasks to unmark. Add a task first. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! Please provide a task number. Use: delete TASK_NUMBER 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! A deadline needs a /by date. Use: deadline DESCRIPTION /by DATE 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! The /by date cannot be empty. Use: deadline DESCRIPTION /by DATE 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! The description of an event cannot be empty. Use: event DESCRIPTION /from START /to END 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! An event needs a /from start time. Use: event DESCRIPTION /from START /to END 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! The /from start time cannot be empty. Use: event DESCRIPTION /from START /to END 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! An event needs a /to end time. Use: event DESCRIPTION /from START /to END 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! The /to end time cannot be empty. Use: event DESCRIPTION /from START /to END 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [T][ ] valid task 🐷
Now you have 1 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Here are the tasks in your list: 🐷
1.[T][ ] valid task 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Bye. Hope to see you again soon! 🐷
____________________________________________________________ 🐷
```

## Test Case: Reject invalid commands and missing todo descriptions
### Aim
Verify that invalid input is reported through the user-facing exception message and that the program continues running afterwards.
### Inputs
```text
todo
blah
bye
```
### Expected output
```text
____________________________________________________________ 🐷
#####  ####   #       ###   ####   ##### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #      #####  #   #  #### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #####  #   #  ####   ##### 🐷
Hello! I'm TBlade. 🐷
What can I do for you? 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! The description of a todo cannot be empty. Use: todo DESCRIPTION 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! I don't know that command. Use: todo, deadline, event, list, find, mark, unmark, delete, or bye. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Bye. Hope to see you again soon! 🐷
____________________________________________________________ 🐷
```

## Test Case: Find tasks by keyword
### Aim
Verify that find lists only the tasks whose description contains the keyword, that a non-matching keyword returns an empty match list, and that an empty keyword is rejected.
### Inputs
```text
todo read book
deadline return book /by 2026-06-06
event borrow laptop /from Mon /to Tue
mark 1
mark 2
find book
find laptop
find zzz
find
bye
```
### Expected output
```text
____________________________________________________________ 🐷
#####  ####   #       ###   ####   ##### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #      #####  #   #  #### 🐷
  #    #   #  #      #   #  #   #  # 🐷
  #    ####   #####  #   #  ####   ##### 🐷
Hello! I'm TBlade. 🐷
What can I do for you? 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [T][ ] read book 🐷
Now you have 1 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [D][ ] return book (by: Jun 06 2026) 🐷
Now you have 2 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Got it. I've added this task: 🐷
  [E][ ] borrow laptop (from: Mon to: Tue) 🐷
Now you have 3 tasks in the list. 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Nice! I've marked this task as done: 🐷
  [X] read book 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Nice! I've marked this task as done: 🐷
  [X] return book 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Here are the matching tasks in your list: 🐷
1.[T][X] read book 🐷
2.[D][X] return book (by: Jun 06 2026) 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Here are the matching tasks in your list: 🐷
1.[E][ ] borrow laptop (from: Mon to: Tue) 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Here are the matching tasks in your list: 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
OOPS!!! The search keyword cannot be empty. Use: find KEYWORD 🐷
____________________________________________________________ 🐷
____________________________________________________________ 🐷
Bye. Hope to see you again soon! 🐷
____________________________________________________________ 🐷
```
