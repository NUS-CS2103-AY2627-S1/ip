---
name: test-ui
description: Runs the UI test plan in test/ui-test-plan.md against the compiled CLI program, comparing actual console output to each test case's expected output. Use when the user asks to run UI tests, test-ui, or verify the app's console behavior against the test plan.
---

# test-ui

Runs the manual/scripted UI test plan for this project's CLI program against
its actual console output, one test case at a time, in order.

## Steps

1. **Read the test plan.** Open `test/ui-test-plan.md` (create it with the
   user if it does not exist yet — do not invent test cases on your own).
   It documents how to compile/run the program and lists test cases, each
   with an **Aim**, an **Input** block (one command per line), and an
   **Expected Output** block (the exact console output that run should
   produce).

2. **Build once.** Compile the program using the command the test plan
   specifies (e.g. `javac -d out src/main/java/*.java`). If compilation
   fails, stop immediately and report the compiler error — do not run any
   test cases.

3. **Run each test case in order**, starting a fresh process per test case
   (state does not carry over between test cases unless the plan says
   otherwise):
   - Feed the test case's `Input` lines to the program's stdin, one command
     per line, in the given order, then close stdin.
   - Capture the full stdout produced by that run.
   - Compare it against the test case's `Expected Output` using the
     comparison rules in the plan (exact text match after normalizing line
     endings, since `println` uses the platform separator; a trailing
     newline at the very end does not count as a mismatch).

4. **On a match:** record the test case as passed, append its input and
   actual output to the running session transcript, and continue to the
   next test case.

5. **On a mismatch:** stop immediately — do not run any remaining test
   cases. Report:
   - Which test case failed (its number and Aim).
   - The exact **Input** used.
   - The full **Expected Output**.
   - The full **Actual Output**, so the two can be compared side by side.
   - Then still print the session transcript gathered so far (steps 4),
     covering every test case run up to and including the failing one.

6. **After the run finishes** (whether it completed all test cases or
   stopped early on a failure), show the full console session transcript —
   for each test case executed, its Input followed by the Actual Output
   it produced — so the user can see exactly what happened, plus a one-line
   summary (e.g. "5/6 test cases passed; stopped at TC6").

## Notes

- Use the project's actual entry point and compile/run commands as written
  in `test/ui-test-plan.md` — do not assume a build tool (e.g. Gradle/Maven)
  unless the plan says to use one.
- Do not modify `src/` files as part of testing. If a test fails because of
  a real bug, report it — fixing it is a separate, explicit task.
- Whitespace matters: the expected output blocks include leading spaces and
  separator lines exactly as the program prints them. Compare verbatim
  rather than trimming lines.
