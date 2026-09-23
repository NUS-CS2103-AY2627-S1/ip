# UI test plan

## Test environment

- Run the plan from the repository root with `py .codex/skills/test-ui/scripts/run_ui_tests.py`.
- The runner compiles every Java file in `src/main/java` before testing.
- ANSI colour codes are removed before comparison, so expected outputs contain plain text.
- Each test case starts a fresh Bob session. End every input block with `bye`.

## Manual GUI background check

Also enter `todo`, `mark 0`, `mark abc`, `deadline test /by 31/02/2026`,
and an unknown command. Verify that Bob's error replies are italic while user
messages are not. Then enter `list` and verify that the normal reply is not italic.

Launch the chatbot with `./gradlew run` using Java 25. Check that the window
title is `Bob`. Check that
`src/main/resources/images/background.jpg` appears behind the chat messages.
Resize the window and verify that the image stays centered, fills the window,
and preserves its proportions without tiling. Send enough messages to scroll
and check that the background stays fixed and the messages and input remain readable.
This visual check is separate from the automated console cases below.

Verify that Send is blue with white text, user bubbles are pale blue, and Bob's
bubbles are white. Both speakers should use dark 16px text and avatars fitting
within 40 by 40 pixels. Check that bubbles have 12px vertical and 16px horizontal
padding, and that long replies wrap without clipping at the default window size.
Verify that the greeting, error replies, and task lists have no empty line after
their final text line. Task lists should retain the line breaks between tasks.

## Manual GUI exit check

Launch Bob, add a task, and enter `bye` using Enter. Verify that the window
closes and the application process ends. Relaunch and use `list` to confirm the
task was saved. Repeat using `b` and the Send button. Enter `bye now` and `b now`
in a fresh session and verify that an error appears and the window stays open.

## Test case: Greeting and unknown command
Aim: Verify the greeting and unknown-command reply use Bob's updated wording.

Input:
```text
nonsense
bye
```

Expected output:
```text
hi im bob
____________________________________________________________

sorry bro, that aint a command
____________________________________________________________

____________________________________________________________

Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Create, update, and list all task types
Aim: Verify that Bob creates to-do, deadline, and event tasks, updates a task's completion status, and lists their formatted details.

Input:
```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 03/12/2019 1400 /to 03/12/2019 1600
mark 1
unmark 1
list
bye
```

Expected output:
```text
hi im bob
____________________________________________________________

Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
[D][ ] return book (by: Dec 02 2019 1800hrs)
Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
[E][ ] project meeting (from: Dec 03 2019 1400hrs to: Dec 03 2019 1600hrs)
Now you have 3 tasks in the list.
____________________________________________________________

____________________________________________________________

I marked this task as done:
[T][X] borrow book
____________________________________________________________

____________________________________________________________

I marked this task as not done:
[T][ ] borrow book
____________________________________________________________

____________________________________________________________

Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 02 2019 1800hrs)
3.[E][ ] project meeting (from: Dec 03 2019 1400hrs to: Dec 03 2019 1600hrs)
____________________________________________________________

____________________________________________________________

Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Show usage for commands without required arguments
Aim: Verify that bare commands requiring arguments show their usage instead of being reported as unknown commands.

Input:
```text
todo
deadline
event
mark
unmark
delete
bye
```

Expected output:
```text
hi im bob
____________________________________________________________

Invalid command format. Use: todo <description>
____________________________________________________________

____________________________________________________________

Invalid command format. Use: deadline <description> /by <dd/MM/yyyy or dd/MM/yyyy HHmm>
____________________________________________________________

____________________________________________________________

Invalid command format. Use: event <description> /from <dd/MM/yyyy or dd/MM/yyyy HHmm> /to <dd/MM/yyyy or dd/MM/yyyy HHmm>
____________________________________________________________

____________________________________________________________

Invalid command format. Use: mark <task_number>
____________________________________________________________

____________________________________________________________

Invalid command format. Use: unmark <task_number>
____________________________________________________________

____________________________________________________________

Invalid command format. Use: delete <task_number>
____________________________________________________________

____________________________________________________________

Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject invalid commands without changing saved tasks
Aim: Verify that invalid deadline, event, mark, unmark, and delete commands do not corrupt saved tasks, while valid deletion removes and reindexes a task.

Input:
```text
todo first task
deadline missing date
deadline impossible date /by 31/02/2019
list
event meeting /from 2pm
event invalid time /from 03/12/2019 2400 /to 03/12/2019 2500
todo second task
delete 3
mark 2
delete 1
unmark nope
list
bye
```

Expected output:
```text
hi im bob
____________________________________________________________

