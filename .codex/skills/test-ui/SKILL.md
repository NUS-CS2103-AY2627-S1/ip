---
name: test-ui
description: Record and run fail-fast console UI tests from lists of input commands and expected outputs. Use after every application or test-code update, and when testing a command-line or text-based program, updating test/ui-test-plan.md, capturing a console test transcript, or diagnosing a mismatch between expected and actual terminal output.
---

# Test UI

After every application or test-code update, review and update `test/ui-test-plan.md` if needed, then run its console test cases. Run each case in a fresh program process, compare its complete output exactly, and stop at the first failure.

## Record the test plan

1. Treat the user's command and expected-output lists as ordered pairs. Preserve their text exactly; do not invent missing output.
2. Create or update `test/ui-test-plan.md` from the repository root.
3. Record the build command, run command, working directory, timeout, and comparison rule under `## Configuration`.
4. Give every case a stable ID. Record its aim, all input lines in order, and the complete expected console output, including startup and shutdown text.
5. Use this exact structure so the bundled runner can parse it:

   ````markdown
   # UI Test Plan

   ## Configuration

   - Build command: `javac -d _temp/ui-test-classes src/main/java/*.java`
   - Run command: `java -cp _temp/ui-test-classes Soar`
   - Working directory: `.`
   - Timeout seconds: `10`
   - Comparison: `exact after normalizing CRLF and CR line endings to LF`

   ## Test cases

   ### TC-001 — Exit command

   - Aim: Verify that the program exits cleanly.

   #### Inputs

   ```text
   bye
   ```

   #### Expected output

   ```text
   <complete output from program startup through exit>
   ```
   ````

Use `none` for the build command when no build step is needed. Keep each fenced block literal: spaces, blank lines, punctuation, and the final newline are significant. Use one test case per independently runnable scenario; put multiple input lines in a case when later commands depend on earlier state.

## Run the tests

1. Confirm the configured runtime meets the project requirements. For this project, run `java --version` and require Java 25 before building or testing.
2. Run from the repository root:

   ```powershell
   python .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
   ```

3. Do not continue with later cases after a failure. The runner enforces this and exits nonzero for build errors, timeouts, nonzero program exits, malformed plans, and output mismatches.
4. Do not silently update expected output to make a failing test pass. Change it only when the user confirms the new behavior is intended.

## Report the result

Show the runner's complete `Console test session` record. On success, report the number of passed cases. On failure, identify the failed case and show its input, actual output, and expected output exactly as emitted by the runner. If output is visually similar, also mention that whitespace and final newlines are compared exactly.

## Resource

`scripts/run_ui_tests.py` parses the plan, performs the optional build once, runs cases in order, normalizes only line-ending style, prints the console transcript, and terminates on the first failure.
