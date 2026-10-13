---
name: test-ui
description: Run console UI acceptance tests from test/ui-test-plan.md, compare each command's output with its expected output, stop on the first failure, and record the session.
---

# Test UI

Use this project-specific skill for scripted tests of the application's console UI.

## Test-plan contract

Treat `test/ui-test-plan.md` as the source of truth and keep it as the audit record. Each test case must be listed in execution order with:

- a unique `TC-...` heading and a short title;
- an `Aim` bullet;
- a `Command` bullet containing the command to run from the repository root;
- an `Inputs` fenced block containing the exact stdin to send (leave it empty when no input is needed); and
- an `Expected output` fenced block containing the exact console output.

If the user supplies a list of commands, inputs, and expected outputs, convert each command into a separate test case and record it in the plan before running it. Do not invent expected output. Add relevant session information to the plan, including the Java version, comparison rule, and latest session transcript.

The expected output is compared exactly after normalizing CRLF to LF and removing one final newline from both sides. Preserve all other whitespace. The captured console output combines stdout and stderr. A non-zero exit code is a failure even if the text matches.

## Execution workflow

1. Read and validate the complete test plan before launching any test. Resolve commands relative to the repository root.
2. Ensure the Java runtime used for this Java project is Java 25. If it is not available or is a different major version, stop and report that no UI tests were run.
3. Run `.codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md --cwd .` (add `--timeout` only when the plan requires a different per-case limit).
4. Run test cases in their listed order, passing each `Inputs` block exactly to stdin. The runner must stop immediately after the first non-zero exit, timeout, or output mismatch; do not continue with later cases.
5. Show the resulting console-session record, including each executed command, its console input, console output, and status. On failure, show both the actual and expected output and identify the failed case. The runner also records this transcript under `## Latest test session` in `test/ui-test-plan.md`.

Testing is diagnostic: do not change application source code or silently edit test expectations to make a case pass. If the plan is malformed or has no test cases, report that before starting the application.
