---
name: test-ui
description: Run fail-fast console UI tests after code updates or when testing is requested, using command lists and exact expected outputs while maintaining test/ui-test-plan.md and reporting the session transcript.
---

# Test the Console UI

Use this skill after every application or test code update, and when the user asks to run, add, or update console UI tests for this project.

## Test plan

Read `test/ui-test-plan.md` before testing. Review whether the preceding code update changed user-visible behavior, commands, or expected console output, and update the plan when needed before running it. Treat test cases supplied by the user as authoritative and record them in that file before execution. Every test case must contain:

- a unique name and aim;
- the commands to send to standard input, in order;
- the exact expected standard output, including spaces, blank lines, and separators.

Keep existing cases unless the user asks to replace or remove them. Do not invent expected behavior when the requirement is ambiguous; report what needs clarification.

## Run tests

1. Confirm that `java` and `javac` report Java 25. Compile every `.java` file under `src/main/java` into a temporary directory outside the repository.
2. Run each test case in its own fresh `Pixel` process. Send its commands to standard input in the recorded order. End the input after the final command.
3. Compare standard output with the expected output exactly. Normalize only CRLF versus LF line endings; do not trim whitespace or discard blank lines.
4. Run cases in their plan order and stop immediately on the first compilation or test failure. Do not run any later cases.

Do not change production code while executing this skill unless the user separately asks for a fix.

## Report results

For every completed case, show a console-session record containing the input commands and actual program output. A clearly separated `Input` block and `Output` block is sufficient; do not pretend piped input was echoed by the application.

For a passing session, identify all cases that passed. For a failed case, report its name and aim, then show the input, actual output, and expected output in separate fenced blocks. State where the first difference occurs when practical.
