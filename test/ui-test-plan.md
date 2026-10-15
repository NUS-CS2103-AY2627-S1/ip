# UI Test Plan

## Test case: Find tasks regardless of letter case

**Aim:** Verify that find supports partial keywords without requiring matching letter case.

### Input

```text
todo Read Book
deadline BOOK flight /by 2026-10-20
todo submit report
find bOo
bye
```

### Expected output

```text
____________________________________________________________
 ____  _____ _____ __  __  ___  
| __ )| ____| ____|  \/  |/ _ \ 
|  _ \|  _| |  _| | |\/| | | | |
| |_) | |___| |___| |  | | |_| |
|____/|_____|_____|_|  |_|\___/ 
Hello! I'm Beemo! (˶ᵔ ᵕ ᵔ˶)
What can I do for you? ᵔ ᵕ ᵔ
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [T][ ] Read Book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [D][ ] BOOK flight (by: Oct 20 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [T][ ] submit report
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] Read Book
2.[D][ ] BOOK flight (by: Oct 20 2026)
____________________________________________________________
____________________________________________________________
Beemo signing off! See you next time! ૮ ˶ᵔ ᵕ ᵔ˶ ა
____________________________________________________________
```

## Test case: Display command guidance

**Aim:** Verify that help lists every supported command without changing task data.

### Input

```text
help
bye
```

### Expected output

```text
____________________________________________________________
 ____  _____ _____ __  __  ___  
| __ )| ____| ____|  \/  |/ _ \ 
|  _ \|  _| |  _| | |\/| | | | |
| |_) | |___| |___| |  | | |_| |
|____/|_____|_____|_|  |_|\___/ 
Hello! I'm Beemo! (˶ᵔ ᵕ ᵔ˶)
What can I do for you? ᵔ ᵕ ᵔ
____________________________________________________________
____________________________________________________________
Here are the commands I understand:
  todo DESCRIPTION
  deadline DESCRIPTION /by yyyy-MM-dd
  event DESCRIPTION /from START /to END
  list
  find KEYWORD
  mark TASK_NUMBER
  unmark TASK_NUMBER
  delete TASK_NUMBER
  help
  bye
____________________________________________________________
____________________________________________________________
Beemo signing off! See you next time! ૮ ˶ᵔ ᵕ ᵔ˶ ა
____________________________________________________________
```

## JavaFX GUI smoke test

Run `./gradlew run` and verify the following manually:

1. The Beemo window opens with a welcome message, chat history, command field, and Send button.
2. Enter `todo read book` and verify both the user message and Beemo's confirmation appear.
3. Enter `list` and verify the added task appears in the response.
4. Enter an invalid command and verify Beemo displays an error without closing the window.
5. Resize the window and verify the input field, Send button, and chat history remain usable.
6. Close the window and verify the Gradle run finishes.

The automated cases below continue to exercise the command-line interface and core chatbot behavior.

The test runner compiles the Java files in `src/main/java`, starts a fresh `Beemo` process in an isolated temporary working directory for each case, sends the listed commands through standard input, and compares the complete standard output exactly. Isolation ensures that saved task data from one case cannot affect another case. Cases run from top to bottom and testing stops at the first failure.

## Test case: Find tasks by description keyword

**Aim:** Verify that find displays only tasks whose descriptions contain the keyword.

### Input

```text
todo read book
deadline return book /by 2026-08-30
event book club /from 2pm /to 4pm
todo submit report
find book
find missing
find
bye
```

### Expected output

```text
____________________________________________________________
 ____  _____ _____ __  __  ___  
| __ )| ____| ____|  \/  |/ _ \ 
|  _ \|  _| |  _| | |\/| | | | |
| |_) | |___| |___| |  | | |_| |
|____/|_____|_____|_|  |_|\___/ 
Hello! I'm Beemo! (˶ᵔ ᵕ ᵔ˶)
What can I do for you? ᵔ ᵕ ᵔ
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [D][ ] return book (by: Aug 30 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [E][ ] book club (from: 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [T][ ] submit report
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Aug 30 2026)
3.[E][ ] book club (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
____________________________________________________________
____________________________________________________________
OOPS... Please provide a keyword after 'find'.
____________________________________________________________
____________________________________________________________
Beemo signing off! See you next time! ૮ ˶ᵔ ᵕ ᵔ˶ ა
____________________________________________________________
```

