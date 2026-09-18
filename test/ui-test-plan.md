# Console UI Test Plan

## Test environment

- **Java version:** 25
- **Build command:** `$consoleSources = Get-ChildItem -Recurse -Filter *.java -Path src/main/java | Where-Object { $_.FullName -notmatch '\\bobby\\gui\\' -and $_.Name -ne 'Launcher.java' } | ForEach-Object FullName; javac --release 25 -d out/date-time-verification $consoleSources`
- **Launch convention:** Run from the repository root with `java -cp out/date-time-verification bobby.Bobby`.
- **Comparison rule:** Exact output match after line-ending normalization, unless a test case explicitly states another deterministic rule.

## Test cases

Each case starts a fresh program process.

## T01 — task and mark errors preserve state

**Aim:** A valid to-do remains unchanged after invalid to-do, unknown-command, and invalid mark/unmark inputs.

**Inputs:**

```text
todo read book
todo
list
blah
list
mark 1
mark 0
list
unmark 1
unmark 2
list
bye
```

**Command:**

```powershell
Remove-Item data/bobby.txt -ErrorAction Ignore; $consoleSources = Get-ChildItem -Recurse -Filter *.java -Path src/main/java | Where-Object { $_.FullName -notmatch '\\bobby\\gui\\' -and $_.Name -ne 'Launcher.java' } | ForEach-Object FullName; javac --release 25 -d out/date-time-verification $consoleSources; "todo read book", "todo", "list", "blah", "list", "mark 1", "mark 0", "list", "unmark 1", "unmark 2", "list", "bye" | java -cp out/date-time-verification bobby.Bobby
```

**Expected output:**

```text
____________________________________________________________
BBBB   OOO   BBBB  BBBB  Y   Y
B   B O   O  B   B B   B  Y Y
BBBB  O   O  BBBB  BBBB    Y
B   B O   O  B   B B   B   Y
BBBB   OOO   BBBB  BBBB    Y
____________________________________________________________
     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.
     What charge wouldst thou have me enter, amend, or proclaim?
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [T][ ] read book
     One duty now standeth upon the register.
____________________________________________________________
____________________________________________________________
     Thy decree containeth no duty to inscribe. Pray use: todo DESCRIPTION.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][ ] read book
____________________________________________________________
____________________________________________________________
     Prithee, forgive this humble steward, for thy decree exceedeth my understanding. I beseech thee, employ one of the appointed commands.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][ ] read book
____________________________________________________________
____________________________________________________________
     Most excellent. I have proclaimed this duty duly accomplished:
       [T][X] read book
____________________________________________________________
____________________________________________________________
     The number thou hast named correspondeth to no duty presently held within the register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][X] read book
____________________________________________________________
____________________________________________________________
     As thou commandest. I have restored this duty to the ranks of unfinished business:
       [T][ ] read book
____________________________________________________________
____________________________________________________________
     The number thou hast named correspondeth to no duty presently held within the register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][ ] read book
____________________________________________________________
____________________________________________________________
     I humbly take my leave. May good fortune attend thee until next we meet.
____________________________________________________________
```

## T02 — deadline and event errors preserve state

**Aim:** Malformed date input and incomplete deadline/event commands do not alter the tasks added by valid commands.

**Inputs:**

```text
deadline submit /by Friday
deadline
list
event meeting /from 2019-10-15 0900 /to 2019-10-15 1000
event coffee /from
list
bye
```

**Command:**

```powershell
Remove-Item data/bobby.txt -ErrorAction Ignore; $consoleSources = Get-ChildItem -Recurse -Filter *.java -Path src/main/java | Where-Object { $_.FullName -notmatch '\\bobby\\gui\\' -and $_.Name -ne 'Launcher.java' } | ForEach-Object FullName; javac --release 25 -d out/date-time-verification $consoleSources; "deadline submit /by Friday", "deadline", "list", "event meeting /from 2019-10-15 0900 /to 2019-10-15 1000", "event coffee /from", "list", "bye" | java -cp out/date-time-verification bobby.Bobby
```

**Expected output:**

