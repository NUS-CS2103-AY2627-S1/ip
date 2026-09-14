---
name: test-ui
description: Run UI tests by piping scripted inputs to the CLI app and comparing actual output against expected output from test/ui-test-plan.md. Use after code changes to verify correctness.
---

# Test UI

Run the test cases defined in `test/ui-test-plan.md` against the compiled CLI application.

## Steps

1. Read `test/ui-test-plan.md` to get the list of test cases. Each test case has:
   - A name/aim
   - A list of inputs (one per line)
   - The expected output (exact stdout)

2. Compile the project:
   ```bash
   javac src/main/java/*.java -d out/production/ip
   ```

3. For each test case, in order:
   - Pipe the inputs to the program:
     ```bash
     printf '<input1>\n<input2>\n...\n' | java -cp out/production/ip Johnny
     ```
   - Compare the actual output against the expected output.
   - If they **match**: record as PASSED and show the console session.
   - If they **don't match**: record as FAILED, show the actual and expected outputs side by side, and **stop immediately** (do not run further test cases).

4. After all tests pass (or on first failure), report:
   - Total tests run and result (e.g., "4/4 PASSED" or "FAILED at test 2/4")
   - The full console session for each test that ran (showing both the input commands and the program output)

## Notes

- Trim trailing whitespace from both actual and expected when comparing, as trailing spaces are not meaningful.
- If compilation fails, report the error and stop.
