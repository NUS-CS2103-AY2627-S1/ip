# Level-10 manual GUI checklist

Run these scenarios in order. Report the scenario number, command, expected
result, and actual result if anything fails. A screenshot helps for layout issues.

## Start with isolated test data

Close any previous SlotBot test window yourself. In PowerShell at the repository
root, run the following. This creates a fresh directory under `_temp` and leaves
your normal `data/slotbot.txt` untouched.

```powershell
.\gradlew.bat shadowJar
$slotbotJar = (Resolve-Path .\build\libs\slotbot.jar).Path
$manualDirectory = Join-Path $PWD ("_temp\level-10-manual-" + [guid]::NewGuid())
New-Item -ItemType Directory -Path $manualDirectory | Out-Null
Push-Location $manualDirectory
java -jar $slotbotJar
```

The terminal waits while the GUI is open. Keep this PowerShell session for
the reopening checks below. Java 25 may print JavaFX native-access,
unnamed-module, or Unsafe warnings; the window should still open.

## 1. Launch and blank submissions

Expect the SlotBot title, welcome message, input field, and Send button.
Press Enter with an empty field, then enter spaces and click Send.
Expect no new conversation messages, no tasks, and no error dialog.

## 2. Enter and Send

Type `todo read book` and press Enter. Expect one user message and one reply
containing `[T][ ] read book` and a count of 1. The field should clear and
remain ready for typing. Type `list` and click Send. Expect that one task,
with no duplicate addition. The field should clear and regain focus.

## 3. Other task types

Submit these commands:

```text
deadline return book /by 2026-09-10
event study /from 2026-09-10 14:00 /to 2026-09-10 15:00
list
```

Expect 3 tasks in order: todo, deadline, event. Dates should display as
`Sep 10 2026`, with event times `14:00` and `15:00`.

## 4. Search and status changes

| Command | Expected result |
| --- | --- |
| `find book` | Only read book and return book, in that order. |
| `find missing` | `No tasks match "missing".` |
| `mark 1` | Read book shows `[T][X]`. |
| `unmark 1` | Read book shows `[T][ ]`. |
| `mark 3` | Study shows `[E][X]`; the last valid index works. |
| `delete 2` | Return book removed; count becomes 2. |
| `list` | Read book is item 1; completed study is item 2. |

## 5. Invalid inputs and boundaries

Submit each command below. Expect an error response and an interactive window.

| Command | Expected result |
| --- | --- |
| `todo` | Empty-description error and usage hint. |
| `blah` | Unknown-command error. |
| `mark 0` | Invalid task-number error. |
| `mark 3` | Task does not exist (only 2 remain). |
| `delete abc` | Whole-number error. |
| `deadline bad /by 2026-02-30` | Invalid-date error and date-format hint. |
| `event bad /from 2026-02-30 14:00 /to 2026-02-30 15:00` | Invalid event-time error. |
| `find` | Missing-keyword error. |
| `bye extra` | Error; window stays open. |

Run `list`: the same two tasks and completion states must remain.

## 6. Long text, scrolling, and resizing

Add `todo ` followed by a long description (several sentences, including one
long word without spaces). Expect count 3.
Run `list` several times. New replies should be visible automatically. Scroll
up to older messages and back down. Resize the window smaller and larger.
Expect readable wrapped text, reachable controls, and no overlapping elements.
Check that the long word also remains readable.

Autoscroll regression: after each submission, the last line of the newest
reply must already be visible without scrolling further. Check both Enter and
Send, including after scrolling up to older messages and after narrowing the
window. You must still be able to scroll up manually between submissions.
For a reply taller than the viewport, expect its bottom to be visible; scroll
up to read its beginning.

## 7. Window close and reload

Close using the window's X. In the same PowerShell session, run:

```powershell
java -jar $slotbotJar
```

Run `list`. Expect all 3 remaining tasks, including the completed event and
long todo. Conversation history starts fresh; task data persists.

## 8. Bye and reload

Enter ` bye ` (with surrounding spaces). Expect a goodbye, disabled input
and Send button, then window closure after about one second. Reopen with
`java -jar $slotbotJar` and run `list`: tasks should still be present. Close
the window before continuing.

## 9. Startup warning (isolated data only)

This adds an invalid record to the disposable manual-test save file. Run:

```powershell
Add-Content -LiteralPath .\data\slotbot.txt -Value 'broken'
java -jar $slotbotJar
```

Expect an invalid-task-data warning in the opening conversation. `list` should
still show the valid tasks. Close the window, then return to the repository:

```powershell
Pop-Location
```

## 10. Console regression

From the repository root run `.\text-ui-test\runtest.bat`. Expect `PASS`.
This uses separate temporary data and checks the original console behavior.

If everything passes, tell me "Level-10 manual tests passed". Tagging
`Level-10` still needs your explicit approval. Nothing is pushed automatically.
