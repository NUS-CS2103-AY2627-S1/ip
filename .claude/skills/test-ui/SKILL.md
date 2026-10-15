---
name: test-ui
description: Run the console UI test cases in test/ui-test-plan.md against the compiled Java chatbot and verify actual output matches expected output. Use after any code change to the chatbot's behavior, when asked to test/verify the chatbot, or when updating the UI test plan.
---

# Test UI

Run the chatbot's console test cases and verify its output against recorded
expectations. Use this after any change to the chatbot's runtime behavior.

## Run the tests

1. Treat the current repository as the target unless the user identifies another repository.
2. Run the bundled test runner from the repository root:

   ```bash
   python3 .claude/skills/test-ui/scripts/run_ui_tests.py . test/ui-test-plan.md coco.Coco
   ```

   The script:
   - Compiles every `.java` file under `src/main/java`.
   - Parses each test case from `test/ui-test-plan.md`.
   - Runs the compiled program fresh for each test case, feeding it that
     test case's input on stdin.
   - Prints a full console transcript (input and output) for every test case
     as it runs, so the session is visible.
   - Compares the captured output to the expected output verbatim (trailing
     newline differences are ignored, nothing else is).
   - Stops immediately at the first failing test case and reports both the
     expected and actual output. It does not run remaining test cases after
     a failure.

3. Report the result: which test case(s) ran, and whether they passed. If a
   test failed, quote the expected/actual mismatch from the script's output
   rather than re-describing it.

## Update the test plan

`test/ui-test-plan.md` is the source of truth for test cases. Each test case
follows this exact structure (required for the parser in
`scripts/run_ui_tests.py` to recognize it):

```markdown
## Test <n>: <short name>

Aim: <what this test case is checking and why>.

### Input

```
<one console input line per line, in order>
```

### Expected Output

```
<exact expected stdout, byte-for-byte>
```
```

When adding or updating a test case:

- Never hand-type the expected output. Run the program yourself with that
  test case's exact input (e.g. `printf '<input>\n' | java -cp out Coco`,
  compiling first if needed) and copy the captured output verbatim,
  including trailing spaces on banner lines. Hand-transcribed expected
  output is a common source of false failures.
- Keep each test case self-contained: it should not depend on state left
  behind by a previous test case, since each one runs against a fresh
  instance of the program.
- Give the test case a specific aim — what behavior it is checking — not
  just a restatement of the input.

## When to use this skill

Invoke it after making a change to the chatbot's behavior (new command,
changed output format, refactor that could affect behavior), and after
updating `test/ui-test-plan.md` itself. See `AGENTS.md` for the project rule
tying code changes to this skill.
