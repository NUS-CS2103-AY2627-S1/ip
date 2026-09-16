---
name: test-ui
description: Run command-line UI test cases for this project from a recorded test plan, compare each response with its expected output, and report the console session.
---

# Test the Aider UI

Use this skill when the user provides, or asks you to create, lists of commands and expected outputs for the Aider console program.

## Test-plan format

Record the test cases in `test/ui-test-plan.md`. Every case must include:

- an aim;
- an ordered `commands` list;
- an ordered `expected outputs` list with exactly one response expectation per command.

The plan must also record the Java version, entry point, compile command, and output-comparison rules. Use this structure:

```markdown
## Test case: Add and list tasks

Aim: Verify that entered text is stored and displayed by `list`.

Commands:

1. Input: `read book`
   Expected output:
   ```text
   added: read book
   ```
2. Input: `list`
   Expected output:
   ```text
   Here are the tasks in your list:
   1.[ ] read book
   ```
3. Input: `bye`
   Expected output:
   ```text
   Bye. Hope to see you again soon!
   ```
```

The expected output for a command is the chatbot response, excluding the command that the terminal echoes as user input. Include all response text, including separators, when exact output comparison requires it.

## Execution workflow

1. Read `test/ui-test-plan.md` and any test cases supplied in the current request. If the request supplies new cases, add them to the plan before running them.
2. Use Java 25. Compile the entry point and its source dependencies into an ignored build directory, for example:
   `javac -d out/production/ip src/main/java/*.java`
3. Run each test case as a fresh process, feeding its commands in order through standard input. Preserve state between commands within a case and do not reuse a process across cases.
4. Compare the actual output with the expected output for each command in order. Account for the program's startup banner separately, and do not treat terminal input echo as program output.
5. Print a console-session record that interleaves each input command with the corresponding actual output. Include the startup output at the beginning.
6. If any expected response differs from the actual response, stop immediately. Report the test case, command number, input, expected output, and actual output. Do not run later commands or test cases after a failure.
7. If all cases pass, report every case as passed and include the complete console-session records.

Do not modify application source code while testing unless the user separately asks for a fix. Do not commit test-plan or skill changes unless explicitly requested.
