# Aider UI Test Plan

## Test configuration

- Java version: 25
- Entry point: `src/main/java/Aider.java`
- Compile command: `javac -d out/production/ip src/main/java/*.java`
- Run command: `java -cp out/production/ip Aider`
- Input: one command per line, in the order shown for each test case
- Comparison: compare chatbot output exactly, excluding terminal echo of typed input
- Failure policy: stop the current test session immediately at the first mismatch

## Test case: Start and exit

Aim: Verify that Aider starts with its greeting and exits when the user enters `bye`.

Commands:

1. Input: `bye`
   Expected output:
   ```text
   Bye. Hope to see you again soon!
   ```

## Test case: Add and list tasks

Aim: Verify that ordinary text is stored as a task and displayed in numbered order by `list`.

Commands:

1. Input: `todo read book`
   Expected output:
   ```text
   Got it. I've added this task:
     [T][ ] read book
   Now you have 1 tasks in the list.
   ```
2. Input: `todo return book`
   Expected output:
   ```text
   Got it. I've added this task:
     [T][ ] return book
   Now you have 2 tasks in the list.
   ```
3. Input: `list`
   Expected output:
   ```text
   Here are the tasks in your list:
   1.[T][ ] read book
   2.[T][ ] return book
   ```
4. Input: `bye`
   Expected output:
   ```text
   Bye. Hope to see you again soon!
   ```

## Test case: Mark and unmark tasks

Aim: Verify that `mark N` sets a task to done and `unmark N` reverses it.

Commands:

1. Input: `todo read book`
   Expected output:
   ```text
   Got it. I've added this task:
     [T][ ] read book
   Now you have 1 tasks in the list.
   ```
2. Input: `todo return book`
   Expected output:
   ```text
   Got it. I've added this task:
     [T][ ] return book
   Now you have 2 tasks in the list.
   ```
3. Input: `mark 2`
   Expected output:
   ```text
   Nice! I've marked this task as done:
     [T][X] return book
   ```
4. Input: `list`
   Expected output:
   ```text
   Here are the tasks in your list:
   1.[T][ ] read book
   2.[T][X] return book
   ```
5. Input: `unmark 2`
   Expected output:
   ```text
   OK, I've marked this task as not done yet:
     [T][ ] return book
   ```
6. Input: `list`
   Expected output:
   ```text
   Here are the tasks in your list:
   1.[T][ ] read book
   2.[T][ ] return book
   ```
7. Input: `bye`
   Expected output:
   ```text
   Bye. Hope to see you again soon!
   ```

## Test case: Typed task types

Aim: Verify that `todo`, `deadline`, and `event` create the correct task subclasses and preserve date/time text as entered.

Commands:

1. Input: `todo borrow book`
   Expected output:
   ```text
   Got it. I've added this task:
     [T][ ] borrow book
   Now you have 1 tasks in the list.
   ```
2. Input: `deadline do homework /by no idea :-p`
   Expected output:
   ```text
   Got it. I've added this task:
     [D][ ] do homework (by: no idea :-p)
   Now you have 2 tasks in the list.
   ```
3. Input: `event project meeting /from Mon 2pm /to 4pm`
   Expected output:
   ```text
   Got it. I've added this task:
     [E][ ] project meeting (from: Mon 2pm to: 4pm)
   Now you have 3 tasks in the list.
   ```
4. Input: `list`
   Expected output:
   ```text
   Here are the tasks in your list:
   1.[T][ ] borrow book
   2.[D][ ] do homework (by: no idea :-p)
   3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
   ```
5. Input: `bye`
   Expected output:
   ```text
   Bye. Hope to see you again soon!
   ```

## Test case: Delete a task

Aim: Verify that `delete N` removes the selected task and renumbers the remaining tasks.

Commands:

1. Input: `todo read book`
   Expected output:
   ```text
   Got it. I've added this task:
     [T][ ] read book
   Now you have 1 tasks in the list.
   ```
2. Input: `deadline return book /by June 6th`
   Expected output:
   ```text
   Got it. I've added this task:
     [D][ ] return book (by: June 6th)
   Now you have 2 tasks in the list.
   ```
3. Input: `event project meeting /from Aug 6th 2pm /to 4pm`
   Expected output:
   ```text
   Got it. I've added this task:
     [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
   Now you have 3 tasks in the list.
   ```
4. Input: `delete 2`
   Expected output:
   ```text
   Noted. I've removed this task:
     [D][ ] return book (by: June 6th)
   Now you have 2 tasks in the list.
   ```
5. Input: `list`
   Expected output:
   ```text
   Here are the tasks in your list:
   1.[T][ ] read book
   2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
   ```
6. Input: `delete`
   Expected output:
   ```text
   OOPS!!! The delete command needs a task number.
   ```
7. Input: `bye`
   Expected output:
   ```text
   Bye. Hope to see you again soon!
   ```

## Test case: Invalid commands and task descriptions

Aim: Verify that malformed task commands and unknown commands produce helpful errors without terminating the session.

Commands:

1. Input: `todo`
   Expected output:
   ```text
   OOPS!!! A todo needs a description, for example: todo read book.
   ```
2. Input: `deadline`
   Expected output:
   ```text
   OOPS!!! A deadline must include a description and a /by date or time.
   ```
3. Input: `event project meeting /from Mon 2pm`
   Expected output:
   ```text
   OOPS!!! An event needs a description, /from time, and /to time.
   ```
4. Input: `blah`
   Expected output:
   ```text
   OOPS!!! I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or delete.
   ```
5. Input: `mark`
   Expected output:
   ```text
   OOPS!!! The mark command needs a task number.
   ```
6. Input: `bye`
   Expected output:
   ```text
   Bye. Hope to see you again soon!
   ```