## Test case: Add and list a todo

**Aim:** Verify that irregular command spacing is accepted and a todo is stored correctly.

### Input

```text
  todo   borrow book
  list
  bye
```

### Expected output

```text
____________________________________________________________
 ____  _____ _____ __  __  ___  
| __ )| ____| ____|  \/  |/ _ \ 
|  _ \|  _| |  _| | |\/| | | | |
| |_) | |___| |___| |  | | |_| |
|____/|_____|_____|_|  |_|\___/ 
Hello! I'm Beemo! (˶ᵔ ᵕ ᵔ˶)
What can I do for you? ᵔ ᵕ ᵔ
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Beemo signing off! See you next time! ૮ ˶ᵔ ᵕ ᵔ˶ ა
____________________________________________________________
```

## Test case: Delete tasks without corrupting list state

**Aim:** Verify invalid deletions preserve state while valid deletions renumber tasks and retain their completion status.

### Input

```text
todo read book
deadline return book /by 2026-08-30
event meeting /from 2pm /to 4pm
mark 1
delete 0
delete two
delete 4
delete 2
list
mark 2
delete 1
list
delete 2
delete 1
delete 1
bye
```

### Expected output

```text
____________________________________________________________
 ____  _____ _____ __  __  ___  
| __ )| ____| ____|  \/  |/ _ \ 
|  _ \|  _| |  _| | |\/| | | | |
| |_) | |___| |___| |  | | |_| |
|____/|_____|_____|_|  |_|\___/ 
Hello! I'm Beemo! (˶ᵔ ᵕ ᵔ˶)
What can I do for you? ᵔ ᵕ ᵔ
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [D][ ] return book (by: Aug 30 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [E][ ] meeting (from: 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Yay good job! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
OOPS... Task 0 is not in your list. ╥‸╥ Please try another number.
____________________________________________________________
____________________________________________________________
OOPS... 'two' is not a valid task number. ╥‸╥ Please enter a number shown in your task list.
____________________________________________________________
____________________________________________________________
OOPS... Task 4 is not in your list. ╥‸╥ Please try another number.
____________________________________________________________
____________________________________________________________
All right, I've removed this task:
  [D][ ] return book (by: Aug 30 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Yay good job! I've marked this task as done:
  [E][X] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
All right, I've removed this task:
  [T][X] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E][X] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
OOPS... Task 2 is not in your list. ╥‸╥ Please try another number.
____________________________________________________________
____________________________________________________________
All right, I've removed this task:
  [E][X] meeting (from: 2pm to: 4pm)
Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS... Task 1 is not in your list. ╥‸╥ Please try another number.
____________________________________________________________
____________________________________________________________
Beemo signing off! See you next time! ૮ ˶ᵔ ᵕ ᵔ˶ ა
____________________________________________________________
```

## Test case: Invalid additions preserve task order

**Aim:** Verify that rejected task commands do not add entries or shift the numbering of valid tasks.

### Input

```text
list
todo read book
todo
deadline return book
deadline return book /by Sunday
deadline return book /by 2026-08-30
event meeting /from Monday
event meeting /from 4pm /to 4pm
event test /from 2026-03-03 /to 2025-03-03
event midterm /from 2026-09-16 9pm /to 2026-09-16 1pm
event CS2100 Quiz /from 2026-02-29 90pm /to 100pm
event hello /from hello /to hello
event test3 /from 2026 09 30 /to 2026 10 01
event conf /from 1pm /to 2pm /from 3pm
event meeting /from 2pm /to 4pm
blah
list
bye
```

