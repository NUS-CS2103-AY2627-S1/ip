---
name: test-ui
description: Run and verify the project's documented console UI test cases after Java code changes. Use for changes that can affect interactive commands or console output.
---

# Console UI testing

Use this skill after each code update that could affect the console application. Keep the test cases in [`test/ui-test-plan.md`](../../../../test/ui-test-plan.md); do not put the cases only in a chat response.

## Maintain the test plan

Before testing, inspect the plan and update it when the changed behavior adds, removes, or changes a console command or its output. Each `## Test case:` section must contain:

- `### Aim` — the behavior being checked.
- `### Inputs` — a fenced `text` block containing the ordered console commands to send to one program run.
- `### Expected output` — a fenced `text` block containing the complete output from that run.

The plan's **Test environment** section names the compile and run commands. Keep them suitable for this repository and Java 25.

## Run the tests

Run the helper from the repository root:

```powershell
powershell -ExecutionPolicy Bypass -File .codex/skills/test-ui/scripts/run-ui-tests.ps1 -PlanPath test/ui-test-plan.md
```

The helper compiles the project with Java 25, executes every documented test case, and prints a transcript that labels each console input and console output. Output comparison normalizes line endings and a final newline only; all displayed content must otherwise match exactly.

If a test fails, stop immediately. Report the test name plus the actual and expected output exactly as printed by the helper. Do not continue to later cases. If all cases pass, include the test transcript or a concise reference to its full console output in the handoff.

