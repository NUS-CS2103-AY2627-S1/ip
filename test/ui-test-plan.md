# UI Test Plan

## Test protocol

- Main class: `stephen.Stephen`
- Java version: 25
- Compiled classes: `build/classes/java/main`
- Machine-readable cases: `test/ui-test-cases.json`
- Runner: `scripts/run_ui_tests.ps1`
- Each case runs in its own ignored directory under `out/ui-test-work`. The runner removes
  that case's prior data file and optionally writes `initialDataLines`, keeping persistent
  sessions isolated and repeatable without touching the user's normal data file.
- Each test case starts a fresh process. Its inputs are sent in order and the entire console output is compared exactly, except for platform line endings and one trailing newline.
- Testing stops immediately after the first failed test case. The transcript shows console inputs with a `>` prefix, followed by the actual console output. A failure also shows the exact expected output.
- The separator emitted by the program is a blank line followed by 48 underscore characters.

## LEVEL4-01 — Add a todo

**Aim:** Verify that a todo is added and displayed with the todo type and incomplete status.

**Inputs:**

1. `todo read book`
2. `bye`

**Expected output:**

```text
Orbit online.
Ready to plan your next move?

________________________________________________

________________________________________________
Mission logged. I've added this task:
  [T][ ] read book
You now have 1 task on the radar.

________________________________________________

________________________________________________
Orbit signing off. Keep moving forward!

________________________________________________
```

## LEVEL4-02 — Add a deadline

**Aim:** Verify that a valid ISO deadline date is parsed and displayed as `Dec 2 2019`.

**Inputs:** `deadline return book /by 2019-12-02`, then `bye`.

**Expected output:** Same greeting, separators, count, and farewell as LEVEL4-01; the added task line is `[D][ ] return book (by: Dec 2 2019)`.

## LEVEL4-03 — Add an event

**Aim:** Verify that an event is added with its description and time string.

**Inputs:** `event project meeting /from 2019-12-02 /to 2019-12-03`, then `bye`.

**Expected output:** Same greeting, separators, count, and farewell as LEVEL4-01; the added task line is `[E][ ] project meeting (from: Dec 2 2019 to: Dec 3 2019)`.

## LEVEL4-04 — List all three task types

**Aim:** Verify that `list` preserves insertion order and displays todo, deadline, and event formats.

**Inputs:** Add `todo read book`, `deadline return book /by 2019-12-02`, and
`event project meeting /from 2019-12-02 /to 2019-12-03`; enter `list`, then `bye`.

**Expected output:** The three normal add confirmations have counts 1, 2, and 3. The list section is exactly:

```text
Current mission plan:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 2 2019)
3.[E][ ] project meeting (from: Dec 2 2019 to: Dec 3 2019)
```

The normal greeting, separators, and farewell surround the interactions as recorded exactly in `test/ui-test-cases.json`.

## LEVEL4-05 — Mark a task

**Aim:** Verify that `mark 1` changes a todo's status from incomplete to complete.

**Inputs:** Add `todo read book`; enter `mark 1`, then `bye`.

**Expected output:** After the normal add response, the mark response is exactly:

```text
Milestone reached! This task is complete:
[T][X] read book
```

## LEVEL4-06 — Unmark a task

**Aim:** Verify that `unmark 1` changes a previously marked todo back to incomplete.

**Inputs:** Add `todo read book`; enter `mark 1`, `unmark 1`, then `bye`.

**Expected output:** After the add and mark responses, the unmark response is exactly:

```text
Task reopened. It's back on the radar:
[T][ ] read book
```

## LEVEL4-07 — Reject an invalid deadline date format

**Aim:** Verify that a non-ISO deadline date produces a clear `yyyy-MM-dd` error.

**Inputs:** `deadline submit report /by sometime after lunch-ish`, then `bye`.

**Expected output:** The invalid date is rejected with
`Navigation alert: Invalid deadline date. Please use yyyy-MM-dd, for example 2019-12-02.`;
the application then processes `bye` normally.

## LEVEL4-08 — Reject a nonexistent event date

