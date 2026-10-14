---
name: test-ui
description: Run black-box console tests for bingus.Bingus using command and expected-output cases recorded in test/ui-test-plan.md. Use when asked to test console commands, verify program output, or run the UI test plan after a code change.
---

# bingus.Bingus UI Tests

Read `test/ui-test-plan.md` before testing. It defines test cases, each with an aim, ordered inputs, and expected output for every command.

## Workflow

For each test case:

1. Compile the source in `src/main/java` with Java 25.
2. Run bingus.Bingus once using the test case's ordered input commands.
3. Record the complete console session, including user input, the startup greeting, and all output.
4. Compare the actual response after every command with that command's expected output in the plan. Preserve spaces, blank lines, punctuation, and line order; ignore only CRLF-versus-LF line endings.
5. If a command differs, terminate the entire test session immediately. Report the test-case aim, the commands entered so far, the expected output for the failing command, and its actual output. Do not run later test cases.
6. If every command passes, report a concise pass summary.
7. In either outcome, show the complete console input/output transcript so the user can inspect the session.

Do not edit the Java source while running the test. If a code change creates a new visible behaviour, update `test/ui-test-plan.md` before testing.
