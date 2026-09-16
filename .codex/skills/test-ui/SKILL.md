---
name: test-ui
description: Run project UI test cases from ordered console inputs and expected outputs, stopping at the first failure and showing the complete console session.
---

# Test UI

Use this skill when testing the command-line UI of this Java project. The default test plan is [`test/ui-test-plan.md`](../../../test/ui-test-plan.md); use a different plan only when the user supplies one.

## Test case format

The plan must describe every case with:

- `name`: a short identifier;
- `aim`: what behavior the case verifies;
- `inputs`: ordered console commands sent to one fresh program session; and
- `expected_outputs`: ordered output snippets, one for each input.

Expected snippets may omit the startup banner and separator lines, but must preserve meaningful text and spacing. Keep the two lists the same length. A test case should normally end its inputs with `bye` so the program exits cleanly.

The plan contains a `test-ui` JSON code block so the bundled runner can execute it. Keep the human-readable aim, inputs, and expected output beside that block when adding or changing cases.

## Run and stop conditions

From the repository root, run:

```text
python .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md
```

The runner checks that Java 25 is active, compiles all files in `src/main/java`, and runs each case in a fresh session. It prints the console input and complete console output for every case. It must stop after the first failed case, terminate that subprocess if it is still running, and report the failing command together with the expected snippet and actual console output. Do not continue with later cases after a failure or hide the session transcript.

When users provide test cases directly instead of using the plan, first record them in `test/ui-test-plan.md` using the same fields, then run the runner and include its session transcript in the response.
