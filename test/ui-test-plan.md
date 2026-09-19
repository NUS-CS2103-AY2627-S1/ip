# UI Test Plan

This file summarizes UI-test coverage. Exact command and expectation data lives in
`test/ui-test-cases.json`; `test/run_ui_tests.py` expands compact fixtures and performs
the comparisons without loading full transcripts into the agent context.

## Test configuration

- Application: Nudge command-line interface retained alongside the JavaFX interface
- Working directory: repository root
- Java runtime: Java 25
- Runner: `python3 test/run_ui_tests.py`
- Targeted runner: `python3 test/run_ui_tests.py --case UI-001`
- Isolation: compile the non-JavaFX sources once, then start a fresh Nudge process in a temporary working directory for every case
- Input: send every recorded command for a case in one batch
- Comparison: exact output after normalizing only `CRLF` to `LF`
- Timeout: five seconds per case
- Failure policy: stop after the first failing case and leave later cases unrun
- Reporting: counts only on success; focused diff and `_temp/ui-test-failure.txt`
  containing complete evidence on failure

## Test cases

| ID | Coverage | Expanded commands |
| --- | --- | ---: |
| UI-001 | Add, list, mark, and unmark todos | 6 |
| UI-007 | Store 101 tasks and update task 101 | 103 |
| UI-002 | Add, list, and mark a deadline | 4 |
| UI-003 | Add, list, and mark an event | 4 |
| UI-004 | Recover after basic input errors | 6 |
| UI-015 | Find tasks by description keyword | 10 |
| UI-016 | Sort deadline positions, renumber tasks, and save the order | 10 |
| UI-017 | Sort empty and no-deadline lists and reject invalid syntax | 7 |
| UI-008 | Delete tasks and renumber the list | 9 |
| UI-009 | Reject invalid delete indices | 9 |
| UI-005 | Reject invalid mark and unmark indices | 10 |
| UI-011 | Load tasks from disk at startup | 2 |
| UI-012 | Recover from invalid saved data | 2 |
| UI-014 | Load legacy saved task data | 2 |
| UI-013 | Preserve tasks after a save failure | 3 |
| UI-010 | Save task-list changes to disk | 8 |
| UI-006 | Validate deadline and event input | 19 |

The fixture order above is the execution order. Every case starts with an empty task
list and records `bye` explicitly as its final command.

## Fixture format

Each case records a stable ID, title, aim, setup, and ordered steps. A step contains
an `input` and semantic `expect` data. The runner centralizes exact output wording for
standard responses such as `added`, `list`, `matches`, `sorted`, `marked`, `unmarked`,
and `removed`.
Use `message` for an exact one-line response or `lines` for an exceptional raw response.

A step can include a closed `repeat` range and `{i}` placeholders. UI-007 uses this to
represent commands 1–101 without copying 101 nearly identical transcripts.
