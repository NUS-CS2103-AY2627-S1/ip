# UI Test Plan

## Configuration

- Java version: `25`
- Source directory: `src/main/java`
- Main class: `shrekanddonkey.ShrekAndDonkey`
- Timeout seconds: `10`
- Comparison: Exact standard output after normalizing CRLF and LF line endings. Spaces, blank lines, punctuation, and the final newline remain significant.

## Test Cases

### TC-DELETE-006: Delete first, last, and only tasks

**Aim:** Verify task output remains unchanged while adding, deleting, and listing tasks.

**Inputs:**
```text
todo first
todo second
todo third
delete 1
delete 2
delete 1
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ____  _              _        _              _ ____              _
/ ___|| |__  _ __ ___| | __   / \   _ __   __| |  _ \  ___  _ __ | | _____ _   _
\___ \| '_ \| '__/ _ \ |/ /  / _ \ | '_ \ / _` | | | |/ _ \| '_ \| |/ / _ \ | | |
 ___) | | | | | |  __/   <  / ___ \| | | | (_| | |_| | (_) | | | |   <  __/ |_| |
|____/|_| |_|_|  \___|_|\_\/_/   \_\_| |_|\__,_|____/ \___/|_| |_|_|\_\___|\__, |
                                                                            |___/
Grrr GET OUT OF MY SWAMP! Ohh, I didn't see you there stranger! I'm ShrekAndDonkey.
What can I do for you?
____________________________________________________________
 Alright, added to the swamp list:
   [T][ ] first
 Now ye have 1 tasks in the swamp.
____________________________________________________________
 Alright, added to the swamp list:
   [T][ ] second
 Now ye have 2 tasks in the swamp.
____________________________________________________________
 Alright, added to the swamp list:
   [T][ ] third
 Now ye have 3 tasks in the swamp.
____________________________________________________________
 Poof! Banished from the swamp:
   [T][ ] first
 Now ye have 2 tasks in the swamp.
____________________________________________________________
 Poof! Banished from the swamp:
   [T][ ] third
 Now ye have 1 tasks in the swamp.
____________________________________________________________
 Poof! Banished from the swamp:
   [T][ ] second
 Now ye have 0 tasks in the swamp.
____________________________________________________________
 Here's what's lurkin' in the swamp:
____________________________________________________________
____________________________________________________________
FEE FIE FOE FUMP. GET OUT OF MY SWAMP!
____________________________________________________________
```

### TC-DELETE-007: Reject invalid delete arguments

**Aim:** Verify validation messages are displayed without terminating the chatbot.

**Inputs:**
```text
delete abc
delete 1
bye
```

**Expected output:**
```text
____________________________________________________________
 ____  _              _        _              _ ____              _
/ ___|| |__  _ __ ___| | __   / \   _ __   __| |  _ \  ___  _ __ | | _____ _   _
\___ \| '_ \| '__/ _ \ |/ /  / _ \ | '_ \ / _` | | | |/ _ \| '_ \| |/ / _ \ | | |
 ___) | | | | | |  __/   <  / ___ \| | | | (_| | |_| | (_) | | | |   <  __/ |_| |
|____/|_| |_|_|  \___|_|\_\/_/   \_\_| |_|\__,_|____/ \___/|_| |_|_|\_\___|\__, |
                                                                            |___/
Grrr GET OUT OF MY SWAMP! Ohh, I didn't see you there stranger! I'm ShrekAndDonkey.
What can I do for you?
____________________________________________________________
 That's not a number! Use: delete N
____________________________________________________________
 Pick a number from 1 to 0, not that hard!
____________________________________________________________
____________________________________________________________
FEE FIE FOE FUMP. GET OUT OF MY SWAMP!
____________________________________________________________
```

### TC-FIND-001: Find tasks matching keyword

**Aim:** Verify find command lists tasks whose descriptions match the given keyword.

**Inputs:**
```text
todo read book
todo return book
todo buy movie
mark 1
mark 2
find book
find movie
find dessert
bye
```

**Expected output:**
```text
____________________________________________________________
 ____  _              _        _              _ ____              _
/ ___|| |__  _ __ ___| | __   / \   _ __   __| |  _ \  ___  _ __ | | _____ _   _
\___ \| '_ \| '__/ _ \ |/ /  / _ \ | '_ \ / _` | | | |/ _ \| '_ \| |/ / _ \ | | |
 ___) | | | | | |  __/   <  / ___ \| | | | (_| | |_| | (_) | | | |   <  __/ |_| |
|____/|_| |_|_|  \___|_|\_\/_/   \_\_| |_|\__,_|____/ \___/|_| |_|_|\_\___|\__, |
                                                                            |___/
Grrr GET OUT OF MY SWAMP! Ohh, I didn't see you there stranger! I'm ShrekAndDonkey.
What can I do for you?
____________________________________________________________
 Alright, added to the swamp list:
   [T][ ] read book
 Now ye have 1 tasks in the swamp.
____________________________________________________________
 Alright, added to the swamp list:
   [T][ ] return book
 Now ye have 2 tasks in the swamp.
____________________________________________________________
 Alright, added to the swamp list:
   [T][ ] buy movie
 Now ye have 3 tasks in the swamp.
____________________________________________________________
 Shrek approves! Marked as done:
   [T][X] read book
____________________________________________________________
 Shrek approves! Marked as done:
   [T][X] return book
____________________________________________________________
 Donkey found these in the swamp:
 1.[T][X] read book
 2.[T][X] return book
____________________________________________________________
 Donkey found these in the swamp:
 1.[T][ ] buy movie
____________________________________________________________
 Donkey found these in the swamp:
____________________________________________________________
____________________________________________________________
FEE FIE FOE FUMP. GET OUT OF MY SWAMP!
____________________________________________________________
```
