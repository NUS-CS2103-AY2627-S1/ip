---
name: test-ui
description: Run scripted end-to-end tests for this project's console UI from command and expected-output lists, recording cases in test/ui-test-plan.md. Use when adding, updating, or executing Rem UI test cases.
---

# Test UI

Use `test/ui-test-plan.md` as the single source of truth for UI test cases and launch information.

## Prepare the plan

- Read the plan before testing.
- When the user supplies commands and expected outputs, add or update the corresponding cases in the plan before running them. Preserve unrelated cases.
- Give every case a unique name and record its aim, its ordered inputs, and its complete expected standard output.
- Put inputs and expected output in fenced `text` blocks. Inputs contain one submitted command per line; blank input is represented by an empty line.
- Expected output is exact, including prompts, separators, spaces, and blank lines. Normalize only CRLF versus LF when comparing. Do not trim or otherwise rewrite output.
- Every case must end the program normally, usually with `bye`. If it intentionally does not, record a timeout in the case and explain why.

Use this shape:

```markdown
## TC-01: Short descriptive name

**Aim:** Behavior this case verifies.

**Inputs:** a fenced `text` block containing one command per line.

**Expected output:** a fenced `text` block containing complete output from a fresh process.
```

## Run the tests

1. Verify that both `java` and `javac` report major version 25. Stop and report the environment problem if either does not.
2. Use the launch information in the plan. For this project, compile `src/main/java/*.java` into `out/`, then run `Rem` with `out/` as the classpath. Recompile once before the session, stopping if compilation fails.
3. Run every selected case in plan order in a fresh program process. Feed its input block to standard input and capture standard output exactly; also capture standard error and the exit code separately. Apply a reasonable timeout so a missing terminating command cannot hang the session.
4. Compare the captured standard output with the case's expected-output block after normalizing line endings only. A nonzero exit code, timeout, or unexpected standard error is also a failure unless the case explicitly expects it.
5. After each passing case, report that it passed and continue.
6. On the first failure, terminate the program if it is still running and stop the whole test session. Do not execute later cases.

## Report the session

Always show the executed cases and a console record for each case. Label submitted lines as input and keep the captured program output verbatim in a fenced block so the session is auditable. Do not silently omit startup or shutdown output.

For a failure, report the case name and failure reason, then show:

- all console inputs submitted before termination;
- the complete actual standard output;
- the complete expected standard output;
- standard error and exit code when relevant;
- the first differing line or character position when it can be identified clearly.

Finish with a concise total such as `3 passed, 1 failed, 2 not run`. Never update expected output merely to make a failing test pass; change the plan only when the user changes the intended behavior.
