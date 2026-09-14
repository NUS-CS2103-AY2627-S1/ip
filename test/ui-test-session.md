# UI test session

## 1. TC01: Exit immediately — PASS

Aim: Verify that the application displays its greeting and a farewell when the user exits.

**Command:**
```text
powershell -NoProfile -Command "Remove-Item data\arrodes.txt -ErrorAction SilentlyContinue; java -jar build\libs\arrodes.jar --cli"
```

**Console input:**
```text
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

**Actual output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

Exit code: 0

## 2. TC09: Display command guide — PASS

Aim: Verify that the help command displays the syntax for every available command.

**Command:**
```text
powershell -NoProfile -Command "Remove-Item data\arrodes.txt -ErrorAction SilentlyContinue; java -jar build\libs\arrodes.jar --cli"
```

**Console input:**
```text
help
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
Arrodes reveals the available commands:

todo <description>
    Adds a todo task.

deadline <description> /by <yyyy-MM-dd>
deadline <description> /by <yyyy-MM-ddTHH:mm>
    Adds a task with a deadline.

event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>
event <description> /from <yyyy-MM-ddTHH:mm> /to <yyyy-MM-ddTHH:mm>
    Adds an event between two dates or date-times.

mark <task number>
    Marks a task as complete.

unmark <task number>
    Marks a task as incomplete.

delete <task number>
    Deletes a task.

list
    Displays all tasks.

find <keyword>
    Finds tasks containing the keyword.

upcoming /on <yyyy-MM-dd>
upcoming /on <yyyy-MM-ddTHH:mm>
    Displays deadlines and events relevant to a date or date-time.

tag <task number> /with <tag>
    Adds a tag to a task.

help
    Displays this command guide.

bye
    Exits Arrodes.

____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

**Actual output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
Arrodes reveals the available commands:

todo <description>
    Adds a todo task.

deadline <description> /by <yyyy-MM-dd>
deadline <description> /by <yyyy-MM-ddTHH:mm>
    Adds a task with a deadline.

event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>
event <description> /from <yyyy-MM-ddTHH:mm> /to <yyyy-MM-ddTHH:mm>
    Adds an event between two dates or date-times.

mark <task number>
    Marks a task as complete.

unmark <task number>
    Marks a task as incomplete.

delete <task number>
    Deletes a task.

list
    Displays all tasks.

find <keyword>
    Finds tasks containing the keyword.

upcoming /on <yyyy-MM-dd>
upcoming /on <yyyy-MM-ddTHH:mm>
    Displays deadlines and events relevant to a date or date-time.

tag <task number> /with <tag>
    Adds a tag to a task.

help
    Displays this command guide.

bye
    Exits Arrodes.

____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

Exit code: 0

## 3. TC04: Recover from a corrupted data file — PASS

Aim: Verify that a corrupted data file is reported and the chatbot continues with an empty task list.

**Command:**
```text
powershell -NoProfile -Command "Set-Content data\arrodes.txt 'X | 0 | corrupt'; java -jar build\libs\arrodes.jar --cli"
```

**Console input:**
```text
list
bye
```

**Expected output:**
```text
Arrodes could not restore the annals.
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
Arrodes reveals the requests entrusted to its memory:

____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

**Actual output:**
```text
Arrodes could not restore the annals.
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
Arrodes reveals the requests entrusted to its memory:

____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

Exit code: 0

## 4. TC02: Add and list every task type — PASS

Aim: Verify that todo, deadline, event, tag, and list commands display their stored tasks.

**Command:**
```text
powershell -NoProfile -Command "Remove-Item data\arrodes.txt -ErrorAction SilentlyContinue; java -jar build\libs\arrodes.jar --cli"
```

