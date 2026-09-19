# BENJI Console UI Test Plan

## Invalid event does not change the task list

Aim: Verify that BENJI rejects event markers in the wrong order and preserves the task list.

### Input

```text
list
event meeting /to 3pm /from 2pm
list
bye
```

### Expected output

```text
____________________________________________________________
  BBBBB   EEEEEEE  NN   NN  JJJJJJJ  IIIIIII
  BB  BB  EE       NNN  NN     JJJ     III
  BBBBB   EEEEE    NN N NN     JJJ     III
  BB  BB  EE       NN  NNN  JJ JJJ     III
  BBBBB   EEEEEEE  NN   NN   JJJJJ   IIIIIII

Hello! I'm BENJI.
What can I do for you?
____________________________________________________________
Here are the tasks in your list:
1.[T][X] sleep
2.[T][ ] homework
3.[T][X] go to school
4.[T][ ] lunch
Please ensure both '/from START' appears before '/to END' in your event description.
Here are the tasks in your list:
1.[T][X] sleep
2.[T][ ] homework
3.[T][X] go to school
4.[T][ ] lunch
Bye. Hope to see you again soon!
```

## Bare commands show specific guidance

Aim: Verify that commands without required arguments show command-specific errors.

### Input

```text
todo
find
deadline
event
mark
unmark
delete
bye
```

### Expected output

```text
____________________________________________________________
  BBBBB   EEEEEEE  NN   NN  JJJJJJJ  IIIIIII
  BB  BB  EE       NNN  NN     JJJ     III
  BBBBB   EEEEE    NN N NN     JJJ     III
  BB  BB  EE       NN  NNN  JJ JJJ     III
  BBBBB   EEEEEEE  NN   NN   JJJJJ   IIIIIII

Hello! I'm BENJI.
What can I do for you?
____________________________________________________________
Please enter a description after todo
Please enter a keyword after find.
Please enter a description after deadline.
Please enter a description after event.
Please enter a whole task number after mark.
Please enter a whole task number after unmark.
Please enter a task number after delete
Bye. Hope to see you again soon!
```
