---
name: test-ui
description: Run planned console UI tests for this project, comparing each program session with its exact expected output and recording the session.
---

# Console UI testing

Use this skill for console-user-interface regression tests in this project.

## Test plan

Keep the complete test-case list in [`test/ui-test-plan.md`](../../../test/ui-test-plan.md). Every case must have a unique heading and these fields:

- **Aim** — the behavior being checked.
- **Command** — the command used to start the program.
- **Input** — all console input supplied to that program session.
- **Expected output** — the complete output expected from that session.

Write `Command`, `Input`, and `Expected output` as fenced code blocks. Input and output comparisons are exact except that CRLF and LF line endings are treated alike. One test case represents one fresh program run, so state does not carry from one case to another.

## Run tests

1. Update the plan before testing whenever commands, test cases, or expected output change.
2. Run `bash .codex/skills/test-ui/scripts/run-ui-tests.sh` from the project root. If the project requires Java, ensure it uses Java 25 first.
3. Review the emitted console transcript. It includes the command, supplied input, actual output, and outcome for every test run that began.

The runner stops at the first failed case. On failure, report the case name and both actual and expected output; do not run later cases. Keep the transcript in `test/ui-test-session.log` so the completed or failed session can be inspected after the command ends.

Do not update expected output merely to make a failing test pass unless the changed behavior is intended and has been confirmed.
