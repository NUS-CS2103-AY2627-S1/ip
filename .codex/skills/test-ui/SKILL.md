---
name: test-ui
description: Run and verify console UI test cases recorded in test/ui-test-plan.md. Use after every source-code update in this project, when asked to test the program with command/input sequences and expected console output, or when maintaining the UI test plan.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for console UI tests. If it does not exist, create it before testing. Record every requested test case there, including:

- a unique name or identifier;
- the aim of the test;
- the exact console inputs, in order;
- the exact expected console output; and
- any command needed to compile or start the program, plus relevant setup or comparison rules.

Preserve existing test cases unless the user asks to replace them. Clarify genuinely ambiguous expected output before execution; do not invent requirements that could change whether a test passes.

After any source-code update, review the plan before running it. Update the plan when the change affects console behavior, inputs, expected output, setup, or test coverage. If no plan change is needed, leave it unchanged and proceed with the existing cases.

## Run tests

1. Inspect the project and test plan to determine the Java 25 compile/run commands. Compile once when practical. Do not change production code unless the user separately asks for a fix.
2. Execute test cases in the order listed. Start a fresh program process for each test case unless the plan explicitly describes one shared session.
3. Feed the listed inputs exactly and capture the complete user-visible console session, including both entered input and program output. Keep input distinguishable from output in the reported transcript.
4. Compare actual output with expected output using the plan's stated rules. If no rules are stated, compare text exactly after normalizing only platform line endings (`CRLF` versus `LF`). Do not silently ignore prompts, whitespace, blank lines, or extra output.
5. Stop immediately on the first failed test. Do not execute remaining cases.

Avoid shell pipelines or command forms that lose input/output ordering. When the program does not echo redirected input, reconstruct the transcript from the exact supplied input and captured output, labeling each clearly rather than implying the program echoed it.

## Report results

Always show a test-session record containing the launch command and, for every executed case, the complete console input and output. State which cases passed.

For a failure, identify the failed case and show the actual and expected outputs in separate fenced blocks. Mention the first meaningful difference when it helps diagnosis, and explicitly state that later cases were not run because testing stops at the first failure.

For a compile error, launch error, timeout, crash, or unexpected end of input, treat the affected case as failed, stop immediately, and include the relevant diagnostic output in the session record.