**Aim:** Verify that strict parsing rejects `2019-02-29` and keeps the program running.

**Inputs:** `event festival /from 2019-02-29 /to 2019-03-01`, then `bye`.

**Expected output:** The nonexistent date is rejected with the existing invalid-event-date
message; the application then processes `bye` normally.

## LEVEL5-01 — Handle invalid input and preserve state

**Aim:** Verify that malformed commands show specific errors, do not terminate the chatbot,
and do not change task state. Valid commands are interleaved with invalid commands.

**Inputs:** Invalid and valid forms of `todo`, `deadline`, `event`, `mark`, `unmark`,
and `delete`, followed by the unknown command `blah`, `list`, and `bye`. The exact sequence
is recorded in `test/ui-test-cases.json` and includes missing descriptions and delimiters,
empty date/time values, missing/non-numeric/zero/negative/out-of-range task numbers, and
successful add, mark, unmark, and delete operations.

**Expected output:** Every invalid command prints a `Navigation alert:` message and the next command is
still processed. The final list contains only the two valid tasks that were not deleted:

```text
Current mission plan:
1.[D][ ] return book (by: Dec 2 2019)
2.[E][ ] meeting (from: Dec 2 2019 to: Dec 3 2019)
```

## LEVEL6-01 — Delete tasks and preserve state after invalid deletes

**Aim:** Verify deletion at every list position, automatic renumbering, deletion until the
list is empty, and preservation of list state after rejected delete commands.

**Inputs:** Add three todos; try `delete`, `delete abc`, `delete 0`, `delete -1`, and
`delete 999`; list the tasks; delete task 2 and list; then delete task 1 twice, listing
after each deletion; finally enter `bye`.

Successful additions and deletions also exercise the new automatic-save path. Saving is
intentionally silent, so the expected console output remains unchanged.

**Expected output:** Each invalid command prints the existing specific task-number error,
and the first list still contains all three tasks. Deleting task 2 removes `second task`
and renumbers `third task` from 3 to 2. The following deletions remove the first task and
then the remaining (last) task. The final list has no numbered task lines. Exact output is
recorded in `test/ui-test-cases.json`.

## Level 7 storage test

`StorageTest` verifies the non-UI persistence behavior directly. It checks serialization
of Todo, Deadline, and Event tasks (including completion status and escaped delimiters),
automatic parent-directory creation, and replacement of the file after status and list
changes. For Level 8 it also checks ISO date persistence and reload, while invalid saved
date values are skipped as malformed records.

## LEVEL7-01 — Load saved tasks and tolerate corrupted records

**Aim:** Verify application startup restores all three task types and completion status,
ignores a blank line and one malformed line, and still accepts `list` and `bye` normally.

**Initial data:** An incomplete Todo, a blank line, a malformed record, a completed
Deadline, and an incomplete Event, as recorded in `test/ui-test-cases.json`.

**Inputs:** `list`, then `bye`.

**Expected output:** The list contains exactly the three valid tasks in file order, with
the Deadline displayed as completed. The malformed and blank records produce no task and
do not crash the application.

## LEVEL8-STRETCH-01 — Show tasks scheduled on a date

**Aim:** Verify that `schedule 2019-12-02` displays deadlines due on that date and events
whose inclusive date range contains that date. Todos and dated tasks outside the requested
date are excluded. Displayed numbers remain the tasks' original list numbers.

**Initial data:** A Todo, two Deadlines on different dates, and a multi-day Event.

**Expected output:** The matching Deadline and Event are shown under the friendly date
heading `On the radar for Dec 2 2019:`.

## LEVEL8-STRETCH-02 — Show an empty schedule

**Aim:** Verify that a valid date with no matching deadlines or events displays the clear
message `Clear skies - no deadlines or events on Dec 5 2019.`.

## LEVEL8-STRETCH-03 — Reject an invalid schedule date

**Aim:** Verify that the nonexistent date in `schedule 2019-02-29` is rejected with the
expected `yyyy-MM-dd` format and that the chatbot continues to process `bye`.