### Expected output

```text
____________________________________________________________
 ____  _____ _____ __  __  ___  
| __ )| ____| ____|  \/  |/ _ \ 
|  _ \|  _| |  _| | |\/| | | | |
| |_) | |___| |___| |  | | |_| |
|____/|_____|_____|_|  |_|\___/ 
Hello! I'm Beemo! (˶ᵔ ᵕ ᵔ˶)
What can I do for you? ᵔ ᵕ ᵔ
____________________________________________________________
____________________________________________________________
Your task list is empty. Add a task whenever you're ready!
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
OOPS... The description of a todo cannot be empty. ╥‸╥ Add a description after 'todo'.
____________________________________________________________
____________________________________________________________
OOPS... A deadline needs a '/by' date or time. ╥‸╥ Use: deadline DESCRIPTION /by yyyy-MM-dd.
____________________________________________________________
____________________________________________________________
OOPS... Deadline dates must use the yyyy-MM-dd format. ╥‸╥ For example, use 2026-09-30.
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [D][ ] return book (by: Aug 30 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS... An event needs both '/from' and '/to' times. ╥‸╥ Use: event DESCRIPTION /from START /to END.
____________________________________________________________
____________________________________________________________
OOPS... An event must end after it starts. ╥‸╥ Enter an '/to' time later than the '/from' time.
____________________________________________________________
____________________________________________________________
OOPS... An event cannot end before it starts. ╥‸╥ Enter an '/to' date later than or equal to the '/from' date.
____________________________________________________________
____________________________________________________________
OOPS... An event must end after it starts. ╥‸╥ Enter an '/to' date and time later than '/from'.
____________________________________________________________
____________________________________________________________
OOPS... Event dates and times are invalid. ╥‸╥ Use yyyy-MM-dd, a clock time such as 2pm, or yyyy-MM-dd 2pm.
____________________________________________________________
____________________________________________________________
OOPS... Event dates and times are invalid. ╥‸╥ Use yyyy-MM-dd, a clock time such as 2pm, or yyyy-MM-dd 2pm.
____________________________________________________________
____________________________________________________________
OOPS... Event dates and times are invalid. ╥‸╥ Use yyyy-MM-dd, a clock time such as 2pm, or yyyy-MM-dd 2pm.
____________________________________________________________
____________________________________________________________
OOPS... An event must contain exactly one '/from' and one '/to'. ╥‸╥ Remove the duplicate time delimiter.
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [E][ ] meeting (from: 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS... I don't know what that means ╥‸╥ Try 'help' to see the available commands.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Aug 30 2026)
3.[E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Beemo signing off! See you next time! ૮ ˶ᵔ ᵕ ᵔ˶ ა
____________________________________________________________
```

## Test case: Invalid status changes preserve completion state

**Aim:** Verify that invalid mark and unmark commands do not alter valid tasks or prevent later status updates.

### Input

```text
todo read book
deadline return book /by 2026-08-30
mark 1
mark 0
mark two
mark 3
unmark 2
unmark 1
unmark -1
mark 2
list
bye
```

### Expected output

```text
____________________________________________________________
 ____  _____ _____ __  __  ___  
| __ )| ____| ____|  \/  |/ _ \ 
|  _ \|  _| |  _| | |\/| | | | |
| |_) | |___| |___| |  | | |_| |
|____/|_____|_____|_|  |_|\___/ 
Hello! I'm Beemo! (˶ᵔ ᵕ ᵔ˶)
What can I do for you? ᵔ ᵕ ᵔ
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [D][ ] return book (by: Aug 30 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Yay good job! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
OOPS... Task 0 is not in your list. ╥‸╥ Please try another number.
____________________________________________________________
____________________________________________________________
OOPS... 'two' is not a valid task number. ╥‸╥ Please enter a number shown in your task list.
____________________________________________________________
____________________________________________________________
OOPS... Task 3 is not in your list. ╥‸╥ Please try another number.
____________________________________________________________
____________________________________________________________
No problem! I've marked this task as not done yet:
  [D][ ] return book (by: Aug 30 2026)
____________________________________________________________
____________________________________________________________
No problem! I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
OOPS... Task -1 is not in your list. ╥‸╥ Please try another number.
____________________________________________________________
____________________________________________________________
Yay good job! I've marked this task as done:
  [D][X] return book (by: Aug 30 2026)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][X] return book (by: Aug 30 2026)
____________________________________________________________
____________________________________________________________
Beemo signing off! See you next time! ૮ ˶ᵔ ᵕ ᵔ˶ ა
____________________________________________________________
```

