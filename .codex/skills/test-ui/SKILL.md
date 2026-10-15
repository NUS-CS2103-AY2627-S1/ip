---
name: test-ui
description: Plan and run exact-output tests for this project's command-line UI. Use when given console command sequences and expected outputs, or when asked to execute or update UI test cases in test/ui-test-plan.md.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth. Each case must state its aim, input commands in order, and complete expected standard output. Also record the launch command, setup, and comparison rules needed to reproduce the session.

## Prepare the plan

1. Read the existing plan before changing or running tests.
2. Add user-supplied cases to the plan and preserve unrelated cases.
3. Give every case a stable ID. Run each in a fresh process so state cannot leak.
4. By default, compare stdout exactly after converting CRLF to LF; prompts, whitespace, and blank lines remain significant.

Follow the plan's template. Every case must contain `Aim`, `Inputs`, and `Expected output`.

## Run the tests

1. Ensure Java 25 is active. On macOS, use `sdk use java 25.0.3.fx-zulu` when SDKMAN is available.
2. Compile and launch using the commands recorded in the plan. Do not change application behavior merely to make a test pass.
3. Run cases in plan order. Send each input line followed by a newline, then EOF.
4. Capture stdout exactly and stderr separately. Stderr is not expected stdout unless the plan explicitly says otherwise.
5. Stop immediately on the first mismatch, non-zero exit, timeout, or launch error. Do not run later cases.

Prefer `scripts/run_ui_test.py` for deterministic execution. Give it a temporary JSON file shaped like this:

```json
{
  "program": ["java", "-cp", "out/production/ip", "Bob"],
  "timeout_seconds": 10,
  "tests": [
    {
      "id": "UI-001",
      "aim": "Exit cleanly",
      "inputs": ["bye"],
      "expected_output": "...exact stdout...\n"
    }
  ]
}
```

Translate the Markdown cases faithfully into temporary runner input; do not keep a second durable test definition. The runner executes in list order and exits at the first failure.

## Report the session

Show a console-session record for every executed case, including launch command, labeled inputs, stdout, non-empty stderr, exit status, and PASS/FAIL. This labeling prevents typed commands from being mistaken for program output.

On failure, show actual and expected stdout in separate fenced blocks, identify the first difference when useful, and state that remaining cases were not run. On success, summarize the number of passing cases.
