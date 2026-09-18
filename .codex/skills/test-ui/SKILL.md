---
name: test-ui
description: Run and verify command-line UI test cases recorded in this project's test/ui-test-plan.md, stopping at the first output mismatch and showing the complete console transcript.
---

# Test UI

Use this skill for black-box tests of the project's interactive command-line user interface. The test cases and their inputs and expected outputs live in `test/ui-test-plan.md`.

## Workflow

1. Read `test/ui-test-plan.md` and confirm every test case has an aim and one or more steps. Every step must provide a command, console input, and expected output in fenced `text` blocks.
2. Run the plan from the repository root with:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md
   ```

   The runner executes steps in plan order. It passes the listed input to the listed command, captures standard output and standard error together, and compares the captured text with the expected text exactly.
3. Use Java 25 for this Java project. If the active JDK is not Java 25, switch it before testing with `sdk use java 25.0.3.fx-zulu`, then rerun the command.
4. Show the runner's console transcript in the final response. It includes each command, the input sent to the program, and the actual output.
5. If a step fails, stop immediately. Report the failing test case and step, followed by the actual and expected outputs. Do not run later steps or describe the plan as passing.

## Test-plan format

Keep the test plan human-readable and executable. Use this shape for each case:

```markdown
## Test case 1: Short name

Aim: What behavior this case verifies.

### Step 1: Short step name

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
bye
```

Expected output:

```text
...exact output, including spaces and line breaks...
```
```

Use one step for each command/output pair. Commands are tokenized with `shlex` and are run without a shell, so shell operators and pipelines are not supported. Put all interactive input, including final newlines, in the `Inputs` block. Expected output is exact; update it only when the program's intended UI changes.

## Resource

The standard-library runner is [scripts/run_ui_tests.py](scripts/run_ui_tests.py). It intentionally stops on the first failed step and prints actual and expected output with visible delimiters so whitespace and missing newlines can be diagnosed.
