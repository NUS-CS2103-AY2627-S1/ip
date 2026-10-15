# UI Test Plan

This is the source of truth for Bob's console UI tests. Cases run in order, each in a fresh process.

## Test configuration

- **Java version:** 25 (`sdk use java 25.0.3.fx-zulu` on macOS when SDKMAN is available)
- **Compile command:** `javac -d /private/tmp/bob-ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*' -print)` (compile the console application without the JavaFX GUI dependencies)
- **Launch command:** `java -ea -cp /private/tmp/bob-ui-test-classes bob.Bob` (enable internal assertion checks)
- **Comparison:** Compare stdout exactly after normalizing line endings to LF. Prompts, spaces, and blank lines are significant.
- **Timeout:** 10 seconds per case.
- **Default setup:** Delete `data/bob.txt` before each case so every case starts with an empty task list. Cases that need saved tasks state their own setup instead.
- **Isolation:** Run each case in a fresh temporary working directory, applying its setup there so repository task data is untouched. Each case starts Bob in a fresh process after its setup is complete.

## UI-001: Add and list todo tasks

**Aim:** Verify that todo tasks can be added, saved without extra console output, and displayed using the `list` command.

**Inputs:**

```text
todo read book
todo return book
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 okays task added:
   [T][ ] read book
 you now have 1 tasks in the list, get to it !!
____________________________________________________________
 okays task added:
   [T][ ] return book
 you now have 2 tasks in the list, get to it !!
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[T][ ] read book
 2.[T][ ] return book
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-002: Mark a task as done

**Aim:** Verify that `mark` changes and saves a task's status from incomplete to complete.

**Inputs:**

```text
todo read book
mark 1
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 okays task added:
   [T][ ] read book
 you now have 1 tasks in the list, get to it !!
____________________________________________________________
 yippee task done, i've marked it as so:
   [T][X] read book
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[T][X] read book
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-003: Unmark a completed task

**Aim:** Verify that `unmark` changes and saves a completed task back to incomplete.

**Inputs:**

```text
todo read book
mark 1
unmark 1
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 okays task added:
   [T][ ] read book
 you now have 1 tasks in the list, get to it !!
____________________________________________________________
 yippee task done, i've marked it as so:
   [T][X] read book
____________________________________________________________
 okie, i've marked this task incomplete:
   [T][ ] read book
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[T][ ] read book
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-004: Add a deadline

**Aim:** Verify that a deadline parses an ISO date and displays it in a friendly format.

**Inputs:**

```text
deadline return book /by 2019-12-02
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 okays task added:
   [D][ ] return book (by: Dec 02 2019)
 you now have 1 tasks in the list, get to it !!
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[D][ ] return book (by: Dec 02 2019)
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-005: Add an event

**Aim:** Verify that an event parses typed date-times and displays them in a friendly format.

**Inputs:**

```text
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 okays task added:
   [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
 you now have 1 tasks in the list, get to it !!
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-012: Load saved tasks on startup

**Aim:** Verify that Bob loads todo, deadline, and event tasks, including their completion states, from the data file when it starts.

**Setup:** Create `data/bob.txt` with these contents:

```text
T | 1 | read book

D | 0 | return book | 2019-12-02
E | 1 | project meeting | 2019-12-02 1400 | 2019-12-02 1600
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[T][X] read book
 2.[D][ ] return book (by: Dec 02 2019)
 3.[E][X] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-013: Handle a malformed data file

**Aim:** Verify that Bob reports corrupt saved data without crashing or exposing a partially loaded task list.

**Setup:** Create `data/bob.txt` with these contents:

```text
T | 0 | valid task
X | 0 | unknown task
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 oopsies !! (´ ∀ ` *) couldn't load saved tasks: invalid data on line 2
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-014: Handle a save failure

**Aim:** Verify that Bob reports a disk-write failure, keeps running, and rolls back the unsaved task.

**Setup:** Create a regular file named `data`, preventing creation of `data/bob.txt`.

**Inputs:**

```text
todo read book
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 oopsies !! (´ ∀ ` *) couldn't save your tasks; nothing was changed
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-015: Load escaped separator characters

**Aim:** Verify that pipes and backslashes inside saved task fields are decoded as task text rather than separators.

**Setup:** Create `data/bob.txt` with these contents:

