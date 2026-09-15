---
name: test-ui
description: Run fail-fast console UI tests for this Java chatbot using command lists and exact expected outputs recorded in test/ui-test-plan.md. Use when Codex needs to add, update, or execute console interaction test cases, verify user-visible terminal output, or report a reproducible UI test session.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for console UI test cases. Each case must contain a unique ID, an aim, an ordered list of inputs, and the complete expected console output as an ordered list of lines.

## Workflow

1. Read `test/ui-test-plan.md` completely.
2. When the user supplies new commands or expected output, add or update cases in its fenced JSON block before testing. Keep the prose and JSON consistent.
3. Preserve command order within each case because all commands in one case run in the same program session and share state.
4. Ensure Java 25 is active. On macOS, run `sdk use java 25.0.3.fx-zulu` first if the active compiler is not Java 25.
5. From the repository root, run:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md
   ```

6. Show the script's console-session record to the user. Do not omit the recorded inputs or program output.
7. If the script reports a failure, stop testing immediately. Report the failed case, actual output, and expected output exactly as printed. Do not continue to later cases.

## Comparison rules

- Compile the project once, then start a fresh program process for each test case.
- Send every input in a case to that process in order.
- Compare the full standard output exactly after normalizing CRLF line endings to LF.
- Treat a timeout, nonzero process exit, or any standard-error output as a failure.
- Do not change application code merely to make a failing test pass unless the user separately asks for a fix.

## Test plan format

Maintain one fenced `json` object in `test/ui-test-plan.md` with these fields:

```json
{
  "main_class": "Tianyi",
  "timeout_seconds": 10,
  "test_cases": [
    {
      "id": "unique-id",
      "aim": "What behavior this case verifies",
      "inputs": ["first command", "bye"],
      "expected_output": ["first console line", "second console line"]
    }
  ]
}
```

Represent each expected console line as one JSON string. Do not include newline characters inside an element; the runner adds one line break after every element.
