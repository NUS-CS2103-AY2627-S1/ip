---
name: test-ui
description: Run command-line UI test cases from test/ui-test-plan.md, compare each program output with its expected output, stop immediately on failure, and preserve a readable console session record.
---

# Test UI

Use this skill for deterministic command-line UI acceptance tests where each
case supplies a command, optional stdin input, and an expected output. The
project test plan is the source of truth and must live at
`test/ui-test-plan.md`.

## Test-plan format

Create or update `test/ui-test-plan.md` before running tests. Each case must
include an **Aim**, **Command** (from the repository root), **Input** (exact
stdin lines or `None`), and complete **Expected output**. Keep setup,
working-directory, environment, timeout, and normalization rules in a short
“Test settings” section. Do not invent cases or alter expected output to make
a failure pass; ask when the plan is ambiguous.

## Execute the plan

1. Read the whole plan and resolve commands relative to the repository root.
   Use the project’s required runtime (Java 25 for this repository).
   For tests that require a clean task list, use an isolated temporary working
   directory with its own empty `data/cheecken.txt`; never overwrite or delete
   the user’s real persistence file.
2. Run cases in listed order, capturing stdout and stderr separately. Feed the
   declared input exactly. Unless the plan explicitly states normalization,
   compare output exactly, including whitespace and final newlines.
3. After each case, print a console-session record containing its name,
   command, input, actual stdout/stderr, expected output, and PASS/FAIL result.
   Preserve the record in the response, not just a summary.
4. On the first failure, terminate immediately. Report the actual and expected
   outputs clearly, plus the command and input that produced the failure. Do
   not run later cases.
5. If all cases pass, report the complete session record and a concise summary.

Prefer a temporary harness for repeatable execution, but do not add generated
logs or harness artifacts to the repository unless requested. A non-zero exit
status is a failure unless the plan explicitly expects it.
