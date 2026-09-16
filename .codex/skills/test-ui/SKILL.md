---
name: test-ui
description: Run interactive or command-line Ui test cases defined by commands, inputs, and expected outputs; compare each result exactly, stop on the first failure, and record the complete console session. Use for testing this project's launched program and maintaining test/ui-test-plan.md.
---

# Ui test runner

Run the project's Ui test cases one at a time from `test/ui-test-plan.md`. Treat the plan as the source of truth for the test command, inputs, and expected output. Use Java 25 when running this Java project, as required by the repository instructions.

## Accept and record test cases

Accept a numbered list of commands and matching expected outputs from the user. For each item, record or update one test case in `test/ui-test-plan.md` before executing it. Include:

- **Aim**: the behavior being checked.
- **Command**: the exact command used to launch the program.
- **Inputs**: each console input, in order; write `None` for no input.
- **Expected output**: the complete expected console output in a fenced code block.
- **Preconditions**: setup or build commands required before launching, if any.

Use this format:

````markdown
### TC-001: Short name

- Aim: Verify ...
- Command: `...`
- Inputs:
  ```text
  ...
  ```
- Expected output:
  ```text
  ...
  ```
- Preconditions: ...
````

Do not change an expected output merely to make a failing test pass. If the plan does not exist, create it with the heading `# Ui Test Plan` and the cases supplied by the user. Keep prior test-session logs unless the user asks to replace them.

## Execute the test session

1. Read the complete plan and resolve the repository root and any preconditions.
2. Run preconditions only when needed, using Java 25 for Java commands.
3. Start the command for the first test case exactly as written. For an interactive command, allocate a terminal and send the listed inputs in order; for a non-interactive command, capture its standard output and standard error.
4. Save the console input and output for that case, including the command, every input line, program output, exit status, and any error output.
5. Compare actual and expected output after normalizing CRLF to LF and ignoring one final line terminator. Otherwise compare the text exactly, including spaces, capitalization, and blank lines. Do not ignore ANSI control sequences unless the test plan explicitly says to do so.
6. If the command exits unsuccessfully or the output differs, stop immediately. Do not run later cases. Report the failed case, actual output, expected output, inputs, exit status, and the captured console record.
7. If it passes, continue to the next case and repeat. After all cases pass, report a passing summary and the complete console session in test-case order.

## Record the session

After execution, append a `## Test session: <date and time>` section to `test/ui-test-plan.md`. For every case actually reached, record `PASS` or `FAIL` and a fenced console transcript showing:

```text
$ <command>
<input lines, if any>
<program output and errors>
[exit code: 0]
```

On a failure, record only the cases reached and mark the failing case with its actual and expected outputs before stopping. Keep the transcript faithful to the console; do not replace it with a summary. Show the same transcript in the final response so the user can inspect the test session without opening the file.
