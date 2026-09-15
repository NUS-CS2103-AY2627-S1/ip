# UI Test Plan

This file records the **manual CLI-style UI test cases** for this project in a format that the `test-ui` Codex skill can execute.

## How it works

- Each test case is a fenced block with language `ui-test`.
- The runner executes test cases **in order** and **stops immediately** on the first failure.
- For each test case, the runner:
  1. Runs the command
  2. Sends the `stdin` text (if any)
  3. Captures combined console output (stdout + stderr)
  4. Compares output to `expected` (after normalization)
  5. Appends a transcript of the session to `_temp/ui-test-session.txt`

### Output normalization

To make tests stable across OSes/editors, comparison normalizes:
- `\r\n` → `\n`
- trailing whitespace at the end of lines is removed
- a final trailing newline is ignored

## Test cases

### TC0: Runner sanity check

```ui-test
id: TC0
aim: Verify the UI test runner can execute a command and compare output.
cmd: python3 -c "print('ok')"
expected: |
  ok
```

### TC1: Launch and exit

```ui-test
id: TC1
aim: Verify the app starts and can exit immediately.
cmd: sh -c "rm -f data/duke.txt; printf 'bye\n' | java -cp build/classes/java/main duke.Duke"
stdin: |
  bye
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  Bye! Hope to see you again soon!
```

### TC2: Add a todo

```ui-test
id: TC2
aim: Verify a todo command adds a task and confirms it.
cmd: sh -c "rm -f data/duke.txt; printf 'todo read book\nbye\n' | java -cp build/classes/java/main duke.Duke"
stdin: |
  todo read book
  bye
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  Got it! Here's the task you added:
    [T] [ ] read book
  Now you have 1 tasks in your list!
  ____________________________________________________________
  Bye! Hope to see you again soon!
```

### TC3: Reject unknown commands

```ui-test
id: TC3
aim: Verify an unknown command shows the generic error message.
cmd: sh -c "rm -f data/duke.txt; printf 'xyz\nbye\n' | java -cp build/classes/java/main duke.Duke"
stdin: |
  xyz
  bye
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  I'm sorry, but Gary doesn't know what that means!
  ____________________________________________________________
  Bye! Hope to see you again soon!
```

### TC4: Show an empty list

```ui-test
id: TC4
aim: Verify list prints the empty-task message when no tasks exist.
cmd: sh -c "rm -f data/duke.txt; printf 'list\nbye\n' | java -cp build/classes/java/main duke.Duke"
stdin: |
  list
  bye
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  These are the tasks you have in your list!
  ____________________________________________________________
  Bye! Hope to see you again soon!
```

### TC5: Add a deadline

```ui-test
id: TC5
aim: Verify a deadline command stores the due date and confirms it.
cmd: sh -c "rm -f data/duke.txt; printf 'deadline submit report /by 2026-06-06\nbye\n' | java -cp build/classes/java/main duke.Duke"
stdin: |
  deadline submit report /by 2026-06-06
  bye
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  Got it! Here's the task you added:
    [D] [ ] submit report (by: 2026-06-06)
  Now you have 1 tasks in your list!
  ____________________________________________________________
  Bye! Hope to see you again soon!
```

### TC6: Add an event

```ui-test
id: TC6
aim: Verify an event command stores the start and end times and confirms it.
cmd: sh -c "rm -f data/duke.txt; printf 'event project meeting /from 2026-08-06 /to 2026-08-07\nbye\n' | java -cp build/classes/java/main duke.Duke"
stdin: |
  event project meeting /from 2026-08-06 /to 2026-08-07
  bye
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  Got it! Here's the task you added:
    [E] [ ] project meeting (from: 2026-08-06 to: 2026-08-07)
  Now you have 1 tasks in your list!
  ____________________________________________________________
  Bye! Hope to see you again soon!
```

### TC7: Save a todo to disk

```ui-test
id: TC7
aim: Verify a todo is written to the hard disk when the task list changes.
cmd: sh -c "rm -f data/duke.txt; printf 'todo read book\nbye\n' | java -cp build/classes/java/main duke.Duke; printf '\n---FILE---\n'; cat data/duke.txt"
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  Got it! Here's the task you added:
    [T] [ ] read book
  Now you have 1 tasks in your list!
  ____________________________________________________________
  Bye! Hope to see you again soon!


  ---FILE---
  T | 0 | read book
```

### TC8: Load tasks from disk

```ui-test
id: TC8
aim: Verify the app loads saved tasks from the hard disk on startup.
cmd: sh -c "printf 'T | 1 | read book\nD | 0 | return book | 2026-06-06\n' > data/duke.txt; printf 'list\nbye\n' | java -cp build/classes/java/main duke.Duke"
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  These are the tasks you have in your list!
  1. [T] [X] read book
  2. [D] [ ] return book (by: 2026-06-06)
  ____________________________________________________________
  Bye! Hope to see you again soon!
```

### TC9: Ignore blank input

```ui-test
id: TC9
aim: Verify blank input is rejected without crashing.
cmd: sh -c "rm -f data/duke.txt; printf '   \nbye\n' | java -cp build/classes/java/main duke.Duke"
stdin: |
    
  bye
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  Please enter a command.
  ____________________________________________________________
  Bye! Hope to see you again soon!
```

