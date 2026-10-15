## Test case 1: Delete a task and renumber the remaining list

**Inputs:**
```text
help
list
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
find book
delete 3
list
bye
```
**Actual output:**
```text
 _____ ____ ___ _____ _   _
| ____|  _ \_ _|_   _| | | |
|  _| | | | | |  | | | |_| |
| |___| |_| | |  | | |  _  |
|_____|____/___| |_| |_| |_|

Hello! I'm EDITH.
Type 'help' to list all commands.
____________________________________________________________
____________________________________________________________
Here are the commands you can use:
1. help
2. todo DESCRIPTION [/tags #TAG ...]
3. deadline DESCRIPTION /by yyyy-MM-dd [HHmm] [/tags #TAG ...]
4. event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm] [/tags #TAG ...]
5. list
6. find KEYWORD
7. mark NUMBER
8. unmark NUMBER
9. delete NUMBER
10. tag NUMBER #TAG [#TAG ...]
11. untag NUMBER #TAG [#TAG ...]
12. bye

Notes:
Replace UPPER_CASE words with your own text.
Square brackets mark optional input; omit the brackets.
NUMBER is the task's position in the full list, starting at 1.
____________________________________________________________
____________________________________________________________
No tasks yet! Use todo, deadline, or event to create a new tasks
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019 18:00)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 06 2019 16:00)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Dec 02 2019 18:00)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 02 2019 18:00)
3.[E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 06 2019 16:00)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 02 2019 18:00)
3.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 06 2019 16:00)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 02 2019 18:00)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
\n## Test case 2: Explain invalid command inputs

**Inputs:**
```text
todo
deadline report
deadline /by tomorrow
deadline report /by
deadline report /by tomorrow
event meeting /from 2pm /to 2019-08-06
event meeting /from 2019-08-06 /to 2pm
event meeting /from 2pm
event /from 2pm /to 3pm
event meeting /from /to 3pm
event meeting /from 2pm /to
mark
mark 1
delete
delete 1
find
blah
bye
```
**Actual output:**
```text
 _____ ____ ___ _____ _   _
| ____|  _ \_ _|_   _| | | |
|  _| | | | | |  | | | |_| |
| |___| |_| | |  | | |  _  |
|_____|____/___| |_| |_| |_|

Hello! I'm EDITH.
Type 'help' to list all commands.
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a description and due date. Use: deadline DESCRIPTION /by yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a deadline cannot be empty. Use: deadline DESCRIPTION /by yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a due date after /by. Use: deadline DESCRIPTION /by yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
OOPS!!! The deadline date must use yyyy-MM-dd, optionally followed by a time in the format HHmm.
____________________________________________________________
____________________________________________________________
OOPS!!! The event start date must use yyyy-MM-dd, optionally followed by a time in the format HHmm.
____________________________________________________________
____________________________________________________________
OOPS!!! The event end date must use yyyy-MM-dd, optionally followed by a time in the format HHmm.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a description, start date, and end date. Use: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
OOPS!!! The description of an event cannot be empty. Use: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a start date after /from. Use: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs an end date after /to. Use: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a whole-number task number. Use: mark NUMBER
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a whole-number task number. Use: delete NUMBER
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][X] read book
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a word or phrase to find. Use: find KEYWORD
____________________________________________________________
____________________________________________________________
OOPS!!! I don't know what that means. Type 'help' to list all commands.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
\n## Test case 3: Restore tasks in a new session

**Inputs:**
```text
list
bye
```
**Actual output:**
```text
 _____ ____ ___ _____ _   _
| ____|  _ \_ _|_   _| | | |
|  _| | | | | |  | | | |_| |
| |___| |_| | |  | | |  _  |
|_____|____/___| |_| |_| |_|

Hello! I'm EDITH.
Type 'help' to list all commands.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][X] return book (by: Dec 02 2019 18:00)
2.[T][X] join sports club
3.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
\n## Test case 4: Mark a restored task as incomplete

**Inputs:**
```text
unmark 1
list
bye
```
**Actual output:**
```text
 _____ ____ ___ _____ _   _
| ____|  _ \_ _|_   _| | | |
|  _| | | | | |  | | | |_| |
| |___| |_| | |  | | |  _  |
|_____|____/___| |_| |_| |_|

Hello! I'm EDITH.
Type 'help' to list all commands.
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [D][ ] return book (by: Dec 02 2019 18:00)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Dec 02 2019 18:00)
2.[T][X] join sports club
3.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
\n## Test case 5: Create tagged tasks of every type

**Inputs:**
```text
todo read notes /tags #fun #school
deadline submit work /by 2026-09-20 /tags #school
event team meeting /from 2026-09-20 /to 2026-09-21 /tags #team
list
bye
```
**Actual output:**
```text
 _____ ____ ___ _____ _   _
| ____|  _ \_ _|_   _| | | |
|  _| | | | | |  | | | |_| |
| |___| |_| | |  | | |  _  |
|_____|____/___| |_| |_| |_|

Hello! I'm EDITH.
Type 'help' to list all commands.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read notes [tags: #fun #school]
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit work (by: Sep 20 2026) [tags: #school]
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] team meeting (from: Sep 20 2026 to: Sep 21 2026) [tags: #team]
Now you have 6 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Dec 02 2019 18:00)
2.[T][X] join sports club
3.[T][ ] borrow book
4.[T][ ] read notes [tags: #fun #school]
5.[D][ ] submit work (by: Sep 20 2026) [tags: #school]
6.[E][ ] team meeting (from: Sep 20 2026 to: Sep 21 2026) [tags: #team]
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
\n## Test case 6: Restore tagged tasks and reject malformed creation

**Inputs:**
```text
todo learn tags /tags #bad!
list
bye
```
**Actual output:**
```text
 _____ ____ ___ _____ _   _