**Console input:**
```text
todo buy milk
deadline submit report /by 2026-08-30
event team meeting /from 2026-08-30T14:00 /to 2026-08-30T15:00
tag 3 /with work
list
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [T][ ] buy milk
1 tasks are being tracked
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [D][ ] submit report (by: Aug 30 2026)
2 tasks are being tracked
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
3 tasks are being tracked
____________________________________________________________
____________________________________________________________
Arrodes seals this record with the work sigil:
  [E][ ] team meeting #work (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
____________________________________________________________
____________________________________________________________
Arrodes reveals the requests entrusted to its memory:
    1.[T][ ] buy milk
    2.[D][ ] submit report (by: Aug 30 2026)
    3.[E][ ] team meeting #work (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)

____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

**Actual output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [T][ ] buy milk
1 tasks are being tracked
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [D][ ] submit report (by: Aug 30 2026)
2 tasks are being tracked
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
3 tasks are being tracked
____________________________________________________________
____________________________________________________________
Arrodes seals this record with the work sigil:
  [E][ ] team meeting #work (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
____________________________________________________________
____________________________________________________________
Arrodes reveals the requests entrusted to its memory:
    1.[T][ ] buy milk
    2.[D][ ] submit report (by: Aug 30 2026)
    3.[E][ ] team meeting #work (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)

____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

Exit code: 0

## 5. TC03: Mark, unmark, and delete tasks — PASS

Aim: Verify that task status changes and deletion update the task list.

**Command:**
```text
powershell -NoProfile -Command "Remove-Item data\arrodes.txt -ErrorAction SilentlyContinue; java -jar build\libs\arrodes.jar --cli"
```

**Console input:**
```text
todo buy milk
event team meeting /from 2026-08-30T14:00 /to 2026-08-30T15:00
mark 1
unmark 1
delete 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [T][ ] buy milk
1 tasks are being tracked
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
2 tasks are being tracked
____________________________________________________________
____________________________________________________________
The mirror bears witness: this task is complete:
  [T][X] buy milk
____________________________________________________________
____________________________________________________________
By your decree, the mark of completion has been withdrawn:
  [T][ ] buy milk