### TC10: Reject invalid mark input

```ui-test
id: TC10
aim: Verify mark rejects non-numeric task numbers.
cmd: sh -c "rm -f data/duke.txt; printf 'mark abc\nbye\n' | java -cp build/classes/java/main duke.Duke"
stdin: |
  mark abc
  bye
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  Please provide a positive whole-number task number.
  ____________________________________________________________
  Bye! Hope to see you again soon!
```

### TC11: Reject invalid delete input

```ui-test
id: TC11
aim: Verify delete rejects task numbers that do not exist.
cmd: sh -c "rm -f data/duke.txt; printf 'delete 9\nbye\n' | java -cp build/classes/java/main duke.Duke"
stdin: |
  delete 9
  bye
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  I can't find a task with that number!
  ____________________________________________________________
  Bye! Hope to see you again soon!
```

### TC12: Reject incomplete deadline input

```ui-test
id: TC12
aim: Verify deadline rejects missing description or due date.
cmd: sh -c "rm -f data/duke.txt; printf 'deadline   /by friday\nbye\n' | java -cp build/classes/java/main duke.Duke"
stdin: |
  deadline   /by friday
  bye
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  Use: deadline <description> /by <YYYY-MM-DD>.
  ____________________________________________________________
  Bye! Hope to see you again soon!
```

### TC13: Reject incomplete event input

```ui-test
id: TC13
aim: Verify event rejects missing start or end time.
cmd: sh -c "rm -f data/duke.txt; printf 'event meet /from 2pm\nbye\n' | java -cp build/classes/java/main duke.Duke"
stdin: |
  event meet /from 2pm
  bye
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  Use: event <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD>.
  ____________________________________________________________
  Bye! Hope to see you again soon!
```

### TC14: Reject malformed save file rows

```ui-test
id: TC14
aim: Verify invalid rows show a startup warning and leave a usable empty list.
cmd: sh -c "printf 'bad line\nT | 1 | keep me\nE | 0 | missing parts\n' > data/duke.txt; printf 'list\nbye\n' | java -cp build/classes/java/main duke.Duke"
stdin: |
  list
  bye
expected: |
   ================================================================== 
    ____                  
   / ___| __ _ _ __ _   _ 
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/ 
   ================================================================== 
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  Task file data is invalid on line 1: the task type is unknown. Gary started with an empty list; changes will not be saved in this session.
  ____________________________________________________________
  ____________________________________________________________
  These are the tasks you have in your list!
  ____________________________________________________________
  Bye! Hope to see you again soon!
```

### TC15: Undo recent task changes

```ui-test
id: TC15
aim: Verify undo reverses recent changes in order and rejects arguments without consuming history.
cmd: sh -c "rm -f data/duke.txt; printf 'todo read book\nmark 1\nundo 2\nundo\nundo\nlist\nbye\n' | java -cp build/classes/java/main duke.Duke"
stdin: |
  todo read book
  mark 1
  undo 2
  undo
  undo
  list
  bye
expected: |
   ==================================================================
    ____
   / ___| __ _ _ __ _   _
  | |  _ / _` | '__| | | |
  | |_| | (_| | |  | |_| |
   \____|\__,_|_|   \__, |
                    |___/
   ==================================================================
  HELLO! I'm GARY!
  How can I help you today?
  (Type bye to exit)

  ____________________________________________________________
  Got it! Here's the task you added:
    [T] [ ] read book
  Now you have 1 tasks in your list!
  ____________________________________________________________
  ____________________________________________________________
  Gary marked task 1 as done!
  ____________________________________________________________
  ____________________________________________________________
  The undo command does not take any arguments.
  ____________________________________________________________
  Undid the last change.
  Undid the last change.
  ____________________________________________________________
  These are the tasks you have in your list!
  ____________________________________________________________
  Bye! Hope to see you again soon!
```

## Manual GUI compatibility checks

Run these checks before a release because window rendering, operating-system
scaling, and native JavaFX behavior are not reliable in headless JUnit tests.

### Window sizes and display scaling

1. Launch the GUI and resize it to its minimum supported size.
2. Repeat at 800×600, 1024×768, 1440×900, and a maximized window.
3. At each size, enter a todo and an invalid command, then paste a response of
   at least 2,000 characters into the conversation history.
4. Verify avatars stay round, messages remain readable, the input stays visible,
   long replies scroll, and decorative background space does not crowd out text.
5. Repeat at 100%, 125%, 150%, and 200% display scaling when the operating
   system supports those settings.

### Operating systems

Repeat the launch, add, list, mark, delete, undo, and exit flow on:

- macOS using `./gradlew runGui`
- Windows using `gradlew.bat runGui`
- Linux using `./gradlew runGui`

Confirm that the task file is saved and reloaded using the native path and line
ending conventions on each operating system.

### Language and locale settings

1. Run once with the operating system language set to English.
2. Run again with a non-English language such as Simplified Chinese.
3. Repeat with a locale whose case rules differ from English, such as Turkish.
4. Verify English commands remain case-insensitive, ISO dates remain unchanged,
   task search still ignores case, and no text is clipped by font substitution.