```text
____________________________________________________________
BBBB   OOO   BBBB  BBBB  Y   Y
B   B O   O  B   B B   B  Y Y
BBBB  O   O  BBBB  BBBB    Y
B   B O   O  B   B B   B   Y
BBBB   OOO   BBBB  BBBB    Y
____________________________________________________________
     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.
     What charge wouldst thou have me enter, amend, or proclaim?
____________________________________________________________
____________________________________________________________
     The appointed date and hour are not in an acceptable form. Pray employ YYYY-MM-DD HHMM.
____________________________________________________________
____________________________________________________________
     Thy decree must take precisely this form: deadline DESCRIPTION /by YYYY-MM-DD HHMM.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
The royal register standeth presently unburdened; no duty hath yet been inscribed.
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [E][ ] meeting (commencing: Oct 15 2019 9:00 AM; concluding: Oct 15 2019 10:00 AM)
     One duty now standeth upon the register.
____________________________________________________________
____________________________________________________________
     Thy decree must take precisely this form: event DESCRIPTION /from YYYY-MM-DD HHMM /to YYYY-MM-DD HHMM.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[E][ ] meeting (commencing: Oct 15 2019 9:00 AM; concluding: Oct 15 2019 10:00 AM)
____________________________________________________________
____________________________________________________________
     I humbly take my leave. May good fortune attend thee until next we meet.
____________________________________________________________
```

## T03 — mixed valid workflow and blank input

**Aim:** Whitespace and case-normalised valid commands work, while a blank input does not affect subsequent task state changes.

**Inputs:**

```text
  TODO Walk dog  
   
list
deadline pay bills /by 2019-10-15 1800
event project /from 2019-10-16 0900 /to 2019-10-16 1000
unmark 1
mark 3
list
bye
```

**Command:**

```powershell
Remove-Item data/bobby.txt -ErrorAction Ignore; $consoleSources = Get-ChildItem -Recurse -Filter *.java -Path src/main/java | Where-Object { $_.FullName -notmatch '\\bobby\\gui\\' -and $_.Name -ne 'Launcher.java' } | ForEach-Object FullName; javac --release 25 -d out/date-time-verification $consoleSources; "  TODO Walk dog  ", "   ", "list", "deadline pay bills /by 2019-10-15 1800", "event project /from 2019-10-16 0900 /to 2019-10-16 1000", "unmark 1", "mark 3", "list", "bye" | java -cp out/date-time-verification bobby.Bobby
```

**Expected output:**

```text
____________________________________________________________
BBBB   OOO   BBBB  BBBB  Y   Y
B   B O   O  B   B B   B  Y Y
BBBB  O   O  BBBB  BBBB    Y
B   B O   O  B   B B   B   Y
BBBB   OOO   BBBB  BBBB    Y
____________________________________________________________
     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.
     What charge wouldst thou have me enter, amend, or proclaim?
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [T][ ] Walk dog
     One duty now standeth upon the register.
____________________________________________________________
____________________________________________________________
     Prithee, forgive this humble steward, for thy decree exceedeth my understanding. I beseech thee, employ one of the appointed commands.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][ ] Walk dog
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [D][ ] pay bills (appointed for: Oct 15 2019 6:00 PM)
     There now stand 2 duties upon the register.
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [E][ ] project (commencing: Oct 16 2019 9:00 AM; concluding: Oct 16 2019 10:00 AM)
     There now stand 3 duties upon the register.
____________________________________________________________
____________________________________________________________
     As thou commandest. I have restored this duty to the ranks of unfinished business:
       [T][ ] Walk dog
____________________________________________________________
____________________________________________________________
     Most excellent. I have proclaimed this duty duly accomplished:
       [E][X] project (commencing: Oct 16 2019 9:00 AM; concluding: Oct 16 2019 10:00 AM)
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][ ] Walk dog
     2.[D][ ] pay bills (appointed for: Oct 15 2019 6:00 PM)
     3.[E][X] project (commencing: Oct 16 2019 9:00 AM; concluding: Oct 16 2019 10:00 AM)
____________________________________________________________
____________________________________________________________
     I humbly take my leave. May good fortune attend thee until next we meet.
____________________________________________________________
```