## Test case: Recover from invalid commands

**Aim:** Verify that invalid task commands report specific errors and do not terminate the program.

### Input

```text
todo
blah
deadline submit report
event meeting /from Monday
mark
mark two
mark 1
todo borrow book
mark 2
bye
```

### Expected output

```text
____________________________________________________________
 ____  _____ _____ __  __  ___  
| __ )| ____| ____|  \/  |/ _ \ 
|  _ \|  _| |  _| | |\/| | | | |
| |_) | |___| |___| |  | | |_| |
|____/|_____|_____|_|  |_|\___/ 
Hello! I'm Beemo! (˶ᵔ ᵕ ᵔ˶)
What can I do for you? ᵔ ᵕ ᵔ
____________________________________________________________
____________________________________________________________
OOPS... The description of a todo cannot be empty. ╥‸╥ Add a description after 'todo'.
____________________________________________________________
____________________________________________________________
OOPS... I don't know what that means ╥‸╥ Try 'help' to see the available commands.
____________________________________________________________
____________________________________________________________
OOPS... A deadline needs a '/by' date or time. ╥‸╥ Use: deadline DESCRIPTION /by yyyy-MM-dd.
____________________________________________________________
____________________________________________________________
OOPS... An event needs both '/from' and '/to' times. ╥‸╥ Use: event DESCRIPTION /from START /to END.
____________________________________________________________
____________________________________________________________
OOPS... Please provide a task number after 'mark'.
____________________________________________________________
____________________________________________________________
OOPS... 'two' is not a valid task number. ╥‸╥ Please enter a number shown in your task list.
____________________________________________________________
____________________________________________________________
OOPS... Task 1 is not in your list. ╥‸╥ Please try another number.
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
OOPS... Task 2 is not in your list. ╥‸╥ Please try another number.
____________________________________________________________
____________________________________________________________
Beemo signing off! See you next time! ૮ ˶ᵔ ᵕ ᵔ˶ ა
____________________________________________________________
```

## Test case: Format deadline date and preserve event text

**Aim:** Verify deadline dates are reformatted while regular and overnight event times remain unchanged.

### Input

```text
deadline do homework /by 2026-12-01
event night shift /from 11pm /to 1am
event project meeting /from 2pm /to 4pm
mark 3
unmark 3
list
bye
```

### Expected output

```text
____________________________________________________________
 ____  _____ _____ __  __  ___  
| __ )| ____| ____|  \/  |/ _ \ 
|  _ \|  _| |  _| | |\/| | | | |
| |_) | |___| |___| |  | | |_| |
|____/|_____|_____|_|  |_|\___/ 
Hello! I'm Beemo! (˶ᵔ ᵕ ᵔ˶)
What can I do for you? ᵔ ᵕ ᵔ
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [D][ ] do homework (by: Dec 1 2026)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [E][ ] night shift (from: 11pm to: 1am)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it! I've added this task for you:
  [E][ ] project meeting (from: 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Yay good job! I've marked this task as done:
  [E][X] project meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
No problem! I've marked this task as not done yet:
  [E][ ] project meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] do homework (by: Dec 1 2026)
2.[E][ ] night shift (from: 11pm to: 1am)
3.[E][ ] project meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Beemo signing off! See you next time! ૮ ˶ᵔ ᵕ ᵔ˶ ა
____________________________________________________________
```
