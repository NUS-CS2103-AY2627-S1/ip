---
name: test-ui
description: Run console UI regression tests for this Java chatbot project from test/ui-test-plan.md. Use when asked to test command-line interactions, compare lists of console commands against expected outputs, verify missing-argument cases, or produce a record of test input/output.
---

# Test UI

Run console UI tests from `test/ui-test-plan.md`. Each test case records its aim,
console input, and expected console output.

## Test Plan Format

Use one `###` heading per test case. Each case must contain:

- `Aim:` one short sentence explaining what the test verifies.
- `Inputs:` followed by a fenced `text` block containing the exact console input.
- `Expected output:` followed by a fenced `text` block containing the exact expected console output.

The first input line should include the user name, because this chatbot asks for
the name before accepting commands.

Cases that verify persistence may also include:

- `Initial storage:` followed by a fenced `text` block containing the exact
  contents to write to `data/duke.txt` before the chatbot starts.
- `Expected storage:` followed by a fenced `text` block containing the exact
  expected final contents of `data/duke.txt`.

## Run Tests

From the repository root, run:

```bash
python3 .codex/skills/test-ui/scripts/run-ui-tests.py
```

The script compiles Java source files recursively under `src/main/java`, runs
`mrchatbot.MrChatbot` once per test case, and compares exact stdout with the
expected output. It prints a console-session record containing the input,
expected output, and actual output for each tested case.

If a test case fails, the script terminates immediately, reports the failing
test case, shows the actual and expected outputs, prints a unified diff, and
exits with a non-zero status.

## Update Tests

When adding a UI behavior, update `test/ui-test-plan.md` with at least one case
covering the main success path. For command parsing features, also add missing
argument or malformed input cases when relevant.
