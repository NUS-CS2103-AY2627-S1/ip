---
name: test-ui
description: Run repeatable console UI tests defined in test/ui-test-plan.md, compare each program output with its expected output, and record the complete session. Use for testing command-line or interactive program interfaces, not unit tests.
---

# Test UI

Use this skill to run the console-interface cases recorded in `test/ui-test-plan.md`.

## Test-plan format

Keep the plan in the repository's `test/ui-test-plan.md`. It must contain one or
more `## Test case:` sections. Every case must include its aim, its console
inputs, and expected output. The command to launch the program is a required
part of the case.

Use this structure (the fenced blocks preserve whitespace exactly):

````markdown
## Test case: A short, unique name

**Aim:** What behavior this case verifies.

**Command:** `java -jar build/app.jar`

**Inputs:**
```text
first input
second input
```

**Expected output:**
```text
Expected text printed by the program
```
````

Use an empty `text` block for a program with no console input. Commands run
from the repository root through the shell, so they may include a Java setup
step when needed. Keep inputs separate from the command; the runner passes
them to standard input. A non-empty fenced block includes its final newline,
which matches ordinary console input and output behavior.

## Run the tests

1. Inspect or update the plan before testing so the aim, inputs, command, and
   expected output accurately describe the behavior being checked.
2. Run the plan from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
   ```

3. The runner writes a timestamped session transcript under
   `test/ui-test-sessions/` and prints it to the console. Share the transcript
   path and a concise pass/fail result with the user.

The runner stops immediately on the first failure. Its transcript includes the
command, supplied console input, expected output, and actual combined output.
For a failure, report the expected and actual output directly; do not run later
cases unless the user asks to continue after fixing or changing the plan.

Do not alter a test's expected output merely to make a failing test pass. First
determine whether the program or the plan is incorrect, then ask before making
any out-of-scope behavior change.
