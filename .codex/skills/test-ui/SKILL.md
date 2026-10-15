---
name: test-ui
description: Run Clarry's console UI test cases from test/ui-test-plan.md and compare each transcript with its expected output.
---

# Test the console UI

Use this project-specific skill after changing Java code that can affect Clarry's console behaviour.

1. Read `test/ui-test-plan.md`. Each test case contains an aim, an `Inputs` text block, and an `Expected output` text block.
2. Confirm Java 25 is active, then compile all files in `src/main/java` into the ignored `out` directory.
3. Run each test case as a separate Clarry session. Pass the input lines in the order listed, and capture all console output.
4. Compare the complete captured output with the expected-output block, treating only CRLF-versus-LF line endings as equivalent.
5. After every passing test, show the recorded console input and output. If any test fails, stop immediately and show that test's input, actual output, and expected output. Do not continue to later cases.

Keep the test plan in sync with intended UI behaviour before running it. Do not weaken expected outputs to make a failing implementation pass; first determine whether the code or the documented expectation is wrong.
