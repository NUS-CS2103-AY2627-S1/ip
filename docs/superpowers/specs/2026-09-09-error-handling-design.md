# NiuLai Error-Handling Design

## Purpose

Harden NiuLai against malformed commands, invalid task data, damaged or inaccessible storage, and missing GUI
resources without changing its small local-task-manager scope. Expected usage and environment failures must produce
specific, actionable messages, preserve as much user data as possible, and leave in-memory and on-disk state
consistent.

## Product Decisions

- Deadline and event values remain free-form. Recognizable temporal values are parsed and validated strictly; text
  that does not have a recognized temporal shape remains unchanged.
- Task identity uses the task type and every user-provided detail after surrounding whitespace is removed, internal
  whitespace is collapsed, and text is converted to lower case. Completion status is not part of identity.
- A partly corrupt data file is recovered record by record. Valid unique tasks remain available, while invalid or
  duplicate records are skipped and reported by line number.
- Expected failures are handled at the narrowest boundary that understands them. Unexpected runtime failures are not
  converted into misleading user errors.

## Architecture

Input follows this path:

```text
raw command
    -> Parser: normalize whitespace and validate command grammar
    -> Task model: validate task fields and comparable temporal values
    -> TaskList: enforce uniqueness
    -> Command: mutate, persist atomically, and roll back failed mutations
    -> Storage: load valid records, report recovery issues, and protect damaged data
    -> NiuLai/Ui: present actionable errors and keep the session usable
```

The parser owns command syntax. Model classes own invariants that must hold regardless of whether data came from a
command or disk. `TaskList` owns collection uniqueness. `Storage` owns filesystem consistency and recovery metadata.
`NiuLai` coordinates the session and converts typed expected failures into UI messages.

## Command Normalization and Grammar

`Parser` will normalize input independently so direct parser callers receive the same behavior as CLI and GUI users.
Leading and trailing whitespace is removed. Runs of horizontal whitespace are replaced with one ASCII space in task
descriptions, search keywords, and temporal fields. Line breaks and other non-whitespace control characters are
rejected because they can corrupt the display or line-oriented storage format.

Command keywords remain lowercase and case-sensitive. `bye`, `list`, and `undo` accept no arguments. Text following
one of those commands is rejected as an unexpected argument rather than treated as an unknown command.

Parameterized commands require at least one horizontal whitespace character after the keyword. Task-number commands
accept exactly one unsigned decimal integer. Signs, decimal points, additional tokens, values larger than `int`, zero,
and out-of-range values receive distinct missing, format, or range errors.

Task creation uses whitespace-delimited parameter markers:

- `todo <description>` has no parameter marker.
- `deadline <description> /by <value>` requires exactly one `/by` marker.
- `event <description> /from <start> /to <end>` requires exactly one marker of each kind, in that order.

Missing, repeated, or misplaced markers are rejected. Marker-like substrings that are not surrounded by whitespace
remain ordinary text. Pipes, backslashes, and normal punctuation remain valid because persistence escapes them.

`find` requires a nonblank normalized argument. A value with a recognized date shape is parsed strictly; an impossible
date receives the date-format error instead of silently becoming a keyword search. Other text remains a keyword.

## Temporal Validation

A focused temporal parser will classify values without forcing all values into a date type. It will recognize the
project's existing formats and common equivalents:

- full dates: `uuuu-MM-dd` and `d/M/uuuu`;
- full date-times: either full date followed by `HHmm` or `HH:mm`;
- time-only values: `HHmm`, `HH:mm`, `ha`, and `h:mma`, case-insensitively;
- English month/day values, with an optional ordinal suffix and optional year or time, such as `Feb 28`, `June 6th`,
  or `Aug 6th 2pm`.

Resolver-style strict parsing rejects impossible values such as `2026-02-30`, `30/2/2026`, `Feb 30`, `24:30`, and
`13pm`. A month/day without a year validates the month and day independently; February 29 remains valid because it is
possible in a leap year.

Free-form text such as `tomorrow morning` is preserved. A value that lexically matches one of the supported temporal
shapes but fails parsing is invalid rather than free-form.

Event ordering is checked only when the endpoints can be compared without guessing:

- two full dates, two full date-times, two month/day values, or two times are compared directly;
- a full date-time followed by a time-only value applies the start date to the end time;
- a month/day with a time followed by a time-only value applies the start month/day to the end time;
- all other mixed or free-form combinations are accepted without an ordering claim.

For comparable endpoints, the end must be strictly later than the start. Equal or reversed values are rejected.
Deadline values have no relative-order invariant but still receive strict validation when recognizable.

## Task Identity and State Preconditions

