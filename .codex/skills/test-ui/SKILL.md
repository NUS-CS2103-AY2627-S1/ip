---
name: test-ui
description: Run command-line UI tests from test/ui-test-plan.md, comparing each console output exactly and recording the test session. Use when testing interactive command flows or validating chatbot console output.
---

# Test UI

Run the project’s planned UI tests using `test/ui-test-plan.md`. Each test case supplies a sequence of console commands and its complete expected standard output.

## Test plan

Before running tests, ensure every test case in `test/ui-test-plan.md` has:

- a name and aim;
- an `Inputs` text block containing the commands, one per line; and
- an `Expected output` text block containing the complete standard output to compare.

Do not silently change expected output to make a failing test pass. Update the plan only when the user confirms the intended behaviour has changed.

## Run tests

From the repository root, run:

```powershell
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner uses the program command in the test plan. To temporarily override it, pass `--command`, for example:

```powershell
python .codex/skills/test-ui/scripts/run_ui_tests.py --command "java src/main/java/Oreo.java"
```

The runner prints a record of each test case’s console input and output. It compares output exactly except for line-ending style and one final newline. On the first failure it stops, reports the actual and expected output, and returns a non-zero exit status.

## Resource

`scripts/run_ui_tests.py` uses only the Python standard library. Keep test cases in `test/ui-test-plan.md`; do not scatter them among scripts or ad-hoc command lines.
