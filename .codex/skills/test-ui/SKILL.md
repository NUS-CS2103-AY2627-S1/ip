---
name: test-ui
description: Record and run exact console UI acceptance tests from lists of commands and expected outputs. Use for testing this project's command-line interaction and producing a visible input/output session record; stop at the first mismatch.
---

# Test UI

Turn the user's commands and expected outputs into repeatable console acceptance tests for this project.

## Test plan

Before running anything, create or update `test/ui-test-plan.md`. Preserve still-relevant existing cases unless the user asks to replace them. Record:

- the program start command, working directory, required Java version, and comparison rules;
- each test case's identifier and aim;
- any expected output printed at startup;
- every input in entry order and the exact output expected after that input; and
- any shutdown input and expected shutdown output.

Use fenced text blocks for inputs and outputs so spaces, blank lines, and punctuation remain visible. Do not invent expected output or copy actual output into the expectation after a run. If information needed to execute or judge a case is absent and cannot be found in the repository, ask the user for it before testing.

## Run the tests

1. Inspect the repository instructions and determine how to compile and start the program. Use Java 25, as required by this project. If Java 25 is unavailable, report that blocker without changing the project.
2. Start every test case in a fresh program process so cases do not share state, unless the plan explicitly defines a shared session.
3. Capture raw standard output and standard error in chronological order. Keep typed input separately; shell-generated input echo is not program output.
4. Enter inputs one at a time. After startup and after each input, compare the newly emitted program output with the corresponding expected block before entering the next input.
5. Compare exact text after normalizing only line endings (`CRLF` and `LF` are equivalent). Spaces, capitalization, punctuation, divider lines, and blank lines are significant. Do not trim output.
6. If the observed output differs, terminate the running program immediately. Do not enter further commands or run later test cases.
7. If the program exits, hangs, or emits unexpected standard error before the expected interaction completes, treat that as a failure and terminate it if needed.

Use a pseudo-terminal or another interaction method that lets you observe output between inputs. Do not pipe an entire test case into the program at once, because that would violate the stop-on-first-failure rule.

## Report

Always show a chronological console-session record. Prefix user-entered lines with `> ` for display, and reproduce program output exactly beneath them. Clearly state which cases passed.

On the first failure, also report:

- the test case and interaction step;
- the input that triggered the mismatch;
- separate fenced blocks labelled `Expected output` and `Actual output`; and
- that the session was terminated and later tests were not run.

Keep the plan as the source of truth. A failed run must not alter its expectations unless the user separately asks to revise the test case.
