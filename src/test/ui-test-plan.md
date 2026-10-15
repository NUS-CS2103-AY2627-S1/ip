# UI Test Plan

`UiTest.java` executes these console flows with exact output comparisons. Each test begins with an empty `data/tasks.txt`, unless an initial save file is stated, and restores any developer data afterwards.

## Exact transcript notation

The following literal blocks are used in every expected transcript. `S` immediately follows every command response; `B` is the final goodbye block.

```text
W = Hello! I'm TardT.␠
    What can I do for you?␠
    ____________________________________________________________

S = ____________________________________________________________

B = ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________
```

The indented lines above are shown without their four-space Markdown indentation; `␠` means one trailing space.
Thus `W + B`, for example, is the complete output for `bye`, including all newlines. Every response text below is followed by its literal `S` block.

## Cases

| Executable test | Aim | Input | Exact output after `W` |
| --- | --- | --- | --- |
| `bye_exitsApplication_showsWelcomeAndGoodbye` | Exit cleanly. | `bye` | `B` |
| `todo_addTask_savesTaskToFile` | Add and save a to-do. | `todo read book`; `bye` | `S`, `Got it. I've added this task:`<br>`  [T][ ] read book \| Priority: LOW`<br>`Now you have 1 tasks in the list.`, `S`, `B` |
| `list_startupWithSavedTasks_reconstructsAndListsTasks` | Load saved tasks. | Initial data: `T \| 1 \| read book`, `D \| 0 \| return book \| 2026-08-23T15:00`, `E \| 0 \| project meeting \| 2026-10-20T15:00 \| 2026-10-20T17:00`; then `list`; `bye` | `S`, `1. [T][X] read book \| Priority: LOW`<br>`2. [D][ ] return book \| Priority: LOW (by: Aug 23 2026 15:00)`<br>`3. [E][ ] project meeting \| Priority: LOW (from: Oct 20 2026 15:00 to: Oct 20 2026 17:00)`, `S`, `B` |
| `deadline_addValidDeadline_isStoredAndListedWithDeadlineValue` | Add an ISO deadline. | `deadline submit assignment /by 2026-10-20T17:00`; `list`; `bye` | `S`, add `submit assignment` by `Oct 20 2026 17:00`, `S`, list the same deadline, `S`, `B` |
| `deadline_spacedDateWithAmPmTime_isAccepted` | Accept a documented spaced date and am/pm time end-to-end. | `deadline submit assignment /by 29 Sep 2026 2pm`; `list`; `bye` | `S`, add `submit assignment` by `Sep 29 2026 14:00`, `S`, list the same deadline, `S`, `B` |
| `deadline_uppercaseByFlag_isAccepted` | Treat `/BY` like `/by`. | `deadline submit assignment /BY 2026-10-20T17:00`; `list`; `bye` | `S`, add `submit assignment` by `Oct 20 2026 17:00`, `S`, list the same deadline, `S`, `B` |
| `deadline_malformedDeadline_isRejectedWithoutAddingSecondTask` | Reject a missing `/by` flag. | `todo read book`; `deadline submit assignment`; `list`; `bye` | Add `read book`, `S`, `Invalid format. Use: deadline [task name] /by [deadline] (optional: /priority low\|medium\|high).`, `S`, list only `read book`, `S`, `B` |
| `event_addValidEvent_isStoredAndListedWithStartAndEndTimes` | Add an ISO event. | `event lecture /from 2026-10-20T15:00 /to 2026-10-20T16:00`; `list`; `bye` | `S`, add `lecture` from `Oct 20 2026 15:00` to `Oct 20 2026 16:00`, `S`, list the same event, `S`, `B` |
| `event_endBeforeStart_isRejectedWithoutAddingTask` | Reject a non-positive event duration. | `event workshop /from 2026-10-20T16:00 /to 2026-10-20T15:00`; `list`; `bye` | `S`, `Event end time must be after its start time.`, `S`, `Your task list is empty!`, `S`, `B` |
| `mark_nonNumericIndex_isRejectedWithoutChangingTaskStatus` | Reject an invalid mark number. | `todo read book`; `mark one`; `list`; `bye` | Add `read book`, `S`, `'one' is not a valid integer.`, `S`, list incomplete `read book`, `S`, `B` |
| `delete_existingTask_removesTaskAndRenumbersRemaining` | Delete a task and retain the remaining deadline. | `todo read book`; `deadline submit assignment /by 2026-10-20T17:00`; `delete 1`; `list`; `bye` | Add each task, `S` after each; `Noted. I've removed this task:`<br>`  [T][ ] read book \| Priority: LOW`<br>`Now you have 1 tasks in the list.`, `S`, list deadline as task 1, `S`, `B` |
| `delete_nonNumericIndex_isRejectedWithoutChangingTasks` | Reject an invalid delete number. | `todo read book`; `delete one`; `list`; `bye` | Add `read book`, `S`, `'one' is not a valid integer.`, `S`, list `read book`, `S`, `B` |
| `blankCommand_isRejected_andApplicationContinues` | Reject an empty command. | empty line; `list`; `bye` | `S`, `Please enter a command.`, `S`, `Your task list is empty!`, `S`, `B` |
| `todo_invalidPriority_isRejectedWithoutAddingTask` | Reject an invalid priority value. | `todo /priority urgent`; `list`; `bye` | `S`, `Invalid priority. Priority is optional; when included, use /priority low, /priority medium, or /priority high.`, `S`, empty list, `S`, `B` |
| `todo_repeatedPriority_isRejectedWithoutAddingTask` | Reject repeated `/priority`. | `todo read book /priority low /priority high`; `list`; `bye` | `S`, `Repeated flags are not allowed.`, `S`, empty list, `S`, `B` |
| `deadline_repeatedByFlag_isRejectedWithoutAddingTask` | Reject repeated `/by`. | `deadline submit assignment /by Friday /by Saturday`; `list`; `bye` | `S`, `Repeated flags are not allowed.`, `S`, empty list, `S`, `B` |
| `event_repeatedToFlag_isRejectedWithoutAddingTask` | Reject repeated `/to`. | `event workshop /from 2pm /to 4pm /to 6pm`; `list`; `bye` | `S`, `Repeated flags are not allowed.`, `S`, empty list, `S`, `B` |

The `unknownCommand_isRejected_andSubsequentValidCommandStillWorks` test additionally verifies the complete parser help text before adding and listing `read book`. Its exact expected string is retained in `UiTest.java`, alongside the exact assertions for every case above.
