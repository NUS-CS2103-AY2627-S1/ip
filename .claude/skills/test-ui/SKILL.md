---
name: test-ui
description: Use when running or updating the console UI test cases for this project's Java program — testing the "list of commands vs expected output" test cases in test/ui-test-plan.md, verifying a change didn't break Meowmeow's console output, or asked to test/verify the UI, run UI tests, or check console output.
---

# Test UI

Runs the project's console UI test cases: for each test case, feeds a
fixed sequence of commands to the compiled program's stdin and checks the
captured stdout against an exact expected transcript.

## Test plan

Test cases live in `test/ui-test-plan.md`. Each is a `## Test: <name>`
section with a one-line `**Aim:**`, an `` ```input `` block (one console
command per line), and an `` ```output `` block (the exact expected
stdout, including divider lines and leading spaces). See that file's
own "Format" section and existing test cases as the template — match
their exact structure when adding a new one.

When adding a test case: write the Aim and Input first, run the program
by hand to see its actual output, then paste that verified output as the
Expected block. Never write the Expected block from what the output
*should* be without running it — a typo there makes a correct program
look broken.

## Running the tests

1. Compile the current sources:

   ```bash
   ./gradlew compileJava
   ```

   This builds to `build/classes/java/main` with the JavaFX GUI classes on
   the classpath. (A plain `javac -d out/production/ip $(find src/main/java
   -name "*.java")` no longer works on its own — `meowmeow.gui` needs the
   JavaFX jars. If Gradle reports a Java version mismatch, run
   `sdk use java 25.0.3.fx-zulu` first — see AGENTS.md.)

2. Run the test runner from the repository root:

   ```bash
   python3 .claude/skills/test-ui/scripts/run_ui_tests.py --classpath build/classes/java/main
   ```

   The runner defaults to `test/ui-test-plan.md`, classpath
   `out/production/ip`, and main class `meowmeow.Meowmeow`; the
   `--classpath` above points it at the Gradle output instead. The console
   app is still `meowmeow.Meowmeow` even though the Gradle `run` task now
   launches the GUI (`meowmeow.gui.Launcher`).

3. The script prints, per test case, the console input sent and the
   actual console output produced — this is the session record. Show
   this output to the user rather than summarizing it away.

4. **On the first failing test case, the script stops immediately** and
   prints the expected output next to the actual output for that case.
   Report that comparison to the user; do not run or describe any
   remaining test cases, and do not attempt to fix the program or the
   test plan without asking the user which one is wrong (a failure can
   mean either the program regressed or the expected output is stale).

5. If every test case passes, report the pass count from the final
   summary line.
