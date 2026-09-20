---
name: test-ui
description: >-
  Run automated UI tests for the AXIOM Java chatbot by piping commands to the
  program and comparing stdout to expected output. Use when the user asks to
  test UI behavior, run ui tests, verify console output, or update test/ui-test-plan.md.
---

# test-ui

Run console UI tests for this project. Test cases live in [test/ui-test-plan.md](../../test/ui-test-plan.md); the runner is [test/run-ui-tests.py](../../test/run-ui-tests.py).

## When to use

- After changing `Axiom.java`, `Task.java`, or related UI output
- When the user asks to run UI tests or verify chatbot behavior
- When adding or updating test cases in the test plan

JUnit unit tests live under `src/test/java/` and are run with `./gradlew test`. See `AGENTS.md` for the ~50% highest-value-method coverage target; update JUnit tests whenever production code changes.

## Workflow

1. Ensure Java 25 is active on macOS:
   ```bash
   sdk use java 25.0.3.fx-zulu
   ```
2. Run the test session from the project root:
   ```bash
   python3 test/run-ui-tests.py
   ```
3. Present the full console transcript from the runner to the user.
4. If a test fails, stop immediately — do not run remaining cases. Report the failing test case name, actual output, and expected output from the runner.

## Test plan format

Each test case in `test/ui-test-plan.md` must include:

| Field | Purpose |
|-------|---------|
| **Aim** | What behavior the test verifies |
| **Inputs** | Commands to pipe to the program (one per line) |
| **Expected output** | Exact stdout, including banner and trailing newline |

Use this template when adding a test case:

```markdown
## Test Case N: Short name

**Aim:** What this test verifies.

**Inputs:**
```
command one
command two
bye
```

**Expected output:**
```
(full expected stdout)
```
```

Rules:

- Each test case is a **full program session** (fresh JVM run).
- Expected output must match **exactly** — every space, line break, and trailing newline.
- Always end inputs with `bye` unless testing mid-session interruption.
- Capture expected output by running the program manually when unsure:
  ```bash
  printf 'your\ncommands\nbye\n' | java src/main/java/axiom/Axiom.java
  ```

## Failure handling

The runner stops on the first failure and prints:

- Test case number and name
- Input transcript
- Actual output
- Expected output

Do not continue the session after a failure. Fix the code or update the test plan, then re-run from the start.

## Updating tests

When adding a new feature:

1. Add a test case to `test/ui-test-plan.md` with aim, inputs, and expected output.
2. Run `python3 test/run-ui-tests.py`.
3. Share the test session output with the user.

When output changes intentionally (e.g. new greeting), update **Expected output** in the affected test cases to match the new behavior.

## Program configuration

The test plan header defines the run command:

```markdown
**Run command:** `java src/main/java/axiom/Axiom.java`
```

If the entry point changes, update this line in `test/ui-test-plan.md` only — the runner reads it from there.
