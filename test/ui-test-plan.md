# UI test plan

## Test: Impossible dates are denied and midnight overflows correctly

**Aim:** Reject impossible dates without creating todos, display the next day's midnight, and exit on EOF.

### Input

```text
deadline work /by 2026-2-30
pending work /after 2026-4-31
event work /from 2026-2-29 /to 2026-3-1
date 2026-13-1
list
deadline work /by 2026-12-31 24:00
list
```

### Expected output

```text
OH, HOW VERY SMART OF YOU TO PROPOSE THAT DATE. DENIED.
OH, HOW VERY SMART OF YOU TO PROPOSE THAT DATE. DENIED.
OH, HOW VERY SMART OF YOU TO PROPOSE THAT DATE. DENIED.
OH, HOW VERY SMART OF YOU TO PROPOSE THAT DATE. DENIED.
VERY WELL. HERE IS YOUR LIST:
BUT, THERE WAS NOTHING TO READ.
ORDER PROCESSED: [D][ ] WORK (BY: JAN 01 2027, 0:00)
VERY WELL. HERE IS YOUR LIST:
1.[D][ ] work (by: Jan 01 2027, 0:00)
```

## Test: Pipes and escape-like text remain visible in descriptions

**Aim:** Preserve literal pipes, backslashes, and percent sequences when adding and displaying tasks.
Storage round-trip and invalid-record skipping are additionally covered by `TaskStorageTest`.

### Input

```text
todo read | write \| %7C
list
bye
```

### Expected output

```text
ORDER PROCESSED: [T][ ] READ | WRITE \| %7C
---
1.[T][ ] read | write \| %7C
```

### Save failure verification

The automated command tests in `TaskListTest.commands_saveFails_returnGuiErrorsAndRecoverOnNextChange`
use an inaccessible destination to verify add, mark, unmark, and delete return `ReplyType.ERROR`.
The GUI renders this result using its existing error dialogue, with no terminal-only save warning or timed pause:

```text
BUT, YOUR CHANGES COULD NOT BE SAVED.
THEY REMAIN IN THIS SESSION ONLY.
CHECK FILE ACCESS AND DISK SPACE BEFORE MAKING ANOTHER CHANGE TO RETRY.
```

Changes remain in memory. After file access is restored, the next successful modifying command saves
the complete current list. Normal console cases below retain their existing expected output.

## Test: Find lists only matching tasks

**Aim:** Verify that find matches task descriptions case-insensitively, renumbers results, and handles missing and blank matches.

### Input

```text
todo read book
todo buy groceries
deadline return BOOK /by 2026-6-6
find book
find phone
find
bye
```

### Expected output

```text
VERY WELL. HERE IS YOUR MATCHING LIST:
1.[T][ ] read book
2.[D][ ] return BOOK (by: Jun 06 2026, 0:00)
---
BUT, THERE WAS NOTHING THAT CONFORMS TO THE TERM.
---
BUT, THERE WAS NOTHING TO LOCATE.
```

## Test: Valid tasks remain correct after an unknown command

**Aim:** Verify that a rejected command does not alter tasks added before or after it.

### Input

```text
todo borrow book
abracadabra
list
deadline submit report /by 2026-8-24 17:30
list
event project meeting /from 2026-8-25 14:00 /to 2026-8-25 16:00
list
bye
```

### Expected output

```text
ORDER PROCESSED: [T][ ] BORROW BOOK
---
WELL, THAT IS NO LONGER A COMMAND.
---
1.[T][ ] borrow book
---
ORDER PROCESSED: [D][ ] SUBMIT REPORT (BY: AUG 24 2026, 5:30 PM)
---
1.[T][ ] borrow book
2.[D][ ] submit report (by: Aug 24 2026, 5:30 PM)
---
ORDER PROCESSED: [E][ ] PROJECT MEETING (FROM: AUG 25 2026, 2:00 PM TO: AUG 25 2026, 4:00 PM)
---
1.[T][ ] borrow book
2.[D][ ] submit report (by: Aug 24 2026, 5:30 PM)
3.[E][ ] project meeting (from: Aug 25 2026, 2:00 PM to: Aug 25 2026, 4:00 PM)
```

## Test: Invalid task details preserve the documented fallback state

**Aim:** Verify that an empty todo and missing event keywords are rejected, while deadlines retain their fallback.