## T04 — delete task and reindex remaining tasks

**Aim:** Deleting a valid task removes it, preserves the other tasks, and reindexes the displayed list.

**Inputs:**

```text
todo read book
deadline return book /by 2019-06-06 1800
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
mark 1
mark 2
delete 3
list
bye
```

**Command:**

```powershell
Remove-Item data/bobby.txt -ErrorAction Ignore; $consoleSources = Get-ChildItem -Recurse -Filter *.java -Path src/main/java | Where-Object { $_.FullName -notmatch '\\bobby\\gui\\' -and $_.Name -ne 'Launcher.java' } | ForEach-Object FullName; javac --release 25 -d out/date-time-verification $consoleSources; "todo read book", "deadline return book /by 2019-06-06 1800", "event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600", "mark 1", "mark 2", "delete 3", "list", "bye" | java -cp out/date-time-verification bobby.Bobby
```

**Expected output:**

```text
____________________________________________________________
BBBB   OOO   BBBB  BBBB  Y   Y
B   B O   O  B   B B   B  Y Y
BBBB  O   O  BBBB  BBBB    Y
B   B O   O  B   B B   B   Y
BBBB   OOO   BBBB  BBBB    Y
____________________________________________________________
     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.
     What charge wouldst thou have me enter, amend, or proclaim?
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [T][ ] read book
     One duty now standeth upon the register.
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [D][ ] return book (appointed for: Jun 06 2019 6:00 PM)
     There now stand 2 duties upon the register.
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [E][ ] project meeting (commencing: Aug 06 2019 2:00 PM; concluding: Aug 06 2019 4:00 PM)
     There now stand 3 duties upon the register.
____________________________________________________________
____________________________________________________________
     Most excellent. I have proclaimed this duty duly accomplished:
       [T][X] read book
____________________________________________________________
____________________________________________________________
     Most excellent. I have proclaimed this duty duly accomplished:
       [D][X] return book (appointed for: Jun 06 2019 6:00 PM)
____________________________________________________________
____________________________________________________________
     It is done. I have struck this duty from the royal register:
       [E][ ] project meeting (commencing: Aug 06 2019 2:00 PM; concluding: Aug 06 2019 4:00 PM)
     There now remain 2 duties upon the register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][X] read book
     2.[D][X] return book (appointed for: Jun 06 2019 6:00 PM)
____________________________________________________________
____________________________________________________________
     I humbly take my leave. May good fortune attend thee until next we meet.
____________________________________________________________
```

## T05 — delete errors preserve tasks between valid deletions

**Aim:** Valid deletions reindex the task list, while non-numeric, out-of-range, and zero task numbers leave the current tasks unchanged.

**Inputs:**

```text
todo alpha
todo beta
todo gamma
delete 2
list
delete 3
list
delete two
list
delete 2
list
delete 0
list
bye
```

**Command:**

```powershell
Remove-Item data/bobby.txt -ErrorAction Ignore; $consoleSources = Get-ChildItem -Recurse -Filter *.java -Path src/main/java | Where-Object { $_.FullName -notmatch '\\bobby\\gui\\' -and $_.Name -ne 'Launcher.java' } | ForEach-Object FullName; javac --release 25 -d out/date-time-verification $consoleSources; "todo alpha", "todo beta", "todo gamma", "delete 2", "list", "delete 3", "list", "delete two", "list", "delete 2", "list", "delete 0", "list", "bye" | java -cp out/date-time-verification bobby.Bobby
```

**Expected output:**

