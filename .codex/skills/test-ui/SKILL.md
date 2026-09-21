---
name: test-ui
description: Run the repository's scripted command-line UI test cases from test/ui-test-plan.md, compare each program session with its expected output, print the console input/output record, and stop immediately on the first failure.
---

# Test UI

Use this skill after every code update in this repository, and whenever the user asks to run or update the UI tests.

## Workflow

1. Read `test/ui-test-plan.md`. Update the plan first when a code change adds, removes, or changes observable command-line behavior. Each case must state its aim, input commands, and complete expected output.
2. Run the bundled standard-library-only runner from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
   ```

3. The runner compiles the project using the plan's compile command, starts a fresh program process for each test case, feeds the listed commands, and compares the complete console output (apart from a final newline).
4. Review the printed console input/output record for every case. A passing run ends with an all-passed summary.
5. If any case fails, do not run later cases or make unrelated changes. Report the actual and expected output shown by the runner, then fix the implementation or test plan as appropriate and rerun.

## Test-plan format

Keep global commands near the top of `test/ui-test-plan.md`:

```markdown
- Compile command: `javac -d /tmp/ui-test-classes src/main/java/*.java`
- Program command: `java -cp /tmp/ui-test-classes Quackie`
```

Record each test case with this structure:

```markdown
## Test Case 1: Short name

- Aim: Explain the behavior being verified.
- Inputs:
  ```text
  command one
  command two
  bye
  ```
- Expected output:
  ```text
  exact console output, including spaces and separators
  ```
```

Use a complete expected session, including the banner and exit output. Treat the commands in the input block as one session; the runner starts a new process for each test case.
