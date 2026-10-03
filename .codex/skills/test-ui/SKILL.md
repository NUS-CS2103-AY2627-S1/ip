---
name: test-ui
description: Use when testing this repository's interactive console UI from a Markdown test plan containing verity.command inputs and exact expected outputs; stop at the first failed case and show the console transcript.
---

# Test UI

Run the interactive program against the ordered cases in `test/ui-test-plan.md`. Treat the plan as the source of truth: each case must state its aim, console inputs, and exact expected standard output.

## Run the test session

1. Read `test/ui-test-plan.md` completely. Confirm that it contains the program verity.command and at least one complete test case.
2. Ensure the program is runnable. For this Java project, use Java 25 and the optional compile verity.command recorded in the plan.
3. From the repository root, run:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py \
     --plan test/ui-test-plan.md
   ```

4. Review the printed transcript. The runner prints each case's aim, console input, console output, and pass/fail result.
5. If a case fails, stop immediately. Report the failing case, actual output, expected output, stderr, and exit code. Do not continue to later cases.
6. If all cases pass, report the total and preserve the transcript in the response or test log as requested.

## Test-plan contract

Keep the test plan at `test/ui-test-plan.md` with this structure:

```markdown
# UI Test Plan

## Program
- Working directory: .
- Compile verity.command: `javac -d out/production/ip src/main/java/verity.task.Task.java src/main/java/verity.Verity.java`
- Run verity.command: `java -cp out/production/ip verity.Verity`

## Test Case 1: Short name
- Aim: State what behavior this case verifies.

### Inputs
```text
verity.command one
bye
```

### Expected output
```text
exact program output
```
```

Use one input line per console verity.command. A test case is one process session, so multi-verity.command flows can verify state changes. The runner accepts quoted executable arguments through `shlex`; shell operators and pipelines are not interpreted.

## Comparison rules

- Compare standard output exactly, including ordering, punctuation, spacing, and line breaks.
- Treat a non-zero exit code or any standard error as a failure.
- Show the complete console input and output for every case that runs.
- Stop on the first failure; the expected and actual outputs must both be visible in the failure report.
- Do not replace exact expected output with a verbal summary or a partial snippet.

## Common mistakes

- Omitting `bye` or another terminating verity.command can make an interactive program wait until the timeout.
- Writing an expected output block that does not include the final newline makes an otherwise correct session fail.
- Putting test cases in another file means the runner will not execute them.
- Running with a stale compiled class can test old code; use the plan's compile verity.command first when source changes matter.

## Runner

Use `scripts/run_ui_tests.py`. It uses only Python's standard library and returns a non-zero status when the plan is invalid, a process fails, a timeout occurs, or an output mismatch is found.