```text
____________________________________________________________
BBBB   OOO   BBBB  BBBB  Y   Y
B   B O   O  B   B B   B  Y Y
BBBB  O   O  BBBB  BBBB    Y
B   B O   O  B   B B   B   Y
BBBB   OOO   BBBB  BBBB    Y
____________________________________________________________
     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.
     What charge wouldst thou have me enter, amend, or proclaim?
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [T][ ] alpha
     One duty now standeth upon the register.
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [T][ ] beta
     There now stand 2 duties upon the register.
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [T][ ] gamma
     There now stand 3 duties upon the register.
____________________________________________________________
____________________________________________________________
     It is done. I have struck this duty from the royal register:
       [T][ ] beta
     There now remain 2 duties upon the register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][ ] alpha
     2.[T][ ] gamma
____________________________________________________________
____________________________________________________________
     The number thou hast named correspondeth to no duty presently held within the register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][ ] alpha
     2.[T][ ] gamma
____________________________________________________________
____________________________________________________________
     The number thou hast named correspondeth to no duty presently held within the register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][ ] alpha
     2.[T][ ] gamma
____________________________________________________________
____________________________________________________________
     It is done. I have struck this duty from the royal register:
       [T][ ] gamma
     One duty now remaineth upon the register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][ ] alpha
____________________________________________________________
____________________________________________________________
     The number thou hast named correspondeth to no duty presently held within the register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][ ] alpha
____________________________________________________________
____________________________________________________________
     I humbly take my leave. May good fortune attend thee until next we meet.
____________________________________________________________
```

## T06 — delete command errors on an empty list

**Aim:** Missing and invalid task numbers, including deletion from an empty list, do not change the list before or after a valid deletion.

**Inputs:**

```text
delete 1
list
todo only task
delete
list
delete 2
list
DELETE 1
list
delete 1
list
bye
```

**Command:**

```powershell
Remove-Item data/bobby.txt -ErrorAction Ignore; $consoleSources = Get-ChildItem -Recurse -Filter *.java -Path src/main/java | Where-Object { $_.FullName -notmatch '\\bobby\\gui\\' -and $_.Name -ne 'Launcher.java' } | ForEach-Object FullName; javac --release 25 -d out/date-time-verification $consoleSources; "delete 1", "list", "todo only task", "delete", "list", "delete 2", "list", "DELETE 1", "list", "delete 1", "list", "bye" | java -cp out/date-time-verification bobby.Bobby
```

**Expected output:**

```text
____________________________________________________________
BBBB   OOO   BBBB  BBBB  Y   Y
B   B O   O  B   B B   B  Y Y
BBBB  O   O  BBBB  BBBB    Y
B   B O   O  B   B B   B   Y
BBBB   OOO   BBBB  BBBB    Y
____________________________________________________________
     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.
     What charge wouldst thou have me enter, amend, or proclaim?
____________________________________________________________
____________________________________________________________
     The number thou hast named correspondeth to no duty presently held within the register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
The royal register standeth presently unburdened; no duty hath yet been inscribed.
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [T][ ] only task
     One duty now standeth upon the register.
____________________________________________________________
____________________________________________________________
     The number thou hast named correspondeth to no duty presently held within the register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][ ] only task
____________________________________________________________
____________________________________________________________
     The number thou hast named correspondeth to no duty presently held within the register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][ ] only task
____________________________________________________________
____________________________________________________________
     It is done. I have struck this duty from the royal register:
       [T][ ] only task
     The register now standeth empty.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
The royal register standeth presently unburdened; no duty hath yet been inscribed.
____________________________________________________________
____________________________________________________________
     The number thou hast named correspondeth to no duty presently held within the register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
The royal register standeth presently unburdened; no duty hath yet been inscribed.
____________________________________________________________
____________________________________________________________
     I humbly take my leave. May good fortune attend thee until next we meet.
____________________________________________________________
```

## T07 — task-list changes are saved to disk

**Aim:** Adding, marking, unmarking, and deleting tasks automatically replaces `data/bobby.txt` with the current task list in the specified save format.

**Inputs:**

```text
todo read book
deadline return book /by 2019-06-06 1800
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
mark 2
unmark 2
delete 1
bye
```

**Command:**

