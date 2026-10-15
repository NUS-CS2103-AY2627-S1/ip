---
name: test-ui
description: Run and strictly verify console UI test cases defined for this Java project. Use when testing commands and their expected terminal output.
---

# Console UI testing

Use [test/ui-test-plan.md](../../../test/ui-test-plan.md) as the test source. It contains one or more test cases, each with an aim, an **Input** code block, and an **Expected output** code block.

For each test case:

1. Compile the current Java sources with Java 25 into a temporary directory under `build/`.
2. Send the complete Input block to Alexa's standard input and capture standard output.
3. Compare captured output to the Expected output block exactly, apart from line-ending differences between Windows and Unix.
4. In the response, show a **Console record** containing the exact input sent and the captured output.

Run cases in their listed order. On the first mismatch, stop immediately. Report the test case aim followed by the complete expected and actual outputs; do not run subsequent cases. If all cases match, state that every listed case passed and show a console record for each session.

Do not change application code while testing. Update `test/ui-test-plan.md` only when the user asks to add, change, or remove test cases.
