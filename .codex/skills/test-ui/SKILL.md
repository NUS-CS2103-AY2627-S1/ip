---
name: test-ui
description: Compile and test this project's command-line UI from command and expected-output cases recorded in test/ui-test-plan.md. Use after Java code changes that may affect commands or console output.
---

# Test the command-line UI

Keep the test cases in `test/ui-test-plan.md`. Each case must have an aim and paired `input` and `expected` fenced blocks. Expected output is compared exactly, including spaces and divider lines.

Run all cases from the repository root:

```powershell
powershell -ExecutionPolicy Bypass -File .codex/skills/test-ui/scripts/run-ui-tests.ps1
```

The runner verifies that Java 25 is active, compiles the sources into a temporary directory, and executes each case in a fresh program instance. It prints the console input and actual output for every case. On the first failure, it prints the expected output and exits immediately with a nonzero status.

When a requested command or output is not covered, update the test plan before running it. Report the transcript and whether all cases passed.
