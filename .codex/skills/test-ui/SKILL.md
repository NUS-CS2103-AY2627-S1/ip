---
name: test-ui
description: Run and verify project console UI test cases recorded in test/ui-test-plan.md, showing a transcript and stopping at the first failed case.
---

# Console UI testing

Use this skill when asked to execute or verify the project's console UI tests. The source of truth for test cases and test setup is [test/ui-test-plan.md](../../../test/ui-test-plan.md). Update that file when the user asks to add, change, or document test cases.

## Test-plan format

Keep the test plan's environment details near the top. Record ordered test cases using this structure:

````markdown
## T01 — short name

**Aim:** What this test proves.

**Inputs:**
```text
<the exact console input, in order>
```

**Command:**
```powershell
<one command that starts the program and supplies the inputs>
```

**Expected output:**
```text
<the complete expected console output>
```
````

Use one test case per command. If a scenario requires multiple console entries, provide them in `Inputs` and feed all of them through that case's single command. Use distinct test cases when a program must be started again. Expected output must be complete enough for an exact comparison, including prompts, messages, and final newlines when relevant.

## Execution

1. Read `test/ui-test-plan.md`; do not invent missing commands or expected output. Confirm that every selected case has an aim, inputs, command, and expected output before running it.
2. Use the environment and build instructions in the plan. Run build tasks with Java 25. Run each test case's command separately, capturing both standard output and standard error in their displayed order.
3. Compare the captured output with the expected-output block exactly after normalizing only line endings (`CRLF`/`LF`). Do not ignore whitespace, reorder lines, or use partial matching unless the plan explicitly specifies a comparison rule for that case.
4. After every passing case, display a transcript containing the case ID, console input, and actual console output. Preserve the text verbatim in fenced `text` blocks.
5. At the first failed case, stop immediately: do not run later cases. Display its console input, actual output, and expected output in separate fenced `text` blocks, clearly marking the failure. State whether the command also returned a non-zero exit code.
6. End with a concise summary: all passed, or the first failed case and the number of cases not run. Do not modify the application or expected outputs merely to make a test pass unless the user specifically asks for that change.

When command output includes nondeterministic data (such as dates, generated IDs, or paths), require the test plan to state a deterministic setup or an explicit comparison rule before executing that case.
