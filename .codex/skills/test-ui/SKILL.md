---
name: test-ui
description: Run scripted console UI test cases from test/ui-test-plan.md, compare each program output with its expected output, and stop at the first failure with a console transcript.
---

# Test UI

Use this skill for interactive command-line UI tests for this project. The test plan is the source of truth: read `test/ui-test-plan.md` before running anything.

## Test-plan format

Each test case is one independently executable command. Keep cases in the order they should run. Every case must contain:

- `Aim`: the behavior being checked.
- `Command`: the command used to start the program.
- `Inputs`: the exact lines sent to standard input, in order.
- `Expected output`: the complete console output expected from that invocation.

The plan may also contain a working directory, one-time setup commands, a Java-version requirement, and notes about output normalization. Do not invent missing expected output; ask the user to complete the plan if a case is underspecified.

## Run the test session

1. Run from the repository root unless the plan specifies another working directory.
2. Check `java --version` before executing Java commands. This project requires Java 25; stop and report the environment problem if Java 25 is not active.
3. Run any setup commands once, in their listed order. Setup commands prepare the program and are not test cases unless the plan explicitly marks them as one.
4. Execute test cases strictly in plan order. For each case, start a fresh process, send the listed inputs through standard input, and capture the complete console output. Do not manually alter or reorder input lines.
5. Compare the captured output with the expected output exactly, after only normalizing CRLF to LF and allowing the usual difference between a final newline being present or absent. Preserve all other whitespace and blank lines.
6. On the first mismatch or non-zero exit status, terminate the session immediately. Do not run later cases. Report the case ID, aim, command, actual output, expected output, exit status, and the first useful difference if one is obvious.
7. If all cases pass, report the result as successful and include every case's transcript.

## Console transcript

After testing, show a transcript for every case that was executed. Use this shape so input and output are easy to distinguish:

```text
### <case ID> — <case name>
Aim: <aim>
Console input:
<exact input, or `(no input)`>
Console output:
<actual captured output>
Result: PASS
```

For a failure, use `Result: FAIL` and include both `Expected output:` and `Actual output:` blocks. Clearly mark cases after the failure as `NOT RUN` rather than claiming they passed. The final response is the test-session record; do not hide the console output behind a summary.

