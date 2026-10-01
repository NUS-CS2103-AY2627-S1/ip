---
name: test-ui
description: "Run planned console UI test cases for this Java project, compare each program transcript with its expected output, and report the session. Use when asked to test or verify the Bany command-line interface."
---

# Test UI

Run the console UI test cases defined in [`test/ui-test-plan.md`](../../../test/ui-test-plan.md).

## Test plan format

Each `## Test case:` section must contain all of the following:

- **Aim** — the behavior being checked.
- **Inputs** — an ordered fenced `text` block containing the command lines sent to Bany.
- **Expected output** — a fenced `text` block containing the complete console output expected from that fresh program run.

Expected output is compared exactly after normalizing only line endings to LF. Blank lines and trailing spaces are significant. Ask the user to resolve an incomplete or ambiguous test case before running it.

## Run the tests

1. Read the complete test plan. Run its test cases in file order.
2. Confirm Java 25 is active. Compile the current source once into `_temp/test-ui-classes`:

   ```bash
   javac -cp 'commons-lang3-3.20.0/commons-lang3-3.20.0.jar:jackson-2.21/*' -d _temp/test-ui-classes $(rg --files src/main/java -g '*.java')
   ```

3. Before each independent test case, clear `data/bany.txt` so persisted tasks from an earlier case cannot affect it. If a case has a **Setup** block, apply that setup instead. Start a **fresh** Bany process for each case. Send the listed inputs to `Bany`, capture standard output and standard error together, and compare that transcript with the case's expected output. Include the Commons Lang and Jackson JARs on the runtime classpath: `java -cp _temp/test-ui-classes:commons-lang3-3.20.0/commons-lang3-3.20.0.jar:jackson-2.21/* Bany`.
4. After every passing case, continue to the next one. If a case fails, stop immediately; do not run any later cases.

Task-command tests should cover these tag rules: required tags may appear in any order; extra tags produce a warning but do not prevent task creation; duplicate critical tags (`by`, `from`, or `to`) reject the task; duplicate non-critical tags produce a warning but do not prevent task creation; and the first token must be a valid command.

## Report the test session

Always show the tested console session in the response. For every test case that ran, include its aim, the console input, and actual console output in separate fenced `text` blocks.

- On success, state that the actual output matched exactly.
- On failure, state that testing stopped at that case and show both the expected and actual outputs. Clearly preserve whitespace in code blocks.
- If compilation or launching fails, stop immediately and report the command, exit status, and captured output.

Do not modify source code or the test plan while running tests unless the user specifically asks for those changes.
