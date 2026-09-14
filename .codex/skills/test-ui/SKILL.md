---
name: test-ui
description: Run the Penguin Java console program against test cases in test/ui-test-plan.md, compare actual and expected behavior, and stop at the first failure. Use after Java code changes or when asked to test the chatbot UI.
---

# Test UI

Run the Java chatbot through its console interface and verify its behavior.

## Procedure

1. Read `test/ui-test-plan.md` and identify each test's input, expected behavior, and ordering requirements.
2. Ensure Java 25 is active. On macOS, use `sdk use java 25.0.3.fx-zulu` when needed.
3. Compile all source files into a temporary directory:

   ```bash
   mkdir -p /tmp/penguin-test-classes
   javac -d /tmp/penguin-test-classes $(find src/main/java -name '*.java' -type f)
   ```

4. Run each test separately by piping its input to `Penguin`:

   ```bash
   printf 'input here\n' | java -cp /tmp/penguin-test-classes penguin.Penguin
   ```

5. Show the console input and output for every test.
6. Compare actual output with the expected behavior in the test plan, including the order of important messages and final task state.
7. For negative tests, verify both the error message and the state shown by a later `list` command. An invalid command must not silently add, remove, mark, or unmark a task.
8. Treat a test as failed if an expected message is missing, appears in the wrong order, or the final state is incorrect.
9. If a test fails, stop immediately and report the test name, expected output, and actual output.
10. If all tests pass, report the complete passing test list and mention any tests marked as known future failures.

Do not modify Java source files while running this skill. Update the test plan only when the user requests new or changed test cases.