Each task exposes an immutable identity consisting of its type and normalized textual fields. A todo's identity is its
description; a deadline adds its deadline value; an event adds both endpoints. Identity comparison ignores letter case
and collapses whitespace but does not treat different textual date formats as equivalent.

`TaskList.add` and indexed insertion check identity before mutation. A duplicate raises a model-specific duplicate
exception. `AddCommand` translates it into a user-facing message; `Storage` treats a later duplicate record as a
recoverable line issue and retains the first record.

`mark` rejects an already completed task, and `unmark` rejects an already pending task. These rejected no-op commands
do not save data or replace the previous undo action. Existing safeguards remain for invalid task numbers and for
`undo` when no successful state-changing command exists.

## Storage Recovery and Consistency

`Storage.load` will return a result containing the recovered `TaskList` and zero or more line issues. A missing data file
returns an empty successful result. Blank lines remain ignorable, and a UTF-8 byte-order mark remains supported on the
first line.

Each nonblank line is parsed independently. Invalid field counts, types, statuses, escapes, blank required fields,
invalid recognizable temporal values, reversed event endpoints, and duplicate tasks skip only that line. The result
records the one-based line number and a concise reason. The UI reports a summary with the skipped line numbers; it does
not expose stack traces.

An invalid UTF-8 stream, access denial, a data path that is a directory, or another failure that prevents reliable
record iteration is an unrecoverable load failure. NiuLai starts with no loaded tasks, reports that persistence changes
are disabled for the session, and still permits `list`, `find`, and `bye`. Mutating commands fail safely and retain the
current in-memory state until the file is fixed and the application is restarted.

After partial recovery, `Storage` marks the original file as needing protection. Before the first replacement save, it
copies the original bytes to the first available sibling name in this sequence:
`niulai.txt.bak`, `niulai.txt.bak.1`, `niulai.txt.bak.2`, and so on. It never replaces an existing backup. If backup
creation fails, the requested task mutation is rolled back and the original file remains untouched.

Normal saves serialize the complete validated list to a temporary file in the same directory, then replace the data
file with an atomic move when supported and a replacement move otherwise. Write, move, permission, or cleanup failures
are surfaced as storage failures and cause the command to restore the exact prior in-memory state. A successful save
updates the storage snapshot used to detect external file changes. If the data file changes after load because another
process or application instance edited it, NiuLai rejects the save rather than overwriting that change and asks the
user to restart.

## Error Presentation and GUI Startup

User messages state the failed operation, the invalid portion where useful, and a valid example or recovery action.
Command messages remain consistent between CLI and GUI through `Ui`. Partial-load warnings list skipped lines and the
backup behavior. Unrecoverable load messages explain that state-changing commands are disabled and advise fixing the
data file permissions/content before restarting.

FXML and CSS resources are resolved explicitly before use. A missing or unreadable required resource produces one
concise fatal startup diagnostic naming the missing application resource. The GUI does not misreport resource failures
as command errors. Detailed causes remain attached to exceptions for development diagnostics.

## Testing

JUnit tests will cover the highest-value behavior:

- parser normalization, blank input, exact no-argument commands, parameter count/order, control characters, task-index
  syntax and overflow, and invalid recognizable dates;
- task identity, case/whitespace duplicates, distinct types and details, free-form temporal values, comparable event
  ordering, and invalid temporal shapes;
- storage round trips, escaped fields, missing files, mixed valid/corrupt lines, duplicate records, invalid UTF-8,
  directory paths, recovery backups, external modifications, and atomic-save failure behavior;
- command rollback for add, delete, status changes, and undo, including storage and backup failures;
- NiuLai integration behavior for recovery warnings, duplicate errors, no-op status errors, continued use after
  recoverable failures, and read-only behavior after unrecoverable loads;
- GUI adapters and explicit startup-resource checks where they can be tested without launching a full desktop session.

`test/ui-test-plan.md` will be updated with complete end-to-end transcripts for syntax errors, duplicates, impossible
dates, equal/reversed event endpoints, and corrupt-file recovery. Verification uses Java 25 and runs the complete
Gradle JUnit suite, Checkstyle, build, and the project UI-test runner. The UI runner stops at the first mismatch as
required by the repository instructions.

## Compatibility and Scope

Existing valid commands and saved files remain compatible. Free-form temporal text, pipes, backslashes, and current
date formats remain supported. Whitespace is canonicalized for new commands and recovered records, so a later save may
rewrite spacing while preserving meaning.

This work does not add new task types, editing commands, network synchronization, or a multi-level undo history. It
does not attempt natural-language date interpretation beyond recognizing the explicitly listed shapes.
