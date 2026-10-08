---
name: test-ui
description: Run scripted console UI checks for this Java project by reading test cases from test/ui-test-plan.md. Use when asked to verify command/output behavior, replay a list of chatbot commands, compare actual stdout with expected output, or show a console test transcript. Stop immediately on the first failed test and report the expected and actual outputs.
---

# Test UI

Use the repository test plan at `test/ui-test-plan.md` as the source of truth for UI tests.

## Workflow

1. Read `test/ui-test-plan.md`.
2. Ensure each test case includes:
   - an `##` heading naming the test
   - an `Aim:` line
   - an `### Inputs` fenced code block
   - an `### Expected Output` fenced code block
3. Run the bundled script from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py
   ```

4. Review the script output.
   - If every test passes, report that and include the saved console transcript path.
   - If a test fails, stop immediately and report the failing test name plus its expected and actual outputs.

## Java setup

Use Java 25 for this project. If the current shell is not already on Java 25, switch before running project commands.

## Test plan format

Each test case in `test/ui-test-plan.md` must use this structure:

```md
## Add a todo
Aim: Verify that adding a todo shows the stored task and updated count.

### Inputs
```text
todo borrow book
bye
```

### Expected Output
```text
____________________________________________________________
... full expected console output here ...
```
```

Treat each test case as one fresh program run. Feed the listed input lines to the program in order.

## Output records

The bundled script writes the latest full console transcript to `test/ui-test-session.log`. Keep that file as the session record to show the tested input/output exchange.
