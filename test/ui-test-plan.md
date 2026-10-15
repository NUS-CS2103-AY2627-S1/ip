# Console UI Test Plan

The test runner compiles the Java sources first, then runs each command in a
fresh console session. Expected output is exact apart from line-ending style,
line-end spaces, and one final newline.

## Environment matrix

Run the cases below on each supported operating system: Windows 11 using
PowerShell or Command Prompt, macOS, and Linux. Build the application with the
repository's Java 25 toolchain on every system.

Run the console cases at 80x24, 120x30, and 160x40 columns by rows. Confirm
that prompts, task descriptions, separators, and error messages do not wrap or
overlap at any tested size.

Run the exact-output cases with an English locale first. Repeat the parser and
storage checks with at least one non-English locale, recording any intentional
date-format differences. Locale-sensitive output must be compared against the
documented locale rather than silently accepted as equivalent.

## Manual environment checks

### Unicode and storage round trip

Run the application with a clean `data/dingleberry.txt` and enter a todo whose
description contains accented letters, CJK text, an emoji, a pipe, a backslash,
and a line break if the console supports it. Use `list`, exit, then run the
application again and use `list`. Confirm the description is displayed in the
same order and with the same characters in both sessions. Also run
`StorageTest` to verify the UTF-8 and escaped-record assertions without relying
on console encoding.

### Storage failure recovery

Before the check, copy `data/dingleberry.txt` to a temporary backup. Replace
the `data` directory with a regular file, start the application, and enter
`list` followed by `bye`. Confirm that startup reports a loading error and the
application still reaches the empty task list and goodbye message. Restore the
directory and data file in a cleanup step even when the check fails. Then run
the storage failure tests and confirm no existing data is replaced after a
save failure.

## Case: add and list task types

### Aim
Verify that todo, deadline, and event commands are added and displayed in list order.

The natural-date output in this case is resolved using the system date. The
expected output below is for 2026-09-08; update the resolved dates when running
this exact-output case on another date. The parser regression tests are
date-independent and cover the relative-date rules with a fixed reference date.

### Command
```powershell
java -cp build\classes dingleberry.Dingleberry
```

### Input
```
todo borrow book
list
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output
```
____________________________________________________________
 ____  _             _      _                          
|  _ \(_)_ __   __ _| | ___| |__   ___ _ __ _ __ _   _ 
| | | | | '_ \ / _` | |/ _ \ '_ \ / _ \ '__| '__| | | |
| |_| | | | | | (_| | |  __/ |_) |  __/ |  | |  | |_| |
|____/|_|_| |_|\__, |_|\___|_.__/ \___|_|  |_|   \__, |
               |___/                              |___|

Hey There! I'm Dingleberry
What can I do for you?
____________________________________________________________

____________________________________________________________
Okie dokie! I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Okie, here are your tasks. I counted them twice!
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Okie dokie! I've added this task:
  [D][ ] return book (by: Sep 13 2026, 12:00 am)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Okie dokie! I've added this task:
  [E][ ] project meeting (from: Sep 14 2026, 2:00 pm to: Sep 14 2026, 4:00 pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Okie, here are your tasks. I counted them twice!
1.[T][ ] borrow book
2.[D][ ] return book (by: Sep 13 2026, 12:00 am)
3.[E][ ] project meeting (from: Sep 14 2026, 2:00 pm to: Sep 14 2026, 4:00 pm)
____________________________________________________________
Bya hope to see your berries again!
```

## Case: reject unsupported commands

### Aim
Verify that unsupported commands are rejected and the chatbot continues accepting valid commands.

### Command
```powershell
java -cp build\classes dingleberry.Dingleberry
```

### Input
```
todo write report
archive 1
list
bye
```

### Expected output
```
____________________________________________________________
 ____  _             _      _                          
|  _ \(_)_ __   __ _| | ___| |__   ___ _ __ _ __ _   _ 
| | | | | '_ \ / _` | |/ _ \ '_ \ / _ \ '__| '__| | | |
| |_| | | | | | (_| | |  __/ |_) |  __/ |  | |  | |_| |
|____/|_|_| |_|\__, |_|\___|_.__/ \___|_|  |_|   \__, |
               |___/                              |___|

Hey There! I'm Dingleberry
What can I do for you?
____________________________________________________________

____________________________________________________________
Okie dokie! I've added this task:
  [T][ ] write report
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Woops, Dingleberry doesn't know that command: I don't recognize that command. Use 'todo', 'list', 'mark', 'unmark', 'delete', 'deadline', or 'event'.
Please try again with the correct command and parameters.
____________________________________________________________
____________________________________________________________
Okie, here are your tasks. I counted them twice!
1.[T][ ] write report
____________________________________________________________
Bya hope to see your berries again!
```

## Case: mark a task as done

### Aim
Verify that the mark command marks the given task as done and that list reflects the updated status.

### Command
```powershell
java -cp build\classes dingleberry.Dingleberry
```

### Input
```
todo write report
mark 1
list
bye
```

### Expected output
```
____________________________________________________________
 ____  _             _      _                          
|  _ \(_)_ __   __ _| | ___| |__   ___ _ __ _ __ _   _ 
| | | | | '_ \ / _` | |/ _ \ '_ \ / _ \ '__| '__| | | |
| |_| | | | | | | (_| | |  __/ |_) |  __/ |  | |  | |_| |
|____/|_|_| |_|\__, |_|\___|_.__/ \___|_|  |_|   \__, |
               |___/                              |___|

Hey There! I'm Dingleberry
What can I do for you?
____________________________________________________________

____________________________________________________________
Okie dokie! I've added this task:
  [T][ ] write report
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Yay! I've marked this task as done. I think:
  [T][X] write report
____________________________________________________________
____________________________________________________________
Okie, here are your tasks. I counted them twice!
1.[T][X] write report
____________________________________________________________
Bya hope to see your berries again!
```

## Case: unmark a task

### Aim
Verify that the unmark command marks a done task as not done and that list reflects the updated status.

### Command
```powershell
java -cp build\classes dingleberry.Dingleberry
```

### Input
```
todo write report
mark 1
unmark 1
list
bye
```

### Expected output
```
____________________________________________________________
 ____  _             _      _                          
|  _ \(_)_ __   __ _| | ___| |__   ___ _ __ _ __ _   _ 
| | | | | '_ \ / _` | |/ _ \ '_ \ / _ \ '__| '__| | | |
| |_| | | | | | | (_| | |  __/ |_) |  __/ |  | |  | |_| |
|____/|_|_| |_|\__, |_|\___|_.__/ \___|_|  |_|   \__, |
               |___/                              |___|

Hey There! I'm Dingleberry
What can I do for you?
____________________________________________________________

____________________________________________________________
Okie dokie! I've added this task:
  [T][ ] write report
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Yay! I've marked this task as done. I think:
  [T][X] write report
____________________________________________________________
____________________________________________________________
Okie, undoing that little oops:
  [T][ ] write report
____________________________________________________________
____________________________________________________________
Okie, here are your tasks. I counted them twice!
1.[T][ ] write report
____________________________________________________________
Bya hope to see your berries again!
```