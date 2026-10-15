---
name: test-ui
description: Run fail-fast console UI tests for this Java chatbot using command and expected-output lists recorded in test/ui-test-plan.md. Use when asked to test the chatbot UI, verify command-line behavior, run the UI test plan, or add console test cases and show their transcripts.
---

# Test UI

Use the deterministic runner in `scripts/run_ui_tests.py` to test the chatbot one test case at a time.

## Workflow

1. Read `test/ui-test-plan.md` from the repository root.
2. If the user supplied new cases, record them in that file before testing. Preserve the documented JSON format:
   - one `## Test case:` section per test case;
   - a non-empty `### Aim`;
   - a JSON list under `### Commands and expected outputs`;
   - one object per command, containing `command` and `expected_output`;
   - `expected_output` is the exact list of output lines produced in response to that command.
   - For a startup-error scenario, use an empty command list and provide `### Expected startup output` as a JSON list of exact output lines.
   - Add `### Initial files` with a JSON object mapping relative paths to their text contents when a test requires pre-existing data.
   - For a scenario that restarts the chatbot, add `"new_session": true` to the first command after a completed `bye`. This starts a fresh process while preserving the same test-specific data folder.
3. Ensure each command sequence ends with the chatbot's exit command so the process terminates normally.
4. From the repository root, run:

   ```powershell
   python .codex/skills/test-ui/scripts/run_ui_tests.py --main-class nerrad.Nerrad
   ```

5. Show the runner's console transcript to the user. It includes the input commands and complete program output for each executed test case.
6. Treat a nonzero exit code as a failed session. Do not run later test cases after a failure. Report the failed command plus the actual and expected output shown by the runner.

## Comparison rules

- The runner starts each test case with a fresh temporary working directory, so saved data cannot leak into another case.
- A test case may contain multiple chatbot sessions using `new_session` after a `bye`; those sessions share that test case's temporary data folder.
- The runner creates any declared initial files inside that temporary directory. Their paths must be relative and cannot leave it.
- It associates responses with commands using the chatbot prompt, `You: ` by default.
- It normalizes Windows and Unix line endings, but otherwise compares output lines exactly. Spaces and blank lines matter.
- It requires Java and `javac` version 25, compiles sources into a temporary directory, and does not modify compiled files in the repository.

Use `--help` to see options for a different plan path, source directory, main class, prompt, or timeout.
