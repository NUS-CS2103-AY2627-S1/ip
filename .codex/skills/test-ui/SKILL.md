---
name: test-ui
description: Run this project's scripted console UI regression tests. Use after changing Java code that affects command-line input, output, command behavior, or application startup and exit behavior; update test/ui-test-plan.md when the observable behavior changes.
---

# Console UI tests

1. Update `test/ui-test-plan.md` if the changed behavior needs new or revised coverage. Each `## Test case:` section must contain an aim plus `### Inputs` and `### Expected output` fenced `text` blocks. Every input line is a command sent to one fresh program run.
2. Run the plan with the bundled Python runtime when `python` is unavailable:

   ```powershell
   & 'C:\Users\Tze Yuon\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe' .codex/skills/test-ui/scripts/run_ui_tests.py
   ```

   Optionally provide another plan with `--plan path/to/plan.md`.
3. Read the printed transcript. It records each test case's console input and output. On the first mismatch, the runner stops and prints the expected and actual output; fix the code or plan before continuing.

The runner compiles all files in `src/main/java` with `javac`, starts `CBT`, and compares normalized output exactly (line endings only are normalized). It leaves compiled test classes in `_temp/ui-test-classes`.