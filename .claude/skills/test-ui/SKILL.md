---
name: test-ui
description: Runs this project's console UI test cases (test/ui-test-plan.md) against the compiled XiaoZhi chatbot and reports pass/fail with a full session transcript. Use this after any change to XiaoZhi's or a Task subclass's behavior, and before committing.
---

# test-ui

Verifies XiaoZhi's console behavior against the test cases recorded in
`test/ui-test-plan.md`, so changes get checked mechanically instead of by
eyeballing output.

## When to use this

Invoke this skill after any code change that could affect what the program
prints or how it parses commands — new commands, changed message wording,
changed parsing logic, changed task formatting. Invoke it again before
committing such a change.

## How to run it

From the project root:

```
python3 test/run_ui_tests.py
```

This script:

1. Compiles every `.java` file under `src/main/java` into `test/_build`.
2. Parses each `## Test N: ...` section out of `test/ui-test-plan.md` —
   its **Aim**, **Input** (stdin lines fed to the program, one per line),
   and **Expected output** (the exact full console output expected back,
   banner and greeting included).
3. Runs the compiled program once per test case, feeding it that test's
   input, and prints the input and actual output as it goes — this is the
   console record of the test session.
4. Compares actual output to expected output for each test case in order.
   On the first mismatch, it immediately stops, prints both the expected
   and actual output for that test case, and exits with a non-zero status.
5. If every test case matches, it prints a summary line and exits 0.

## If a test fails

Do not continue to the next test case. Report the failing test's name, its
aim, and the expected vs. actual output shown by the script, then decide
with the user whether the code or the test plan needs to change.

## Keeping the test plan current

Whenever a command's behavior changes on purpose (new command, changed
message wording, new field in a task type, etc.), update the matching test
case(s) in `test/ui-test-plan.md` — either regenerate the expected output
by running the new code by hand and capturing its output, or reason
through what should change and edit the expected block directly. Add a new
`## Test N: ...` section, following the existing format, for any new
command or behavior that isn't covered yet.
