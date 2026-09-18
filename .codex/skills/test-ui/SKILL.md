---
name: test-ui
description: Run scripted console UI tests for this Java project. Use when asked to test the chatbot's command-line interaction, add or update UI test cases, compare console output with expected output, or show a console test-session transcript. Read test/ui-test-plan.md as the source of test cases.
---

# Console UI tests

Use `test/ui-test-plan.md` as the single source of truth for UI test cases. Each case must provide an aim, run command, inputs, and exact expected output. Keep the plan updated when adding, changing, or removing a UI behaviour.

## Plan format

Use this exact heading structure for every case:

````markdown
## Test case: <short name>

### Aim

<what behaviour this case verifies>

### Run command

```sh
<command that starts the program>
```

### Inputs

```text
<one line per console input>
```

### Expected output

```text
<complete stdout, exactly as expected>
```
````

Run the current source, not stale compiled classes. For this project, start each command with `javac src/main/java/*.java &&` before invoking `java -cp src/main/java Rene`.

## Run the tests

From the repository root, run:

```sh
python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

The runner executes cases in plan order, supplies the listed inputs, and compares complete combined console output to the expected output. It prints an input/output transcript for every completed case. On the first failure, it immediately stops, prints the expected and actual outputs, and exits with a non-zero status. Do not continue manually after a failure unless asked; fix or clarify the mismatch first.

Treat whitespace and line order as part of the UI contract. Update expected output only when the intended user-facing behaviour has changed.
