---
name: test-ui
description: Run and maintain this project's chatbot terminal UI tests after code changes or when asked to verify command/output interactions.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for UI test cases.

Before running tests:

1. Review the code change and the existing test plan.
2. If behavior changed or a relevant case is missing, update the plan from the requirement before testing. Do not copy incorrect program output into the expected results merely to make a test pass.
3. When the user supplies commands and expected outputs, record them as a test case with an aim, an `Inputs` JSON list, and a parallel `Expected outputs` JSON list. Keep one expected response per command.

Run the deterministic test runner from the project root with an available Python 3 interpreter:

```text
python .agents/skills/test-ui/scripts/run_ui_tests.py
```

The runner compiles all Java sources, starts a fresh `Gary` process for each test case, sends commands in order, and compares each response exactly after normalizing line endings and surrounding blank lines. It terminates immediately on the first failure and displays the command, actual response, expected response, and console session.

After testing, show the console input/output record emitted by the runner and report the pass/fail result. Do not omit a failure transcript or continue with later cases after a failure.