```powershell
Remove-Item data/bobby.txt -ErrorAction Ignore; $consoleSources = Get-ChildItem -Recurse -Filter *.java -Path src/main/java | Where-Object { $_.FullName -notmatch '\\bobby\\gui\\' -and $_.Name -ne 'Launcher.java' } | ForEach-Object FullName; javac --release 25 -d out/date-time-verification $consoleSources; "todo read book", "deadline return book /by 2019-06-06 1800", "event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600", "mark 2", "unmark 2", "delete 1", "bye" | java -cp out/date-time-verification bobby.Bobby; Get-Content data/bobby.txt
```

**Expected output:**

```text
____________________________________________________________
BBBB   OOO   BBBB  BBBB  Y   Y
B   B O   O  B   B B   B  Y Y
BBBB  O   O  BBBB  BBBB    Y
B   B O   O  B   B B   B   Y
BBBB   OOO   BBBB  BBBB    Y
____________________________________________________________
     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.
     What charge wouldst thou have me enter, amend, or proclaim?
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [T][ ] read book
     One duty now standeth upon the register.
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [D][ ] return book (appointed for: Jun 06 2019 6:00 PM)
     There now stand 2 duties upon the register.
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [E][ ] project meeting (commencing: Aug 06 2019 2:00 PM; concluding: Aug 06 2019 4:00 PM)
     There now stand 3 duties upon the register.
____________________________________________________________
____________________________________________________________
     Most excellent. I have proclaimed this duty duly accomplished:
       [D][X] return book (appointed for: Jun 06 2019 6:00 PM)
____________________________________________________________
____________________________________________________________
     As thou commandest. I have restored this duty to the ranks of unfinished business:
       [D][ ] return book (appointed for: Jun 06 2019 6:00 PM)
____________________________________________________________
____________________________________________________________
     It is done. I have struck this duty from the royal register:
       [T][ ] read book
     There now remain 2 duties upon the register.
____________________________________________________________
____________________________________________________________
     I humbly take my leave. May good fortune attend thee until next we meet.
____________________________________________________________
D | 0 | return book | 2019-06-06T18:00 | -
E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00 | -
```

## T08 — saved tasks load when the application starts

**Aim:** A valid legacy save file restores to-do, deadline, and event tasks with their saved completion states before the first command is processed.

**Inputs:**

```text
list
bye
```

**Command:**

```powershell
$ProgressPreference = 'SilentlyContinue'; New-Item -ItemType Directory -Force data | Out-Null; "T | 1 | read book", "D | 0 | return book | 2019-06-06T18:00", "E | 1 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00" | Set-Content data/bobby.txt; $consoleSources = Get-ChildItem -Recurse -Filter *.java -Path src/main/java | Where-Object { $_.FullName -notmatch '\\bobby\\gui\\' -and $_.Name -ne 'Launcher.java' } | ForEach-Object FullName; javac --release 25 -d out/date-time-verification $consoleSources; "list", "bye" | java -cp out/date-time-verification bobby.Bobby
```

**Expected output:**

```text
____________________________________________________________
BBBB   OOO   BBBB  BBBB  Y   Y
B   B O   O  B   B B   B  Y Y
BBBB  O   O  BBBB  BBBB    Y
B   B O   O  B   B B   B   Y
BBBB   OOO   BBBB  BBBB    Y
____________________________________________________________
     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.
     What charge wouldst thou have me enter, amend, or proclaim?
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][X] read book
     2.[D][ ] return book (appointed for: Jun 06 2019 6:00 PM)
     3.[E][X] project meeting (commencing: Aug 06 2019 2:00 PM; concluding: Aug 06 2019 4:00 PM)
____________________________________________________________
____________________________________________________________
     I humbly take my leave. May good fortune attend thee until next we meet.
____________________________________________________________
```

## T09 — invalid saved data starts with an empty list

**Aim:** A malformed save record produces a clear error and does not load a partial or invalid task list.

**Inputs:**

```text
list
bye
```

**Command:**

```powershell
$ProgressPreference = 'SilentlyContinue'; New-Item -ItemType Directory -Force data | Out-Null; "D | 1 | missing deadline" | Set-Content data/bobby.txt; $consoleSources = Get-ChildItem -Recurse -Filter *.java -Path src/main/java | Where-Object { $_.FullName -notmatch '\\bobby\\gui\\' -and $_.Name -ne 'Launcher.java' } | ForEach-Object FullName; javac --release 25 -d out/date-time-verification $consoleSources; "list", "bye" | java -cp out/date-time-verification bobby.Bobby
```