## LEVEL9-01 — Find tasks by description

**Aim:** Verify that `find KEYWORD` searches task descriptions case-insensitively, treats
all text after `find` as one phrase, preserves task order, and displays each task once.

**Initial data:** Four tasks include one unique groceries match, three book matches, and
two matches for the phrase `read book`. One description repeats `book` to confirm that a
task is displayed only once.

**Inputs:** Search for `groceries`, `book`, `READ BOOK`, and `missing phrase`; then enter
`find` without a keyword and `bye`.

**Expected output:** Successful searches show `Scan found N matching task(s):` followed
by numbered task lines in their original order. The missing phrase reports no matches.
The empty search prints `Navigation alert: Please provide a search keyword. Try: find book` and the
application continues to process `bye`.

## Level 10 JavaFX GUI coverage

The production GUI uses the same parser, commands, task list, and storage classes as the CLI
through the UI-independent `Chatbot` facade. `ChatbotTest` automatically verifies a stateful
add/list interaction, invalid-command feedback, divider-free response text, and propagation of
the existing `bye` exit signal.

The JavaFX setup and production-window behavior require a graphical environment and are checked
manually. The smoke test covers FXML/controller/CSS loading, startup, Send and Enter submission,
empty-input prevention, consecutive messages and scrolling, Shift+Enter multiline input,
resizing, and the `bye` close behavior. The exact CLI expectations in
`test/ui-test-cases.json` also verify Orbit's mission-control response language.

## A-MORE-ERROR-HANDLING-01 — Reject additional malformed input

**Aim:** Verify that the parser rejects an empty command, unexpected arguments, repeated
date markers, event markers in the wrong order, and an event whose end date precedes its
start date. It must continue processing after every error without changing task state.

**Inputs:** Submit an empty command; add extra text to `list`; use duplicate `/by`, `/from`,
and `/to` markers; put `/to` before `/from`; enter a reversed event range; then add a Todo
using an uppercase command and tab separator, list it, and enter `bye`.

**Expected output:** Each invalid command receives a specific `Navigation alert:` response.
The uppercase, tab-separated Todo command succeeds, proving whitespace and command case are
handled safely. The final list contains only that Todo. Exact output is recorded in
`test/ui-test-cases.json`.

Storage tests additionally verify that missing files are harmless, malformed and reversed
saved records are skipped, and an unusable save destination raises a recoverable I/O error.

## A-MORE-TESTING — Expanded automated coverage

JUnit tests cover the remaining automatable behavior outside the JavaFX window:

- `CommandExecutionTest` verifies modifying-command persistence, read-only command delegation,
  exit signaling, and conversion of save failures into `ChatbotException` messages.
- `UiTest` verifies command input trimming and every console-output branch for session, task,
  schedule, and search messages, including singular, plural, matching, and empty results.
- `TaskTest` verifies status transitions, escaped save data, formatted dated tasks, and rejection
  of null or reversed event dates.
- `ChatbotTest` verifies both normal startup and recovery when the data path cannot be read.

JavaFX stage rendering, resizing, keyboard shortcuts, automatic scrolling, and visual styling
remain in the manual smoke test because they require a graphical environment. They should be
checked at the minimum and default window sizes on the target OS.

## C-SORT-01 — Sort tasks alphabetically and persist their order

**Aim:** Verify that `sort` orders mixed task types by description without regard to
capitalization, preserves the relative order of descriptions equal under that comparison,
renumbers the tasks, and leaves the sorted order available to subsequent commands.

**Initial data:** An incomplete Todo named `zebra task`, a completed Deadline named
`Alpha deadline`, and an incomplete Event named `alpha deadline`, in that order.

**Inputs:** `sort`, `list`, then `bye`.

**Expected output:** The sort confirmation and following list both show the completed
Deadline first, then the Event whose description differs only by case, and the Todo last.
This confirms case-insensitive stable ordering and renumbering. The exact complete output
is recorded in `test/ui-test-cases.json`. Persistence of the new order is also checked by
`CommandTest` against the saved data file.