____________________________________________________________
____________________________________________________________
The following record has been erased from the annals:
[E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
1 tasks remaining are being tracked
____________________________________________________________
____________________________________________________________
Arrodes reveals the requests entrusted to its memory:
    1.[T][ ] buy milk

____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

**Actual output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [T][ ] buy milk
1 tasks are being tracked
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
2 tasks are being tracked
____________________________________________________________
____________________________________________________________
The mirror bears witness: this task is complete:
  [T][X] buy milk
____________________________________________________________
____________________________________________________________
By your decree, the mark of completion has been withdrawn:
  [T][ ] buy milk
____________________________________________________________
____________________________________________________________
The following record has been erased from the annals:
[E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
1 tasks remaining are being tracked
____________________________________________________________
____________________________________________________________
Arrodes reveals the requests entrusted to its memory:
    1.[T][ ] buy milk

____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

Exit code: 0

## 6. TC05: Reject impossible calendar dates — PASS

Aim: Verify that a correctly shaped but impossible date, such as February 31, is rejected clearly.

**Command:**
```text
powershell -NoProfile -Command "Remove-Item data\arrodes.txt -ErrorAction SilentlyContinue; java -jar build\libs\arrodes.jar --cli"
```

**Console input:**
```text
deadline submit report /by 2026-02-31
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
That appointed date or time does not exist.
____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

**Actual output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
That appointed date or time does not exist.
____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

Exit code: 0

## 7. TC07: Find upcoming tasks on a date — PASS

Aim: Verify that upcoming lists matching deadlines and events for date-only and date-time queries.

**Command:**
```text
powershell -NoProfile -Command "Remove-Item data\arrodes.txt -ErrorAction SilentlyContinue; java -jar build\libs\arrodes.jar --cli"
```

**Console input:**
```text
deadline submit report /by 2026-10-15T17:00
event all day /from 2026-10-15 /to 2026-10-15
event meeting /from 2026-10-14T23:00 /to 2026-10-15T01:00
todo buy milk
upcoming /on 2026-10-15
upcoming /on 2026-10-15T00:30
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [D][ ] submit report (by: Oct 15 2026 17:00)
1 tasks are being tracked
____________________________________________________________
____________________________________________________________
The appointed hour cannot end before it begins.
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [E][ ] meeting (from: Oct 14 2026 23:00 to Oct 15 2026 01:00)
2 tasks are being tracked
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [T][ ] buy milk
3 tasks are being tracked
____________________________________________________________
____________________________________________________________
Arrodes gazes into the appointed hour and reveals requests for Oct 15 2026:
    1.[D][ ] submit report (by: Oct 15 2026 17:00)
    2.[E][ ] meeting (from: Oct 14 2026 23:00 to Oct 15 2026 01:00)

____________________________________________________________
____________________________________________________________
Arrodes gazes into the appointed hour and reveals requests for Oct 15 2026 00:30:
    2.[E][ ] meeting (from: Oct 14 2026 23:00 to Oct 15 2026 01:00)

____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

**Actual output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [D][ ] submit report (by: Oct 15 2026 17:00)
1 tasks are being tracked
____________________________________________________________
____________________________________________________________
The appointed hour cannot end before it begins.
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [E][ ] meeting (from: Oct 14 2026 23:00 to Oct 15 2026 01:00)
2 tasks are being tracked
____________________________________________________________
____________________________________________________________
Your request has been inscribed in Arrodes' annals:
   [T][ ] buy milk
3 tasks are being tracked
____________________________________________________________
____________________________________________________________
Arrodes gazes into the appointed hour and reveals requests for Oct 15 2026:
    1.[D][ ] submit report (by: Oct 15 2026 17:00)
    2.[E][ ] meeting (from: Oct 14 2026 23:00 to Oct 15 2026 01:00)

____________________________________________________________
____________________________________________________________
Arrodes gazes into the appointed hour and reveals requests for Oct 15 2026 00:30:
    2.[E][ ] meeting (from: Oct 14 2026 23:00 to Oct 15 2026 01:00)

____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

Exit code: 0

## 8. TC08: Find tasks by description — PASS

Aim: Verify that find displays matching tasks with their original list numbers and reports when there are no matches.

**Command:**
```text
powershell -NoProfile -Command "Set-Content data\arrodes.txt 'T | 1 | read book | ','D | 1 | return book | 2026-06-06 | ','T | 1 | wash dishes | '; java -jar build\libs\arrodes.jar --cli"
```

**Console input:**
```text
find book
find laptop
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
Arrodes reveals the records matching your words:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2026)
____________________________________________________________
____________________________________________________________
Arrodes reveals the records matching your words:
The mirror finds no record matching those words.
____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

**Actual output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
Arrodes reveals the records matching your words:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2026)
____________________________________________________________
____________________________________________________________
Arrodes reveals the records matching your words:
The mirror finds no record matching those words.
____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

Exit code: 0

## 9. TC06: Validate event time ordering — PASS

Aim: Verify that equal and reversed event times are rejected.

**Command:**
```text
powershell -NoProfile -Command "Remove-Item data\arrodes.txt -ErrorAction SilentlyContinue; java -jar build\libs\arrodes.jar --cli"
```

**Console input:**
```text
event same day /from 2026-10-15 /to 2026-10-15
event reversed /from 2026-10-15T10:00 /to 2026-10-15T09:00
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
The appointed hour cannot end before it begins.
____________________________________________________________
____________________________________________________________
The appointed hour cannot end before it begins.
____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

**Actual output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
The mirror opens its eye...
The Stigmata from the Primordial Land
The Great Arrodes stands before you.
Speak your request!
____________________________________________________________
____________________________________________________________
The appointed hour cannot end before it begins.
____________________________________________________________
____________________________________________________________
The appointed hour cannot end before it begins.
____________________________________________________________
____________________________________________________________
The mirror shall close its eye. Until your next request.
____________________________________________________________

```

Exit code: 0
