---
name: test-ui
description: Run the console UI test cases in src/test/ui-test-plan.md and compare each session's output against the expected output. Use after every code update, and when asked to test command-line interactions or verify console output against the test plan.
---

# Test UI

Runs the console UI test cases defined in `../../../src/test/ui-test-plan.md`. Each case holds
its aim, its console inputs, and the complete expected output of one session.

This skill shares its runner with the Codex skill in `.codex/skills/test-ui/`, so
both tools test the project the same way. The full skill documentation, including
the test-plan format, lives in `.codex/skills/test-ui/SKILL.md`.

## Run tests

1. Update `../../../src/test/ui-test-plan.md` first, so its build command, run command, and
   test cases describe the program's current interface. Moving or renaming source
   files usually means the build and run commands need changing too.
2. Run the project's own runner from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py src/test/ui-test-plan.md
   ```

   Never substitute a one-off script written for the occasion: the point is to
   test the project the way the project defines.
3. The runner builds once, runs the application once per case, prints each
   transcript, and stops at the first failure.
4. On a failure, report the failed case's aim with its expected and actual
   output, and do not run the later cases.
