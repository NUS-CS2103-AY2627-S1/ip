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
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
I shall await your next request...
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
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
I shall await your next request...
____________________________________________________________

```

Exit code: 0

## 2. TC04: Recover from a corrupted data file — PASS

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
Arrodes could not load your requests.
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
Arrodes recalls your requests:
____________________________________________________________
____________________________________________________________
I shall await your next request...
____________________________________________________________

```

**Actual output:**
```text
Arrodes could not load your requests.
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
Arrodes recalls your requests:
____________________________________________________________
____________________________________________________________
I shall await your next request...
____________________________________________________________

```

Exit code: 0

## 3. TC02: Add and list every task type — FAIL

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
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [T][ ] buy milk
1 tasks are being tracked
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [D][ ] submit report (by: Aug 30 2026)
2 tasks are being tracked
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
3 tasks are being tracked
____________________________________________________________
____________________________________________________________
Arrodes shall bestow the task with work tag
  [E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00) #work
____________________________________________________________
____________________________________________________________
Arrodes recalls your requests:
1.[T][ ] buy milk
2.[D][ ] submit report (by: Aug 30 2026)
3.[E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00) #work
____________________________________________________________
____________________________________________________________
I shall await your next request...
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
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [T][ ] buy milk
1 tasks are being tracked
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [D][ ] submit report (by: Aug 30 2026)
2 tasks are being tracked
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
3 tasks are being tracked
____________________________________________________________
____________________________________________________________
Arrodes shall bestow the task with work tag
  [E][ ] team meeting #work (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
____________________________________________________________
____________________________________________________________
Arrodes recalls your requests:
1.[T][ ] buy milk
2.[D][ ] submit report (by: Aug 30 2026)
3.[E][ ] team meeting #work (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
____________________________________________________________
____________________________________________________________
I shall await your next request...
____________________________________________________________

```

Exit code: 0
