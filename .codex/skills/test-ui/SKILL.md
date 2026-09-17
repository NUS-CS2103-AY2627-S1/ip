---
name: test-ui
description: Run scripted command-line UI tests for this Java project from lists of commands, inputs, and expected outputs. Use when validating Megatron's console behaviour, recording a test session, or updating test/ui-test-plan.md.
---

# Test the console UI

Use this skill to run cases in `test/ui-test-plan.md` or a user-provided JSON list. Run cases in order and stop immediately after the first failure.

## Workflow

1. Read `test/ui-test-plan.md` and confirm every case states its aim, inputs, and expected output.
2. Use JDK 25 and report the version if it is not Java 25.
3. Run each command with its input and capture stdout and stderr separately.
4. Compare stdout exactly, including line breaks and spaces. Treat a non-zero exit code as a failure.
5. Print the command, input, actual output, and result for every executed case.
6. Stop on the first failure and report actual and expected output. Do not run later cases.

Each case must contain `name`, `aim`, `command`, `input`, and `expected_output`. Use the bundled runner:

```powershell
python .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md
```

It also accepts `--cases cases.json`. The plan stores machine-readable cases between `<!-- TEST_CASES_JSON` and `-->` markers.
