---
name: test-ui
description: Run command-line UI test cases from test/ui-test-plan.md or a supplied list of commands, inputs, and expected outputs; compare each session exactly and stop at the first failure.
---

# Test UI

Use this skill after a code update changes the program's console behavior, or when the user explicitly asks for UI testing.

## Test-case input

Read `test/ui-test-plan.md` first. If the user supplies test cases, use those too; treat each command/input/expected-output record as one test case and preserve the supplied order. A test case must identify:

- `aim`: the behavior being verified.
- `command`: the complete command that starts the program. Use a list of arguments when that avoids shell quoting ambiguity.
- `inputs`: the exact lines sent to standard input, in order.
- `expected output`: the complete console output expected from that session.

If a code update changes observable behavior, update the plan before running the tests. Keep the plan's commands runnable from the repository root and use Java 25 for Java programs.

## Execution

1. Run any documented setup or compilation command once, then run test cases one at a time in the documented order.
2. Feed each test case's inputs to a fresh process. Capture the complete visible console session and the process exit status. Treat a non-zero exit status as a failure.
3. Compare the captured console output with the expected output exactly. Normalize only platform line endings (`CRLF` and `CR` to `LF`); do not ignore whitespace, prompts, ordering, or extra lines.
4. After each case, print a transcript showing the command, console input, and actual console output. Clearly label the test case and whether it passed.
5. If a case fails, stop immediately. Do not run later cases. Report the actual output and expected output in full, along with the exit status, and identify the first failing test case.
6. If all cases pass, print all transcripts and a concise summary naming the number of passed cases.

Do not silently edit expected output to make a failing test pass. If the behavior is intentionally changed, update the test plan first, then rerun from the first test case.
