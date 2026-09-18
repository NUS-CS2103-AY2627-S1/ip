---
name: test-ui
description: Run console UI test cases for this Java project from lists of commands and expected outputs, stopping at the first failure and showing the complete test transcript.
metadata:
  short-description: Run the project's console UI test plan
---

# Test UI

Use this skill for black-box testing of Chausistant through its console input
and output. The test cases and their current expected behavior are recorded
in `test/ui-test-plan.md`.

## Test plan

Before running tests, make sure every requested test case is recorded in
`test/ui-test-plan.md`. Each case must contain:

- an `Aim:` line explaining what the case verifies;
- an `### Inputs` fenced `text` block containing one console command per line;
- an optional `Match:` line, either `contains` or `exact` (default:
  `contains`); and
- an `### Expected output` fenced `text` block containing the expected console
  output for that input session.

The default `contains` mode is useful for this application because every run
prints a startup banner. It checks that the expected output block appears
verbatim in the actual console output. Use `Match: exact` when the expected
block includes the complete stdout transcript.

## Run the tests

From the repository root, run:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

The runner compiles `src/main/java/Chausistant.java` with Java 25, then starts
a fresh program session for each test case, sending the listed commands as
stdin. It reports the console input and output for every completed case.

If a case fails, stop immediately. Do not run later cases. Report the failed
case's actual output and expected output, including a diff when useful. A
non-zero exit status from compilation or the program is also a failure.

When the user supplies new command/expected-output lists, update
`test/ui-test-plan.md` first, then run the runner. Preserve the user's command
order and expected text; do not silently weaken an exact test into a partial
match.
