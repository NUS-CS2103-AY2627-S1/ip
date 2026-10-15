---
name: test-ui
description: Run and verify interactive console or command-line UI test cases defined in test/ui-test-plan.md. Use when a project needs repeatable tests that provide command input, compare the complete console output with an expected output block, print a session transcript, and stop immediately after the first failed test.
---

# Test UI

Use this skill to execute the project’s scripted console UI tests. The test cases live in test/ui-test-plan.md, so the plan is both the source of test inputs and the reviewable record of expected behavior.

## Workflow

1. Read test/ui-test-plan.md and confirm that every test case has an aim, command, input, and expected output.
2. Run the bundled runner from the project root:

   ```sh
   python3 skills/test-ui/scripts/run_ui_tests.py
   ```

3. Preserve the runner’s console transcript in the response. It includes the command, console input, and actual console output for each test.
4. If a test fails, stop immediately. Report the failing test, exit status, actual output, and expected output. Do not run later test cases.
5. If all tests pass, report the number of completed test cases.

## Test-plan format

Record test cases in document order. Each Test Case section is one command/output pair. Use fenced blocks so whitespace and line breaks remain unambiguous:

```markdown
## Test Case: Add a deadline

### Aim
Verify that a deadline command stores the description and the text after /by.

### Command
```sh
java -cp out/production/ip Pebby
```

### Input
```text
deadline return book /by Sunday
bye
```

### Expected Output
```text
...the complete console output...
```
```

The command is run from the project root through the system shell, and the input block is sent to its standard input exactly as written. The runner compares combined standard output and standard error, allowing only a final newline difference. Keep prompts, separators, and other visible console text in the expected output when the command prints them.

For Java projects, use Java 25 as required by the repository instructions. If the shell is not already using it, configure the command or shell session with sdk use java 25.0.3.fx-zulu before compiling or running Java code.

## Runner

The implementation is in skills/test-ui/scripts/run_ui_tests.py. Pass a different plan when needed:

```sh
python3 skills/test-ui/scripts/run_ui_tests.py path/to/another-plan.md
```

The runner validates the plan before execution, runs cases sequentially, prints each console session, and exits with status 1 on the first failure or malformed test case.
