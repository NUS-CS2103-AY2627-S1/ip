---
name: test-ui
description: Run and verify planned console UI tests for this Java project using test/ui-test-plan.md. Use when adding, updating, or executing command-and-output UI regression tests.
---

# Console UI testing

Use this skill for repeatable tests of the application's command-line interface. The source of truth is `test/ui-test-plan.md`; keep its test cases current when UI behavior changes.

## After application code changes

When application code has changed, first review `test/ui-test-plan.md`. Update it if the change affects observable console behavior, including valid commands, invalid-input handling, messages, or task state. Then run the complete plan before reporting the code change as complete. If any case fails, stop immediately and report the session record's actual and expected outputs.

## Test-plan format

Each case is a level-two Markdown heading followed by all four fields below. Put commands, console input, and expected output in fenced `text` blocks. The `Command` must be a command that starts the program and reads the specified input from standard input.

```markdown
## Add one item

**Aim:** Confirm that adding an item is shown in the list.

**Command:**
```text
java -jar build/libs/app.jar
```

**Input:**
```text
add Read book
list
exit
```

**Expected output:**
```text
Added: Read book
1. Read book
Bye!
```
```

Use the actual build/run command for this repository. Do not include shell prompts in any field. Expected output must contain only application output; the runner records the command and supplied input separately.

## Running tests

Before testing, ensure the application is built with Java 25. Run all planned cases with:

```powershell
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner writes `test/ui-test-session.md` and prints the same console-session record. It compares output exactly after normalizing Windows and Unix line endings and a single final newline.

Stop immediately when a case fails. Report the failed case's actual and expected outputs exactly as the runner presents them; do not run later cases. A passing run should state that all cases passed and link the session record.
