---
name: test-ui
description: Run and verify planned console UI tests for this Java project. Use when adding, updating, or executing command-line user-interface test cases, especially when commands, stdin input, exact expected output, and a test-session transcript are required.
---

# Console UI testing

Maintain test cases in `test/ui-test-plan.md`. Every case must have an aim, a command, input sent to standard input, and exact expected output. Use the fenced-block layout below. The command runs from the project root.

````markdown
## Test case: short-id

Aim:
```text
Describe the behaviour being checked.
```

Command:
```shell
java -cp src/main/java Hu9o
```

Input:
```text
todo read chapter
bye
```

Expected output:
```text
...exact program output...
```
````

Run all planned cases with:

```powershell
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner reads `test/ui-test-plan.md`, feeds each case's input to its command, and compares combined standard output and standard error with the expected output after normalising only line endings. It writes the full command, supplied input, and output to `test/ui-test-session.md` and also prints it to the console.

Stop at the first failed case. Report its aim, command, actual output, and expected output; do not run any later case. Treat a non-zero exit code as a failure even if its output matches. Update the plan's expected-output block only after intentionally reviewing the changed behaviour.

Before executing this project, ensure Java 25 is selected. Keep inputs such as `bye` in each case so an interactive program exits normally.