**Expected output:**

```text
____________________________________________________________
BBBB   OOO   BBBB  BBBB  Y   Y
B   B O   O  B   B B   B  Y Y
BBBB  O   O  BBBB  BBBB    Y
B   B O   O  B   B B   B   Y
BBBB   OOO   BBBB  BBBB    Y
____________________________________________________________
     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.
     What charge wouldst thou have me enter, amend, or proclaim?
____________________________________________________________
____________________________________________________________
     Regrettably, the saved register could not be read.
     I shall therefore commence with an empty register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
The royal register standeth presently unburdened; no duty hath yet been inscribed.
____________________________________________________________
____________________________________________________________
     I humbly take my leave. May good fortune attend thee until next we meet.
____________________________________________________________
```

## T10 — find tasks by description keyword

**Aim:** A keyword search displays all matching task descriptions in their original order, including different task types and completion states.

**Inputs:**

```text
todo read book
deadline return book /by 2019-06-06 1800
todo buy groceries
mark 1
mark 2
find book
bye
```

**Command:**

```powershell
Remove-Item data/bobby.txt -ErrorAction Ignore; $consoleSources = Get-ChildItem -Recurse -Filter *.java -Path src/main/java | Where-Object { $_.FullName -notmatch '\\bobby\\gui\\' -and $_.Name -ne 'Launcher.java' } | ForEach-Object FullName; javac --release 25 -d out/date-time-verification $consoleSources; "todo read book", "deadline return book /by 2019-06-06 1800", "todo buy groceries", "mark 1", "mark 2", "find book", "bye" | java -cp out/date-time-verification bobby.Bobby
```

**Expected output:**

```text
____________________________________________________________
BBBB   OOO   BBBB  BBBB  Y   Y
B   B O   O  B   B B   B  Y Y
BBBB  O   O  BBBB  BBBB    Y
B   B O   O  B   B B   B   Y
BBBB   OOO   BBBB  BBBB    Y
____________________________________________________________
     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.
     What charge wouldst thou have me enter, amend, or proclaim?
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [T][ ] read book
     One duty now standeth upon the register.
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [D][ ] return book (appointed for: Jun 06 2019 6:00 PM)
     There now stand 2 duties upon the register.
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [T][ ] buy groceries
     There now stand 3 duties upon the register.
____________________________________________________________
____________________________________________________________
     Most excellent. I have proclaimed this duty duly accomplished:
       [T][X] read book
____________________________________________________________
____________________________________________________________
     Most excellent. I have proclaimed this duty duly accomplished:
       [D][X] return book (appointed for: Jun 06 2019 6:00 PM)
____________________________________________________________
____________________________________________________________
Behold, the duties answering thy inquiry:
     1.[T][X] read book
     2.[D][X] return book (appointed for: Jun 06 2019 6:00 PM)
____________________________________________________________
____________________________________________________________
     I humbly take my leave. May good fortune attend thee until next we meet.
____________________________________________________________
```

## T11 — show current-week task statistics

**Aim:** The statistics command reports the current Monday-to-Sunday period and summarizes completed and pending tasks without listing task descriptions.

**Inputs:**

```text
todo read book
todo buy groceries
mark 1
stats
bye
```

**Command:**

```powershell
Remove-Item data/bobby.txt -ErrorAction Ignore; $consoleSources = Get-ChildItem -Recurse -Filter *.java -Path src/main/java | Where-Object { $_.FullName -notmatch '\\bobby\\gui\\' -and $_.Name -ne 'Launcher.java' } | ForEach-Object FullName; javac --release 25 -d out/date-time-verification $consoleSources; "todo read book", "todo buy groceries", "mark 1", "stats", "bye" | java -cp out/date-time-verification bobby.Bobby
```

