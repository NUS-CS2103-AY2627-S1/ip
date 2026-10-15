---
name: test-ui
description: Run the project's recorded console UI test cases, compare their output exactly, and show a test-session transcript. Use after changes to the Java console UI.
---

# Test UI

Use this skill to verify the console interface described by `test/ui-test-plan.md`.

## Maintain the plan

Before testing a code update, review `test/ui-test-plan.md`. Add or revise a test case whenever the update changes a user-visible command, input, response, or output format. Each test case must contain:

- an aim;
- a `Run command` in inline code;
- an `Inputs` fenced `text` block; and
- an `Expected output` fenced `text` block.

The plan's example and format rules are authoritative. Keep expected output exact, including separators, spaces, and line order.

## Run the tests

1. Confirm that `java -version` and `javac -version` use Java 25. On macOS, run `sdk use java 25.0.3.fx-zulu` in the same shell if a different version is selected.
2. Compile the current sources from the repository root:

   ```bash
   javac -d out/production/ip src/main/java/*.java
   ```

3. Run the test runner:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
   ```

The runner executes every test case's `Run command`, supplies its `Inputs`, and compares standard output exactly with `Expected output`. It prints each test's command, console input, and console output. On the first failed case it stops immediately and reports both actual and expected output. Do not continue with later test cases after a failure.

## Result

Report the runner's result and its displayed transcript. If it fails, identify the failed test case and preserve the actual-versus-expected output in the handoff.
