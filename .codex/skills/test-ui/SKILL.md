---
name: test-ui
description: Run documented command-line UI test cases against the project, compare each session with its expected output, and stop at the first failure while reporting the console transcript.
---

# Test UI

Use this skill for manual-style, end-to-end tests of the program's text interface. Test cases are recorded in `test/ui-test-plan.md`; do not invent undocumented cases during a run.

## Test plan

Maintain `test/ui-test-plan.md` with the project command configuration and one section per test case. Each case must include:

- an `Aim` explaining what behavior is being tested;
- an `Inputs` fenced `text` block containing the commands sent to one fresh program session, one command per line; and
- an `Expected output` fenced `text` block containing the exact output expected from that session.

The plan may specify an optional build command, working directory, timeout, and
comma-separated `Reset files` list. Reset files are removed before each test
case, which is useful for isolating stateful programs. A test case may also
include an optional fenced `Setup` command block to seed state before the
program starts. The bundled runner uses the repository root by default and
treats output differences other than line-ending style as failures.

## Run the tests

From the repository root, run:

```text
python .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md
```

The runner executes the optional build command once, then starts a fresh program process for each test case. It prints each session's console input and output. A failing case prints the expected and actual output, returns a non-zero exit code, and stops without running later cases.

Do not continue after a failure or silently update expected output. Report the failing test case and its actual-versus-expected output so the user can decide whether the implementation or the plan should change.

The runner uses only Python's standard library; no package installation is required.
