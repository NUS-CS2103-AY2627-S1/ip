---
name: test-ui
description: Run fail-fast console UI tests for this Java project from command and expected-output cases recorded in test/ui-test-plan.md. Use when asked to test Beemo's text interface, add UI test cases, verify console transcripts, or compare actual and expected command output.
---

# Test UI

Maintain and run exact console tests for Beemo.

## Test plan

Record every test case in `test/ui-test-plan.md`. Each case must contain:

- a unique name;
- one-line aim;
- a fenced `text` block containing the commands sent to one fresh program run;
- a fenced `text` block containing the complete expected standard output.

Follow the existing case structure exactly because the runner parses it. Add or update cases when the user supplies new commands and expected outputs.

## Run tests

From the repository root, run:

```bash
python3 .codex/skills/test-ui/scripts/run-ui-tests.py
```

The runner compiles every Java source in `src/main/java` with the available JDK, then runs the cases in plan order. Each case uses an isolated temporary working directory so saved application data cannot leak into another case. It terminates immediately on the first compilation or output failure. On an output failure, it prints the expected output, actual output, and a unified diff.

Always show the resulting console input/output record to the user. The runner prints it and saves the same record to `_temp/ui-test-session.log`. Report the absolute log path and summarize passed cases; never claim later cases passed after an earlier failure.