Got it. I've added this task:
[T][ ] first task
Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________

Invalid command format. Use: deadline <description> /by <dd/MM/yyyy or dd/MM/yyyy HHmm>
____________________________________________________________

____________________________________________________________

Invalid date or time. Use dd/MM/yyyy or dd/MM/yyyy HHmm with a 24-hour time.
____________________________________________________________

____________________________________________________________

Here are the tasks in your list:
1.[T][ ] first task
____________________________________________________________

____________________________________________________________

Invalid command format. Use: event <description> /from <dd/MM/yyyy or dd/MM/yyyy HHmm> /to <dd/MM/yyyy or dd/MM/yyyy HHmm>
____________________________________________________________

____________________________________________________________

Invalid date or time. Use dd/MM/yyyy or dd/MM/yyyy HHmm with a 24-hour time.
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
[T][ ] second task
Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________

Invalid task number.
____________________________________________________________

____________________________________________________________

I marked this task as done:
[T][X] second task
____________________________________________________________

____________________________________________________________

Noted. I've removed this task:
[T][ ] first task
Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________

Invalid task number format.
____________________________________________________________

____________________________________________________________

Here are the tasks in your list:
1.[T][X] second task
____________________________________________________________

____________________________________________________________

Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject tasks without a description
Aim: Verify that malformed deadline and event commands do not terminate Bob and that a subsequent valid task can still be added and listed.

Input:
```text
deadline /by Sunday
event /from 2pm /to 4pm
todo valid task
list
bye
```

Expected output:
```text
hi im bob
____________________________________________________________

Invalid command format. Use: deadline <description> /by <dd/MM/yyyy or dd/MM/yyyy HHmm>
____________________________________________________________

____________________________________________________________

Invalid command format. Use: event <description> /from <dd/MM/yyyy or dd/MM/yyyy HHmm> /to <dd/MM/yyyy or dd/MM/yyyy HHmm>
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
[T][ ] valid task
Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________

Here are the tasks in your list:
1.[T][ ] valid task
____________________________________________________________

____________________________________________________________

Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Find tasks by keyword
Aim: Verify that find displays matching tasks in their original order, ignores letter case, and rejects a blank keyword.

Input:
```text
todo read book
deadline return book /by 2/12/2019
todo watch movie
mark 1
mark 2
find BOOK
find movie
find missing
find
bye
```

Expected output:
```text
hi im bob
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
[T][ ] watch movie
Now you have 3 tasks in the list.
____________________________________________________________

____________________________________________________________

I marked this task as done:
[T][X] read book
____________________________________________________________

____________________________________________________________

I marked this task as done:
[D][X] return book (by: Dec 02 2019)
____________________________________________________________

____________________________________________________________

Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 02 2019)
____________________________________________________________

____________________________________________________________

Here are the matching tasks in your list:
1.[T][ ] watch movie
____________________________________________________________

____________________________________________________________

Here are the matching tasks in your list:
____________________________________________________________

____________________________________________________________

Invalid command format. Use: find <keyword>
____________________________________________________________

____________________________________________________________

Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Short aliases - Create, update, and list all task types
Aim: Verify that short aliases behave like full commands: Bob creates to-do, deadline, and event tasks, updates a task's completion status, and lists their formatted details.

Input:
```text
t borrow book
d return book /by 2/12/2019 1800
e project meeting /from 03/12/2019 1400 /to 03/12/2019 1600
m 1
u 1
l
b
bye
```

Expected output:
```text
hi im bob
____________________________________________________________

Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
[D][ ] return book (by: Dec 02 2019 1800hrs)
Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
[E][ ] project meeting (from: Dec 03 2019 1400hrs to: Dec 03 2019 1600hrs)
Now you have 3 tasks in the list.
____________________________________________________________

____________________________________________________________

I marked this task as done:
[T][X] borrow book
____________________________________________________________

____________________________________________________________

I marked this task as not done:
[T][ ] borrow book
____________________________________________________________

____________________________________________________________

Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 02 2019 1800hrs)
3.[E][ ] project meeting (from: Dec 03 2019 1400hrs to: Dec 03 2019 1600hrs)
____________________________________________________________

____________________________________________________________

Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Short aliases - Show usage for commands without required arguments
Aim: Verify that short aliases behave like full commands: bare commands requiring arguments show their usage instead of being reported as unknown commands.

Input:
```text
t
d
e
m
u
del
b
bye
```

Expected output:
```text
hi im bob
____________________________________________________________

