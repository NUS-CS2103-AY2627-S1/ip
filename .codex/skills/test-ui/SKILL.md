---
name: test-ui
description: Run this project's console Ui test cases from test/ui-test-plan.md, compare each complete output exactly, and stop at the first failure.
metadata:
  short-description: Run fail-fast console Ui tests
---

# Test Ui

Use this skill when the user asks to run or verify the project's text-based Ui tests. The test plan is the source of truth for the ordered test cases; do not invent additional cases during a run.

## Test plan

Read [test/ui-test-plan.md](../../../test/ui-test-plan.md) before testing. It contains one `json` code block with this shape:

```json
{
  "program": ["executable", "argument"],
  "working_directory": ".",
  "timeout_seconds": 30,
  "test_cases": [
    {
      "name": "Short descriptive name",
      "aim": "What behavior this case verifies.",
      "inputs": ["one command", "another command"],
      "expected_output": "The complete output, including newlines and tabs."
    }
  ]
}
```

Every test case must include a non-empty `aim`, an ordered `inputs` list (one console input line per item), and an exact `expected_output`. Output comparison normalizes only CRLF/CR to LF; leading spaces, tabs, trailing spaces, and the final newline remain significant. The runner also accepts `expected_output` as a list of exact output lines and joins those lines with LF, including a final newline.

Keep the plan human-readable: update the explanation around the JSON block when adding or changing cases, and keep expected output synchronized with the program's actual console behavior.

## Running tests

Run the bundled helper from the project root:

```text
<available-python> .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

Use the workspace-bundled Python runtime when the default `python` command is unavailable. The helper validates the plan, runs the program once per test case with the listed inputs, and captures stdout and stderr together. If the configured program starts with `java`, it first checks that the Java major version is 25, as required by this project.

The helper prints a transcript for every case, including the console input and output. It must stop immediately after the first failed case; do not run later cases manually. For a failure, report the case name and aim, exit status or timeout information, and the complete actual and expected outputs. For a successful run, report that all listed cases passed after showing the transcript.

Do not edit production code or silently rewrite the test plan while testing. If the plan is malformed or the Java version is wrong, report the setup error and do not start the test cases.
