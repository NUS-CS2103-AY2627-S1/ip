---
name: test-ui
description: Record and run exact-output console UI tests for this Java project. Use when given command sequences and expected console output, when asked to maintain test/ui-test-plan.md, or when asked to run fail-fast text UI tests and show the test session.
---

# Test UI

Turn the user's console commands and expected outputs into repeatable UI test cases, then run them with the bundled deterministic runner.

## Record test cases

1. Use `test/ui-test-plan.md` as the source of truth. Create it if missing and retain its configuration section.
2. Record every requested test case before running it. Each case must have:
   - a unique `### <ID>: <title>` heading;
   - one single-line `**Aim:**`;
   - an `**Inputs:**` fenced `text` block containing the commands in order; and
   - an `**Expected output:**` fenced `text` block containing the complete expected standard output.
3. Preserve spaces, blank lines, punctuation, and command order. Comparison is exact except that CRLF and LF line endings are treated as equivalent.
4. Do not invent a missing expected output. Ask the user for it when it cannot be derived from an explicitly supplied expected result.
5. Unless the user asks to append or retain existing cases, make the test-case section reflect the test list in the current request.

## Run tests

Run the following command from the repository root:

```powershell
python .agents/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

The runner verifies Java 25, compiles every Java source under the configured source directory into an isolated temporary directory, and launches a fresh program process for each test case. It compares the complete standard output with the expected output.

The runner prints the aim, console input, and console output for every passing case. On the first failure it prints the actual output, expected output, and unified diff, exits with a nonzero status, and does not execute later cases.

## Report results

- Include the runner's console-session record in the response so the user can inspect what was entered and printed.
- On failure, stop the test workflow immediately and identify the first failing case. Report its actual and expected outputs without running later cases or modifying the application.
- On success, report how many cases passed and link to `test/ui-test-plan.md`.

## Resource

`scripts/run_ui_tests.py` parses the Markdown plan and performs the fail-fast comparison using only Python's standard library.
