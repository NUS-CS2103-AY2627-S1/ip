---
name: test-ui
description: Run and verify scripted console user-interface tests for this Java project. Use when asked to test the chatbot or console UI, execute command/input scenarios, compare actual console output with expected output, or display a console test-session transcript.
---

# Test UI

Run the test cases recorded in `test/ui-test-plan.md`. Each case supplies an aim, the shell command that launches the program, console inputs, and the exact expected console output.

## Run the plan

1. Inspect and update `test/ui-test-plan.md` when the UI behavior or its intended output changes. Keep every test case in the documented format.
2. Use Java 25. On macOS, run `sdk use java 25.0.3.fx-zulu` in the same shell that launches the runner.
3. Run the checker from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
   ```

4. Report the transcript printed by the checker. It records every case's console input and output.

The checker stops at the first failed case. It prints that case's actual and expected output and exits unsuccessfully; do not run remaining cases.

## Test-plan format

Use this structure for every case. `Command`, `Inputs`, and `Expected output` must be fenced blocks. `Command` must launch a fresh program process, and `Inputs` must include its terminating command when applicable.

````markdown
### <unique test-case name>
- Aim: <behavior being verified>
- Command:
  ```sh
  <shell command that builds if necessary and starts the program>
  ```
- Inputs:
  ```text
  <one console command per line>
  ```
- Expected output:
  ```text
  <complete, exact console output>
  ```
````

Keep expected output exact, including prompts, dividers, blank lines, and final newlines. The runner normalizes only line-ending style (`CRLF` versus `LF`).