Invalid command format. Use: todo <description>
____________________________________________________________

____________________________________________________________

Invalid command format. Use: deadline <description> /by <dd/MM/yyyy or dd/MM/yyyy HHmm>
____________________________________________________________

____________________________________________________________

Invalid command format. Use: event <description> /from <dd/MM/yyyy or dd/MM/yyyy HHmm> /to <dd/MM/yyyy or dd/MM/yyyy HHmm>
____________________________________________________________

____________________________________________________________

Invalid command format. Use: mark <task_number>
____________________________________________________________

____________________________________________________________

Invalid command format. Use: unmark <task_number>
____________________________________________________________

____________________________________________________________

Invalid command format. Use: delete <task_number>
____________________________________________________________

____________________________________________________________

Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Short aliases - Reject invalid commands without changing saved tasks
Aim: Verify that short aliases behave like full commands: invalid deadline, event, mark, unmark, and delete commands do not corrupt saved tasks, while valid deletion removes and reindexes a task.

Input:
```text
t first task
d missing date
d impossible date /by 31/02/2019
l
e meeting /from 2pm
e invalid time /from 03/12/2019 2400 /to 03/12/2019 2500
t second task
del 3
m 2
del 1
u nope
l
b
bye
```

Expected output:
```text
hi im bob
____________________________________________________________

Got it. I've added this task:
[T][ ] first task
Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________

Invalid command format. Use: deadline <description> /by <dd/MM/yyyy or dd/MM/yyyy HHmm>
____________________________________________________________

____________________________________________________________

Invalid date or time. Use dd/MM/yyyy or dd/MM/yyyy HHmm with a 24-hour time.
____________________________________________________________

____________________________________________________________

Here are the tasks in your list:
1.[T][ ] first task
____________________________________________________________

____________________________________________________________

Invalid command format. Use: event <description> /from <dd/MM/yyyy or dd/MM/yyyy HHmm> /to <dd/MM/yyyy or dd/MM/yyyy HHmm>
____________________________________________________________

____________________________________________________________

Invalid date or time. Use dd/MM/yyyy or dd/MM/yyyy HHmm with a 24-hour time.
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
[T][ ] second task
Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________

Invalid task number.
____________________________________________________________

____________________________________________________________

I marked this task as done:
[T][X] second task
____________________________________________________________

____________________________________________________________

Noted. I've removed this task:
[T][ ] first task
Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________

Invalid task number format.
____________________________________________________________

____________________________________________________________

Here are the tasks in your list:
1.[T][X] second task
____________________________________________________________

____________________________________________________________

Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Short aliases - Reject tasks without a description
Aim: Verify that short aliases behave like full commands: malformed deadline and event commands do not terminate Bob and that a subsequent valid task can still be added and listed.

Input:
```text
d /by Sunday
e /from 2pm /to 4pm
t valid task
l
b
bye
```

Expected output:
```text
hi im bob
____________________________________________________________

Invalid command format. Use: deadline <description> /by <dd/MM/yyyy or dd/MM/yyyy HHmm>
____________________________________________________________

____________________________________________________________

Invalid command format. Use: event <description> /from <dd/MM/yyyy or dd/MM/yyyy HHmm> /to <dd/MM/yyyy or dd/MM/yyyy HHmm>
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
[T][ ] valid task
Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________

Here are the tasks in your list:
1.[T][ ] valid task
____________________________________________________________

____________________________________________________________

Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Short aliases - Find tasks by keyword
Aim: Verify that short aliases behave like full commands: find displays matching tasks in their original order, ignores letter case, and rejects a blank keyword.

Input:
```text
t read book
d return book /by 2/12/2019
t watch movie
m 1
m 2
f BOOK
f movie
f missing
f
b
bye
```

Expected output:
```text
hi im bob
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
[T][ ] watch movie
Now you have 3 tasks in the list.
____________________________________________________________

____________________________________________________________

I marked this task as done:
[T][X] read book
____________________________________________________________

____________________________________________________________

I marked this task as done:
[D][X] return book (by: Dec 02 2019)
____________________________________________________________

____________________________________________________________

Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 02 2019)
____________________________________________________________

____________________________________________________________

Here are the matching tasks in your list:
1.[T][ ] watch movie
____________________________________________________________

____________________________________________________________

Here are the matching tasks in your list:
____________________________________________________________

____________________________________________________________

Invalid command format. Use: find <keyword>
____________________________________________________________

____________________________________________________________

Bye. Hope to see you again soon!
____________________________________________________________
```
