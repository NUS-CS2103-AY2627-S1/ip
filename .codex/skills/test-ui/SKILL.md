---
name: test-ui
description: Run command-line UI test cases for this Java project from test/ui-test-plan.md, compare actual and expected console output, and stop at the first failure.
---

# Test the command-line UI

Use this skill when the user asks to execute or verify the project's interactive console UI using scripted inputs and expected outputs. This skill is also mandatory after every code update in this repository, even when the user does not separately request testing.

## Workflow

1. After a code update, review `test/ui-test-plan.md` and update it if the change affects observable UI behavior, commands, inputs, prerequisites, or expected output. Keep the test cases aligned with the current behavior before running them.
2. Read `test/ui-test-plan.md`. Treat each `###` test-case section as an ordered test case. Each section must contain `Aim`, `Command`, `Inputs`, and `Expected output`.
3. The runner freshly compiles the current Java source with the project's existing command (`javac -d out src/main/java/*.java`) before running any UI test. If compilation fails, it records the compiler output, stops immediately, and does not run UI cases. Successful UI tests therefore use the freshly compiled classes.
4. Run the bundled `scripts/run_ui_tests.py` from the repository root using the workspace Python runtime if `python` is unavailable:

   ```powershell
   $python = 'C:\Users\aagya\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe'
   & $python .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
   ```

   The runner executes test cases in plan order, sends the listed input to each command through standard input, and compares output after normalizing line endings and the final newline only. Other whitespace is significant.
5. The runner writes the complete console input/output record to `test/ui-test-session.log` and also prints it. Preserve the record when reporting results.
6. If a test fails, do not run later test cases. Report the failing test case, actual output, and expected output from the runner. A non-zero exit code is a failed test session.
7. If all tests pass, report the number of passed cases and the transcript path.

Do not edit production code or silently change expected output to make a test pass. Update the plan when a code update changes observable behavior; otherwise leave the existing cases unchanged.