### Input

```text
todo 
list
deadline revise notes
list
event orientation /from Monday
list
bye
```

### Expected output

```text
IT IS BARREN AND CANNOT BE CREATED.
---
BUT, THERE WAS NOTHING TO READ.
---
YOU MUST BE
MISTAKEN.

HERE.
ORDER PROCESSED: [T][ ] REVISE NOTES
---
1.[T][ ] revise notes
---
BUT, AN EVENT NEEDS EXACTLY ONE /FROM AND ONE /TO.
---
VERY WELL. HERE IS YOUR LIST:
1.[T][ ] revise notes
```

## Test: Invalid marking leaves task state unchanged

**Aim:** Verify that an invalid mark command does not complete the task, while later valid mark and unmark commands still work.

### Input

```text
todo write tests
mark not-a-number
list
mark 1
list
unmark 1
list
bye
```

### Expected output

```text
ORDER PROCESSED: [T][ ] WRITE TESTS
---
BUT, IT IS INVALID.
---
1.[T][ ] write tests
---
THEN, IT IS DONE.
[T][X] write tests
---
1.[T][X] write tests
---
THEN, IT WAS AS IF IT WAS NEVER DONE.
[T][ ] write tests
---
1.[T][ ] write tests
```

## Test: Missing mark argument leaves an empty list unchanged

**Aim:** Verify that a malformed mark command is rejected without creating or modifying a task.

### Input

```text
mark 
list
bye
```

### Expected output

```text
BUT, THE OBJECT IS NOT SPECIFIED.
---
BUT, THERE WAS NOTHING TO READ.
```

## Test: After-tasks unlock and cascade with their parent

**Aim:** Verify date-only release times, dependency locking, unlocking, recursive unmarking, and cascading deletion.

### Input

```text
pending already available /after 2026-9-1
mark 1
todo finish exam
pending return book /aftertask 2
mark 3
mark 2
mark 3
unmark 2
list
delete 2
list
bye
```

### Expected output

```text
ORDER PROCESSED: [A][ ] ALREADY AVAILABLE (AFTER: SEP 01 2026, 0:00)
---
THEN, IT IS DONE.
[A][X] already available (after: Sep 01 2026, 0:00)
---
BUT, ITS CONDITIONS ARE NOT SATISFIED.
---
THEN, IT IS DONE.
[T][X] finish exam
---
THEN, IT IS DONE.
[A][X] return book (after task: finish exam)
---
1.[A][X] already available (after: Sep 01 2026, 0:00)
2.[T][ ] finish exam
3.[A][L][ ] return book (after task: finish exam)
---
AS WITH ITS 1 CHILDREN.
---
1.[A][X] already available (after: Sep 01 2026, 0:00)
```

## Test: Invalid event ranges and extra conditions do not create tasks

**Aim:** Reject equal or reversed ranges, repeated event keywords, and conflicting or repeated pending conditions.

### Input

```text
event meeting /from 2026-9-17 /to 2026-9-16
event meeting /from 2026-9-16 /to 2026-9-16
event meeting /from 2026-9-16 /from 2026-9-17
pending work /after 2026-9-17 /aftertask 1
pending work /aftertask 1 /after 2026-9-17
pending work /after 2026-9-17 /after 2026-9-18
pending work /aftertask 1 /aftertask 2
list
event meeting /TO 2026-9-17 /FROM 2026-9-16
list
bye
```

### Expected output

```text
BUT, THE EVENT MUST START BEFORE IT ENDS.
BUT, THE EVENT MUST START BEFORE IT ENDS.
BUT, AN EVENT NEEDS EXACTLY ONE /FROM AND ONE /TO.
BUT, A PENDING TASK CAN HAVE ONLY ONE CONDITION.
BUT, A PENDING TASK CAN HAVE ONLY ONE CONDITION.
BUT, A PENDING TASK CAN HAVE ONLY ONE CONDITION.
BUT, A PENDING TASK CAN HAVE ONLY ONE CONDITION.
VERY WELL. HERE IS YOUR LIST:
BUT, THERE WAS NOTHING TO READ.
---
VERY WELL. HERE IS YOUR LIST:
1.[E][ ] meeting (from: Sep 16 2026, 0:00 to: Sep 17 2026, 0:00)
```