```text
T | 0 | read \| review \\ notes
D | 1 | return \| renew | 2019-12-02
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[T][ ] read | review \ notes
 2.[D][X] return | renew (by: Dec 02 2019)
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-006: Reject malformed deadline input

**Aim:** Verify that a deadline without `/by` is rejected.

**Inputs:**

```text
deadline return book
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 oopsies !! (´ ∀ ` *) a deadline needs /by and a date, eg play /by 2019-12-02
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-007: Reject malformed event input

**Aim:** Verify that an event without `/from` and `/to` is rejected.

**Inputs:**

```text
event project meeting
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 oopsies !! (´ ∀ ` *) an event needs /from and /to, eg event meeting /from 2019-12-02 1400 /to 2019-12-02 1600
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-008: Handle an unknown command

**Aim:** Verify that Bob handles an unrecognized command.

**Inputs:**

```text
blah
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 oopsies !! (´ ∀ ` *) pls try either one of list, find, todo, deadline, event, mark, unmark, delete, or bye
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-011: Delete a task

**Aim:** Verify that `delete` removes and saves the selected task, reports the new count, and renumbers the remaining tasks.

**Inputs:**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo join sports club
todo borrow book
delete 3
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 okays task added:
   [T][ ] read book
 you now have 1 tasks in the list, get to it !!
____________________________________________________________
 okays task added:
   [D][ ] return book (by: Jun 06 2019)
 you now have 2 tasks in the list, get to it !!
____________________________________________________________
 okays task added:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 you now have 3 tasks in the list, get to it !!
____________________________________________________________
 okays task added:
   [T][ ] join sports club
 you now have 4 tasks in the list, get to it !!
____________________________________________________________
 okays task added:
   [T][ ] borrow book
 you now have 5 tasks in the list, get to it !!
____________________________________________________________
 okays here's the task i deleted: 
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 pls get to the remaining 4 tasks in your list
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[T][ ] read book
 2.[D][ ] return book (by: Jun 06 2019)
 3.[T][ ] join sports club
 4.[T][ ] borrow book
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-009: Reject empty task details

**Aim:** Verify that task commands and `find` reject missing required details with specific guidance.

**Inputs:**

```text
todo
deadline /by 2019-12-02
deadline return book /by
event /from 2pm /to 4pm
event meeting /from  /to 4pm
event meeting /from 2pm /to
find
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 oopsies !! (´ ∀ ` *) a todo needs a desc, eg: todo sleep
____________________________________________________________
 oopsies !! (´ ∀ ` *) pls gimme a desc before /by
____________________________________________________________
 oopsies !! (´ ∀ ` *) pls gimme a date after /by
____________________________________________________________
 oopsies !! (´ ∀ ` *) pls gimme event desc before /from.
____________________________________________________________
 oopsies !! (´ ∀ ` *) pls gimme start time after /from.
____________________________________________________________
 oopsies !! (´ ∀ ` *) pls gimme end time after /to.
____________________________________________________________
 oopsies !! (´ ∀ ` *) can't find something if idk what it is
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-010: Reject invalid task numbers

**Aim:** Verify that mark and unmark commands handle missing, non-numeric, and out-of-range task numbers without crashing.

**Inputs:**

```text
mark
mark one
mark 1
todo read book
mark 2
unmark 0
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 oopsies !! (´ ∀ ` *) can't help if idk which task no
____________________________________________________________
 oopsies !! (´ ∀ ` *) enter a valid task no pls
____________________________________________________________
 oopsies !! (´ ∀ ` *) can't do anyth if there's no task
____________________________________________________________
 okays task added:
   [T][ ] read book
 you now have 1 tasks in the list, get to it !!
____________________________________________________________
 oopsies !! (´ ∀ ` *) task doesn't exist, whats your fav no from 1 to 1?
____________________________________________________________
 oopsies !! (´ ∀ ` *) task doesn't exist, whats your fav no from 1 to 1?
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-016: Reject invalid dates and times

**Aim:** Distinguish malformed dates and times from nonexistent dates and times, and reject an event that ends before it starts.

**Inputs:**

```text
deadline return book /by 2/12/2019
deadline return book /by 2026-02-30
event meeting /from 2019-12-02 2pm /to 2019-12-02 1600
event meeting /from 2026-02-30 1400 /to 2026-03-01 1600
event meeting /from 2026-02-28 3300 /to 2026-03-01 1600
event meeting /from 2026-02-28 1400 /to 2026-03-01 3300
event meeting /from 2019-12-02 1800 /to 2019-12-02 1600
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 oopsies !! (´ ∀ ` *) wrong format for date !! here’s an eg: 2019-12-02
____________________________________________________________
 oopsies !! (´ ∀ ` *) date doesn't exist, try again
____________________________________________________________
 oopsies !! (´ ∀ ` *) wrong format !! here’s an eg: 2019-12-02 1800
____________________________________________________________
 oopsies !! (´ ∀ ` *) date or time doesn't exist, try again
____________________________________________________________
 oopsies !! (´ ∀ ` *) date or time doesn't exist, try again
____________________________________________________________
 oopsies !! (´ ∀ ` *) date or time doesn't exist, try again
____________________________________________________________
 oopsies !! (´ ∀ ` *) how can an event end before it starts?
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-017: Find tasks by keyword

**Aim:** Verify that `find` displays only tasks whose descriptions contain the keyword, in their original order.

**Inputs:**

```text
todo read book
deadline return book /by 2019-12-02
todo buy pen
mark 1
find book
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 okays task added:
   [T][ ] read book
 you now have 1 tasks in the list, get to it !!
____________________________________________________________
 okays task added:
   [D][ ] return book (by: Dec 02 2019)
 you now have 2 tasks in the list, get to it !!
____________________________________________________________
 okays task added:
   [T][ ] buy pen
 you now have 3 tasks in the list, get to it !!
____________________________________________________________
 yippee task done, i've marked it as so:
   [T][X] read book
____________________________________________________________
 here's what i found:
 1.[T][X] read book
 2.[D][ ] return book (by: Dec 02 2019)
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-018: Add priorities to all task types

**Aim:** Verify low, moderate, high, and explicit no tag appear correctly.

**Inputs:**

```text
todo read /priority low
deadline submit /by 2026-09-09 /priority moderate
event meeting /from 2026-09-09 1400 /to 2026-09-09 1600 /priority high
todo rest /priority none
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 okays task added:
   [T][ ][priority: low] read
 you now have 1 tasks in the list, get to it !!
____________________________________________________________
 okays task added:
   [D][ ][priority: moderate] submit (by: Sep 09 2026)
 you now have 2 tasks in the list, get to it !!
____________________________________________________________
 okays task added:
   [E][ ][priority: high] meeting (from: Sep 09 2026, 2:00 PM to: Sep 09 2026, 4:00 PM)
 you now have 3 tasks in the list, get to it !!
____________________________________________________________
 okays task added:
   [T][ ] rest
 you now have 4 tasks in the list, get to it !!
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[T][ ][priority: low] read
 2.[D][ ][priority: moderate] submit (by: Sep 09 2026)
 3.[E][ ][priority: high] meeting (from: Sep 09 2026, 2:00 PM to: Sep 09 2026, 4:00 PM)
 4.[T][ ] rest
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-019: Reject invalid priorities

**Aim:** Verify missing, unknown, and duplicate priorities add no tasks.

**Inputs:**

```text
todo read /priority
todo read /priority urgent
todo read /priority high /priority low
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 oopsies !! (´ ∀ ` *) pls end the task with /priority low, moderate, high, or none
____________________________________________________________
 oopsies !! (´ ∀ ` *) pls end the task with /priority low, moderate, high, or none
____________________________________________________________
 oopsies !! (´ ∀ ` *) pls end the task with /priority low, moderate, high, or none
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-020: Load priorities on startup

**Aim:** Verify priority tags and completion states survive restarting alongside legacy untagged tasks.

**Setup:** Create `data/bob.txt` with these contents:

```text
T | 1 | read | high
D | 0 | submit | 2026-09-09 | moderate
E | 0 | meeting | 2026-09-09 1400 | 2026-09-09 1600 | low
T | 0 | rest
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[T][X][priority: high] read
 2.[D][ ][priority: moderate] submit (by: Sep 09 2026)
 3.[E][ ][priority: low] meeting (from: Sep 09 2026, 2:00 PM to: Sep 09 2026, 4:00 PM)
 4.[T][ ] rest
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-021: Reject malformed commands and duplicate tasks

**Aim:** Normalize extra spaces while still rejecting duplicate tasks and invalid dates or markers.

**Inputs:**

```text
  todo read
todo read  book
todo read
todo read
deadline return /by 2026-02-30
event meeting /from 2026-09-16 1500 /to 2026-09-16 1400
deadline return /by 2026-09-16 /by 2026-09-17
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 okays task added:
   [T][ ] read
 you now have 1 tasks in the list, get to it !!
____________________________________________________________
 okays task added:
   [T][ ] read book
 you now have 2 tasks in the list, get to it !!
____________________________________________________________
 oopsies !! (´ ∀ ` *) that task is already in the list
____________________________________________________________
 oopsies !! (´ ∀ ` *) that task is already in the list
____________________________________________________________
 oopsies !! (´ ∀ ` *) date doesn't exist, try again
____________________________________________________________
 oopsies !! (´ ∀ ` *) how can an event end before it starts?
____________________________________________________________
 oopsies !! (´ ∀ ` *) use /by exactly once after the deadline desc
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[T][ ] read
 2.[T][ ] read book
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-022: Reject duplicate saved tasks and prevent overwriting them

**Aim:** Reject duplicate saved tasks and prevent overwriting them.

**Setup:** Create `data/bob.txt` with these contents:

```text
T | 0 | read
T | 1 | read
```

**Inputs:**

```text
todo new
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 oopsies !! (´ ∀ ` *) couldn't load saved tasks: invalid data on line 2
____________________________________________________________
 oopsies !! (´ ∀ ` *) saved tasks could not be loaded; fix the data file before changing tasks
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-023: Reject an incomplete saved task record

**Aim:** Verify that a saved record missing its description reports a data error without crashing or overwriting the file.

**Setup:** Create `data/bob.txt` with these contents:

```text
T | 0
```

**Inputs:**

```text
todo new
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 oopsies !! (´ ∀ ` *) couldn't load saved tasks: invalid data on line 1
____________________________________________________________
 oopsies !! (´ ∀ ` *) saved tasks could not be loaded; fix the data file before changing tasks
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-024: Use full-list numbers from search results

**Aim:** Verify nonconsecutive search results retain full-list numbers for marking, unmarking, and deleting, and refresh those numbers after deletion; an unmatched search shows no tasks.

**Setup:** Create `data/bob.txt` with these contents:

```text
T | 0 | buy milk
T | 0 | read book
T | 0 | buy pen
T | 0 | return book
```

**Inputs:**

```text
find book
mark 2
unmark 2
delete 2
find book
find missing
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 here's what i found:
 2.[T][ ] read book
 4.[T][ ] return book
____________________________________________________________
 yippee task done, i've marked it as so:
   [T][X] read book
____________________________________________________________
 okie, i've marked this task incomplete:
   [T][ ] read book
____________________________________________________________
 okays here's the task i deleted: 
   [T][ ] read book
 pls get to the remaining 3 tasks in your list
____________________________________________________________
 here's what i found:
 3.[T][ ] return book
____________________________________________________________
 here's what i found:
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[T][ ] buy milk
 2.[T][ ] buy pen
 3.[T][ ] return book
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-025: Find a slash in a task description

**Aim:** Verify that a slash is a valid search keyword when it appears in a task description.

**Inputs:**

```text
todo buy groceries tdy/tmr
find /
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 okays task added:
   [T][ ] buy groceries tdy/tmr
 you now have 1 tasks in the list, get to it !!
____________________________________________________________
 here's what i found:
 1.[T][ ] buy groceries tdy/tmr
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-026: Allow an event with equal start and end times

**Aim:** Verify that an event can start and end at the same time.

**Inputs:**

```text
event quick meeting /from 2026-09-18 1400 /to 2026-09-18 1400
list
bye
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 okays task added:
   [E][ ] quick meeting (from: Sep 18 2026, 2:00 PM to: Sep 18 2026, 2:00 PM)
 you now have 1 tasks in the list, get to it !!
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[E][ ] quick meeting (from: Sep 18 2026, 2:00 PM to: Sep 18 2026, 2:00 PM)
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```

## UI-027: Accept extra spaces in valid commands

**Aim:** Verify that leading, trailing, and repeated spaces are accepted and task descriptions are normalized.

**Inputs:**

```text
  todo  play  game  
deadline  submit  report  /by  2026-09-18  
event  meeting  /from  2026-09-18  1400  /to  2026-09-18  1400
find  play  game
list
  bye  
```

**Expected output:**

```text
 ____        _     
| __ )  ___ | |__  
|  _ \ / _ \| '_ \ 
| |_) | (_) | |_) |
|____/ \___/|_.__/ 

hello im bob !!
how can i help :)
____________________________________________________________
 okays task added:
   [T][ ] play game
 you now have 1 tasks in the list, get to it !!
____________________________________________________________
 okays task added:
   [D][ ] submit report (by: Sep 18 2026)
 you now have 2 tasks in the list, get to it !!
____________________________________________________________
 okays task added:
   [E][ ] meeting (from: Sep 18 2026, 2:00 PM to: Sep 18 2026, 2:00 PM)
 you now have 3 tasks in the list, get to it !!
____________________________________________________________
 here's what i found:
 1.[T][ ] play game
____________________________________________________________
 here are your tasks (⌒‿⌒) 加油 !! :
 1.[T][ ] play game
 2.[D][ ] submit report (by: Sep 18 2026)
 3.[E][ ] meeting (from: Sep 18 2026, 2:00 PM to: Sep 18 2026, 2:00 PM)
____________________________________________________________
  yippee glad to have helped (＠＾◡＾)
  byebye !! have a good day (๑˃ᴗ˂)ﻭ
____________________________________________________________
```
