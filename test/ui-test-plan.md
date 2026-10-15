# UI Test Plan

This file records UI test cases for the `test-ui` project skill.

Format: a single JSON code block (```json ... ```). The JSON object has a "tests" array; each test must include:
- id: short identifier
- aim: what the test checks
- command: command to run (shell command)
- input: string to send to stdin (optional, empty string means no input)
- expected: expected stdout (use "\n" for newlines)

Example test cases:

```json
{
  "tests": [
    {
      "id": "tc-echo-hello",
      "aim": "Verify echo prints Hello",
      "command": "cmd /c echo Hello",
      "input": "",
      "expected": "Hello\n"
    }
  ]
}
```

Add or edit tests as needed. Save this file and run the skill to execute the tests.
# UI Test Plan - Bubu Chatbot

This document outlines the test cases used to verify command processing, input validation, and task management in Bubu up to Level 6.

---

## 1. Task Creation

### `todo`
* **Valid input:** `todo read book`
  * **Expected:** Task `[T][ ] read book` added.
* **Missing description:** `todo` or `todo   `
  * **Expected:** Exception caught; error message prompt displayed.

### `deadline`
* **Valid input:** `deadline return book /by Sunday`
  * **Expected:** Task `[D][ ] return book (by: Sunday)` added.
* **Missing flag or date:** `deadline return book` or `deadline return book /by `
  * **Expected:** Exception caught; usage syntax error displayed.

### `event`
* **Valid input:** `event team meeting /from Mon 2pm /to 4pm`
  * **Expected:** Task `[E][ ] team meeting (from: Mon 2pm to: 4pm)` added.
* **Missing `/to` section:** `event team meeting /from Mon 2pm`
  * **Expected:** Exception caught; usage syntax error displayed.

---

## 2. Task List Management

### `list`
* **Empty list:** Run `list` before adding tasks.
  * **Expected:** Empty list notice printed.
* **Populated list:** Run `list` after adding tasks.
  * **Expected:** Correctly formatted and numbered list of active tasks.

### `mark` & `unmark`
* **Valid index:** `mark 1` / `unmark 1`
  * **Expected:** Task status updated to `[X]` or `[ ]` with confirmation output.
* **Out-of-bounds index:** `mark 0` or `mark 100` (when list size is 2)
  * **Expected:** Exception caught; invalid index message printed.
* **Non-numeric argument:** `mark abc` or `mark`
  * **Expected:** Exception caught; invalid index or missing argument message printed.

---

## 3. Task Deletion (`delete`)

### `delete`
* **Valid deletion:** `delete 1`
  * **Expected:** Task 1 removed, confirmation displayed, and remaining task count updated.
* **Out-of-bounds deletion:** `delete 99`
  * **Expected:** Exception caught; invalid index message printed.
* **Missing argument:** `delete`
  * **Expected:** Exception caught; missing argument error printed.
* **Extra whitespace:** `delete   1`
  * **Expected:** Extra spaces trimmed, task 1 safely deleted.

---

## 4. Termination

### `bye`
* **Exit application:** `bye`
  * **Expected:** Farewell message printed, program terminates gracefully.

---

## 5. Student Scenarios: Todos, Deadlines, Events, Errors, and Deletion

These cases are based on the commands used in the project demonstration. Run each
case in a new application session unless the setup commands are listed.

| ID | Setup and command | Expected result |
|---|---|---|
| TC-TODO-01 | `todo borrow book` | Adds `[T][ ] borrow book` and reports the updated task count. |
| TC-LIST-01 | Add `read book`, `return book /by June 6th`, `project meeting /from Aug 6th 2pm /to 4pm`, `join sports club`, and `borrow book`; mark tasks 1 and 4; then run `list` | Displays five numbered tasks, preserving each task type, completion status, and deadline/event details. |
| TC-DEADLINE-01 | `deadline return book /by Sunday` | Adds `[D][ ] return book (by: Sunday)`. |
| TC-EVENT-01 | `event project meeting /from Mon 2pm /to 4pm` | Adds `[E][ ] project meeting (from: Mon 2pm to: 4pm)`. |
| TC-ERROR-01 | `todo` | Displays `Meow! The description of a todo task cannot be empty.` |
| TC-ERROR-02 | `blah` | Displays the unknown-command error and keeps the application running. |
| TC-DELETE-01 | With the five-task setup above, run `delete 3` | Removes `[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)` and reports four tasks remaining. |

The expected text above matches the current application wording. The exact prefix
(`Meow!`) is intentional because the program's messages include the chatbot's
personality.

---

## 6. Task Persistence (Writing)

| ID | Setup and command | Expected result |
|---|---|---|
| TC-SAVE-01 | Add a todo, deadline, and event; mark or unmark a task; then delete one task. | `data/bubu.txt` is updated after each successful change and contains the final task list in `type | status | details` format. |
| TC-LOAD-01 | Seed `data/bubu.txt`, start the chatbot, then enter `list`. | The seeded todo, deadline, and event are loaded with their saved completion statuses and details. |

`text-ui-test/runtest.bat` compares the final saved file to
`text-ui-test/PERSISTENCE-EXPECTED.TXT` and uses `LOAD-DATA.TXT` to test loading.
It temporarily backs up, then restores, an existing `data/bubu.txt` file.

---

## 7. Level 8: Dates

| ID | Command | Expected result |
|---|---|---|
| TC-DATE-01 | `deadline return book /by 2023-10-10 1800` | Stores the deadline as a `LocalDateTime` and displays `Oct 10 2023, 6:00PM`. |
| TC-DATE-02 | `event meeting /from 2023-10-10 1400 /to 2023-10-10 1600` | Stores and displays both event date-times in the formatted form. |
| TC-DATE-03 | `deadline invalid /by 2023-02-30` | Rejects the impossible date and displays the date-format error. |
| TC-DATE-04 | `deadline date only /by 2026-08-30` | Uses the default deadline time of `23:59`. |
| TC-DATE-05 | Start with a saved ISO date-time in `data/bubu.txt`, then enter `list`. | Loads the ISO date-time and displays the formatted version. |

The implementation accepts `yyyy-MM-dd` and `yyyy-MM-dd HHmm`. A date-only deadline
uses `23:59`; a date-only event start uses `00:00` and end uses `23:59`.

---

## 8. Command-Object Regression Coverage

| ID | Commands exercised | Expected result |
|---|---|---|
| TC-COMMAND-01 | `list`, `bye` | The extracted `ListCommand` and `ExitCommand` preserve the existing list and farewell output. |
| TC-COMMAND-02 | `todo Buy milk` | The extracted `TodoCommand` adds, saves, and confirms a to-do task. |
| TC-COMMAND-03 | `deadline Submit report /by 2023-10-10 1800` | The extracted `DeadlineCommand` parses, saves, and displays a deadline. |
| TC-COMMAND-04 | `event Party /from 2023-10-10 1400 /to 2023-10-10 1600` | The extracted `EventCommand` parses, saves, and displays an event. |

These are end-to-end regression cases: they verify that moving behavior into
command objects does not change the visible chatbot behavior or saved-file output.
