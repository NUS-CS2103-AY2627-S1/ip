---
name: test-ui
description: Run this project's console UI test cases from test/ui-test-plan.md and compare each captured output with its expected output. Use when asked to test or verify the chatbot's command-line interface.
---

# Test UI

Run the console UI cases recorded in `test/ui-test-plan.md`. Each case must state its aim, console input, and exact expected console output. The runner compiles the Java source, runs cases in order, prints each test session's input and output, and stops at the first failure.

## Maintain the test plan

Use this exact Markdown structure for every case:

````markdown
## Descriptive test name
**Aim:** What behavior this case verifies.

**Input:**
```text
command 1
command 2
```

**Expected output:**
```text
The exact console output, including separators and blank lines.
```
````

Add a case when a command or meaningful error path is added or changed. Keep inputs and expected outputs in the plan rather than embedding them in the runner.

## Run the tests

From the project root, use Java 25 and run:

```bash
source /Users/ksyeo/.sdkman/bin/sdkman-init.sh
sdk use java 25.0.3.fx-zulu
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

Because `rocky.Rocky` launches the graphical interface, run the console plan with
`--main-class rocky.cli.CliLauncher`.

The runner compiles all files in `main/java`, runs the selected main class, and reads `test/ui-test-plan.md` by default. Use `--help` for optional paths or a different main class.

## Results

For every executed case, report its aim and show the captured console input and output. If a case fails, stop immediately and show that case's expected output alongside its actual output. Do not continue to later cases.
