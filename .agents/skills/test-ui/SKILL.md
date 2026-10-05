---
name: test-ui
description: Run the project's scripted console UI tests from test/ui-test-plan.md, compile the Java application with Java 25, compare each case's expected output with the actual output, print the full input/output transcript, and stop immediately at the first failure. Use when validating changes to Gatsby's command-line behavior or updating the UI test plan.
---

# Test UI

Run the project-specific console UI test plan deterministically.

## Workflow

1. Read `test/ui-test-plan.md` before testing. Each `## Test ...` section must contain an `Aim:`, an `Input:` fenced `text` block, and an `Expected output:` fenced `text` block.
2. Treat each non-empty expected-output line as an ordered expected line. The runner ignores leading/trailing whitespace so the plan does not need to reproduce the UI's indentation, but it does require the lines to appear in order.
3. Run the bundled script from the repository root:

   ```bash
   python3 .agents/skills/test-ui/scripts/run_ui_tests.py
   ```

4. Use the transcript printed for every passing case as the test record. If a case fails, stop without running later cases and report the case name, aim, input, expected lines, actual output, and process error if present.
5. Keep test cases independent: the runner starts a fresh `Gatsby` process for each case.

## Test-plan format

Add cases to `test/ui-test-plan.md` using this structure:

```markdown
## Test 1: Create each task type

Aim: Verify ToDos, deadlines, and events are created and listed correctly.

Input:
```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

Expected output:
```text
[T][ ] borrow book
[D][ ] return book (by: Sunday)
[E][ ] project meeting (from: Mon 2pm to: 4pm)
```
```

Use complete user input in the `Input:` block, one command per line. Use only the output lines that matter for the assertion in the `Expected output:` block; the banner and separators do not need to be repeated.

The runner requires `javac` and `java` version 25. If the environment is not already using Java 25, switch first with `sdk use java 25.0.3.fx-zulu` and rerun the script.