**Comparison rule:** Verify that the actual `Period under review` dates are the Monday and Sunday containing the local
execution date. Then replace that line with the placeholder shown below before applying the standard exact-output
comparison.

**Expected output:**

```text
____________________________________________________________
BBBB   OOO   BBBB  BBBB  Y   Y
B   B O   O  B   B B   B  Y Y
BBBB  O   O  BBBB  BBBB    Y
B   B O   O  B   B B   B   Y
BBBB   OOO   BBBB  BBBB    Y
____________________________________________________________
     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.
     What charge wouldst thou have me enter, amend, or proclaim?
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [T][ ] read book
     One duty now standeth upon the register.
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [T][ ] buy groceries
     There now stand 2 duties upon the register.
____________________________________________________________
____________________________________________________________
     Most excellent. I have proclaimed this duty duly accomplished:
       [T][X] read book
____________________________________________________________
____________________________________________________________
Attend now to the formal reckoning of thy duties:
     Period under review: <CURRENT_WEEK>
     Accomplished within the present week: 1
     Accomplished across all recorded time: 1
     Yet awaiting fulfilment: 1
     Total duties inscribed: 2
____________________________________________________________
____________________________________________________________
     I humbly take my leave. May good fortune attend thee until next we meet.
____________________________________________________________
```

## T12 — reject malformed and inconsistent task data

**Aim:** Flexible whitespace is normalized, while duplicate tasks, repeated parameters, impossible dates,
invalid event ranges, reserved description characters, and malformed task numbers are rejected without
changing the task list.

**Inputs:**

```text
   todo    read   book
todo READ BOOK
deadline report /by 2026-02-30 1200
deadline report /by 2026-09-20 1200 /by 2026-09-21 1200
event meeting /from 2026-09-20 1200 /to 2026-09-20 1200
todo unsafe | description
mark +1
list
bye
```

**Command:**

```powershell
Remove-Item data/bobby.txt -ErrorAction Ignore; $consoleSources = Get-ChildItem -Recurse -Filter *.java -Path src/main/java | Where-Object { $_.FullName -notmatch '\\bobby\\gui\\' -and $_.Name -ne 'Launcher.java' } | ForEach-Object FullName; javac --release 25 -d out/date-time-verification $consoleSources; "   todo    read   book", "todo READ BOOK", "deadline report /by 2026-02-30 1200", "deadline report /by 2026-09-20 1200 /by 2026-09-21 1200", "event meeting /from 2026-09-20 1200 /to 2026-09-20 1200", "todo unsafe | description", "mark +1", "list", "bye" | java -cp out/date-time-verification bobby.Bobby
```

**Expected output:**

```text
____________________________________________________________
BBBB   OOO   BBBB  BBBB  Y   Y
B   B O   O  B   B B   B  Y Y
BBBB  O   O  BBBB  BBBB    Y
B   B O   O  B   B B   B   Y
BBBB   OOO   BBBB  BBBB    Y
____________________________________________________________
     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.
     What charge wouldst thou have me enter, amend, or proclaim?
____________________________________________________________
____________________________________________________________
     It is done. By thy command, I have inscribed this duty upon the royal register:
       [T][ ] read book
     One duty now standeth upon the register.
____________________________________________________________
____________________________________________________________
     That very duty already standeth upon the royal register.
____________________________________________________________
____________________________________________________________
     The appointed date and hour are not in an acceptable form. Pray employ YYYY-MM-DD HHMM.
____________________________________________________________
____________________________________________________________
     Thy decree must take precisely this form: deadline DESCRIPTION /by YYYY-MM-DD HHMM.
____________________________________________________________
____________________________________________________________
     An event must commence before it concludeth.
____________________________________________________________
____________________________________________________________
     A duty's description must contain readable text and may not contain the character '|'.
____________________________________________________________
____________________________________________________________
     The number thou hast named correspondeth to no duty presently held within the register.
____________________________________________________________
____________________________________________________________
Behold, the full register of thy appointed duties:
     1.[T][ ] read book
____________________________________________________________
____________________________________________________________
     I humbly take my leave. May good fortune attend thee until next we meet.
____________________________________________________________
```
