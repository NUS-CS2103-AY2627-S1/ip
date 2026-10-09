# Console UI test history

This file contains concise records of completed runs of `test/ui-test-plan.md`. Git history retains the detailed changes and earlier full transcripts.

For a successful run, record the date, Java version, cases executed, and overall result. For a failed run, additionally record only the first failing test case, command, actual output, and expected output.

## Sessions

### 2026-08-29 — PASS (correct UI-003 spacing)

Result: All ten listed UI test cases passed under Java 25.0.4 after correcting the missing leading space in UI-003's expected `blah` error output.

### 2026-08-29 — PASS (find tasks by keyword)

Result: All ten listed UI test cases passed under Java 25.0.4.

### 2026-08-29 — PASS (Java package organization)

Result: All nine listed test cases passed under Java 25.0.4 after organizing the source classes into packages.

### 2026-08-29 — PASS (command hierarchy foundation)

Result: All nine listed test cases passed under Java 25.0.4 after introducing the command hierarchy and extracting exit, list, and date-filter commands.

### 2026-08-29 — PASS (instance-based Sumo coordinator)

Result: All nine listed test cases passed under Java 25.0.4 after converting `Sumo` into an instance-based coordinator.

### 2026-08-29 — PASS (TaskList class extraction)

Result: All nine listed test cases passed under Java 25.0.4 after task collection operations were extracted into `TaskList`.

### 2026-08-29 — PASS (Parser class extraction)

Result: All nine listed test cases passed under Java 25.0.4 after command parsing was extracted into `Parser`.

### 2026-08-29 — PASS (Storage class extraction)

Result: All nine listed test cases passed under Java 25.0.4. No expected console or persisted-file output changed after moving file access and stored-record parsing into `Storage`.

### 2026-08-29 — PASS (UI class extraction)

Result: All nine listed test cases passed under Java 25.0.4. No expected console output changed after moving console input and output into `Ui`.

### 2026-08-29 — PASS (on-date filtering)

Result: All nine listed test cases passed under Java 25.0.4. UI-009 confirmed exact-date deadlines, inclusive multi-day event matching, alternate date input, filtered numbering, and empty results.

### 2026-08-29 — PASS (typed date/time parsing and formatting)

Result: All eight listed test cases passed under Java 25.0.4. UI-005 confirmed immediate persistence, UI-006 confirmed typed dates/times survive restart, and UI-008 confirmed both requested input styles and display formats.

### 2026-08-28 — PASS (relative-path and clean-start verification)

Result: All seven listed test cases passed under Java 25.0.4. UI-005 confirmed immediate persistence after each task-list mutation, and UI-007 confirmed startup succeeds when both the data folder and file are absent.

### 2026-08-21 — PASS

Result: All listed test cases passed under Java 25.0.4.

### 2026-08-27 — PASS (read-on-startup verification)

Result: All six listed test cases passed under Java 25.0.4. UI-006 confirmed that tasks and completion state were reconstructed after restarting Sumo.

### 2026-08-27 — PASS (write-only persistence verification)

Result: All five listed test cases passed under Java 25.0.4. UI-005 confirmed that every task-list mutation immediately rewrote `data/sumo.txt` with the expected content.

### 2026-08-21 — PASS (post-agent-rule verification)

Result: All listed test cases passed under Java 25.0.4 after updating `AGENTS.md`.

### 2026-08-24 — PASS (error-handling verification)

Result: All listed test cases passed under Java 25.0.4.

### 2026-08-24 — PASS (delete-task verification)

Result: All listed test cases passed under Java 25.0.4.