| ____|  _ \_ _|_   _| | | |
|  _| | | | | |  | | | |_| |
| |___| |_| | |  | | |  _  |
|_____|____/___| |_| |_| |_|

Hello! I'm EDITH.
Type 'help' to list all commands.
____________________________________________________________
____________________________________________________________
OOPS!!! Tags must start with # and contain only letters, digits, underscores, or hyphens.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Dec 02 2019 18:00)
2.[T][X] join sports club
3.[T][ ] borrow book
4.[T][ ] read notes [tags: #fun #school]
5.[D][ ] submit work (by: Sep 20 2026) [tags: #school]
6.[E][ ] team meeting (from: Sep 20 2026 to: Sep 21 2026) [tags: #team]
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
\n## Test case 7: Add and remove tags by full-list number

**Inputs:**
```text
find notes
tag 4 #Fun #travel
tag 4 #FUN
untag 4 #school #missing
untag 4 #missing
list
bye
```
**Actual output:**
```text
 _____ ____ ___ _____ _   _
| ____|  _ \_ _|_   _| | | |
|  _| | | | | |  | | | |_| |
| |___| |_| | |  | | |  _  |
|_____|____/___| |_| |_| |_|

Hello! I'm EDITH.
Type 'help' to list all commands.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read notes [tags: #fun #school]
____________________________________________________________
____________________________________________________________
Got it. I've added tags to this task:
  [T][ ] read notes [tags: #fun #school #travel]
____________________________________________________________
____________________________________________________________
No tags were added; this task already has them:
  [T][ ] read notes [tags: #fun #school #travel]
____________________________________________________________
____________________________________________________________
Noted. I've removed tags from this task:
  [T][ ] read notes [tags: #fun #travel]
____________________________________________________________
____________________________________________________________
No tags were removed; this task does not have them:
  [T][ ] read notes [tags: #fun #travel]
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Dec 02 2019 18:00)
2.[T][X] join sports club
3.[T][ ] borrow book
4.[T][ ] read notes [tags: #fun #travel]
5.[D][ ] submit work (by: Sep 20 2026) [tags: #school]
6.[E][ ] team meeting (from: Sep 20 2026 to: Sep 21 2026) [tags: #team]
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
\n## Test case 8: Reject invalid tag changes and restore the list

**Inputs:**
```text
tag 4 #new #bad!
untag 4
tag 9 #fun
list
bye
```
**Actual output:**
```text
 _____ ____ ___ _____ _   _
| ____|  _ \_ _|_   _| | | |
|  _| | | | | |  | | | |_| |
| |___| |_| | |  | | |  _  |
|_____|____/___| |_| |_| |_|

Hello! I'm EDITH.
Type 'help' to list all commands.
____________________________________________________________
____________________________________________________________
OOPS!!! Tags must start with # and contain only letters, digits, underscores, or hyphens.
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide at least one tag. Use: untag NUMBER #tag [#tag ...]
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a task number from 1 to 6.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Dec 02 2019 18:00)
2.[T][X] join sports club
3.[T][ ] borrow book
4.[T][ ] read notes [tags: #fun #travel]
5.[D][ ] submit work (by: Sep 20 2026) [tags: #school]
6.[E][ ] team meeting (from: Sep 20 2026 to: Sep 21 2026) [tags: #team]
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
\n## Test case 9: Find exact tags and text within descriptions or tags

**Inputs:**
```text
todo plan #fun party
todo plan holiday /tags #funny
find #fun
find fun
find #fu
find #fun party
bye
```
**Actual output:**
```text
 _____ ____ ___ _____ _   _
| ____|  _ \_ _|_   _| | | |
|  _| | | | | |  | | | |_| |
| |___| |_| | |  | | |  _  |
|_____|____/___| |_| |_| |_|

Hello! I'm EDITH.
Type 'help' to list all commands.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] plan #fun party
Now you have 7 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] plan holiday [tags: #funny]
Now you have 8 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read notes [tags: #fun #travel]
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read notes [tags: #fun #travel]
2.[T][ ] plan #fun party
3.[T][ ] plan holiday [tags: #funny]
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] plan #fun party
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
\n## Test case 10: Reject invalid commands and duplicate tasks

**Inputs:**
```text
  todo   unique errand  
todo unique errand
event bad meeting /from 2026-09-21 /to 2026-09-20
deadline report /by 2026-02-30
deadline report /by 2026-09-20 /by 2026-09-21
bye
```
**Actual output:**
```text
 _____ ____ ___ _____ _   _
| ____|  _ \_ _|_   _| | | |
|  _| | | | | |  | | | |_| |
| |___| |_| | |  | | |  _  |
|_____|____/___| |_| |_| |_|

Hello! I'm EDITH.
Type 'help' to list all commands.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] unique errand
Now you have 9 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! That task already exists in your list.
____________________________________________________________
____________________________________________________________
OOPS!!! The event start must be before its end.
____________________________________________________________
____________________________________________________________
OOPS!!! The deadline date must use yyyy-MM-dd, optionally followed by a time in the format HHmm.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs exactly one /by marker. Use: deadline DESCRIPTION /by yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
\n